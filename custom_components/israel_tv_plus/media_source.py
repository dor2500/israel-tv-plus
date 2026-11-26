"""Media Source implementation for Israel TV+."""
from __future__ import annotations

import json
import logging
import time
import urllib.parse
from typing import Any

import aiohttp
from homeassistant.components.media_player.const import MediaClass, MediaType
from homeassistant.components.media_source.error import MediaSourceError, Unresolvable
from homeassistant.components.media_source.models import (
    BrowseMediaSource,
    MediaSource,
    MediaSourceItem,
    PlayMedia,
)
from homeassistant.core import HomeAssistant

from .const import (
    CATEGORIES,
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

MIME_TYPE_HLS = "application/x-mpegurl"
MIME_TYPE_MP4 = "video/mp4"


async def async_get_media_source(hass: HomeAssistant) -> MediaSource:
    """Set up Israel TV+ media source."""
    data = hass.data[DOMAIN]
    return IsraelTvPlusMediaSource(hass, data)


class IsraelTvPlusMediaSource(MediaSource):
    """Provide Israel TV+ channels and radio as a media source."""

    name: str = "Israel TV+"

    def __init__(self, hass: HomeAssistant, data_manager: Any) -> None:
        """Initialize Israel TV+ media source."""
        super().__init__(DOMAIN)
        self.hass = hass
        self.manager = data_manager

    async def async_browse_media(
        self, item: MediaSourceItem | None
    ) -> BrowseMediaSource:
        """Return media browser items."""
        await self.manager.async_ensure_data()
        identifier = item.identifier if item else None

        if not identifier:
            # Root directory of categories
            children = [
                BrowseMediaSource(
                    domain=DOMAIN,
                    identifier=cat["id"],
                    media_class=MediaClass.DIRECTORY,
                    media_content_type=MediaType.CHANNELS,
                    title=cat["title"],
                    can_play=False,
                    can_expand=True,
                    thumbnail=f"{REMOTE_BASE_URL}icon.png",
                )
                for cat in CATEGORIES
            ]
            return BrowseMediaSource(
                domain=DOMAIN,
                identifier="",
                media_class=MediaClass.DIRECTORY,
                media_content_type=MediaType.CHANNELS,
                title="Israel TV+ (שידורים חיים)",
                can_play=False,
                can_expand=True,
                thumbnail=f"{REMOTE_BASE_URL}icon.png",
                children=children,
            )

        # Category selected
        if identifier == "vod":
            vod_series = self.manager.vod_data
            children = [
                BrowseMediaSource(
                    domain=DOMAIN,
                    identifier=f"vod_series_{s.get('id')}",
                    media_class=MediaClass.DIRECTORY,
                    media_content_type=MediaType.TVSHOW,
                    title=s.get("title", "סדרה"),
                    can_play=False,
                    can_expand=True,
                    thumbnail=self._thumb(s.get("poster", "")),
                )
                for s in vod_series
            ]
            return BrowseMediaSource(
                domain=DOMAIN,
                identifier="vod",
                media_class=MediaClass.DIRECTORY,
                media_content_type=MediaType.TVSHOW,
                title="סדרות וסרטים (VOD)",
                can_play=False,
                can_expand=True,
                children=children,
            )

        if identifier.startswith("vod_series_"):
            series_id = identifier.removeprefix("vod_series_")
            series = next((s for s in self.manager.vod_data if s.get("id") == series_id), None)
            if not series:
                raise MediaSourceError(f"Series not found: {series_id}")

            children = [
                BrowseMediaSource(
                    domain=DOMAIN,
                    identifier=f"vod_ep_{series_id}_{ep.get('id')}",
                    media_class=MediaClass.EPISODE,
                    media_content_type=MediaType.VIDEO,
                    title=f"{series.get('title')} · {ep.get('title')}",
                    can_play=True,
                    can_expand=False,
                    thumbnail=self._thumb(ep.get("thumb") or series.get("poster", "")),
                )
                for ep in series.get("episodes", [])
            ]
            return BrowseMediaSource(
                domain=DOMAIN,
                identifier=identifier,
                media_class=MediaClass.DIRECTORY,
                media_content_type=MediaType.TVSHOW,
                title=series.get("title", "פרקים"),
                can_play=False,
                can_expand=True,
                thumbnail=self._thumb(series.get("poster", "")),
                children=children,
            )

        # Standard channel categories
        channels = self.manager.get_channels_by_category(identifier)
        children = [
            BrowseMediaSource(
                domain=DOMAIN,
                identifier=f"ch_{ch.get('id')}",
                media_class=MediaClass.TRACK if ch.get("is_radio") else MediaClass.CHANNEL,
                media_content_type=MediaType.MUSIC if ch.get("is_radio") else MediaType.TVSHOW,
                title=ch.get("name", ""),
                can_play=True,
                can_expand=False,
                thumbnail=self._thumb(ch.get("logo", "")),
            )
            for ch in channels
        ]

        cat_title = next((c["title"] for c in CATEGORIES if c["id"] == identifier), identifier)
        return BrowseMediaSource(
            domain=DOMAIN,
            identifier=identifier,
            media_class=MediaClass.DIRECTORY,
            media_content_type=MediaType.CHANNELS,
            title=cat_title,
            can_play=False,
            can_expand=True,
            children=children,
        )

    async def async_play_media(self, item: MediaSourceItem) -> PlayMedia:
        """Resolve item to playable stream URL."""
        await self.manager.async_ensure_data()
        identifier = item.identifier

        if identifier.startswith("vod_ep_"):
            parts = identifier.split("_", 3)
            if len(parts) >= 4:
                series_id, ep_id = parts[2], parts[3]
                series = next((s for s in self.manager.vod_data if s.get("id") == series_id), None)
                if series:
                    ep = next((e for e in series.get("episodes", []) if e.get("id") == ep_id), None)
                    if ep and ep.get("url"):
                        mime = MIME_TYPE_MP4 if ep["url"].endswith(".mp4") else MIME_TYPE_HLS
                        return PlayMedia(url=ep["url"], mime_type=mime)

        ch_id = identifier.removeprefix("ch_")
        channel = self.manager.by_id.get(ch_id)
        if not channel:
            raise Unresolvable(f"Channel not found: {ch_id}")

        stream_url = await self.manager.async_resolve_stream(channel)
        if not stream_url:
            raise Unresolvable(f"Could not resolve stream for channel: {channel.get('name')}")

        mime = MIME_TYPE_HLS
        if stream_url.endswith(".mp3") or stream_url.endswith(".aac"):
            mime = "audio/mpeg"

        return PlayMedia(url=stream_url, mime_type=mime)

    def _thumb(self, path: str) -> str:
        """Return full thumbnail URL."""
        if not path:
            return f"{REMOTE_BASE_URL}icon.png"
        if path.startswith("http://") or path.startswith("https://"):
            return path
        return f"{REMOTE_BASE_URL}{path.lstrip('/')}"

