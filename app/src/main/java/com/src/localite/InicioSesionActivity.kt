package com.src.localite

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.src.localite.databinding.ActivityInicioSesionBinding

class InicioSesionActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityInicioSesionBinding
    private val locationPermissionRequestCode = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicioSesionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = Firebase.auth

        binding.IniciaSesion.setOnClickListener {
            if (checkLocationPermission()) {
                launchHomeActivity()
            } else {
                requestLocationPermission()
            }
        }

        binding.textoRegistro.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            locationPermissionRequestCode
        )
    }

    private fun login() {
        if (validateFields())
            auth.signInWithEmailAndPassword(
                binding.correoLogin.editText?.text.toString(),
                binding.Contra.editText?.text.toString()
            ).addOnCompleteListener(this) { task ->
                if (task.isSuccessful and checkLocationPermission()) {
                    launchHomeActivity()
                } else {
                    // If sign in fails, display a message to the user.
                    val snackbar = task.exception?.localizedMessage?.let {
                        Snackbar.make(
                            binding.root,
                            it, Snackbar.LENGTH_INDEFINITE
                        )
                    }
                    snackbar?.setAction("Error al Iniciar Sesión") { snackbar?.dismiss() }
                    snackbar?.show()
                }
            }
    }

    private fun validateFields(): Boolean {
        // Validate email
        if (binding.correoLogin.editText?.text.toString().isEmpty() ||
            !android.util.Patterns.EMAIL_ADDRESS.matcher(binding.correoLogin.editText?.text.toString())
                .matches()
        ) {
            binding.correoLogin.error = "Error con el correo electronico"
            return false
        } else binding.correoLogin.isErrorEnabled = false

        // Validate password
        if (binding.Contra.editText?.text.toString().isEmpty()) {
            binding.Contra.error = "Falta ingresar contraseña"
            return false
        } else binding.Contra.isErrorEnabled = false;

        return true
    }

    private fun launchHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchHomeActivity()
            } else {
                Toast.makeText(
                    this,
                    "Permiso de ubicación requerido",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
