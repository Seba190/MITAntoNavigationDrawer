package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirInventario

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.databinding.ItemProductoBinding
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.AlmacenesItemResponse

class AnadirInventarioViewHolder(view: View): RecyclerView.ViewHolder(view) {
    private val binding = ItemProductoBinding.bind(view)

    fun bind(productoItemResponse: ProductosItemResponse, onItemSelected:(String) -> Unit) {
        binding.tvProductoName.text = productoItemResponse.Nombre
        binding.tvInventarioNameProducto.text = productoItemResponse.Inventario
       // binding.tvDescripcionNameProducto.text = productoItemResponse.Descripcion
        binding.root.setOnClickListener {
            // onItemSelected(productoItemResponse.Id)
            onItemSelected(adapterPosition.toString())
         }
        }
    }