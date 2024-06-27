package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.facturaSalida.editarFacturaSalida

import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentEditarFacturaEntradaBinding
import com.seba.mitantonavigationdrawer.databinding.FragmentEditarFacturaSalidaBinding
import com.seba.mitantonavigationdrawer.databinding.FragmentEditarTransferenciaBinding
import com.seba.mitantonavigationdrawer.databinding.FragmentEstadisticaBinding
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirInventario.AnadirInventarioAdapter
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirInventario.ElegirProductoAnadirFragment
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.TransaccionesFragmentArgs
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.facturaEntrada.editarFacturaEntrada.EditarFacturaEntradaViewModel
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.facturaEntrada.editarFacturaEntrada.ElegirProductoFacturaEntradaFragment
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transferencias.TransferenciasFragmentArgs
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transferencias.editarTransferencia.ElegirProductoEditarTransferenciaFragment
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import com.seba.mitantonavigationdrawer.ui.estadística.EstadisticaViewModel
import com.seba.mitantonavigationdrawer.ui.removerInventario.RemoverInventarioAdapter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import org.json.JSONArray
import org.json.JSONObject
import java.util.Date

class EditarFacturaSalidaFragment : Fragment(R.layout.fragment_editar_factura_salida) {
    private var _binding: FragmentEditarFacturaSalidaBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel by activityViewModels<SharedViewModel>()
    private val args: TransaccionesFragmentArgs by navArgs()
    var TextNombre: EditText? = null
    var TextFecha: EditText? = null
    var TextComentarios: EditText? = null
    var DropDownCliente: AutoCompleteTextView? = null
    var DropDownAlmacen: AutoCompleteTextView? = null
    private lateinit var adapter: RemoverInventarioAdapter
    private var requestCamara: ActivityResultLauncher<String>? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val editarFacturaSalidaViewModel =
            ViewModelProvider(this).get(EditarFacturaSalidaViewModel::class.java)

        _binding = FragmentEditarFacturaSalidaBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa
        TextNombre = binding.etNombreFacturaSalidaEditar.findViewById(R.id.etNombreFacturaSalidaEditar)
        TextFecha = binding.etFacturaSalidaFechaTransaccionEditar.findViewById(R.id.etFacturaSalidaFechaTransaccionEditar)
        TextComentarios = binding.etFacturaSalidaComentariosEditar.findViewById(R.id.etFacturaSalidaComentariosEditar)
        DropDownCliente = binding.tvListaDesplegableClienteEditar.findViewById(R.id.tvListaDesplegableClienteEditar)
        DropDownAlmacen = binding.tvListaDesplegableAlmacenEditar.findViewById(R.id.tvListaDesplegableAlmacenEditar)

        TextNombre?.getBackground()
            ?.setColorFilter(getResources().getColor(R.color.color_list), PorterDuff.Mode.SRC_ATOP)
        TextFecha?.getBackground()
            ?.setColorFilter(getResources().getColor(R.color.color_list), PorterDuff.Mode.SRC_ATOP)
        TextComentarios?.getBackground()
            ?.setColorFilter(getResources().getColor(R.color.color_list), PorterDuff.Mode.SRC_ATOP)

        ListaDesplegableCliente()
        ListaDesplegableAlmacen()

        /*binding.nsvElegirProductoEditar.isVisible = true
        binding.bAnadirNuevoProductoEditar.setOnClickListener {
            if (DropDownProveedor?.text.toString() != "Eliga una opción" && DropDownAlmacen?.text.toString() != "Eliga una opción") {
                val elegirProductoEditarTransferenciaFragment = ElegirProductoEditarTransferenciaFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.clEditarTransferencia, elegirProductoEditarTransferenciaFragment)
                    .commit()
                binding.tvProductosAnadidosEditar.isVisible = true
             }
        }*/
        if (TextFecha?.text?.isBlank() == true) {
            val fecha =  SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Calendar.getInstance().time)
            TextFecha?.setText(fecha.format(Date()))
        }
        requestCamara = registerForActivityResult(ActivityResultContracts.RequestPermission(),) {
            if (it) {
                findNavController().navigate(R.id.action_nav_editar_factura_salida_to_nav_barcode_scan_factura_salida)
            } else {
                Toast.makeText(requireContext(), "Permiso denegado", Toast.LENGTH_LONG).show()
            }
        }
        binding.bEscanearCodigoDeBarraFacturaSalidaEditar.setOnClickListener {
            requestCamara?.launch(android.Manifest.permission.CAMERA)
        }
        recyclerViewElegirProducto()
        binding.bActualizarRecyclerViewFacturaSalidaEditar.setOnClickListener {
            binding.tvProductosAnadidosFacturaSalidaEditar.isVisible =
                !(sharedViewModel.listaDeCantidadesRemover.size == 0 || sharedViewModel.listaDeProductosRemover.size == 0 || sharedViewModel.listaDePreciosRemover.size == 0)
            binding.llMontoFacturaSalidaEditar.isVisible =
                !(sharedViewModel.listaDeCantidadesRemover.size == 0 || sharedViewModel.listaDeProductosRemover.size == 0 || sharedViewModel.listaDePreciosRemover.size == 0)
            adapter.notifyDataSetChanged()
            Handler(Looper.getMainLooper()).postDelayed({
                binding.tvMontoFacturaSalidaEditar.text =
                    sharedViewModel.listaDePreciosDeProductosRemover.sum().toString()
            }, 300)
            sharedViewModel.listaDePreciosDeProductosRemover.clear()
            binding.rvElegirProductoFacturaSalidaEditar.requestLayout()
        }
        binding.nsvElegirProductoFacturaSalidaEditar.isVisible = true
        binding.llMontoFacturaSalidaEditar.isVisible = true
        binding.bAnadirNuevoProductoFacturaSalidaEditar.setOnClickListener {
            if (DropDownCliente?.text.toString() != "Eliga una opción" && DropDownAlmacen?.text.toString() != "Eliga una opción") {
                sharedViewModel.clienteRemover = DropDownCliente?.text.toString()
                sharedViewModel.almacenRemover = DropDownAlmacen?.text.toString()
                binding.nsvElegirProductoFacturaSalidaEditar.isVisible = true
                binding.llMontoFacturaSalidaEditar.isVisible = true
                //setFragmentResult("Proveedor", bundleOf("Proveedor" to DropDownProveedor?.text.toString()))
                //Toast.makeText(requireContext(),"Funciona el traspaso de información", Toast.LENGTH_LONG).show()
                val elegirProductoFacturaSalidaFragment = ElegirProductoFacturaSalidaFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.clFacturaSalidaEditar, elegirProductoFacturaSalidaFragment)
                    .commit()
                binding.tvProductosAnadidosFacturaSalidaEditar.isVisible = true
                // binding.tvProductosAnadidosAnadir.isVisible = !(adapter.listaDeCantidadesAnadir.size == 0 && adapter.listaDeProductosAnadir.size == 0 && adapter.listaDePreciosAnadir.size == 0)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Eliga el proveedor y el cliente",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
        try {
            sharedViewModel.id.add(args.id)
        } catch (e: Exception) {
        }
        binding.FacturaSalidaButtonEnviarEditar.setOnClickListener {
            if (binding.etNombreFacturaSalidaEditar.text.isNotBlank() && binding.tvListaDesplegableAlmacenEditar.text.toString() != "Eliga una opción" && binding.tvListaDesplegableClienteEditar.text.toString() != "Eliga una opción") {
                actualizarFacturaDeEntrada()
            }
        }


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val queue1 = Volley.newRequestQueue(requireContext())
        Log.i("Sebastian", "Valor de Id de destino es: ${sharedViewModel.id.last()}")
        val url1 =
            "http://186.64.123.248/Reportes/Transacciones/FacturaSalida/registroInsertar.php?ID_FACTURA_SALIDA=${sharedViewModel.id.last()}"
        val jsonObjectRequest1 = JsonObjectRequest(
            Request.Method.GET, url1, null,
            { response ->
                Log.i("Sebastian", "ID: ${sharedViewModel.id.last()}")
                TextNombre?.setText(response.getString("FACTURA_SALIDA"))
                TextFecha?.setText(response.getString("FECHA_TRANSACCION"))
                DropDownCliente?.setText(response.getString("CLIENTE"), false)
                DropDownAlmacen?.setText(response.getString("ALMACEN"), false)
                TextComentarios?.setText(response.getString("COMENTARIOS"))

                val ListaDeProductos = response.getJSONArray("PRODUCTOS")

                val productosList = mutableListOf<String>()
                for (i in 0 until ListaDeProductos.length()) {
                    productosList.add(ListaDeProductos.getString(i))
                }

                val jsonArray = JSONArray(ListaDeProductos.toString())
                // Convierte el array JSON a una lista mutable
                for (i in 0 until productosList.size) {
                    val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                    val producto: String = jsonObject.getString("PRODUCTO")
                    val cantidad: String = jsonObject.getString("CANTIDAD")
                    val precioVenta: String = jsonObject.getString("PRECIO_VENTA")
                    Log.i("Sebastian", "PRODUCTO: $producto")
                    Log.i("Sebastian", "CANTIDAD: $cantidad")
                    Log.i("Sebastian", "PRECIO_VENTA: $precioVenta")
                    if (!sharedViewModel.listaDeProductosRemover.contains("$producto ( 0 unid. )")) {
                        sharedViewModel.listaDeProductosRemover.add("$producto ( 0 unid. )")
                        sharedViewModel.listaDeCantidadesRemover.add(cantidad)
                        sharedViewModel.listaDePreciosRemover.add(precioVenta)
                    }
                }

                binding.nsvElegirProductoFacturaSalidaEditar.isVisible = true
                binding.tvProductosAnadidosFacturaSalidaEditar.isVisible =
                    !(adapter.listaDeCantidadesRemover.size == 0 || adapter.listaDeProductosRemover.size == 0 || adapter.listaDePreciosRemover.size == 0)
                binding.llMontoFacturaSalidaEditar.isVisible =
                    !(adapter.listaDeCantidadesRemover.size == 0 || adapter.listaDeProductosRemover.size == 0 || adapter.listaDePreciosRemover.size == 0)
                adapter.notifyDataSetChanged()
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.tvMontoFacturaSalidaEditar.text =
                        sharedViewModel.listaDePreciosDeProductosRemover.sum().toString()
                }, 300)
                sharedViewModel.listaDePreciosDeProductos.clear()
                binding.rvElegirProductoFacturaSalidaEditar.requestLayout()


            }, { error ->
                Toast.makeText(
                    requireContext(),
                    "El id es ${sharedViewModel.id.last()}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
        queue1.add(jsonObjectRequest1)
    }

    fun recyclerViewElegirProducto() {
        adapter = RemoverInventarioAdapter(
            sharedViewModel.listaDeCantidadesRemover,
            sharedViewModel.listaDeProductosRemover,
            sharedViewModel.listaDePreciosRemover,
            sharedViewModel
        ) { position ->
            onDeletedItem(position)
        }
        binding.rvElegirProductoFacturaSalidaEditar.setHasFixedSize(true)
        binding.rvElegirProductoFacturaSalidaEditar.adapter = adapter
        binding.rvElegirProductoFacturaSalidaEditar.layoutManager = LinearLayoutManager(requireContext())
        adapter.updateList(
            sharedViewModel.listaDeCantidadesRemover,
            sharedViewModel.listaDeProductosRemover,
            sharedViewModel.listaDePreciosRemover)
        adapter.notifyDataSetChanged()
        binding.rvElegirProductoFacturaSalidaEditar.requestLayout()
    }

    private fun onDeletedItem(position: Int) {
        //sharedViewModel.opcionesListSalida.add(position,sharedViewModel.reponedorListSalida[position])
        sharedViewModel.listaDeCantidadesRemover.removeAt(position)
        sharedViewModel.listaDeProductosRemover.removeAt(position)
        sharedViewModel.listaDePreciosRemover.removeAt(position)
        adapter.notifyItemRemoved(position)
        adapter.notifyDataSetChanged()
        binding.rvElegirProductoFacturaSalidaEditar.requestLayout()
        Handler(Looper.getMainLooper()).postDelayed({
            binding.tvMontoFacturaSalidaEditar.text = sharedViewModel.listaDePreciosDeProductosRemover.sum().toString()
        }, 300)
        sharedViewModel.listaDePreciosDeProductosRemover.clear()
        binding.tvProductosAnadidosFacturaSalidaEditar.isVisible =
            !(adapter.listaDeCantidadesRemover.size == 0 || adapter.listaDeProductosRemover.size == 0 || adapter.listaDePreciosRemover.size == 0)
        binding.llMontoFacturaSalidaEditar.isVisible =
            !(adapter.listaDeCantidadesRemover.size == 0 || adapter.listaDeProductosRemover.size == 0 || adapter.listaDePreciosRemover.size == 0)
    }

    private fun actualizarFacturaDeEntrada() {
        val queue = Volley.newRequestQueue(requireContext())
        val url = "http://186.64.123.248/Reportes/Transacciones/FacturaSalida/registro.php"
        val jsonObjectRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                if (TextNombre?.text.toString().isNotBlank()) {
                    val id = JSONObject(response).getString("ID_FACTURA_SALIDA")
                    //Aqui va el código para validar el almacen
                    val url1 = "http://186.64.123.248/Reportes/Transacciones/FacturaSalida/actualizarFacturaSalida.php" // Reemplaza esto con tu URL de la API
                    val queue1 = Volley.newRequestQueue(requireContext())
                    val stringRequest = object : StringRequest(
                        Request.Method.POST,
                        url1,
                        { response ->
                            if (binding.etNombreFacturaSalidaEditar.text.isNotBlank() && binding.tvListaDesplegableAlmacenEditar.text.toString() != "Eliga una opción" && binding.tvListaDesplegableClienteEditar.text.toString() != "Eliga una opción") {
                                Handler(Looper.getMainLooper()).postDelayed({
                                    InsertarPreciosYCantidades()
                                }, 2500)
                                Handler(Looper.getMainLooper()).postDelayed({
                                    modificarInventario()
                                }, 5000)
                                binding.tvProductosAnadidosFacturaSalidaEditar.isVisible =
                                    !(adapter.listaDeCantidadesRemover.size == 0 && adapter.listaDeProductosRemover.size == 0 && adapter.listaDePreciosRemover.size == 0)
                            }

                            Toast.makeText(
                                requireContext(),
                                "Transferencia actualizada exitosamente. El id de ingreso es el número $id ",
                                Toast.LENGTH_LONG
                            ).show()


                        },
                        { error ->
                            Toast.makeText(requireContext(), "$error", Toast.LENGTH_LONG).show()
                            //Toast.makeText(requireContext(),"Error $error", Toast.LENGTH_LONG).show()
                        }
                    ) {
                        override fun getParams(): MutableMap<String, String> {
                            val parametros = HashMap<String, String>()
                            parametros.put("ID_FACTURA_SALIDA", id.toString())
                            parametros.put("FACTURA_SALIDA", TextNombre?.text.toString().uppercase())
                            parametros.put("CLIENTE", DropDownCliente?.text.toString())
                            parametros.put("ALMACEN", DropDownAlmacen?.text.toString())
                            parametros.put("FECHA_TRANSACCION", TextFecha?.text.toString())
                            parametros.put("COMENTARIOS", TextComentarios?.text.toString().uppercase())
                            /* parametros.put("NUMERO_DE_PRODUCTOS",sharedViewModel.listaDeProductos.size.toString())
                            for (i in 0..<sharedViewModel.listaDeProductos.size) {
                                parametros["PRODUCTO$i"] = sharedViewModel.listaDeProductos[i].uppercase()
                                parametros["CANTIDAD$i"] = sharedViewModel.listaDeCantidades[i]
                            }*/
                            return parametros
                        }
                    }
                    queue1.add(stringRequest)

                    /*   } else if (unico == "0") {
                           //VolleyError("El almacén ya se encuentra en la base de datos")
                           //queue1.cancelAll(TAG)
                           //jsonObjectRequest1.cancel()
                           Toast.makeText(
                               requireContext(),
                               "La transferencia ya se encuentra en la base de datos",
                               Toast.LENGTH_LONG
                           ).show()
                       }*/
                } else {
                    Toast.makeText(
                        requireContext(),
                        "El nombre de la factura de salida es obligatorio",
                        Toast.LENGTH_LONG
                    ).show()
                }

                // TextId?.setText(response.getString("ID_ALMACEN"))
                // Toast.makeText(requireContext(),"Id ingresado correctamente al formulario.", Toast.LENGTH_LONG).show()
            }, { error ->
                Toast.makeText(
                    requireContext(),
                    "Conecte la aplicación al servidor",
                    Toast.LENGTH_LONG
                ).show()
                //Toast.makeText(requireContext(),"Error $error", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("FACTURA_SALIDA", TextNombre?.text.toString().uppercase())
                return parametros
            }
        }
        queue.add(jsonObjectRequest)
    }

    private fun InsertarPreciosYCantidades() {
        //INICIO EXPERIMIENTO!!!!!!!!!!!!!!!!!!!!!!!!!! (FUNCIONO)
        val queue = Volley.newRequestQueue(requireContext())
        val url = "http://186.64.123.248/Reportes/Transacciones/FacturaSalida/registroProductos.php"
        val jsonObjectRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                if (TextNombre?.text.toString().isNotBlank()) {
                    val id = JSONObject(response).getString("ID_FACTURA_SALIDA")
                    //unico = 1
                    //no unico = 0
                    //Aqui va el código para validar el almacen
                    val url1 =
                        "http://186.64.123.248/Reportes/Transacciones/FacturaSalida/insertarProductosFacturaSalida.php" // Reemplaza esto con tu URL de la API
                    val queue1 = Volley.newRequestQueue(requireContext())
                    val stringRequest = object : StringRequest(
                        Request.Method.POST,
                        url1,
                        { response ->
                            Toast.makeText(
                                requireContext(),
                                "Productos agregados exitosamente",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        { error ->
                            Toast.makeText(
                                requireContext(),
                                "Productos agregados exitosamente",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    ) {
                        override fun getParams(): MutableMap<String, String> {
                            val parametros = HashMap<String, String>()
                            parametros.put("ID_FACTURA_SALIDA", id.toString())
                            parametros.put("NUMERO_DE_PRODUCTOS", sharedViewModel.listaDeProductosRemover.size.toString())
                            for (i in 0..<sharedViewModel.listaDeProductosRemover.size) {
                                parametros["PRODUCTO$i"] = sharedViewModel.listaDeProductosRemover[i].uppercase()
                                parametros["CANTIDAD$i"] = sharedViewModel.listaDeCantidadesRemover[i]
                                parametros["PRECIO_VENTA$i"] = sharedViewModel.listaDePreciosRemover[i]}
                            return parametros
                        }
                    }
                    queue1.add(stringRequest)

                }

                // TextId?.setText(response.getString("ID_ALMACEN"))
                // Toast.makeText(requireContext(),"Id ingresado correctamente al formulario.", Toast.LENGTH_LONG).show()
            }, { error ->
                //Toast.makeText(requireContext(),"Conecte la aplicación al servidor", Toast.LENGTH_LONG).show()
                //Toast.makeText(requireContext(),"Error $error", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("FACTURA_SALIDA", TextNombre?.text.toString().uppercase())
                return parametros
            }
        }
        queue.add(jsonObjectRequest)
    }

    fun modificarInventario() {
        //Si el inventario es menor que la transferencia que no la haga y si el almacen de destino no existe, que lo cree
        val url1 =
            "http://186.64.123.248/Reportes/Transacciones/FacturaSalida/removerInventarioFacturaSalida.php" // Reemplaza esto con tu URL de la API
        val queue1 = Volley.newRequestQueue(requireContext())
        val stringRequest = object : StringRequest(
            Request.Method.POST,
            url1,
            { response ->
                Toast.makeText(
                    requireContext(),
                    "Inventario modificado exitosamente",
                    Toast.LENGTH_SHORT
                ).show()
                TextNombre?.setText("")
                TextFecha?.setText(
                    SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(
                        Calendar.getInstance().time
                    )
                )
                DropDownCliente?.setText("Eliga una opción", false)
                DropDownAlmacen?.setText("Eliga una opción", false)
                TextComentarios?.setText("")
                sharedViewModel.listaDeCantidadesRemover.removeAll(sharedViewModel.listaDeCantidadesRemover)
                sharedViewModel.listaDeProductosRemover.removeAll(sharedViewModel.listaDeProductosRemover)
                sharedViewModel.listaDePreciosRemover.removeAll(sharedViewModel.listaDePreciosRemover)
                sharedViewModel.listaDePreciosDeProductosRemover.clear()
                adapter.notifyDataSetChanged()
                binding.rvElegirProductoFacturaSalidaEditar.requestLayout()
                binding.tvProductosAnadidosFacturaSalidaEditar.isVisible = false
                binding.nsvElegirProductoFacturaSalidaEditar.isVisible = false
                binding.tvMontoFacturaSalidaEditar.text =
                    sharedViewModel.listaDePreciosDeProductosRemover.sum().toString()
                binding.llMontoFacturaSalidaEditar.isVisible =
                    !(adapter.listaDeCantidadesRemover.size == 0 || adapter.listaDeProductosRemover.size == 0 || adapter.listaDePreciosRemover.size == 0)

            },
            { error ->
                /*  TextNombre?.setText("")
                          TextFecha?.setText("")
                          DropDownOrigen?.setText("Eliga una opción",false)
                          DropDownDestino?.setText("Eliga una opción",false)
                          TextComentarios?.setText("")
                          TextCodigoDeBarra?.setText("")*/
                Toast.makeText(
                    requireContext(),
                    "Error $error y ${sharedViewModel.listaDeProductos} y ${sharedViewModel.listaDeCantidades}",
                    Toast.LENGTH_LONG
                ).show()
                Log.i(
                    "Sebastian",
                    "$error, ${sharedViewModel.listaDeProductos} y ${sharedViewModel.listaDeCantidades}"
                )
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("NUMERO_DE_PRODUCTOS", sharedViewModel.listaDeProductosRemover.size.toString())
                parametros.put("ALMACEN", DropDownAlmacen?.text.toString())
                for (i in 0..<sharedViewModel.listaDeProductosRemover.size) {
                    parametros["PRODUCTO$i"] = sharedViewModel.listaDeProductosRemover[i].uppercase()
                    parametros["CANTIDAD$i"] = sharedViewModel.listaDeCantidadesRemover[i]
                }

                return parametros
            }
        }
        queue1.add(stringRequest)
    }

    private fun ListaDesplegableAlmacen() {
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 = "http://186.64.123.248/FacturaEntrada/almacen.php"
        val jsonObjectRequest1 = JsonObjectRequest(
            Request.Method.GET, url1, null,
            { response ->
                // Obtén el array de opciones desde el objeto JSON
                val jsonArray = response.getJSONArray("Lista")
                // Convierte el array JSON a una lista mutable
                val opcionesList = mutableListOf<String>()
                for (i in 0 until jsonArray.length()) {
                    opcionesList.add(jsonArray.getString(i).removeSurrounding("'", "'"))
                }
                //Crea un adpatador para el dropdown
                val adapter = ArrayAdapter(requireContext(), R.layout.list_item, opcionesList)
                //binding.tvholaMundo?.setText(response.getString("Lista"))
                DropDownAlmacen?.setAdapter(adapter)

                DropDownAlmacen?.onItemClickListener =
                    AdapterView.OnItemClickListener { parent, view, position, id ->
                        val itemSelected = parent.getItemAtPosition(position)
                    }
            }, { error ->
                Toast.makeText(
                    requireContext(),
                    " La aplicación no se ha conectado con el servidor",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
        queue1.add(jsonObjectRequest1)
    }

    private fun ListaDesplegableCliente() {
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 = "http://186.64.123.248/Reportes/Transacciones/FacturaSalida/listaDesplegableCliente.php"
        val jsonObjectRequest1 = JsonObjectRequest(
            Request.Method.GET, url1, null,
            { response ->
                // Obtén el array de opciones desde el objeto JSON
                val jsonArray = response.getJSONArray("Lista")
                // Convierte el array JSON a una lista mutable
                val opcionesList = mutableListOf<String>()
                for (i in 0 until jsonArray.length()) {
                    opcionesList.add(jsonArray.getString(i).removeSurrounding("'", "'"))
                }
                //Crea un adpatador para el dropdown
                val adapter = ArrayAdapter(requireContext(), R.layout.list_item, opcionesList)
                //binding.tvholaMundo?.setText(response.getString("Lista"))
                DropDownCliente?.setAdapter(adapter)

                DropDownCliente?.onItemClickListener =
                    AdapterView.OnItemClickListener { parent, view, position, id ->
                        val itemSelected = parent.getItemAtPosition(position)
                    }
            }, { error ->
                Toast.makeText(
                    requireContext(),
                    " La aplicación no se ha conectado con el servidor",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
        queue1.add(jsonObjectRequest1)
    }


    override fun onDestroyView() {
        super.onDestroyView()
       /* sharedViewModel.listaDeCantidadesRemover.removeAll(sharedViewModel.listaDeCantidadesRemover)
        sharedViewModel.listaDeProductosRemover.removeAll(sharedViewModel.listaDeProductosRemover)
        sharedViewModel.listaDePreciosRemover.removeAll(sharedViewModel.listaDePreciosRemover)
        sharedViewModel.listaDePreciosDeProductosRemover.clear()*/
        adapter.notifyDataSetChanged()
        binding.rvElegirProductoFacturaSalidaEditar.requestLayout()
        binding.tvProductosAnadidosFacturaSalidaEditar.isVisible = false
        binding.nsvElegirProductoFacturaSalidaEditar.isVisible = false
        binding.tvMontoFacturaSalidaEditar.text = sharedViewModel.listaDePreciosDeProductosRemover.sum().toString()
        binding.llMontoFacturaSalidaEditar.isVisible =
            !(adapter.listaDeCantidadesRemover.size == 0 || adapter.listaDeProductosRemover.size == 0 || adapter.listaDePreciosRemover.size == 0)
        _binding = null
    }
}