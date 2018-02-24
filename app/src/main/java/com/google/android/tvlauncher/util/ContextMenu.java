package com.google.android.tvlauncher.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.AccessibilityDelegate;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewOutlineProvider;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.CollectionInfo;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.tvlauncher.R;

import java.util.ArrayList;
import java.util.List;

public class ContextMenu {
    private static final int BOTTOM_ALIGN = 1;
    private static final int SCROLL = 2;
    private static final int TOP_ALIGN = 0;
    private Activity mActivity;
    private View mAnchor;
    private int mAnchorRealHeight;
    private int mAnchorRealWidth;
    private int mAnchorX;
    private int mAnchorY;
    private List<ContextMenuItem> mContextMenuItems;
    private CutoutOverlayLayout mCutoutOverlay;
    private int mDeltaX;
    private int mDeltaY;
    private final int mDimBackgroundColor;
    private final int mDisabledColor;
    private final int mEnabledColor;
    private final int mFocusedColor;
    private int mGravity;
    private int mHorizontalPosition;
    private boolean mIsShowing;
    private FrameLayout mMenuContainer;
    private int mMenuHeight;
    private final int mMenuHeightPerRow;
    private LinearLayout mMenuLinearLayout;
    private final int mMenuVerticalMargin;
    private int mMenuWidth;
    private OnDismissListener mOnDismissListener;
    private OnItemClickListener mOnItemClickListener;
    private final float mOverlayAlpha;
    private ObjectAnimator mOverlayAnimator;
    private final int mOverlayDismissAnimationDuration;
    private final int mOverlayShowAnimationDuration;
    private final int mOverscanHorizontal;
    private final int mOverscanVertical;
    private PopupWindow mPopupWindow;
    private ViewGroup mRootParentWindow;
    private ImageView mTriangle;
    private final int mTriangleEdgeOffset;
    private final int mTriangleHeight;
    private final int mTriangleVerticalMenuMargin;
    private final int mTriangleWidth;
    private final int mUnfocusedColor;
    @VerticalPosition
    private int mVerticalPosition;
    private List<ContextMenuItem> mVisibleItems;
    private List<View> mVisibleMenuItemViews;

    private class CutoutOverlayLayout extends FrameLayout {
        private Paint mPaint = new Paint();
        private int mRadiusX;
        private int mRadiusY;
        private RectF mRect;

        public CutoutOverlayLayout(@NonNull Context context, int radiusX, int radiusY) {
            super(context);
            this.mRadiusX = radiusX;
            this.mRadiusY = radiusY;
            setWillNotDraw(false);
            setLayerType(2, null);
            this.mPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
        }

        public void setAnchorRect(RectF rect) {
            this.mRect = rect;
        }

        protected void onDraw(Canvas canvas) {
            canvas.drawColor(ContextMenu.this.mDimBackgroundColor);
            canvas.drawRoundRect(this.mRect, (float) this.mRadiusX, (float) this.mRadiusY, this.mPaint);
        }
    }

    public interface OnDismissListener {
        void onDismiss();
    }

    public interface OnItemClickListener {
        void onItemClick(ContextMenuItem contextMenuItem);
    }

    public @interface VerticalPosition {
    }

    public boolean isShowing() {
        return this.mIsShowing;
    }

    public ContextMenu(Activity activity, View anchor, int cornerRadius) {
        this(activity, anchor, cornerRadius, anchor.getScaleX(), anchor.getScaleY());
    }

    public ContextMenu(Activity activity, View anchor, int cornerRadius, float scaleX, float scaleY) {
        this.mContextMenuItems = new ArrayList();
        this.mVisibleMenuItemViews = new ArrayList();
        this.mIsShowing = false;
        this.mAnchor = anchor;
        this.mActivity = activity;
        this.mMenuContainer = new FrameLayout(this.mActivity);
        this.mMenuContainer.setContentDescription(this.mActivity.getString(R.string.context_menu_description));
        this.mPopupWindow = new PopupWindow(this.mMenuContainer, -2, -2);
        this.mPopupWindow.setFocusable(true);
        this.mPopupWindow.setOnDismissListener(new android.widget.PopupWindow.OnDismissListener() {
            public void onDismiss() {
                ContextMenu.this.clearDimBackground();
                if (ContextMenu.this.mOnDismissListener != null) {
                    ContextMenu.this.mOnDismissListener.onDismiss();
                }
                ContextMenu.this.mIsShowing = false;
            }
        });
        int[] mAnchorCoordinates = new int[2];
        this.mAnchor.getLocationInWindow(mAnchorCoordinates);
        this.mAnchorX = mAnchorCoordinates[0];
        this.mAnchorY = mAnchorCoordinates[1];
        this.mAnchorRealWidth = (int) (((float) this.mAnchor.getWidth()) * scaleX);
        this.mAnchorRealHeight = (int) (((float) this.mAnchor.getHeight()) * scaleY);
        int anchorCornerRadiusX = (int) (((float) cornerRadius) * scaleX);
        int anchorCornerRadiusY = (int) (((float) cornerRadius) * scaleY);
        this.mMenuVerticalMargin = getDimenInPixels(R.dimen.context_menu_vertical_margin);
        this.mTriangleVerticalMenuMargin = getDimenInPixels(R.dimen.context_menu_triangle_vertical_margin);
        this.mTriangleEdgeOffset = getDimenInPixels(R.dimen.context_menu_triangle_edge_offset);
        this.mTriangleHeight = getDimenInPixels(R.dimen.context_menu_triangle_height);
        this.mTriangleWidth = getDimenInPixels(R.dimen.context_menu_triangle_width);
        this.mFocusedColor = ContextCompat.getColor(this.mActivity, R.color.context_menu_background_focused_color);
        this.mUnfocusedColor = ContextCompat.getColor(this.mActivity, R.color.context_menu_background_unfocused_color);
        this.mEnabledColor = ContextCompat.getColor(this.mActivity, R.color.context_menu_icon_enabled_color);
        this.mDisabledColor = ContextCompat.getColor(this.mActivity, R.color.context_menu_icon_disabled_color);
        this.mMenuHeightPerRow = getDimenInPixels(R.dimen.context_menu_height_per_row);
        this.mOverscanHorizontal = getDimenInPixels(R.dimen.overscan_horizontal);
        this.mOverscanVertical = getDimenInPixels(R.dimen.overscan_vertical);
        this.mOverlayAlpha = getFloat(R.fraction.context_menu_overlay_alpha);
        this.mOverlayShowAnimationDuration = this.mActivity.getResources().getInteger(R.integer.context_menu_overlay_show_animation_duration_ms);
        this.mOverlayDismissAnimationDuration = this.mActivity.getResources().getInteger(R.integer.context_menu_overlay_dismiss_animation_duration_ms);
        this.mDimBackgroundColor = ContextCompat.getColor(this.mActivity, R.color.context_menu_overlay_background_color);
        RectF anchorRect = new RectF((float) this.mAnchorX, (float) this.mAnchorY, (float) (this.mAnchorX + this.mAnchorRealWidth), (float) (this.mAnchorY + this.mAnchorRealHeight));
        this.mCutoutOverlay = new CutoutOverlayLayout(this.mActivity, anchorCornerRadiusX, anchorCornerRadiusY);
        this.mCutoutOverlay.setAnchorRect(anchorRect);
        this.mTriangle = new ImageView(this.mActivity);
    }

    public ContextMenuItem findItem(int menuId) {
        for (ContextMenuItem item : this.mContextMenuItems) {
            if (item.getId() == menuId) {
                return item;
            }
        }
        return null;
    }

    private boolean testBit(int bitSet, int mask) {
        if (mask == 0) {
            if (bitSet == 0) {
                return true;
            }
            return false;
        } else if ((bitSet & mask) != mask) {
            return false;
        } else {
            return true;
        }
    }

    private void dimBackground() {
        this.mRootParentWindow.addView(this.mCutoutOverlay, this.mRootParentWindow.getWidth(), this.mRootParentWindow.getHeight());
        this.mCutoutOverlay.setAlpha(0.0f);
        animateBackgroundOverlayAlpha(this.mOverlayAlpha, this.mOverlayShowAnimationDuration);
    }

    private void animateBackgroundOverlayAlpha(float destinationAlpha, int duration) {
        if (this.mOverlayAnimator != null) {
            this.mOverlayAnimator.cancel();
        }
        this.mOverlayAnimator = ObjectAnimator.ofFloat(this.mCutoutOverlay, View.ALPHA, new float[]{destinationAlpha});
        this.mOverlayAnimator.setDuration((long) duration);
        this.mOverlayAnimator.start();
    }

    private void clearDimBackground() {
        animateBackgroundOverlayAlpha(0.0f, this.mOverlayDismissAnimationDuration);
        this.mOverlayAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                ContextMenu.this.mRootParentWindow.removeView(ContextMenu.this.mCutoutOverlay);
            }
        });
    }

    public void forceDismiss() {
        this.mPopupWindow.dismiss();
    }

    private int getDimenInPixels(int resourceId) {
        return this.mAnchor.getResources().getDimensionPixelSize(resourceId);
    }

    private List<ContextMenuItem> getVisibleItems() {
        List<ContextMenuItem> list = new ArrayList();
        for (ContextMenuItem item : this.mContextMenuItems) {
            if (item.isVisible()) {
                list.add(item);
            }
        }
        return list;
    }

    private void determineGravity() {
        this.mGravity = 0;
        this.mHorizontalPosition = 17;
        if (this.mAnchor.getLayoutDirection() == 1) {
            if ((this.mAnchorX + this.mAnchorRealWidth) - this.mMenuWidth >= this.mOverscanHorizontal) {
                this.mGravity |= 5;
            } else {
                this.mGravity |= 3;
            }
        } else if (this.mAnchorX + this.mMenuWidth <= this.mRootParentWindow.getWidth() - this.mOverscanHorizontal) {
            this.mGravity |= 3;
        } else {
            this.mGravity |= 5;
        }
        if ((this.mAnchorY + this.mAnchorRealHeight) + this.mMenuHeight <= this.mRootParentWindow.getHeight() - this.mOverscanVertical) {
            this.mGravity |= 80;
        } else if (this.mAnchorY - this.mMenuHeight >= this.mOverscanVertical) {
            this.mGravity |= 48;
        } else {
            this.mMenuHeight -= this.mMenuVerticalMargin;
            if (this.mAnchor.getLayoutDirection() == 0) {
                if (((this.mAnchorX + this.mAnchorRealWidth) + this.mMenuWidth) + this.mTriangleHeight <= this.mRootParentWindow.getWidth() - this.mOverscanHorizontal) {
                    this.mHorizontalPosition = 5;
                } else {
                    this.mHorizontalPosition = 3;
                }
            } else if ((this.mAnchorX - this.mMenuWidth) - this.mTriangleHeight >= this.mOverscanHorizontal) {
                this.mHorizontalPosition = 3;
            } else {
                this.mHorizontalPosition = 5;
            }
            if (this.mAnchorY + this.mMenuHeight <= this.mRootParentWindow.getHeight() - this.mOverscanVertical) {
                this.mVerticalPosition = 0;
            } else if ((this.mAnchorY + this.mAnchorRealHeight) - this.mMenuHeight >= this.mOverscanVertical) {
                this.mVerticalPosition = 1;
            } else {
                this.mVerticalPosition = 2;
            }
        }
    }

    private void calculateMenuSize() {
        this.mMenuLinearLayout.measure(0, 0);
        this.mMenuWidth = this.mMenuLinearLayout.getMeasuredWidth();
        this.mMenuHeight = this.mMenuLinearLayout.getMeasuredHeight() + this.mMenuVerticalMargin;
    }

    public void addItem(ContextMenuItem item) {
        this.mContextMenuItems.add(item);
    }

    private int getRelativeGravity(int absoluteGravity, int layoutDirection) {
        if ((testBit(absoluteGravity, 5) && layoutDirection == 0) || (testBit(absoluteGravity, 3) && layoutDirection == 1)) {
            return GravityCompat.END;
        }
        if ((testBit(absoluteGravity, 3) && layoutDirection == 0) || (testBit(absoluteGravity, 5) && layoutDirection == 1)) {
            return GravityCompat.START;
        }
        return 0;
    }

    private void adjustTrianglePosition() {
        float f = 270.0f;
        float f2 = 90.0f;
        LayoutParams triangleLayoutParams = (LayoutParams) this.mTriangle.getLayoutParams();
        triangleLayoutParams.gravity = 0;
        if (this.mHorizontalPosition == 17) {
            if (getRelativeGravity(this.mGravity, this.mAnchor.getLayoutDirection()) == GravityCompat.END) {
                triangleLayoutParams.gravity |= GravityCompat.END;
                triangleLayoutParams.setMarginEnd(this.mTriangleEdgeOffset);
            } else {
                triangleLayoutParams.gravity |= GravityCompat.START;
                triangleLayoutParams.setMarginStart(this.mTriangleEdgeOffset);
            }
            if (testBit(this.mGravity, 48)) {
                triangleLayoutParams.gravity |= 80;
                triangleLayoutParams.bottomMargin = this.mTriangleVerticalMenuMargin;
                this.mTriangle.setScaleY(-1.0f);
                return;
            }
            triangleLayoutParams.gravity |= 48;
            triangleLayoutParams.topMargin = this.mTriangleVerticalMenuMargin;
            return;
        }
        triangleLayoutParams.gravity |= GravityCompat.START;
        ImageView imageView;
        if (getRelativeGravity(this.mHorizontalPosition, this.mAnchor.getLayoutDirection()) == GravityCompat.START) {
            triangleLayoutParams.setMarginStart(this.mMenuWidth - 2);
            imageView = this.mTriangle;
            if (this.mAnchor.getLayoutDirection() != 1) {
                f = 90.0f;
            }
            imageView.setRotation(f);
        } else {
            triangleLayoutParams.setMarginStart(0);
            imageView = this.mTriangle;
            if (this.mAnchor.getLayoutDirection() != 1) {
                f2 = 270.0f;
            }
            imageView.setRotation(f2);
        }
        if (this.mVerticalPosition == 0) {
            triangleLayoutParams.gravity |= 48;
            triangleLayoutParams.topMargin = (this.mAnchorRealHeight - this.mTriangleWidth) / 2;
        } else if (this.mVerticalPosition == 1) {
            triangleLayoutParams.gravity |= 80;
            triangleLayoutParams.bottomMargin = (this.mAnchorRealHeight - this.mTriangleWidth) / 2;
        } else {
            int menuLocationY = this.mRootParentWindow.getHeight() - this.mMenuHeight;
            triangleLayoutParams.gravity |= 48;
            triangleLayoutParams.topMargin = (this.mAnchorY - menuLocationY) + ((this.mAnchorRealHeight - this.mTriangleWidth) / 2);
        }
        this.mMenuWidth += this.mTriangleHeight;
    }

    private void adjustLayoutMenu() {
        MarginLayoutParams menuLayoutParams = (MarginLayoutParams) this.mMenuLinearLayout.getLayoutParams();
        if (this.mHorizontalPosition != 17) {
            menuLayoutParams.topMargin = 0;
            menuLayoutParams.bottomMargin = 0;
            if (this.mHorizontalPosition == 3) {
                menuLayoutParams.rightMargin = this.mTriangleHeight;
            } else {
                menuLayoutParams.leftMargin = this.mTriangleHeight;
            }
        } else if (testBit(this.mGravity, 48)) {
            menuLayoutParams.bottomMargin = this.mMenuVerticalMargin;
            menuLayoutParams.topMargin = 0;
        } else {
            menuLayoutParams.bottomMargin = 0;
            menuLayoutParams.topMargin = this.mMenuVerticalMargin;
        }
        if ((this.mHorizontalPosition == 17 && testBit(this.mGravity, 48)) || (this.mHorizontalPosition != 17 && this.mVerticalPosition == 1)) {
            this.mMenuLinearLayout.removeAllViews();
            for (int i = this.mVisibleItems.size() - 1; i >= 0; i--) {
                this.mMenuLinearLayout.addView((View) this.mVisibleMenuItemViews.get(i));
            }
        }
    }

    private void adjustMenuShowUpPosition() {
        this.mDeltaX = 0;
        this.mDeltaY = 0;
        if (this.mHorizontalPosition == 17) {
            int deltaX = this.mAnchorRealWidth - this.mAnchor.getWidth();
            int deltaY = this.mAnchorRealHeight - this.mAnchor.getHeight();
            if (!testBit(this.mGravity, 80)) {
                deltaY = 0;
            }
            this.mDeltaY = deltaY;
            if (!testBit(this.mGravity, 5)) {
                deltaX = 0;
            }
            this.mDeltaX = deltaX;
            return;
        }
        if (this.mHorizontalPosition == 3) {
            this.mDeltaX = -this.mMenuWidth;
        } else {
            this.mDeltaX = this.mAnchorRealWidth;
        }
        this.mPopupWindow.setOverlapAnchor(true);
        if (this.mVerticalPosition == 1) {
            this.mDeltaY = -(this.mMenuHeight - this.mAnchorRealHeight);
        }
    }

    public void show() {
        this.mVisibleItems = getVisibleItems();
        this.mVisibleMenuItemViews.clear();
        if (this.mRootParentWindow == null) {
            this.mRootParentWindow = (ViewGroup) this.mActivity.getWindow().getDecorView().getRootView();
        }
        dimBackground();
        this.mMenuLinearLayout = new LinearLayout(this.mActivity);
        this.mMenuLinearLayout.setOrientation(1);
        this.mMenuContainer.addView(this.mMenuLinearLayout, -2, -2);
        this.mMenuContainer.setAccessibilityDelegate(new AccessibilityDelegate() {
            public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(host, info);
                info.setCollectionInfo(CollectionInfo.obtain(ContextMenu.this.mVisibleItems.size(), 0, false));
            }
        });
        this.mMenuLinearLayout.setOutlineProvider(new ViewOutlineProvider() {
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), (float) view.getResources().getDimensionPixelSize(R.dimen.card_rounded_corner_radius));
            }
        });
        this.mMenuLinearLayout.setClipToOutline(true);
        this.mTriangle.setImageDrawable(this.mActivity.getDrawable(R.drawable.context_menu_triangle));
        this.mTriangle.setColorFilter(this.mUnfocusedColor, Mode.SRC_ATOP);
        addMenuItemViews();
        calculateMenuSize();
        determineGravity();
        this.mMenuContainer.addView(this.mTriangle, -2, -2);
        adjustTrianglePosition();
        adjustLayoutMenu();
        int triangleTopLocation = ((MarginLayoutParams) this.mTriangle.getLayoutParams()).topMargin;
        if (this.mHorizontalPosition != 17) {
            int i = 0;
            while (i < this.mVisibleItems.size()) {
                if (triangleTopLocation >= this.mMenuHeightPerRow * i && triangleTopLocation <= this.mMenuHeightPerRow * (i + 1)) {
                    ((ContextMenuItem) this.mVisibleItems.get(i)).setLinkedWithTriangle(true);
                    break;
                }
                i++;
            }
        } else {
            ((ContextMenuItem) this.mVisibleItems.get(0)).setLinkedWithTriangle(true);
        }
        adjustMenuShowUpPosition();
        ((View) this.mVisibleMenuItemViews.get(0)).requestFocus();
        this.mPopupWindow.setWidth(this.mMenuWidth);
        this.mPopupWindow.setHeight(this.mMenuHeight);
        if (this.mHorizontalPosition == 17) {
            this.mPopupWindow.showAsDropDown(this.mAnchor, this.mDeltaX, this.mDeltaY, this.mGravity);
        } else {
            this.mPopupWindow.showAsDropDown(this.mAnchor, this.mDeltaX, this.mDeltaY, 3);
        }
        this.mIsShowing = true;
    }

    public void setOnMenuItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnDismissListener(OnDismissListener listener) {
        this.mOnDismissListener = listener;
    }

    private float getFloat(int resourceId) {
        TypedValue resValue = new TypedValue();
        this.mActivity.getResources().getValue(resourceId, resValue, true);
        return resValue.getFloat();
    }

    private void bindMenuItemView(final ContextMenuItem menuItem, final View view) {
        TextView actionTextView = (TextView) view.findViewById(R.id.title);
        actionTextView.setText(menuItem.getTitle());
        actionTextView.setTextColor(view.getContext().getColor(menuItem.isEnabled() ? R.color.context_menu_text_enabled_color : R.color.context_menu_text_disabled_color));
        ImageView menuIcon = (ImageView) view.findViewById(R.id.icon);
        menuIcon.setColorFilter(menuItem.isEnabled() ? this.mEnabledColor : this.mDisabledColor, Mode.SRC_ATOP);
        if (menuItem.getIcon() != null) {
            menuIcon.setImageDrawable(menuItem.getIcon());
        }
        view.setBackgroundColor(this.mUnfocusedColor);
        view.setFocusable(menuItem.isEnabled());
        view.setEnabled(menuItem.isEnabled());
        view.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (menuItem.isEnabled()) {
                    ContextMenu.this.forceDismiss();
                    if (ContextMenu.this.mOnItemClickListener != null) {
                        ContextMenu.this.mOnItemClickListener.onItemClick(menuItem);
                    }
                }
            }
        });
        view.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (menuItem.isLinkedWithTriangle()) {
                        ContextMenu.this.mTriangle.setColorFilter(ContextMenu.this.mFocusedColor, Mode.SRC_ATOP);
                    }
                    view.setBackgroundColor(ContextMenu.this.mFocusedColor);
                    return;
                }
                if (menuItem.isLinkedWithTriangle()) {
                    ContextMenu.this.mTriangle.setColorFilter(ContextMenu.this.mUnfocusedColor, Mode.SRC_ATOP);
                }
                view.setBackgroundColor(ContextMenu.this.mUnfocusedColor);
            }
        });
        this.mVisibleMenuItemViews.add(view);
    }

    private void addMenuItemViews() {
        this.mMenuLinearLayout.removeAllViews();
        LayoutInflater layoutInflater = (LayoutInflater) this.mActivity.getSystemService("layout_inflater");
        for (int i = 0; i < this.mVisibleItems.size(); i++) {
            View rowView = layoutInflater.inflate(R.layout.context_menu_item, null);
            bindMenuItemView((ContextMenuItem) this.mVisibleItems.get(i), rowView);
            this.mMenuLinearLayout.addView(rowView, -1, this.mMenuHeightPerRow);
        }
    }
}
