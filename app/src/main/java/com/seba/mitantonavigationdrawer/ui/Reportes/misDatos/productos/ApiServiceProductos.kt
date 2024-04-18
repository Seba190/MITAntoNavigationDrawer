package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos

import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.clientes.ClientesDataResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiServiceProductos {
    @GET("Productos/productoFinal.php")
    suspend fun getProductos(): Response<ProductosDataResponse>
}