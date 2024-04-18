package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos.editarProducto

import com.google.gson.annotations.SerializedName

data class ClientePrecioVentaDataResponseEditar (
    @SerializedName("Respuesta")  val Respuesta: String,
    @SerializedName("Clientes")  val Clientes: List<ClientePrecioVentaItemResponseEditar>,
)
data class ClientePrecioVentaItemResponseEditar(
    @SerializedName("Nombre")  val Nombre: String

)