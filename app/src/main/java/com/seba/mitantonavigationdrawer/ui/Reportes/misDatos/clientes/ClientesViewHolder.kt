package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.clientes

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.databinding.ItemClienteBinding


class ClientesViewHolder(view: View): RecyclerView.ViewHolder(view) {
    private val binding = ItemClienteBinding.bind(view)
    fun bind(clientesItemResponse: ClientesItemResponse, onItemSelected:(String) -> Unit){
        binding.tvClienteName.text = clientesItemResponse.Nombre
        binding.root.setOnClickListener {
            onItemSelected(clientesItemResponse.Id)  }
    }
}