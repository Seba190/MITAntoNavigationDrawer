package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.tiposDeProductos

import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.AlmacenesDataResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiServiceTiposDeProductos {
    //@GET("TiposDeProducto/tiposDeProductosFinal.php")
    //suspend fun getTiposDeProductos(): Response<TiposDeProductosDataResponse>

    @FormUrlEncoded
    @POST("TiposDeProducto/tiposDeProductosFinal.php")
    suspend fun postTiposDeProductos(@Field("actividad") actividad: String):Response<TiposDeProductosDataResponse>
}