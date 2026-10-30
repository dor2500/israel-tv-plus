/* iOS shell (Capacitor), loaded right after hls.min.js in the IPA's index.html only.
 * Native HLS instead of hls.js: AVPlayer has no CORS limits from capacitor://localhost, and gives AirPlay + PiP. */
(function () {
  'use strict';
  window.ITV_IOS = true;
  if (window.Hls) window.Hls.isSupported = function () { return false; };
  var root = document.documentElement;
  root.classList.add('ios');
  // iPadOS reports a Mac user agent, so tell it apart by its screen size
  root.classList.add(Math.min(screen.width, screen.height) >= 600 ? 'ipad' : 'iphone');
})();
