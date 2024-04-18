package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.tiposDeProductos

import com.google.gson.annotations.SerializedName

data class TiposDeProductosDataResponse (
    @SerializedName("Respuesta")  val Respuesta: String,
    @SerializedName("TiposDeProductos")  val TiposDeProductos: List<TiposDeProductosItemResponse>,
)
data class TiposDeProductosItemResponse(
    @SerializedName("Id")  val Id: String,
    @SerializedName("Nombre")  val Nombre: String,
    @SerializedName("Imagen")  val Imagen: String,
    @SerializedName("Descripcion")  val Descripcion: String
)