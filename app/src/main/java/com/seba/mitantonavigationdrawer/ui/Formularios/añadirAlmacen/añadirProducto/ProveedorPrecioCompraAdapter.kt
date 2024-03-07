package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.ui.SharedViewModel

class ProveedorPrecioCompraAdapter(
                                   var proveedorPrecioCompraList:
                                   List<ProveedorPrecioCompraItemResponse> = emptyList(),
                                   private val sharedViewModel: SharedViewModel,
                                   private val listener: OnTextChangeListenerPrecioCompra,
                                   private val onCheckBoxClickListener: OnCheckBoxClickListenerPrecioCompra)
                              : RecyclerView.Adapter<ProveedorPrecioCompraViewHolder>() {

    fun updateList(proveedorPrecioCompraList: List<ProveedorPrecioCompraItemResponse>){
        this.proveedorPrecioCompraList = proveedorPrecioCompraList
        notifyDataSetChanged()
    }
    interface OnCheckBoxClickListenerPrecioCompra {
        fun onCheckBoxClick(id: Int, isChecked: Boolean)
    }

    inner class TuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkBox: CheckBox = itemView.findViewById(R.id.tvSuppliers)

        init {
            // Configura un listener para el CheckBox en el ViewHolder
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                onCheckBoxClickListener.onCheckBoxClick(adapterPosition, isChecked)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProveedorPrecioCompraViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ProveedorPrecioCompraViewHolder(layoutInflater.inflate(R.layout.item_precio_compra,parent,false)
            ,listener,sharedViewModel)
    }

    override fun onBindViewHolder(viewholder: ProveedorPrecioCompraViewHolder, position: Int) {
        val item = proveedorPrecioCompraList[position]
        viewholder.bind(item,this)

    }

    override fun getItemCount() = proveedorPrecioCompraList.size
}