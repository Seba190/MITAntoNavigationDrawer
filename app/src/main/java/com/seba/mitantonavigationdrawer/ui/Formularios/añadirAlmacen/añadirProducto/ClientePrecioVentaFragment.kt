package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentClientePrecioVentaBinding


class ClientePrecioVentaFragment : Fragment(R.layout.fragment_cliente_precio_venta) {

    private var _binding: FragmentClientePrecioVentaBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val clientePrecioVentaViewHolder =
            ViewModelProvider(this).get(ClientePrecioVentaViewModel::class.java)

        _binding = FragmentClientePrecioVentaBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}