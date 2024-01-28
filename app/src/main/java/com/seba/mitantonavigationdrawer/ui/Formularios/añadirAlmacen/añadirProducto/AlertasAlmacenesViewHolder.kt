package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.MainActivity
import com.seba.mitantonavigationdrawer.databinding.FragmentAnadirProductoBinding
import com.seba.mitantonavigationdrawer.databinding.ItemAlertBinding


class AlertasAlmacenesViewHolder(view: View, private val listener: OnTextChangeListener): RecyclerView.ViewHolder(view) {
    private val binding = ItemAlertBinding.bind(view)
    private val valoresDelRecyclerView: MutableList<String> = mutableListOf()
    private var textChangeListener: ((String, Int) -> Unit)? = null
    val listaDeAlertas: MutableList<String> = mutableListOf()
    fun obtenerTextos(): List<String> {
        return valoresDelRecyclerView.toList()
    }

    fun obtenerLista(): List<String>{
        return listaDeAlertas
    }
    fun bindTextChangeListener(listener: (String,Int) -> Unit) {
        textChangeListener = listener
    }

   // private val bindingAlert = FragmentAnadirProductoBinding.bind(view)
   init {
       binding.etAlert.addTextChangedListener(object : TextWatcher {
           override fun beforeTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}

           override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
               val texto = charSequence?.toString() ?: ""
               if(binding.etAlert.text.isNotBlank() && !binding.etAlert.isFocusable){
                   listener.onTextChange(texto,this@AlertasAlmacenesViewHolder)
                   listaDeAlertas.add(texto)
               }
           }

           override fun afterTextChanged(s: Editable?) {
               binding.etAlert.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                   if(!hasFocus){
                       if(binding.etAlert.text.length >=2){
                           val texto = s.toString()
                           // valoresDelRecyclerView.add(adapterPosition, s.toString())
                           // textChangeListener?.invoke(s.toString(),adapterPosition)
                           listener.afterTextChange(texto, this@AlertasAlmacenesViewHolder)
                       }
                   }
               }
              // if (binding.etAlert.text.isNotBlank() && !binding.etAlert.isFocusable) {

           }
       })
   }
    fun bind(alertasAlmacenesItemResponse: AlertasAlmacenesItemResponse) {
        binding.tvWarehouse.text = alertasAlmacenesItemResponse.Nombre
        // Verificar si el CheckBox está marcado o no
        binding.cbWarehouse.setOnClickListener {
            binding.etAlert.isEnabled = binding.cbWarehouse.isChecked
        }
       // binding.bAlertaAlmacen.setOnClickListener {
           // bindingAlert.etCodigoBarraProducto.setText(alertasAlmacenesItemResponse.Nombre)
           // bindingAlert.etCodigoBarraProducto.setText(binding.tvWarehouse.text)
       // }
        //binding.etAlert.isVisible = binding.cbWarehouse.isChecked
    }





}