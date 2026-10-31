import json
import os
import sys
import ssl
import gzip
import time
import urllib.request
import urllib.parse
from concurrent.futures import ThreadPoolExecutor

ctx = ssl.create_default_context()
ctx.check_hostname = False
ctx.verify_mode = ssl.CERT_NONE

HEADERS = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36',
    'Accept': '*/*',
    'Origin': 'https://kondoplay.cfd',
    'Referer': 'https://kondoplay.cfd/'
}

def fetch_data(url, timeout=4.0, retries=1):
    req = urllib.request.Request(url, headers=HEADERS)
    for attempt in range(retries + 1):
        try:
            with urllib.request.urlopen(req, timeout=timeout, context=ctx) as res:
                raw = res.read(64 * 1024)
                if raw.startswith(b'\x1f\x8b'):
                    try:
                        raw = gzip.decompress(raw)
                    except Exception:
                        pass
                return res.status, raw, res.geturl()
        except urllib.error.HTTPError as e:
            if e.code == 429 and attempt < retries:
                time.sleep(0.5)
                continue
            return e.code, b'', url
        except Exception as e:
            if attempt < retries:
                time.sleep(0.3)
                continue
            return 0, str(e).encode(), url
    return 0, b'', url

def test_channel_url(url, auth=None):
    if not url or not url.startswith('http'):
        return False, "Not HTTP URL", url
    if any(ext in url for ext in ['yes.co.il', 'hot.net.il', 'stingtv.co.il', 'partner.co.il', 'cellcom.co.il']):
        return False, "Web portal URL", url
    
    test_url = url
    if auth == 'mako':
        try:
            path = urllib.parse.urlparse(url).path
            ticket_url = 'https://mass.mako.co.il/ClicksStatistics/entitlementsServicesV2.jsp?et=ngt&rv=AKAMAI&lp=' + urllib.parse.quote(path)
            _, t_raw, _ = fetch_data(ticket_url, timeout=3.5)
            ticket = json.loads(t_raw.decode('utf-8'))['tickets'][0]['ticket']
            test_url = url + ('&' if '?' in url else '?') + ticket
        except Exception as e:
            return False, f"Mako ticket error: {e}", url

    try:
        status, data, final_url = fetch_data(test_url, timeout=4.0)
        if status != 200:
            return False, f"HTTP {status}", final_url
        
        sample = data[:1024].lower()
        if b'<html' in sample or b'<!doctype' in sample:
            return False, "Returned HTML page", final_url
            
        if b'#EXTM3U' in data:
            text = data.decode('utf-8', errors='ignore')
            lines = [l.strip() for l in text.splitlines() if l.strip()]
            variant_urls = [lines[i] for i in range(1, len(lines)) if lines[i-1].startswith('#EXT-X-STREAM-INF') and not lines[i].startswith('#')]
            if variant_urls:
                sub_url = urllib.parse.urljoin(final_url, variant_urls[0])
                status2, data2, final_url2 = fetch_data(sub_url, timeout=4.0)
                if status2 != 200:
                    return False, f"Variant HTTP {status2}", sub_url
                text = data2.decode('utf-8', errors='ignore')
                lines = [l.strip() for l in text.splitlines() if l.strip()]
                final_url = final_url2

            segments = [l for l in lines if not l.startswith('#') and ('.ts' in l or '.m4s' in l or 'segment' in l or 'fragment' in l or 'stream' in l or '?' in l)]
            if not segments:
                return False, "Empty HLS playlist (0 segments)", final_url
            
            # Fetch first segment
            seg_url = urllib.parse.urljoin(final_url, segments[0])
            s_status, s_data, _ = fetch_data(seg_url, timeout=4.0)
            if s_status != 200 or len(s_data) < 188:
                return False, f"Segment fetch failed (HTTP {s_status}, len {len(s_data)})", seg_url
            return True, "HLS OK", final_url

        # Audio stream (MP3/AAC/OGG) or Direct video
        if len(data) > 200 and not (b'<html' in sample):
            return True, "Direct Stream/Audio OK", final_url

        return False, f"Unknown/short response ({len(data)} bytes)", final_url
    except Exception as e:
        return False, str(e), url

def main():
    with open('channels.json', 'r', encoding='utf-8') as f:
        channels = json.load(f)

    print(f"Testing all {len(channels)} channels...")
    
    def check_one(ch):
        url = ch.get('url', '')
        auth = ch.get('auth')
        ok, reason, final_url = test_channel_url(url, auth)
        backup_results = []
        if not ok:
            # Test backups
            for b_url in ch.get('backups', []) + ch.get('fallbackUrls', []):
                if b_url and b_url != url:
                    b_ok, b_reason, b_final = test_channel_url(b_url, auth)
                    if b_ok:
                        backup_results.append((b_url, b_reason, b_final))
                        break
        return ch, ok, reason, final_url, backup_results

    results = []
    with ThreadPoolExecutor(max_workers=16) as ex:
        for r in ex.map(check_one, channels):
            results.append(r)
            ch, ok, reason, final_url, backups = r
            status_str = "OK" if ok else ("RECOVERABLE" if backups else "FAIL")
            if not ok:
                print(f"[{status_str}] #{ch.get('number')} {ch.get('name')}: {reason} -> URL: {ch.get('url')[:60]}...")
                if backups:
                    print(f"     -> Working backup found: {backups[0][0][:60]}...")

    ok_count = sum(1 for _, ok, _, _, _ in results)
    recov_count = sum(1 for _, ok, _, _, b in results if not ok and b)
    fail_count = sum(1 for _, ok, _, _, b in results if not ok and not b)

    print("\n" + "="*50)
    print(f"Summary:")
    print(f"  Working directly: {ok_count}/{len(channels)}")
    print(f"  Recoverable via backups: {recov_count}")
    print(f"  Failed (no working stream/backup): {fail_count}")

    # Save detailed report
    report = []
    for ch, ok, reason, final_url, backups in results:
        report.append({
            'number': ch.get('number'),
            'id': ch.get('id'),
            'name': ch.get('name'),
            'category': ch.get('category'),
            'url': ch.get('url'),
            'ok': ok,
            'reason': reason,
            'final_url': final_url,
            'working_backup': backups[0][0] if backups else None
        })

    with open('channel_test_report.json', 'w', encoding='utf-8') as f:
        json.dump(report, f, indent=2, ensure_ascii=False)
    print("Report saved to channel_test_report.json")

if __name__ == '__main__':
    main()
