package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.facturaSalida

import com.google.gson.annotations.SerializedName

data class FacturaSalidaDataResponse (
    @SerializedName("Respuesta")  val Respuesta: String,
    @SerializedName("Facturas de Salida")  val FacturaSalida: List<FacturaSalidaItemResponse>,
)
data class FacturaSalidaItemResponse(
    @SerializedName("Id Factura Salida")  val IdFacturaSalida: String,
    @SerializedName("Factura Salida")  val FacturaSalida: String,
    @SerializedName("Productos")  val Productos: List<ProductosFacturaSalidaItemResponse>,
    @SerializedName("Cliente")  val Cliente: String,
    @SerializedName("Almacen")  val Almacen: String,
    @SerializedName("Fecha Completa")  val FechaCompleta: String,
    @SerializedName("Dia Factura Salida")  val DiaFacturaSalida: String,
    @SerializedName("Fecha Factura Salida")  val FechaFacturaSalida: String,
    @SerializedName("Comentarios")  val Comentarios: String,
)

data class ProductosFacturaSalidaItemResponse (
    @SerializedName("Id Factura Salida")  val IdFacturaSalida: String,
    @SerializedName("Producto")  val Producto: String,
    @SerializedName("Cantidad")  val Cantidad: String,
    @SerializedName("Precio Venta")  val PrecioVenta: String,
)