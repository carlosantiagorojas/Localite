package com.src.localite

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.src.localite.databinding.ActivityFiltroCategoriasBinding

class FiltroCategoriaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFiltroCategoriasBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFiltroCategoriasBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.izquierdaFiltroCategoria.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        binding.homeIconCategoria.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        binding.accountIconFiltroCategoria.setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
        }

        binding.cartaEventos.setOnClickListener {
            val intent = Intent(this, FiltroCategoriaEspecificaActivity::class.java)
            intent.putExtra("Categoria", "Eventos")
            startActivity(intent)
        }

        binding.cartaEntretenimiento.setOnClickListener {
            val intent = Intent(this, FiltroCategoriaEspecificaActivity::class.java)
            intent.putExtra("Categoria", "Entretenimiento")
            startActivity(intent)
        }

        binding.cartaGastronomia.setOnClickListener{
            val intent = Intent(this, FiltroCategoriaEspecificaActivity::class.java)
            intent.putExtra("Categoria", "Gastronomia")
            startActivity(intent)
        }

        binding.cartaMercados.setOnClickListener {
            intent.putExtra("Categoria", "Mercados")
            val intent = Intent(this, FiltroCategoriaEspecificaActivity::class.java)
            startActivity(intent)
        }

        binding.cartaHistoricosYCulturales.setOnClickListener {
            val intent = Intent(this, FiltroCategoriaEspecificaActivity::class.java)
            intent.putExtra("Categoria", "Historicos y culturales")
            startActivity(intent)
        }

        binding.cartaNaturalezaYParques.setOnClickListener {
            val intent = Intent(this, FiltroCategoriaEspecificaActivity::class.java)
            intent.putExtra("Categoria", "Naturaleza y parques")
            startActivity(intent)
        }
    }
}