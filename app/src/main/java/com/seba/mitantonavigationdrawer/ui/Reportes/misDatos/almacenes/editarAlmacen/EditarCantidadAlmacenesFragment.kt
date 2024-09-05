package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.editarAlmacen

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
import com.seba.mitantonavigationdrawer.databinding.FragmentEditarCantidadAlmacenesBinding
import com.seba.mitantonavigationdrawer.databinding.FragmentEditarCantidadProductosBinding
import com.seba.mitantonavigationdrawer.databinding.FragmentEditarCantidadTiposDeProductosBinding
import com.seba.mitantonavigationdrawer.databinding.FragmentEstadisticaBinding
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirTransferencia.AnadirTransferenciaFragment
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos.editarProducto.EditarCantidadProductosViewModel
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos.editarProducto.EditarProductoFragment
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.tiposDeProductos.editarTiposDeProductos.EditarCantidadTiposDeProductosViewModel
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.tiposDeProductos.editarTiposDeProductos.EditarTiposDeProductosFragment
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import com.seba.mitantonavigationdrawer.ui.estadística.EstadisticaViewModel

class EditarCantidadAlmacenesFragment : Fragment(R.layout.fragment_editar_cantidad_almacenes) {
    private var _binding: FragmentEditarCantidadAlmacenesBinding? = null
    private val sharedViewModel by activityViewModels<SharedViewModel>()
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val editarCantidadAlmacenesViewModel =
            ViewModelProvider(this).get(EditarCantidadAlmacenesViewModel::class.java)

        _binding = FragmentEditarCantidadAlmacenesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa
        binding.etEditarCantidadAlmacen.setText(sharedViewModel.inventario.last())
        binding.bmasEditarAlmacen.setOnClickListener {
            val cantidadActual = binding.etEditarCantidadAlmacen.text.toString().toIntOrNull() ?: 0
            val cantidadNueva = cantidadActual + 1
            binding.etEditarCantidadAlmacen.setText(cantidadNueva.toString())
        }
        binding.bmenosEditarAlmacen.setOnClickListener {
            val cantidadActual = binding.etEditarCantidadAlmacen.text.toString().toIntOrNull() ?: 0
            val cantidadNueva = if (cantidadActual > 0) cantidadActual - 1 else 0
            binding.etEditarCantidadAlmacen.setText(cantidadNueva.toString())
        }

        binding.rlEditarCantidadAlmacenes.setOnClickListener{
            binding.rlEditarCantidadAlmacenes.isVisible = false
            val editarAlmacenFragment = EditarAlmacenFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.rlEditarCantidadAlmacenes,editarAlmacenFragment)
                .commit()
        }

        binding.bCambiarAlmacen.setOnClickListener {
            if(binding.etEditarCantidadAlmacen.text.isNotBlank()) {
                binding.rlEditarCantidadAlmacenes.isVisible = false
                val editarAlmacenFragment = EditarAlmacenFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.rlEditarCantidadAlmacenes, editarAlmacenFragment)
                    .commit()
                registrarFactura(binding.etEditarCantidadAlmacen.text.toString().toInt().toString())
                editarCantidad(binding.etEditarCantidadAlmacen.text.toString().toInt().toString())
                //  Handler(Looper.getMainLooper()).postDelayed({
              //  }, 100)

            }
        }
        return root
    }

    private fun editarCantidad(cantidad: String){
        val url1 = "http://186.64.123.248/Reportes/Almacenes/editarCantidad.php"
        val queue1 = Volley.newRequestQueue(requireContext())
        val stringRequest = object: StringRequest(
            Request.Method.POST,
            url1,
            { response ->
                Toast.makeText(requireContext(), "Cantidad actualizada exitosamente", Toast.LENGTH_SHORT).show()
                //   TextNombre?.setText("")
                //   TextDireccion?.setText("")
                //   TextDropdown?.setText("Elija una opción",false)
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
                parametros.put("ID_ALMACEN",  sharedViewModel.id.last())
                parametros.put("PRODUCTO",sharedViewModel.productos.last())
                parametros.put("CANTIDAD", cantidad)
                return parametros
            }
        }
        queue1.add(stringRequest)
    }

    private fun registrarFactura(cantidad: String){
        val url2 = "http://186.64.123.248/Reportes/Almacenes/transferencia.php"
        val queue2 = Volley.newRequestQueue(requireContext())
        val stringRequest2 = object: StringRequest(
            Request.Method.POST,
            url2,
            { response ->
                Log.i("Sebastian","$response, ${sharedViewModel.id.last()}, ${sharedViewModel.productos.last()}, $cantidad")
                //   TextNombre?.setText("")
                //   TextDireccion?.setText("")
                //   TextDropdown?.setText("Elija una opción",false)
            },
            { error ->
               // Toast.makeText(requireContext(),"$error", Toast.LENGTH_SHORT).show()
                Log.i("Sebastian","$error, ${sharedViewModel.id.last()}, ${sharedViewModel.productos.last()}, $cantidad")

                // Toast.makeText(requireContext(),"Solo se ha podido borrar el almacen.", Toast.LENGTH_SHORT).show()
            }
        )

        {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("ID_ALMACEN",  sharedViewModel.id.last())
                parametros.put("PRODUCTO",sharedViewModel.productos.last())
                parametros.put("CANTIDAD", cantidad)
                return parametros
            }
        }
        queue2.add(stringRequest2)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}