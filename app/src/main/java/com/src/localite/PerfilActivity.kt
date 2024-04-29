package com.src.localite

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.src.localite.databinding.ActivityPerfilBinding

class PerfilActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPerfilBinding

    var auth: FirebaseAuth = Firebase.auth
    private var user: FirebaseUser = auth.currentUser!!
    private val database = Firebase.database
    private val storage = Firebase.storage
    private val refData = database.getReference("Usuarios/${user.uid}")
    private val refStore = storage.reference.child("Usuarios/${Firebase.auth.currentUser?.uid}/profile.jpg")
    private lateinit var currentUser: FirebaseUser
    private lateinit var profileImageUrl: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentUser = auth.currentUser!!


        loadDataFromFirebase()

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

    private fun loadDataFromFirebase() {
        println("Usuario actual ${currentUser.email}")
        val userId = auth.currentUser?.uid
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("Usuarios")

        ref.child(userId!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val nombre = dataSnapshot.child("nombre").value.toString()
                    val telefono = dataSnapshot.child("telefono").value.toString()

                    // Actualizar las vistas con los datos obtenidos
                    binding.username.text = nombre
                    println("User $nombre")
                    binding.telview.text = telefono
                    println("tel $telefono")
                    binding.email.text = currentUser.email
                    println("email ${currentUser.email}")

                    loadProfileImage(userId)

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Error cargando datos: ${databaseError.message}")
            }
        })
    }

    private fun loadProfileImage(userId: String) {
        val profileRef = storage.reference.child("Usuarios").child(userId).child("profile")

        profileRef.downloadUrl.addOnSuccessListener { uri ->
            profileImageUrl = uri.toString()

            // Cargar la imagen usando Glide
            Glide.with(this)
                .load(profileImageUrl)
                .placeholder(R.drawable.load_image)
                .into(binding.profileImage)
        }.addOnFailureListener {
            // Manejar errores al cargar la imagen
        }
    }


}