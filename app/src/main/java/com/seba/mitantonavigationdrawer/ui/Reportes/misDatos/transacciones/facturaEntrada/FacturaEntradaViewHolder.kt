package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.facturaEntrada

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.databinding.ItemAlmacenBinding
import com.seba.mitantonavigationdrawer.databinding.ItemEditarFacturaEntradaBinding

class FacturaEntradaViewHolder(view: View): RecyclerView.ViewHolder(view) {
    private val binding = ItemEditarFacturaEntradaBinding.bind(view)
    fun bind(facturaEntradaItemResponse:FacturaEntradaItemResponse, onItemSelected:(String) -> Unit){
        binding.tvDiaFacturaEntrada.text = facturaEntradaItemResponse.DiaFacturaEntrada
        binding.tvFechaFacturaEntrada.text = facturaEntradaItemResponse.FechaFacturaEntrada
        binding.tvNombreFacturaEntrada.text = facturaEntradaItemResponse.FacturaEntrada
        binding.tvNombreAlmacen.text = facturaEntradaItemResponse.Almacen
        binding.tvNombreProveedor.text = facturaEntradaItemResponse.Proveedor
        binding.root.setOnClickListener { onItemSelected(facturaEntradaItemResponse.IdFacturaEntrada) }
    }
}











