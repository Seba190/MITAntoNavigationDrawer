package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos

import com.google.gson.annotations.SerializedName

data class ProductosDataResponse (
    @SerializedName("Productos")  val Productos: List<ProductosItemResponse>,
)
data class ProductosItemResponse(
    @SerializedName("Id")  val Id: String,
    @SerializedName("Nombre")  val Nombre: String,
    @SerializedName("Inventario")  val Inventario: String,
    @SerializedName("Imagen")  val Imagen: String,

)