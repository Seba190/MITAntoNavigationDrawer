package com.seba.mitantonavigationdrawer.ui.inventario

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiServiceInventario {
    @GET("Inventario/inventarioFinalNeto.php")
    suspend fun getInventario(@Query("ALMACEN") almacen:String):Response<InventarioDataResponse>

}


