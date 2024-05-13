package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto

import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto.AlertasAlmacenesAdapter.*

interface OnTextChangeListener {

   // fun onEditTextValueChanged(position: Int, value: String ,viewHolder: AlertasAlmacenesViewHolder)
    fun afterTextChange(text: String, viewHolder: AlertasAlmacenesViewHolder)
    fun getAllItems(): List<AlertasAlmacenesItemResponse>

   // fun obtenerListaEnPosicion(position: Int): List<String>
}



