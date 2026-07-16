package com.example.pico_botella.view

import android.widget.ImageView
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.pico_botella.R
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PanelAnimacionVictoriaEstadoInicialTest {

    @Test
    fun elTextoGanasteEmpiezaInvisibleYSinEscala() {
        ActivityScenario.launch(InstruccionesActivity::class.java).use { escenario ->
            escenario.onActivity { actividad ->
                val texto = actividad.findViewById<TextView>(R.id.textoGanasteVictoria)
                assertEquals("¡Ganaste!", texto.text.toString())
                assertEquals(0f, texto.alpha)
                assertEquals(0f, texto.scaleX)
            }
        }
    }

    @Test
    fun elPanelTieneLasCincoEstrellasYLaBotella() {
        ActivityScenario.launch(InstruccionesActivity::class.java).use { escenario ->
            escenario.onActivity { actividad ->
                assertEquals(
                    android.view.View.VISIBLE,
                    actividad.findViewById<ImageView>(R.id.imagenBotellaVictoria).visibility
                )
                assertEquals(
                    android.view.View.VISIBLE,
                    actividad.findViewById<ImageView>(R.id.estrellaVictoria5).visibility
                )
            }
        }
    }
}
