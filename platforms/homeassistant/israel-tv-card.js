/**
 * Israel TV+ Remote & Player Card for Home Assistant (v4.8.74)
 * Search, filter by category, direct one-tap channel playback and media controls!
 */
class IsraelTvCard extends HTMLElement {
  constructor() {
    super();
    this._selectedCategory = "ALL";
    this._searchQuery = "";
  }

  set hass(hass) {
    this._hass = hass;
    if (!this.content) {
      this._buildCard();
      this.content = true;
      this._populatePlayers();
      this._renderChannels();
      this._attachEventListeners();
    } else {
      this._updatePlayerControls();
    }
  }

  _buildCard() {
    this.innerHTML = `
      <ha-card style="direction: rtl; font-family: system-ui, -apple-system, sans-serif; overflow: hidden; border-radius: 16px; background: var(--card-background-color, #1a1e24); color: var(--primary-text-color, #fff);">
        <!-- Card Header -->
        <div style="padding: 14px 16px 10px; border-bottom: 1px solid var(--divider-color, rgba(255,255,255,0.08));">
          <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px;">
            <div style="display: flex; align-items: center; gap: 8px;">
              <span style="font-size: 22px;">📺</span>
              <strong style="font-size: 16px; letter-spacing: 0.5px;">ישראל TV+</strong>
            </div>
            <div style="display: flex; align-items: center; gap: 8px; flex: 1; max-width: 220px;">
              <select id="itvPlayerSelect" style="width: 100%; padding: 6px 10px; border-radius: 8px; border: 1px solid var(--divider-color, rgba(255,255,255,0.15)); background: rgba(127,127,127,0.12); color: inherit; font-size: 13px; outline: none;">
              </select>
            </div>
          </div>

          <!-- Quick Media Player Controls -->
          <div id="itvMediaControls" style="display: flex; align-items: center; gap: 8px; padding: 6px 10px; border-radius: 10px; background: rgba(0,0,0,0.2); margin-bottom: 10px;">
            <button id="itvBtnPlayPause" title="נגן / השהה" style="border: none; background: rgba(255,255,255,0.1); color: inherit; border-radius: 6px; padding: 6px 10px; cursor: pointer; font-size: 14px;">⏯️</button>
            <button id="itvBtnStop" title="עצור" style="border: none; background: rgba(255,255,255,0.1); color: inherit; border-radius: 6px; padding: 6px 10px; cursor: pointer; font-size: 14px;">⏹️</button>
            <button id="itvBtnMute" title="השתק" style="border: none; background: rgba(255,255,255,0.1); color: inherit; border-radius: 6px; padding: 6px 10px; cursor: pointer; font-size: 14px;">🔇</button>
            <input type="range" id="itvVolSlider" min="0" max="100" value="50" style="flex: 1; cursor: pointer; accent-color: var(--primary-color, #03a9f4);" />
            <span id="itvNowPlaying" style="font-size: 12px; opacity: 0.8; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; max-width: 130px;"></span>
          </div>

          <!-- Live Channel Search & Filters -->
          <div style="display: flex; flex-direction: column; gap: 8px;">
            <div style="position: relative;">
              <input type="text" id="itvSearchInput" placeholder="🔍 חפש ערוץ או מספר..." style="width: 100%; box-sizing: border-box; padding: 8px 12px; border-radius: 8px; border: 1px solid var(--divider-color, rgba(255,255,255,0.15)); background: rgba(127,127,127,0.08); color: inherit; font-size: 13px; outline: none;" />
            </div>
            
            <!-- Category Filter Pills -->
            <div id="itvCategoryPills" style="display: flex; gap: 6px; overflow-x: auto; padding-bottom: 4px; scrollbar-width: none;">
              <button class="itv-pill active" data-cat="ALL" style="border: none; border-radius: 20px; padding: 4px 12px; font-size: 12px; cursor: pointer; background: var(--primary-color, #03a9f4); color: #fff; white-space: nowrap;">⭐ הכל</button>
              <button class="itv-pill" data-cat="MAIN" style="border: none; border-radius: 20px; padding: 4px 12px; font-size: 12px; cursor: pointer; background: rgba(127,127,127,0.15); color: inherit; white-space: nowrap;">📺 טלוויזיה</button>
              <button class="itv-pill" data-cat="NEWS" style="border: none; border-radius: 20px; padding: 4px 12px; font-size: 12px; cursor: pointer; background: rgba(127,127,127,0.15); color: inherit; white-space: nowrap;">⚡ חדשות</button>
              <button class="itv-pill" data-cat="SPORTS" style="border: none; border-radius: 20px; padding: 4px 12px; font-size: 12px; cursor: pointer; background: rgba(127,127,127,0.15); color: inherit; white-space: nowrap;">⚽ ספורט</button>
              <button class="itv-pill" data-cat="KIDS" style="border: none; border-radius: 20px; padding: 4px 12px; font-size: 12px; cursor: pointer; background: rgba(127,127,127,0.15); color: inherit; white-space: nowrap;">🧒 ילדים</button>
              <button class="itv-pill" data-cat="MOVIES" style="border: none; border-radius: 20px; padding: 4px 12px; font-size: 12px; cursor: pointer; background: rgba(127,127,127,0.15); color: inherit; white-space: nowrap;">🎬 סרטים</button>
              <button class="itv-pill" data-cat="HERITAGE" style="border: none; border-radius: 20px; padding: 4px 12px; font-size: 12px; cursor: pointer; background: rgba(127,127,127,0.15); color: inherit; white-space: nowrap;">🕍 מסורת</button>
              <button class="itv-pill" data-cat="RADIO" style="border: none; border-radius: 20px; padding: 4px 12px; font-size: 12px; cursor: pointer; background: rgba(127,127,127,0.15); color: inherit; white-space: nowrap;">📻 רדיו</button>
            </div>
          </div>
        </div>

        <!-- Channels Grid -->
        <div id="itvChannelGrid" style="display: grid; grid-template-columns: repeat(auto-fill, minmax(80px, 1fr)); gap: 10px; max-height: 440px; overflow-y: auto; padding: 14px 16px; scrollbar-width: thin;">
        </div>
      </ha-card>
    `;
  }

  _populatePlayers() {
    const sel = this.querySelector("#itvPlayerSelect");
    if (!sel || !this._hass) return;
    const mediaPlayers = Object.keys(this._hass.states).filter(k => k.startsWith("media_player."));
    sel.innerHTML = mediaPlayers.map(id => {
      const state = this._hass.states[id];
      const name = state.attributes.friendly_name || id;
      return `<option value="${id}">${name}</option>`;
    }).join("");
  }

  _updatePlayerControls() {
    const sel = this.querySelector("#itvPlayerSelect");
    const np = this.querySelector("#itvNowPlaying");
    const slider = this.querySelector("#itvVolSlider");
    if (!sel || !this._hass) return;
    const current = this._hass.states[sel.value];
    if (current && np) {
      const title = current.attributes.media_title || current.state;
      np.textContent = title ? `🔊 ${title}` : "";
      if (current.attributes.volume_level !== undefined && slider && document.activeElement !== slider) {
        slider.value = Math.round(current.attributes.volume_level * 100);
      }
    }
  }

  _attachEventListeners() {
    const searchInput = this.querySelector("#itvSearchInput");
    if (searchInput) {
      searchInput.oninput = (e) => {
        this._searchQuery = e.target.value.toLowerCase().trim();
        this._renderChannels();
      };
    }

    const pills = this.querySelectorAll(".itv-pill");
    pills.forEach(pill => {
      pill.onclick = () => {
        pills.forEach(p => {
          p.style.background = "rgba(127,127,127,0.15)";
          p.style.color = "inherit";
          p.classList.remove("active");
        });
        pill.style.background = "var(--primary-color, #03a9f4)";
        pill.style.color = "#fff";
        pill.classList.add("active");
        this._selectedCategory = pill.getAttribute("data-cat");
        this._renderChannels();
      };
    });

    const getSelectedPlayer = () => {
      const sel = this.querySelector("#itvPlayerSelect");
      return sel ? sel.value : null;
    };

    const btnPlayPause = this.querySelector("#itvBtnPlayPause");
    if (btnPlayPause) {
      btnPlayPause.onclick = () => {
        const id = getSelectedPlayer();
        if (id && this._hass) this._hass.callService("media_player", "media_play_pause", { entity_id: id });
      };
    }

    const btnStop = this.querySelector("#itvBtnStop");
    if (btnStop) {
      btnStop.onclick = () => {
        const id = getSelectedPlayer();
        if (id && this._hass) this._hass.callService("media_player", "media_stop", { entity_id: id });
      };
    }

    const btnMute = this.querySelector("#itvBtnMute");
    if (btnMute) {
      btnMute.onclick = () => {
        const id = getSelectedPlayer();
        if (id && this._hass) this._hass.callService("media_player", "volume_mute", { entity_id: id, is_volume_muted: true });
      };
    }

    const slider = this.querySelector("#itvVolSlider");
    if (slider) {
      slider.onchange = (e) => {
        const id = getSelectedPlayer();
        const val = parseFloat(e.target.value) / 100;
        if (id && this._hass) this._hass.callService("media_player", "volume_set", { entity_id: id, volume_level: val });
      };
    }
  }

  _getChannelsCatalog() {
    return [
      { id: "kan11", number: 11, name: "כאן 11", cat: "MAIN", logo: "kan11.png", url: "https://kancdn.medonecdn.net/livehls/oil/kancdn-live/live/kan11/live.livx/playlist.m3u8" },
      { id: "keshet12", number: 12, name: "קשת 12", cat: "MAIN", logo: "keshet12.png", url: "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/keshet_12/live.livx/playlist.m3u8" },
      { id: "reshet13", number: 13, name: "רשת 13", cat: "MAIN", logo: "reshet13.png", url: "https://d18b0e6mopany4.cloudfront.net/out/v1/2f2bc414a3db4698a8e94b89eaf2da2a/index.m3u8" },
      { id: "now14", number: 14, name: "עכשיו 14", cat: "NEWS", logo: "now14.png", url: "https://r.il.cdn-redge.media/livehls/oil/ch14/live/ch14/live.livx/playlist.m3u8" },
      { id: "i24news_he", number: 15, name: "i24NEWS", cat: "NEWS", logo: "i24news_he.png", url: "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/i24_he/live.livx/playlist.m3u8" },
      { id: "channel9", number: 9, name: "ערוץ 9", cat: "MAIN", logo: "ch9.png", url: "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/ch9/live.livx/playlist.m3u8" },
      { id: "channel24_israel", number: 24, name: "ערוץ 24", cat: "MAIN", logo: "channel24_israel.png", url: "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/music24/live.livx/playlist.m3u8" },
      { id: "kan_hinuchit", number: 23, name: "כאן חינוכית", cat: "KIDS", logo: "kan_hinuchit.png", url: "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/edu23/live.livx/playlist.m3u8" },
      { id: "knesset", number: 99, name: "ערוץ הכנסת", cat: "NEWS", logo: "knesset.png", url: "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/kneset/live.livx/playlist.m3u8" },
      { id: "kotel_live_il", number: 90, name: "הכותל 24/7", cat: "HERITAGE", logo: "kotel_live_il.png", url: "https://cdn1.cast-tv.com/23595/Live_Kotel3_ABR/playlist.m3u8" },
      { id: "sport5_israel_hd", number: 30, name: "5SPORT", cat: "SPORTS", logo: "sport5.png", url: "http://stream.mcquack.net/40/index.m3u8" },
      { id: "sport5_plus_hd", number: 31, name: "5PLUS", cat: "SPORTS", logo: "sport5_plus.png", url: "http://stream.mcquack.net/41/index.m3u8" },
      { id: "sport1_israel", number: 32, name: "ספורט 1", cat: "SPORTS", logo: "sport1_israel.png", url: "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/2347/index.m3u8" },
      { id: "sport2_israel", number: 33, name: "ספורט 2", cat: "SPORTS", logo: "sport2_israel.png", url: "http://b84203c4.ottrast.com/iptv/QAP2YS2594ZQS3/2348/index.m3u8" },
      { id: "one_1_hd", number: 34, name: "ONE 1", cat: "SPORTS", logo: "one.png", url: "http://stream.mcquack.net/38/index.m3u8" },
      { id: "one_2_hd", number: 35, name: "ONE 2", cat: "SPORTS", logo: "one2.png", url: "http://stream.mcquack.net/39/index.m3u8" },
      { id: "hop_israel_hd", number: 44, name: "ערוץ הופ!", cat: "KIDS", logo: "hop.png", url: "http://stream.mcquack.net/44/index.m3u8" },
      { id: "freetv_kids_family_il", number: 60, name: "סרטי ילדים", cat: "KIDS", logo: "freetv_kids_family_il.png", url: "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/imagin_family_kids/live.livx/playlist.m3u8" },
      { id: "junior_kids_il", number: 45, name: "ג'וניור", cat: "KIDS", logo: "junior.png", url: "http://stream.mcquack.net/45/index.m3u8" },
      { id: "luli_mc", number: 46, name: "לולי לפעוטות", cat: "KIDS", logo: "luli.png", url: "http://stream.mcquack.net/46/index.m3u8" },
      { id: "freetv_lifestyle_il", number: 529, name: "FreeTV לייף", cat: "MAIN", logo: "freetv_lifestyle_il.png", url: "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/imagin_lifestyle/live.livx/playlist.m3u8" },
      { id: "music_channel_il", number: 530, name: "מוזיקה ישראלית", cat: "MAIN", logo: "music_channel_il.png", url: "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/music_channel/live.livx/playlist.m3u8" },
      { id: "partner_reality_il", number: 531, name: "ריאליטי", cat: "MOVIES", logo: "partner_reality_il.png", url: "https://d2dffl3588mvfk.cloudfront.net/out/v1/d8e15050ca4148aab0ee387a5e2eb46b/index_1.m3u8" },
      { id: "freetv_action_il", number: 40, name: "FreeTV אקשן", cat: "MOVIES", logo: "freetv_movies_action.png", url: "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/imagin_action/live.livx/playlist.m3u8" },
      { id: "freetv_comedy_il", number: 41, name: "FreeTV קומדיה", cat: "MOVIES", logo: "freetv_movies_comedy.png", url: "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/imagin_comedy/live.livx/playlist.m3u8" },
      { id: "freetv_drama_il", number: 42, name: "FreeTV דרמה", cat: "MOVIES", logo: "freetv_movies_drama.png", url: "https://d1zqtf09wb8nt5.cloudfront.net/livehls/oil/freetv/live/imagin_drama/live.livx/playlist.m3u8" },
      { id: "hidabroot", number: 97, name: "הידברות 97", cat: "HERITAGE", logo: "hidabroot.png", url: "https://cdn.cybercdn.live/HidabrootIL/Live97/playlist.m3u8" },
      { id: "galgalatz_live", number: 201, name: "גלגלצ", cat: "RADIO", logo: "galgalatz_live.png", url: "https://glz-live.cdn.streamgate.net/glz/glz_audio.aac/playlist.m3u8" },
      { id: "glz_live", number: 202, name: "גלצ", cat: "RADIO", logo: "glz_live.png", url: "https://glz-live.cdn.streamgate.net/glz/glz_audio.aac/playlist.m3u8" },
      { id: "kan_bet", number: 203, name: "כאן רשת ב", cat: "RADIO", logo: "kan_bet.png", url: "https://playerservices.streamtheworld.com/api/livestream-redirect/KAN_BET.mp3" },
      { id: "kan_gimel", number: 204, name: "כאן גימל", cat: "RADIO", logo: "kan_gimel.png", url: "https://playerservices.streamtheworld.com/api/livestream-redirect/KAN_GIMMEL.mp3" },
      { id: "kan_88", number: 205, name: "כאן 88", cat: "RADIO", logo: "kan_88.png", url: "https://playerservices.streamtheworld.com/api/livestream-redirect/KAN_88.mp3" },
      { id: "radio_103fm", number: 206, name: "103FM", cat: "RADIO", logo: "radio_103fm.png", url: "https://103fm.streamgate.net/103fm/103fm_audio.aac/playlist.m3u8" },
      { id: "eco99fm", number: 207, name: "Eco 99FM", cat: "RADIO", logo: "eco99.png", url: "https://eco99.streamgate.net/eco99/eco99_audio.aac/playlist.m3u8" },
      { id: "radios100fm", number: 208, name: "רדיוס 100FM", cat: "RADIO", logo: "radios100.png", url: "https://100fm.streamgate.net/100fm/100fm_audio.aac/playlist.m3u8" }
    ];
  }

  _renderChannels() {
    const grid = this.querySelector("#itvChannelGrid");
    if (!grid) return;

    let list = this._getChannelsCatalog();

    if (this._selectedCategory !== "ALL") {
      list = list.filter(ch => ch.cat === this._selectedCategory);
    }

    if (this._searchQuery) {
      list = list.filter(ch => 
        ch.name.toLowerCase().includes(this._searchQuery) ||
        String(ch.number).includes(this._searchQuery) ||
        ch.id.toLowerCase().includes(this._searchQuery)
      );
    }

    if (list.length === 0) {
      grid.innerHTML = `<div style="grid-column: 1 / -1; text-align: center; padding: 24px; opacity: 0.6; font-size: 13px;">לא נמצאו ערוצים תואמים לחיפוש</div>`;
      return;
    }

    grid.innerHTML = list.map(ch => `
      <button data-id="${ch.id}" data-url="${ch.url}" style="border: none; background: rgba(127,127,127,0.12); border-radius: 12px; padding: 10px 4px; display: flex; flex-direction: column; align-items: center; justify-content: center; cursor: pointer; transition: transform 0.15s ease, background 0.15s ease; position: relative;">
        <span style="position: absolute; top: 4px; right: 6px; font-size: 9px; opacity: 0.7; font-weight: bold;">${ch.number}</span>
        <img src="https://dor2500.github.io/israel-tv-plus/assets/logos/${ch.logo}" style="width: 44px; height: 44px; object-fit: contain; margin-bottom: 6px;" onerror="this.src='https://dor2500.github.io/israel-tv-plus/icon.png'" />
        <span style="font-size: 11px; font-weight: bold; color: var(--primary-text-color, #fff); text-align: center; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; width: 100%;">${ch.name}</span>
      </button>
    `).join("");

    grid.querySelectorAll("button").forEach(btn => {
      btn.onclick = () => {
        const id = btn.getAttribute("data-id");
        const url = btn.getAttribute("data-url");
        const sel = this.querySelector("#itvPlayerSelect");
        const target = sel ? sel.value : null;
        if (target && this._hass) {
          btn.style.transform = "scale(0.92)";
          btn.style.background = "var(--primary-color, #03a9f4)";
          setTimeout(() => {
            btn.style.transform = "none";
            btn.style.background = "rgba(127,127,127,0.12)";
          }, 250);

          // Try custom component service first, or fall back to media_player.play_media
          if (this._hass.services && this._hass.services.israel_tv_plus) {
            this._hass.callService("israel_tv_plus", "play_channel", {
              entity_id: target,
              channel: id
            });
          } else {
            this._hass.callService("media_player", "play_media", {
              entity_id: target,
              media_content_id: url,
              media_content_type: url.includes(".mp3") ? "audio/mp3" : "video/hls"
            });
          }
        }
      };
    });
  }

  setConfig(config) {
    this._config = config;
  }

  getCardSize() {
    return 5;
  }
}

customElements.define("israel-tv-card", IsraelTvCard);
window.customCards = window.customCards || [];
window.customCards.push({
  type: "israel-tv-card",
  name: "Israel TV+ Card",
  description: "Searchable, categorized Israeli Live TV & Radio remote for Home Assistant"
});
