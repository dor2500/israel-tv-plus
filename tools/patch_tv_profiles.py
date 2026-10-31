import re

with open("app.js", "r", encoding="utf-8") as f:
    code = f.read()

# 1. Update DEFAULTS to include profileOnStart
if "profileOnStart: false" not in code:
    code = code.replace(
        "kidsPin: '', autoUpdate: true, liveUpdates: true, avatar: '1f60e',",
        "kidsPin: '', autoUpdate: true, liveUpdates: true, avatar: '1f60e', profileOnStart: false,"
    )

# 2. Add 'profiles' to ATV_TABS
if "['פרופיל', 'profiles']" not in code:
    code = code.replace(
        "var ATV_TABS = [['צפייה כעת', 'top'],",
        "var ATV_TABS = [['פרופיל', 'profiles'], ['צפייה כעת', 'top'],"
    )

# 3. Update atvRenderPill to handle profiles tab
old_atv_filter = "return t[1] === 'top' || t[1] === 'search' || t[1] === 'multiview' || t[1] === 'settings' || atvRowIndex(t[1]) !== -1 || (atv.search && t[1] !== 'mine');"
new_atv_filter = "return t[1] === 'profiles' || t[1] === 'top' || t[1] === 'search' || t[1] === 'multiview' || t[1] === 'settings' || atvRowIndex(t[1]) !== -1 || (atv.search && t[1] !== 'mine');"
code = code.replace(old_atv_filter, new_atv_filter)

old_atv_pill_map = """      if (id === 'search' || id === 'multiview' || id === 'settings') {
        var ico = id === 'search' ? I('search') : id === 'multiview' ? I('multiview') : I('settings');
        return '<button class="atv-tab ico-only" data-i="' + i + '" aria-label="' + esc(t[0]) + '" title="' + esc(t[0]) + '">' + ico + '</button>';
      }"""

new_atv_pill_map = """      if (id === 'profiles') {
        var p = getActiveTvProfile();
        var av = p ? avatarUrl(p.avatar || '1f60e') : avatarUrl('1f60e');
        var nm = p ? p.name : 'פרופיל';
        return '<button class="atv-tab atv-tab-prof" data-i="' + i + '" title="מי צופה? - החלף פרופיל"><img class="atv-prof-ico" src="' + av + '" alt=""><span>' + esc(nm) + '</span></button>';
      }
      if (id === 'search' || id === 'multiview' || id === 'settings') {
        var ico = id === 'search' ? I('search') : id === 'multiview' ? I('multiview') : I('settings');
        return '<button class="atv-tab ico-only" data-i="' + i + '" aria-label="' + esc(t[0]) + '" title="' + esc(t[0]) + '">' + ico + '</button>';
      }"""

code = code.replace(old_atv_pill_map, new_atv_pill_map)

# 4. In atvOk, handle t === 'profiles'
old_atv_ok = """      var t = atv.tabs[atv.tab] && atv.tabs[atv.tab][1];
      if (t === 'search') atvOpenSearch();"""

new_atv_ok = """      var t = atv.tabs[atv.tab] && atv.tabs[atv.tab][1];
      if (t === 'profiles') openTvProfiles();
      else if (t === 'search') atvOpenSearch();"""

code = code.replace(old_atv_ok, new_atv_ok)

# 5. In onKeyDown switch (state.overlay)
old_switch_key = """    switch (state.overlay) {
      case 'multiview': multiViewKey(code, e); return;"""

new_switch_key = """    switch (state.overlay) {
      case 'tv-profiles': tvProfilesKey(code); return;
      case 'multiview': multiViewKey(code, e); return;"""

code = code.replace(old_switch_key, new_switch_key)

# 6. In shortOk switch (state.overlay)
old_switch_ok = """    switch (state.overlay) {
      case 'multiview': multiViewKey(13, { preventDefault: function () {} }); return;"""

new_switch_ok = """    switch (state.overlay) {
      case 'tv-profiles': tvProfilesOk(); return;
      case 'multiview': multiViewKey(13, { preventDefault: function () {} }); return;"""

code = code.replace(old_switch_ok, new_switch_ok)

# 7. Add full TV Profiles Engine right before `// ================================================================== phone: scroll physics`
tv_profiles_code = """
  // ================================================================== TV Profiles Engine (Netflix / Disney+ Style)
  var TV_PROFILES_KEY = 'itv_tv_profiles';
  var TV_ACTIVE_KEY = 'itv_tv_active_profile_id';
  var tvProfiles = [];
  var tvActiveProfile = null;
  var tvProfileManaging = false;
  var tvProfileZone = 'cards'; // 'cards' | 'actions'
  var tvProfileIdx = 0;

  function loadTvProfiles() {
    try {
      var raw = localStorage.getItem(TV_PROFILES_KEY);
      if (raw) tvProfiles = JSON.parse(raw);
    } catch (e) { tvProfiles = []; }
    if (!Array.isArray(tvProfiles) || !tvProfiles.length) {
      tvProfiles = [
        {
          id: 'p_main',
          name: 'ראשי',
          avatar: (S && S.avatar) || '1f60e',
          color: 'gold',
          isKids: false,
          favorites: state.favorites ? state.favorites.slice() : [],
          recents: state.recents ? state.recents.slice() : []
        },
        {
          id: 'p_kids',
          name: 'ילדים',
          avatar: '1f981',
          color: 'amber',
          isKids: true,
          favorites: ['partner_kids_il', 'partner_toddlers_luli_il', 'hop_kids_israel', 'junior_plus_kids_il', 'baby_israel_tv', 'kan_hinuchit'],
          recents: []
        }
      ];
      saveTvProfiles();
    }
    var activeId = localStorage.getItem(TV_ACTIVE_KEY);
    tvActiveProfile = tvProfiles.find(function (p) { return p.id === activeId; }) || tvProfiles[0];
    applyTvProfileData(tvActiveProfile, false);
  }

  function saveTvProfiles() {
    if (tvActiveProfile) {
      tvActiveProfile.favorites = state.favorites ? state.favorites.slice() : [];
      tvActiveProfile.recents = state.recents ? state.recents.slice() : [];
    }
    try {
      localStorage.setItem(TV_PROFILES_KEY, JSON.stringify(tvProfiles));
      if (tvActiveProfile) localStorage.setItem(TV_ACTIVE_KEY, tvActiveProfile.id);
    } catch (e) {}
  }

  function getActiveTvProfile() {
    if (!tvActiveProfile) loadTvProfiles();
    return tvActiveProfile;
  }

  function applyTvProfileData(p, notify) {
    if (!p) return;
    tvActiveProfile = p;
    try { localStorage.setItem(TV_ACTIVE_KEY, p.id); } catch (e) {}
    state.favorites = Array.isArray(p.favorites) ? p.favorites.slice() : [];
    state.recents = Array.isArray(p.recents) ? p.recents.slice() : [];
    lsSet('israel_tv_favorites', state.favorites);
    lsSet('israel_tv_recents', state.recents);
    S.avatar = p.avatar || '1f60e';
    S.kids = !!p.isKids;
    updateAvatar();
    if (typeof atvRenderPill === 'function') atvRenderPill();
    if (typeof buildCategories === 'function') buildCategories();
    if (typeof filterList === 'function') filterList();
    if (typeof atvRender === 'function') atvRender();
    if (notify) toast('שלום, ' + p.name + '!');
  }

  function renderTvProfilesScreen() {
    var grid = $('tv-profiles-grid');
    if (!grid) return;
    var html = '';
    tvProfiles.forEach(function (p, idx) {
      var isCurrent = tvActiveProfile && tvActiveProfile.id === p.id;
      var badge = p.isKids ? '<span class="net-prof-badge">🧸 ילדים</span>' : '';
      var editIcon = tvProfileManaging ? '<span class="net-prof-edit-badge">✏️ עריכה</span>' : '';
      html += '<div class="net-prof-card" data-idx="' + idx + '" data-pid="' + esc(p.id) + '">' +
        '<img class="net-prof-avatar" src="' + avatarUrl(p.avatar || '1f60e') + '" alt="">' +
        editIcon +
        '<div class="net-prof-name">' + esc(p.name) + '</div>' +
        badge +
        (isCurrent && !tvProfileManaging ? '<div class="net-prof-active-dot">● פעיל</div>' : '') +
        '</div>';
    });
    if (tvProfiles.length < 8) {
      html += '<div class="net-prof-card net-prof-add" data-idx="' + tvProfiles.length + '" data-act="add">' +
        '<div class="net-prof-add-ico">+</div>' +
        '<div class="net-prof-name">הוסף פרופיל</div>' +
        '</div>';
    }
    grid.innerHTML = html;

    var btnManage = $('btn-tv-prof-manage');
    var btnDone = $('btn-tv-prof-done');
    if (btnManage && btnDone) {
      toggleClass(btnManage, 'hidden', tvProfileManaging);
      toggleClass(btnDone, 'hidden', !tvProfileManaging);
    }

    Array.prototype.forEach.call(grid.children, function (card) {
      card.addEventListener('click', function () {
        var act = card.getAttribute('data-act');
        if (act === 'add') {
          openAddProfileDialog();
          return;
        }
        var pid = card.getAttribute('data-pid');
        var p = tvProfiles.find(function (item) { return item.id === pid; });
        if (!p) return;
        if (tvProfileManaging) {
          openEditProfileDialog(p);
        } else {
          applyTvProfileData(p, true);
          saveTvProfiles();
          closeTvProfiles();
        }
      });
    });
  }

  function markTvProfilesFocus() {
    var grid = $('tv-profiles-grid');
    if (!grid) return;
    Array.prototype.forEach.call(grid.querySelectorAll('.net-prof-card'), function (c) {
      c.classList.remove('focused');
    });
    var actBtns = $('tv-profiles-actions').querySelectorAll('button:not(.hidden)');
    Array.prototype.forEach.call(actBtns, function (b) {
      b.classList.remove('focused');
    });

    if (tvProfileZone === 'cards') {
      var card = grid.children[tvProfileIdx];
      if (card) {
        card.classList.add('focused');
        try { card.scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'center' }); } catch (e) {}
      }
    } else {
      var btn = actBtns[tvProfileIdx];
      if (btn) btn.classList.add('focused');
    }
  }

  function openTvProfiles() {
    if (!tvProfiles.length) loadTvProfiles();
    tvProfileManaging = false;
    tvProfileZone = 'cards';
    var curIdx = tvProfiles.findIndex(function (p) { return p.id === (tvActiveProfile && tvActiveProfile.id); });
    tvProfileIdx = curIdx >= 0 ? curIdx : 0;
    state.overlay = 'tv-profiles';
    renderTvProfilesScreen();
    var elScreen = $('tv-profiles');
    if (elScreen) elScreen.classList.remove('hidden');
    markTvProfilesFocus();
  }

  function closeTvProfiles() {
    var elScreen = $('tv-profiles');
    if (elScreen) elScreen.classList.add('hidden');
    state.overlay = null;
    markFocus();
  }

  function tvProfilesKey(code) {
    var totalCards = tvProfiles.length + (tvProfiles.length < 8 ? 1 : 0);
    if (isBack(code)) {
      if (tvProfileManaging) {
        tvProfileManaging = false;
        renderTvProfilesScreen();
        markTvProfilesFocus();
        return;
      }
      closeTvProfiles();
      return;
    }
    if (code === 37) {
      if (tvProfileZone === 'cards') {
        tvProfileIdx = (tvProfileIdx + 1) % totalCards;
        markTvProfilesFocus();
      } else {
        var actBtns = $('tv-profiles-actions').querySelectorAll('button:not(.hidden)');
        tvProfileIdx = Math.min(tvProfileIdx + 1, actBtns.length - 1);
        markTvProfilesFocus();
      }
    } else if (code === 39) {
      if (tvProfileZone === 'cards') {
        tvProfileIdx = (tvProfileIdx - 1 + totalCards) % totalCards;
        markTvProfilesFocus();
      } else {
        tvProfileIdx = Math.max(tvProfileIdx - 1, 0);
        markTvProfilesFocus();
      }
    } else if (code === 40) {
      if (tvProfileZone === 'cards') {
        tvProfileZone = 'actions';
        tvProfileIdx = 0;
        markTvProfilesFocus();
      }
    } else if (code === 38) {
      if (tvProfileZone === 'actions') {
        tvProfileZone = 'cards';
        tvProfileIdx = Math.min(tvProfileIdx, totalCards - 1);
        markTvProfilesFocus();
      }
    }
  }

  function tvProfilesOk() {
    if (tvProfileZone === 'cards') {
      var cards = $('tv-profiles-grid').children;
      var c = cards[tvProfileIdx];
      if (c) c.click();
    } else {
      var actBtns = $('tv-profiles-actions').querySelectorAll('button:not(.hidden)');
      var b = actBtns[tvProfileIdx];
      if (b) b.click();
    }
  }

  function openAddProfileDialog() {
    var chosenAv = AVATARS[tvProfiles.length % AVATARS.length];
    var html = '<div style="margin-bottom:1.5rem;">' +
      '<label style="display:block;margin-bottom:.5rem;font-weight:700;">שם הפרופיל:</label>' +
      '<input id="prof-add-name" type="text" class="dlg-focus" value="פרופיל ' + (tvProfiles.length + 1) + '" style="width:100%;padding:.8rem;border-radius:1rem;background:rgba(255,255,255,.1);border:1px solid rgba(255,255,255,.2);color:#fff;font-size:1.3rem;">' +
      '</div>' +
      '<div style="margin-bottom:1.5rem;">' +
      '<label style="display:block;margin-bottom:.5rem;font-weight:700;">בחירת אווטאר:</label>' +
      '<div class="av-grid">' + AVATARS.map(function (c) {
        return '<button class="btn dlg-focus av-opt' + (c === chosenAv ? ' on' : '') + '" data-av="' + c + '"><img alt="" src="' + avatarUrl(c) + '"></button>';
      }).join('') + '</div>' +
      '</div>' +
      '<div>' +
      '<label style="display:inline-flex;align-items:center;gap:.8rem;cursor:pointer;font-size:1.2rem;font-weight:700;">' +
      '<input id="prof-add-kids" type="checkbox" style="transform:scale(1.4);"> פרופיל ילדים (מציג תכני ילדים בלבד)' +
      '</label>' +
      '</div>';

    openDialog({
      ico: '👤',
      title: 'הוספת פרופיל חדש',
      html: html,
      buttons: [
        { label: 'ביטול' },
        {
          label: 'שמור פרופיל',
          primary: true,
          act: function () {
            var nameInp = $('prof-add-name');
            var kidsInp = $('prof-add-kids');
            var name = (nameInp && nameInp.value.trim()) || ('פרופיל ' + (tvProfiles.length + 1));
            var isKids = kidsInp ? kidsInp.checked : false;
            var newProf = {
              id: 'p_' + Date.now(),
              name: name,
              avatar: chosenAv,
              color: isKids ? 'amber' : 'arctic',
              isKids: isKids,
              favorites: isKids ? ['partner_kids_il', 'partner_toddlers_luli_il', 'hop_kids_israel', 'junior_plus_kids_il', 'baby_israel_tv'] : [],
              recents: []
            };
            tvProfiles.push(newProf);
            applyTvProfileData(newProf, true);
            saveTvProfiles();
            openTvProfiles();
          }
        }
      ]
    });

    Array.prototype.forEach.call(el.dialogBody.querySelectorAll('.av-opt'), function (b) {
      b.onclickAction = function () {
        chosenAv = b.getAttribute('data-av');
        Array.prototype.forEach.call(el.dialogBody.querySelectorAll('.av-opt'), function (x) { toggleClass(x, 'on', x === b); });
      };
      b.addEventListener('click', function (e) { e.stopPropagation(); b.onclickAction(); });
    });
  }

  function openEditProfileDialog(p) {
    var chosenAv = p.avatar || '1f60e';
    var html = '<div style="margin-bottom:1.5rem;">' +
      '<label style="display:block;margin-bottom:.5rem;font-weight:700;">שם הפרופיל:</label>' +
      '<input id="prof-edit-name" type="text" class="dlg-focus" value="' + esc(p.name) + '" style="width:100%;padding:.8rem;border-radius:1rem;background:rgba(255,255,255,.1);border:1px solid rgba(255,255,255,.2);color:#fff;font-size:1.3rem;">' +
      '</div>' +
      '<div style="margin-bottom:1.5rem;">' +
      '<label style="display:block;margin-bottom:.5rem;font-weight:700;">בחירת אווטאר:</label>' +
      '<div class="av-grid">' + AVATARS.map(function (c) {
        return '<button class="btn dlg-focus av-opt' + (c === chosenAv ? ' on' : '') + '" data-av="' + c + '"><img alt="" src="' + avatarUrl(c) + '"></button>';
      }).join('') + '</div>' +
      '</div>' +
      '<div>' +
      '<label style="display:inline-flex;align-items:center;gap:.8rem;cursor:pointer;font-size:1.2rem;font-weight:700;">' +
      '<input id="prof-edit-kids" type="checkbox" ' + (p.isKids ? 'checked' : '') + ' style="transform:scale(1.4);"> פרופיל ילדים (מציג תכני ילדים בלבד)' +
      '</label>' +
      '</div>';

    var btns = [
      { label: 'ביטול' },
      {
        label: 'שמור שינויים',
        primary: true,
        act: function () {
          var nameInp = $('prof-edit-name');
          var kidsInp = $('prof-edit-kids');
          p.name = (nameInp && nameInp.value.trim()) || p.name;
          p.avatar = chosenAv;
          p.isKids = kidsInp ? kidsInp.checked : false;
          if (tvActiveProfile && tvActiveProfile.id === p.id) {
            applyTvProfileData(p, false);
          }
          saveTvProfiles();
          openTvProfiles();
        }
      }
    ];

    if (tvProfiles.length > 1) {
      btns.unshift({
        label: 'מחק פרופיל',
        act: function () {
          confirmBox('האם למחוק את הפרופיל "' + p.name + '"?', function () {
            tvProfiles = tvProfiles.filter(function (x) { return x.id !== p.id; });
            if (tvActiveProfile.id === p.id) {
              applyTvProfileData(tvProfiles[0], true);
            }
            saveTvProfiles();
            openTvProfiles();
          });
        }
      });
    }

    openDialog({
      ico: '✏️',
      title: 'עריכת פרופיל: ' + p.name,
      html: html,
      buttons: btns
    });

    Array.prototype.forEach.call(el.dialogBody.querySelectorAll('.av-opt'), function (b) {
      b.onclickAction = function () {
        chosenAv = b.getAttribute('data-av');
        Array.prototype.forEach.call(el.dialogBody.querySelectorAll('.av-opt'), function (x) { toggleClass(x, 'on', x === b); });
      };
      b.addEventListener('click', function (e) { e.stopPropagation(); b.onclickAction(); });
    });
  }
"""

if "function loadTvProfiles" not in code:
    code = code.replace(
        "  // ================================================================== phone: scroll physics",
        tv_profiles_code + "\n  // ================================================================== phone: scroll physics"
    )

# 8. In setupInput, attach events for profile buttons
setup_input_target = "    el.btnSettings.addEventListener('click', function () { openSettings(); });"
setup_input_addition = """    var btnManage = $('btn-tv-prof-manage');
    if (btnManage) btnManage.addEventListener('click', function () { tvProfileManaging = true; renderTvProfilesScreen(); markTvProfilesFocus(); });
    var btnDone = $('btn-tv-prof-done');
    if (btnDone) btnDone.addEventListener('click', function () { tvProfileManaging = false; renderTvProfilesScreen(); markTvProfilesFocus(); });
    var btnClose = $('btn-tv-prof-close');
    if (btnClose) btnClose.addEventListener('click', function () { closeTvProfiles(); });
"""
if "btn-tv-prof-manage" not in code:
    code = code.replace(setup_input_target, setup_input_addition + setup_input_target)

# 9. In start(), call loadTvProfiles() and optionally openTvProfiles() if profileOnStart
start_target = "    readDeviceInfo();"
start_addition = """    loadTvProfiles();
    if (TVUI && S.profileOnStart) setTimeout(openTvProfiles, 300);
"""
if "loadTvProfiles();" not in code:
    code = code.replace(start_target, start_addition + start_target)

# 10. In toggleFav and recordRecent, call saveTvProfiles()
if "saveTvProfiles();" not in code:
    code = code.replace(
        "lsSet('israel_tv_favorites', state.favorites);",
        "lsSet('israel_tv_favorites', state.favorites); saveTvProfiles();"
    )

# 11. In openSettings, add profile option
old_settings_row = "action('ניהול מועדפים', function () {"
new_settings_row = """action('פרופילים (מי צופה?)', function () { openTvProfiles(); }, 'החלפת פרופיל, ניהול מועדפים ומצב ילדים'),
        toggle('בחירת פרופיל בהפעלת הטלוויזיה', function () { return !!S.profileOnStart; }, function () { setSetting('profileOnStart', !S.profileOnStart); }),
        action('ניהול מועדפים', function () {"""
code = code.replace(old_settings_row, new_settings_row)

with open("app.js", "w", encoding="utf-8") as f:
    f.write(code)

print("Successfully patched app.js with Netflix TV profiles engine.")

