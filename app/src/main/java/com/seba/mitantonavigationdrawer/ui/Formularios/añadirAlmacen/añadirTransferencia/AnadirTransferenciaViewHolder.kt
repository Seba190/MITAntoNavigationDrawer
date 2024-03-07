package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirTransferencia

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.databinding.ItemElegirProductoBinding
import com.seba.mitantonavigationdrawer.ui.SharedViewModel

class AnadirTransferenciaViewHolder(view: View): RecyclerView.ViewHolder(view) {
    private val binding = ItemElegirProductoBinding.bind(view)
    fun bind(cantidad:String,producto: String , onClickDelete: (Int) -> Unit){
        binding.tvUnidades.text = cantidad
        binding.tvProducto.text = producto.substringBefore('(',producto)
        binding.cvTrash.setOnClickListener {
            onClickDelete(adapterPosition)  }
    }
}