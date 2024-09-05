package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes

import com.google.gson.annotations.SerializedName
import java.io.ObjectInput
import java.util.Objects

data class AlmacenesDataResponse (
    @SerializedName("Respuesta")  val Respuesta: String,
    @SerializedName("Almacenes")  val Almacenes: List<AlmacenesItemResponse>
)
data class AlmacenesItemResponse(
    @SerializedName("Id")  val Id: String ,
    @SerializedName("Nombre")  val Nombre: String
)





