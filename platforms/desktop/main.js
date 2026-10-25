// Israel TV+ desktop app for Linux and macOS (Electron). The web app is in ./www and loads from local files with
// web security off, so http streams and streams without CORS play (the https web site can't play those).
// Built by tools/build_desktop.py.
const { app, BrowserWindow, Menu, shell } = require('electron');
const path = require('path');

app.commandLine.appendSwitch('autoplay-policy', 'no-user-gesture-required');
app.setName('Israel TV Plus');
app.userAgentFallback = app.userAgentFallback + ' IsraelTVPlus/4 Desktop';
if (process.platform !== 'darwin') Menu.setApplicationMenu(null);

function openExternal(url) {
  if (/^https?:\/\//i.test(url)) shell.openExternal(url);
}

function createWindow() {
  const win = new BrowserWindow({
    width: 1440,
    height: 860,
    backgroundColor: '#000000',
    title: 'ישראל TV+',
    icon: path.join(__dirname, 'www', 'assets', 'app', 'icon-512.png'),
    autoHideMenuBar: true,
    webPreferences: { webSecurity: false, allowRunningInsecureContent: true, contextIsolation: true, sandbox: true }
  });
  win.maximize();
  win.loadFile(path.join(__dirname, 'www', 'index.html'));
  // links and the TV-service tiles open in the system browser
  win.webContents.setWindowOpenHandler(({ url }) => { openExternal(url); return { action: 'deny' }; });
  win.webContents.on('will-navigate', (e, url) => {
    if (!url.startsWith('file:')) { e.preventDefault(); openExternal(url); }
  });
  win.webContents.on('before-input-event', (e, input) => {
    if (input.type === 'keyDown' && input.key === 'F11') { win.setFullScreen(!win.isFullScreen()); e.preventDefault(); }
  });
}

app.whenReady().then(() => {
  createWindow();
  app.on('activate', () => { if (BrowserWindow.getAllWindows().length === 0) createWindow(); });
});
app.on('window-all-closed', () => app.quit());
