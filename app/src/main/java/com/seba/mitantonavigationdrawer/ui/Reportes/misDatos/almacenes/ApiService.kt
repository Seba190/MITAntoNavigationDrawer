package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface ApiService {
    @GET("Almacenes/almacenFinal.php")
    suspend fun getAlmacenes():Response<AlmacenesDataResponse>
   // @GET("Almacenes/caracteristicas.php")
    //suspend fun getCaracteristicas():Response<CaracteristicasDataResponse>
}