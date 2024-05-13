package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.tiposDeProductos.editarTiposDeProductos

import androidx.fragment.app.activityViewModels
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.tiposDeProductos.TiposDeProductosDataResponse
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiServiceEditarTiposDeProductos {

        @GET("TiposDeProducto/editarTiposDeProductosFinal.php")
        suspend fun getProductos(@Query("ID_TIPO_PRODUCTO") id: String): Response<EditarTiposDeProductosDataResponse>

}