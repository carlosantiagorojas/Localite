package com.src.localite

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.slider.Slider
import com.src.localite.databinding.ActivityFiltroDistanciaBinding
import kotlin.math.abs

class FiltroDistanciaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFiltroDistanciaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFiltroDistanciaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val typedArray = resources.obtainTypedArray(R.array.initial_slider_values)
        val steps = FloatArray(typedArray.length())
        for (i in 0 until typedArray.length()) {
            steps[i] = typedArray.getFloat(i, 0f)
        }
        typedArray.recycle()

        if (steps.isNotEmpty()) {
            binding.sliderFiltroDistancia.apply {
                valueFrom = steps.first()
                valueTo = steps.last()

                addOnChangeListener(Slider.OnChangeListener { slider, value, fromUser ->
                    if (fromUser) {
                        val nearestValue = steps.minByOrNull { abs(value - it) } ?: value
                        slider.value = nearestValue
                    }
                })
            }
        }

        binding.accountIconFiltroDistancia.setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
        }

        binding.homeIconFiltroDistancia.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        binding.izquierdaFiltroDistancia.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        //TESTING PURPOSES ONLY

        binding.btnDescubrirFiltroDistancia.setOnClickListener {
            val intent = Intent(this, DestinoActivity::class.java)
            startActivity(intent)
        }

        binding.btnDescubrirFiltroDistancia2.setOnClickListener {
            val intent = Intent(this, DestinoActivity::class.java)
            startActivity(intent)
        }
    }
}
