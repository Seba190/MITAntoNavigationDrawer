package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.tiposDeProductos.editarTiposDeProductos

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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

import com.seba.mitantonavigationdrawer.databinding.FragmentEditarTiposDeProductosBinding
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.MisDatosFragmentArgs
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.MisDatosFragmentDirections
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.editarAlmacen.EditarAlmacenFragmentDirections
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos.ApiServiceProductos
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos.ProductosAdapter
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos.ProductosDataResponse
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.proveedores.editarProveedor.EditarProveedorViewModel
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class EditarTiposDeProductosFragment: Fragment(R.layout.fragment_editar_tipos_de_productos), RadioGroup.OnCheckedChangeListener{
    companion object {
        const val EXTRA_ID = "extra_id"
    }

    private var _binding: FragmentEditarTiposDeProductosBinding? = null
    private val args: MisDatosFragmentArgs by navArgs()
    private val sharedViewModel : SharedViewModel by activityViewModels()
    private lateinit var retrofit: Retrofit
    private lateinit var adapter: EditarTiposDeProductosAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    var TextNombre: EditText? = null
    var TextDescripcion: EditText? = null
    var TextEstado: Int = 1
    var radioGroup: RadioGroup? = null
    var radio1: RadioButton? = null
    var radio2: RadioButton? = null
   // var EliminarTipoDeProducto: ImageView? = null
    //var TextId: EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val editarTiposDeProductosViewModel =
            ViewModelProvider(this).get(EditarTiposDeProductosViewModel::class.java)

        _binding = FragmentEditarTiposDeProductosBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa
        TextNombre = binding.etNombreTipoDeProductoEditar.findViewById(R.id.etNombreTipoDeProductoEditar)
        TextDescripcion = binding.etDescripcionTipoDeProductoEditar.findViewById(R.id.etDescripcionTipoDeProductoEditar)
      //  EliminarTipoDeProducto = binding.ivEliminarTipoDeProducto.findViewById(R.id.ivEliminarTipoDeProducto)
        radio1 = binding.EstadoRadioButton1.findViewById(R.id.EstadoRadioButton1)
        radio2 = binding.EstadoRadioButton2.findViewById(R.id.EstadoRadioButton2)
        radioGroup = binding.radioGroupEstado.findViewById(R.id.radioGroupEstado)
        radioGroup?.setOnCheckedChangeListener(this)

        binding.etNombreTipoDeProductoEditar.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etDescripcionTipoDeProductoEditar.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)

        //EliminarTipoDeProducto?.setOnClickListener {
        //    mensajeEliminarTipoDeProducto()
       // }

        //   Mostrar los usuarios responsables en la lista desplegable
        binding.buttonTipoDeProductoEditar.setOnClickListener {
            if (TextNombre?.text.toString().isNotBlank()) {
                actualizarTipoDeProducto()
            }else {
                Toast.makeText(
                    requireContext(),
                    "El nombre del tipo de producto es obligatorio",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        if(binding.buttonTipoDeProductoEditar.isPressed){
            radio1?.isChecked
        }
        try {
            sharedViewModel.id.add(args.id)
        }
        catch(e:Exception){
        }
        retrofit = getRetrofit()
        initUI()
        return root
    }

    private fun initUI() {

        searchByName()
        adapter = EditarTiposDeProductosAdapter{/*navigateToEditarImagen(it)*/}
        // adapter2 = CaracteristicasAdapter()
        binding.rvProductos.setHasFixedSize(true)
        binding.rvProductos.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProductos.adapter = adapter
        // binding.rvAlmacenes.adapter = adapter2
    }

    private fun searchByName() {
        CoroutineScope(Dispatchers.IO).launch{
            val myResponse : Response<EditarTiposDeProductosDataResponse> = retrofit.create(ApiServiceEditarTiposDeProductos::class.java).getProductos(sharedViewModel.id.last())
            if(myResponse.isSuccessful){
                Log.i("Sebastian", "Funciona!!")
                val response: EditarTiposDeProductosDataResponse? = myResponse.body()
                // val response2: AlmacenesItemResponse? = myResponse2.body()
                if(response != null){
                    Log.i("Sebastian", response.toString())
                    //Log.i("Sebastian", response2.toString())
                    activity?.runOnUiThread {
                        adapter.updateList(response.Productos)
                    }
                }

            }else{
                Log.i("Sebastian", "No funciona.")
            }
        }
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl("http://186.64.123.248/Reportes/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

  /*  private fun navigateToEditarImagen(id:String){
        //val action = MisDatosFragmentDirections.actionNavMisDatosToNavEditarCliente(id = id)
        val action = MisDatosFragmentDirections.actionNavMisDatosToNavEditarProducto(id = id)
        findNavController().navigate(action)
    }*/
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val queue1 = Volley.newRequestQueue(requireContext())
        //val id = arguments?.getString(EXTRA_ID).toString()
        Log.i("Sebastian", "Valor de Id de destino es: ${sharedViewModel.id.last()}")
        //  val id2 = this.arguments
        // val id3 = id2?.get(EXTRA_ID)
        //val id4 = almacenesItemResponse.Id
        val url1 = "http://186.64.123.248/Reportes/TiposDeProducto/registroInsertar.php?ID_TIPO_PRODUCTO=${sharedViewModel.id.last()}"
        val jsonObjectRequest1 = JsonObjectRequest(
            Request.Method.GET, url1, null,
            { response ->
                inventarioDeProductos()
                existenciasPorAlmacen()
                TextNombre?.setText(response.getString("TIPO_PRODUCTO"))
                TextDescripcion?.setText(response.getString("DESCRIPCION"))
                TextEstado = response.getString("ESTADO_TIPO_PRODUCTO").toInt()
                if (TextEstado == 1) {
                    radio1?.isChecked = true
                } else if (TextEstado == 0) {
                    radio2?.isChecked = true
                }

            }, { error ->
                Toast.makeText(requireContext(), "El id es ${sharedViewModel.id.last()}", Toast.LENGTH_SHORT).show()
            }
        )
        queue1.add(jsonObjectRequest1)

      requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
          override fun handleOnBackPressed() {
              // Navegar a MisDatosFragment y pasar un identificador para saber qué fragmento llamar luego
              // val action = EditarClienteFragmentDirections.actionEditarClienteFragmentToMisDatosFragment("clientes")
              val action = EditarTiposDeProductosFragmentDirections.actionNavEditarTiposDeProductosToNavMisDatos(destino = "tipos de producto")
              findNavController().navigate(action)
          }
      })
    }
    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when(checkedId){
            radio1?.id -> TextEstado = 1
            radio2?.id -> TextEstado = 0
        }
    }
    private fun actualizarTipoDeProducto() {
        //unico = 1
        //no unico = 0
        //Aqui va el código para validar el almacen
        val url1 = "http://186.64.123.248/Reportes/TiposDeProducto/actualizarTipoDeProducto.php" // Reemplaza esto con tu URL de la API
        val queue1 =Volley.newRequestQueue(requireContext())
        // Convertir la imagen a Base64 y agregarla a los parámetros
        // Ahora, puedes establecer este BitmapDrawable en tu ImageView
        val stringRequest = object: StringRequest(
            Request.Method.POST,
            url1,
            { response ->
                Toast.makeText(requireContext(), "Tipo de producto agregado exitosamente. Id número ${sharedViewModel.id.last()} ", Toast.LENGTH_SHORT).show()

            },
            { error ->
                //Toast.makeText(requireContext(), "Producto agregado exitosamente. El id de ingreso es el número $id ", Toast.LENGTH_SHORT).show()
                Toast.makeText(requireContext(),"$error",Toast.LENGTH_SHORT).show()
                Log.i("Sebastian", "$error")
                //Toast.makeText(requireContext(),"Error $fotoBase64", Toast.LENGTH_SHORT).show()


            }
        )

        {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("ID_TIPO_PRODUCTO", sharedViewModel.id.last())
                parametros.put("TIPO_PRODUCTO", TextNombre?.text.toString().uppercase())
                parametros.put("ESTADO_TIPO_PRODUCTO", TextEstado.toString())
                parametros.put("DESCRIPCION",TextDescripcion?.text.toString().uppercase())

                return parametros
            }
        }
        queue1.add(stringRequest)

        // TextId?.setText(response.getString("ID_ALMACEN"))
        // Toast.makeText(requireContext(),"Id ingresado correctamente al formulario.", Toast.LENGTH_SHORT).show()
    }

    private fun inventarioDeProductos(){
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 = "http://186.64.123.248/Reportes/TiposDeProducto/inventariosDeProducto.php?ID_TIPO_PRODUCTO=${sharedViewModel.id.last()}"
        val jsonObjectRequest1 = JsonObjectRequest(
            Request.Method.GET, url1, null,
            { response ->
                try {
                    // val matriz = response.getJSONArray("Lista")
                    val productos = response.getJSONArray("Productos")
                    val inventario = response.getJSONArray("Inventario")
                    val listaProductos = mutableListOf<String>()
                    val listaInventario = mutableListOf<String>()
                    for (i in (0 until productos.length())) {
                        listaProductos.add(productos.getString(i))
                    }
                    for (i in (0 until inventario.length())) {
                        listaInventario.add(inventario.getString(i))
                    }
                    for (i in (0 until listaProductos.count())) {
                        val registro = LayoutInflater.from(requireContext())
                            .inflate(R.layout.filas_inventario, null, false)
                        val tv0 = registro.findViewById<View>(R.id.tv0) as TextView
                        val tv1 = registro.findViewById<View>(R.id.tv1) as TextView
                        tv0.text = listaProductos[i]
                        tv1.text = listaInventario[i]
                        tv0.setTextColor(Color.DKGRAY)
                        tv1.setTextColor(Color.DKGRAY)
                        tv0.gravity = Gravity.CENTER
                        tv1.gravity = Gravity.CENTER
                        binding.tlInventariosDeProducto.addView(registro)
                        if(listaInventario[i] != "0"){
                        registro.findViewById<TableRow>(R.id.trFilaTabla).setOnClickListener {
                            //  binding.clEditarTiposDeProductos.isVisible = false
                            sharedViewModel.inventario.add(listaInventario[i])
                            val editarCantidadTiposDeProductosFragment =
                                EditarCantidadTiposDeProductosFragment()
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.rlEditarTiposDeProductos, editarCantidadTiposDeProductosFragment)
                                .commit()
                           }
                        }
                    }
                }catch(e:Exception){
                    binding.llInventariosDeProducto.isVisible = false
                }
                //Toast.makeText(requireContext(),"${listaAlmacenes[0][0]} y $listaAlmacenes",Toast.LENGTH_SHORT).show()

            }, { error ->
                binding.llInventariosDeProducto.isVisible = false
            }
        )
        queue1.add(jsonObjectRequest1)
    }

    private fun existenciasPorAlmacen(){
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 = "http://186.64.123.248/Reportes/TiposDeProducto/existenciasPorAlmacen.php?ID_TIPO_PRODUCTO=${sharedViewModel.id.last()}"
        val jsonObjectRequest1 = JsonObjectRequest(
            Request.Method.GET, url1, null,
            { response ->
                try {
                    // val matriz = response.getJSONArray("Lista")
                    val almacenes = response.getJSONArray("Almacenes")
                    val inventario = response.getJSONArray("Inventario")
                    val listaAlmacenes = mutableListOf<String>()
                    val listaInventario = mutableListOf<String>()
                    for (i in (0 until almacenes.length())) {
                        listaAlmacenes.add(almacenes.getString(i))
                    }
                    for (i in (0 until inventario.length())) {
                        listaInventario.add(inventario.getString(i))
                    }
                    for (i in (0 until listaAlmacenes.count())) {
                        val registro = LayoutInflater.from(requireContext())
                            .inflate(R.layout.filas_inventario, null, false)
                        val tv0 = registro.findViewById<View>(R.id.tv0) as TextView
                        val tv1 = registro.findViewById<View>(R.id.tv1) as TextView
                        tv0.text = listaAlmacenes[i]
                        tv1.text = listaInventario[i]
                        tv0.setTextColor(Color.DKGRAY)
                        tv1.setTextColor(Color.DKGRAY)
                        tv0.gravity = Gravity.CENTER
                        tv1.gravity = Gravity.CENTER
                        binding.tlExistenciasPorAlmacen.addView(registro)
                    }
                }catch(e:Exception){
                    val queue2 = Volley.newRequestQueue(requireContext())
                    val url2 = "http://186.64.123.248/Reportes/Productos/almacenAlertas.php"
                    val jsonObjectRequest2 = JsonObjectRequest(
                        Request.Method.GET, url2, null,
                        { response ->
                            val almacenes2 = response.getJSONArray("Lista")
                            val listaAlmacenes2 = mutableListOf<String>()
                            for (i in (0 until almacenes2.length())) {
                                listaAlmacenes2.add(almacenes2.getString(i))
                            }
                            for (i in (0 until listaAlmacenes2.count())) {
                                val registro = LayoutInflater.from(requireContext())
                                    .inflate(R.layout.filas_inventario, null, false)
                                val tv0 = registro.findViewById<View>(R.id.tv0) as TextView
                                val tv1 = registro.findViewById<View>(R.id.tv1) as TextView
                                tv0.text = listaAlmacenes2[i]
                                tv1.text = "0"
                                tv0.setTextColor(Color.DKGRAY)
                                tv1.setTextColor(Color.DKGRAY)
                                tv0.gravity = Gravity.CENTER
                                tv1.gravity = Gravity.CENTER
                                binding.tlExistenciasPorAlmacen.addView(registro)
                            }
                        },{error ->

                        } )
                    queue2.add(jsonObjectRequest2)
                }
                //Toast.makeText(requireContext(),"${listaAlmacenes[0][0]} y $listaAlmacenes",Toast.LENGTH_SHORT).show()

            }, { error ->
                val queue3 = Volley.newRequestQueue(requireContext())
                val url3 = "http://186.64.123.248/Reportes/Productos/almacenAlertas.php"
                val jsonObjectRequest3 = JsonObjectRequest(
                    Request.Method.GET, url3, null,
                    { response ->
                        val almacenes3 = response.getJSONArray("Lista")
                        val listaAlmacenes3 = mutableListOf<String>()
                        for (i in (0 until almacenes3.length())) {
                            listaAlmacenes3.add(almacenes3.getString(i))
                        }
                        for (i in (0 until listaAlmacenes3.count())) {
                            val registro = LayoutInflater.from(requireContext())
                                .inflate(R.layout.filas_inventario, null, false)
                            val tv0 = registro.findViewById<View>(R.id.tv0) as TextView
                            val tv1 = registro.findViewById<View>(R.id.tv1) as TextView
                            tv0.text = listaAlmacenes3[i]
                            tv1.text = "0"
                            tv0.setTextColor(Color.DKGRAY)
                            tv1.setTextColor(Color.DKGRAY)
                            tv0.gravity = Gravity.CENTER
                            tv1.gravity = Gravity.CENTER
                            binding.tlExistenciasPorAlmacen.addView(registro)
                        }
                    },{error ->

                    } )
                queue3.add(jsonObjectRequest3)
            }
        )
        queue1.add(jsonObjectRequest1)
    }

    private fun mensajeEliminarTipoDeProducto(){
        AlertDialog.Builder(context).apply {
            setTitle("¿Quieres eliminar este tipo de producto?")
            setMessage("Esta acción no se puede deshacer.")
            setPositiveButton("Sí") { dialog, _ ->
                eliminarTipoDeProducto()
                findNavController().navigate(R.id.action_nav_editar_tipos_de_productos_to_nav_mis_datos)
                dialog.dismiss()
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }

    }
    private fun eliminarTipoDeProducto(){
        val url = "http://186.64.123.248/Reportes/TiposDeProducto/borrar.php"
        val queue = Volley.newRequestQueue(requireContext())
        var jsonObjectRequest = object : StringRequest(Request.Method.POST, url,
            { response ->
                Toast.makeText(requireContext(), "Tipo de producto eliminado exitosamente", Toast.LENGTH_SHORT).show()

            },{error ->
                Toast.makeText(requireContext(), "No se pudo eliminar el tipo de producto", Toast.LENGTH_SHORT).show()
            })
        {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("ID_TIPO_PRODUCTO", sharedViewModel.id.last())
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