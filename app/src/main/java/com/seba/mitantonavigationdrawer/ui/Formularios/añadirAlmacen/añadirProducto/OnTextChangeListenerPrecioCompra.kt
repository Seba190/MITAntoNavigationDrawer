package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto

interface OnTextChangeListenerPrecioCompra {
    fun afterTextChange(text: String, viewHolder: ProveedorPrecioCompraViewHolder)

    fun onTextChange(text: String, viewHolder: ProveedorPrecioCompraViewHolder)
}