package com.rockon999.android.leanbacklauncher.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import com.rockon999.android.leanbacklauncher.MainActivity;
import com.rockon999.android.leanbacklauncher.util.Preconditions;

public abstract class RowViewAdapter<VH extends ViewHolder> extends Adapter<VH> {
    protected final MainActivity mContext;

    /* renamed from: RowViewAdapter.1 */
    class C02081 implements OnLayoutChangeListener {
        final /* synthetic */ ViewHolder val$holder;

        C02081(ViewHolder val$holder) {
            this.val$holder = val$holder;
        }

        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            v.removeOnLayoutChangeListener(this);
            RowViewAdapter.this.mContext.includeInLaunchAnimation(this.val$holder.itemView);
        }
    }

    protected RowViewAdapter(Context context) {
        this.mContext = (MainActivity) Preconditions.checkNotNull((MainActivity) context);
    }

    public void onViewDetachedFromWindow(VH holder) {
        super.onViewDetachedFromWindow(holder);
        this.mContext.excludeFromLaunchAnimation(holder.itemView);
    }

    public void onViewAttachedToWindow(VH holder) {
        super.onViewAttachedToWindow(holder);
        if (this.mContext.isLaunchAnimationInProgress()) {
            holder.itemView.addOnLayoutChangeListener(new C02081(holder));
        }
    }
}
