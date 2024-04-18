package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto

interface OnTextChangeListenerPrecioVenta {

    fun afterTextChange(text: String, viewHolder: ClientePrecioVentaViewHolder, position: Int)

    fun onTextChange(text: String, viewHolder: ClientePrecioVentaViewHolder)
}