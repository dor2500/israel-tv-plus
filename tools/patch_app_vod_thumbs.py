import os

with open('app.js', 'r', encoding='utf-8') as f:
    c = f.read()

target = '''    var eps = series.episodes || [];
    var epHtml = eps.map(function (ep, idx) {
      return '<div class="vod-ep-card" data-epi="' + idx + '" tabindex="0">' +
        '<div class="vod-ep-info"><b>' + esc(ep.title) + '</b><span>' + esc(ep.duration || '') + '</span></div>' +
        '<div style="display:flex;gap:0.5rem;align-items:center;">' +
        '<button class="btn vod-rec-btn" data-rec-epi="' + idx + '" title="הקלט פרק זה ישירות ל-Google Drive">💾 הקלט ל-Drive</button>' +
        '<button class="btn primary vod-play-btn" data-epi="' + idx + '">▶ צפייה בפרק</button>' +
        '</div>' +
        '</div>';
    }).join('');

    var html = '<div class="vod-modal-hero" style="display:flex;gap:1.5rem;align-items:flex-start;margin-bottom:1.5rem;">' +
      '<img src="' + esc(series.poster) + '" style="width:130px;height:185px;object-fit:cover;border-radius:12px;box-shadow:0 8px 24px rgba(0,0,0,0.6);" alt="">' +
      '<div style="flex:1;"><h2 style="margin:0 0 0.5rem;font-size:1.8rem;">' + esc(series.title) + '</h2>' +'''

replacement = '''    var eps = series.episodes || [];
    var epHtml = eps.map(function (ep, idx) {
      var epThumb = ep.thumb || series.backdrop || series.poster || '';
      var thumbTag = epThumb ? '<img src="' + esc(epThumb) + '" style="width:84px;height:48px;object-fit:cover;border-radius:6px;flex-shrink:0;box-shadow:0 2px 8px rgba(0,0,0,0.4);" onerror="this.style.display=\\'none\\'" alt="">' : '';
      return '<div class="vod-ep-card" data-epi="' + idx + '" tabindex="0" style="display:flex;align-items:center;gap:0.9rem;padding:0.6rem 0.8rem;background:rgba(255,255,255,0.06);border-radius:8px;border:1px solid rgba(255,255,255,0.08);">' +
        thumbTag +
        '<div class="vod-ep-info" style="flex:1;"><b>' + esc(ep.title) + '</b><span style="display:block;font-size:0.85rem;color:rgba(255,255,255,0.6);">' + esc(ep.duration || '') + '</span></div>' +
        '<div style="display:flex;gap:0.5rem;align-items:center;">' +
        '<button class="btn vod-rec-btn" data-rec-epi="' + idx + '" title="הקלט פרק זה ישירות ל-Google Drive">💾 הקלט ל-Drive</button>' +
        '<button class="btn primary vod-play-btn" data-epi="' + idx + '">▶ צפייה בפרק</button>' +
        '</div>' +
        '</div>';
    }).join('');

    var html = '<div class="vod-modal-hero" style="display:flex;gap:1.5rem;align-items:flex-start;margin-bottom:1.5rem;">' +
      '<img src="' + esc(series.poster) + '" style="width:130px;height:185px;object-fit:cover;border-radius:12px;box-shadow:0 8px 24px rgba(0,0,0,0.6);" onerror="this.src=\\'assets/vod/checkout.png\\'" alt="">' +
      '<div style="flex:1;"><h2 style="margin:0 0 0.5rem;font-size:1.8rem;">' + esc(series.title) + '</h2>' +'''

if target in c:
    c = c.replace(target, replacement, 1)
    with open('app.js', 'w', encoding='utf-8') as f:
        f.write(c)
    print('Successfully patched app.js!')
else:
    print('Target not found in app.js')
