package com.amazon.tv.leanbacklauncher.util

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.amazon.tv.leanbacklauncher.R
import java.util.*

class OpenWeatherIcons(
    private val context: Context,
    private val weatherIcon: String,
    imageView: AppCompatImageView
) {
    private val image: Drawable?
        get() = when (weatherIcon.lowercase(Locale.getDefault())) {
            "01n" -> ContextCompat.getDrawable(
                context, R.drawable.weather01n
            )
            "01d" -> ContextCompat.getDrawable(context, R.drawable.weather01d)
            "02n" -> ContextCompat.getDrawable(context, R.drawable.weather02n)
            "02d" -> ContextCompat.getDrawable(context, R.drawable.weather02d)
            "03n", "03d" -> ContextCompat.getDrawable(context, R.drawable.weather03d)
            "04n", "04d" -> ContextCompat.getDrawable(context, R.drawable.weather04d)
            "09n", "09d" -> ContextCompat.getDrawable(context, R.drawable.weather09d)
            "10d" -> ContextCompat.getDrawable(context, R.drawable.weather10d)
            "11n", "11d" -> ContextCompat.getDrawable(context, R.drawable.weather11d)
            "13n", "13d" -> ContextCompat.getDrawable(context, R.drawable.weather13d)
            "50n", "50d" -> ContextCompat.getDrawable(context, R.drawable.weather50d)
            else -> {
                Log.e("Drawable TAG", weatherIcon)
                object : ColorDrawable(Color.TRANSPARENT) {}
            }
        }

    init {
        imageView.setImageDrawable(image)
    }
}