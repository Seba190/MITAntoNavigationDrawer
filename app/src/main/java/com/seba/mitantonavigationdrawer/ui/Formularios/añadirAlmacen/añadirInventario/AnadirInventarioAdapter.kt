package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirInventario

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.ui.SharedViewModel

class AnadirInventarioAdapter (var listaDeCantidadesAnadir: MutableList<String>
                               , var listaDeProductosAnadir: MutableList<String>
                               , var listaDePreciosAnadir: MutableList<String>,
                                private val sharedViewModel: SharedViewModel
                               , private val onClickDelete:(Int) -> Unit)
    : RecyclerView.Adapter<AnadirInventarioViewHolder>() {

     fun updateList(listaDeCantidad: MutableList<String>,listaDeProducto: MutableList<String>, listaDePrecio: MutableList<String>){
         this.listaDeCantidadesAnadir =listaDeCantidad
         this.listaDeProductosAnadir = listaDeProducto
         this.listaDePreciosAnadir = listaDePrecio
         notifyDataSetChanged()
     }

     fun updateData(newDataCantidad: MutableList<String>, newDataProducto: MutableList<String>,newDataPrecio: MutableList<String>){
         listaDeCantidadesAnadir = newDataCantidad
         listaDeProductosAnadir = newDataProducto
         listaDePreciosAnadir = newDataPrecio
         notifyDataSetChanged()
     }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnadirInventarioViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_anadir,parent,false)
        return AnadirInventarioViewHolder(v,sharedViewModel)
    }

    override fun getItemCount(): Int {
        return listaDeCantidadesAnadir.size
    }

    override fun onBindViewHolder(holder: AnadirInventarioViewHolder, position: Int) {
        val itemCantidad = listaDeCantidadesAnadir[position]
        val itemProducto = listaDeProductosAnadir[position]
        val itemPrecio = listaDePreciosAnadir[position]
        holder.bind(itemCantidad,itemProducto,itemPrecio,onClickDelete)

    }


}