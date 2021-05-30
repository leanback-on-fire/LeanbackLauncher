package com.amazon.tv.leanbacklauncher.inputs

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView
import com.amazon.tv.leanbacklauncher.R

class InputsTextView : AppCompatTextView {
    private val mPaddingDefault = TypedValue()
    private val mPaddingWrapped = TypedValue()
    private var mRes: Resources? = null
    private var mTextChanged = false
    private val mTextSizeDefault = TypedValue()
    private val mTextSizeWrapped = TypedValue()

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        mRes = context.resources
        mRes?.let {
            it.getValue(R.dimen.input_banner_text_bottom_padding, mPaddingDefault, false)
            it.getValue(R.dimen.input_banner_text_bottom_padding_wrapped, mPaddingWrapped, false)
            it.getValue(R.dimen.input_banner_label_text_size, mTextSizeDefault, false)
            it.getValue(R.dimen.input_banner_label_text_size_wrapped, mTextSizeWrapped, false)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (mTextChanged) {
            mTextChanged = false
            maxLines = 1
            setTextSize(mTextSizeDefault)
            setPaddingBottom(mPaddingDefault)
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            if (isEllipsized) {
                setTextSize(mTextSizeWrapped)
                super.onMeasure(widthMeasureSpec, heightMeasureSpec)
                if (isEllipsized) {
                    maxLines = 2
                    setPaddingBottom(mPaddingWrapped)
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
                    return
                }
                return
            }
            return
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    private fun setTextSize(value: TypedValue) {
        setTextSize(value.data shr 0 and 15, TypedValue.complexToFloat(value.data))
    }

    private fun setPaddingBottom(value: TypedValue) {
        setPadding(
            paddingLeft,
            paddingTop,
            paddingRight,
            TypedValue.complexToDimensionPixelSize(value.data, mRes!!.displayMetrics)
        )
    }

    override fun setText(text: CharSequence, type: BufferType) {
        mTextChanged = true
        requestLayout()
        super.setText(text, type)
    }

    private val isEllipsized: Boolean
        get() {
            val l = layout ?: return false
            val lines = l.lineCount
            return lines > 0 && l.getEllipsisCount(lines - 1) > 0
        }
}