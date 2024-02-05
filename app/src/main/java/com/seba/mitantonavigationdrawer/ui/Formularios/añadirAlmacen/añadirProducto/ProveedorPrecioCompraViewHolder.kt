package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.databinding.ItemPrecioCompraBinding


class ProveedorPrecioCompraViewHolder(view: View, private val listener: OnTextChangeListenerPrecioCompra): RecyclerView.ViewHolder(view) {
    private val binding = ItemPrecioCompraBinding.bind(view)
    val listaDePreciosCompra: MutableList<String> = mutableListOf()

    init {
        binding.etPrecioCompra.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                val texto = charSequence?.toString() ?: ""
                if(binding.etPrecioCompra.text.isNotBlank() && !binding.etPrecioCompra.isFocusable){
                    listener.onTextChange(texto,this@ProveedorPrecioCompraViewHolder)
                    listaDePreciosCompra.add(texto)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                binding.etPrecioCompra.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                    if(!hasFocus){
                        if(binding.etPrecioCompra.text.length >=2){
                            val texto = s.toString()
                            // valoresDelRecyclerView.add(adapterPosition, s.toString())
                            // textChangeListener?.invoke(s.toString(),adapterPosition)
                            listener.afterTextChange(texto, this@ProveedorPrecioCompraViewHolder)
                        }
                    }
                }


            }
        })
    }

    fun bind(proveedorPrecioCompraItemResponse: ProveedorPrecioCompraItemResponse) {
        binding.tvSuppliers.text = proveedorPrecioCompraItemResponse.Nombre
        binding.cbSuppliers.setOnClickListener {
            binding.etPrecioCompra.isEnabled = binding.cbSuppliers.isChecked
        }
        // Verificar si el CheckBox está marcado o no
        //binding.etPrecioCompra.isVisible = binding.cbSuppliers.isChecked

    }


}