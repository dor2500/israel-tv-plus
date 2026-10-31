import sys

with open('app.js', 'r', encoding='utf-8') as f:
    text = f.read()

# 1. stopPlayback:
if 'state.catchup = state.catchupNext = null;' not in text:
    text = text.replace('state.preview = null;', 'state.preview = null;\n    state.catchup = state.catchupNext = null;', 1)
    print('1. Added catchup nulling in stopPlayback')

# 2. loadStream:
if 'var cu = state.catchupNext' not in text:
    old_load = '''  function loadStream(ch) {
    state.candidates = candidateUrlsOf(ch);'''
    new_load = '''  function loadStream(ch) {
    var cu = state.catchupNext && state.catchupNext.ch === ch ? state.catchupNext : null;
    state.catchupNext = null;
    state.catchup = cu;
    if (Guide) Guide.fill();
    if (cu) {
      state.candidates = [cu.url];
      state.candIdx = 0;
      updateServerButton();
      loadResolved(ch, cu.url);
      return;
    }
    state.candidates = candidateUrlsOf(ch);'''
    text = text.replace(old_load, new_load, 1)
    print('2. Updated loadStream for catchup')

# 3. LEVEL_LOADED catchup check:
if 'if (state.catchup) {' not in text:
    old_hls_load = 'hls.on(Hls.Events.LEVEL_LOADED, function (ev, data) {'
    new_hls_load = '''hls.on(Hls.Events.LEVEL_LOADED, function (ev, data) {
        if (state.catchup) {
          if (!adjusted) {
            adjusted = true;
            if (!catchupSeek(hls, data.details)) onFatal(ch, 'התכנית כבר לא זמינה');
          }
          return;
        }'''
    text = text.replace(old_hls_load, new_hls_load, 1)
    print('3. Added LEVEL_LOADED catchup check')

# 4. fillOsd:
if 'if (Guide) Guide.fill();' not in text:
    old_fill = 'function fillOsd(ch) {'
    new_fill = 'function fillOsd(ch) {\n    if (Guide) Guide.fill();'
    text = text.replace(old_fill, new_fill, 1)
    print('4. Added Guide.fill to fillOsd')

# 5. showOsd hide timer:
if 'if (Guide) Guide.onHide();' not in text:
    target_pattern = "state.zone = 'video';\n        markFocus();\n      }\n      el.osd.classList.add('off');"
    replacement_pattern = "state.zone = 'video';\n        markFocus();\n      }\n      if (Guide) Guide.onHide();\n      el.osd.classList.add('off');"
    if target_pattern in text:
        text = text.replace(target_pattern, replacement_pattern, 1)
        print('5. Added Guide.onHide to showOsd timer')
    else:
        print('5. target_pattern not found for showOsd')

# 6. playerMenu:
if "'לוח שידורים'" not in text:
    old_pmenu = 'var items = [];'
    new_pmenu = "var items = [];\n    if (Guide) items.push({ ico: '▦', label: 'לוח שידורים', act: Guide.open });"
    text = text.replace(old_pmenu, new_pmenu, 1)
    print('6. Added Guide.open to playerMenu')

# 7. setupInput: call setupGuide()
if 'setupGuide();' not in text:
    old_setup = 'function setupInput() {'
    new_setup = 'function setupInput() {\n    setupGuide();'
    text = text.replace(old_setup, new_setup, 1)
    print('7. Added setupGuide() in setupInput')

# 8. ATV_TABS:
if "'guide'" not in text:
    text = text.replace("['מועדפים', 'mine'], ['חיפוש', 'search']", "['מועדפים', 'mine'], ['לוח שידורים', 'guide'], ['חיפוש', 'search']", 1)
    print('8. Added guide to ATV_TABS')

if "t[1] === 'guide'" not in text:
    text = text.replace("t[1] === 'multiview' ||", "t[1] === 'multiview' || t[1] === 'guide' ||", 1)
    text = text.replace("else if (t === 'settings') openSettings();", "else if (t === 'guide') { if (Guide) Guide.open(); }\n      else if (t === 'settings') openSettings();", 1)
    print('8b. Added guide handling in atvRenderPill and atvOk')

# 9. Key normalization and remote wheel fixes:
# In onKeyDown:
if 'code === 66' not in text:
    text = text.replace('if (code === 23) code = 13;', 'if (code === 23 || code === 66 || code === 160 || code === 96) code = 13;', 2)
    print('9. Key normalization applied for 23, 66, 160, 96')

# In onKeyDown OK handling:
old_ok = '''    // OK / Enter: short press on release, long press = options
    if (code === 13) {
      if (e.preventDefault) e.preventDefault();
      if (!state.okUpSeen) { shortOk(); return; }
      if (state.okDown) return; // key repeat while held
      state.okDown = true;
      state.okLong = false;
      state.okTimer = setTimeout(function () {
        state.okTimer = null;
        state.okLong = true;
        longOk();
      }, 650);
      return;
    }'''

new_ok = '''    // OK / Enter: on TV remote / wheel click, execute shortOk immediately without delay or stuck states
    if (code === 13) {
      if (e.preventDefault) e.preventDefault();
      if (TVUI) {
        shortOk();
        return;
      }
      if (!state.okUpSeen) { shortOk(); return; }
      if (state.okDown) {
        state.okDown = false;
        shortOk();
        return;
      }
      state.okDown = true;
      state.okLong = false;
      if (state.okTimer) clearTimeout(state.okTimer);
      state.okTimer = setTimeout(function () {
        state.okTimer = null;
        state.okDown = false;
        state.okLong = true;
        longOk();
      }, 550);
      return;
    }'''

if old_ok in text:
    text = text.replace(old_ok, new_ok, 1)
    print('10. Replaced OK handling for TV / wheel click')
else:
    print('10. Notice: old_ok not found exactly')

# Guide key shortcut in onKeyDown:
if 'code === 71 || code === 458' not in text:
    old_shortcuts = "if (code === 83) { openSettings(); return; }           // S"
    new_shortcuts = "if (code === 83) { openSettings(); return; }           // S\n      if ((code === 71 || code === 458) && Guide) { Guide.open(); return; } // G / the remote's Guide key"
    text = text.replace(old_shortcuts, new_shortcuts, 1)
    print('11. Added Guide key shortcut (G / 458)')

# Switch overlays in onKeyDown:
if "case 'tvguide': if (Guide) Guide.key(code); return;" not in text:
    text = text.replace("case 'player': playerKey(code); return;", "case 'player': playerKey(code); return;\n      case 'tvguide': if (Guide) Guide.key(code); return;", 1)
    print('12. Added tvguide overlay in onKeyDown')

# zapOrPage:
if "state.overlay === 'tvguide'" not in text:
    text = text.replace("if (state.overlay === 'player') { zap(dir); return; }", "if (state.overlay === 'player') { zap(dir); return; }\n    if (state.overlay === 'tvguide') { if (Guide) Guide.page(dir > 0 ? -1 : 1); return; }", 1)
    print('13. Added tvguide page in zapOrPage')

# shortOk:
old_sok = '''    switch (state.overlay) {
      case 'tv-profiles': tvProfilesOk(); return;
      case 'multiview': multiViewKey(13, { preventDefault: function () {} }); return;
      case 'settings': settingsKey(13, { preventDefault: function () {} }); return;
      case 'dialog': pressDialog(state.dialogIdx); return;
      case 'menu': runMenuItem(state.menuIdx); return;
      case 'drawer': var i = state.drawerIdx; closeDrawer(true); playIndex(i); return;
      case 'player':
        if (state.zone === 'perror') { errorButtons()[state.perrIdx || 0].click(); return; }
        if (el.osd.classList.contains('off')) showOsd(); else playerMenu();
        return;
    }'''

new_sok = '''    switch (state.overlay) {
      case 'tv-profiles': tvProfilesOk(); return;
      case 'multiview': multiViewKey(13, { preventDefault: function () {} }); return;
      case 'settings': settingsKey(13, { preventDefault: function () {} }); return;
      case 'dialog': pressDialog(state.dialogIdx); return;
      case 'menu': runMenuItem(state.menuIdx); return;
      case 'drawer': var i = state.drawerIdx; closeDrawer(true); playIndex(i); return;
      case 'tvguide': if (Guide) Guide.key(13); return;
      case 'player':
        if (state.zone === 'perror') { errorButtons()[state.perrIdx || 0].click(); return; }
        if (el.osd.classList.contains('off')) showOsd(); else if (Guide) Guide.ok(); else playerMenu();
        return;
    }'''

if old_sok in text:
    text = text.replace(old_sok, new_sok, 1)
    print('14. Updated shortOk for tvguide and Guide.ok()')
else:
    print('14. Notice: old_sok not found exactly')

# playerKey:
old_pkey = '''  function playerKey(code) {
    showOsd();
    if (state.zone === 'perror') {'''

new_pkey = '''  function playerKey(code) {
    var wasOff = el.osd.classList.contains('off');
    showOsd();
    if (state.zone === 'perror') {'''

if old_pkey in text:
    text = text.replace(old_pkey, new_pkey, 1)
    # also add Guide.playerKey after perror check:
    old_after_perror = '''      markFocus();
      return;
    }'''
    new_after_perror = '''      markFocus();
      return;
    }
    if (Guide && Guide.playerKey(code, wasOff)) return;'''
    text = text.replace(old_after_perror, new_after_perror, 1)
    print('15. Updated playerKey with Guide.playerKey')
else:
    print('15. Notice: old_pkey not found exactly')

# Add global wheel handler in setupInput:
if 'wheelGlobalAt' not in text:
    wheel_code = '''    var wheelGlobalAt = 0;
    window.addEventListener('wheel', function (e) {
      var now = Date.now();
      if (now - wheelGlobalAt < 160) return;
      wheelGlobalAt = now;
      var dy = e.deltaY, dx = e.deltaX;
      if (Math.abs(dy) < 5 && Math.abs(dx) < 5) return;
      
      if (state.overlay === 'player') {
        if (Guide && !el.osd.classList.contains('off')) {
          Guide.playerKey(dy > 0 ? 37 : 39, false);
        } else {
          zap(dy > 0 ? (S.upNext ? 1 : -1) : (S.upNext ? -1 : 1));
        }
        return;
      }
      if (state.overlay === 'tvguide' && Guide) {
        Guide.key(dy > 0 ? 40 : 38);
        return;
      }
      if (!state.overlay && state.zone === 'atv') {
        if (Math.abs(dx) > Math.abs(dy) || e.shiftKey) atvKey((dx || dy) > 0 ? 37 : 39);
        else atvKey(dy > 0 ? 40 : 38);
        return;
      }
    }, { passive: true });
'''
    text = text.replace('window.addEventListener(\'keydown\', onKeyDown);', wheel_code + '    window.addEventListener(\'keydown\', onKeyDown);', 1)
    print('16. Added global wheel handler')

with open('app.js', 'w', encoding='utf-8') as f:
    f.write(text)

print('All changes applied successfully!')

