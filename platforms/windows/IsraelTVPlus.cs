// Israel TV+ for Windows: the web app (www\ next to the exe) in Microsoft Edge WebView2.
// Loaded from local files with web security off, so http streams and streams without CORS play
// (the https web site can't play those). F11 / the app's fullscreen button = borderless fullscreen.
// C# 5 (builds with the csc.exe that ships with Windows' .NET Framework 4.x): see tools\build_windows.ps1.
using System;
using System.Diagnostics;
using System.Drawing;
using System.IO;
using System.Windows.Forms;
using Microsoft.Web.WebView2.Core;
using Microsoft.Web.WebView2.WinForms;

[assembly: System.Reflection.AssemblyTitle("Israel TV+")]
[assembly: System.Reflection.AssemblyProduct("Israel TV+")]
[assembly: System.Reflection.AssemblyVersion("4.6.8.0")]

namespace IsraelTVPlus
{
    public class MainForm : Form
    {
        readonly WebView2 web = new WebView2();
        bool fullscreen;
        FormWindowState before = FormWindowState.Maximized;

        public MainForm()
        {
            Text = "ישראל TV+";
            BackColor = Color.Black;
            Icon = Icon.ExtractAssociatedIcon(Application.ExecutablePath);
            StartPosition = FormStartPosition.CenterScreen;
            Size = new Size(1440, 860);
            WindowState = FormWindowState.Maximized;
            KeyPreview = true;
            web.Dock = DockStyle.Fill;
            web.DefaultBackgroundColor = Color.Black;
            Controls.Add(web);
            Load += async (s, e) => await Start();
        }

        async System.Threading.Tasks.Task Start()
        {
            string dir = Path.GetDirectoryName(Application.ExecutablePath);
            string page = Path.Combine(dir, "www", "index.html");
            string data = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData), "IsraelTVPlus");
            var opts = new CoreWebView2EnvironmentOptions(
                "--disable-web-security --allow-file-access-from-files --allow-running-insecure-content " +
                "--autoplay-policy=no-user-gesture-required --disable-features=BlockInsecurePrivateNetworkRequests");
            CoreWebView2Environment env;
            try
            {
                env = await CoreWebView2Environment.CreateAsync(null, data, opts);
                await web.EnsureCoreWebView2Async(env);
            }
            catch (Exception ex)
            {
                if (MessageBox.Show("חסר רכיב Microsoft Edge WebView2 Runtime.\nלפתוח את דף ההורדה?\n\n" + ex.Message,
                        "ישראל TV+", MessageBoxButtons.YesNo, MessageBoxIcon.Warning) == DialogResult.Yes)
                    Open("https://go.microsoft.com/fwlink/p/?LinkId=2124703");
                Close();
                return;
            }
            var core = web.CoreWebView2;
            core.Settings.AreDevToolsEnabled = true;
            core.Settings.IsStatusBarEnabled = false;
            core.Settings.AreDefaultContextMenusEnabled = false;
            core.Settings.UserAgent = core.Settings.UserAgent + " IsraelTVPlus/3 Windows";
            core.WebMessageReceived += OnMessage;
            core.NewWindowRequested += (s, e) => { e.Handled = true; Open(e.Uri); };
            core.NavigationStarting += (s, e) =>
            {
                if (!e.Uri.StartsWith("file:", StringComparison.OrdinalIgnoreCase)) { e.Cancel = true; Open(e.Uri); }
            };
            core.ContainsFullScreenElementChanged += (s, e) => SetFullscreen(core.ContainsFullScreenElement);
            core.Navigate(new Uri(page).AbsoluteUri);
        }

        void OnMessage(object sender, CoreWebView2WebMessageReceivedEventArgs e)
        {
            string msg;
            try { msg = e.TryGetWebMessageAsString(); } catch { return; }
            // {"open":"https://..."} or {"exit":true} (tiny messages, no JSON library needed)
            int i = msg.IndexOf("\"open\"", StringComparison.Ordinal);
            if (i >= 0)
            {
                int a = msg.IndexOf('"', msg.IndexOf(':', i) + 1), b = a < 0 ? -1 : msg.IndexOf('"', a + 1);
                if (a >= 0 && b > a) Open(msg.Substring(a + 1, b - a - 1).Replace("\\/", "/"));
            }
            else if (msg.Contains("\"update\"")) Update(Value(msg, "update"));
            else if (msg.Contains("\"exit\"")) Close();
        }

        static string Value(string msg, string key)
        {
            int i = msg.IndexOf("\"" + key + "\"", StringComparison.Ordinal);
            if (i < 0) return null;
            int a = msg.IndexOf('"', msg.IndexOf(':', i) + 1), b = a < 0 ? -1 : msg.IndexOf('"', a + 1);
            return a >= 0 && b > a ? msg.Substring(a + 1, b - a - 1).Replace("\\/", "/") : null;
        }

        void Report(string state, string value)
        {
            string js = "window.__itvUpdate && window.__itvUpdate('" + state + "','" + (value ?? "").Replace("\\", "\\\\").Replace("'", "\\'") + "')";
            BeginInvoke((Action)(() => { if (web.CoreWebView2 != null) web.CoreWebView2.ExecuteScriptAsync(js); }));
        }

        // In-app update: download the new installer from the GitHub release and run it silently (it closes this
        // app, installs over it and starts the new version). The portable ZIP has no installer: open the page.
        void Update(string url)
        {
            // the installer runs silently: only this project's own GitHub releases are accepted
            if (url == null || !url.StartsWith("https://github.com/dor2500/israel-tv-plus/releases/download/")) return;
            string dir = Path.GetDirectoryName(Application.ExecutablePath);
            if (!File.Exists(Path.Combine(dir, "unins000.exe")))
            {
                Report("error", "הגרסה הניידת (ZIP) – מורידים את הגרסה החדשה מדף ההורדות");
                Open("https://github.com/dor2500/israel-tv-plus/releases/latest");
                return;
            }
            string file = Path.Combine(Path.GetTempPath(), "IsraelTVPlus-windows-setup.exe");
            System.Net.ServicePointManager.SecurityProtocol = System.Net.SecurityProtocolType.Tls12;
            var wc = new System.Net.WebClient();
            wc.DownloadProgressChanged += (s, e) => Report("progress", e.ProgressPercentage.ToString());
            wc.DownloadFileCompleted += (s, e) =>
            {
                if (e.Error != null) { Report("error", e.Error.Message); return; }
                Report("installing", "");
                Process.Start(new ProcessStartInfo(file, "/SILENT /SUPPRESSMSGBOXES /NORESTART /CLOSEAPPLICATIONS") { UseShellExecute = true });
                BeginInvoke((Action)Close);
            };
            wc.DownloadFileAsync(new Uri(url), file);
        }

        static void Open(string url)
        {
            if (url != null && (url.StartsWith("https://") || url.StartsWith("http://")))
                Process.Start(new ProcessStartInfo(url) { UseShellExecute = true });
        }

        void SetFullscreen(bool on)
        {
            if (on == fullscreen) return;
            fullscreen = on;
            if (on)
            {
                before = WindowState;
                WindowState = FormWindowState.Normal;
                FormBorderStyle = FormBorderStyle.None;
                WindowState = FormWindowState.Maximized;
            }
            else
            {
                FormBorderStyle = FormBorderStyle.Sizable;
                WindowState = before;
            }
        }

        protected override bool ProcessCmdKey(ref Message m, Keys key)
        {
            if (key == Keys.F11) { SetFullscreen(!fullscreen); return true; }
            return base.ProcessCmdKey(ref m, key);
        }

        [STAThread]
        static void Main()
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            Application.Run(new MainForm());
        }
    }
}

