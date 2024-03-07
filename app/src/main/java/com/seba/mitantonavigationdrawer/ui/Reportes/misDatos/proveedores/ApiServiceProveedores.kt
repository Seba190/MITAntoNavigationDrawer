package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.proveedores

import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.AlmacenesDataResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiServiceProveedores {
    @GET("Proveedores/proveedorFinal.php")
    suspend fun getProveedores(): Response<ProveedoresDataResponse>
}