package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;

public class GameViewActivity extends AppCompatActivity {
    WebView webView;
    private LocalWebServer localWebServer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_view);

        webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webView.setWebViewClient(new WebViewClient());




    }
    private void game(){
        localWebServer = new LocalWebServer(8080, this,"Game1/balloon_escape");
        try {
            localWebServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                Toast.makeText(GameViewActivity.this, message, Toast.LENGTH_SHORT).show();
                Log.e("MAIN", message);

                result.confirm();
                return true;
            }
        });
        // Load the URL in WebView
        webView.loadUrl("http://localhost:8080/");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (localWebServer != null) {
            localWebServer.stop();
        }
    }
}