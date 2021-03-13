package com.santiagobattaglino.mvvm.codebase.presentation.ui.base

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

abstract class BasePermissionFragment : BaseFragment(), EasyPermissions.PermissionCallbacks,
    EasyPermissions.RationaleCallbacks {

    private val mTag = javaClass.simpleName

    protected val cameraPermissions = arrayOf(
        Manifest.permission.CAMERA
    )

    // TODO Manifest.permission.ACCESS_MEDIA_LOCATION
    @RequiresApi(Build.VERSION_CODES.Q)
    protected val locationPermission = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    protected val storagePermissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @RequiresApi(Build.VERSION_CODES.Q)
    protected val broadcastPermissions = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    protected val locationPermReq = 1
    protected val cameraPermPhotoReq = 2
    protected val cameraPermVideoReq = 3
    protected val storagePermReqPhotoGallery = 4
    protected val storagePermReqVideoGallery = 5
    protected val broadcastPermReq = 6

    protected val takePhotoReq = 7
    protected val takeVideoReq = 8
    protected val pickPhotoFromGalleryReq = 9
    protected val pickVideoFromGalleryReq = 10

    protected fun hasLocationPermission(): Boolean {
        return context?.let {
            EasyPermissions.hasPermissions(
                it,
                *locationPermission
            )
        } ?: run { false }
    }

    protected fun hasCameraPermission(): Boolean {
        return context?.let { EasyPermissions.hasPermissions(it, Manifest.permission.CAMERA) }
            ?: run { false }
    }

    protected fun hasStoragePermission(): Boolean {
        return context?.let {
            EasyPermissions.hasPermissions(
                it,
                *storagePermissions
            )
        }
            ?: run { false }
    }

    protected fun hasBroadcastPermissions(): Boolean {
        return context?.let {
            EasyPermissions.hasPermissions(
                it,
                *broadcastPermissions
            )
        } ?: run { false }
    }

    override fun onPermissionsGranted(
        requestCode: Int,
        perms: List<String?>
    ) {
        Log.d(tag, "onPermissionsGranted:" + requestCode + ":" + perms.size)
    }

    override fun onPermissionsDenied(
        requestCode: Int,
        perms: List<String?>
    ) {
        Log.d(tag, "onPermissionsDenied:" + requestCode + ":" + perms.size)

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onRationaleAccepted(requestCode: Int) {
        Log.d(tag, "onRationaleAccepted:$requestCode")
    }

    override fun onRationaleDenied(requestCode: Int) {
        Log.d(tag, "onRationaleDenied:$requestCode")
    }
}