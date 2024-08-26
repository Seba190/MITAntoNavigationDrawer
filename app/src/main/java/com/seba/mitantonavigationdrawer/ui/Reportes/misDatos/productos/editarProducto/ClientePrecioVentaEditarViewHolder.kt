package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos.editarProducto

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
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.ItemAlertBinding
import com.seba.mitantonavigationdrawer.databinding.ItemPrecioCompraBinding
import com.seba.mitantonavigationdrawer.databinding.ItemPrecioVentaBinding
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import org.json.JSONObject


class ClientePrecioVentaEditarViewHolder(itemView: View, private val listener: OnTextChangeListenerPrecioVentaEditar, private val sharedViewModel: SharedViewModel): RecyclerView.ViewHolder(itemView) {
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
                        listener.afterTextChange(texto, this@ClientePrecioVentaEditarViewHolder)
                        onFocusChanged = true
                    }
                }
            }
        })

    }*/
    var segundaVez = false
    private var codeExecuted = false
    fun bind(clientePrecioVentaItemResponse: ClientePrecioVentaItemResponseEditar,
             listaAdapter: RecyclerView.Adapter<*>) {
        // buttonAgregar = itemView.findViewById(R.id.bAgregarCliente)
        binding.tvCustomers.text = clientePrecioVentaItemResponse.Nombre
        binding.cbCustomers.setOnClickListener {
            binding.etPrecioVenta.isEnabled = binding.cbCustomers.isChecked
        }

       /* if (sharedViewModel.listaDePreciosVenta.isNotEmpty()) {
            for (i in 0..<sharedViewModel.listaDePreciosVenta.size) {
                if (binding.etPrecioVenta.text.isBlank() && binding.tvCustomers.text.toString() == sharedViewModel.listaDeClientes[i]) {
                    binding.etPrecioVenta.setText(sharedViewModel.listaDePreciosVenta[i])
                }
            }
        }*/

        if (sharedViewModel.listaDePreciosVenta.isNotEmpty()) {
            for (i in 0..<sharedViewModel.numeroPreciosVenta.size) {
                if (binding.etPrecioVenta.text.isBlank()) {
                    if(binding.tvCustomers.text.toString() == sharedViewModel.listaDeClientes[i]){
                        binding.etPrecioVenta.setText(sharedViewModel.listaDePreciosVenta[i])
                    }else{
                        binding.etPrecioVenta.setText("")
                    }

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
        if(!segundaVez && sharedViewModel.listaDePreciosVenta.isEmpty()) {
            obtenerClientes()
            segundaVez = true
        }
        Log.i("Sebastian", "${sharedViewModel.listaDeClientes} y ${sharedViewModel.listaDePreciosVenta}")
    }
    private fun obtenerClientes() {
        val queue1 = Volley.newRequestQueue(itemView.context)
        val url1 ="http://186.64.123.248/Reportes/Productos/clientePreciosVenta.php"
        val jsonObjectRequest1 = JsonObjectRequest(
            Request.Method.GET,url1, null,
            { response ->
                val jsonArray = response.getJSONArray("Lista")

                for (i in 0 until jsonArray.length()) {
                    sharedViewModel.clientesList.add(jsonArray.getString(i))
                }
                sharedViewModel.listaDeClientes = sharedViewModel.clientesList.toSet().toMutableList()

                if(sharedViewModel.listaDePreciosVenta.size < sharedViewModel.listaDeClientes.size) {
                    sharedViewModel.listaDePreciosVenta.add("")
                }
                for (i in 0..<sharedViewModel.clientesList.toSet().size) {
                    if (binding.etPrecioVenta.text.isBlank() && binding.tvCustomers.text.toString() == sharedViewModel.listaDeClientes[i]) {
                        obtenerCliente(sharedViewModel.listaDeClientes[i],i)

                    }
                }

            }, { error ->
                Toast.makeText(itemView.context, "$error", Toast.LENGTH_SHORT).show()
            }
        )
        queue1.add(jsonObjectRequest1)
    }
    private fun obtenerCliente(cliente: String, position: Int){
        val queue1 = Volley.newRequestQueue(itemView.context)
        val url1 = "http://186.64.123.248/Reportes/Productos/obtenerPreciosVenta.php"
        val stringRequest = object : StringRequest(
            Request.Method.POST,
            url1,
            { response ->
                val precioVenta = JSONObject(response).getString("PRECIO_VENTA")
                binding.etPrecioVenta.setText(precioVenta)
                sharedViewModel.listaDePreciosVenta[position]= precioVenta
               // Toast.makeText(itemView.context,"${sharedViewModel.listaDePreciosVenta} y ${sharedViewModel.listaDeClientes}",Toast.LENGTH_SHORT).show()

            },
            { error ->
                //Toast.makeText(itemView.context, "$error", Toast.LENGTH_SHORT).show()
                //Log.i("Sebastian","$error")
                // Toast.makeText(itemView.context, "El error es $error", Toast.LENGTH_SHORT).show()
                // Toast.makeText(requireContext(),"Solo se ha podido borrar el almacen.", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("CLIENTE",cliente)
                parametros.put("ID_PRODUCTO", sharedViewModel.id.last())
                return parametros
            }
        }
        queue1.add(stringRequest)
    }

}