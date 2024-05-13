package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transferencias

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.R


class TransferenciasAdapter(var transferenciasList: List<TransferenciasItemResponse> = emptyList(),
                            private val onItemSelected:(String) -> Unit)
    : RecyclerView.Adapter<TransferenciasViewHolder>() {
    fun updateList(transferenciasList: List<TransferenciasItemResponse>){
        this.transferenciasList = transferenciasList
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransferenciasViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return TransferenciasViewHolder(layoutInflater.inflate(R.layout.item_transferencia,parent,false))
    }

    override fun onBindViewHolder(viewholder: TransferenciasViewHolder, position: Int) {
        val item = transferenciasList[position]
        viewholder.bind(item, onItemSelected)
    }

    override fun getItemCount() = transferenciasList.size

}