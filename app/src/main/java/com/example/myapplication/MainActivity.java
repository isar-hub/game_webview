package com.example.myapplication;

import static android.os.Environment.getExternalStoragePublicDirectory;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MainActivity extends AppCompatActivity {
    ImageButton game2;
    ImageView icon;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Log.e("Activity","Started main activity");
        icon = findViewById(R.id.game2Icon);
        progressBar = findViewById(R.id.loader);
        ImageButton game1 = findViewById(R.id.game1);
        game2 = findViewById(R.id.game2);
        File targetDirectory = new File(getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "MyApp");
        File file = new File(targetDirectory, "3 DOTS");
        if (file.exists()) {
            icon.setVisibility(View.GONE);

        }
        game1.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, GameViewActivity.class);
            startActivity(intent);
        });
        game2.setOnClickListener(view -> {

            if (file.exists()) {
                Intent intent = new Intent(MainActivity.this, GameViewActivity.class);
                intent.putExtra("path", file.getAbsolutePath());
                startActivity(intent);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    progressBar.setVisibility(View.VISIBLE);
                    icon.setVisibility(View.GONE);
                    downloadFileFromGoogleDrive();
                }
            }

        });

    }

    private static final int REQUEST_PERMISSIONS = 1;


    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void downloadFileFromGoogleDrive() {
        String fileId = "1BaGgSmGEYL8QssMGLil8wgmsobtpfYse";
        String fileUrl = "https://drive.google.com/uc?export=download&id=" + fileId;
        File directory = new File(getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "MyApp");

        Log.d("DownloadFile", "Checking if directory exists...");
        if (!directory.exists()) {
            directory.mkdirs();
            Log.d("DownloadFile", "Directory created at: " + directory.getAbsolutePath());
        } else {
            Log.d("DownloadFile", "Directory already exists: " + directory.getAbsolutePath());
        }

        File file = new File(directory, "game2.zip");
        if (file.exists()) {
            Log.d("DownloadFile", "File already exists: " + file.getAbsolutePath());
            unzipAndLoadGame(file, this);

        } else {
            Log.d("DownloadFile", "File will be downloaded to: " + file.getAbsolutePath());
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
            request.setTitle("Downloading Game");
            request.setDescription("Downloading the game from Google Drive...");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationUri(Uri.fromFile(file));

            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            if (downloadManager != null) {
                long downloadId = downloadManager.enqueue(request);
                Log.d("DownloadFile", "Download started with ID: " + downloadId);


                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> {

                    if (file.exists()) {
                        icon.setVisibility(View.GONE);
                        unzipAndLoadGame(file,this);
                        Toast.makeText(this, "Click to play", Toast.LENGTH_SHORT).show();
                    }


                }, 5000);  // Delay in milliseconds (e.g., 2000ms = 2 seconds)

                registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                        Log.d("DownloadFile", "Download completed with ID: " + id);
                        if (id == downloadId) {
                            Log.d("DownloadFile", "Download completed with ID: " + downloadId);
                            Log.e("Main", "Unzipping " + file.getAbsolutePath());
                            unzipAndLoadGame(file, context);
                        }
                    }
                }, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_NOT_EXPORTED);
            } else {
                Log.e("DownloadFile", "Failed to get DownloadManager service.");
            }
        }

    }

    private void unzipAndLoadGame(File zipFile, Context context) {
        File targetDirectory = new File(getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "MyApp");
        File file = new File(targetDirectory, "3 DOTS");
        if (file.exists()) {
            Log.e("FILE", "FILE ALREADY EXISTS");
        } else {

            Log.e("UnzipFile", "Zip file name = " + zipFile);
            Log.e("UnzipFile", "Destination folder = " + targetDirectory);
            try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile))) {
                ZipEntry entry;
                byte[] buffer = new byte[1024];
                while ((entry = zipInputStream.getNextEntry()) != null) {
                    File newFile = new File(targetDirectory + File.separator, entry.getName());
                    if (entry.isDirectory()) {
                        newFile.mkdirs();
                        Log.d("UnzipFile", "Directory created: " + newFile.getAbsolutePath());
                    } else {
                        newFile.getParentFile().mkdirs();
                        Log.d("UnzipFile", "Extracting file: " + newFile.getAbsolutePath());
                        try (FileOutputStream fileOutputStream = new FileOutputStream(newFile)) {
                            int length;
                            while ((length = zipInputStream.read(buffer)) > 0) {
                                fileOutputStream.write(buffer, 0, length);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                Log.e("UnzipFile", "Error unzipping file: " + e.getMessage());
                e.printStackTrace();
            }

            progressBar.setVisibility(View.GONE);
        }
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