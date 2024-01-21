package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirInventario

import retrofit2.Response
import retrofit2.http.GET

interface ApiServiceProducto {
    @GET("FacturaEntrada/productos.php")
    suspend fun getProductos():Response<ProductosDataResponse>
    // @GET("Almacenes/caracteristicas.php")
    //suspend fun getCaracteristicas():Response<CaracteristicasDataResponse>
}