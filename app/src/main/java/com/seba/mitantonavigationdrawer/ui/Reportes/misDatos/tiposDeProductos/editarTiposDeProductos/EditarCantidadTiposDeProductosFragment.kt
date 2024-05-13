package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.tiposDeProductos.editarTiposDeProductos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentEditarCantidadTiposDeProductosBinding
import com.seba.mitantonavigationdrawer.databinding.FragmentEstadisticaBinding
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirTransferencia.AnadirTransferenciaFragment
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import com.seba.mitantonavigationdrawer.ui.estadística.EstadisticaViewModel

class EditarCantidadTiposDeProductosFragment : Fragment(R.layout.fragment_editar_cantidad_tipos_de_productos) {
    private var _binding: FragmentEditarCantidadTiposDeProductosBinding? = null
    private val sharedViewModel by activityViewModels<SharedViewModel>()
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val editarCantidadTiposDeProductosViewModel =
            ViewModelProvider(this).get(EditarCantidadTiposDeProductosViewModel::class.java)

        _binding = FragmentEditarCantidadTiposDeProductosBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa
        binding.etEditarCantidad.setText(sharedViewModel.inventario.last())
        binding.bmasEditar.setOnClickListener {
            val cantidadActual = binding.etEditarCantidad.text.toString().toIntOrNull() ?: 0
            val cantidadNueva = cantidadActual + 1
            binding.etEditarCantidad.setText(cantidadNueva.toString())
        }
        binding.bmenosEditar.setOnClickListener {
            val cantidadActual = binding.etEditarCantidad.text.toString().toIntOrNull() ?: 0
            val cantidadNueva = if (cantidadActual > 0) cantidadActual - 1 else 0
            binding.etEditarCantidad.setText(cantidadNueva.toString())
        }

        binding.rlElegirCantidad.setOnClickListener{
            binding.rlElegirCantidad.isVisible = false
            val editarTiposDeProductosFragment = EditarTiposDeProductosFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.rlElegirCantidad,editarTiposDeProductosFragment)
                .commit()
        }

        binding.bCambiar.setOnClickListener {
            binding.rlElegirCantidad.isVisible = false
            val editarTiposDeProductosFragment = EditarTiposDeProductosFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.rlElegirCantidad,editarTiposDeProductosFragment)
                .commit()
        }
        return root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}