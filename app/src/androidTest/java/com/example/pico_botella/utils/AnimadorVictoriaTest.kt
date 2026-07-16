package com.example.pico_botella.utils

import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.pico_botella.R
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AnimadorVictoriaTest {

    private val contexto = InstrumentationRegistry.getInstrumentation().targetContext

    private fun construirContenedorDePrueba(): FrameLayout {
        val contenedor = FrameLayout(contexto)
        contenedor.layout(0, 0, 300, 200)
        contenedor.addView(ImageView(contexto).apply { id = R.id.imagenBotellaVictoria })
        contenedor.addView(TextView(contexto).apply { id = R.id.textoEmojiVictoria })
        contenedor.addView(TextView(contexto).apply { id = R.id.textoGanasteVictoria })
        contenedor.addView(ImageView(contexto).apply { id = R.id.estrellaVictoria1 })
        contenedor.addView(ImageView(contexto).apply { id = R.id.estrellaVictoria2 })
        contenedor.addView(ImageView(contexto).apply { id = R.id.estrellaVictoria3 })
        contenedor.addView(ImageView(contexto).apply { id = R.id.estrellaVictoria4 })
        contenedor.addView(ImageView(contexto).apply { id = R.id.estrellaVictoria5 })
        return contenedor
    }

    @Test
    fun iniciarAgregaCatorcePiezasDeConfetiConTagCorrecto() {
        val contenedor = construirContenedorDePrueba()
        val hijosAntes = contenedor.childCount

        AnimadorVictoria.iniciar(contenedor)

        assertEquals(hijosAntes + 14, contenedor.childCount)
        for (indice in hijosAntes until contenedor.childCount) {
            assertEquals("pieza_confeti_victoria", contenedor.getChildAt(indice).tag)
        }

        AnimadorVictoria.detener(contenedor)
    }

    @Test
    fun detenerQuitaTodasLasPiezasDeConfeti() {
        val contenedor = construirContenedorDePrueba()
        val hijosAntes = contenedor.childCount

        AnimadorVictoria.iniciar(contenedor)
        AnimadorVictoria.detener(contenedor)

        assertEquals(hijosAntes, contenedor.childCount)
    }

    @Test
    fun iniciarLlamadoDosVecesSeguidasNoDuplicaElConfeti() {
        val contenedor = construirContenedorDePrueba()
        val hijosAntes = contenedor.childCount

        AnimadorVictoria.iniciar(contenedor)
        AnimadorVictoria.iniciar(contenedor)

        assertEquals(hijosAntes + 14, contenedor.childCount)
        AnimadorVictoria.detener(contenedor)
    }
}
