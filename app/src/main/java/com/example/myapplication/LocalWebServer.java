package com.example.myapplication;

import android.content.Context;
import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;
import java.io.InputStream;
//https://drive.google.com/file/d/1BaGgSmGEYL8QssMGLil8wgmsobtpfYse/view?usp=drive_link
//https://drive.google.com/uc?export=download&id=your-file-id
public class LocalWebServer extends NanoHTTPD {
    private Context context;

    private String fileName;
    public LocalWebServer(int port, Context context,String fileName) {
        super(port);
        this.context = context;
        this.fileName= fileName;

    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        if (uri.equals("/")) {
            uri = "/index.html";
        }
        String mimeType = "text/html";
        if (uri.endsWith(".js")) {
            mimeType = "application/javascript";
        } else if (uri.endsWith(".css")) {
            mimeType = "text/css";
        } else if (uri.endsWith(".html")) {
            mimeType = "text/html";
        } else if (uri.endsWith(".json")) {
            mimeType = "application/json";
        } else if (uri.endsWith(".png")) {
            mimeType = "image/png";
        } else if (uri.endsWith(".jpg") || uri.endsWith(".jpeg")) {
            mimeType = "image/jpeg";
        }
        try {
            InputStream inputStream = context.getAssets().open(fileName + uri);
            return newFixedLengthResponse(Response.Status.OK, mimeType, inputStream, inputStream.available());
        } catch (IOException e) {
            e.printStackTrace();
            return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "File Not Found");
        }
    }
}
