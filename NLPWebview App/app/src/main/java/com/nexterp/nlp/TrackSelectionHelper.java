package com.nexterp.nlp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.util.Pair;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;

import com.google.android.exoplayer2.RendererCapabilities;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.FixedTrackSelection;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.RandomTrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.nexterp.nlp.player.VideoQuality;
import com.nexterp.nlp.player.VideoQualityAdapter;

import java.util.ArrayList;
import java.util.Arrays;



/**
 * Created by jayprakashk on 30-04-2017.
 */

public class TrackSelectionHelper implements View.OnClickListener {
    private static final TrackSelection.Factory FIXED_FACTORY = new FixedTrackSelection.Factory();
    private static final TrackSelection.Factory RANDOM_FACTORY = new RandomTrackSelection.Factory();

    private final MappingTrackSelector selector;
    private final TrackSelection.Factory adaptiveTrackSelectionFactory;

    private MappingTrackSelector.MappedTrackInfo trackInfo;
    private int rendererIndex;
    private TrackGroupArray trackGroups;
    private boolean isDisabled;
    private MappingTrackSelector.SelectionOverride override;

    private RadioButton defaultRadioBtn;
    private RadioButton [][] trackViews;
    private BottomSheetBehavior bottomSheetBehavior = null;
    private boolean isDevMode = false;
    private ArrayList<VideoQuality> videoQualities = null;

    public TrackSelectionHelper(MappingTrackSelector selector,
                                TrackSelection.Factory adaptiveTrackSelectionFactory) {
        this.selector = selector;
        this.adaptiveTrackSelectionFactory = adaptiveTrackSelectionFactory;
    }



    public  void showSelectionDialog(Activity activity,  MappingTrackSelector.MappedTrackInfo trackInfo,
                                     int rendererIndex) {
        if(!isDevMode){
            this.trackInfo = trackInfo;
            this.rendererIndex = rendererIndex;
            trackGroups = trackInfo.getTrackGroups(rendererIndex);
            isDisabled = selector.getRendererDisabled(rendererIndex);
            override = selector.getSelectionOverride(rendererIndex, trackGroups);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle( activity.getResources().getString(R.string.video_quality) )
                .setView(buildView(builder.getContext()))
                .create()
                .show();
    }


    @SuppressLint("InflateParams")
    private View buildView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.track_selection_dialog, null);
        ListView videoQualityList = (ListView) view.findViewById(R.id.videoQualityList);
        if( videoQualities == null){
            videoQualities = createVideoQualityList( view);
        }
        VideoQualityAdapter videoQualityAdapter = new VideoQualityAdapter( view.getContext() ,videoQualities );
        videoQualityList.setAdapter( videoQualityAdapter);
        videoQualityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ImageView imageView = (ImageView)view.findViewById(R.id.image_view);
                if( imageView.getVisibility() == View.VISIBLE){
                    return;
                }
                // make all videoQuality deselected
                for( int i = 0 ; i < parent.getCount() ; i++){
                    LinearLayout linearLayout = (LinearLayout) parent.getChildAt( i );
                    ImageView imgView = (ImageView)linearLayout.findViewById(R.id.image_view);
                    imgView.setVisibility(View.GONE);
                }
                // make current selected quality selected
                imageView.setVisibility(View.VISIBLE);
                for( int i = 0 ; i < videoQualities.size() ; i++){
                    VideoQuality  vidQuality = videoQualities.get(i);
                    vidQuality.setEnable(false);
                }
                VideoQuality  videoQuality = videoQualities.get(position);
                videoQuality.setEnable(true);
                if( isDevMode) {
                    return;
                }
                String tagValue  =(String)view.getTag(R.string.defaultBtn);
                if (videoQuality.getName().equalsIgnoreCase( view.getResources().getString(R.string.defaultBtn))) {
                    isDisabled = false;
                    override = null;
                }  else {
                    isDisabled = false;
                    int groupIndex = videoQuality.getGroupIndex();
                    int trackIndex = videoQuality.getTrackIndex();
                    override = new MappingTrackSelector.SelectionOverride(FIXED_FACTORY, groupIndex, trackIndex);
                }
                if( selector != null){
                    selector.setRendererDisabled(rendererIndex, isDisabled);
                    if (override != null) {
                        selector.setSelectionOverride(rendererIndex, trackGroups, override);
                    } else {
                        selector.clearSelectionOverrides(rendererIndex);
                    }
                }

            }
        });
        return view;
    }


    public ArrayList<VideoQuality> createVideoQualityList( View view){
        ArrayList<VideoQuality> videoQualities = new ArrayList<VideoQuality>();
        VideoQuality  videoQuality = new VideoQuality();
        videoQuality.setName( view.getResources().getString(R.string.auto));
        videoQuality.setEnable( true);
        videoQualities.add( videoQuality);
        if(!isDevMode){
            for (int groupIndex = 0; groupIndex < trackGroups.length; groupIndex++) {
                TrackGroup group = trackGroups.get(groupIndex);
                for (int trackIndex = 0; trackIndex < group.length; trackIndex++) {
                    VideoQuality  videoQulty = new VideoQuality();
                    videoQulty.setName( ExoplayerUtil.buildTrackName(group.getFormat(trackIndex)));
                    videoQulty.setGroupIndex( groupIndex);
                    videoQulty.setTrackIndex( trackIndex);
                    if (trackInfo.getTrackFormatSupport(rendererIndex, groupIndex, trackIndex)
                            == RendererCapabilities.FORMAT_HANDLED) {
                        videoQulty.setEnable( false);
                    }
                    videoQualities.add( videoQulty);
                }
            }
        }
        return videoQualities;
    }


    @Override
    public void onClick(View view) {
        String tagValue  =(String)view.getTag(R.string.defaultBtn);
        if (tagValue != null && tagValue.equalsIgnoreCase( view.getResources().getString(R.string.defaultBtn))) {
            isDisabled = false;
            override = null;
        }  else {
            isDisabled = false;
            Pair<Integer, Integer> tag = (Pair<Integer, Integer>) view.getTag();
            int groupIndex = tag.first;
            int trackIndex = tag.second;
            if ( override == null || override.groupIndex != groupIndex) {
                override = new MappingTrackSelector.SelectionOverride(FIXED_FACTORY, groupIndex, trackIndex);
            }
        }
        if( selector != null){
            selector.setRendererDisabled(rendererIndex, isDisabled);
            if (override != null) {
                selector.setSelectionOverride(rendererIndex, trackGroups, override);
            } else {
                selector.clearSelectionOverrides(rendererIndex);
            }
        }


    }

    private void setOverride(int group, int[] tracks, boolean enableRandomAdaptation) {
        TrackSelection.Factory factory = tracks.length == 1 ? FIXED_FACTORY
                : (enableRandomAdaptation ? RANDOM_FACTORY : adaptiveTrackSelectionFactory);
        override = new MappingTrackSelector.SelectionOverride(factory, group, tracks);
    }

    // Track array manipulation.

    private static int[] getTracksAdding(MappingTrackSelector.SelectionOverride override, int addedTrack) {
        int[] tracks = override.tracks;
        tracks = Arrays.copyOf(tracks, tracks.length + 1);
        tracks[tracks.length - 1] = addedTrack;
        return tracks;
    }

    private static int[] getTracksRemoving(MappingTrackSelector.SelectionOverride override, int removedTrack) {
        int[] tracks = new int[override.length - 1];
        int trackCount = 0;
        for (int i = 0; i < tracks.length + 1; i++) {
            int track = override.tracks[i];
            if (track != removedTrack) {
                tracks[trackCount++] = track;
            }
        }
        return tracks;
    }


}
