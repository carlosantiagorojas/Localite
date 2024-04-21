package com.src.localite

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.src.localite.databinding.ActivitySugerirActividadBinding

class SugerirActividadActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySugerirActividadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySugerirActividadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val destino = intent.getParcelableExtra<Destino>("destino")

        binding.btnSugiereActividad.setOnClickListener {
            val intent = Intent(this, DestinoActivity::class.java).apply {
                putExtra("destino", destino) // "destino" es la clave para acceder al objeto en la actividad de destino
            }
            startActivity(intent)
        }

        // Configurar clics de iconos de la barra inferior
        binding.includeBottomBar.accountIcon.setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
        }

        binding.includeBottomBar.homeIcon.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        binding.includeTopBar.izquierda.setOnClickListener {
            val intent = Intent(this, DestinoActivity::class.java).apply {
                putExtra("destino", destino) // "destino" es la clave para acceder al objeto en la actividad de destino
            }
            startActivity(intent)
        }
    }
}