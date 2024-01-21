package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.R


class AlmacenesAdapter (var almacenesList: List<AlmacenesItemResponse> = emptyList(),
                        private val onItemSelected:(String) -> Unit)
    : RecyclerView.Adapter<AlmacenesViewHolder>() {
     fun updateList(almacenesList: List<AlmacenesItemResponse>){
         this.almacenesList = almacenesList
         notifyDataSetChanged()
     }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlmacenesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return AlmacenesViewHolder(layoutInflater.inflate(R.layout.item_almacen,parent,false))
    }

    override fun onBindViewHolder(viewholder: AlmacenesViewHolder, position: Int) {
        val item = almacenesList[position]
        viewholder.bind(item, onItemSelected)
    }

    override fun getItemCount() = almacenesList.size

}
