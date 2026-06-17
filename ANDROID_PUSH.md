# Notifiche push su Android (Capacitor wrapper remoto)

L'APK carica il sito `https://www.mondocartaordini.it/magazzino-app/`.
Le push usano **Web Push + service worker** sul server (non FCM nativo).

## Cosa fa il server (già deployato)

1. Dopo il **login** → redirect con `?registra-push=1` → modale «Consenti notifiche»
2. Registra il dispositivo in `magazzinieri_push_subscriptions`
3. Test dal PC: `GET /magazzino-app/push/test?k=CHIAVE` (chiave in `app/config.ini`)

## Permesso Android 13+ (obbligatorio per nuovo APK)

Dopo `npx cap add android`, modifica:

`android/app/src/main/AndroidManifest.xml`

Aggiungi **prima** di `<application>`:

```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.INTERNET" />
```

Poi ricompila l'APK:

```bash
npx cap sync android
# Android Studio → Build APK
```

Senza `POST_NOTIFICATIONS` il WebView **non** mostra il dialog notifiche su Android 13+.

## Verifica

1. Installa APK aggiornato
2. Login magazziniere → compare modale notifiche → **Consenti**
3. Dal PC apri il link di test push
4. Deve arrivare la notifica anche con app in background

## Se non compare il dialog

- Controlla Impostazioni Android → App INTIME Magazzino → Notifiche (attive?)
- Prova `/magazzino-app/push/registra` dopo login
- Se compare «Service worker non disponibile» → serve nuovo APK con permesso sopra

## FCM / plugin nativo

Non usato in questo POC. Il backend è VAPID (`minishlink/web-push`).
Passare a `@capacitor/push-notifications` + Firebase solo se Web Push nel WebView non funziona.
