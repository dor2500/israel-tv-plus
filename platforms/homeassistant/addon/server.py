#!/usr/bin/env python3
"""Lightweight Ingress HTTP server for Israel TV+ Home Assistant Add-on."""
import http.server
import os
import socketserver

PORT = 8080
DIRECTORY = "/app"

class IngressHandler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=DIRECTORY, **kwargs)

    def end_headers(self):
        self.send_header("Access-Control-Allow-Origin", "*")
        self.send_header("Cache-Control", "no-cache")
        super().end_headers()

if __name__ == "__main__":
    os.chdir(DIRECTORY)
    with socketserver.TCPServer(("", PORT), IngressHandler) as httpd:
        print(f"Israel TV+ Home Assistant Add-on running on port {PORT}...")
        httpd.serve_forever()

