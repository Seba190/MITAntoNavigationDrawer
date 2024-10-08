package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos.editarProducto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.ui.SharedViewModel

class ProveedorPrecioCompraEditarAdapter(
                                   var proveedorPrecioCompraList:
                                   List<ProveedorPrecioCompraItemResponseEditar> = emptyList(),
                                   private val sharedViewModel: SharedViewModel,
                                   private val listener: OnTextChangeListenerPrecioCompraEditar,
                                   private val onCheckBoxClickListener: OnCheckBoxClickListenerPrecioCompraEditar)
                              : RecyclerView.Adapter<ProveedorPrecioCompraEditarViewHolder>() {

    private val viewHolders = mutableListOf<ProveedorPrecioCompraEditarViewHolder>()

    fun updateList(proveedorPrecioCompraList: List<ProveedorPrecioCompraItemResponseEditar>){
        this.proveedorPrecioCompraList = proveedorPrecioCompraList
        notifyDataSetChanged()
    }
    interface OnCheckBoxClickListenerPrecioCompraEditar {
        fun onCheckBoxClick(id: Int, isChecked: Boolean)
    }

    inner class TuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkBox: CheckBox = itemView.findViewById(R.id.tvSuppliers)

        init {
            // Configura un listener para el CheckBox en el ViewHolder
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                onCheckBoxClickListener.onCheckBoxClick(adapterPosition, isChecked)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProveedorPrecioCompraEditarViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ProveedorPrecioCompraEditarViewHolder(layoutInflater.inflate(R.layout.item_precio_compra,parent,false)
            ,listener,sharedViewModel)
    }

    override fun onBindViewHolder(viewholder: ProveedorPrecioCompraEditarViewHolder, position: Int) {
        val item = proveedorPrecioCompraList[position]
        viewholder.bind(item,this)
        if(!viewHolders.contains(viewholder)){
            viewHolders.add(viewholder)
        }

    }

    override fun onViewRecycled(holder: ProveedorPrecioCompraEditarViewHolder) {
        viewHolders.remove(holder)
        super.onViewRecycled(holder)
    }

    override fun getItemCount() = proveedorPrecioCompraList.size

    fun getAllEditTextContents() {
        sharedViewModel.listaDePreciosCompra.clear()
        viewHolders.forEach { holder ->
            val text = holder.editText.text.toString()
            if(text.isEmpty()){
                sharedViewModel.listaDePreciosCompra.add("")
            }else{
                sharedViewModel.listaDePreciosCompra.add(text)
            }
        }
    }
    fun getAllTextViewContents(){
        sharedViewModel.listaDeProveedores.clear()
        viewHolders.forEach { holder ->
            val text = holder.textView.text.toString()
            val edit = holder.editText.text.toString()
            if(edit.isEmpty()){
                sharedViewModel.listaDeProveedores.add("")
            }else{
                sharedViewModel.listaDeProveedores.add(text)
            }
        }
    }
}