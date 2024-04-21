package com.src.localite

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionsUtils {
    const val CAMERA_PERMISSION: Int = 101
    const val RECORD_AUDIO_PERMISSION: Int = 102
    const val WRITE_EXTERNAL_STORAGE_PERMISSION: Int = 103
    const val READ_EXTERNAL_STORAGE_PERMISSION: Int = 104
    const val ACCESS_FINE_LOCATION_PERMISSION: Int = 105
    const val ACCESS_COARSE_LOCATION_PERMISSION: Int = 106


    fun requestCameraPermission(
        activity: AppCompatActivity,
        onPermissionGranted: () -> Unit,
        onPermissionDenied: () -> Unit
    ) {
        when {
            ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Si me autorizaron los permisos
                onPermissionGranted()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.CAMERA
            ) -> {
                // Explicar al usuario por qué se necesita el permiso
                onPermissionDenied()
            }

            else -> {
                // Solicitar el permiso
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION
                )
            }
        }
    }
    fun requestGalleryPermission(
        activity: AppCompatActivity,
        onPermissionGranted: () -> Unit,
        onPermissionDenied: () -> Unit
    ){
        when {
            ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Si me autorizaron los permisos
                onPermissionGranted()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) -> {
                // Explicar al usuario por qué se necesita el permiso
                onPermissionDenied()
            }

            else -> {
                // Solicitar el permiso
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_EXTERNAL_STORAGE_PERMISSION
                )
            }
        }
    }

    fun requestLocationPermission(
        activity: AppCompatActivity,
        onPermissionGranted: () -> Unit,
        onPermissionDenied: () -> Unit
    ){
        when {
            ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Si me autorizaron los permisos
                onPermissionGranted()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                // Explicar al usuario por qué se necesita el permiso
                onPermissionDenied()
            }

            else -> {
                // Solicitar el permiso
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    ACCESS_FINE_LOCATION_PERMISSION
                )
            }
        }
    }
}