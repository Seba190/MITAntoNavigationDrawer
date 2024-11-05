package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentAnadirProductoBinding
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.lang.Exception


//@Suppress("UNREACHABLE_CODE")
class AnadirProductoFragment : Fragment(R.layout.fragment_anadir_producto), TuDialogo.OnDialogResultListener {
    // UploadRequestBody.UploadCallback {
    /*   companion object{
        private const val  CAMERA_PERMISSION = android.Manifest.permission.CAMERA

    }*/
    private val sharedViewModel2: SharedViewModel by activityViewModels()

    // private val viewModel: AnadirProductoViewModel by viewModels()
    private val alertasAlmacenesItemResponse: AlertasAlmacenesItemResponse? = null
    private lateinit var adapterPrecioCompra: ProveedorPrecioCompraAdapter
    private lateinit var adapterPrecioVenta: ClientePrecioVentaAdapter
    private lateinit var retrofit: Retrofit
    private var selectedImageUrl: Uri? = null
    private var _binding: FragmentAnadirProductoBinding? = null
    private var requestCamara: ActivityResultLauncher<String>? = null

    // private lateinit var  viewModel: AnadirProductoViewModel
    private var base64 = ""
    private val sharedViewModel: SharedViewModel by activityViewModels()
    var TextEstado: Int = 1
    var TextTipoDeProducto: Int = 1
    var TvAutoCompleteTipoDeProducto: AutoCompleteTextView? = null
    var TextNombre: EditText? = null
    var TextPeso: EditText? = null
    var TextVolumen: EditText? = null
    var TextFoto: EditText? = null
    var TextCodigoBarra: EditText? = null
    var TextCodigoEmbalaje: EditText? = null
    var TextUnidadesEmbalaje: EditText? = null
    var Imagen: ImageView? = null

    //Guardar la información en el fragment
    private var nombre: String? = null
    private var peso: Float? = null
    private var volumen: Float? = null
    private var codigoDeBarra: String? = null
    private var codigoDeBarraEmbalaje: String? = null
    private var unidadesEmbalaje: Int? = null

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

        binding.etNombreProducto.getBackground().setColorFilter(
            getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP
        )
        binding.etPesoProducto.getBackground().setColorFilter(
            getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP
        )
        binding.etVolumenProducto.getBackground().setColorFilter(
            getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP
        )
        binding.etCodigoBarraProducto.getBackground().setColorFilter(
            getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP
        )
        binding.etCodigoBarraEmbalaje.getBackground().setColorFilter(
            getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP
        )
        binding.etUnidadesEmbalaje.getBackground().setColorFilter(
            getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP
        )
        binding.etNombreImagen.getBackground().setColorFilter(
            getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP
        )

        // binding.tvAutoCompleteTipoDeProducto.setOnClickListener {
        ListaDesplegableTipoDeProducto()

        //}

        binding.buttonProducto.setOnClickListener {
            if(sharedViewModel.opcionesListProductoTipoDeProducto.contains(TvAutoCompleteTipoDeProducto?.text.toString()) &&
                TvAutoCompleteTipoDeProducto?.text.toString() != "Elija una opción") {
                ValidacionesIdInsertarDatos()
            }else if (!sharedViewModel.opcionesListProductoTipoDeProducto.contains(TvAutoCompleteTipoDeProducto?.text.toString()) &&
                TvAutoCompleteTipoDeProducto?.text.toString() == "Elija una opción"){
                Toast.makeText(requireContext(),"Debe elegir el tipo de producto", Toast.LENGTH_SHORT).show()

            }else if (!sharedViewModel.opcionesListProductoTipoDeProducto.contains(TvAutoCompleteTipoDeProducto?.text.toString()) &&
                TvAutoCompleteTipoDeProducto?.text.toString() != "Elija una opción") {
                Toast.makeText(requireContext(), "El tipo de producto no es válido", Toast.LENGTH_SHORT
                ).show()
            }
        }
        ///////El dialogo con alertas por almacen////////////

        binding.buttonAlerts.setOnClickListener {
            /* for( i in 0 ..< sharedViewModel.listaDeAlertas.size){
                if(sharedViewModel.listaDeAlertas[i] == "" && sharedViewModel.listaDeBodegas[i] == "")
                    sharedViewModel.listaDeAlertas.removeAt(i)
                    sharedViewModel.listaDeBodegas.removeAt(i)
            }*/
            checkIfFragmentAttached {
                val alertasAlmacenesFragment = AlertasAlmacenesFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.rlAnadirProducto, alertasAlmacenesFragment, "tag_añadir_producto")
                    .commit()
            }

            // findNavController().navigate(R.id.action_nav_añadir_producto_to_nav_alertas_almacenes)
            //  goToDialogAlertsWarehouses()
        }

        binding.buttonBuyPrice.setOnClickListener {
            checkIfFragmentAttached {
                val proveedorPrecioCompraFragment = ProveedorPrecioCompraFragment()
                parentFragmentManager.beginTransaction()
                    .replace(
                        R.id.rlAnadirProducto,
                        proveedorPrecioCompraFragment,
                        "tag_proveedor_precio_compra"
                    )
                    .commit()
            }
        }

        binding.buttonSellPrice.setOnClickListener {
            checkIfFragmentAttached {
                val clientePrecioVentaFragment = ClientePrecioVentaFragment()
                parentFragmentManager.beginTransaction()
                    .replace(
                        R.id.rlAnadirProducto,
                        clientePrecioVentaFragment,
                        "tag_precio_venta_clientes"
                    )
                    .commit()
            }
        }
        ////La camara///////////////
        val btnCamara = binding.buttonTomar.findViewById<Button>(R.id.buttonTomar)
        btnCamara.setOnClickListener {
            findNavController().navigate(R.id.action_nav_añadir_producto_to_nav_camara_actualizada)
        }

        retrofit = getRetrofit()

        sharedViewModel.selectedImage.observe(viewLifecycleOwner) { imageByteArray ->
            if (imageByteArray != null && imageByteArray.isNotEmpty()) {
                val bitmap = BitmapFactory.decodeByteArray(
                    imageByteArray,
                    0,
                    imageByteArray/*?*/.size/* ?: 0*/
                )
                val redondearBitmap = getRoundedCornerBitmap(bitmap, 200f)
                binding.ivImageSelectedProducto.setImageBitmap(redondearBitmap)
            }else{
                binding.ivImageSelectedProducto.setImageResource(R.drawable.android_logo)
            }
        }
        requestCamara = registerForActivityResult(ActivityResultContracts.RequestPermission(),) {
            if (it) {
                /*val barcodeScanProductoFragment = BarcodeScanProductoFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.rlAnadirProducto, barcodeScanProductoFragment)
                    .commitNow()*/
                findNavController().navigate(R.id.action_nav_añadir_producto_to_nav_barcode_scan_producto)
            } else {
                Toast.makeText(requireContext(), "Permiso denegado", Toast.LENGTH_SHORT).show()
            }
        }
        binding.bEscanearCodigoDeBarraProducto.setOnClickListener {
            setFragmentResult(
                "Añadir Producto",
                bundleOf(
                    "nombre" to binding.etNombreProducto.text.toString(),
                    "peso" to binding.etPesoProducto.text.toString(),
                    "volumen" to binding.etVolumenProducto.text.toString(),
                    "tipoDeProducto" to binding.tvAutoCompleteTipoDeProducto.text.toString(),
                    "unidadesEmbalaje" to binding.etUnidadesEmbalaje.text.toString()
                )
            )
            requestCamara?.launch(android.Manifest.permission.CAMERA)

        }
        //Tengo que correr la aplicación con la lista de Edittext de esta forma
        val listaDeEditText: MutableList<EditText?> = mutableListOf(
            TextNombre,
            TextCodigoBarra
        )

        codigoDeBarraProducto()
        codigoDeBarraEmbalaje()

        // val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        // isFirstRun = prefs.getBoolean("is_first_run",true)
        setFragmentResultListener("Producto") { key, bundle ->
            binding.etCodigoBarraProducto.setText(
                bundle.getString("Producto")
            )
        }
        setFragmentResultListener("Embalaje") { key, bundle ->
            binding.etCodigoBarraEmbalaje.setText(
                bundle.getString("Embalaje")
            )
        }

        setFragmentResultListener("Añadir Producto vuelta") { key, bundle ->
            binding.etNombreProducto.setText(bundle.getString("nombre"))
            binding.etPesoProducto.setText(bundle.getString("peso"))
            binding.etVolumenProducto.setText(bundle.getString("volumen"))
            binding.tvAutoCompleteTipoDeProducto.setText(bundle.getString("tipoDeProducto"), false)
            binding.etUnidadesEmbalaje.setText(bundle.getString("unidadesEmbalaje"))
        }
        //De alertas almacenes
        //No funcionan las listas que agregan el nombre del producto, están vacias
        for (i in 0..<sharedViewModel.listaDeAlertasAnadir.size) {
            if (sharedViewModel.listaDeAlertasAnadir[i] != "") {
                setFragmentResultListener("AlertaAlmacen$i") { key, bundle ->
                    sharedViewModel.ListasDeAlmacenes.add(
                        bundle.getString("Almacen$i")!!.toString().uppercase()
                    )
                    sharedViewModel.ListasDeAlertas.add(bundle.getString("Alerta$i")!!)
                    //sharedViewModel.ListasDeProductosAlertas.add(binding.etNombreProducto.text.toString().uppercase())
                }
            }
        }
        //De precio compra proeedores
        for (i in 0..<sharedViewModel.listaDePreciosCompraAnadir.size) {
            if (sharedViewModel.listaDePreciosCompraAnadir[i] != "") {
                setFragmentResultListener("PreciosCompraProveedores$i") { key, bundle ->
                    sharedViewModel.ListasDeProveedores.add(
                        bundle.getString("Proveedores$i")!!.toString().uppercase()
                    )
                    sharedViewModel.ListasDePreciosDeCompra.add(bundle.getString("PreciosCompra$i")!!)
                    // sharedViewModel.ListasDeProductosPrecioCompra.add(binding.etNombreProducto.text.toString().uppercase())
                }
            }
        }
        //De precio venta clientes
        for (i in 0..<sharedViewModel.listaDePreciosVentaAnadir.size) {
            if (sharedViewModel.listaDePreciosVentaAnadir[i] != "") {
                setFragmentResultListener("PreciosVentaClientes$i") { key, bundle ->
                    sharedViewModel.ListasDeClientes.add(
                        bundle.getString("Clientes$i")!!.toString().uppercase()
                    )
                    sharedViewModel.ListasDePreciosDeVenta.add(bundle.getString("PreciosDeVenta$i")!!)
                    // sharedViewModel.ListasDeProductosPrecioVenta.add(binding.etNombreProducto.text.toString().uppercase())
                }
            }
        }
        return root

    }


    private fun ValidacionesIdInsertarDatos() {
        //INICIO EXPERIMIENTO!!!!!!!!!!!!!!!!!!!!!!!!!! (FUNCIONO)
        val queue = Volley.newRequestQueue(requireContext())
        val url = "http://186.64.123.248/Producto/registro.php"
        val jsonObjectRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                val drawable = Imagen?.drawable as BitmapDrawable
                val bitmap = (drawable).bitmap
                val fotoBase64 = convertirBitmapABase64(bitmap)
                if (!TextNombre?.text.toString().isBlank() && drawable is BitmapDrawable) {
                    val id = JSONObject(response).getString("ID_PRODUCTO")
                    //unico = 1
                    //no unico = 0
                    val unico = JSONObject(response).getString("PRODUCTO_UNICO")
                    if (unico == "1") {
                        //Aqui va el código para validar el almacen
                        val url1 =
                            "http://186.64.123.248/Producto/insertar.php" // Reemplaza esto con tu URL de la API
                        val queue1 = Volley.newRequestQueue(requireContext())
                        // Convertir la imagen a Base64 y agregarla a los parámetros
                        //val resourceId = R.id.ivImageSelectedProducto // Reemplaza con el ID de tu imagen en recursos
                        //val bitmapDrawable = obtenerBitmapDrawableDesdeRecurso(requireContext(), resourceId)
                        // Ahora, puedes establecer este BitmapDrawable en tu ImageView
                        val stringRequest = object : StringRequest(
                            Request.Method.POST,
                            url1,
                            { response ->
                                Toast.makeText(
                                    requireContext(),
                                    "Producto agregado exitosamente. El id de ingreso es el número $id ",
                                    Toast.LENGTH_SHORT
                                ).show()
                                //Solo 1 se envía:
                                if (sharedViewModel.ListasDeAlertas.isNotEmpty() && sharedViewModel.ListasDePreciosDeVenta.isEmpty() && sharedViewModel.ListasDePreciosDeCompra.isEmpty()) {
                                 //   Handler(Looper.getMainLooper()).postDelayed({
                                        InsertarAlertasyAlmacenes()
                                  //  }, 4000)
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        Toast.makeText(requireContext(),
                                            "Alertas ingresadas exitosamente a la base de datos"
                                            , Toast.LENGTH_SHORT).show()
                                          }, 3000)
                                }
                                //Solo 2 se envía:
                                if (sharedViewModel.ListasDeAlertas.isEmpty() && sharedViewModel.ListasDePreciosDeVenta.isEmpty() && sharedViewModel.ListasDePreciosDeCompra.isNotEmpty()) {
                                        InsertarPreciosCompraYProveedores()
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        Toast.makeText(requireContext(),
                                            "Precios de compra ingresados exitosamente a la base de datos"
                                            , Toast.LENGTH_SHORT).show()
                                    }, 3000)
                                }
                                //Solo 3 se envía
                                if (sharedViewModel.ListasDeAlertas.isEmpty() && sharedViewModel.ListasDePreciosDeVenta.isNotEmpty() && sharedViewModel.ListasDePreciosDeCompra.isEmpty()) {
                                        InsertarPreciosVentayClientes()
                                        Handler(Looper.getMainLooper()).postDelayed({
                                        Toast.makeText(requireContext(),
                                            "Precios de venta ingresados exitosamente a la base de datos"
                                            , Toast.LENGTH_SHORT).show()
                                    }, 3000)

                                }
                                //1 y 2 se envían
                                if (sharedViewModel.ListasDeAlertas.isNotEmpty() && sharedViewModel.ListasDePreciosDeVenta.isEmpty() && sharedViewModel.ListasDePreciosDeCompra.isNotEmpty()) {
                                   // Handler(Looper.getMainLooper()).postDelayed({
                                        InsertarAlertasyAlmacenes()
                                  //  }, 4000)
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        InsertarPreciosCompraYProveedores()
                                    }, 50)
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        Toast.makeText(requireContext(),
                                            "Alertas y precios de compra ingresados exitosamente a la base de datos"
                                            , Toast.LENGTH_SHORT).show()
                                    }, 3000)
                                }
                                //1 y 3 s envían
                                if (sharedViewModel.ListasDeAlertas.isNotEmpty() && sharedViewModel.ListasDePreciosDeVenta.isNotEmpty() && sharedViewModel.ListasDePreciosDeCompra.isEmpty()) {
                                 //   Handler(Looper.getMainLooper()).postDelayed({
                                        InsertarAlertasyAlmacenes()
                                //    }, 4000)
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        InsertarPreciosVentayClientes()
                                    }, 50)
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        Toast.makeText(requireContext(),
                                            "Alertas y precios de venta ingresados exitosamente a la base de datos"
                                            , Toast.LENGTH_SHORT).show()
                                    }, 3000)
                                }
                                //2 y 3 se envían
                                if (sharedViewModel.ListasDeAlertas.isEmpty() && sharedViewModel.ListasDePreciosDeVenta.isNotEmpty() && sharedViewModel.ListasDePreciosDeCompra.isNotEmpty()) {
                              //      Handler(Looper.getMainLooper()).postDelayed({
                                        InsertarPreciosCompraYProveedores()
                              //      }, 4000)
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        InsertarPreciosVentayClientes()
                                    }, 50)
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        Toast.makeText(requireContext(),
                                            "Precios de compra y precios de venta ingresados exitosamente a la base de datos"
                                            , Toast.LENGTH_SHORT).show()
                                    }, 3000)
                                }
                                //1, 2 y 3 se envían
                                if (sharedViewModel.ListasDeAlertas.isNotEmpty() && sharedViewModel.ListasDePreciosDeVenta.isNotEmpty() && sharedViewModel.ListasDePreciosDeCompra.isNotEmpty()) {
                             //       Handler(Looper.getMainLooper()).postDelayed({
                                        InsertarAlertasyAlmacenes()
                             //       }, 4000)
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        InsertarPreciosCompraYProveedores()
                                    }, 50)
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        InsertarPreciosVentayClientes()
                                    }, 100)
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        Toast.makeText(requireContext(),
                                            "Alertas, precios de compra y precios de venta ingresados exitosamente"
                                            , Toast.LENGTH_SHORT).show()
                                    }, 3000)
                                }
                                TextPeso?.setText("")
                                TextVolumen?.setText("")
                                TextFoto?.setText("")
                                TextCodigoBarra?.setText("")
                                TextCodigoEmbalaje?.setText("")
                                TextUnidadesEmbalaje?.setText("")
                                //Imagen?.setImageDrawable(null)
                                Imagen?.setImageResource(R.drawable.android_logo)
                                TvAutoCompleteTipoDeProducto?.setText("Elija una opción", false)
                            },
                            { error ->
                                //Toast.makeText(requireContext(), "Producto agregado exitosamente. El id de ingreso es el número $id ", Toast.LENGTH_SHORT).show()
                                Toast.makeText(
                                    requireContext(),
                                    "El tipo de producto es obligatorio.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                //Toast.makeText(requireContext(),"Error $fotoBase64", Toast.LENGTH_SHORT).show()


                            }
                        ) {
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
                                parametros.put(
                                    "CODIGO_BARRA_PRODUCTO",
                                    TextCodigoBarra?.text.toString().uppercase()
                                )
                                parametros.put(
                                    "CODIGO_BARRA_EMBALAJE",
                                    TextCodigoEmbalaje?.text.toString().uppercase()
                                )
                                parametros.put(
                                    "UNIDADES_EMBALAJE",
                                    TextUnidadesEmbalaje?.text.toString()
                                )
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
                            "El producto ya se encuentra en la base de datos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (TextNombre?.text.toString().isBlank() && drawable is BitmapDrawable) {
                    Toast.makeText(
                        requireContext(),
                        "El nombre del producto es obligatorio",
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
                //Toast.makeText(requireContext(), "Conecte la aplicación al servidor", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("PRODUCTO", TextNombre?.text.toString().uppercase())

                return parametros
            }
        }
        queue.add(jsonObjectRequest)
    }

    private fun InsertarAlertasyAlmacenes() {
        val queueAlertas = Volley.newRequestQueue(requireContext())
        val urlAlertas = "http://186.64.123.248/Producto/alertasAlmacenEnviar.php"
        val jsonObjectRequestAlertas = object : StringRequest(
            Request.Method.POST,
            urlAlertas, { response ->
                sharedViewModel.listaDeAlertasAnadir.clear()
                sharedViewModel.listaDeBodegasAnadir.clear()
                sharedViewModel.ListasDeAlertas.clear()
                sharedViewModel.ListasDeAlmacenes.clear()
                sharedViewModel.ListasDeProductosAlertas.clear()
                if (sharedViewModel.ListasDeAlertas.isNotEmpty() && sharedViewModel.ListasDePreciosDeVenta.isEmpty() && sharedViewModel.ListasDePreciosDeCompra.isEmpty()) {
                    TextNombre?.setText("")
                }
               /* Toast.makeText(
                    requireContext(),
                   "Alertas ingresadas exitosamente a la base de datos",
                    Toast.LENGTH_SHORT
                ).show()*/
            },
            { error ->
              /*  Toast.makeText(
                    requireContext(),
                    " No se ha podido enviar los almacenes y alertas",
                    Toast.LENGTH_SHORT
                ).show()*/
                /*  for (i in 0..<sharedViewModel.listaDeAlertas.size) {
                        Log.i("ALMACEN$i",ListaDeAlmacenes[i])
                         Log.i("PRODUCTO$i", ListaDeProductos[i])
                        Log.i("ALERTA$i",ListaDeAlertas[i])
                    }*/
            }) {
            override fun getParams(): MutableMap<String, String> {
                val parametrosAlertas = HashMap<String, String>()
                parametrosAlertas["NUMERO_DE_ALERTAS"] =
                    sharedViewModel.ListasDeAlertas.size.toString()
                for (i in 0..<sharedViewModel.ListasDeAlertas.size) {
                    parametrosAlertas["ALMACEN$i"] = sharedViewModel.ListasDeAlmacenes[i]
                    parametrosAlertas["PRODUCTO$i"] = binding.etNombreProducto.text.toString()
                    parametrosAlertas["ALERTA$i"] = sharedViewModel.ListasDeAlertas[i]

                }

                return parametrosAlertas
            }
        }
        queueAlertas.add(jsonObjectRequestAlertas)
    }

    private fun InsertarPreciosCompraYProveedores() {
        val queuePrecioCompra = Volley.newRequestQueue(requireContext())
        val urlPrecioCompra = "http://186.64.123.248/Producto/proveedorPrecioCompraEnviar.php"
        val jsonObjectRequestPrecioCompra = object : StringRequest(
            Request.Method.POST,
            urlPrecioCompra, { response ->
                sharedViewModel.listaDePreciosCompraAnadir.clear()
                sharedViewModel.listaDeProveedoresAnadir.clear()
                sharedViewModel.ListasDePreciosDeCompra.clear()
                sharedViewModel.ListasDeProveedores.clear()
                sharedViewModel.ListasDeProductosPrecioCompra.clear()
                if (sharedViewModel.ListasDePreciosDeCompra.isNotEmpty() && sharedViewModel.ListasDeAlertas.isEmpty() && sharedViewModel.ListasDePreciosDeVenta.isEmpty()
                    || (sharedViewModel.ListasDePreciosDeCompra.isNotEmpty() && sharedViewModel.ListasDeAlertas.isNotEmpty() && sharedViewModel.ListasDePreciosDeVenta.isEmpty())
                ) {
                    TextNombre?.setText("")
                }
               /* Toast.makeText(
                    requireContext(),
                    "Precios de compra ingresados exitosamente a la base de datos",
                    Toast.LENGTH_SHORT
                ).show()*/
            },
            { error ->
               /* Toast.makeText(
                    requireContext(),
                    " No se ha podido enviar los proveedores y precios de compra",
                    Toast.LENGTH_SHORT
                ).show()*/
                /*  for (i in 0..<sharedViewModel.listaDeAlertas.size) {
                      Log.i("ALMACEN$i",ListaDeAlmacenes[i])
                       Log.i("PRODUCTO$i", ListaDeProductos[i])
                      Log.i("ALERTA$i",ListaDeAlertas[i])
                  }*/
            }) {
            override fun getParams(): MutableMap<String, String> {
                val parametrosPrecioCompra = HashMap<String, String>()
                parametrosPrecioCompra["NUMERO_DE_PRECIOS_DE_COMPRA"] =
                    sharedViewModel.ListasDePreciosDeCompra.size.toString()
                for (i in 0..<sharedViewModel.ListasDePreciosDeCompra.size) {
                    parametrosPrecioCompra["PROVEEDOR$i"] = sharedViewModel.ListasDeProveedores[i]
                    parametrosPrecioCompra["PRODUCTO$i"] = binding.etNombreProducto.text.toString()
                    parametrosPrecioCompra["PRECIO_COMPRA$i"] =
                        sharedViewModel.ListasDePreciosDeCompra[i]

                }

                return parametrosPrecioCompra
            }
        }
        queuePrecioCompra.add(jsonObjectRequestPrecioCompra)
    }

    private fun InsertarPreciosVentayClientes() {
        val queuePrecioVenta = Volley.newRequestQueue(requireContext())
        val urlPrecioVenta = "http://186.64.123.248/Producto/clientePrecioVentaEnviar.php"
        val jsonObjectRequestPrecioVenta = object : StringRequest(
            Request.Method.POST,
            urlPrecioVenta, { response ->
                sharedViewModel.listaDePreciosVentaAnadir.clear()
                sharedViewModel.listaDeClientesAnadir.clear()
                sharedViewModel.ListasDePreciosDeVenta.clear()
                sharedViewModel.ListasDeClientes.clear()
                sharedViewModel.ListasDeProductosPrecioVenta.clear()
                TextNombre?.setText("")
               /* Toast.makeText(
                    requireContext(),
                   "Precios de venta ingresados exitosamente a la base de datos",
                    Toast.LENGTH_SHORT
                ).show()*/
            },
            { error ->
               /* Toast.makeText(
                    requireContext(),
                    " No se ha podido enviar los clientes y precios de venta",
                    Toast.LENGTH_SHORT
                ).show()*/
                /*  for (i in 0..<sharedViewModel.listaDeAlertas.size) {
                      Log.i("ALMACEN$i",ListaDeAlmacenes[i])
                       Log.i("PRODUCTO$i", ListaDeProductos[i])
                      Log.i("ALERTA$i",ListaDeAlertas[i])
                  }*/
            }) {
            override fun getParams(): MutableMap<String, String> {
                val parametrosPrecioVenta = HashMap<String, String>()
                parametrosPrecioVenta["NUMERO_DE_PRECIOS_DE_VENTA"] =
                    sharedViewModel.ListasDePreciosDeVenta.size.toString()
                for (i in 0..<sharedViewModel.ListasDePreciosDeVenta.size) {
                    parametrosPrecioVenta["CLIENTE$i"] = sharedViewModel.ListasDeClientes[i]
                    parametrosPrecioVenta["PRODUCTO$i"] = binding.etNombreProducto.text.toString()
                    parametrosPrecioVenta["PRECIO_VENTA$i"] = sharedViewModel.ListasDePreciosDeVenta[i]

                }

                return parametrosPrecioVenta
            }
        }
        queuePrecioVenta.add(jsonObjectRequestPrecioVenta)
    }

    private fun ListaDesplegableTipoDeProducto() {
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 = "http://186.64.123.248/Producto/TipoDeProducto.php"
        val jsonObjectRequest1 = JsonObjectRequest(
            Request.Method.GET, url1, null,
            { response ->
                // Obtén el array de opciones desde el objeto JSON
                val jsonArray = response.getJSONArray("Lista2")
                // Convierte el array JSON a una lista mutable
                sharedViewModel.opcionesListProductoTipoDeProducto.clear()
                if(sharedViewModel.opcionesListProductoTipoDeProducto.isEmpty()) {
                    for (i in 0 until jsonArray.length()) {
                        sharedViewModel.opcionesListProductoTipoDeProducto.add(jsonArray.getString(i).removeSurrounding("'", "'"))
                    }
                }
                sharedViewModel.opcionesListProductoTipoDeProducto.sort()
                //Crea un adpatador para el dropdown
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sharedViewModel.opcionesListProductoTipoDeProducto)
                //binding.TvAutoCompleteTipoDeProducto?.setText(response.getString("Lista"))
                TvAutoCompleteTipoDeProducto?.setAdapter(adapter)
                TvAutoCompleteTipoDeProducto?.threshold = 1
                TvAutoCompleteTipoDeProducto?.onItemClickListener =
                    AdapterView.OnItemClickListener { parent, view, position, id ->
                        val itemSelected = parent.getItemAtPosition(position)
                    }
                TvAutoCompleteTipoDeProducto?.setOnClickListener {
                    if(TvAutoCompleteTipoDeProducto?.text.toString() == "Elija una opción"){
                        binding.tvAutoCompleteTipoDeProducto.setText("",false)
                        TvAutoCompleteTipoDeProducto?.showDropDown()
                    }
                }
                TvAutoCompleteTipoDeProducto?.setOnFocusChangeListener { _, hasFocus ->
                    if(hasFocus && TvAutoCompleteTipoDeProducto?.text.toString() == "Elija una opción"){
                        binding.tvAutoCompleteTipoDeProducto.setText("",false)
                        TvAutoCompleteTipoDeProducto?.showDropDown()
                    }
                    else if (!hasFocus && !sharedViewModel.opcionesListProductoTipoDeProducto.contains(TvAutoCompleteTipoDeProducto?.text.toString())){
                        Toast.makeText(requireContext(),"El tipo de producto no es válido", Toast.LENGTH_SHORT).show()
                    }
                }
            }, { error ->
                // Toast.makeText(requireContext(), " Conecte la aplicación al servidor", Toast.LENGTH_SHORT).show()
            }
        )
        queue1.add(jsonObjectRequest1)
    }

    fun convertirBitmapABase64(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
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

    fun getRoundedCornerBitmap(bitmap: Bitmap, radius: Float): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val paint = Paint()
        paint.isAntiAlias = true
        val shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.shader = shader

        val rect = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
        canvas.drawRoundRect(rect, radius, radius, paint)

        bitmap.recycle()
        return output
    }
    //override fun onOptionsItemSelected(item: MenuItem): Boolean {
      /*  if(item.itemId == android.R.id.home){
            viewModel.CodigoDeBarra.observe(viewLifecycleOwner) { newText ->
                // if(!segundaVez) {
                val pictureDialog = AlertDialog.Builder(requireContext())
                pictureDialog.setTitle("¿Cómo quieres ingresar tu código?")
                val pictureDialogItem = arrayOf(
                    "Código de Producto",
                    "Código de embalaje"
                )
                pictureDialog.setItems(pictureDialogItem) { dialog, which ->
                    when (which) {
                        0 -> binding.etCodigoBarraProducto.setText(newText)
                        1 -> binding.etCodigoBarraEmbalaje.setText(newText)
                    }
                }
                pictureDialog.show()
                // segundaVez = true
                //}

            }


        }*/
    //    return super.onOptionsItemSelected(item)

    //}
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

    /*override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState?.let { bundle ->
            nombre = bundle.getString("nombre")
            peso = bundle.getFloat("peso")
            volumen = bundle.getFloat("volumen")
            codigoDeBarra = bundle.getString("codigoDeBarra")
            codigoDeBarraEmbalaje = bundle.getString("codigoDeBarraEmbalaje")
            unidadesEmbalaje = bundle.getInt("unidadesEmbalaje")
        }
        nombre.let { nombre -> binding.etNombreProducto.setText(nombre)}
        /*peso.let { peso -> binding.etPesoProducto.setText(peso.toString())}
        volumen.let { volumen -> binding.etVolumenProducto.setText(volumen.toString())}
        codigoDeBarra.let { codigoDeBarra -> binding.etCodigoBarraProducto.setText(codigoDeBarra)}
        codigoDeBarraEmbalaje.let { codigoDeBarraEmbalaje -> binding.etCodigoBarraEmbalaje.setText(codigoDeBarraEmbalaje)}
        unidadesEmbalaje.let { unidadesEmbalaje -> binding.etUnidadesEmbalaje.setText(unidadesEmbalaje.toString())}*/
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if(peso != null) { outState.putFloat("peso", peso!!)}
        if(volumen != null) { outState.putFloat("volumen", volumen!!)}
        if(unidadesEmbalaje != null){outState.putInt("unidadesEmbalaje", unidadesEmbalaje!!)}
        outState.putString("nombre", nombre)
        if(codigoDeBarra != ""){outState.putString("codigoDeBarra", codigoDeBarra)}
        if(codigoDeBarraEmbalaje != ""){outState.putString("codigoDeBarraEmbalaje", codigoDeBarraEmbalaje)}
}*/




    interface OnTextChangeListener {
        fun onTextChange(text: String, viewHolder: AlertasAlmacenesViewHolder)
    }
    fun actualizarDatos(){
        sharedViewModel2.sharedData.value = "Datos actualizados desde el fragmento principal"
    }



    private fun codigoDeBarraProducto(){
        binding.etCodigoBarraProducto.setOnClickListener {
            if(binding.etCodigoBarraProducto.text.isNotBlank()){
                val queue = Volley.newRequestQueue(requireContext())
                val url = "http://186.64.123.248/Transferencia/codigoDeBarraProducto.php"
                val jsonObjectRequest = object : StringRequest(
                    Request.Method.POST, url,
                    { response ->
                        try {
                            val codigoBarraProducto = JSONObject(response).getString("PRODUCTO")
                            val codigoBarraEmbalaje = JSONObject(response).getString("EMBALAJE")
                            if (codigoBarraEmbalaje == "") {
                                Toast.makeText(
                                    requireContext(),
                                    "EL código de barra pertenece al producto $codigoBarraProducto",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else if (codigoBarraProducto == "") {
                                Toast.makeText(
                                    requireContext(),
                                    "Código de barra de embalaje que pertenece al producto $codigoBarraEmbalaje",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        catch(e: Exception){
                            Toast.makeText(requireContext(), "No hay producto o embalaje asociado a este producto",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    { error ->
                        Toast.makeText(
                            requireContext(),
                            "No hay producto o embalaje asociado a este producto",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                    override fun getParams(): MutableMap<String, String> {
                        val parametros = HashMap<String, String>()
                        parametros.put(
                            "CODIGO_BARRA_PRODUCTO",
                            binding.etCodigoBarraProducto.text.toString()
                        )
                        return parametros
                    }
                }
                queue.add(jsonObjectRequest)
            }
        }
    }

    private fun codigoDeBarraEmbalaje(){
        binding.etCodigoBarraEmbalaje.setOnClickListener {
            if(binding.etCodigoBarraEmbalaje.text.isNotBlank()){
                val queue = Volley.newRequestQueue(requireContext())
                val url = "http://186.64.123.248/Transferencia/codigoDeBarraProducto.php"
                val jsonObjectRequest = object : StringRequest(
                    Request.Method.POST, url,
                    { response ->
                        try {
                            val codigoBarraProducto = JSONObject(response).getString("PRODUCTO")
                            val codigoBarraEmbalaje = JSONObject(response).getString("EMBALAJE")
                            if (codigoBarraEmbalaje == "") {
                                Toast.makeText(
                                    requireContext(),
                                    "EL código de barra pertenece al producto $codigoBarraProducto",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else if (codigoBarraProducto == "") {
                                Toast.makeText(
                                    requireContext(),
                                    "Código de barra de embalaje que pertenece al producto $codigoBarraEmbalaje",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        catch(e: Exception){
                            Toast.makeText(requireContext(), "No hay producto o embalaje asociado a este producto",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    { error ->
                        Toast.makeText(
                            requireContext(),
                            "No hay producto o embalaje asociado a este producto",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                    override fun getParams(): MutableMap<String, String> {
                        val parametros = HashMap<String, String>()
                        parametros.put(
                            "CODIGO_BARRA_PRODUCTO",
                            binding.etCodigoBarraEmbalaje.text.toString()
                        )
                        return parametros
                    }
                }
                queue.add(jsonObjectRequest)
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}




