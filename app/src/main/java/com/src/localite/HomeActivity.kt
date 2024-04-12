package com.src.localite

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.src.localite.databinding.ActivityHomeBinding
private lateinit var binding: ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityHomeBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.includeBottomBar.accountIcon.setOnClickListener{
        val intent = Intent(this, PerfilActivity::class.java)
        startActivity(intent)
    }

    binding.btnDescubrir.setOnClickListener {
        val intent = Intent(this, DestinoActivity::class.java)
        startActivity(intent)
    }

    binding.btnCercanos.setOnClickListener {
        val intent = Intent(this, FiltroDistanciaActivity::class.java)
        startActivity(intent)
    }

    binding.btnCategorias.setOnClickListener {
        val intent = Intent(this, FiltroCategoriaActivity::class.java)
        startActivity(intent)
    }
}

}