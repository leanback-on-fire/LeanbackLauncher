package com.rockchips.android.leanbacklauncher.recline.app;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v17.leanback.widget.VerticalGridView;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver.OnGlobalFocusChangeListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.rockchips.android.leanbacklauncher.R;

import java.util.ArrayList;

public class DialogFragment extends Fragment {
    private View mActionContainer;
    private ArrayList<Action> mActions;
    private DialogActionAdapter mAdapter;
    private int mBackgroundColor;
    private boolean mBackgroundColorSet;
    private int mBrandColor;
    private boolean mBrandColorSet;
    private String mBreadcrumb;
    private String mDescription;
    private boolean mEntryTransitionEnabled;
    private boolean mEntryTransitionPerformed;
    private int mIconBackgroundColor;
    private Bitmap mIconBitmap;
    private int mIconPadding;
    private int mIconResourceId;
    private Uri mIconUri;
    private boolean mIntroAnimationInProgress;
    private VerticalGridView mListView;
    private Action.Listener mListener;
    private String mName;
    private int mSelectedIndex;
    private String mTitle;

    /* renamed from: com.rockchips.android.recline.app.DialogFragment.1 */
    class C02121 implements OnGlobalFocusChangeListener {
        private boolean mChildFocused;
        final /* synthetic */ View val$selectorView;

        C02121(View val$selectorView) {
            this.val$selectorView = val$selectorView;
        }

        public void onGlobalFocusChanged(View oldFocus, View newFocus) {
            if (DialogFragment.this.mListView.getFocusedChild() == null) {
                this.val$selectorView.setVisibility(4);
                this.mChildFocused = false;
            } else if (!this.mChildFocused) {
                this.mChildFocused = true;
                this.val$selectorView.setVisibility(0);
                if (!DialogFragment.this.isIntroAnimationInProgress()) {
                    DialogFragment.this.onIntroAnimationFinished();
                }
            }
        }
    }

    /* renamed from: com.rockchips.android.recline.app.DialogFragment.2 */
    class C02132 implements Action.Listener {
        C02132() {
        }

        public void onActionClicked(Action action) {
            if (action.isEnabled() && !action.infoOnly()) {
                if (DialogFragment.this.mListener != null) {
                    DialogFragment.this.mListener.onActionClicked(action);
                } else if (DialogFragment.this.getActivity() instanceof Action.Listener) {
                    ((Action.Listener) DialogFragment.this.getActivity()).onActionClicked(action);
                }
            }
        }
    }

    /* renamed from: com.rockchips.android.recline.app.DialogFragment.3 */
    class C02143 implements Action.OnFocusListener {
        C02143() {
        }

        public void onActionFocused(Action action) {
            if (DialogFragment.this.getActivity() instanceof Action.OnFocusListener) {
                ((Action.OnFocusListener) DialogFragment.this.getActivity()).onActionFocused(action);
            }
        }
    }

    /* renamed from: com.rockchips.android.recline.app.DialogFragment.4 */
    class C02164 implements OnGlobalLayoutListener {
        Runnable mEntryAnimationRunnable;
        final /* synthetic */ View val$actionContainerView;
        final /* synthetic */ ColorDrawable val$bgDrawable;
        final /* synthetic */ View val$contentView;
        final /* synthetic */ View val$dialogView;

        /* renamed from: com.rockchips.android.recline.app.DialogFragment.4.1 */
        class C02151 implements Runnable {
            final /* synthetic */ View val$actionContainerView;
            final /* synthetic */ ColorDrawable val$bgDrawable;
            final /* synthetic */ View val$contentView;
            final /* synthetic */ View val$dialogView;

            C02151(View val$dialogView, ColorDrawable val$bgDrawable, View val$contentView, View val$actionContainerView) {
                this.val$dialogView = val$dialogView;
                this.val$bgDrawable = val$bgDrawable;
                this.val$contentView = val$contentView;
                this.val$actionContainerView = val$actionContainerView;
            }

            public void run() {
                if (DialogFragment.this.isAdded()) {
                    this.val$dialogView.setVisibility(0);
                    ObjectAnimator oa = ObjectAnimator.ofInt(this.val$bgDrawable, "alpha", new int[]{255});
                    oa.setDuration(250);
                    oa.setStartDelay(120);
                    oa.setInterpolator(new DecelerateInterpolator(1.0f));
                    oa.start();
                    DialogFragment.this.animateInContentView(this.val$contentView);
                    DialogFragment.this.animateInActionView(this.val$actionContainerView);
                }
            }
        }

        C02164(View val$contentView, View val$dialogView, ColorDrawable val$bgDrawable, View val$actionContainerView) {
            this.val$contentView = val$contentView;
            this.val$dialogView = val$dialogView;
            this.val$bgDrawable = val$bgDrawable;
            this.val$actionContainerView = val$actionContainerView;
            this.mEntryAnimationRunnable = new C02151(this.val$dialogView, this.val$bgDrawable, this.val$contentView, this.val$actionContainerView);
        }

        public void onGlobalLayout() {
            this.val$contentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            this.val$contentView.postOnAnimationDelayed(this.mEntryAnimationRunnable, 550);
        }
    }

    /* renamed from: com.rockchips.android.recline.app.DialogFragment.5 */
    class C02175 extends AnimatorListenerAdapter {
        final /* synthetic */ boolean val$notifyAnimationFinished;
        final /* synthetic */ View val$v;

        C02175(View val$v, boolean val$notifyAnimationFinished) {
            this.val$v = val$v;
            this.val$notifyAnimationFinished = val$notifyAnimationFinished;
        }

        public void onAnimationEnd(Animator animation) {
            this.val$v.setLayerType(0, null);
            if (this.val$notifyAnimationFinished) {
                DialogFragment.this.onIntroAnimationFinished();
            }
        }
    }

    public static class Action implements Parcelable {
        public static final Creator<Action> CREATOR;
        private int mCheckSetId;
        private boolean mChecked;
        private String mDescription;
        private int mDrawableResource;
        private boolean mEnabled;
        private boolean mHasNext;
        private Uri mIconUri;
        private boolean mInfoOnly;
        private Intent mIntent;
        private String mKey;
        private boolean mMultilineDescription;
        private String mResourcePackageName;
        private String mTitle;

        public interface Listener {
            void onActionClicked(Action action);
        }

        public interface OnFocusListener {
            void onActionFocused(Action action);
        }

        /* renamed from: com.rockchips.android.recline.app.DialogFragment.Action.1 */
        static class C02181 implements Creator<Action> {
            C02181() {
            }

            public Action createFromParcel(Parcel source) {
                boolean z = true;
                Builder checked = new Builder().key(source.readString()).title(source.readString()).description(source.readString()).intent((Intent) source.readParcelable(Intent.class.getClassLoader())).resourcePackageName(source.readString()).drawableResource(source.readInt()).iconUri((Uri) source.readParcelable(Uri.class.getClassLoader())).checked(source.readInt() != 0);
                if (source.readInt() == 0) {
                    z = false;
                }
                return checked.multilineDescription(z).checkSetId(source.readInt()).build();
            }

            public Action[] newArray(int size) {
                return new Action[size];
            }
        }

        public static class Builder {
            private int mCheckSetId;
            private boolean mChecked;
            private String mDescription;
            private int mDrawableResource;
            private boolean mEnabled;
            private boolean mHasNext;
            private Uri mIconUri;
            private boolean mInfoOnly;
            private Intent mIntent;
            private String mKey;
            private boolean mMultilineDescription;
            private String mResourcePackageName;
            private String mTitle;

            public Builder() {
                this.mDrawableResource = 0;
                this.mCheckSetId = 0;
                this.mEnabled = true;
            }

            public Action build() {
                Action action = new Action();
                action.mKey = this.mKey;
                action.mTitle = this.mTitle;
                action.mDescription = this.mDescription;
                action.mIntent = this.mIntent;
                action.mResourcePackageName = this.mResourcePackageName;
                action.mDrawableResource = this.mDrawableResource;
                action.mIconUri = this.mIconUri;
                action.mChecked = this.mChecked;
                action.mMultilineDescription = this.mMultilineDescription;
                action.mHasNext = this.mHasNext;
                action.mInfoOnly = this.mInfoOnly;
                action.mCheckSetId = this.mCheckSetId;
                action.mEnabled = this.mEnabled;
                return action;
            }

            public Builder key(String key) {
                this.mKey = key;
                return this;
            }

            public Builder title(String title) {
                this.mTitle = title;
                return this;
            }

            public Builder description(String description) {
                this.mDescription = description;
                return this;
            }

            public Builder intent(Intent intent) {
                this.mIntent = intent;
                return this;
            }

            public Builder resourcePackageName(String resourcePackageName) {
                this.mResourcePackageName = resourcePackageName;
                return this;
            }

            public Builder drawableResource(int drawableResource) {
                this.mDrawableResource = drawableResource;
                return this;
            }

            public Builder iconUri(Uri iconUri) {
                this.mIconUri = iconUri;
                return this;
            }

            public Builder checked(boolean checked) {
                this.mChecked = checked;
                return this;
            }

            public Builder multilineDescription(boolean multilineDescription) {
                this.mMultilineDescription = multilineDescription;
                return this;
            }

            public Builder checkSetId(int checkSetId) {
                this.mCheckSetId = checkSetId;
                return this;
            }
        }

        private Action() {
        }

        public String getKey() {
            return this.mKey;
        }

        public String getTitle() {
            return this.mTitle;
        }

        public String getDescription() {
            return this.mDescription;
        }

        public boolean isChecked() {
            return this.mChecked;
        }

        public Uri getIconUri() {
            return this.mIconUri;
        }

        public int getCheckSetId() {
            return this.mCheckSetId;
        }

        public boolean hasMultilineDescription() {
            return this.mMultilineDescription;
        }

        public boolean isEnabled() {
            return this.mEnabled;
        }

        public void setChecked(boolean checked) {
            this.mChecked = checked;
        }

        public boolean hasNext() {
            return this.mHasNext;
        }

        public boolean infoOnly() {
            return this.mInfoOnly;
        }

        public Drawable getIndicator(Context context) {
            if (this.mDrawableResource == 0) {
                return null;
            }
            if (this.mResourcePackageName == null) {
                return context.getResources().getDrawable(this.mDrawableResource);
            }
            Drawable icon = null;
            try {
                icon = context.createPackageContext(this.mResourcePackageName, 0).getResources().getDrawable(this.mDrawableResource);
            } catch (NameNotFoundException e) {
                if (Log.isLoggable("Action", 5)) {
                    Log.w("Action", "No icon for this action.");
                }
            } catch (NotFoundException e2) {
                if (Log.isLoggable("Action", 5)) {
                    Log.w("Action", "No icon for this action.");
                }
            }
            return icon;
        }

        static {
            CREATOR = new C02181();
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            int i;
            int i2 = 1;
            dest.writeString(this.mKey);
            dest.writeString(this.mTitle);
            dest.writeString(this.mDescription);
            dest.writeParcelable(this.mIntent, flags);
            dest.writeString(this.mResourcePackageName);
            dest.writeInt(this.mDrawableResource);
            dest.writeParcelable(this.mIconUri, flags);
            if (this.mChecked) {
                i = 1;
            } else {
                i = 0;
            }
            dest.writeInt(i);
            if (!this.mMultilineDescription) {
                i2 = 0;
            }
            dest.writeInt(i2);
            dest.writeInt(this.mCheckSetId);
        }
    }

    public static class Builder {
        private ArrayList<Action> mActions;
        private String mContentBreadcrumb;
        private String mContentDescription;
        private String mContentTitle;
        private boolean mEntryTransitionEnabled;
        private int mIconBackgroundColor;
        private Bitmap mIconBitmap;
        private int mIconPadding;
        private int mIconResourceId;
        private Uri mIconUri;
        private String mName;
        private int mSelectedIndex;

        public Builder() {
            this.mIconBackgroundColor = 0;
            this.mEntryTransitionEnabled = true;
        }

        public DialogFragment build() {
            DialogFragment fragment = new DialogFragment();
            Bundle args = new Bundle();
            args.putString("title", this.mContentTitle);
            args.putString("breadcrumb", this.mContentBreadcrumb);
            args.putString("description", this.mContentDescription);
            args.putInt("iconResourceId", this.mIconResourceId);
            args.putParcelable("iconUri", this.mIconUri);
            args.putParcelable("iconBitmap", this.mIconBitmap);
            args.putInt("iconBackground", this.mIconBackgroundColor);
            args.putInt("iconPadding", this.mIconPadding);
            args.putParcelableArrayList("actions", this.mActions);
            args.putString("name", this.mName);
            args.putInt("selectedIndex", this.mSelectedIndex);
            args.putBoolean("entryTransitionEnabled", this.mEntryTransitionEnabled);
            fragment.setArguments(args);
            return fragment;
        }

        public Builder title(String title) {
            this.mContentTitle = title;
            return this;
        }

        public Builder breadcrumb(String breadcrumb) {
            this.mContentBreadcrumb = breadcrumb;
            return this;
        }

        public Builder description(String description) {
            this.mContentDescription = description;
            return this;
        }

        public Builder iconResourceId(int iconResourceId) {
            this.mIconResourceId = iconResourceId;
            return this;
        }

        public Builder iconBackgroundColor(int iconBackgroundColor) {
            this.mIconBackgroundColor = iconBackgroundColor;
            return this;
        }

        public Builder actions(ArrayList<Action> actions) {
            this.mActions = actions;
            return this;
        }

        public Builder selectedIndex(int selectedIndex) {
            this.mSelectedIndex = selectedIndex;
            return this;
        }
    }

    private static class SelectorAnimator extends OnScrollListener {
        private final int mAnimationDuration;
        private volatile boolean mFadedOut;
        private final ViewGroup mParentView;
        private final View mSelectorView;

        private class Listener implements AnimatorListener {
            private boolean mCanceled;
            private boolean mFadingOut;

            public Listener(boolean fadingOut) {
                this.mFadingOut = fadingOut;
            }

            public void onAnimationStart(Animator animation) {
                if (!this.mFadingOut) {
                    SelectorAnimator.this.mFadedOut = false;
                }
            }

            public void onAnimationEnd(Animator animation) {
                if (!this.mCanceled && this.mFadingOut) {
                    SelectorAnimator.this.mFadedOut = true;
                }
            }

            public void onAnimationCancel(Animator animation) {
                this.mCanceled = true;
            }

            public void onAnimationRepeat(Animator animation) {
            }
        }

        SelectorAnimator(View selectorView, ViewGroup parentView) {
            this.mFadedOut = true;
            this.mSelectorView = selectorView;
            this.mParentView = parentView;
            this.mAnimationDuration = selectorView.getContext().getResources().getInteger(R.integer.lb_dialog_animation_duration);
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == 0) {
                int selectorHeight = this.mSelectorView.getHeight();
                if (selectorHeight == 0) {
                    LayoutParams lp = this.mSelectorView.getLayoutParams();
                    selectorHeight = this.mSelectorView.getContext().getResources().getDimensionPixelSize(R.dimen.lb_action_fragment_selector_min_height);
                    lp.height = selectorHeight;
                    this.mSelectorView.setLayoutParams(lp);
                }
                View focusedChild = this.mParentView.getFocusedChild();
                if (focusedChild != null) {
                    float scaleY = ((float) focusedChild.getHeight()) / ((float) selectorHeight);
                    ViewPropertyAnimator animation = this.mSelectorView.animate().alpha(1.0f).setListener(new Listener(false)).setDuration((long) this.mAnimationDuration).setInterpolator(new DecelerateInterpolator(2.0f));
                    if (this.mFadedOut) {
                        this.mSelectorView.setScaleY(scaleY);
                    } else {
                        animation.scaleY(scaleY);
                    }
                    animation.start();
                    return;
                }
                return;
            }
            this.mSelectorView.animate().alpha(0.0f).setDuration((long) this.mAnimationDuration).setInterpolator(new DecelerateInterpolator(2.0f)).setListener(new Listener(true)).start();
        }
    }

    private static class UntargetableAnimatorSet extends Animator {
        private final AnimatorSet mAnimatorSet;

        UntargetableAnimatorSet(AnimatorSet animatorSet) {
            this.mAnimatorSet = animatorSet;
        }

        public void addListener(AnimatorListener listener) {
            this.mAnimatorSet.addListener(listener);
        }

        public void cancel() {
            this.mAnimatorSet.cancel();
        }

        public Animator clone() {
            return this.mAnimatorSet.clone();
        }

        public void end() {
            this.mAnimatorSet.end();
        }

        public long getDuration() {
            return this.mAnimatorSet.getDuration();
        }

        public ArrayList<AnimatorListener> getListeners() {
            return this.mAnimatorSet.getListeners();
        }

        public long getStartDelay() {
            return this.mAnimatorSet.getStartDelay();
        }

        public boolean isRunning() {
            return this.mAnimatorSet.isRunning();
        }

        public boolean isStarted() {
            return this.mAnimatorSet.isStarted();
        }

        public void removeAllListeners() {
            this.mAnimatorSet.removeAllListeners();
        }

        public void removeListener(AnimatorListener listener) {
            this.mAnimatorSet.removeListener(listener);
        }

        public Animator setDuration(long duration) {
            return this.mAnimatorSet.setDuration(duration);
        }

        public void setInterpolator(TimeInterpolator value) {
            this.mAnimatorSet.setInterpolator(value);
        }

        public void setStartDelay(long startDelay) {
            this.mAnimatorSet.setStartDelay(startDelay);
        }

        public void setTarget(Object target) {
        }

        public void setupEndValues() {
            this.mAnimatorSet.setupEndValues();
        }

        public void setupStartValues() {
            this.mAnimatorSet.setupStartValues();
        }

        public void start() {
            this.mAnimatorSet.start();
        }
    }

    public DialogFragment() {
        this.mIconBackgroundColor = 0;
        this.mSelectedIndex = -1;
    }

    public static void add(FragmentManager fm, DialogFragment f) {
        add(fm, f, 16908290);
    }

    public static void add(FragmentManager fm, DialogFragment f, int id) {
        boolean hasDialog = getCurrentDialogFragment(fm) != null;
        FragmentTransaction ft = fm.beginTransaction();
        if (hasDialog) {
            ft.setCustomAnimations(1, 2, 3, 4);
            ft.addToBackStack(null);
        }
        ft.replace(id, f, "leanBackDialogFragment").commit();
    }

    public static DialogFragment getCurrentDialogFragment(FragmentManager fm) {
        Fragment f = fm.findFragmentByTag("leanBackDialogFragment");
        if (f instanceof DialogFragment) {
            return (DialogFragment) f;
        }
        return null;
    }

    public void onCreate(Bundle savedInstanceState) {
        Log.v("DialogFragment", "onCreate");
        super.onCreate(savedInstanceState);
        Bundle state = savedInstanceState != null ? savedInstanceState : getArguments();
        if (this.mTitle == null) {
            this.mTitle = state.getString("title");
        }
        if (this.mBreadcrumb == null) {
            this.mBreadcrumb = state.getString("breadcrumb");
        }
        if (this.mDescription == null) {
            this.mDescription = state.getString("description");
        }
        if (this.mIconResourceId == 0) {
            this.mIconResourceId = state.getInt("iconResourceId", 0);
        }
        if (this.mIconUri == null) {
            this.mIconUri = (Uri) state.getParcelable("iconUri");
        }
        if (this.mIconBitmap == null) {
            this.mIconBitmap = (Bitmap) state.getParcelable("iconBitmap");
        }
        if (this.mIconBackgroundColor == 0) {
            this.mIconBackgroundColor = state.getInt("iconBackground", 0);
        }
        if (this.mIconPadding == 0) {
            this.mIconPadding = state.getInt("iconPadding", 0);
        }
        if (this.mActions == null) {
            this.mActions = state.getParcelableArrayList("actions");
        }
        if (this.mName == null) {
            this.mName = state.getString("name");
        }
        if (this.mSelectedIndex == -1) {
            this.mSelectedIndex = state.getInt("selectedIndex", -1);
        }
        this.mEntryTransitionEnabled = state.getBoolean("entryTransitionEnabled", true);
        this.mEntryTransitionPerformed = state.getBoolean("entryTransitionPerformed", false);
        if (!this.mBrandColorSet) {
            this.mBrandColorSet = state.getBoolean("brandColorSet", false);
            if (this.mBrandColorSet) {
                this.mBrandColor = state.getInt("brandColor", 0);
            }
        }
        if (!this.mBackgroundColorSet) {
            this.mBackgroundColorSet = state.getBoolean("backgroundColorSet", false);
            if (this.mBackgroundColorSet) {
                this.mBackgroundColor = state.getInt("backgroundColor", 0);
            }
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.lb_dialog_fragment, container, false);
        View contentContainer = v.findViewById(R.id.content_fragment);
        View content = inflater.inflate(getContentLayoutId(), container, false);
        ((ViewGroup) contentContainer).addView(content);
        setContentView(content);
        v.setTag(R.id.content_fragment, content);
        this.mActionContainer = v.findViewById(R.id.action_fragment);
        if (this.mBrandColorSet) {
            this.mActionContainer.setBackgroundColor(this.mBrandColor);
        }
        View action = inflater.inflate(R.layout.lb_dialog_action_list, container, false);
        ((ViewGroup) this.mActionContainer).addView(action);
        setActionView(action);
        v.setTag(R.id.action_fragment, action);
        View selectorView = action.findViewById(R.id.selector);
        if (selectorView != null) {
            this.mListView.getViewTreeObserver().addOnGlobalFocusChangeListener(new C02121(selectorView));
        }
        if (!isEntryTransitionEnabled()) {
            v.setBackground(new ColorDrawable(getBackgroundColor(v.getContext())));
        }
        return v;
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("title", this.mTitle);
        outState.putString("breadcrumb", this.mBreadcrumb);
        outState.putString("description", this.mDescription);
        outState.putInt("iconResourceId", this.mIconResourceId);
        outState.putParcelable("iconUri", this.mIconUri);
        outState.putParcelable("iconBitmap", this.mIconBitmap);
        outState.putInt("iconBackground", this.mIconBackgroundColor);
        outState.putInt("iconPadding", this.mIconPadding);
        outState.putParcelableArrayList("actions", this.mActions);
        outState.putInt("selectedIndex", this.mListView != null ? getSelectedItemPosition() : this.mSelectedIndex);
        outState.putString("name", this.mName);
        outState.putBoolean("entryTransitionPerformed", this.mEntryTransitionPerformed);
        outState.putBoolean("brandColorSet", this.mBrandColorSet);
        outState.putInt("brandColor", this.mBrandColor);
        outState.putBoolean("backgroundColorSet", this.mBackgroundColorSet);
        outState.putInt("backgroundColor", this.mBackgroundColor);
    }

    public void onStart() {
        super.onStart();
        if (isEntryTransitionEnabled() && !this.mEntryTransitionPerformed) {
            this.mEntryTransitionPerformed = true;
            performEntryTransition();
        }
    }

    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        View dialogView = getView();
        View contentView = (View) dialogView.getTag(R.id.content_fragment);
        View actionView = (View) dialogView.getTag(R.id.action_fragment);
        View actionContainerView = dialogView.findViewById(R.id.action_fragment);
        View listView = (View) actionView.getTag(R.id.list);
        View selectorView = (View) actionView.getTag(R.id.selector);
        ArrayList<Animator> animators = new ArrayList();
        addContentViewAnimations(contentView, nextAnim, animators);
        switch (nextAnim) {
            case android.support.v7.recyclerview.R.styleable.RecyclerView_android_descendantFocusability /*1*/:
                animators.add(createSlideInFromEndAnimator(listView));
                animators.add(createSlideInFromEndAnimator(selectorView));
                break;
            case android.support.v7.recyclerview.R.styleable.RecyclerView_layoutManager /*2*/:
                animators.add(createSlideOutToStartAnimator(listView));
                animators.add(createSlideOutToStartAnimator(selectorView));
                animators.add(createFadeOutAnimator(actionContainerView));
                break;
            case android.support.v7.preference.R.styleable.Preference_android_layout /*3*/:
                animators.add(createSlideInFromStartAnimator(listView));
                animators.add(createSlideInFromStartAnimator(selectorView));
                break;
            case android.support.v7.preference.R.styleable.Preference_android_title /*4*/:
                animators.add(createSlideOutToEndAnimator(listView));
                animators.add(createSlideOutToEndAnimator(selectorView));
                animators.add(createFadeOutAnimator(actionContainerView));
                break;
            default:
                return super.onCreateAnimator(transit, enter, nextAnim);
        }
        this.mEntryTransitionPerformed = true;
        return createDummyAnimator(dialogView, animators);
    }

    protected boolean isEntryTransitionEnabled() {
        return this.mEntryTransitionEnabled;
    }

    protected void addContentViewAnimations(View contentView, int nextAnim, ArrayList<Animator> animators) {
        View titleView = (View) contentView.getTag(R.id.title);
        View breadcrumbView = (View) contentView.getTag(R.id.breadcrumb);
        View descriptionView = (View) contentView.getTag(R.id.description);
        View iconView = (View) contentView.getTag(R.id.icon);
        switch (nextAnim) {
            case android.support.v7.recyclerview.R.styleable.RecyclerView_android_descendantFocusability /*1*/:
                animators.add(createSlideInFromEndAnimator(titleView));
                animators.add(createSlideInFromEndAnimator(breadcrumbView));
                animators.add(createSlideInFromEndAnimator(descriptionView));
                animators.add(createSlideInFromEndAnimator(iconView));
            case android.support.v7.recyclerview.R.styleable.RecyclerView_layoutManager /*2*/:
                animators.add(createSlideOutToStartAnimator(titleView));
                animators.add(createSlideOutToStartAnimator(breadcrumbView));
                animators.add(createSlideOutToStartAnimator(descriptionView));
                animators.add(createSlideOutToStartAnimator(iconView));
            case android.support.v7.preference.R.styleable.Preference_android_layout /*3*/:
                animators.add(createSlideInFromStartAnimator(titleView));
                animators.add(createSlideInFromStartAnimator(breadcrumbView));
                animators.add(createSlideInFromStartAnimator(descriptionView));
                animators.add(createSlideInFromStartAnimator(iconView));
            case android.support.v7.preference.R.styleable.Preference_android_title /*4*/:
                animators.add(createSlideOutToEndAnimator(titleView));
                animators.add(createSlideOutToEndAnimator(breadcrumbView));
                animators.add(createSlideOutToEndAnimator(descriptionView));
                animators.add(createSlideOutToEndAnimator(iconView));
            default:
        }
    }

    public ArrayList<Action> getActions() {
        return this.mActions;
    }

    public void setActions(ArrayList<Action> actions) {
        this.mActions = actions;
        if (this.mAdapter != null) {
            this.mAdapter.setActions(this.mActions);
        }
    }

    public int getSelectedItemPosition() {
        return this.mListView.indexOfChild(this.mListView.getFocusedChild());
    }

    public void onIntroAnimationFinished() {
        this.mIntroAnimationInProgress = false;
        View focusedChild = this.mListView.getFocusedChild();
        if (focusedChild != null) {
            View actionView = (View) getView().getTag(R.id.action_fragment);
            int height = focusedChild.getHeight();
            View selectorView = actionView.findViewById(R.id.selector);
            LayoutParams lp = selectorView.getLayoutParams();
            lp.height = height;
            selectorView.setLayoutParams(lp);
            selectorView.setAlpha(1.0f);
        }
    }

    public boolean isIntroAnimationInProgress() {
        return this.mIntroAnimationInProgress;
    }

    protected int getContentLayoutId() {
        return R.layout.lb_dialog_content;
    }

    protected void setContentView(View content) {
        TextView titleView = (TextView) content.findViewById(R.id.title);
        TextView breadcrumbView = (TextView) content.findViewById(R.id.breadcrumb);
        TextView descriptionView = (TextView) content.findViewById(R.id.description);
        titleView.setText(this.mTitle);
        breadcrumbView.setText(this.mBreadcrumb);
        descriptionView.setText(this.mDescription);
        ImageView iconImageView = (ImageView) content.findViewById(R.id.icon);
        if (this.mIconBackgroundColor != 0) {
            iconImageView.setBackgroundColor(this.mIconBackgroundColor);
        }
        iconImageView.setPadding(this.mIconPadding, this.mIconPadding, this.mIconPadding, this.mIconPadding);
        if (this.mIconResourceId != 0) {
            iconImageView.setImageResource(this.mIconResourceId);
            updateViewSize(iconImageView);
        } else if (this.mIconBitmap != null) {
            iconImageView.setImageBitmap(this.mIconBitmap);
            updateViewSize(iconImageView);
        } else if (this.mIconUri != null) {
            iconImageView.setVisibility(4);
        } else {
            iconImageView.setVisibility(8);
        }
        content.setTag(R.id.title, titleView);
        content.setTag(R.id.breadcrumb, breadcrumbView);
        content.setTag(R.id.description, descriptionView);
        content.setTag(R.id.icon, iconImageView);
    }

    private int getBackgroundColor(Context context) {
        if (this.mBackgroundColorSet) {
            return this.mBackgroundColor;
        }
        return context.getResources().getColor(R.color.lb_dialog_activity_background);
    }

    private void setActionView(View action) {
        int firstCheckedAction;
        this.mAdapter = new DialogActionAdapter(new C02132(), new C02143(), this.mActions);
        if (action instanceof VerticalGridView) {
            this.mListView = (VerticalGridView) action;
        } else {
            this.mListView = (VerticalGridView) action.findViewById(R.id.list);
            if (this.mListView == null) {
                throw new IllegalStateException("No ListView exists.");
            }
            this.mListView.setWindowAlignmentOffset(0);
            this.mListView.setWindowAlignmentOffsetPercent(50.0f);
            this.mListView.setWindowAlignment(0);
            View selectorView = action.findViewById(R.id.selector);
            if (selectorView != null) {
                this.mListView.setOnScrollListener(new SelectorAnimator(selectorView, this.mListView));
            }
        }
        this.mListView.requestFocusFromTouch();
        this.mListView.setAdapter(this.mAdapter);
        VerticalGridView verticalGridView = this.mListView;
        if (this.mSelectedIndex < 0 || this.mSelectedIndex >= this.mActions.size()) {
            firstCheckedAction = getFirstCheckedAction();
        } else {
            firstCheckedAction = this.mSelectedIndex;
        }
        verticalGridView.setSelectedPosition(firstCheckedAction);
        action.setTag(R.id.list, this.mListView);
        action.setTag(R.id.selector, action.findViewById(R.id.selector));
    }

    private int getFirstCheckedAction() {
        int size = this.mActions.size();
        for (int i = 0; i < size; i++) {
            if (((Action) this.mActions.get(i)).isChecked()) {
                return i;
            }
        }
        return 0;
    }

    private void updateViewSize(ImageView iconView) {
        int intrinsicWidth = iconView.getDrawable().getIntrinsicWidth();
        LayoutParams lp = iconView.getLayoutParams();
        if (intrinsicWidth > 0) {
            lp.height = (lp.width * iconView.getDrawable().getIntrinsicHeight()) / intrinsicWidth;
        } else {
            lp.height = lp.width;
        }
    }

    private void performEntryTransition() {
        View dialogView = getView();
        View contentView = (View) dialogView.getTag(R.id.content_fragment);
        View actionContainerView = dialogView.findViewById(R.id.action_fragment);
        this.mIntroAnimationInProgress = true;
        getActivity().overridePendingTransition(0, R.anim.lb_dialog_fade_out);
        ColorDrawable bgDrawable = new ColorDrawable(getBackgroundColor(getActivity()));
        bgDrawable.setAlpha(0);
        dialogView.setBackground(bgDrawable);
        dialogView.setVisibility(4);
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new C02164(contentView, dialogView, bgDrawable, actionContainerView));
    }

    protected void animateInContentView(View contentView) {
        boolean isRtl = true;
        if (ViewCompat.getLayoutDirection(contentView) != 1) {
            isRtl = false;
        }
        int startDist = isRtl ? 120 : -120;
        prepareAndAnimateView((View) contentView.getTag(R.id.title), (float) startDist, false);
        prepareAndAnimateView((View) contentView.getTag(R.id.breadcrumb), (float) startDist, false);
        prepareAndAnimateView((View) contentView.getTag(R.id.description), (float) startDist, false);
        prepareAndAnimateView((View) contentView.getTag(R.id.icon), (float) startDist, false);
    }

    protected void animateInActionView(View actionView) {
        prepareAndAnimateView(actionView, (float) (ViewCompat.getLayoutDirection(actionView) == 1 ? -actionView.getMeasuredWidth() : actionView.getMeasuredWidth()), true);
    }

    protected void prepareAndAnimateView(View v, float initTransX, boolean notifyAnimationFinished) {
        v.setLayerType(2, null);
        v.buildLayer();
        v.setAlpha(0.0f);
        v.setTranslationX(initTransX);
        v.animate().alpha(1.0f).translationX(0.0f).setDuration(250).setStartDelay(120);
        v.animate().setInterpolator(new DecelerateInterpolator(1.0f));
        v.animate().setListener(new C02175(v, notifyAnimationFinished));
        v.animate().start();
    }

    private Animator createDummyAnimator(View v, ArrayList<Animator> animators) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animators);
        return new UntargetableAnimatorSet(animatorSet);
    }

    protected Animator createSlideOutToStartAnimator(View v) {
        return createTranslateAlphaAnimator(v, R.anim.lb_dialog_slide_out_to_start);
    }

    protected Animator createSlideInFromEndAnimator(View v) {
        return createTranslateAlphaAnimator(v, R.anim.lb_dialog_slide_in_from_end);
    }

    protected Animator createSlideInFromStartAnimator(View v) {
        return createTranslateAlphaAnimator(v, R.anim.lb_dialog_slide_in_from_start);
    }

    protected Animator createSlideOutToEndAnimator(View v) {
        return createTranslateAlphaAnimator(v, R.anim.lb_dialog_slide_out_to_end);
    }

    private Animator createFadeOutAnimator(View v) {
        return createAlphaAnimator(v, 1.0f, 0.0f);
    }

    private Animator createTranslateAlphaAnimator(View v, int animatorResourceId) {
        Animator animator = AnimatorInflater.loadAnimator(v.getContext(), animatorResourceId);
        animator.setTarget(v);
        return animator;
    }

    private Animator createAlphaAnimator(View v, float fromAlpha, float toAlpha) {
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(v, "alpha", new float[]{fromAlpha, toAlpha});
        alphaAnimator.setDuration((long) getResources().getInteger(17694722));
        return alphaAnimator;
    }
}
