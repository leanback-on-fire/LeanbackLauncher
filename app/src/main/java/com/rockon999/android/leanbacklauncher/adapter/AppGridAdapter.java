package com.rockon999.android.leanbacklauncher.adapter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.rockon999.android.leanbacklauncher.MainActivity;
import com.rockon999.android.leanbacklauncher.R;
import com.rockon999.android.leanbacklauncher.apps.LaunchPoint;

import java.util.List;


public class AppGridAdapter extends ArrayAdapter<LaunchPoint> {
    private MainActivity mContext;
    private int mResourceId;
    private LayoutInflater mInflator;

    public AppGridAdapter(MainActivity context, int resource, List<LaunchPoint> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResourceId = resource;
        this.mInflator = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        ViewHolder holder;

        if (itemView == null) {
            itemView = mInflator.inflate(mResourceId, parent, false);
            holder = new ViewHolder();
            holder.appIcon = (ImageView) itemView.findViewById(R.id.app_default_icon);
            holder.selectBox = (CheckBox) itemView.findViewById(R.id.check_select);
            holder.textTitle = (TextView) itemView.findViewById(R.id.text_title);
            itemView.setTag(holder);
        } else {
            holder = (ViewHolder) itemView.getTag();
        }

        LaunchPoint launchPoint = getItem(position);

        if (launchPoint != null) {
            holder.appIcon.setImageDrawable(launchPoint.getIconDrawable());
            holder.selectBox.setChecked(mContext.getAppRankings().isFavorite(launchPoint));
            holder.textTitle.setText(launchPoint.getTitle());
        }

        return itemView;
    }

    class ViewHolder {
        ImageView appIcon;
        CheckBox selectBox;
        TextView textTitle;
    }
}
