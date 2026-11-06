import json, os

with open('channels.json', 'r', encoding='utf-8') as f:
    channels = json.load(f)

print(f"Auditing {len(channels)} channels...")

broken_or_missing = []
valid_local = []
remote = []

for ch in channels:
    cid = ch.get('id', '')
    name = ch.get('name', '')
    logo = ch.get('logo', '')
    
    if not logo:
        broken_or_missing.append((cid, name, 'EMPTY_LOGO', ''))
        continue
        
    if logo.startswith('http'):
        remote.append((cid, name, logo))
        continue
        
    clean_path = logo.lstrip('/')
    if not os.path.exists(clean_path):
        broken_or_missing.append((cid, name, 'FILE_NOT_FOUND', clean_path))
    else:
        sz = os.path.getsize(clean_path)
        if sz < 100:
            broken_or_missing.append((cid, name, 'FILE_TOO_SMALL', clean_path))
        else:
            valid_local.append((cid, name, clean_path, sz))

print(f"Valid local logos: {len(valid_local)}")
print(f"Remote URL logos: {len(remote)}")
print(f"Broken / Missing local logos: {len(broken_or_missing)}")

print("\n--- BROKEN / MISSING LOCAL LOGOS ---")
for b in broken_or_missing:
    print(b)

print(f"\n--- REMOTE LOGOS ({len(remote)}) ---")
for r in remote:
    print(r)

