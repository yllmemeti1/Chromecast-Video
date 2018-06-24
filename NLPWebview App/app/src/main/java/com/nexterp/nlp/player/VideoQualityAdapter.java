package com.nexterp.nlp.player;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nexterp.nlp.R;

import java.util.ArrayList;

/**
 * Created by jayprakashk on 08-05-2017.
 */

public class VideoQualityAdapter extends ArrayAdapter {
    ArrayList <VideoQuality> videoQualities = null;
    Context context;

    public VideoQualityAdapter(Context context, ArrayList<VideoQuality> videoQualities) {
        super(context, R.layout.video_list_item , videoQualities);
        this.videoQualities = videoQualities;
        this.context = context;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewRow = layoutInflater.inflate(R.layout.video_list_item, null ,true);
        if( i == 0){
            View dividerView = (View) viewRow.findViewById(R.id.videoQualityDivider);
            dividerView.setVisibility( View.GONE);
        }
        if( this.videoQualities.get(i).isEnable()){
            ImageView mimageView = (ImageView) viewRow.findViewById(R.id.image_view);
            mimageView.setVisibility( View.VISIBLE);
        }
        TextView textView = (TextView) viewRow.findViewById(R.id.text_view);
        textView.setText(this.videoQualities.get(i).getName());
        return viewRow;
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return super.getItem(position);
    }


}
