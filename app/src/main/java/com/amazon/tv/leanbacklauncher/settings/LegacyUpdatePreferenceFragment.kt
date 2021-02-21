package com.amazon.tv.leanbacklauncher.settings

import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist.Guidance
import androidx.leanback.widget.GuidedAction
import com.amazon.tv.leanbacklauncher.BuildConfig
import com.amazon.tv.leanbacklauncher.R
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection

class LegacyUpdatePreferenceFragment : GuidedStepSupportFragment() {
    private val RELEASES_LINK = "https://api.github.com/repos/tsynik/LeanbackLauncher/releases/latest"
    private var DOWNLOAD_LINK: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GetInfo(RELEASES_LINK)
    }

    override fun onCreateGuidance(savedInstanceState: Bundle?): Guidance {
        return Guidance(getString(R.string.update), getString(R.string.update_desc), null, ResourcesCompat.getDrawable(resources, R.drawable.ic_settings_home, null))
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        super.onCreateActions(actions, savedInstanceState)
        val info = GuidedAction.Builder(requireContext())
                .id(1)
                .title(R.string.update_in_progress)
                .description(null)
                .infoOnly(true)
                .build()
        actions.add(info)
    }

    fun updateAction(info: JSONObject) {
        val lastVersion = info.optString("tag_name", BuildConfig.VERSION_NAME).replace("v", "")
        val assets = info.optJSONArray("assets")
        if (assets != null) {
            val firstAssets = assets.optJSONObject(0)
            if (firstAssets.optString("content_type") == "application/vnd.android.package-archive") {
                DOWNLOAD_LINK = firstAssets.optString("browser_download_url")
            }
        }
        val lastVersionDouble: Double
        lastVersionDouble = try {
            lastVersion.toDouble()
        } catch (npe: NumberFormatException) {
            0.0
        }
        val currentVersionDouble: Double
        currentVersionDouble = try {
            BuildConfig.VERSION_NAME.toDouble()
        } catch (npe: NumberFormatException) {
            0.0
        }
        val actionInfo = findActionById(1)
        actionInfo.title = String.format("%s %s", getString(R.string.app_name), info.optString("name"))
        actionInfo.description = getString(R.string.update_no_updates)
        val position = findActionPositionById(actionInfo.id)
        notifyActionChanged(position)
        if (lastVersionDouble.compareTo(currentVersionDouble) > 0) {
            actionInfo.description = getString(R.string.update_new_version)
            val listActions: MutableList<GuidedAction> = ArrayList()
            listActions.add(GuidedAction.Builder(requireContext())
                    .id(2)
                    .title(R.string.update_changes)
                    .description(info.optString("body", getString(R.string.update_no_info)))
                    .multilineDescription(true)
                    .focusable(false)
                    .infoOnly(true)
                    .build()
            )
            listActions.add(actionInfo)
            listActions.add(GuidedAction.Builder(requireContext())
                    .id(3)
                    .title(R.string.update_install)
                    .build()
            )
            actions = listActions
        }
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        super.onGuidedActionClicked(action)
        if (action.id == 3L && DOWNLOAD_LINK!!.isNotEmpty()) {
            Download(DOWNLOAD_LINK, context?.externalCacheDir.toString())
        }
    }

    private inner class GetInfo(url: String?) : AsyncTask<String?, Void?, String?>() {
        override fun doInBackground(vararg strings: String?): String? {
            var urlConnection: HttpsURLConnection? = null
            try {
                val url = URL(strings[0])
                urlConnection = url.openConnection() as HttpsURLConnection
                urlConnection.connect()
                val responseCode = urlConnection.responseCode
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    return readStream(urlConnection.inputStream)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                urlConnection?.disconnect()
            }
            return null
        }

        override fun onPostExecute(s: String?) {
            super.onPostExecute(s)
            try {
                JSONObject(s)?.let {
                    updateAction(it)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        @Throws(IOException::class)
        private fun readStream(input: InputStream?): String? {
            if (input == null) {
                return null
            }
            val inputStreamReader = InputStreamReader(input)
            val bufferedReader = BufferedReader(inputStreamReader)
            val buffer = StringBuilder()
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                buffer.append(line).append("\n")
            }
            return if (buffer.length == 0) {
                null
            } else buffer.toString()
        }

        init {
            this.execute(url)
        }
    }

    private inner class Download(url: String?, filePath: String) : AsyncTask<String?, Void?, File?>() {
        override fun doInBackground(vararg strings: String?): File? {
            val fileURL = strings[0]
            val saveDir = strings[1]
            var urlConnection: HttpsURLConnection? = null
            try {
                val url = URL(fileURL)
                urlConnection = url.openConnection() as HttpsURLConnection
                urlConnection.connect()
                val responseCode = urlConnection.responseCode
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    var fileName = ""
                    val disposition = urlConnection.getHeaderField("Content-Disposition")
                    val contentType = urlConnection.contentType
                    val contentLength = urlConnection.contentLength
                    if (disposition != null) {
                        // extracts file name from header field
                        val index = disposition.indexOf("filename=")
                        if (index > 0) {
                            fileName = disposition.substring(index + 9)
                        }
                    } else {
                        if (fileURL != null) {
                            fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1)
                        }
                    }
                    println("Content-Type = $contentType")
                    println("Content-Length = $contentLength")
                    println("fileName = $fileName")
                    println("Content-Disposition = $disposition")

                    // opens input stream from the HTTP connection
                    val inputStream = urlConnection.inputStream
                    val saveFilePath = saveDir + File.separator + fileName

                    // opens an output stream to save into file
                    val outputStream = FileOutputStream(saveFilePath)
                    var bytesRead = -1
                    val buffer = ByteArray(Companion.BUFFER_SIZE)
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                    }
                    outputStream.close()
                    inputStream.close()
                    return File(saveFilePath)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                urlConnection?.disconnect()
            }
            return null
        }

        override fun onPostExecute(f: File?) {
            super.onPostExecute(f)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            if (Build.VERSION.SDK_INT < 23) {
                intent.setDataAndType(Uri.fromFile(f), "application/vnd.android.package-archive")
            } else {
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                intent.setDataAndType(FileProvider.getUriForFile(requireContext(),
                        String.format("%s.fileProvider", BuildConfig.APPLICATION_ID),
                        f!!
                ), "application/vnd.android.package-archive")
            }
            try {
                context?.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            finishGuidedStepSupportFragments()
        }

        init {
            println("Start download to $filePath")
            this.execute(url, filePath)
        }
    }

    companion object {
        private const val BUFFER_SIZE = 4096
    }

}