package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.proveedores

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.R


class ProveedoresAdapter(var proveedoresList: List<ProveedoresItemResponse> = emptyList(),
                        private val onItemSelected:(String) -> Unit)
    : RecyclerView.Adapter<ProveedoresViewHolder>() {
    fun updateList(proveedoresList: List<ProveedoresItemResponse>){
        this.proveedoresList = proveedoresList
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProveedoresViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ProveedoresViewHolder(layoutInflater.inflate(R.layout.item_proveedor,parent,false))
    }

    override fun onBindViewHolder(viewholder: ProveedoresViewHolder, position: Int) {
        val item = proveedoresList[position]
        viewholder.bind(item, onItemSelected)
    }

    override fun getItemCount() = proveedoresList.size

}