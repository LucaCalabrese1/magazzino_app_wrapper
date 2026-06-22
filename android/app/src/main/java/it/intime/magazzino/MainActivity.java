package it.intime.magazzino;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getBridge().getWebView().addJavascriptInterface(new AndroidDownloader(), "AndroidDownloader");
        getBridge().getWebView().setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            startPdfDownload(url, userAgent, contentDisposition, mimetype);
        });
    }

    private void startPdfDownload(String url, String userAgent, String contentDisposition, String mimetype) {
        runOnUiThread(() -> {
            try {
                String filename = URLUtil.guessFileName(url, contentDisposition, mimetype);
                DownloadManager.Request req = new DownloadManager.Request(Uri.parse(url));
                if (mimetype != null && !mimetype.isEmpty()) {
                    req.setMimeType(mimetype);
                }
                String cookie = CookieManager.getInstance().getCookie(url);
                if (cookie != null && !cookie.isEmpty()) {
                    req.addRequestHeader("Cookie", cookie);
                }
                if (userAgent != null && !userAgent.isEmpty()) {
                    req.addRequestHeader("User-Agent", userAgent);
                }
                req.setDescription("Download in corso...");
                req.setTitle(filename);
                req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
                DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                if (dm != null) {
                    dm.enqueue(req);
                    Toast.makeText(this, "Download etichetta avviato", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "DownloadManager non disponibile", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Errore download etichetta: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public class AndroidDownloader {
        @JavascriptInterface
        public void download(String url) {
            startPdfDownload(url, null, null, "application/pdf");
        }
    }
}
