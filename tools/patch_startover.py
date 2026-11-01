with open("app.js", "r", encoding="utf-8") as f:
    lines = f.read().split("\n")

repl = """    if (CATCHUP[ch.id] || CATCHUP[(ch.id || '').toLowerCase()]) {
      var nowMin = Date.now() / 60000;
      var epgKey = (typeof CATCHUP_EPG !== 'undefined' && CATCHUP_EPG[ch.id]) || ch.id;
      var epgList = (window.ITV_EPG && window.ITV_EPG.channels && window.ITV_EPG.channels[epgKey]) || [];
      var curProg = null;
      for (var pi = 0; pi < epgList.length; pi++) {
        if (epgList[pi][0] <= nowMin && nowMin < epgList[pi][0] + epgList[pi][1]) {
          curProg = epgList[pi];
          break;
        }
      }
      if (curProg && (nowMin - curProg[0] > 1)) {
        items.push({
          ico: '⏮️',
          label: 'התחל תכנית מחדש: ' + curProg[2],
          act: function () { playCatchup(ch, curProg[0], curProg[0] + curProg[1]); }
        });
      }
      items.push({ ico: '⏮️', label: 'קאץ\' אפ ושידורים קודמים', act: function () { openCatchupModal(ch); } });
    }"""

for idx, line in enumerate(lines):
    if "if (CATCHUP[ch.id] || CATCHUP[(ch.id" in line:
        print(f"Found line {idx+1}: {line}")
        lines[idx:idx+3] = repl.split("\n")
        break

with open("app.js", "w", encoding="utf-8") as f:
    f.write("\n".join(lines))
print("Patched app.js successfully!")

