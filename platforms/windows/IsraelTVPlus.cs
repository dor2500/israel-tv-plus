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
[assembly: System.Reflection.AssemblyVersion("2.0.0.0")]

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
            core.Settings.UserAgent = core.Settings.UserAgent + " IsraelTVPlus/2 Windows";
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
            else if (msg.Contains("\"exit\"")) Close();
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
