package com.amazon.tv.leanbacklauncher;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.OnHierarchyChangeListener;

import androidx.leanback.widget.HorizontalGridView;

import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences;
import com.amazon.tv.leanbacklauncher.animation.ParticipatesInScrollAnimation;
import com.amazon.tv.leanbacklauncher.animation.ViewDimmer;
import com.amazon.tv.leanbacklauncher.apps.AppsAdapter;
import com.amazon.tv.leanbacklauncher.capabilities.LauncherConfiguration;

public class ActiveItemsRowView extends HorizontalGridView implements OnHierarchyChangeListener {
    private final boolean mCardElevationSupported;
    private int mCardSpacing;
    AdapterDataObserver mChangeObserver;
    protected ViewDimmer.DimState mDimState;
    private boolean mIsAdjustable;
    private int mNumRows;
    private int mRowHeight;

    public ActiveItemsRowView(Context context) {
        this(context, null, 0);
    }

    public ActiveItemsRowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActiveItemsRowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mChangeObserver = new AdapterDataObserver() {
            public void onChanged() {
                ActiveItemsRowView.this.adjustNumRows();
                Adapter adapter = ActiveItemsRowView.this.getAdapter();
                if ((adapter instanceof AppsAdapter) && ((AppsAdapter) adapter).takeItemsHaveBeenSorted()) {
                    ActiveItemsRowView.this.setSelectedPosition(0);
                }
            }

            public void onItemRangeInserted(int positionStart, int itemCount) {
                ActiveItemsRowView.this.adjustNumRows();
            }

            public void onItemRangeRemoved(int positionStart, int itemCount) {
                ActiveItemsRowView.this.adjustNumRows();
            }
        };
        setChildrenDrawingOrderEnabled(true);
        setAnimateChildLayout(true);
        this.mDimState = ViewDimmer.DimState.INACTIVE;
        this.mCardElevationSupported = LauncherConfiguration.getInstance().isCardElevationEnabled();
    }

    public boolean hasOverlappingRendering() {
        return hasFocus() && super.hasOverlappingRendering();
    }

    public void setAdapter(Adapter adapter) {
        if (getAdapter() != null) {
            getAdapter().unregisterAdapterDataObserver(this.mChangeObserver);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(this.mChangeObserver);
        }
    }

    public void onChildAttachedToWindow(View child) {
        super.onChildAttachedToWindow(child);
        child.setActivated(ViewDimmer.dimStateToActivated(this.mDimState));
        if (child instanceof DimmableItem) {
            ((DimmableItem) child).setDimState(this.mDimState, false);
        }
        if (this.mCardElevationSupported) {
            child.setZ((float) getResources().getDimensionPixelOffset(R.dimen.unselected_item_z));
        }
    }

    public int getNumRows() {
        return this.mNumRows;
    }

    public void setActivated(boolean activated) {
        if (ViewDimmer.dimStateToActivated(this.mDimState) != activated) {
            this.mDimState = ViewDimmer.activatedToDimState(activated);
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                View view = getChildAt(i);
                if (view instanceof DimmableItem) {
                    ((DimmableItem) view).setDimState(this.mDimState, false);
                }
            }
        }
        super.setActivated(activated);
    }

    public boolean isRowActive() {
        return ViewDimmer.dimStateToActivated(this.mDimState);
    }

    public void setIsNumRowsAdjustable(boolean isAdjustable) {
        this.mIsAdjustable = isAdjustable;
        setOnHierarchyChangeListener(isAdjustable ? this : null);
    }

    public void adjustNumRows(int numRows, int cardSpacing, int rowHeight) {
        // Log.w("+++ adjustNumRows INIT", "" + numRows + " " + cardSpacing + " " + rowHeight);
        if (this.mIsAdjustable && this.mNumRows != numRows) {
            this.mNumRows = numRows;
            this.mCardSpacing = cardSpacing;
            this.mRowHeight = rowHeight;
            post(new Runnable() {
                public void run() {
                    ActiveItemsRowView.this.getLayoutParams().height = ((ActiveItemsRowView.this.mNumRows * ActiveItemsRowView.this.mRowHeight) + ((ActiveItemsRowView.this.mNumRows - 1) * ActiveItemsRowView.this.mCardSpacing)) + (ActiveItemsRowView.this.getPaddingTop() + ActiveItemsRowView.this.getPaddingBottom());
                    // Log.w("+++ adjustNumRows height", "" + ActiveItemsRowView.this.getLayoutParams().height);
                    ActiveItemsRowView.this.setNumRows(ActiveItemsRowView.this.mNumRows);
                    // Log.w("+++ adjustNumRows setNumRows", "" + ActiveItemsRowView.this.mNumRows);
                    ActiveItemsRowView.this.setRowHeight(ActiveItemsRowView.this.mRowHeight);
                    // Log.w("+++ adjustNumRows setRowHeight", "" + ActiveItemsRowView.this.mRowHeight);
                }
            });
        }
    }

    private void adjustNumRows() {
        int integer;
        if (this.mNumRows > 0)
            integer = this.mNumRows;
        else {
            Resources res = getResources();
            Context ctx = getContext();
            if (getAdapter().getItemCount() > RowPreferences.getAppsMax(ctx)) {
                integer = res.getInteger(R.integer.max_num_banner_rows);
            } else {
                integer = res.getInteger(R.integer.min_num_banner_rows);
            }
        }
        adjustNumRows(integer, this.mCardSpacing, this.mRowHeight);
    }

    public void childHasTransientStateChanged(View child, boolean hasTransientState) {
    }

    public void onChildViewAdded(View parent, View child) {
        adjustNumRows();
    }

    public void onChildViewRemoved(View parent, View child) {
        adjustNumRows();
    }

    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        enableAnimations(this);
    }

    private void enableAnimations(View view) {
        if (view instanceof ParticipatesInScrollAnimation) {
            ((ParticipatesInScrollAnimation) view).setAnimationsEnabled(true);
        }
        if (view instanceof ViewGroup) {
            int count = ((ViewGroup) view).getChildCount();
            for (int i = 0; i < count; i++) {
                enableAnimations(((ViewGroup) view).getChildAt(i));
            }
        }
    }

    public View focusSearch(View focused, int direction) {
        if (direction == 17 || direction == 66) {
            enableAnimations(this);
        }
        return super.focusSearch(focused, direction);
    }

}
