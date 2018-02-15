package com.rockon999.android.leanbacklauncher.recline.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;

public class CardView extends BaseCardView {
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        setCardFocusedState(gainFocus);
    }

    public CardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public CardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CardView(Context context) {
        super(context);
    }
}
