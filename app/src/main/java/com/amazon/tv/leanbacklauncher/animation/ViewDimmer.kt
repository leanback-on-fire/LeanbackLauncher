package com.amazon.tv.leanbacklauncher.animation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Keep
import com.amazon.tv.leanbacklauncher.R
import com.amazon.tv.leanbacklauncher.widget.PlayingIndicatorView
import java.util.*

class ViewDimmer(private val mTargetView: View) {
    private val mActiveDimLevel: Float
    private var mAnimationEnabled = true
    private var mConcatMatrix: ColorMatrix? = null
    private var mDesatDrawables: MutableList<Drawable?>? = null
    private var mDesatImageViews: MutableList<ImageView?>? = null
    private val mDimAnimation: ObjectAnimator
    private var mDimLevel = 0f
    private var mDimState: DimState? = null
    private var mDrawables: MutableList<Drawable?>? = null
    private val mEditModeDimLevel: Float
    private var mImageViews: MutableList<ImageView?>? = null
    private val mInactiveDimLevel: Float
    private var mOriginalTextColors: MutableList<Int?>? = null
    private var mPlayingIndicatorViews: MutableList<PlayingIndicatorView?>? = null
    private var mTextViews: MutableList<TextView?>? = null

    enum class DimState {
        ACTIVE, INACTIVE, EDIT_MODE
    }

    fun setAnimationEnabled(enabled: Boolean) {
        mAnimationEnabled = enabled
        if (!enabled && mDimAnimation.isStarted) {
            mDimAnimation.end()
        }
    }

    fun setConcatMatrix(matrix: ColorMatrix?) {
        mConcatMatrix = matrix
        dimLevel = mDimLevel // FIXME
    }

    @set:Keep
    var dimLevel: Float
        get() = mDimLevel
        private set(level) {
            var size: Int
            var i: Int
            mDimLevel = level
            var filter: ColorFilter? = null
            var desatFilter: ColorFilter? = null
            if (!(mImageViews == null && mDrawables == null) && mDimLevel > 0.0f && mDimLevel <= 1.0f) {
                val filterIndex = (255.0f * level).toInt()
                filter = if (mConcatMatrix == null) {
                    sFilters!![filterIndex]
                } else {
                    val matrix = ColorMatrix()
                    matrix.setConcat(sMatrices[filterIndex], mConcatMatrix)
                    ColorMatrixColorFilter(matrix)
                }
            }
            if (filter == null && mConcatMatrix != null) {
                filter = ColorMatrixColorFilter(mConcatMatrix!!)
            }
            if (!(mDesatImageViews == null && mDesatDrawables == null) && mDimLevel >= 0.0f && mDimLevel <= 1.0f) {
                desatFilter = sFiltersDesat!![(255.0f * level).toInt()]
            }
            if (mImageViews != null) {
                size = mImageViews!!.size
                i = 0
                while (i < size) {
                    mImageViews!![i]!!.colorFilter = filter
                    i++
                }
            }
            if (mDesatImageViews != null) {
                size = mDesatImageViews!!.size
                i = 0
                while (i < size) {
                    mDesatImageViews!![i]!!.colorFilter = desatFilter
                    i++
                }
            }
            if (mDrawables != null) {
                size = mDrawables!!.size
                i = 0
                while (i < size) {
                    mDrawables!![i]!!.colorFilter = filter
                    i++
                }
            }
            if (mTextViews != null) {
                size = mTextViews!!.size
                i = 0
                while (i < size) {
                    mTextViews!![i]!!
                        .setTextColor(getDimmedColor(mOriginalTextColors!![i]!!, level))
                    i++
                }
            }
            if (mPlayingIndicatorViews != null) {
                size = mPlayingIndicatorViews!!.size
                i = 0
                while (i < size) {
                    mPlayingIndicatorViews!![i]!!.setColorFilter(filter)
                    i++
                }
            }
            if (mDesatDrawables != null) {
                size = mDesatDrawables!!.size
                i = 0
                while (i < size) {
                    mDesatDrawables!![i]!!.mutate()
                    mDesatDrawables!![i]!!.colorFilter = desatFilter
                    i++
                }
            }
        }

    private fun convertToDimLevel(dimState: DimState?): Float {
        return when (dimState) {
            DimState.ACTIVE -> mActiveDimLevel
            DimState.INACTIVE -> mInactiveDimLevel
            DimState.EDIT_MODE -> mEditModeDimLevel
            else -> throw IllegalArgumentException("Illegal dimState: $dimState")
        }
    }

    private fun animateDim(dimState: DimState?) {
        if (mAnimationEnabled) {
            if (mDimAnimation.isStarted) {
                mDimAnimation.cancel()
            }
            if (dimLevel != convertToDimLevel(dimState)) {
                mDimAnimation.setFloatValues(dimLevel, R.id.end.toFloat())
                mDimAnimation.start()
                return
            }
            return
        }
        setDimLevelImmediate(dimState) // FIXME
    }

    fun setDimLevelImmediate(dimState: DimState?) {
        if (mDimAnimation.isStarted) {
            mDimAnimation.cancel()
        }
        dimLevel = convertToDimLevel(dimState) // FIXME
    }

    fun setDimLevelImmediate() {
        if (mDimState != null) {
            setDimLevelImmediate(mDimState) // FIXME
        } else {
            setDimLevelImmediate(DimState.INACTIVE) // FIXME
        }
    }
    // TODO: add option to enable immeddiate dim
    fun setDimState(dimState: DimState?, immediate: Boolean) {
        if (immediate) {
        //    setDimLevelImmediate(dimState) // FIXME
        } else {
        //    animateDim(dimState) // FIXME
        }
        mDimState = dimState
    }

    fun addDimTarget(view: PlayingIndicatorView?) {
        if (mPlayingIndicatorViews == null) {
            mPlayingIndicatorViews = ArrayList<PlayingIndicatorView?>(4)
        }
        mPlayingIndicatorViews!!.add(view)
    }

    fun addDimTarget(view: ImageView?) {
        if (mImageViews == null) {
            mImageViews = ArrayList<ImageView?>(4)
        }
        mImageViews!!.add(view)
    }

    fun addDesatDimTarget(view: ImageView?) {
        if (mDesatImageViews == null) {
            mDesatImageViews = ArrayList<ImageView?>(4)
        }
        mDesatImageViews!!.add(view)
    }

    fun addDesatDimTarget(drawable: Drawable?) {
        if (mDesatDrawables == null) {
            mDesatDrawables = ArrayList<Drawable?>(4)
        }
        mDesatDrawables!!.add(drawable)
    }

    fun addDimTarget(view: TextView) {
        if (mTextViews == null) {
            mTextViews = ArrayList<TextView?>(4)
        }
        if (mOriginalTextColors == null) {
            mOriginalTextColors = ArrayList<Int?>(4)
        }
        mTextViews!!.add(view)
        mOriginalTextColors!!.add(view.currentTextColor)
    }

    fun setTargetTextColor(view: TextView?, newColor: Int) {
        if (mTextViews != null && mOriginalTextColors != null) {
            val index = mTextViews!!.indexOf(view)
            if (index >= 0) {
                mOriginalTextColors!![index] = newColor
                view?.setTextColor(getDimmedColor(newColor, mDimLevel))
            }
        }
    }

    fun addDimTarget(drawable: Drawable?) {
        if (mDrawables == null) {
            mDrawables = ArrayList<Drawable?>(4)
        }
        mDrawables!!.add(drawable)
    }

    fun removeDimTarget(drawable: Drawable?) {
        if (mDrawables != null) {
            mDrawables!!.remove(drawable)
        }
    }

    fun removeDesatDimTarget(drawable: Drawable?) {
        if (mDesatDrawables != null) {
            mDesatDrawables!!.remove(drawable)
        }
    }

    companion object {
        private var sFilters: Array<ColorFilter?>? = null
        private var sFiltersDesat: Array<ColorFilter?>? = null
        private var sMatrices: Array<ColorMatrix?> = arrayOfNulls(256)
        fun getDimmedColor(color: Int, level: Float): Int {
            val factor = 1.0f - level
            return Color.argb(
                Color.alpha(color), (Color.red(color)
                    .toFloat() * factor).toInt(), (Color.green(color)
                    .toFloat() * factor).toInt(), (Color.blue(color)
                    .toFloat() * factor).toInt()
            )
        }

        fun activatedToDimState(activated: Boolean): DimState {
            return if (activated) {
                DimState.ACTIVE
            } else DimState.INACTIVE
        }

        fun dimStateToActivated(dimState: DimState): Boolean {
            return dimState != DimState.INACTIVE
        }
    }

    init {
        if (sFilters == null || sFiltersDesat == null) {
            sFilters = arrayOfNulls(256)
            sFiltersDesat = arrayOfNulls(256)
            sMatrices = arrayOfNulls(256)
            val desat = ColorMatrix()
            desat.setSaturation(0.0f)
            for (i in 0..255) {
                val dimVal = 1.0f - i.toFloat() / 255.0f
                val dimMatrix = ColorMatrix()
                dimMatrix.setScale(dimVal, dimVal, dimVal, 1.0f)
                sMatrices[i] = dimMatrix
                sFilters!![i] = ColorMatrixColorFilter(dimMatrix)
                val dimDesatMatrix = ColorMatrix()
                dimDesatMatrix.setScale(dimVal, dimVal, dimVal, 1.0f)
                dimDesatMatrix.postConcat(desat)
                sFiltersDesat!![i] = ColorMatrixColorFilter(dimDesatMatrix)
            }
        }
        mActiveDimLevel = mTargetView.resources.getFraction(R.fraction.launcher_active_dim_level, 1, 1)
        mInactiveDimLevel = mTargetView.resources.getFraction(R.fraction.launcher_inactive_dim_level, 1, 1)
        mEditModeDimLevel = mTargetView.resources.getFraction(R.fraction.launcher_edit_mode_dim_level, 1, 1)
        val dimAnimDuration = mTargetView.resources.getInteger(R.integer.item_dim_anim_duration)
        mDimAnimation = ObjectAnimator.ofFloat(this, "dimLevel", mInactiveDimLevel)
        mDimAnimation.duration = dimAnimDuration.toLong()
        mDimAnimation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                mTargetView.setHasTransientState(true)
            }

            override fun onAnimationEnd(animation: Animator) {
                mTargetView.setHasTransientState(false)
            }
        })
    }
}