package com.src.localite

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.src.localite.databinding.ActivityDestinoBinding
import com.src.localite.databinding.ActivityHomeBinding

class DestinoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDestinoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDestinoBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
