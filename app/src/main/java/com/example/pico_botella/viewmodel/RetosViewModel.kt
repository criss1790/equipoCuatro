package com.example.pico_botella.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pico_botella.model.Reto
import com.example.pico_botella.repository.RepositorioRetos
import com.example.pico_botella.model.EstadoUI
import com.example.pico_botella.utils.Constantes
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ViewModel de la pantalla principal: lleva el contador regresivo,
// el estado del sonido y la obtención de un reto al presionar el botón.
// El repositorio llega por constructor (inyección de dependencias) a través
// de FabricaRetosViewModel, para poder cambiarlo por otro en tests o a futuro.
class RetosViewModel(
    private val repositorioRetos: RepositorioRetos
) : ViewModel() {

    private val _valorContador = MutableStateFlow(Constantes.VALOR_INICIAL_CONTADOR)
    val valorContador: StateFlow<Int> = _valorContador.asStateFlow()

    private val _sonidoActivo = MutableStateFlow(true)
    val sonidoActivo: StateFlow<Boolean> = _sonidoActivo.asStateFlow()

    private val _estadoReto = MutableStateFlow<EstadoUI<Reto>>(EstadoUI.Vacio)
    val estadoReto: StateFlow<EstadoUI<Reto>> = _estadoReto.asStateFlow()

    init {
        iniciarContadorRegresivo()
    }

    // Cuenta hacia atrás desde el valor inicial hasta cero usando coroutines
    private fun iniciarContadorRegresivo() {
        viewModelScope.launch {
            _valorContador.value = Constantes.VALOR_INICIAL_CONTADOR
            while (_valorContador.value > 0) {
                delay(Constantes.MILISEGUNDOS_POR_PASO)
                _valorContador.value -= 1
            }
        }
    }

    // Cambia el estado del sonido entre activo e inactivo
    fun alternarSonido() {
        _sonidoActivo.value = !_sonidoActivo.value
    }

    // Pide al repositorio los retos disponibles y muestra uno elegido al azar
    fun cargarRetoAleatorio() {
        viewModelScope.launch {
            _estadoReto.value = EstadoUI.Cargando
            try {
                repositorioRetos.obtenerRetos().collect { retos ->
                    if (retos.isEmpty()) {
                        _estadoReto.value = EstadoUI.Vacio
                    } else {
                        _estadoReto.value = EstadoUI.Exito(retos.random())
                    }
                }
            } catch (excepcion: CancellationException) {
                // La cancelación es el mecanismo normal de las corutinas para detenerse:
                // se relanza para no confundirla con un error de datos
                throw excepcion
            } catch (excepcion: Exception) {
                _estadoReto.value = EstadoUI.Error(excepcion.message ?: Constantes.MENSAJE_ERROR_DESCONOCIDO)
            }
        }
    }

    // Vuelve al estado Vacio cuando la UI ya mostró el resultado (éxito o error).
    // Sin esto, el StateFlow re-emitiría el último valor al volver a coleccionar
    // (por ejemplo al rotar la pantalla) y el reto se repetiría.
    fun confirmarEstadoMostrado() {
        _estadoReto.value = EstadoUI.Vacio
    }
}
