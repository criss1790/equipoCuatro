package com.example.pico_botella.viewmodel

import com.example.pico_botella.domain.modelo.Reto
import com.example.pico_botella.domain.repositorio.RepositorioRetos
import com.example.pico_botella.ui.estado.EstadoUI
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// Pruebas unitarias de RetosViewModel: verifican el manejo de estados (EstadoUI)
// sin necesidad de un emulador, reemplazando el dispatcher principal por uno de prueba.
@OptIn(ExperimentalCoroutinesApi::class)
class RetosViewModelTest {

    // Repositorio de prueba que entrega una lista fija de retos
    private class RepositorioDePrueba(private val retos: List<Reto>) : RepositorioRetos {
        override fun obtenerRetos(): Flow<List<Reto>> = flowOf(retos)
    }

    // Repositorio de prueba que falla con la excepción indicada
    private class RepositorioQueFalla(private val excepcion: Throwable) : RepositorioRetos {
        override fun obtenerRetos(): Flow<List<Reto>> = flow { throw excepcion }
    }

    private val retoDePrueba = Reto(id = 1, texto = "Canta una canción", categoria = "Reto")

    @Before
    fun configurarDispatcherPrincipal() {
        // viewModelScope usa Dispatchers.Main, que no existe en tests unitarios:
        // se reemplaza por un dispatcher de prueba
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun restaurarDispatcherPrincipal() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cargar reto con datos disponibles produce estado Exito`() = runTest {
        val viewModel = RetosViewModel(RepositorioDePrueba(listOf(retoDePrueba)))

        viewModel.cargarRetoAleatorio()

        val estado = viewModel.estadoReto.value
        assertTrue(estado is EstadoUI.Exito)
        assertEquals(retoDePrueba, (estado as EstadoUI.Exito).datos)
    }

    @Test
    fun `cargar reto con lista vacia produce estado Vacio`() = runTest {
        val viewModel = RetosViewModel(RepositorioDePrueba(emptyList()))

        viewModel.cargarRetoAleatorio()

        assertTrue(viewModel.estadoReto.value is EstadoUI.Vacio)
    }

    @Test
    fun `un fallo del repositorio produce estado Error con su mensaje`() = runTest {
        val viewModel = RetosViewModel(RepositorioQueFalla(IOException("Sin conexión")))

        viewModel.cargarRetoAleatorio()

        val estado = viewModel.estadoReto.value
        assertTrue(estado is EstadoUI.Error)
        assertEquals("Sin conexión", (estado as EstadoUI.Error).mensaje)
    }

    @Test
    fun `confirmar estado mostrado vuelve al estado Vacio para no repetir el reto al rotar`() = runTest {
        val viewModel = RetosViewModel(RepositorioDePrueba(listOf(retoDePrueba)))
        viewModel.cargarRetoAleatorio()
        assertTrue(viewModel.estadoReto.value is EstadoUI.Exito)

        viewModel.confirmarEstadoMostrado()

        assertTrue(viewModel.estadoReto.value is EstadoUI.Vacio)
    }

    @Test
    fun `la cancelacion de la corutina no se convierte en estado Error`() = runTest {
        val viewModel = RetosViewModel(RepositorioQueFalla(CancellationException("cancelada")))

        viewModel.cargarRetoAleatorio()

        // La cancelación es parte del funcionamiento normal de las corutinas:
        // no debe mostrarse al usuario como un error
        assertTrue(viewModel.estadoReto.value !is EstadoUI.Error)
    }
}
