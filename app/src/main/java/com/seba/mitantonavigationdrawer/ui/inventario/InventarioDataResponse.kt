package com.seba.mitantonavigationdrawer.ui.inventario

import com.google.gson.annotations.SerializedName
import java.io.ObjectInput
import java.util.Objects

data class InventarioDataResponse (
    @SerializedName("Respuesta")  val Respuesta: String,
    @SerializedName("Productos por Almacen")  val ProductosPorAlmacen: List<InventarioItemResponse>,
)
data class InventarioItemResponse(
    @SerializedName("Id")  val Id: String ,
    @SerializedName("Producto")  val Producto: String ,
    @SerializedName("Cantidad")  val Cantidad: String

)





