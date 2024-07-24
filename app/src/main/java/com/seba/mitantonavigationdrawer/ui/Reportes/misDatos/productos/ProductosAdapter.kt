package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.R



class ProductosAdapter(var productosList: List<ProductosItemResponse> = emptyList(),
                      private val onItemSelected:(String) -> Unit)
    : RecyclerView.Adapter<ProductosViewHolder>() {

    fun updateList(productosList: List<ProductosItemResponse>){
        this.productosList = productosList
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductosViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ProductosViewHolder(layoutInflater.inflate(R.layout.item_producto,parent,false))
    }

    override fun onBindViewHolder(viewholder: ProductosViewHolder, position: Int) {
        val item = productosList[position]
        viewholder.bind(item, onItemSelected)
    }

    override fun getItemCount() = productosList.size

}