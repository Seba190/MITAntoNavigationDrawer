package com.seba.mitantonavigationdrawer.ui.removerInventario

import android.graphics.PorterDuff
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
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
import androidx.fragment.app.activityViewModels
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentElegirProductoRemoverBinding
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirInventario.AnadirInventarioFragment
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirInventario.ElegirProductoAnadirViewModel
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import org.json.JSONObject
import java.lang.Exception

class ElegirProductoRemoverFragment : Fragment(R.layout.fragment_elegir_producto_remover) {

    private var _binding: FragmentElegirProductoRemoverBinding? = null
    private val binding get() = _binding!!
    var DropDownProducto: AutoCompleteTextView? = null
    var TextCodigoDeBarra: EditText? = null
    private val sharedViewModel by activityViewModels<SharedViewModel>()
    private var encontrarProducto: Boolean = false
    private var adapter : ArrayAdapter<String>? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val elegirProductoRemoverViewModel =
            ViewModelProvider(this).get(ElegirProductoRemoverViewModel::class.java)

        _binding = FragmentElegirProductoRemoverBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa
        DropDownProducto = binding.tvListaDesplegableElegirProductoRemover.findViewById(R.id.tvListaDesplegableElegirProductoRemover)
        TextCodigoDeBarra = binding.etCodigoDeBarraRemover.findViewById(R.id.etCodigoDeBarraAnadir)
        // Poner los edittext con borde gris
        binding.etCantidadRemover.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etArticulosPorCajaRemover.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etNumeroDeCajasRemover.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etCodigoDeBarraRemover.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)

        // ListaDesplegableElegirProveedor()

        binding.llCajasDeProductoElegirProductoRemover.isVisible = false
        binding.bUnidadesRemover.setOnClickListener {
            binding.llUnidadesElegirProductoRemover.isVisible = false
            binding.llCajasDeProductoElegirProductoRemover.isVisible = true

        }
        binding.bCajasDeProductoRemover.setOnClickListener {
            binding.llCajasDeProductoElegirProductoRemover.isVisible = false
            binding.llUnidadesElegirProductoRemover.isVisible = true
        }

        binding.rlElegirProductoRemover.setOnClickListener {
            binding.rlElegirProductoRemover.isVisible = false
            val anadirInventarioFragment = AnadirInventarioFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.rlElegirProductoRemover,anadirInventarioFragment)
                .commit()
        }

        binding.bmasRemover.setOnClickListener {
            val cantidadActual = binding.etCantidadRemover.text.toString().toIntOrNull() ?: 0
            val cantidadNueva = cantidadActual + 1
            binding.etCantidadRemover.setText(cantidadNueva.toString())
        }
        binding.bmenosRemover.setOnClickListener {
            val cantidadActual = binding.etCantidadRemover.text.toString().toIntOrNull() ?: 0
            val cantidadNueva = if (cantidadActual > 0) cantidadActual - 1 else 0
            binding.etCantidadRemover.setText(cantidadNueva.toString())
        }

        binding.bAnadirFacturaRemover.setOnClickListener {
            encontrarProducto = sharedViewModel.opcionesListRemover.any { item ->
                val trimmedItem = item.substringBefore(" (")
                Log.i("Sebastian2", "${sharedViewModel.opcionesListRemover}, $trimmedItem, ${DropDownProducto?.text.toString().substringBefore(" (").uppercase()}")
                trimmedItem == DropDownProducto?.text.toString().substringBefore(" (").uppercase()
            }
            if(encontrarProducto && DropDownProducto?.text.toString().contains("(") &&
                DropDownProducto?.text.toString().contains(")")) {
                val queue = Volley.newRequestQueue(requireContext())
                val url = "http://186.64.123.248/FacturaSalida/preguntarInventario.php"
                val jsonObjectRequest = object : StringRequest(
                    Request.Method.POST, url,
                    { response ->
                        try {
                            val cantidadUnidades = JSONObject(response).getString("Cantidad")
                            if (binding.llUnidadesElegirProductoRemover.isVisible) {
                                if (binding.etCantidadRemover.text.isNotBlank()) {
                                    if (cantidadUnidades.toInt() < binding.etCantidadRemover.text.toString()
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
                                        sharedViewModel.listaDeProductosRemover.add("${DropDownProducto?.text.toString().substringBefore('(').uppercase()}( 0 unid. )")
                                        sharedViewModel.listaDeCantidadesRemover.add(binding.etCantidadRemover.text.toString())
                                        sharedViewModel.listaDePreciosRemover.add(binding.etPrecioRemover.text.toString())
                                       /* sharedViewModel.opcionesListRemover.removeAll { elemento ->
                                            val nombreProducto = elemento.substringBefore("(")
                                            Log.i("RemoverProducto1", "${sharedViewModel.opcionesListRemover}, ${nombreProducto.uppercase()} , ${sharedViewModel.listaDeProductosRemover}, ${DropDownProducto?.text.toString().substringBefore('(').uppercase()}( 0 unid. )")
                                            sharedViewModel.listaDeProductosRemover.any { it.startsWith("${nombreProducto.uppercase()} ") }
                                        }*/
                                        eliminarElementoDeListaDesplegableConParentesis(DropDownProducto?.text.toString())
                                        //  refreshAdapterAnadirTransferenciaFragment()
                                        binding.tvListaDesplegableElegirProductoRemover.setText(
                                            "Eliga una opción",
                                            false
                                        )
                                        binding.etCantidadRemover.setText("")
                                        binding.etPrecioRemover.setText("")

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
                            } else if (binding.llCajasDeProductoElegirProductoRemover.isVisible) {
                                if (binding.etNumeroDeCajasRemover.text.isNotBlank() && binding.etArticulosPorCajaRemover.text.isNotBlank()) {
                                    if (cantidadUnidades.toInt() < binding.etNumeroDeCajasRemover.text.toString()
                                            .toInt() * binding.etArticulosPorCajaRemover.text.toString()
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
                                            binding.etNumeroDeCajasRemover.text.toString().toInt()
                                                .times(
                                                    binding.etArticulosPorCajaRemover.text.toString()
                                                        .toInt()
                                                )
                                        sharedViewModel.listaDeProductosRemover.add("${DropDownProducto?.text.toString().substringBefore('(').uppercase()}( 0 unid. )")
                                        sharedViewModel.listaDeCantidadesRemover.add(cantidad.toString())
                                        sharedViewModel.listaDePreciosRemover.add(binding.etPrecioRemover.text.toString())
                                        // refreshAdapterAnadirTransferenciaFragment()
                                       /* sharedViewModel.opcionesListRemover.removeAll { elemento ->
                                            val nombreProducto = elemento.substringBefore("(")
                                            Log.i("RemoverProducto2", "${sharedViewModel.opcionesListRemover}, ${nombreProducto.uppercase()} , ${sharedViewModel.listaDeProductosRemover}, ${DropDownProducto?.text.toString().substringBefore('(').uppercase()}( 0 unid. )")
                                            sharedViewModel.listaDeProductosRemover.any { it.startsWith("${nombreProducto.uppercase()} ") }
                                        }*/
                                        eliminarElementoDeListaDesplegableConParentesis(DropDownProducto?.text.toString())
                                        binding.tvListaDesplegableElegirProductoRemover.setText(
                                            "Eliga una opción",
                                            false
                                        )
                                        binding.etArticulosPorCajaRemover.setText("")
                                        binding.etNumeroDeCajasRemover.setText("")
                                        binding.etPrecioRemover.setText("")
                                        adapter?.notifyDataSetChanged()
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
                            Toast.makeText(
                                requireContext(),
                                "La excepcion es $e",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            Log.i("Sebastián", "$e")
                        }
                    },
                    { error -> //Toast.makeText(requireContext(), "El error es $error", Toast.LENGTH_SHORT).show()
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
            } else if(!encontrarProducto && DropDownProducto?.text.toString() != "Eliga una opción") {
                Toast.makeText(requireContext(), "El nombre del producto no es válido", Toast.LENGTH_SHORT).show()
            } else if(DropDownProducto?.text.toString() == "Eliga una opción"){
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
                            if (binding.llUnidadesElegirProductoRemover.isVisible) {
                                if (binding.etCantidadRemover.text.isNotBlank()) {
                                    if (cantidadUnidades.toInt() < binding.etCantidadRemover.text.toString()
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
                                        sharedViewModel.listaDeProductosRemover.add("${DropDownProducto?.text.toString().uppercase()} ( 0 unid. )")
                                        sharedViewModel.listaDeCantidadesRemover.add(binding.etCantidadRemover.text.toString())
                                        sharedViewModel.listaDePreciosRemover.add(binding.etPrecioRemover.text.toString())
                                        eliminarElementoDeListaDesplegableSinParentesis(DropDownProducto?.text.toString().uppercase())
                                        binding.tvListaDesplegableElegirProductoRemover.setText(
                                            "Eliga una opción",
                                            false
                                        )
                                        binding.etCantidadRemover.setText("")
                                        binding.etPrecioRemover.setText("")
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
                            } else if (binding.llCajasDeProductoElegirProductoRemover.isVisible) {
                                if (binding.etNumeroDeCajasRemover.text.isNotBlank() && binding.etArticulosPorCajaRemover.text.isNotBlank()) {
                                    if (cantidadUnidades.toInt() < binding.etNumeroDeCajasRemover.text.toString()
                                            .toInt() * binding.etArticulosPorCajaRemover.text.toString()
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
                                            binding.etNumeroDeCajasRemover.text.toString().toInt()
                                                .times(
                                                    binding.etArticulosPorCajaRemover.text.toString()
                                                        .toInt()
                                                )
                                        sharedViewModel.listaDeProductosRemover.add("${DropDownProducto?.text.toString().uppercase()} ( 0 unid. )")
                                        sharedViewModel.listaDeCantidadesRemover.add(cantidad.toString())
                                        sharedViewModel.listaDePreciosRemover.add(binding.etPrecioRemover.text.toString())
                                        eliminarElementoDeListaDesplegableSinParentesis(DropDownProducto?.text.toString().uppercase())
                                        binding.tvListaDesplegableElegirProductoRemover.setText(
                                            "Eliga una opción",
                                            false
                                        )
                                        binding.etArticulosPorCajaRemover.setText("")
                                        binding.etNumeroDeCajasRemover.setText("")
                                        binding.etPrecioRemover.setText("")
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
                            Toast.makeText(
                                requireContext(),
                                "La excepcion es $e",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            Log.i("Sebastián", "$e")
                        }
                    },
                    { error -> //Toast.makeText(requireContext(), "El error es $error", Toast.LENGTH_SHORT).show()
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
        }

        binding.bVolverRemover.setOnClickListener {
            binding.rlElegirProductoRemover.isVisible = false
            val removerInventarioFragment = RemoverInventarioFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.rlElegirProductoRemover,removerInventarioFragment)
                .commit()
        }

        binding.etCantidadRemover.addTextChangedListener {
            if(binding.etCantidadRemover.text.isNotBlank()) {
                preguntarInventario()
            }
        }

        binding.etNumeroDeCajasRemover.addTextChangedListener{
            if(binding.etNumeroDeCajasRemover.text.isNotBlank()) {
                preguntarInventario()
            }
        }
        binding.etArticulosPorCajaRemover.addTextChangedListener {
            if(binding.etArticulosPorCajaRemover.text.isNotBlank()) {
                preguntarInventario()
            }
        }

        if(sharedViewModel.listaDeAlmacenesRemover.size > 1) {
            for (i in 1..<sharedViewModel.listaDeAlmacenesRemover.size){
                if(sharedViewModel.listaDeAlmacenesRemover[i]==sharedViewModel.listaDeAlmacenesRemover[i-1]){
                    ListaDesplegableElegirProducto()
                }else if(sharedViewModel.listaDeAlmacenesRemover[i]!=sharedViewModel.listaDeAlmacenesRemover[i-1]){
                    sharedViewModel.opcionesListRemover.clear()
                    sharedViewModel.listaDeProductosRemover.clear()
                    sharedViewModel.listaDeCantidadesRemover.clear()
                    sharedViewModel.listaDePreciosRemover.clear()
                    sharedViewModel.listaDePreciosDeProductosRemover.clear()
                    ListaDesplegableElegirProducto()
                }
            }
        }else{
            ListaDesplegableElegirProducto()
        }

        return root
    }

    //Te muestra los productos en inventario de cada almacen

    private fun ListaDesplegableElegirProducto(){
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 ="http://186.64.123.248/FacturaSalida/elegirProductoCantidad.php"
        val jsonObjectRequest = object : StringRequest(
            Request.Method.POST, url1,
            { response ->
                // Obtén el array de opciones desde el objeto JSON
                val jsonArray = JSONObject(response).getJSONArray("Lista")
                // Convierte el array JSON a una lista mutable
                for (i in 0..<jsonArray.length()) {
                    if (!sharedViewModel.opcionesListRemover.contains(jsonArray.getString(i).replace("'", "")) &&
                        !sharedViewModel.listaDeProductosRemover.contains("${jsonArray.getString(i).replace("'", "").substringBefore('(')}( 0 unid. )")) {
                        sharedViewModel.opcionesListRemover.add(jsonArray.getString(i).replace("'", ""))
                    }
                }
                sharedViewModel.opcionesListRemover.removeAll { elemento ->
                    Log.i("ListaRemoverInventario5", "${sharedViewModel.opcionesListRemover}, $elemento, ${sharedViewModel.listaDeProductosRemover}")
                    sharedViewModel.listaDeProductosRemover.any{it == elemento}
                }

               /* sharedViewModel.opcionesListRemover.removeAll { elemento ->
                    val nombreProducto = elemento.substringBefore("(")
                    Log.i("RemoverProducto5", "${sharedViewModel.opcionesListRemover}, ${nombreProducto.uppercase()} , ${sharedViewModel.listaDeProductosRemover}, ${DropDownProducto?.text.toString().uppercase()} ( 0 unid. )")
                    sharedViewModel.listaDeProductosRemover.any { it.startsWith("${nombreProducto.uppercase()} ") }
                }*/
                sharedViewModel.opcionesListRemover.sort()
                adapter?.notifyDataSetChanged()
                //Crea un adpatador para el dropdown
                adapter = ArrayAdapter(requireContext(),android.R.layout.simple_dropdown_item_1line,sharedViewModel.opcionesListRemover)
                //binding.tvholaMundo?.setText(response.getString("Lista"))
                DropDownProducto?.setAdapter(adapter)
                DropDownProducto?.threshold = 1
                DropDownProducto?.onItemClickListener = AdapterView.OnItemClickListener {
                        parent, view, position, id ->
                        precioYCantidad(parent.getItemAtPosition(position).toString())
                }
                binding.etCodigoDeBarraRemover.addTextChangedListener(object: TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        if(s != null && s.length == 13) {
                            codigoDeBarra()
                            Handler(Looper.getMainLooper()).postDelayed({
                                if(DropDownProducto?.text.toString().contains("(") &&
                                    DropDownProducto?.text.toString().contains(")")){
                                    precioYCantidad(DropDownProducto?.text.toString())
                                }else if(!DropDownProducto?.text.toString().contains("(") &&
                                    !DropDownProducto?.text.toString().contains(")")){
                                    Log.i("PrecioyCantidad2","${DropDownProducto?.text.toString().uppercase()}( 0 unid. )")
                                    precioYCantidad("${DropDownProducto?.text.toString().uppercase()}( 0 unid. )")
                                }
                            }, 300)
                        }
                    }

                })
                DropDownProducto?.setOnClickListener {
                    if(DropDownProducto?.text.toString() == "Eliga una opción"){
                        binding.tvListaDesplegableElegirProductoRemover.setText("",false)
                        DropDownProducto?.showDropDown()
                    }
                }
                DropDownProducto?.setOnFocusChangeListener { _, hasFocus ->
                    if(hasFocus && DropDownProducto?.text.toString() == "Eliga una opción"){
                        binding.tvListaDesplegableElegirProductoRemover.setText("",false)
                        DropDownProducto?.showDropDown()
                    }
                    encontrarProducto = sharedViewModel.opcionesListRemover.any { item ->
                        val trimmedItem = item.substringBefore(" (")
                        Log.i("Sebastian3", "${sharedViewModel.opcionesListRemover}, $trimmedItem, ${DropDownProducto?.text.toString().substringBefore(" (").uppercase()}")
                        trimmedItem == DropDownProducto?.text.toString().substringBefore(" (").uppercase()
                    }
                    //Si es un valor no válido con los parentesis
                    if((binding.tvListaDesplegableElegirProductoRemover.text.toString().contains("(") &&
                                binding.tvListaDesplegableElegirProductoRemover.text.toString().contains(")"))) {
                        if (!hasFocus && !sharedViewModel.opcionesListRemover.contains(
                                DropDownProducto?.text.toString())) {
                            Toast.makeText(requireContext(), "El nombre del producto no es válido", Toast.LENGTH_SHORT).show()
                        }

                        //Si es un valor no válido sin los parentesis
                    }else if(!binding.tvListaDesplegableElegirProductoRemover.text.toString().contains("(") &&
                        !binding.tvListaDesplegableElegirProductoRemover.text.toString().contains(")")){

                       //Si esta con minusculas y sin parentesis, no tiene el foco y no se encuentra el producto, que diga el mensaje
                        if(!hasFocus && !encontrarProducto){
                            Toast.makeText(requireContext(), "El nombre del producto no es válido", Toast.LENGTH_SHORT).show()
                        }
                        //Si no esta puesto el foco cuando no hay parentesis y esta escrito por ejemplo agua con gas, que obtenga el precio y cantidad
                        if(!hasFocus){
                            precioYCantidad("${DropDownProducto?.text.toString().uppercase()}( 0 unid. )")
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

    /*private fun ListaDesplegableElegirProducto() {
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 ="http://186.64.123.248/FacturaSalida/elegirProducto.php"
        val jsonObjectRequest1 = JsonObjectRequest(
            Request.Method.GET,url1, null,
            { response ->
                // Obtén el array de opciones desde el objeto JSON
                val jsonArray = response.getJSONArray("Lista")
                // Convierte el array JSON a una lista mutable
                for (i in 0 until jsonArray.length()) {
                    opcionesList.add(jsonArray.getString(i).replace("'",""))
                }
                //Crea un adpatador para el dropdown
                val adapter = ArrayAdapter(requireContext(),R.layout.list_item,opcionesList)
                //binding.tvholaMundo?.setText(response.getString("Lista"))
                DropDownProducto?.setAdapter(adapter)
                DropDownProducto?.onItemClickListener = AdapterView.OnItemClickListener {
                        parent, view, position, id ->
                    if(binding.llCajasDeProductoElegirProductoRemover.isVisible){
                        precioYCantidad(parent.getItemAtPosition(position).toString())
                    }
                }
                binding.etCodigoDeBarraRemover.setOnClickListener {
                    codigoDeBarra()
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (DropDownProducto?.text.toString() != "Eliga una opción") {
                            precioYCantidad(DropDownProducto?.text.toString())
                        }
                    }, 300)
                }
            }, { error ->
                Toast.makeText(requireContext(), " La aplicación no se ha conectado con el servidor", Toast.LENGTH_SHORT).show()
            }
        )
        queue1.add(jsonObjectRequest1)
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.CodigoDeBarraRemover.observe(viewLifecycleOwner) { newText ->
            binding.etCodigoDeBarraRemover.setText(newText)
            Handler(Looper.getMainLooper()).postDelayed({
                if(DropDownProducto?.text.toString().contains("(") &&
                    DropDownProducto?.text.toString().contains(")")){
                    precioYCantidad(DropDownProducto?.text.toString())
                }else if(!DropDownProducto?.text.toString().contains("(") &&
                    !DropDownProducto?.text.toString().contains(")")){
                    Log.i("PrecioyCantidad","${DropDownProducto?.text.toString().uppercase()}( 0 unid. )")
                    precioYCantidad("${DropDownProducto?.text.toString().uppercase()}( 0 unid. )")
                }
            }, 300)
        }
        Handler(Looper.getMainLooper()).postDelayed({
            codigoDeBarra()
        }, 100)
    }

    fun precioYCantidad(producto: String){
        //setFragmentResultListener("Proveedor"){key,bundle ->
        //  sharedViewModel.ListaDeProveedoresAnadir.add(bundle.getString("Proveedor")!!) }
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 = "http://186.64.123.248/FacturaSalida/precioYCantidad.php"
        val jsonObjectRequest1 = object : StringRequest(
            Request.Method.POST, url1,
            { response ->
                val precio = JSONObject(response).getString("Precio")
                val unidades = JSONObject(response).getString("Unidades de embalaje")
                // binding.etPrecioPorUnidadUnidades.setText("200")
                //binding.etCantidad.setText("12")
                if (binding.llCajasDeProductoElegirProductoRemover.isVisible) {
                    binding.etArticulosPorCajaRemover.setText(unidades)
                }
                binding.etPrecioRemover.setText(precio)
                //binding.tvListaDesplegableElegirProveedor.setText("Eliga una opción",false)
                // binding.tvListaDesplegableElegirProducto.setText("Eliga una opción",false)

            }, { error ->
                /*if(binding.tvListaDesplegableElegirProveedor.text.toString() != "Eliga una opción" &&
            binding.tvListaDesplegableElegirProducto.text.toString() != "Eliga una opción") {*/
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
        if (binding.etCodigoDeBarraRemover.text.isNotBlank()) {
            val queue = Volley.newRequestQueue(requireContext())
            val url = "http://186.64.123.248/FacturaSalida/codigoDeBarraProducto.php"
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
                            binding.tvListaDesplegableElegirProductoRemover.postDelayed({
                                binding.tvListaDesplegableElegirProductoRemover.setSelection(sharedViewModel.opcionesListRemover.indexOf("$codigoBarraProducto ( 0 unid. )"))
                                binding.tvListaDesplegableElegirProductoRemover.setText("$codigoBarraProducto ( 0 unid. )",false)
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
                    parametros.put(
                        "CODIGO_BARRA_PRODUCTO",
                        binding.etCodigoDeBarraRemover.text.toString()
                    )
                    return parametros
                }
            }
            queue.add(jsonObjectRequest)
        }
    }

    fun cantidad(producto: String){
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 = "http://186.64.123.248/FacturaSalida/cantidad.php"
        val jsonObjectRequest1 = object : StringRequest(
            Request.Method.POST, url1,
            { response ->
                val precio = JSONObject(response).getString("Precio")
                val unidades = JSONObject(response).getString("Unidades de embalaje")
                // binding.etPrecioPorUnidadUnidades.setText("200")
                //binding.etCantidad.setText("12")
                if (binding.llCajasDeProductoElegirProductoRemover.isVisible) {
                    binding.etArticulosPorCajaRemover.setText(unidades)
                    //binding.etPrecioPorUnidadCajasDeProductos.setText("200")
                }
                //binding.tvListaDesplegableElegirProveedor.setText("Eliga una opción",false)
                // binding.tvListaDesplegableElegirProducto.setText("Eliga una opción",false)

            }, { error ->
                /*if(binding.tvListaDesplegableElegirProveedor.text.toString() != "Eliga una opción" &&
            binding.tvListaDesplegableElegirProducto.text.toString() != "Eliga una opción") {*/
                Toast.makeText(requireContext(), "No se ha podido cargar los parametros $error", Toast.LENGTH_SHORT).show()
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

    fun preguntarInventario(){
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
                        if (binding.llUnidadesElegirProductoRemover.isVisible) {
                            if (cantidad.toInt() < binding.etCantidadRemover.text.toString()
                                    .toInt()
                            ) {
                                val inflaterRemover = requireActivity().layoutInflater
                                val layoutRemover =
                                    inflaterRemover.inflate(R.layout.toast_custom_remover, null)
                                val textRemover =
                                    layoutRemover.findViewById<TextView>(R.id.text_view_toast_remover)
                                textRemover.text =
                                    "La cantidad es mayor que la cantidad en inventario"
                                val toast = Toast(requireContext())
                                toast.duration = Toast.LENGTH_SHORT
                                toast.view = layoutRemover
                                toast.setGravity(Gravity.BOTTOM, 0, 600)
                                toast.show()
                            }
                        } else if (binding.llCajasDeProductoElegirProductoRemover.isVisible) {
                            if (binding.etNumeroDeCajasRemover.text.isNotBlank() && binding.etArticulosPorCajaRemover.text.isNotBlank()) {
                                if (cantidad.toInt() < binding.etNumeroDeCajasRemover.text.toString()
                                        .toInt()
                                        .times(
                                            binding.etArticulosPorCajaRemover.text.toString()
                                                .toInt()
                                        )
                                ) {
                                    val inflaterRemover = requireActivity().layoutInflater
                                    val layoutRemover =
                                        inflaterRemover.inflate(R.layout.toast_custom_remover, null)
                                    val textRemover =
                                        layoutRemover.findViewById<TextView>(R.id.text_view_toast_remover)
                                    textRemover.text =
                                        "La cantidad es mayor que la cantidad en inventario"
                                    val toast = Toast(requireContext())
                                    toast.duration = Toast.LENGTH_SHORT
                                    toast.view = layoutRemover
                                    toast.setGravity(Gravity.BOTTOM, 0, 600)
                                    toast.show()
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            requireContext(), "La excepcion es $e",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.i("Sebastián", "$e")
                    }
                },
                { error -> //Toast.makeText(requireContext(), "El error es $error", Toast.LENGTH_SHORT).show()
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
        }else if (!DropDownProducto?.text.toString().contains("(") &&
            !DropDownProducto?.text.toString().contains(")")
        ) {
            val queue = Volley.newRequestQueue(requireContext())
            val url = "http://186.64.123.248/FacturaSalida/preguntarInventario.php"
            val jsonObjectRequest = object : StringRequest(
                Request.Method.POST, url,
                { response ->
                    try {
                        val cantidad = JSONObject(response).getString("Cantidad")
                        if (binding.llUnidadesElegirProductoRemover.isVisible) {
                            if (cantidad.toInt() < binding.etCantidadRemover.text.toString()
                                    .toInt()
                            ) {
                                val inflaterRemover = requireActivity().layoutInflater
                                val layoutRemover =
                                    inflaterRemover.inflate(R.layout.toast_custom_remover, null)
                                val textRemover =
                                    layoutRemover.findViewById<TextView>(R.id.text_view_toast_remover)
                                textRemover.text =
                                    "La cantidad es mayor que la cantidad en inventario"
                                val toast = Toast(requireContext())
                                toast.duration = Toast.LENGTH_SHORT
                                toast.view = layoutRemover
                                toast.setGravity(Gravity.BOTTOM, 0, 600)
                                toast.show()
                            }
                        } else if (binding.llCajasDeProductoElegirProductoRemover.isVisible) {
                            if (binding.etNumeroDeCajasRemover.text.isNotBlank() && binding.etArticulosPorCajaRemover.text.isNotBlank()) {
                                if (cantidad.toInt() < binding.etNumeroDeCajasRemover.text.toString()
                                        .toInt()
                                        .times(
                                            binding.etArticulosPorCajaRemover.text.toString()
                                                .toInt()
                                        )
                                ) {
                                    val inflaterRemover = requireActivity().layoutInflater
                                    val layoutRemover =
                                        inflaterRemover.inflate(R.layout.toast_custom_remover, null)
                                    val textRemover =
                                        layoutRemover.findViewById<TextView>(R.id.text_view_toast_remover)
                                    textRemover.text =
                                        "La cantidad es mayor que la cantidad en inventario"
                                    val toast = Toast(requireContext())
                                    toast.duration = Toast.LENGTH_SHORT
                                    toast.view = layoutRemover
                                    toast.setGravity(Gravity.BOTTOM, 0, 600)
                                    toast.show()
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            requireContext(), "La excepcion es $e",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.i("Sebastián", "$e")
                    }
                },
                { error -> //Toast.makeText(requireContext(), "El error es $error", Toast.LENGTH_SHORT).show()
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
    }

    fun eliminarElementoDeListaDesplegableSinParentesis(nombre: String) {
        // Filtra la lista para encontrar el elemento a eliminar
        val itemToRemove = sharedViewModel.opcionesListRemover.find { it.startsWith(nombre) }
        Log.i("eliminarElementoDeListaDesplegable","$itemToRemove")
        // Si se encuentra el elemento, eliminarlo
        if (itemToRemove != null) {
            sharedViewModel.opcionesListRemover.remove(itemToRemove)
            // Notificar al adaptador que los datos han cambiado
            adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sharedViewModel.opcionesListRemover)
            DropDownProducto?.threshold = 1
            DropDownProducto?.setAdapter(adapter)
            adapter!!.notifyDataSetChanged()

        }

    }

    fun eliminarElementoDeListaDesplegableConParentesis(nombre: String) {
        // Filtra la lista para encontrar el elemento a eliminar
        val itemToRemove = nombre
        // Si se encuentra el elemento, eliminarlo
        sharedViewModel.opcionesListRemover.remove(itemToRemove)
        // Notificar al adaptador que los datos han cambiado
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sharedViewModel.opcionesListRemover)
        DropDownProducto?.threshold = 1
        DropDownProducto?.setAdapter(adapter)
        adapter!!.notifyDataSetChanged()

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

