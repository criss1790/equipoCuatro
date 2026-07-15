package com.example.pico_botella.view.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.pico_botella.R
import com.example.pico_botella.databinding.DialogEditarRetoBinding
import com.example.pico_botella.model.Reto

// HU 8.0 — Diálogo personalizado para editar un reto (patrón clase6):
// Dialog + DataBinding, sin DialogFragment ni AlertDialog.
// Recibe el Reto completo (con su id real de la BD) y entrega el reto
// actualizado por el callback alGuardar. No conoce Room ni el ViewModel.
class EditarRetoDialog(
    private val contexto: Context,
    private val reto: Reto,                     // con id + texto reales, desde la BD
    private val alGuardar: (Reto) -> Unit
) {
    fun mostrar() {
        val dialogo = Dialog(contexto)
        val binding: DialogEditarRetoBinding = DataBindingUtil.inflate(
            LayoutInflater.from(contexto), R.layout.dialog_editar_reto, null, false
        )
        dialogo.setContentView(binding.root)
        dialogo.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // C1
        dialogo.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // C7 — no se cierra al tocar por fuera
        dialogo.setCanceledOnTouchOutside(false)
        dialogo.setCancelable(false)

        // C3 — precargado desde la BD, cursor al final
        binding.campoReto.setText(reto.texto)
        binding.campoReto.setSelection(binding.campoReto.text.length)

        // C4 — Cancelar cierra sin actualizar
        binding.botonCancelar.setOnClickListener { dialogo.dismiss() }

        // C5 + C6 — Guardar SIEMPRE habilitado (a diferencia de HU 7.0, aquí NO hay TextWatcher)
        binding.botonGuardar.setOnClickListener {
            val retoActualizado = reto.copy(
                texto = binding.campoReto.text.toString().trim()
            )                                    // conserva el MISMO id → Room hace UPDATE
            alGuardar(retoActualizado)
            dialogo.dismiss()
        }

        dialogo.show()
    }
}
