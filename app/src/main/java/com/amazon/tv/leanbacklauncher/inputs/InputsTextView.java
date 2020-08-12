package com.amazon.tv.leanbacklauncher.inputs;

import android.content.Context;
import android.content.res.Resources;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.appcompat.widget.AppCompatTextView;

import com.amazon.tv.leanbacklauncher.R;

public final class InputsTextView extends AppCompatTextView {
    private TypedValue mPaddingDefault = new TypedValue();
    private TypedValue mPaddingWrapped = new TypedValue();
    private Resources mRes;
    private boolean mTextChanged;
    private TypedValue mTextSizeDefault = new TypedValue();
    private TypedValue mTextSizeWrapped = new TypedValue();

    public InputsTextView(Context context) {
        super(context);
        init(context);
    }

    public InputsTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public InputsTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
        setTextSize((value.data >> 0) & 15, TypedValue.complexToFloat(value.data));
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
        Layout l = getLayout();
        if (l == null) {
            return false;
        }
        int lines = l.getLineCount();
        return lines > 0 && l.getEllipsisCount(lines - 1) > 0;
    }
}
