package com.seba.mitantonavigationdrawer.ui

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val selectedImage = MutableLiveData<ByteArray?>()
    val CodigoDeBarra = MutableLiveData<String>()
    val CodigoDeBarraTransferencia = MutableLiveData<String>()

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

     val ListasDeAlmacenes : MutableList<String> = mutableListOf()
     val ListasDeAlertas : MutableList<String> = mutableListOf()
     val ListasDeProductosAlertas : MutableList<String> = mutableListOf()
     val ListasDeProveedores : MutableList<String> = mutableListOf()
     val ListasDePreciosDeCompra : MutableList<String> = mutableListOf()
     val ListasDeProductosPrecioCompra : MutableList<String> = mutableListOf()
     val ListasDeClientes : MutableList<String> = mutableListOf()
     val ListasDePreciosDeVenta : MutableList<String> = mutableListOf()
     val ListasDeProductosPrecioVenta : MutableList<String> = mutableListOf()
}


