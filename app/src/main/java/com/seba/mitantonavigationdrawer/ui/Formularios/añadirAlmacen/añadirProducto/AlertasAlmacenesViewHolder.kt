package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.databinding.FragmentAnadirProductoBinding
import com.seba.mitantonavigationdrawer.databinding.ItemAlertBinding


class AlertasAlmacenesViewHolder(view: View, private val listener: OnTextChangeListener): RecyclerView.ViewHolder(view) {
    private val binding = ItemAlertBinding.bind(view)

   // private val bindingAlert = FragmentAnadirProductoBinding.bind(view)
   init {
       binding.etAlert.addTextChangedListener(object : TextWatcher {
           override fun beforeTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}

           override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
               val texto = charSequence?.toString() ?: ""
               listener.onTextChange(texto,this@AlertasAlmacenesViewHolder)
           }

           override fun afterTextChanged(editable: Editable?) {

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