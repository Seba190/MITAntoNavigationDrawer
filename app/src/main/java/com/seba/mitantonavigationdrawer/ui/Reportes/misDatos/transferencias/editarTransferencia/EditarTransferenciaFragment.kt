package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transferencias.editarTransferencia

import android.annotation.SuppressLint
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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentEditarTransferenciaBinding
import com.seba.mitantonavigationdrawer.databinding.FragmentEstadisticaBinding
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirTransferencia.AnadirTransferenciaAdapter
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirTransferencia.ElegirProductoFragment
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.MisDatosFragmentArgs
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transferencias.TransferenciasFragmentArgs
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import com.seba.mitantonavigationdrawer.ui.estadística.EstadisticaViewModel
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EditarTransferenciaFragment : Fragment(R.layout.fragment_editar_transferencia) {
    private var _binding: FragmentEditarTransferenciaBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel by activityViewModels<SharedViewModel>()
    private val args: TransferenciasFragmentArgs by navArgs()
    var TextNombre: EditText? = null
    var TextFecha: EditText? = null
    var TextComentarios: EditText? = null
    var DropDownOrigen: AutoCompleteTextView? = null
    var DropDownDestino: AutoCompleteTextView? = null
    private var requestCamara: ActivityResultLauncher<String>? = null
    private var CodigoDeBarra: EditText? = null
    private val ListaDeProductos: MutableList<String> = mutableListOf()
    private val ListaDeCantidades: MutableList<String> = mutableListOf()
    private val ListaDePreciosUnidad: MutableList<String> = mutableListOf()
    private val ListaDePreciosCajas: MutableList<String> = mutableListOf()
    private val ListaDeProductosAnadidos: MutableList<CardView> = mutableListOf()
    //Me lanza una excepcion si descomento estas dos listas
    // private val listaAntiguaProductos = sharedViewModel.listaDeProductos
    // private val listaAntiguaCantidades = sharedViewModel.listaDeCantidades
    private lateinit var adapter: AnadirTransferenciaAdapter
    private var cantidad: String = ""
    private var exceso: String? = ""
    var EliminarTransferencia: ImageView? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val editarTransferenciaViewModel =
            ViewModelProvider(this).get(EditarTransferenciaViewModel::class.java)

        _binding = FragmentEditarTransferenciaBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa
        TextNombre = binding.etNombreTransferencia.findViewById(R.id.etNombreTransferencia)
        TextFecha = binding.etFechaTransferencia.findViewById(R.id.etFechaTransferencia)
        EliminarTransferencia = binding.ivEliminarTransferencia.findViewById(R.id.ivEliminarTransferencia)
        TextComentarios =
            binding.etTransferenciaComentarios.findViewById(R.id.etTransferenciaComentarios)
        DropDownOrigen =
            binding.tvListaDesplegableAlmacenOrigen.findViewById(R.id.tvListaDesplegableAlmacenOrigen)
        DropDownDestino =
            binding.tvListaDesplegableAlmacenDestino.findViewById(R.id.tvListaDesplegableAlmacenDestino)

        //Poner los edit text con sombra gris
        binding.etNombreTransferencia.getBackground().setColorFilter(
            getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP
        )
        binding.etFechaTransferencia.getBackground().setColorFilter(
            getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP
        )
        binding.etTransferenciaComentarios.getBackground().setColorFilter(
            getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP
        )
        ListaDesplegableOrigen()
        ListaDesplegableDestino()

        EliminarTransferencia?.setOnClickListener {
            mensajeEliminarTransferencia()
        }

        binding.nsvElegirProducto.isVisible = true
        binding.bAnadirNuevoProducto.setOnClickListener {
            if (sharedViewModel.opcionesListEditarTransferenciaOrigen.contains(DropDownOrigen?.text.toString()) &&
                sharedViewModel.opcionesListEditarTransferenciaDestino.contains(DropDownDestino?.text.toString())) {
                sharedViewModel.almacen = DropDownOrigen?.text.toString()
                sharedViewModel.listaDeAlmacenesEditarTransferencia.add(DropDownOrigen?.text.toString())
                binding.nsvElegirProducto.isVisible = true
                //Aquí se obtiene el id de la transferencia actual
                if (TextNombre?.text.toString().isNotBlank()) {
                    val queue = Volley.newRequestQueue(requireContext())
                    val url = "http://186.64.123.248/Reportes/Transferencias/registro.php"
                    val jsonObjectRequest = object : StringRequest(
                        Request.Method.POST, url,
                        { response ->
                            if (TextNombre?.text.toString().isNotBlank()) {
                                val id = JSONObject(response).getString("ID_TRANSFERENCIA")
                                setFragmentResult(
                                    "ID_TRANSFERENCIA", bundleOf(
                                        "id_transferencia" to id.toString()
                                    )
                                )
                                //unico = 1
                                //no unico = 0
                              //  val unico = JSONObject(response).getString("TRANSFERENCIA_UNICA")
                                Toast.makeText(requireContext(), "El id de ingreso es $id", Toast.LENGTH_SHORT).show()
                            }
                        }, { error ->
                            Toast.makeText(
                                requireContext(),
                                "Esta cambiando la transferencia a ${TextNombre?.text.toString().uppercase()}",
                                Toast.LENGTH_SHORT
                            ).show()
                            //Toast.makeText(requireContext(),"Error $error", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        override fun getParams(): MutableMap<String, String> {
                            val parametros = HashMap<String, String>()
                            parametros.put("TRANSFERENCIA", TextNombre?.text.toString().uppercase())
                            return parametros
                        }
                    }
                    queue.add(jsonObjectRequest)

                }
                val elegirProductoEditarTransferenciaFragment = ElegirProductoEditarTransferenciaFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.clEditarTransferencia, elegirProductoEditarTransferenciaFragment)
                    .commit()
                binding.tvProductosAnadidos.isVisible = true

                // binding.tvProductosAnadidos.isVisible = !(adapter.listaDeCantidades.size == 0 && adapter.listaDeProductos.size == 0)
            } else if((!sharedViewModel.opcionesListEditarTransferenciaOrigen.contains(DropDownOrigen?.text.toString()) ||
                        !sharedViewModel.opcionesListEditarTransferenciaDestino.contains(DropDownDestino?.text.toString())) &&
                (DropDownOrigen?.text.toString() == "Elija una opción" &&
                        DropDownDestino?.text.toString() == "Elija una opción")) {
                Toast.makeText(requireContext(), "Elija el almacén de origen y de destino", Toast.LENGTH_SHORT).show()
            } else if((!sharedViewModel.opcionesListEditarTransferenciaOrigen.contains(DropDownOrigen?.text.toString()) ||
                        !sharedViewModel.opcionesListEditarTransferenciaDestino.contains(DropDownDestino?.text.toString())) &&
                (DropDownOrigen?.text.toString() != "Elija una opción" ||
                        DropDownDestino?.text.toString() != "Elija una opción")){
                Toast.makeText(requireContext(),"El nombre del almacén de origen o destino no es válido", Toast.LENGTH_SHORT).show()
            }

        }
        binding.TransferenciaButtonEnviar.setOnClickListener {
                if ((sharedViewModel.listaDeProductos.size > 0 && sharedViewModel.listaDeCantidades.size > 0) &&
                    sharedViewModel.opcionesListEditarTransferenciaOrigen.contains(DropDownOrigen?.text.toString()) &&
                    sharedViewModel.opcionesListEditarTransferenciaDestino.contains(DropDownDestino?.text.toString())) {
                    ValidacionesIdInsertarDatos()
                    adapter.notifyDataSetChanged()
                    binding.rvElegirProducto.requestLayout()
                }else if(sharedViewModel.listaDeProductos.size == 0 && sharedViewModel.listaDeCantidades.size == 0) {
                    Toast.makeText(requireContext(), "Tiene que elegir al menos un producto", Toast.LENGTH_SHORT).show()
                } else if(!sharedViewModel.opcionesListEditarTransferenciaOrigen.contains(DropDownOrigen?.text.toString()) ||
                    !sharedViewModel.opcionesListEditarTransferenciaDestino.contains(DropDownDestino?.text.toString())) {
                    Toast.makeText(requireContext(), "El almacén de origen o de destino no es válido", Toast.LENGTH_SHORT).show()
                } else if(DropDownOrigen?.text.toString() == "Elija una opción" ||
                    DropDownDestino?.text.toString() == "Elija una opción"){
                    Toast.makeText(requireContext(),"Debe elegir el almacén de origen y de destino", Toast.LENGTH_SHORT).show()
                }

        }

        requestCamara = registerForActivityResult(ActivityResultContracts.RequestPermission(),) {
            if (it) {
                findNavController().navigate(R.id.action_nav_editar_transferencias_to_nav_barcode_scan_editar_transferencia)
            } else {
                Toast.makeText(requireContext(), "Permiso denegado", Toast.LENGTH_SHORT).show()
            }
        }
        binding.bEscanearCodigoDeBarra.setOnClickListener {
            requestCamara?.launch(android.Manifest.permission.CAMERA)
        }

        //  CodigoDeBarra = activity?.findViewById(R.id.txtBarcodeValue)
        // if(CodigoDeBarra?.text!!.isNullOrBlank()){

        //    binding.etCodigoDeBarra.setText(CodigoDeBarra!!.text)

        // }
        // var EditTextEmpty = binding.etCodigoDeBarra.text.toString()
        // val action = AnadirTransferenciaFragmentDirections.actionNavAñadirTransferenciaToNavBarcodeScan(EditTextEmpty)
        // findNavController().navigate(action)
        /* parentFragmentManager.setFragmentResultListener("Codigo de barra transferencia",this) { key, bundle ->
            binding.bAnadirNuevoProducto.setOnClickListener {
                binding.etCodigoDeBarra.setText(bundle.getString("codigo"))
            }
           }*/
        binding.tvProductosAnadidos.isVisible = false

        val productosOrdenados = sharedViewModel.listaDeProductos.sorted().toMutableList()
        val listaCombinada = sharedViewModel.listaDeProductos.zip(sharedViewModel.listaDeCantidades)
        val listaOrdenadaCombinada = listaCombinada.sortedBy { it.first }
        val cantidadesOrdenadas = listaOrdenadaCombinada.map { it.second }.toMutableList()
        recyclerViewElegirProducto(cantidadesOrdenadas,productosOrdenados)
       // var segundaVez = false
        binding.bActualizarRecyclerView.setOnClickListener {
            val productosOrdenadosActualizados = sharedViewModel.listaDeProductos.sorted().toMutableList()
            val listaCombinadaActualizada = sharedViewModel.listaDeProductos.zip(sharedViewModel.listaDeCantidades)
            val listaOrdenadaCombinadaActualizada = listaCombinadaActualizada.sortedBy { it.first }
            val cantidadesOrdenadasActualizada = listaOrdenadaCombinadaActualizada.map { it.second}.toMutableList()
            adapter.updateList(cantidadesOrdenadasActualizada,productosOrdenadosActualizados)
            binding.tvProductosAnadidos.isVisible = !(sharedViewModel.listaDeCantidades.size == 0 && sharedViewModel.listaDeProductos.size == 0)
            adapter.notifyDataSetChanged()
            binding.nsvElegirProducto.isVisible = !(sharedViewModel.listaDeCantidades.size == 0 && sharedViewModel.listaDeProductos.size == 0)
            binding.rvElegirProducto.requestLayout()
           /* if(!segundaVez) {
                for (i in 0..<sharedViewModel.listaDeCantidades.size) {
                    if ((sharedViewModel.listaDeCantidadesAntigua.size < sharedViewModel.listaDeCantidades.size) ||
                        (sharedViewModel.listaDeProductosAntigua.size < sharedViewModel.listaDeProductos.size)
                    ) {
                        sharedViewModel.listaDeCantidadesAntigua.add(sharedViewModel.listaDeCantidades[i])
                        sharedViewModel.listaDeProductosAntigua.add(sharedViewModel.listaDeProductos[i])
                    }

                }
                segundaVez = true
            }
            //preguntarInventario()
            Handler(Looper.getMainLooper()).postDelayed({
                for (i in 0..<sharedViewModel.listaDeCantidades.size) {
                    if(sharedViewModel.listaDeCantidadesAntigua[i] != sharedViewModel.listaDeCantidades[i]){
                        sharedViewModel.listaDeCantidadesAntigua[i] = sharedViewModel.listaDeCantidades[i]
                    }
                    if(sharedViewModel.listaDeProductosAntigua[i] != sharedViewModel.listaDeProductos[i]){
                        sharedViewModel.listaDeProductosAntigua[i] = sharedViewModel.listaDeProductos[i]
                    }
                }
            }, 500)*/

            /* Log.i(
                 "Sebastian",
                 "${sharedViewModel.listaDeProductos} , ${sharedViewModel.listaDeCantidades},${sharedViewModel.listaDeProductosAntigua} y ${sharedViewModel.listaDeCantidadesAntigua}")*/

        }
        binding.tvProductosAnadidos.isVisible = sharedViewModel.listaDeCantidades.isNotEmpty()

        if (TextFecha?.text?.isBlank() == true) {
            val fecha = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Calendar.getInstance().time)
            binding.etFechaTransferencia.setText(fecha.format(Date()))
        }
        try {
            sharedViewModel.id.add(args.id)
        }
        catch(e:Exception){
        }

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val queue1 = Volley.newRequestQueue(requireContext())
        Log.i("Sebastian", "Valor de Id de destino es: ${sharedViewModel.id.last()}")
        val url1 = "http://186.64.123.248/Reportes/Transferencias/registroInsertarTransferencia.php?ID_TRANSFERENCIA=${sharedViewModel.id.last()}"
        val jsonObjectRequest1 = JsonObjectRequest(
            Request.Method.GET, url1, null,
            { response ->
                Log.i("Sebastian","ID: ${sharedViewModel.id.last()}" )
                TextNombre?.setText(response.getString("TRANSFERENCIA"))
                TextFecha?.setText(response.getString("FECHA_TRANSFERENCIA"))
                DropDownOrigen?.setText(response.getString("ALMACEN_ORIGEN"),false)
                DropDownDestino?.setText(response.getString("ALMACEN_DESTINO"),false)
                TextComentarios?.setText(response.getString("COMENTARIOS"))

                val ListaDeProductos = response.getJSONArray("PRODUCTOS")
                if(sharedViewModel.listaDeProductos.size == 0 && sharedViewModel.listaDeCantidades.size == 0) {
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
                       Log.i("Sebastian", "PRODUCTO: $producto")
                       Log.i("Sebastian", "CANTIDAD: $cantidad")
                       if (!sharedViewModel.listaDeProductos.contains("$producto ( 0 unid. )")) {
                           sharedViewModel.listaDeProductos.add("$producto ( 0 unid. )")
                           sharedViewModel.listaDeCantidades.add(cantidad)
                       }
                   }
               }
                /*if(!sharedViewModel.listaDeProductos.contains(response.getString("PRODUCTO").toString()) &&
                    !sharedViewModel.listaDeCantidades.contains(response.getString("CANTIDAD").toString())) {
                    sharedViewModel.listaDeCantidades.add(response.getString("CANTIDAD").toString())
                    sharedViewModel.listaDeProductos.add("${response.getString("PRODUCTO")} ( 0 unid. )")
                }*/

                Handler(Looper.getMainLooper()).postDelayed({
                    binding.nsvElegirProducto.isVisible = true
                    binding.tvProductosAnadidos.isVisible = !(sharedViewModel.listaDeCantidades.size == 0 && sharedViewModel.listaDeProductos.size == 0)
                    adapter.updateList(sharedViewModel.listaDeCantidades, sharedViewModel.listaDeProductos)
                    adapter.notifyDataSetChanged()
                    binding.rvElegirProducto.requestLayout()

                }, 500)


            }, { error ->
                Toast.makeText(requireContext(), "El id es ${sharedViewModel.id.last()}", Toast.LENGTH_SHORT).show()
            }
        )
        queue1.add(jsonObjectRequest1)
    }

    fun updateData(dataCantidad: MutableList<String>, dataProducto: MutableList<String>) {
        adapter.updateList(dataCantidad, dataProducto)
        binding.rvElegirProducto.adapter?.notifyDataSetChanged()
        binding.rvElegirProducto.requestLayout()
    }

    /* fun refreshAdapter(){
       adapter.notifyDataSetChanged()
        binding.rvElegirProducto.requestLayout()
    }*/

    fun recyclerViewElegirProducto(listaDeCantidades: MutableList<String>, listaDeProductos: MutableList<String>) {
        adapter = AnadirTransferenciaAdapter(listaDeCantidades, listaDeProductos, sharedViewModel) { position ->onDeletedItem(position) }
        binding.rvElegirProducto.setHasFixedSize(true)
        binding.rvElegirProducto.adapter = adapter
        binding.rvElegirProducto.layoutManager = LinearLayoutManager(requireContext())
        activity?.runOnUiThread{
            adapter.notifyDataSetChanged()
            binding.rvElegirProducto.requestLayout()
        }
        binding.tvProductosAnadidos.isVisible =
            !(adapter.listaDeCantidades.size == 0 && adapter.listaDeProductos.size == 0)
    }

    private fun onDeletedItem(position: Int) {
        sharedViewModel.listaDeCantidades.removeAt(position)
        sharedViewModel.listaDeProductos.removeAt(position)
        adapter.notifyItemRemoved(position)
        adapter.notifyDataSetChanged()
        binding.rvElegirProducto.requestLayout()
    }

    private fun ListaDesplegableDestino() {
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 = "http://186.64.123.248/Transferencia/almacenDestino.php"
        val jsonObjectRequest1 = JsonObjectRequest(
            Request.Method.GET, url1, null,
            { response ->
                // Obtén el array de opciones desde el objeto JSON
                val jsonArray = response.getJSONArray("Lista")
                // Convierte el array JSON a una lista mutable
                if(sharedViewModel.opcionesListEditarTransferenciaDestino.isEmpty()) {
                    for (i in 0 until jsonArray.length()) {
                        sharedViewModel.opcionesListEditarTransferenciaDestino.add(jsonArray.getString(i).removeSurrounding("'", "'"))
                    }
                }
                sharedViewModel.opcionesListEditarTransferenciaDestino.sort()
                //Crea un adpatador para el dropdown
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sharedViewModel.opcionesListTransferenciaDestino)
                //binding.tvholaMundo?.setText(response.getString("Lista"))
                DropDownDestino?.setAdapter(adapter)
                DropDownDestino?.threshold = 1
                DropDownDestino?.onItemClickListener =
                    AdapterView.OnItemClickListener { parent, view, position, id ->
                        val itemSelected = parent.getItemAtPosition(position)
                    }
                DropDownDestino?.setOnClickListener {
                    if(DropDownDestino?.text.toString() == "Elija una opción"){
                        binding.tvListaDesplegableAlmacenDestino.setText("",false)
                        DropDownDestino?.showDropDown()
                    }
                }
                DropDownDestino?.setOnFocusChangeListener { _, hasFocus ->
                    if(hasFocus && DropDownDestino?.text.toString() == "Elija una opción"){
                        binding.tvListaDesplegableAlmacenDestino.setText("",false)
                        DropDownDestino?.showDropDown()
                    }
                    else if (!hasFocus && !sharedViewModel.opcionesListEditarTransferenciaDestino.contains(DropDownDestino?.text.toString())){
                        Toast.makeText(requireContext(),"El nombre del almacén de destino no es válido", Toast.LENGTH_SHORT).show()
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

    private fun ListaDesplegableOrigen() {
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 = "http://186.64.123.248/Transferencia/almacenOrigen.php"
        val jsonObjectRequest1 = JsonObjectRequest(
            Request.Method.GET, url1, null,
            { response ->
                // Obtén el array de opciones desde el objeto JSON
                val jsonArray = response.getJSONArray("Lista")
                // Convierte el array JSON a una lista mutable
                if(sharedViewModel.opcionesListEditarTransferenciaOrigen.isEmpty()) {
                    for (i in 0 until jsonArray.length()) {
                        sharedViewModel.opcionesListEditarTransferenciaOrigen.add(jsonArray.getString(i).removeSurrounding("'", "'"))
                    }
                }
                sharedViewModel.opcionesListEditarTransferenciaOrigen.sort()
                //Crea un adpatador para el dropdown
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sharedViewModel.opcionesListEditarTransferenciaOrigen)
                //binding.tvholaMundo?.setText(response.getString("Lista"))
                DropDownOrigen?.setAdapter(adapter)
                DropDownOrigen?.threshold = 1
                DropDownOrigen?.onItemClickListener =
                    AdapterView.OnItemClickListener { parent, view, position, id ->
                        sharedViewModel.listaDeProductos.clear()
                        sharedViewModel.listaDeCantidades.clear()
                        binding.tvProductosAnadidos.isVisible = sharedViewModel.listaDeProductos.size != 0
                        binding.nsvElegirProducto.isVisible = false
                        binding.rvElegirProducto.requestLayout()
                        val itemSelected = parent.getItemAtPosition(position)
                    }
                DropDownOrigen?.setOnClickListener {
                    if(DropDownOrigen?.text.toString() == "Elija una opción"){
                        binding.tvListaDesplegableAlmacenOrigen.setText("",false)
                        DropDownOrigen?.showDropDown()
                    }
                }
                DropDownOrigen?.setOnFocusChangeListener { _, hasFocus ->
                    if(hasFocus && DropDownOrigen?.text.toString() == "Elija una opción"){
                        binding.tvListaDesplegableAlmacenOrigen.setText("",false)
                        DropDownOrigen?.showDropDown()
                    }
                    else if (!hasFocus && !sharedViewModel.opcionesListEditarTransferenciaOrigen.contains(DropDownOrigen?.text.toString())){
                        Toast.makeText(requireContext(),"El nombre del almacén de origen no es válido", Toast.LENGTH_SHORT).show()
                    }
                }
            }, { error ->
                //Toast.makeText(requireContext(), " La aplicación no se ha conectado con el servidor", Toast.LENGTH_SHORT).show()
            }
        )
        queue1.add(jsonObjectRequest1)
    }

    private fun ValidacionesIdInsertarDatos() {
        if (TextNombre?.text.toString().isNotBlank()) {
        val url1 = "http://186.64.123.248/Reportes/Transferencias/actualizarTransferencia.php" // Reemplaza esto con tu URL de la API
        val queue1 = Volley.newRequestQueue(requireContext())
        val stringRequest = object : StringRequest(
            Request.Method.POST,
            url1,
            { response ->
                if (binding.etNombreTransferencia.text.isNotBlank() && binding.tvListaDesplegableAlmacenDestino.text.toString() != "Elija una opción" && binding.tvListaDesplegableAlmacenOrigen.text.toString() != "Elija una opción") {
                    //    Handler(Looper.getMainLooper()).postDelayed({
                    eliminarProductos()
                    modificarInventarioTotal()
                    //    }, 2500)
                    Handler(Looper.getMainLooper()).postDelayed({
                        InsertarPreciosYCantidades()
                    }, 500)
                    binding.tvProductosAnadidos.isVisible = !(adapter.listaDeCantidades.size == 0 && adapter.listaDeProductos.size == 0)
                }

                Toast.makeText(
                    requireContext(),
                    "Transferencia agregada exitosamente. El id de ingreso es el número ${sharedViewModel.id.last()} ",
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
                parametros.put("ID_TRANSFERENCIA", sharedViewModel.id.last())
                parametros.put("TRANSFERENCIA", TextNombre?.text.toString().uppercase())
                parametros.put("FECHA_TRANSFERENCIA", TextFecha?.text.toString().uppercase())
                parametros.put("ALMACEN_ORIGEN", DropDownOrigen?.text.toString())
                parametros.put("ALMACEN_DESTINO", DropDownDestino?.text.toString())
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
        } else {
            Toast.makeText(
                requireContext(),
                "El nombre de la transferencia es obligatorio",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun eliminarProductos() {
        val url1 = "http://186.64.123.248/Reportes/Transferencias/eliminarProductosEditarTransferencia.php" // Reemplaza esto con tu URL de la API
        val queue1 = Volley.newRequestQueue(requireContext())
        val stringRequest = object : StringRequest(
            Request.Method.POST,
            url1,
            { response ->
                Toast.makeText(
                    requireContext(),
                    "Transferencia e inventario modificados exitosamente",
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
                parametros.put("TRANSFERENCIA",TextNombre?.text.toString())
                parametros.put("NUMERO_DE_PRODUCTOS", sharedViewModel.listaDeProductos.size.toString())
                parametros.put("ALMACEN_ORIGEN",DropDownOrigen?.text.toString())
                parametros.put("ALMACEN_DESTINO",DropDownDestino?.text.toString())
                for (i in 0..<sharedViewModel.listaDeProductos.size) {
                    parametros["PRODUCTO$i"] = sharedViewModel.listaDeProductos[i].uppercase()
                }

                return parametros
            }
        }
        queue1.add(stringRequest)
    }

    private fun InsertarPreciosYCantidades() {
        val url1 = "http://186.64.123.248/Reportes/Transferencias/insertarProductosTransferenciaTotal.php" // Reemplaza esto con tu URL de la API
        val queue1 = Volley.newRequestQueue(requireContext())
        val stringRequest = object : StringRequest(
            Request.Method.POST,
            url1,
            { response ->
                Toast.makeText(
                    requireContext(),
                    "Productos agregados exitosamente a la transferencia",
                    Toast.LENGTH_SHORT
                ).show()
                TextNombre?.setText("")
                TextFecha?.setText(SimpleDateFormat("dd-MM-yyyy",Locale.getDefault()).format(Calendar.getInstance().time))
                DropDownOrigen?.setText("Elija una opción", false)
                DropDownDestino?.setText("Elija una opción", false)
                TextComentarios?.setText("")
                sharedViewModel.listaDeCantidades.removeAll(sharedViewModel.listaDeCantidades)
                sharedViewModel.listaDeProductos.removeAll(sharedViewModel.listaDeProductos)
                sharedViewModel.listaDeProductosAntigua.removeAll(sharedViewModel.listaDeProductosAntigua)
                adapter.notifyDataSetChanged()
                binding.rvElegirProducto.requestLayout()
                binding.tvProductosAnadidos.isVisible = false
                binding.nsvElegirProducto.isVisible = false
                sharedViewModel.opcionesListEditarTransferencia.clear()
            },
            { error ->
                Toast.makeText(
                    requireContext(),
                    "Productos agregados exitosamente a la transferencia",
                    Toast.LENGTH_SHORT
                ).show()
                TextNombre?.setText("")
                TextFecha?.setText(SimpleDateFormat("dd-MM-yyyy",Locale.getDefault()).format(Calendar.getInstance().time))
                DropDownOrigen?.setText("Elija una opción", false)
                DropDownDestino?.setText("Elija una opción", false)
                TextComentarios?.setText("")
                sharedViewModel.listaDeCantidades.removeAll(sharedViewModel.listaDeCantidades)
                sharedViewModel.listaDeProductos.removeAll(sharedViewModel.listaDeProductos)
                sharedViewModel.listaDeProductosAntigua.removeAll(sharedViewModel.listaDeProductosAntigua)
                adapter.notifyDataSetChanged()
                binding.rvElegirProducto.requestLayout()
                binding.tvProductosAnadidos.isVisible = false
                binding.nsvElegirProducto.isVisible = false
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("ID_TRANSFERENCIA", sharedViewModel.id.last())
                parametros.put("NUMERO_DE_PRODUCTOS", sharedViewModel.listaDeProductos.size.toString())
                for (i in 0..<sharedViewModel.listaDeProductos.size) {
                    parametros["PRODUCTO$i"] = sharedViewModel.listaDeProductos[i].uppercase()
                    parametros["CANTIDAD$i"] = sharedViewModel.listaDeCantidades[i]
                }
                return parametros
            }
        }
        queue1.add(stringRequest)
    }

    fun obtenerMiView(): View {
        return binding.etNombreTransferencia // Tu vista que quieres acceder
    }


    fun modificarInventario() {
        //Si el inventario es menor que la transferencia que no la haga y si el almacen de destino no existe, que lo cree
        val url1 = "http://186.64.123.248/Reportes/Transferencias/modificarInventarioCompleta.php" // Reemplaza esto con tu URL de la API
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
                DropDownOrigen?.setText("Elija una opción", false)
                DropDownDestino?.setText("Elija una opción", false)
                TextComentarios?.setText("")
                sharedViewModel.listaDeCantidades.removeAll(sharedViewModel.listaDeCantidades)
                sharedViewModel.listaDeProductos.removeAll(sharedViewModel.listaDeProductos)
                sharedViewModel.listaDeProductosAntigua.removeAll(sharedViewModel.listaDeProductosAntigua)
                adapter.notifyDataSetChanged()
                binding.rvElegirProducto.requestLayout()
                binding.tvProductosAnadidos.isVisible = false
                binding.nsvElegirProducto.isVisible = false

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
                parametros.put("NUMERO_DE_PRODUCTOS", sharedViewModel.listaDeProductos.size.toString())
                parametros.put("ALMACEN_ORIGEN", DropDownOrigen?.text.toString())
                parametros.put("ALMACEN_DESTINO", DropDownDestino?.text.toString())
                for (i in 0..<sharedViewModel.listaDeProductos.size) {
                    parametros["PRODUCTO$i"] = sharedViewModel.listaDeProductos[i].uppercase()
                    parametros["CANTIDAD$i"] = sharedViewModel.listaDeCantidades[i]
                }

                return parametros
            }
        }
        queue1.add(stringRequest)
    }

    fun modificarInventarioTotal() {
        //Si el inventario es menor que la transferencia que no la haga y si el almacen de destino no existe, que lo cree
        val url1 = "http://186.64.123.248/Reportes/Transferencias/modificarInventarioEditarTransferencia.php" // Reemplaza esto con tu URL de la API
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
                parametros.put("TRANSFERENCIA", TextNombre?.text.toString())
                parametros.put("NUMERO_DE_PRODUCTOS", sharedViewModel.listaDeProductos.size.toString())
                parametros.put("ALMACEN_ORIGEN", DropDownOrigen?.text.toString())
                parametros.put("ALMACEN_DESTINO", DropDownDestino?.text.toString())
                for (i in 0..<sharedViewModel.listaDeProductos.size) {
                    parametros["PRODUCTO$i"] = sharedViewModel.listaDeProductos[i].uppercase()
                    parametros["CANTIDAD$i"] = sharedViewModel.listaDeCantidades[i]
                }

                return parametros
            }
        }
        queue1.add(stringRequest)
    }

    private fun mensajeEliminarTransferencia(){
        AlertDialog.Builder(context).apply {
            setTitle("¿Quieres eliminar esta transferencia?")
            setMessage("Esta acción no se puede deshacer.")
            setPositiveButton("Sí") { dialog, _ ->
                eliminarTransferencia()
                findNavController().navigate(R.id.action_nav_editar_transferencias_to_nav_transferencias)
                dialog.dismiss()
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }

    }
    private fun eliminarTransferencia(){
        val url = "http://186.64.123.248/Reportes/Transferencias/borrar.php"
        val queue = Volley.newRequestQueue(requireContext())
        var jsonObjectRequest = object : StringRequest(Request.Method.POST, url,
            { response ->
                Toast.makeText(requireContext(), "Transferencia eliminada exitosamente", Toast.LENGTH_SHORT).show()

            },{error ->
                Toast.makeText(requireContext(), "No se pudo eliminar la transferencia", Toast.LENGTH_SHORT).show()
            })
        {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("ID_TRANSFERENCIA", sharedViewModel.id.last())
                return parametros
            }
        }
        queue.add(jsonObjectRequest)
    }


    /* private var segundaVez = false
    override fun onResume() {
        super.onResume()
        if (segundaVez && sharedViewModel.listaDeProductos.isNotEmpty() && sharedViewModel.listaDeCantidades.isNotEmpty()){
            view?.findViewById<TextView>(R.id.tvProductosAnadidos)?.isVisible = true
        }
        segundaVez = true

    }*/

   /* fun preguntarInventario() {
        val url1 = "http://186.64.123.248/Transferencia/validacionProductos.php"
        val queue1 = Volley.newRequestQueue(requireContext())
        val stringRequest = @SuppressLint("ClickableViewAccessibility")
        object : StringRequest(
            Request.Method.POST, url1,
            { response ->
                val encontrado = JSONObject(response).getString("ENCONTRADO")
                if(encontrado == "1") {
                    val queue = Volley.newRequestQueue(requireContext())
                    val url = "http://186.64.123.248/Transferencia/preguntarInventario.php"
                    val jsonObjectRequest = @SuppressLint("ClickableViewAccessibility")
                    object : StringRequest(
                        Request.Method.POST, url,
                        { response ->
                            //  try {
                            cantidad = JSONObject(response).getString("Cantidad")
                            val diferencia1 = sharedViewModel.listaDeCantidades.subtract(sharedViewModel.listaDeCantidadesAntigua.toSet()).firstOrNull()
                            val diferencia2 = sharedViewModel.listaDeCantidadesAntigua.subtract(sharedViewModel.listaDeCantidades.toSet()).firstOrNull()
                            val diferencia = diferencia1 ?: diferencia2
                            exceso = sharedViewModel.listaDeCantidades.find{ it.toInt() > cantidad.toInt() }
                            // for (i in 0..<sharedViewModel.listaDeCantidades.size) {
                            // if (sharedViewModel.listaDeCantidades[i] != sharedViewModel.listaDeCantidadesAntigua[i]) {
                            // val cantidadIngresada = sharedViewModel.listaDeCantidades[i].toInt()
                            //    val indiceCantidad = i
                            if(diferencia != null) {
                                if (cantidad.toInt() < diferencia.toInt()) {
                                    val inflater = requireActivity().layoutInflater
                                    val layout =
                                        inflater.inflate(R.layout.toast_custom, null)
                                    val text =
                                        layout.findViewById<TextView>(R.id.text_view_toast)
                                    text.text =
                                        "La cantidad es mayor que la cantidad en inventario"
                                    val toast = Toast(requireContext())
                                    toast.duration = Toast.LENGTH_SHORT
                                    toast.view = layout
                                    toast.setGravity(Gravity.BOTTOM, 0, 500)
                                    toast.show()
                                    Log.i("Sebastian","Diferencia: $diferencia")
                                    Log.i("Sebastian","Exceso: $exceso")
                                    Log.i("Sebastian","Cantidad: $cantidad")
                                    Log.i("Sebastian", "Mensaje 1")
                                    //else if (exceso != null && (sharedViewModel.listaDeCantidadesAntigua == sharedViewModel.listaDeCantidades)){
                                }
                            } else if(exceso != null && (cantidad.toInt() < exceso?.toInt()!!)){
                                val inflater = requireActivity().layoutInflater
                                val layout =
                                    inflater.inflate(R.layout.toast_custom, null)
                                val text =
                                    layout.findViewById<TextView>(R.id.text_view_toast)
                                text.text =
                                    "La cantidad es mayor que la cantidad en inventario"
                                val toast = Toast(requireContext())
                                toast.duration = Toast.LENGTH_SHORT
                                toast.view = layout
                                toast.setGravity(Gravity.BOTTOM, 0, 500)
                                toast.show()
                                Log.i("Sebastian","Exceso: $exceso")
                                Log.i("Sebastian", "Mensaje 2")
                            }
                            else {
                                Log.i("Sebastian", "Mensaje 6")
                                //binding.TransferenciaButtonEnviar.setOnTouchListener { _, motionEvent ->
                                //  if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                                if(sharedViewModel.listaDeProductos.size >0 && sharedViewModel.listaDeCantidades.size>0) {
                                    ValidacionesIdInsertarDatos()
                                }else{
                                    Toast.makeText(requireContext(),"Tiene que elegir al menos un producto", Toast.LENGTH_SHORT).show()
                                }
                                //   }
                                //    return@setOnTouchListener false
                                //  }
                            }
                            Log.i(
                                "Sebastian",
                                "${sharedViewModel.listaDeProductos} , ${sharedViewModel.listaDeCantidades},${sharedViewModel.listaDeProductosAntigua} y ${sharedViewModel.listaDeCantidadesAntigua}"
                            )
                            Log.i("Sebastian", "Mensaje 3")
                            /*   } catch (e: Exception) {
                                   Toast.makeText(
                                       requireContext(), "La excepcion es $e",
                                       Toast.LENGTH_SHORT
                                   ).show()
                                   Log.i("Sebastián", "$e")
                               }*/
                        },
                        { error ->
                            /*binding.TransferenciaButtonEnviar.setOnTouchListener { _ , motionEvent ->
                                if(motionEvent.action == MotionEvent.ACTION_DOWN) {
                                    ValidacionesIdInsertarDatos()
                                }
                                return@setOnTouchListener false*/
                            val responseBody: ByteArray? = error.networkResponse?.data
                            if(responseBody != null) {
                                val responseString = String(responseBody)
                                if (responseString.startsWith("<!DOCTYPE")) {

                                } else {
                                    val jsonResponse = JSONObject(responseString)
                                    val cantidad = jsonResponse.getString("Cantidad")
                                    val exceso =
                                        sharedViewModel.listaDeCantidades.find { it.toInt() > cantidad.toInt() }
                                    Log.i("Sebastian", "Exceso: $exceso")
                                    Log.i("Sebastian", "Cantidad: $cantidad")
                                    if ((sharedViewModel.listaDeCantidades != sharedViewModel.listaDeCantidadesAntigua) || (exceso?.toInt()!! > cantidad.toInt())) {
                                        val inflater = requireActivity().layoutInflater
                                        val layout =
                                            inflater.inflate(R.layout.toast_custom, null)
                                        val text =
                                            layout.findViewById<TextView>(R.id.text_view_toast)
                                        text.text =
                                            "La cantidad es mayor que la cantidad en inventario"
                                        val toast = Toast(requireContext())
                                        toast.duration = Toast.LENGTH_SHORT
                                        toast.view = layout
                                        toast.setGravity(Gravity.BOTTOM, 0, 500)
                                        toast.show()
                                    }
                                }
                            }
                            Log.i(
                                "Sebastian",
                                "${sharedViewModel.listaDeProductos} , ${sharedViewModel.listaDeCantidades},${sharedViewModel.listaDeProductosAntigua} y ${sharedViewModel.listaDeCantidadesAntigua}"
                            )
                            //Toast.makeText(requireContext(), "El error es $error", Toast.LENGTH_SHORT).show()
                            // Log.i("Sebastián", "$error")
                            Log.i("Sebastian", "Mensaje 4")
                        }) {
                        // Ejemplo: "ALMACEN_ORIGEN" : BODEGA_SANTIAGO
                        //          "PRODUCTO"       : TABLETON ( 0 unid. )
                        override fun getParams(): MutableMap<String, String> {
                            val parametros = HashMap<String, String>()
                            parametros.put("ALMACEN_ORIGEN", sharedViewModel.almacen)
                            for (i in 0..<sharedViewModel.listaDeProductos.size) {
                                if ((sharedViewModel.listaDeProductos[i].substringBefore('(', sharedViewModel.listaDeProductos[i])
                                            != sharedViewModel.listaDeProductosAntigua[i].substringBefore('(',
                                        sharedViewModel.listaDeProductosAntigua[i]
                                    )) ||
                                    (sharedViewModel.listaDeCantidades[i] != sharedViewModel.listaDeCantidadesAntigua[i])
                                ) {
                                    parametros.put("PRODUCTO", sharedViewModel.listaDeProductos[i])
                                }
                                /* else if(sharedViewModel.listaDeCantidades[i] == sharedViewModel.listaDeCantidadesAntigua[i] &&
                                     (sharedViewModel.listaDeProductos[i].substringBefore('(', sharedViewModel.listaDeProductos[i])
                                             == sharedViewModel.listaDeProductosAntigua[i].substringBefore('(',
                                         sharedViewModel.listaDeProductosAntigua[i]
                                     )) && exceso != null){
                                     parametros.put("PRODUCTO", sharedViewModel.listaDeProductos[sharedViewModel.listaDeCantidades.indexOf(exceso)])
                                 }*/
                                /*  else  {
                                      binding.TransferenciaButtonEnviar.setOnTouchListener { _ , motionEvent ->
                                          if(motionEvent.action == MotionEvent.ACTION_DOWN) {
                                              ValidacionesIdInsertarDatos()
                                          }
                                          return@setOnTouchListener false
                                      }
                                  }*/
                            }

                            return parametros
                        }
                    }
                    queue.add(jsonObjectRequest)
                }else if (encontrado == "0"){
                    Toast.makeText(requireContext(), "El producto no se encuentra en la base de datos", Toast.LENGTH_SHORT).show()
                    Log.i(
                        "Sebastian",
                        "${sharedViewModel.listaDeProductos} , ${sharedViewModel.listaDeCantidades},${sharedViewModel.listaDeProductosAntigua} y ${sharedViewModel.listaDeCantidadesAntigua}"
                    )
                    Log.i("Sebas",sharedViewModel.listaDeProductos[0].substringBefore('(', sharedViewModel.listaDeProductos[0]))
                }
            }, { error ->
                Log.i("Sebastian", "Mensaje 5")

                //  binding.TransferenciaButtonEnviar.setOnTouchListener { _ , motionEvent ->
                //      if(motionEvent.action == MotionEvent.ACTION_DOWN) {
                if(sharedViewModel.listaDeProductos.size >0 && sharedViewModel.listaDeCantidades.size>0) {
                    ValidacionesIdInsertarDatos()
                }else{
                    Toast.makeText(requireContext(),"Tiene que elegir al menos un producto", Toast.LENGTH_SHORT).show()
                }
                //       }
                //       return@setOnTouchListener false
                //  }
                // Toast.makeText(requireContext(),"Aquí esta el error de la api validacionProductos",Toast.LENGTH_SHORT).show()
            }) {
            // Ejemplo: "PRODUCTO" :***AGUA CON GAS**
            //Los asteriscos son espacios
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                for (i in 0..<sharedViewModel.listaDeProductos.size) {
                    //Ejemplos:
                    // Si ALOE de lista productos es distinto a otro elemento de lista productos antigua
                    // Y 20 si esta en lista cantidad y no en lista cantidad antigua
                    if ((sharedViewModel.listaDeProductos[i].substringBefore('(', sharedViewModel.listaDeProductos[i]) != sharedViewModel.listaDeProductosAntigua[i].substringBefore('(', sharedViewModel.listaDeProductosAntigua[i]
                        )) ||
                        (sharedViewModel.listaDeCantidades[i] != sharedViewModel.listaDeCantidadesAntigua[i])
                    ) {
                        parametros.put("PRODUCTO", sharedViewModel.listaDeProductos[i].substringBefore('(', sharedViewModel.listaDeProductos[i]))
                    }
                    else if ((sharedViewModel.listaDeProductos[i].substringBefore('(', sharedViewModel.listaDeProductos[i]) != sharedViewModel.listaDeProductosAntigua[i].substringBefore('(', sharedViewModel.listaDeProductosAntigua[i]
                        )) ||(sharedViewModel.listaDeCantidades[i] == sharedViewModel.listaDeCantidadesAntigua[i])){
                        parametros.put("PRODUCTO", sharedViewModel.listaDeProductos[i].substringBefore('(', sharedViewModel.listaDeProductos[i]))
                    }
                }
                return parametros
            }
        }
        queue1.add(stringRequest)
    }*/
    override fun onDestroyView() {
        super.onDestroyView()
        adapter.notifyDataSetChanged()
        binding.rvElegirProducto.requestLayout()
        _binding = null
    }
}