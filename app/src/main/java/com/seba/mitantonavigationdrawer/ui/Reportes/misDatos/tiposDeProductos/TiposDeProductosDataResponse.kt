package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.tiposDeProductos

import com.google.gson.annotations.SerializedName

data class TiposDeProductosDataResponse (
    @SerializedName("Respuesta")  val Respuesta: String,
    @SerializedName("Tipos De Productos")  val TiposDeProductos: List<TiposDeProductosItemResponse>,
)
data class TiposDeProductosItemResponse(
    @SerializedName("Id")  val Id: String,
    @SerializedName("Nombre")  val Nombre: String,
)