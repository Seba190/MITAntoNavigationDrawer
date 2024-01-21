package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.databinding.ItemAlertBinding
import com.seba.mitantonavigationdrawer.databinding.ItemPrecioCompraBinding
import com.seba.mitantonavigationdrawer.databinding.ItemPrecioVentaBinding


class ClientePrecioVentaViewHolder(view: View): RecyclerView.ViewHolder(view) {
    private val binding = ItemPrecioVentaBinding.bind(view)
    fun bind(clientePrecioVentaItemResponse: ClientePrecioVentaItemResponse) {
        binding.tvCustomers.text = clientePrecioVentaItemResponse.Nombre
        binding.cbCustomers.setOnClickListener {
            binding.etPrecioVenta.isEnabled = binding.cbCustomers.isChecked
        }
       // binding.etPrecioVenta.isEnabled = binding.cbCustomers.isChecked
        // Verificar si el CheckBox está marcado o no
       // binding.etPrecioVenta.isVisible = binding.cbCustomers.isChecked

    }


}