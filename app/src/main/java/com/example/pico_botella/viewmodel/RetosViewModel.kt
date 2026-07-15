package com.example.pico_botella.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pico_botella.model.Reto
import com.example.pico_botella.repository.RepositorioRetos
import com.example.pico_botella.model.EstadoUI
import com.example.pico_botella.model.GeneradorGiro
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

    // Instanciamos nuestro generador de giro matemático
    private val generadorGiro = GeneradorGiro()


    // El flujo que observará la MainActivity para saber a qué ángulo girar
    private val _eventoGirarBotella = MutableStateFlow<Float?>(null)
    val eventoGirarBotella: StateFlow<Float?> = _eventoGirarBotella.asStateFlow()

    private val _valorContador = MutableStateFlow(Constantes.VALOR_INICIAL_CONTADOR)
    val valorContador: StateFlow<Int> = _valorContador.asStateFlow()

    private val _sonidoActivo = MutableStateFlow(true)
    val sonidoActivo: StateFlow<Boolean> = _sonidoActivo.asStateFlow()

    private val _estadoReto = MutableStateFlow<EstadoUI<Reto>>(EstadoUI.Vacio)
    val estadoReto: StateFlow<EstadoUI<Reto>> = _estadoReto.asStateFlow()

    // 1. El flujo de datos privado (MutableStateFlow) que guarda temporalmente la información a compartir
    private val _eventoCompartir = MutableStateFlow<DatosCompartir?>(null)
    // 2. El flujo público de solo lectura para que la MainActivity pueda observar los cambios
    val eventoCompartir: StateFlow<DatosCompartir?> = _eventoCompartir.asStateFlow()
    /**
    * Prepara los datos requeridos y activa el flujo de compartir.
    * Se llamará cuando el usuario presione el icono de compartir en la toolbar.
    */
    fun compartirAplicacion () {
        _eventoCompartir.value = DatosCompartir (
            titulo = "App pico botella",
            eslogan = "Solo los valientes lo juegan !!",
            enlaceDescarga = "https://play.google.com/store/apps/details?id=com.nequi.MobileApp&hl=es_419&gl=es"
        )
    }
    /**
     * Resetea el estado del flujo de compartir a 'null' una vez que la interfaz (MainActivity)
     * ya ha procesado y mostrado el Bottom Sheet nativo.
     * Esto previene que se vuelva a abrir el menú de compartir de forma automática al rotar la pantalla.
     */
    fun confirmarCompartido() {
        _eventoCompartir.value = null
    }
    /**
     * Inicia el proceso de giro calculando el ángulo destino de forma limpia.
     */
    fun iniciarGiroBotella() {
        // Le delegamos todo el cálculo matemático a la clase especializada
        val anguloDestino = generadorGiro.calcularSiguienteAngulo()
        // Emitimos el ángulo hacia la vista
        _eventoGirarBotella.value = anguloDestino
    }
    /**
     * Limpia el estado una vez que la MainActivity comenzó a girar la botella
     */
    fun confirmarGiroIniciado() {
        _eventoGirarBotella.value = null

    }





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
    /**
     * Ejecuta una cuenta regresiva asíncrona de 3 a 0 segundo a segundo (HU 11 - Criterio 5)
     */
    fun iniciarCuentaRegresiva() {
        viewModelScope.launch {
            // Recorremos los números del 3 al 0 en orden descendente
            for (segundo in 3 downTo 0) {
                _valorContador.value = segundo
                delay(1000) // Pausa la corrutina por 1000 milisegundos (1 segundo)
            }
            //Criterio 6
            cargarRetoAleatorio()
        }
    }

}

data class DatosCompartir(
    val titulo: String,
    val eslogan: String,
    val enlaceDescarga: String
)
