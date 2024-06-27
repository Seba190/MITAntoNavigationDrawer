package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.facturaEntrada

import com.google.gson.annotations.SerializedName

data class FacturaEntradaDataResponse (
    @SerializedName("Respuesta")  val Respuesta: String,
    @SerializedName("Facturas de Entrada")  val FacturaEntrada: List<FacturaEntradaItemResponse>,
)
data class FacturaEntradaItemResponse(
    @SerializedName("Id Factura Entrada")  val IdFacturaEntrada: String,
    @SerializedName("Factura Entrada")  val FacturaEntrada: String,
    @SerializedName("Productos")  val Productos: List<ProductosFacturaEntradaItemResponse>,
    @SerializedName("Proveedor")  val Proveedor: String,
    @SerializedName("Almacen")  val Almacen: String,
    @SerializedName("Dia Factura Entrada")  val DiaFacturaEntrada: String,
    @SerializedName("Fecha Factura Entrada")  val FechaFacturaEntrada: String,
    @SerializedName("Comentarios")  val Comentarios: String,
)

data class ProductosFacturaEntradaItemResponse (
    @SerializedName("Id Factura Entrada")  val IdFacturaEntrada: String,
    @SerializedName("Producto")  val Producto: String,
    @SerializedName("Cantidad")  val Cantidad: String,
    @SerializedName("Precio Compra")  val PrecioCompra: String,

)

