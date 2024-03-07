package com.seba.mitantonavigationdrawer.ui.removerInventario

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.ui.SharedViewModel

class RemoverInventarioAdapter (var listaDeCantidadesRemover: MutableList<String>
                                , var listaDeProductosRemover: MutableList<String>
                                , var listaDePreciosRemover: MutableList<String>
                                , private val onClickDelete:(Int) -> Unit)
    : RecyclerView.Adapter<RemoverInventarioViewHolder>() {

     fun updateList(listaDeCantidad: MutableList<String>,listaDeProducto: MutableList<String>, listaDePrecio: MutableList<String>){
         this.listaDeCantidadesRemover =listaDeCantidad
         this.listaDeProductosRemover = listaDeProducto
         this.listaDePreciosRemover = listaDePrecio
         notifyDataSetChanged()
     }

     fun updateData(newDataCantidad: MutableList<String>, newDataProducto: MutableList<String>,newDataPrecio: MutableList<String>){
         listaDeCantidadesRemover = newDataCantidad
         listaDeProductosRemover = newDataProducto
         listaDePreciosRemover = newDataPrecio
         notifyDataSetChanged()
     }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RemoverInventarioViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_anadir,parent,false)
        return RemoverInventarioViewHolder(v)
    }

    override fun getItemCount(): Int {
        return listaDeCantidadesRemover.size
    }

    override fun onBindViewHolder(holder: RemoverInventarioViewHolder, position: Int) {
        val itemCantidad = listaDeCantidadesRemover[position]
        val itemProducto = listaDeProductosRemover[position]
        val itemPrecio = listaDePreciosRemover[position]
        holder.bind(itemCantidad,itemProducto,itemPrecio,onClickDelete)

    }


}