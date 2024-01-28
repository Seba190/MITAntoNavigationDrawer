package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.transform.CircleCropTransformation
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentAnadirProductoBinding
import com.seba.mitantonavigationdrawer.databinding.ItemAlertBinding
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.MisDatosFragmentArgs
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.AlmacenesAdapter
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.AlmacenesDataResponse
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.ApiService
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream


//@Suppress("UNREACHABLE_CODE")
class AnadirProductoFragment : Fragment(R.layout.fragment_anadir_producto), TuDialogo.OnDialogResultListener{
   // UploadRequestBody.UploadCallback {
 /*   companion object{
        private const val  CAMERA_PERMISSION = android.Manifest.permission.CAMERA

    }*/

    private val sharedViewModel2: SharedViewModel by activityViewModels()
    private val viewModel: AnadirProductoViewModel by viewModels()
    private val alertasAlmacenesItemResponse: AlertasAlmacenesItemResponse? = null
    private lateinit var adapterPrecioCompra :ProveedorPrecioCompraAdapter
    private lateinit var adapterPrecioVenta :ClientePrecioVentaAdapter
    private lateinit var retrofit: Retrofit
    private var selectedImageUrl: Uri? = null
    private var _binding: FragmentAnadirProductoBinding? = null
    private var requestCamara: ActivityResultLauncher<String>? = null
   // private lateinit var  viewModel: AnadirProductoViewModel
    private var base64 = ""
    private val sharedViewModel : SharedViewModel by activityViewModels()
    var TextEstado :Int = 1
    var TextTipoDeProducto : Int = 1
    var TvAutoCompleteTipoDeProducto: AutoCompleteTextView? = null
    var TextNombre: EditText? = null
    var TextPeso: EditText? = null
    var TextVolumen: EditText? = null
    var TextFoto: EditText? = null
    var TextCodigoBarra: EditText? = null
    var TextCodigoEmbalaje: EditText? = null
    var TextUnidadesEmbalaje: EditText? = null
    var Imagen: ImageView? = null
    private val ListaDeAlmacenes : MutableList<String> = mutableListOf()
    private val ListaDeAlertas : MutableList<String> = mutableListOf()
    private val ListaDeProductos : MutableList<String> = mutableListOf()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //El codigo del onActivityResult

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       // val anadirDatosViewModel =
        //    ViewModelProvider(this).get(AnadirProductoViewModel::class.java)
        _binding = FragmentAnadirProductoBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa

        ///////La lista desplegable y la subida de los datos al servidor////////////
        TvAutoCompleteTipoDeProducto = binding.tvAutoCompleteTipoDeProducto.findViewById(R.id.tvAutoCompleteTipoDeProducto)
        TextNombre = binding.etNombreProducto.findViewById(R.id.etNombreProducto)
        TextPeso = binding.etPesoProducto.findViewById(R.id.etPesoProducto)
        TextVolumen = binding.etVolumenProducto.findViewById(R.id.etVolumenProducto)
        TextCodigoBarra = binding.etCodigoBarraProducto.findViewById(R.id.etCodigoBarraProducto)
        TextCodigoEmbalaje = binding.etCodigoBarraEmbalaje.findViewById(R.id.etCodigoBarraEmbalaje)
        TextUnidadesEmbalaje = binding.etUnidadesEmbalaje.findViewById(R.id.etUnidadesEmbalaje)
        TextFoto = binding.etNombreImagen.findViewById(R.id.etNombreImagen)
        Imagen = binding.ivImageSelectedProducto.findViewById(R.id.ivImageSelectedProducto)

        binding.etNombreProducto.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etPesoProducto.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etVolumenProducto.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etCodigoBarraProducto.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etCodigoBarraEmbalaje.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etUnidadesEmbalaje.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etNombreImagen.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)

        binding.tvAutoCompleteTipoDeProducto.setOnClickListener {
            ListaDesplegableProducto()
        }

        binding.buttonProducto.setOnClickListener {
            ValidacionesIdInsertarDatos()
            Handler(Looper.getMainLooper()).postDelayed({
                InsertarAlertasyAlmacenes()
            },5000)

            //binding.etCodigoBarraProducto.setText(arguments?.getString("key"))
        }


        ///////El dialogo con alertas por almacen////////////

        binding.buttonAlerts.setOnClickListener {
           checkIfFragmentAttached { val alertasAlmacenesFragment = AlertasAlmacenesFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.rlAnadirProducto,alertasAlmacenesFragment,"tag_añadir_producto")
                .commit()}

           // findNavController().navigate(R.id.action_nav_añadir_producto_to_nav_alertas_almacenes)
          //  goToDialogAlertsWarehouses()
        }

        binding.buttonBuyPrice.setOnClickListener {
           // goToDialogBuyPriceSuppliers()
        }

        binding.buttonSellPrice.setOnClickListener {
          //  goToDialogSellPriceCustomers()
        }
        ////La camara///////////////
        val btnCamara = binding.buttonTomar.findViewById<Button>(R.id.buttonTomar)
        btnCamara.setOnClickListener {
            findNavController().navigate(R.id.action_nav_añadir_producto_to_nav_camara)
        }

        sharedViewModel.selectedImage.observe(viewLifecycleOwner) { imageByteArray ->
            binding.ivImageSelectedProducto.setImageBitmap(
                BitmapFactory.decodeByteArray(
                    imageByteArray,
                    0,
                    imageByteArray?.size ?: 0

                )
            )
        }

        retrofit = getRetrofit()

        requestCamara = registerForActivityResult(ActivityResultContracts.RequestPermission(),){
            if(it){
                findNavController().navigate(R.id.action_nav_añadir_producto_to_nav_barcode_scan_producto)
            }else{
                Toast.makeText(requireContext(),"Permiso denegado",Toast.LENGTH_LONG).show()
            }
        }
        binding.bEscanearCodigoDeBarraProducto.setOnClickListener {
            requestCamara?.launch(android.Manifest.permission.CAMERA)
        }
        //Tengo que correr la aplicación con la lista de Edittext de esta forma
        val listaDeEditText : MutableList<EditText?> = mutableListOf(TextNombre,
            TextCodigoBarra)

        return root

    }

    private fun ValidacionesIdInsertarDatos() {
        //INICIO EXPERIMIENTO!!!!!!!!!!!!!!!!!!!!!!!!!! (FUNCIONO)
        val queue =Volley.newRequestQueue(requireContext())
        val url ="http://186.64.123.248/Producto/registro.php"
        val jsonObjectRequest = object: StringRequest(
            Request.Method.POST,url,
            { response ->
                val drawable = Imagen?.drawable as BitmapDrawable
                val bitmap = (drawable).bitmap
                val fotoBase64 = convertirBitmapABase64(bitmap)
                if(!TextNombre?.text.toString().isBlank() && drawable is BitmapDrawable){
                    val id = JSONObject(response).getString("ID_PRODUCTO")
                    //unico = 1
                    //no unico = 0
                    val unico = JSONObject(response).getString("PRODUCTO_UNICO")
                    if (unico == "1") {
                        //Aqui va el código para validar el almacen
                        val url1 = "http://186.64.123.248/Producto/insertar.php" // Reemplaza esto con tu URL de la API
                        val queue1 =Volley.newRequestQueue(requireContext())
                        // Convertir la imagen a Base64 y agregarla a los parámetros
                        //val resourceId = R.id.ivImageSelectedProducto // Reemplaza con el ID de tu imagen en recursos
                        //val bitmapDrawable = obtenerBitmapDrawableDesdeRecurso(requireContext(), resourceId)
                         // Ahora, puedes establecer este BitmapDrawable en tu ImageView
                        val stringRequest = object: StringRequest(
                            Request.Method.POST,
                            url1,
                            { response ->
                                Toast.makeText(requireContext(), "Producto agregado exitosamente. El id de ingreso es el número $id ", Toast.LENGTH_LONG).show()
                                TextPeso?.setText("")
                                TextVolumen?.setText("")
                                TextFoto?.setText("")
                                TextCodigoBarra?.setText("")
                                TextCodigoEmbalaje?.setText("")
                                TextUnidadesEmbalaje?.setText("")
                                //Imagen?.setImageDrawable(null)
                                Imagen?.setImageResource(R.drawable.android_logo)
                                TvAutoCompleteTipoDeProducto?.setText("Eliga una opción",false)
                            },
                            { error ->
                                //Toast.makeText(requireContext(), "Producto agregado exitosamente. El id de ingreso es el número $id ", Toast.LENGTH_LONG).show()
                                Toast.makeText(requireContext(),"El tipo de producto es obligatorio.", Toast.LENGTH_LONG).show()
                                //Toast.makeText(requireContext(),"Error $fotoBase64", Toast.LENGTH_LONG).show()


                            }
                        )

                        {
                            override fun getParams(): MutableMap<String, String> {
                                val parametros = HashMap<String, String>()
                                parametros.put("ID_PRODUCTO", id.toString())
                                parametros.put("PRODUCTO", TextNombre?.text.toString().uppercase())
                                parametros.put("TIPO_PRODUCTO", TvAutoCompleteTipoDeProducto?.text.toString())
                                parametros.put("ESTADO_PRODUCTO", TextEstado.toString())
                                parametros.put("PESO_PRODUCTO", TextPeso?.text.toString())
                                parametros.put("VOLUMEN_PRODUCTO", TextVolumen?.text.toString())
                                //parametros.put("FOTO_PRODUCTO", TextFoto?.text.toString().uppercase())
                                parametros.put("FOTO_PRODUCTO", fotoBase64)
                                parametros.put("CODIGO_BARRA_PRODUCTO", TextCodigoBarra?.text.toString().uppercase())
                                parametros.put("CODIGO_BARRA_EMBALAJE", TextCodigoEmbalaje?.text.toString().uppercase())
                                parametros.put("UNIDADES_EMBALAJE", TextUnidadesEmbalaje?.text.toString())
                                return parametros
                            }
                        }
                        queue1.add(stringRequest)

                    }
                    else if (unico == "0"){
                        //VolleyError("El almacén ya se encuentra en la base de datos")
                        //queue1.cancelAll(TAG)
                        //jsonObjectRequest1.cancel()
                        Toast.makeText(requireContext(), "El producto ya se encuentra en la base de datos", Toast.LENGTH_LONG).show()
                    }
                }
                else if(TextNombre?.text.toString().isBlank() && drawable is BitmapDrawable){
                    Toast.makeText(requireContext(), "El nombre del producto es obligatorio", Toast.LENGTH_LONG).show()
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
                parametros.put("PRODUCTO", TextNombre?.text.toString().uppercase())

                return parametros
            }
        }
        queue.add(jsonObjectRequest)
    }
    private fun InsertarAlertasyAlmacenes(){
        for (i in 0..<sharedViewModel.listaDeAlertas.size) {
            setFragmentResultListener("AlertaAlmacen$i"){key,bundle ->
                ListaDeAlmacenes.add(bundle.getString("Almacen$i")!!.toString().uppercase())
                ListaDeAlertas.add(bundle.getString("Alerta$i")!!)
                ListaDeProductos.add(binding.etNombreProducto.text.toString().uppercase())
            }}
        val queueAlertas =Volley.newRequestQueue(requireContext())
        val urlAlertas = "http://186.64.123.248/Producto/alertasAlmacenEnviar.php"
        val jsonObjectRequestAlertas =object: StringRequest(
            Request.Method.POST,
            urlAlertas,{
                response ->
                      TextNombre?.setText("")
                      Toast.makeText(requireContext(), "Alertas ingresadas exitosamente a la base de datos", Toast.LENGTH_LONG).show()
                    },
                    { error ->
                    Toast.makeText(requireContext(), " No se ha podido enviar los almacenes y alertas", Toast.LENGTH_LONG).show()
                  /*  for (i in 0..<sharedViewModel.listaDeAlertas.size) {
                        Log.i("ALMACEN$i",ListaDeAlmacenes[i])
                         Log.i("PRODUCTO$i", ListaDeProductos[i])
                        Log.i("ALERTA$i",ListaDeAlertas[i])
                    }*/
            })
            { override fun getParams(): MutableMap<String, String> {
                val parametrosAlertas = HashMap<String, String>()
                parametrosAlertas["NUMERO_DE_ALERTAS"] = sharedViewModel.listaDeAlertas.size.toString()
                for (i in 0..<sharedViewModel.listaDeAlertas.size) {
                    parametrosAlertas["ALMACEN$i"] = ListaDeAlmacenes[i]
                    parametrosAlertas["PRODUCTO$i"] = ListaDeProductos[i]
                    parametrosAlertas["ALERTA$i"] = ListaDeAlertas[i]
                    }

                 return parametrosAlertas
                }
            }
        queueAlertas.add(jsonObjectRequestAlertas)
    }

    private fun ListaDesplegableProducto() {
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 ="http://186.64.123.248/Producto/TipoDeProducto.php"
        val jsonObjectRequest1 = JsonObjectRequest(
            Request.Method.GET,url1, null,
            { response ->
                // Obtén el array de opciones desde el objeto JSON
                val jsonArray = response.getJSONArray("Lista2")
                // Convierte el array JSON a una lista mutable
                val opcionesList = mutableListOf<String>()
                for (i in 0 until jsonArray.length()) {
                    opcionesList.add(jsonArray.getString(i).removeSurrounding("'","'"))
                }
                //Crea un adpatador para el dropdown
                val adapter = ArrayAdapter(requireContext(),R.layout.list_item_producto,opcionesList)
                //binding.tvholaMundo?.setText(response.getString("Lista"))
                TvAutoCompleteTipoDeProducto?.setAdapter(adapter)

                TvAutoCompleteTipoDeProducto?.onItemClickListener = AdapterView.OnItemClickListener {
                        parent, view, position, id ->
                    val itemSelected = parent.getItemAtPosition(position)
                }
            }, { error ->
                Toast.makeText(requireContext(), " Conecte la aplicación al servidor", Toast.LENGTH_LONG).show()
            }
        )
        queue1.add(jsonObjectRequest1)
    }

    fun convertirBitmapABase64(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl("http://186.64.123.248/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }



    private fun searchByNameBuyPrice() {
        CoroutineScope(Dispatchers.IO).launch{
            val myResponse : Response<ProveedorPrecioCompraDataResponse> = retrofit.create(ApiServicePrecioCompra::class.java).getProveedorPrecioCompra()
            // retrofit.create(ApiServiceAlertas::class.java).getAlertasAlmacenes()
            if(myResponse.isSuccessful){
                Log.i("Sebastian", "Funciona!!")
                val response: ProveedorPrecioCompraDataResponse? = myResponse.body()
                if(response != null){
                    Log.i("Sebastian", response.toString())
                    //Log.i("Sebastian", response2.toString())
                    activity?.runOnUiThread {
                        adapterPrecioCompra.updateList(response.Proveedores)

                    }
                }

            }else{
                Log.i("Sebastian", "No funciona.")
            }
        }
    }

    private fun searchByNameSellPrice() {
        CoroutineScope(Dispatchers.IO).launch{
            val myResponse : Response<ClientePrecioVentaDataResponse> = retrofit.create(ApiServicePrecioVenta::class.java).getClientePrecioVenta()
            // retrofit.create(ApiServiceAlertas::class.java).getAlertasAlmacenes()
            if(myResponse.isSuccessful){
                Log.i("Sebastian", "Funciona!!")
                val response: ClientePrecioVentaDataResponse? = myResponse.body()
                if(response != null){
                    Log.i("Sebastian", response.toString())
                    //Log.i("Sebastian", response2.toString())
                    activity?.runOnUiThread {
                        adapterPrecioVenta.updateList(response.Clientes)

                    }
                }

            }else{
                Log.i("Sebastian", "No funciona.")
            }
        }
    }

    override fun onResult(parametro: String) {
        val editText = view?.findViewById<EditText>(R.id.etCodigoBarraProducto)
        editText?.setText(parametro)
        //
    }
    fun checkIfFragmentAttached(operation: Context.() -> Unit) {
        if (isAdded) {
            operation(requireContext())
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




    /* private fun goToDialogTest(){
         val dialog = TuDialogo()
         dialog.onDialogResultListener = this
         dialog.show(requireActivity().supportFragmentManager,"TagDelDialogo")
     }

     private fun goToDialogAlertsWarehouses() {
         val dialog = Dialog(requireActivity())
         dialog.setContentView(R.layout.alertas_por_almacen)
         val bindingAlertWarehouses = ItemAlertBinding.bind(requireView())
         adapter = AlertasAlmacenesAdapter()
         val recyclerView = dialog.findViewById<RecyclerView>(R.id.rvAlmacenes)
         recyclerView.setHasFixedSize(true)
         recyclerView.layoutManager = LinearLayoutManager(requireContext())
         recyclerView.adapter = adapter
         searchByName()
         val button =dialog.findViewById<Button>(R.id.bAgregarAlmacen)

         button.setOnClickListener {
            // binding.etCodigoBarraProducto.setText(alertasAlmacenesItemResponse!!.Nombre)
             //binding.etCodigoBarraProducto.setText(bindingAlertWarehouses.tvWarehouse.text.toString())
            dialog.hide()
         }
         update()
         dialog.show()
     }


     private fun goToDialogBuyPriceSuppliers() {
         val dialog = Dialog(requireActivity())
         dialog.setContentView(R.layout.precios_compra_por_proveedor)
         adapterPrecioCompra = ProveedorPrecioCompraAdapter()
         val recyclerView = dialog.findViewById<RecyclerView>(R.id.rvSuppliers)
         recyclerView.setHasFixedSize(true)
         recyclerView.layoutManager = LinearLayoutManager(requireContext())
         recyclerView.adapter = adapterPrecioCompra
         searchByNameBuyPrice()
         dialog.show()
     }

     private fun goToDialogSellPriceCustomers() {
         val dialog = Dialog(requireActivity())
         dialog.setContentView(R.layout.precios_venta_por_cliente)
         adapterPrecioVenta = ClientePrecioVentaAdapter()
         val recyclerView = dialog.findViewById<RecyclerView>(R.id.rvCustomers)
         recyclerView.setHasFixedSize(true)
         recyclerView.layoutManager = LinearLayoutManager(requireContext())
         recyclerView.adapter = adapterPrecioVenta
         searchByNameSellPrice()
         dialog.show()
     }

     */

    interface OnTextChangeListener {
        fun onTextChange(text: String, viewHolder: AlertasAlmacenesViewHolder)
    }
    fun actualizarDatos(){
        sharedViewModel2.sharedData.value = "Datos actualizados desde el fragmento principal"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel2.sharedData.observe(viewLifecycleOwner){
                newData ->
            val alertasAlmacenesFragment = parentFragmentManager.findFragmentByTag("tag_alertas_almacenes") as? AlertasAlmacenesFragment
            alertasAlmacenesFragment?.view?.findViewById<Button>(R.id.bAgregarAlmacen)?.setOnClickListener {
                binding.etCodigoBarraProducto.setText(newData)
                Toast.makeText(requireContext(),newData,Toast.LENGTH_LONG).show()
            }
            alertasAlmacenesFragment?.view?.findViewById<Button>(R.id.bAlertaAlmacenVolver)?.setOnClickListener {

            }
        }
    }


}




