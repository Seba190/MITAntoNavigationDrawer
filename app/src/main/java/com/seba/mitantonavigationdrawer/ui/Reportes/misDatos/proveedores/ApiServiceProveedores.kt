package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.proveedores

import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.AlmacenesDataResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiServiceProveedores {
    //@GET("Proveedores/proveedorFinal.php")
   // suspend fun getProveedores(): Response<ProveedoresDataResponse>

    @FormUrlEncoded
    @POST("Proveedores/proveedorFinal.php")
    suspend fun postProveedores(@Field("actividad") actividad: String):Response<ProveedoresDataResponse>
}