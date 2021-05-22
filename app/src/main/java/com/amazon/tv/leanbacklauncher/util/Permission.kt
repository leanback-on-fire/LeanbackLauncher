package com.amazon.tv.leanbacklauncher.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import com.amazon.tv.leanbacklauncher.MainActivity

object Permission {

    fun isReadStoragePermissionGranted(activity: Activity): Boolean {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                return true
            } else {
                ActivityCompat.requestPermissions(activity, arrayOf(permission), 1)
                return false
            }
        } else {
            return true
        }
    }

    fun isWriteStoragePermissionGranted(activity: Activity): Boolean {
        val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                return true
            } else {
                ActivityCompat.requestPermissions(activity, arrayOf(permission), 2)
                return false
            }
        } else {
            return true
        }
    }

    fun isLocationPermissionGranted(activity: Activity): Boolean {
        val permission = Manifest.permission.ACCESS_COARSE_LOCATION
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                return true
            } else {
                ActivityCompat.requestPermissions(activity, arrayOf(permission), MainActivity.PERMISSIONS_REQUEST_LOCATION)
                return false
            }
        } else {
            return true
        }
    }

}