package com.amazon.tv.leanbacklauncher;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.widget.OnChildViewHolderSelectedListener;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalFocusChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.amazon.tv.leanbacklauncher.apps.AppsAdapter;
import com.amazon.tv.leanbacklauncher.apps.BannerSelectedChangedListener;
import com.amazon.tv.leanbacklauncher.apps.BannerView;
import com.amazon.tv.leanbacklauncher.apps.LaunchPoint;
import com.amazon.tv.leanbacklauncher.apps.OnEditModeChangedListener;
import com.amazon.tv.leanbacklauncher.util.Util;
import com.amazon.tv.leanbacklauncher.widget.EditModeView;
import com.amazon.tv.leanbacklauncher.widget.EditModeViewActionListener;
import com.amazon.tv.leanbacklauncher.animation.ViewDimmer;

import java.util.ArrayList;
import java.util.Iterator;

public class EditableAppsRowView extends ActiveItemsRowView implements OnGlobalFocusChangeListener, BannerSelectedChangedListener, OnEditModeChangedListener, EditModeViewActionListener {
    AdapterDataObserver mChangeObserver;
    private int mCurFocused;
    private final ArrayList<OnEditModeChangedListener> mEditListeners;
    private boolean mEditMode;
    private boolean mEditModePending;
    private EditModeView mEditModeView;
    private int mLastFocused;
    private boolean mSwapping;

    public EditableAppsRowView(Context context) {
        this(context, null);
    }

    public EditableAppsRowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditableAppsRowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mLastFocused = -1;
        this.mEditListeners = new ArrayList();
        this.mChangeObserver = new AdapterDataObserver() {
            public void onItemRangeInserted(int positionStart, int itemCount) {
                if (EditableAppsRowView.this.mCurFocused >= positionStart) {
                    EditableAppsRowView.this.mCurFocused = EditableAppsRowView.this.mCurFocused + itemCount;
                }
                if (EditableAppsRowView.this.mLastFocused >= positionStart) {
                    EditableAppsRowView.this.mLastFocused = EditableAppsRowView.this.mLastFocused + itemCount;
                }
            }

            public void onItemRangeRemoved(int positionStart, int itemCount) {
                if (EditableAppsRowView.this.mCurFocused >= positionStart + itemCount) {
                    EditableAppsRowView.this.mCurFocused = EditableAppsRowView.this.mCurFocused - itemCount;
                } else if (EditableAppsRowView.this.mCurFocused >= positionStart) {
                    EditableAppsRowView.this.focusOnNewPosition();
                }
                if (EditableAppsRowView.this.mLastFocused >= positionStart + itemCount) {
                    EditableAppsRowView.this.mLastFocused = EditableAppsRowView.this.mLastFocused - itemCount;
                }
            }
        };
        setOnChildViewHolderSelectedListener(new OnChildViewHolderSelectedListener() {
            public void onChildViewHolderSelected(RecyclerView parent, ViewHolder holder, int position, int subposition) {
                EditableAppsRowView.this.onChildViewHolderSelected(parent, holder, position, subposition);
            }
        });
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
        if (child instanceof BannerView) {
            addEditModeListener((BannerView) child);
            ((BannerView) child).addSelectedListener(this.mEditModeView);
            ((BannerView) child).addSelectedListener(this);
            ((BannerView) child).setOnEditModeChangedListener(this);
        }
    }

    public void onChildDetachedFromWindow(View child) {
        super.onChildDetachedFromWindow(child);
        if (child instanceof BannerView) {
            removeEditModeListener((BannerView) child);
            ((BannerView) child).removeSelectedListener(this.mEditModeView);
            ((BannerView) child).removeSelectedListener(this);
            ((BannerView) child).setOnEditModeChangedListener(null);
        }
    }

    public void setEditMode(boolean editMode) {
        this.mEditModePending = false;
        if (Util.isInTouchExploration(getContext()) && editMode) {
            ((Activity) getContext()).setTitle(R.string.title_app_edit_mode);
        }
        if (this.mEditMode != editMode) {
            int dimensionPixelSize;
            this.mEditMode = editMode;
            if (editMode) {
                dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.banner_width);
            } else {
                dimensionPixelSize = 0;
            }
            setExtraLayoutSpace(dimensionPixelSize);
            if (!editMode) {
                getViewTreeObserver().removeOnGlobalFocusChangeListener(this);
                AppsAdapter adapter = (AppsAdapter) getAdapter();
                if (adapter != null) {
                    adapter.saveAppOrderSnapshot();
                }
            } else if (isAccessibilityEnabled()) {
                getViewTreeObserver().addOnGlobalFocusChangeListener(this);
            }
            if (!this.mEditListeners.isEmpty()) {
                Iterator it = this.mEditListeners.iterator();
                while (it.hasNext()) {
                    ((OnEditModeChangedListener) it.next()).onEditModeChanged(editMode);
                }
            }
            if (Log.isLoggable("EditableAppsRowView", 2)) {
                Log.d("EditableAppsRowView", "Edit Mode is now " + this.mEditMode + ".");
            }
        }
    }

    public void addEditModeListener(OnEditModeChangedListener listener) {
        this.mEditListeners.add(listener);
    }

    public void removeEditModeListener(OnEditModeChangedListener listener) {
        this.mEditListeners.remove(listener);
    }

    public void setEditModeView(EditModeView editModeView) {
        if (this.mEditModeView != null) {
            this.mEditModeView.removeActionListener(this);
        }
        this.mEditModeView = editModeView;
        this.mEditModeView.addActionListener(this);
    }

    public boolean getEditMode() {
        return this.mEditMode;
    }

    public boolean isRowEditable() {
        return true;
    }

    public void onSelectedChanged(BannerView v, boolean selected) {
        if (isUninstallDisallowed(v) && selected) {
            this.mEditModeView.clearUninstallAndFinishLayers();
        } else {
            this.mEditModeView.onSelectedChanged(v, selected);
        }
        int childCount = getChildCount();
        refreshSelectedView();
        this.mDimState = selected ? ViewDimmer.DimState.EDIT_MODE : ViewDimmer.DimState.ACTIVE;
        ViewHolder curViewHolder = getCurViewHolderInt();
        View itemView = curViewHolder != null ? curViewHolder.itemView : null;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child != itemView && (child instanceof BannerView)) {
                ((DimmableItem) child).setDimState(this.mDimState, false);
            }
        }
    }

    private boolean isUninstallDisallowed(BannerView v) {
        if (v == null) {
            return true;
        }
        LaunchPoint lp = getViewLaunchPoint(v);
        if (lp == null || Util.isSystemApp(getContext(), getViewPackageName(v)) || !Util.isUninstallAllowed(getContext()) || lp.isInstalling()) {
            return true;
        }
        return false;
    }

    public void onEditModeExitTriggered() {
        ViewHolder curViewHolder = getCurViewHolderInt();
        if (isRowActive() && curViewHolder != null) {
            curViewHolder.itemView.requestFocus();
            setBannerDrawableUninstallState(false);
        }
        if (Util.isInTouchExploration(getContext())) {
            ((Activity) getContext()).setTitle(R.string.app_label);
        }
        setEditMode(false);
    }

    public void onFocusLeavingEditModeLayer(int from) {
        if (isRowActive()) {
            View lastFocusedView;
            ViewHolder lastFocusedViewHolder = getLastFocusedViewHolderInt();
            if (lastFocusedViewHolder != null) {
                lastFocusedView = lastFocusedViewHolder.itemView;
            } else {
                lastFocusedView = null;
            }
            ViewHolder curFocusedViewHolder = getCurViewHolderInt();
            View curFocusedView;
            if (curFocusedViewHolder != null) {
                curFocusedView = curFocusedViewHolder.itemView;
            } else {
                curFocusedView = null;
            }
            if ((lastFocusedView instanceof BannerView) && (curFocusedView instanceof BannerView)) {
                lastFocusedView.requestFocus();
                if (this.mLastFocused != this.mCurFocused) {
                    focusOnNewPosition();
                    this.mLastFocused = this.mCurFocused;
                }
                if (from == 1) {
                    lastFocusedView.setSelected(true);
                    this.mEditModeView.setBannerUninstallModeWithAnimation(false, (BannerView) lastFocusedView, this);
                }
            } else if (getChildCount() > 0 && this.mEditMode) {
                focusOnNewPosition();
            }
        }
    }

    public String onPrepForUninstall() {
        String packageName = "";
        ViewHolder lastFocusedViewHolder = getLastFocusedViewHolderInt();
        if (lastFocusedViewHolder == null || !(getAdapter() instanceof AppsAdapter)) {
            return packageName;
        }
        return getViewPackageName(lastFocusedViewHolder.itemView);
    }

    private LaunchPoint getViewLaunchPoint(View view) {
        if (getAdapter() instanceof AppsAdapter) {
            int position = getChildAdapterPosition(view);
            AppsAdapter adapter = (AppsAdapter) getAdapter();
            if (!(adapter == null || position == -1)) {
                return adapter.getLaunchPointForPosition(position);
            }
        }
        return null;
    }

    private String getViewPackageName(View view) {
        LaunchPoint lp = getViewLaunchPoint(view);
        return lp != null ? lp.getPackageName() : null;
    }

    public void onUninstallComplete() {
        ViewHolder lastFocusedViewHolder = getLastFocusedViewHolderInt();
        if (lastFocusedViewHolder != null) {
            lastFocusedViewHolder.itemView.setSelected(false);
            if (lastFocusedViewHolder.itemView instanceof BannerView) {
                ((BannerView) lastFocusedViewHolder.itemView).notifyEditModeManager(false);
            }
            setChildrenLastFocusedBanner(null);
            setBannerDrawableUninstallState(false);
            focusOnNewPosition();
        }
    }

    public void onUninstallFailure() {
        ViewHolder lastFocusedViewHolder = getLastFocusedViewHolderInt();
        if (lastFocusedViewHolder != null) {
            lastFocusedViewHolder.itemView.requestFocus();
            setBannerDrawableUninstallState(false);
        }
    }

    public void setUninstallState() {
        refreshSelectedView();
        ViewHolder curFocusedViewHolder = getCurViewHolderInt();
        View curView = curFocusedViewHolder != null ? curFocusedViewHolder.itemView : null;
        if ((curView instanceof BannerView) && !isUninstallDisallowed((BannerView) curView)) {
            this.mLastFocused = this.mCurFocused;
            this.mEditModeView.requestUninstallIconFocus((BannerView) curView, this);
            this.mEditModeView.setBannerDrawable(getLastFocusedBannerDrawable());
        }
    }

    public void onEditModeChanged(boolean editMode) {
        setEditMode(editMode);
    }

    public void swapAppOrder(BannerView movingBanner, BannerView otherBanner) {
        ((AppsAdapter) getAdapter()).moveLaunchPoint(getChildAdapterPosition(movingBanner), getChildAdapterPosition(otherBanner), true);
    }

    public void onGlobalFocusChanged(View oldFocus, View newFocus) {
        if (isRowActive() && oldFocus != null && newFocus != null) {
            if (!(oldFocus instanceof BannerView) || !(newFocus instanceof BannerView)) {
                ViewHolder curFocusedViewHolder = getCurViewHolderInt();
                ViewHolder lastFocusedViewHolder = getLastFocusedViewHolderInt();
                if (newFocus.equals(this.mEditModeView.getUninstallIcon())) {
                    setUninstallState();
                    setChildrenLastFocusedBanner(lastFocusedViewHolder != null ? (BannerView) lastFocusedViewHolder.itemView : null);
                } else if (oldFocus.equals(this.mEditModeView.getUninstallIcon())) {
                    BannerView bannerView;
                    EditModeView editModeView = this.mEditModeView;
                    if (curFocusedViewHolder != null) {
                        bannerView = (BannerView) curFocusedViewHolder.itemView;
                    } else {
                        bannerView = null;
                    }
                    editModeView.setBannerUninstallModeWithAnimation(false, bannerView, this);
                    setChildrenLastFocusedBanner(null);
                } else if (newFocus.equals(this.mEditModeView.getFinishButton())) {
                    setChildrenLastFocusedBanner(curFocusedViewHolder != null ? (BannerView) curFocusedViewHolder.itemView : null);
                } else if (oldFocus.equals(this.mEditModeView.getFinishButton())) {
                    setChildrenLastFocusedBanner(null);
                }
            } else if (oldFocus.isSelected() && !this.mSwapping) {
                try {
                    this.mSwapping = true;
                    swapAppOrder((BannerView) oldFocus, (BannerView) newFocus);
                    oldFocus.requestFocus();
                } finally {
                    this.mSwapping = false;
                }
            }
        }
    }

    public View focusSearch(View focused, int direction) {
        if (this.mEditMode) {
            int position = getChildAdapterPosition(focused);
            int numRows = getNumRows();
            if (focused.isSelected()) {
                if (getItemAnimator().isRunning()) {
                    return focused;
                }
                if (getLayoutDirection() == 1 && (direction == 17 || direction == 66)) {
                    direction = direction == 17 ? 66 : 17;
                }
                if (direction == 130) {
                    if (position % numRows >= numRows - 1 || position >= getAdapter().getItemCount() - 1) {
                        setUninstallState();
                        return this.mEditModeView.getUninstallIcon();
                    }
                    moveLaunchPoint(position, position + 1);
                    return focused;
                } else if (direction == 33) {
                    if (position % numRows <= 0) {
                        return focused;
                    }
                    moveLaunchPoint(position, position - 1);
                    return focused;
                } else if (direction == 66) {
                    moveLaunchPoint(position, position + numRows);
                    return focused;
                } else if (direction == 17) {
                    moveLaunchPoint(position, position - numRows);
                    return focused;
                }
            } else if (direction == 130 && position % numRows == numRows - 1) {
                setLastFocused();
                return this.mEditModeView.getFinishButton();
            } else if (direction == 130 && position == getAdapter().getItemCount() - 1) {
                return focused;
            } else {
                if (direction == 33 && position % numRows == 0) {
                    return focused;
                }
            }
        }
        return super.focusSearch(focused, direction);
    }

    public void setEditModePending(boolean isPending) {
        this.mEditModePending = isPending;
    }

    public boolean isEditModePending() {
        return this.mEditModePending;
    }

    private boolean isAccessibilityEnabled() {
        return ((AccessibilityManager) getContext().getSystemService("accessibility")).isEnabled();
    }

    private boolean moveLaunchPoint(int fromPosition, int toPosition) {
        return ((AppsAdapter) getAdapter()).moveLaunchPoint(fromPosition, toPosition, true);
    }

    private void focusOnNewPosition() {
        ViewHolder curFocusedViewHolder = getCurViewHolderInt();
        if (curFocusedViewHolder == null) {
            this.mCurFocused = -1;
            return;
        }
        int newFocusPosition = curFocusedViewHolder.getLayoutPosition() + getNumRows();
        if (isAccessibilityEnabled()) {
            newFocusPosition = 0;
        }
        int numApps = getChildCount();
        if (newFocusPosition >= numApps) {
            newFocusPosition = numApps - 1;
        }
        if (newFocusPosition == curFocusedViewHolder.getLayoutPosition()) {
            newFocusPosition--;
        }
        if (newFocusPosition < 0 || !(curFocusedViewHolder.itemView instanceof BannerView)) {
            onEditModeExitTriggered();
            return;
        }
        onSelectedChanged((BannerView) curFocusedViewHolder.itemView, false);
        View newFocusView = getChildAt(newFocusPosition);
        newFocusView.requestFocus();
        newFocusView.setSelected(false);
    }

    private Drawable getLastFocusedBannerDrawable() {
        Adapter adapter = getAdapter();
        ViewHolder lastFocusedViewHolder = getLastFocusedViewHolderInt();
        if (lastFocusedViewHolder == null || !(adapter instanceof AppsAdapter)) {
            return null;
        }
        AppsAdapter appsAdapter = (AppsAdapter) adapter;
        int position = lastFocusedViewHolder.getAdapterPosition();
        if (position != -1) {
            Drawable d = appsAdapter.getDrawableFromLaunchPoint(position);
            if (d == null) {
                lastFocusedViewHolder.itemView.setDrawingCacheEnabled(true);
                Bitmap tmpBitmap = Bitmap.createBitmap(lastFocusedViewHolder.itemView.getDrawingCache());
                lastFocusedViewHolder.itemView.setDrawingCacheEnabled(false);
                d = new BitmapDrawable(getResources(), tmpBitmap);
            }
            return d;
        }
        return null;
    }

    public void setBannerDrawableUninstallState(boolean uninstalling) {
        int i = 8;
        ViewHolder lastFocusedViewHolder = getLastFocusedViewHolderInt();
        if (lastFocusedViewHolder != null && (lastFocusedViewHolder.itemView instanceof BannerView)) {
            View itemView = lastFocusedViewHolder.itemView;
            View bannerView = itemView.findViewById(R.id.app_banner);
            if (bannerView != null) {
                Drawable drawable;
                if (uninstalling) {
                    drawable = getResources().getDrawable(R.drawable.dashed_holder, null);
                } else {
                    drawable = getResources().getDrawable(R.drawable.banner_background, null);
                }
                bannerView.setBackground(drawable);
                if (bannerView instanceof LinearLayout) {
                    int i2;
                    View icon = itemView.findViewById(R.id.banner_icon);
                    if (uninstalling) {
                        i2 = 8;
                    } else {
                        i2 = 0;
                    }
                    icon.setVisibility(i2);
                    View text = itemView.findViewById(R.id.banner_label);
                    if (!uninstalling) {
                        i = 0;
                    }
                    text.setVisibility(i);
                }
                if (bannerView instanceof ImageView) {
                    ImageView imageView = (ImageView) bannerView;
                    if (uninstalling) {
                        drawable = null;
                    } else {
                        drawable = getLastFocusedBannerDrawable();
                    }
                    imageView.setImageDrawable(drawable);
                }
                if (!uninstalling) {
                    this.mLastFocused = -1;
                }
            }
        }
    }

    private void setChildrenLastFocusedBanner(BannerView view) {
        int children = getChildCount();
        for (int i = 0; i < children; i++) {
            if (getChildAt(i) instanceof BannerView) {
                ((BannerView) getChildAt(i)).setLastFocusedBanner(view);
            }
        }
    }

    public void setLastFocused() {
        this.mLastFocused = this.mCurFocused;
    }

    public void onChildViewHolderSelected(RecyclerView parent, ViewHolder holder, int position, int subposition) {
        if (position != this.mCurFocused) {
            if (this.mEditMode) {
                this.mLastFocused = this.mCurFocused;
            }
            this.mCurFocused = position;
        }
        postInvalidateDelayed(50);
    }

    public void refreshSelectedView() {
        this.mCurFocused = getSelectedPosition();
    }

    public ViewHolder getCurViewHolder() {
        if (this.mCurFocused == -1) {
            refreshSelectedView();
        }
        return getCurViewHolderInt();
    }

    private ViewHolder getCurViewHolderInt() {
        if (this.mCurFocused != -1) {
            return findViewHolderForLayoutPosition(this.mCurFocused);
        }
        return null;
    }

    private ViewHolder getLastFocusedViewHolderInt() {
        if (this.mLastFocused != -1) {
            return findViewHolderForLayoutPosition(this.mLastFocused);
        }
        return null;
    }
}
