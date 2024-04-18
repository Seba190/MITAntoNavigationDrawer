package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos.editarProducto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.ui.SharedViewModel

//Puse sharedViewModel aquí y al lado del listener
class AlertasAlmacenesEditarAdapter(
    var alertasAlmacenesList:
                              List<AlertasAlmacenesItemResponseEditar> = emptyList(),
    private val sharedViewModel: SharedViewModel,
    private val onCheckBoxClickListenerEditar: OnCheckBoxClickListenerEditar,
                              // private val textChangeListener: (String,Int) -> Unit,
    private val listener: OnTextChangeListenerEditar)
                              : RecyclerView.Adapter<AlertasAlmacenesEditarViewHolder>() {




    fun updateList(alertasAlmacenesList: List<AlertasAlmacenesItemResponseEditar>){
        this.alertasAlmacenesList = alertasAlmacenesList
        notifyDataSetChanged()
    }


    interface OnCheckBoxClickListenerEditar {
        fun onCheckBoxClick(id: Int, isChecked: Boolean)
    }

    inner class TuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.tvWarehouse)

        init {
            // Configura un listener para el CheckBox en el ViewHolder
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                onCheckBoxClickListenerEditar.onCheckBoxClick(adapterPosition, isChecked)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertasAlmacenesEditarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_alert,parent,false)
        return AlertasAlmacenesEditarViewHolder(view,listener,sharedViewModel,this)
    }
    override fun onBindViewHolder(viewholder: AlertasAlmacenesEditarViewHolder, position: Int) {
        val item = alertasAlmacenesList[position]
        viewholder.bind(item,this)
        }

    override fun getItemCount() = alertasAlmacenesList.size

    fun getAllItems(): List<AlertasAlmacenesItemResponseEditar> {
        // Devuelve la lista completa de elementos
        return alertasAlmacenesList
    }
}