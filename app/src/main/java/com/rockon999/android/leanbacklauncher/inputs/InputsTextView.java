package com.rockon999.android.leanbacklauncher.inputs;

import android.content.Context;
import android.content.res.Resources;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.rockon999.android.leanbacklauncher.R;

public final class InputsTextView extends android.support.v7.widget.AppCompatTextView {
    private TypedValue mPaddingDefault;
    private TypedValue mPaddingWrapped;
    private Resources mRes;
    private boolean mTextChanged;
    private TypedValue mTextSizeDefault;
    private TypedValue mTextSizeWrapped;

    public InputsTextView(Context context) {
        super(context);
        this.mPaddingDefault = new TypedValue();
        this.mPaddingWrapped = new TypedValue();
        this.mTextSizeDefault = new TypedValue();
        this.mTextSizeWrapped = new TypedValue();
        init(context);
    }

    public InputsTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mPaddingDefault = new TypedValue();
        this.mPaddingWrapped = new TypedValue();
        this.mTextSizeDefault = new TypedValue();
        this.mTextSizeWrapped = new TypedValue();
        init(context);
    }

    public InputsTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mPaddingDefault = new TypedValue();
        this.mPaddingWrapped = new TypedValue();
        this.mTextSizeDefault = new TypedValue();
        this.mTextSizeWrapped = new TypedValue();
        init(context);
    }

    private void init(Context context) {
        this.mRes = context.getResources();
        this.mRes.getValue(R.dimen.input_banner_text_bottom_padding, this.mPaddingDefault, false);
        this.mRes.getValue(R.dimen.input_banner_text_bottom_padding_wrapped, this.mPaddingWrapped, false);
        this.mRes.getValue(R.dimen.input_banner_label_text_size, this.mTextSizeDefault, false);
        this.mRes.getValue(R.dimen.input_banner_label_text_size_wrapped, this.mTextSizeWrapped, false);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.mTextChanged) {
            this.mTextChanged = false;
            setMaxLines(1);
            setTextSize(this.mTextSizeDefault);
            setPaddingBottom(this.mPaddingDefault);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            if (isEllipsized()) {
                setTextSize(this.mTextSizeWrapped);
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                if (isEllipsized()) {
                    setMaxLines(2);
                    setPaddingBottom(this.mPaddingWrapped);
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                    return;
                }
                return;
            }
            return;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void setTextSize(TypedValue value) {
        setTextSize((value.data) & 15, TypedValue.complexToFloat(value.data));
    }

    private void setPaddingBottom(TypedValue value) {
        setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), TypedValue.complexToDimensionPixelSize(value.data, this.mRes.getDisplayMetrics()));
    }

    public void setText(CharSequence text, BufferType type) {
        this.mTextChanged = true;
        requestLayout();
        super.setText(text, type);
    }

    private boolean isEllipsized() {
        boolean z = false;
        Layout l = getLayout();
        if (l != null) {
            int lines = l.getLineCount();
            if (lines > 0) {
                if (l.getEllipsisCount(lines - 1) > 0) {
                    z = true;
                }
                return z;
            }
        }
        return false;
    }
}
