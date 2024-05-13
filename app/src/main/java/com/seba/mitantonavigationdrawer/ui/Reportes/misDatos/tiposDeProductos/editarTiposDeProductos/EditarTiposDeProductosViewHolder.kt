package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.tiposDeProductos.editarTiposDeProductos

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.databinding.ItemEditarTiposDeProductosBinding
import com.seba.mitantonavigationdrawer.databinding.ItemProveedorBinding
import com.squareup.picasso.Picasso

class EditarTiposDeProductosViewHolder(view: View): RecyclerView.ViewHolder(view) {
    private val binding = ItemEditarTiposDeProductosBinding.bind(view)
    fun bind(editarTiposDeProductosItemResponse: EditarTiposDeProductosItemResponse, onItemSelected:(String) -> Unit){
        binding.tvProducto.text = editarTiposDeProductosItemResponse.Nombre
        binding.cvInformation.setOnClickListener {onItemSelected(editarTiposDeProductosItemResponse.Id)}
        Picasso.get().load(editarTiposDeProductosItemResponse.Imagen).into(binding.ivProducto)
    }
}