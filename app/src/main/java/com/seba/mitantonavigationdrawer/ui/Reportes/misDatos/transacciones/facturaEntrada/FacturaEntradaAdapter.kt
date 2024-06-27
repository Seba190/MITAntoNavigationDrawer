package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.facturaEntrada

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.R


class FacturaEntradaAdapter (var facturaEntradaList: List<FacturaEntradaItemResponse> = emptyList(),
                             private val onItemSelected:(String) -> Unit)
    : RecyclerView.Adapter<FacturaEntradaViewHolder>() {
     fun updateList(facturaEntradaList: List<FacturaEntradaItemResponse>){
         this.facturaEntradaList = facturaEntradaList
         notifyDataSetChanged()
     }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacturaEntradaViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return FacturaEntradaViewHolder(layoutInflater.inflate(R.layout.item_editar_factura_entrada,parent,false))
    }

    override fun onBindViewHolder(viewholder: FacturaEntradaViewHolder, position: Int) {
        val item = facturaEntradaList[position]
        viewholder.bind(item, onItemSelected)
    }

    override fun getItemCount() = facturaEntradaList.size

}
