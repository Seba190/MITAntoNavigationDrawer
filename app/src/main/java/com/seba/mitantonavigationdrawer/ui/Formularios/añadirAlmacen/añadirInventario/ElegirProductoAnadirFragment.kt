package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirInventario

import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.seba.mitantonavigationdrawer.databinding.FragmentElegirProductoAnadirBinding
import com.seba.mitantonavigationdrawer.databinding.FragmentEstadisticaBinding
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirTransferencia.AnadirTransferenciaFragment
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import com.seba.mitantonavigationdrawer.ui.estadística.EstadisticaViewModel
import org.json.JSONObject
import java.math.BigInteger
import kotlin.Exception

class ElegirProductoAnadirFragment : Fragment(R.layout.fragment_elegir_producto_anadir) {
    private var _binding: FragmentElegirProductoAnadirBinding? = null
    private val binding get() = _binding!!
    var DropDownProducto: AutoCompleteTextView? = null
    var TextCodigoDeBarra: EditText? = null
    private val sharedViewModel by activityViewModels<SharedViewModel>()
    private var encontrarProducto: Boolean = false
    private var adapter : ArrayAdapter<String>? = null
    private var contador: Int = 0
    private var listaEliminados : MutableList<String> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val elegirProductoAnadirViewModel =
            ViewModelProvider(this).get(ElegirProductoAnadirViewModel::class.java)

        _binding = FragmentElegirProductoAnadirBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa
        DropDownProducto = binding.tvListaDesplegableElegirProductoAnadir.findViewById(R.id.tvListaDesplegableElegirProductoAnadir)
        TextCodigoDeBarra = binding.etCodigoDeBarraAnadir.findViewById(R.id.etCodigoDeBarraAnadir)
        // Poner los edittext con borde gris
        binding.etCantidadAnadir.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etArticulosPorCajaAnadir.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etNumeroDeCajasAnadir.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etCodigoDeBarraAnadir.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)

        if(sharedViewModel.listaDeAlmacenesAnadir.size > 1) {
            for (i in 1..<sharedViewModel.listaDeAlmacenesAnadir.size){
                if(sharedViewModel.listaDeAlmacenesAnadir[i]==sharedViewModel.listaDeAlmacenesAnadir[i-1]){
                    ListaDesplegableElegirProducto()
                }else if(sharedViewModel.listaDeAlmacenesAnadir[i]!=sharedViewModel.listaDeAlmacenesAnadir[i-1]){
                    sharedViewModel.opcionesListAnadir.clear()
                    sharedViewModel.listaDeProductosAnadir.clear()
                    sharedViewModel.listaDeCantidadesAnadir.clear()
                    sharedViewModel.listaDePreciosAnadir.clear()
                    ListaDesplegableElegirProducto()
                }
            }
        }else{
            ListaDesplegableElegirProducto()
        }



        // ListaDesplegableElegirProveedor()

        binding.llCajasDeProductoElegirProductoAnadir.isVisible = false
        binding.bUnidadesAnadir.setOnClickListener {
            binding.llUnidadesElegirProductoAnadir.isVisible = false
            binding.llCajasDeProductoElegirProductoAnadir.isVisible = true

        }
        binding.bCajasDeProductoAnadir.setOnClickListener {
            binding.llCajasDeProductoElegirProductoAnadir.isVisible = false
            binding.llUnidadesElegirProductoAnadir.isVisible = true
        }

        binding.rlElegirProductoAnadir.setOnClickListener {
            binding.rlElegirProductoAnadir.isVisible = false
            val anadirInventarioFragment = AnadirInventarioFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.rlElegirProductoAnadir,anadirInventarioFragment)
                .commit()
        }

        binding.bmasAnadir.setOnClickListener {
            val cantidadActual = binding.etCantidadAnadir.text.toString().toIntOrNull() ?: 0
            val cantidadNueva = cantidadActual + 1
            binding.etCantidadAnadir.setText(cantidadNueva.toString())
        }
        binding.bmenosAnadir.setOnClickListener {
            val cantidadActual = binding.etCantidadAnadir.text.toString().toIntOrNull() ?: 0
            val cantidadNueva = if (cantidadActual > 0) cantidadActual - 1 else 0
            binding.etCantidadAnadir.setText(cantidadNueva.toString())
        }

        binding.bAnadirFacturaAnadir.setOnClickListener {
            encontrarProducto = sharedViewModel.opcionesListAnadir.any { item ->
                val trimmedItem = item.substringBefore(" (")
                //Log.i("Sebastian2", "${sharedViewModel.opcionesListTransferencia}, $trimmedItem, ${DropDownProducto?.text.toString()}")
                trimmedItem == DropDownProducto?.text.toString().substringBefore(" (").uppercase()
            }

            if(binding.llUnidadesElegirProductoAnadir.isVisible){
                if(binding.etCantidadAnadir.text.isNotEmpty() && binding.etPrecioAnadir.text.isNotEmpty()) {
                    sharedViewModel.facturaTotalAnadir.add(
                        binding.etCantidadAnadir.text.toString().toInt()
                            .times(binding.etPrecioAnadir.text.toString().toInt()))

                }
            }else {
                if (binding.etNumeroDeCajasAnadir.text.isNotEmpty() &&
                    binding.etArticulosPorCajaAnadir.text.isNotEmpty() &&
                    binding.etPrecioAnadir.text.isNotEmpty()) {
                    val cantidad = binding.etNumeroDeCajasAnadir.text.toString().toInt()
                        .times(binding.etArticulosPorCajaAnadir.text.toString().toInt())
                    sharedViewModel.facturaTotalAnadir.add(
                        cantidad.times(binding.etPrecioAnadir.text.toString().toInt()))
                }
            }
                Log.i("facturaTotalProducto", "${sharedViewModel.facturaTotalAnadir.sum()}")
            //Log.i("facturaTotalLargoString", "${binding.etCantidadAnadir.text.toString().toInt()
              //  .times(binding.etPrecioAnadir.text.toString().toInt()).toString().length}")
                if (sharedViewModel.facturaTotalAnadir.sum()  <= 100000000) {
                    if (encontrarProducto && DropDownProducto?.text.toString().contains("(") &&
                        DropDownProducto?.text.toString().contains(")")
                    ) {
                        if (binding.llUnidadesElegirProductoAnadir.isVisible) {
                            if (binding.etCantidadAnadir.text.isNotBlank()) {
                                try {
                                    sharedViewModel.listaDeProductosAnadir.add(
                                        "${
                                            DropDownProducto?.text.toString().substringBefore('(')
                                                .uppercase()
                                        }( 0 unid. )"
                                    )
                                    sharedViewModel.listaDeCantidadesAnadir.add(binding.etCantidadAnadir.text.toString())
                                    sharedViewModel.listaDePreciosAnadir.add(binding.etPrecioAnadir.text.toString())

                                    eliminarElementoDeListaDesplegableConParentesis(DropDownProducto?.text.toString())
                                    //  refreshAdapterAnadirTransferenciaFragment()
                                    binding.tvListaDesplegableElegirProductoAnadir.setText(
                                        "Elija una opción",
                                        false
                                    )
                                    binding.etCantidadAnadir.setText("")
                                    binding.etPrecioAnadir.setText("")
                                    // sharedViewModel.opcionesListAnadir.removeAll(sharedViewModel.listaDeProductosAnadir)
                                    Toast.makeText(
                                        requireContext(),
                                        "Se ha agregado el producto a la factura",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }catch (e:Exception){
                                    Toast.makeText(
                                        requireContext(),
                                        "El monto es demasiado grande",
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
                        } else if (binding.llCajasDeProductoElegirProductoAnadir.isVisible) {
                            if (binding.etNumeroDeCajasAnadir.text.isNotBlank() && binding.etArticulosPorCajaAnadir.text.isNotBlank()) {
                                try{
                                val cantidad = binding.etNumeroDeCajasAnadir.text.toString().toInt()
                                    .times(binding.etArticulosPorCajaAnadir.text.toString().toInt())
                                sharedViewModel.listaDeProductosAnadir.add(
                                    "${
                                        DropDownProducto?.text.toString().substringBefore('(')
                                            .uppercase()
                                    }( 0 unid. )"
                                )
                                sharedViewModel.listaDeCantidadesAnadir.add(cantidad.toString())
                                sharedViewModel.listaDePreciosAnadir.add(binding.etPrecioAnadir.text.toString())
                                eliminarElementoDeListaDesplegableConParentesis(DropDownProducto?.text.toString())
                                // refreshAdapterAnadirTransferenciaFragment()
                                binding.tvListaDesplegableElegirProductoAnadir.setText(
                                    "Elija una opción",
                                    false
                                )
                                binding.etArticulosPorCajaAnadir.setText("")
                                binding.etNumeroDeCajasAnadir.setText("")
                                binding.etPrecioAnadir.setText("")
                                // sharedViewModel.opcionesListAnadir.removeAll(sharedViewModel.listaDeProductosAnadir)
                                Toast.makeText(
                                    requireContext(), "Se ha agregado el producto a la factura",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }catch (e:Exception){
                                Toast.makeText(
                                    requireContext(),
                                    "El monto es demasiado grande",
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
                    } else if (!encontrarProducto && DropDownProducto?.text.toString() != "Elija una opción") {
                        Toast.makeText(
                            requireContext(),
                            "El nombre del producto no es válido",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (DropDownProducto?.text.toString() == "Elija una opción") {
                        Toast.makeText(requireContext(), "Debe elegir producto", Toast.LENGTH_SHORT)
                            .show()
                    } else if (encontrarProducto && !DropDownProducto?.text.toString()
                            .contains("(") &&
                        !DropDownProducto?.text.toString().contains(")")
                    ) {
                        if (binding.llUnidadesElegirProductoAnadir.isVisible) {
                            if (binding.etCantidadAnadir.text.isNotBlank()) {
                                try{
                                sharedViewModel.listaDeProductosAnadir.add(
                                    "${
                                        DropDownProducto?.text.toString().uppercase()
                                    }( 0 unid. )"
                                )
                                sharedViewModel.listaDeCantidadesAnadir.add(binding.etCantidadAnadir.text.toString())
                                sharedViewModel.listaDePreciosAnadir.add(binding.etPrecioAnadir.text.toString())
                                eliminarElementoDeListaDesplegableSinParentesis(DropDownProducto?.text.toString().uppercase())
                                //  refreshAdapterAnadirTransferenciaFragment()
                                binding.tvListaDesplegableElegirProductoAnadir.setText(
                                    "Elija una opción",
                                    false
                                )
                                binding.etCantidadAnadir.setText("")
                                binding.etPrecioAnadir.setText("")
                                // sharedViewModel.opcionesListAnadir.removeAll(sharedViewModel.listaDeProductosAnadir)
                                Toast.makeText(
                                    requireContext(),
                                    "Se ha agregado el producto a la factura",
                                    Toast.LENGTH_SHORT
                                    ).show()
                                }catch (e:Exception){
                                    Toast.makeText(
                                        requireContext(),
                                        "El monto es demasiado grande",
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
                        } else if (binding.llCajasDeProductoElegirProductoAnadir.isVisible) {
                            if (binding.etNumeroDeCajasAnadir.text.isNotBlank() && binding.etArticulosPorCajaAnadir.text.isNotBlank()) {
                                try{
                                val cantidad = binding.etNumeroDeCajasAnadir.text.toString().toInt()
                                    .times(binding.etArticulosPorCajaAnadir.text.toString().toInt())
                                sharedViewModel.listaDeProductosAnadir.add("${DropDownProducto?.text.toString().uppercase()}( 0 unid. )")
                                sharedViewModel.listaDeCantidadesAnadir.add(cantidad.toString())
                                sharedViewModel.listaDePreciosAnadir.add(binding.etPrecioAnadir.text.toString())
                                eliminarElementoDeListaDesplegableSinParentesis(
                                    DropDownProducto?.text.toString().uppercase()
                                )
                                // refreshAdapterAnadirTransferenciaFragment()
                                binding.tvListaDesplegableElegirProductoAnadir.setText(
                                    "Elija una opción",
                                    false
                                )
                                binding.etArticulosPorCajaAnadir.setText("")
                                binding.etNumeroDeCajasAnadir.setText("")
                                binding.etPrecioAnadir.setText("")
                                // sharedViewModel.opcionesListAnadir.removeAll(sharedViewModel.listaDeProductosAnadir)
                                Toast.makeText(
                                    requireContext(), "Se ha agregado el producto a la factura",
                                    Toast.LENGTH_SHORT
                                ).show()
                                }catch (e:Exception){
                                    Toast.makeText(
                                        requireContext(),
                                        "El monto es demasiado grande",
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
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "La factura total no puede superar los cien millones de pesos",
                        Toast.LENGTH_SHORT
                    ).show()
                    sharedViewModel.facturaTotalAnadir.removeLast()
                }
            }


        binding.bVolverAnadir.setOnClickListener {
            binding.rlElegirProductoAnadir.isVisible = false
            val anadirInventarioFragment = AnadirInventarioFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.rlElegirProductoAnadir,anadirInventarioFragment)
                .commit()
        }

        return root
    }

    //val opcionesList = mutableListOf<String>()
    private fun ListaDesplegableElegirProducto(){
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 ="http://186.64.123.248/FacturaEntrada/elegirProductoCantidad.php"
        val jsonObjectRequest = object : StringRequest(
            Request.Method.POST, url1,
            { response ->
                // Obtén el array de opciones desde el objeto JSON
                val jsonArray = JSONObject(response).getJSONArray("Lista")
                // Convierte el array JSON a una lista mutable
                for (i in 0..<jsonArray.length()) {
                    if (!sharedViewModel.opcionesListAnadir.contains(jsonArray.getString(i).replace("'", ""))&&
                        !sharedViewModel.listaDeProductosAnadir.contains("${jsonArray.getString(i).replace("'", "").substringBefore('(')}( 0 unid. )")) {
                        sharedViewModel.opcionesListAnadir.add(jsonArray.getString(i).replace("'", ""))
                    }
                  }

                sharedViewModel.opcionesListAnadir.removeAll { elemento ->
                    val elementoABuscar = "${elemento.substringBefore('(')}( 0 unid. )"
                    sharedViewModel.listaDeProductosAnadir.contains(elementoABuscar)
                }

                sharedViewModel.opcionesListAnadir.sort()

                //Crea un adpatador para el dropdown
                adapter = ArrayAdapter(requireContext(),android.R.layout.simple_dropdown_item_1line,sharedViewModel.opcionesListAnadir)
                //binding.tvholaMundo?.setText(response.getString("Lista"))
                DropDownProducto?.setAdapter(adapter)
                DropDownProducto?.threshold = 1
                DropDownProducto?.onItemClickListener = AdapterView.OnItemClickListener {
                        parent, view, position, id ->
                        binding.etPrecioAnadir.setText("")
                        binding.etArticulosPorCajaAnadir.setText("")
                        precioYCantidad(parent.getItemAtPosition(position).toString())

                }
                binding.etCodigoDeBarraAnadir.addTextChangedListener(object: TextWatcher{
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        if(s != null && s.length == 13) {
                            codigoDeBarra()
                            Handler(Looper.getMainLooper()).postDelayed({
                                if(DropDownProducto?.text.toString().contains("(") &&
                                    DropDownProducto?.text.toString().contains(")")){
                                    binding.etPrecioAnadir.setText("")
                                    binding.etArticulosPorCajaAnadir.setText("")
                                    precioYCantidad(DropDownProducto?.text.toString())
                                }else if(!DropDownProducto?.text.toString().contains("(") &&
                                    !DropDownProducto?.text.toString().contains(")")){
                                    Log.i("PrecioyCantidad2","${DropDownProducto?.text.toString().uppercase()}( 0 unid. )")
                                    binding.etPrecioAnadir.setText("")
                                    binding.etArticulosPorCajaAnadir.setText("")
                                    precioYCantidad("${DropDownProducto?.text.toString().uppercase()}( 0 unid. )")
                                }
                            }, 300)
                        }
                    }

                })
                DropDownProducto?.setOnClickListener {
                    if(DropDownProducto?.text.toString() == "Elija una opción"){
                        binding.tvListaDesplegableElegirProductoAnadir.setText("",false)
                        DropDownProducto?.showDropDown()
                    }
                }
                DropDownProducto?.setOnFocusChangeListener { _, hasFocus ->
                    if(hasFocus && DropDownProducto?.text.toString() == "Elija una opción"){
                        binding.tvListaDesplegableElegirProductoAnadir.setText("",false)
                        DropDownProducto?.showDropDown()
                    }
                    encontrarProducto = sharedViewModel.opcionesListAnadir.any { item ->
                        val trimmedItem = item.substringBefore(" (")
                       // Log.i("ListaDesplegableAnadir", "${sharedViewModel.opcionesListAnadir}, $trimmedItem , ${DropDownProducto?.text.toString().uppercase()}")
                        trimmedItem == DropDownProducto?.text.toString().substringBefore(" (").uppercase()
                    }
                    //Si es un valor no válido con los parentesis
                    if((binding.tvListaDesplegableElegirProductoAnadir.text.toString().contains("(") &&
                                binding.tvListaDesplegableElegirProductoAnadir.text.toString().contains(")"))) {
                        if (!hasFocus && !sharedViewModel.opcionesListAnadir.contains(
                                DropDownProducto?.text.toString())) {
                            Toast.makeText(requireContext(), "El nombre del producto no es válido", Toast.LENGTH_SHORT).show()
                        }
                        //Si es un valor no válido sin los parentesis
                    }else if(!binding.tvListaDesplegableElegirProductoAnadir.text.toString().contains("(") &&
                                !binding.tvListaDesplegableElegirProductoAnadir.text.toString().contains(")")){

                        if(!hasFocus && !encontrarProducto){
                            Toast.makeText(requireContext(), "El nombre del producto no es válido", Toast.LENGTH_SHORT).show()
                        }
                        if(!hasFocus){
                            binding.etPrecioAnadir.setText("")
                            binding.etArticulosPorCajaAnadir.setText("")
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
                parametros.put("ALMACEN", sharedViewModel.almacenAnadir)
                return parametros
            }
        }
        queue1.add(jsonObjectRequest)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.CodigoDeBarraAnadir.observe(viewLifecycleOwner) { newText ->
            binding.etCodigoDeBarraAnadir.setText(newText)
            Handler(Looper.getMainLooper()).postDelayed({
                if(DropDownProducto?.text.toString().contains("(") &&
                    DropDownProducto?.text.toString().contains(")")){
                    binding.etPrecioAnadir.setText("")
                    binding.etArticulosPorCajaAnadir.setText("")
                    precioYCantidad(DropDownProducto?.text.toString())
                }else if(!DropDownProducto?.text.toString().contains("(") &&
                    !DropDownProducto?.text.toString().contains(")")){
                    Log.i("PrecioyCantidad","${DropDownProducto?.text.toString().uppercase()}( 0 unid. )")
                    binding.etPrecioAnadir.setText("")
                    binding.etArticulosPorCajaAnadir.setText("")
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
        val url1 = "http://186.64.123.248/FacturaEntrada/precioYCantidad.php"
        val jsonObjectRequest1 = object : StringRequest(
            Request.Method.POST, url1,
            { response ->
                val precio = JSONObject(response).getString("Precio")
                val unidades = JSONObject(response).getString("Unidades de embalaje")
                // binding.etPrecioPorUnidadUnidades.setText("200")
                //binding.etCantidad.setText("12")
                if (binding.llCajasDeProductoElegirProductoAnadir.isVisible) {
                        binding.etArticulosPorCajaAnadir.setText(unidades)
                }
                    binding.etPrecioAnadir.setText(precio)

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
                parametros.put("PROVEEDOR", sharedViewModel.proveedorAnadir)
                parametros.put("PRODUCTO", producto)
                return parametros

            }
        }
        queue1.add(jsonObjectRequest1)
    }

    private fun codigoDeBarra(){
        if (binding.etCodigoDeBarraAnadir.text.isNotBlank()) {
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
                            binding.tvListaDesplegableElegirProductoAnadir.postDelayed({
                                binding.tvListaDesplegableElegirProductoAnadir.setSelection(sharedViewModel.opcionesListAnadir.indexOf("$codigoBarraProducto ( 0 unid. )"))
                                binding.tvListaDesplegableElegirProductoAnadir.setText("$codigoBarraProducto ( 0 unid. )",false)
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
                    parametros.put("CODIGO_BARRA_PRODUCTO",
                        binding.etCodigoDeBarraAnadir.text.toString())
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
                if (binding.llCajasDeProductoElegirProductoAnadir.isVisible) {
                    binding.etArticulosPorCajaAnadir.setText(unidades)
                }
                    //binding.etPrecioPorUnidadCajasDeProductos.setText("200")

                //binding.tvListaDesplegableElegirProveedor.setText("Elija una opción",false)
                // binding.tvListaDesplegableElegirProducto.setText("Elija una opción",false)

            }, { error ->
                /*if(binding.tvListaDesplegableElegirProveedor.text.toString() != "Elija una opción" &&
            binding.tvListaDesplegableElegirProducto.text.toString() != "Elija una opción") {*/
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

    fun eliminarElementoDeListaDesplegableSinParentesis(nombre: String) {
        // Filtra la lista para encontrar el elemento a eliminar
        val itemToRemove = sharedViewModel.opcionesListAnadir.find { it.startsWith(nombre) }
        Log.i("eliminarElementoDeListaDesplegableSinParentesis", "$nombre ,$itemToRemove, ${sharedViewModel.opcionesListAnadir}")
        // Si se encuentra el elemento, eliminarlo
        if (itemToRemove != null) {
            sharedViewModel.opcionesListAnadir.remove(itemToRemove)
            contador++
            listaEliminados.add(itemToRemove)
            // Notificar al adaptador que los datos han cambiado
            adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sharedViewModel.opcionesListAnadir)
            DropDownProducto?.threshold = 1
            DropDownProducto?.setAdapter(adapter)
            adapter!!.notifyDataSetChanged()

        }

    }

    fun eliminarElementoDeListaDesplegableConParentesis(nombre: String) {
        // Filtra la lista para encontrar el elemento a eliminar
        // Si se encuentra el elemento, eliminarlo
        Log.i("eliminarElementoDeListaDesplegableConParentesis", "$nombre , ${sharedViewModel.opcionesListAnadir}")
        sharedViewModel.opcionesListAnadir.remove(nombre)
        contador++
        listaEliminados.add(nombre)
        Log.i("eliminarElementoDeListaDesplegableConParentesisDespues", "$nombre , ${sharedViewModel.opcionesListAnadir}")
        // Notificar al adaptador que los datos han cambiado
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sharedViewModel.opcionesListAnadir)
        DropDownProducto?.threshold = 1
        DropDownProducto?.setAdapter(adapter)
        adapter!!.notifyDataSetChanged()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}