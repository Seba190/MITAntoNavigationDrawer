package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.clientes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.R


class ClientesAdapter(var clientesList: List<ClientesItemResponse> = emptyList(),
                        private val onItemSelected:(String) -> Unit)
    : RecyclerView.Adapter<ClientesViewHolder>() {
    fun updateList(clientesList: List<ClientesItemResponse>){
        this.clientesList = clientesList
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ClientesViewHolder(layoutInflater.inflate(R.layout.item_cliente,parent,false))
    }

    override fun onBindViewHolder(viewholder: ClientesViewHolder, position: Int) {
        val item = clientesList[position]
        viewholder.bind(item, onItemSelected)
    }

    override fun getItemCount() = clientesList.size

}