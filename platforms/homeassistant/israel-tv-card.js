/**
 * Israel TV+ Lovelace Card for Home Assistant
 * Direct one-tap channel switching on your Home Assistant dashboard!
 */
class IsraelTvCard extends HTMLElement {
  set hass(hass) {
    this._hass = hass;
    if (!this.content) {
      this.innerHTML = `
        <ha-card header="📺 ישראל TV+">
          <div style="padding: 12px 16px;">
            <div style="margin-bottom: 12px; display: flex; gap: 8px; align-items: center;">
              <span style="font-size: 14px; opacity: 0.8;">נגן יעד:</span>
              <select id="playerSelect" style="flex: 1; padding: 6px; border-radius: 8px; border: 1px solid var(--divider-color); background: var(--card-background-color); color: var(--primary-text-color);">
              </select>
            </div>
            <div id="channelGrid" style="display: grid; grid-template-columns: repeat(auto-fill, minmax(75px, 1fr)); gap: 10px; max-height: 400px; overflow-y: auto; padding: 4px;">
            </div>
          </div>
        </ha-card>
      `;
      this.content = true;
      this._populatePlayers();
      this._loadChannels();
    }
  }

  _populatePlayers() {
    const sel = this.querySelector("#playerSelect");
    if (!sel || !this._hass) return;
    const mediaPlayers = Object.keys(this._hass.states).filter(k => k.startsWith("media_player."));
    sel.innerHTML = mediaPlayers.map(id => {
      const state = this._hass.states[id];
      const name = state.attributes.friendly_name || id;
      return `<option value="${id}">${name}</option>`;
    }).join("");
  }

  async _loadChannels() {
    const grid = this.querySelector("#channelGrid");
    if (!grid) return;

    const topChannels = [
      { id: "kan11", name: "כאן 11", logo: "kan11.png" },
      { id: "keshet12", name: "קשת 12", logo: "keshet12.png" },
      { id: "reshet13", name: "רשת 13", logo: "reshet13.png" },
      { id: "now14", name: "עכשיו 14", logo: "now14.png" },
      { id: "i24news_he", name: "i24NEWS", logo: "i24news_he.png" },
      { id: "channel9", name: "ערוץ 9", logo: "ch9.png" },
      { id: "channel24_israel", name: "ערוץ 24", logo: "channel24_israel.png" },
      { id: "sport5_israel_hd", name: "5SPORT", logo: "sport5.png" },
      { id: "sport5_plus_hd", name: "5PLUS", logo: "sport5_plus.png" },
      { id: "sport1_israel", name: "ספורט 1", logo: "sport1_israel.png" },
      { id: "sport2_israel", name: "ספורט 2", logo: "sport2_israel.png" },
      { id: "one_1_hd", name: "ONE 1", logo: "one.png" },
      { id: "galgalatz_live", name: "גלגלצ", logo: "galgalatz_live.png" },
      { id: "glz_live", name: "גלצ", logo: "glz_live.png" },
      { id: "kan_bet", name: "כאן ב", logo: "kan_bet.png" },
      { id: "radio_103fm", name: "103FM", logo: "radio_103fm.png" }
    ];

    grid.innerHTML = topChannels.map(ch => `
      <button data-id="${ch.id}" style="border: none; background: rgba(127,127,127,0.1); border-radius: 10px; padding: 8px 4px; display: flex; flex-direction: column; align-items: center; justify-content: center; cursor: pointer; transition: transform 0.15s ease, background 0.15s ease;">
        <img src="https://dor2500.github.io/israel-tv-plus/assets/logos/${ch.logo}" style="width: 44px; height: 44px; object-fit: contain; margin-bottom: 6px;" onerror="this.src='https://dor2500.github.io/israel-tv-plus/icon.png'" />
        <span style="font-size: 11px; font-weight: bold; color: var(--primary-text-color); text-align: center; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; width: 100%;">${ch.name}</span>
      </button>
    `).join("");

    grid.querySelectorAll("button").forEach(btn => {
      btn.onclick = () => {
        const id = btn.getAttribute("data-id");
        const sel = this.querySelector("#playerSelect");
        const target = sel ? sel.value : null;
        if (target && this._hass) {
          this._hass.callService("israel_tv_plus", "play_channel", {
            entity_id: target,
            channel: id
          });
        }
      };
    });
  }

  setConfig(config) {
    this._config = config;
  }

  getCardSize() {
    return 4;
  }
}

customElements.define("israel-tv-card", IsraelTvCard);
window.customCards = window.customCards || [];
window.customCards.push({
  type: "israel-tv-card",
  name: "Israel TV+ Remote Card",
  description: "One-tap Israeli Live TV and Radio player for Home Assistant"
});

