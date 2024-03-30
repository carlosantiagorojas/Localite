package com.src.localite

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.src.localite.databinding.ActivityInicioSesionBinding

class InicioSesionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInicioSesionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicioSesionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.IniciaSesion.setOnClickListener(){
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        binding.textoRegistro.setOnClickListener(){
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }
    }
}