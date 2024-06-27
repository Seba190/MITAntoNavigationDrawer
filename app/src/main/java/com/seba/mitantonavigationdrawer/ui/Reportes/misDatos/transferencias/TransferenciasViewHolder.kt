package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transferencias

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.databinding.ItemClienteBinding
import com.seba.mitantonavigationdrawer.databinding.ItemTransferenciaBinding
import com.seba.mitantonavigationdrawer.ui.SharedViewModel


class TransferenciasViewHolder(view: View, private val sharedViewModel: SharedViewModel): RecyclerView.ViewHolder(view) {
    private val binding = ItemTransferenciaBinding.bind(view)

    var segundaVez = false
    fun bind(transferenciasItemResponse: TransferenciasItemResponse, onItemSelected:(String) -> Unit){
        binding.tvTituloTransferencia.text = transferenciasItemResponse.Transferencia
        binding.tvDiaTransferencia.text = transferenciasItemResponse.DiaTransferencia
        binding.tvFechaTransferencia.text = transferenciasItemResponse.FechaTransferencia
        binding.tvContenidoTransferencia.text = transferenciasItemResponse.Contenido
        binding.tvComentariosTransferencia.text = transferenciasItemResponse.Comentarios
        binding.root.setOnClickListener { onItemSelected(transferenciasItemResponse.IdTransferencia) }
    }

}