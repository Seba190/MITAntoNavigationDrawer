package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto

import com.google.gson.annotations.SerializedName

data class ClientePrecioVentaDataResponse (
    @SerializedName("Respuesta")  val Respuesta: String,
    @SerializedName("Clientes")  val Clientes: List<ClientePrecioVentaItemResponse>,
)
data class ClientePrecioVentaItemResponse(
    @SerializedName("Nombre")  val Nombre: String

)