package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.facturaEntrada.editarFacturaEntrada

import android.app.AlertDialog
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
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
import androidx.activity.OnBackPressedCallback
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
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.clientes.editarCliente.EditarClienteFragmentDirections
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
            if (DropDownProveedor?.text.toString() != "Elija una opción" && DropDownAlmacen?.text.toString() != "Elija una opción") {
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
                Toast.makeText(requireContext(),"Permiso denegado",Toast.LENGTH_SHORT).show()
            }
        }
        binding.bEscanearCodigoDeBarraEditar.setOnClickListener {
            requestCamara?.launch(android.Manifest.permission.CAMERA)
        }


       /* val productosOrdenados = sharedViewModel.listaDeProductosAnadir.sorted().toMutableList()
        val listaCombinada = sharedViewModel.listaDeProductosAnadir.zip(sharedViewModel.listaDeCantidadesAnadir)
        val listaOrdenadaCombinada = listaCombinada.sortedBy { it.first }
        val cantidadesOrdenadas = listaOrdenadaCombinada.map { it.second }.toMutableList()
        val listaCombinada2 = sharedViewModel.listaDeProductosAnadir.zip(sharedViewModel.listaDePreciosAnadir)
        val listaOrdenadaCombinada2 = listaCombinada2.sortedBy { it.first }
        val preciosOrdenados = listaOrdenadaCombinada2.map { it.second }.toMutableList()*/

        binding.bActualizarRecyclerViewEditar.setOnClickListener {
           /* val productosOrdenadosActualizado = sharedViewModel.listaDeProductosAnadir.sorted().toMutableList()
            val listaCombinadaActualizada = sharedViewModel.listaDeProductosAnadir.zip(sharedViewModel.listaDeCantidadesAnadir)
            val listaOrdenadaCombinadaActualizada = listaCombinadaActualizada.sortedBy { it.first }
            val cantidadesOrdenadasActualizada = listaOrdenadaCombinadaActualizada.map { it.second }.toMutableList()
            val listaCombinadaActualizada2 = sharedViewModel.listaDeProductosAnadir.zip(sharedViewModel.listaDePreciosAnadir)
            val listaOrdenadaCombinadaActualizada2 = listaCombinadaActualizada2.sortedBy { it.first }
            val preciosOrdenadosActualizado = listaOrdenadaCombinadaActualizada2.map { it.second }.toMutableList()*/
            ordenarListas()
            adapter.updateList(sharedViewModel.listaDeCantidadesAnadir, sharedViewModel.listaDeProductosAnadir, sharedViewModel.listaDePreciosAnadir)
            binding.tvProductosAnadidosEditar.isVisible = !(sharedViewModel.listaDeCantidadesAnadir.size == 0 || sharedViewModel.listaDeProductosAnadir.size == 0 || sharedViewModel.listaDePreciosAnadir.size == 0)
            binding.llMontoEditar.isVisible = !(sharedViewModel.listaDeCantidadesAnadir.size == 0 || sharedViewModel.listaDeProductosAnadir.size == 0 || sharedViewModel.listaDePreciosAnadir.size == 0)
            adapter.notifyDataSetChanged()
            Handler(Looper.getMainLooper()).postDelayed({
                binding.tvMontoEditar.text = sharedViewModel.listaDePreciosDeProductos.sum().toString()
                sharedViewModel.facturaTotalEntrada = sharedViewModel.listaDePreciosDeProductos
            }, 300)
            sharedViewModel.listaDePreciosDeProductos.clear()
            binding.nsvElegirProductoEditar.isVisible = !(sharedViewModel.listaDeCantidadesAnadir.size == 0 || sharedViewModel.listaDeProductosAnadir.size == 0 || sharedViewModel.listaDePreciosAnadir.size == 0)
            binding.rvElegirProductoEditar.requestLayout()
        }
        binding.nsvElegirProductoEditar.isVisible = true
        binding.llMontoEditar.isVisible = true
        binding.bAnadirNuevoProductoEditar.setOnClickListener {
            if(sharedViewModel.opcionesListEntradaProveedor.contains(DropDownProveedor?.text.toString()) &&
                sharedViewModel.opcionesListEntradaAlmacen.contains(DropDownAlmacen?.text.toString())){
                sharedViewModel.proveedorAnadir = DropDownProveedor?.text.toString()
                sharedViewModel.almacenAnadir = DropDownAlmacen?.text.toString()
                sharedViewModel.listaDeAlmacenesEntrada.add(DropDownAlmacen?.text.toString())
                binding.nsvElegirProductoEditar.isVisible = true
                binding.llMontoEditar.isVisible = true
                //setFragmentResult("Proveedor", bundleOf("Proveedor" to DropDownProveedor?.text.toString()))
                //Toast.makeText(requireContext(),"Funciona el traspaso de información", Toast.LENGTH_SHORT).show()
                val elegirProductoFacturaEntradaFragment = ElegirProductoFacturaEntradaFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.clFacturaEntradaEditar, elegirProductoFacturaEntradaFragment)
                    .commit()
                binding.tvProductosAnadidosEditar.isVisible = true
                // binding.tvProductosAnadidosAnadir.isVisible = !(adapter.listaDeCantidadesAnadir.size == 0 && adapter.listaDeProductosAnadir.size == 0 && adapter.listaDePreciosAnadir.size == 0)
            }else if((!sharedViewModel.opcionesListEntradaProveedor.contains(DropDownProveedor?.text.toString()) &&
                        !sharedViewModel.opcionesListEntradaAlmacen.contains(DropDownAlmacen?.text.toString())) &&
                (DropDownProveedor?.text.toString() == "Elija una opción" ||
                        DropDownAlmacen?.text.toString() == "Elija una opción")){
                Toast.makeText(requireContext(),"Debe elegir proveedor y almacén", Toast.LENGTH_SHORT).show()
            }else if((DropDownProveedor?.text.toString() != "Elija una opción" ||
                        DropDownAlmacen?.text.toString() != "Elija una opción") &&
                (!sharedViewModel.opcionesListEntradaProveedor.contains(DropDownProveedor?.text.toString()) ||
                        !sharedViewModel.opcionesListEntradaAlmacen.contains(DropDownAlmacen?.text.toString()))){
                Toast.makeText(requireContext(),"El nombre del proveedor o del almacén no es válido", Toast.LENGTH_SHORT).show()
            }

        }
        try {
            sharedViewModel.id.add(args.id)
        }
        catch(e:Exception){
        }
        binding.FacturaEntradaButtonEnviarEditar.setOnClickListener {
            if ((sharedViewModel.listaDeProductosAnadir.size > 0 && sharedViewModel.listaDeCantidadesAnadir.size > 0 && sharedViewModel.listaDePreciosAnadir.size > 0) &&
                (sharedViewModel.opcionesListEntradaProveedor.contains(DropDownProveedor?.text.toString()) &&
                        sharedViewModel.opcionesListEntradaAlmacen.contains(DropDownAlmacen?.text.toString()))
            ) {
                actualizarFacturaDeEntrada()
                adapter.notifyDataSetChanged()
                binding.rvElegirProductoEditar.requestLayout()
            } else if (sharedViewModel.listaDeCantidadesAnadir.size == 0 &&
                sharedViewModel.listaDeProductosAnadir.size == 0 &&
                sharedViewModel.listaDePreciosAnadir.size == 0
            ) {
                Toast.makeText(
                    requireContext(),
                    "Debe elegir al menos un producto en la factura de entrada",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!sharedViewModel.opcionesListEntradaProveedor.contains(DropDownProveedor?.text.toString()) ||
                !sharedViewModel.opcionesListEntradaAlmacen.contains(DropDownAlmacen?.text.toString())) {
                Toast.makeText(
                    requireContext(), "El nombre del proveedor o del almacén no es válido",
                    Toast.LENGTH_SHORT
                ).show()
            }else if(DropDownProveedor?.text.toString() == "Elija una opción" ||
                DropDownAlmacen?.text.toString() == "Elija una opción"){
                Toast.makeText(requireContext(),"Debe elegir el proveedor y el almacén", Toast.LENGTH_SHORT).show()
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

                    Log.i("productos","${sharedViewModel.listaDeProductosAnadir}")
                    ordenarListas()
                    Log.i("productosOrdenados","${sharedViewModel.listaDeProductosAnadir}")
                    recyclerViewElegirProducto(sharedViewModel.listaDeCantidadesAnadir, sharedViewModel.listaDeProductosAnadir, sharedViewModel.listaDePreciosAnadir)
                    adapter.updateList(sharedViewModel.listaDeCantidadesAnadir, sharedViewModel.listaDeProductosAnadir, sharedViewModel.listaDePreciosAnadir)
                    adapter.notifyDataSetChanged()
                    binding.rvElegirProductoEditar.requestLayout()

                Handler(Looper.getMainLooper()).postDelayed({
                    binding.nsvElegirProductoEditar.isVisible = true
                    binding.tvProductosAnadidosEditar.isVisible =
                        !(adapter.listaDeCantidadesAnadir.size == 0 || adapter.listaDeProductosAnadir.size == 0 || adapter.listaDePreciosAnadir.size == 0)
                    binding.llMontoEditar.isVisible =
                        !(adapter.listaDeCantidadesAnadir.size == 0 || adapter.listaDeProductosAnadir.size == 0 || adapter.listaDePreciosAnadir.size == 0)
                    ordenarListas()
                    adapter.notifyDataSetChanged()
                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.tvMontoEditar.text =
                            sharedViewModel.listaDePreciosDeProductos.sum().toString()
                    }, 300)
                    sharedViewModel.listaDePreciosDeProductos.clear()
                    binding.rvElegirProductoEditar.requestLayout()
                },100)


            }, { error ->
                Toast.makeText(requireContext(), "El id es ${sharedViewModel.id.last()}", Toast.LENGTH_SHORT).show()
            }
        )
        queue1.add(jsonObjectRequest1)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Navegar a MisDatosFragment y pasar un identificador para saber qué fragmento llamar luego
                // val action = EditarClienteFragmentDirections.actionEditarClienteFragmentToMisDatosFragment("clientes")
                val action = EditarFacturaEntradaFragmentDirections.actionNavEditarFacturaEntradaToNavTransacciones(destino = "factura entrada")
                findNavController().navigate(action)
                borrarListas()
            }
        })
    }

    fun recyclerViewElegirProducto(listaDeCantidades: MutableList<String>, listaDeProductos: MutableList<String>, listaDePrecios: MutableList<String>){
        adapter = AnadirInventarioAdapter(listaDeCantidades,listaDeProductos,listaDePrecios,sharedViewModel) { position ->
            onDeletedItem(position)}
        binding.rvElegirProductoEditar.setHasFixedSize(true)
        binding.rvElegirProductoEditar.adapter = adapter
        binding.rvElegirProductoEditar.layoutManager = LinearLayoutManager(requireContext())
       // adapter.updateList(sharedViewModel.listaDeCantidadesAnadir,sharedViewModel.listaDeProductosAnadir,sharedViewModel.listaDePreciosAnadir)
        adapter.notifyDataSetChanged()
       // binding.rvElegirProductoEditar.requestLayout()
    }

    private fun onDeletedItem(position: Int){
       // sharedViewModel.opcionesListEntrada.add(position,sharedViewModel.listaDeProductosAnadir[position])
        try {
            val cantidad = sharedViewModel.listaDeCantidadesAnadir.removeAt(position).toInt()
            val producto = sharedViewModel.listaDeProductosAnadir.removeAt(position)
            sharedViewModel.listaDePreciosAnadir.removeAt(position)
            ordenarListas()
            sharedViewModel.listaCombinadaEntrada.add(Pair(producto, cantidad))
            adapter.updateList(
                sharedViewModel.listaDeCantidadesAnadir,
                sharedViewModel.listaDeProductosAnadir,
                sharedViewModel.listaDePreciosAnadir
            )
            adapter.notifyItemRemoved(position)
            adapter.notifyDataSetChanged()
            binding.rvElegirProductoEditar.requestLayout()
            Handler(Looper.getMainLooper()).postDelayed({
                binding.tvMontoEditar.text =
                    sharedViewModel.listaDePreciosDeProductos.sum().toString()
                sharedViewModel.facturaTotalEntrada = sharedViewModel.listaDePreciosDeProductos
            }, 300)
            sharedViewModel.listaDePreciosDeProductos.clear()
            binding.tvProductosAnadidosEditar.isVisible =
                !(adapter.listaDeCantidadesAnadir.size == 0 || adapter.listaDeProductosAnadir.size == 0 || adapter.listaDePreciosAnadir.size == 0)
            binding.llMontoEditar.isVisible =
                !(adapter.listaDeCantidadesAnadir.size == 0 || adapter.listaDeProductosAnadir.size == 0 || adapter.listaDePreciosAnadir.size == 0)
        }catch(e:Exception){
            val inflaterRemover = requireActivity().layoutInflater
            val layoutRemover = inflaterRemover.inflate(
                R.layout.toast_custom_remover,
                null
            )
            val textRemover =
                layoutRemover.findViewById<TextView>(R.id.text_view_toast_remover)
            textRemover.text =
                "Hubo un error al eliminar un producto, debe salir y volver a entrar"
            val toast = Toast(requireContext())
            toast.duration = Toast.LENGTH_SHORT
            toast.view = layoutRemover
            toast.setGravity(Gravity.BOTTOM, 0, 600)
            toast.show()
        }
    }

    private fun actualizarFacturaDeEntrada(){
        if (TextNombre?.text.toString().isNotBlank()) {
            val url1 =
                "http://186.64.123.248/Reportes/Transacciones/FacturaEntrada/actualizarFacturaEntrada.php" // Reemplaza esto con tu URL de la API
            val queue1 = Volley.newRequestQueue(requireContext())
            val stringRequest = object : StringRequest(
                Request.Method.POST,
                url1,
                { response ->
                    if (binding.etNombreFacturaEntradaEditar.text.isNotBlank() && binding.tvListaDesplegableAlmacenEditar.text.toString() != "Elija una opción" && binding.tvListaDesplegableProveedorEditar.text.toString() != "Elija una opción") {
                        // Handler(Looper.getMainLooper()).postDelayed({
                        eliminarProductos()
                        anadirInventario()
                        //  }, 2500)
                        Handler(Looper.getMainLooper()).postDelayed({
                            InsertarPreciosYCantidades()
                        }, 200)
                        binding.tvProductosAnadidosEditar.isVisible =
                            !(adapter.listaDeCantidadesAnadir.size == 0 && adapter.listaDeProductosAnadir.size == 0 && adapter.listaDePreciosAnadir.size == 0)
                    }

                    Toast.makeText(
                        requireContext(),
                        "Factura actualizada exitosamente. El id de ingreso es el número ${sharedViewModel.id.last()} ",
                        Toast.LENGTH_SHORT
                    ).show()


                },
                { error ->
                    Toast.makeText(requireContext(), "Conecte la aplicación al servidor", Toast.LENGTH_SHORT).show()
                    //Toast.makeText(requireContext(), "Conecte la aplicación al servidor", Toast.LENGTH_SHORT).show()
                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    val parametros = HashMap<String, String>()
                    parametros.put("ID_FACTURA_ENTRADA", sharedViewModel.id.last())
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
        }else {
            Toast.makeText(
                requireContext(),
                "El nombre de la factura de entrada es obligatorio",
                Toast.LENGTH_SHORT
            ).show()
        }
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
        val url1 = "http://186.64.123.248/Reportes/Transacciones/FacturaEntrada/insertarProductosFacturaEntrada.php" // Reemplaza esto con tu URL de la API
        val queue1 = Volley.newRequestQueue(requireContext())
        val stringRequest = object : StringRequest(
            Request.Method.POST,
            url1,
            { response ->
                Toast.makeText(
                    requireContext(),
                    "Productos editados exitosamente en la factura",
                    Toast.LENGTH_SHORT
                ).show()
                TextFecha?.setText(SimpleDateFormat("dd-MM-yyyy",Locale.getDefault()).format(Calendar.getInstance().time))
                /*TextNombre?.setText("")
                DropDownProveedor?.setText("Elija una opción", false)
                DropDownAlmacen?.setText("Elija una opción", false)
                TextComentarios?.setText("")
                sharedViewModel.listaDeCantidadesAnadir.clear()
                sharedViewModel.listaDeProductosAnadir.clear()
                sharedViewModel.listaDePreciosAnadir.clear()
                adapter.notifyDataSetChanged()
                binding.rvElegirProductoEditar.requestLayout()
                binding.tvProductosAnadidosEditar.isVisible = false
                binding.nsvElegirProductoEditar.isVisible = false
                binding.tvMontoEditar.text = sharedViewModel.listaDePreciosDeProductos.sum().toString()
                binding.llMontoEditar.isVisible = false
                sharedViewModel.opcionesListEntrada.clear()*/
            },
            { error ->
                Toast.makeText(
                    requireContext(),
                    "Productos editados exitosamente en la factura",
                    Toast.LENGTH_SHORT
                ).show()
                TextFecha?.setText(SimpleDateFormat("dd-MM-yyyy",Locale.getDefault()).format(Calendar.getInstance().time))
               /* TextNombre?.setText("")
                DropDownProveedor?.setText("Elija una opción", false)
                DropDownAlmacen?.setText("Elija una opción", false)
                TextComentarios?.setText("")
                sharedViewModel.listaDeCantidadesAnadir.clear()
                sharedViewModel.listaDeProductosAnadir.clear()
                sharedViewModel.listaDePreciosAnadir.clear()
                adapter.notifyDataSetChanged()
                binding.rvElegirProductoEditar.requestLayout()
                binding.tvProductosAnadidosEditar.isVisible = false
                binding.nsvElegirProductoEditar.isVisible = false
                binding.tvMontoEditar.text = sharedViewModel.listaDePreciosDeProductos.sum().toString()
                binding.llMontoEditar.isVisible = !(adapter.listaDeCantidadesAnadir.size == 0 || adapter.listaDeProductosAnadir.size == 0 || adapter.listaDePreciosAnadir.size == 0)
                sharedViewModel.opcionesListEntrada.clear()*/
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("ID_FACTURA_ENTRADA", sharedViewModel.id.last())
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
                DropDownProveedor?.setText("Elija una opción", false)
                DropDownAlmacen?.setText("Elija una opción", false)
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
                          DropDownOrigen?.setText("Elija una opción",false)
                          DropDownDestino?.setText("Elija una opción",false)
                          TextComentarios?.setText("")
                          TextCodigoDeBarra?.setText("")*/
                Toast.makeText(
                    requireContext(),
                    "Error $error y ${sharedViewModel.listaDeProductos} y ${sharedViewModel.listaDeCantidades}",
                    Toast.LENGTH_SHORT
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
                sharedViewModel.opcionesListEntradaAlmacen.clear()
                if(sharedViewModel.opcionesListEntradaAlmacen.isEmpty()) {
                    for (i in 0 until jsonArray.length()) {
                        sharedViewModel.opcionesListEntradaAlmacen.add(jsonArray.getString(i).removeSurrounding("'", "'"))
                    }
                }
                sharedViewModel.opcionesListEntradaAlmacen.sort()
                //Crea un adpatador para el dropdown
                val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_dropdown_item_1line,sharedViewModel.opcionesListEntradaAlmacen)
                //binding.tvholaMundo?.setText(response.getString("Lista"))
                DropDownAlmacen?.setAdapter(adapter)
                DropDownAlmacen?.threshold = 1
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
                DropDownAlmacen?.setOnClickListener {
                    if(DropDownAlmacen?.text.toString() == "Elija una opción"){
                        binding.tvListaDesplegableAlmacenEditar.setText("",false)
                        DropDownAlmacen?.showDropDown()
                    }
                }
                DropDownAlmacen?.setOnFocusChangeListener { _, hasFocus ->
                    if(hasFocus && DropDownAlmacen?.text.toString() == "Elija una opción"){
                        binding.tvListaDesplegableAlmacenEditar.setText("",false)
                        DropDownAlmacen?.showDropDown()
                    }
                    else if (!hasFocus && !sharedViewModel.opcionesListEntradaAlmacen.contains(DropDownAlmacen?.text.toString())){
                        Toast.makeText(requireContext(),"El nombre del almacén no es válido", Toast.LENGTH_SHORT).show()
                    }
                }
            }, { error ->
                Toast.makeText(requireContext(), " La aplicación no se ha conectado con el servidor", Toast.LENGTH_SHORT).show()
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
                sharedViewModel.opcionesListEntradaProveedor.clear()
                if(sharedViewModel.opcionesListEntradaProveedor.isEmpty()) {
                    for (i in 0 until jsonArray.length()) {
                        sharedViewModel.opcionesListEntradaProveedor.add(jsonArray.getString(i).removeSurrounding("'", "'"))
                    }
                }
                sharedViewModel.opcionesListEntradaProveedor.sort()
                //Crea un adpatador para el dropdown
                val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_dropdown_item_1line,sharedViewModel.opcionesListEntradaProveedor)
                //binding.tvholaMundo?.setText(response.getString("Lista"))
                DropDownProveedor?.setAdapter(adapter)
                DropDownProveedor?.threshold = 1
                DropDownProveedor?.onItemClickListener = AdapterView.OnItemClickListener {
                        parent, view, position, id ->
                    val itemSelected = parent.getItemAtPosition(position)
                }
                DropDownProveedor?.setOnClickListener {
                    if(DropDownProveedor?.text.toString() == "Elija una opción"){
                        binding.tvListaDesplegableProveedorEditar.setText("",false)
                        DropDownProveedor?.showDropDown()
                    }
                }
                DropDownProveedor?.setOnFocusChangeListener { _, hasFocus ->
                    if(hasFocus && DropDownProveedor?.text.toString() == "Elija una opción"){
                        binding.tvListaDesplegableProveedorEditar.setText("",false)
                        DropDownProveedor?.showDropDown()
                    }
                    else if (!hasFocus && !sharedViewModel.opcionesListEntradaProveedor.contains(DropDownProveedor?.text.toString())){
                        Toast.makeText(requireContext(),"El nombre del proveedor no es válido", Toast.LENGTH_SHORT).show()
                    }
                }
            }, { error ->
                Toast.makeText(requireContext(), " La aplicación no se ha conectado con el servidor", Toast.LENGTH_SHORT).show()
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

    private fun ordenarListas(){
        val listaCombinada = sharedViewModel.listaDeProductosAnadir.indices.map{ i ->
            Triple(sharedViewModel.listaDeProductosAnadir[i], sharedViewModel.listaDeCantidadesAnadir[i], sharedViewModel.listaDePreciosAnadir[i])
        }
        val listaOrdenada = listaCombinada.sortedBy { it.first }

        sharedViewModel.listaDeProductosAnadir = listaOrdenada.map { it.first }.toMutableList()
        sharedViewModel.listaDeCantidadesAnadir = listaOrdenada.map { it.second }.toMutableList()
        sharedViewModel.listaDePreciosAnadir = listaOrdenada.map { it.third }.toMutableList()

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

    private fun borrarListas(){
        sharedViewModel.listaDeProductos.clear()
        sharedViewModel.listaDeCantidades.clear()
        sharedViewModel.listaDeProductosAnadir.clear()
        sharedViewModel.listaDeCantidadesAnadir.clear()
        sharedViewModel.listaDePreciosAnadir.clear()
        sharedViewModel.listaDePreciosDeProductos.clear()
        sharedViewModel.listaDeProductosRemover.clear()
        sharedViewModel.listaDeCantidadesRemover.clear()
        sharedViewModel.listaDePreciosRemover.clear()
        sharedViewModel.listaDePreciosDeProductosRemover.clear()
        sharedViewModel.listaDeBodegasAnadir.clear()
        sharedViewModel.listaDeAlertasAnadir.clear()
        sharedViewModel.ListasDeAlertas.clear()
        sharedViewModel.ListasDeAlmacenes.clear()
        sharedViewModel.ListasDeProductosAlertas.clear()
        sharedViewModel.listaDeClientesAnadir.clear()
        sharedViewModel.listaDePreciosVentaAnadir.clear()
        sharedViewModel.ListasDeClientes.clear()
        sharedViewModel.ListasDePreciosDeVenta.clear()
        sharedViewModel.ListasDeProductosPrecioVenta.clear()
        sharedViewModel.listaDePreciosCompraAnadir.clear()
        sharedViewModel.listaDeProveedoresAnadir.clear()
        sharedViewModel.ListasDeProveedores.clear()
        sharedViewModel.ListasDePreciosDeCompra.clear()
        sharedViewModel.ListasDeProductosPrecioCompra.clear()
        sharedViewModel.id.clear()
        sharedViewModel.listaDeAlertas.clear()
        sharedViewModel.listaDePreciosVenta.clear()
        sharedViewModel.listaDePreciosCompra.clear()
        sharedViewModel.listaDeBodegas.clear()
        sharedViewModel.listaDeClientes.clear()
        sharedViewModel.listaDeProveedores.clear()
        sharedViewModel.numeroAlertas.clear()
        sharedViewModel.numeroPreciosCompra.clear()
        sharedViewModel.numeroPreciosVenta.clear()
        sharedViewModel.listaDeAlmacenesAnadir.clear()
        sharedViewModel.listaDeAlmacenesRemover.clear()
        sharedViewModel.listaDeAlmacenesEntrada.clear()
        sharedViewModel.listaDeAlmacenesSalida.clear()
        sharedViewModel.listaDeAlmacenesTransferencia.clear()
        sharedViewModel.listaDeAlmacenesEditarTransferencia.clear()
        sharedViewModel.productos.clear()
        sharedViewModel.inventario.clear()

        sharedViewModel.facturaTotalAnadir.clear()
        sharedViewModel.facturaTotalRemover.clear()
        sharedViewModel.facturaTotalEntrada.clear()
        sharedViewModel.facturaTotalSalida.clear()
        sharedViewModel.cantidadTotalTransferencia.clear()
        sharedViewModel.cantidadTotalEditarTransferencia.clear()

        sharedViewModel.listaCombinadaEntrada.clear()
        sharedViewModel.listaCombinadaSalida.clear()
        sharedViewModel.listaCombinadaTransferencia.clear()
    }
}