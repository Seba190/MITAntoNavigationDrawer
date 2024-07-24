package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.ItemAlertBinding
import com.seba.mitantonavigationdrawer.ui.SharedViewModel

//Inserte sharedViewModel en AlertasAlmacenesAdapter
class AlertasAlmacenesViewHolder(view: View, private val listener: OnTextChangeListener, private val sharedViewModel: SharedViewModel): RecyclerView.ViewHolder(view) {
    private val binding = ItemAlertBinding.bind(view)
    val editText : EditText = view.findViewById(R.id.etAlert)
    val textView : TextView = view.findViewById(R.id.tvWarehouse)
    val listaDeAlertas: MutableList<String> = mutableListOf()
  /*  init {

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
                         listener.afterTextChange(texto, this@AlertasAlmacenesViewHolder)
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

    }*/
     private var codeExecuted = false
    fun bind(alertasAlmacenesItemResponse: AlertasAlmacenesItemResponse, listaAdapter: RecyclerView.Adapter<*>) {
        binding.tvWarehouse.text = alertasAlmacenesItemResponse.Nombre
        // Verificar si el CheckBox está marcado o no
        binding.cbWarehouse.setOnClickListener {
            binding.etAlert.isEnabled = binding.cbWarehouse.isChecked
        }
        if (sharedViewModel.listaDeAlertasAnadir.isNotEmpty()) {
            for (i in 0..<sharedViewModel.listaDeAlertasAnadir.size) {
                if (binding.etAlert.text.isBlank() && binding.tvWarehouse.text.toString() == sharedViewModel.listaDeBodegasAnadir[i]) {
                    binding.etAlert.setText(sharedViewModel.listaDeAlertasAnadir[i])
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
        if(sharedViewModel.listaDeBodegasAnadir.size < listaAdapter.itemCount) {
            sharedViewModel.listaDeBodegasAnadir.add("")
            sharedViewModel.listaDeAlertasAnadir.add("")
        }

        Log.i("Sebastian", "${sharedViewModel.listaDeBodegasAnadir} y ${sharedViewModel.listaDeAlertasAnadir}")
    }
}