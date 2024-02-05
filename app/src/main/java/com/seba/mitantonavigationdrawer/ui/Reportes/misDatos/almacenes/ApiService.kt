package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes

import retrofit2.Response
import retrofit2.http.GET


interface ApiService {
    @GET("Almacenes/almacenFinal.php")
    suspend fun getAlmacenes():Response<AlmacenesDataResponse>

}


