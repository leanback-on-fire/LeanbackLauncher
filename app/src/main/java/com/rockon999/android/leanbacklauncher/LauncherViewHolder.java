package com.rockon999.android.leanbacklauncher;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.rockon999.android.leanbacklauncher.R;
import com.rockon999.android.leanbacklauncher.apps.BannerView;
import com.rockon999.android.leanbacklauncher.core.LaunchException;

public abstract class LauncherViewHolder extends ViewHolder implements OnClickListener {
    private static final String TAG = "LauncherViewHolder";
    protected final Context mCtx;
    private int mLaunchColor;
    private Intent mLaunchIntent;
    private boolean mLaunchTranslucent;

    /* renamed from: LauncherViewHolder.1 */
    class C01571 implements Runnable {
        C01571() {
        }

        public void run() {
            try {
                LauncherViewHolder.this.performLaunch();
            } catch (LaunchException e) {
                Log.e("LauncherViewHolder", "Could not perform launch:", e);
                Toast.makeText(LauncherViewHolder.this.mCtx, R.string.failed_launch, 0).show();
            }
        }
    }

    protected LauncherViewHolder(View v) {
        super(v);
        this.mCtx = v.getContext();
        v.setOnClickListener(this);
    }

    public void onClick(View v) {
        if(v instanceof BannerView && ((BannerView)v).mIsAddItem){
            //启动添加页面
            Log.i(TAG, "onClick->addItem->className:" + v.getContext().getClass().getName());
            Context context =  v.getContext();
            if(context != null && context instanceof MainActivity){
                ((MainActivity)context).preformIconMoreClick();
            }
            //Toast.makeText(v.getContext(), "启动添加页面", Toast.LENGTH_SHORT).show();
            return;
        }
        if (v != null && v == this.itemView) {
            ((MainActivity) this.mCtx).beginLaunchAnimation(v, this.mLaunchTranslucent, this.mLaunchColor, new C01571());
        }
    }

    protected final void setLaunchTranslucent(boolean launchTranslucent) {
        this.mLaunchTranslucent = launchTranslucent;
    }

    protected final void setLaunchColor(int launchColor) {
        this.mLaunchColor = launchColor;
    }

    protected final void setLaunchIntent(Intent launchIntent) {
        this.mLaunchIntent = launchIntent;
    }

    protected void performLaunch() {
        try {
            this.mCtx.startActivity(this.mLaunchIntent);
            onLaunchSucceeded();
        } catch (Throwable t) {
            LaunchException launchException = new LaunchException("Failed to launch intent: " + this.mLaunchIntent, t);
        }
    }

    protected void onLaunchSucceeded() {
    }
}
