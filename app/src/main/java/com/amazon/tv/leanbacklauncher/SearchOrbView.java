package com.amazon.tv.leanbacklauncher;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.accessibility.AccessibilityManager;
import android.widget.FrameLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

import androidx.core.content.ContextCompat;
import androidx.leanback.widget.SearchOrbView.Colors;

import com.amazon.tv.leanbacklauncher.MainActivity.IdleListener;
import com.amazon.tv.leanbacklauncher.apps.AppsManager.SearchPackageChangeListener;
import com.amazon.tv.leanbacklauncher.util.Partner;
import com.amazon.tv.leanbacklauncher.util.Util;

import java.util.Random;

public class SearchOrbView extends FrameLayout implements IdleListener, SearchPackageChangeListener {
    private Drawable mAssistantIcon;
    private int mClickDeviceId = -1;
    private int mColorBright;
    private int mColorDim;
    private Context mContext;
    private int mCurrentIndex = 0;
    private Drawable mDefaultColorMicIcon;
    private String[] mDefaultTextToShow;
    private boolean mEatDpadCenterKeyDown;
    private final int mFocusedColor;
    private final String mFocusedKeyboardText;
    private final String mFocusedMicText;
    private final String mFocusedText;
    private Handler mHandler = new Handler();
    private final int mIdleTextFlipDelay;
    private final boolean mIsHintFlippingAllowed;
    private FrameLayout mKeyboardContainer;
    private Drawable mKeyboardFocusedIcon;
    private final int mKeyboardOrbAnimationDuration;
    private float mKeyboardOrbProgress = 0.0f;
    private androidx.leanback.widget.SearchOrbView mKeyboardOrbView;
    private Drawable mKeyboardUnfocusedIcon;
    private final int mLaunchFadeDuration;
    private SearchLaunchListener mListener;
    private androidx.leanback.widget.SearchOrbView mMicOrbView;
    private Drawable mMicUnfocusedIcon;
    private ObjectAnimator mOrbAnimation;
    private final String mSearchHintText;
    private final Intent mSearchIntent = Util.getSearchIntent();
    private final int mSearchOrbsSpacing;
    private Runnable mSwitchRunnable;
    private TextSwitcher mSwitcher;
    private String[] mTextToShow;
    private final int mUnfocusedColor;
    private boolean mWahlbergUx;
    private View mWidgetView;

    public interface SearchLaunchListener {
        void onSearchLaunched();
    }

    public SearchOrbView(Context context, AttributeSet attrs) {
        super(context, attrs);
        boolean z = false;
        this.mContext = context;
        Resources res = context.getResources();
        this.mDefaultTextToShow = res.getStringArray(R.array.search_orb_text_to_show);
        this.mIdleTextFlipDelay = res.getInteger(R.integer.search_orb_idle_hint_flip_delay);
        this.mLaunchFadeDuration = res.getInteger(R.integer.search_orb_text_fade_duration);
        this.mSearchHintText = fixItalics(context.getString(R.string.search_hint_text));
        this.mFocusedText = fixItalics(context.getString(R.string.focused_search_hint_text));
        this.mFocusedMicText = fixItalics(context.getString(R.string.focused_search_mic_hint_text));
        this.mFocusedKeyboardText = context.getString(R.string.focused_search_keyboard_hint_text);
        this.mFocusedColor = ContextCompat.getColor(this.mContext, R.color.search_orb_focused_hint_color);
        this.mUnfocusedColor = ContextCompat.getColor(this.mContext, R.color.search_orb_unfocused_hint_color);
        if (res.getBoolean(R.bool.is_hint_flipping_allowed) && isKatnissPackagePresent()) {
            z = true;
        }
        this.mIsHintFlippingAllowed = z;
        this.mWahlbergUx = useWahlbergUx();
        this.mSearchOrbsSpacing = res.getDimensionPixelSize(R.dimen.search_orbs_spacing);
        this.mKeyboardOrbAnimationDuration = res.getInteger(R.integer.lb_search_orb_scale_duration_ms);
        this.mDefaultColorMicIcon = res.getDrawable(R.drawable.ic_mic_color, null);
        this.mMicUnfocusedIcon = res.getDrawable(R.drawable.ic_mic_grey, null);
    }

    private boolean useWahlbergUx() {
        try {
            Resources searchResources = this.mContext.getPackageManager().getResourcesForApplication("com.google.android.katniss");
            int resId = 0;
            if (searchResources != null) {
                resId = searchResources.getIdentifier("katniss_uses_new_google_logo", "bool", "com.google.android.katniss");
            }
            if (resId != 0) {
                return searchResources.getBoolean(resId);
            }
            // FIXME: versionCode deprecated in API28, only Katniss 3.13+
            int vc = this.mContext.getPackageManager().getPackageInfo("com.google.android.katniss", 0).versionCode; // 11000272
            if (vc > 11000000) {
                return true;
            }
        } catch (NameNotFoundException e) {
        }
        return false;
    }

    private boolean isKatnissPackagePresent() {
        PackageInfo info;
        try {
            info = this.mContext.getPackageManager().getPackageInfo("com.google.android.katniss", 0);
        } catch (NameNotFoundException e) {
            info = null;
        }
        return info != null;
    }

    public void onFinishInflate() {
        super.onFinishInflate();
        this.mWidgetView = findViewById(R.id.widget_wrapper);
        this.mMicOrbView = findViewById(R.id.mic_orb);
        this.mMicOrbView.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                SearchOrbView.this.setSearchState(false);
                if (SearchOrbView.this.mAssistantIcon != null) {
                    SearchOrbView.this.mMicOrbView.setOrbIcon(hasFocus ? SearchOrbView.this.mDefaultColorMicIcon : SearchOrbView.this.mAssistantIcon);
                }
            }
        });
        initializeSearchOrbs();
        initTextSwitcher(getContext());
    }

    public void onSearchPackageChanged() {
        if (useWahlbergUx() != this.mWahlbergUx) {
            this.mWahlbergUx = useWahlbergUx();
            initializeSearchOrbs();
            setSearchState(false);
        }
    }

    public String getSearchPackageName() {
        return "com.google.android.katniss";
    }

    private void initializeSearchOrbs() {
        if (this.mOrbAnimation != null && (this.mOrbAnimation.isRunning() || this.mOrbAnimation.isStarted())) {
            this.mOrbAnimation.cancel();
        }
        this.mOrbAnimation = null;
        if (this.mWahlbergUx) {
            this.mKeyboardOrbView = findViewById(R.id.keyboard_orb);
            this.mKeyboardContainer = findViewById(R.id.keyboard_orb_container);
            this.mKeyboardFocusedIcon = ContextCompat.getDrawable(this.mContext, R.drawable.ic_keyboard_blue);
            this.mKeyboardUnfocusedIcon = ContextCompat.getDrawable(this.mContext, R.drawable.ic_keyboard_grey);
            this.mColorBright = ContextCompat.getColor(this.mContext, R.color.search_orb_bg_bright_color);
            this.mColorDim = ContextCompat.getColor(this.mContext, R.color.search_orb_bg_dim_color);
            this.mKeyboardOrbView.setOrbIcon(this.mKeyboardUnfocusedIcon);
            this.mKeyboardOrbView.enableOrbColorAnimation(false);
            this.mKeyboardOrbView.setOnFocusChangeListener(new OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    SearchOrbView.this.setSearchState(false);
                    androidx.leanback.widget.SearchOrbView keyboardOrbView = (androidx.leanback.widget.SearchOrbView) v;
                    keyboardOrbView.setOrbIcon(hasFocus ? SearchOrbView.this.mKeyboardFocusedIcon : SearchOrbView.this.mKeyboardUnfocusedIcon);
                    keyboardOrbView.setOrbColor(hasFocus ? SearchOrbView.this.mColorBright : SearchOrbView.this.mColorDim);
                    if (hasFocus) {
                        SearchOrbView.this.mMicOrbView.setOrbIcon(SearchOrbView.this.mMicUnfocusedIcon);
                    } else if (SearchOrbView.this.mAssistantIcon != null) {
                        SearchOrbView.this.mMicOrbView.setOrbIcon(SearchOrbView.this.mAssistantIcon);
                    } else {
                        SearchOrbView.this.mMicOrbView.setOrbIcon(SearchOrbView.this.mDefaultColorMicIcon);
                    }
                }
            });
            this.mOrbAnimation = ObjectAnimator.ofFloat(this, "keyboardOrbProgress", 0.0f);
            this.mOrbAnimation.setDuration(this.mKeyboardOrbAnimationDuration);
        } else {
            this.mKeyboardFocusedIcon = null;
            this.mKeyboardUnfocusedIcon = null;
            this.mKeyboardOrbView = null;
            this.mKeyboardContainer = null;
        }
        Drawable partnerSearchIcon = Partner.get(this.mContext).getCustomSearchIcon();
        if (this.mAssistantIcon != null) {
            this.mMicOrbView.setOrbIcon(this.mAssistantIcon);
            this.mMicOrbView.setOrbColor(this.mColorBright);
            this.mMicOrbView.enableOrbColorAnimation(false);
        } else if (partnerSearchIcon != null) {
            this.mMicOrbView.setOrbIcon(partnerSearchIcon);
        } else if (this.mWahlbergUx) {
            this.mMicOrbView.setOrbColor(this.mColorBright);
            this.mMicOrbView.setOrbIcon(this.mDefaultColorMicIcon);
            this.mMicOrbView.enableOrbColorAnimation(false);
        } else {
            this.mMicOrbView.setOrbColors(new Colors(ContextCompat.getColor(this.mContext, R.color.search_orb_bg_color_old), ContextCompat.getColor(this.mContext, R.color.search_orb_bg_bright_color_old)));
            this.mMicOrbView.setOrbIcon(ContextCompat.getDrawable(this.mContext, R.drawable.ic_search_mic_out_normal));
            this.mMicOrbView.enableOrbColorAnimation(true);
        }
        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                SearchOrbView.this.setKeyboardOrbProgress(SearchOrbView.this.mKeyboardOrbProgress);
            }
        });
    }

    private boolean focusIsOnSearchView() {
        return this.mMicOrbView.hasFocus() || (this.mKeyboardOrbView != null && this.mKeyboardOrbView.hasFocus());
    }

    private void setSearchState(boolean isReset) {
        boolean isKeyboard;
        this.mHandler.removeCallbacks(this.mSwitchRunnable);
        boolean focused = focusIsOnSearchView();
        int old = this.mCurrentIndex;
        isKeyboard = this.mWahlbergUx && focused && !this.mMicOrbView.hasFocus();
        int i = focused ? !isKeyboard ? -2 : -3 : -1;
        this.mCurrentIndex = i;
        if (old != this.mCurrentIndex) {
            boolean useFade;
            boolean z;
            useFade = old != -1 && this.mCurrentIndex != -1;
            z = !isReset;
            configSwitcher(z, focused, useFade ? 2 : 1);
            if (this.mWahlbergUx) {
                this.mSwitcher.setText(fixItalics(getHintText(focused, isKeyboard)));
            } else {
                this.mSwitcher.setText(fixItalics(getHintText(focused, false)));
            }
        }
        if (this.mKeyboardOrbView != null) {
            animateKeyboardOrb(focused);
        }
    }

    private String getHintText(boolean focused, boolean isKeyboard) {
        if (!this.mWahlbergUx) {
            return focused ? this.mFocusedText : this.mSearchHintText;
        } else {
            if (focused) {
                return isKeyboard ? this.mFocusedKeyboardText : this.mFocusedMicText;
            } else {
                return this.mSearchHintText;
            }
        }
    }

    private void initTextSwitcher(final Context context) {
        this.mSwitcher = findViewById(R.id.text_switcher);
        this.mSwitcher.setAnimateFirstView(false);
        this.mSwitcher.setFactory(new ViewFactory() {
            LayoutInflater inflater = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));

            public View makeView() {
                return this.inflater.inflate(R.layout.search_orb_text_hint, SearchOrbView.this, false);
            }
        });
        this.mSwitchRunnable = new Runnable() {
            public void run() {
                int old = SearchOrbView.this.mCurrentIndex;
                SearchOrbView.this.mCurrentIndex = new Random().nextInt(SearchOrbView.this.mTextToShow.length);
                if (old == SearchOrbView.this.mCurrentIndex) {
                    SearchOrbView.this.mCurrentIndex = (SearchOrbView.this.mCurrentIndex + 1) % SearchOrbView.this.mTextToShow.length;
                }
                SearchOrbView.this.configSwitcher(true, false, 0);
                SearchOrbView.this.mSwitcher.setText(SearchOrbView.this.fixItalics(SearchOrbView.this.mTextToShow[SearchOrbView.this.mCurrentIndex]));
                SearchOrbView.this.mHandler.postDelayed(this, SearchOrbView.this.mIdleTextFlipDelay);
            }
        };
        reset();
    }

    public String fixItalics(String text) {
        if (text == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder(text);
        if (getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            builder.insert(0, " ");
        } else {
            builder.append(" ");
        }
        return builder.toString();
    }

    private void configSwitcher(boolean switchViews, boolean focused, int animType) {
        int inAnim;
        int outAnim;
        View v = switchViews ? this.mSwitcher.getNextView() : this.mSwitcher.getCurrentView();
        if (v instanceof TextView) {
            TextView textView = (TextView) v;
            textView.setTextColor(focused ? this.mFocusedColor : this.mUnfocusedColor);
            textView.setTypeface(null, Typeface.ITALIC);
        }
        if (animType == 1) {
            inAnim = R.anim.slide_in_left;
            outAnim = R.anim.slide_out_right;
        } else if (animType == 0) {
            inAnim = R.anim.slide_in_bottom;
            outAnim = R.anim.slide_out_top;
        } else {
            inAnim = R.anim.fade_in;
            outAnim = R.anim.fade_out;
        }
        this.mSwitcher.setInAnimation(this.mContext, inAnim);
        this.mSwitcher.setOutAnimation(this.mContext, outAnim);
    }

    public void reset() {
        this.mHandler.removeCallbacks(this.mSwitchRunnable);
        this.mSwitcher.reset();
        this.mCurrentIndex = 0;
        setSearchState(true);
    }

    public void onIdleStateChange(boolean isIdle) {
        if (this.mIsHintFlippingAllowed) {
            this.mHandler.removeCallbacks(this.mSwitchRunnable);
            if (isIdle && isAttachedToWindow() && isOnScreen() && !this.mMicOrbView.hasFocus()) {
                this.mHandler.post(this.mSwitchRunnable);
            }
        }
    }

    private boolean isOnScreen() {
        Rect rect = new Rect();
        return getGlobalVisibleRect(rect) && getHeight() == rect.height() && getWidth() == rect.width();
    }

    public void onVisibilityChange(boolean isVisible) {
        if (!isVisible) {
            reset();
        } else if (this.mKeyboardOrbView != null && this.mKeyboardOrbView.hasFocus()) {
            this.mMicOrbView.requestFocus();
        }
        androidx.leanback.widget.SearchOrbView searchOrbView = this.mMicOrbView;
        boolean z = isVisible && this.mMicOrbView.hasFocus() && !this.mWahlbergUx;
        searchOrbView.enableOrbColorAnimation(z);
    }

    private void setVisibile(boolean visible) {
        animateVisibility(this.mWidgetView, visible);
        animateVisibility(this.mSwitcher, visible);
    }

    private void animateVisibility(View view, boolean visible) {
        view.clearAnimation();
        ViewPropertyAnimator anim = view.animate().alpha(visible ? 1.0f : 0.0f).setDuration(this.mLaunchFadeDuration);
        if (!(!this.mWahlbergUx || this.mKeyboardOrbView == null || visible)) {
            anim.setListener(new AnimatorListener() {
                public void onAnimationStart(Animator animation) {
                }

                public void onAnimationEnd(Animator animation) {
                    if (SearchOrbView.this.mKeyboardOrbView != null && SearchOrbView.this.mKeyboardOrbView.hasFocus()) {
                        SearchOrbView.this.mMicOrbView.requestFocus();
                    }
                }

                public void onAnimationCancel(Animator animation) {
                }

                public void onAnimationRepeat(Animator animation) {
                }
            });
        }
        anim.start();
    }

    private void animateKeyboardOrb(boolean visible) {
        if (this.mOrbAnimation != null) {
            if (this.mOrbAnimation.isStarted()) {
                this.mOrbAnimation.cancel();
            }
            if (this.mKeyboardOrbProgress != (visible ? 1.0f : 0.0f)) {
                this.mOrbAnimation.setFloatValues(this.mKeyboardOrbProgress, visible ? 1.0f : 0.0f);
                this.mOrbAnimation.start();
            }
        }
    }

    private void setKeyboardOrbProgress(float prog) {
        boolean focusable;
        boolean visible;
        int i = 0;
        int i2 = 1;
        focusable = ((double) prog) == 1.0d;
        visible = prog > 0.0f;
        this.mKeyboardOrbProgress = prog;
        if (this.mKeyboardOrbView != null) {
            this.mKeyboardOrbView.setFocusable(focusable);
            this.mKeyboardOrbView.setAlpha(prog);
            FrameLayout frameLayout = this.mKeyboardContainer;
            if (!visible) {
                i = View.INVISIBLE;
            }
            frameLayout.setVisibility(i);
            this.mKeyboardContainer.setScaleX(prog);
            this.mKeyboardContainer.setScaleY(prog);
            int orbWidth = this.mKeyboardOrbView.getMeasuredWidth();
            if (getLayoutDirection() != View.LAYOUT_DIRECTION_RTL) {
                i2 = -1;
            }
            float offset = ((float) (i2 * (this.mSearchOrbsSpacing + orbWidth))) * (1.0f - prog);
            this.mKeyboardOrbView.setTranslationX(offset / prog);
            if (this.mSwitcher != null) {
                this.mSwitcher.setTranslationX(offset);
            }
        }
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        reset();
    }

    public void onAttachedToWindow() {
        boolean isTouchExplorationEnabled = true;
        super.onAttachedToWindow();
        setVisibile(true);
        AccessibilityManager am = (AccessibilityManager) this.mContext.getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (!(am.isEnabled() && am.isTouchExplorationEnabled())) {
            isTouchExplorationEnabled = false;
        }
        final boolean finalIsTouchExplorationEnabled = isTouchExplorationEnabled;
        OnClickListener listener = new OnClickListener() {
            public void onClick(View view) {
                boolean iskeyboardSearch;
                boolean success;
                iskeyboardSearch = SearchOrbView.this.mKeyboardOrbView != null && SearchOrbView.this.mKeyboardOrbView.hasFocus();
                if (!finalIsTouchExplorationEnabled) {
                    success = Util.startSearchActivitySafely(SearchOrbView.this.mContext, SearchOrbView.this.mSearchIntent, SearchOrbView.this.mClickDeviceId, iskeyboardSearch) && SearchOrbView.this.mListener != null;
                } else success = Util.startSearchActivitySafely(SearchOrbView.this.mContext, SearchOrbView.this.mSearchIntent, iskeyboardSearch) && SearchOrbView.this.mListener != null;
                if (success) {
                    SearchOrbView.this.animateOut();
                    SearchOrbView.this.mListener.onSearchLaunched();
                }
            }
        };
        this.mMicOrbView.setOnClickListener(listener);
        if (this.mKeyboardOrbView != null) {
            this.mKeyboardOrbView.setOnClickListener(listener);
        }
    }

    public void setLaunchListener(SearchLaunchListener listener) {
        this.mListener = listener;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        if (Util.isConfirmKey(event.getKeyCode())) {
            if (event.isLongPress()) {
                this.mEatDpadCenterKeyDown = true;
                Util.playErrorSound(getContext());
                return true;
            } else if (action == 1) {
                if (this.mEatDpadCenterKeyDown) {
                    this.mEatDpadCenterKeyDown = false;
                    return true;
                }
                this.mClickDeviceId = event.getDeviceId();
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public void animateIn() {
        setVisibile(true);
    }

    private void animateOut() {
        setVisibile(false);
        reset();
    }

    public void setActivated(boolean active) {
    }

    public void updateSearchSuggestions(String[] suggestions) {
        this.mCurrentIndex = 0;
        if (suggestions == null || suggestions.length == 0) {
            this.mTextToShow = this.mDefaultTextToShow;
        } else {
            this.mTextToShow = suggestions;
        }
    }

    public void updateAssistantIcon(Drawable icon) {
        this.mAssistantIcon = icon;
        initializeSearchOrbs();
    }
}
