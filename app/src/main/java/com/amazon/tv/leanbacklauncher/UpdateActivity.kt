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
import androidx.lifecycle.lifecycleScope
import com.amazon.tv.leanbacklauncher.util.Permission
import com.amazon.tv.leanbacklauncher.util.Updater
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.String.format

class UpdateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        Permission.isReadStoragePermissionGranted(this)
        Permission.isWriteStoragePermissionGranted(this)

        findViewById<ProgressBar>(R.id.pbUpdate).visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            if (!Updater.check())
                finish()
        }
        findViewById<ProgressBar>(R.id.pbUpdate).visibility = View.GONE

        findViewById<TextView>(R.id.tvUpdateTitle)?.text =
            format(getString(R.string.update_app_found), getString(R.string.app_name))

        findViewById<TextView>(R.id.tvCurrentVersion)?.text =
            ("${getString(R.string.update_cur_version)}: ${BuildConfig.VERSION_NAME}")
        findViewById<TextView>(R.id.tvNewVersion)?.text =
            ("${getString(R.string.update_new_version)}: ${Updater.getVersion()}")
        findViewById<TextView>(R.id.tvOverview)?.text = Updater.getOverview()

        findViewById<Button>(R.id.btnCancel)?.setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnUpdate)?.setOnClickListener {
            Permission.isReadStoragePermissionGranted(this)
            Permission.isWriteStoragePermissionGranted(this)
            it.isEnabled = false
            lifecycleScope.launch(Dispatchers.IO) {
                if (update())
                    finish()
                withContext(Dispatchers.Main) {
                    it.isEnabled = true
                }
            }
        }
    }

    private suspend fun update(): Boolean {
        val pBar = findViewById<ProgressBar>(R.id.pbUpdate)
        withContext(Dispatchers.Main) {
            pBar.visibility = View.VISIBLE
            pBar.isIndeterminate = false
            findViewById<TextView>(R.id.tvUpdateInfo).setText(R.string.update_loading)
        }
        try {
            Updater.installNewVersion { prc ->
                Handler(Looper.getMainLooper()).post {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        pBar.setProgress(prc, true)
                    else
                        pBar.progress = prc
                    findViewById<TextView>(R.id.tvUpdatePrc).text = "$prc%"
                }
            }
            delay(1000)
            withContext(Dispatchers.Main) {
                pBar.visibility = View.GONE
                pBar.isIndeterminate = true
                findViewById<TextView>(R.id.tvUpdateInfo).text = ""
                findViewById<TextView>(R.id.tvUpdatePrc).text = ""
            }
            return true
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                val msg = "Error: " + (e.message ?: "")
                findViewById<TextView>(R.id.tvUpdateInfo).text = msg
                pBar.visibility = View.GONE
            }
        }
        return false
    }
}