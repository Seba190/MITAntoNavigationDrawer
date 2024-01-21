package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto

import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.AlmacenesDataResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiServiceAlertas {
    @GET("Producto/alertasAlmacen.php")
    suspend fun getAlertasAlmacenes(): Response<AlertasAlmacenesDataResponse>
}