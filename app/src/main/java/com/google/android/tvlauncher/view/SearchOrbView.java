package com.google.android.tvlauncher.view;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v17.leanback.widget.SearchOrbView.Colors;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.accessibility.AccessibilityManager;
import android.widget.FrameLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

import com.google.android.tvlauncher.R;
import com.google.android.tvlauncher.appsview.AppsManager.SearchPackageChangeListener;
import com.google.android.tvlauncher.home.util.ProgramUtil;
import com.google.android.tvlauncher.util.Partner;

import java.util.Random;

public class SearchOrbView extends FrameLayout implements SearchPackageChangeListener {
    private static final String EXTRA_SEARCH_TYPE = "search_type";
    private static final int FOCUSED_KEYBOARD_TEXT = -3;
    private static final int FOCUSED_MIC_TEXT = -2;
    private static final int INIT_TEXT = -1;
    private static final String KATNISS_PACKAGE = "com.google.android.katniss";
    private static final int SEARCH_TYPE_KEYBOARD = 2;
    private static final int SEARCH_TYPE_VOICE = 1;
    private static final String TAG = "SearchOrbView";
    private static final int TEXT_ANIM_FADE = 2;
    private static final int TEXT_ANIM_HORIZONTAL = 1;
    private static final int TEXT_ANIM_VERTICAL = 0;
    private int mClickDeviceId = -1;
    private int mColorBright;
    private int mColorDim;
    private Context mContext;
    private int mCurrentIndex = 0;
    private boolean mEatDpadCenterKeyDown;
    private final int mFocusedColor;
    private final String mFocusedKeyboardText;
    private final String mFocusedMicText;
    private final String mFocusedText;
    private final OnGlobalLayoutListener mGlobalLayoutListener = new OnGlobalLayoutListener() {
        public void onGlobalLayout() {
            SearchOrbView.this.setKeyboardOrbProgress(SearchOrbView.this.mKeyboardOrbProgress);
        }
    };
    private Handler mHandler = new Handler();
    private final int mIdleTextFlipDelay;
    private final boolean mIsHintFlippingAllowed;
    private FrameLayout mKeyboardContainer;
    private Drawable mKeyboardFocusedIcon;
    private final int mKeyboardOrbAnimationDuration;
    private float mKeyboardOrbProgress = 0.0f;
    private android.support.v17.leanback.widget.SearchOrbView mKeyboardOrbView;
    private Drawable mKeyboardUnfocusedIcon;
    private final int mLaunchFadeDuration;
    private android.support.v17.leanback.widget.SearchOrbView mMicOrbView;
    private ObjectAnimator mOrbAnimation;
    private final String mSearchHintText;
    private final Intent mSearchIntent = getSearchIntent();
    private final int mSearchOrbsSpacing;
    private Runnable mSwitchRunnable;
    private TextSwitcher mSwitcher;
    private final String[] mTextToShow;
    private final int mUnfocusedColor;
    private boolean mWahlbergUx;

    public SearchOrbView(Context context, AttributeSet attrs) {
        super(context, attrs);
        boolean z = false;
        this.mContext = context;
        Theme theme = this.mContext.getTheme();
        Resources res = context.getResources();
        this.mTextToShow = res.getStringArray(R.array.search_orb_text_to_show);
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
    }

    private boolean useWahlbergUx() {
        try {
            Resources searchResources = this.mContext.getPackageManager().getResourcesForApplication(KATNISS_PACKAGE);
            int resId = 0;
            if (searchResources != null) {
                resId = searchResources.getIdentifier("katniss_uses_new_google_logo", "bool", KATNISS_PACKAGE);
            }
            if (resId != 0) {
                return searchResources.getBoolean(resId);
            }
        } catch (NameNotFoundException e) {
        }
        return false;
    }

    private boolean isKatnissPackagePresent() {
        PackageInfo info;
        try {
            info = this.mContext.getPackageManager().getPackageInfo(KATNISS_PACKAGE, 0);
        } catch (NameNotFoundException e) {
            info = null;
        }
        if (info != null) {
            return true;
        }
        return false;
    }

    public void onFinishInflate() {
        super.onFinishInflate();
        this.mMicOrbView = (android.support.v17.leanback.widget.SearchOrbView) findViewById(R.id.mic_orb);
        this.mMicOrbView.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                SearchOrbView.this.setSearchState(false);
                if (SearchOrbView.this.mWahlbergUx) {
                    SearchOrbView.this.mMicOrbView.setOrbColor(hasFocus ? SearchOrbView.this.mColorBright : SearchOrbView.this.mColorDim);
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
        return KATNISS_PACKAGE;
    }

    private void initializeSearchOrbs() {
        if (this.mOrbAnimation != null && (this.mOrbAnimation.isRunning() || this.mOrbAnimation.isStarted())) {
            this.mOrbAnimation.cancel();
        }
        this.mOrbAnimation = null;
        setKeyboardOrbProgress(0.0f);
        if (this.mWahlbergUx) {
            this.mKeyboardOrbView = (android.support.v17.leanback.widget.SearchOrbView) findViewById(R.id.keyboard_orb);
            this.mKeyboardContainer = (FrameLayout) findViewById(R.id.keyboard_orb_container);
            this.mKeyboardFocusedIcon = ContextCompat.getDrawable(this.mContext, R.drawable.ic_keyboard_black);
            this.mKeyboardUnfocusedIcon = ContextCompat.getDrawable(this.mContext, R.drawable.ic_keyboard_grey);
            this.mColorBright = ContextCompat.getColor(this.mContext, R.color.search_orb_bg_bright_color);
            this.mColorDim = ContextCompat.getColor(this.mContext, R.color.search_orb_bg_dim_color);
            this.mKeyboardOrbView.setOrbIcon(this.mKeyboardUnfocusedIcon);
            this.mKeyboardOrbView.setOrbColor(this.mColorDim);
            this.mKeyboardOrbView.enableOrbColorAnimation(false);
            this.mKeyboardOrbView.setOnFocusChangeListener(new OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    SearchOrbView.this.setSearchState(false);
                    android.support.v17.leanback.widget.SearchOrbView keyboardOrbView = (android.support.v17.leanback.widget.SearchOrbView) v;
                    keyboardOrbView.setOrbIcon(hasFocus ? SearchOrbView.this.mKeyboardFocusedIcon : SearchOrbView.this.mKeyboardUnfocusedIcon);
                    keyboardOrbView.setOrbColor(hasFocus ? SearchOrbView.this.mColorBright : SearchOrbView.this.mColorDim);
                }
            });
            this.mOrbAnimation = ObjectAnimator.ofFloat(this, "keyboardOrbProgress", new float[]{0.0f});
            this.mOrbAnimation.setDuration((long) this.mKeyboardOrbAnimationDuration);
        } else {
            this.mKeyboardFocusedIcon = null;
            this.mKeyboardUnfocusedIcon = null;
            this.mKeyboardOrbView = null;
            this.mKeyboardContainer = null;
        }
        Drawable partnerSearchIcon = Partner.get(this.mContext).getCustomSearchIcon();
        if (partnerSearchIcon != null) {
            this.mMicOrbView.setOrbIcon(partnerSearchIcon);
        } else if (this.mWahlbergUx) {
            this.mMicOrbView.setOrbColor(this.mColorDim);
            this.mMicOrbView.setOrbIcon(ContextCompat.getDrawable(this.mContext, R.drawable.ic_mic_color));
            this.mMicOrbView.enableOrbColorAnimation(false);
        } else {
            this.mMicOrbView.setOrbColors(new Colors(ContextCompat.getColor(this.mContext, R.color.search_orb_bg_color_old), ContextCompat.getColor(this.mContext, R.color.search_orb_bg_bright_color_old)));
            this.mMicOrbView.setOrbIcon(ContextCompat.getDrawable(this.mContext, R.drawable.ic_search_mic_out_normal));
            this.mMicOrbView.enableOrbColorAnimation(true);
        }
    }

    public static int getColor(Resources res, int id, @Nullable Theme theme) {
        return ResourcesCompat.getColor(res, id, theme); // api
    }

    private boolean focusIsOnSearchView() {
        return this.mMicOrbView.hasFocus() || (this.mKeyboardOrbView != null && this.mKeyboardOrbView.hasFocus());
    }

    private void setSearchState(boolean isReset) {
        boolean isKeyboard;
        this.mHandler.removeCallbacks(this.mSwitchRunnable);
        boolean focused = focusIsOnSearchView();
        int old = this.mCurrentIndex;
        if (this.mWahlbergUx && focused && !this.mMicOrbView.hasFocus()) {
            isKeyboard = true;
        } else {
            isKeyboard = false;
        }
        int i = focused ? !isKeyboard ? -2 : -3 : -1;
        this.mCurrentIndex = i;
        if (old != this.mCurrentIndex) {
            boolean useFade;
            boolean z;
            if (old == -1 || this.mCurrentIndex == -1) {
                useFade = false;
            } else {
                useFade = true;
            }
            if (isReset) {
                z = false;
            } else {
                z = true;
            }
            configSwitcher(z, focused, useFade ? 2 : 1);
            if (this.mWahlbergUx) {
                this.mSwitcher.setText(fixItalics(getHintText(focused, isKeyboard)));
            } else {
                this.mSwitcher.setText(fixItalics(getHintText(focused, false)));
            }
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
        this.mSwitcher = (TextSwitcher) findViewById(R.id.text_switcher);
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
                SearchOrbView.this.mHandler.postDelayed(this, (long) SearchOrbView.this.mIdleTextFlipDelay);
            }
        };
        reset();
    }

    public String fixItalics(String text) {
        if (text == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder(text);
        if (getLayoutDirection() == LAYOUT_DIRECTION_RTL) {
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
            textView.setTypeface(null, 2);
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

    private void setVisible(boolean visible) {
        animateVisibility(this.mSwitcher, visible);
    }

    private void animateVisibility(View view, boolean visible) {
        view.clearAnimation();
        ViewPropertyAnimator anim = view.animate().alpha(visible ? 1.0f : 0.0f).setDuration((long) this.mLaunchFadeDuration);
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

    public void animateKeyboardOrb(boolean visible) {
        if (this.mKeyboardOrbView != null && this.mOrbAnimation != null) {
            if (this.mOrbAnimation.isStarted()) {
                this.mOrbAnimation.cancel();
            }
            if (this.mKeyboardOrbProgress != (visible ? 1.0f : 0.0f)) {
                this.mOrbAnimation.setFloatValues(new float[]{this.mKeyboardOrbProgress, visible ? 1.0f : 0.0f});
                this.mOrbAnimation.start();
            }
        }
    }

    private void setKeyboardOrbProgress(float prog) {
        boolean focusable;
        boolean visible;
        int i = 0;
        int i2 = 1;
        if (((double) prog) == ProgramUtil.ASPECT_RATIO_1_1) {
            focusable = true;
        } else {
            focusable = false;
        }
        if (prog > 0.0f) {
            visible = true;
        } else {
            visible = false;
        }
        this.mKeyboardOrbProgress = prog;
        if (this.mKeyboardOrbView != null) {
            this.mKeyboardOrbView.setFocusable(focusable);
            this.mKeyboardOrbView.setClipToOutline(true);
            this.mKeyboardOrbView.setAlpha(prog);
            FrameLayout frameLayout = this.mKeyboardContainer;
            if (!visible) {
                i = 4;
            }
            frameLayout.setVisibility(i);
            this.mKeyboardContainer.setScaleX(prog);
            this.mKeyboardContainer.setScaleY(prog);
            int orbWidth = this.mKeyboardOrbView.getMeasuredWidth();
            if (getLayoutDirection() != LAYOUT_DIRECTION_RTL) {
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
        getViewTreeObserver().removeOnGlobalLayoutListener(this.mGlobalLayoutListener);
        reset();
    }

    public void onAttachedToWindow() {
        boolean isTouchExplorationEnabled = true;
        super.onAttachedToWindow();
        setVisible(true);
        AccessibilityManager am = (AccessibilityManager) this.mContext.getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (am != null && !(am.isEnabled() && am.isTouchExplorationEnabled())) {
            isTouchExplorationEnabled = false;
        }
        final boolean finalIsTouchExplorationEnabled = isTouchExplorationEnabled;
        OnClickListener listener = new OnClickListener() {
            public void onClick(View view) {
                boolean success;
                boolean isKeyboardSearch = SearchOrbView.this.mKeyboardOrbView != null && SearchOrbView.this.mKeyboardOrbView.hasFocus();
                if (finalIsTouchExplorationEnabled) {
                    success = SearchOrbView.startSearchActivitySafely(SearchOrbView.this.mContext, SearchOrbView.this.mSearchIntent, isKeyboardSearch);
                } else {
                    success = SearchOrbView.startSearchActivitySafely(SearchOrbView.this.mContext, SearchOrbView.this.mSearchIntent, SearchOrbView.this.mClickDeviceId, isKeyboardSearch);
                }
                if (success) {
                    SearchOrbView.this.reset();
                }
            }
        };
        this.mMicOrbView.setOnClickListener(listener);
        if (this.mKeyboardOrbView != null) {
            this.mKeyboardOrbView.setOnClickListener(listener);
        }
        getViewTreeObserver().addOnGlobalLayoutListener(this.mGlobalLayoutListener);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        if (isConfirmKey(event.getKeyCode())) {
            if (event.isLongPress()) {
                this.mEatDpadCenterKeyDown = true;
                playErrorSound(getContext());
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

    protected void onVisibilityChanged(View changedView, int visibility) {
        boolean z = true;
        if (changedView == this) {
            boolean isVisible = visibility == 0;
            if (!isVisible) {
                reset();
            } else if (this.mKeyboardOrbView != null && this.mKeyboardOrbView.hasFocus()) {
                this.mMicOrbView.requestFocus();
            }
            android.support.v17.leanback.widget.SearchOrbView searchOrbView = this.mMicOrbView;
            if (!(isVisible && this.mMicOrbView.hasFocus() && !this.mWahlbergUx)) {
                z = false;
            }
            searchOrbView.enableOrbColorAnimation(z);
        }
    }

    public void animateIn() {
        setVisible(true);
    }

    private void animateOut() {
        setVisible(false);
        reset();
    }

    public static Intent getSearchIntent() {
        return new Intent("android.intent.action.ASSIST").addFlags(270532608);
    }

    private static boolean startActivitySafely(Context context, Intent intent) {
        try {
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "Exception launching intent " + intent, e);
            Toast.makeText(context, context.getString(R.string.app_unavailable), 0).show();
            return false;
        }
    }

    private static boolean startSearchActivitySafely(Context context, Intent intent, int deviceId, boolean isKeyboardSearch) {
        intent.putExtra("android.intent.extra.ASSIST_INPUT_DEVICE_ID", deviceId);
        intent.putExtra(EXTRA_SEARCH_TYPE, isKeyboardSearch ? 2 : 1);
        return startActivitySafely(context, intent);
    }

    private static boolean startSearchActivitySafely(Context context, Intent intent, boolean isKeyboardSearch) {
        intent.putExtra(EXTRA_SEARCH_TYPE, isKeyboardSearch ? 2 : 1);
        return startActivitySafely(context, intent);
    }

    private static void playErrorSound(Context context) {
        ((AudioManager) context.getSystemService("audio")).playSoundEffect(9);
    }

    private static boolean isConfirmKey(int keyCode) {
        switch (keyCode) {
            case 23:
            case 62:
            case 66:
            case 96:
            case 160:
                return true;
            default:
                return false;
        }
    }
}
