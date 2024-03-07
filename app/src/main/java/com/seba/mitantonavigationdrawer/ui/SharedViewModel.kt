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

    fun setImageData(data: ByteArray?) {
        selectedImage.value = data
    }
    val sharedData = MutableLiveData<String>()

    val listaDeAlertas: MutableList<String> = mutableListOf()
    val listaDeBodegas : MutableList<String> = mutableListOf()
    val listaDePreciosCompra: MutableList<String> = mutableListOf()
    val listaDeProveedores : MutableList<String> = mutableListOf()
    val listaDePreciosVenta: MutableList<String> = mutableListOf()
    val listaDeClientes : MutableList<String> = mutableListOf()

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

    //Mini formulario añadir transferencia
    val listaDeCantidades : MutableList<String> = mutableListOf()
    val listaDeProductos  : MutableList<String> = mutableListOf()

    //Mini formulario añadir inventario y añadir inventario
    val listaDeCantidadesAnadir : MutableList<String> = mutableListOf()
    val listaDeProductosAnadir  : MutableList<String> = mutableListOf()
    val listaDePreciosAnadir : MutableList<String> = mutableListOf()
    val ListaDeProveedoresAnadir : MutableList<String> = mutableListOf()


    //Mini formulario remover inventario y remover inventario
    val listaDeCantidadesRemover : MutableList<String> = mutableListOf()
    val listaDeProductosRemover  : MutableList<String> = mutableListOf()
    val listaDePreciosRemover : MutableList<String> = mutableListOf()
    val ListaDeClientesRemover : MutableList<String> = mutableListOf()

    //Elegir producto de formulario añadir transferencia
    var almacen : String = ""
}


