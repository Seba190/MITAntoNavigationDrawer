package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST


interface ApiService {
   // @GET("Almacenes/almacenFinal.php")
   // suspend fun getAlmacenes():Response<AlmacenesDataResponse>

   // @POST("Almacenes/almacenFinal.php")
   // suspend fun postAlmacenes(@Body actividad: String):Response<AlmacenesDataResponse>
    @FormUrlEncoded
    @POST("Almacenes/almacenFinal.php")
    suspend fun postAlmacenes(@Field("actividad") actividad: String):Response<AlmacenesDataResponse>

}


