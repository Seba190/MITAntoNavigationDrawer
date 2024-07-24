package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.ui.SharedViewModel

//Puse sharedViewModel aquí y al lado del listener
class AlertasAlmacenesAdapter(var alertasAlmacenesList:
                              List<AlertasAlmacenesItemResponse> = emptyList(),
                              private val sharedViewModel: SharedViewModel,
                              private val onCheckBoxClickListener: OnCheckBoxClickListener,
                              // private val textChangeListener: (String,Int) -> Unit,
                              private val listener: OnTextChangeListener)
                              : RecyclerView.Adapter<AlertasAlmacenesViewHolder>() {


    private val viewHolders = mutableListOf<AlertasAlmacenesViewHolder>()


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
        return AlertasAlmacenesViewHolder(view,listener,sharedViewModel)
    }
    override fun onBindViewHolder(viewholder: AlertasAlmacenesViewHolder, position: Int) {
        val item = alertasAlmacenesList[position]
        viewholder.bind(item,this)
        if(!viewHolders.contains(viewholder)){
            viewHolders.add(viewholder)
        }
    }

    override fun onViewRecycled(holder: AlertasAlmacenesViewHolder) {
        viewHolders.remove(holder)
        super.onViewRecycled(holder)
    }

    override fun getItemCount() = alertasAlmacenesList.size

    fun getAllEditTextContents() {
        sharedViewModel.listaDeAlertasAnadir.clear()
        viewHolders.forEach { holder ->
            val text = holder.editText.text.toString()
              if(text.isEmpty()){
              sharedViewModel.listaDeAlertasAnadir.add("")
             }else{
            sharedViewModel.listaDeAlertasAnadir.add(text)
             }
        }
    }
    fun getAllTextViewContents(){
        sharedViewModel.listaDeBodegasAnadir.clear()
        viewHolders.forEach { holder ->
            val text = holder.textView.text.toString()
            val edit = holder.editText.text.toString()
            if(edit.isEmpty()){
                sharedViewModel.listaDeBodegasAnadir.add("")
            }else{
                sharedViewModel.listaDeBodegasAnadir.add(text)
            }
        }
    }

    fun getAllItems(): List<AlertasAlmacenesItemResponse> {
        // Devuelve la lista completa de elementos
        return alertasAlmacenesList
    }


}