package com.example.pico_botella.view.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.pico_botella.R
import com.example.pico_botella.databinding.DialogEliminarRetoBinding
import com.example.pico_botella.model.Reto

// HU 9.0 — Diálogo de confirmación para eliminar un reto (patrón clase6):
// Dialog + DataBinding, sin DialogFragment ni AlertDialog.
// "SI" y "NO" son TextView naranjas (no botones). Recibe el Reto completo
// (con su id real de la BD) y lo entrega por el callback alConfirmar.
// No conoce Room ni el ViewModel.
class EliminarRetoDialog(
    private val contexto: Context,
    private val reto: Reto,                      // con id + texto reales, desde la BD
    private val alConfirmar: (Reto) -> Unit
) {
    fun mostrar() {
        val dialogo = Dialog(contexto)
        val binding: DialogEliminarRetoBinding = DataBindingUtil.inflate(
            LayoutInflater.from(contexto), R.layout.dialog_eliminar_reto, null, false
        )
        dialogo.setContentView(binding.root)
        dialogo.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // C1
        dialogo.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // C6 — no se cierra al tocar por fuera
        dialogo.setCanceledOnTouchOutside(false)
        dialogo.setCancelable(false)

        // C3 — texto del reto tal como viene de la BD
        binding.textoReto.text = reto.texto

        // C4 — "NO": cancela y cierra
        binding.textoNo.setOnClickListener { dialogo.dismiss() }

        // C5 — "SI": elimina (pasa el Reto con su id real → Room borra por clave primaria)
        binding.textoSi.setOnClickListener {
            alConfirmar(reto)
            dialogo.dismiss()
        }

        dialogo.show()
    }
}
