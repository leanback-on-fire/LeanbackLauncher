package com.amazon.tv.leanbacklauncher.notifications

import android.content.Context
import android.graphics.Bitmap
import android.os.ConditionVariable
import android.os.RemoteException
import android.util.Log
import com.amazon.tv.leanbacklauncher.BuildConfig
import com.amazon.tv.leanbacklauncher.recommendations.SwitchingRecommendationsClient
import com.amazon.tv.tvrecommendations.IRecommendationsService

class RecImageLoader private constructor(context: Context) {
    @get:Synchronized
    @set:Synchronized
    private var service: IRecommendationsService? = null
    private val mClient: SwitchingRecommendationsClient
    private val mServiceBound = ConditionVariable()
    private val TAG by lazy { if (BuildConfig.DEBUG) ("[*]" + javaClass.simpleName).take(21) else javaClass.simpleName }

    companion object {
        private var sInstance: RecImageLoader? = null

        @JvmStatic
        fun getInstance(context: Context): RecImageLoader? {
            if (sInstance == null) {
                sInstance = RecImageLoader(context)
            }
            return sInstance
        }
    }

    init {
        mClient = object : SwitchingRecommendationsClient(context) {
            override fun onConnected(service: IRecommendationsService) {
                onServiceConnected(service)
            }

            override fun onDisconnected() {
                onServiceDisconnected()
            }
        }
        Log.i(TAG, "Connecting to recommendations service")
        mClient.connect()
    }

    fun getImageForRecommendation(key: String): Bitmap? {
        if (service == null) {
            Log.i(TAG, "Waiting for service connection")
            mServiceBound.block(10000)
            if (service == null) {
                Log.e(TAG, "Service still null after waiting, attempting to reconnect")
                mClient.reconnect()
                mServiceBound.block(10000)
            }
        }
        val service = service
        if (service != null) {
            try {
                Log.w(TAG, "Obtain bitmap for key: $key")
                val bitmap = service.getImageForRecommendation(key)
                if (bitmap != null) {
                    return bitmap
                }
                Log.e(TAG, "Recommendations service returned a null image")
                return bitmap // todo: default image
            } catch (e: RemoteException) {
                Log.e(TAG, "Cannot obtain recommendation image", e)
            }
        } else {
            Log.e(TAG, "Cannot obtain recommendation image - service not connected")
            return null
        }
        return null
    }

    private fun onServiceConnected(service: IRecommendationsService) {
        Log.i(TAG, "Service connected")
        this.service = service
        mServiceBound.open()
    }

    private fun onServiceDisconnected() {
        Log.i(TAG, "Service disconnected")
        service = null
        mServiceBound.close()
    }

}