package com.rockon999.android.leanbacklauncher.recline.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.rockon999.android.leanbacklauncher.recline.util.RefcountBitmapDrawable;

public class RefcountImageView extends AppCompatImageView {
    private boolean mAutoUnrefOnDetach;
    private RectF mClipRect;
    private boolean mHasClipRect;

    protected void onDetachedFromWindow() {
        if (this.mAutoUnrefOnDetach) {
            setImageDrawable(null);
        }
        super.onDetachedFromWindow();
    }

    public void setImageDrawable(Drawable drawable) {
        Drawable previousDrawable = getDrawable();
        super.setImageDrawable(drawable);
        releaseRef(previousDrawable);
    }

    private static void releaseRef(Drawable drawable) {
        if (drawable instanceof RefcountBitmapDrawable) {
            ((RefcountBitmapDrawable) drawable).getRefcountObject().releaseRef();
        } else if (drawable instanceof LayerDrawable) {
            LayerDrawable layerDrawable = (LayerDrawable) drawable;
            int z = layerDrawable.getNumberOfLayers();
            for (int i = 0; i < z; i++) {
                releaseRef(layerDrawable.getDrawable(i));
            }
        }
    }

    protected void onDraw(Canvas canvas) {
        if (this.mHasClipRect) {
            int saveCount = canvas.save();
            canvas.clipRect(this.mClipRect);
            super.onDraw(canvas);
            canvas.restoreToCount(saveCount);
            return;
        }
        super.onDraw(canvas);
    }

    public RefcountImageView(Context context) {
        super(context);
    }

    public RefcountImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RefcountImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
