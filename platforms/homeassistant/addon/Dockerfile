ARG BUILD_FROM=ghcr.io/home-assistant/amd64-base:latest
FROM ${BUILD_FROM}

ENV LANG=C.UTF-8

RUN apk add --no-cache python3 py3-pip

WORKDIR /app

# Copy web files from repo
COPY index.html pair.html version.js boot.js ui.js fonts.css style.css tvguide.js app.js gdrive.js vod_data.json dub.js dub-local-worker.js channels.js icon.png manifest.webmanifest epg.json channels.json ./
COPY assets ./assets
COPY platforms/homeassistant/addon/server.py ./server.py
COPY platforms/homeassistant/addon/run.sh ./run.sh

RUN chmod +x ./run.sh

CMD [ "./run.sh" ]

