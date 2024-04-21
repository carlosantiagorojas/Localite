package com.src.localite

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.text.DateFormat
import java.util.Date

object ImageCaptureUtils {
    const val REQUEST_IMAGE_CAPTURE = 1
    const val REQUEST_GALLERY_PICK = 3
    var outputPath: Uri? = null

    fun startCamera(activity: AppCompatActivity) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val fileName = "${DateFormat.getDateInstance().format(Date())}.jpg"
        val file = File(
            activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/" + fileName
        )

        outputPath = FileProvider.getUriForFile(activity, "com.example.android.fileprovider", file)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputPath)

        try {
            activity.startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // Mostrar un mensaje de error al usuario
            Toast.makeText(activity, "Error: No se puede abrir la cámara", Toast.LENGTH_SHORT).show()
        }
    }

    fun startGallery(activity: AppCompatActivity, onlyImage: Boolean = true) {
        val intentPick = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (onlyImage)
        {
            intentPick.type = "image/*"
        }else
        {
            intentPick.type = "*/*"
        }

        activity.startActivityForResult(intentPick, REQUEST_GALLERY_PICK)
    }
}