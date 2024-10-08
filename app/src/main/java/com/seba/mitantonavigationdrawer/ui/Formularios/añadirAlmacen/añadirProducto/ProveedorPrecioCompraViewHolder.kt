package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.ItemPrecioCompraBinding
import com.seba.mitantonavigationdrawer.ui.SharedViewModel


class ProveedorPrecioCompraViewHolder(view: View, private val listener: OnTextChangeListenerPrecioCompra,private val sharedViewModel: SharedViewModel): RecyclerView.ViewHolder(view) {
    private val binding = ItemPrecioCompraBinding.bind(view)
    val editText : EditText = view.findViewById(R.id.etPrecioCompra)
    val textView : TextView = view.findViewById(R.id.tvSuppliers)
    val listaDePreciosCompra: MutableList<String> = mutableListOf()

  /*  init {
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
                var onFocusChanged = false
                binding.etPrecioCompra.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                    val texto = s.toString()
                    if (!onFocusChanged && !hasFocus) {
                        listener.afterTextChange(texto, this@ProveedorPrecioCompraViewHolder)
                        onFocusChanged = true
                    }
                }
            }
        })
    }*/
    private var codeExecuted = false
    fun bind(proveedorPrecioCompraItemResponse: ProveedorPrecioCompraItemResponse,listaAdapter:RecyclerView.Adapter<*>) {
        binding.tvSuppliers.text = proveedorPrecioCompraItemResponse.Nombre
        binding.cbSuppliers.setOnClickListener {
            binding.etPrecioCompra.isEnabled = binding.cbSuppliers.isChecked
        }
        if (sharedViewModel.listaDePreciosCompraAnadir.isNotEmpty()) {
            for (i in 0..<sharedViewModel.listaDePreciosCompraAnadir.size) {
                if (binding.etPrecioCompra.text.isBlank() && binding.tvSuppliers.text.toString() == sharedViewModel.listaDeProveedoresAnadir[i]) {
                     binding.etPrecioCompra.setText(sharedViewModel.listaDePreciosCompraAnadir[i])
                    }
                }
            }

        /*if(!codeExecuted) {
            for (i in 0..listaAdapter.itemCount) {
                if (sharedViewModel.listaDeProveedores.size > i && sharedViewModel.listaDePreciosCompra.size > i) {
                    if (sharedViewModel.listaDePreciosCompra.isNotEmpty() && sharedViewModel.listaDeProveedores.isNotEmpty() && binding.etPrecioCompra.text.isNotBlank() && binding.tvSuppliers.text.toString() == sharedViewModel.listaDeProveedores[i]) {
                        sharedViewModel.listaDePreciosCompra.remove(sharedViewModel.listaDePreciosCompra[i])
                        sharedViewModel.listaDeProveedores.remove(sharedViewModel.listaDeProveedores[i])
                    }
                }else {
                    break
                }
            }
            codeExecuted = true
        }*/
        if(sharedViewModel.listaDeProveedoresAnadir.size < listaAdapter.itemCount) {
            sharedViewModel.listaDeProveedoresAnadir.add("")
            sharedViewModel.listaDePreciosCompraAnadir.add("")
        }

        Log.i("Sebastian", "${sharedViewModel.listaDeProveedoresAnadir} y ${sharedViewModel.listaDePreciosCompraAnadir}")
    }
}