package com.example.pico_botella.view

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.pico_botella.R
import com.example.pico_botella.databinding.ActivityMainBinding
import com.example.pico_botella.model.Reto
import com.example.pico_botella.model.EstadoUI
import com.example.pico_botella.utils.AnimadorBotella
import com.example.pico_botella.utils.Constantes
import com.example.pico_botella.viewmodel.CalificacionViewModel
import com.example.pico_botella.viewmodel.FabricaCalificacionViewModel
import com.example.pico_botella.viewmodel.FabricaRetosViewModel
import com.example.pico_botella.viewmodel.RetosViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

/**
 * Pantalla principal del juego Pico Botella.
 * Muestra toolbar personalizada, botella, contador regresivo y botón parpadeante.
 * La lógica del contador, el sonido y los retos vive en [RetosViewModel].
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var reproductorSonido: MediaPlayer? = null

    private var reproductorGiro: MediaPlayer? = null

    private val viewModel: RetosViewModel by lazy {
        // 1. Creamos el repositorio de retos de prueba directamente (¡no necesita base de datos!)
        val repositorioRetos = com.example.pico_botella.repository.RepositorioRetosFalso()

        // 2. Inicializamos la fábrica pasándole este repositorio falso
        // (Recuerda que el repositorio de Pokémon ya lo pusimos por defecto en el paso anterior)
        val fabrica = FabricaRetosViewModel(repositorioRetos)

        ViewModelProvider(this, fabrica)[RetosViewModel::class.java]
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
            it.animate().scaleX(0.85f).scaleY(0.85f).setDuration(100).withEndAction {
                it.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start()
                mostrarDialogoCalificacion()
            }.start()
        }

        binding.iconoSonido.setOnClickListener {
            it.animate().scaleX(0.85f).scaleY(0.85f).setDuration(100).withEndAction {
                it.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start()
                viewModel.alternarSonido()
                val mensaje = if (viewModel.sonidoActivo.value) {
                    getString(R.string.toast_sonido_on)
                } else {
                    getString(R.string.toast_sonido_off)
                }
                mostrarToast(mensaje)
            }.start()
        }

        binding.iconoInstrucciones.setOnClickListener {
            it.animate().scaleX(0.85f).scaleY(0.85f).setDuration(100).withEndAction {
                it.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start()
                startActivity(Intent(this, InstruccionesActivity::class.java))
            }.start()
        }

        binding.iconoAgregar.setOnClickListener {
            startActivity(Intent(this, RetosActivity::class.java))
        }

        binding.iconoCompartir.setOnClickListener {
            // Animación rápida de toque (achica un poco el ícono y lo vuelve a su tamaño)
            it.animate().scaleX(0.85f).scaleY(0.85f).setDuration(100).withEndAction {
                it.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start()

                // Llamamos al ViewModel para que active el flujo de compartir
                viewModel.compartirAplicacion()
            }.start()
        }

        binding.botonPresioname.setOnClickListener {
            it.animate().scaleX(0.85f).scaleY(0.85f).setDuration(100).withEndAction {
                it.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start()

                // CRITERIO 8: Pausar el sonido de fondo si está activo al iniciar la partida
                if (viewModel.sonidoActivo.value) {
                    reproductorSonido?.pause()
                }

                // Ocultamos tanto el círculo como el texto de abajo
                binding.botonPresioname.visibility = android.view.View.GONE
                binding.textoBotonPresioname.visibility = android.view.View.GONE

                // Le pide al ViewModel que calcule e inicie el giro
                viewModel.iniciarGiroBotella()
            }.start()
        }
    }

    // Aplica animación de parpadeo continuo al botón (lógica en AnimadorBotella)
    private fun iniciarParpadeoBoton() {
        AnimadorBotella.iniciarParpadeo(binding.botonPresioname)
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
                launch {
                    viewModel.eventoCompartir.collect { datos->
                        datos?.let {
                            ejecutarIntentCompartir(it)
                            //Le avisamos al ViewModel que ya lo procesamos para que limpie el estado
                            viewModel.confirmarCompartido()
                        }
                    }
                }
                launch {
                    viewModel.eventoGirarBotella.collect { anguloDestino ->
                        anguloDestino?.let {
                            iniciarAnimacionBotella(it)
                            // Le confirmamos al ViewModel que ya iniciamos la animación
                            viewModel.confirmarGiroIniciado()

                        }
                    }
                }
            }
        }
    }

    // Muestra la tarjeta de calificación (ícono estrella o disparo automático),
    // evitando abrirla dos veces si ya está visible
    private fun mostrarDialogoCalificacion() {
        if (supportFragmentManager.findFragmentByTag(Constantes.ETIQUETA_DIALOGO_CALIFICACION) != null) return
        CalificacionBottomSheet().show(supportFragmentManager, Constantes.ETIQUETA_DIALOGO_CALIFICACION)
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

    // Muestra el reto obtenido en un diálogo personalizado con Pokémon,
    // vuelve a habilitar el botón y confirma al ViewModel
    private fun mostrarContenido(reto: Reto) {
        binding.botonPresioname.isEnabled = true

        // En lugar de un Toast, abrimos el hermoso diálogo personalizado con el Pokémon
        val dialogoReto = RetoDialogFragment(
            reto = reto,
            alCerrar = {
                // Cuando el usuario presione "Cerrar", ejecutamos esta acción:

                // 1. Reanudamos el sonido de fondo si estaba encendido (Criterio 8)
                if (viewModel.sonidoActivo.value) {
                    reproductorSonido?.start()
                }

                // 2. Volvemos a mostrar el círculo y el texto abajo para volver a jugar (Criterio 7)
                binding.botonPresioname.visibility = android.view.View.VISIBLE
                binding.textoBotonPresioname.visibility = android.view.View.VISIBLE
            }
        )

        // Mostramos el diálogo en pantalla
        dialogoReto.show(supportFragmentManager, "dialogo_reto")

        calificacionViewModel.registrarRetoJugado()
        viewModel.confirmarEstadoMostrado()
    }

    // Muestra un mensaje de error y vuelve a hacer visible el botón principal
    private fun mostrarError(mensaje: String) {
        binding.botonPresioname.isEnabled = true
        binding.botonPresioname.visibility = android.view.View.VISIBLE // Volvemos a mostrar el botón en caso de error (Criterio 7)

        // CRITERIO 8: Reanudamos el sonido de fondo en caso de error si estaba encendido
        if (viewModel.sonidoActivo.value) {
            reproductorSonido?.start()
        }
        // Volvemos a mostrar el círculo y el texto abajo (Criterio 7)
        binding.botonPresioname.visibility = android.view.View.VISIBLE
        binding.textoBotonPresioname.visibility = android.view.View.VISIBLE

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
        binding.imagenBotella.clearAnimation() // Limpia la animación de la botella por si seguía girando
        reproductorSonido?.stop()
        reproductorSonido?.release()
        reproductorSonido = null

        // Liberamos también el sonido de giro
        reproductorGiro?.stop()
        reproductorGiro?.release()
        reproductorGiro = null
    }
    // Método para estructurar los datos del Criterio 2 de la HU 10 y lanzar el selector nativo
    private fun ejecutarIntentCompartir(datos: com.example.pico_botella.viewmodel.DatosCompartir){
        // Formateamos el mensaje tal como lo pide la HU 10
        val mensajeACompartir = """
        ${datos.titulo}
        ${datos.eslogan}
        ${datos.enlaceDescarga}
        """.trimIndent()
        // Creamos un Intent nativo para enviar texto plano
        val sendIntent = android.content.Intent().apply {
            action = android.content.Intent.ACTION_SEND
            putExtra(android.content.Intent.EXTRA_TEXT, mensajeACompartir)
            type = "text/plain"
        }
        // Creamos el Chooser (Bottom Sheet nativo) para elegir qué app usar
        val chooserIntent =android.content.Intent.createChooser(sendIntent, "Compartir usando:")
        startActivity(chooserIntent)
    }
    /**
     * Realiza la animación física de rotación de la botella (HU 11 - Criterio 1)
     */
    private fun iniciarAnimacionBotella(anguloDestino: Float) {
        // Duración aleatoria entre 3000 ms (3s) y 5000 ms (5s) (Criterio 1)
        val duracionAleatoria = (3000..5000).random().toLong()

        // INICIAR SONIDO DE GIRO (Criterio 2)
        val recursoGiro = resources.getIdentifier("botella_giro", "raw", packageName)
        if (recursoGiro != 0) {
            // Inicializamos el reproductor secundario
            reproductorGiro = MediaPlayer.create(this, recursoGiro).apply {
                isLooping = true // Hace que el sonido suene continuamente en bucle mientras la botella rote
                start()
            }
        }

        binding.imagenBotella.animate()

            .rotation(anguloDestino) // Gira hasta el angulo calculado
            .setDuration(duracionAleatoria)
            .setInterpolator(android.view.animation.DecelerateInterpolator()) //// Desacelera al final
            .withEndAction {
                //DETENER EL SONIDO AL TERMINAR EL MOVIMIENTO (Criterio 2)
                reproductorGiro?.stop()
                reproductorGiro?.release()
                reproductorGiro = null
                // Iniciamos la cuenta regresiva en el ViewModel (Criterio 5)
                viewModel.iniciarCuentaRegresiva()

            }
            .start()

    }





}
