import re

with open('app.js', encoding='utf-8') as f:
    text = f.read()

# Look for html templates in app.js
found = re.findall(r'id=["\']([^"\']+)["\']', text)
for i in found:
    if any(k in i.lower() for k in ['player', 'osd', 'control', 'bar', 'guide', 'catchup']):
        print(i)

