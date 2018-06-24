package com.nexterp.nlp;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastState;
import com.google.android.gms.cast.framework.CastStateListener;
import com.google.android.gms.cast.framework.IntroductoryOverlay;

import java.util.ArrayList;

public class NLPActivity extends AppCompatActivity {

    private final static int FILECHOOSER_RESULTCODE = 1;
    private static final int INPUT_FILE_REQUEST_CODE = 1;
    ProgressBar nlpProgress;
    WebView nlpWebView;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mFilePathCallback;
    AndroidJavascriptInterface nlpJavascriptInterface ;
    private Toolbar mToolbar;
    private CastContext mCastContext;
    private MenuItem mediaRouteMenuItem;
    private IntroductoryOverlay mIntroductoryOverlay;
    private CastStateListener mCastStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nlp);
       // setupActionBar();
        Button lauchplayerBtn  = (Button)findViewById(R.id.lauchplayer);
        lauchplayerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchPlayer();
            }
        });
        /*
        nlpWebView = (WebView) findViewById(R.id.nlpwebview);
        nlpProgress = (ProgressBar)findViewById(R.id.nlpProgress);
        nlpwebViewSettings();
        setWebChromeClient();
        nlpJavascriptInterface = new AndroidJavascriptInterface( this, nlpWebView);
        nlpWebView.addJavascriptInterface( nlpJavascriptInterface,"androidApp");
      //  nlpWebView.loadUrl("https://apac.nexterp.in/nlp/nlp/login");
       // nlpWebView.loadUrl("http://192.168.35.98:8080/nlp/nlp/login");
      //  nlpWebView.loadUrl("http://192.168.35.4:10000/nlp/nlp/login");
       // nlpWebView.loadUrl("http://192.168.36.34:8080/nlp/nlp/login");
        //nlpWebView.loadUrl("file:///android_asset/filebrowser.html");
        nlpWebView.loadUrl("file:///android_asset/onlyofficepoc.html");
    */
        
    @Override
    protected void onResume() {
        //mCastContext.addCastStateListener(mCastStateListener);
        super.onResume();
    }

    @Override
    protected void onPause() {
        //mCastContext.removeCastStateListener(mCastStateListener);
        super.onPause();
    }

    protected  void launchPlayer( ){
        Intent intent = new Intent(this, ExoplayerActivity.class);
        Resource resource = createResource();
        ArrayList<String> drmKeyRequestPropertiesList = new ArrayList<>();
        drmKeyRequestPropertiesList.add("X-NextEdDRM-Message");
        drmKeyRequestPropertiesList.add( "8ce3c075-428a-40ab-9795-2e042f9f259d" );
        //      intent.putExtra(exoplayer.PREFER_EXTENSION_DECODERS, preferExtensionDecoders);
        if (resource.getDrmSchemeUuid() != null) {
            intent.putExtra(ExoplayerActivity.DRM_SCHEME_UUID_EXTRA, resource.getDrmSchemeUuid().toString());
            intent.putExtra(ExoplayerActivity.DRM_LICENSE_URL, resource.getDrmLicenseUrl());
            intent.putExtra(ExoplayerActivity.DRM_KEY_REQUEST_PROPERTIES, drmKeyRequestPropertiesList.toArray(new String[0]));
        }
        intent.setData(Uri.parse(resource.getUri()))
                .putExtra(ExoplayerActivity.EXTENSION_EXTRA, resource.getExtension())
                .setAction(ExoplayerActivity.ACTION_VIEW);
        startActivity(intent);
    }


    private Resource createResource(){
        Resource resource = new Resource();
        resource.setDrmSchemeUuid(C.WIDEVINE_UUID);
        Log.e("uuid",(C.WIDEVINE_UUID).toString());
       //resource.setDrmLicenseUrl( "https://apac.nexterp.in/nextdrm/nextdrm/license");
        //resource.setDrmLicenseUrl( "192.168.43.246/receiver/license");
        resource.setDrmLicenseUrl( "https://proxy.uat.widevine.com/proxy?video_id=e06c39f1151da3df&provider=widevine_test");
        //resource.setUri("https://d34h5de3fkci09.cloudfront.net/100000000082/data/8ce3c075-428a-40ab-9795-2e042f9f259d/manifest.mpd");
        resource.setUri("https://storage.googleapis.com/wvmedia/cenc/h264/tears/tears.mpd");
        resource.setExtension("mpd");
        return resource;
    }



    private void nlpwebViewSettings(){

        nlpWebView.setVerticalScrollBarEnabled(true);
        WebSettings nlpWebViewSettings = nlpWebView.getSettings();

        nlpWebViewSettings.setJavaScriptEnabled(true);
        nlpWebViewSettings.setTextZoom(100);
        //  nlpWebViewSettings.setPluginState(WebSettings.PluginState.ON );
        nlpWebViewSettings.setUseWideViewPort(true);
        nlpWebViewSettings.setAllowContentAccess(true);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            nlpWebViewSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        nlpWebView.setWebViewClient(new NlpWebViewClient());
        nlpWebViewSettings.setAppCacheEnabled(false);
        nlpWebViewSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        nlpWebViewSettings.setLoadsImagesAutomatically(true);
        nlpWebViewSettings.setBuiltInZoomControls(true);
        nlpWebViewSettings.setDisplayZoomControls(false);
        nlpWebViewSettings.setSupportZoom(true);
        nlpWebViewSettings.setLoadWithOverviewMode(true);
        nlpWebViewSettings.setUseWideViewPort(true);
        nlpWebViewSettings.setDomStorageEnabled(true);
        nlpWebViewSettings.setAllowFileAccessFromFileURLs(true);
        nlpWebViewSettings.setAllowUniversalAccessFromFileURLs( true);
        nlpWebViewSettings.setJavaScriptCanOpenWindowsAutomatically( true );
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            nlpWebView.setWebContentsDebuggingEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // chromium, enable hardware acceleration
            nlpWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            nlpWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        nlpWebViewSettings.setAllowUniversalAccessFromFileURLs(true);
    }

    public void setWebChromeClient() {
        nlpWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {

            }

            //The undocumented magic method override
            //Eclipse will swear at you if you try to put @Override here
            // For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {

                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "File Chooser"), FILECHOOSER_RESULTCODE);
            }

            // For Android 3.0+
            public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(
                        Intent.createChooser( intent, "File Browser"),
                        FILECHOOSER_RESULTCODE);
            }

            //For Android 4.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser( intent , "File Chooser"), FILECHOOSER_RESULTCODE);

            }

            // For Android 5.0
            public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> filePath, WebChromeClient.FileChooserParams fileChooserParams) {
                // Double check that we don't have any existing callbacks
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePath;

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser( intent , "File Chooser"), FILECHOOSER_RESULTCODE);
                return true;
            }
        });

    }



    @Override
    public void onBackPressed() {
        if (nlpWebView.canGoBack()) {
            nlpWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    class NlpWebViewClient extends WebViewClient
    {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon)
        {
            nlpProgress.setVisibility(View.VISIBLE);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url)
        {
            nlpProgress.setVisibility(View.GONE);
            super.onPageFinished(view, url);
            view.clearCache(true);
        }

        @Override
        public void onLoadResource(WebView view, String url)
        {
            super.onLoadResource(view, url);
        }
    }

    private void showIntroductoryOverlay() {
        if (mIntroductoryOverlay != null) {
            mIntroductoryOverlay.remove();
        }
        if ((mediaRouteMenuItem != null) && mediaRouteMenuItem.isVisible()) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    mIntroductoryOverlay = new IntroductoryOverlay.Builder(
                            NLPActivity.this, mediaRouteMenuItem)
                            .setTitleText("Introducing Cast")
                            .setSingleTime()
                            .setOnOverlayDismissedListener(
                                    new IntroductoryOverlay.OnOverlayDismissedListener() {
                                        @Override
                                        public void onOverlayDismissed() {
                                            mIntroductoryOverlay = null;
                                        }
                                    })
                            .build();
                    mIntroductoryOverlay.show();
                }
            });
        }
    }

    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }

            Uri[] results = null;

            // Check that the response is a good one
            if (resultCode == Activity.RESULT_OK) {
                String dataString = data.getDataString();
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};

                }

                mFilePathCallback.onReceiveValue(results);
                mFilePathCallback = null;

            } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                if (requestCode != FILECHOOSER_RESULTCODE || mFilePathCallback == null) {
                    super.onActivityResult(requestCode, resultCode, data);
                    return;
                }

                if (requestCode == FILECHOOSER_RESULTCODE) {

                    if (null == this.mFilePathCallback) {
                        return;

                    }

                    Uri result = null;

                    try {
                        if (resultCode != RESULT_OK) {

                            result = null;

                        } else {

                            // retrieve from the private variable if the intent is null
                            result = data.getData();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "activity :" + e,
                                Toast.LENGTH_LONG).show();
                    }

                    mUploadMessage.onReceiveValue(result);
                }
            }
        }
    }


}


