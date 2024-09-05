package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.R


class AlmacenesAdapter (var almacenesList: List<AlmacenesItemResponse> = emptyList(),
                        private val onItemSelected:(String) -> Unit)
    : RecyclerView.Adapter<AlmacenesViewHolder>(){

     fun updateList(almacenesList: List<AlmacenesItemResponse>){
         this.almacenesList = almacenesList
         notifyDataSetChanged()
     }

   // private var filteredItemList: MutableList<AlmacenesItemResponse> = almacenesList.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlmacenesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return AlmacenesViewHolder(layoutInflater.inflate(R.layout.item_almacen,parent,false))
    }

    override fun onBindViewHolder(viewholder: AlmacenesViewHolder, position: Int) {
        val item = almacenesList[position]
        viewholder.bind(item, onItemSelected)
    }

    override fun getItemCount() = almacenesList.size

   /* fun filter(query: String) {
        filteredItemList = if (query.isEmpty()) {
            almacenesList.toMutableList()
        } else {
            // Filtra por coincidencias parciales
            almacenesList.filter { it.Nombre.contains(query,ignoreCase = true) }
                .sortedBy { it.Nombre }
                .toMutableList()
        }
        val sortedList = filteredItemList.toMutableList()
        sortedList.sortBy { it.Nombre }
        updateList(sortedList)
    }*/

}
