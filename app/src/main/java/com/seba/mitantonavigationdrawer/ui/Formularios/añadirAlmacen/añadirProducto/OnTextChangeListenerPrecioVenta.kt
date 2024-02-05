package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto

interface OnTextChangeListenerPrecioVenta {
    fun onTextChange(text: String, viewHolder: ClientePrecioVentaViewHolder)
    fun afterTextChange(text: String, viewHolder: ClientePrecioVentaViewHolder)
}