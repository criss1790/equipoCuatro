package com.example.pico_botella

import android.media.MediaPlayer
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.pico_botella.domain.modelo.Reto
import com.example.pico_botella.ui.estado.EstadoUI
import com.example.pico_botella.viewmodel.FabricaRetosViewModel
import com.example.pico_botella.viewmodel.RetosViewModel
import kotlinx.coroutines.launch

/**
 * Pantalla principal del juego Pico Botella.
 * Muestra toolbar personalizada, botella, contador regresivo y botón parpadeante.
 * La lógica del contador, el sonido y los retos vive en [RetosViewModel].
 */
class MainActivity : AppCompatActivity() {

    private lateinit var textoContador: TextView
    private lateinit var botonPresioname: Button
    private lateinit var iconoSonido: ImageView

    private var reproductorSonido: MediaPlayer? = null

    private val viewModel: RetosViewModel by lazy {
        ViewModelProvider(this, FabricaRetosViewModel())[RetosViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Desactiva edge-to-edge para que el contenido quede debajo de la barra de estado
        WindowCompat.setDecorFitsSystemWindows(window, true)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        inicializarVistas()
        configurarToolbar()
        iniciarParpadeoBoton()
        iniciarSonidoFondo()
        observarEstado()
    }

    // Vincula las vistas del layout con las variables
    private fun inicializarVistas() {
        textoContador = findViewById(R.id.textoContador)
        botonPresioname = findViewById(R.id.botonPresioname)
        iconoSonido = findViewById(R.id.iconoSonido)
    }

    // Asigna listeners a cada ícono de la toolbar y al botón principal
    private fun configurarToolbar() {
        val iconoEstrella: ImageView = findViewById(R.id.iconoEstrella)
        val iconoInstrucciones: ImageView = findViewById(R.id.iconoInstrucciones)
        val iconoAgregar: ImageView = findViewById(R.id.iconoAgregar)
        val iconoCompartir: ImageView = findViewById(R.id.iconoCompartir)
        val iconoSonido: ImageView = findViewById(R.id.iconoSonido)

        iconoEstrella.setOnClickListener {
            mostrarToast(getString(R.string.toast_estrella))
        }

        iconoSonido.setOnClickListener {
            viewModel.alternarSonido()
            val mensaje = if (viewModel.sonidoActivo.value) {
                getString(R.string.toast_sonido_on)
            } else {
                getString(R.string.toast_sonido_off)
            }
            mostrarToast(mensaje)
        }

        iconoInstrucciones.setOnClickListener {
            mostrarToast(getString(R.string.toast_instrucciones))
        }

        iconoAgregar.setOnClickListener {
            mostrarToast(getString(R.string.toast_agregar))
        }

        iconoCompartir.setOnClickListener {
            mostrarToast(getString(R.string.toast_compartir))
        }

        botonPresioname.setOnClickListener {
            viewModel.cargarRetoAleatorio()
        }
    }

    // Aplica animación de parpadeo continuo al botón
    private fun iniciarParpadeoBoton() {
        val animacionParpadeo = AlphaAnimation(1.0f, 0.2f)
        animacionParpadeo.duration = 600
        animacionParpadeo.repeatMode = Animation.REVERSE
        animacionParpadeo.repeatCount = Animation.INFINITE
        botonPresioname.startAnimation(animacionParpadeo)
    }

    // Prepara y reproduce el sonido de fondo en bucle.
    // Si res/raw/sonido_fondo.mp3 no existe, retorna sin error.
    private fun iniciarSonidoFondo() {
        val recursoSonido = resources.getIdentifier("sonido_fondo", "raw", packageName)
        if (recursoSonido == 0) return

        reproductorSonido = MediaPlayer.create(this, recursoSonido)
        if (reproductorSonido == null) return

        reproductorSonido!!.isLooping = true
        reproductorSonido!!.start()
    }

    // Observa los estados expuestos por el ViewModel y actualiza la UI en consecuencia
    private fun observarEstado() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.valorContador.collect { valor ->
                        textoContador.text = valor.toString()
                    }
                }
                launch {
                    viewModel.sonidoActivo.collect { activo ->
                        actualizarSonido(activo)
                    }
                }
                launch {
                    viewModel.estadoReto.collect { estado ->
                        when (estado) {
                            is EstadoUI.Cargando -> mostrarCargando()
                            is EstadoUI.Exito -> mostrarContenido(estado.datos)
                            is EstadoUI.Error -> mostrarError(estado.mensaje)
                            is EstadoUI.Vacio -> Unit
                        }
                    }
                }
            }
        }
    }

    // Sincroniza el reproductor y el ícono con el estado de sonido del ViewModel
    private fun actualizarSonido(activo: Boolean) {
        if (activo) {
            reproductorSonido?.start()
            iconoSonido.setImageResource(R.drawable.sonido)
        } else {
            reproductorSonido?.pause()
            iconoSonido.setImageResource(R.drawable.asonido)
        }
    }

    // Deshabilita el botón mientras se obtiene un reto
    private fun mostrarCargando() {
        botonPresioname.isEnabled = false
    }

    // Muestra el reto obtenido y vuelve a habilitar el botón
    private fun mostrarContenido(reto: Reto) {
        botonPresioname.isEnabled = true
        mostrarToast(reto.texto)
    }

    // Muestra un mensaje de error y vuelve a habilitar el botón
    private fun mostrarError(mensaje: String) {
        botonPresioname.isEnabled = true
        mostrarToast(mensaje)
    }

    private fun mostrarToast(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    // Pausa el sonido cuando la Activity pierde foco
    override fun onPause() {
        super.onPause()
        if (viewModel.sonidoActivo.value) {
            reproductorSonido?.pause()
        }
    }

    // Reanuda el sonido cuando la Activity recupera foco (solo si estaba activo)
    override fun onResume() {
        super.onResume()
        if (viewModel.sonidoActivo.value) {
            reproductorSonido?.start()
        }
    }

    // Libera todos los recursos al destruir la Activity
    override fun onDestroy() {
        super.onDestroy()
        botonPresioname.clearAnimation()
        reproductorSonido?.stop()
        reproductorSonido?.release()
        reproductorSonido = null
    }
}
