package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirTransferencia

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.ui.SharedViewModel

class AnadirTransferenciaAdapter (var listaDeCantidades: MutableList<String>
,var listaDeProductos: MutableList<String>
, private val sharedViewModel: SharedViewModel
,private val onClickDelete:(Int) -> Unit)
    : RecyclerView.Adapter<AnadirTransferenciaViewHolder>() {

     fun updateList(listaDeCantidad: MutableList<String>,listaDeProducto: MutableList<String>){
         this.listaDeCantidades =listaDeCantidad
         this.listaDeProductos = listaDeProducto
         notifyDataSetChanged()
     }

     fun updateData(newDataCantidad: MutableList<String>, newDataProducto: MutableList<String>){
         listaDeCantidades = newDataCantidad
         listaDeProductos = newDataProducto
         notifyDataSetChanged()
     }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnadirTransferenciaViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_elegir_producto,parent,false)
        return AnadirTransferenciaViewHolder(v,sharedViewModel)
    }

    override fun getItemCount(): Int {
        return listaDeCantidades.size
    }

    override fun onBindViewHolder(holder: AnadirTransferenciaViewHolder, position: Int) {
        val itemCantidad = listaDeCantidades[position]
        val itemProducto = listaDeProductos[position]
        holder.bind(itemCantidad,itemProducto,position,onClickDelete)

    }
    fun getDataProductoAtPosition(position: Int): String {
        return listaDeProductos[position]
    }

    fun getDataCantidadAtPosition(position: Int): String {
        return listaDeCantidades[position]
    }


}