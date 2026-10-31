import os

base = r'C:\Users\Dorp\Downloads\Compressed\NuvioMobile-cmp-rewrite\NuvioMobile-cmp-rewrite\composeApp\src\commonMain\kotlin'
for root, dirs, files in os.walk(base):
    rel = os.path.relpath(root, base)
    kt_files = [f for f in files if f.endswith('.kt')]
    if kt_files:
        sample = ', '.join(kt_files[:4])
        print(f"{rel}: {len(kt_files)} files [{sample}]")

