import os

p1 = 'android/app/src/main/kotlin/com/israeltv/plus/ui/player/PlayerScreen.kt'
txt1 = open(p1, 'r', encoding='utf-8').read()
old1 = '.setReadTimeoutMs(9_000)'
new1 = '.setReadTimeoutMs(9_000)\n        .setDefaultRequestProperties(mapOf("Referer" to "https://stmify.com/"))'
if old1 in txt1 and 'setDefaultRequestProperties' not in txt1:
    txt1 = txt1.replace(old1, new1, 1)
    open(p1, 'w', encoding='utf-8', newline='').write(txt1)
    print('Patched PlayerScreen.kt')
else:
    print('PlayerScreen.kt already patched or old1 not found')

p2 = 'android/app/src/main/kotlin/com/israeltv/plus/ui/screens/HomeScreen.kt'
txt2 = open(p2, 'r', encoding='utf-8').read()
old2 = 'DefaultHttpDataSource.Factory()\n            .setUserAgent(USER_AGENT)'
if old2 not in txt2:
    old2 = 'DefaultHttpDataSource.Factory()\r\n            .setUserAgent(USER_AGENT)'
new2 = 'DefaultHttpDataSource.Factory()\n            .setUserAgent(USER_AGENT)\n            .setDefaultRequestProperties(mapOf("Referer" to "https://stmify.com/"))'
if 'setDefaultRequestProperties' not in txt2:
    for target in ['DefaultHttpDataSource.Factory().setUserAgent(USER_AGENT)', 'DefaultHttpDataSource.Factory()\n            .setUserAgent(USER_AGENT)', 'DefaultHttpDataSource.Factory()\r\n            .setUserAgent(USER_AGENT)']:
        if target in txt2:
            txt2 = txt2.replace(target, target + '\n            .setDefaultRequestProperties(mapOf("Referer" to "https://stmify.com/"))', 1)
            open(p2, 'w', encoding='utf-8', newline='').write(txt2)
            print('Patched HomeScreen.kt')
            break

p3 = 'wear/app/src/main/kotlin/com/israeltv/plus/wear/Playback.kt'
txt3 = open(p3, 'r', encoding='utf-8').read()
old3 = '.setAllowCrossProtocolRedirects(true)'
if old3 in txt3 and 'setDefaultRequestProperties' not in txt3:
    txt3 = txt3.replace(old3, old3 + '\n            .setDefaultRequestProperties(mapOf("Referer" to "https://stmify.com/"))', 1)
    open(p3, 'w', encoding='utf-8', newline='').write(txt3)
    print('Patched Playback.kt')

