package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.R

class AlertasAlmacenesAdapter(var alertasAlmacenesList:
                              List<AlertasAlmacenesItemResponse> = emptyList(),
                              private val onCheckBoxClickListener: OnCheckBoxClickListener,
                              private val listener: OnTextChangeListener)
                              : RecyclerView.Adapter<AlertasAlmacenesViewHolder>() {

    fun updateList(alertasAlmacenesList: List<AlertasAlmacenesItemResponse>){
        this.alertasAlmacenesList = alertasAlmacenesList
        notifyDataSetChanged()
    }

    interface OnCheckBoxClickListener {
        fun onCheckBoxClick(id: Int, isChecked: Boolean)
    }

    inner class TuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.tvWarehouse)

        init {
            // Configura un listener para el CheckBox en el ViewHolder
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                onCheckBoxClickListener.onCheckBoxClick(adapterPosition, isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertasAlmacenesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_alert,parent,false)
        return AlertasAlmacenesViewHolder(view,listener)

    }

    override fun onBindViewHolder(viewholder: AlertasAlmacenesViewHolder, position: Int) {
        val item = alertasAlmacenesList[position]
        viewholder.bind(item)

    }

    override fun getItemCount() = alertasAlmacenesList.size
}