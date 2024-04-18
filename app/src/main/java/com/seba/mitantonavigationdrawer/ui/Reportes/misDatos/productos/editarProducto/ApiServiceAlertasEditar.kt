package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos.editarProducto

import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.AlmacenesDataResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiServiceAlertasEditar {
    @GET("Producto/alertasAlmacen.php")
    suspend fun getAlertasAlmacenes(): Response<AlertasAlmacenesDataResponseEditar>
}