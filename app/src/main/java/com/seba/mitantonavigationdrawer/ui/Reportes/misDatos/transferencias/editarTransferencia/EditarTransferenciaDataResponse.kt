package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transferencias.editarTransferencia

import com.google.gson.annotations.SerializedName

data class EditarTransferenciaDataResponse (
    @SerializedName("Respuesta")  val Respuesta: String,
    @SerializedName("Transferencias")  val Transferencias: List<EditarTransferenciaItemResponse>,
)
data class EditarTransferenciaItemResponse(
    @SerializedName("Id")  val Id: String,
    @SerializedName("Id transferencia")  val IdTransferencia: String,
    @SerializedName("Nombre")  val Nombre: String,
    @SerializedName("Producto")  val Producto: String,
    @SerializedName("Dia Transferencia")  val DiaTransferencia: String,
    @SerializedName("Fecha Transferencia")  val FechaTransferencia: String,
    @SerializedName("Contenido")  val Contenido: String,
    @SerializedName("Comentarios")  val Comentarios: String,
)