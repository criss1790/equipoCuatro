package com.example.pico_botella.model

import kotlin.random.Random

/**
 * Clase encargada de calcular la física del movimiento de rotación de la botella.
 * Mantiene de forma segura el registro de dónde se detuvo la botella la última vez.
 */

class GeneradorGiro {

    // Guarda el último ángulo acumulado en el que se detuvo la botella (Criterio 4)
    private var ultimoAngulo = 0f

    /**
     * Calcula el siguiente ángulo destino sumando vueltas completas y un residuo aleatorio.
     * @return El ángulo total acumulado en grados al que debe rotar la botella.
     */
    fun calcularSiguienteAngulo(): Float {
        // Generamos un número aleatorio de vueltas completas (entre 3 y 5 vueltas)
        val vueltasCompletas= Random.nextInt(3, 6) //Genera 3,4,5
        val gradosVueltas= vueltasCompletas* 360f

        //Generamos el ángulo final aleatorio ( 0 a 359 grados) donde donde se detendrá (Criterio 3)
        val anguloAleatorio= Random.nextInt(0, 360).toFloat()

        // El nuevo destino arranca desde donde quedó la última vez (Criterio 4)
        val nuevoAnguloDestino = ultimoAngulo + gradosVueltas +anguloAleatorio

        // Actualizamos el registro de la última posición para el siguiente tiro
        ultimoAngulo= nuevoAnguloDestino

        return nuevoAnguloDestino
    }

    /**
     * Resetea el generador a su posición inicial si es necesario.
     */
    fun reiniciar() {
        ultimoAngulo = 0f
    }
}