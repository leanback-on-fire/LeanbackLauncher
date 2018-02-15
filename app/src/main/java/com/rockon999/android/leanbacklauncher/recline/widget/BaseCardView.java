package com.rockon999.android.leanbacklauncher.recline.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

public abstract class BaseCardView extends ViewGroup {
    private boolean mCardFocused;
    private CardLayoutManager mLayoutMgr;

    public static abstract class CardLayoutManager {
        protected abstract void onLayout(boolean z, int i, int i2, int i3, int i4);

        protected abstract void onMeasure(int i, int i2);

        protected void onCardFocusedStateChanged(boolean focused) {
        }
    }

    public boolean shouldDelayChildPressedState() {
        return false;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.mLayoutMgr.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        this.mLayoutMgr.onLayout(changed, left, top, right, bottom);
    }

    public void setCardFocusedState(boolean focused) {
        if (this.mCardFocused != focused) {
            this.mCardFocused = focused;
            this.mLayoutMgr.onCardFocusedStateChanged(focused);
        }
    }

    public BaseCardView(Context context) {
        super(context);
    }

    public BaseCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BaseCardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
