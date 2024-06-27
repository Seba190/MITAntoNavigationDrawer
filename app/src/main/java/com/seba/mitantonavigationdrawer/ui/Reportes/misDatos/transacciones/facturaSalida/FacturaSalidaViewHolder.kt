package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.facturaSalida

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.databinding.ItemAlmacenBinding
import com.seba.mitantonavigationdrawer.databinding.ItemEditarFacturaSalidaBinding

class FacturaSalidaViewHolder(view: View): RecyclerView.ViewHolder(view) {
    private val binding = ItemEditarFacturaSalidaBinding.bind(view)
    fun bind(facturaSalidaItemResponse:FacturaSalidaItemResponse, onItemSelected:(String) -> Unit){
        binding.tvDiaFacturaSalida.text = facturaSalidaItemResponse.DiaFacturaSalida
        binding.tvFechaFacturaSalida.text =facturaSalidaItemResponse.FechaFacturaSalida
        binding.tvNombreFacturaSalida.text = facturaSalidaItemResponse.FacturaSalida
        binding.tvNombreClienteFacturaSalida.text = facturaSalidaItemResponse.Cliente
        binding.tvNombreAlmacenFacturaSalida.text = facturaSalidaItemResponse.Almacen
        binding.root.setOnClickListener { onItemSelected(facturaSalidaItemResponse.IdFacturaSalida) }
    }
}