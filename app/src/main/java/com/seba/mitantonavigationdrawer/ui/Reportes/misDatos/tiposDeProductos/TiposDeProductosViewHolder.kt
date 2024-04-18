package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.tiposDeProductos

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.databinding.ItemProveedorBinding

class TiposDeProductosViewHolder(view: View): RecyclerView.ViewHolder(view) {
    private val binding = ItemProveedorBinding.bind(view)
    fun bind(proveedoresItemResponse: TiposDeProductosItemResponse, onItemSelected:(String) -> Unit){
        binding.tvProveedorName.text = proveedoresItemResponse.Nombre
        binding.root.setOnClickListener {
            onItemSelected(proveedoresItemResponse.Id)  }
    }
}