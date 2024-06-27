package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.sinFactura

import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.facturaEntrada.FacturaEntradaDataResponse
import retrofit2.Response
import retrofit2.http.GET


interface ApiServiceSinFactura {
    @GET("Transacciones/SinFactura/sinFacturaFinal.php")
    suspend fun getSinFacturas():Response<SinFacturaDataResponse>

}


