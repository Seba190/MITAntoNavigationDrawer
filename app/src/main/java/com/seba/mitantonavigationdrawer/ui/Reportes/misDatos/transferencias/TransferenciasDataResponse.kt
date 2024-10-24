package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transferencias

import com.google.gson.annotations.SerializedName

data class TransferenciasDataResponse (
    @SerializedName("Respuesta")  val Respuesta: String,
    @SerializedName("Transferencias")  val Transferencias: List<TransferenciasItemResponse>,
)
data class TransferenciasItemResponse(
    @SerializedName("Id Transferencia")  val IdTransferencia: String,
    @SerializedName("Transferencia")  val Transferencia: String,
    @SerializedName("Productos")  val Productos: List<ProductosTransferenciasItemResponse>,
    @SerializedName("Fecha Completa")  val FechaCompleta: String,
    @SerializedName("Dia Transferencia")  val DiaTransferencia: String,
    @SerializedName("Fecha Transferencia")  val FechaTransferencia: String,
    @SerializedName("Contenido")  val Contenido: String,
    @SerializedName("Comentarios")  val Comentarios: String,
)

data class ProductosTransferenciasItemResponse(
    @SerializedName("Id transferencia")  val IdTransferencia: String,
    @SerializedName("Producto")  val Nombre: String,
    @SerializedName("Cantidad")  val Cantidad: String,
    @SerializedName("Dia Transferencia")  val DiaTransferencia: String,
)