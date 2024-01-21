package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirInventario

import android.graphics.PorterDuff
import android.os.Bundle
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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentAnadirInventarioBinding
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.AlmacenesAdapter
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.AlmacenesDataResponse
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.SocketTimeoutException


class AnadirInventarioFragment : Fragment(R.layout.fragment_anadir_inventario) {

    private var _binding: FragmentAnadirInventarioBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    var TextNombre: EditText? = null
    var TextFecha: EditText? = null
    var TextComentarios: EditText? = null
    var DropDownProveedor: AutoCompleteTextView? = null
    var DropDownAlmacen: AutoCompleteTextView? = null
    private lateinit var retrofit: Retrofit
    private lateinit var adapter: AnadirInventarioAdapter
    private var listaProductos: MutableList<ProductosItemResponse> = arrayListOf()

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
        binding.etProductos.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)

        ListaDesplegableProveedor()
        ListaDesplegableAlmacen()
        retrofit = getRetrofit()

        searchByName()

        binding.FacturaEntradaButtonEnviar.setOnClickListener {
            ValidacionesIdInsertarDatos()
        }
        binding.llCajasDeProducto.isVisible = false
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
        }


       /* binding.etProductos.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                filtrar(s.toString())
            }

        })*/


        return root
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

    private fun setUpRecyclerView() {

        //adapter = AnadirInventarioAdapter{appearQuantity(it)}
        // adapter2 = CaracteristicasAdapter()
        binding.rvProductos.setHasFixedSize(true)
        binding.rvProductos.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProductos.adapter = adapter
    }

    private fun ValidacionesIdInsertarDatos() {
        //INICIO EXPERIMIENTO!!!!!!!!!!!!!!!!!!!!!!!!!! (FUNCIONO)
        val queue =Volley.newRequestQueue(requireContext())
        val url ="http://186.64.123.248/FacturaEntrada/registro.php"
        val jsonObjectRequest = object: StringRequest(
            Request.Method.POST,url,
            { response ->
                if(!TextNombre?.text.toString().isBlank()){
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
                                Toast.makeText(requireContext(), "Factura agregada exitosamente. El id de ingreso es el número $id ", Toast.LENGTH_LONG).show()
                                TextNombre?.setText("")
                                TextFecha?.setText("")
                                DropDownProveedor?.setText("Eliga una opción",false)
                                DropDownAlmacen?.setText("Eliga una opción",false)
                                TextComentarios?.setText("")
                            },
                            { error ->
                                Toast.makeText(requireContext(),"El proveedor y el almacén son obligatorios", Toast.LENGTH_LONG).show()
                                //Toast.makeText(requireContext(),"Error $error", Toast.LENGTH_LONG).show()
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

    private fun getRetrofit(): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl("http://186.64.123.248/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun searchByName() {
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
    }

    private fun appearQuantity(id:String) {
        val nombre = adapter.obtenerNombreEnPosicion(id.toInt())
        binding.llProductos.isVisible = true
        binding.tvProductos.setText(nombre)
        binding.rvProductos.isVisible = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}