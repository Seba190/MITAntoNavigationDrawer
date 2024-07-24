package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.facturaEntrada.editarFacturaEntrada

import android.app.AlertDialog
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
import android.widget.ImageView
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
import com.seba.mitantonavigationdrawer.databinding.FragmentEditarTransferenciaBinding
import com.seba.mitantonavigationdrawer.databinding.FragmentEstadisticaBinding
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirInventario.AnadirInventarioAdapter
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirInventario.ElegirProductoAnadirFragment
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.TransaccionesFragmentArgs
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transferencias.TransferenciasFragmentArgs
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transferencias.editarTransferencia.ElegirProductoEditarTransferenciaFragment
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import com.seba.mitantonavigationdrawer.ui.estadística.EstadisticaViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import org.json.JSONArray
import org.json.JSONObject
class EditarFacturaEntradaFragment : Fragment(R.layout.fragment_editar_factura_entrada) {
    private var _binding: FragmentEditarFacturaEntradaBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel by activityViewModels<SharedViewModel>()
    private val args: TransaccionesFragmentArgs by navArgs()
    var TextNombre: EditText? = null
    var TextFecha: EditText? = null
    var TextComentarios: EditText? = null
    var DropDownProveedor: AutoCompleteTextView? = null
    var DropDownAlmacen: AutoCompleteTextView? = null
    var EliminarFacturaEntrada: ImageView? = null
    private lateinit var adapter: AnadirInventarioAdapter
    private var requestCamara: ActivityResultLauncher<String>? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val editarFacturaEntradaViewModel =
            ViewModelProvider(this).get(EditarFacturaEntradaViewModel::class.java)

        _binding = FragmentEditarFacturaEntradaBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa
         TextNombre = binding.etNombreFacturaEntradaEditar.findViewById(R.id.etNombreFacturaEntradaEditar)
         TextFecha = binding.etFechaTransaccionEditar.findViewById(R.id.etFechaTransaccionEditar)
         EliminarFacturaEntrada = binding.ivEliminarFacturaEntrada.findViewById(R.id.ivEliminarFacturaEntrada)
         TextComentarios = binding.etFacturaEntradaComentariosEditar.findViewById(R.id.etFacturaEntradaComentariosEditar)
         DropDownProveedor = binding.tvListaDesplegableProveedorEditar.findViewById(R.id.tvListaDesplegableProveedorEditar)
         DropDownAlmacen = binding.tvListaDesplegableAlmacenEditar.findViewById(R.id.tvListaDesplegableAlmacenEditar)

        TextNombre?.getBackground()?.setColorFilter(getResources().getColor(R.color.color_list), PorterDuff.Mode.SRC_ATOP)
        TextFecha?.getBackground()?.setColorFilter(getResources().getColor(R.color.color_list), PorterDuff.Mode.SRC_ATOP)
        TextComentarios?.getBackground()?.setColorFilter(getResources().getColor(R.color.color_list), PorterDuff.Mode.SRC_ATOP)

        EliminarFacturaEntrada?.setOnClickListener {
            mensajeEliminarFacturaEntrada()
        }

        ListaDesplegableProveedor()
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
        if(TextFecha?.text?.isBlank() == true) {
            TextFecha?.setText(SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Calendar.getInstance().time)
            )
        }
        requestCamara = registerForActivityResult(ActivityResultContracts.RequestPermission(),){
            if(it){
                findNavController().navigate(R.id.action_nav_editar_factura_entrada_to_nav_barcode_scan_factura_entrada)
            }else{
                Toast.makeText(requireContext(),"Permiso denegado",Toast.LENGTH_LONG).show()
            }
        }
        binding.bEscanearCodigoDeBarraEditar.setOnClickListener {
            requestCamara?.launch(android.Manifest.permission.CAMERA)
        }


        recyclerViewElegirProducto()
        binding.bActualizarRecyclerViewEditar.setOnClickListener {
            binding.tvProductosAnadidosEditar.isVisible = !(sharedViewModel.listaDeCantidadesAnadir.size == 0 || sharedViewModel.listaDeProductosAnadir.size == 0 || sharedViewModel.listaDePreciosAnadir.size == 0)
            binding.llMontoEditar.isVisible = !(sharedViewModel.listaDeCantidadesAnadir.size == 0 || sharedViewModel.listaDeProductosAnadir.size == 0 || sharedViewModel.listaDePreciosAnadir.size == 0)
            adapter.notifyDataSetChanged()
            Handler(Looper.getMainLooper()).postDelayed({
                binding.tvMontoEditar.text = sharedViewModel.listaDePreciosDeProductos.sum().toString()
            }, 300)
            sharedViewModel.listaDePreciosDeProductos.clear()
            binding.rvElegirProductoEditar.requestLayout()
        }
        binding.nsvElegirProductoEditar.isVisible = true
        binding.llMontoEditar.isVisible = true
        binding.bAnadirNuevoProductoEditar.setOnClickListener {
            if(DropDownProveedor?.text.toString() != "Eliga una opción" && DropDownAlmacen?.text.toString() != "Eliga una opción"){
                sharedViewModel.proveedorAnadir = DropDownProveedor?.text.toString()
                sharedViewModel.almacenAnadir = DropDownAlmacen?.text.toString()
                sharedViewModel.listaDeAlmacenesEntrada.add(DropDownAlmacen?.text.toString())
                binding.nsvElegirProductoEditar.isVisible = true
                binding.llMontoEditar.isVisible = true
                //setFragmentResult("Proveedor", bundleOf("Proveedor" to DropDownProveedor?.text.toString()))
                //Toast.makeText(requireContext(),"Funciona el traspaso de información", Toast.LENGTH_LONG).show()
                val elegirProductoFacturaEntradaFragment = ElegirProductoFacturaEntradaFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.clFacturaEntradaEditar, elegirProductoFacturaEntradaFragment)
                    .commit()
                binding.tvProductosAnadidosEditar.isVisible = true
                // binding.tvProductosAnadidosAnadir.isVisible = !(adapter.listaDeCantidadesAnadir.size == 0 && adapter.listaDeProductosAnadir.size == 0 && adapter.listaDePreciosAnadir.size == 0)
            }else{
                Toast.makeText(requireContext(),"Eliga el proveedor y el cliente", Toast.LENGTH_SHORT).show()
            }

        }
        try {
            sharedViewModel.id.add(args.id)
        }
        catch(e:Exception){
        }
        binding.FacturaEntradaButtonEnviarEditar.setOnClickListener {
            if (sharedViewModel.listaDeProductosAnadir.size > 0 && sharedViewModel.listaDeCantidadesAnadir.size> 0 && sharedViewModel.listaDePreciosAnadir.size > 0) {
                actualizarFacturaDeEntrada()
                adapter.notifyDataSetChanged()
                binding.rvElegirProductoEditar.requestLayout()
            }else{
                Toast.makeText(requireContext(),"Tiene que elegir al menos un producto", Toast.LENGTH_LONG).show()
            }
        }





        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val queue1 = Volley.newRequestQueue(requireContext())
        Log.i("Sebastian", "Valor de Id de destino es: ${sharedViewModel.id.last()}")
        val url1 = "http://186.64.123.248/Reportes/Transacciones/FacturaEntrada/registroInsertar.php?ID_FACTURA_ENTRADA=${sharedViewModel.id.last()}"
        val jsonObjectRequest1 = JsonObjectRequest(
            Request.Method.GET, url1, null,
            { response ->
                Log.i("Sebastian","ID: ${sharedViewModel.id.last()}" )
                TextNombre?.setText(response.getString("FACTURA_ENTRADA"))
                TextFecha?.setText(response.getString("FECHA_TRANSACCION"))
                DropDownProveedor?.setText(response.getString("PROVEEDOR"),false)
                DropDownAlmacen?.setText(response.getString("ALMACEN"),false)
                TextComentarios?.setText(response.getString("COMENTARIOS"))

                val ListaDeProductos = response.getJSONArray("PRODUCTOS")
                if(sharedViewModel.listaDeProductosAnadir.size == 0 && sharedViewModel.listaDeCantidadesAnadir.size == 0
                    && sharedViewModel.listaDePreciosAnadir.size == 0) {
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
                        val precioCompra: String = jsonObject.getString("PRECIO_COMPRA")
                        Log.i("Sebastian", "PRODUCTO: $producto")
                        Log.i("Sebastian", "CANTIDAD: $cantidad")
                        Log.i("Sebastian", "PRECIO_COMPRA: $precioCompra")
                        if (!sharedViewModel.listaDeProductosAnadir.contains("$producto ( 0 unid. )")) {
                            sharedViewModel.listaDeProductosAnadir.add("$producto ( 0 unid. )")
                            sharedViewModel.listaDeCantidadesAnadir.add(cantidad)
                            sharedViewModel.listaDePreciosAnadir.add(precioCompra)
                        }
                    }
                }

                    binding.nsvElegirProductoEditar.isVisible = true
                    binding.tvProductosAnadidosEditar.isVisible = !(adapter.listaDeCantidadesAnadir.size == 0 || adapter.listaDeProductosAnadir.size == 0 || adapter.listaDePreciosAnadir.size == 0)
                    binding.llMontoEditar.isVisible = !(adapter.listaDeCantidadesAnadir.size == 0 || adapter.listaDeProductosAnadir.size == 0 || adapter.listaDePreciosAnadir.size == 0)
                    adapter.notifyDataSetChanged()
                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.tvMontoEditar.text = sharedViewModel.listaDePreciosDeProductos.sum().toString()
                    }, 300)
                    sharedViewModel.listaDePreciosDeProductos.clear()
                    binding.rvElegirProductoEditar.requestLayout()



            }, { error ->
                Toast.makeText(requireContext(), "El id es ${sharedViewModel.id.last()}", Toast.LENGTH_LONG).show()
            }
        )
        queue1.add(jsonObjectRequest1)
    }

    fun recyclerViewElegirProducto(){
        adapter = AnadirInventarioAdapter(sharedViewModel.listaDeCantidadesAnadir,sharedViewModel.listaDeProductosAnadir,sharedViewModel.listaDePreciosAnadir,sharedViewModel) { position ->
            onDeletedItem(position)}
        binding.rvElegirProductoEditar.setHasFixedSize(true)
        binding.rvElegirProductoEditar.adapter = adapter
        binding.rvElegirProductoEditar.layoutManager = LinearLayoutManager(requireContext())
        adapter.updateList(sharedViewModel.listaDeCantidadesAnadir,sharedViewModel.listaDeProductosAnadir,sharedViewModel.listaDePreciosAnadir)
        adapter.notifyDataSetChanged()
        binding.rvElegirProductoEditar.requestLayout()
    }

    private fun onDeletedItem(position: Int){
       // sharedViewModel.opcionesListEntrada.add(position,sharedViewModel.listaDeProductosAnadir[position])
        sharedViewModel.listaDeCantidadesAnadir.removeAt(position)
        sharedViewModel.listaDeProductosAnadir.removeAt(position)
        sharedViewModel.listaDePreciosAnadir.removeAt(position)
        adapter.notifyItemRemoved(position)
        adapter.notifyDataSetChanged()
        binding.rvElegirProductoEditar.requestLayout()
        Handler(Looper.getMainLooper()).postDelayed({
            binding.tvMontoEditar.text = sharedViewModel.listaDePreciosDeProductos.sum().toString()
        }, 300)
        sharedViewModel.listaDePreciosDeProductos.clear()
        binding.tvProductosAnadidosEditar.isVisible = !(adapter.listaDeCantidadesAnadir.size == 0 || adapter.listaDeProductosAnadir.size == 0 || adapter.listaDePreciosAnadir.size == 0)
        binding.llMontoEditar.isVisible = !(adapter.listaDeCantidadesAnadir.size == 0 || adapter.listaDeProductosAnadir.size == 0 || adapter.listaDePreciosAnadir.size == 0)
    }

    private fun actualizarFacturaDeEntrada(){
        val queue = Volley.newRequestQueue(requireContext())
        val url = "http://186.64.123.248/Reportes/Transacciones/FacturaEntrada/registro.php"
        val jsonObjectRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                if (TextNombre?.text.toString().isNotBlank()) {
                    val id = JSONObject(response).getString("ID_FACTURA_ENTRADA")
                    //Aqui va el código para validar el almacen
                    val url1 = "http://186.64.123.248/Reportes/Transacciones/FacturaEntrada/actualizarFacturaEntrada.php" // Reemplaza esto con tu URL de la API
                    val queue1 = Volley.newRequestQueue(requireContext())
                    val stringRequest = object : StringRequest(
                        Request.Method.POST,
                        url1,
                        { response ->
                            if (binding.etNombreFacturaEntradaEditar.text.isNotBlank() && binding.tvListaDesplegableAlmacenEditar.text.toString() != "Eliga una opción" && binding.tvListaDesplegableProveedorEditar.text.toString() != "Eliga una opción") {
                               // Handler(Looper.getMainLooper()).postDelayed({
                                eliminarProductos()
                                anadirInventario()
                              //  }, 2500)
                               Handler(Looper.getMainLooper()).postDelayed({
                                   InsertarPreciosYCantidades()
                               }, 500)
                                binding.tvProductosAnadidosEditar.isVisible = !(adapter.listaDeCantidadesAnadir.size == 0 && adapter.listaDeProductosAnadir.size == 0 && adapter.listaDePreciosAnadir.size == 0)
                            }

                            Toast.makeText(
                                requireContext(),
                                "Factura actualizada exitosamente. El id de ingreso es el número $id ",
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
                            parametros.put("ID_FACTURA_ENTRADA", id.toString())
                            parametros.put("FACTURA_ENTRADA", TextNombre?.text.toString().uppercase())
                            parametros.put("PROVEEDOR", DropDownProveedor?.text.toString())
                            parametros.put("ALMACEN", DropDownAlmacen?.text.toString())
                            parametros.put("FECHA_TRANSACCION", TextFecha?.text.toString().uppercase())
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
                        "El nombre de la factura de entrada es obligatorio",
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
                parametros.put("FACTURA_ENTRADA", TextNombre?.text.toString().uppercase())
                return parametros
            }
        }
        queue.add(jsonObjectRequest)
    }

   private fun eliminarProductos() {
        val url1 = "http://186.64.123.248/Reportes/Transacciones/FacturaEntrada/eliminarProductosEditarFacturaEntrada.php" // Reemplaza esto con tu URL de la API
        val queue1 = Volley.newRequestQueue(requireContext())
        val stringRequest = object : StringRequest(
            Request.Method.POST,
            url1,
            { response ->
                Toast.makeText(
                    requireContext(),
                    "Factura e inventario modificados exitosamente",
                    Toast.LENGTH_SHORT
                ).show()
            },
            { error ->
                Toast.makeText(
                    requireContext(),
                    "$error",
                    Toast.LENGTH_SHORT
                ).show()
                Log.i("Sebastian2", "$error")
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("FACTURA_ENTRADA",TextNombre?.text.toString())
                parametros.put("NUMERO_DE_PRODUCTOS", sharedViewModel.listaDeProductosAnadir.size.toString())
                parametros.put("ALMACEN",DropDownAlmacen?.text.toString())
                for (i in 0..<sharedViewModel.listaDeProductosAnadir.size) {
                    parametros["PRODUCTO$i"] = sharedViewModel.listaDeProductosAnadir[i].uppercase()
                }

                return parametros
            }
        }
        queue1.add(stringRequest)
    }

    private fun InsertarPreciosYCantidades() {
        //INICIO EXPERIMIENTO!!!!!!!!!!!!!!!!!!!!!!!!!! (FUNCIONO)
        val queue = Volley.newRequestQueue(requireContext())
        val url = "http://186.64.123.248/Reportes/Transacciones/FacturaEntrada/registroProductos.php"
        val jsonObjectRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                if (TextNombre?.text.toString().isNotBlank()) {
                    val id = JSONObject(response).getString("ID_FACTURA_ENTRADA")
                    //unico = 1
                    //no unico = 0
                    //Aqui va el código para validar el almacen
                    val url1 = "http://186.64.123.248/Reportes/Transacciones/FacturaEntrada/insertarProductosFacturaEntrada.php" // Reemplaza esto con tu URL de la API
                    val queue1 = Volley.newRequestQueue(requireContext())
                    val stringRequest = object : StringRequest(
                        Request.Method.POST,
                        url1,
                        { response ->
                            Toast.makeText(
                                requireContext(),
                                "Productos agregados exitosamente a la factura",
                                Toast.LENGTH_SHORT
                            ).show()
                            TextNombre?.setText("")
                            TextFecha?.setText(SimpleDateFormat("dd-MM-yyyy",Locale.getDefault()).format(Calendar.getInstance().time))
                            DropDownProveedor?.setText("Eliga una opción", false)
                            DropDownAlmacen?.setText("Eliga una opción", false)
                            TextComentarios?.setText("")
                            sharedViewModel.listaDeCantidadesAnadir.removeAll(sharedViewModel.listaDeCantidadesAnadir)
                            sharedViewModel.listaDeProductosAnadir.removeAll(sharedViewModel.listaDeProductosAnadir)
                            sharedViewModel.listaDePreciosAnadir.removeAll(sharedViewModel.listaDePreciosAnadir)
                            adapter.notifyDataSetChanged()
                            binding.rvElegirProductoEditar.requestLayout()
                            binding.tvProductosAnadidosEditar.isVisible = false
                            binding.nsvElegirProductoEditar.isVisible = false
                            binding.tvMontoEditar.text = sharedViewModel.listaDePreciosDeProductos.sum().toString()
                            binding.llMontoEditar.isVisible = !(adapter.listaDeCantidadesAnadir.size == 0 || adapter.listaDeProductosAnadir.size == 0 || adapter.listaDePreciosAnadir.size == 0)
                            sharedViewModel.opcionesListEntrada.clear()
                        },
                        { error ->
                            Toast.makeText(
                                requireContext(),
                                "Productos agregados exitosamente a la factura",
                                Toast.LENGTH_LONG
                            ).show()
                            TextNombre?.setText("")
                            TextFecha?.setText(SimpleDateFormat("dd-MM-yyyy",Locale.getDefault()).format(Calendar.getInstance().time))
                            DropDownProveedor?.setText("Eliga una opción", false)
                            DropDownAlmacen?.setText("Eliga una opción", false)
                            TextComentarios?.setText("")
                            sharedViewModel.listaDeCantidadesAnadir.removeAll(sharedViewModel.listaDeCantidadesAnadir)
                            sharedViewModel.listaDeProductosAnadir.removeAll(sharedViewModel.listaDeProductosAnadir)
                            sharedViewModel.listaDePreciosAnadir.removeAll(sharedViewModel.listaDePreciosAnadir)
                            adapter.notifyDataSetChanged()
                            binding.rvElegirProductoEditar.requestLayout()
                            binding.tvProductosAnadidosEditar.isVisible = false
                            binding.nsvElegirProductoEditar.isVisible = false
                            binding.tvMontoEditar.text = sharedViewModel.listaDePreciosDeProductos.sum().toString()
                            binding.llMontoEditar.isVisible = !(adapter.listaDeCantidadesAnadir.size == 0 || adapter.listaDeProductosAnadir.size == 0 || adapter.listaDePreciosAnadir.size == 0)
                            sharedViewModel.opcionesListEntrada.clear()
                        }
                    ) {
                        override fun getParams(): MutableMap<String, String> {
                            val parametros = HashMap<String, String>()
                            parametros.put("ID_FACTURA_ENTRADA", id.toString())
                            parametros.put("NUMERO_DE_PRODUCTOS", sharedViewModel.listaDeProductosAnadir.size.toString())
                            for (i in 0..<sharedViewModel.listaDeProductosAnadir.size) {
                                parametros["PRODUCTO$i"] = sharedViewModel.listaDeProductosAnadir[i].uppercase()
                                parametros["CANTIDAD$i"] = sharedViewModel.listaDeCantidadesAnadir[i]
                                parametros["PRECIO_COMPRA$i"] = sharedViewModel.listaDePreciosAnadir[i]
                            }
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
                parametros.put("FACTURA_ENTRADA", TextNombre?.text.toString().uppercase())
                return parametros
            }
        }
        queue.add(jsonObjectRequest)
    }

    fun modificarInventario() {
        //Si el inventario es menor que la transferencia que no la haga y si el almacen de destino no existe, que lo cree
        val url1 = "http://186.64.123.248/Reportes/Transacciones/FacturaEntrada/anadirInventarioFacturaEntrada.php" // Reemplaza esto con tu URL de la API
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
                TextFecha?.setText(SimpleDateFormat("dd-MM-yyyy",Locale.getDefault()).format(Calendar.getInstance().time))
                DropDownProveedor?.setText("Eliga una opción", false)
                DropDownAlmacen?.setText("Eliga una opción", false)
                TextComentarios?.setText("")
                sharedViewModel.listaDeCantidadesAnadir.removeAll(sharedViewModel.listaDeCantidadesAnadir)
                sharedViewModel.listaDeProductosAnadir.removeAll(sharedViewModel.listaDeProductosAnadir)
                sharedViewModel.listaDePreciosAnadir.removeAll(sharedViewModel.listaDePreciosAnadir)
                adapter.notifyDataSetChanged()
                binding.rvElegirProductoEditar.requestLayout()
                binding.tvProductosAnadidosEditar.isVisible = false
                binding.nsvElegirProductoEditar.isVisible = false
                binding.tvMontoEditar.text = sharedViewModel.listaDePreciosDeProductos.sum().toString()
                binding.llMontoEditar.isVisible = !(adapter.listaDeCantidadesAnadir.size == 0 || adapter.listaDeProductosAnadir.size == 0 || adapter.listaDePreciosAnadir.size == 0)

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
                Log.i("Sebastian","$error, ${sharedViewModel.listaDeProductos} y ${sharedViewModel.listaDeCantidades}")
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("NUMERO_DE_PRODUCTOS", sharedViewModel.listaDeProductosAnadir.size.toString())
                parametros.put("ALMACEN", DropDownAlmacen?.text.toString())
                for (i in 0..<sharedViewModel.listaDeProductosAnadir.size) {
                    parametros["PRODUCTO$i"] = sharedViewModel.listaDeProductosAnadir[i].uppercase()
                    parametros["CANTIDAD$i"] = sharedViewModel.listaDeCantidadesAnadir[i]
                }

                return parametros
            }
        }
        queue1.add(stringRequest)
    }

    fun anadirInventario() {
        //Si el inventario es menor que la transferencia que no la haga y si el almacen de destino no existe, que lo cree
        val url1 = "http://186.64.123.248/Reportes/Transacciones/FacturaEntrada/anadirInventarioFacturaEntradaTotal.php" // Reemplaza esto con tu URL de la API
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

            },
            { error ->
                Toast.makeText(
                    requireContext(),
                    "Inventario modificado exitosamente",
                    Toast.LENGTH_SHORT
                ).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("FACTURA_ENTRADA",TextNombre?.text.toString())
                parametros.put("NUMERO_DE_PRODUCTOS", sharedViewModel.listaDeProductosAnadir.size.toString())
                parametros.put("ALMACEN", DropDownAlmacen?.text.toString())
                for (i in 0..<sharedViewModel.listaDeProductosAnadir.size) {
                    parametros["PRODUCTO$i"] = sharedViewModel.listaDeProductosAnadir[i].uppercase()
                    parametros["CANTIDAD$i"] = sharedViewModel.listaDeCantidadesAnadir[i]
                }

                return parametros
            }
        }
        queue1.add(stringRequest)
    }

    private fun ListaDesplegableAlmacen() {
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 ="http://186.64.123.248/FacturaEntrada/almacen.php"
        val jsonObjectRequest1 = JsonObjectRequest(
            Request.Method.GET,url1, null,
            { response ->
                // Obtén el array de opciones desde el objeto JSON
                val jsonArray = response.getJSONArray("Lista")
                // Convierte el array JSON a una lista mutable
                val opcionesList = mutableListOf<String>()
                for (i in 0 until jsonArray.length()) {
                    opcionesList.add(jsonArray.getString(i).removeSurrounding("'","'"))
                }
                //Crea un adpatador para el dropdown
                val adapter = ArrayAdapter(requireContext(),R.layout.list_item,opcionesList)
                //binding.tvholaMundo?.setText(response.getString("Lista"))
                DropDownAlmacen?.setAdapter(adapter)

                DropDownAlmacen?.onItemClickListener = AdapterView.OnItemClickListener {
                        parent, view, position, id ->
                    sharedViewModel.listaDeProductosAnadir.clear()
                    sharedViewModel.listaDeCantidadesAnadir.clear()
                    sharedViewModel.listaDePreciosAnadir.clear()
                    binding.tvProductosAnadidosEditar.isVisible = sharedViewModel.listaDeProductosAnadir.size != 0
                    binding.llMontoEditar.isVisible = sharedViewModel.listaDeProductosAnadir.size != 0
                    binding.nsvElegirProductoEditar.isVisible = false
                    binding.rvElegirProductoEditar.requestLayout()
                    val itemSelected = parent.getItemAtPosition(position)
                }
            }, { error ->
                Toast.makeText(requireContext(), " La aplicación no se ha conectado con el servidor", Toast.LENGTH_LONG).show()
            }
        )
        queue1.add(jsonObjectRequest1)
    }

    private fun ListaDesplegableProveedor() {
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 ="http://186.64.123.248/FacturaEntrada/proveedor.php"
        val jsonObjectRequest1 = JsonObjectRequest(
            Request.Method.GET,url1, null,
            { response ->
                // Obtén el array de opciones desde el objeto JSON
                val jsonArray = response.getJSONArray("Lista")
                // Convierte el array JSON a una lista mutable
                val opcionesList = mutableListOf<String>()
                for (i in 0 until jsonArray.length()) {
                    opcionesList.add(jsonArray.getString(i).removeSurrounding("'","'"))
                }
                //Crea un adpatador para el dropdown
                val adapter = ArrayAdapter(requireContext(),R.layout.list_item,opcionesList)
                //binding.tvholaMundo?.setText(response.getString("Lista"))
                DropDownProveedor?.setAdapter(adapter)

                DropDownProveedor?.onItemClickListener = AdapterView.OnItemClickListener {
                        parent, view, position, id ->
                    val itemSelected = parent.getItemAtPosition(position)
                }
            }, { error ->
                Toast.makeText(requireContext(), " La aplicación no se ha conectado con el servidor", Toast.LENGTH_LONG).show()
            }
        )
        queue1.add(jsonObjectRequest1)
    }

    private fun mensajeEliminarFacturaEntrada(){
        AlertDialog.Builder(context).apply {
            setTitle("¿Quieres eliminar esta factura de entrada?")
            setMessage("Esta acción no se puede deshacer.")
            setPositiveButton("Sí") { dialog, _ ->
                eliminarFacturaEntrada()
                findNavController().navigate(R.id.action_nav_editar_factura_entrada_to_nav_transacciones)
                dialog.dismiss()
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }

    }
    private fun eliminarFacturaEntrada(){
        val url = "http://186.64.123.248/Reportes/Transacciones/FacturaEntrada/borrar.php"
        val queue = Volley.newRequestQueue(requireContext())
        var jsonObjectRequest = object : StringRequest(Request.Method.POST, url,
            { response ->
                Toast.makeText(requireContext(), "Factura de entrada eliminada exitosamente", Toast.LENGTH_SHORT).show()

            },{error ->
                Toast.makeText(requireContext(), "No se pudo eliminar la factura de entrada", Toast.LENGTH_SHORT).show()
            })
        {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("ID_FACTURA_ENTRADA", sharedViewModel.id.last())
                return parametros
            }
        }
        queue.add(jsonObjectRequest)
    }


    override fun onDestroyView() {
        super.onDestroyView()
       /* sharedViewModel.listaDeCantidadesAnadir.removeAll(sharedViewModel.listaDeCantidadesAnadir)
        sharedViewModel.listaDeProductosAnadir.removeAll(sharedViewModel.listaDeProductosAnadir)
        sharedViewModel.listaDePreciosAnadir.removeAll(sharedViewModel.listaDePreciosAnadir)
        sharedViewModel.listaDePreciosDeProductos.clear()*/
        adapter.notifyDataSetChanged()
        binding.rvElegirProductoEditar.requestLayout()
        binding.tvProductosAnadidosEditar.isVisible = false
        binding.nsvElegirProductoEditar.isVisible = false
        binding.tvMontoEditar.text = sharedViewModel.listaDePreciosDeProductos.sum().toString()
        binding.llMontoEditar.isVisible = !(sharedViewModel.listaDeCantidadesAnadir.size == 0 || sharedViewModel.listaDeProductosAnadir.size == 0 || sharedViewModel.listaDePreciosAnadir.size == 0)
        _binding = null
    }
}