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
    private var SegundaVez = false
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

      /* if(!SegundaVez) {
           ListaDesplegableElegirProducto()
           SegundaVez = true
       }*/
        if(sharedViewModel.listaDeAlmacenesSalida.size > 1) {
            for (i in 1..<sharedViewModel.listaDeAlmacenesSalida.size){
                if(sharedViewModel.listaDeAlmacenesSalida[i]==sharedViewModel.listaDeAlmacenesSalida[i-1]){
                    ListaDesplegableElegirProducto()
                }else if(sharedViewModel.listaDeAlmacenesSalida[i]!=sharedViewModel.listaDeAlmacenesSalida[i-1]){
                    sharedViewModel.opcionesListSalida.clear()
                    sharedViewModel.listaDeProductosRemover.clear()
                    sharedViewModel.listaDeCantidadesRemover.clear()
                    sharedViewModel.listaDePreciosRemover.clear()
                    ListaDesplegableElegirProducto()
                }
            }
        }else{
            ListaDesplegableElegirProducto()
        }
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

        binding.bAnadirFacturaSalida.setOnClickListener {
            val queue = Volley.newRequestQueue(requireContext())
            val url = "http://186.64.123.248/FacturaSalida/preguntarInventario.php"
            val jsonObjectRequest = object : StringRequest(
                Request.Method.POST, url,
                { response ->
                    try {
                        if(binding.tvListaDesplegableElegirProductoFacturaSalida.text.toString() != "Eliga una opción") {
                            val cantidadUnidades = JSONObject(response).getString("Cantidad")
                            if (binding.llUnidadesElegirProductoFacturaSalida.isVisible) {
                                if(binding.etCantidadFacturaSalida.text.isNotBlank()){
                                    if (cantidadUnidades.toInt() < binding.etCantidadFacturaSalida.text.toString().toInt()) {
                                        val inflaterRemover = requireActivity().layoutInflater
                                        val layoutRemover = inflaterRemover.inflate(R.layout.toast_custom_remover, null)
                                        val textRemover = layoutRemover.findViewById<TextView>(R.id.text_view_toast_remover)
                                        textRemover.text = "La cantidad es mayor que la cantidad en inventario"
                                        val toast = Toast(requireContext())
                                        toast.duration = Toast.LENGTH_LONG
                                        toast.view = layoutRemover
                                        toast.setGravity(Gravity.BOTTOM, 0, 600)
                                        toast.show()
                                    }else {
                                        sharedViewModel.listaDeProductosRemover.add("${binding.tvListaDesplegableElegirProductoFacturaSalida.text.toString().substringBefore('(')}( 0 unid. )")
                                        sharedViewModel.listaDeCantidadesRemover.add(binding.etCantidadFacturaSalida.text.toString())
                                        sharedViewModel.listaDePreciosRemover.add(binding.etPrecioFacturaSalida.text.toString())
                                        //  refreshAdapterAnadirTransferenciaFragment()
                                        binding.tvListaDesplegableElegirProductoFacturaSalida.setText("Eliga una opción", false)
                                        binding.etCantidadFacturaSalida.setText("")
                                        binding.etPrecioFacturaSalida.setText("")
                                        sharedViewModel.opcionesListSalida.removeAll { elemento ->
                                            val elementoABuscar = "${elemento.substringBefore('(')}( 0 unid. )"
                                            sharedViewModel.listaDeProductosRemover.contains(elementoABuscar)
                                        }
                                        // sharedViewModel.opcionesListRemover.removeAll(sharedViewModel.listaDeProductosRemover)
                                        Toast.makeText(
                                            requireContext(),
                                            "Se ha agregado el producto a la factura",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }else{
                                    Toast.makeText(requireContext(),"Falta ingresar la cantidad", Toast.LENGTH_SHORT).show()
                                }
                            } else if (binding.llCajasDeProductoElegirProductoFacturaSalida.isVisible) {
                                if (binding.etNumeroDeCajasFacturaSalida.text.isNotBlank() && binding.etArticulosPorCajaFacturaSalida.text.isNotBlank()) {
                                    if (cantidadUnidades.toInt() < binding.etNumeroDeCajasFacturaSalida.text.toString().toInt()*binding.etArticulosPorCajaFacturaSalida.text.toString().toInt()) {
                                        val inflaterRemover = requireActivity().layoutInflater
                                        val layoutRemover = inflaterRemover.inflate(R.layout.toast_custom_remover, null)
                                        val textRemover = layoutRemover.findViewById<TextView>(R.id.text_view_toast_remover)
                                        textRemover.text = "La cantidad es mayor que la cantidad en inventario"
                                        val toast = Toast(requireContext())
                                        toast.duration = Toast.LENGTH_LONG
                                        toast.view = layoutRemover
                                        toast.setGravity(Gravity.BOTTOM, 0, 600)
                                        toast.show()
                                    }else {
                                        val cantidad =
                                            binding.etNumeroDeCajasFacturaSalida.text.toString().toInt()
                                                .times(
                                                    binding.etArticulosPorCajaFacturaSalida.text.toString()
                                                        .toInt()
                                                )
                                        sharedViewModel.listaDeProductosRemover.add("${binding.tvListaDesplegableElegirProductoFacturaSalida.text.toString().substringBefore('(')}( 0 unid. )")
                                        sharedViewModel.listaDeCantidadesRemover.add(cantidad.toString())
                                        sharedViewModel.listaDePreciosRemover.add(binding.etPrecioFacturaSalida.text.toString())
                                        // refreshAdapterAnadirTransferenciaFragment()
                                        binding.tvListaDesplegableElegirProductoFacturaSalida.setText("Eliga una opción", false)
                                        binding.etArticulosPorCajaFacturaSalida.setText("")
                                        binding.etNumeroDeCajasFacturaSalida.setText("")
                                        binding.etPrecioFacturaSalida.setText("")
                                        sharedViewModel.opcionesListSalida.removeAll { elemento ->
                                            val elementoABuscar = "${elemento.substringBefore('(')}( 0 unid. )"
                                            sharedViewModel.listaDeProductosRemover.contains(elementoABuscar)
                                        }
                                        //sharedViewModel.opcionesListRemover.removeAll(sharedViewModel.listaDeProductosRemover)
                                        Toast.makeText(
                                            requireContext(),
                                            "Se ha agregado el producto a la factura",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                } else {
                                    Toast.makeText(requireContext(), "Falta ingresar al menos los articulos por caja o el número de cajas", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(requireContext(), "No se ha podido ingresar la factura", Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            Toast.makeText(requireContext(),"No olvide elegir el producto", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "La excepcion es $e", Toast.LENGTH_LONG)
                            .show()
                        Log.i("Sebastián", "$e")
                    }
                },
                { error -> //Toast.makeText(requireContext(), "El error es $error", Toast.LENGTH_LONG).show()
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


            /*  if(binding.tvListaDesplegableElegirProductoFacturaSalida.text.toString() != "Eliga una opción") {
                  if (binding.llUnidadesElegirProductoFacturaSalida.isVisible) {
                      if(binding.etCantidadFacturaSalida.text.isNotBlank()){
                          sharedViewModel.listaDeProductosRemover.add("${binding.tvListaDesplegableElegirProductoFacturaSalida.text.toString().substringBefore('(')}( 0 unid. )")
                          sharedViewModel.listaDeCantidadesRemover.add(binding.etCantidadFacturaSalida.text.toString())
                          sharedViewModel.listaDePreciosRemover.add(binding.etPrecioFacturaSalida.text.toString())
                          //  refreshAdapterFacturaEntradaTransferenciaFragment()
                          binding.tvListaDesplegableElegirProductoFacturaSalida.setText("Eliga una opción",false)
                          binding.etCantidadFacturaSalida.setText("")
                          binding.etPrecioFacturaSalida.setText("")
                          //crear un loop para remover con ( 0 unid. ) o usar otra opción a remove
                          sharedViewModel.opcionesListSalida.removeAll { elemento ->
                              val elementoABuscar = "${elemento.substringBefore('(')}( 0 unid. )"
                              sharedViewModel.listaDeProductosRemover.contains(elementoABuscar)
                          }
                          //sharedViewModel.opcionesListSalida.removeAll(sharedViewModel.listaDeProductosRemover)
                          Toast.makeText(
                              requireContext(),
                              "Se ha agregado el producto a la factura",
                              Toast.LENGTH_LONG
                          ).show()

                      }else{
                          Toast.makeText(requireContext(),"Falta ingresar la cantidad", Toast.LENGTH_SHORT).show()
                      }
                  } else if (binding.llCajasDeProductoElegirProductoFacturaSalida.isVisible) {
                      if (binding.etNumeroDeCajasFacturaSalida.text.isNotBlank() && binding.etArticulosPorCajaFacturaSalida.text.isNotBlank()) {
                          val cantidad = binding.etNumeroDeCajasFacturaSalida.text.toString().toInt()
                              .times(binding.etArticulosPorCajaFacturaSalida.text.toString().toInt())
                          sharedViewModel.listaDeProductosRemover.add("${binding.tvListaDesplegableElegirProductoFacturaSalida.text.toString().substringBefore('(')}( 0 unid. )")
                          sharedViewModel.listaDeCantidadesRemover.add(cantidad.toString())
                          sharedViewModel.listaDePreciosRemover.add(binding.etPrecioFacturaSalida.text.toString())
                          // refreshAdapterFacturaEntradaTransferenciaFragment()
                          binding.tvListaDesplegableElegirProductoFacturaSalida.setText("Eliga una opción",false)
                          binding.etArticulosPorCajaFacturaSalida.setText("")
                          binding.etNumeroDeCajasFacturaSalida.setText("")
                          binding.etPrecioFacturaSalida.setText("")
                          sharedViewModel.opcionesListSalida.removeAll { elemento ->
                              val elementoABuscar = "${elemento.substringBefore('(')}( 0 unid. )"
                              sharedViewModel.listaDeProductosRemover.contains(elementoABuscar)
                          }
                         // sharedViewModel.opcionesListSalida.removeAll(sharedViewModel.listaDeProductosRemover)
                          Toast.makeText(
                              requireContext(), "Se ha agregado el producto a la factura",
                              Toast.LENGTH_LONG).show()

                      } else {
                          Toast.makeText(requireContext(), "Falta ingresar al menos los articulos por caja o el número de cajas", Toast.LENGTH_SHORT).show()
                      }
                  } else {
                      Toast.makeText(requireContext(), "No se ha podido ingresar la factura", Toast.LENGTH_SHORT).show()
                  }
              }else{
                  Toast.makeText(requireContext(),"No olvide elegir el producto", Toast.LENGTH_SHORT).show()
              }*/
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

               /* for (i in 0..100){
                    if(sharedViewModel.reponedorListSalida[i] != "Hola")
                      sharedViewModel.opcionesListSalida.remove(sharedViewModel.reponedorListSalida[i])
                }*/
                //Crea un adpatador para el dropdown
                val adapter = ArrayAdapter(requireContext(),R.layout.list_item,sharedViewModel.opcionesListSalida)
                //binding.tvholaMundo?.setText(response.getString("Lista"))
                DropDownProducto?.setAdapter(adapter)
                DropDownProducto?.onItemClickListener = AdapterView.OnItemClickListener {
                        parent, view, position, id ->
                        precioYCantidad(parent.getItemAtPosition(position).toString())
                }
                binding.etCodigoDeBarraFacturaSalida.addTextChangedListener(object: TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        if(s != null && s.length == 13) {
                            codigoDeBarra()
                            Handler(Looper.getMainLooper()).postDelayed({
                                if (DropDownProducto?.text.toString() != "Eliga una opción") {
                                    precioYCantidad(DropDownProducto?.text.toString())
                                }
                            }, 300)
                        }
                    }

                })
            },
            { error ->
                Toast.makeText(requireContext(), " La aplicación no se ha conectado con el servidor", Toast.LENGTH_LONG).show()
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
            codigoDeBarra()
            Handler(Looper.getMainLooper()).postDelayed({
                if (DropDownProducto?.text.toString() != "Eliga una opción") {
                    precioYCantidad(DropDownProducto?.text.toString())
                }
            }, 300)
        }

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
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                { error ->
                    Toast.makeText(
                        requireContext(),
                        "No hay producto o embalaje asociado a este código de barra, error: $error",
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
                //binding.tvListaDesplegableElegirProveedor.setText("Eliga una opción",false)
                // binding.tvListaDesplegableElegirProducto.setText("Eliga una opción",false)

            }, { error ->
                /*if(binding.tvListaDesplegableElegirProveedor.text.toString() != "Eliga una opción" &&
            binding.tvListaDesplegableElegirProducto.text.toString() != "Eliga una opción") {*/
                Toast.makeText(requireContext(), "No se ha podido cargar los parametros $error", Toast.LENGTH_LONG).show()
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
        val queue = Volley.newRequestQueue(requireContext())
        val url = "http://186.64.123.248/FacturaSalida/preguntarInventario.php"
        val jsonObjectRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                try {
                    val cantidad = JSONObject(response).getString("Cantidad")
                    if(binding.llUnidadesElegirProductoFacturaSalida.isVisible) {
                        if (cantidad.toInt() < binding.etCantidadFacturaSalida.text.toString().toInt()) {
                            val inflaterRemover = requireActivity().layoutInflater
                            val layoutRemover = inflaterRemover.inflate(R.layout.toast_custom_remover, null)
                            val textRemover = layoutRemover.findViewById<TextView>(R.id.text_view_toast_remover)
                            textRemover.text = "La cantidad es mayor que la cantidad en inventario"
                            val toast = Toast(requireContext())
                            toast.duration = Toast.LENGTH_LONG
                            toast.view = layoutRemover
                            toast.setGravity(Gravity.BOTTOM, 0, 600)
                            toast.show()
                        }
                    }
                    else if(binding.llCajasDeProductoElegirProductoFacturaSalida.isVisible) {
                        if(binding.etNumeroDeCajasFacturaSalida.text.isNotBlank() && binding.etArticulosPorCajaFacturaSalida.text.isNotBlank()) {
                            if (cantidad.toInt() < binding.etNumeroDeCajasFacturaSalida.text.toString().toInt()
                                    .times(binding.etArticulosPorCajaFacturaSalida.text.toString().toInt())
                            ) {
                                val inflaterRemover = requireActivity().layoutInflater
                                val layoutRemover = inflaterRemover.inflate(R.layout.toast_custom_remover, null)
                                val textRemover = layoutRemover.findViewById<TextView>(R.id.text_view_toast_remover)
                                textRemover.text = "La cantidad es mayor que la cantidad en inventario"
                                val toast = Toast(requireContext())
                                toast.duration = Toast.LENGTH_LONG
                                toast.view = layoutRemover
                                toast.setGravity(Gravity.BOTTOM, 0, 600)
                                toast.show()
                            }
                        }
                    }
                }
                catch(e: Exception){
                    Toast.makeText(requireContext(), "La excepcion es $e",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.i("Sebastián", "$e")
                }
            },
            { error -> //Toast.makeText(requireContext(), "El error es $error", Toast.LENGTH_LONG).show()
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}