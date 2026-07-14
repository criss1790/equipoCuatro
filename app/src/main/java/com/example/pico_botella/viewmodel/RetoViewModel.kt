package com.example.pico_botella.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pico_botella.data.PicoBotellaDB
import com.example.pico_botella.model.Reto
import com.example.pico_botella.repository.RepositorioRetos
import com.example.pico_botella.repository.RepositorioRetosRoom
import kotlinx.coroutines.launch

// ViewModel del listado y CRUD de retos (HU 6/7/8/9), con el patrón del profesor:
// AndroidViewModel + LiveData + _progresState. No requiere fábrica.
class RetoViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio: RepositorioRetos

    private val _listaRetos = MutableLiveData<List<Reto>>()
    val listaRetos: LiveData<List<Reto>> get() = _listaRetos

    private val _progresState = MutableLiveData<Boolean>()
    val progresState: LiveData<Boolean> get() = _progresState

    init {
        val dao = PicoBotellaDB.obtenerBaseDatos(application).retoDao()
        repositorio = RepositorioRetosRoom(dao)
    }

    fun listarRetos() {
        viewModelScope.launch {
            _progresState.value = true
            _listaRetos.value = repositorio.listarRetos()
            _progresState.value = false
        }
    }

    fun insertarReto(reto: Reto) {
        viewModelScope.launch {
            _progresState.value = true
            repositorio.insertarReto(reto)
            _listaRetos.value = repositorio.listarRetos()   // re-lista tras insertar
            _progresState.value = false
        }
    }

    fun actualizarReto(reto: Reto) {
        viewModelScope.launch {
            _progresState.value = true
            repositorio.actualizarReto(reto)
            _listaRetos.value = repositorio.listarRetos()   // re-lista tras actualizar
            _progresState.value = false
        }
    }

    fun eliminarReto(reto: Reto) {
        viewModelScope.launch {
            _progresState.value = true
            repositorio.eliminarReto(reto)
            _listaRetos.value = repositorio.listarRetos()   // re-lista tras eliminar
            _progresState.value = false
        }
    }
}
