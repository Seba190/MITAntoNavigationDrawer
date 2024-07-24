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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentAnadirInventarioBinding
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirTransferencia.AnadirTransferenciaAdapter
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirTransferencia.ElegirProductoFragment
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.AlmacenesAdapter
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.AlmacenesDataResponse
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.ApiService
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.SocketTimeoutException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AnadirInventarioFragment : Fragment(R.layout.fragment_anadir_inventario) {

    private var _binding: FragmentAnadirInventarioBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val sharedViewModel by activityViewModels<SharedViewModel>()
    var TextNombre: EditText? = null
    var TextFecha: EditText? = null
    var TextComentarios: EditText? = null
    var DropDownProveedor: AutoCompleteTextView? = null
    var DropDownAlmacen: AutoCompleteTextView? = null
    private lateinit var retrofit: Retrofit
    private lateinit var adapter: AnadirInventarioAdapter
    private var requestCamara: ActivityResultLauncher<String>? = null
    //private var listaProductos: MutableList<ProductosItemResponse> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val anadirInventarioViewModel =
            ViewModelProvider(this).get(AnadirInventarioViewModel::class.java)

        _binding = FragmentAnadirInventarioBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa
        TextNombre = binding.etNombreFacturaEntrada.findViewById(R.id.etNombreFacturaEntrada)
        TextFecha = binding.etFechaTransaccion.findViewById(R.id.etFechaTransaccion)
        TextComentarios = binding.etFacturaEntradaComentarios.findViewById(R.id.etFacturaEntradaComentarios)
        DropDownProveedor = binding.tvListaDesplegableProveedor.findViewById(R.id.tvListaDesplegableProveedor)
        DropDownAlmacen = binding.tvListaDesplegableAlmacen.findViewById(R.id.tvListaDesplegableAlmacen)

        binding.etNombreFacturaEntrada.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etFechaTransaccion.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etFacturaEntradaComentarios.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        //binding.etProductos.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
         //   PorterDuff.Mode.SRC_ATOP)

        ListaDesplegableProveedor()
        ListaDesplegableAlmacen()
       // retrofit = getRetrofit()

        requestCamara = registerForActivityResult(ActivityResultContracts.RequestPermission(),){
            if(it){
                findNavController().navigate(R.id.action_nav_añadir_inventario_to_nav_barcode_scan_anadir)
            }else{
                Toast.makeText(requireContext(),"Permiso denegado",Toast.LENGTH_LONG).show()
            }
        }
        binding.bEscanearCodigoDeBarraAnadir.setOnClickListener {
            requestCamara?.launch(android.Manifest.permission.CAMERA)
        }
        //searchByName()

        binding.FacturaEntradaButtonEnviar.setOnClickListener {
            if(sharedViewModel.listaDeCantidadesAnadir.size > 0 && sharedViewModel.listaDeCantidadesAnadir.size>0 && sharedViewModel.listaDePreciosAnadir.size >0) {
                ValidacionesIdInsertarDatos()
                adapter.notifyDataSetChanged()
                binding.rvElegirProductoAnadir.requestLayout()
            }else{
                Toast.makeText(requireContext(),"Debe elegir al menos un producto para añadir inventario", Toast.LENGTH_LONG).show()
            }
        }
      /*  binding.llCajasDeProducto.isVisible = false
        binding.bUnidades.setOnClickListener {
            binding.llUnidades.isVisible = false
            binding.llCajasDeProducto.isVisible = true
        }
        binding.bCajasDeProducto.setOnClickListener {
            binding.llCajasDeProducto.isVisible = false
            binding.llUnidades.isVisible = true
        }
        binding.llProductos.isVisible = false

        binding.ivTrash.setOnClickListener {
            binding.llProductos.isVisible = false
        }*/


       /* binding.etProductos.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                filtrar(s.toString())
            }

        })*/
        binding.tvProductosAnadidosAnadir.isVisible = false
        recyclerViewElegirProducto()
        binding.bActualizarRecyclerViewAnadir.setOnClickListener {
            binding.tvProductosAnadidosAnadir.isVisible = !(sharedViewModel.listaDeCantidadesAnadir.size == 0 || sharedViewModel.listaDeProductosAnadir.size == 0 || sharedViewModel.listaDePreciosAnadir.size == 0)
            binding.llMonto.isVisible = !(sharedViewModel.listaDeCantidadesAnadir.size == 0 || sharedViewModel.listaDeProductosAnadir.size == 0 || sharedViewModel.listaDePreciosAnadir.size == 0)
            adapter.notifyDataSetChanged()
            Handler(Looper.getMainLooper()).postDelayed({
                binding.tvMonto.text = sharedViewModel.listaDePreciosDeProductos.sum().toString()
            }, 300)
            sharedViewModel.listaDePreciosDeProductos.clear()
            binding.rvElegirProductoAnadir.requestLayout()
        }
        binding.nsvElegirProductoAnadir.isVisible = false
        binding.llMonto.isVisible = false
        binding.bAnadirNuevoProductoAnadir.setOnClickListener {
            if(DropDownProveedor?.text.toString() != "Eliga una opción" && DropDownAlmacen?.text.toString() != "Eliga una opción"){
                sharedViewModel.proveedorAnadir = DropDownProveedor?.text.toString()
                sharedViewModel.almacenAnadir = DropDownAlmacen?.text.toString()
                sharedViewModel.listaDeAlmacenesAnadir.add(DropDownAlmacen?.text.toString())
                binding.nsvElegirProductoAnadir.isVisible = true
                binding.llMonto.isVisible = true
            //setFragmentResult("Proveedor", bundleOf("Proveedor" to DropDownProveedor?.text.toString()))
            //Toast.makeText(requireContext(),"Funciona el traspaso de información", Toast.LENGTH_LONG).show()
                val elegirProductoAnadirFragment = ElegirProductoAnadirFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.clAnadirInventario, elegirProductoAnadirFragment)
                    .commit()
                binding.tvProductosAnadidosAnadir.isVisible = true
               // binding.tvProductosAnadidosAnadir.isVisible = !(adapter.listaDeCantidadesAnadir.size == 0 && adapter.listaDeProductosAnadir.size == 0 && adapter.listaDePreciosAnadir.size == 0)
            }else{
                Toast.makeText(requireContext(),"Eliga el proveedor y el cliente", Toast.LENGTH_SHORT).show()
            }

        }

      //  if(sharedViewModel.listaDePreciosDeProductos.isEmpty() && binding.llMonto.isVisible) {
      //      binding.tvMonto.text = 0.toString()
      //  }else{
      //      binding.tvMonto.text = sharedViewModel.listaDePreciosDeProductos.sum().toString()
      //  }
      binding.etFechaTransaccion.setText(SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Calendar.getInstance().time))
        return root
    }






    fun updateData(dataCantidad:MutableList<String>, dataProducto: MutableList<String>,dataPrecio: MutableList<String>){
        adapter.updateData(dataCantidad,dataProducto,dataPrecio)
        binding.rvElegirProductoAnadir.adapter?.notifyDataSetChanged()
        binding.rvElegirProductoAnadir.requestLayout()
    }

    fun recyclerViewElegirProducto(){
        adapter = AnadirInventarioAdapter(sharedViewModel.listaDeCantidadesAnadir,sharedViewModel.listaDeProductosAnadir,sharedViewModel.listaDePreciosAnadir,sharedViewModel) { position ->
            onDeletedItem(position)}
        binding.rvElegirProductoAnadir.setHasFixedSize(true)
        binding.rvElegirProductoAnadir.adapter = adapter
        binding.rvElegirProductoAnadir.layoutManager = LinearLayoutManager(requireContext())
        adapter.updateList(sharedViewModel.listaDeCantidadesAnadir,sharedViewModel.listaDeProductosAnadir,sharedViewModel.listaDePreciosAnadir)
        adapter.notifyDataSetChanged()
        binding.rvElegirProductoAnadir.requestLayout()
    }

    private fun onDeletedItem(position: Int){
       // sharedViewModel.opcionesListAnadir.add(position,sharedViewModel.listaDeProductosAnadir[position])
        sharedViewModel.listaDeCantidadesAnadir.removeAt(position)
        sharedViewModel.listaDeProductosAnadir.removeAt(position)
        sharedViewModel.listaDePreciosAnadir.removeAt(position)
        adapter.notifyItemRemoved(position)
        adapter.notifyDataSetChanged()
        binding.rvElegirProductoAnadir.requestLayout()
        Handler(Looper.getMainLooper()).postDelayed({
            binding.tvMonto.text = sharedViewModel.listaDePreciosDeProductos.sum().toString()
        }, 300)
        sharedViewModel.listaDePreciosDeProductos.clear()
        binding.tvProductosAnadidosAnadir.isVisible = !(adapter.listaDeCantidadesAnadir.size == 0 || adapter.listaDeProductosAnadir.size == 0 || adapter.listaDePreciosAnadir.size == 0)
        binding.llMonto.isVisible = !(adapter.listaDeCantidadesAnadir.size == 0 || adapter.listaDeProductosAnadir.size == 0 || adapter.listaDePreciosAnadir.size == 0)
    }
   /* private fun filtrar(texto: String) {
         var listaFiltrada = arrayListOf<ProductosItemResponse>()
         listaProductos.forEach{
           if(it.Nombre.lowercase().contains(texto.lowercase())){
               listaFiltrada.add(it)
           }
        }
        adapter.filtrar(listaFiltrada)
    }*/

   /* private fun setUpRecyclerView() {

        //adapter = AnadirInventarioAdapter{appearQuantity(it)}
        // adapter2 = CaracteristicasAdapter()
        binding.rvProductos.setHasFixedSize(true)
        binding.rvProductos.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProductos.adapter = adapter
    }*/

    private fun ValidacionesIdInsertarDatos() {
        //INICIO EXPERIMIENTO!!!!!!!!!!!!!!!!!!!!!!!!!! (FUNCIONO)
        val queue =Volley.newRequestQueue(requireContext())
        val url ="http://186.64.123.248/FacturaEntrada/registro.php"
        val jsonObjectRequest = object: StringRequest(
            Request.Method.POST,url,
            { response ->
                if(TextNombre?.text.toString().isNotBlank()){
                    val id = JSONObject(response).getString("ID_FACTURA_ENTRADA")
                    //unico = 1
                    //no unico = 0
                    val unico = JSONObject(response).getString("FACTURA_ENTRADA_UNICA")
                    if (unico == "1") {
                        //Aqui va el código para validar el almacen
                        val url1 = "http://186.64.123.248/FacturaEntrada/insertar.php" // Reemplaza esto con tu URL de la API
                        val queue1 =Volley.newRequestQueue(requireContext())
                        val stringRequest = object: StringRequest(
                            Request.Method.POST,
                            url1,
                            { response ->
                                if(binding.etNombreFacturaEntrada.text.isNotBlank() && binding.tvListaDesplegableProveedor.text.toString() != "Eliga una opción" && binding.tvListaDesplegableAlmacen.text.toString() != "Eliga una opción") {
                               //     Handler(Looper.getMainLooper()).postDelayed({
                                    anadirInventario()
                               //     }, 2500)
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        InsertarPreciosYCantidades()
                                    }, 500)
                                    binding.tvProductosAnadidosAnadir.isVisible = !(adapter.listaDeCantidadesAnadir.size == 0 && adapter.listaDeProductosAnadir.size == 0 && adapter.listaDePreciosAnadir.size == 0)
                                }
                                Toast.makeText(requireContext(), "Factura agregada exitosamente. El id de ingreso es el número $id ", Toast.LENGTH_LONG).show()
                                /*TextNombre?.setText("")
                                TextFecha?.setText("")
                                DropDownProveedor?.setText("Eliga una opción",false)
                                DropDownAlmacen?.setText("Eliga una opción",false)
                                TextComentarios?.setText("")*/
                            },
                            { error ->
                                //Toast.makeText(requireContext(),"El proveedor y el almacén son obligatorios", Toast.LENGTH_LONG).show()
                                Log.e("Sebastian4","$error")
                                Toast.makeText(requireContext(),"Error $error", Toast.LENGTH_LONG).show()
                            }
                        )

                        {
                            override fun getParams(): MutableMap<String, String> {
                                val parametros = HashMap<String, String>()
                                parametros.put("ID_FACTURA_ENTRADA", id.toString())
                                parametros.put("FACTURA_ENTRADA", TextNombre?.text.toString().uppercase())
                                parametros.put("PROVEEDOR", DropDownProveedor?.text.toString().uppercase())
                                parametros.put("ALMACEN", DropDownAlmacen?.text.toString())
                                parametros.put("FECHA_TRANSACCION", TextFecha?.text.toString())
                                parametros.put("COMENTARIOS", TextComentarios?.text.toString().uppercase())

                                return parametros
                            }
                        }
                        queue1.add(stringRequest)

                    }
                    else if (unico == "0"){
                        //VolleyError("El almacén ya se encuentra en la base de datos")
                        //queue1.cancelAll(TAG)
                        //jsonObjectRequest1.cancel()
                        Toast.makeText(requireContext(), "La factura ya se encuentra en la base de datos", Toast.LENGTH_LONG).show()
                    }
                }
                else{
                    Toast.makeText(requireContext(), "El nombre de la factura es obligatorio", Toast.LENGTH_LONG).show()
                }

                // TextId?.setText(response.getString("ID_ALMACEN"))
                // Toast.makeText(requireContext(),"Id ingresado correctamente al formulario.", Toast.LENGTH_LONG).show()
            }, { error ->
                Toast.makeText(requireContext(),"Conecte la aplicación al servidor", Toast.LENGTH_LONG).show()
                //Toast.makeText(requireContext(),"Error $error", Toast.LENGTH_LONG).show()
            }
        ){
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("FACTURA_ENTRADA", TextNombre?.text.toString().uppercase())
                return parametros
            }
        }
        queue.add(jsonObjectRequest)
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
                    binding.tvProductosAnadidosAnadir.isVisible = sharedViewModel.listaDeProductosAnadir.size != 0
                    binding.llMonto.isVisible = sharedViewModel.listaDeProductosAnadir.size != 0
                    binding.nsvElegirProductoAnadir.isVisible = false
                    binding.rvElegirProductoAnadir.requestLayout()
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

    private fun InsertarPreciosYCantidades(){
        val queue =Volley.newRequestQueue(requireContext())
        val url ="http://186.64.123.248/FacturaEntrada/registroProductos.php"
        val jsonObjectRequest = object: StringRequest(
            Request.Method.POST,url,
            { response ->
                if(TextNombre?.text.toString().isNotBlank()){
                    val id = JSONObject(response).getString("ID_FACTURA_ENTRADA")
                    //unico = 1
                    //no unico = 0
                    //Aqui va el código para validar el almacen
                    val url1 = "http://186.64.123.248/FacturaEntrada/insertarProductos.php" // Reemplaza esto con tu URL de la API
                    val queue1 =Volley.newRequestQueue(requireContext())
                    val stringRequest = object: StringRequest(
                        Request.Method.POST,
                        url1,
                        { response ->
                           Toast.makeText(requireContext(),"Productos agregados exitosamente", Toast.LENGTH_LONG).show()
                            TextNombre?.setText("")
                            TextFecha?.setText("")
                            DropDownProveedor?.setText("Eliga una opción",false)
                            DropDownAlmacen?.setText("Eliga una opción",false)
                            TextComentarios?.setText("")
                            sharedViewModel.listaDeCantidadesAnadir.removeAll(sharedViewModel.listaDeCantidadesAnadir)
                            sharedViewModel.listaDeProductosAnadir.removeAll(sharedViewModel.listaDeProductosAnadir)
                            sharedViewModel.listaDePreciosAnadir.removeAll(sharedViewModel.listaDePreciosAnadir)
                            adapter.notifyDataSetChanged()
                            binding.rvElegirProductoAnadir.requestLayout()
                            binding.tvProductosAnadidosAnadir.isVisible = false
                            binding.nsvElegirProductoAnadir.isVisible = false
                            binding.llMonto.isVisible = false
                            sharedViewModel.opcionesListAnadir.clear()

                        },
                        { error ->
                          /*  TextNombre?.setText("")
                            TextFecha?.setText("")
                            binding.tvListaDesplegableProveedor.setText("Eliga una opción",false)
                            binding.tvListaDesplegableAlmacen.setText("Eliga una opción",false)
                            TextComentarios?.setText("")*/
                            Toast.makeText(requireContext(),"Error $error y ${sharedViewModel.listaDeProductosAnadir} y ${sharedViewModel.listaDeCantidadesAnadir}", Toast.LENGTH_LONG).show()
                        }
                    )

                    {
                        override fun getParams(): MutableMap<String, String> {
                            val parametros = HashMap<String, String>()
                            parametros.put("ID_FACTURA_ENTRADA", id.toString())
                            parametros.put("NUMERO_DE_PRODUCTOS",sharedViewModel.listaDeProductosAnadir.size.toString())
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
                Toast.makeText(requireContext(),"Error $error", Toast.LENGTH_LONG).show()
            }
        ){
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("FACTURA_ENTRADA", TextNombre?.text.toString())
                return parametros
            }
        }
        queue.add(jsonObjectRequest)
    }

    fun anadirInventario(){
        //Si el inventario es menor que la transferencia que no la haga y si el almacen de destino no existe, que lo cree
        val url1 = "http://186.64.123.248/FacturaEntrada/anadirInventarioCompleta.php" // Reemplaza esto con tu URL de la API
        val queue1 =Volley.newRequestQueue(requireContext())
        val stringRequest = object: StringRequest(
            Request.Method.POST,
            url1,
            { response ->
                Toast.makeText(requireContext(),"Cantidad de inventario añadida exitosamente", Toast.LENGTH_SHORT).show()
            },
            { error ->
                /*  TextNombre?.setText("")
                  TextFecha?.setText("")
                  DropDownOrigen?.setText("Eliga una opción",false)
                  DropDownDestino?.setText("Eliga una opción",false)
                  TextComentarios?.setText("")
                  TextCodigoDeBarra?.setText("")*/
                Toast.makeText(requireContext(),"Error $error y ${sharedViewModel.listaDeProductosAnadir} y ${sharedViewModel.listaDeCantidadesAnadir}", Toast.LENGTH_LONG).show()
            }
        )

        {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("NUMERO_DE_PRODUCTOS",sharedViewModel.listaDeProductosAnadir.size.toString())
                parametros.put("ALMACEN", DropDownAlmacen?.text.toString())
                for (i in 0..<sharedViewModel.listaDeProductosAnadir.size) {
                    parametros["PRODUCTO$i"] = sharedViewModel.listaDeProductosAnadir[i].uppercase()
                    parametros["CANTIDAD$i"] = sharedViewModel.listaDeCantidadesAnadir[i]
                }

                return parametros
            }
        }
        queue1.add(stringRequest)
    }/*,

   /* private fun getRetrofit(): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl("http://186.64.123.248/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }*/


   /* private fun searchByName() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
            val myResponse: Response<ProductosDataResponse> =
                retrofit.create(ApiServiceProducto::class.java).getProductos()
                adapter = AnadirInventarioAdapter { appearQuantity(it) }
                //myResponse.body()!!.Productos.toMutableList()
                if (myResponse.isSuccessful) {
                    Log.i("Sebastian", "Funciona!!")
                    val response: ProductosDataResponse? = myResponse.body()
                    withContext(Dispatchers.Main) {
                        // val response2: AlmacenesItemResponse? = myResponse2.body()
                        if (response != null) {
                            Log.i("Sebastian", response.toString())
                            //Log.i("Sebastian", response2.toString())
                            activity?.runOnUiThread {
                                binding.rvProductos.isVisible = false
                                binding.etProductos.addTextChangedListener { userFilter ->
                                    binding.rvProductos.isVisible = true
                                    if (binding.etProductos.text.isNullOrBlank()) {
                                        binding.rvProductos.isVisible = false
                                    } else {
                                        //Modificar aquí, se revienta porque no hay conexion al servidor
                                        val listaFiltradas =
                                            myResponse.body()!!.Productos.toMutableList()
                                                .filter { producto ->
                                                    producto.Nombre.lowercase()
                                                        .contains(userFilter.toString().lowercase())
                                                }

                                        adapter.filtrar(listaFiltradas)
                                    }
                                }
                                adapter.updateList(response.Productos)
                                setUpRecyclerView()

                            }

                        }
                    }
                } else {
                    Log.i("Sebastian", "No funciona.")
                }
            }catch (e:SocketTimeoutException){
                activity?.runOnUiThread { Toast.makeText(requireContext(),"Se acabo el tiempo de espera para conectar la aplicación al servidor",Toast.LENGTH_LONG).show()}
            }catch(e:Exception){
                activity?.runOnUiThread { Toast.makeText(requireContext(),"No se puedo conectar la aplicación al servidor",Toast.LENGTH_LONG).show()}
            }
        }
    }*/

   /* private fun appearQuantity(id:String) {
        val nombre = adapter.obtenerNombreEnPosicion(id.toInt())
        binding.llProductos.isVisible = true
        binding.tvProductos.setText(nombre)
        binding.rvProductos.isVisible = false
    }*/*/

    override fun onDestroyView() {
        super.onDestroyView()
        adapter.notifyDataSetChanged()
        binding.rvElegirProductoAnadir.requestLayout()
        _binding = null
    }
}