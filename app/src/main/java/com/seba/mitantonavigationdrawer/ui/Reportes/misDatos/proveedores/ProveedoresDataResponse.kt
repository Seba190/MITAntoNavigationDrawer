package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.proveedores

import com.google.gson.annotations.SerializedName

data class ProveedoresDataResponse (
    @SerializedName("Respuesta")  val Respuesta: String,
    @SerializedName("Proveedores")  val Proveedores: List<ProveedoresItemResponse>,
)
data class ProveedoresItemResponse(
    @SerializedName("Id")  val Id: String,
    @SerializedName("Nombre")  val Nombre: String,
    @SerializedName("Imagen")  val Imagen: String,
    @SerializedName("Descripcion")  val Descripcion: String
)