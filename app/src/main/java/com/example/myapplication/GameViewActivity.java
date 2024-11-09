package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class GameViewActivity extends AppCompatActivity {
    private WebView webView;
    private LocalWebServer localWebServer;
    private LocalWebServerFile localWebServerFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_view);
        Log.e("Activity","Started game activity");

        webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webView.setWebViewClient(new WebViewClient());

        Intent intent = getIntent();
        String filePath = (intent != null && intent.getStringExtra("path") != null) ?
                intent.getStringExtra("path") : "Game1/balloon_escape";
        boolean isAsset = intent == null || intent.getStringExtra("path") == null;

        Log.d("GameViewActivity", "File path: " + filePath + ", Is Asset: " + isAsset);

        if(isAsset){
            game(filePath);
        }
        else{
            game2(filePath);

        }
    }

    private void game2(String filePath){
        localWebServerFile = new LocalWebServerFile(8081,this,filePath);
        try {
            localWebServerFile.start();
            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                    Toast.makeText(GameViewActivity.this, message, Toast.LENGTH_SHORT).show();
                    Log.e("MAIN", message);
                    result.confirm();
                    return true;
                }
            });
            webView.loadUrl("http://localhost:8081/");
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error starting the local server", Toast.LENGTH_SHORT).show();
        }
    }
    private void game(String filePath) {
        localWebServer = new LocalWebServer(8080, this, filePath);

        try {
            localWebServer.start();
            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                    Toast.makeText(GameViewActivity.this, message, Toast.LENGTH_SHORT).show();
                    Log.e("MAIN", message);
                    result.confirm();
                    return true;
                }
            });
            webView.loadUrl("http://localhost:8080/");
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error starting the local server", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (localWebServer != null) {
            localWebServer.stop();

        }
        if (localWebServerFile != null) {
            localWebServerFile.stop();

        }
    }
    @Override
    public void onBackPressed() {
        if (localWebServer != null) {
            localWebServer.stop();
            Intent intent = new Intent(GameViewActivity.this, MainActivity.class);
            startActivity(intent);
            Log.d("GameViewActivity", "Local server stopped");
        }
        if (localWebServerFile != null) {
            localWebServerFile.stop();
            Intent intent = new Intent(GameViewActivity.this, MainActivity.class);
            startActivity(intent);
            Log.d("GameViewActivity", "Local server stopped");
        }
        super.onBackPressed();
    }
}
