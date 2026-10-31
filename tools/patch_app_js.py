import re

with open('app.js', 'r', encoding='utf-8') as f:
    code = f.read().replace('\r\n', '\n')

# 1. Update DEFAULTS to latency: 'low'
code = code.replace("latency: 'stable'", "latency: 'low'")

# 2. Update isKidsCh to pure strict kids
old_iskids = "function isKidsCh(ch) { return /ילדים/.test(ch.category) || ch.id === 'radios_kids'; }"
new_iskids = """function isKidsCh(ch) {
    if (!ch) return false;
    var cat = (ch.category || '').toLowerCase();
    var en = (ch.categoryEn || '').toLowerCase();
    var name = (ch.name || '').toLowerCase();
    var id = (ch.id || '').toLowerCase();
    if (/חדשות|ספורט|פוליטיקה|דת |כלכלה|ויוה|viva|טלנובלה|telenovela|היפ הופ|hiphop|אימה|horror|news|sport|finance/.test(cat + ' ' + name + ' ' + en + ' ' + id)) {
      return false;
    }
    if (/ילדים/.test(cat) || /^(kids|kids_he|kids_world|cartoons)$/.test(en)) return true;
    if (/ילדים|נוער|פעוטות|קטנטנים|אנימציה|מצוירים|הופ!|בייבי|דיסני|ניקלודיאון|ניק ג'וניור|ג'וניור|קארטון נטוורק|בומרנג|baby shark|ducktv|lego|transformers|cartoon network|boomerang/.test(name + ' ' + id)) return true;
    return false;
  }"""
if old_iskids in code:
    code = code.replace(old_iskids, new_iskids, 1)
    print("Patched isKidsCh successfully!")
else:
    print("Could not find old_iskids!")

# 3. Patch Hls config in loadStream (around lines 2640-2650)
old_hls_config = """      var delayed = Dub && Dub.wantsSync(ch);
      var low = S.latency === 'low' && !delayed;
      var hls = new Hls({
        enableWorker: true, lowLatencyMode: low, backBufferLength: fastMode() ? 15 : 45,
        liveSyncDurationCount: low ? 2 : 4, maxBufferLength: fastMode() ? 20 : 30,
        manifestLoadingMaxRetry: 2, levelLoadingMaxRetry: 2, fragLoadingMaxRetry: 3,
        capLevelToPlayerSize: S.quality === 'auto'
      });"""

new_hls_config = """      var delayed = Dub && Dub.wantsSync(ch);
      var low = (S.latency === 'low' || S.latency === 'realtime' || TVUI) && !delayed;
      var hls = new Hls({
        enableWorker: true,
        lowLatencyMode: true,
        backBufferLength: fastMode() ? 15 : 30,
        liveSyncDurationCount: low ? 2 : 3,
        liveMaxLatencyDurationCount: low ? 3 : 5,
        liveDurationInfinity: true,
        maxBufferLength: low ? 12 : 20,
        maxMaxBufferLength: low ? 20 : 35,
        manifestLoadingMaxRetry: 3,
        levelLoadingMaxRetry: 3,
        fragLoadingMaxRetry: 4,
        capLevelToPlayerSize: S.quality === 'auto'
      });"""

if old_hls_config in code:
    code = code.replace(old_hls_config, new_hls_config, 1)
    print("Patched Hls config in loadStream successfully!")
else:
    print("Could not find old_hls_config in loadStream!")

# 4. Patch renderTvProfilesScreen profile click to immediately switch & start watching
old_card_click = """        if (tvProfileManaging) {
          openEditProfileDialog(p);
        } else {
          applyTvProfileData(p, true);
          saveTvProfiles();
          closeTvProfiles();
        }"""

new_card_click = """        if (tvProfileManaging) {
          openEditProfileDialog(p);
        } else {
          applyTvProfileData(p, true);
          saveTvProfiles();
          closeTvProfiles();
          // Immediately enter playback / TV guide
          if (TVUI) {
            var targetCh = null;
            if (p.recents && p.recents.length) {
              for (var ri = 0; ri < p.recents.length; ri++) {
                var cIdx = indexInAll(p.recents[ri]);
                if (cIdx !== -1 && (!p.isKids || isKidsCh(state.all[cIdx]))) {
                  targetCh = state.all[cIdx];
                  break;
                }
              }
            }
            if (!targetCh && state.visible && state.visible.length) targetCh = state.visible[0];
            if (!targetCh && state.all && state.all.length) {
              targetCh = p.isKids ? state.all.find(isKidsCh) : state.all[0];
            }
            if (targetCh) {
              playChannel(targetCh);
            }
          }
        }"""

if old_card_click in code:
    code = code.replace(old_card_click, new_card_click, 1)
    print("Patched card click in renderTvProfilesScreen successfully!")
else:
    print("Could not find old_card_click!")

# 5. Patch atvRowsData for pure kids rows
old_atv_rows_data = """  function atvRowsData() {
    if (atv.search) {
      var q = atv.query.trim().toLowerCase();
      var res = !q ? [] : state.all.filter(function (c) {
        return c.name.toLowerCase().indexOf(q) !== -1 || String(c.number) === q ||
          (c.countryHe && c.countryHe.indexOf(q) !== -1) || (c.title && c.title.toLowerCase().indexOf(q) !== -1);
      });
      return [{ id: 'search', title: q ? 'תוצאות לחיפוש "' + atv.query.trim() + '" · ' + res.length : 'הקלידו שם ערוץ, מדינה או מספר', list: res }];
    }
    var buckets = {};"""

new_atv_rows_data = """  function atvRowsData() {
    if (atv.search) {
      var q = atv.query.trim().toLowerCase();
      var res = !q ? [] : state.all.filter(function (c) {
        if (S.kids && !isKidsCh(c)) return false;
        return c.name.toLowerCase().indexOf(q) !== -1 || String(c.number) === q ||
          (c.countryHe && c.countryHe.indexOf(q) !== -1) || (c.title && c.title.toLowerCase().indexOf(q) !== -1);
      });
      return [{ id: 'search', title: q ? 'תוצאות לחיפוש ילדים "' + atv.query.trim() + '" · ' + res.length : 'הקלידו שם ערוץ ילדים', list: res }];
    }

    if (S.kids) {
      var kidsAll = state.all.filter(isKidsCh);
      var kidsHe = kidsAll.filter(function (c) { return c.country === 'IL' || c.lang === 'he'; });
      var kidsWorld = kidsAll.filter(function (c) { return c.country !== 'IL' && c.lang !== 'he'; });
      var kidsMine = [];
      var seenK = {};
      (state.recents || []).concat(state.favorites || []).forEach(function (id) {
        var idx = indexInAll(id);
        if (idx !== -1 && isKidsCh(state.all[idx]) && !seenK[id]) {
          seenK[id] = 1;
          kidsMine.push(state.all[idx]);
        }
      });
      var kRows = [];
      if (kidsMine.length) kRows.push({ id: 'mine', title: '🧸 מועדפים לאחרונה (ילדים)', list: kidsMine });
      if (kidsHe.length) kRows.push({ id: 'kids_he', title: '🇮🇱 ערוצי ילדים ונוער בעברית', list: kidsHe });
      if (kidsWorld.length) kRows.push({ id: 'kids_world', title: '🌍 ערוצי ילדים וסדרות מצוירות מהעולם', list: kidsWorld });
      return kRows;
    }

    var buckets = {};"""

if old_atv_rows_data in code:
    code = code.replace(old_atv_rows_data, new_atv_rows_data, 1)
    print("Patched atvRowsData successfully!")
else:
    print("Could not find old_atv_rows_data!")

# 6. Patch onFatal for broadcast standby apology slide
old_on_fatal = """    var insecure = location.protocol === 'https:' && /^http:/i.test(streamUrl(ch));
    el.perrorTitle.textContent = insecure ? 'הערוץ פועל באפליקציה' : 'השידור לא זמין כרגע';
    el.perrorDesc.textContent = insecure
      ? 'הערוץ משדר ב-HTTP, ודפדפנים חוסמים שידור כזה מאתר מאובטח. באפליקציות (טלוויזיה, אנדרואיד, מחשב) הוא פועל.'
      : 'לא הצלחנו להתחבר ל"' + ch.name + '" (' + (reason || 'שגיאת רשת') + '). ייתכן שהשידור כבוי זמנית – נסו שוב או עברו ערוץ.';"""

new_on_fatal = """    var insecure = location.protocol === 'https:' && /^http:/i.test(streamUrl(ch));
    var chLogo = $('perror-ch-logo');
    if (chLogo) {
      chLogo.src = ch.logo || 'icon.png';
      chLogo.onerror = function () { chLogo.src = 'icon.png'; };
    }
    el.perrorTitle.textContent = insecure ? 'הערוץ פועל באפליקציה' : 'סליחה, תקלה זמנית בשידור';
    el.perrorDesc.textContent = insecure
      ? 'הערוץ משדר ב-HTTP, ודפדפנים חוסמים שידור כזה מאתר מאובטח. באפליקציות טלוויזיה, אנדרואיד ומחשב השידור פועל תקין.'
      : 'אנו מתנצלים על אי הנוחות. השידור בערוץ "' + ch.name + '" אינו זמין כעת. צוות הערוץ פועל להחזרת השידור בהקדם.';"""

if old_on_fatal in code:
    code = code.replace(old_on_fatal, new_on_fatal, 1)
    print("Patched onFatal successfully!")
else:
    print("Could not find old_on_fatal!")

with open('app.js', 'w', encoding='utf-8', newline='\n') as f:
    f.write(code)
print("Finished patching app.js!")
