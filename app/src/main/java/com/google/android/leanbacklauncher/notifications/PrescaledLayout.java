package com.google.android.leanbacklauncher.notifications;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import com.google.android.leanbacklauncher.R;

public class PrescaledLayout extends ViewGroup {
    private View mContent;
    private float mScaleFactor;

    public PrescaledLayout(Context context) {
        super(context);
    }

    public PrescaledLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PrescaledLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        setClipChildren(false);
        this.mScaleFactor = getResources().getFraction(R.fraction.lb_focus_zoom_factor_medium, 1, 1);
        this.mContent = getChildAt(0);
        this.mContent.setPivotX(0.0f);
        this.mContent.setPivotY(0.0f);
        this.mContent.setScaleX(1.0f / this.mScaleFactor);
        this.mContent.setScaleY(1.0f / this.mScaleFactor);
    }

    public int getContentHeight() {
        return (int) (((float) this.mContent.getMeasuredHeight()) / this.mScaleFactor);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        this.mContent.measure(MeasureSpec.makeMeasureSpec((int) Math.ceil((double) (((float) width) * this.mScaleFactor)), 1073741824), 0);
        setMeasuredDimension(width, (int) (((float) this.mContent.getMeasuredHeight()) / this.mScaleFactor));
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        this.mContent.layout(0, 0, this.mContent.getMeasuredWidth(), this.mContent.getMeasuredHeight());
    }
}
