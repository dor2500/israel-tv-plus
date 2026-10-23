"""Builds the LG webOS package (same layout as `ares-package`) without the webOS SDK.

usage: python -I tools/build_ipk.py [out_dir]   -> <out_dir>/<id>_<version>_all.ipk  (default: dist/)
Only the files the app needs go in (no tools, docs, scripts or old packages).
"""
import io
import json
import os
import sys
import tarfile
import time

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
FILES = ["appinfo.json", "largeIcon.png", "splash.png"] + [
    l.strip() for l in open(os.path.join(ROOT, "tools", "webfiles.txt"), encoding="utf-8")
    if l.strip() and not l.startswith("#") and l.strip() != "manifest.webmanifest"]
DIRS = ["assets"]
SKIP = ("&", ".map")  # junk downloads with query strings in the name, source maps


def tar_gz(entries, mtime):
    """entries: list of (path, bytes or None for a directory)."""
    buf = io.BytesIO()
    with tarfile.open(fileobj=buf, mode="w:gz", format=tarfile.GNU_FORMAT) as t:
        for path, data in entries:
            ti = tarfile.TarInfo(path)
            ti.mtime = mtime
            ti.uid = ti.gid = 0
            if data is None:
                ti.type, ti.mode = tarfile.DIRTYPE, 0o755
                t.addfile(ti)
            else:
                ti.size, ti.mode = len(data), 0o644
                t.addfile(ti, io.BytesIO(data))
    return buf.getvalue()


def ar(members, mtime):
    out = b"!<arch>\n"
    for name, data in members:
        out += f"{name:<16}{mtime:<12}{0:<6}{0:<6}{'100644':<8}{len(data):<10}`\n".encode()
        out += data + (b"\n" if len(data) % 2 else b"")
    return out


def main():
    out_dir = sys.argv[1] if len(sys.argv) > 1 else os.path.join(ROOT, "dist")
    with open(os.path.join(ROOT, "appinfo.json"), encoding="utf-8") as f:
        info = json.load(f)
    app_id, version = info["id"], info["version"]
    mtime = int(time.time())

    files = list(FILES)
    for d in DIRS:
        for base, _, names in os.walk(os.path.join(ROOT, d)):
            for n in sorted(names):
                rel = os.path.relpath(os.path.join(base, n), ROOT).replace("\\", "/")
                if not any(s in rel for s in SKIP):
                    files.append(rel)

    app_dir = f"usr/palm/applications/{app_id}/"
    pkg_dir = f"usr/palm/packages/{app_id}/"
    dirs = set()
    for p in [app_dir + f for f in files] + [pkg_dir + "packageinfo.json"]:
        parts = p.split("/")[:-1]
        for i in range(1, len(parts) + 1):
            dirs.add("/".join(parts[:i]) + "/")
    entries = [(d, None) for d in sorted(dirs)]
    for f in files:
        with open(os.path.join(ROOT, f), "rb") as fh:
            entries.append((app_dir + f, fh.read()))
    pkginfo = {"app": app_id, "id": app_id, "loc_name": info.get("title", app_id), "package_format_version": 2,
               "vendor": info.get("vendor", ""), "version": version}
    entries.append((pkg_dir + "packageinfo.json", json.dumps(pkginfo, ensure_ascii=False, indent=2).encode()))

    size_kb = sum(len(d) for _, d in entries if d) // 1024
    control = (f"Package: {app_id}\nVersion: {version}\nSection: misc\nPriority: optional\nArchitecture: all\n"
               f"Installed-Size: {size_kb}\nMaintainer: {info.get('vendor', '')}\n"
               f"Description: {info.get('title', app_id)}\nwebOS-Package-Format-Version: 2\n"
               f"webOS-Packager-Version: x.y.z\n")
    ipk = ar([("debian-binary", b"2.0\n"),
              ("control.tar.gz", tar_gz([("./", None), ("./control", control.encode())], mtime)),
              ("data.tar.gz", tar_gz(entries, mtime))], mtime)
    os.makedirs(out_dir, exist_ok=True)
    path = os.path.join(out_dir, f"{app_id}_{version}_all.ipk")
    with open(path, "wb") as f:
        f.write(ipk)
    print(f"{path}  {len(ipk) // 1024} KB, {len(files)} files")


if __name__ == "__main__":
    main()
