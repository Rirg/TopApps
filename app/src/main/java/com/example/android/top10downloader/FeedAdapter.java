package com.example.android.top10downloader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by rirg9 on 6/8/2017.
 */

public class FeedAdapter extends ArrayAdapter<FeedEntry> {

    private static final String TAG = "FeedAdapter";

    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private final List<FeedEntry> applications;

    public FeedAdapter(@NonNull Context context, int resource, List<FeedEntry> applications) {
        super(context, resource);
        this.layoutInflater = LayoutInflater.from(context);
        this.layoutResource = resource;
        this.applications = applications;
    }

    @Override
    public int getCount() {
        return applications.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            Log.d(TAG, "getView: called with null convertView");
            convertView = layoutInflater.inflate(layoutResource, parent, false);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            Log.d(TAG, "getView: provided a convertView");
            viewHolder = (ViewHolder) convertView.getTag();
        }

        FeedEntry currentApp = applications.get(position);

        viewHolder.tvName.setText(currentApp.getName());
        viewHolder.tvArtist.setText(currentApp.getArtist());
        viewHolder.tvSummary.setText(currentApp.getSummary());

        return convertView;
    }

    private class ViewHolder {
        final TextView tvName;
        final TextView tvArtist;
        final TextView tvSummary;

        ViewHolder(View v) {
            tvName = (TextView) v.findViewById(R.id.tvName);
            tvArtist = (TextView) v.findViewById(R.id.tvArtist);
            tvSummary = (TextView) v.findViewById(R.id.tvSummary);
        }
    }
}
