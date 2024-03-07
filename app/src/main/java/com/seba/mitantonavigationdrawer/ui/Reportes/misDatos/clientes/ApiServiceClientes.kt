package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.clientes

import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.AlmacenesDataResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiServiceClientes {
    @GET("Clientes/clienteFinal.php")
    suspend fun getClientes(): Response<ClientesDataResponse>
}