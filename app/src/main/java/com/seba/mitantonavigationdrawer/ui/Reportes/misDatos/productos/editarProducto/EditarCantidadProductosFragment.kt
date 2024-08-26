package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos.editarProducto

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentEditarCantidadProductosBinding
import com.seba.mitantonavigationdrawer.databinding.FragmentEditarCantidadTiposDeProductosBinding
import com.seba.mitantonavigationdrawer.databinding.FragmentEstadisticaBinding
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirTransferencia.AnadirTransferenciaFragment
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.tiposDeProductos.editarTiposDeProductos.EditarCantidadTiposDeProductosViewModel
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.tiposDeProductos.editarTiposDeProductos.EditarTiposDeProductosFragment
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import com.seba.mitantonavigationdrawer.ui.estadística.EstadisticaViewModel

class EditarCantidadProductosFragment : Fragment(R.layout.fragment_editar_cantidad_productos) {
    private var _binding: FragmentEditarCantidadProductosBinding? = null
    private val sharedViewModel by activityViewModels<SharedViewModel>()
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val editarCantidadProductosViewModel =
            ViewModelProvider(this).get(EditarCantidadProductosViewModel::class.java)

        _binding = FragmentEditarCantidadProductosBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa
        binding.etEditarCantidadProducto.setText(sharedViewModel.inventario.last())
        binding.bmasEditarProducto.setOnClickListener {
            val cantidadActual = binding.etEditarCantidadProducto.text.toString().toIntOrNull() ?: 0
            val cantidadNueva = cantidadActual + 1
            binding.etEditarCantidadProducto.setText(cantidadNueva.toString())
        }
        binding.bmenosEditarProducto.setOnClickListener {
            val cantidadActual = binding.etEditarCantidadProducto.text.toString().toIntOrNull() ?: 0
            val cantidadNueva = if (cantidadActual > 0) cantidadActual - 1 else 0
            binding.etEditarCantidadProducto.setText(cantidadNueva.toString())
        }

        binding.rlEditarCantidadProductos.setOnClickListener{
            binding.rlEditarCantidadProductos.isVisible = false
            val editarProductoFragment = EditarProductoFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.rlEditarCantidadProductos,editarProductoFragment)
                .commit()
        }

        binding.bCambiarProducto.setOnClickListener {
            if(binding.etEditarCantidadProducto.text.isNotBlank()) {
                binding.rlEditarCantidadProductos.isVisible = false
                val editarProductoFragment = EditarProductoFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.rlEditarCantidadProductos, editarProductoFragment)
                    .commit()
                registrarFactura(binding.etEditarCantidadProducto.text.toString().toInt())
                editarCantidad(binding.etEditarCantidadProducto.text.toString().toInt())
              //  Handler(Looper.getMainLooper()).postDelayed({

              //  }, 3000)
            }
        }
        return root
    }

    private fun editarCantidad(cantidad: Int){
        val url1 = "http://186.64.123.248/Reportes/Productos/editarCantidad.php"
        val queue1 = Volley.newRequestQueue(requireContext())
        val stringRequest = object: StringRequest(
            Request.Method.POST,
            url1,
            { response ->
                Toast.makeText(requireContext(), "Cantidad actualizada exitosamente", Toast.LENGTH_SHORT).show()
                //   TextNombre?.setText("")
                //   TextDireccion?.setText("")
                //   TextDropdown?.setText("Eliga una opción",false)
            },
            { error ->
                Toast.makeText(requireContext(), "Cantidad actualizada exitosamente", Toast.LENGTH_SHORT).show()
                //Toast.makeText(requireContext(),"$error", Toast.LENGTH_SHORT).show()
                // Toast.makeText(requireContext(),"Solo se ha podido borrar el almacen.", Toast.LENGTH_SHORT).show()
            }
        )

        {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("ID_PRODUCTO", sharedViewModel.id.last())
                parametros.put("ALMACEN", sharedViewModel.almacenes.last())
                parametros.put("CANTIDAD", cantidad.toString())
                return parametros
            }
        }
        queue1.add(stringRequest)
    }

    private fun registrarFactura(cantidad: Int){
        val url1 = "http://186.64.123.248/Reportes/Productos/transferencia.php"
        val queue1 = Volley.newRequestQueue(requireContext())
        val stringRequest = object: StringRequest(
            Request.Method.POST,
            url1,
            { response ->

                //   TextNombre?.setText("")
                //   TextDireccion?.setText("")
                //   TextDropdown?.setText("Eliga una opción",false)
            },
            { error ->
               // Toast.makeText(requireContext(),"$error", Toast.LENGTH_SHORT).show()
                Log.i("Sebastian","$error, ${sharedViewModel.id.last()} , ${sharedViewModel.almacenes.last()}, $cantidad")
                // Toast.makeText(requireContext(),"Solo se ha podido borrar el almacen.", Toast.LENGTH_SHORT).show()
            }
        )

        {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("ID_PRODUCTO", sharedViewModel.id.last())
                parametros.put("ALMACEN", sharedViewModel.almacenes.last())
                parametros.put("CANTIDAD", cantidad.toString())
                return parametros
            }
        }
        queue1.add(stringRequest)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}