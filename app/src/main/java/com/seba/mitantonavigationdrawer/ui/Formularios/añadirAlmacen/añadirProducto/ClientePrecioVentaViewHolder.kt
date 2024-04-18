package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.ItemAlertBinding
import com.seba.mitantonavigationdrawer.databinding.ItemPrecioCompraBinding
import com.seba.mitantonavigationdrawer.databinding.ItemPrecioVentaBinding
import com.seba.mitantonavigationdrawer.ui.SharedViewModel


class ClientePrecioVentaViewHolder(itemView: View, private val listener: OnTextChangeListenerPrecioVenta,private val sharedViewModel: SharedViewModel): RecyclerView.ViewHolder(itemView) {
    private val binding = ItemPrecioVentaBinding.bind(itemView)
   // var buttonAgregar : Button? = null


    init {
        binding.etPrecioVenta.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}
          //  }
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {

          }

            override fun afterTextChanged(s: Editable?) {
                var onFocusChanged = false
                binding.etPrecioVenta.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                    val texto = s.toString()
                    for (i in 0..<100) {
                        if (!onFocusChanged && !hasFocus) {
                            listener.afterTextChange(texto, this@ClientePrecioVentaViewHolder,i)
                            onFocusChanged = true
                        }
                    }
                }
            }
        })

    }
    private var codeExecuted = false
    fun bind(clientePrecioVentaItemResponse: ClientePrecioVentaItemResponse,
             listaAdapter: RecyclerView.Adapter<*>) {
       // buttonAgregar = itemView.findViewById(R.id.bAgregarCliente)
        binding.tvCustomers.text = clientePrecioVentaItemResponse.Nombre
        binding.cbCustomers.setOnClickListener {
            binding.etPrecioVenta.isEnabled = binding.cbCustomers.isChecked
        }
        for (i in 0..listaAdapter.itemCount) {
            if (sharedViewModel.listaDeClientes.size > i) {
                if (sharedViewModel.listaDeClientes.isNotEmpty()) {
                    if (binding.etPrecioVenta.text.isBlank() && binding.tvCustomers.text.toString() == sharedViewModel.listaDeClientes[i]) {
                        binding.etPrecioVenta.setText(sharedViewModel.listaDePreciosVenta[i])
                    }
                }
            } else {
                break
            }
        }
       /* if(!codeExecuted) {
            for (i in 0..listaAdapter.itemCount) {
                if (sharedViewModel.listaDeClientes.size > i && sharedViewModel.listaDePreciosVenta.size > i) {
                    if (sharedViewModel.listaDePreciosVenta.isNotEmpty() && sharedViewModel.listaDeClientes.isNotEmpty() && binding.etPrecioVenta.text.isNotBlank() && binding.tvCustomers.text.toString() == sharedViewModel.listaDeClientes[i]) {
                        sharedViewModel.listaDePreciosVenta.remove(sharedViewModel.listaDePreciosVenta[i])
                        sharedViewModel.listaDeClientes.remove(sharedViewModel.listaDeClientes[i])
                    }
                }else {
                    break
                }
            }
            codeExecuted = true
        }*/
    }
    fun simulateButtonClick(){
       // buttonAgregar?.performClick()
    }
}