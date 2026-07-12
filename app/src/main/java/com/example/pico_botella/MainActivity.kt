package com.example.pico_botella

import android.media.MediaPlayer
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.pico_botella.databinding.ActivityMainBinding
import com.example.pico_botella.domain.modelo.Reto
import com.example.pico_botella.ui.calificacion.CalificacionBottomSheet
import com.example.pico_botella.ui.estado.EstadoUI
import com.example.pico_botella.viewmodel.CalificacionViewModel
import com.example.pico_botella.viewmodel.FabricaCalificacionViewModel
import com.example.pico_botella.viewmodel.FabricaRetosViewModel
import com.example.pico_botella.viewmodel.RetosViewModel
import kotlinx.coroutines.launch

/**
 * Pantalla principal del juego Pico Botella.
 * Muestra toolbar personalizada, botella, contador regresivo y botón parpadeante.
 * La lógica del contador, el sonido y los retos vive en [RetosViewModel].
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var reproductorSonido: MediaPlayer? = null

    private val viewModel: RetosViewModel by lazy {
        ViewModelProvider(this, FabricaRetosViewModel())[RetosViewModel::class.java]
    }

    private val calificacionViewModel: CalificacionViewModel by lazy {
        ViewModelProvider(
            this,
            FabricaCalificacionViewModel(applicationContext)
        )[CalificacionViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Desactiva edge-to-edge para que el contenido quede debajo de la barra de estado
        WindowCompat.setDecorFitsSystemWindows(window, true)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarToolbar()
        iniciarParpadeoBoton()
        iniciarSonidoFondo()
        observarEstado()
    }

    // Asigna listeners a cada ícono de la toolbar y al botón principal
    private fun configurarToolbar() {
        binding.iconoEstrella.setOnClickListener {
            mostrarDialogoCalificacion()
        }

        binding.iconoSonido.setOnClickListener {
            viewModel.alternarSonido()
            val mensaje = if (viewModel.sonidoActivo.value) {
                getString(R.string.toast_sonido_on)
            } else {
                getString(R.string.toast_sonido_off)
            }
            mostrarToast(mensaje)
        }

        binding.iconoInstrucciones.setOnClickListener {
            mostrarToast(getString(R.string.toast_instrucciones))
        }

        binding.iconoAgregar.setOnClickListener {
            mostrarToast(getString(R.string.toast_agregar))
        }

        binding.iconoCompartir.setOnClickListener {
            mostrarToast(getString(R.string.toast_compartir))
        }

        binding.botonPresioname.setOnClickListener {
            viewModel.cargarRetoAleatorio()
        }
    }

    // Aplica animación de parpadeo continuo al botón
    private fun iniciarParpadeoBoton() {
        val animacionParpadeo = AlphaAnimation(1.0f, 0.2f)
        animacionParpadeo.duration = 600
        animacionParpadeo.repeatMode = Animation.REVERSE
        animacionParpadeo.repeatCount = Animation.INFINITE
        binding.botonPresioname.startAnimation(animacionParpadeo)
    }

    // Prepara y reproduce el sonido de fondo en bucle.
    // Si res/raw/sonido_fondo.mp3 no existe, retorna sin error.
    private fun iniciarSonidoFondo() {
        val recursoSonido = resources.getIdentifier("sonido_fondo", "raw", packageName)
        if (recursoSonido == 0) return

        reproductorSonido = MediaPlayer.create(this, recursoSonido)
        reproductorSonido?.apply {
            isLooping = true
            start()
        }
    }

    // Observa los estados expuestos por el ViewModel y actualiza la UI en consecuencia
    private fun observarEstado() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.valorContador.collect { valor ->
                        binding.textoContador.text = valor.toString()
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
                launch {
                    calificacionViewModel.mostrarDialogoAutomatico.collect { mostrar ->
                        if (mostrar) {
                            mostrarDialogoCalificacion()
                            calificacionViewModel.confirmarDialogoAutomaticoMostrado()
                        }
                    }
                }
            }
        }
    }

    // Muestra la tarjeta de calificación (ícono estrella o disparo automático),
    // evitando abrirla dos veces si ya está visible
    private fun mostrarDialogoCalificacion() {
        if (supportFragmentManager.findFragmentByTag(ETIQUETA_DIALOGO_CALIFICACION) != null) return
        CalificacionBottomSheet().show(supportFragmentManager, ETIQUETA_DIALOGO_CALIFICACION)
    }

    // Sincroniza el reproductor y el ícono con el estado de sonido del ViewModel
    private fun actualizarSonido(activo: Boolean) {
        if (activo) {
            reproductorSonido?.start()
            binding.iconoSonido.setImageResource(R.drawable.sonido)
        } else {
            reproductorSonido?.pause()
            binding.iconoSonido.setImageResource(R.drawable.asonido)
        }
    }

    // Deshabilita el botón mientras se obtiene un reto
    private fun mostrarCargando() {
        binding.botonPresioname.isEnabled = false
    }

    // Muestra el reto obtenido, vuelve a habilitar el botón y confirma al ViewModel
    // que el estado ya se consumió (evita repetir el Toast al rotar la pantalla)
    private fun mostrarContenido(reto: Reto) {
        binding.botonPresioname.isEnabled = true
        mostrarToast(reto.texto)
        calificacionViewModel.registrarRetoJugado()
        viewModel.confirmarEstadoMostrado()
    }

    // Muestra un mensaje de error, vuelve a habilitar el botón y confirma el consumo del estado
    private fun mostrarError(mensaje: String) {
        binding.botonPresioname.isEnabled = true
        mostrarToast(mensaje)
        viewModel.confirmarEstadoMostrado()
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
        binding.botonPresioname.clearAnimation()
        reproductorSonido?.stop()
        reproductorSonido?.release()
        reproductorSonido = null
    }

    companion object {
        private const val ETIQUETA_DIALOGO_CALIFICACION = "dialogo_calificacion"
    }
}
