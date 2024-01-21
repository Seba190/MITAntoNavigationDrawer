package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirInventario

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.AlmacenesViewHolder

class AnadirInventarioAdapter(var productosList: List<ProductosItemResponse> = emptyList(),
                              private val onItemSelected:(String) -> Unit
    ): RecyclerView.Adapter<AnadirInventarioViewHolder>() {


    fun updateList(almacenesList: List<ProductosItemResponse>){
        this.productosList = almacenesList
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnadirInventarioViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return AnadirInventarioViewHolder(layoutInflater.inflate(R.layout.item_producto,parent,false))
    }

    override fun getItemCount() = productosList.size
    override fun onBindViewHolder(viewholder: AnadirInventarioViewHolder, position: Int) {
        val item = productosList[position]
        viewholder.bind(item, onItemSelected)
    }
    fun filtrar(listaFiltrada: List<ProductosItemResponse>){
        this.productosList = listaFiltrada
        notifyDataSetChanged()
    }
    fun obtenerNombreEnPosicion(posicion: Int): String {
        notifyDataSetChanged()
        return this.productosList[posicion].Nombre
    }
}