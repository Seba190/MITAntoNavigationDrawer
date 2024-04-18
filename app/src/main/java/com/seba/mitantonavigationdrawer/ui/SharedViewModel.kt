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


    //Mini formulario añadir transferencia
    val listaDeCantidades : MutableList<String> = mutableListOf()
    val listaDeProductos  : MutableList<String> = mutableListOf()
    val listaDeCantidadesAntigua : MutableList<String> = mutableListOf()
    val listaDeProductosAntigua : MutableList<String> = mutableListOf()

    //Mini formulario añadir inventario y añadir inventario
    val listaDeCantidadesAnadir : MutableList<String> = mutableListOf()
    val listaDeProductosAnadir  : MutableList<String> = mutableListOf()
    val listaDePreciosAnadir : MutableList<String> = mutableListOf()
    var almacenAnadir : String = ""
    var proveedorAnadir : String = ""

    //Añadir inventario view holder
    val listaDePreciosDeProductos : MutableList<Int> = mutableListOf()

    //Mini formulario remover inventario y remover inventario
    val listaDeCantidadesRemover : MutableList<String> = mutableListOf()
    val listaDeProductosRemover  : MutableList<String> = mutableListOf()
    val listaDePreciosRemover : MutableList<String> = mutableListOf()
    var clienteRemover : String = ""
    var almacenRemover : String = ""
    val listaDePreciosDeProductosRemover : MutableList<Int> = mutableListOf()

    //Elegir producto de formulario añadir transferencia
    var almacen : String = ""

}


