package com.src.localite

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import android.Manifest
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import com.src.localite.databinding.ActivityRegistroBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ktx.database
import com.google.firebase.storage.ktx.storage
import com.src.localite.ImageCaptureUtils.REQUEST_IMAGE_CAPTURE

class RegistroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistroBinding
    private val locationPermissionRequestCode = 1001
    var auth: FirebaseAuth = Firebase.auth
    private var user: FirebaseUser? = null
    private var photoURI: Uri? = null
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        user = auth.currentUser

        binding.registrate.setOnClickListener() {
            signUp()
        }

        binding.includeTopBar.GoBack.setOnClickListener() {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                photoURI = uri
                binding.AddPhoto.setImageURI(uri)
                println("URI CORRECTO")
            }


        }

        binding.AddPhoto.setOnClickListener {
            // Intent explícito para seleccionar una imagen de la galería
            imagePickerLauncher.launch("image/*")
        }


    }

    private fun validateFields(): Boolean {
        // Validate name
        if (binding.Usuario.editText?.text.toString().isEmpty()) {
            binding.Usuario.error = "Falta ingresar nombre"
            return false
        } else binding.Usuario.isErrorEnabled = false

        // Validate email
        if (binding.Email.editText?.text.toString().isEmpty() ||
            !android.util.Patterns.EMAIL_ADDRESS.matcher(binding.Email.editText?.text.toString())
                .matches()
        ) {
            binding.Email.error = "Error con el correo electronico"
            return false
        } else binding.Email.isErrorEnabled = false

        // Validate cellphone
        if (binding.Telefono.editText?.text.toString().isEmpty()) {
            binding.Telefono.error = "Falta ingresar teléfono"
            return false
        } else binding.Telefono.isErrorEnabled = false

        // Validate password
        if (binding.Contra.editText?.text.toString().isEmpty()) {
            binding.Contra.error = "Falta ingresar contraseña"
            return false
        } else binding.Contra.isErrorEnabled = false;

        // Validate password
        if (binding.ConfirmarContra.editText?.text.toString().isEmpty()) {
            binding.ConfirmarContra.error = "Falta ingresar contraseña"
            return false
        } else binding.ConfirmarContra.isErrorEnabled = false;

        // Validate password confirmation
        if (binding.ConfirmarContra.editText?.text.toString() != binding.ConfirmarContra.editText?.text.toString()) {
            binding.ConfirmarContra.error = "No coinciden las contraseñas"
            binding.ConfirmarContra.error = "No coinciden las contraseñas"
            return false
        } else {
            binding.ConfirmarContra.isErrorEnabled = false
            binding.ConfirmarContra.isErrorEnabled = false
        }
        return true
    }

    // Sign up with email and password
    private fun signUp() {
        if (validateFields()) {
            val name = binding.Usuario.editText?.text.toString()
            val email = binding.Email.editText?.text.toString()
            val phone = binding.Telefono.editText?.text.toString()
            val password = binding.Contra.editText?.text.toString()
            val confirmPassword = binding.ConfirmarContra.editText?.text.toString()

            if (password == confirmPassword) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            user = auth.currentUser
                            val userId = user?.uid
                            // Guarda los datos del usuario en la base de datos
                            val database = Firebase.database
                            val ref = database.getReference("Usuarios").child(userId!!)
                            val userData = HashMap<String, Any>()
                            userData["nombre"] = name
                            userData["email"] = email
                            userData["telefono"] = phone
                            ref.setValue(userData)
                                .addOnSuccessListener {
                                    // Sube la foto a Firebase Storage
                                    uploadProfilePhoto(userId)

                                    if (checkLocationPermission()) {
                                        val intent = Intent(this, HomeActivity::class.java)
                                        startActivity(intent)
                                    } else {
                                        requestLocationPermission()
                                    }

                                }
                                .addOnFailureListener { e ->
                                    Snackbar.make(binding.root, "Error: ${e.message}", Snackbar.LENGTH_SHORT).show()
                                }
                        } else {
                            Snackbar.make(binding.root, "Error: ${task.exception?.message}", Snackbar.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Snackbar.make(binding.root, "Las contraseñas no coinciden", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadProfilePhoto(userId: String) {
        photoURI?.let { uri ->
            val storageRef = Firebase.storage.reference.child("Usuarios/$userId/profile")

            storageRef.putFile(uri)
                .addOnSuccessListener {
                    // Limpiar campos después de la carga exitosa de la foto
                    clearFields()
                }
                .addOnFailureListener { e ->
                    println("No funciono la carga de la foto del usuario")
                }
        } ?: run {
            println("URI de foto nula")
        }
    }

    private fun clearFields() {
        binding.Usuario.editText?.setText("")
        binding.Email.editText?.setText("")
        binding.Telefono.editText?.setText("")
        binding.Contra.editText?.setText("")
        binding.ConfirmarContra.editText?.setText("")
        binding.AddPhoto.setImageResource(R.drawable.blank_profile_picture)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(
                    this,
                    "Permiso de ubicación requerido",
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }
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




}