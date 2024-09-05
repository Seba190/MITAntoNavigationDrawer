package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos

import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.AlmacenesDataResponse
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.clientes.ClientesDataResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiServiceProductos {
    //@GET("Productos/productoFinal.php")
    //suspend fun getProductos(): Response<ProductosDataResponse>
    @FormUrlEncoded
    @POST("Productos/productoFinal.php")
    suspend fun postProductos(@Field("actividad") actividad: String):Response<ProductosDataResponse>
}