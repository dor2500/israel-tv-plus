import type { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.israeltv.plus.ios',
  appName: 'ישראל TV פלוס',
  webDir: 'www',
  server: {
    url: 'https://dor2500.github.io/israel-tv-plus/',
    cleartext: false,
    allowNavigation: ['dor2500.github.io', '*.github.io']
  },
  ios: {
    contentInset: 'never'
  }
};

export default config;
