package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.databinding.ItemPrecioCompraBinding


class ProveedorPrecioCompraViewHolder(view: View): RecyclerView.ViewHolder(view) {
    private val binding = ItemPrecioCompraBinding.bind(view)
    fun bind(proveedorPrecioCompraItemResponse: ProveedorPrecioCompraItemResponse) {
        binding.tvSuppliers.text = proveedorPrecioCompraItemResponse.Nombre
        binding.cbSuppliers.setOnClickListener {
            binding.etPrecioCompra.isEnabled = binding.cbSuppliers.isChecked
        }
        // Verificar si el CheckBox está marcado o no
        //binding.etPrecioCompra.isVisible = binding.cbSuppliers.isChecked

    }


}