package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto

import com.google.gson.annotations.SerializedName

data class AlertasAlmacenesDataResponse (
    @SerializedName("Respuesta")  val Respuesta: String,
    @SerializedName("Almacenes")  val Almacenes: List<AlertasAlmacenesItemResponse>,
)
data class AlertasAlmacenesItemResponse(
    @SerializedName("Nombre")  val Nombre: String

)