package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.ui.SharedViewModel

class ClientePrecioVentaAdapter(var clientePrecioVentaList:
                                List<ClientePrecioVentaItemResponse> = emptyList(),
                                private val sharedViewModel: SharedViewModel,
                                private val onCheckBoxClickListener: OnCheckBoxClickListenerPrecioVenta,
                                private val listener: OnTextChangeListenerPrecioVenta)
                              : RecyclerView.Adapter<ClientePrecioVentaViewHolder>() {

    private val viewHolders = mutableListOf<ClientePrecioVentaViewHolder>()

    fun updateList(clientePrecioVentaList: List<ClientePrecioVentaItemResponse>) {
        this.clientePrecioVentaList = clientePrecioVentaList
        notifyDataSetChanged()
    }

    interface OnCheckBoxClickListenerPrecioVenta {
        fun onCheckBoxClick(id: Int, isChecked: Boolean)
    }

    inner class TuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.tvCustomers)

        init {
            // Configura un listener para el CheckBox en el ViewHolder
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                onCheckBoxClickListener.onCheckBoxClick(adapterPosition, isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientePrecioVentaViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ClientePrecioVentaViewHolder(layoutInflater.inflate(R.layout.item_precio_venta, parent, false)
        ,listener,sharedViewModel)
    }

    override fun getItemCount() = clientePrecioVentaList.size

    override fun onBindViewHolder(viewholder: ClientePrecioVentaViewHolder, position: Int) {
        val item = clientePrecioVentaList[position]
        viewholder.bind(item,this)
        if(!viewHolders.contains(viewholder)){
            viewHolders.add(viewholder)
        }
    }

    override fun onViewRecycled(holder: ClientePrecioVentaViewHolder) {
        viewHolders.remove(holder)
        super.onViewRecycled(holder)
    }

    fun getAllEditTextContents() {
        sharedViewModel.listaDePreciosVentaAnadir.clear()
        viewHolders.forEach { holder ->
            val text = holder.editText.text.toString()
            if(text.isEmpty()){
                sharedViewModel.listaDePreciosVentaAnadir.add("")
            }else{
                sharedViewModel.listaDePreciosVentaAnadir.add(text)
            }
        }
    }
    fun getAllTextViewContents(){
        sharedViewModel.listaDeClientesAnadir.clear()
        viewHolders.forEach { holder ->
            val text = holder.textView.text.toString()
            val edit = holder.editText.text.toString()
            if(edit.isEmpty()){
                sharedViewModel.listaDeClientesAnadir.add("")
            }else{
                sharedViewModel.listaDeClientesAnadir.add(text)
            }
        }
    }

}
