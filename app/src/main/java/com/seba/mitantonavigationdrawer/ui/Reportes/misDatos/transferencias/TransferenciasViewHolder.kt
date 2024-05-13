package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transferencias

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.databinding.ItemClienteBinding
import com.seba.mitantonavigationdrawer.databinding.ItemTransferenciaBinding


class TransferenciasViewHolder(view: View): RecyclerView.ViewHolder(view) {
    private val binding = ItemTransferenciaBinding.bind(view)
    fun bind(transferenciasItemResponse: TransferenciasItemResponse, onItemSelected:(String) -> Unit){
        binding.tvClienteName.text = transferenciasItemResponse.Nombre
        binding.root.setOnClickListener {
            onItemSelected(transferenciasItemResponse.Id)  }
    }
}