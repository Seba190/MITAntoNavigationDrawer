package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos.editarProducto

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.ItemAlertBinding
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.MisDatosFragmentArgs
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import kotlinx.coroutines.currentCoroutineContext
import org.json.JSONObject
import kotlin.coroutines.coroutineContext
import kotlin.math.abs

//Inserte sharedViewModel en AlertasAlmacenesAdapter
class AlertasAlmacenesEditarViewHolder(view: View, private val listener: OnTextChangeListenerEditar, private val sharedViewModel: SharedViewModel, private val adapter: AlertasAlmacenesEditarAdapter): RecyclerView.ViewHolder(view) {
    private val binding = ItemAlertBinding.bind(view)
    var segundaVez = false

    init {
        binding.etAlert.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                // val texto = charSequence?.toString() ?: ""
                // binding.etAlert.setText(texto)
            }

            override fun onTextChanged(
                charSequence: CharSequence?, start: Int, before: Int, count: Int
            ) {

            }

            override fun afterTextChanged(s: Editable?) {
                var onFocusChanged = false
                binding.etAlert.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                    val texto = s.toString()
                    // if(adapterPosition != RecyclerView.NO_POSITION){
                    //    listener.onEditTextValueChanged(adapterPosition,binding.etAlert.text.toString(), this@AlertasAlmacenesViewHolder)
                    // }

                    if (!onFocusChanged && !hasFocus) {
                            listener.afterTextChange(texto, this@AlertasAlmacenesEditarViewHolder)
                            onFocusChanged = true
                    }
                    // if (binding.etAlert.text.isNotEmpty()) {
                    // valoresDelRecyclerView.add(adapterPosition, s.toString())
                    // textChangeListener?.invoke(s.toString(),adapterPosition)
                    // onFocusChanged = true
                    //}
                }

            }
        })


    }
    private var codeExecuted = false
    fun bind(
        alertasAlmacenesItemResponse: AlertasAlmacenesItemResponseEditar,
        listaAdapter: RecyclerView.Adapter<*>
    ) {
        binding.tvWarehouse.text = alertasAlmacenesItemResponse.Nombre
        // Verificar si el CheckBox está marcado o no
        binding.cbWarehouse.setOnClickListener {
            binding.etAlert.isEnabled = binding.cbWarehouse.isChecked
        }
        for (i in 0..listaAdapter.itemCount) {
            if (sharedViewModel.listaDeAlertas.isNotEmpty()) {
                if (sharedViewModel.listaDeAlertas.size > i) {
                    if (binding.etAlert.text.isBlank() && binding.tvWarehouse.text.toString() == sharedViewModel.listaDeBodegas[i]) {
                        binding.etAlert.setText(sharedViewModel.listaDeAlertas[i])
                    }
                } else {
                    break
                }
            }
        }
        /*if(!codeExecuted) {
            for (i in 0..listaAdapter.itemCount) {
                if (sharedViewModel.listaDeBodegas.size > i && sharedViewModel.listaDeAlertas.size > i) {
                    if (sharedViewModel.listaDeAlertas.isNotEmpty() && sharedViewModel.listaDeBodegas.isNotEmpty() && binding.etAlert.text.isNotBlank() && binding.tvWarehouse.text.toString() == sharedViewModel.listaDeBodegas[i]) {
                        sharedViewModel.listaDeAlertas.remove(sharedViewModel.listaDeAlertas[i])
                        sharedViewModel.listaDeBodegas.remove(sharedViewModel.listaDeBodegas[i])
                    }
                }else {
                    break
                }
            }
            codeExecuted = true
        }*/
        if(!segundaVez) {
            obtenerAlertas()
            segundaVez = true
        }

    }

    private fun obtenerAlertas() {
        val queue1 = Volley.newRequestQueue(itemView.context)
        val url1 ="http://186.64.123.248/Reportes/Productos/almacenAlertas.php"
        val jsonObjectRequest1 = JsonObjectRequest(
            Request.Method.GET,url1, null,
            { response ->
                val jsonArray = response.getJSONArray("Lista")
                for (i in 0 until jsonArray.length()) {
                    sharedViewModel.almacenesList.add(jsonArray.getString(i))
                }

                sharedViewModel.listaDeAlertas.add("")
                            // sharedViewModel.listaDeBodegas.add(almacenesList[i])
                    /*else if (binding.etAlert.text.isBlank() && binding.tvWarehouse.text.toString() != almacenesList[i]){
                           sharedViewModel.listaDeAlertas.add("")
                    }*/
                sharedViewModel.listaDeBodegas = sharedViewModel.almacenesList.toSet().toMutableList()
                for (i in 0 ..<sharedViewModel.almacenesList.toSet().size){
                    if (sharedViewModel.listaDeBodegas[i] == binding.tvWarehouse.text.toString() && binding.etAlert.text.isBlank()) {
                        obtenerAlerta(sharedViewModel.listaDeBodegas[i],i)
                    }
                }

                /*sharedViewModel.bodegas = sharedViewModel.listaDeBodegas.sortedBy { sharedViewModel.almacenesList.indexOf(it) }
                sharedViewModel.pares = sharedViewModel.bodegas.zip(sharedViewModel.listaDeAlertas).sortedBy { it.first }

                sharedViewModel.sortedAlertas = sharedViewModel.pares.map{it.second}
                sharedViewModel.sortedBodegas = sharedViewModel.pares.map{it.first}

                val there = sharedViewModel.listaDeBodegas.map{v ->sharedViewModel.almacenesList.indexOf(v)}.sorted().map{v ->sharedViewModel.almacenesList[v]}*/
                /*for (i in 0 ..<almacenesList.size){
                    if((sharedViewModel.listaDeBodegas[i] != sharedViewModel.listaDeAlertas[i]) && binding.tvWarehouse.text.toString() != almacenesList[i]) {
                        sharedViewModel.listaDeBodegas.removeAt(i)
                    }
                }*/
            }, { error ->
                 Toast.makeText(itemView.context, "$error", Toast.LENGTH_LONG).show()
            }
        )
        queue1.add(jsonObjectRequest1)
    }
    private fun obtenerAlerta(almacen: String,position:Int){
        val queue1 = Volley.newRequestQueue(itemView.context)
        val url1 = "http://186.64.123.248/Reportes/Productos/obtenerAlertas.php"
        val stringRequest = object : StringRequest(
            Request.Method.POST,
            url1,
            { response ->
                val alerta = JSONObject(response).getString("ALERTA")
                binding.etAlert.setText(alerta)
                sharedViewModel.listaDeAlertas[position] = alerta
               /* if(sharedViewModel.listaDeAlertas.lastIndexOf("") != -1){
                    sharedViewModel.listaDeAlertas.removeAt(sharedViewModel.listaDeAlertas.lastIndexOf(""))
                }*/
                Toast.makeText(itemView.context,"${sharedViewModel.listaDeAlertas} y ${sharedViewModel.listaDeBodegas}",Toast.LENGTH_LONG).show()
                Log.i("Sebastian", "${sharedViewModel.listaDeBodegas} y ${sharedViewModel.listaDeAlertas}")
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
                parametros.put("ALMACEN",almacen)
                parametros.put("ID_PRODUCTO", sharedViewModel.id.last())
                return parametros
            }
        }
        queue1.add(stringRequest)
    }
}