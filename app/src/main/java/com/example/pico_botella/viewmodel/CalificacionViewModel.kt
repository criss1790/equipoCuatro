package com.example.pico_botella.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pico_botella.domain.modelo.CalificacionModelo
import com.example.pico_botella.domain.repositorio.RepositorioCalificacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ViewModel de la calificación interna: lleva las estrellas seleccionadas en el
// formulario, guarda el envío (que solo redirige a la ficha de referencia de
// Play Store) y decide cuándo pedirle al jugador que califique automáticamente
// después de una acción importante.
class CalificacionViewModel(
    private val repositorioCalificacion: RepositorioCalificacion
) : ViewModel() {

    private val _estrellasSeleccionadas = MutableStateFlow(0)
    val estrellasSeleccionadas: StateFlow<Int> = _estrellasSeleccionadas.asStateFlow()

    // Comentario que el jugador dejó la última vez que calificó (vacío si nunca lo hizo)
    private val _comentarioPrevio = MutableStateFlow("")
    val comentarioPrevio: StateFlow<String> = _comentarioPrevio.asStateFlow()

    private val _mostrarDialogoAutomatico = MutableStateFlow(false)
    val mostrarDialogoAutomatico: StateFlow<Boolean> = _mostrarDialogoAutomatico.asStateFlow()

    private var retosJugados = 0

    init {
        // Carga la calificación previa en una corutina porque el repositorio
        // hace entrada/salida (funciones suspend) y no debe bloquear el hilo principal
        viewModelScope.launch {
            val calificacionPrevia = repositorioCalificacion.obtenerCalificacion()
            _estrellasSeleccionadas.value = calificacionPrevia?.estrellas ?: 0
            _comentarioPrevio.value = calificacionPrevia?.comentario ?: ""
        }
    }

    // Actualiza la cantidad de estrellas que el jugador seleccionó en el formulario
    fun seleccionarEstrella(estrellas: Int) {
        _estrellasSeleccionadas.value = estrellas
    }

    // Guarda localmente las estrellas y el comentario opcional. La calificación
    // "real" ocurre al redirigir a la ficha de Play Store, así que aquí no se exige un mínimo.
    fun guardarCalificacion(comentario: String?) {
        val comentarioLimpio = comentario?.trim()?.takeIf { it.isNotEmpty() }
        viewModelScope.launch {
            repositorioCalificacion.guardarCalificacion(
                CalificacionModelo(
                    estrellas = _estrellasSeleccionadas.value,
                    comentario = comentarioLimpio,
                    fechaEnvio = System.currentTimeMillis(),
                    yaCalifico = true
                )
            )
            _comentarioPrevio.value = comentarioLimpio ?: ""
        }
    }

    // Cuenta una acción importante del jugador (un reto jugado con éxito) y, si aún
    // no se le ha pedido calificar ni ha calificado antes, dispara el diálogo una sola vez
    fun registrarRetoJugado() {
        retosJugados += 1
        if (retosJugados < RETOS_PARA_PEDIR_CALIFICACION) return

        viewModelScope.launch {
            val yaCalifico = repositorioCalificacion.obtenerCalificacion() != null
            if (!repositorioCalificacion.yaSeMostroAutomaticamente() && !yaCalifico) {
                repositorioCalificacion.marcarMostradoAutomaticamente()
                _mostrarDialogoAutomatico.value = true
            }
        }
    }

    // Confirma que ya se atendió la señal de mostrar el diálogo automático, para no repetirla
    fun confirmarDialogoAutomaticoMostrado() {
        _mostrarDialogoAutomatico.value = false
    }

    companion object {
        private const val RETOS_PARA_PEDIR_CALIFICACION = 3
    }
}
