@echo off
title Israel TV+ DLNA Media Server
chcp 65001 >nul
echo ================================================================
echo        Israel TV+ DLNA & UPnP AV Media Server
echo ================================================================
echo.
python "%~dp0tools\dlna_server.py"
pause
