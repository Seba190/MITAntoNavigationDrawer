package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirTransferencia

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
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
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.seba.mitantonavigationdrawer.MainActivity
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentAnadirTransferenciaBinding
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto.AlertasAlmacenesFragment
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.AlmacenesAdapter
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AnadirTransferenciaFragment : Fragment(R.layout.fragment_anadir_transferencia) {

    companion object {
        const val CODIGO_DE_BARRA = "CODIGO_DE_BARRA"
    }


    private val sharedViewModel by activityViewModels<SharedViewModel>()
    private var _binding: FragmentAnadirTransferenciaBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val anadirTransferenciaViewModel =
            ViewModelProvider(this).get(AnadirTransferenciaViewModel::class.java)

        _binding = FragmentAnadirTransferenciaBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa
        //Definir los editText
        TextNombre = binding.etNombreTransferencia.findViewById(R.id.etNombreTransferencia)
        TextFecha = binding.etFechaTransferencia.findViewById(R.id.etFechaTransferencia)
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

        binding.nsvElegirProducto.isVisible = false
        binding.bAnadirNuevoProducto.setOnClickListener {
            if (sharedViewModel.opcionesListTransferenciaOrigen.contains(DropDownOrigen?.text.toString()) &&
                sharedViewModel.opcionesListTransferenciaDestino.contains(DropDownDestino?.text.toString())) {
                sharedViewModel.almacen = DropDownOrigen?.text.toString()
                sharedViewModel.listaDeAlmacenesTransferencia.add(DropDownOrigen?.text.toString())
                binding.nsvElegirProducto.isVisible = true
                //Aquí se obtiene el id de la transferencia actual
                if (TextNombre?.text.toString().isNotBlank()) {
                    val queue = Volley.newRequestQueue(requireContext())
                    val url = "http://186.64.123.248/Transferencia/registro.php"
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
                                val unico = JSONObject(response).getString("TRANSFERENCIA_UNICA")
                                Toast.makeText(
                                    requireContext(),
                                    "El id de ingreso es $id",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }, { error ->
                            Toast.makeText(
                                requireContext(),
                                "Conecte la aplicación al servidor",
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
                val elegirProductoFragment = ElegirProductoFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.clAnadirTransferencia, elegirProductoFragment)
                    .commit()
                binding.tvProductosAnadidos.isVisible = true

                // binding.tvProductosAnadidos.isVisible = !(adapter.listaDeCantidades.size == 0 && adapter.listaDeProductos.size == 0)
            } else if((!sharedViewModel.opcionesListTransferenciaOrigen.contains(DropDownOrigen?.text.toString()) &&
                        !sharedViewModel.opcionesListTransferenciaDestino.contains(DropDownDestino?.text.toString())) &&
                       (DropDownOrigen?.text.toString() == "Eliga una opción" &&
                        DropDownDestino?.text.toString() == "Eliga una opción")) {
                Toast.makeText(requireContext(), "Eliga el almacén de origen y de destino", Toast.LENGTH_SHORT).show()
            } else if((!sharedViewModel.opcionesListTransferenciaOrigen.contains(DropDownOrigen?.text.toString()) &&
                        !sharedViewModel.opcionesListTransferenciaDestino.contains(DropDownDestino?.text.toString())) &&
                (DropDownOrigen?.text.toString() != "Eliga una opción" &&
                        DropDownDestino?.text.toString() != "Eliga una opción")){
                Toast.makeText(requireContext(),"El nombre del almacén de origen o destino no es válido", Toast.LENGTH_SHORT).show()
            }

        }
        binding.TransferenciaButtonEnviar.setOnClickListener {
                if ((sharedViewModel.listaDeProductos.size > 0 && sharedViewModel.listaDeCantidades.size > 0) &&
                    sharedViewModel.opcionesListTransferenciaOrigen.contains(DropDownOrigen?.text.toString()) &&
                    sharedViewModel.opcionesListTransferenciaDestino.contains(DropDownDestino?.text.toString())) {
                    ValidacionesIdInsertarDatos()
                    adapter.notifyDataSetChanged()
                    binding.rvElegirProducto.requestLayout()
                } else if(sharedViewModel.listaDeProductos.size == 0 && sharedViewModel.listaDeCantidades.size == 0) {
                    Toast.makeText(requireContext(), "Tiene que elegir al menos un producto", Toast.LENGTH_SHORT).show()
                } else if(!sharedViewModel.opcionesListTransferenciaOrigen.contains(DropDownOrigen?.text.toString()) ||
                !sharedViewModel.opcionesListTransferenciaDestino.contains(DropDownDestino?.text.toString())) {
            Toast.makeText(requireContext(), "El almacén de origen o de destino no es válido", Toast.LENGTH_SHORT).show()
                } else if(DropDownOrigen?.text.toString() == "Eliga una opción" ||
                    DropDownDestino?.text.toString() == "Eliga una opción"){
                    Toast.makeText(requireContext(),"Debe elegir el almacén de origen y de destino", Toast.LENGTH_SHORT).show()
                }
        }

        requestCamara = registerForActivityResult(ActivityResultContracts.RequestPermission(),) {
            if (it) {
                findNavController().navigate(R.id.action_nav_añadir_transferencia_to_nav_barcode_scan)
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
        recyclerViewElegirProducto()
        //var segundaVez = false
        binding.bActualizarRecyclerView.setOnClickListener {
            binding.tvProductosAnadidos.isVisible = !(sharedViewModel.listaDeCantidades.size == 0 && sharedViewModel.listaDeProductos.size == 0)
             binding.nsvElegirProducto.isVisible = !(sharedViewModel.listaDeCantidades.size == 0 && sharedViewModel.listaDeProductos.size == 0)
            adapter.notifyDataSetChanged()
            binding.rvElegirProducto.requestLayout()
            Log.i(
                "Sebastian",
                "${sharedViewModel.listaDeProductos} , ${sharedViewModel.listaDeCantidades},${sharedViewModel.listaDeProductosAntigua} y ${sharedViewModel.listaDeCantidadesAntigua}")
        }

        binding.tvProductosAnadidos.isVisible = sharedViewModel.listaDeCantidades.isNotEmpty()
        binding.tvProductosAnadidos.isVisible = false

        binding.etFechaTransferencia.setText(SimpleDateFormat("dd-MM-yyyy",Locale.getDefault()).format(Calendar.getInstance().time))
        return root
    }

    fun updateData(dataCantidad: MutableList<String>, dataProducto: MutableList<String>) {
        adapter.updateData(dataCantidad, dataProducto)
        binding.rvElegirProducto.adapter?.notifyDataSetChanged()
        binding.rvElegirProducto.requestLayout()
    }

    /* fun refreshAdapter(){
       adapter.notifyDataSetChanged()
        binding.rvElegirProducto.requestLayout()
    }*/

    fun recyclerViewElegirProducto() {
        adapter = AnadirTransferenciaAdapter(sharedViewModel.listaDeCantidades,
            sharedViewModel.listaDeProductos,sharedViewModel) { position -> onDeletedItem(position)}
        binding.rvElegirProducto.setHasFixedSize(true)
        binding.rvElegirProducto.adapter = adapter
        binding.rvElegirProducto.layoutManager = LinearLayoutManager(requireContext())
        adapter.notifyDataSetChanged()
        binding.rvElegirProducto.requestLayout()
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
               if(sharedViewModel.opcionesListTransferenciaDestino.isEmpty()) {
                   for (i in 0 until jsonArray.length()) {
                       sharedViewModel.opcionesListTransferenciaDestino.add(jsonArray.getString(i).removeSurrounding("'", "'"))
                   }
               }
                sharedViewModel.opcionesListTransferenciaDestino.sort()
                //Crea un adpatador para el dropdown
                val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_dropdown_item_1line, sharedViewModel.opcionesListTransferenciaDestino)
                //binding.tvholaMundo?.setText(response.getString("Lista"))
                DropDownDestino?.setAdapter(adapter)
                DropDownDestino?.threshold = 1
                DropDownDestino?.onItemClickListener =
                    AdapterView.OnItemClickListener { parent, view, position, id ->
                        val itemSelected = parent.getItemAtPosition(position)
                    }
                DropDownDestino?.setOnClickListener {
                    if(DropDownDestino?.text.toString() == "Eliga una opción"){
                        binding.tvListaDesplegableAlmacenDestino.setText("",false)
                        DropDownDestino?.showDropDown()
                    }
                }
                DropDownDestino?.setOnFocusChangeListener { _, hasFocus ->
                    if(hasFocus && DropDownDestino?.text.toString() == "Eliga una opción"){
                        binding.tvListaDesplegableAlmacenDestino.setText("",false)
                        DropDownDestino?.showDropDown()
                    }
                    else if (!hasFocus && !sharedViewModel.opcionesListTransferenciaDestino.contains(DropDownDestino?.text.toString())){
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
                if(sharedViewModel.opcionesListTransferenciaOrigen.isEmpty()) {
                    for (i in 0 until jsonArray.length()) {
                        sharedViewModel.opcionesListTransferenciaOrigen.add(jsonArray.getString(i).removeSurrounding("'", "'"))
                    }
                }
                sharedViewModel.opcionesListTransferenciaOrigen.sort()
                //Crea un adpatador para el dropdown
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sharedViewModel.opcionesListTransferenciaOrigen)
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
                    if(DropDownOrigen?.text.toString() == "Eliga una opción"){
                        binding.tvListaDesplegableAlmacenOrigen.setText("",false)
                        DropDownOrigen?.showDropDown()
                    }
                }
                DropDownOrigen?.setOnFocusChangeListener { _, hasFocus ->
                    if(hasFocus && DropDownOrigen?.text.toString() == "Eliga una opción"){
                        binding.tvListaDesplegableAlmacenOrigen.setText("",false)
                        DropDownOrigen?.showDropDown()
                    }
                    else if (!hasFocus && !sharedViewModel.opcionesListTransferenciaOrigen.contains(DropDownOrigen?.text.toString())){
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
        //INICIO EXPERIMIENTO!!!!!!!!!!!!!!!!!!!!!!!!!! (FUNCIONO)
        val queue = Volley.newRequestQueue(requireContext())
        val url = "http://186.64.123.248/Transferencia/registro.php"
        val jsonObjectRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                if (TextNombre?.text.toString().isNotBlank()) {
                    val id = JSONObject(response).getString("ID_TRANSFERENCIA")
                    //unico = 1
                    //no unico = 0
                    val unico = JSONObject(response).getString("TRANSFERENCIA_UNICA")
                    if (unico == "1") {
                        //Aqui va el código para validar el almacen
                        val url1 =
                            "http://186.64.123.248/Transferencia/insertarTransferencia.php" // Reemplaza esto con tu URL de la API
                        val queue1 = Volley.newRequestQueue(requireContext())
                        val stringRequest = object : StringRequest(
                            Request.Method.POST,
                            url1,
                            { response ->
                                if (binding.etNombreTransferencia.text.isNotBlank() && binding.tvListaDesplegableAlmacenDestino.text.toString() != "Eliga una opción" && binding.tvListaDesplegableAlmacenOrigen.text.toString() != "Eliga una opción") {
                                      //  Handler(Looper.getMainLooper()).postDelayed({
                                    modificarInventario()
                                      //  }, 2500)
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        InsertarPreciosYCantidades()
                                        }, 5000)
                                        binding.tvProductosAnadidos.isVisible = !(sharedViewModel.listaDeCantidades.size == 0 && sharedViewModel.listaDeProductos.size == 0)
                                    }

                                Toast.makeText(
                                    requireContext(),
                                    "Transferencia agregada exitosamente. El id de ingreso es el número $id ",
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
                                parametros.put("ID_TRANSFERENCIA", id.toString())
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

                    } else if (unico == "0") {
                        //VolleyError("El almacén ya se encuentra en la base de datos")
                        //queue1.cancelAll(TAG)
                        //jsonObjectRequest1.cancel()
                        Toast.makeText(
                            requireContext(),
                            "La transferencia ya se encuentra en la base de datos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "El nombre de la transferencia es obligatorio",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                // TextId?.setText(response.getString("ID_ALMACEN"))
                // Toast.makeText(requireContext(),"Id ingresado correctamente al formulario.", Toast.LENGTH_SHORT).show()
            }, { error ->
                Toast.makeText(
                    requireContext(),
                    "Conecte la aplicación al servidor",
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

    private fun InsertarPreciosYCantidades() {
        //INICIO EXPERIMIENTO!!!!!!!!!!!!!!!!!!!!!!!!!! (FUNCIONO)
        val queue = Volley.newRequestQueue(requireContext())
        val url = "http://186.64.123.248/Transferencia/registroProductos.php"
        val jsonObjectRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                if (TextNombre?.text.toString().isNotBlank()) {
                    val id = JSONObject(response).getString("ID_TRANSFERENCIA")
                    //unico = 1
                    //no unico = 0
                    //Aqui va el código para validar el almacen
                    val url1 =
                        "http://186.64.123.248/Transferencia/insertarProductosTotal.php" // Reemplaza esto con tu URL de la API
                    val queue1 = Volley.newRequestQueue(requireContext())
                    val stringRequest = object : StringRequest(
                        Request.Method.POST,
                        url1,
                        { response ->
                            TextNombre?.setText("")
                            TextFecha?.setText(SimpleDateFormat("dd-MM-yyyy",Locale.getDefault()).format(Calendar.getInstance().time))
                            DropDownOrigen?.setText("Eliga una opción", false)
                            DropDownDestino?.setText("Eliga una opción", false)
                            TextComentarios?.setText("")
                            sharedViewModel.listaDeCantidades.removeAll(sharedViewModel.listaDeCantidades)
                            sharedViewModel.listaDeProductos.removeAll(sharedViewModel.listaDeProductos)
                            sharedViewModel.listaDeProductosAntigua.removeAll(sharedViewModel.listaDeProductosAntigua)
                            adapter.notifyDataSetChanged()
                            binding.rvElegirProducto.requestLayout()
                            binding.tvProductosAnadidos.isVisible = false
                            binding.nsvElegirProducto.isVisible = false
                            sharedViewModel.opcionesListTransferencia.clear()
                            Toast.makeText(
                                requireContext(),
                                "Productos agregados exitosamente",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        { error ->
                            Toast.makeText(
                                requireContext(),
                                "Error $error y ${sharedViewModel.listaDeProductos} y ${sharedViewModel.listaDeCantidades}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    ) {
                        override fun getParams(): MutableMap<String, String> {
                            val parametros = HashMap<String, String>()
                            parametros.put("ID_TRANSFERENCIA", id.toString())
                            parametros.put("NUMERO_DE_PRODUCTOS", sharedViewModel.listaDeProductos.size.toString())
                            for (i in 0..<sharedViewModel.listaDeProductos.size) {
                                parametros["PRODUCTO$i"] =
                                    sharedViewModel.listaDeProductos[i].uppercase()
                                parametros["CANTIDAD$i"] = sharedViewModel.listaDeCantidades[i]
                            }

                            return parametros
                        }
                    }
                    queue1.add(stringRequest)

                }

                // TextId?.setText(response.getString("ID_ALMACEN"))
                // Toast.makeText(requireContext(),"Id ingresado correctamente al formulario.", Toast.LENGTH_SHORT).show()
            }, { error ->
                //Toast.makeText(requireContext(),"Conecte la aplicación al servidor", Toast.LENGTH_SHORT).show()
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

    fun obtenerMiView(): View {
        return binding.etNombreTransferencia // Tu vista que quieres acceder
    }


    fun modificarInventario() {
        //Si el inventario es menor que la transferencia que no la haga y si el almacen de destino no existe, que lo cree
        val url1 = "http://186.64.123.248/Transferencia/modificarInventarioCompleta.php" // Reemplaza esto con tu URL de la API
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
                /*  TextNombre?.setText("")
                          TextFecha?.setText("")
                          DropDownOrigen?.setText("Eliga una opción",false)
                          DropDownDestino?.setText("Eliga una opción",false)
                          TextComentarios?.setText("")
                          TextCodigoDeBarra?.setText("")*/
                Toast.makeText(
                    requireContext(),
                    "Error $error y ${sharedViewModel.listaDeProductos} y ${sharedViewModel.listaDeCantidades}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put(
                    "NUMERO_DE_PRODUCTOS",
                    sharedViewModel.listaDeProductos.size.toString()
                )
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
    }/*,
            {error ->
                Toast.makeText(requireContext(),"El inventario del almacén es menor que la transferencia",Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {

                val parametros = HashMap<String, String>()
                for (i in 0..<sharedViewModel.listaDeProductos.size) {
                    parametros["CANTIDAD$i"] = sharedViewModel.listaDeCantidades[i]
                }
                return parametros
            }
        }
        queue.add(stringRequest1)*/


    /* private var segundaVez = false
    override fun onResume() {
        super.onResume()
        if (segundaVez && sharedViewModel.listaDeProductos.isNotEmpty() && sharedViewModel.listaDeCantidades.isNotEmpty()){
            view?.findViewById<TextView>(R.id.tvProductosAnadidos)?.isVisible = true
        }
        segundaVez = true

    }*/

    fun preguntarInventario() {
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
    }
    override fun onDestroyView() {
        super.onDestroyView()
       /* sharedViewModel.listaDeProductos.clear()
        sharedViewModel.listaDeCantidades.clear()*/
        adapter.notifyDataSetChanged()
        binding.rvElegirProducto.requestLayout()
        _binding = null
    }
}
