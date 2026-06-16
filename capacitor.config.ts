import type { CapacitorConfig } from '@capacitor/cli';

/**
 * Wrapper remoto: l'APK apre il sito su ordini.
 * Cambia APP_URL se usi un altro dominio (staging, IP interno, ecc.).
 */
const APP_URL = 'https://www.mondocartaordini.it/magazzino-app/dashboard';

const config: CapacitorConfig = {
  appId: 'it.intime.magazzino',
  appName: 'INTIME Magazzino',
  webDir: 'www',
  server: {
    url: APP_URL,
    androidScheme: 'https',
    cleartext: false,
  },
  android: {
    allowMixedContent: false,
  },
};

export default config;
