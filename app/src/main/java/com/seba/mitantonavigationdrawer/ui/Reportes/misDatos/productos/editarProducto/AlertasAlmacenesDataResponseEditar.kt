package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos.editarProducto

import com.google.gson.annotations.SerializedName

data class AlertasAlmacenesDataResponseEditar (
    @SerializedName("Respuesta")  val Respuesta: String,
    @SerializedName("Almacenes")  val Almacenes: List<AlertasAlmacenesItemResponseEditar>,
)
data class AlertasAlmacenesItemResponseEditar(
    @SerializedName("Nombre")  val Nombre: String

)