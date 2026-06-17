# Push Notifications — APK Capacitor (FCM)

Il sistema usa **Firebase Cloud Messaging (FCM)** tramite il plugin
`@capacitor/push-notifications` per notifiche native su Android.

---

## 1. Crea progetto Firebase

1. Vai su <https://console.firebase.google.com/>
2. **Aggiungi progetto** → nome es. "intime-magazzino"
3. Nella home del progetto → **Aggiungi app Android**
   - Package: `it.intime.magazzino`
   - Nickname: Magazzino
4. Scarica `google-services.json`
5. Copialo in `android/app/google-services.json`

---

## 2. Service Account (per il backend PHP)

1. Firebase Console → Impostazioni progetto → **Account di servizio**
2. **Genera nuova chiave privata** → scarica il JSON
3. Caricalo sul server in `/var/www/ordini/app/firebase-service-account.json`
   (non committare mai questo file nel repo)
4. Modifica `config.ini`:
   ```ini
   fcm.service_account_json="/var/www/ordini/app/firebase-service-account.json"
   fcm.project_id="intime-magazzino-XXXXX"
   ```
   L'ID progetto lo trovi in Firebase Console → impostazioni → *Project ID*.

---

## 3. Configurazione Android (una volta sola)

### 3a. Fix ProGuard (se ancora non fatto)

In `android/app/build.gradle` riga ~22:

```gradle
// Sostituisci:
proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
// Con:
proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
```

### 3b. Aggiungi plugin Firebase in `android/build.gradle` (root)

```gradle
buildscript {
    dependencies {
        // ...
        classpath 'com.google.gms:google-services:4.4.2'
    }
}
```

### 3c. Applica plugin e Google Services in `android/app/build.gradle`

```gradle
apply plugin: 'com.google.gms.google-services'
```

> Se stai usando Gradle 8+:
> in `android/app/build.gradle` aggiunge nella sezione plugins:
> `id 'com.google.gms.google-services'`

### 3d. AndroidManifest.xml già aggiornato

Deve già avere (è stato aggiunto nella sessione precedente):
```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

---

## 4. Sync e build

```powershell
npx cap sync android
```

Poi compila il debug APK da Android Studio oppure:
```powershell
cd android
.\gradlew assembleDebug
```

APK generato: `android/app/build/outputs/apk/debug/app-debug.apk`

---

## 5. Migration DB (server)

```bash
mysql -u ordini_user -p qiurpult_dbmaster < /var/www/ordini/migrations/create_magazzinieri_fcm_tokens.sql
```

---

## 6. Come funziona il flusso

```
APK apre app
  └─ push.js rileva window.Capacitor.isNativePlatform() === true
       └─ chiama PushNotifications.requestPermissions()
            └─ utente accetta
                 └─ PushNotifications.register()
                      └─ evento "registration" → token FCM
                           └─ POST /magazzino-app/push/fcm-token  { token: "..." }
                                └─ server salva in magazzinieri_fcm_tokens

Quando succede un evento (es. nuovo ordine):
  MagazzinoAppFcm::sendToImpiegato(...)
    └─ backend genera JWT firmato con service account
         └─ POST FCM HTTP v1 API
              └─ Google → dispositivo → notifica nativa
```

---

## 7. Test

Dopo aver installato il nuovo APK e aperto la pagina push/registra:

```
GET https://TUO_DOMINIO/magazzino-app/push/test?k=CHIAVE_IN_CONFIG_INI
```

Deve apparire la notifica sul telefono.
