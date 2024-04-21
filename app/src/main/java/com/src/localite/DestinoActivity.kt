package com.src.localite

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.src.localite.databinding.ActivityDestinoBinding
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.recyclerview.widget.LinearLayoutManager


class DestinoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDestinoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDestinoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerview.layoutManager = LinearLayoutManager(this)

        val destino = intent.getParcelableExtra<Destino>("destino")

        binding.infoGeneral.text =  destino?.General
        binding.actividadesUbicacion.text =  destino?.Actividades
        binding.historiaUbicacion.text =  destino?.Historia
        binding.descripcion.text = destino?.Descripcion
        binding.titulo.text = destino?.Ciudad + ", " + destino?.Pais

        val storageReference = FirebaseStorage.getInstance().reference.child("Lugares/${destino?.nombre}/Secundaria.jpg")
        storageReference.downloadUrl.addOnSuccessListener { uri: Uri ->
            Glide.with(this)
                .load(uri)
                .placeholder(R.drawable.load_image)
                .error(R.drawable.error_image)
                .into(binding.imagenDestino)
            println("Imagen cargada con éxito desde: $uri")
        }.addOnFailureListener { exception ->
            println("Error al cargar la imagen: ${exception.message}")
            binding.imagenDestino.setImageResource(R.drawable.error_image)
        }

        // Extract the list of suggestions from the "destino" object
        val sugerencias = destino?.Sugeridas?.filterNotNull() ?: listOf()

        // Print the size of the sugerencias list
        Log.d(TAG, "Size of sugerencias list: ${sugerencias.size}")

        // Set the LayoutManager for the RecyclerView
        binding.recyclerview.layoutManager = LinearLayoutManager(this)

        // Create a RecyclerView Adapter to populate the RecyclerView
        val adapter = SugerenciasAdapter(sugerencias)

        // Set the LayoutManager for the RecyclerView
        binding.recyclerview.layoutManager = LinearLayoutManager(this)

        // Set the adapter for the RecyclerView
        binding.recyclerview.adapter = adapter

        // Configurar clics de botones
        binding.btnInfoGeneral.setOnClickListener {
            // Alternar la visibilidad del TextView infoGeneral
            binding.infoGeneral.visibility = if (binding.infoGeneral.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }

        binding.btnHistoria.setOnClickListener {


            // Alternar la visibilidad del TextView historiaUbicacion
            binding.historiaUbicacion.visibility = if (binding.historiaUbicacion.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }

        binding.btnActividades.setOnClickListener {
            binding.tituloActividades.visibility = if (binding.tituloActividades.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }

            // Alternar la visibilidad del TextView actividadesUbicacion
            binding.actividadesUbicacion.visibility = if (binding.actividadesUbicacion.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }

            // Toggle the visibility of the new button
            binding.btnSugiereActividad.visibility = if (binding.btnSugiereActividad.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }

            // Toggle the visibility of the ListView
            binding.recyclerview.visibility = if (binding.recyclerview.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }

        binding.btnSugiereActividad.setOnClickListener {
            val intent = Intent(this, SugerirActividadActivity::class.java).apply {
                putExtra("destino", destino) // "destino" es la clave para acceder al objeto en la actividad de destino
            }
            startActivity(intent)
        }

        // Configurar clics de iconos de la barra inferior
        binding.includeBottomBar.accountIcon.setOnClickListener{
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
        }

        binding.includeBottomBar.homeIcon.setOnClickListener{
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        binding.includeTopBar.izquierda.setOnClickListener{
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}
