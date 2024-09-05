package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes

import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.ItemAlmacenBinding

class AlmacenesViewHolder(view: View): RecyclerView.ViewHolder(view) {
    private val binding = ItemAlmacenBinding.bind(view)
    fun bind(almacenesItemResponse:AlmacenesItemResponse, onItemSelected:(String) -> Unit){
        binding.tvAlmacenName.text = almacenesItemResponse.Nombre
        binding.root.setOnClickListener {
            onItemSelected(almacenesItemResponse.Id)  }
    }
}