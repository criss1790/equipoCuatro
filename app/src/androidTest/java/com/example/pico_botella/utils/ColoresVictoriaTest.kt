package com.example.pico_botella.utils

import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.pico_botella.R
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ColoresVictoriaTest {

    private val contexto = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun colorAmarilloTieneElHexEsperado() {
        assertEquals(Color.parseColor("#FFD500"), ContextCompat.getColor(contexto, R.color.amarillo))
    }

    @Test
    fun colorAzulTieneElHexEsperado() {
        assertEquals(Color.parseColor("#2E9CFF"), ContextCompat.getColor(contexto, R.color.azul))
    }

    @Test
    fun colorVerdeTieneElHexEsperado() {
        assertEquals(Color.parseColor("#2ECC71"), ContextCompat.getColor(contexto, R.color.verde))
    }
}
