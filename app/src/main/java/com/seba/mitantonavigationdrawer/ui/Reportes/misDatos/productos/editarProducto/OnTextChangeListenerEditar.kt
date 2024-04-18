package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos.editarProducto

import androidx.fragment.app.activityViewModels
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto.AlertasAlmacenesAdapter.*
import com.seba.mitantonavigationdrawer.ui.SharedViewModel

interface OnTextChangeListenerEditar {

   // fun onEditTextValueChanged(position: Int, value: String ,viewHolder: AlertasAlmacenesViewHolder)
    fun afterTextChange(text: String, viewHolder: AlertasAlmacenesEditarViewHolder)
    fun getAllItems(): List<AlertasAlmacenesItemResponseEditar>

   // fun obtenerListaEnPosicion(position: Int): List<String>
}



