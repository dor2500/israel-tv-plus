"""Israel TV+ integration for Home Assistant."""
from __future__ import annotations

import json
import logging
import os
import time
import urllib.parse
from typing import Any

import aiohttp
from homeassistant.config_entries import ConfigEntry
from homeassistant.core import HomeAssistant, ServiceCall
from homeassistant.helpers.aiohttp_client import async_get_clientsession

from .const import (
    DEFAULT_USER_AGENT,
    DOMAIN,
    MAKO_ENTITLEMENT_URL,
    REMOTE_BASE_URL,
    REMOTE_CHANNELS_URL,
    REMOTE_VOD_URL,
    TOP_RADIO_IDS,
    TOP_TV_IDS,
)

_LOGGER = logging.getLogger(__name__)

PLATFORMS = ["media_source"]


async def async_setup_entry(hass: HomeAssistant, entry: ConfigEntry) -> bool:
    """Set up Israel TV+ from a config entry."""
    manager = IsraelTvDataManager(hass)
    await manager.async_ensure_data()
    hass.data.setdefault(DOMAIN, manager)

    await hass.config_entries.async_forward_entry_setups(entry, PLATFORMS)

    # 1. Register static path for Lovelace card
    www_dir = os.path.join(os.path.dirname(__file__), "www")
    os.makedirs(www_dir, exist_ok=True)
    card_file = os.path.join(www_dir, "israel-tv-card.js")
    if not os.path.isfile(card_file):
        card_src = os.path.join(
            os.path.dirname(os.path.dirname(os.path.dirname(__file__))),
            "platforms", "homeassistant", "israel-tv-card.js"
        )
        if os.path.isfile(card_src):
            try:
                import shutil
                shutil.copy2(card_src, card_file)
            except Exception:
                pass

    try:
        hass.http.register_static_path(
            "/israel_tv_plus_card",
            www_dir,
            cache_headers=False,
        )
    except Exception as err:
        _LOGGER.debug("Static path registration: %s", err)

    # 2. Automatically register Lovelace resource if available
    try:
        lovelace_resources = getattr(hass.data.get("lovelace"), "resources", None)
        if lovelace_resources and hasattr(lovelace_resources, "async_get_items"):
            items = lovelace_resources.async_get_items()
            card_url = "/israel_tv_plus_card/israel-tv-card.js"
            if not any(item.get("url") == card_url for item in items):
                await lovelace_resources.async_create_item({"res_type": "module", "url": card_url})
                _LOGGER.info("Successfully auto-registered Israel TV+ card resource")
    except Exception as err:
        _LOGGER.debug("Could not auto-register Lovelace resource: %s", err)

    # 3. Add to Home Screen & Sidebar Navigation
    if "frontend" in hass.config.components:
        try:
            hass.components.frontend.async_register_built_in_panel(
                component_name="iframe",
                sidebar_title="ישראל TV+",
                sidebar_icon="mdi:television-classic",
                frontend_url_path="israel-tv-plus",
                config={"url": "https://dor2500.github.io/israel-tv-plus/"},
                require_admin=False,
            )
            _LOGGER.info("Successfully added Israel TV+ to Home Assistant sidebar panel")
        except Exception as err:
            _LOGGER.debug("Could not register sidebar panel: %s", err)

    async def async_handle_play_channel(call: ServiceCall) -> None:
        """Handle service call to play a channel by ID, number, or name."""
        entity_ids = call.data.get("entity_id")
        target_channel = str(call.data.get("channel", "")).strip()

        if not target_channel:
            _LOGGER.warning("play_channel called without a channel parameter")
            return

        ch = manager.find_channel(target_channel)
        if not ch:
            _LOGGER.error("Channel not found: %s", target_channel)
            return

        stream_url = await manager.async_resolve_stream(ch)
        if not stream_url:
            _LOGGER.error("Unable to resolve stream for channel: %s", ch.get("name"))
            return

        logo = ch.get("logo", "")
        if logo and not logo.startswith("http"):
            logo = f"{REMOTE_BASE_URL}{logo.lstrip('/')}"

        mime = "application/x-mpegurl"
        if stream_url.endswith(".mp3") or stream_url.endswith(".aac"):
            mime = "audio/mpeg"

        media_player_data = {
            "entity_id": entity_ids,
            "media_content_id": stream_url,
            "media_content_type": mime,
            "extra": {
                "title": ch.get("name"),
                "thumb": logo,
            },
        }

        await hass.services.async_call(
            "media_player",
            "play_media",
            media_player_data,
            blocking=True,
        )

    hass.services.async_register(DOMAIN, "play_channel", async_handle_play_channel)
    return True


async def async_unload_entry(hass: HomeAssistant, entry: ConfigEntry) -> bool:
    """Unload a config entry."""
    unload_ok = await hass.config_entries.async_unload_platforms(entry, PLATFORMS)
    if unload_ok:
        hass.data.pop(DOMAIN, None)
        hass.services.async_remove(DOMAIN, "play_channel")
        if "frontend" in hass.config.components:
            try:
                hass.components.frontend.async_remove_panel("israel-tv-plus")
            except Exception:
                pass
    return unload_ok


class IsraelTvDataManager:
    """Manages channel list, categories, VOD, and token resolution."""

    def __init__(self, hass: HomeAssistant) -> None:
        """Initialize data manager."""
        self.hass = hass
        self.channels: list[dict[str, Any]] = []
        self.vod_data: list[dict[str, Any]] = []
        self.by_id: dict[str, dict[str, Any]] = {}
        self.last_fetch: float = 0
        self.mako_ticket_cache: dict[str, tuple[str, float]] = {}

    async def async_ensure_data(self) -> None:
        """Ensure channels and VOD catalogs are loaded and fresh."""
        now = time.time()
        if self.channels and (now - self.last_fetch) < 1800:
            return

        session = async_get_clientsession(self.hass)

        # 1. Try local channels.json from integration directory first
        local_file = os.path.join(os.path.dirname(__file__), "channels.json")
        loaded_local = False
        if os.path.isfile(local_file):
            try:
                with open(local_file, "r", encoding="utf-8") as f:
                    self.channels = json.load(f)
                    loaded_local = True
            except Exception as err:
                _LOGGER.warning("Could not load local channels.json: %s", err)

        # 2. Update/fetch latest from GitHub Pages
        try:
            async with session.get(REMOTE_CHANNELS_URL, timeout=aiohttp.ClientTimeout(total=10)) as resp:
                if resp.status == 200:
                    text = await resp.text()
                    data = json.loads(text)
                    if isinstance(data, list) and len(data) > 50:
                        self.channels = data
                        self.last_fetch = now
        except Exception as err:
            if not loaded_local:
                _LOGGER.error("Failed to fetch channels list: %s", err)

        # 3. Load VOD
        local_vod = os.path.join(os.path.dirname(__file__), "vod_data.json")
        if os.path.isfile(local_vod):
            try:
                with open(local_vod, "r", encoding="utf-8") as f:
                    self.vod_data = json.load(f)
            except Exception:
                pass

        try:
            async with session.get(REMOTE_VOD_URL, timeout=aiohttp.ClientTimeout(total=8)) as resp:
                if resp.status == 200:
                    self.vod_data = await resp.json()
        except Exception:
            pass

        self.by_id = {ch["id"]: ch for ch in self.channels if "id" in ch}

    def find_channel(self, query: str) -> dict[str, Any] | None:
        """Find channel by ID, number, or exact/fuzzy name."""
        q = query.strip().lower()
        if q in self.by_id:
            return self.by_id[q]

        # Check by channel number
        if q.isdigit():
            num = int(q)
            for ch in self.channels:
                if ch.get("number") == num:
                    return ch

        # Exact name match
        for ch in self.channels:
            if ch.get("name", "").lower() == q or ch.get("title", "").lower() == q:
                return ch

        # Substring name match
        for ch in self.channels:
            if q in ch.get("name", "").lower() or q in ch.get("title", "").lower():
                return ch

        return None

    def get_channels_by_category(self, cat_id: str) -> list[dict[str, Any]]:
        """Filter channels by category identifier."""
        tv = [c for c in self.channels if not c.get("is_radio")]
        radio = [c for c in self.channels if c.get("is_radio")]

        if cat_id == "tv_main":
            top_map = {c["id"]: c for c in tv if c["id"] in TOP_TV_IDS}
            ordered = [top_map[k] for k in TOP_TV_IDS if k in top_map]
            rest = [c for c in tv if c.get("categoryEn") == "israel" and c["id"] not in TOP_TV_IDS]
            return ordered + sorted(rest, key=lambda x: x.get("number", 0))

        if cat_id == "tv_sports":
            return [
                c for c in tv
                if c.get("categoryEn") == "sports"
                or "ספורט" in c.get("category", "")
                or "sport" in c.get("name", "").lower()
                or "ספורט" in c.get("name", "")
                or "one" in c.get("name", "").lower()
            ]

        if cat_id == "tv_news":
            return [
                c for c in tv
                if c.get("categoryEn") == "news"
                or "חדשות" in c.get("category", "")
                or "חדשות" in c.get("name", "")
                or "news" in c.get("name", "").lower()
                or c.get("id") in ["kan11", "keshet12", "reshet13", "now14", "i24news_he", "knesset", "ynet_live"]
            ]

        if cat_id == "tv_kids":
            kids_words = ["ילדים", "kids", "נוער", "קטנטנים", "אנימציה", "מצוירים", "דיסני", "ניק", "הופ", "לולי", "ג'וניור"]
            return [
                c for c in tv
                if c.get("categoryEn") in ["kids", "kids_he", "kids_world"]
                or any(w in c.get("name", "").lower() or w in c.get("category", "").lower() for w in kids_words)
            ]

        if cat_id == "tv_entertainment":
            return [
                c for c in tv
                if c.get("categoryEn") in ["movies", "series", "lifestyle", "food", "docu"]
                or any(w in c.get("category", "") for w in ["סרטים", "סדרות", "בידור", "אוכל", "דוקו"])
            ]

        if cat_id == "tv_all":
            return sorted(tv, key=lambda x: x.get("number", 0))

        if cat_id == "radio_top":
            top_map = {c["id"]: c for c in radio if c["id"] in TOP_RADIO_IDS}
            ordered = [top_map[k] for k in TOP_RADIO_IDS if k in top_map]
            rest = [c for c in radio if c["id"] not in TOP_RADIO_IDS]
            return ordered + rest[:10]

        if cat_id == "radio_all":
            return sorted(radio, key=lambda x: x.get("name", ""))

        return []

    async def async_resolve_stream(self, ch: dict[str, Any]) -> str | None:
        """Resolve a candidate stream URL with authentication/ticket if required."""
        raw_url = ch.get("url", "")
        auth = ch.get("auth", "")

        needs_mako = "mako-streaming.akamaized.net" in raw_url.lower() or (
            auth == "mako" and "mako" in raw_url.lower()
        )

        if not needs_mako:
            return raw_url

        now = time.time()
        if raw_url in self.mako_ticket_cache:
            signed, ts = self.mako_ticket_cache[raw_url]
            if now - ts < 600:  # 10 minutes cache
                return signed

        session = async_get_clientsession(self.hass)
        try:
            parsed = urllib.parse.urlparse(raw_url)
            path_query = parsed.path + (f"?{parsed.query}" if parsed.query else "")
            ticket_req_url = f"{MAKO_ENTITLEMENT_URL}?et=ngt&rv=AKAMAI&lp={urllib.parse.quote(path_query)}"

            async with session.get(ticket_req_url, timeout=aiohttp.ClientTimeout(total=5)) as resp:
                if resp.status == 200:
                    data = await resp.json(content_type=None)
                    ticket = data.get("tickets", [{}])[0].get("ticket", "")
                    if ticket:
                        delim = "&" if "?" in raw_url else "?"
                        signed_url = f"{raw_url}{delim}{ticket}"
                        self.mako_ticket_cache[raw_url] = (signed_url, now)
                        return signed_url
        except Exception as err:
            _LOGGER.warning("Error fetching Mako ticket for %s: %s", raw_url, err)

        return raw_url

