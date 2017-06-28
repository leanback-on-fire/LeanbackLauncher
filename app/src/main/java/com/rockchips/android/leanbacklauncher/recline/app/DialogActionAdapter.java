package com.rockchips.android.leanbacklauncher.recline.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.rockchips.android.leanbacklauncher.R;

import java.util.ArrayList;
import java.util.List;

class DialogActionAdapter extends Adapter {
    private final ActionOnFocusAnimator mActionOnFocusAnimator;
    private final ActionOnKeyPressAnimator mActionOnKeyPressAnimator;
    private final List<DialogFragment.Action> mActions;
    private LayoutInflater mInflater;
    private DialogFragment.Action.Listener mListener;
    private final OnClickListener mOnClickListener;

    /* renamed from: com.rockchips.android.recline.app.DialogActionAdapter.1 */
    class C02091 implements OnClickListener {
        C02091() {
        }

        public void onClick(View v) {
            if (v != null && v.getWindowToken() != null && DialogActionAdapter.this.mListener != null) {
                DialogActionAdapter.this.mListener.onActionClicked(((ActionViewHolder) v.getTag(R.id.action_title)).getAction());
            }
        }
    }

    private static class ActionOnFocusAnimator implements OnFocusChangeListener {
        private int mAnimationDuration;
        private float mDisabledChevronAlpha;
        private float mDisabledDescriptionAlpha;
        private float mDisabledTitleAlpha;
        private DialogFragment.Action.OnFocusListener mOnFocusListener;
        private boolean mResourcesSet;
        private float mSelectedChevronAlpha;
        private float mSelectedDescriptionAlpha;
        private float mSelectedTitleAlpha;
        private View mSelectedView;
        private float mUnselectedAlpha;
        private float mUnselectedDescriptionAlpha;

        ActionOnFocusAnimator(DialogFragment.Action.OnFocusListener onFocusListener) {
            this.mOnFocusListener = onFocusListener;
        }

        public void unFocus(View v) {
            if (v == null) {
                v = this.mSelectedView;
            }
            changeFocus(v, false, false);
        }

        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                this.mSelectedView = v;
                changeFocus(v, true, true);
                if (this.mOnFocusListener != null) {
                    this.mOnFocusListener.onActionFocused(((ActionViewHolder) v.getTag(R.id.action_title)).getAction());
                    return;
                }
                return;
            }
            if (this.mSelectedView == v) {
                this.mSelectedView = null;
            }
            changeFocus(v, false, true);
        }

        private void changeFocus(View v, boolean hasFocus, boolean shouldAnimate) {
            if (v != null) {
                if (!this.mResourcesSet) {
                    this.mResourcesSet = true;
                    Resources res = v.getContext().getResources();
                    this.mAnimationDuration = res.getInteger(R.integer.lb_dialog_animation_duration);
                    this.mUnselectedAlpha = Float.valueOf(res.getString(R.string.lb_dialog_list_item_unselected_text_alpha)).floatValue();
                    this.mSelectedTitleAlpha = Float.valueOf(res.getString(R.string.lb_dialog_list_item_selected_title_text_alpha)).floatValue();
                    this.mDisabledTitleAlpha = Float.valueOf(res.getString(R.string.lb_dialog_list_item_disabled_title_text_alpha)).floatValue();
                    this.mSelectedDescriptionAlpha = Float.valueOf(res.getString(R.string.lb_dialog_list_item_selected_description_text_alpha)).floatValue();
                    this.mUnselectedDescriptionAlpha = Float.valueOf(res.getString(R.string.lb_dialog_list_item_unselected_description_text_alpha)).floatValue();
                    this.mDisabledDescriptionAlpha = Float.valueOf(res.getString(R.string.lb_dialog_list_item_disabled_description_text_alpha)).floatValue();
                    this.mSelectedChevronAlpha = Float.valueOf(res.getString(R.string.lb_dialog_list_item_selected_chevron_background_alpha)).floatValue();
                    this.mDisabledChevronAlpha = Float.valueOf(res.getString(R.string.lb_dialog_list_item_disabled_chevron_background_alpha)).floatValue();
                }
                DialogFragment.Action action = ((ActionViewHolder) v.getTag(R.id.action_title)).getAction();
                float titleAlpha = (!action.isEnabled() || action.infoOnly()) ? this.mDisabledTitleAlpha : hasFocus ? this.mSelectedTitleAlpha : this.mUnselectedAlpha;
                float descriptionAlpha = (!hasFocus || action.infoOnly()) ? this.mUnselectedDescriptionAlpha : action.isEnabled() ? this.mSelectedDescriptionAlpha : this.mDisabledDescriptionAlpha;
                float chevronAlpha = (!action.hasNext() || action.infoOnly()) ? 0.0f : action.isEnabled() ? this.mSelectedChevronAlpha : this.mDisabledChevronAlpha;
                setAlpha((TextView) v.findViewById(R.id.action_title), shouldAnimate, titleAlpha);
                setAlpha((TextView) v.findViewById(R.id.action_description), shouldAnimate, descriptionAlpha);
                setAlpha((ImageView) v.findViewById(R.id.action_checkmark), shouldAnimate, titleAlpha);
                setAlpha((ImageView) v.findViewById(R.id.action_icon), shouldAnimate, titleAlpha);
                setAlpha((ImageView) v.findViewById(R.id.action_next_chevron), shouldAnimate, chevronAlpha);
            }
        }

        private void setAlpha(View view, boolean shouldAnimate, float alpha) {
            if (shouldAnimate) {
                view.animate().alpha(alpha).setDuration((long) this.mAnimationDuration).setInterpolator(new DecelerateInterpolator(2.0f)).start();
            } else {
                view.setAlpha(alpha);
            }
        }
    }

    private static class ActionOnKeyPressAnimator implements OnKeyListener {
        private final List<DialogFragment.Action> mActions;
        private boolean mKeyPressed;
        private DialogFragment.Action.Listener mListener;

        /* renamed from: com.rockchips.android.recline.app.DialogActionAdapter.ActionOnKeyPressAnimator.1 */
        class C02101 extends AnimatorListenerAdapter {
            final /* synthetic */ DialogFragment.Action val$action;
            final /* synthetic */ boolean val$pressed;
            final /* synthetic */ View val$v;

            C02101(View val$v, boolean val$pressed, DialogFragment.Action val$action) {
                this.val$v = val$v;
                this.val$pressed = val$pressed;
                this.val$action = val$action;
            }

            public void onAnimationEnd(Animator animation) {
                this.val$v.setLayerType(0, null);
                if (!this.val$pressed && ActionOnKeyPressAnimator.this.mListener != null) {
                    ActionOnKeyPressAnimator.this.mListener.onActionClicked(this.val$action);
                }
            }
        }

        /* renamed from: com.rockchips.android.recline.app.DialogActionAdapter.ActionOnKeyPressAnimator.2 */
        class C02112 extends AnimatorListenerAdapter {
            final /* synthetic */ View val$checkView;

            C02112(View val$checkView) {
                this.val$checkView = val$checkView;
            }

            public void onAnimationEnd(Animator animation) {
                this.val$checkView.setVisibility(4);
            }
        }

        public ActionOnKeyPressAnimator(DialogFragment.Action.Listener listener, List<DialogFragment.Action> actions) {
            this.mKeyPressed = false;
            this.mListener = listener;
            this.mActions = actions;
        }

        private void playSound(Context context, int soundEffect) {
            ((AudioManager) context.getSystemService("audio")).playSoundEffect(soundEffect);
        }

        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (v == null) {
                return false;
            }
            boolean handled = false;
            DialogFragment.Action action = ((ActionViewHolder) v.getTag(R.id.action_title)).getAction();
            switch (keyCode) {
                case android.support.v7.preference.R.styleable.Preference_defaultValue /*23*/:
                case 66:
                case 99:
                case 100:
                case 160:
                    if (action.isEnabled() && !action.infoOnly()) {
                        switch (event.getAction()) {
                            case android.support.v7.preference.R.styleable.Preference_android_icon /*0*/:
                                if (!this.mKeyPressed) {
                                    this.mKeyPressed = true;
                                    if (v.isSoundEffectsEnabled()) {
                                        playSound(v.getContext(), 0);
                                    }
                                    prepareAndAnimateView(v, 1.0f, 0.2f, 100, 0, null, this.mKeyPressed);
                                    handled = true;
                                    break;
                                }
                                break;
                            case android.support.v7.recyclerview.R.styleable.RecyclerView_android_descendantFocusability /*1*/:
                                if (this.mKeyPressed) {
                                    this.mKeyPressed = false;
                                    prepareAndAnimateView(v, 0.2f, 1.0f, 100, 0, null, this.mKeyPressed);
                                    handled = true;
                                    break;
                                }
                                break;
                            default:
                                break;
                        }
                    }
                    if (!v.isSoundEffectsEnabled() || event.getAction() == 0) {
                    }
                    return true;
            }
            return handled;
        }

        private void prepareAndAnimateView(View v, float initAlpha, float destAlpha, int duration, int delay, Interpolator interpolator, boolean pressed) {
            if (v != null && v.getWindowToken() != null) {
                DialogFragment.Action action = ((ActionViewHolder) v.getTag(R.id.action_title)).getAction();
                if (!pressed) {
                    fadeCheckmarks(v, action, duration, delay, interpolator);
                }
                v.setAlpha(initAlpha);
                v.setLayerType(2, null);
                v.buildLayer();
                v.animate().alpha(destAlpha).setDuration((long) duration).setStartDelay((long) delay);
                if (interpolator != null) {
                    v.animate().setInterpolator(interpolator);
                }
                v.animate().setListener(new C02101(v, pressed, action));
                v.animate().start();
            }
        }

        private void fadeCheckmarks(View v, DialogFragment.Action action, int duration, int delay, Interpolator interpolator) {
            int actionCheckSetId = action.getCheckSetId();
            if (actionCheckSetId != 0) {
                View checkView;
                ViewGroup parent = (ViewGroup) v.getTag(R.layout.lb_dialog_action_list_item);
                int size = this.mActions.size();
                for (int i = 0; i < size; i++) {
                    DialogFragment.Action a = (DialogFragment.Action) this.mActions.get(i);
                    if (a != action && a.getCheckSetId() == actionCheckSetId && a.isChecked()) {
                        a.setChecked(false);
                        View viewToAnimateOut = parent.getChildAt(i);
                        if (viewToAnimateOut != null) {
                            checkView = viewToAnimateOut.findViewById(R.id.action_checkmark);
                            checkView.animate().alpha(0.0f).setDuration((long) duration).setStartDelay((long) delay);
                            if (interpolator != null) {
                                checkView.animate().setInterpolator(interpolator);
                            }
                            checkView.animate().setListener(new C02112(checkView));
                        }
                    }
                }
                if (!action.isChecked()) {
                    action.setChecked(true);
                    checkView = v.findViewById(R.id.action_checkmark);
                    checkView.setVisibility(0);
                    checkView.setAlpha(0.0f);
                    checkView.animate().alpha(1.0f).setDuration((long) duration).setStartDelay((long) delay);
                    if (interpolator != null) {
                        checkView.animate().setInterpolator(interpolator);
                    }
                    checkView.animate().setListener(null);
                }
            }
        }
    }

    private static class ActionViewHolder extends ViewHolder {
        private DialogFragment.Action mAction;
        private final ActionOnFocusAnimator mActionOnFocusAnimator;
        private final ActionOnKeyPressAnimator mActionOnKeyPressAnimator;
        private final OnClickListener mViewOnClickListener;

        public ActionViewHolder(View v, ActionOnKeyPressAnimator actionOnKeyPressAnimator, ActionOnFocusAnimator actionOnFocusAnimator, OnClickListener viewOnClickListener) {
            super(v);
            this.mActionOnKeyPressAnimator = actionOnKeyPressAnimator;
            this.mActionOnFocusAnimator = actionOnFocusAnimator;
            this.mViewOnClickListener = viewOnClickListener;
        }

        public DialogFragment.Action getAction() {
            return this.mAction;
        }

        public void init(DialogFragment.Action action) {
            int i;
            int i2 = 0;
            this.mAction = action;
            TextView title = (TextView) this.itemView.findViewById(R.id.action_title);
            TextView description = (TextView) this.itemView.findViewById(R.id.action_description);
            description.setText(action.getDescription());
            if (TextUtils.isEmpty(action.getDescription())) {
                i = 8;
            } else {
                i = 0;
            }
            description.setVisibility(i);
            title.setText(action.getTitle());
            ImageView checkmarkView = (ImageView) this.itemView.findViewById(R.id.action_checkmark);
            if (action.isChecked()) {
                i = 0;
            } else {
                i = 4;
            }
            checkmarkView.setVisibility(i);
            ImageView indicatorView = (ImageView) this.itemView.findViewById(R.id.action_icon);
            View content = this.itemView.findViewById(R.id.action_content);
            LayoutParams contentLp = content.getLayoutParams();
            if (setIndicator(indicatorView, action)) {
                contentLp.width = this.itemView.getContext().getResources().getDimensionPixelSize(R.dimen.lb_action_text_width);
            } else {
                contentLp.width = this.itemView.getContext().getResources().getDimensionPixelSize(R.dimen.lb_action_text_width_no_icon);
            }
            content.setLayoutParams(contentLp);
            ImageView chevronView = (ImageView) this.itemView.findViewById(R.id.action_next_chevron);
            if (!action.hasNext()) {
                i2 = 4;
            }
            chevronView.setVisibility(i2);
            Resources res = this.itemView.getContext().getResources();
            if (action.hasMultilineDescription()) {
                title.setMaxLines(res.getInteger(R.integer.lb_dialog_action_title_max_lines));
                description.setMaxHeight(getDescriptionMaxHeight(this.itemView.getContext(), title));
            } else {
                title.setMaxLines(res.getInteger(R.integer.lb_dialog_action_title_min_lines));
                description.setMaxLines(res.getInteger(R.integer.lb_dialog_action_description_min_lines));
            }
            this.itemView.setTag(R.id.action_title, this);
            this.itemView.setOnKeyListener(this.mActionOnKeyPressAnimator);
            this.itemView.setOnClickListener(this.mViewOnClickListener);
            this.itemView.setOnFocusChangeListener(this.mActionOnFocusAnimator);
            this.mActionOnFocusAnimator.unFocus(this.itemView);
        }

        private boolean setIndicator(ImageView indicatorView, DialogFragment.Action action) {
            Drawable indicator = action.getIndicator(indicatorView.getContext());
            if (indicator != null) {
                indicatorView.setImageDrawable(indicator);
                indicatorView.setVisibility(0);
            } else if (action.getIconUri() != null) {
                indicatorView.setVisibility(4);
            } else {
                indicatorView.setVisibility(8);
                return false;
            }
            return true;
        }

        private int getDescriptionMaxHeight(Context context, TextView title) {
            Resources res = context.getResources();
            return (int) ((((float) ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getHeight()) - (2.0f * res.getDimension(R.dimen.lb_dialog_list_item_vertical_padding))) - ((float) ((res.getInteger(R.integer.lb_dialog_action_title_max_lines) * 2) * title.getLineHeight())));
        }
    }

    public DialogActionAdapter(DialogFragment.Action.Listener listener, DialogFragment.Action.OnFocusListener onFocusListener, List<DialogFragment.Action> actions) {
        this.mOnClickListener = new C02091();
        this.mListener = listener;
        this.mActions = new ArrayList(actions);
        this.mActionOnKeyPressAnimator = new ActionOnKeyPressAnimator(listener, this.mActions);
        this.mActionOnFocusAnimator = new ActionOnFocusAnimator(onFocusListener);
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (this.mInflater == null) {
            this.mInflater = (LayoutInflater) parent.getContext().getSystemService("layout_inflater");
        }
        View v = this.mInflater.inflate(R.layout.lb_dialog_action_list_item, parent, false);
        v.setTag(R.layout.lb_dialog_action_list_item, parent);
        return new ActionViewHolder(v, this.mActionOnKeyPressAnimator, this.mActionOnFocusAnimator, this.mOnClickListener);
    }

    public void onBindViewHolder(ViewHolder baseHolder, int position) {
        ActionViewHolder holder = (ActionViewHolder) baseHolder;
        if (position < this.mActions.size()) {
            holder.init((DialogFragment.Action) this.mActions.get(position));
        }
    }

    public int getItemCount() {
        return this.mActions.size();
    }

    public void setActions(ArrayList<DialogFragment.Action> actions) {
        this.mActionOnFocusAnimator.unFocus(null);
        this.mActions.clear();
        this.mActions.addAll(actions);
        notifyDataSetChanged();
    }
}
