package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto

import android.app.Dialog
import android.content.Context
import androidx.fragment.app.DialogFragment

// En TuDialogo.kt
class TuDialogo : DialogFragment() {

    // ...

    interface OnDialogResultListener {
        fun onResult(parametro: String)
    }

    var onDialogResultListener: OnDialogResultListener? = null

    // En algún lugar del diálogo donde quieras enviar el parámetro al Fragment
    fun enviarParametro() {
        val parametro = "valorDelParametro"
        onDialogResultListener?.onResult(parametro)
        dismiss() // Cerrar el diálogo después de enviar el parámetro
    }
    // ...
}

