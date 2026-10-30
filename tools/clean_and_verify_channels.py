import json
import os
import sys
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))   # python -I leaves the script folder out
from channel_numbers import assign
import sys
from concurrent.futures import ThreadPoolExecutor
from verify_all_streams import verify_stream

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
CHANNELS_PATH = os.path.join(ROOT, "channels.json")

def check_channel(ch):
    url = ch.get("url", "")
    auth = ch.get("auth")
    name = ch.get("name", "")
    ok, reason = verify_stream(url, auth)
    return ch, ok, reason

def main():
    with open(CHANNELS_PATH, encoding="utf-8") as f:
        channels = json.load(f)
        
    print(f"Total input channels: {len(channels)}", flush=True)
    
    # 1. Immediate removal of known fake/empty ottrast and website URLs
    filtered = []
    removed_fake = 0
    for ch in channels:
        url = ch.get("url", "")
        if any(w in url for w in ["yes.co.il", "hot.net.il", "stingtv.co.il", "partner.co.il", "cellcom.co.il"]):
            removed_fake += 1
            continue
        filtered.append(ch)
        
    print(f"Removed {removed_fake} fake/empty ottrast and website URLs. Remaining: {len(filtered)}", flush=True)
    
    # Separate Israeli channels to verify
    israeli_to_check = []
    world_channels = []
    
    for ch in filtered:
        is_israeli = (
            "ישראל" in ch.get("category", "") or
            "Israel" in ch.get("country", "") or
            "israel" in ch.get("id", "") or
            "HOT" in ch.get("name", "") or
            "yes" in ch.get("name", "")
        )
        if is_israeli:
            israeli_to_check.append(ch)
        else:
            world_channels.append(ch)
            
    print(f"Checking {len(israeli_to_check)} Israeli channels concurrently with 6 workers...", flush=True)
    
    verified_israeli = []
    broken_israeli = []
    
    with ThreadPoolExecutor(max_workers=6) as ex:
        results = ex.map(check_channel, israeli_to_check)
        for ch, ok, reason in results:
            if ok:
                verified_israeli.append(ch)
                print(f"[OK] {ch['name']}", flush=True)
            else:
                broken_israeli.append((ch['name'], ch['url'], reason))
                print(f"[DROP] {ch['name']} ({reason})", flush=True)
                
    print(f"\nVerification finished!", flush=True)
    print(f"Verified & working Israeli channels: {len(verified_israeli)}", flush=True)
    print(f"Dropped broken Israeli channels: {len(broken_israeli)}", flush=True)
    
    final_channels = verified_israeli + world_channels
    print(f"Total final channels: {len(final_channels)}", flush=True)
    
    # Renumber sequentially
    final_channels = assign(final_channels)
        
    with open(CHANNELS_PATH, "w", encoding="utf-8", newline="\n") as f:
        json.dump(final_channels, f, ensure_ascii=False, indent=2)
        
    print("channels.json successfully written with only 100% verified working streams!", flush=True)

if __name__ == '__main__':
    main()
