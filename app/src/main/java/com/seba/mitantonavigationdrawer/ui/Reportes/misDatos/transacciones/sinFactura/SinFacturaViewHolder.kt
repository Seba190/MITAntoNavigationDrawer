package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.sinFactura

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.databinding.ItemAlmacenBinding
import com.seba.mitantonavigationdrawer.databinding.ItemEditarSinFacturaBinding

class SinFacturaViewHolder(view: View): RecyclerView.ViewHolder(view) {
    private val binding = ItemEditarSinFacturaBinding.bind(view)
    fun bind(sinFacturaItemResponse:SinFacturaItemResponse, onItemSelected:(String) -> Unit){
        binding.tvDiaSinFactura.text = sinFacturaItemResponse.DiaSinFactura
        binding.tvFechaSinFactura.text = sinFacturaItemResponse.FechaSinFactura
        binding.tvNombreSinFactura.text = sinFacturaItemResponse.Producto
        binding.tvNombreAlmacenSinFactura.text = sinFacturaItemResponse.Almacen
        if(sinFacturaItemResponse.TipoFactura == "FACTURA_ENTRADA") {
            binding.tvUnidadesSinFactura.text = sinFacturaItemResponse.Unidades
        }else{
            binding.tvUnidadesSinFactura.text = "-${sinFacturaItemResponse.Unidades}"
        }
        binding.root.setOnClickListener { onItemSelected(sinFacturaItemResponse.Id) }
    }
}