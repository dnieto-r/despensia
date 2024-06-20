package com.solstix.despensia

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionsManager private constructor() {
    companion object {
        fun checkPermission(
            context: Activity,
            isGranted: Boolean,
            permission: String,
            acceptedAction: (() -> Unit)? = null,
            deniedAction: (() -> Unit)? = null,
            permanentlyDeniedAction: (() -> Unit)? = null
        ) {
            if (!isGranted) {
                if (!shouldShowRequestPermissionRationale(context, permission)) {
                    permanentlyDeniedAction?.invoke()
                } else {
                    deniedAction?.invoke()
                }
            } else {
                acceptedAction?.invoke()
            }
        }

        fun isPermissionAccepted(context: Context, permission: String): Boolean {
            return ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }

        fun shouldShowRequestPermissionRationale(context: Activity, permission: String): Boolean =
            ActivityCompat.shouldShowRequestPermissionRationale(
                context,
                permission)
    }
}