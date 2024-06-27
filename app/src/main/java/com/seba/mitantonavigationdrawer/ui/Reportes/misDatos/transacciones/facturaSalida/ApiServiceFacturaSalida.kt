package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.facturaSalida

import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.facturaEntrada.FacturaEntradaDataResponse
import retrofit2.Response
import retrofit2.http.GET


interface ApiServiceFacturaSalida {
    @GET("Transacciones/FacturaSalida/facturaSalidaFinal.php")
    suspend fun getFacturasSalida():Response<FacturaSalidaDataResponse>

}


