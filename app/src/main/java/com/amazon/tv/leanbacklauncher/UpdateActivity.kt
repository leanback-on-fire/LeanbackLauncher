package com.amazon.tv.leanbacklauncher

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.amazon.tv.leanbacklauncher.BuildConfig
import com.amazon.tv.leanbacklauncher.R
import com.amazon.tv.leanbacklauncher.util.Updater
import com.amazon.tv.leanbacklauncher.util.Permission
import java.lang.String.format
import kotlin.concurrent.thread

class UpdateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        Permission.isReadStoragePermissionGranted(this)
        Permission.isWriteStoragePermissionGranted(this)

        findViewById<ProgressBar>(R.id.pbUpdate).visibility = View.VISIBLE
        thread {
            if (!Updater.check())
                finish()
        }.join()
        findViewById<ProgressBar>(R.id.pbUpdate).visibility = View.GONE

        findViewById<TextView>(R.id.tvUpdateTitle)?.text = format(getString(R.string.update_app_found), getString(R.string.app_name))

        findViewById<TextView>(R.id.tvCurrentVersion)?.text = ("${getString(R.string.update_cur_version)}: ${BuildConfig.VERSION_NAME}")
        findViewById<TextView>(R.id.tvNewVersion)?.text = ("${getString(R.string.update_new_version)}: ${Updater.getVersion()}")
        findViewById<TextView>(R.id.tvOverview)?.text = Updater.getOverview()

        findViewById<Button>(R.id.btnCancel)?.setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnUpdate)?.setOnClickListener {
            Permission.isReadStoragePermissionGranted(this)
            Permission.isWriteStoragePermissionGranted(this)
            it.isEnabled = false
            thread {
                if (update())
                    finish()
                Handler(Looper.getMainLooper()).post {
                    it.isEnabled = true
                }
            }
        }
    }

    private fun update(): Boolean {
        Handler(Looper.getMainLooper()).post {
            findViewById<ProgressBar>(R.id.pbUpdate).visibility = View.VISIBLE
            findViewById<ProgressBar>(R.id.pbUpdate).isIndeterminate = false
            findViewById<TextView>(R.id.tvUpdateInfo).setText(R.string.update_loading)
        }
        try {
            Updater.installNewVersion { prc ->
                Handler(Looper.getMainLooper()).post {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        findViewById<ProgressBar>(R.id.pbUpdate).setProgress(prc, true)
                    else
                        findViewById<ProgressBar>(R.id.pbUpdate).setProgress(prc)
                }
            }
            Handler(Looper.getMainLooper()).post {
                findViewById<ProgressBar>(R.id.pbUpdate).isIndeterminate = true
                findViewById<TextView>(R.id.tvUpdateInfo).text = ""
            }
            Thread.sleep(1000)
            Handler(Looper.getMainLooper()).post {
                findViewById<ProgressBar>(R.id.pbUpdate).visibility = View.GONE
            }
            return true
        } catch (e: Exception) {
            Handler(Looper.getMainLooper()).post {
                val msg = "Error download apk: " + (e.message ?: "")
                findViewById<TextView>(R.id.tvUpdateInfo).text = msg
                findViewById<ProgressBar>(R.id.pbUpdate).visibility = View.GONE
            }
        }
        return false
    }
}