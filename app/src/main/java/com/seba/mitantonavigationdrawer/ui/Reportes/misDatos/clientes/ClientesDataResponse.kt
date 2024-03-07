package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.clientes

import com.google.gson.annotations.SerializedName

data class ClientesDataResponse (
    @SerializedName("Respuesta")  val Respuesta: String,
    @SerializedName("Clientes")  val Clientes: List<ClientesItemResponse>,
)
data class ClientesItemResponse(
    @SerializedName("Id")  val Id: String,
    @SerializedName("Nombre")  val Nombre: String,
    @SerializedName("Imagen")  val Imagen: String,
    @SerializedName("Descripcion")  val Descripcion: String
)