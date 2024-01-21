package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.R

class ClientePrecioVentaAdapter(var clientePrecioVentaList: List<ClientePrecioVentaItemResponse> = emptyList())
                              : RecyclerView.Adapter<ClientePrecioVentaViewHolder>() {
    fun updateList(clientePrecioVentaList: List<ClientePrecioVentaItemResponse>) {
        this.clientePrecioVentaList = clientePrecioVentaList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientePrecioVentaViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ClientePrecioVentaViewHolder(layoutInflater.inflate(R.layout.item_precio_venta, parent, false)
        )
    }

    override fun getItemCount() = clientePrecioVentaList.size

    override fun onBindViewHolder(viewholder: ClientePrecioVentaViewHolder, position: Int) {
        val item = clientePrecioVentaList[position]
        viewholder.bind(item)
    }

}
