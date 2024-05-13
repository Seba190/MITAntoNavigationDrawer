package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transferencias

import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.AlmacenesDataResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiServiceTransferencias {
    @GET("Transferencias/transferenciaFinal.php")
    suspend fun getTransfernecias(): Response<TransferenciasDataResponse>
}