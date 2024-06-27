package com.seba.mitantonavigationdrawer.ui.inventario

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.R


class InventarioAdapter (var inventarioList: List<InventarioItemResponse> = emptyList())
    : RecyclerView.Adapter<InventarioViewHolder>() {
     fun updateList(inventarioList: List<InventarioItemResponse>){
         this.inventarioList = inventarioList
         notifyDataSetChanged()
     }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventarioViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return InventarioViewHolder(layoutInflater.inflate(R.layout.item_inventario,parent,false))
    }

    override fun onBindViewHolder(viewholder: InventarioViewHolder, position: Int) {
        val item = inventarioList[position]
        viewholder.bind(item)
    }

    override fun getItemCount() = inventarioList.size

}
