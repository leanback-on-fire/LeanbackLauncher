package com.google.android.leanbacklauncher;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import com.google.android.leanbacklauncher.core.LaunchException;

public abstract class LauncherViewHolder extends ViewHolder implements OnClickListener {
    protected final Context mCtx;
    private int mLaunchColor;
    private Intent mLaunchIntent;
    private boolean mLaunchTranslucent;

    /* renamed from: com.google.android.leanbacklauncher.LauncherViewHolder.1 */
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
