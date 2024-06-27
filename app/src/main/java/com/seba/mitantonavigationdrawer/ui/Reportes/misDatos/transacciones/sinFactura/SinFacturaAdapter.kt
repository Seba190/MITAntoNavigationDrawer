package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.sinFactura

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.R


class SinFacturaAdapter (var sinFacturaList: List<SinFacturaItemResponse> = emptyList(),
                         private val onItemSelected:(String) -> Unit)
    : RecyclerView.Adapter<SinFacturaViewHolder>() {
     fun updateList(sinFacturaList: List<SinFacturaItemResponse>){
         this.sinFacturaList = sinFacturaList
         notifyDataSetChanged()
     }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SinFacturaViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return SinFacturaViewHolder(layoutInflater.inflate(R.layout.item_editar_sin_factura,parent,false))
    }

    override fun onBindViewHolder(viewholder:SinFacturaViewHolder, position: Int) {
        val item = sinFacturaList[position]
        viewholder.bind(item, onItemSelected)
    }

    override fun getItemCount() = sinFacturaList.size

}
