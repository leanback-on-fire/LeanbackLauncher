package com.rockon999.android.leanbacklauncher;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.rockon999.android.leanbacklauncher.apps.AppsAdapter;
import com.rockon999.android.leanbacklauncher.core.LaunchException;
import com.rockon999.android.leanbacklauncher.settings.FullScreenSettingsActivity;
import com.rockon999.android.leanbacklauncher.util.SettingsUtil;

public abstract class LauncherViewHolder extends ViewHolder implements OnClickListener {
    private static final String TAG = "LauncherViewHolder";
    protected final Context mCtx;
    private int mLaunchColor;
    private Intent mLaunchIntent;
    private boolean mLaunchTranslucent;

    class Launch implements Runnable {
        Launch() {
        }

        public void run() {
            try {
                LauncherViewHolder.this.performLaunch();
            } catch (LaunchException e) {
                Log.e("LauncherViewHolder", "Could not perform launch:", e);
                Toast.makeText(LauncherViewHolder.this.mCtx, R.string.failed_launch, Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected LauncherViewHolder(View v) {
        super(v);
        this.mCtx = v.getContext();
        v.setOnClickListener(this);
    }

    public void onClick(View v) {
        // todo this is horrible
        if (this instanceof AppsAdapter.SettingViewHolder) {
            int type = ((AppsAdapter.SettingViewHolder) this).getSettingsType();

            if (type == SettingsUtil.Type.EDIT_FAVORITES.getCode()) {
                Log.i(TAG, "onClick->addItem->className:" + v.getContext().getClass().getName());
                Context context = v.getContext();
                if (context != null && context instanceof MainActivity) {
                    ((MainActivity) context).editFavorites();
                }
                return;
            } else if (type == SettingsUtil.Type.APP_CONFIGURE.getCode()) {
                Intent intent = new Intent(mCtx, FullScreenSettingsActivity.class);
                mCtx.startActivity(intent);
                return;
            }
        }


        if (v != null && v == this.itemView) {
            ((MainActivity) this.mCtx).beginLaunchAnimation(v, this.mLaunchTranslucent, this.mLaunchColor, new Launch());
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
            throw new LaunchException("Failed to launch intent: " + this.mLaunchIntent, t);
        }
    }

    protected void onLaunchSucceeded() {
    }
}
