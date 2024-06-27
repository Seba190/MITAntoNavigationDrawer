package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.facturaSalida

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.R


class FacturaSalidaAdapter (var facturaSalidaList: List<FacturaSalidaItemResponse> = emptyList(),
                            private val onItemSelected:(String) -> Unit)
    : RecyclerView.Adapter<FacturaSalidaViewHolder>() {
     fun updateList(facturaSalidaList: List<FacturaSalidaItemResponse>){
         this.facturaSalidaList = facturaSalidaList
         notifyDataSetChanged()
     }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacturaSalidaViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return FacturaSalidaViewHolder(layoutInflater.inflate(R.layout.item_editar_factura_salida,parent,false))
    }

    override fun onBindViewHolder(viewholder: FacturaSalidaViewHolder, position: Int) {
        val item = facturaSalidaList[position]
        viewholder.bind(item, onItemSelected)
    }

    override fun getItemCount() = facturaSalidaList.size

}
