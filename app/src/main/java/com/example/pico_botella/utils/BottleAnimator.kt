package com.example.pico_botella.utils

import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation

// Aplica la animación de parpadeo continuo al botón de la botella.
// Se extrajo de MainActivity para mantener la Activity libre de lógica de animación.
object AnimadorBotella {
    fun iniciarParpadeo(vista: View) {
        val animacionParpadeo = AlphaAnimation(1.0f, 0.2f)
        animacionParpadeo.duration = Constantes.DURACION_PARPADEO_MS
        animacionParpadeo.repeatMode = Animation.REVERSE
        animacionParpadeo.repeatCount = Animation.INFINITE
        vista.startAnimation(animacionParpadeo)
    }
}
