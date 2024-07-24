package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProductosViewModel : ViewModel() {
    private val _productos = MutableLiveData<List<ProductosItemResponse>>()
    val productos: LiveData<List<ProductosItemResponse>> get() = _productos

    fun updateProductos(newProductos: List<ProductosItemResponse>) {
        _productos.value = newProductos
    }
}