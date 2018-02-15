package com.rockon999.android.leanbacklauncher;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.rockon999.android.leanbacklauncher.R;
import com.rockon999.android.leanbacklauncher.animation.ParticipatesInScrollAnimation;

import java.util.ArrayList;

public class HomeScrollManager {
    private boolean mAnimationsEnabled;
    private float mFractionFromTop;
    private final RecyclerView mRecyclerView;
    private int mScrollAnimationThreshold;
    private ArrayList<HomeScrollFractionListener> mScrollListeners;
    private int mScrollPosition;
    private int mScrollThreshold;

    public interface HomeScrollFractionListener {
        void onScrollPositionChanged(int i, float f);
    }

    public HomeScrollManager(Context context, RecyclerView recyclerView) {
        this.mScrollListeners = new ArrayList<>();
        this.mAnimationsEnabled = true;
        this.mRecyclerView = recyclerView;
        Resources resources = context.getResources();
        this.mScrollThreshold = resources.getDimensionPixelOffset(R.dimen.home_scroll_size_search);
        this.mScrollAnimationThreshold = resources.getDimensionPixelOffset(R.dimen.home_scroll_animation_threshold);
    }

    public void addHomeScrollListener(HomeScrollFractionListener listener) {
        if (!this.mScrollListeners.contains(listener)) {
            this.mScrollListeners.add(listener);
            listener.onScrollPositionChanged(this.mScrollPosition, this.mFractionFromTop);
        }
    }

    public void removeHomeScrollListener(HomeScrollFractionListener listener) {
        for (int i = 0; i < this.mScrollListeners.size(); i++) {
            if (this.mScrollListeners.get(i) == listener) {
                this.mScrollListeners.remove(i);
                return;
            }
        }
    }

    public void onScrolled(int dy, int scrollPosition) {
        if (this.mScrollPosition != scrollPosition) {
            this.mScrollPosition = scrollPosition;
            if (this.mScrollThreshold > 0) {
                this.mFractionFromTop = Math.max(0.0f, Math.min(1.0f, Math.abs(((float) this.mScrollPosition) / ((float) this.mScrollThreshold))));
            } else {
                this.mFractionFromTop = 0.0f;
            }
            if (!this.mAnimationsEnabled && dy < 0 && Math.abs(this.mScrollPosition) <= this.mScrollAnimationThreshold) {
                this.mAnimationsEnabled = true;
                adjustAnimationsEnabledState(this.mRecyclerView);
            }
            updateListeners();
        }
    }

    private void updateListeners() {
        for (int i = 0; i < this.mScrollListeners.size(); i++) {
            this.mScrollListeners.get(i).onScrollPositionChanged(this.mScrollPosition, this.mFractionFromTop);
        }
    }

    public void onScrollStateChanged(int newState) {
        boolean z = false;
        if (newState == 0) {
            z = true;
        }
        this.mAnimationsEnabled = z;
        adjustAnimationsEnabledState(this.mRecyclerView);
    }

    private void adjustAnimationsEnabledState(View view) {
        if (view instanceof ParticipatesInScrollAnimation) {
            ((ParticipatesInScrollAnimation) view).setAnimationsEnabled(this.mAnimationsEnabled);
        }
        if (view instanceof ViewGroup) {
            int count = ((ViewGroup) view).getChildCount();
            for (int i = 0; i < count; i++) {
                adjustAnimationsEnabledState(((ViewGroup) view).getChildAt(i));
            }
        }
    }
}
