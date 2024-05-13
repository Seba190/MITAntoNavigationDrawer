package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirTransferencia

import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.ItemElegirProductoBinding
import com.seba.mitantonavigationdrawer.ui.SharedViewModel

class AnadirTransferenciaViewHolder(view: View, private val sharedViewModel: SharedViewModel): RecyclerView.ViewHolder(view) {
    private val binding = ItemElegirProductoBinding.bind(view)
   // val adapter = (view.context as RecyclerView).adapter as AnadirTransferenciaAdapter
   // private val editTextCanidad : EditText = view.findViewById(R.id.etUnidades)
    fun bind(cantidad:String,producto: String, position : Int, onClickDelete: (Int) -> Unit){
       binding.etUnidades.text = cantidad
       binding.etProducto.text = producto.substringBefore('(',producto)
        binding.cvTrash.setOnClickListener {
            onClickDelete(adapterPosition)  }
      /*  var onFocusChanged1 = false
        var onFocusChanged2 = false
        var onFocusChanged3 = false
        var onFocusChanged4 = false
        binding.etUnidades.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                if (binding.etUnidades.text.isNotBlank() && !hasFocus && !onFocusChanged1) {
                    if(adapterPosition > 0) {
                        //Si no esta con el focus, es la primera vez, no es la primera posición
                        // y la posición una más arriba de la cantidad y el producto es distinto de vacio
                        if (sharedViewModel.listaDeCantidades[adapterPosition - 1] != "" || sharedViewModel.listaDeProductos[adapterPosition - 1] == "") {
                            sharedViewModel.listaDeCantidades[adapterPosition] = binding.etUnidades.text.toString()
                            //binding.etUnidades.setText(sharedViewModel.listaDeCantidadesAntigua[adapterPosition])
                           // sharedViewModel.listaDeCantidades[adapterPosition] = binding.etUnidades.text.toString()
                            //sharedViewModel.listaDeCantidades.add(adapterPosition, binding.etUnidades.text.toString())
                            Log.i("Sebastian", "Se agrega ${binding.etUnidades.text}")
                            onFocusChanged1 = true
                        }
                    }
                        if(adapterPosition != RecyclerView.NO_POSITION) {
                            if (adapterPosition < sharedViewModel.listaDeCantidades.size - 1) {
                                if (sharedViewModel.listaDeCantidades[adapterPosition + 1] != "" || sharedViewModel.listaDeProductos[adapterPosition + 1] == "") {
                                    sharedViewModel.listaDeCantidades[adapterPosition] = binding.etUnidades.text.toString()
                                    //binding.etUnidades.setText(sharedViewModel.listaDeCantidadesAntigua[adapterPosition])
                                    //sharedViewModel.listaDeCantidades[adapterPosition] = binding.etUnidades.text.toString()
                                    // sharedViewModel.listaDeCantidades.add(adapterPosition, binding.etUnidades.text.toString())
                                    Log.i("Sebastian", "Se agrega ${binding.etUnidades.text}")
                                    onFocusChanged1 = true
                                }
                            }
                        }

                }

                if (binding.etUnidades.text.isBlank() && sharedViewModel.listaDeCantidades.isNotEmpty() && !hasFocus && !onFocusChanged2) {
                    sharedViewModel.listaDeCantidades[adapterPosition] = ""
                   // binding.etUnidades.setText(sharedViewModel.listaDeCantidadesAntigua[adapterPosition])
                    //sharedViewModel.listaDeCantidades[adapterPosition] = ""
                    //sharedViewModel.listaDeCantidades.removeAt(adapterPosition)
                    Log.i("Sebastian", "Se ha eliminado la cantidad")
                    onFocusChanged2 = true
                }

        }
        binding.etProducto.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (binding.etProducto.text.isNotBlank() && !hasFocus && !onFocusChanged3) {
                if (adapterPosition > 0) {
                    if (sharedViewModel.listaDeCantidades[adapterPosition - 1] != "" || sharedViewModel.listaDeProductos[adapterPosition - 1] == "") {
                        sharedViewModel.listaDeProductos[adapterPosition] = "${binding.etProducto.text} ( 0 unid. )"
                        //binding.etProducto.setText(sharedViewModel.listaDeProductosAntigua[adapterPosition])
                        //sharedViewModel.listaDeProductos[adapterPosition] = "${binding.etProducto.text} ( 0 unid. ) "
                        //sharedViewModel.listaDeProductos.add(adapterPosition, binding.etProducto.text.toString())
                        Log.i("Sebastian", "Se agrega ${binding.etProducto.text}")
                        onFocusChanged3 = true
                    }
                }
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    if (adapterPosition < sharedViewModel.listaDeProductos.size - 1) {
                        if (sharedViewModel.listaDeCantidades[adapterPosition + 1] != "" || sharedViewModel.listaDeProductos[adapterPosition + 1] == "") {
                            sharedViewModel.listaDeProductos[adapterPosition] = "${binding.etProducto.text} ( 0 unid. )"
                           // binding.etProducto.setText(sharedViewModel.listaDeProductosAntigua[adapterPosition])
                            //sharedViewModel.listaDeProductos[adapterPosition] = "${binding.etProducto.text} ( 0 unid. )"
                            // sharedViewModel.listaDeProductos.add(adapterPosition, binding.etProducto.text.toString())
                            Log.i("Sebastian", "Se agrega ${binding.etProducto.text}")
                            onFocusChanged3 = true
                        }
                    }
                }
            }

            if(binding.etProducto.text.isBlank() && sharedViewModel.listaDeProductos.isNotEmpty() && !hasFocus && !onFocusChanged4) {
                sharedViewModel.listaDeProductos[adapterPosition] = ""
                //binding.etProducto.setText(sharedViewModel.listaDeProductosAntigua[adapterPosition])
                //sharedViewModel.listaDeProductos[adapterPosition] = ""
               // sharedViewModel.listaDeProductos.removeAt(adapterPosition)
                Log.i("Sebastian", "Se ha eliminado el producto")
                onFocusChanged4 = true
            }
        }*/

    }
}