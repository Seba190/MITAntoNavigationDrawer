package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirInventario

import com.google.gson.annotations.SerializedName

data class ProductosDataResponse (
    @SerializedName("Respuesta")  val Respuesta: String,
    @SerializedName("Productos")  val Productos: List<ProductosItemResponse>,
)
data class ProductosItemResponse(
    @SerializedName("Id")  val Id: String,
    @SerializedName("Nombre")  val Nombre: String,
    @SerializedName("Inventario")  val Inventario: String,
    @SerializedName("Descripcion")  val Descripcion: String
)