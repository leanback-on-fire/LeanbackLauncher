package com.google.android.leanbacklauncher.animation;

import android.support.v7.recyclerview.R.styleable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.leanbacklauncher.ActiveFrame;
import com.google.android.leanbacklauncher.ActiveItemsRowView;
import com.google.android.leanbacklauncher.HomeScreenRow;
import com.google.android.leanbacklauncher.MainActivity;
import com.google.android.leanbacklauncher.R;
import com.google.android.leanbacklauncher.util.Preconditions;

public  class EditModeMassFadeAnimator extends PropagatingAnimator<PropagatingAnimator.ViewHolder> implements Joinable {
    private static final String TAG = "EditFadeAnimator";
    private final EditMode mEditMode;

    public enum EditMode {
        ENTER,
        EXIT
    }

    static final class ViewHolder extends PropagatingAnimator.ViewHolder {
        private static  /* synthetic */ int[] f1x162e7c0 ;
        public Direction mDirection;
        private  float mEndAlpha;
        public boolean mOnOffOnly;
        private  float mStartAlpha;

        public enum Direction {
            FADE_IN,
            FADE_OUT
        }

        private static /* synthetic */ int[] m1981x91b6cb64() {
            if (f1x162e7c0 != null) {
                return f1x162e7c0;
            }
            int[] iArr = new int[Direction.values().length];
            try {
                iArr[Direction.FADE_IN.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[Direction.FADE_OUT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            f1x162e7c0 = iArr;
            return iArr;
        }

        ViewHolder(View view, Direction direction) {
            super(view);
            this.mOnOffOnly = false;
            this.mDirection = direction;
            Log.i(TAG, "ViewHolder->mDirection->ordinal:" + mDirection.ordinal());
            Log.i(TAG, "ViewHolder->value:" + m1981x91b6cb64()[direction.ordinal()]);
            switch (m1981x91b6cb64()[direction.ordinal()]) {
                case android.support.v7.recyclerview.R.styleable.RecyclerView_android_descendantFocusability /*1*/:
                    this.mStartAlpha = 0.0f;
                    this.mEndAlpha = 1.0f;
                    break;
                case android.support.v7.recyclerview.R.styleable.RecyclerView_layoutManager /*2*/:
                    this.mStartAlpha = 1.0f;
                    this.mEndAlpha = 0.0f;
                    break;
                default:
                    throw new IllegalStateException("Unknown direction: " + direction);
            }
        }

        ViewHolder(View view, Direction direction, boolean onOffOnly) {
            this(view, direction);
            this.mOnOffOnly = onOffOnly;
        }
    }

    public EditModeMassFadeAnimator(MainActivity activity, EditMode editMode) {
        super(10);
        this.mEditMode = (EditMode) Preconditions.checkNotNull(editMode);
        if (editMode == EditMode.EXIT) {
            setDuration((long) activity.getResources().getInteger(R.integer.edit_mode_exit_fade_duration));
            //setDuration(1000);
        } else {
            setDuration((long) activity.getResources().getInteger(R.integer.edit_mode_entrance_fade_duration));
            //setDuration(500);
        }
        addViews(activity);
    }

    private void addViews(MainActivity activity) {
        for (HomeScreenRow row : activity.getHomeAdapter().getAllRows()) {
            View activeFrame = row.getRowView();
            if (activeFrame instanceof ActiveFrame) {
                for (int i = 0; i < ((ActiveFrame) activeFrame).getChildCount(); i++) {
                    View rowView = ((ActiveFrame) activeFrame).getChildAt(i);
                    if (!(rowView instanceof ActiveItemsRowView)) {
                        addView(new ViewHolder(rowView, this.mEditMode == EditMode.ENTER ? ViewHolder.Direction.FADE_OUT : ViewHolder.Direction.FADE_IN));
                    } else if (!((ActiveItemsRowView) rowView).getEditMode()) {
                        int i2;
                        float alpha = rowView.getAlpha();
                        if (this.mEditMode == EditMode.ENTER) {
                            i2 = 0;
                        } else {
                            i2 = 1;
                        }
                        if (alpha != ((float) i2)) {
                            addView(new ViewHolder(rowView, this.mEditMode == EditMode.ENTER ? ViewHolder.Direction.FADE_OUT : ViewHolder.Direction.FADE_IN));
                        }
                    }
                }
            }
        }
        addView(new ViewHolder(activity.getWallpaperView(), this.mEditMode == EditMode.ENTER ? ViewHolder.Direction.FADE_OUT : ViewHolder.Direction.FADE_IN));
        addView(new ViewHolder(activity.getEditModeView(), this.mEditMode == EditMode.ENTER ? ViewHolder.Direction.FADE_IN : ViewHolder.Direction.FADE_OUT));
        addView(new ViewHolder(activity.getEditModeWallpaper(), this.mEditMode == EditMode.ENTER ? ViewHolder.Direction.FADE_IN : ViewHolder.Direction.FADE_OUT, true));
    }

    protected void onSetupStartValues(ViewHolder holder) {
        if (!holder.mOnOffOnly) {
            holder.view.setAlpha(holder.mStartAlpha);
        } else if (holder.mStartAlpha == 0.0f) {
            holder.view.setVisibility(4);
        } else {
            holder.view.setAlpha(1.0f);
            holder.view.setVisibility(0);
        }
    }

    protected void onUpdateView(ViewHolder holder, float fraction) {
        float alpha = holder.mStartAlpha + ((holder.mEndAlpha - holder.mStartAlpha) * fraction);
        if (!holder.mOnOffOnly) {
            holder.view.setAlpha(alpha);
        } else if (alpha == 0.0f) {
            holder.view.setVisibility(4);
        } else {
            holder.view.setAlpha(1.0f);
            holder.view.setVisibility(0);
        }
    }

    protected void onResetView(ViewHolder holder) {
        int i = 0;
        if (holder.mOnOffOnly) {
            int i2;
            View view = holder.view;
            if (holder.mDirection == ViewHolder.Direction.FADE_IN) {
                i2 = 4;
            } else {
                i2 = 0;
            }
            view.setVisibility(i2);
            View view2 = holder.view;
            if (holder.mDirection != ViewHolder.Direction.FADE_IN) {
                i = 1;
            }
            view2.setAlpha((float) i);
            return;
        }
        holder.view.setAlpha(1.0f);
    }

    public void include(View target) {
        boolean z = false;
        if (target instanceof ActiveFrame) {
            int childCount = ((ActiveFrame) target).getChildCount();
            for (int j = 0; j < childCount; j++) {
                View activeItemsRow = ((ViewGroup) target).getChildAt(j);
                if (activeItemsRow instanceof ActiveItemsRowView) {
                    z = ((ActiveItemsRowView) activeItemsRow).getEditMode();
                }
            }
        }
        ViewHolder.Direction direction = ViewHolder.Direction.FADE_OUT;
        if (!(z && this.mEditMode == EditMode.ENTER)) {
            if (!z && this.mEditMode == EditMode.EXIT) {
            }
            addView(new ViewHolder(target, direction));
        }
        direction = ViewHolder.Direction.FADE_IN;
        addView(new ViewHolder(target, direction));
    }

    public void exclude(View target) {
        int n = size();
        for (int i = 0; i < n; i++) {
            if (((ViewHolder) getView(i)).view == target) {
                removeView(i);
                return;
            }
        }
    }

    @Override
    protected void onResetView(PropagatingAnimator.ViewHolder viewHolder) {

    }

    @Override
    protected void onSetupStartValues(PropagatingAnimator.ViewHolder viewHolder) {

    }

    @Override
    protected void onUpdateView(PropagatingAnimator.ViewHolder viewHolder, float f) {

    }

    public String toString() {
        StringBuilder buf = new StringBuilder().append("EditModeMassFadeAnimator@").append(Integer.toHexString(hashCode())).append(':').append(this.mEditMode == EditMode.ENTER ? "ENTER" : "EXIT").append('{');
        int n = size();
        for (int i = 0; i < n; i++) {
            buf.append("\n    ").append(((ViewHolder) getView(i)).toString().replaceAll("\n", "\n    "));
        }
        return buf.append("\n}").toString();
    }
}
