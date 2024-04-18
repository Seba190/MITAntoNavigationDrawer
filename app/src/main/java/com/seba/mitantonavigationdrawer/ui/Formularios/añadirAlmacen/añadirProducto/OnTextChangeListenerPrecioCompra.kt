package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto

interface OnTextChangeListenerPrecioCompra {
    fun afterTextChange(text: String, viewHolder: ProveedorPrecioCompraViewHolder, position : Int)

    fun onTextChange(text: String, viewHolder: ProveedorPrecioCompraViewHolder)
}