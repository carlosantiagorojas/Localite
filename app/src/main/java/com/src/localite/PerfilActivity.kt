package com.src.localite

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.src.localite.Alerts
import com.src.localite.databinding.ActivityPerfilBinding
import com.src.localite.InicioSesionActivity

class PerfilActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPerfilBinding

    var auth: FirebaseAuth = Firebase.auth
    private var user: FirebaseUser = auth.currentUser!!
    private val database = Firebase.database
    private val storage = Firebase.storage
    private val refData = database.getReference("Usuarios/${user.uid}")
    private val refStore = storage.reference.child("Usuarios/${Firebase.auth.currentUser?.uid}/profile.jpg")
    private lateinit var currentUser: UserProfile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.includeBottomBar.homeIcon.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        binding.CerrarSesion.setOnClickListener {
            // Sign out the user
            auth.signOut()

            // Redirect the user to the login activity
            val intent = Intent(this, InicioSesionActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        val alerts = Alerts(this)
        refData.get().addOnSuccessListener { data ->
            if (data.exists()) {
                val user = data.getValue(UserProfile::class.java)
                if (user != null) {
                    binding.username.text = user.name
                    binding.email.text = auth.currentUser?.email
                    currentUser = user

                    // Get the download URL and load the profile image using Glide
                    refStore.downloadUrl.addOnSuccessListener { uri ->
                        Glide.with(this)
                            .load(uri as Uri)
                            .centerCrop()
                            .placeholder(R.drawable.baseline_image_24)
                            .into(binding.profileImage)
                    }.addOnFailureListener {
                        alerts.longToast("Error al cargar imagen de perfil")
                    }
                } else {
                    alerts.longToast("Error al cargar datos de usuario")
                }
            } else {
                alerts.longToast("Error al cargar datos de usuario")
            }
        }
    }
}