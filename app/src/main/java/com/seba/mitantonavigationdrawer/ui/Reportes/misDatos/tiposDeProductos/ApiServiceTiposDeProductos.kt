package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.tiposDeProductos

import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.AlmacenesDataResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiServiceTiposDeProductos {
    @GET("TiposDeProducto/tiposDeProductosFinal.php")
    suspend fun getTiposDeProductos(): Response<TiposDeProductosDataResponse>
}