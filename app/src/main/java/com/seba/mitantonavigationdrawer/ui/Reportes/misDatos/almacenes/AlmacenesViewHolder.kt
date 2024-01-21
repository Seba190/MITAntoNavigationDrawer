package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.databinding.ItemAlmacenBinding

class AlmacenesViewHolder(view: View): RecyclerView.ViewHolder(view) {
    private val binding = ItemAlmacenBinding.bind(view)
    fun bind(almacenesItemResponse:AlmacenesItemResponse, onItemSelected:(String) -> Unit){
        binding.tvAlmacenName.text = almacenesItemResponse.Nombre
        binding.tvImagenName.text = almacenesItemResponse.Imagen
        binding.tvDescripcionName.text = almacenesItemResponse.Descripcion
        binding.root.setOnClickListener {
            onItemSelected(almacenesItemResponse.Id)  }
    }
}