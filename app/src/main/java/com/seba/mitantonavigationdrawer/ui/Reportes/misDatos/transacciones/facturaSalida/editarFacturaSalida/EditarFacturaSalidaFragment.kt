package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.facturaSalida.editarFacturaSalida

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
import com.seba.mitantonavigationdrawer.databinding.FragmentEditarFacturaSalidaBinding
import com.seba.mitantonavigationdrawer.databinding.FragmentEditarTransferenciaBinding
import com.seba.mitantonavigationdrawer.databinding.FragmentEstadisticaBinding
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirInventario.AnadirInventarioAdapter
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirInventario.ElegirProductoAnadirFragment
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.clientes.editarCliente.EditarClienteFragmentDirections
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
    var EliminarFacturaSalida: ImageView? = null
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
        EliminarFacturaSalida = binding.ivEliminarFacturaSalida.findViewById(R.id.ivEliminarFacturaSalida)
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

        EliminarFacturaSalida?.setOnClickListener {
            mensajeEliminarFacturaSalida()
        }

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
        if (TextFecha?.text?.isBlank() == true) {
            val fecha =  SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Calendar.getInstance().time)
            TextFecha?.setText(fecha.format(Date()))
        }
        requestCamara = registerForActivityResult(ActivityResultContracts.RequestPermission(),) {
            if (it) {
                findNavController().navigate(R.id.action_nav_editar_factura_salida_to_nav_barcode_scan_factura_salida)
            } else {
                Toast.makeText(requireContext(), "Permiso denegado", Toast.LENGTH_SHORT).show()
            }
        }
        binding.bEscanearCodigoDeBarraFacturaSalidaEditar.setOnClickListener {
            requestCamara?.launch(android.Manifest.permission.CAMERA)
        }

        val productosOrdenados = sharedViewModel.listaDeProductosRemover.sorted().toMutableList()
        val listaCombinada = sharedViewModel.listaDeProductosRemover.zip(sharedViewModel.listaDeCantidadesRemover)
        val listaOrdenadaCombinada = listaCombinada.sortedBy { it.first }
        val cantidadesOrdenadas = listaOrdenadaCombinada.map { it.second }.toMutableList()
        val listaCombinada2 = sharedViewModel.listaDeProductosRemover.zip(sharedViewModel.listaDePreciosRemover)
        val listaOrdenadaCombinada2 = listaCombinada2.sortedBy { it.first }
        val preciosOrdenados = listaOrdenadaCombinada2.map { it.second }.toMutableList()
        recyclerViewElegirProducto(cantidadesOrdenadas,productosOrdenados,preciosOrdenados)
        binding.bActualizarRecyclerViewFacturaSalidaEditar.setOnClickListener {
           /* val productosOrdenadosActualizado = sharedViewModel.listaDeProductosRemover.sorted().toMutableList()
            val listaCombinadaActualizada = sharedViewModel.listaDeProductosRemover.zip(sharedViewModel.listaDeCantidadesRemover)
            val listaOrdenadaCombinadaActualizada = listaCombinadaActualizada.sortedBy { it.first }
            val cantidadesOrdenadasActualizada = listaOrdenadaCombinadaActualizada.map { it.second }.toMutableList()
            val listaCombinadaActualizada2 = sharedViewModel.listaDeProductosRemover.zip(sharedViewModel.listaDePreciosRemover)
            val listaOrdenadaCombinadaActualizada2 = listaCombinadaActualizada2.sortedBy { it.first }
            val preciosOrdenadosActualizado = listaOrdenadaCombinadaActualizada2.map { it.second }.toMutableList()
            adapter.updateList(cantidadesOrdenadasActualizada,productosOrdenadosActualizado,preciosOrdenadosActualizado)*/
            ordenarListas()
            adapter.updateList(sharedViewModel.listaDeCantidadesRemover,sharedViewModel.listaDeProductosRemover,sharedViewModel.listaDePreciosRemover)
            binding.tvProductosAnadidosFacturaSalidaEditar.isVisible =
                !(sharedViewModel.listaDeCantidadesRemover.size == 0 || sharedViewModel.listaDeProductosRemover.size == 0 || sharedViewModel.listaDePreciosRemover.size == 0)
            binding.llMontoFacturaSalidaEditar.isVisible =
                !(sharedViewModel.listaDeCantidadesRemover.size == 0 || sharedViewModel.listaDeProductosRemover.size == 0 || sharedViewModel.listaDePreciosRemover.size == 0)
            adapter.notifyDataSetChanged()
            Handler(Looper.getMainLooper()).postDelayed({
                binding.tvMontoFacturaSalidaEditar.text =
                    sharedViewModel.listaDePreciosDeProductosRemover.sum().toString()
                sharedViewModel.facturaTotalSalida = sharedViewModel.listaDePreciosDeProductosRemover
            }, 300)
            sharedViewModel.listaDePreciosDeProductosRemover.clear()
            binding.nsvElegirProductoFacturaSalidaEditar.isVisible = !(sharedViewModel.listaDeCantidadesRemover.size == 0 || sharedViewModel.listaDeProductosRemover.size == 0 || sharedViewModel.listaDePreciosRemover.size == 0)
            binding.rvElegirProductoFacturaSalidaEditar.requestLayout()
        }
        binding.nsvElegirProductoFacturaSalidaEditar.isVisible = true
        binding.llMontoFacturaSalidaEditar.isVisible = true
        binding.bAnadirNuevoProductoFacturaSalidaEditar.setOnClickListener {
            if (sharedViewModel.opcionesListSalidaCliente.contains(DropDownCliente?.text.toString()) &&
                sharedViewModel.opcionesListSalidaAlmacen.contains(DropDownAlmacen?.text.toString())) {
                sharedViewModel.clienteRemover = DropDownCliente?.text.toString()
                sharedViewModel.almacenRemover = DropDownAlmacen?.text.toString()
                sharedViewModel.listaDeAlmacenesSalida.add(DropDownAlmacen?.text.toString())
                binding.nsvElegirProductoFacturaSalidaEditar.isVisible = true
                binding.llMontoFacturaSalidaEditar.isVisible = true
                //setFragmentResult("Proveedor", bundleOf("Proveedor" to DropDownProveedor?.text.toString()))
                //Toast.makeText(requireContext(),"Funciona el traspaso de información", Toast.LENGTH_SHORT).show()
                val elegirProductoFacturaSalidaFragment = ElegirProductoFacturaSalidaFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.clFacturaSalidaEditar, elegirProductoFacturaSalidaFragment)
                    .commit()
                binding.tvProductosAnadidosFacturaSalidaEditar.isVisible = true
                // binding.tvProductosAnadidosAnadir.isVisible = !(adapter.listaDeCantidadesAnadir.size == 0 && adapter.listaDeProductosAnadir.size == 0 && adapter.listaDePreciosAnadir.size == 0)
            } else if ((!sharedViewModel.opcionesListSalidaCliente.contains(DropDownCliente?.text.toString()) &&
                        !sharedViewModel.opcionesListSalidaAlmacen.contains(DropDownAlmacen?.text.toString())) &&
                (DropDownCliente?.text.toString() == "Elija una opción" ||
                        DropDownAlmacen?.text.toString() == "Elija una opción")){
                Toast.makeText(requireContext(),"Debe elegir cliente y almacén", Toast.LENGTH_SHORT).show()

             }else if((DropDownCliente?.text.toString() != "Elija una opción" ||
                        DropDownAlmacen?.text.toString() != "Elija una opción") &&
                (!sharedViewModel.opcionesListSalidaCliente.contains(DropDownCliente?.text.toString()) ||
                        !sharedViewModel.opcionesListSalidaAlmacen.contains(DropDownAlmacen?.text.toString()))){
                Toast.makeText(requireContext(),"El nombre del cliente o del almacén no es válido", Toast.LENGTH_SHORT).show()
              }
            }


        try {
            sharedViewModel.id.add(args.id)
        } catch (e: Exception) {
        }
        binding.FacturaSalidaButtonEnviarEditar.setOnClickListener {
                if ((sharedViewModel.listaDeCantidadesRemover.size > 0 && sharedViewModel.listaDeProductosRemover.size > 0 && sharedViewModel.listaDePreciosRemover.size > 0) &&
                    (sharedViewModel.opcionesListSalidaCliente.contains(DropDownCliente?.text.toString()) &&
                            sharedViewModel.opcionesListSalidaAlmacen.contains(DropDownAlmacen?.text.toString()))) {
                    actualizarFacturaDeEntrada()
                    adapter.notifyDataSetChanged()
                    binding.rvElegirProductoFacturaSalidaEditar.requestLayout()
                } else if(sharedViewModel.listaDeCantidadesRemover.size == 0 &&
                    sharedViewModel.listaDeProductosRemover.size == 0 &&
                    sharedViewModel.listaDePreciosRemover.size == 0) {
                    Toast.makeText(requireContext(),
                        "Debe elegir al menos un producto para remover inventario", Toast.LENGTH_SHORT).show()
                }else if(!sharedViewModel.opcionesListSalidaCliente.contains(DropDownCliente?.text.toString()) ||
                    !sharedViewModel.opcionesListSalidaAlmacen.contains(DropDownAlmacen?.text.toString())){
                    Toast.makeText(requireContext(),"El nombre del cliente o del almacén no es válido",
                        Toast.LENGTH_SHORT).show()
                }else if(DropDownCliente?.text.toString() == "Elija una opción" ||
                    DropDownAlmacen?.text.toString() == "Elija una opción"){
                    Toast.makeText(requireContext(),"Debe elegir el cliente y el almacén", Toast.LENGTH_SHORT).show()
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
                if(sharedViewModel.listaDeProductosRemover.size == 0 && sharedViewModel.listaDeCantidadesRemover.size == 0
                    && sharedViewModel.listaDePreciosRemover.size == 0) {
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
                }

                binding.nsvElegirProductoFacturaSalidaEditar.isVisible = true
                binding.tvProductosAnadidosFacturaSalidaEditar.isVisible = true
                binding.llMontoFacturaSalidaEditar.isVisible = true

                Handler(Looper.getMainLooper()).postDelayed({

                    binding.tvMontoFacturaSalidaEditar.text =
                        sharedViewModel.listaDePreciosDeProductosRemover.sum().toString()

                }, 300)
                sharedViewModel.listaDePreciosDeProductos.clear()
                adapter.notifyDataSetChanged()
                binding.rvElegirProductoFacturaSalidaEditar.requestLayout()


            }, { error ->
                Toast.makeText(
                    requireContext(),
                    "El id es ${sharedViewModel.id.last()}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
        queue1.add(jsonObjectRequest1)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Navegar a MisDatosFragment y pasar un identificador para saber qué fragmento llamar luego
                // val action = EditarClienteFragmentDirections.actionEditarClienteFragmentToMisDatosFragment("clientes")
                val action = EditarFacturaSalidaFragmentDirections.actionNavEditarFacturaSalidaToNavTransacciones(destino = "factura salida")
                findNavController().navigate(action)
                borrarListas()
            }
        })
    }

    fun recyclerViewElegirProducto(listaDeCantidades: MutableList<String>, listaDeProductos: MutableList<String>, listaDePrecios: MutableList<String>) {
        adapter = RemoverInventarioAdapter(
            listaDeCantidades,
            listaDeProductos,
            listaDePrecios,
            sharedViewModel
        ) { position ->
            onDeletedItem(position)
        }
        binding.rvElegirProductoFacturaSalidaEditar.setHasFixedSize(true)
        binding.rvElegirProductoFacturaSalidaEditar.adapter = adapter
        binding.rvElegirProductoFacturaSalidaEditar.layoutManager = LinearLayoutManager(requireContext())
        adapter.updateList(sharedViewModel.listaDeCantidadesRemover,sharedViewModel.listaDeProductosRemover,sharedViewModel.listaDePreciosRemover)
        adapter.notifyDataSetChanged()
        binding.rvElegirProductoFacturaSalidaEditar.requestLayout()
    }

    private fun onDeletedItem(position: Int) {
        //sharedViewModel.opcionesListSalida.add(position,sharedViewModel.reponedorListSalida[position])
        //sharedViewModel.listaDeCantidadesRemover.removeAt(position)
       // sharedViewModel.listaDeProductosRemover.removeAt(position)
        try {
            val producto = sharedViewModel.listaDeProductosRemover.removeAt(position)
            val cantidad = sharedViewModel.listaDeCantidadesRemover.removeAt(position).toInt()
            sharedViewModel.listaDePreciosRemover.removeAt(position)
            ordenarListas()
            sharedViewModel.listaCombinadaSalida.add(Pair(producto, cantidad))
            adapter.updateList(
                sharedViewModel.listaDeCantidadesRemover,
                sharedViewModel.listaDeProductosRemover,
                sharedViewModel.listaDePreciosRemover
            )
            adapter.notifyItemRemoved(position)
            adapter.notifyDataSetChanged()
            binding.rvElegirProductoFacturaSalidaEditar.requestLayout()
            Handler(Looper.getMainLooper()).postDelayed({
                binding.tvMontoFacturaSalidaEditar.text =
                    sharedViewModel.listaDePreciosDeProductosRemover.sum().toString()
                sharedViewModel.facturaTotalSalida =
                    sharedViewModel.listaDePreciosDeProductosRemover
            }, 300)
            sharedViewModel.listaDePreciosDeProductosRemover.clear()
            binding.tvProductosAnadidosFacturaSalidaEditar.isVisible =
                !(adapter.listaDeCantidadesRemover.size == 0 || adapter.listaDeProductosRemover.size == 0 || adapter.listaDePreciosRemover.size == 0)
            binding.llMontoFacturaSalidaEditar.isVisible =
                !(adapter.listaDeCantidadesRemover.size == 0 || adapter.listaDeProductosRemover.size == 0 || adapter.listaDePreciosRemover.size == 0)
        }catch (e:Exception){
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

    private fun actualizarFacturaDeEntrada() {
        if (TextNombre?.text.toString().isNotBlank()) {
            val url1 =
                "http://186.64.123.248/Reportes/Transacciones/FacturaSalida/actualizarFacturaSalida.php" // Reemplaza esto con tu URL de la API
            val queue1 = Volley.newRequestQueue(requireContext())
            val stringRequest = object : StringRequest(
                Request.Method.POST,
                url1,
                { response ->
                    if (binding.etNombreFacturaSalidaEditar.text.isNotBlank() && binding.tvListaDesplegableAlmacenEditar.text.toString() != "Elija una opción" && binding.tvListaDesplegableClienteEditar.text.toString() != "Elija una opción") {
                        //   Handler(Looper.getMainLooper()).postDelayed({
                        eliminarProductos()
                        removerInventario()
                        //   }, 2500)
                        Handler(Looper.getMainLooper()).postDelayed({
                            InsertarPreciosYCantidades()
                        }, 200)
                        binding.tvProductosAnadidosFacturaSalidaEditar.isVisible =
                            !(adapter.listaDeCantidadesRemover.size == 0 && adapter.listaDeProductosRemover.size == 0 && adapter.listaDePreciosRemover.size == 0)
                    }

                    Toast.makeText(
                        requireContext(),
                        "Factura actualizada exitosamente. El id de ingreso es el número ${sharedViewModel.id.last()} ",
                        Toast.LENGTH_SHORT
                    ).show()


                },
                { error ->
                    Toast.makeText(requireContext(), "$error", Toast.LENGTH_SHORT).show()
                    //Toast.makeText(requireContext(),"Error $error", Toast.LENGTH_SHORT).show()
                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    val parametros = HashMap<String, String>()
                    parametros.put("ID_FACTURA_SALIDA", sharedViewModel.id.last())
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
        }else {
            Toast.makeText(
                requireContext(),
                "El nombre de la factura de salida es obligatorio",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun InsertarPreciosYCantidades() {
        //INICIO EXPERIMIENTO!!!!!!!!!!!!!!!!!!!!!!!!!! (FUNCIONO)
        val url1 =
            "http://186.64.123.248/Reportes/Transacciones/FacturaSalida/insertarProductosFacturaSalida.php" // Reemplaza esto con tu URL de la API
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
                TextFecha?.setText(
                    SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(
                        Calendar.getInstance().time
                    )
                )
                /*TextNombre?.setText("")
                DropDownCliente?.setText("Elija una opción", false)
                DropDownAlmacen?.setText("Elija una opción", false)
                TextComentarios?.setText("")
                sharedViewModel.listaDeCantidadesRemover.clear()
                sharedViewModel.listaDeProductosRemover.clear()
                sharedViewModel.listaDePreciosRemover.clear()
                sharedViewModel.listaDePreciosDeProductosRemover.clear()
                adapter.notifyDataSetChanged()
                binding.rvElegirProductoFacturaSalidaEditar.requestLayout()
                binding.tvProductosAnadidosFacturaSalidaEditar.isVisible = false
                binding.nsvElegirProductoFacturaSalidaEditar.isVisible = false
                binding.tvMontoFacturaSalidaEditar.text =
                    sharedViewModel.listaDePreciosDeProductosRemover.sum().toString()
                binding.llMontoFacturaSalidaEditar.isVisible =
                    !(adapter.listaDeCantidadesRemover.size == 0 || adapter.listaDeProductosRemover.size == 0 || adapter.listaDePreciosRemover.size == 0)
                sharedViewModel.opcionesListSalida.clear()*/
            },
            { error ->
                Toast.makeText(
                    requireContext(),
                    "Productos editados exitosamente a la factura",
                    Toast.LENGTH_SHORT
                ).show()
                TextFecha?.setText(
                    SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(
                        Calendar.getInstance().time
                    )
                )
               /* TextNombre?.setText("")
                DropDownCliente?.setText("Elija una opción", false)
                DropDownAlmacen?.setText("Elija una opción", false)
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
                sharedViewModel.opcionesListSalida.clear()*/
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("ID_FACTURA_SALIDA", sharedViewModel.id.last())
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

    fun modificarInventario() {
        //Si el inventario es menor que la transferencia que no la haga y si el almacen de destino no existe, que lo cree
        val url1 = "http://186.64.123.248/Reportes/Transacciones/FacturaSalida/removerInventarioFacturaSalida.php" // Reemplaza esto con tu URL de la API
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
                DropDownCliente?.setText("Elija una opción", false)
                DropDownAlmacen?.setText("Elija una opción", false)
                TextComentarios?.setText("")
                sharedViewModel.listaDeCantidadesRemover.clear()
                sharedViewModel.listaDeProductosRemover.clear()
                sharedViewModel.listaDePreciosRemover.clear()
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
                          DropDownOrigen?.setText("Elija una opción",false)
                          DropDownDestino?.setText("Elija una opción",false)
                          TextComentarios?.setText("")
                          TextCodigoDeBarra?.setText("")*/
                Toast.makeText(
                    requireContext(),
                    "Error $error y ${sharedViewModel.listaDeProductos} y ${sharedViewModel.listaDeCantidades}",
                    Toast.LENGTH_SHORT
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

    fun removerInventario() {
        //Si el inventario es menor que la transferencia que no la haga y si el almacen de destino no existe, que lo cree
        val url1 =
            "http://186.64.123.248/Reportes/Transacciones/FacturaSalida/removerInventarioFacturaSalidaTotal.php" // Reemplaza esto con tu URL de la API
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
                parametros.put("FACTURA_SALIDA",TextNombre?.text.toString())
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

     private fun eliminarProductos() {
        val url1 = "http://186.64.123.248/Reportes/Transacciones/FacturaSalida/eliminarProductosEditarFacturaSalida.php" // Reemplaza esto con tu URL de la API
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
                parametros.put("FACTURA_SALIDA",TextNombre?.text.toString())
                parametros.put("NUMERO_DE_PRODUCTOS", sharedViewModel.listaDeProductosRemover.size.toString())
                parametros.put("ALMACEN",DropDownAlmacen?.text.toString())
                for (i in 0..<sharedViewModel.listaDeProductosRemover.size) {
                    parametros["PRODUCTO$i"] = sharedViewModel.listaDeProductosRemover[i].uppercase()
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
                sharedViewModel.opcionesListSalidaAlmacen.clear()
               if(sharedViewModel.opcionesListSalidaAlmacen.isEmpty()) {
                   for (i in 0 until jsonArray.length()) {
                       sharedViewModel.opcionesListSalidaAlmacen.add(jsonArray.getString(i).removeSurrounding("'", "'"))
                   }
               }
                sharedViewModel.opcionesListSalidaAlmacen.sort()
                //Crea un adpatador para el dropdown
                val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_dropdown_item_1line,sharedViewModel.opcionesListSalidaAlmacen)
                //binding.tvholaMundo?.setText(response.getString("Lista"))
                DropDownAlmacen?.setAdapter(adapter)
                DropDownAlmacen?.threshold = 1
                DropDownAlmacen?.onItemClickListener =
                    AdapterView.OnItemClickListener { parent, view, position, id ->
                        sharedViewModel.listaDeProductosRemover.clear()
                        sharedViewModel.listaDeCantidadesRemover.clear()
                        sharedViewModel.listaDePreciosRemover.clear()
                        binding.tvProductosAnadidosFacturaSalidaEditar.isVisible = sharedViewModel.listaDeProductosRemover.size != 0
                        binding.llMontoFacturaSalidaEditar.isVisible = sharedViewModel.listaDeProductosRemover.size != 0
                        binding.nsvElegirProductoFacturaSalidaEditar.isVisible = false
                        binding.rvElegirProductoFacturaSalidaEditar.requestLayout()
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
                    else if (!hasFocus && !sharedViewModel.opcionesListSalidaAlmacen.contains(DropDownAlmacen?.text.toString())){
                        Toast.makeText(requireContext(),"El nombre del almacén no es válido", Toast.LENGTH_SHORT).show()
                    }
                }
            }, { error ->
                Toast.makeText(
                    requireContext(),
                    " La aplicación no se ha conectado con el servidor",
                    Toast.LENGTH_SHORT
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
                sharedViewModel.opcionesListSalidaCliente.clear()
               if(sharedViewModel.opcionesListSalidaCliente.isEmpty()) {
                   for (i in 0 until jsonArray.length()) {
                       sharedViewModel.opcionesListSalidaCliente.add(
                           jsonArray.getString(i).removeSurrounding("'", "'"))
                   }
               }
                sharedViewModel.opcionesListSalidaCliente.sort()
                //Crea un adpatador para el dropdown
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line,sharedViewModel.opcionesListSalidaCliente)
                //binding.tvholaMundo?.setText(response.getString("Lista"))
                DropDownCliente?.setAdapter(adapter)
                DropDownCliente?.threshold = 1
                DropDownCliente?.onItemClickListener =
                    AdapterView.OnItemClickListener { parent, view, position, id ->
                        val itemSelected = parent.getItemAtPosition(position)
                    }
                DropDownCliente?.setOnClickListener {
                    if(DropDownCliente?.text.toString() == "Elija una opción"){
                        binding.tvListaDesplegableClienteEditar.setText("",false)
                        DropDownCliente?.showDropDown()
                    }
                }
                DropDownCliente?.setOnFocusChangeListener { _, hasFocus ->
                    if(hasFocus && DropDownCliente?.text.toString() == "Elija una opción"){
                        binding.tvListaDesplegableClienteEditar.setText("",false)
                        DropDownCliente?.showDropDown()
                    }
                    else if (!hasFocus && !sharedViewModel.opcionesListSalidaCliente.contains(DropDownCliente?.text.toString())){
                        Toast.makeText(requireContext(),"El nombre del cliente no es válido", Toast.LENGTH_SHORT).show()
                    }
                }
            }, { error ->
                Toast.makeText(
                    requireContext(),
                    " La aplicación no se ha conectado con el servidor",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
        queue1.add(jsonObjectRequest1)
    }

    private fun mensajeEliminarFacturaSalida(){
        AlertDialog.Builder(context).apply {
            setTitle("¿Quieres eliminar esta factura de salida?")
            setMessage("Esta acción no se puede deshacer.")
            setPositiveButton("Sí") { dialog, _ ->
                eliminarFacturaSalida()
                findNavController().navigate(R.id.action_nav_editar_factura_salida_to_nav_transacciones)
                dialog.dismiss()
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }

    }
    private fun eliminarFacturaSalida(){
        val url = "http://186.64.123.248/Reportes/Transacciones/FacturaSalida/borrar.php"
        val queue = Volley.newRequestQueue(requireContext())
        var jsonObjectRequest = object : StringRequest(Request.Method.POST, url,
            { response ->
                Toast.makeText(requireContext(), "Factura de salida eliminada exitosamente", Toast.LENGTH_SHORT).show()

            },{error ->
                Toast.makeText(requireContext(), "No se pudo eliminar la factura de salida", Toast.LENGTH_SHORT).show()
            })
        {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("ID_FACTURA_SALIDA", sharedViewModel.id.last())
                return parametros
            }
        }
        queue.add(jsonObjectRequest)
    }

    private fun ordenarListas(){
        val listaCombinada = sharedViewModel.listaDeProductosRemover.indices.map{ i ->
            Triple(sharedViewModel.listaDeProductosRemover[i], sharedViewModel.listaDeCantidadesRemover[i], sharedViewModel.listaDePreciosRemover[i])
        }
        val listaOrdenada = listaCombinada.sortedBy { it.first }

        sharedViewModel.listaDeProductosRemover = listaOrdenada.map { it.first }.toMutableList()
        sharedViewModel.listaDeCantidadesRemover = listaOrdenada.map { it.second }.toMutableList()
        sharedViewModel.listaDePreciosRemover = listaOrdenada.map { it.third }.toMutableList()

        adapter.notifyDataSetChanged()
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