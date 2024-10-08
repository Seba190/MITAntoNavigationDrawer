package com.seba.mitantonavigationdrawer.ui

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val selectedImage = MutableLiveData<ByteArray?>()
    val CodigoDeBarra = MutableLiveData<String>()
    val CodigoDeBarraTransferencia = MutableLiveData<String>()
    val CodigoDeBarraAnadir = MutableLiveData<String>()
    val CodigoDeBarraRemover = MutableLiveData<String>()

    val id : MutableList<String> = mutableListOf()

    fun setImageData(data: ByteArray?) {
        selectedImage.value = data
    }

    val sharedData = MutableLiveData<String>()

    val listaDeAlertas: MutableList<String> = mutableListOf()
    var listaDeBodegas : MutableList<String> = mutableListOf()
    val listaDePreciosCompra: MutableList<String> = mutableListOf()
    var listaDeProveedores : MutableList<String> = mutableListOf()
    val listaDePreciosVenta: MutableList<String> = mutableListOf()
    var listaDeClientes : MutableList<String> = mutableListOf()

    val listaDeAlertasAnadir: MutableList<String> = mutableListOf()
    var listaDeBodegasAnadir : MutableList<String> = mutableListOf()
    val listaDePreciosCompraAnadir: MutableList<String> = mutableListOf()
    var listaDeProveedoresAnadir : MutableList<String> = mutableListOf()
    val listaDePreciosVentaAnadir: MutableList<String> = mutableListOf()
    var listaDeClientesAnadir : MutableList<String> = mutableListOf()

    // Mini formularios añadir productos
     val ListasDeAlmacenes : MutableList<String> = mutableListOf()
     val ListasDeAlertas : MutableList<String> = mutableListOf()
     val ListasDeProductosAlertas : MutableList<String> = mutableListOf()
     val ListasDeProveedores : MutableList<String> = mutableListOf()
     val ListasDePreciosDeCompra : MutableList<String> = mutableListOf()
     val ListasDeProductosPrecioCompra : MutableList<String> = mutableListOf()
     val ListasDeClientes : MutableList<String> = mutableListOf()
     val ListasDePreciosDeVenta : MutableList<String> = mutableListOf()
     val ListasDeProductosPrecioVenta : MutableList<String> = mutableListOf()

    //Reporte editar producto
    var almacenesList: MutableList<String> = mutableListOf()
    var clientesList: MutableList<String> = mutableListOf()
    var proveedoresList: MutableList<String> = mutableListOf()
    val numeroAlertas: MutableList<String> = mutableListOf()
    val numeroPreciosCompra: MutableList<String> = mutableListOf()
    val numeroPreciosVenta: MutableList<String> = mutableListOf()
    val opcionesListEditarProductoTipoDeProducto : MutableList<String> = mutableListOf()
    //val alertasFiltradas: MutableList<String>  = mutableListOf()


    //Mini formulario añadir transferencia y añadir transferencia
    var listaDeCantidades : MutableList<String> = mutableListOf()
    var listaDeProductos  : MutableList<String> = mutableListOf()
    val listaDeCantidadesAntigua : MutableList<String> = mutableListOf()
    val listaDeProductosAntigua : MutableList<String> = mutableListOf()
    val opcionesListTransferencia: MutableList<String> = mutableListOf()
    val listaDeAlmacenesTransferencia : MutableList<String> = mutableListOf()
    val opcionesListTransferenciaOrigen : MutableList<String> = mutableListOf()
    val opcionesListTransferenciaDestino : MutableList<String> = mutableListOf()
    var cantidadTotalTransferencia : MutableList<Int> = mutableListOf()
    val cantidadDeProductos : MutableList<Int> = mutableListOf()


    //Mini formulario añadir inventario y añadir inventario
    var listaDeCantidadesAnadir : MutableList<String> = mutableListOf()
    var listaDeProductosAnadir  : MutableList<String> = mutableListOf()
    var listaDePreciosAnadir : MutableList<String> = mutableListOf()
    var almacenAnadir : String = ""
    var proveedorAnadir : String = ""
    val listaDeAlmacenesAnadir : MutableList<String> = mutableListOf()
    val opcionesListAnadir : MutableList<String> = mutableListOf()
    val opcionesListAnadirProveedor : MutableList<String> = mutableListOf()
    val opcionesListAnadirAlmacen : MutableList<String> = mutableListOf()
    var facturaTotalAnadir : MutableList<Int> = mutableListOf()

    //Añadir inventario view holder
    val listaDePreciosDeProductos : MutableList<Int> = mutableListOf()

    //Mini formulario remover inventario y remover inventario
    var listaDeCantidadesRemover : MutableList<String> = mutableListOf()
    var listaDeProductosRemover  : MutableList<String> = mutableListOf()
    var listaDePreciosRemover : MutableList<String> = mutableListOf()
    var clienteRemover : String = ""
    var almacenRemover : String = ""
    val listaDePreciosDeProductosRemover : MutableList<Int> = mutableListOf()
    val opcionesListRemover : MutableList<String> = mutableListOf()
    val listaDeAlmacenesRemover : MutableList<String> = mutableListOf()
    val opcionesListRemoverCliente : MutableList<String> = mutableListOf()
    val opcionesListRemoverAlmacen : MutableList<String> = mutableListOf()
    var facturaTotalRemover : MutableList<Int> = mutableListOf()



    //Elegir producto de formulario añadir transferencia
    var almacen : String = ""


    //Editar cantidad tipo de producto
    val inventario : MutableList<String> = mutableListOf()

    //Editar cantidad producto
    val almacenes : MutableList<String> = mutableListOf()

    //Editar cantidad Almacenes
    val productos : MutableList<String> = mutableListOf()

    //Editar transferencia
    val transferenciasId : MutableList<String> = mutableListOf()
    val opcionesListEditarTransferencia : MutableList<String> = mutableListOf()
    val listaDeAlmacenesEditarTransferencia : MutableList<String> = mutableListOf()
    val opcionesListEditarTransferenciaOrigen : MutableList<String> = mutableListOf()
    val opcionesListEditarTransferenciaDestino : MutableList<String> = mutableListOf()
    var cantidadTotalEditarTransferencia : MutableList<Int> = mutableListOf()
    var listaCombinadaTransferencia : MutableList<Pair<String,Int>>  = mutableListOf()

    //Editar factura de entrada
    val opcionesListEntrada : MutableList<String> = mutableListOf()
    val listaDeAlmacenesEntrada : MutableList<String> = mutableListOf()
    val opcionesListEntradaAlmacen : MutableList<String> = mutableListOf()
    val opcionesListEntradaProveedor : MutableList<String> = mutableListOf()
    var facturaTotalEntrada : MutableList<Int> = mutableListOf()
    var listaCombinadaEntrada : MutableList<Pair<String,Int>>  = mutableListOf()

    //Editar factura de salida
    val opcionesListSalida : MutableList<String> = mutableListOf()
    val listaDeAlmacenesSalida : MutableList<String> = mutableListOf()
    val opcionesListSalidaCliente : MutableList<String> = mutableListOf()
    val opcionesListSalidaAlmacen : MutableList<String> = mutableListOf()
    var facturaTotalSalida : MutableList<Int> = mutableListOf()
    var listaCombinadaSalida : MutableList<Pair<String,Int>>  = mutableListOf()

    //Añadir producto
    val opcionesListProductoTipoDeProducto : MutableList<String> = mutableListOf()

    //Añadir almacén
    val opcionesListAlmacenUsuario : MutableList<String> = mutableListOf()

    //Editar Almacén
    val opcionesListEditarAlmacenUsuario : MutableList<String> = mutableListOf()

    //Editar sin factura
    val opcionesListSinFacturaAlmacen : MutableList<String> = mutableListOf()
    val opcionesListSinFacturaProducto : MutableList<String> = mutableListOf()
}


