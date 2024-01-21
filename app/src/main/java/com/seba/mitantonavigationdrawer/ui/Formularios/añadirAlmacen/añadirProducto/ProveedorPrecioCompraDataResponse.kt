package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto

import com.google.gson.annotations.SerializedName

data class ProveedorPrecioCompraDataResponse (
    @SerializedName("Respuesta")  val Respuesta: String,
    @SerializedName("Proveedores")  val Proveedores: List<ProveedorPrecioCompraItemResponse>,
)
data class ProveedorPrecioCompraItemResponse(
    @SerializedName("Nombre")  val Nombre: String

)