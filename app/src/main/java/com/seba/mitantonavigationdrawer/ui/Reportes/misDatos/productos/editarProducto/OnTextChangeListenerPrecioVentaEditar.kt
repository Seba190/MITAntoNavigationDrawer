package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos.editarProducto


interface OnTextChangeListenerPrecioVentaEditar {
    fun afterTextChange(text: String, viewHolder: ClientePrecioVentaEditarViewHolder)

    fun onTextChange(text: String, viewHolder: ClientePrecioVentaEditarViewHolder)
}