package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.R

class ProveedorPrecioCompraAdapter(var proveedorPrecioCompraList:
                              List<ProveedorPrecioCompraItemResponse> = emptyList())
                              : RecyclerView.Adapter<ProveedorPrecioCompraViewHolder>() {

    fun updateList(proveedorPrecioCompraList: List<ProveedorPrecioCompraItemResponse>){
        this.proveedorPrecioCompraList = proveedorPrecioCompraList
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProveedorPrecioCompraViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ProveedorPrecioCompraViewHolder(layoutInflater.inflate(R.layout.item_precio_compra,parent,false))
    }

    override fun onBindViewHolder(viewholder: ProveedorPrecioCompraViewHolder, position: Int) {
        val item = proveedorPrecioCompraList[position]
        viewholder.bind(item)

    }

    override fun getItemCount() = proveedorPrecioCompraList.size
}