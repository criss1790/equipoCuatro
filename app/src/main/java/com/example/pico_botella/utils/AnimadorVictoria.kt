package com.example.pico_botella.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.pico_botella.R
import kotlin.random.Random

// Anima el panel ilustrativo de victoria de la pantalla de Instrucciones:
// confeti y estrellas en bucle continuo, y el ciclo botella -> emoji -> texto "¡Ganaste!".
object AnimadorVictoria {

    private const val NUMERO_PIEZAS_CONFETI = 14
    private const val TAG_PIEZA_CONFETI = "pieza_confeti_victoria"
    private val COLORES_CONFETI = listOf(R.color.amarillo, R.color.naranja, R.color.azul, R.color.verde)

    private var cicloActivo = false
    private var animadorCicloActual: AnimatorSet? = null
    private val animadoresSecundarios = mutableListOf<Animator>()

    fun iniciar(contenedor: ViewGroup) {
        detener(contenedor)
        cicloActivo = true

        val piezas = crearPiezasConfeti(contenedor)
        piezas.forEach { contenedor.addView(it) }
        animadoresSecundarios += animarConfeti(piezas, contenedor)

        val estrellas = listOf(
            contenedor.findViewById<ImageView>(R.id.estrellaVictoria1),
            contenedor.findViewById<ImageView>(R.id.estrellaVictoria2),
            contenedor.findViewById<ImageView>(R.id.estrellaVictoria3),
            contenedor.findViewById<ImageView>(R.id.estrellaVictoria4),
            contenedor.findViewById<ImageView>(R.id.estrellaVictoria5)
        )
        animadoresSecundarios += animarEstrellas(estrellas)
        animadoresSecundarios.forEach { it.start() }

        val botella = contenedor.findViewById<ImageView>(R.id.imagenBotellaVictoria)
        val emoji = contenedor.findViewById<TextView>(R.id.textoEmojiVictoria)
        val texto = contenedor.findViewById<TextView>(R.id.textoGanasteVictoria)
        ejecutarCicloPrincipal(botella, emoji, texto)
    }

    fun detener(contenedor: ViewGroup) {
        cicloActivo = false
        animadorCicloActual?.cancel()
        animadorCicloActual = null
        animadoresSecundarios.forEach { it.cancel() }
        animadoresSecundarios.clear()

        for (indice in contenedor.childCount - 1 downTo 0) {
            val hijo = contenedor.getChildAt(indice)
            if (hijo.tag == TAG_PIEZA_CONFETI) {
                contenedor.removeViewAt(indice)
            }
        }
    }

    private fun crearPiezasConfeti(contenedor: ViewGroup): List<View> {
        val contexto = contenedor.context
        val anchoPieza = dpAPx(contexto, 8)
        val altoPieza = dpAPx(contexto, 14)

        return (0 until NUMERO_PIEZAS_CONFETI).map { indice ->
            View(contexto).apply {
                tag = TAG_PIEZA_CONFETI
                layoutParams = FrameLayout.LayoutParams(anchoPieza, altoPieza)
                background = ContextCompat.getDrawable(contexto, R.drawable.confeti_pieza)
                backgroundTintList = ContextCompat.getColorStateList(
                    contexto,
                    COLORES_CONFETI[indice % COLORES_CONFETI.size]
                )
            }
        }
    }

    private fun animarConfeti(piezas: List<View>, contenedor: ViewGroup): List<Animator> {
        val ancho = anchoDisponible(contenedor)
        val alto = altoDisponible(contenedor)

        return piezas.map { pieza ->
            pieza.alpha = 1f
            val anchoPieza = pieza.layoutParams.width
            pieza.translationX = Random.nextInt(0, (ancho - anchoPieza).coerceAtLeast(1)).toFloat()

            val caida = ObjectAnimator.ofFloat(
                pieza, View.TRANSLATION_Y, -pieza.layoutParams.height.toFloat(), alto.toFloat()
            ).apply {
                duration = Random.nextLong(2200L, 3400L)
                startDelay = Random.nextLong(0L, 3000L)
                repeatCount = ValueAnimator.INFINITE
                interpolator = LinearInterpolator()
            }
            val giro = ObjectAnimator.ofFloat(pieza, View.ROTATION, 0f, 360f).apply {
                duration = Random.nextLong(900L, 1600L)
                repeatCount = ValueAnimator.INFINITE
                interpolator = LinearInterpolator()
            }
            AnimatorSet().apply { playTogether(caida, giro) }
        }
    }

    private fun animarEstrellas(estrellas: List<ImageView>): List<Animator> {
        return estrellas.mapIndexed { indice, estrella ->
            ObjectAnimator.ofPropertyValuesHolder(
                estrella,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 0.6f, 1.3f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.6f, 1.3f),
                PropertyValuesHolder.ofFloat(View.ALPHA, 0.4f, 1f)
            ).apply {
                duration = 700L + indice * 150L
                startDelay = indice * 200L
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.REVERSE
            }
        }
    }

    private fun ejecutarCicloPrincipal(botella: ImageView, emoji: TextView, texto: TextView) {
        val set = construirAnimatorSetCiclo(botella, emoji, texto)
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (cicloActivo) ejecutarCicloPrincipal(botella, emoji, texto)
            }
        })
        animadorCicloActual = set
        set.start()
    }

    private fun construirAnimatorSetCiclo(botella: ImageView, emoji: TextView, texto: TextView): AnimatorSet {
        botella.rotation = 0f
        emoji.alpha = 0f
        emoji.scaleX = 0.4f
        emoji.scaleY = 0.4f
        texto.alpha = 0f
        texto.scaleX = 0f
        texto.scaleY = 0f

        val giroBotella = ObjectAnimator.ofFloat(botella, View.ROTATION, 0f, 360f * 4).apply {
            duration = 1800L
            interpolator = DecelerateInterpolator()
        }

        val apareceEmoji = ObjectAnimator.ofPropertyValuesHolder(
            emoji,
            PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f),
            PropertyValuesHolder.ofFloat(View.SCALE_X, 0.4f, 1f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.4f, 1f)
        ).apply {
            duration = 300L
            interpolator = OvershootInterpolator()
        }

        val apareceTexto = ObjectAnimator.ofPropertyValuesHolder(
            texto,
            PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f),
            PropertyValuesHolder.ofFloat(View.SCALE_X, 0f, 1f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f, 1f)
        ).apply {
            duration = 600L
            interpolator = OvershootInterpolator()
        }

        val espera = ValueAnimator.ofFloat(0f, 1f).apply { duration = 1000L }

        val desvanece = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(texto, View.ALPHA, 1f, 0f).apply { duration = 400L },
                ObjectAnimator.ofFloat(emoji, View.ALPHA, 1f, 0f).apply { duration = 400L }
            )
        }

        return AnimatorSet().apply {
            playSequentially(giroBotella, apareceEmoji, apareceTexto, espera, desvanece)
        }
    }

    private fun anchoDisponible(contenedor: ViewGroup): Int =
        if (contenedor.width > 0) contenedor.width else dpAPx(contenedor.context, 300)

    private fun altoDisponible(contenedor: ViewGroup): Int =
        if (contenedor.height > 0) contenedor.height else dpAPx(contenedor.context, 200)

    private fun dpAPx(contexto: Context, dp: Int): Int =
        (dp * contexto.resources.displayMetrics.density).toInt()
}
