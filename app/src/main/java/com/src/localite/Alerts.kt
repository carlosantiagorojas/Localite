package com.src.localite

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

class Alerts(private val context: Context){
    private var TAG = Alerts::class.java.name

    fun shortToast(message: String?) {
        if (message != null) {
            Log.i(TAG, message)
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun longToast(message: String?) {
        if (message != null) {
            Log.i(TAG, message)
        }
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun shortSimpleSnackbar(parentView: View, message: String) {
        Log.i(TAG, message)
        Snackbar.make(parentView, message, Snackbar.LENGTH_SHORT).show()
    }

    fun longSimpleSnackbar(parentView: View, message: String) {
        Log.i(TAG, message)
        Snackbar.make(parentView, message, Snackbar.LENGTH_LONG).show()
    }

    fun indefiniteSnackbar(parentView: View, message: String) {
        Log.i(TAG, "indefiniteSnackbar: $message")
        val snackbar = Snackbar.make(parentView, message, Snackbar.LENGTH_INDEFINITE)
        snackbar.setAction("Cerrar") { snackbar.dismiss() }
        snackbar.show()
    }
}