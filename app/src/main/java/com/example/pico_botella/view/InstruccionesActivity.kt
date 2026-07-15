package com.example.pico_botella.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pico_botella.databinding.ActivityInstruccionesBinding

/**
 * Pantalla de Instrucciones del juego (HU 5.0).
 * Muestra las reglas y una animación de triunfo.
 */
class InstruccionesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInstruccionesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        
        binding = ActivityInstruccionesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Criterio 3: Botón de flecha atrás para volver al home
        binding.btnAtras.setOnClickListener {
            // Al finalizar la actividad, MainActivity volverá a primer plano.
            // La lógica de reanudar el audio ya está manejada en el onResume de MainActivity.
            finish()
        }
    }
}
