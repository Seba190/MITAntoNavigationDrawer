package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.facturaEntrada.editarFacturaEntrada

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
import com.seba.mitantonavigationdrawer.databinding.FragmentElegirProductoFacturaEntradaBinding
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirInventario.AnadirInventarioFragment
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirInventario.ElegirProductoAnadirViewModel
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirTransferencia.AnadirTransferenciaFragment
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import org.json.JSONObject
import java.lang.Exception

class ElegirProductoFacturaEntradaFragment : Fragment(R.layout.fragment_elegir_producto_factura_entrada) {
    private var _binding: FragmentElegirProductoFacturaEntradaBinding? = null
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
        val elegirProductoFacturaEntradaViewModel =
            ViewModelProvider(this).get(ElegirProductoFacturaEntradaViewModel::class.java)

        _binding = FragmentElegirProductoFacturaEntradaBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa
        DropDownProducto = binding.tvListaDesplegableElegirProductoFacturaEntrada.findViewById(R.id.tvListaDesplegableElegirProductoFacturaEntrada)
        TextCodigoDeBarra = binding.etCodigoDeBarraFacturaEntrada.findViewById(R.id.etCodigoDeBarraFacturaEntrada)
        // Poner los edittext con borde gris
        binding.etCantidadFacturaEntrada.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etArticulosPorCajaFacturaEntrada.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etNumeroDeCajasFacturaEntrada.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etCodigoDeBarraFacturaEntrada.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)

        if(sharedViewModel.listaDeAlmacenesEntrada.size > 1) {
            for (i in 1..<sharedViewModel.listaDeAlmacenesEntrada.size){
                if(sharedViewModel.listaDeAlmacenesEntrada[i]==sharedViewModel.listaDeAlmacenesEntrada[i-1]){
                    ListaDesplegableElegirProducto()
                }else if(sharedViewModel.listaDeAlmacenesEntrada[i]!=sharedViewModel.listaDeAlmacenesEntrada[i-1]){
                    sharedViewModel.opcionesListEntrada.clear()
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

        binding.llCajasDeProductoElegirProductoFacturaEntrada.isVisible = false
        binding.bUnidadesFacturaEntrada.setOnClickListener {
            binding.llUnidadesElegirProductoFacturaEntrada.isVisible = false
            binding.llCajasDeProductoElegirProductoFacturaEntrada.isVisible = true

        }
        binding.bCajasDeProductoFacturaEntrada.setOnClickListener {
            binding.llCajasDeProductoElegirProductoFacturaEntrada.isVisible = false
            binding.llUnidadesElegirProductoFacturaEntrada.isVisible = true
        }

        binding.rlElegirProductoFacturaEntrada.setOnClickListener {
            binding.rlElegirProductoFacturaEntrada.isVisible = false
            val editarFacturaEntradaFragment = EditarFacturaEntradaFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.rlElegirProductoFacturaEntrada,editarFacturaEntradaFragment)
                .commit()
        }

        binding.bmasFacturaEntrada.setOnClickListener {
            val cantidadActual = binding.etCantidadFacturaEntrada.text.toString().toIntOrNull() ?: 0
            val cantidadNueva = cantidadActual + 1
            binding.etCantidadFacturaEntrada.setText(cantidadNueva.toString())
        }
        binding.bmenosFacturaEntrada.setOnClickListener {
            val cantidadActual = binding.etCantidadFacturaEntrada.text.toString().toIntOrNull() ?: 0
            val cantidadNueva = if (cantidadActual > 0) cantidadActual - 1 else 0
            binding.etCantidadFacturaEntrada.setText(cantidadNueva.toString())
        }

        binding.bAnadirFacturaEntrada.setOnClickListener {
            encontrarProducto = sharedViewModel.opcionesListEntrada.any { item ->
                val trimmedItem = item.substringBefore(" (")
                //Log.i("Sebastian2", "${sharedViewModel.opcionesListTransferencia}, $trimmedItem, ${DropDownProducto?.text.toString()}")
                trimmedItem == DropDownProducto?.text.toString().substringBefore(" (").uppercase()
            }
            if(encontrarProducto && DropDownProducto?.text.toString().contains("(") &&
                DropDownProducto?.text.toString().contains(")")) {
                if (binding.llUnidadesElegirProductoFacturaEntrada.isVisible) {
                    if(binding.etCantidadFacturaEntrada.text.isNotBlank()){
                        sharedViewModel.listaDeProductosAnadir.add("${DropDownProducto?.text.toString().substringBefore('(')}( 0 unid. )")
                        sharedViewModel.listaDeCantidadesAnadir.add(binding.etCantidadFacturaEntrada.text.toString())
                        sharedViewModel.listaDePreciosAnadir.add(binding.etPrecioFacturaEntrada.text.toString())
                        eliminarElementoDeListaDesplegableConParentesis(DropDownProducto?.text.toString())
                        //  refreshAdapterFacturaEntradaTransferenciaFragment()
                        binding.tvListaDesplegableElegirProductoFacturaEntrada.setText("Eliga una opción",false)
                        binding.etCantidadFacturaEntrada.setText("")
                        binding.etPrecioFacturaEntrada.setText("")
                        //sharedViewModel.opcionesListEntrada.removeAll(sharedViewModel.listaDeProductosAnadir)
                        Toast.makeText(
                            requireContext(),
                            "Se ha agregado el producto a la factura",
                            Toast.LENGTH_SHORT
                        ).show()

                    }else{
                        Toast.makeText(requireContext(),"Falta ingresar la cantidad", Toast.LENGTH_SHORT).show()
                    }
                } else if (binding.llCajasDeProductoElegirProductoFacturaEntrada.isVisible) {
                    if (binding.etNumeroDeCajasFacturaEntrada.text.isNotBlank() && binding.etArticulosPorCajaFacturaEntrada.text.isNotBlank()) {
                        val cantidad = binding.etNumeroDeCajasFacturaEntrada.text.toString().toInt()
                            .times(binding.etArticulosPorCajaFacturaEntrada.text.toString().toInt())
                        sharedViewModel.listaDeProductosAnadir.add("${DropDownProducto?.text.toString().substringBefore('(')}( 0 unid. )")
                        sharedViewModel.listaDeCantidadesAnadir.add(cantidad.toString())
                        sharedViewModel.listaDePreciosAnadir.add(binding.etPrecioFacturaEntrada.text.toString())
                        eliminarElementoDeListaDesplegableConParentesis(DropDownProducto?.text.toString())
                        // refreshAdapterFacturaEntradaTransferenciaFragment()
                        binding.tvListaDesplegableElegirProductoFacturaEntrada.setText("Eliga una opción",false)
                        binding.etArticulosPorCajaFacturaEntrada.setText("")
                        binding.etNumeroDeCajasFacturaEntrada.setText("")
                        binding.etPrecioFacturaEntrada.setText("")
                      //  sharedViewModel.opcionesListEntrada.removeAll(sharedViewModel.listaDeProductosAnadir)
                        Toast.makeText(
                            requireContext(), "Se ha agregado el producto a la factura",
                            Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(requireContext(), "Falta ingresar al menos los articulos por caja o el número de cajas", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "No se ha podido ingresar la factura", Toast.LENGTH_SHORT).show()
                }
            }else if(!encontrarProducto && DropDownProducto?.text.toString() != "Eliga una opción") {
                Toast.makeText(requireContext(), "El nombre del producto no es válido", Toast.LENGTH_SHORT).show()
            }else if(DropDownProducto?.text.toString() == "Eliga una opción"){
                Toast.makeText(requireContext(), "Debe elegir producto", Toast.LENGTH_SHORT).show()
            }else if(encontrarProducto && !DropDownProducto?.text.toString().contains("(") &&
                !DropDownProducto?.text.toString().contains(")")) {
                if (binding.llUnidadesElegirProductoFacturaEntrada.isVisible) {
                    if(binding.etCantidadFacturaEntrada.text.isNotBlank()){
                        sharedViewModel.listaDeProductosAnadir.add("${DropDownProducto?.text.toString()}( 0 unid. )")
                        sharedViewModel.listaDeCantidadesAnadir.add(binding.etCantidadFacturaEntrada.text.toString())
                        sharedViewModel.listaDePreciosAnadir.add(binding.etPrecioFacturaEntrada.text.toString())
                        eliminarElementoDeListaDesplegableSinParentesis(DropDownProducto?.text.toString().uppercase())
                        //  refreshAdapterFacturaEntradaTransferenciaFragment()
                        binding.tvListaDesplegableElegirProductoFacturaEntrada.setText("Eliga una opción",false)
                        binding.etCantidadFacturaEntrada.setText("")
                        binding.etPrecioFacturaEntrada.setText("")
                        //sharedViewModel.opcionesListEntrada.removeAll(sharedViewModel.listaDeProductosAnadir)
                        Toast.makeText(
                            requireContext(),
                            "Se ha agregado el producto a la factura",
                            Toast.LENGTH_SHORT
                        ).show()

                    }else{
                        Toast.makeText(requireContext(),"Falta ingresar la cantidad", Toast.LENGTH_SHORT).show()
                    }
                } else if (binding.llCajasDeProductoElegirProductoFacturaEntrada.isVisible) {
                    if (binding.etNumeroDeCajasFacturaEntrada.text.isNotBlank() && binding.etArticulosPorCajaFacturaEntrada.text.isNotBlank()) {
                        val cantidad = binding.etNumeroDeCajasFacturaEntrada.text.toString().toInt()
                            .times(binding.etArticulosPorCajaFacturaEntrada.text.toString().toInt())
                        sharedViewModel.listaDeProductosAnadir.add("${DropDownProducto?.text.toString()}( 0 unid. )")
                        sharedViewModel.listaDeCantidadesAnadir.add(cantidad.toString())
                        sharedViewModel.listaDePreciosAnadir.add(binding.etPrecioFacturaEntrada.text.toString())
                        eliminarElementoDeListaDesplegableSinParentesis(DropDownProducto?.text.toString().uppercase())
                        // refreshAdapterFacturaEntradaTransferenciaFragment()
                        binding.tvListaDesplegableElegirProductoFacturaEntrada.setText("Eliga una opción",false)
                        binding.etArticulosPorCajaFacturaEntrada.setText("")
                        binding.etNumeroDeCajasFacturaEntrada.setText("")
                        binding.etPrecioFacturaEntrada.setText("")
                        //  sharedViewModel.opcionesListEntrada.removeAll(sharedViewModel.listaDeProductosAnadir)
                        Toast.makeText(
                            requireContext(), "Se ha agregado el producto a la factura",
                            Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(requireContext(), "Falta ingresar al menos los articulos por caja o el número de cajas", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "No se ha podido ingresar la factura", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.bVolverFacturaEntrada.setOnClickListener {
            binding.rlElegirProductoFacturaEntrada.isVisible = false
            val editarFacturaEntradaFragment = EditarFacturaEntradaFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.rlElegirProductoFacturaEntrada,editarFacturaEntradaFragment)
                .commit()
        }

        return root
    }

    private fun ListaDesplegableElegirProducto(){
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 ="http://186.64.123.248/Reportes/Transacciones/FacturaEntrada/elegirProductoCantidad.php"
        val jsonObjectRequest = object : StringRequest(
            Request.Method.POST, url1,
            { response ->
                // Obtén el array de opciones desde el objeto JSON
                val jsonArray = JSONObject(response).getJSONArray("Lista")
                // Convierte el array JSON a una lista mutable
                for (i in 0..<jsonArray.length()) {
                    if (!sharedViewModel.opcionesListEntrada.contains(jsonArray.getString(i).replace("'", ""))&&
                        !sharedViewModel.listaDeProductosAnadir.contains("${jsonArray.getString(i).replace("'", "").substringBefore('(')}( 0 unid. )")){
                        sharedViewModel.opcionesListEntrada.add(jsonArray.getString(i).replace("'", ""))
                    }
                }
                sharedViewModel.opcionesListEntrada.removeAll { elemento ->
                    val elementoABuscar = "${elemento.substringBefore('(')}( 0 unid. )"
                    sharedViewModel.listaDeProductosAnadir.contains(elementoABuscar)
                }
                sharedViewModel.opcionesListEntrada.sort()
                //Crea un adpatador para el dropdown
                adapter = ArrayAdapter(requireContext(),R.layout.list_item,sharedViewModel.opcionesListEntrada)
                //binding.tvholaMundo?.setText(response.getString("Lista"))
                DropDownProducto?.threshold = 1
                DropDownProducto?.setAdapter(adapter)
                DropDownProducto?.onItemClickListener = AdapterView.OnItemClickListener {
                       parent, view, position, id ->
                        precioYCantidad(parent?.getItemAtPosition(position).toString())
                }
                binding.etCodigoDeBarraFacturaEntrada.addTextChangedListener(object: TextWatcher {
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
                        binding.tvListaDesplegableElegirProductoFacturaEntrada.setText("",false)
                        DropDownProducto?.showDropDown()
                    }
                }
                DropDownProducto?.setOnFocusChangeListener { _, hasFocus ->
                    if(hasFocus && DropDownProducto?.text.toString() == "Eliga una opción"){
                        binding.tvListaDesplegableElegirProductoFacturaEntrada.setText("",false)
                        DropDownProducto?.showDropDown()
                    }
                    encontrarProducto = sharedViewModel.opcionesListEntrada.any { item ->
                        val trimmedItem = item.substringBefore(" (")
                        // Log.i("ListaDesplegableAnadir", "${sharedViewModel.opcionesListAnadir}, $trimmedItem , ${DropDownProducto?.text.toString().uppercase()}")
                        trimmedItem == DropDownProducto?.text.toString().substringBefore(" (").uppercase()
                    }
                    //Si es un valor no válido con los parentesis
                    if((binding.tvListaDesplegableElegirProductoFacturaEntrada.text.toString().contains("(") &&
                                binding.tvListaDesplegableElegirProductoFacturaEntrada.text.toString().contains(")"))) {
                        if (!hasFocus && !sharedViewModel.opcionesListEntrada.contains(
                                DropDownProducto?.text.toString())) {
                            Toast.makeText(requireContext(), "El nombre del producto no es válido", Toast.LENGTH_SHORT).show()
                        }
                        //Si es un valor no válido sin los parentesis
                    }else if(!binding.tvListaDesplegableElegirProductoFacturaEntrada.text.toString().contains("(") &&
                        !binding.tvListaDesplegableElegirProductoFacturaEntrada.text.toString().contains(")")){

                        if(!hasFocus && !encontrarProducto){
                            Toast.makeText(requireContext(), "El nombre del producto no es válido", Toast.LENGTH_SHORT).show()
                        }
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
                parametros.put("ALMACEN", sharedViewModel.almacenAnadir)
                return parametros
            }
        }
        queue1.add(jsonObjectRequest)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.CodigoDeBarraAnadir.observe(viewLifecycleOwner) { newText ->
            binding.etCodigoDeBarraFacturaEntrada.setText(newText)
            codigoDeBarra()
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
        //  sharedViewModel.ListaDeProveedoresFacturaEntrada.add(bundle.getString("Proveedor")!!) }
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 = "http://186.64.123.248/FacturaEntrada/precioYCantidad.php"
        val jsonObjectRequest1 = object : StringRequest(
            Request.Method.POST, url1,
            { response ->
                val precio = JSONObject(response).getString("Precio")
                val unidades = JSONObject(response).getString("Unidades de embalaje")
                // binding.etPrecioPorUnidadUnidades.setText("200")
                //binding.etCantidad.setText("12")
                if (binding.llCajasDeProductoElegirProductoFacturaEntrada.isVisible) {
                    binding.etArticulosPorCajaFacturaEntrada.setText(unidades)
                }
                binding.etPrecioFacturaEntrada.setText(precio)
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
                parametros.put("PROVEEDOR", sharedViewModel.proveedorAnadir)
                parametros.put("PRODUCTO", producto)
                return parametros

            }
        }
        queue1.add(jsonObjectRequest1)
    }

    private fun codigoDeBarra(){
        if (binding.etCodigoDeBarraFacturaEntrada.text.isNotBlank()) {
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
                            binding.tvListaDesplegableElegirProductoFacturaEntrada.postDelayed({
                                binding.tvListaDesplegableElegirProductoFacturaEntrada.setSelection(sharedViewModel.opcionesListEntrada.indexOf("$codigoBarraProducto ( 0 unid. )"))
                                binding.tvListaDesplegableElegirProductoFacturaEntrada.setText("$codigoBarraProducto ( 0 unid. )",false)
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
                        binding.etCodigoDeBarraFacturaEntrada.text.toString()
                    )
                    return parametros
                }
            }
            queue.add(jsonObjectRequest)
        }
    }
   //Cuando no hay precio de compra para ese producto por ese cliente se llama esta función
    //El problema radica en que esta función no es llamada cuando esta el producto sin los parentesis
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
                if (binding.llCajasDeProductoElegirProductoFacturaEntrada.isVisible) {
                    binding.etArticulosPorCajaFacturaEntrada.setText(unidades)
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

    fun eliminarElementoDeListaDesplegableSinParentesis(nombre: String) {
        // Filtra la lista para encontrar el elemento a eliminar
        val itemToRemove = sharedViewModel.opcionesListEntrada.find { it.startsWith(nombre) }
        Log.i("eliminarElementoDeListaDesplegableSinParentesis", "$nombre ,$itemToRemove, ${sharedViewModel.opcionesListEntrada}")
        // Si se encuentra el elemento, eliminarlo
        if (itemToRemove != null) {
            sharedViewModel.opcionesListEntrada.remove(itemToRemove)
            // Notificar al adaptador que los datos han cambiado
            adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sharedViewModel.opcionesListEntrada)
            DropDownProducto?.threshold = 1
            DropDownProducto?.setAdapter(adapter)
            adapter!!.notifyDataSetChanged()

        }

    }

    fun eliminarElementoDeListaDesplegableConParentesis(nombre: String) {
        // Filtra la lista para encontrar el elemento a eliminar
        // Si se encuentra el elemento, eliminarlo
        Log.i("eliminarElementoDeListaDesplegableConParentesis", "$nombre , ${sharedViewModel.opcionesListEntrada}")
        sharedViewModel.opcionesListEntrada.remove(nombre)
        Log.i("eliminarElementoDeListaDesplegableConParentesisDespues", "$nombre , ${sharedViewModel.opcionesListEntrada}")
        // Notificar al adaptador que los datos han cambiado
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sharedViewModel.opcionesListEntrada)
        DropDownProducto?.threshold = 1
        DropDownProducto?.setAdapter(adapter)
        adapter!!.notifyDataSetChanged()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}