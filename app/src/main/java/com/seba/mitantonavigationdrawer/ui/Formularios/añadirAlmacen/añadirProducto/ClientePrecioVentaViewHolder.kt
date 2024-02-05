package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.databinding.ItemAlertBinding
import com.seba.mitantonavigationdrawer.databinding.ItemPrecioCompraBinding
import com.seba.mitantonavigationdrawer.databinding.ItemPrecioVentaBinding


class ClientePrecioVentaViewHolder(view: View, private val listener: OnTextChangeListenerPrecioVenta): RecyclerView.ViewHolder(view) {
    private val binding = ItemPrecioVentaBinding.bind(view)
    val listaDePrecioVenta: MutableList<String> = mutableListOf()

    init {
        binding.etPrecioVenta.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                val texto = charSequence?.toString() ?: ""
                if(binding.etPrecioVenta.text.isNotBlank() && !binding.etPrecioVenta.isFocusable){
                    listener.onTextChange(texto,this@ClientePrecioVentaViewHolder)
                    listaDePrecioVenta.add(texto)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                binding.etPrecioVenta.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                    if(!hasFocus){
                        if(binding.etPrecioVenta.text.length >=2){
                            val texto = s.toString()
                            // valoresDelRecyclerView.add(adapterPosition, s.toString())
                            // textChangeListener?.invoke(s.toString(),adapterPosition)
                            listener.afterTextChange(texto, this@ClientePrecioVentaViewHolder)
                        }
                    }
                }


            }
        })
    }

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