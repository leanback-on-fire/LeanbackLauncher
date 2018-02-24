package com.google.android.leanbacklauncher.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import com.google.android.leanbacklauncher.MainActivity;
import com.google.android.leanbacklauncher.util.Preconditions;

public abstract class RowViewAdapter<VH extends ViewHolder> extends Adapter<VH> {
    protected final Context mContext;

    protected RowViewAdapter(Context context) {
        this.mContext = (Context) Preconditions.checkNotNull(context);
    }

    public void onViewDetachedFromWindow(VH holder) {
        super.onViewDetachedFromWindow(holder);
        if (this.mContext instanceof MainActivity) {
            ((MainActivity) this.mContext).excludeFromLaunchAnimation(holder.itemView);
        }
    }

    public void onViewAttachedToWindow(final VH holder) {
        super.onViewAttachedToWindow(holder);
        if ((this.mContext instanceof MainActivity) && ((MainActivity) this.mContext).isLaunchAnimationInProgress()) {
            holder.itemView.addOnLayoutChangeListener(new OnLayoutChangeListener() {
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    v.removeOnLayoutChangeListener(this);
                    ((MainActivity) RowViewAdapter.this.mContext).includeInLaunchAnimation(holder.itemView);
                }
            });
        }
    }
}
