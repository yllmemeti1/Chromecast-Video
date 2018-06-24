package com.nexterp.nlp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.nexterp.nlp.player.VideoQuality;
import com.nexterp.nlp.player.VideoQualityAdapter;

import java.util.ArrayList;

/**
 * Created by jayprakashk on 05-05-2017.
 */

public class BottomSheetDialog extends BottomSheetDialogFragment {
    ArrayList<VideoQuality> videoQualities = null;
    View view = null;

    public BottomSheetDialog(){
        super();
    }

    @SuppressLint("ValidFragment")
    public BottomSheetDialog(ArrayList<VideoQuality> videoQualiies ){
         super();
         this.videoQualities = videoQualiies;
    }


    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
       // View view = inflater.inflate(R.layout.player_setting_bottom_sheet, container, false);
        view = View.inflate(getContext(), R.layout.player_setting_bottom_sheet, null);
        dialog.setContentView(view);
        ListView videoQualityList = (ListView) view.findViewById(R.id.videoQualityList);
        VideoQualityAdapter videoQualityAdapter = new VideoQualityAdapter( view.getContext() ,videoQualities );
        videoQualityList.setAdapter( videoQualityAdapter);
        videoQualityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // make all videoQuality deselected
                for (int i = 0; i < parent.getCount(); i++) {
                    LinearLayout linearLayout = (LinearLayout) parent.getChildAt(i);
                    ImageView imageView = (ImageView) linearLayout.findViewById(R.id.image_view);
                    imageView.setVisibility(View.GONE);
                }
                // make current selected quality selected
                ImageView imageView = (ImageView) view.findViewById(R.id.image_view);
                imageView.setVisibility(View.VISIBLE);
                VideoQuality videoQuality = videoQualities.get(position);
              //  ((ExoplayerActivity)getActivity()).updateVideoQaulity( videoQuality);
            }
        });
    }


}
