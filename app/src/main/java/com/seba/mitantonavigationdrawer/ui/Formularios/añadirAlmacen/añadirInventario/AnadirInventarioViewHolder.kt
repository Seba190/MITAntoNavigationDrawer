package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirInventario

import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.databinding.ItemAnadirBinding
import com.seba.mitantonavigationdrawer.databinding.ItemElegirProductoBinding
import com.seba.mitantonavigationdrawer.ui.SharedViewModel

class AnadirInventarioViewHolder(view: View, private val sharedViewModel: SharedViewModel): RecyclerView.ViewHolder(view) {
    private val binding = ItemAnadirBinding.bind(view)
    fun bind(cantidad:String,producto: String ,precio: String, onClickDelete: (Int) -> Unit){
        binding.tvUnidades.text = cantidad
        binding.tvProducto.text = producto.substringBefore('(',producto).uppercase()
        if(precio.isNotEmpty() && cantidad.isNotEmpty()){
                binding.tvPrecio.text = "$ ${precio.toInt().times(cantidad.toInt())}"
                sharedViewModel.listaDePreciosDeProductos.add(
                    precio.toInt().times(cantidad.toInt()))

        } else{
            binding.tvPrecio.text = "$ ${0}"
        }
        binding.cvTrash.setOnClickListener {
            onClickDelete(adapterPosition)  }

    }
}