package com.example.pico_botella.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.pico_botella.R
import com.example.pico_botella.utils.Constantes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Activity de splash que se muestra al iniciar la aplicación.
 * Presenta una imagen de botella durante unos segundos y luego navega a MainActivity.
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        ocultarBarrasDelSistema()
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
     * Espera Constantes.TIEMPO_SPLASH_MS milisegundos con una coroutine ligada al ciclo de vida
     * y luego navega a MainActivity. lifecycleScope cancela automáticamente si la Activity se destruye.
     */
    private fun programarNavegacionAutomatica() {
        lifecycleScope.launch {
            delay(Constantes.TIEMPO_SPLASH_MS)
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
