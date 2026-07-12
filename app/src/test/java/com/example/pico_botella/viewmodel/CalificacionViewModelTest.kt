package com.example.pico_botella.viewmodel

import com.example.pico_botella.domain.modelo.CalificacionModelo
import com.example.pico_botella.domain.repositorio.RepositorioCalificacion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// Pruebas unitarias de CalificacionViewModel usando un repositorio en memoria,
// sin depender de SharedPreferences ni de un dispositivo Android.
@OptIn(ExperimentalCoroutinesApi::class)
class CalificacionViewModelTest {

    // Implementación en memoria del repositorio: mismo contrato (suspend),
    // pero guardando en variables en lugar de SharedPreferences
    private class RepositorioEnMemoria : RepositorioCalificacion {
        var calificacionGuardada: CalificacionModelo? = null
        var mostradoAutomaticamente = false

        override suspend fun guardarCalificacion(calificacion: CalificacionModelo) {
            calificacionGuardada = calificacion
        }

        override suspend fun obtenerCalificacion(): CalificacionModelo? = calificacionGuardada

        override suspend fun yaSeMostroAutomaticamente(): Boolean = mostradoAutomaticamente

        override suspend fun marcarMostradoAutomaticamente() {
            mostradoAutomaticamente = true
        }
    }

    private lateinit var repositorio: RepositorioEnMemoria

    @Before
    fun configurarDispatcherPrincipal() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repositorio = RepositorioEnMemoria()
    }

    @After
    fun restaurarDispatcherPrincipal() {
        Dispatchers.resetMain()
    }

    @Test
    fun `al iniciar carga las estrellas y el comentario de una calificacion previa`() = runTest {
        repositorio.calificacionGuardada = CalificacionModelo(
            estrellas = 4,
            comentario = "Muy divertida",
            fechaEnvio = 123L,
            yaCalifico = true
        )

        val viewModel = CalificacionViewModel(repositorio)

        assertEquals(4, viewModel.estrellasSeleccionadas.value)
        assertEquals("Muy divertida", viewModel.comentarioPrevio.value)
    }

    @Test
    fun `guardar calificacion envia al repositorio las estrellas y el comentario sin espacios`() = runTest {
        val viewModel = CalificacionViewModel(repositorio)
        viewModel.seleccionarEstrella(5)

        viewModel.guardarCalificacion("  Buena app  ")

        val guardada = repositorio.calificacionGuardada
        assertNotNull(guardada)
        assertEquals(5, guardada?.estrellas)
        assertEquals("Buena app", guardada?.comentario)
    }

    @Test
    fun `el comentario previo se actualiza despues de guardar una nueva calificacion`() = runTest {
        val viewModel = CalificacionViewModel(repositorio)

        viewModel.guardarCalificacion("Nuevo comentario")

        assertEquals("Nuevo comentario", viewModel.comentarioPrevio.value)
    }

    @Test
    fun `el dialogo automatico se dispara al tercer reto jugado`() = runTest {
        val viewModel = CalificacionViewModel(repositorio)

        repeat(3) { viewModel.registrarRetoJugado() }

        assertTrue(viewModel.mostrarDialogoAutomatico.value)
        assertTrue(repositorio.mostradoAutomaticamente)
    }

    @Test
    fun `el dialogo automatico no se dispara antes del tercer reto`() = runTest {
        val viewModel = CalificacionViewModel(repositorio)

        repeat(2) { viewModel.registrarRetoJugado() }

        assertFalse(viewModel.mostrarDialogoAutomatico.value)
    }

    @Test
    fun `el dialogo automatico no se repite si ya se mostro antes`() = runTest {
        repositorio.mostradoAutomaticamente = true
        val viewModel = CalificacionViewModel(repositorio)

        repeat(3) { viewModel.registrarRetoJugado() }

        assertFalse(viewModel.mostrarDialogoAutomatico.value)
    }

    @Test
    fun `el dialogo automatico no se dispara si el jugador ya califico`() = runTest {
        repositorio.calificacionGuardada = CalificacionModelo(
            estrellas = 5,
            comentario = null,
            fechaEnvio = 123L,
            yaCalifico = true
        )
        val viewModel = CalificacionViewModel(repositorio)

        repeat(3) { viewModel.registrarRetoJugado() }

        assertFalse(viewModel.mostrarDialogoAutomatico.value)
    }
}
