package com.src.localite

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
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

    private var alerts: Alerts = Alerts(this)

    var auth: FirebaseAuth = Firebase.auth
    private var user: FirebaseUser? = null
    private val database = Firebase.database
    private val storage = Firebase.storage
    private var refData = database.getReference("Usuarios/${user?.uid}")
    private var refStore =
        storage.reference.child("Usuarios/${Firebase.auth.currentUser?.uid}/profile.jpg")

    private lateinit var currentUser: UserProfile

    private var outputPath: Uri? = null
    private val REQUEST_PICK = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = Firebase.auth
        user = auth.currentUser

        binding.registrate.setOnClickListener() {
            signUp()
        }

        binding.includeTopBar.GoBack.setOnClickListener() {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Aplicar permisos de galeria
        binding.AddPhoto.setOnClickListener() {

            PermissionsUtils.requestGalleryPermission(
                this,
                onPermissionGranted = {
                    // Acciones cuando se concede el permiso
                    ImageCaptureUtils.startGallery(this)
                },
                onPermissionDenied = {
                    // Acciones cuando se deniega el permiso
                    alerts.indefiniteSnackbar(
                        binding.root,
                        "El permiso de la cámara es necesario para usar esta función 😭"
                    )
                }
            )
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
            auth.createUserWithEmailAndPassword(
                binding.Email.editText?.text.toString(),
                binding.ConfirmarContra.editText?.text.toString()
            ).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Update the FirebaseUser object
                    user = Firebase.auth.currentUser!!

                    // Update the DatabaseReference and StorageReference objects
                    refData = database.getReference("Usuarios/${user!!.uid}")
                    refStore = storage.reference.child("Usuarios/${user!!.uid}/profile.jpg")

                    currentUser = UserProfile(
                        binding.Usuario.editText?.text.toString(),
                        binding.Email.editText?.text.toString(),
                        ""
                    )

                    if (outputPath != null) {
                        val uploadTask = refStore.putFile(outputPath!!)
                        uploadTask.continueWithTask { task ->
                            if (!task.isSuccessful) {
                                task.exception?.let {
                                    throw it
                                }
                            }
                            refStore.downloadUrl
                        }.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val downloadUri = task.result

                                // Update the photoUrl field of the UserProfile object
                                currentUser.photoUrl = downloadUri.toString()

                                // Save the UserProfile object to the Realtime Database
                                refData.setValue(currentUser)
                                    .addOnSuccessListener {
                                        startActivity(Intent(this, HomeActivity::class.java))
                                    }
                                    .addOnFailureListener {
                                        // Handle failure to save user data in the database
                                        alerts.shortSimpleSnackbar(
                                            binding.root,
                                            "Error al guardar datos de usuario"
                                        )
                                    }
                            } else {
                                // Handle failure
                                alerts.shortSimpleSnackbar(
                                    binding.root,
                                    "Error al subir imagen de perfil"
                                )
                            }
                        }
                    } else {
                        // User data saved without profile image
                        startActivity(Intent(this, HomeActivity::class.java))
                    }
                } else {
                    // If account creation fails, show an error message
                    val snackbar = task.exception?.localizedMessage?.let {
                        Snackbar.make(
                            binding.root,
                            it, Snackbar.LENGTH_INDEFINITE
                        )
                    }
                    snackbar?.setAction("Error al crear la cuenta") { snackbar?.dismiss() }
                    snackbar?.show()
                }
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermissionsUtils.CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ImageCaptureUtils.startCamera(this)
                } else {
                    alerts.shortSimpleSnackbar(
                        binding.root,
                        "Se denegó el permiso de la cámara 😭"
                    )
                }
            }

            PermissionsUtils.READ_EXTERNAL_STORAGE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ImageCaptureUtils.startGallery(this)
                } else {
                    alerts.shortSimpleSnackbar(
                        binding.root,
                        "Se denegó el permiso a la galeria 😭"
                    )
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            Glide.with(this)
                .clear(binding.AddPhoto)
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    alerts.shortSimpleSnackbar(binding.root, "Foto tomada correctamente")
                    outputPath = data?.data
                }

                REQUEST_PICK -> {
                    alerts.shortSimpleSnackbar(binding.root, "Imagen seleccionada correctamente")
                    outputPath = data?.data
                }
            }
            Glide.with(this)
                .load(outputPath)
                .centerCrop()
                .into(binding.AddPhoto)
        }
    }
}