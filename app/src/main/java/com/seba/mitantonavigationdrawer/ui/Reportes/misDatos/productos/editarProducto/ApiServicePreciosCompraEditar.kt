package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos.editarProducto

import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto.ProveedorPrecioCompraDataResponse
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.AlmacenesDataResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiServicePreciosCompraEditar {
    @GET("Producto/proveedorPrecioCompra.php")
    suspend fun getProveedorPrecioCompra(): Response<ProveedorPrecioCompraDataResponseEditar>
}