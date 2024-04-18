package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.databinding.ItemProductoBinding
import com.squareup.picasso.Picasso

class ProductosViewHolder(view: View): RecyclerView.ViewHolder(view) {
    private val binding = ItemProductoBinding.bind(view)
    fun bind(productosItemResponse: ProductosItemResponse, onItemSelected:(String) -> Unit){
        binding.tvProducto.text = productosItemResponse.Nombre
        binding.tvInventario.text = productosItemResponse.Inventario
        binding.root.setOnClickListener {
            onItemSelected(productosItemResponse.Id)  }
        Picasso.get().load(productosItemResponse.Imagen).into(binding.ivProducto)
    }
}