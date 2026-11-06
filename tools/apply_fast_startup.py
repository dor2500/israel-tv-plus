import os

# 1. Update PlayerScreen.kt
kt_path = "android/app/src/main/kotlin/com/israeltv/plus/ui/player/PlayerScreen.kt"
with open(kt_path, "r", encoding="utf-8") as f:
    kt_code = f.read()

assert ".setConnectTimeoutMs(6_000)" in kt_code
assert ".setReadTimeoutMs(8_000)" in kt_code
assert "/* bufferForPlaybackMs = */ 1_000," in kt_code
assert "delay(6000)" in kt_code
assert "delay(5500)" in kt_code

kt_code = kt_code.replace(".setConnectTimeoutMs(6_000)", ".setConnectTimeoutMs(3_500)")
kt_code = kt_code.replace(".setReadTimeoutMs(8_000)", ".setReadTimeoutMs(4_500)")
kt_code = kt_code.replace("/* bufferForPlaybackMs = */ 1_000,", "/* bufferForPlaybackMs = */ 500,")
kt_code = kt_code.replace("delay(6000)", "delay(3500)")
kt_code = kt_code.replace("delay(5500)", "delay(3500)")

with open(kt_path, "w", encoding="utf-8") as f:
    f.write(kt_code)
print("Updated PlayerScreen.kt for sub-5s startup & failover")

# 2. Update app.js and www/app.js
for js_path in ["app.js", "www/app.js"]:
    with open(js_path, "r", encoding="utf-8") as f:
        js_code = f.read()

    assert "}, 7500);" in js_code
    js_code = js_code.replace("}, 7500);", "}, 3500);")

    old_hls_block = "fragLoadingRetryDelay: 500,\n        capLevelToPlayerSize: S.quality === 'auto'"
    new_hls_block = ("manifestLoadingTimeOut: 3500,\n"
                     "        fragLoadingTimeOut: 4500,\n"
                     "        levelLoadingTimeOut: 3500,\n"
                     "        fragLoadingRetryDelay: 300,\n"
                     "        capLevelToPlayerSize: S.quality === 'auto'")
    
    if old_hls_block in js_code:
        js_code = js_code.replace(old_hls_block, new_hls_block)
    else:
        old_hls_block_crlf = old_hls_block.replace("\n", "\r\n")
        new_hls_block_crlf = new_hls_block.replace("\n", "\r\n")
        assert old_hls_block_crlf in js_code
        js_code = js_code.replace(old_hls_block_crlf, new_hls_block_crlf)

    with open(js_path, "w", encoding="utf-8") as f:
        f.write(js_code)
    print(f"Updated {js_path} for sub-5s startup & failover")

