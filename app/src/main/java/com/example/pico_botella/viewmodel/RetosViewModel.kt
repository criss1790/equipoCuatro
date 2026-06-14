package com.example.pico_botella.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pico_botella.data.repositorio.RepositorioRetosFalso
import com.example.pico_botella.domain.modelo.Reto
import com.example.pico_botella.domain.repositorio.RepositorioRetos
import com.example.pico_botella.ui.estado.EstadoUI
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ViewModel de la pantalla principal: lleva el contador regresivo,
// el estado del sonido y la obtención de un reto al presionar el botón.
class RetosViewModel(
    private val repositorioRetos: RepositorioRetos = RepositorioRetosFalso()
) : ViewModel() {

    private val _valorContador = MutableStateFlow(VALOR_INICIAL_CONTADOR)
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
            _valorContador.value = VALOR_INICIAL_CONTADOR
            while (_valorContador.value > 0) {
                delay(MILISEGUNDOS_POR_PASO)
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
            } catch (excepcion: Exception) {
                _estadoReto.value = EstadoUI.Error(excepcion.message ?: "Ocurrió un error inesperado")
            }
        }
    }

    companion object {
        private const val VALOR_INICIAL_CONTADOR = 3
        private const val MILISEGUNDOS_POR_PASO = 1000L
    }
}
