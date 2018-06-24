package com.nexterp.nlp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.android.exoplayer2.C;

import java.util.ArrayList;

/**
 * Created by jayprakash on 11-04-2017.
 */

public class AndroidJavascriptInterface {


    Context context = null;
    WebView webView = null ;

    AndroidJavascriptInterface(Context context , WebView webView){
        this.context = context;
        this.webView = webView;
    }



    @JavascriptInterface
    public void openExoPlayer(  String drmLicenseUrl  , String manifest , String resourceUUID , String extension ){
        System.out.println( "drmLicenseUrl - "+ drmLicenseUrl +" - uri - "+ manifest +" resourceUUID - "+ resourceUUID + "extension" + extension );
        Intent intent = new Intent(this.context, ExoplayerActivity.class);
        ArrayList<String> drmKeyRequestPropertiesList = new ArrayList<>();
        drmKeyRequestPropertiesList.add("X-NextEdDRM-Message");
        drmKeyRequestPropertiesList.add( resourceUUID );
        String host ="https:";
        drmLicenseUrl = host + drmLicenseUrl;
        manifest  = host + manifest ;
        if ( drmLicenseUrl != null) {
            intent.putExtra(ExoplayerActivity.DRM_SCHEME_UUID_EXTRA, C.WIDEVINE_UUID.toString());
            intent.putExtra(ExoplayerActivity.DRM_LICENSE_URL, drmLicenseUrl );
            intent.putExtra(ExoplayerActivity.DRM_KEY_REQUEST_PROPERTIES, drmKeyRequestPropertiesList.toArray(new String[0]));
        }
        intent.setData(Uri.parse(manifest))
                .putExtra(ExoplayerActivity.EXTENSION_EXTRA, extension )
                .setAction(ExoplayerActivity.ACTION_VIEW);
        try {
            Activity currActivity = (Activity) context;
            currActivity.startActivity( intent );
        }catch ( android.content.ActivityNotFoundException e){
            Toast.makeText( context , "Please install a File Manager.",Toast.LENGTH_SHORT).show();
        }
    }
}
