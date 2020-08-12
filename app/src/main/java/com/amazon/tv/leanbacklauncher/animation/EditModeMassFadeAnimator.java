package com.amazon.tv.leanbacklauncher.animation;

import android.view.View;
import android.view.ViewGroup;

import com.amazon.tv.leanbacklauncher.ActiveFrame;
import com.amazon.tv.leanbacklauncher.EditableAppsRowView;
import com.amazon.tv.leanbacklauncher.HomeScreenRow;
import com.amazon.tv.leanbacklauncher.MainActivity;
import com.amazon.tv.leanbacklauncher.R;
import com.amazon.tv.leanbacklauncher.util.Preconditions;

import java.util.Iterator;

import static com.amazon.tv.leanbacklauncher.animation.EditModeMassFadeAnimator.ViewHolder.Direction;

public final class EditModeMassFadeAnimator extends PropagatingAnimator<EditModeMassFadeAnimator.ViewHolder> implements Joinable {
    private final EditMode mEditMode;

    public enum EditMode {
        ENTER,
        EXIT
    }

    static final class ViewHolder extends PropagatingAnimator.ViewHolder {
        public Direction mDirection;
        private final float mEndAlpha;
        public boolean mOnOffOnly;
        private final float mStartAlpha;

        public enum Direction {
            FADE_IN,
            FADE_OUT
        }

        ViewHolder(View view, Direction direction) {
            super(view);
            this.mOnOffOnly = false;
            this.mDirection = direction;
            switch (direction) {
                case FADE_IN:
                    this.mStartAlpha = 0.0f;
                    this.mEndAlpha = 1.0f;
                    return;
                case FADE_OUT:
                    this.mStartAlpha = 1.0f;
                    this.mEndAlpha = 0.0f;
                    return;
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
        this.mEditMode = Preconditions.checkNotNull(editMode);
        if (editMode == EditMode.EXIT) {
            setDuration(activity.getResources().getInteger(R.integer.edit_mode_exit_fade_duration));
        } else {
            setDuration(activity.getResources().getInteger(R.integer.edit_mode_entrance_fade_duration));
        }
        addViews(activity);
    }

    private void addViews(MainActivity activity) {
        Iterator it = activity.getHomeAdapter().getAllRows().iterator();
        while (it.hasNext()) {
            View activeFrame = ((HomeScreenRow) it.next()).getRowView();
            if (activeFrame instanceof ActiveFrame) {
                for (int i = 0; i < ((ActiveFrame) activeFrame).getChildCount(); i++) {
                    View rowView = ((ActiveFrame) activeFrame).getChildAt(i);
                    if (!(rowView instanceof EditableAppsRowView)) {
                        addView(new ViewHolder(rowView, this.mEditMode == EditMode.ENTER ? Direction.FADE_OUT : Direction.FADE_IN));
                    } else if (!((EditableAppsRowView) rowView).getEditMode()) {
                        addView(new ViewHolder(rowView, this.mEditMode == EditMode.ENTER ? Direction.FADE_OUT : Direction.FADE_IN));
                    }
                }
            }
        }
        addView(new ViewHolder(activity.getWallpaperView(), this.mEditMode == EditMode.ENTER ? Direction.FADE_OUT : Direction.FADE_IN));
        addView(new ViewHolder(activity.getEditModeView(), this.mEditMode == EditMode.ENTER ? Direction.FADE_IN : Direction.FADE_OUT));
        addView(new ViewHolder(activity.getEditModeWallpaper(), this.mEditMode == EditMode.ENTER ? Direction.FADE_IN : Direction.FADE_OUT, true));
    }

    protected void onSetupStartValues(ViewHolder holder) {
        if (!holder.mOnOffOnly) {
            holder.view.setAlpha(holder.mStartAlpha);
        } else if (holder.mStartAlpha == 0.0f) {
            holder.view.setVisibility(View.INVISIBLE);
        } else {
            holder.view.setAlpha(1.0f);
            holder.view.setVisibility(View.VISIBLE);
        }
    }

    protected void onUpdateView(ViewHolder holder, float fraction) {
        float alpha = holder.mStartAlpha + ((holder.mEndAlpha - holder.mStartAlpha) * fraction);
        if (!holder.mOnOffOnly) {
            holder.view.setAlpha(alpha);
        } else if (alpha == 0.0f) {
            holder.view.setVisibility(View.INVISIBLE);
        } else {
            holder.view.setAlpha(1.0f);
            holder.view.setVisibility(View.VISIBLE);
        }
    }

    protected void onResetView(ViewHolder holder) {
        if (holder.mOnOffOnly) {
            float f;
            holder.view.setVisibility(holder.mDirection == Direction.FADE_IN ? View.INVISIBLE : View.VISIBLE);
            View view = holder.view;
            if (holder.mDirection == Direction.FADE_IN) {
                f = 0.0f;
            } else {
                f = 1.0f;
            }
            view.setAlpha(f);
            return;
        }
        holder.view.setAlpha(1.0f);
    }

    public void include(View target) {
        boolean editModeParticipant = false;
        if (target instanceof ActiveFrame) {
            int childCount = ((ActiveFrame) target).getChildCount();
            for (int j = 0; j < childCount; j++) {
                View activeItemsRow = ((ViewGroup) target).getChildAt(j);
                if (activeItemsRow instanceof EditableAppsRowView) {
                    editModeParticipant = ((EditableAppsRowView) activeItemsRow).getEditMode();
                }
            }
        }
        Direction direction = Direction.FADE_OUT;
        if ((editModeParticipant && this.mEditMode == EditMode.ENTER) || (!editModeParticipant && this.mEditMode == EditMode.EXIT)) {
            direction = Direction.FADE_IN;
        }
        addView(new ViewHolder(target, direction));
    }

    public void exclude(View target) {
        int n = size();
        for (int i = 0; i < n; i++) {
            if (getView(i).view == target) {
                removeView(i);
                return;
            }
        }
    }

    public String toString() {
        StringBuilder buf = new StringBuilder().append("EditModeMassFadeAnimator@").append(Integer.toHexString(hashCode())).append(':').append(this.mEditMode == EditMode.ENTER ? "ENTER" : "EXIT").append('{');
        int n = size();
        for (int i = 0; i < n; i++) {
            buf.append("\n    ").append(getView(i).toString().replaceAll("\n", "\n    "));
        }
        return buf.append("\n}").toString();
    }
}
