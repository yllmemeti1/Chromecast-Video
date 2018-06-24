package com.nexterp.nlp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;


import fi.iki.elonen.NanoHTTPD;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class AndroidWebServerActivity extends NanoHTTPD {
    private NLPActivity arc;
    public AndroidWebServerActivity(int port) {
        super(port);
    }
    private String uri;
    private String mimetype;
   private  ExoplayerActivity temp=new ExoplayerActivity();
    public AndroidWebServerActivity(String hostname, int port) {
        super(hostname, port);
    }
    public Context mContext;
@Override
public Response serve(IHTTPSession session) {
    uri=temp.getUri();
    mimetype=getMimeTypeForFile(uri);
    FileInputStream fis = null;
    File file;
    try {
        file = new File(uri);
        //file = new File(Environment.getExternalStorageDirectory().getPath() + "/1.mp3");
        fis = new FileInputStream(file);
    } catch (FileNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
    return newChunkedResponse(Response.Status.OK,mimetype,fis);
    //return newChunkedResponse(Response.Status.OK, "audio/mp3", fis);
}
}