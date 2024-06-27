package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transferencias.editarTransferencia

import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.AlmacenesDataResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiServiceEditarTransferencia {
    @GET("Transferencias/editarTransferenciaFinal.php")
    suspend fun getTransferencias(): Response<EditarTransferenciaDataResponse>
}