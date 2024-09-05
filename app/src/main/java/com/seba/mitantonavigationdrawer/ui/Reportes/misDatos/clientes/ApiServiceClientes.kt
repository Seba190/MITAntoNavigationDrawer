package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.clientes

import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.AlmacenesDataResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiServiceClientes {
    //@GET("Clientes/clienteFinal.php")
    //suspend fun getClientes(): Response<ClientesDataResponse>

    @FormUrlEncoded
    @POST("Clientes/clienteFinal.php")
    suspend fun postClientes(@Field("actividad") actividad: String):Response<ClientesDataResponse>
}