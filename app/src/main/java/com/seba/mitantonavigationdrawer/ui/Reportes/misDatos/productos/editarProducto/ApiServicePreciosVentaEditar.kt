package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos.editarProducto

import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto.ClientePrecioVentaDataResponse
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto.ProveedorPrecioCompraDataResponse
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.AlmacenesDataResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiServicePreciosVentaEditar {
    @GET("Producto/clientePrecioVenta.php")
    suspend fun getClientePrecioVenta(): Response<ClientePrecioVentaDataResponseEditar>
}