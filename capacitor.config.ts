import type { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.israeltv.plus.ios',
  appName: 'ישראל TV+',
  webDir: 'www',
  backgroundColor: '#000000',
  ios: {
    contentInset: 'never'
  }
};

export default config;
