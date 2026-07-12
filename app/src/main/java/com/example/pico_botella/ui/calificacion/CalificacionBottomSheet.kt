package com.example.pico_botella.ui.calificacion

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.pico_botella.R
import com.example.pico_botella.databinding.BottomSheetCalificacionBinding
import com.example.pico_botella.viewmodel.CalificacionViewModel
import com.example.pico_botella.viewmodel.FabricaCalificacionViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * Tarjeta de calificación interna simulada: permite elegir estrellas y dejar un
 * comentario opcional que se guarda localmente, y luego redirige a la ficha de
 * Play Store usada como referencia académica (no es la app real ni su marca).
 * La lógica de guardado vive en [CalificacionViewModel]; esta clase solo maneja la vista.
 */
class CalificacionBottomSheet : BottomSheetDialogFragment() {

    private val viewModel: CalificacionViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            FabricaCalificacionViewModel(requireContext())
        )[CalificacionViewModel::class.java]
    }

    private var _binding: BottomSheetCalificacionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        contenedor: ViewGroup?,
        estadoGuardado: Bundle?
    ): View {
        _binding = BottomSheetCalificacionBinding.inflate(inflater, contenedor, false)
        return binding.root
    }

    override fun onViewCreated(vista: View, estadoGuardado: Bundle?) {
        super.onViewCreated(vista, estadoGuardado)
        precargarCalificacionPrevia()
        configurarListeners()
    }

    // Muestra la calificación y el comentario previos del jugador, si ya había calificado antes
    private fun precargarCalificacionPrevia() {
        binding.barraEstrellas.rating = viewModel.estrellasSeleccionadas.value.toFloat()
        binding.campoComentario.setText(viewModel.comentarioPrevio.value)
    }

    private fun configurarListeners() {
        binding.barraEstrellas.setOnRatingBarChangeListener { _, calificacion, _ ->
            seleccionarEstrella(calificacion)
        }

        binding.botonCalificar.setOnClickListener {
            viewModel.guardarCalificacion(binding.campoComentario.text.toString())
            mostrarMensajeAgradecimiento()
            abrirCalificacionEnPlayStore()
            dismiss()
        }

        binding.botonCalificarDespues.setOnClickListener {
            cerrarCalificacion()
        }
    }


    private fun seleccionarEstrella(calificacion: Float) {
        viewModel.seleccionarEstrella(calificacion.toInt())
    }

    private fun mostrarMensajeAgradecimiento() {
        Toast.makeText(
            requireContext(),
            getString(R.string.mensaje_agradecimiento_calificacion),
            Toast.LENGTH_SHORT
        ).show()
    }

    // Simula el redirect que hacen las apps reales al pedir una calificación:
    // abre en el navegador la ficha de Play Store usada como referencia académica
    private fun abrirCalificacionEnPlayStore() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(ENLACE_CALIFICACION_PLAY_STORE))
        startActivity(intent)
    }

    // Cierra la tarjeta sin guardar ni redirigir (opción "Calificar después")
    private fun cerrarCalificacion() {
        dismiss()
    }

    // Libera la referencia al binding: el View del Fragment puede destruirse
    // antes que el propio Fragment (p. ej. al cerrar el BottomSheet)
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ENLACE_CALIFICACION_PLAY_STORE =
            "https://play.google.com/store/apps/details?id=com.nequi.MobileApp&hl=es_419&gl=es"
    }
}
