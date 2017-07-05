package com.rockchips.android.leanbacklauncher.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.rockchips.android.leanbacklauncher.R;
import com.rockchips.android.leanbacklauncher.apps.LaunchPoint;
import java.util.List;


/**
 * 添加应用适配器
 * Created by GaoFei on 2017/7/4.
 */

public class AllAppGridAdapter extends ArrayAdapter<LaunchPoint> {
    private Context mContext;
    private int mResourceId;
    private LayoutInflater mInflator;
    public AllAppGridAdapter(Context context, int resource, List<LaunchPoint> objects) {
        super(context, resource, objects);
        mResourceId = resource;
        mContext = context;
        mInflator = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        ViewHolder holder;
        if(itemView == null){
            itemView = mInflator.inflate(mResourceId, parent, false);
            holder = new ViewHolder();
            holder.appIcon = (ImageView) itemView.findViewById(R.id.img_photo);
            holder.selectBox = (CheckBox) itemView.findViewById(R.id.check_select);
            holder.textTitle = (TextView) itemView.findViewById(R.id.text_title);
            itemView.setTag(holder);
        }else{
            holder = (ViewHolder) itemView.getTag();
        }
        LaunchPoint launchPoint = getItem(position);
        if(launchPoint != null){
            holder.appIcon.setImageDrawable(launchPoint.getIconDrawable());
            holder.selectBox.setChecked(launchPoint.isRecommendApp());
            holder.textTitle.setText(launchPoint.getTitle());
        }
        return itemView;
    }

    class ViewHolder{
        ImageView appIcon;
        CheckBox selectBox;
        TextView textTitle;
    }
}
