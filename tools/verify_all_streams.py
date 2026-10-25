import json
import urllib.request
import urllib.parse
import gzip
import ssl
import time

ctx = ssl.create_default_context()
ctx.check_hostname = False
ctx.verify_mode = ssl.CERT_NONE

HEADERS = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36',
    'Accept': '*/*',
    'Origin': 'https://kondoplay.cfd',
    'Referer': 'https://kondoplay.cfd/'
}

def fetch_data(url, timeout=3.5, retries=2):
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
                return res.status, raw
        except urllib.error.HTTPError as e:
            if e.code == 429 and attempt < retries:
                time.sleep(0.5 * (attempt + 1))
                continue
            return e.code, b''
        except Exception as e:
            if attempt < retries:
                time.sleep(0.3)
                continue
            return 0, b''
    return 0, b''

def verify_stream(url, auth=None):
    if not url or not url.startswith('http'):
        return False, "Not HTTP URL"
    if any(ext in url for ext in ['yes.co.il', 'hot.net.il', 'stingtv.co.il', 'partner.co.il', 'cellcom.co.il']):
        return False, "Web portal URL"
    
    # Handle mako entitlement
    test_url = url
    if auth == 'mako':
        try:
            path = urllib.parse.urlparse(url).path
            ticket_url = 'https://mass.mako.co.il/ClicksStatistics/entitlementsServicesV2.jsp?et=ngt&rv=AKAMAI&lp=' + urllib.parse.quote(path)
            _, t_raw = fetch_data(ticket_url, timeout=3.0)
            ticket = json.loads(t_raw.decode('utf-8'))['tickets'][0]['ticket']
            test_url = url + ('&' if '?' in url else '?') + ticket
        except Exception as e:
            return False, f"Mako ticket error: {e}"

    try:
        status, data = fetch_data(test_url, timeout=3.5)
        if status != 200:
            return False, f"HTTP {status}"
        
        # Check if HTML
        sample = data[:1024].lower()
        if b'<html' in sample or b'<!doctype' in sample:
            return False, "Returned HTML page"
            
        # If HLS
        if b'#EXTM3U' in data:
            text = data.decode('utf-8', errors='ignore')
            lines = [l.strip() for l in text.splitlines() if l.strip()]
            
            # Variant stream?
            variant_urls = [lines[i] for i in range(1, len(lines)) if lines[i-1].startswith('#EXT-X-STREAM-INF') and not lines[i].startswith('#')]
            if variant_urls:
                sub_url = urllib.parse.urljoin(test_url, variant_urls[0])
                status2, data2 = fetch_data(sub_url, timeout=3.5)
                if status2 != 200:
                    return False, f"Variant HTTP {status2}"
                text = data2.decode('utf-8', errors='ignore')
                lines = [l.strip() for l in text.splitlines() if l.strip()]
                test_url = sub_url

            # Find media segments
            segments = [l for l in lines if not l.startswith('#') and ('.ts' in l or '.m4s' in l or 'segment' in l or 'fragment' in l or 'stream' in l or '?' in l)]
            if not segments:
                return False, "Empty HLS playlist (0 segments)"
            
            # Fetch the first segment!
            seg_url = urllib.parse.urljoin(test_url, segments[0])
            status_seg, data_seg = fetch_data(seg_url, timeout=3.5)
            if status_seg != 200 or len(data_seg) < 188:
                return False, f"Segment fetch failed (HTTP {status_seg}, len {len(data_seg)})"
            
            # Verify media header: MPEG-TS starts with 0x47, fMP4 has 'ftyp' or 'moof', or general binary
            if data_seg[0] == 0x47 or b'ftyp' in data_seg[:32] or b'moof' in data_seg[:32] or len(data_seg) > 500:
                return True, "HLS Verified OK"
            return False, f"Unrecognized segment header (first byte {hex(data_seg[0])})"
            
        # Audio or MP4 stream
        if len(data) > 200 and not (b'<html' in sample):
            return True, "Direct stream OK"
            
        return False, "Too short or invalid"
    except Exception as e:
        return False, str(e)
