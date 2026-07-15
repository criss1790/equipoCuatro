package com.example.pico_botella.view.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.pico_botella.R
import com.example.pico_botella.databinding.DialogAgregarRetoBinding

// HU 7.0 — Diálogo personalizado para agregar un reto (patrón clase6):
// Dialog + DataBinding, sin DialogFragment ni AlertDialog.
// No conoce Room ni el ViewModel: entrega el texto por el callback alGuardar.
class AgregarRetoDialog(
    private val contexto: Context,
    private val alGuardar: (String) -> Unit
) {
    fun mostrar() {
        val dialogo = Dialog(contexto)
        val binding: DialogAgregarRetoBinding = DataBindingUtil.inflate(
            LayoutInflater.from(contexto), R.layout.dialog_agregar_reto, null, false
        )
        dialogo.setContentView(binding.root)
        dialogo.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // C1
        dialogo.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // C7 — no se cierra al tocar por fuera
        dialogo.setCanceledOnTouchOutside(false)
        dialogo.setCancelable(false)

        // C5 — Guardar arranca deshabilitado y reacciona en tiempo real (bidireccional)
        binding.botonGuardar.isEnabled = false
        binding.campoReto.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {
                binding.botonGuardar.isEnabled = !s.isNullOrBlank() // ignora solo-espacios
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // C4 — Cancelar cierra sin guardar
        binding.botonCancelar.setOnClickListener { dialogo.dismiss() }

        // C6 — Guardar entrega el texto y cierra
        binding.botonGuardar.setOnClickListener {
            alGuardar(binding.campoReto.text.toString().trim())
            dialogo.dismiss()
        }

        dialogo.show()
    }
}
