package com.example.myapplication;

import static android.os.Environment.getExternalStoragePublicDirectory;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MainActivity extends AppCompatActivity {
    ImageButton game2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ImageButton game1 = findViewById(R.id.game1);
      game2 = findViewById(R.id.game2);

        game1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Game2Activity.class);
                intent.putExtra("isGame1", true);
                startActivity(intent);
            }
        });
        game2.setOnClickListener(view -> {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                downloadFileFromGoogleDrive();
            }
        });

    }
    private static final int REQUEST_PERMISSIONS = 1;




    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void downloadFileFromGoogleDrive() {
        String fileId = "1BaGgSmGEYL8QssMGLil8wgmsobtpfYse";
        String fileUrl = "https://drive.google.com/uc?export=download&id=" + fileId;
        File directory = new File(getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),"MyApp");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File file = new File(directory,"game2.zip");



        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));

        request.setTitle("Downloading Game");
        request.setDescription("Downloading the game from Google Drive...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationUri(Uri.fromFile(file));

        // Initialize DownloadManager and enqueue the request
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            long downloadId = downloadManager.enqueue(request);

            // Register a receiver to listen for download completion
            registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    if (id == downloadId) {
                        // Once download is complete, unzip and load the game
                        unzipAndLoadGame(file, context);
                    }
                }
            }, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_NOT_EXPORTED);
        }
    }

    private void unzipAndLoadGame(File zipFile, Context context) {
        File targetDirectory = new File(context.getFilesDir(), "game");

        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                File newFile = new File(targetDirectory, entry.getName());
                if (entry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    newFile.getParentFile().mkdirs();
                    try (FileOutputStream fileOutputStream = new FileOutputStream(newFile)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = zipInputStream.read(buffer)) > 0) {
                            fileOutputStream.write(buffer, 0, length);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        game2.setVisibility(View.GONE);

    }

//    private void loadGameInWebView() {
//        WebView webView = findViewById(R.id.webView);
//        WebSettings webSettings = webView.getSettings();
//        webSettings.setJavaScriptEnabled(true);
//
//        File gameFile = new File(getFilesDir(), "game/index.html");
//        webView.loadUrl("file://" + gameFile.getAbsolutePath());
//    }

}