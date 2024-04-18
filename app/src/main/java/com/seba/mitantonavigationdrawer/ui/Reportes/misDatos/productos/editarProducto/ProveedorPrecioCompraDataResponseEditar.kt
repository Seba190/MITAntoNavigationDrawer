package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos.editarProducto

import com.google.gson.annotations.SerializedName

data class ProveedorPrecioCompraDataResponseEditar (
    @SerializedName("Respuesta")  val Respuesta: String,
    @SerializedName("Proveedores")  val Proveedores: List<ProveedorPrecioCompraItemResponseEditar>,
)
data class ProveedorPrecioCompraItemResponseEditar(
    @SerializedName("Nombre")  val Nombre: String

)