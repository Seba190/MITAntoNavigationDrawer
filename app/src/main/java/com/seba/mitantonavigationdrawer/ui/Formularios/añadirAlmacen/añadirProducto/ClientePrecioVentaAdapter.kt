package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.R

class ClientePrecioVentaAdapter(var clientePrecioVentaList:
                                List<ClientePrecioVentaItemResponse> = emptyList(),
                                private val onCheckBoxClickListener: OnCheckBoxClickListenerPrecioVenta,
                                private val listener: OnTextChangeListenerPrecioVenta)
                              : RecyclerView.Adapter<ClientePrecioVentaViewHolder>() {
    fun updateList(clientePrecioVentaList: List<ClientePrecioVentaItemResponse>) {
        this.clientePrecioVentaList = clientePrecioVentaList
        notifyDataSetChanged()
    }

    interface OnCheckBoxClickListenerPrecioVenta {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientePrecioVentaViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ClientePrecioVentaViewHolder(layoutInflater.inflate(R.layout.item_precio_venta, parent, false)
        ,listener)
    }

    override fun getItemCount() = clientePrecioVentaList.size

    override fun onBindViewHolder(viewholder: ClientePrecioVentaViewHolder, position: Int) {
        val item = clientePrecioVentaList[position]
        viewholder.bind(item)
    }

}
