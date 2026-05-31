package com.example.pico_botella

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Activity de splash que se muestra al iniciar la aplicación.
 * Presenta una animación Lottie de botella durante 5 segundos y luego navega a MainActivity.
 */
class SplashActivity : AppCompatActivity() {

    // Duración exacta del splash: 5 segundos.
    private val TIEMPO_SPLASH: Long = 5000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        ocultarBarrasDelSistema()
        iniciarAnimacionBotella()
        programarNavegacionAutomatica()
    }

    /**
     * Oculta barras de estado y navegación para una experiencia de pantalla completa.
     */
    private fun ocultarBarrasDelSistema() {
        supportActionBar?.hide()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val controlador: WindowInsetsController = window.insetsController ?: return
            controlador.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            controlador.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
        }
    }

    /**
     * Obtiene la vista Lottie e inicia la animación de la botella en bucle infinito.
     */
    private fun iniciarAnimacionBotella() {
        val imagenBotella: com.airbnb.lottie.LottieAnimationView = findViewById(R.id.imagenBotella)
        imagenBotella.setAnimation(R.raw.botella)
        imagenBotella.repeatCount = com.airbnb.lottie.LottieDrawable.INFINITE
        imagenBotella.playAnimation()
    }

    /**
     * Espera TIEMPO_SPLASH milisegundos con una coroutine ligada al ciclo de vida
     * y luego navega a MainActivity. lifecycleScope cancela automáticamente si la Activity se destruye.
     */
    private fun programarNavegacionAutomatica() {
        lifecycleScope.launch {
            delay(TIEMPO_SPLASH)
            abrirPantallaPrincipal()
        }
    }

    /**
     * Crea el Intent hacia MainActivity, lo lanza y cierra SplashActivity
     * para que el usuario no pueda regresar a ella con el botón Atrás.
     */
    private fun abrirPantallaPrincipal() {
        val intentPantallaPrincipal = Intent(this, MainActivity::class.java)
        startActivity(intentPantallaPrincipal)
        finish()
    }
}
