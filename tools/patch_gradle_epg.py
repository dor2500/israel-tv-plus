import sys

with open("android/app/build.gradle.kts", "r", encoding="utf-8") as f:
    text = f.read()

target1 = """    @get:InputFile @get:PathSensitive(PathSensitivity.NONE) abstract val vodData: RegularFileProperty"""
replacement1 = """    @get:InputFile @get:PathSensitive(PathSensitivity.NONE) abstract val vodData: RegularFileProperty
    @get:InputFile @get:PathSensitive(PathSensitivity.NONE) abstract val epg: RegularFileProperty"""

target2 = """            from(vodData)"""
replacement2 = """            from(vodData)
            from(epg)"""

target3 = """    vodData.set(repo.resolve("vod_data.json"))"""
replacement3 = """    vodData.set(repo.resolve("vod_data.json"))
    epg.set(repo.resolve("epg.json"))"""

assert target1 in text, "target1 not found"
assert target2 in text, "target2 not found"
assert target3 in text, "target3 not found"

text = text.replace(target1, replacement1, 1)
text = text.replace(target2, replacement2, 1)
text = text.replace(target3, replacement3, 1)

with open("android/app/build.gradle.kts", "w", encoding="utf-8", newline="\n") as f:
    f.write(text)

print("build.gradle.kts updated successfully")

# Also add epg.json to tools/webfiles.txt if not present
with open("tools/webfiles.txt", "r", encoding="utf-8") as f:
    wf = f.read()

if "epg.json" not in wf:
    wf = wf.strip() + "\nepg.json\n"
    with open("tools/webfiles.txt", "w", encoding="utf-8", newline="\n") as f:
        f.write(wf)
    print("tools/webfiles.txt updated with epg.json")

