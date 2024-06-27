package com.seba.mitantonavigationdrawer.ui.inventario

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.databinding.ItemAlmacenBinding
import com.seba.mitantonavigationdrawer.databinding.ItemInventarioBinding

class InventarioViewHolder(view: View): RecyclerView.ViewHolder(view) {
    private val binding = ItemInventarioBinding.bind(view)
    fun bind(inventarioItemResponse:InventarioItemResponse){
        binding.tvProductoInventario.text = inventarioItemResponse.Producto
        binding.tvCantidadInventario.text = inventarioItemResponse.Cantidad
       // binding.tvImagenName.text = almacenesItemResponse.Imagen
       // binding.tvDescripcionName.text = almacenesItemResponse.Descripcion
    }
}