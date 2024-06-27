package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.facturaEntrada

import retrofit2.Response
import retrofit2.http.GET


interface ApiServiceFacturaEntrada {
    @GET("Transacciones/FacturaEntrada/facturaEntradaFinal.php")
    suspend fun getFacturasEntrada():Response<FacturaEntradaDataResponse>

}


