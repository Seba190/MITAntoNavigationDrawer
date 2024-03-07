package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.databinding.ItemAlertBinding
import com.seba.mitantonavigationdrawer.ui.SharedViewModel

//Inserte sharedViewModel en AlertasAlmacenesAdapter
class AlertasAlmacenesViewHolder(view: View, private val listener: OnTextChangeListener, private val listener2: OnTextChangeListener2,private val sharedViewModel: SharedViewModel): RecyclerView.ViewHolder(view) {
    private val binding = ItemAlertBinding.bind(view)
    private val valoresDelRecyclerView: MutableList<String> = mutableListOf()
    private var textChangeListener: ((String, Int) -> Unit)? = null
    val listaDeAlertas: MutableList<String> = mutableListOf()

    fun obtenerTextos(): List<String> {
        return valoresDelRecyclerView.toList()
    }

    fun obtenerLista(): List<String> {
        return listaDeAlertas
    }

    fun bindTextChangeListener(listener: (String, Int) -> Unit) {
        textChangeListener = listener
    }

    // private val bindingAlert = FragmentAnadirProductoBinding.bind(view)
    init {
        binding.etAlert.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.etAlert.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                    val texto = s?.toString() ?: ""
                    if(texto in sharedViewModel.listaDeAlertas) {
                        if (hasFocus /*!segundaVez*/) {
                            if (sharedViewModel.listaDeBodegas.isNotEmpty() && sharedViewModel.listaDeAlertas.isNotEmpty()) {
                                listener2.beforeTextChanged(texto, this@AlertasAlmacenesViewHolder)
                            }
                            //segundaVez = true
                        }
                    }
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
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
                charSequence: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                var onFocusChanged = false
                binding.etAlert.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                    val texto = s.toString()
                    if (!onFocusChanged && !hasFocus) {
                        if (binding.etAlert.text.isNotEmpty()) {
                            // valoresDelRecyclerView.add(adapterPosition, s.toString())
                            // textChangeListener?.invoke(s.toString(),adapterPosition)
                            listener.afterTextChange(texto, this@AlertasAlmacenesViewHolder)
                            onFocusChanged = true
                        }
                        else{
                            listener.afterTextChange(texto, this@AlertasAlmacenesViewHolder)

                        }
                    }
                }
            }
        })

    }
     private var codeExecuted = false
    fun bind(
        alertasAlmacenesItemResponse: AlertasAlmacenesItemResponse,
        listaAdapter: RecyclerView.Adapter<*>
    ) {
        binding.tvWarehouse.text = alertasAlmacenesItemResponse.Nombre
        // Verificar si el CheckBox está marcado o no
        binding.cbWarehouse.setOnClickListener {
            binding.etAlert.isEnabled = binding.cbWarehouse.isChecked
        }
        for (i in 0..listaAdapter.itemCount) {
            if (sharedViewModel.listaDeBodegas.size > i) {
                if (sharedViewModel.listaDeBodegas.isNotEmpty()) {
                    if (binding.etAlert.text.isBlank() && binding.tvWarehouse.text.toString() == sharedViewModel.listaDeBodegas[i]) {
                        binding.etAlert.setText(sharedViewModel.listaDeAlertas[i])
                    }
                }
            } else {
                break
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
    }
}