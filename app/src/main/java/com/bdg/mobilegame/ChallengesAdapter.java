package com.bdg.mobilegame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ChallengesAdapter extends BaseAdapter {

    private Context context;
    private int[] challengeImages;

    public ChallengesAdapter(Context context, int[] challengeImages) {
        this.context = context;
        this.challengeImages = challengeImages;
    }

    @Override
    public int getCount() {
        return challengeImages.length;
    }

    @Override
    public Object getItem(int position) {
        return challengeImages[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            // Inflate the layout for each grid item
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.grid_item, null);
        }

        // Set the image for the current grid item
        ImageView imageView = convertView.findViewById(R.id.challengeImage);
        imageView.setImageResource(challengeImages[position]);

        return convertView;
    }
}
