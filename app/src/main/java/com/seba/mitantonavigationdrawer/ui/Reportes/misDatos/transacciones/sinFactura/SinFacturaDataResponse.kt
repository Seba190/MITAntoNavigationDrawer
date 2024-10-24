package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.sinFactura

import com.google.gson.annotations.SerializedName

data class SinFacturaDataResponse (
    @SerializedName("Respuesta")  val Respuesta: String,
    @SerializedName("Sin Factura")  val SinFactura: List<SinFacturaItemResponse>,
)
data class SinFacturaItemResponse(
    @SerializedName("Id")  val Id: String,
    @SerializedName("Almacen")  val Almacen: String,
    @SerializedName("Producto")  val Producto: String,
    @SerializedName("Unidades")  val Unidades: String,
    @SerializedName("Tipo Factura")  val TipoFactura: String,
    @SerializedName("Fecha Completa")  val FechaCompleta: String,
    @SerializedName("Dia Sin Factura")  val DiaSinFactura: String,
    @SerializedName("Fecha Sin Factura")  val FechaSinFactura: String,
    )
