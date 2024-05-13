package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.tiposDeProductos.editarTiposDeProductos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.R


class EditarTiposDeProductosAdapter(var productosList: List<EditarTiposDeProductosItemResponse> = emptyList(),
                                    private val onItemSelected:(String) -> Unit)
    : RecyclerView.Adapter<EditarTiposDeProductosViewHolder>() {
    fun updateList(tiposDeProductosList: List<EditarTiposDeProductosItemResponse>){
        this.productosList = tiposDeProductosList
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditarTiposDeProductosViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return EditarTiposDeProductosViewHolder(layoutInflater.inflate(R.layout.item_editar_tipos_de_productos,parent,false))
    }

    override fun onBindViewHolder(viewholder: EditarTiposDeProductosViewHolder, position: Int) {
        val item = productosList[position]
        viewholder.bind(item, onItemSelected)
    }

    override fun getItemCount() = productosList.size

}