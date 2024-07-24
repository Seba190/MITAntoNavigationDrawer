package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos.editarProducto

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentEditarProductoBinding
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto.AlertasAlmacenesFragment
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto.ClientePrecioVentaFragment
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto.ProveedorPrecioCompraFragment
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.MisDatosFragmentArgs
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos.Redondeado
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.tiposDeProductos.editarTiposDeProductos.EditarCantidadTiposDeProductosFragment
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import com.squareup.picasso.Picasso
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import kotlin.Exception
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import java.net.MalformedURLException
import java.net.URL


class EditarProductoFragment : Fragment(R.layout.fragment_editar_producto),RadioGroup.OnCheckedChangeListener {
    private var _binding: FragmentEditarProductoBinding? = null
    private val args: MisDatosFragmentArgs by navArgs()
    private val binding get() = _binding!!
    var TextNombre: EditText? = null
    var TextPeso: EditText? = null
    var TextVolumen: EditText? = null
    var DropwDownTipoDeProducto: AutoCompleteTextView? = null
    var ImagenProducto: ImageView? = null
    var TextNombreImagen: EditText? = null
    var ButtonImagenes: Button? = null
    var ButtonEscanearCodigo: Button? = null
    var TextCodigoDeBarraProducto: EditText? = null
    var TextCodigoDeBarraEmbalaje: EditText? = null
    var TextUnidadesEmbalaje: EditText? = null
    var ButtonAlerts: Button? = null
    var ButtonBuyPrice: Button? = null
    var ButtonSellPrice: Button? = null
    var ButtonProducto: Button? = null
    var TablaAlmacenes: TableLayout? = null
    var Imagen: ImageView? = null
    var TextEstado :Int = 1
    var radioGroup: RadioGroup? = null
    var radio1: RadioButton? = null
    var radio2: RadioButton? = null
    //var EliminarProducto: ImageView? = null

    private var requestCamara: ActivityResultLauncher<String>? = null
    private val sharedViewModel : SharedViewModel by activityViewModels()
    private lateinit var retrofit: Retrofit
    @SuppressLint("CutPasteId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val editarProductoViewModel =
            ViewModelProvider(this).get(EditarProductoViewModel::class.java)

        _binding = FragmentEditarProductoBinding.inflate(inflater, container, false)
        val root: View = binding.root
        TextNombre = binding.etNombreProducto.findViewById(R.id.etNombreProducto)
       // EliminarProducto = binding.ivEliminarProducto.findViewById(R.id.ivEliminarProducto)
        TextPeso = binding.etPesoProducto.findViewById(R.id.etPesoProducto)
        TextVolumen = binding.etVolumenProducto.findViewById(R.id.etVolumenProducto)
        DropwDownTipoDeProducto = binding.tvAutoCompleteTipoDeProducto.findViewById(R.id.tvAutoCompleteTipoDeProducto)
        ImagenProducto = binding.ivImageSelectedProducto.findViewById(R.id.ivImageSelectedProducto)
        TextNombreImagen = binding.etNombreImagen.findViewById(R.id.etNombreImagen)
        ButtonImagenes = binding.buttonTomar.findViewById(R.id.buttonTomar)
        ButtonEscanearCodigo = binding.bEscanearCodigoDeBarraProducto.findViewById(R.id.bEscanearCodigoDeBarraProducto)
        TextCodigoDeBarraProducto = binding.etCodigoBarraProducto.findViewById(R.id.etCodigoBarraProducto)
        TextCodigoDeBarraEmbalaje = binding.etCodigoBarraEmbalaje.findViewById(R.id.etCodigoBarraEmbalaje)
        TextUnidadesEmbalaje = binding.etUnidadesEmbalaje.findViewById(R.id.etUnidadesEmbalaje)
        ButtonAlerts = binding.buttonAlerts.findViewById(R.id.buttonAlerts)
        ButtonBuyPrice = binding.buttonBuyPrice.findViewById(R.id.buttonBuyPrice)
        ButtonSellPrice = binding.buttonSellPrice.findViewById(R.id.buttonSellPrice)
        ButtonProducto = binding.buttonProducto.findViewById(R.id.buttonProducto)
        TablaAlmacenes = binding.tablaAlmacenes.findViewById(R.id.tablaAlmacenes)
        Imagen = binding.ivImageSelectedProducto.findViewById(R.id.ivImageSelectedProducto)
        TablaAlmacenes?.removeAllViews()
        radio1 = binding.EstadoProductoRadioButton1.findViewById(R.id.EstadoProductoRadioButton1)
        radio2 = binding.EstadoProductoRadioButton2.findViewById(R.id.EstadoProductoRadioButton2)
        radioGroup = binding.radioGroupEstadoProducto.findViewById(R.id.radioGroupEstadoProducto)
        radioGroup?.setOnCheckedChangeListener(this)


        TextNombre?.getBackground()?.setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        TextPeso?.getBackground()?.setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        TextVolumen?.getBackground()?.setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        TextNombreImagen?.getBackground()?.setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        TextCodigoDeBarraProducto?.getBackground()?.setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        TextCodigoDeBarraEmbalaje?.getBackground()?.setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        TextUnidadesEmbalaje?.getBackground()?.setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)

        ListaDesplegableTipoDeProducto()

       // EliminarProducto?.setOnClickListener {
       //     mensajeEliminarProducto()
       // }

        ButtonImagenes?.setOnClickListener {
            findNavController().navigate(R.id.action_nav_editar_producto_to_nav_camara_actualizada)
        }

        sharedViewModel.selectedImage.observe(viewLifecycleOwner) { imageByteArray ->
            if (imageByteArray != null && imageByteArray.isNotEmpty()) {
            val bitmap = BitmapFactory.decodeByteArray(
                imageByteArray,
                0,
                imageByteArray/*?*/.size /*?: 0*/
            )
            val redondearBitmap = getRoundedCornerBitmap(bitmap, 200f)
            binding.ivImageSelectedProducto.setImageBitmap(redondearBitmap)
             }else{
            //   binding.ivImageSelectedProducto.setImageBitmap(null)
                binding.ivImageSelectedProducto.setImageResource(R.drawable.android_logo)
            }
        }
        requestCamara = registerForActivityResult(ActivityResultContracts.RequestPermission(),){
            if(it){
                /*val barcodeScanProductoFragment = BarcodeScanProductoFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.rlAnadirProducto, barcodeScanProductoFragment)
                    .commitNow()*/
                findNavController().navigate(R.id.action_nav_editar_producto_to_nav_barcode_scan_editar_producto)
            }else{
                Toast.makeText(requireContext(),"Permiso denegado",Toast.LENGTH_LONG).show()
            }
        }

        ButtonProducto?.setOnClickListener {
            actualizarProducto()
          /*  Toast.makeText(requireContext(),"${sharedViewModel.listaDeAlertas} y ${sharedViewModel.listaDeBodegas}",Toast.LENGTH_LONG).show()
            Handler(Looper.getMainLooper()).postDelayed({
                Toast.makeText(requireContext(),"${sharedViewModel.listaDePreciosCompra} y ${sharedViewModel.listaDeProveedores}",Toast.LENGTH_LONG).show()
            }, 4000)
            Handler(Looper.getMainLooper()).postDelayed({
                Toast.makeText(requireContext(),"${sharedViewModel.listaDePreciosVenta} y ${sharedViewModel.listaDeClientes}",Toast.LENGTH_LONG).show()
            }, 8000)
            Handler(Looper.getMainLooper()).postDelayed({
                Toast.makeText(requireContext(),"${sharedViewModel.numeroAlertas} , ${sharedViewModel.numeroPreciosCompra} y ${sharedViewModel.numeroPreciosVenta}",Toast.LENGTH_LONG).show()
            }, 12000)
            Log.i("Sebastian","${sharedViewModel.listaDeAlertas} y ${sharedViewModel.listaDeBodegas}")
            Log.i("Sebastian","${sharedViewModel.listaDePreciosCompra} y ${sharedViewModel.listaDeProveedores}")
            Log.i("Sebastian","${sharedViewModel.listaDePreciosVenta} y ${sharedViewModel.listaDeClientes}")
            Log.i("Sebastian", "${sharedViewModel.numeroAlertas} , ${sharedViewModel.numeroPreciosCompra} y ${sharedViewModel.numeroPreciosVenta}")*/
        }

        codigoDeBarraProducto()
        codigoDeBarraEmbalaje()
        retrofit = getRetrofit()

        ButtonAlerts?.setOnClickListener {
             val alertasAlmacenesEditarFragment = AlertasAlmacenesEditarFragment()
             parentFragmentManager.beginTransaction()
                    .replace(R.id.rlAnadirProductoEditar,alertasAlmacenesEditarFragment)
                    .commit()}

        //Arreglar los otros dos botones con sus fragments correspondientes
        ButtonBuyPrice?.setOnClickListener {
             val proveedorPrecioCompraEditarFragment = ProveedorPrecioCompraEditarFragment()
             parentFragmentManager.beginTransaction()
                    .replace(R.id.rlAnadirProductoEditar,proveedorPrecioCompraEditarFragment)
                    .commit()
        }

        ButtonSellPrice?.setOnClickListener {
             val clientePrecioVentaEditarFragment = ClientePrecioVentaEditarFragment()
             parentFragmentManager.beginTransaction()
                    .replace(R.id.rlAnadirProductoEditar,clientePrecioVentaEditarFragment)
                    .commit()
        }

        try {
            sharedViewModel.id.add(args.id)
        }
        catch(e:Exception){
        }

        ButtonEscanearCodigo?.setOnClickListener {
            setFragmentResult("Añadir Producto",
                bundleOf("nombre" to binding.etNombreProducto.text.toString(),
                    "peso" to binding.etPesoProducto.text.toString(),
                    "volumen" to binding.etVolumenProducto.text.toString(),
                    "tipoDeProducto" to binding.tvAutoCompleteTipoDeProducto.text.toString(),
                    "unidadesEmbalaje"  to binding.etUnidadesEmbalaje.text.toString())
            )
            requestCamara?.launch(android.Manifest.permission.CAMERA)

        }
        setFragmentResultListener("Producto"){key,bundle ->binding.etCodigoBarraProducto.setText(bundle.getString("Producto"))}
        setFragmentResultListener("Embalaje"){key,bundle ->binding.etCodigoBarraEmbalaje.setText(bundle.getString("Embalaje"))}
        //Aquí se programa
        return root
    }
    //private var segundaVez = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //if(!segundaVez) {
        val queue1 = Volley.newRequestQueue(requireContext())
        //val id = arguments?.getString(EXTRA_ID).toString()
        Log.i("Sebastian", "Valor de Id de destino es: ${sharedViewModel.id.last()}")
        //  val id2 = this.arguments
        // val id3 = id2?.get(EXTRA_ID)
        //val id4 = almacenesItemResponse.Id
        val cornerRadius = 150f
        val url1 = "http://186.64.123.248/Reportes/Productos/registroInsertar.php?ID_PRODUCTO=${sharedViewModel.id.last()}"
         val jsonObjectRequest1 = JsonObjectRequest(
            Request.Method.GET, url1, null,
            { response ->
                tablaInventario()
                if(TextNombre?.text?.isEmpty() == true && TextPeso?.text?.isEmpty() == true &&
                    TextVolumen?.text?.isEmpty() == true && TextCodigoDeBarraProducto?.text?.isEmpty() == true &&
                    TextCodigoDeBarraEmbalaje?.text?.isEmpty() == true && TextUnidadesEmbalaje?.text?.isEmpty() == true) {
                    val urlImagen = response.getString("URL_PRODUCTO")
                    val modificarUrl = urlImagen.replace("'", "%27")
                    val modificarUrlFinal = modificarUrl.replace(" ", "%20")
                    if(isValidUrl(modificarUrl)) {
                        Picasso.get().load(Uri.parse(modificarUrlFinal).toString())
                            .transform(Redondeado(cornerRadius)).into(Imagen, object: com.squareup.picasso.Callback{
                                override fun onSuccess() {
                                    Log.i("Sebastian", "Imagen cargada correctamente desde: $modificarUrlFinal")
                                }

                                override fun onError(e: java.lang.Exception?) {
                                    Log.e("Sebastian", "Error al cargar la imagen desde: $modificarUrlFinal", e)
                                    binding.ivImageSelectedProducto.setImageResource(R.drawable.android_logo)
                                }

                            })
                    }else{
                        Log.e("Sebastian", "URL no válida: $modificarUrlFinal")
                        binding.ivImageSelectedProducto.setImageResource(R.drawable.android_logo)
                    }
                    TextNombre?.setText(response.getString("PRODUCTO"))
                    TextPeso?.setText(response.getString("PESO_PRODUCTO"))
                    TextVolumen?.setText(response.getString("VOLUMEN_PRODUCTO"))
                    // TextNombreImagen?.setText(response.getString("TELEFONO_CLIENTE"))
                    TextCodigoDeBarraProducto?.setText(response.getString("CODIGO_BARRA_PRODUCTO"))
                    TextCodigoDeBarraEmbalaje?.setText(response.getString("CODIGO_BARRA_EMBALAJE"))
                    TextUnidadesEmbalaje?.setText(response.getString("UNIDADES_EMBALAJE"))
                    DropwDownTipoDeProducto?.setText(response.getString("TIPO_PRODUCTO"), false)
                }
                TextEstado = response.getString("ESTADO_PRODUCTO").toInt()
                if (TextEstado == 1) {
                    radio1?.isChecked = true
                } else if (TextEstado == 0) {
                    radio2?.isChecked = true
                }
            }, { error ->
                Toast.makeText(requireContext(), "El id es ${sharedViewModel.id.last()}", Toast.LENGTH_LONG).show()
            }
        )
        queue1.add(jsonObjectRequest1)

         //   segundaVez = true
       // }
    }

    private fun actualizarRegistros() {
        val url1 = "http://186.64.123.248/Reportes/Productos/actualizarProducto.php"
        val queue1 =Volley.newRequestQueue(requireContext())
        val stringRequest = object: StringRequest(
            Request.Method.POST,
            url1,
            { response ->
                Toast.makeText(requireContext(), "Producto actualizado exitosamente. El id de ingreso es el número ${args.id} ", Toast.LENGTH_LONG).show()
                //   TextNombre?.setText("")
                //   TextDireccion?.setText("")
                //   TextDropdown?.setText("Eliga una opción",false)
            },
            { error ->
                Toast.makeText(requireContext(),"$error", Toast.LENGTH_LONG).show()
                // Toast.makeText(requireContext(),"Solo se ha podido borrar el almacen.", Toast.LENGTH_LONG).show()
            }
        )

        {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("ID_PRODUCTO", sharedViewModel.id.last())
                parametros.put("PRODUCTO", TextNombre?.text.toString().uppercase())
                parametros.put("TIPO_PRODUCTO", DropwDownTipoDeProducto?.text.toString().uppercase())
                parametros.put("PESO_PRODUCTO", TextPeso?.text.toString())
                parametros.put("VOLUMEN_PRODUCTO", TextVolumen?.text.toString())
                parametros.put("CODIGO_BARRA_PRODUCTO", TextCodigoDeBarraProducto?.text.toString())
                parametros.put("CODIGO_BARRA_EMBALAJE", TextCodigoDeBarraEmbalaje?.text.toString().uppercase())
                parametros.put("UNIDADES_EMBALAJE", TextUnidadesEmbalaje?.text.toString())
                return parametros
            }
        }
        queue1.add(stringRequest)
    }
    private fun ListaDesplegableTipoDeProducto() {
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 ="http://186.64.123.248/Reportes/Productos/TipoDeProducto.php"
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
                DropwDownTipoDeProducto?.setAdapter(adapter)

                DropwDownTipoDeProducto?.onItemClickListener = AdapterView.OnItemClickListener {
                        parent, view, position, id ->
                    val itemSelected = parent.getItemAtPosition(position)
                }
            }, { error ->
                // Toast.makeText(requireContext(), " Conecte la aplicación al servidor", Toast.LENGTH_LONG).show()
            }
        )
        queue1.add(jsonObjectRequest1)
    }

    private fun tablaInventario(){
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 = "http://186.64.123.248/Reportes/Productos/tablaAlmacenes.php?ID_PRODUCTO=${sharedViewModel.id.last()}"
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
                        TablaAlmacenes?.addView(registro)
                            registro.findViewById<TableRow>(R.id.trFilaTabla).setOnClickListener {
                                //  binding.clEditarTiposDeProductos.isVisible = false
                                sharedViewModel.inventario.add(listaInventario[i])
                                sharedViewModel.almacenes.add(listaAlmacenes[i])
                                val editarCantidadProductosFragment =
                                    EditarCantidadProductosFragment()
                                parentFragmentManager.beginTransaction()
                                    .replace(
                                        R.id.rlAnadirProductoEditar,
                                        editarCantidadProductosFragment
                                    )
                                    .commit()
                            }
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
                                TablaAlmacenes?.addView(registro)
                                registro.findViewById<TableRow>(R.id.trFilaTabla).setOnClickListener {
                                    //  binding.clEditarTiposDeProductos.isVisible = false
                                    sharedViewModel.inventario.add(0.toString())
                                    sharedViewModel.almacenes.add(listaAlmacenes2[i])
                                    val editarCantidadProductosFragment =
                                        EditarCantidadProductosFragment()
                                    parentFragmentManager.beginTransaction()
                                        .replace(
                                            R.id.rlAnadirProductoEditar,
                                            editarCantidadProductosFragment
                                        )
                                        .commit()
                                }
                            }
                        },{error ->

                        } )
                    queue2.add(jsonObjectRequest2)
                }
                        //Toast.makeText(requireContext(),"${listaAlmacenes[0][0]} y $listaAlmacenes",Toast.LENGTH_LONG).show()

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
                            TablaAlmacenes?.addView(registro)
                            registro.findViewById<TableRow>(R.id.trFilaTabla).setOnClickListener {
                                //  binding.clEditarTiposDeProductos.isVisible = false
                                sharedViewModel.inventario.add(tv1.text.toString())
                                sharedViewModel.almacenes.add(listaAlmacenes3[i])
                                val editarCantidadProductosFragment =
                                    EditarCantidadProductosFragment()
                                parentFragmentManager.beginTransaction()
                                    .replace(
                                        R.id.rlAnadirProductoEditar,
                                        editarCantidadProductosFragment
                                    )
                                    .commit()
                            }
                        }
                    },{error ->

                    } )
                queue3.add(jsonObjectRequest3)
                    }
                )
        queue1.add(jsonObjectRequest1)
            }

    private fun insertarProducto2() {
        //INICIO EXPERIMIENTO!!!!!!!!!!!!!!!!!!!!!!!!!! (FUNCIONO)
        val queue =Volley.newRequestQueue(requireContext())
        val url ="http://186.64.123.248/Producto/registro.php"
        val jsonObjectRequest = object: StringRequest(
            Request.Method.POST,url,
            { response ->
                val drawable = ImagenProducto?.drawable as BitmapDrawable
                val bitmap = (drawable).bitmap
                val fotoBase64 = convertirBitmapABase64(bitmap)
                if(drawable is BitmapDrawable){
                    val id = JSONObject(response).getString("ID_PRODUCTO")
                    //unico = 1
                    //no unico = 0
                    val unico = JSONObject(response).getString("PRODUCTO_UNICO")
                        //Aqui va el código para validar el almacen
                        val url1 = "http://186.64.123.248/Producto/insertarProducto.php" // Reemplaza esto con tu URL de la API
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
                               /* TextPeso?.setText("")
                                TextVolumen?.setText("")
                                TextNombreImagen?.setText("")
                                TextCodigoDeBarraProducto?.setText("")
                                TextCodigoDeBarraEmbalaje?.setText("")
                                TextUnidadesEmbalaje?.setText("")
                                //Imagen?.setImageDrawable(null)
                                Imagen?.setImageResource(R.drawable.android_logo)
                                DropwDownTipoDeProducto?.setText("Eliga una opción",false)*/
                            },
                            { error ->
                                //Toast.makeText(requireContext(), "Producto agregado exitosamente. El id de ingreso es el número $id ", Toast.LENGTH_LONG).show()
                                Toast.makeText(requireContext(),"$error",Toast.LENGTH_LONG).show()
                                Log.i("Sebastian", "$error")
                                //Toast.makeText(requireContext(),"Error $fotoBase64", Toast.LENGTH_LONG).show()


                            }
                        )

                        {
                            override fun getParams(): MutableMap<String, String> {
                                val parametros = HashMap<String, String>()
                                parametros.put("ID_PRODUCTO", id.toString())
                                parametros.put("PRODUCTO", TextNombre?.text.toString().uppercase())
                                parametros.put("TIPO_PRODUCTO", DropwDownTipoDeProducto?.text.toString())
                                parametros.put("ESTADO_PRODUCTO", TextEstado.toString())
                                parametros.put("PESO_PRODUCTO", TextPeso?.text.toString())
                                parametros.put("VOLUMEN_PRODUCTO", TextVolumen?.text.toString())
                                //parametros.put("FOTO_PRODUCTO", TextFoto?.text.toString().uppercase())
                                parametros.put("FOTO_PRODUCTO", fotoBase64)
                                parametros.put("CODIGO_BARRA_PRODUCTO", TextCodigoDeBarraProducto?.text.toString().uppercase())
                                parametros.put("CODIGO_BARRA_EMBALAJE", TextCodigoDeBarraEmbalaje?.text.toString().uppercase())
                                parametros.put("UNIDADES_EMBALAJE", TextUnidadesEmbalaje?.text.toString())
                                return parametros
                            }
                        }
                    queue1.add(stringRequest)
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

    private fun actualizarProducto() {
        val drawable = ImagenProducto?.drawable as BitmapDrawable
        val bitmap = drawable.bitmap
        //val bitmap = (drawable).bitmap
        val fotoBase64 = convertirBitmapABase64(bitmap)
        //unico = 1
        //no unico = 0
        //Aqui va el código para validar el almacen
        val url1 = "http://186.64.123.248/Reportes/Productos/actualizarProducto.php" // Reemplaza esto con tu URL de la API
        val queue1 =Volley.newRequestQueue(requireContext())
        // Convertir la imagen a Base64 y agregarla a los parámetros
        // Ahora, puedes establecer este BitmapDrawable en tu ImageView
        val stringRequest = object: StringRequest(
            Request.Method.POST,
            url1,
            { response ->
                Toast.makeText(requireContext(), "Producto actualizado exitosamente. El id de ingreso es el número ${sharedViewModel.id.last()} ", Toast.LENGTH_SHORT).show()
                //Solo 1 se envía:
                if(sharedViewModel.listaDeAlertas.isNotEmpty() && sharedViewModel.listaDePreciosVenta.isEmpty() && sharedViewModel.listaDePreciosCompra.isEmpty()){
                  //  Handler(Looper.getMainLooper()).postDelayed({
                        borrarAlertas()
                  //  }, 4000)
                    Handler(Looper.getMainLooper()).postDelayed({
                        actualizarAlertas()
                    }, 100)
                    Handler(Looper.getMainLooper()).postDelayed({
                        Toast.makeText(requireContext(), "Alertas actualizadas exitosamente.", Toast.LENGTH_SHORT).show()
                    }, 4000)
                    Log.i("Sebastian4", "${sharedViewModel.listaDeAlertas}, ${sharedViewModel.listaDePreciosCompra} y ${sharedViewModel.listaDePreciosVenta}")
                    Log.i("Sebastian4", "${sharedViewModel.numeroAlertas}, ${sharedViewModel.numeroPreciosCompra} y ${sharedViewModel.numeroPreciosVenta}")
                }
                //Solo 2 se envía:
               else if(sharedViewModel.listaDePreciosCompra.isNotEmpty() && sharedViewModel.listaDeAlertas.isEmpty() && sharedViewModel.listaDePreciosVenta.isEmpty()){
                  //  Handler(Looper.getMainLooper()).postDelayed({
                        borrarPreciosCompra()
                  //  }, 4000)
                    Handler(Looper.getMainLooper()).postDelayed({
                        actualizarPreciosCompra()
                    }, 100)
                    Handler(Looper.getMainLooper()).postDelayed({
                        Toast.makeText(requireContext(), "Precios de compra actualizados exitosamente.", Toast.LENGTH_SHORT).show()
                    }, 4000)
                    Log.i("Sebastian4", "${sharedViewModel.listaDeAlertas}, ${sharedViewModel.listaDePreciosCompra} y ${sharedViewModel.listaDePreciosVenta}")
                    Log.i("Sebastian4", "${sharedViewModel.numeroAlertas}, ${sharedViewModel.numeroPreciosCompra} y ${sharedViewModel.numeroPreciosVenta}")
                }
                //Solo 3 se envía
               else if(sharedViewModel.listaDePreciosVenta.isNotEmpty() &&  sharedViewModel.listaDeAlertas.isEmpty() && sharedViewModel.listaDePreciosCompra.isEmpty()){
                   // Handler(Looper.getMainLooper()).postDelayed({
                        borrarPreciosVenta()
                  //  }, 4000)
                    Handler(Looper.getMainLooper()).postDelayed({
                        actualizarPreciosVenta()
                    }, 100)
                    Handler(Looper.getMainLooper()).postDelayed({
                        Toast.makeText(requireContext(), "Precios de venta actualizados exitosamente.", Toast.LENGTH_SHORT).show()
                    }, 4000)
                    Log.i("Sebastian4", "${sharedViewModel.listaDeAlertas}, ${sharedViewModel.listaDePreciosCompra} y ${sharedViewModel.listaDePreciosVenta}")
                    Log.i("Sebastian4", "${sharedViewModel.numeroAlertas}, ${sharedViewModel.numeroPreciosCompra} y ${sharedViewModel.numeroPreciosVenta}")
                }
                //1 y 2 se envían
               else if(sharedViewModel.listaDePreciosCompra.isNotEmpty() && sharedViewModel.listaDeAlertas.isNotEmpty() && sharedViewModel.listaDePreciosVenta.isEmpty()){
                  //  Handler(Looper.getMainLooper()).postDelayed({
                        borrarAlertas()
                 //   }, 4000)
                    Handler(Looper.getMainLooper()).postDelayed({
                        actualizarAlertas()
                    }, 100)
                    Handler(Looper.getMainLooper()).postDelayed({
                        borrarPreciosCompra()
                    }, 150)
                    Handler(Looper.getMainLooper()).postDelayed({
                        actualizarPreciosCompra()
                    }, 250)
                    Handler(Looper.getMainLooper()).postDelayed({
                        Toast.makeText(requireContext(), "Alertas y precios de compra actualizados exitosamente.", Toast.LENGTH_SHORT).show()
                    }, 4000)
                    Log.i("Sebastian4", "${sharedViewModel.listaDeAlertas}, ${sharedViewModel.listaDePreciosCompra} y ${sharedViewModel.listaDePreciosVenta}")
                    Log.i("Sebastian4", "${sharedViewModel.numeroAlertas}, ${sharedViewModel.numeroPreciosCompra} y ${sharedViewModel.numeroPreciosVenta}")
                }
               // Toast.makeText(requireContext(), "Precios de compra actualizados exitosamente.", Toast.LENGTH_LONG).show()
                //1 y 3 s envían
               else if(sharedViewModel.listaDePreciosCompra.isEmpty() &&  sharedViewModel.listaDeAlertas.isNotEmpty() && sharedViewModel.listaDePreciosVenta.isNotEmpty()){
                   // Handler(Looper.getMainLooper()).postDelayed({
                        borrarAlertas()
                  //  }, 4000)
                    Handler(Looper.getMainLooper()).postDelayed({
                        actualizarAlertas()
                    }, 100)
                    Handler(Looper.getMainLooper()).postDelayed({
                        borrarPreciosVenta()
                    }, 150)
                    Handler(Looper.getMainLooper()).postDelayed({
                        actualizarPreciosVenta()
                    }, 250)
                    Handler(Looper.getMainLooper()).postDelayed({
                        Toast.makeText(requireContext(), "Alertas y precios de venta actualizados exitosamente.", Toast.LENGTH_SHORT).show()
                    }, 4000)
                    Log.i("Sebastian4", "${sharedViewModel.listaDeAlertas}, ${sharedViewModel.listaDePreciosCompra} y ${sharedViewModel.listaDePreciosVenta}")
                    Log.i("Sebastian4", "${sharedViewModel.numeroAlertas}, ${sharedViewModel.numeroPreciosCompra} y ${sharedViewModel.numeroPreciosVenta}")
                }
                //Toast.makeText(requireContext(), "Precios de venta actualizados exitosamente.", Toast.LENGTH_LONG).show()
                //2 y 3 se envían
               else if(sharedViewModel.listaDePreciosVenta.isNotEmpty() && sharedViewModel.listaDePreciosCompra.isNotEmpty() && sharedViewModel.listaDeAlertas.isEmpty() ){
                  //  Handler(Looper.getMainLooper()).postDelayed({
                        borrarPreciosCompra()
                  //  }, 4000)
                    Handler(Looper.getMainLooper()).postDelayed({
                        actualizarPreciosCompra()
                    }, 100)
                    Handler(Looper.getMainLooper()).postDelayed({
                        borrarPreciosVenta()
                    }, 150)
                    Handler(Looper.getMainLooper()).postDelayed({
                        actualizarPreciosVenta()
                    }, 250)
                    Handler(Looper.getMainLooper()).postDelayed({
                        Toast.makeText(requireContext(), "Precios de compra y de venta actualizados exitosamente.", Toast.LENGTH_SHORT).show()
                    }, 4000)
                    Log.i("Sebastian4", "${sharedViewModel.listaDeAlertas}, ${sharedViewModel.listaDePreciosCompra} y ${sharedViewModel.listaDePreciosVenta}")
                    Log.i("Sebastian4", "${sharedViewModel.numeroAlertas}, ${sharedViewModel.numeroPreciosCompra} y ${sharedViewModel.numeroPreciosVenta}")
                }
               //Toast.makeText(requireContext(), "Precios de venta actualizados exitosamente.", Toast.LENGTH_LONG).show()
                //1, 2 y 3 se envían
               else if(sharedViewModel.listaDePreciosVenta.isNotEmpty() && sharedViewModel.listaDePreciosCompra.isNotEmpty() && sharedViewModel.listaDeAlertas.isNotEmpty()){
                    Handler(Looper.getMainLooper()).postDelayed({
                        Toast.makeText(requireContext(), "Alertas, precios de compra y de venta actualizados exitosamente.", Toast.LENGTH_SHORT).show()
                    }, 4000)
                    Log.i("Sebastian4", "${sharedViewModel.listaDeAlertas}, ${sharedViewModel.listaDePreciosCompra} y ${sharedViewModel.listaDePreciosVenta}")
                    Log.i("Sebastian4", "${sharedViewModel.numeroAlertas}, ${sharedViewModel.numeroPreciosCompra} y ${sharedViewModel.numeroPreciosVenta}")
                 //   Handler(Looper.getMainLooper()).postDelayed({
                        borrarAlertas()
                //    }, 4000)
                    Handler(Looper.getMainLooper()).postDelayed({
                        actualizarAlertas()
                    }, 100)
                    Handler(Looper.getMainLooper()).postDelayed({
                        borrarPreciosCompra()
                    }, 150)
                    Handler(Looper.getMainLooper()).postDelayed({
                        actualizarPreciosCompra()
                    }, 250)
                    Handler(Looper.getMainLooper()).postDelayed({
                        borrarPreciosVenta()
                    }, 300)
                    Handler(Looper.getMainLooper()).postDelayed({
                        actualizarPreciosVenta()
                    }, 400)
                }

              //  Toast.makeText(requireContext(), "Precios de compra actualizados exitosamente.", Toast.LENGTH_LONG).show()
              //  Toast.makeText(requireContext(), "Precios de venta actualizados exitosamente.", Toast.LENGTH_LONG).show()
                /* TextPeso?.setText("")
                TextVolumen?.setText("")
                TextNombreImagen?.setText("")
                TextCodigoDeBarraProducto?.setText("")
                TextCodigoDeBarraEmbalaje?.setText("")
                TextUnidadesEmbalaje?.setText("")
                //Imagen?.setImageDrawable(null)
                Imagen?.setImageResource(R.drawable.android_logo)
                DropwDownTipoDeProducto?.setText("Eliga una opción",false)*/
            },
            { error ->
            //Toast.makeText(requireContext(), "Producto agregado exitosamente. El id de ingreso es el número $id ", Toast.LENGTH_LONG).show()
            Toast.makeText(requireContext(),"Producto agregado exitosamente. El id de ingreso es el número ${sharedViewModel.id.last()}",Toast.LENGTH_LONG).show()
            Log.i("Sebastian", "$error")
            //Toast.makeText(requireContext(),"Error $fotoBase64", Toast.LENGTH_LONG).show()


            }
        )

        {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("ID_PRODUCTO", sharedViewModel.id.last())
                parametros.put("PRODUCTO", TextNombre?.text.toString().uppercase())
                parametros.put("TIPO_PRODUCTO", DropwDownTipoDeProducto?.text.toString())
                parametros.put("ESTADO_PRODUCTO", TextEstado.toString())
                parametros.put("PESO_PRODUCTO", TextPeso?.text.toString())
                parametros.put("VOLUMEN_PRODUCTO", TextVolumen?.text.toString())
                parametros.put("FOTO_PRODUCTO", fotoBase64)
                parametros.put("CODIGO_BARRA_PRODUCTO", TextCodigoDeBarraProducto?.text.toString().uppercase())
                parametros.put("CODIGO_BARRA_EMBALAJE", TextCodigoDeBarraEmbalaje?.text.toString().uppercase())
                parametros.put("UNIDADES_EMBALAJE", TextUnidadesEmbalaje?.text.toString())
                return parametros
            }
        }
        queue1.add(stringRequest)

                // TextId?.setText(response.getString("ID_ALMACEN"))
                 // Toast.makeText(requireContext(),"Id ingresado correctamente al formulario.", Toast.LENGTH_LONG).show()
    }

    private fun borrarAlertas(){
        val url = "http://186.64.123.248/Reportes/Productos/borrarAlertas.php"
        val queue = Volley.newRequestQueue(requireContext())
        val jsonObjectRequest = object : StringRequest(Request.Method.POST, url,
            { response ->
                //  Toast.makeText(requireContext(), "Exito", Toast.LENGTH_LONG).show()

            },{error ->
                //Toast.makeText(requireContext(), "$error", Toast.LENGTH_LONG).show()
            })
        {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("ID_PRODUCTO", sharedViewModel.id.last())
                parametros.put("NUMERO_DE_ALERTAS",sharedViewModel.numeroAlertas.size.toString())
                for(i in 0..<sharedViewModel.numeroAlertas.size){
                    parametros.put("ALMACEN$i",sharedViewModel.numeroAlertas[i])
                }
                return parametros
            }
        }
        queue.add(jsonObjectRequest)
    }

    private fun actualizarAlertas() {
        val url1 = "http://186.64.123.248/Reportes/Productos/insertarAlertas.php"
        val queue1 =Volley.newRequestQueue(requireContext())
        val stringRequest = object: StringRequest(
            Request.Method.POST,
            url1,
            { response ->

                //   TextNombre?.setText("")
                //   TextDireccion?.setText("")
                //   TextDropdown?.setText("Eliga una opción",false)
            },
            { error ->
                Log.i("Sebastian3", "${sharedViewModel.numeroAlertas}, ${sharedViewModel.listaDeAlertas}, ${sharedViewModel.listaDeBodegas}")
               // Toast.makeText(requireContext(), "Alertas actualizadas exitosamente.", Toast.LENGTH_LONG).show()

                // Toast.makeText(requireContext(),"Solo se ha podido borrar el almacen.", Toast.LENGTH_LONG).show()
            }
        )

        {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("ID_PRODUCTO", sharedViewModel.id.last())
                parametros.put("NUMERO_DE_ALERTAS",sharedViewModel.numeroAlertas.size.toString())
                for(i in 0..<sharedViewModel.numeroAlertas.size){
                    parametros.put("ALMACEN$i", sharedViewModel.numeroAlertas[i])
                    parametros.put("ALERTA$i", sharedViewModel.listaDeAlertas[i])

                }

                return parametros
            }
        }
        queue1.add(stringRequest)
    }

    private fun borrarPreciosCompra(){
        val url = "http://186.64.123.248/Reportes/Productos/borrarPreciosCompra.php"
        val queue = Volley.newRequestQueue(requireContext())
        val jsonObjectRequest = object : StringRequest(Request.Method.POST, url,
            { response ->
                //  Toast.makeText(requireContext(), "Exito", Toast.LENGTH_LONG).show()

            },{error ->
                //Toast.makeText(requireContext(), "$error", Toast.LENGTH_LONG).show()
            })
        {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("ID_PRODUCTO", sharedViewModel.id.last())
                parametros.put("NUMERO_DE_PRECIOS_COMPRA",sharedViewModel.numeroPreciosCompra.size.toString())
                for(i in 0..<sharedViewModel.numeroPreciosCompra.size){
                    parametros.put("PROVEEDOR$i",sharedViewModel.numeroPreciosCompra[i])}
                return parametros
            }
        }
        queue.add(jsonObjectRequest)
    }

    private fun actualizarPreciosCompra() {
        val url1 = "http://186.64.123.248/Reportes/Productos/insertarPreciosCompra.php"
        val queue1 =Volley.newRequestQueue(requireContext())
        val stringRequest = object: StringRequest(
            Request.Method.POST,
            url1,
            { response ->

                //   TextNombre?.setText("")
                //   TextDireccion?.setText("")
                //   TextDropdown?.setText("Eliga una opción",false)
            },
            { error ->
                Log.i("Sebastian3", "${sharedViewModel.numeroPreciosCompra}, ${sharedViewModel.listaDePreciosCompra}, ${sharedViewModel.listaDeProveedores}")
               // Toast.makeText(requireContext(), "Precios de compra actualizados exitosamente.", Toast.LENGTH_LONG).show()
                // Toast.makeText(requireContext(),"Solo se ha podido borrar el almacen.", Toast.LENGTH_LONG).show()
            }
        )

        {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("ID_PRODUCTO", sharedViewModel.id.last())
                parametros.put("NUMERO_DE_PRECIOS_COMPRA",sharedViewModel.numeroPreciosCompra.size.toString())
                for(i in 0..<sharedViewModel.numeroPreciosCompra.size){
                    parametros.put("PROVEEDOR$i",sharedViewModel.numeroPreciosCompra[i])
                    parametros.put("PRECIO_COMPRA$i", sharedViewModel.listaDePreciosCompra[i])


                }
                return parametros
            }
        }
        queue1.add(stringRequest)
    }

    private fun borrarPreciosVenta(){
        val url = "http://186.64.123.248/Reportes/Productos/borrarPreciosVenta.php"
        val queue = Volley.newRequestQueue(requireContext())
        val jsonObjectRequest = object : StringRequest(Request.Method.POST, url,
            { response ->
                //  Toast.makeText(requireContext(), "Exito", Toast.LENGTH_LONG).show()

            },{error ->
                //Toast.makeText(requireContext(), "$error", Toast.LENGTH_LONG).show()
            })
        {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("ID_PRODUCTO", sharedViewModel.id.last())
                parametros.put("NUMERO_DE_PRECIOS_VENTA",sharedViewModel.numeroPreciosVenta.size.toString())
                for(i in 0..<sharedViewModel.numeroPreciosVenta.size){
                    parametros.put("CLIENTE$i",sharedViewModel.numeroPreciosVenta[i])
                }
                return parametros
            }
        }
        queue.add(jsonObjectRequest)
    }

    private fun actualizarPreciosVenta() {
        val url1 = "http://186.64.123.248/Reportes/Productos/insertarPreciosVenta.php"
        val queue1 =Volley.newRequestQueue(requireContext())
        val stringRequest = object: StringRequest(
            Request.Method.POST,
            url1,
            { response ->
                /* TextNombre?.setText("")
                 TextPeso?.setText("")
                 TextVolumen?.setText("")
                 TextNombreImagen?.setText("")
                 TextCodigoDeBarraProducto?.setText("")
                 TextCodigoDeBarraEmbalaje?.setText("")
                 TextUnidadesEmbalaje?.setText("")
                 //Imagen?.setImageDrawable(null)
                 ImagenProducto?.setImageResource(R.drawable.android_logo)
                 DropwDownTipoDeProducto?.setText("Eliga una opción",false)*/
            },
            { error ->
                Log.i("Sebastian3", "${sharedViewModel.numeroPreciosVenta}, ${sharedViewModel.listaDePreciosVenta}, ${sharedViewModel.listaDeClientes}")
               // Toast.makeText(requireContext(), "Precios de venta actualizados exitosamente.", Toast.LENGTH_LONG).show()
                // Toast.makeText(requireContext(),"Solo se ha podido borrar el almacen.", Toast.LENGTH_LONG).show()
            }
        )

        {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("ID_PRODUCTO", sharedViewModel.id.last())
                parametros.put("NUMERO_DE_PRECIOS_VENTA",sharedViewModel.numeroPreciosVenta.size.toString())
                for (i in 0..<sharedViewModel.numeroPreciosVenta.size) {
                    parametros.put("CLIENTE$i", sharedViewModel.numeroPreciosVenta[i])
                    parametros.put("PRECIO_VENTA$i", sharedViewModel.listaDePreciosVenta[i])
                }
                return parametros
            }
        }
        queue1.add(stringRequest)
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
    /*private fun ModificarAlertasyAlmacenes(){
        for (i in 0..<sharedViewModel.listaDeAlertas.size) {
            setFragmentResultListener("AlertaAlmacen$i"){key,bundle ->
                sharedViewModel.ListasDeAlmacenes.add(bundle.getString("Almacen$i")!!.toString().uppercase())
                sharedViewModel.ListasDeAlertas.add(bundle.getString("Alerta$i")!!)
                sharedViewModel.ListasDeProductosAlertas.add(binding.etNombreProducto.text.toString().uppercase())
            }}
        val queueAlertas =Volley.newRequestQueue(requireContext())
        val urlAlertas = "http://186.64.123.248/Producto/alertasAlmacenEnviar.php"
        val jsonObjectRequestAlertas =object: StringRequest(
            Request.Method.POST,
            urlAlertas,{
                    response ->
                sharedViewModel.listaDeAlertas.clear()
                sharedViewModel.listaDeBodegas.clear()
                sharedViewModel.ListasDeAlertas.clear()
                sharedViewModel.ListasDeAlmacenes.clear()
                sharedViewModel.ListasDeProductosAlertas.clear()
                if(sharedViewModel.listaDeAlertas.isNotEmpty() && sharedViewModel.listaDePreciosVenta.isEmpty() && sharedViewModel.listaDePreciosCompra.isEmpty()){
                    TextNombre?.setText("")  }
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
                parametrosAlertas["ALMACEN$i"] = sharedViewModel.ListasDeAlmacenes[i]
                parametrosAlertas["PRODUCTO$i"] = sharedViewModel.ListasDeProductosAlertas[i]
                parametrosAlertas["ALERTA$i"] = sharedViewModel.ListasDeAlertas[i]
            }

            return parametrosAlertas
        }
        }
        queueAlertas.add(jsonObjectRequestAlertas)
    }

    private fun ModificarPreciosCompraYProveedores(){
        for (i in 0..<sharedViewModel.listaDePreciosCompra.size) {
            setFragmentResultListener("PreciosCompraProveedores$i"){key,bundle ->
                sharedViewModel.ListasDeProveedores.add(bundle.getString("Proveedores$i")!!.toString().uppercase())
                sharedViewModel.ListasDePreciosDeCompra.add(bundle.getString("PreciosCompra$i")!!)
                sharedViewModel.ListasDeProductosPrecioCompra.add(binding.etNombreProducto.text.toString().uppercase())
            }}
        val queuePrecioCompra =Volley.newRequestQueue(requireContext())
        val urlPrecioCompra = "http://186.64.123.248/Producto/proveedorPrecioCompraEnviar.php"
        val jsonObjectRequestPrecioCompra = object: StringRequest(
            Request.Method.POST,
            urlPrecioCompra,{
                    response ->
                sharedViewModel.listaDePreciosCompra.clear()
                sharedViewModel.listaDeProveedores.clear()
                sharedViewModel.ListasDePreciosDeCompra.clear()
                sharedViewModel.ListasDeProveedores.clear()
                sharedViewModel.ListasDeProductosPrecioCompra.clear()
                if(sharedViewModel.listaDePreciosCompra.isNotEmpty() && sharedViewModel.listaDeAlertas.isEmpty() && sharedViewModel.listaDePreciosVenta.isEmpty()
                    || (sharedViewModel.listaDePreciosCompra.isNotEmpty() && sharedViewModel.listaDeAlertas.isNotEmpty() && sharedViewModel.listaDePreciosVenta.isEmpty())){
                    TextNombre?.setText("")
                }
                Toast.makeText(requireContext(), "Precios de compra ingresados exitosamente a la base de datos", Toast.LENGTH_LONG).show()
            },
            { error ->
                Toast.makeText(requireContext(), " No se ha podido enviar los proveedores y precios de compra", Toast.LENGTH_LONG).show()
                /*  for (i in 0..<sharedViewModel.listaDeAlertas.size) {
                      Log.i("ALMACEN$i",ListaDeAlmacenes[i])
                       Log.i("PRODUCTO$i", ListaDeProductos[i])
                      Log.i("ALERTA$i",ListaDeAlertas[i])
                  }*/
            })
        { override fun getParams(): MutableMap<String, String> {
            val parametrosPrecioCompra = HashMap<String, String>()
            parametrosPrecioCompra["NUMERO_DE_PRECIOS_DE_COMPRA"] = sharedViewModel.listaDePreciosCompra.size.toString()
            for (i in 0..<sharedViewModel.listaDePreciosCompra.size) {
                parametrosPrecioCompra["PROVEEDOR$i"] = sharedViewModel.ListasDeProveedores[i]
                parametrosPrecioCompra["PRODUCTO$i"] = sharedViewModel.ListasDeProductosPrecioCompra[i]
                parametrosPrecioCompra["PRECIO_COMPRA$i"] = sharedViewModel.ListasDePreciosDeCompra[i]
            }

            return parametrosPrecioCompra
        }
        }
        queuePrecioCompra.add(jsonObjectRequestPrecioCompra)
    }

    private fun ModificarPreciosVentayClientes(){
        for (i in 0..<sharedViewModel.listaDePreciosVenta.size) {
            setFragmentResultListener("PreciosVentaClientes$i"){key,bundle ->
                sharedViewModel.ListasDeClientes.add(bundle.getString("Clientes$i")!!.toString().uppercase())
                sharedViewModel.ListasDePreciosDeVenta.add(bundle.getString("PreciosDeVenta$i")!!)
                sharedViewModel.ListasDeProductosPrecioVenta.add(binding.etNombreProducto.text.toString().uppercase())
            }}
        val queuePrecioVenta =Volley.newRequestQueue(requireContext())
        val urlPrecioVenta = "http://186.64.123.248/Producto/clientePrecioVentaEnviar.php"
        val jsonObjectRequestPrecioVenta =object: StringRequest(
            Request.Method.POST,
            urlPrecioVenta,{
                    response ->
                sharedViewModel.listaDePreciosVenta.clear()
                sharedViewModel.listaDeClientes.clear()
                sharedViewModel.ListasDePreciosDeVenta.clear()
                sharedViewModel.ListasDeClientes.clear()
                sharedViewModel.ListasDeProductosPrecioVenta.clear()
                TextNombre?.setText("")
                Toast.makeText(requireContext(), "Precios de venta ingresados exitosamente a la base de datos", Toast.LENGTH_LONG).show()
            },
            { error ->
                Toast.makeText(requireContext(), " No se ha podido enviar los clientes y precios de venta", Toast.LENGTH_LONG).show()
                /*  for (i in 0..<sharedViewModel.listaDeAlertas.size) {
                      Log.i("ALMACEN$i",ListaDeAlmacenes[i])
                       Log.i("PRODUCTO$i", ListaDeProductos[i])
                      Log.i("ALERTA$i",ListaDeAlertas[i])
                  }*/
            })
        { override fun getParams(): MutableMap<String, String> {
            val parametrosPrecioVenta = HashMap<String, String>()
            parametrosPrecioVenta["NUMERO_DE_PRECIOS_DE_VENTA"] = sharedViewModel.listaDePreciosVenta.size.toString()
            for (i in 0..<sharedViewModel.listaDePreciosVenta.size) {
                parametrosPrecioVenta["CLIENTE$i"] = sharedViewModel.ListasDeClientes[i]
                parametrosPrecioVenta["PRODUCTO$i"] = sharedViewModel.ListasDeProductosPrecioVenta[i]
                parametrosPrecioVenta["PRECIO_VENTA$i"] = sharedViewModel.ListasDePreciosDeVenta[i]
            }

            return parametrosPrecioVenta
        }
        }
        queuePrecioVenta.add(jsonObjectRequestPrecioVenta)
    }*/

    private fun getRetrofit(): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl("http://186.64.123.248/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
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
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    },
                    { error ->
                        Toast.makeText(
                            requireContext(),
                            "No hay producto o embalaje asociado a este producto $error",
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
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    },
                    { error ->
                        Toast.makeText(
                            requireContext(),
                            "No hay producto o embalaje asociado a este producto $error",
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

    fun convertirBitmapABase64(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when(checkedId){
            radio1?.id -> TextEstado = 1
            radio2?.id -> TextEstado = 0
        }
    }
    private fun isValidUrl(url: String): Boolean{
        return try{
            URL(url)
            true
        }catch(e: MalformedURLException){
            false
        }
    }

    private fun mensajeEliminarProducto(){
        AlertDialog.Builder(context).apply {
            setTitle("¿Quieres eliminar este producto?")
            setMessage("Esta acción no se puede deshacer.")
            setPositiveButton("Sí") { dialog, _ ->
                eliminarProducto()
                findNavController().navigate(R.id.action_nav_editar_producto_to_nav_mis_datos)
                dialog.dismiss()
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }

    }
    private fun eliminarProducto(){
        val url = "http://186.64.123.248/Reportes/Productos/borrar.php"
        val queue = Volley.newRequestQueue(requireContext())
        var jsonObjectRequest = object : StringRequest(Request.Method.POST, url,
            { response ->
                Toast.makeText(requireContext(), "Producto eliminado exitosamente", Toast.LENGTH_SHORT).show()

            },{error ->
                Toast.makeText(requireContext(), "No se pudo eliminar el producto", Toast.LENGTH_SHORT).show()
            })
        {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("ID_PRODUCTO", sharedViewModel.id.last())
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




