package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transferencias

import com.google.gson.annotations.SerializedName

data class TransferenciasDataResponse (
    @SerializedName("Respuesta")  val Respuesta: String,
    @SerializedName("Transferencias")  val Transferencias: List<TransferenciasItemResponse>,
)
data class TransferenciasItemResponse(
    @SerializedName("Id")  val Id: String,
    @SerializedName("Nombre")  val Nombre: String,
    @SerializedName("Imagen")  val Imagen: String,
    @SerializedName("Descripcion")  val Descripcion: String
)