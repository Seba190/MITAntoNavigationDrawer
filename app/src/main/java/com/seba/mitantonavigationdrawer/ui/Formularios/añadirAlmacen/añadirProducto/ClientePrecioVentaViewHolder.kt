package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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
    val editText : EditText = itemView.findViewById(R.id.etPrecioVenta)
    val textView : TextView = itemView.findViewById(R.id.tvCustomers)
   // var buttonAgregar : Button? = null


   /* init {

        binding.etPrecioVenta.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}
          //  }
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {

          }

            override fun afterTextChanged(s: Editable?) {
                var onFocusChanged = false
                binding.etPrecioVenta.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                    val texto = s.toString()
                        if (!onFocusChanged && !hasFocus) {
                            listener.afterTextChange(texto, this@ClientePrecioVentaViewHolder)
                            onFocusChanged = true
                        }
                    }
                }
        })

    }*/
    private var codeExecuted = false
    fun bind(clientePrecioVentaItemResponse: ClientePrecioVentaItemResponse,
             listaAdapter: RecyclerView.Adapter<*>) {
       // buttonAgregar = itemView.findViewById(R.id.bAgregarCliente)
        binding.tvCustomers.text = clientePrecioVentaItemResponse.Nombre
        binding.cbCustomers.setOnClickListener {
            binding.etPrecioVenta.isEnabled = binding.cbCustomers.isChecked
        }
        if (sharedViewModel.listaDePreciosVentaAnadir.isNotEmpty()) {
            for (i in 0..<sharedViewModel.listaDePreciosVentaAnadir.size) {
                if (binding.etPrecioVenta.text.isBlank() && binding.tvCustomers.text.toString() == sharedViewModel.listaDeClientesAnadir[i]) {
                    binding.etPrecioVenta.setText(sharedViewModel.listaDePreciosVentaAnadir[i])
                    }
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
        if(sharedViewModel.listaDeClientesAnadir.size < listaAdapter.itemCount){
           sharedViewModel.listaDeClientesAnadir.add("")
           sharedViewModel.listaDePreciosVentaAnadir.add("")
        }
        Log.i("Sebastian", "${sharedViewModel.listaDeClientesAnadir} y ${sharedViewModel.listaDePreciosVentaAnadir}")
    }
    fun simulateButtonClick(){
       // buttonAgregar?.performClick()
    }
}