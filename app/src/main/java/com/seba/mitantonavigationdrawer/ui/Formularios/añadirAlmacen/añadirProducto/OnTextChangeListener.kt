package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto

import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto.AlertasAlmacenesAdapter.*

interface OnTextChangeListener {
    fun afterTextChange(text: String, viewHolder: AlertasAlmacenesViewHolder)
    fun getAllItems(): List<AlertasAlmacenesItemResponse>

   // fun obtenerListaEnPosicion(position: Int): List<String>
}



