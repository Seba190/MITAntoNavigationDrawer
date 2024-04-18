package com.seba.mitantonavigationdrawer.ui.removerInventario

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.databinding.ItemAnadirBinding
import com.seba.mitantonavigationdrawer.databinding.ItemElegirProductoBinding
import com.seba.mitantonavigationdrawer.ui.SharedViewModel

class RemoverInventarioViewHolder(view: View, private val sharedViewModel: SharedViewModel): RecyclerView.ViewHolder(view) {
    private val binding = ItemAnadirBinding.bind(view)
    fun bind(cantidad:String,producto: String ,precio: String, onClickDelete: (Int) -> Unit){
        binding.tvUnidades.text = cantidad
        binding.tvProducto.text = producto.substringBefore('(',producto)
        if(precio.isNotEmpty()){
            binding.tvPrecio.text= "$ ${precio.toInt().times(cantidad.toInt())}"
            sharedViewModel.listaDePreciosDeProductosRemover.add(precio.toInt().times(cantidad.toInt()))
        }
        else{
            binding.tvPrecio.text = "$ ${0}"
        }
        binding.cvTrash.setOnClickListener {
            onClickDelete(adapterPosition)  }
    }
}