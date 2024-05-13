package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos.editarProducto

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.seba.mitantonavigationdrawer.databinding.ItemPrecioCompraBinding
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import org.json.JSONObject


class ProveedorPrecioCompraEditarViewHolder(view: View, private val listener: OnTextChangeListenerPrecioCompraEditar, private val sharedViewModel: SharedViewModel): RecyclerView.ViewHolder(view) {
    private val binding = ItemPrecioCompraBinding.bind(view)
    val listaDePreciosCompra: MutableList<String> = mutableListOf()

    init {
        binding.etPrecioCompra.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                val texto = charSequence?.toString() ?: ""
                if(binding.etPrecioCompra.text.isNotBlank() && !binding.etPrecioCompra.isFocusable){
                    listener.onTextChange(texto,this@ProveedorPrecioCompraEditarViewHolder)
                    listaDePreciosCompra.add(texto)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                var onFocusChanged = false
                binding.etPrecioCompra.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                    val texto = s.toString()
                    if (!onFocusChanged && !hasFocus) {
                        listener.afterTextChange(texto, this@ProveedorPrecioCompraEditarViewHolder)
                        onFocusChanged = true
                    }
                }
            }
        })
    }
    private var codeExecuted = false
    var segundaVez = false
    fun bind(proveedorPrecioCompraItemResponse: ProveedorPrecioCompraItemResponseEditar,listaAdapter:RecyclerView.Adapter<*>) {
        binding.tvSuppliers.text = proveedorPrecioCompraItemResponse.Nombre
        binding.cbSuppliers.setOnClickListener {
            binding.etPrecioCompra.isEnabled = binding.cbSuppliers.isChecked
        }
            if (sharedViewModel.listaDeProveedores.isNotEmpty()) {
                for (i in 0..<sharedViewModel.listaDePreciosCompra.size) {
                if (binding.etPrecioCompra.text.isBlank() && binding.tvSuppliers.text.toString() == sharedViewModel.listaDeProveedores[i]) {
                    binding.etPrecioCompra.setText(sharedViewModel.listaDePreciosCompra[i])
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
        if(!segundaVez) {
            obtenerPreciosCompra()
            segundaVez = true
        }

        Log.i("Sebastian", "${sharedViewModel.listaDePreciosCompra} y ${sharedViewModel.listaDeProveedores}")
    }
    private fun obtenerPrecioCompra(proveedor: String, position: Int){
        val queue1 = Volley.newRequestQueue(itemView.context)
        val url1 = "http://186.64.123.248/Reportes/Productos/obtenerPreciosCompra.php"
        val stringRequest = object : StringRequest(
            Request.Method.POST,
            url1,
            { response ->
                val precioCompra = JSONObject(response).getString("PRECIO_COMPRA")
                binding.etPrecioCompra.setText(precioCompra)
                sharedViewModel.listaDePreciosCompra[position] = precioCompra
               /* if(sharedViewModel.listaDePreciosCompra.lastIndexOf("") != -1){
                    sharedViewModel.listaDePreciosCompra.removeAt(sharedViewModel.listaDePreciosCompra.lastIndexOf(""))
                }*/
                //Toast.makeText(itemView.context,"${sharedViewModel.listaDePreciosCompra} y ${sharedViewModel.listaDeProveedores}",
                //    Toast.LENGTH_LONG).show()
            },
            { error ->
                //Toast.makeText(itemView.context, "$error", Toast.LENGTH_LONG).show()
                //Log.i("Sebastian","$error")
                // Toast.makeText(itemView.context, "El error es $error", Toast.LENGTH_LONG).show()
                // Toast.makeText(requireContext(),"Solo se ha podido borrar el almacen.", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("PROVEEDOR",proveedor)
                parametros.put("ID_PRODUCTO", sharedViewModel.id.last())
                return parametros
            }
        }
        queue1.add(stringRequest)
    }

    var segundaVez2 = false
    private fun obtenerPreciosCompra() {
        val queue1 = Volley.newRequestQueue(itemView.context)
        val url1 ="http://186.64.123.248/Reportes/Productos/proveedorPreciosCompra.php"
        val jsonObjectRequest1 = JsonObjectRequest(
            Request.Method.GET,url1, null,
            { response ->
                val jsonArray = response.getJSONArray("Lista")
                for (i in 0 until jsonArray.length()) {
                    sharedViewModel.proveedoresList.add(jsonArray.getString(i))
                }


                    sharedViewModel.listaDeProveedores = sharedViewModel.proveedoresList.toSet().toMutableList()
                if(sharedViewModel.listaDePreciosCompra.size < sharedViewModel.listaDeProveedores.size){
                    sharedViewModel.listaDePreciosCompra.add("")
                }
                //if(sharedViewModel.listaDePreciosCompra.size > sharedViewModel.listaDeProveedores.size){
                 //   sharedViewModel.listaDePreciosCompra.dropLast(sharedViewModel.listaDePreciosCompra.size - sharedViewModel.listaDeProveedores.size)
               // }

                for (i in 0..<sharedViewModel.proveedoresList.toSet().size) {
                    if (binding.etPrecioCompra.text.isBlank() && sharedViewModel.listaDeProveedores[i] == binding.tvSuppliers.text.toString() ) {
                        obtenerPrecioCompra(sharedViewModel.listaDeProveedores[i],i)
                    }
                }

            }, { error ->
                Toast.makeText(itemView.context, "$error", Toast.LENGTH_LONG).show()
            }
        )
        queue1.add(jsonObjectRequest1)
    }

}