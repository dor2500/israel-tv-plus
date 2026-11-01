import os

p = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\android\app\build.gradle.kts"
with open(p, "r", encoding="utf-8") as f:
    c = f.read()

c = c.replace("\r\n", "\n")

target1 = """    @get:InputFile @get:PathSensitive(PathSensitivity.NONE) abstract val channels: RegularFileProperty
    @get:OutputDirectory abstract val outputDir: DirectoryProperty"""

repl1 = """    @get:InputFile @get:PathSensitive(PathSensitivity.NONE) abstract val channels: RegularFileProperty
    @get:InputFile @get:PathSensitive(PathSensitivity.NONE) abstract val vodData: RegularFileProperty
    @get:OutputDirectory abstract val outputDir: DirectoryProperty"""

assert target1 in c, "target1 not found"
c = c.replace(target1, repl1)

target2 = """            from(channels)
        }"""

repl2 = """            from(channels)
            from(vodData)
        }"""

assert target2 in c, "target2 not found"
c = c.replace(target2, repl2)

target3 = """    channels.set(repo.resolve("channels.json"))
}"""

repl3 = """    channels.set(repo.resolve("channels.json"))
    vodData.set(repo.resolve("vod_data.json"))
}"""

assert target3 in c, "target3 not found"
c = c.replace(target3, repl3)

with open(p, "w", encoding="utf-8") as f:
    f.write(c)

print("build.gradle.kts updated successfully!")

