package com.example.myapplication;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;

public class LocalWebServerFile extends NanoHTTPD {
    private final Context context;
    private final String fileName;

    public LocalWebServerFile(int port, Context context, String fileName) {
        super(port);
        this.context = context;
        this.fileName = fileName;
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        if (uri.equals("/")) {
            uri = "/index.html";
        }

        String mimeType = getMimeType(uri);

        try {
            FileInputStream inputStream;
            long contentLength;
            File file = new File(fileName, uri);
            Log.e("MAIN", "RUNNING FROM file");
            if (file.exists() && file.isFile()) {
                inputStream = new FileInputStream(file);
                contentLength = file.length();
                return newFixedLengthResponse(Response.Status.OK, mimeType, inputStream, contentLength);
            } else {
                return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "File Not Found");
            }


        } catch (IOException e) {
            e.printStackTrace();
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", "Internal Server Error");
        }
    }

    private String getMimeType(String uri) {
        if (uri.endsWith(".js")) {
            return "application/javascript";
        } else if (uri.endsWith(".css")) {
            return "text/css";
        } else if (uri.endsWith(".html")) {
            return "text/html";
        } else if (uri.endsWith(".json")) {
            return "application/json";
        } else if (uri.endsWith(".png")) {
            return "image/png";
        } else if (uri.endsWith(".jpg") || uri.endsWith(".jpeg")) {
            return "image/jpeg";
        } else {
            return "text/html";
        }
    }
}
