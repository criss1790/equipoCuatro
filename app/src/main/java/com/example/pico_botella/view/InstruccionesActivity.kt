package com.example.pico_botella.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pico_botella.databinding.ActivityInstruccionesBinding

/**
 * Pantalla que muestra las instrucciones del juego (HU 5.0).
 */
class InstruccionesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInstruccionesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityInstruccionesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.iconoVolver.setOnClickListener {
            finish()
        }
    }
}
