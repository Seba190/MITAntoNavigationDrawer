package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.facturaSalida.editarFacturaSalida

import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentElegirProductoFacturaEntradaBinding
import com.seba.mitantonavigationdrawer.databinding.FragmentElegirProductoFacturaSalidaBinding
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirInventario.AnadirInventarioFragment
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirInventario.ElegirProductoAnadirViewModel
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirTransferencia.AnadirTransferenciaFragment
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.facturaEntrada.editarFacturaEntrada.EditarFacturaEntradaFragment
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.facturaEntrada.editarFacturaEntrada.ElegirProductoFacturaEntradaViewModel
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import org.json.JSONObject
import java.lang.Exception

class ElegirProductoFacturaSalidaFragment : Fragment(R.layout.fragment_elegir_producto_factura_salida) {
    private var _binding: FragmentElegirProductoFacturaSalidaBinding? = null
    private val binding get() = _binding!!
    var DropDownProducto: AutoCompleteTextView? = null
    var TextCodigoDeBarra: EditText? = null
    private val sharedViewModel by activityViewModels<SharedViewModel>()
    private var encontrarProducto: Boolean = false
    private var adapter : ArrayAdapter<String>? = null
    private var SegundaVez = false
    private var parEncontrado: Pair<String, Int>? = null
    private var cantidadAgregada: Int? = 0
    private val opcionesListSalidaActualizada: MutableList<String> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val elegirProductoFacturaSalidaViewModel =
            ViewModelProvider(this).get(ElegirProductoFacturaSalidaViewModel::class.java)

        _binding = FragmentElegirProductoFacturaSalidaBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa
        DropDownProducto = binding.tvListaDesplegableElegirProductoFacturaSalida.findViewById(R.id.tvListaDesplegableElegirProductoFacturaSalida)
        TextCodigoDeBarra = binding.etCodigoDeBarraFacturaSalida.findViewById(R.id.etCodigoDeBarraFacturaEntrada)
        // Poner los edittext con borde gris
        binding.etCantidadFacturaSalida.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etArticulosPorCajaFacturaSalida.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etNumeroDeCajasFacturaSalida.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etCodigoDeBarraFacturaSalida.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etPrecioFacturaSalida.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)

      /* if(!SegundaVez) {
           ListaDesplegableElegirProducto()
           SegundaVez = true
       }*/

        // ListaDesplegableElegirProveedor()

        binding.llCajasDeProductoElegirProductoFacturaSalida.isVisible = false
        binding.bUnidadesFacturaSalida.setOnClickListener {
            binding.llUnidadesElegirProductoFacturaSalida.isVisible = false
            binding.llCajasDeProductoElegirProductoFacturaSalida.isVisible = true

        }
        binding.bCajasDeProductoFacturaSalida.setOnClickListener {
            binding.llCajasDeProductoElegirProductoFacturaSalida.isVisible = false
            binding.llUnidadesElegirProductoFacturaSalida.isVisible = true
        }

        binding.rlElegirProductoFacturaSalida.setOnClickListener {
            binding.rlElegirProductoFacturaSalida.isVisible = false
            val editarFacturaSalidaFragment = EditarFacturaSalidaFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.rlElegirProductoFacturaSalida,editarFacturaSalidaFragment)
                .commit()
        }

        binding.bmasFacturaSalida.setOnClickListener {
            val cantidadActual = binding.etCantidadFacturaSalida.text.toString().toIntOrNull() ?: 0
            val cantidadNueva = cantidadActual + 1
            binding.etCantidadFacturaSalida.setText(cantidadNueva.toString())
        }
        binding.bmenosFacturaSalida.setOnClickListener {
            val cantidadActual = binding.etCantidadFacturaSalida.text.toString().toIntOrNull() ?: 0
            val cantidadNueva = if (cantidadActual > 0) cantidadActual - 1 else 0
            binding.etCantidadFacturaSalida.setText(cantidadNueva.toString())
        }

       // configurarListaDesplegable()
        ListaDesplegableElegirProducto()
        binding.bAnadirFacturaSalida.setOnClickListener {
            encontrarProducto = sharedViewModel.opcionesListSalida.any { item ->
                val trimmedItem = item.substringBefore(" (")
                Log.i("Sebastian2", "${sharedViewModel.opcionesListSalida}, $trimmedItem, ${DropDownProducto?.text.toString().substringBefore(" (").uppercase()}")
                trimmedItem == DropDownProducto?.text.toString().substringBefore(" (").uppercase()
            }

           // configurarListaDesplegable()

            if(binding.llUnidadesElegirProductoFacturaSalida.isVisible){
                if(binding.etCantidadFacturaSalida.text.isNotEmpty() && binding.etPrecioFacturaSalida.text.isNotEmpty()) {
                    sharedViewModel.facturaTotalSalida.add(
                        binding.etCantidadFacturaSalida.text.toString().toInt()
                            .times(binding.etPrecioFacturaSalida.text.toString().toInt()))
                }
            }else {
                if (binding.etNumeroDeCajasFacturaSalida.text.isNotEmpty() &&
                    binding.etArticulosPorCajaFacturaSalida.text.isNotEmpty() &&
                    binding.etPrecioFacturaSalida.text.isNotEmpty()) {
                    val cantidad = binding.etNumeroDeCajasFacturaSalida.text.toString().toInt()
                        .times(binding.etArticulosPorCajaFacturaSalida.text.toString().toInt())
                    sharedViewModel.facturaTotalSalida.add(
                        cantidad.times(
                            binding.etPrecioFacturaSalida.text.toString().toInt()))
                }
            }

            if(sharedViewModel.facturaTotalSalida.sum() <= 100000000) {
            if(encontrarProducto && DropDownProducto?.text.toString().contains("(") &&
                DropDownProducto?.text.toString().contains(")")) {
                val queue = Volley.newRequestQueue(requireContext())
                val url = "http://186.64.123.248/FacturaSalida/preguntarInventario.php"
                val jsonObjectRequest = object : StringRequest(
                    Request.Method.POST, url,
                    { response ->
                        try {
                                val cantidadUnidades = JSONObject(response).getString("Cantidad")
                                if (binding.llUnidadesElegirProductoFacturaSalida.isVisible) {
                                    if (binding.etCantidadFacturaSalida.text.isNotBlank()) {
                                        parEncontrado = sharedViewModel.listaCombinadaSalida.find { it.first.substringBefore('(').trim() == DropDownProducto?.text.toString().substringBefore('(').trim() } ?: Pair("",0)
                                        if (cantidadUnidades.toInt() + parEncontrado!!.second
                                            < binding.etCantidadFacturaSalida.text.toString()
                                                .toInt()
                                        ) {
                                            val inflaterRemover = requireActivity().layoutInflater
                                            val layoutRemover = inflaterRemover.inflate(
                                                R.layout.toast_custom_remover,
                                                null
                                            )
                                            val textRemover =
                                                layoutRemover.findViewById<TextView>(R.id.text_view_toast_remover)
                                            textRemover.text =
                                                "La cantidad es mayor que la cantidad en inventario"
                                            val toast = Toast(requireContext())
                                            toast.duration = Toast.LENGTH_SHORT
                                            toast.view = layoutRemover
                                            toast.setGravity(Gravity.BOTTOM, 0, 600)
                                            toast.show()
                                        } else {
                                            sharedViewModel.listaDeProductosRemover.add(
                                                "${
                                                    DropDownProducto?.text.toString()
                                                        .substringBefore('(')
                                                }( 0 unid. )"
                                            )
                                            sharedViewModel.listaDeCantidadesRemover.add(binding.etCantidadFacturaSalida.text.toString())
                                            sharedViewModel.listaDePreciosRemover.add(binding.etPrecioFacturaSalida.text.toString())
                                            eliminarElementoDeListaDesplegableConParentesis(
                                                DropDownProducto?.text.toString()
                                            )
                                            //  refreshAdapterAnadirTransferenciaFragment()
                                            binding.tvListaDesplegableElegirProductoFacturaSalida.setText(
                                                "Elija una opción",
                                                false
                                            )
                                            binding.etCantidadFacturaSalida.setText("")
                                            binding.etPrecioFacturaSalida.setText("")
                                            sharedViewModel.opcionesListSalida.removeAll { elemento ->
                                                val elementoABuscar =
                                                    "${elemento.substringBefore('(')}( 0 unid. )"
                                                sharedViewModel.listaDeProductosRemover.contains(
                                                    elementoABuscar
                                                )
                                            }


                                            // Verifica que el elemento esté en la lista y remuévelo
                                            //ajustarInventarioListaDesplegable()
                                            ListaDesplegableElegirProducto()
                                            // sharedViewModel.opcionesListRemover.removeAll(sharedViewModel.listaDeProductosRemover)
                                            Toast.makeText(
                                                requireContext(),
                                                "Se ha agregado el producto a la factura",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } else {
                                        Toast.makeText(
                                            requireContext(),
                                            "Falta ingresar la cantidad",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else if (binding.llCajasDeProductoElegirProductoFacturaSalida.isVisible) {
                                    if (binding.etNumeroDeCajasFacturaSalida.text.isNotBlank() && binding.etArticulosPorCajaFacturaSalida.text.isNotBlank()) {
                                        parEncontrado = sharedViewModel.listaCombinadaSalida.find { it.first.substringBefore('(').trim() == DropDownProducto?.text.toString().substringBefore('(').trim() } ?: Pair("",0)
                                        if (cantidadUnidades.toInt() + parEncontrado!!.second < binding.etNumeroDeCajasFacturaSalida.text.toString()
                                                .toInt() * binding.etArticulosPorCajaFacturaSalida.text.toString()
                                                .toInt()
                                        ) {
                                            val inflaterRemover = requireActivity().layoutInflater
                                            val layoutRemover = inflaterRemover.inflate(
                                                R.layout.toast_custom_remover,
                                                null
                                            )
                                            val textRemover =
                                                layoutRemover.findViewById<TextView>(R.id.text_view_toast_remover)
                                            textRemover.text =
                                                "La cantidad es mayor que la cantidad en inventario"
                                            val toast = Toast(requireContext())
                                            toast.duration = Toast.LENGTH_SHORT
                                            toast.view = layoutRemover
                                            toast.setGravity(Gravity.BOTTOM, 0, 600)
                                            toast.show()
                                        } else {
                                            val cantidad =
                                                binding.etNumeroDeCajasFacturaSalida.text.toString()
                                                    .toInt()
                                                    .times(
                                                        binding.etArticulosPorCajaFacturaSalida.text.toString()
                                                            .toInt()
                                                    )
                                            sharedViewModel.listaDeProductosRemover.add(
                                                "${
                                                    DropDownProducto?.text.toString()
                                                        .substringBefore('(')
                                                }( 0 unid. )"
                                            )
                                            sharedViewModel.listaDeCantidadesRemover.add(cantidad.toString())
                                            sharedViewModel.listaDePreciosRemover.add(binding.etPrecioFacturaSalida.text.toString())
                                            eliminarElementoDeListaDesplegableConParentesis(
                                                DropDownProducto?.text.toString()
                                            )
                                            // refreshAdapterAnadirTransferenciaFragment()
                                            binding.tvListaDesplegableElegirProductoFacturaSalida.setText(
                                                "Elija una opción",
                                                false
                                            )
                                            binding.etArticulosPorCajaFacturaSalida.setText("")
                                            binding.etNumeroDeCajasFacturaSalida.setText("")
                                            binding.etPrecioFacturaSalida.setText("")
                                            sharedViewModel.opcionesListSalida.removeAll { elemento ->
                                                val elementoABuscar =
                                                    "${elemento.substringBefore('(')}( 0 unid. )"
                                                sharedViewModel.listaDeProductosRemover.contains(
                                                    elementoABuscar
                                                )
                                            }

                                            //ajustarInventarioListaDesplegable()
                                            //ListaDesplegableElegirProducto()
                                            //sharedViewModel.opcionesListRemover.removeAll(sharedViewModel.listaDeProductosRemover)
                                            Toast.makeText(
                                                requireContext(),
                                                "Se ha agregado el producto a la factura",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } else {
                                        Toast.makeText(
                                            requireContext(),
                                            "Falta ingresar al menos los articulos por caja o el número de cajas",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "No se ha podido ingresar la factura",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        } catch (e: Exception) {
                            Log.i("Sebastián", "$e")
                        }
                    },
                    { error -> //Toast.makeText(requireContext(), "Conecte la aplicación al servidor", Toast.LENGTH_SHORT).show()
                        Log.i("Sebastián", "$error")
                    }) {
                    override fun getParams(): MutableMap<String, String> {
                        val parametros = HashMap<String, String>()
                        parametros.put("ALMACEN", sharedViewModel.almacenRemover)
                        parametros.put("PRODUCTO", "${DropDownProducto?.text.toString().substringBefore('(').uppercase()}( 0 unid. )")
                        return parametros
                    }
                }
                queue.add(jsonObjectRequest)
            }else if(!encontrarProducto && DropDownProducto?.text.toString() != "Elija una opción") {
                Toast.makeText(requireContext(), "El nombre del producto no es válido", Toast.LENGTH_SHORT).show()
            } else if(DropDownProducto?.text.toString() == "Elija una opción"){
                Toast.makeText(requireContext(), "Debe elegir producto", Toast.LENGTH_SHORT).show()
            }else if(encontrarProducto && !DropDownProducto?.text.toString().contains("(") &&
                !DropDownProducto?.text.toString().contains(")")){
                val queue = Volley.newRequestQueue(requireContext())
                val url = "http://186.64.123.248/FacturaSalida/preguntarInventario.php"
                val jsonObjectRequest = object : StringRequest(
                    Request.Method.POST, url,
                    { response ->
                        try {
                                val cantidadUnidades = JSONObject(response).getString("Cantidad")
                                if (binding.llUnidadesElegirProductoFacturaSalida.isVisible) {
                                    if (binding.etCantidadFacturaSalida.text.isNotBlank()) {
                                        parEncontrado = sharedViewModel.listaCombinadaSalida.find { it.first.substringBefore('(').trim() == DropDownProducto?.text.toString().uppercase().trim() } ?: Pair("",0)
                                        if (cantidadUnidades.toInt() + parEncontrado!!.second < binding.etCantidadFacturaSalida.text.toString()
                                                .toInt()
                                        ) {
                                            val inflaterRemover = requireActivity().layoutInflater
                                            val layoutRemover = inflaterRemover.inflate(
                                                R.layout.toast_custom_remover,
                                                null
                                            )
                                            val textRemover =
                                                layoutRemover.findViewById<TextView>(R.id.text_view_toast_remover)
                                            textRemover.text =
                                                "La cantidad es mayor que la cantidad en inventario"
                                            val toast = Toast(requireContext())
                                            toast.duration = Toast.LENGTH_SHORT
                                            toast.view = layoutRemover
                                            toast.setGravity(Gravity.BOTTOM, 0, 600)
                                            toast.show()
                                        } else {
                                            sharedViewModel.listaDeProductosRemover.add(
                                                "${
                                                    DropDownProducto?.text.toString()
                                                        .substringBefore('(')
                                                }( 0 unid. )"
                                            )
                                            sharedViewModel.listaDeCantidadesRemover.add(binding.etCantidadFacturaSalida.text.toString())
                                            sharedViewModel.listaDePreciosRemover.add(binding.etPrecioFacturaSalida.text.toString())
                                            eliminarElementoDeListaDesplegableSinParentesis(
                                                DropDownProducto?.text.toString().uppercase()
                                            )
                                            //  refreshAdapterAnadirTransferenciaFragment()
                                            binding.tvListaDesplegableElegirProductoFacturaSalida.setText(
                                                "Elija una opción",
                                                false
                                            )
                                            binding.etCantidadFacturaSalida.setText("")
                                            binding.etPrecioFacturaSalida.setText("")
                                            sharedViewModel.opcionesListSalida.removeAll { elemento ->
                                                val elementoABuscar =
                                                    "${elemento.substringBefore('(')}( 0 unid. )"
                                                sharedViewModel.listaDeProductosRemover.contains(
                                                    elementoABuscar
                                                )
                                            }
                                            ListaDesplegableElegirProducto()
                                            //ajustarInventarioListaDesplegable()
                                            // sharedViewModel.opcionesListRemover.removeAll(sharedViewModel.listaDeProductosRemover)
                                            Toast.makeText(
                                                requireContext(),
                                                "Se ha agregado el producto a la factura",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } else {
                                        Toast.makeText(
                                            requireContext(),
                                            "Falta ingresar la cantidad",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else if (binding.llCajasDeProductoElegirProductoFacturaSalida.isVisible) {
                                    if (binding.etNumeroDeCajasFacturaSalida.text.isNotBlank() && binding.etArticulosPorCajaFacturaSalida.text.isNotBlank()) {
                                        parEncontrado = sharedViewModel.listaCombinadaSalida.find { it.first.substringBefore('(').trim() == DropDownProducto?.text.toString().uppercase().trim() } ?: Pair("",0)
                                        if (cantidadUnidades.toInt() + parEncontrado!!.second < binding.etNumeroDeCajasFacturaSalida.text.toString()
                                                .toInt() * binding.etArticulosPorCajaFacturaSalida.text.toString()
                                                .toInt()
                                        ) {
                                            val inflaterRemover = requireActivity().layoutInflater
                                            val layoutRemover = inflaterRemover.inflate(
                                                R.layout.toast_custom_remover,
                                                null
                                            )
                                            val textRemover =
                                                layoutRemover.findViewById<TextView>(R.id.text_view_toast_remover)
                                            textRemover.text =
                                                "La cantidad es mayor que la cantidad en inventario"
                                            val toast = Toast(requireContext())
                                            toast.duration = Toast.LENGTH_SHORT
                                            toast.view = layoutRemover
                                            toast.setGravity(Gravity.BOTTOM, 0, 600)
                                            toast.show()
                                        } else {
                                            val cantidad =
                                                binding.etNumeroDeCajasFacturaSalida.text.toString()
                                                    .toInt()
                                                    .times(
                                                        binding.etArticulosPorCajaFacturaSalida.text.toString()
                                                            .toInt()
                                                    )
                                            sharedViewModel.listaDeProductosRemover.add(
                                                "${
                                                    DropDownProducto?.text.toString()
                                                        .substringBefore('(')
                                                }( 0 unid. )"
                                            )
                                            sharedViewModel.listaDeCantidadesRemover.add(cantidad.toString())
                                            sharedViewModel.listaDePreciosRemover.add(binding.etPrecioFacturaSalida.text.toString())
                                            eliminarElementoDeListaDesplegableSinParentesis(
                                                DropDownProducto?.text.toString().uppercase()
                                            )
                                            // refreshAdapterAnadirTransferenciaFragment()
                                            binding.tvListaDesplegableElegirProductoFacturaSalida.setText(
                                                "Elija una opción",
                                                false
                                            )
                                            binding.etArticulosPorCajaFacturaSalida.setText("")
                                            binding.etNumeroDeCajasFacturaSalida.setText("")
                                            binding.etPrecioFacturaSalida.setText("")
                                            sharedViewModel.opcionesListSalida.removeAll { elemento ->
                                                val elementoABuscar =
                                                    "${elemento.substringBefore('(')}( 0 unid. )"
                                                sharedViewModel.listaDeProductosRemover.contains(
                                                    elementoABuscar
                                                )
                                            }
                                            ListaDesplegableElegirProducto()
                                            //ajustarInventarioListaDesplegable()
                                            //sharedViewModel.opcionesListRemover.removeAll(sharedViewModel.listaDeProductosRemover)
                                            Toast.makeText(
                                                requireContext(),
                                                "Se ha agregado el producto a la factura",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } else {
                                        Toast.makeText(
                                            requireContext(),
                                            "Falta ingresar al menos los articulos por caja o el número de cajas",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "No se ha podido ingresar la factura",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        } catch (e: Exception) {
                            Log.i("Sebastián", "$e")
                        }
                    },
                    { error -> //Toast.makeText(requireContext(), "Conecte la aplicación al servidor", Toast.LENGTH_SHORT).show()
                        Log.i("Sebastián", "$error")
                    }) {
                    override fun getParams(): MutableMap<String, String> {
                        val parametros = HashMap<String, String>()
                        parametros.put("ALMACEN", sharedViewModel.almacenRemover)
                        parametros.put("PRODUCTO", "${DropDownProducto?.text.toString().uppercase()}( 0 unid. )")
                        return parametros
                    }
                }
                queue.add(jsonObjectRequest)
            }
            }else{
                Toast.makeText(
                    requireContext(),
                    "La factura total no puede superar los cien millones de pesos",
                    Toast.LENGTH_SHORT
                ).show()
                sharedViewModel.facturaTotalSalida.removeLast()
            }
        }

        binding.bVolverFacturaSalida.setOnClickListener {
            binding.rlElegirProductoFacturaSalida.isVisible = false
            val editarFacturaSalidaFragment = EditarFacturaSalidaFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.rlElegirProductoFacturaSalida,editarFacturaSalidaFragment)
                .commit()
        }

        binding.etCantidadFacturaSalida.addTextChangedListener {
            if(binding.etCantidadFacturaSalida.text.isNotBlank()) {
                preguntarInventario()
            }
        }

        binding.etNumeroDeCajasFacturaSalida.addTextChangedListener{
            if(binding.etNumeroDeCajasFacturaSalida.text.isNotBlank()) {
                preguntarInventario()
            }
        }
        binding.etArticulosPorCajaFacturaSalida.addTextChangedListener {
            if(binding.etArticulosPorCajaFacturaSalida.text.isNotBlank()) {
                preguntarInventario()
            }
        }

        return root
    }


    private fun ListaDesplegableElegirProducto(){
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 ="http://186.64.123.248/FacturaSalida/elegirProductoCantidad.php"
        val jsonObjectRequest = object : StringRequest(
            Request.Method.POST, url1,
            { response ->
                opcionesListSalidaActualizada.clear()
                // Obtén el array de opciones desde el objeto JSON
                val jsonArray = JSONObject(response).getJSONArray("Lista")
                // Convierte el array JSON a una lista mutable
               // Log.i("Sebastian2", "${sharedViewModel.opcionesListSalida} y ${sharedViewModel.listaDeProductosRemover}")
                for (i in 0..<jsonArray.length()) {
                    if (!sharedViewModel.opcionesListSalida.contains(jsonArray.getString(i).replace("'", "")) &&
                        !sharedViewModel.listaDeProductosRemover.contains("${jsonArray.getString(i).replace("'", "").substringBefore('(')}( 0 unid. )")){
                       //Log.i("Sebastian2","${jsonArray.getString(i).replace("'", "").substringBefore('(')}( 0 unid. )")
                        sharedViewModel.opcionesListSalida.add(jsonArray.getString(i).replace("'", ""))
                    }
                }
               //Log.i("Sebastian","${sharedViewModel.opcionesListSalida[0].substringBefore('(',sharedViewModel.opcionesListSalida[0])}( 0 unid. )")
               /* for (i in 0..<sharedViewModel.opcionesListSalida.size-sharedViewModel.listaDeProductosRemover.size){
                    //Log.i("Sebastian", "${sharedViewModel.listaDeProductosRemover[i].substringBefore('(',sharedViewModel.listaDeProductosRemover[i])} ( 0 unid. )" )
                    if(sharedViewModel.listaDeProductosRemover.contains("${sharedViewModel.opcionesListSalida[i].substringBefore('(',sharedViewModel.opcionesListSalida[i])}( 0 unid. )")){
                      sharedViewModel.opcionesListSalida.remove(sharedViewModel.opcionesListSalida[i])
                    }
                }*/
                sharedViewModel.opcionesListSalida.removeAll { elemento ->
                    val elementoABuscar = "${elemento.substringBefore('(')}( 0 unid. )"
                    sharedViewModel.listaDeProductosRemover.contains(elementoABuscar)
                }
                sharedViewModel.opcionesListSalida.sort()

                ajustarInventarioListaDesplegable()

               /* for (i in 0..100){
                    if(sharedViewModel.reponedorListSalida[i] != "Hola")
                      sharedViewModel.opcionesListSalida.remove(sharedViewModel.reponedorListSalida[i])
                }*/
                //Crea un adpatador para el dropdown
                adapter = ArrayAdapter(requireContext(),R.layout.list_item,opcionesListSalidaActualizada)
                //binding.tvholaMundo?.setText(response.getString("Lista"))
                DropDownProducto?.setAdapter(adapter)
                DropDownProducto?.threshold = 1
                DropDownProducto?.onItemClickListener = AdapterView.OnItemClickListener {
                        parent, view, position, id ->
                    binding.etPrecioFacturaSalida.setText("")
                    binding.etCantidadFacturaSalida.setText("")
                    binding.etArticulosPorCajaFacturaSalida.setText("")
                    binding.etNumeroDeCajasFacturaSalida.setText("")
                    precioYCantidad(parent.getItemAtPosition(position).toString())
                    Handler(Looper.getMainLooper()).postDelayed({
                        preguntarInventario()
                    },200)
                }
                binding.etCodigoDeBarraFacturaSalida.addTextChangedListener(object: TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        if(s != null && s.length == 13) {
                            codigoDeBarra()
                            Handler(Looper.getMainLooper()).postDelayed({
                                if(DropDownProducto?.text.toString().contains("(") &&
                                    DropDownProducto?.text.toString().contains(")")){
                                    binding.etPrecioFacturaSalida.setText("")
                                    binding.etCantidadFacturaSalida.setText("")
                                    binding.etArticulosPorCajaFacturaSalida.setText("")
                                    binding.etNumeroDeCajasFacturaSalida.setText("")
                                    precioYCantidad(DropDownProducto?.text.toString())
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        preguntarInventario()
                                    },200)
                                }else if(!DropDownProducto?.text.toString().contains("(") &&
                                    !DropDownProducto?.text.toString().contains(")")){
                                    Log.i("PrecioyCantidad2","${DropDownProducto?.text.toString().uppercase()}( 0 unid. )")
                                    binding.etPrecioFacturaSalida.setText("")
                                    binding.etCantidadFacturaSalida.setText("")
                                    binding.etArticulosPorCajaFacturaSalida.setText("")
                                    binding.etNumeroDeCajasFacturaSalida.setText("")
                                    precioYCantidad("${DropDownProducto?.text.toString().uppercase()}( 0 unid. )")
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        preguntarInventario()
                                    },200)
                                }
                            }, 300)
                        }
                    }

                })
                DropDownProducto?.setOnClickListener {
                    if(DropDownProducto?.text.toString() == "Elija una opción"){
                        binding.tvListaDesplegableElegirProductoFacturaSalida.setText("",false)
                        DropDownProducto?.showDropDown()
                    }
                }
                DropDownProducto?.setOnFocusChangeListener { _, hasFocus ->
                    if(hasFocus && DropDownProducto?.text.toString() == "Elija una opción"){
                        binding.tvListaDesplegableElegirProductoFacturaSalida.setText("",false)
                        DropDownProducto?.showDropDown()
                    }
                    encontrarProducto = opcionesListSalidaActualizada.any { item ->
                        val trimmedItem = item.substringBefore(" (")
                        Log.i("Sebastian3", "${sharedViewModel.opcionesListSalida}, $trimmedItem, ${DropDownProducto?.text.toString().substringBefore(" (").uppercase()}")
                        trimmedItem == DropDownProducto?.text.toString().substringBefore(" (").uppercase()
                    }
                    //Si es un valor no válido con los parentesis
                    if((binding.tvListaDesplegableElegirProductoFacturaSalida.text.toString().contains("("))) {
                        if (!hasFocus && !opcionesListSalidaActualizada.contains(
                                DropDownProducto?.text.toString())) {
                            Toast.makeText(requireContext(), "El nombre del producto no es válido", Toast.LENGTH_SHORT).show()
                        }

                        //Si es un valor no válido sin los parentesis
                    }else if(!binding.tvListaDesplegableElegirProductoFacturaSalida.text.toString().contains("(") &&
                        !binding.tvListaDesplegableElegirProductoFacturaSalida.text.toString().contains(")")){

                        //Si esta con minusculas y sin parentesis, no tiene el foco y no se encuentra el producto, que diga el mensaje
                        if(!hasFocus && !encontrarProducto){
                            Toast.makeText(requireContext(), "El nombre del producto no es válido", Toast.LENGTH_SHORT).show()
                        }
                        //Si no esta puesto el foco cuando no hay parentesis y esta escrito por ejemplo agua con gas, que obtenga el precio y cantidad
                        if(!hasFocus){
                            binding.etPrecioFacturaSalida.setText("")
                            binding.etCantidadFacturaSalida.setText("")
                            binding.etArticulosPorCajaFacturaSalida.setText("")
                            binding.etNumeroDeCajasFacturaSalida.setText("")
                            precioYCantidad("${DropDownProducto?.text.toString().uppercase()}( 0 unid. )")
                            Handler(Looper.getMainLooper()).postDelayed({
                                preguntarInventario()
                            },200)
                        }
                    }
                }
            },
            { error ->
                Toast.makeText(requireContext(), " La aplicación no se ha conectado con el servidor", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("ALMACEN", sharedViewModel.almacenRemover)
                return parametros
            }
        }
        queue1.add(jsonObjectRequest)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.CodigoDeBarraRemover.observe(viewLifecycleOwner) { newText ->
            binding.etCodigoDeBarraFacturaSalida.setText(newText)
            Handler(Looper.getMainLooper()).postDelayed({
                if(DropDownProducto?.text.toString().contains("(") &&
                    DropDownProducto?.text.toString().contains(")")){
                    binding.etPrecioFacturaSalida.setText("")
                    binding.etCantidadFacturaSalida.setText("")
                    binding.etArticulosPorCajaFacturaSalida.setText("")
                    binding.etNumeroDeCajasFacturaSalida.setText("")
                    precioYCantidad(DropDownProducto?.text.toString())
                    Handler(Looper.getMainLooper()).postDelayed({
                        preguntarInventario()
                    },200)
                }else if(!DropDownProducto?.text.toString().contains("(") &&
                    !DropDownProducto?.text.toString().contains(")")){
                    Log.i("PrecioyCantidad","${DropDownProducto?.text.toString().uppercase()}( 0 unid. )")
                    binding.etPrecioFacturaSalida.setText("")
                    binding.etCantidadFacturaSalida.setText("")
                    binding.etArticulosPorCajaFacturaSalida.setText("")
                    binding.etNumeroDeCajasFacturaSalida.setText("")
                    precioYCantidad("${DropDownProducto?.text.toString().uppercase()}( 0 unid. )")
                    Handler(Looper.getMainLooper()).postDelayed({
                        preguntarInventario()
                    },200)
                }
            }, 300)
        }
        Handler(Looper.getMainLooper()).postDelayed({
           codigoDeBarra()
        }, 100)

    }

    fun precioYCantidad(producto: String){
        //setFragmentResultListener("Proveedor"){key,bundle ->
        //  sharedViewModel.ListaDeProveedoresFacturaEntrada.add(bundle.getString("Proveedor")!!) }
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 = "http://186.64.123.248/FacturaSalida/precioYCantidad.php"
        val jsonObjectRequest1 = object : StringRequest(
            Request.Method.POST, url1,
            { response ->
                val precio = JSONObject(response).getString("Precio")
                val unidades = JSONObject(response).getString("Unidades de embalaje")
                // binding.etPrecioPorUnidadUnidades.setText("200")
                //binding.etCantidad.setText("12")
                if (binding.llCajasDeProductoElegirProductoFacturaSalida.isVisible) {
                        binding.etArticulosPorCajaFacturaSalida.setText(unidades)
                }
                    binding.etPrecioFacturaSalida.setText(precio)

                //binding.tvListaDesplegableElegirProveedor.setText("Elija una opción",false)
                // binding.tvListaDesplegableElegirProducto.setText("Elija una opción",false)

            }, { error ->
                /*if(binding.tvListaDesplegableElegirProveedor.text.toString() != "Elija una opción" &&
            binding.tvListaDesplegableElegirProducto.text.toString() != "Elija una opción") {*/
                cantidad(producto)
            }

            // }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                // parametros.put("PRODUCTO", DropDownProducto?.text.toString())
                parametros.put("CLIENTE", sharedViewModel.clienteRemover)
                parametros.put("PRODUCTO", producto)
                return parametros

            }
        }
        queue1.add(jsonObjectRequest1)
    }

    private fun codigoDeBarra(){
        if (binding.etCodigoDeBarraFacturaSalida.text.isNotBlank()) {
            val queue = Volley.newRequestQueue(requireContext())
            val url = "http://186.64.123.248/FacturaEntrada/codigoDeBarraProducto.php"
            val jsonObjectRequest = object : StringRequest(
                Request.Method.POST, url,
                { response ->
                    try {
                        val codigoBarraProducto = JSONObject(response).getString("PRODUCTO")
                        val codigoBarraEmbalaje = JSONObject(response).getString("EMBALAJE")
                        if (codigoBarraEmbalaje == "") {
                            Toast.makeText(
                                requireContext(),
                                "EL código de barra pertenece al producto $codigoBarraProducto",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.tvListaDesplegableElegirProductoFacturaSalida.postDelayed({
                                binding.tvListaDesplegableElegirProductoFacturaSalida.setSelection(sharedViewModel.opcionesListSalida.indexOf("$codigoBarraProducto ( 0 unid. )"))
                                binding.tvListaDesplegableElegirProductoFacturaSalida.setText("$codigoBarraProducto ( 0 unid. )",false)
                            },100)

                            // DropDownProducto?.performClick()


                            //DropDownProducto?.setSelection(opcionesList.indexOf("$codigoBarraProducto ( 0 unid. )"))


                        } else if (codigoBarraProducto == "") {
                            Toast.makeText(
                                requireContext(),
                                "Código de barra de embalaje que pertenece al producto $codigoBarraEmbalaje",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    catch(e: Exception){
                        Toast.makeText(requireContext(), "No hay producto o embalaje asociado a este código de barra",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                { error ->
                    Toast.makeText(
                        requireContext(),
                        "No hay producto o embalaje asociado a este código de barra",
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                override fun getParams(): MutableMap<String, String> {
                    val parametros = HashMap<String, String>()
                    parametros.put("CODIGO_BARRA_PRODUCTO", binding.etCodigoDeBarraFacturaSalida.text.toString())
                    return parametros
                }
            }
            queue.add(jsonObjectRequest)
        }
    }

    fun cantidad(producto: String){
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 = "http://186.64.123.248/FacturaEntrada/cantidad.php"
        val jsonObjectRequest1 = object : StringRequest(
            Request.Method.POST, url1,
            { response ->
                //val precio = JSONObject(response).getString("Precio")
                val unidades = JSONObject(response).getString("Unidades de embalaje")
                // binding.etPrecioPorUnidadUnidades.setText("200")
                //binding.etCantidad.setText("12")
                if (binding.llCajasDeProductoElegirProductoFacturaSalida.isVisible) {
                        binding.etArticulosPorCajaFacturaSalida.setText(unidades)

                    //binding.etPrecioPorUnidadCajasDeProductos.setText("200")
                }
                //binding.tvListaDesplegableElegirProveedor.setText("Elija una opción",false)
                // binding.tvListaDesplegableElegirProducto.setText("Elija una opción",false)

            }, { error ->
                /*if(binding.tvListaDesplegableElegirProveedor.text.toString() != "Elija una opción" &&
            binding.tvListaDesplegableElegirProducto.text.toString() != "Elija una opción") {*/
                Toast.makeText(requireContext(), "No se ha podido cargar los parametros", Toast.LENGTH_SHORT).show()
                // binding.etCantidad.setText(unidadesEmbalaje)
                Log.i("Runtime","$error")
            }

            // }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                // parametros.put("PRODUCTO", DropDownProducto?.text.toString())
                parametros.put("PRODUCTO", producto)
                return parametros

            }
        }
        queue1.add(jsonObjectRequest1)
    }

    fun preguntarInventario() {
            if (DropDownProducto?.text.toString().contains("(") &&
                DropDownProducto?.text.toString().contains(")")
            ) {

                val queue = Volley.newRequestQueue(requireContext())
                val url = "http://186.64.123.248/FacturaSalida/preguntarInventario.php"
                val jsonObjectRequest = object : StringRequest(
                    Request.Method.POST, url,
                    { response ->
                        try {
                            val cantidad = JSONObject(response).getString("Cantidad")
                            if (binding.llUnidadesElegirProductoFacturaSalida.isVisible) {
                                parEncontrado = sharedViewModel.listaCombinadaSalida.find { it.first.substringBefore('(').trim() == DropDownProducto?.text.toString().substringBefore('(').trim() } ?: Pair("",0)
                                Log.i("Cero","${cantidad.toInt() + parEncontrado!!.second}")
                                if(binding.etCantidadFacturaSalida.text.isNotBlank()) {
                                if (cantidad.toInt() + parEncontrado!!.second < binding.etCantidadFacturaSalida.text.toString()
                                        .toInt()
                                ) {
                                    val inflaterRemover = requireActivity().layoutInflater
                                    val layoutRemover =
                                        inflaterRemover.inflate(
                                            R.layout.toast_custom_remover,
                                            null
                                        )
                                    val textRemover =
                                        layoutRemover.findViewById<TextView>(R.id.text_view_toast_remover)
                                    textRemover.text =
                                        "La cantidad es mayor que la cantidad en inventario"
                                    val toast = Toast(requireContext())
                                    toast.duration = Toast.LENGTH_SHORT
                                    toast.view = layoutRemover
                                    toast.setGravity(Gravity.BOTTOM, 0, 600)
                                    toast.show()
                                    binding.tvListaDesplegableElegirProductoFacturaSalida.setText(
                                        "Elija una opción",
                                        false
                                    )
                                    binding.etPrecioFacturaSalida.setText("")
                                    binding.etArticulosPorCajaFacturaSalida.setText("")
                                    binding.etCantidadFacturaSalida.setText("")
                                  }
                                }else {
                                    if (cantidad.toInt() + parEncontrado!!.second == 0
                                    ) {
                                        Log.i(
                                            "CeroDentro",
                                            "${cantidad.toInt() + parEncontrado!!.second}"
                                        )
                                        val inflaterRemover = requireActivity().layoutInflater
                                        val layoutRemover =
                                            inflaterRemover.inflate(
                                                R.layout.toast_custom_remover,
                                                null
                                            )
                                        val textRemover =
                                            layoutRemover.findViewById<TextView>(R.id.text_view_toast_remover)
                                        textRemover.text =
                                            "No existe inventario en este almacén"
                                        val toast = Toast(requireContext())
                                        toast.duration = Toast.LENGTH_SHORT
                                        toast.view = layoutRemover
                                        toast.setGravity(Gravity.BOTTOM, 0, 600)
                                        toast.show()
                                        binding.tvListaDesplegableElegirProductoFacturaSalida.setText(
                                            "Elija una opción",
                                            false
                                        )
                                        Handler(Looper.getMainLooper()).postDelayed({
                                            binding.etPrecioFacturaSalida.setText("")
                                            binding.etArticulosPorCajaFacturaSalida.setText("")
                                            binding.etCantidadFacturaSalida.setText("")
                                            binding.etNumeroDeCajasFacturaSalida.setText("")
                                        },300)
                                    }
                                }
                            } else if (binding.llCajasDeProductoElegirProductoFacturaSalida.isVisible) {
                                parEncontrado = sharedViewModel.listaCombinadaSalida.find { it.first.substringBefore('(').trim() == DropDownProducto?.text.toString().substringBefore('(').trim() } ?: Pair("",0)
                                if (binding.etNumeroDeCajasFacturaSalida.text.isNotBlank() && binding.etArticulosPorCajaFacturaSalida.text.isNotBlank()) {
                                    Log.i(
                                        "CeroCajas",
                                        "${cantidad.toInt() + parEncontrado!!.second}"
                                    )
                                    if (cantidad.toInt() + parEncontrado!!.second < binding.etNumeroDeCajasFacturaSalida.text.toString()
                                            .toInt()
                                            .times(
                                                binding.etArticulosPorCajaFacturaSalida.text.toString()
                                                    .toInt()
                                            )
                                    ) {
                                        val inflaterRemover = requireActivity().layoutInflater
                                        val layoutRemover =
                                            inflaterRemover.inflate(
                                                R.layout.toast_custom_remover,
                                                null
                                            )
                                        val textRemover =
                                            layoutRemover.findViewById<TextView>(R.id.text_view_toast_remover)
                                        textRemover.text =
                                            "La cantidad es mayor que la cantidad en inventario"
                                        val toast = Toast(requireContext())
                                        toast.duration = Toast.LENGTH_SHORT
                                        toast.view = layoutRemover
                                        toast.setGravity(Gravity.BOTTOM, 0, 600)
                                        toast.show()
                                        binding.tvListaDesplegableElegirProductoFacturaSalida.setText(
                                            "Elija una opción",
                                            false
                                        )
                                        binding.etPrecioFacturaSalida.setText("")
                                        binding.etArticulosPorCajaFacturaSalida.setText("")
                                        binding.etCantidadFacturaSalida.setText("")
                                    }
                                }else{
                                    if(cantidad.toInt() + parEncontrado!!.second == 0){
                                        Log.i(
                                            "CeroDentroCajas",
                                            "${cantidad.toInt() + parEncontrado!!.second}"
                                        )
                                        val inflaterRemover = requireActivity().layoutInflater
                                        val layoutRemover =
                                            inflaterRemover.inflate(
                                                R.layout.toast_custom_remover,
                                                null
                                            )
                                        val textRemover =
                                            layoutRemover.findViewById<TextView>(R.id.text_view_toast_remover)
                                        textRemover.text =
                                            "No existe inventario en este almacén"
                                        val toast = Toast(requireContext())
                                        toast.duration = Toast.LENGTH_SHORT
                                        toast.view = layoutRemover
                                        toast.setGravity(Gravity.BOTTOM, 0, 600)
                                        toast.show()
                                        binding.tvListaDesplegableElegirProductoFacturaSalida.setText(
                                            "Elija una opción",
                                            false
                                        )
                                        Handler(Looper.getMainLooper()).postDelayed({
                                            binding.etPrecioFacturaSalida.setText("")
                                            binding.etArticulosPorCajaFacturaSalida.setText("")
                                            binding.etCantidadFacturaSalida.setText("")
                                            binding.etNumeroDeCajasFacturaSalida.setText("")
                                        },300)

                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Log.i("Sebastián", "$e")
                        }
                    },
                    { error -> //Toast.makeText(requireContext(), "Conecte la aplicación al servidor", Toast.LENGTH_SHORT).show()
                        Log.i("Sebastián", "$error")
                    }) {
                    override fun getParams(): MutableMap<String, String> {
                        val parametros = HashMap<String, String>()
                        parametros.put("ALMACEN", sharedViewModel.almacenRemover)
                        parametros.put("PRODUCTO", DropDownProducto?.text.toString())
                        return parametros
                    }
                }
                queue.add(jsonObjectRequest)

            } else if (!DropDownProducto?.text.toString().contains("(") &&
                !DropDownProducto?.text.toString().contains(")")
            ) {
                val queue = Volley.newRequestQueue(requireContext())
                val url = "http://186.64.123.248/FacturaSalida/preguntarInventario.php"
                val jsonObjectRequest = object : StringRequest(
                    Request.Method.POST, url,
                    { response ->
                        try {
                            val cantidad = JSONObject(response).getString("Cantidad")
                            if (binding.llUnidadesElegirProductoFacturaSalida.isVisible) {
                                parEncontrado = sharedViewModel.listaCombinadaSalida.find { it.first.substringBefore('(').trim() == DropDownProducto?.text.toString().uppercase().trim() } ?: Pair("",0)
                                if(binding.etCantidadFacturaSalida.text.isNotBlank()) {
                                    if (cantidad.toInt() + parEncontrado!!.second < binding.etCantidadFacturaSalida.text.toString()
                                            .toInt()
                                    ) {
                                        val inflaterRemover = requireActivity().layoutInflater
                                        val layoutRemover =
                                            inflaterRemover.inflate(
                                                R.layout.toast_custom_remover,
                                                null
                                            )
                                        val textRemover =
                                            layoutRemover.findViewById<TextView>(R.id.text_view_toast_remover)
                                        textRemover.text =
                                            "La cantidad es mayor que la cantidad en inventario"
                                        val toast = Toast(requireContext())
                                        toast.duration = Toast.LENGTH_SHORT
                                        toast.view = layoutRemover
                                        toast.setGravity(Gravity.BOTTOM, 0, 600)
                                        toast.show()
                                        binding.tvListaDesplegableElegirProductoFacturaSalida.setText(
                                            "Elija una opción",
                                            false
                                        )
                                        binding.etPrecioFacturaSalida.setText("")
                                        binding.etArticulosPorCajaFacturaSalida.setText("")
                                        binding.etCantidadFacturaSalida.setText("")
                                    }
                                }else{
                                    if (cantidad.toInt() + parEncontrado!!.second == 0
                                    ) {
                                        Log.i(
                                            "CeroDentroSinParentesis",
                                            "${cantidad.toInt() + parEncontrado!!.second}"
                                        )
                                        val inflaterRemover = requireActivity().layoutInflater
                                        val layoutRemover =
                                            inflaterRemover.inflate(
                                                R.layout.toast_custom_remover,
                                                null
                                            )
                                        val textRemover =
                                            layoutRemover.findViewById<TextView>(R.id.text_view_toast_remover)
                                        textRemover.text =
                                            "No existe inventario en este almacén"
                                        val toast = Toast(requireContext())
                                        toast.duration = Toast.LENGTH_SHORT
                                        toast.view = layoutRemover
                                        toast.setGravity(Gravity.BOTTOM, 0, 600)
                                        toast.show()
                                        binding.tvListaDesplegableElegirProductoFacturaSalida.setText(
                                            "Elija una opción",
                                            false
                                        )
                                        Handler(Looper.getMainLooper()).postDelayed({
                                            binding.etPrecioFacturaSalida.setText("")
                                            binding.etArticulosPorCajaFacturaSalida.setText("")
                                            binding.etCantidadFacturaSalida.setText("")
                                            binding.etNumeroDeCajasFacturaSalida.setText("")
                                        },300)
                                    }
                                }
                            } else if (binding.llCajasDeProductoElegirProductoFacturaSalida.isVisible) {
                                parEncontrado = sharedViewModel.listaCombinadaSalida.find { it.first.substringBefore('(').trim() == DropDownProducto?.text.toString().uppercase().trim() } ?: Pair("",0)
                                if (binding.etNumeroDeCajasFacturaSalida.text.isNotBlank() && binding.etArticulosPorCajaFacturaSalida.text.isNotBlank()) {
                                    if (cantidad.toInt() + parEncontrado!!.second < binding.etNumeroDeCajasFacturaSalida.text.toString()
                                            .toInt()
                                            .times(
                                                binding.etArticulosPorCajaFacturaSalida.text.toString()
                                                    .toInt()
                                            )
                                    ) {
                                        val inflaterRemover = requireActivity().layoutInflater
                                        val layoutRemover =
                                            inflaterRemover.inflate(
                                                R.layout.toast_custom_remover,
                                                null
                                            )
                                        val textRemover =
                                            layoutRemover.findViewById<TextView>(R.id.text_view_toast_remover)
                                        textRemover.text =
                                            "La cantidad es mayor que la cantidad en inventario"
                                        val toast = Toast(requireContext())
                                        toast.duration = Toast.LENGTH_SHORT
                                        toast.view = layoutRemover
                                        toast.setGravity(Gravity.BOTTOM, 0, 600)
                                        toast.show()
                                        binding.tvListaDesplegableElegirProductoFacturaSalida.setText(
                                            "Elija una opción",
                                            false
                                        )
                                        binding.etPrecioFacturaSalida.setText("")
                                        binding.etArticulosPorCajaFacturaSalida.setText("")
                                        binding.etCantidadFacturaSalida.setText("")
                                    }
                                } else{
                                    if (cantidad.toInt() + parEncontrado!!.second == 0
                                    ) {
                                        Log.i(
                                            "CeroDentroCajasSinParentesis",
                                            "${cantidad.toInt() + parEncontrado!!.second}"
                                        )
                                        val inflaterRemover = requireActivity().layoutInflater
                                        val layoutRemover =
                                            inflaterRemover.inflate(
                                                R.layout.toast_custom_remover,
                                                null
                                            )
                                        val textRemover =
                                            layoutRemover.findViewById<TextView>(R.id.text_view_toast_remover)
                                        textRemover.text =
                                            "No existe inventario en este almacén"
                                        val toast = Toast(requireContext())
                                        toast.duration = Toast.LENGTH_SHORT
                                        toast.view = layoutRemover
                                        toast.setGravity(Gravity.BOTTOM, 0, 600)
                                        toast.show()
                                        binding.tvListaDesplegableElegirProductoFacturaSalida.setText(
                                            "Elija una opción",
                                            false
                                        )
                                        Handler(Looper.getMainLooper()).postDelayed({
                                            binding.etPrecioFacturaSalida.setText("")
                                            binding.etArticulosPorCajaFacturaSalida.setText("")
                                            binding.etCantidadFacturaSalida.setText("")
                                            binding.etNumeroDeCajasFacturaSalida.setText("")
                                        },300)
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Log.i("Sebastián", "$e")
                        }
                    },
                    { error -> //Toast.makeText(requireContext(), "Conecte la aplicación al servidor", Toast.LENGTH_SHORT).show()
                        Log.i("Sebastián", "$error")
                    }) {
                    override fun getParams(): MutableMap<String, String> {
                        val parametros = HashMap<String, String>()
                        parametros.put("ALMACEN", sharedViewModel.almacenRemover)
                        parametros.put(
                            "PRODUCTO",
                            "${DropDownProducto?.text.toString().uppercase()}( 0 unid. )"
                        )
                        return parametros
                    }
                }
                queue.add(jsonObjectRequest)
            }

    }

    fun eliminarElementoDeListaDesplegableSinParentesis(nombre: String) {
        // Filtra la lista para encontrar el elemento a eliminar
        val itemToRemove = sharedViewModel.opcionesListSalida.find { it.startsWith(nombre) }
        Log.i("eliminarElementoDeListaDesplegable","$itemToRemove")
        // Si se encuentra el elemento, eliminarlo
        if (itemToRemove != null) {
            sharedViewModel.opcionesListSalida.remove(itemToRemove)

            // Notificar al adaptador que los datos han cambiado
            adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sharedViewModel.opcionesListSalida)
            DropDownProducto?.threshold = 1
            DropDownProducto?.setAdapter(adapter)
            adapter!!.notifyDataSetChanged()

        }

    }

    fun eliminarElementoDeListaDesplegableConParentesis(nombre: String) {
        // Filtra la lista para encontrar el elemento a eliminar
        val itemToRemove = nombre
        // Si se encuentra el elemento, eliminarlo
        sharedViewModel.opcionesListSalida.remove(itemToRemove)
        // Notificar al adaptador que los datos han cambiado
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sharedViewModel.opcionesListSalida)
        DropDownProducto?.threshold = 1
        DropDownProducto?.setAdapter(adapter)
        adapter!!.notifyDataSetChanged()

    }

    fun configurarListaDesplegable(){
       /* if(sharedViewModel.listaDeAlmacenesSalida.size > 1) {
            for (i in 1..<sharedViewModel.listaDeAlmacenesSalida.size){
                if(sharedViewModel.listaDeAlmacenesSalida[i]==sharedViewModel.listaDeAlmacenesSalida[i-1]){
                    ListaDesplegableElegirProducto()
                }else if(sharedViewModel.listaDeAlmacenesSalida[i]!=sharedViewModel.listaDeAlmacenesSalida[i-1]){
                    sharedViewModel.opcionesListSalida.clear()
                    sharedViewModel.listaDeProductosRemover.clear()
                    sharedViewModel.listaDeCantidadesRemover.clear()
                    sharedViewModel.listaDePreciosRemover.clear()
                    opcionesListSalidaActualizada.clear()
                    ListaDesplegableElegirProducto()
                }
            }
        }else{
            ListaDesplegableElegirProducto()
        }*/
    }

  fun ajustarInventarioListaDesplegable(){
      val regex = "\\d+".toRegex()
      // val listaGuardada = mutableListOf<Int>()
      // val listaGuardadaProductos = mutableListOf<String>()
      for (cadena in sharedViewModel.opcionesListSalida){
          val match = regex.find(cadena)
          if (match != null) {
              val numeroOriginal = match.value.toInt()
              parEncontrado = sharedViewModel.listaCombinadaSalida.find { it.first.substringBefore('(').trim() == cadena.substringBefore('(').trim() }
             // Log.i("parEncontrado", "$parEncontrado , $numeroOriginal, $cadena, ${sharedViewModel.listaCombinadaSalida}")
              if (parEncontrado != null) {
                  cantidadAgregada = parEncontrado!!.second
                  Log.i("parEncontrado2", "$parEncontrado ,$cantidadAgregada, $numeroOriginal, $cadena, ${sharedViewModel.listaCombinadaSalida}")
                  // listaGuardadaProductos.add(parEncontrado!!.first.substringBefore('('))
                  val nuevoNumero = numeroOriginal + cantidadAgregada!!
                  val nuevaCadena = cadena.replaceFirst(
                      numeroOriginal.toString(),
                      nuevoNumero.toString())
                  opcionesListSalidaActualizada.add(nuevaCadena)

                  //listaGuardada.add(cantidadAgregada!!)
              } else{
                  opcionesListSalidaActualizada.add(cadena)
              }
          } else{
              opcionesListSalidaActualizada.add(cadena)
          }
      }

      Log.i("opcionesListSalida", "${sharedViewModel.opcionesListSalida} y $opcionesListSalidaActualizada")

  }

   /* fun ajustarInventarioListaDesplegable(){
        val regex = "\\d+".toRegex()
        // val listaGuardada = mutableListOf<Int>()
        // val listaGuardadaProductos = mutableListOf<String>()
        for (cadena in sharedViewModel.opcionesListSalida) {
            val match = regex.find(cadena)
            if (match != null) {
                val numeroOriginal = match.value.toInt()
                for ((index, par) in sharedViewModel.listaCombinadaSalida.withIndex()) {
                    // Log.i("parEncontrado", "$parEncontrado , $numeroOriginal, $cadena, ${sharedViewModel.listaCombinadaSalida}")
                    if (par.first.substringBefore('(').trim() == cadena.substringBefore('(')
                            .trim()) {
                        cantidadAgregada = par.second
                        Log.i("parEncontrado2", "$parEncontrado ,$cantidadAgregada, $numeroOriginal, $cadena, ${sharedViewModel.listaCombinadaSalida}")
                        // listaGuardadaProductos.add(parEncontrado!!.first.substringBefore('('))
                        val nuevoNumero = numeroOriginal + cantidadAgregada!!
                        val nuevaCadena = cadena.replaceFirst(
                            numeroOriginal.toString(),
                            nuevoNumero.toString()
                        )
                        opcionesListSalidaActualizada.add(nuevaCadena)
                        break
                        //listaGuardada.add(cantidadAgregada!!)
                    }
                }
            } else {
                opcionesListSalidaActualizada.add(cadena)
            }
        }
        Log.i("opcionesListSalida", "${sharedViewModel.opcionesListSalida} y $opcionesListSalidaActualizada")

    }*/

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}