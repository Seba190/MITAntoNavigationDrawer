package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.tiposDeProductos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.R


class TiposDeProductosAdapter(var tiposDeProductosList: List<TiposDeProductosItemResponse> = emptyList(),
                              private val onItemSelected:(String) -> Unit)
    : RecyclerView.Adapter<TiposDeProductosViewHolder>() {
    fun updateList(tiposDeProductosList: List<TiposDeProductosItemResponse>){
        this.tiposDeProductosList = tiposDeProductosList
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TiposDeProductosViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return TiposDeProductosViewHolder(layoutInflater.inflate(R.layout.item_proveedor,parent,false))
    }

    override fun onBindViewHolder(viewholder: TiposDeProductosViewHolder, position: Int) {
        val item = tiposDeProductosList[position]
        viewholder.bind(item, onItemSelected)
    }

    override fun getItemCount() = tiposDeProductosList.size

}