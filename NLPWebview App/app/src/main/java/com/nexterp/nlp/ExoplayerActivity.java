package com.nexterp.nlp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.MediaRouteButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.EventLog;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.drm.FrameworkMediaDrm;
import com.google.android.exoplayer2.drm.HttpMediaDrmCallback;
import com.google.android.exoplayer2.drm.UnsupportedDrmException;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashChunkSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.DebugTextViewHelper;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.CastState;
import com.google.android.gms.cast.framework.CastStateListener;
import com.google.android.gms.cast.framework.IntroductoryOverlay;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.nexterp.nlp.player.VideoQuality;
import com.google.android.gms.cast.framework.CastContext;
import java.util.ArrayList;
import java.util.UUID;
import com.google.android.gms.cast.framework.CastButtonFactory;
public class ExoplayerActivity extends AppCompatActivity implements View.OnClickListener ,ExoPlayer.EventListener, PlaybackControlView.VisibilityListener{

    public static final String PREFER_EXTENSION_DECODERS = "prefer_extension_decoders";
    public static final String DRM_SCHEME_UUID_EXTRA = "drm_scheme_uuid";
    public static final String DRM_LICENSE_URL = "drm_license_url";
    public static final String DRM_KEY_REQUEST_PROPERTIES = "drm_key_request_properties";
    public static final String ACTION_VIEW = "com.google.android.exoplayer.demo.action.VIEW";
    public static final String EXTENSION_EXTRA = "extension";
    public static final String EXTENSION_LIST_EXTRA = "extension_list";
    public static final String ACTION_VIEW_LIST ="com.google.android.exoplayer.demo.action.VIEW_LIST";
    public static final String URI_LIST_EXTRA = "uri_list";
    private static String uri3;
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private DataSource.Factory mediaDataSourceFactory;
    private SimpleExoPlayer player;
    private DefaultTrackSelector trackSelector;
    private TrackSelectionHelper trackSelectionHelper;
    private TrackGroupArray lastSeenTrackGroupArray;
    private boolean needRetrySource;
    private ProgressBar progressBar;
    private ImageButton playBtn;
    private ImageButton pauseBtn;
    private ImageButton replyBtn;
    private MediaRouteButton mMediaRouteButton;
    private Handler mainHandler;
    private ExoplayerEventLogger eventLogger;
    private SimpleExoPlayerView simpleExoPlayerView;
    private SessionManager mSessionManager;
    private CastContext mCastContext;
    private MediaSource mSelectedMedia;
    private SessionManagerListener<CastSession> mSessionManagerListener;
    private MenuItem mediaRouteMenuItem;
    public String[] extensions2;
    private CastSession mCastSession;
    private Toolbar mToolbar;
    private boolean shouldAutoPlay = true;
    private int resumeWindow;
    private long resumePosition;
    private BottomSheetBehavior bottomSheetBehavior = null;
    private boolean isDevMode = false;
    private int index;
    private Uri uri2;
    private PlaybackLocation mLocation;
    private PlaybackState mPlaybackState;
    private long position;
    private Uri[] uris2;
    public enum PlaybackLocation {
        LOCAL,
        REMOTE
    }
    public enum PlaybackState {
        PLAYING, PAUSED, BUFFERING, IDLE
    }
    public Toolbar toolbar ;
    public String licenseUrl;
    public String[] licenseCustomData;
    public String Quality;
    public String ipaddress;
    public static String mime;
    // INSTANCE OF ANDROID WEB SERVER
    private AndroidWebServerActivity androidWebServer;
    private BroadcastReceiver broadcastReceiverNetworkState;
    private static boolean isStarted = false;
    private TextView editTextPort;
    private IntroductoryOverlay mIntroductoryOverlay;
    private CastStateListener mCastStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
           getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_exoplayer);
        mLocation=PlaybackLocation.LOCAL;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupActionBar();
        ipaddress=getIpAccess()+"8080";
        mCastContext = CastContext.getSharedInstance(this);
        ImageButton settingBtn = ( ImageButton)findViewById(R.id.playersetting);
        //uri3=Environment.getExternalStorageDirectory()+"/1.mp4";
        playBtn  = ( ImageButton)findViewById(R.id.exo_play);
        //pauseBtn  = ( ImageButton)findViewById(R.id.exo_pause);
        replyBtn  = ( ImageButton)findViewById(R.id.exo_replay);
        replyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( player.getPlaybackState() == ExoPlayer.STATE_ENDED){
                    needRetrySource = true;
                    initializePlayer();
                    return;
                }

                if( player.getPlaybackState() == ExoPlayer.STATE_READY){
                    playBack();
                    return;
                }
            }
        });
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( isDevMode){
                    trackSelectionHelper = new TrackSelectionHelper( null , null);
                    trackSelectionHelper.showSelectionDialog( (Activity)v.getContext() , null , 0);
                    return;
                }
               MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
                if (mappedTrackInfo != null) {
                    trackSelectionHelper.showSelectionDialog( (Activity)v.getContext() ,trackSelector.getCurrentMappedTrackInfo(), 0);
                }

            }
        });
        if( isDevMode) {
            return ;
        }
        mediaDataSourceFactory = buildDataSourceFactory( true );
        View rootView = findViewById(R.id.root);
        rootView.setOnClickListener(this);
        mainHandler = new Handler();
        simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.player_view);
        progressBar = (ProgressBar) findViewById(R.id.videoProgressBar);
        simpleExoPlayerView.setControllerVisibilityListener(this);
        simpleExoPlayerView.requestFocus();
        pauseBtn = (ImageButton) simpleExoPlayerView.findViewById(R.id.exo_pause);
        mCastStateListener = new CastStateListener() {
            @Override
            public void onCastStateChanged(int newState) {
                if (newState != CastState.NO_DEVICES_AVAILABLE) {
                    showIntroductoryOverlay();
                }
            }
        };
        setupCastListener();

        //code for starting local server to stream local videos to chromecast
        if (isConnectedInWifi()) {
            if (!isStarted && startAndroidWebServer()) {
                isStarted = true;
            }
            else if (stopAndroidWebServer()){
                isStarted=false;
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.exoplayer, menu);
        mediaRouteMenuItem = CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), menu,
                R.id.media_route_menu_item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        if (item.getItemId() == R.id.action_settings) {
            intent = new Intent(ExoplayerActivity.this, CastPreference.class);
            startActivity(intent);
        } else if (item.getItemId() == android.R.id.home) {
            ActivityCompat.finishAfterTransition(this);
        }
        return true;
    }

    private void setupActionBar() {

       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initializePlayer() {
        Intent intent = getIntent();
        boolean needNewPlayer = player == null;
        if (needNewPlayer) {
            boolean preferExtensionDecoders = intent.getBooleanExtra(PREFER_EXTENSION_DECODERS, false);
            UUID drmSchemeUuid = intent.hasExtra(DRM_SCHEME_UUID_EXTRA)
                    ? UUID.fromString(intent.getStringExtra(DRM_SCHEME_UUID_EXTRA)) : null;
            DrmSessionManager<FrameworkMediaCrypto> drmSessionManager = null;
            if (drmSchemeUuid != null) {
                String drmLicenseUrl = intent.getStringExtra(DRM_LICENSE_URL);
                String[] keyRequestPropertiesArray = intent.getStringArrayExtra(DRM_KEY_REQUEST_PROPERTIES);
                licenseUrl=drmLicenseUrl;
                licenseCustomData=keyRequestPropertiesArray;
                try {

                    drmSessionManager = buildDrmSessionManager(drmSchemeUuid, drmLicenseUrl,
                            keyRequestPropertiesArray);
                } catch (UnsupportedDrmException e) {
                    int errorStringId = Util.SDK_INT < 18 ? R.string.error_drm_not_supported
                            : (e.reason == UnsupportedDrmException.REASON_UNSUPPORTED_SCHEME
                            ? R.string.error_drm_unsupported_scheme : R.string.error_drm_unknown);
                    showToast(errorStringId);
                    return;
                }
            }

            @DefaultRenderersFactory.ExtensionRendererMode int extensionRendererMode =
                    ((NLPApplication) getApplication()).useExtensionRenderers()
                            ? (preferExtensionDecoders ? DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
                            : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
                            : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF;
            DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(this,
                    drmSessionManager, extensionRendererMode);

            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
            trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
            trackSelectionHelper = new TrackSelectionHelper(trackSelector, videoTrackSelectionFactory);
            lastSeenTrackGroupArray = null;

            player = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector);
            player.addListener(this);

            eventLogger = new ExoplayerEventLogger(trackSelector);
            player.addListener(eventLogger);
            player.setAudioDebugListener(eventLogger);
            player.setVideoDebugListener(eventLogger);
            player.setMetadataOutput(eventLogger);

            simpleExoPlayerView.setPlayer(player);
            player.setPlayWhenReady(false);
            mPlaybackState=PlaybackState.PAUSED;

        }
        if (needNewPlayer || needRetrySource) {
            String action = intent.getAction();
            Uri[] uris;
            String[] extensions;
            if (ACTION_VIEW.equals(action)) {
                uris = new Uri[] {intent.getData()};
                extensions = new String[] {intent.getStringExtra(EXTENSION_EXTRA)};
            } else if (ACTION_VIEW_LIST.equals(action)) {
                String[] uriStrings = intent.getStringArrayExtra(URI_LIST_EXTRA);
                uris = new Uri[uriStrings.length];
                for (int i = 0; i < uriStrings.length; i++) {
                    uris[i] = Uri.parse(uriStrings[i]);
                }
                extensions = intent.getStringArrayExtra(EXTENSION_LIST_EXTRA);
                if (extensions == null) {
                    extensions = new String[uriStrings.length];
                }
            } else {
                //   showToast(getString(R.string.unexpected_intent_action, action));
                return;
            }
            if (Util.maybeRequestReadExternalStoragePermission(this, uris)) {
                // The player will be reinitialized if the permission is granted.
                return;
            }
            MediaSource[] mediaSources = new MediaSource[uris.length];
            for (int i = 0; i < uris.length; i++) {
                mediaSources[i] = buildMediaSource(uris[i], extensions[i]);
            }
            uris2=uris;
            extensions2=extensions;
            MediaSource mediaSource = mediaSources.length == 1 ? mediaSources[0]
                    : new ConcatenatingMediaSource(mediaSources);
            boolean haveResumePosition = resumeWindow != C.INDEX_UNSET;
            if (haveResumePosition) {
                player.seekTo(resumeWindow, resumePosition);
            }


            player.prepare(mediaSource, !haveResumePosition, false);
            needRetrySource = false;
            mPlaybackState=PlaybackState.PLAYING;

        }
    }

    private MediaSource buildMediaSource(Uri uri, String overrideExtension) {

        int type = TextUtils.isEmpty(overrideExtension) ? Util.inferContentType(uri)
                : Util.inferContentType("." + overrideExtension);
        switch (type) {
            case C.TYPE_SS:
                return new SsMediaSource(uri, buildDataSourceFactory(false),
                        new DefaultSsChunkSource.Factory(mediaDataSourceFactory), mainHandler, eventLogger);
            case C.TYPE_DASH:
                DashChunkSource.Factory factory = new DefaultDashChunkSource.Factory(mediaDataSourceFactory);
                return new DashMediaSource(uri, buildDataSourceFactory(false),factory, mainHandler, eventLogger);
            case C.TYPE_HLS:
                return new HlsMediaSource(uri, mediaDataSourceFactory, mainHandler, eventLogger);
                //note that chromecast will work only if the hosting server have enabled Cross-Origin Resource Sharing (CORS)
            case C.TYPE_OTHER:
                return new ExtractorMediaSource(uri, mediaDataSourceFactory, new DefaultExtractorsFactory(),
                        mainHandler, eventLogger);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }



    private DrmSessionManager<FrameworkMediaCrypto> buildDrmSessionManager(UUID uuid,
                                                                           String licenseUrl, String[] keyRequestPropertiesArray) throws UnsupportedDrmException {
        if (Util.SDK_INT < 18) {
            return null;
        }
        HttpMediaDrmCallback drmCallback = new HttpMediaDrmCallback(licenseUrl,
                buildHttpDataSourceFactory(false));
        if (keyRequestPropertiesArray != null) {
            for (int i = 0; i < keyRequestPropertiesArray.length - 1; i += 2) {
                drmCallback.setKeyRequestProperty(keyRequestPropertiesArray[i],
                        keyRequestPropertiesArray[i + 1]);
            }
        }
        return new DefaultDrmSessionManager<>(uuid,
                FrameworkMediaDrm.newInstance(uuid), drmCallback, null, mainHandler, eventLogger);
    }


    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
        NLPApplication exoapp  = (NLPApplication) getApplication();
        return exoapp.buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }
    private HttpDataSource.Factory buildHttpDataSourceFactory(boolean useBandwidthMeter) {
        return ((NLPApplication) getApplication())
                .buildHttpDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }


    public void playBack(){
        long currentTime = Math.max(0, player.getCurrentPosition());
        long updateTime  = currentTime - 10000;
        if( updateTime < 0){
            updateTime = 0;
        }
        switch (mLocation) {
            case LOCAL:
                player.seekTo( updateTime);
                mPlaybackState=PlaybackState.PLAYING;
                simpleExoPlayerView.showController();
                break;
            case REMOTE:
                mPlaybackState = PlaybackState.BUFFERING;
                mCastSession.getRemoteMediaClient().seek(updateTime);
                break;
            default:
                break;
        }

        //player.seekTo( updateTime);
        //simpleExoPlayerView.showController();
    }


    @Override
    public void onNewIntent(Intent intent) {
        shouldAutoPlay = true;
        setIntent(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        if( !isDevMode){
            initializePlayer();

        }
    }

    // Activity input

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // Show the controls on any key event.
        simpleExoPlayerView.showController();
        // If the event was not handled then see if the player view can handle it as a media key event.
        return super.dispatchKeyEvent(event) || simpleExoPlayerView.dispatchMediaKeyEvent(event);
    }


    @Override
    public void onClick(View view) {
    }


    @Override
    public void onVisibilityChange(int visibility) {
        if( progressBar.getVisibility() == View.VISIBLE){
            simpleExoPlayerView.hideController();
        }else{
            simpleExoPlayerView.showController();
        }
    }


    @Override
    public void onLoadingChanged(boolean isLoading) {
        // Do nothing.
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if( playbackState == ExoPlayer.STATE_READY){
            progressBar.setVisibility( View.GONE);
            simpleExoPlayerView.showController();
        }
        if( playbackState == ExoPlayer.STATE_BUFFERING){
            progressBar.setVisibility( View.VISIBLE);
            simpleExoPlayerView.hideController();
        }
    }

    private void showControls() {
        // debugRootView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPositionDiscontinuity() {
    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        // Do
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
        // Do nothing.
    }

    @Override
    public void onPlayerError(ExoPlaybackException e) {
        String errorString = null;
        if (e.type == ExoPlaybackException.TYPE_RENDERER) {
            Exception cause = e.getRendererException();
            if (cause instanceof MediaCodecRenderer.DecoderInitializationException) {
                // Special case for decoder initialization failures.
                MediaCodecRenderer.DecoderInitializationException decoderInitializationException =
                        (MediaCodecRenderer.DecoderInitializationException) cause;
                if (decoderInitializationException.decoderName == null) {
                    if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
                        errorString = getString(R.string.error_querying_decoders);
                    } else if (decoderInitializationException.secureDecoderRequired) {
                        // errorString = getString(R.string.error_no_secure_decoder,
                        //       decoderInitializationException.mimeType);
                    } else {
                        //  errorString = getString(R.string.error_no_decoder,
                        //    decoderInitializationException.mimeType);
                    }
                } else {
                    //  errorString = getString(R.string.error_instantiating_decoder,
                    //    decoderInitializationException.decoderName);
                }
            }
        }
        if (errorString != null) {
            showToast(errorString);
        }
        needRetrySource = true;
        if (isBehindLiveWindow(e)) {
            clearResumePosition();
            initializePlayer();

        } else {
            updateResumePosition();
            showControls();
        }
    }
    private static boolean isBehindLiveWindow(ExoPlaybackException e) {
        if (e.type != ExoPlaybackException.TYPE_SOURCE) {
            return false;
        }
        Throwable cause = e.getSourceException();
        while (cause != null) {
            if (cause instanceof BehindLiveWindowException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }


    private void updateResumePosition() {
        resumeWindow = player.getCurrentWindowIndex();
        resumePosition = player.isCurrentWindowSeekable() ? Math.max(0, player.getCurrentPosition())
                : C.TIME_UNSET;
    }

    private void clearResumePosition() {
        resumeWindow = C.INDEX_UNSET;
        resumePosition = C.TIME_UNSET;
    }






    @Override
    @SuppressWarnings("ReferenceEquality")
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        if (trackGroups != lastSeenTrackGroupArray) {
            MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
            if (mappedTrackInfo != null) {
                if (mappedTrackInfo.getTrackTypeRendererSupport(C.TRACK_TYPE_VIDEO)
                        == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                    showToast(R.string.error_unsupported_video);
                }
                if (mappedTrackInfo.getTrackTypeRendererSupport(C.TRACK_TYPE_AUDIO)
                        == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                    showToast(R.string.error_unsupported_audio);
                }
            }
            lastSeenTrackGroupArray = trackGroups;
        }
    }



    private void showToast(int messageId) {
        showToast(getString(messageId));
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
        if (broadcastReceiverNetworkState != null) {
            unregisterReceiver(broadcastReceiverNetworkState);
        }
        stopAndroidWebServer();
    }

    @Override
    protected void onResume() {
        mCastContext.getSessionManager().addSessionManagerListener(
                mSessionManagerListener, CastSession.class);
        if (mCastSession != null && mCastSession.isConnected()) {
            mLocation=PlaybackLocation.REMOTE;
            mPlaybackState=PlaybackState.BUFFERING;
            position=player.getCurrentPosition();
            index=player.getCurrentPeriodIndex();
            long duration=player.getDuration();
            player.setPlayWhenReady(false);
            loadRemoteMedia(index,duration,position,true);
        } else {
            mLocation=PlaybackLocation.LOCAL;
            mPlaybackState=PlaybackState.PLAYING;
            player.setPlayWhenReady(true);
        }
        super.onResume();
        //player.setPlayWhenReady(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.setPlayWhenReady(false);
        if (mLocation == PlaybackLocation.LOCAL) {
            // since we are playing locally, we need to stop the playback of
            // video (if user is not watching, pause it!)
            mPlaybackState = PlaybackState.PAUSED;
        }
        mCastContext.getSessionManager().removeSessionManagerListener(
                mSessionManagerListener, CastSession.class);
    }

    public boolean isConnectedInWifi() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        NetworkInfo networkInfo = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()
                && wifiManager.isWifiEnabled() && networkInfo.getTypeName().equals("WIFI")) {
            return true;
        }
        return false;
    }
    private void setupCastListener() {
        mSessionManagerListener = new SessionManagerListener<CastSession>() {

            @Override
            public void onSessionEnded(CastSession session, int error) {
                onApplicationDisconnected();
            }

            @Override
            public void onSessionResumed(CastSession session, boolean wasSuspended) {
                onApplicationConnected(session);
            }

            @Override
            public void onSessionResumeFailed(CastSession session, int error) {
                onApplicationDisconnected();
            }

            @Override
            public void onSessionStarted(CastSession session, String sessionId) {
                onApplicationConnected(session);
            }

            @Override
            public void onSessionStartFailed(CastSession session, int error) {
                onApplicationDisconnected();
            }

            @Override
            public void onSessionStarting(CastSession session) {
            }

            @Override
            public void onSessionEnding(CastSession session) {
            }

            @Override
            public void onSessionResuming(CastSession session, String sessionId) {
            }

            @Override
            public void onSessionSuspended(CastSession session, int reason) {
            }

            private void onApplicationConnected(CastSession castSession) {
                mCastSession = castSession;
                player.setPlayWhenReady(false);
                position= player.getCurrentPosition();
                index=player.getCurrentPeriodIndex();
                long duration=player.getDuration();
                loadRemoteMedia(index,duration,position, true);//i am not sure what position 0 will initiate

                /** if (null != mSelectedMedia) {

                 if (mPlaybackState == PlaybackState.PLAYING) {
                 player.setPlayWhenReady(false);
                 long  position2= player.getCurrentPosition();
                 loadRemoteMedia(position2, true);
                 return;
                 } else {
                 mPlaybackState = PlaybackState.IDLE;
                 }
                 }**/
                invalidateOptionsMenu();
            }

            private void onApplicationDisconnected() {
                mPlaybackState = PlaybackState.IDLE;
                mLocation = PlaybackLocation.LOCAL;
                invalidateOptionsMenu();
            }
        };
    }
    private void loadRemoteMedia(int index,long duration,long position, boolean autoPlay) {
        if (mCastSession == null) {
            return;
        }
        final RemoteMediaClient remoteMediaClient = mCastSession.getRemoteMediaClient();
        if (remoteMediaClient == null) {
            return;
        }
        remoteMediaClient.addListener(new RemoteMediaClient.Listener() {
            @Override
            public void onStatusUpdated() {

                Intent intent= new Intent(ExoplayerActivity.this, ExpandedControlsActivity.class);
                startActivity(intent);
                remoteMediaClient.removeListener(this);
            }

            @Override
            public void onMetadataUpdated() {
            }

            @Override
            public void onQueueStatusUpdated() {
            }

            @Override
            public void onPreloadStatusUpdated() {
            }

            @Override
            public void onSendingRemoteMediaRequest() {
            }
        });
       
        if(licenseUrl!=null) {
          
            mCastSession.sendMessage("urn:x-cast:com.nexterp.nlp.licenseurl",licenseUrl);
            if(licenseCustomData!=null){
                
                mCastSession.sendMessage("urn:x-cast:com.nexterp.nlp.licensekey",licenseCustomData[1]);
                mCastSession.sendMessage("urn:x-cast:com.nexterp.nlp.licenseid",licenseCustomData[0]);
            }

        }
        
        mCastSession.sendMessage("urn:x-cast:com.nexterp.nlp.Quality",Quality);
        remoteMediaClient.load(buildMediaInfo(index,duration), autoPlay , position);


    }
    private MediaInfo buildMediaInfo(int index, long duration) {
        MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);

        uri2=uris2[index];
        String extension2=extensions2[index];
        String mimetype;
        
        int streamtype;
        streamtype=MediaInfo.STREAM_TYPE_BUFFERED;
        int type = TextUtils.isEmpty(extension2) ? Util.inferContentType(uri2): Util.inferContentType("." + extension2);
       

        switch (type) {
            case C.TYPE_SS:
                mimetype="application/vnd.ms-sstr+xml";
            case C.TYPE_DASH:
                mimetype="application/dash+xml";
            case C.TYPE_HLS:
                mimetype="application/x-mpegurl";
                streamtype=MediaInfo.STREAM_TYPE_LIVE;
            default: {
                mimetype="videos/mp4";
            }
        }
        

        if (isnotUrl(uri2)){
            uri3= Environment.getExternalStorageDirectory()+uri2.toString();
            mime=mimetype;

            stopAndroidWebServer();
            if (isConnectedInWifi()) {
                if (!isStarted && startAndroidWebServer()) {
                    isStarted = true;
                }
                else if (stopAndroidWebServer()){
                    isStarted=false;
                }
            }
            uri2=Uri.parse(getIpAccess()+"8080");
        }


        uri2=Uri.parse("https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd");
        streamtype=MediaInfo.STREAM_TYPE_BUFFERED;
        mimetype="application/dash+xml";

        return new MediaInfo.Builder(uri2.toString())
                .setContentType(mimetype)
                .setMetadata(movieMetadata)
                .setStreamDuration(duration* 1000)
                .build();

    }


    private boolean startAndroidWebServer() {
        if (!isStarted) {
            int port = 8080;
            try {
                if (port == 0) {
                    throw new Exception();
                }
                androidWebServer = new AndroidWebServerActivity(port);
                androidWebServer.start();
                return true;
            } catch (Exception e) {
               // Toast.makeText(getApplicationContext(), "Free port 8080 for making local server :" + e, Toast.LENGTH_LONG).show();
            }
        }
        return false;
    }
    private boolean stopAndroidWebServer() {
        if (isStarted && androidWebServer != null) {
            androidWebServer.stop();
            return true;
        }
        return false;
    }
    private String getIpAccess() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
        final String formatedIpAddress = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        return "http://" + formatedIpAddress + ":";
    }
    public static String getUri(){
        return uri3;
    }
    public boolean isnotUrl(Uri uri){
        String uri4=uri.toString();
        if(uri4.toLowerCase().contains("http")){return false;}
        return true;

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
                            ExoplayerActivity.this, mediaRouteMenuItem)
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
    /*
    public  void updateVideoQaulity(VideoQuality videoQuality){
        trackSelectionHelper.updateVideoQuality( videoQuality);
    }*/
}
