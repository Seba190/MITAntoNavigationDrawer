package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.tiposDeProductos.editarTiposDeProductos

import com.google.gson.annotations.SerializedName

data class EditarTiposDeProductosDataResponse (
    @SerializedName("Respuesta")  val Respuesta: String,
    @SerializedName("Productos")  val Productos: List<EditarTiposDeProductosItemResponse>,
)
data class EditarTiposDeProductosItemResponse(
    @SerializedName("Id")  val Id: String,
    @SerializedName("Nombre")  val Nombre: String,
    @SerializedName("Imagen")  val Imagen: String,
    @SerializedName("Descripcion")  val Descripcion: String
)