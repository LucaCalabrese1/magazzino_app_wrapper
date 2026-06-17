# INTIME Magazzino — APK Capacitor (wrapper remoto)

L'APK carica il sito: `https://www.mondocartaordini.it/magazzino-app/`

Il PHP e l'UI restano sul server in `/var/www/ordini/`. Questa cartella serve **solo** a generare l'APK Android.

## Dove lanciare i comandi

**Sempre dentro questa cartella:**

```
/var/www/ordini/magazzino-app-capacitor/
```

Struttura:

```
ordini/
  app/                          ← backend PHP (già esistente)
  public_html/magazzino-app/    ← PWA sul server (già esistente)
  magazzino-app-capacitor/      ← QUI: wrapper APK
    package.json
    capacitor.config.ts
    www/
      index.html                ← placeholder + redirect fallback
    android/                    ← creato da `npx cap add android` (sul PC)
```

## Prerequisiti (PC con Android Studio)

- Node.js LTS
- Android Studio + Android SDK
- JDK 17+

## Setup (prima volta)

```bash
cd /var/www/ordini/magazzino-app-capacitor

npm install

# Se android/ non esiste ancora:
npx cap add android

npx cap sync android
npx cap open android
```

In Android Studio: **Build → Build Bundle(s) / APK(s) → Build APK(s)**.

> **Nota:** `npx cap init` non serve se usi questo repo: `capacitor.config.ts` e `package.json` ci sono già.
> `cap init` andrebbe lanciato solo su un progetto vuoto; qui è già inizializzato.

## Cambiare dominio (staging / altro server)

Modifica `APP_URL` in `capacitor.config.ts` e la stessa URL in `www/index.html`, poi:

```bash
npx cap sync android
```

## Aggiornare l'app sugli utenti

- **UI e logica:** deploy sul server → utenti vedono subito le novità (nessun nuovo APK).
- **Notifiche push:** dopo login chiede permesso lato server (`push.js`). Se non compare su Android 13+, vedi **`ANDROID_PUSH.md`** e ricompila APK con `POST_NOTIFICATIONS`.
- **Nuovo APK:** icona, permessi manifest, plugin nativi o URL in `capacitor.config.ts`.
