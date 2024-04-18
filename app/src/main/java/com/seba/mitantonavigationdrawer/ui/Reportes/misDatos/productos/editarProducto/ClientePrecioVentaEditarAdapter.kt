package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos.editarProducto

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.ui.SharedViewModel

class ClientePrecioVentaEditarAdapter(var clientePrecioVentaList:
                                List<ClientePrecioVentaItemResponseEditar> = emptyList(),
                                      private val sharedViewModel: SharedViewModel,
                                      private val onCheckBoxClickListener: OnCheckBoxClickListenerPrecioVentaEditar,
                                      private val listener: OnTextChangeListenerPrecioVentaEditar)
                              : RecyclerView.Adapter<ClientePrecioVentaEditarViewHolder>() {
    fun updateList(clientePrecioVentaList: List<ClientePrecioVentaItemResponseEditar>) {
        this.clientePrecioVentaList = clientePrecioVentaList
        notifyDataSetChanged()
    }

    interface OnCheckBoxClickListenerPrecioVentaEditar {
        fun onCheckBoxClick(id: Int, isChecked: Boolean)
    }

    inner class TuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.tvCustomers)

        init {
            // Configura un listener para el CheckBox en el ViewHolder
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                onCheckBoxClickListener.onCheckBoxClick(adapterPosition, isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientePrecioVentaEditarViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ClientePrecioVentaEditarViewHolder(layoutInflater.inflate(R.layout.item_precio_venta, parent, false)
        ,listener,sharedViewModel)
    }

    override fun getItemCount() = clientePrecioVentaList.size

    override fun onBindViewHolder(viewholder: ClientePrecioVentaEditarViewHolder, position: Int) {
        val item = clientePrecioVentaList[position]
        viewholder.bind(item,this)
    }

}
