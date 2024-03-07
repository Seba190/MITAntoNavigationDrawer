package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirTransferencia

import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
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
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentElegirProductoBinding
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import org.json.JSONObject
import java.lang.Exception
import android.text.TextUtils

class ElegirProductoFragment : Fragment(R.layout.fragment_elegir_producto) {

    interface RefreshInterface{
        fun refreshAdapterAnadirTransferenciaFragment()
    }

    private var _binding: FragmentElegirProductoBinding? = null
    private lateinit var anadirTransferenciaUpdater: AnadirTransferenciaUpdater
    private var requestCamara: ActivityResultLauncher<String>? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    var itemSelectedProducto : MutableList<String> = mutableListOf()
    var itemSelectedProveedor : MutableList<String> = mutableListOf()
    var DropDownProducto: AutoCompleteTextView? = null
    var TextCodigoDeBarra: EditText? = null
    var Transferencia: String? = null
    private val sharedViewModel by activityViewModels<SharedViewModel>()
   // var DropDownProveedor: AutoCompleteTextView? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val elegirProductoViewModel =
            ViewModelProvider(this).get(ElegirProductoViewModel::class.java)
        _binding = FragmentElegirProductoBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa
        DropDownProducto = binding.tvListaDesplegableElegirProducto.findViewById(R.id.tvListaDesplegableElegirProducto)
        TextCodigoDeBarra = binding.etCodigoDeBarra.findViewById(R.id.etCodigoDeBarra)
        // Poner los edittext con borde gris
        binding.etCantidad.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etArticulosPorCaja.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etNumeroDeCajas.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
       binding.etCodigoDeBarra.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
           PorterDuff.Mode.SRC_ATOP)


        ListaDesplegableElegirProducto()
       // ListaDesplegableElegirProveedor()

        binding.llCajasDeProductoElegirProducto.isVisible = false
        binding.bUnidades.setOnClickListener {
            binding.llUnidadesElegirProducto.isVisible = false
            binding.llCajasDeProductoElegirProducto.isVisible = true

        }
        binding.bCajasDeProducto.setOnClickListener {
            binding.llCajasDeProductoElegirProducto.isVisible = false
            binding.llUnidadesElegirProducto.isVisible = true
        }

        binding.rlElegirProducto.setOnClickListener {
            binding.rlElegirProducto.isVisible = false
            val anadirTransferenciaFragment = AnadirTransferenciaFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.rlElegirProducto,anadirTransferenciaFragment)
                .commit()

        }


            binding.bmas.setOnClickListener {
                val cantidadActual = binding.etCantidad.text.toString().toIntOrNull() ?: 0
                val cantidadNueva = cantidadActual + 1
                binding.etCantidad.setText(cantidadNueva.toString())
            }
            binding.bmenos.setOnClickListener {
                val cantidadActual = binding.etCantidad.text.toString().toIntOrNull() ?: 0
                val cantidadNueva = if (cantidadActual > 0) cantidadActual - 1 else 0
                binding.etCantidad.setText(cantidadNueva.toString())
            }

        binding.bAnadirFactura.setOnClickListener {
            val queue = Volley.newRequestQueue(requireContext())
            val url = "http://186.64.123.248/Transferencia/preguntarInventario.php"
            val jsonObjectRequest = object : StringRequest(
                Request.Method.POST, url,
                { response ->
                    try {
                        if (binding.tvListaDesplegableElegirProducto.text.toString() != "Eliga una opción") {
                            val cantidadUnidades = JSONObject(response).getString("Cantidad")
                                if (binding.llUnidadesElegirProducto.isVisible) {
                                    if (binding.etCantidad.text.isNotBlank()) {
                                        if (cantidadUnidades.toInt() < binding.etCantidad.text.toString().toInt()) {
                                            val inflater = requireActivity().layoutInflater
                                            val layout = inflater.inflate(R.layout.toast_custom, null)
                                            val text = layout.findViewById<TextView>(R.id.text_view_toast)
                                            text.text = "La cantidad es mayor que la cantidad en inventario"
                                            val toast = Toast(requireContext())
                                            toast.duration = Toast.LENGTH_LONG
                                            toast.view = layout
                                            toast.setGravity(Gravity.BOTTOM, 0, 600)
                                            toast.show()
                                        } else {
                                            sharedViewModel.listaDeProductos.add(binding.tvListaDesplegableElegirProducto.text.toString())
                                            sharedViewModel.listaDeCantidades.add(binding.etCantidad.text.toString())
                                            someFunctionToUpdateAnadirTransferenciaFragment()
                                            sendDataToAnadirTransferenciaFragment(
                                            sharedViewModel.listaDeCantidades,
                                            sharedViewModel.listaDeProductos
                                        )
                                        //  refreshAdapterAnadirTransferenciaFragment()
                                        binding.tvListaDesplegableElegirProducto.setText(
                                            "Eliga una opción",
                                            false
                                        )
                                        binding.etCodigoDeBarra.setText("")
                                        binding.etCantidad.setText("")
                                        Toast.makeText(requireContext(), "Se ha agregado el producto a la factura", Toast.LENGTH_LONG).show()
                                        }
                                    } else {
                                        Toast.makeText(
                                            requireContext(),
                                            "Falta ingresar la cantidad",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            else if (binding.llCajasDeProductoElegirProducto.isVisible) {
                                if (binding.etNumeroDeCajas.text.isNotBlank() && binding.etArticulosPorCaja.text.isNotBlank()) {
                                    if (cantidadUnidades.toInt() < binding.etNumeroDeCajas.text.toString().toInt()*binding.etArticulosPorCaja.text.toString().toInt()) {
                                        val inflater = requireActivity().layoutInflater
                                        val layout = inflater.inflate(R.layout.toast_custom, null)
                                        val text = layout.findViewById<TextView>(R.id.text_view_toast)
                                        text.text = "La cantidad es mayor que la cantidad en inventario"
                                        val toast = Toast(requireContext())
                                        toast.duration = Toast.LENGTH_LONG
                                        toast.view = layout
                                        toast.setGravity(Gravity.BOTTOM, 0, 600)
                                        toast.show()
                                    } else {
                                        val cantidad =
                                            binding.etNumeroDeCajas.text.toString().toInt()
                                                .times(
                                                    binding.etArticulosPorCaja.text.toString()
                                                        .toInt()
                                                )
                                        sharedViewModel.listaDeProductos.add(binding.tvListaDesplegableElegirProducto.text.toString())
                                        sharedViewModel.listaDeCantidades.add(cantidad.toString())
                                        someFunctionToUpdateAnadirTransferenciaFragment()
                                        sendDataToAnadirTransferenciaFragment(
                                            sharedViewModel.listaDeCantidades,
                                            sharedViewModel.listaDeProductos
                                        )
                                        // refreshAdapterAnadirTransferenciaFragment()
                                        binding.tvListaDesplegableElegirProducto.setText(
                                            "Eliga una opción",
                                            false
                                        )
                                        binding.etArticulosPorCaja.setText("")
                                        binding.etNumeroDeCajas.setText("")
                                        binding.etCodigoDeBarra.setText("")
                                        Toast.makeText(
                                            requireContext(),
                                            "Se ha agregado el producto a la factura",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "Falta ingresar al menos los articulos por caja o el número de cajas",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(requireContext(), "No se ha podido ingresar la factura", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(requireContext(), "No olvide elegir el producto", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "La excepcion es $e", Toast.LENGTH_LONG)
                            .show()
                        Log.i("Sebastián", "$e")
                    }
                },
                { error -> Toast.makeText(requireContext(), "El error es $error", Toast.LENGTH_LONG).show()
                    Log.i("Sebastián", "$error")
                }) {
                override fun getParams(): MutableMap<String, String> {
                    val parametros = HashMap<String, String>()
                    parametros.put("ALMACEN_ORIGEN", sharedViewModel.almacen)
                    parametros.put("PRODUCTO", DropDownProducto?.text.toString())
                    return parametros
                }
            }
            queue.add(jsonObjectRequest)
        }

       binding.bVolver.setOnClickListener {
           binding.rlElegirProducto.isVisible = false
           val anadirTransferenciaFragment = AnadirTransferenciaFragment()
           parentFragmentManager.beginTransaction()
               .replace(R.id.rlElegirProducto,anadirTransferenciaFragment)
               .commit()
       }
      /* requestCamara = registerForActivityResult(ActivityResultContracts.RequestPermission(),){
           if(it){
               findNavController().navigate(R.id.action_nav_añadir_transferencia_to_nav_barcode_scan)
           }else{
               Toast.makeText(requireContext(),"Permiso denegado",Toast.LENGTH_LONG).show()
           }
       }
       binding.bEscanearCodigoDeBarra.setOnClickListener {
           requestCamara?.launch(android.Manifest.permission.CAMERA)
       }*/


           binding.etCantidad.addTextChangedListener {
               preguntarInventario()
           }


           binding.etNumeroDeCajas.addTextChangedListener{
               preguntarInventario()
           }
           binding.etArticulosPorCaja.addTextChangedListener {
               preguntarInventario()
           }


        return root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        anadirTransferenciaUpdater = context as AnadirTransferenciaUpdater
    }

    private fun sendDataToAnadirTransferenciaFragment(dataCantidad: MutableList<String>, dataProducto: MutableList<String>){
        anadirTransferenciaUpdater.updateRecyclerView(dataCantidad,dataProducto)
    }
    private fun someFunctionToUpdateAnadirTransferenciaFragment(){
        val newDataCantidad = sharedViewModel.listaDeCantidades
        val newDataProducto = sharedViewModel.listaDeProductos
        sendDataToAnadirTransferenciaFragment(newDataCantidad,newDataProducto)
    }

   /* fun refreshAdapterAnadirTransferenciaFragment(){
        val fragmentAnadirTransferencia = requireActivity().supportFragmentManager.findFragmentById(R.id.clAnadirTransferencia) as AnadirTransferenciaFragment?
        fragmentAnadirTransferencia?.refreshAdapter()!!
    }*/

    private fun setCantidad() {
      //  binding.etCantidad.text.toString().toIntOrNull() = cantidad.toString().toIntOrNull()
    }

    val opcionesList = mutableListOf<String>()
    private fun ListaDesplegableElegirProducto() {
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 ="http://186.64.123.248/Transferencia/elegirProducto.php"
        val jsonObjectRequest1 = JsonObjectRequest(
            Request.Method.GET,url1, null,
            { response ->
                // Obtén el array de opciones desde el objeto JSON
                val jsonArray = response.getJSONArray("Lista")
                // Convierte el array JSON a una lista mutable
                for (i in 0 until jsonArray.length()) {
                    opcionesList.add(jsonArray.getString(i).replace("'",""))
                }
                //Crea un adpatador para el dropdown
                val adapter = ArrayAdapter(requireContext(),R.layout.list_item,opcionesList)
                //binding.tvholaMundo?.setText(response.getString("Lista"))
                DropDownProducto?.setAdapter(adapter)
                DropDownProducto?.onItemClickListener = AdapterView.OnItemClickListener {
                        parent, view, position, id ->
                    if(binding.llCajasDeProductoElegirProducto.isVisible){
                        precioYCantidad(parent.getItemAtPosition(position).toString())
                    }
                }
                binding.etCodigoDeBarra.setOnClickListener {
                    codigoDeBarra()
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (DropDownProducto?.text.toString() != "Eliga una opción") {
                            precioYCantidad(DropDownProducto?.text.toString())
                        }
                    }, 300)
                }
            }, { error ->
                Toast.makeText(requireContext(), " La aplicación no se ha conectado con el servidor", Toast.LENGTH_LONG).show()
            }
        )
        queue1.add(jsonObjectRequest1)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.CodigoDeBarraTransferencia.observe(viewLifecycleOwner) { newText ->
            binding.etCodigoDeBarra.setText(newText)
            codigoDeBarra()
            Handler(Looper.getMainLooper()).postDelayed({
                if (DropDownProducto?.text.toString() != "Eliga una opción") {
                    precioYCantidad(DropDownProducto?.text.toString())
                }
            }, 300)
        }

        }
      // codigoDeBarra()


   /* private fun ListaDesplegableElegirProveedor() {
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 ="http://186.64.123.248/Transferencia/elegirProveedor.php"
        val jsonObjectRequest1 = JsonObjectRequest(
            Request.Method.GET,url1, null,
            { response ->
                // Obtén el array de opciones desde el objeto JSON
                val jsonArray = response.getJSONArray("Lista")
                // Convierte el array JSON a una lista mutable
                val opcionesList = mutableListOf<String>()
                for (i in 0 until jsonArray.length()) {
                    opcionesList.add(jsonArray.getString(i).replace("'",""))
                }
                //Crea un adpatador para el dropdown
                val adapter = ArrayAdapter(requireContext(),R.layout.list_item,opcionesList)
                //binding.tvholaMundo?.setText(response.getString("Lista"))
                DropDownProveedor?.setAdapter(adapter)

                DropDownProveedor?.onItemClickListener = AdapterView.OnItemClickListener {
                        parent, view, position, id ->
                    itemSelectedProveedor.add(parent.getItemAtPosition(position).toString())
                }
                DropDownProveedor?.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                       // InsertarPrecioYCantidad()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        TODO("Not yet implemented")
                    }

                }
            }, { error ->
                //Toast.makeText(requireContext(), " La aplicación no se ha conectado con el servidor", Toast.LENGTH_LONG).show()
            }
        )
        queue1.add(jsonObjectRequest1)
    }*/

    /*override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bAnadirParametros.setOnClickListener {
            precioYCantidad(DropDownProducto?.text.toString())
        }
    }*/

    fun precioYCantidad(producto: String){
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 = "http://186.64.123.248/Transferencia/precioYCantidad.php"
        val jsonObjectRequest1 = object : StringRequest(
            Request.Method.POST, url1,
            { response ->
                val precio = JSONObject(response).getString("Precio")
                val unidades = JSONObject(response).getString("Unidades de embalaje")
                // binding.etPrecioPorUnidadUnidades.setText("200")
                //binding.etCantidad.setText("12")
                if (binding.llCajasDeProductoElegirProducto.isVisible) {
                    binding.etArticulosPorCaja.setText(unidades)
                    //binding.etPrecioPorUnidadCajasDeProductos.setText("200")
                }
                //binding.tvListaDesplegableElegirProveedor.setText("Eliga una opción",false)
                // binding.tvListaDesplegableElegirProducto.setText("Eliga una opción",false)

            }, { error ->
                /*if(binding.tvListaDesplegableElegirProveedor.text.toString() != "Eliga una opción" &&
            binding.tvListaDesplegableElegirProducto.text.toString() != "Eliga una opción") {*/
                Toast.makeText(requireContext(), "No se ha podido cargar los parametros $error", Toast.LENGTH_LONG).show()
                // binding.etCantidad.setText(unidadesEmbalaje)
                Log.i("Runtime","$error")
            }

            // }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
               // parametros.put("PRODUCTO", DropDownProducto?.text.toString())
                parametros.put("PRODUCTO", producto)
                return parametros

            }
        }
        queue1.add(jsonObjectRequest1)
    }

    private fun codigoDeBarra(){
            if (binding.etCodigoDeBarra.text.isNotBlank()) {
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
                                    "EL código de barra pertenece al producto $codigoBarraProducto y ${opcionesList.indexOf("$codigoBarraProducto ( 0 unid. )")}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                binding.tvListaDesplegableElegirProducto.postDelayed({
                                    binding.tvListaDesplegableElegirProducto.setSelection(opcionesList.indexOf("$codigoBarraProducto ( 0 unid. )"))
                                    binding.tvListaDesplegableElegirProducto.setText("$codigoBarraProducto ( 0 unid. )",false)
                                },100)

                                // DropDownProducto?.performClick()


                                //DropDownProducto?.setSelection(opcionesList.indexOf("$codigoBarraProducto ( 0 unid. )"))


                            } else if (codigoBarraProducto == "") {
                                Toast.makeText(
                                    requireContext(),
                                    "Código de barra de embalaje que pertenece al producto $codigoBarraEmbalaje",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        catch(e: Exception){
                            Toast.makeText(requireContext(), "No hay producto o embalaje asociado a este código de barra",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    },
                    { error ->
                        Toast.makeText(
                            requireContext(),
                            "No hay producto o embalaje asociado a este código de barra $error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                    override fun getParams(): MutableMap<String, String> {
                        val parametros = HashMap<String, String>()
                        parametros.put(
                            "CODIGO_BARRA_PRODUCTO",
                            binding.etCodigoDeBarra.text.toString()
                        )
                        return parametros
                    }
                }
                queue.add(jsonObjectRequest)
            }
    }

    fun preguntarInventario(){
        val queue = Volley.newRequestQueue(requireContext())
        val url = "http://186.64.123.248/Transferencia/preguntarInventario.php"
        val jsonObjectRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                try {
                    val cantidad = JSONObject(response).getString("Cantidad")
                    if(binding.llUnidadesElegirProducto.isVisible) {
                        if (cantidad.toInt() < binding.etCantidad.text.toString().toInt()) {
                            val inflater = requireActivity().layoutInflater
                            val layout = inflater.inflate(R.layout.toast_custom,null)
                            val text = layout.findViewById<TextView>(R.id.text_view_toast)
                            text.text = "La cantidad es mayor que la cantidad en inventario"
                            val toast = Toast(requireContext())
                            toast.duration = Toast.LENGTH_LONG
                            toast.view = layout
                            toast.setGravity(Gravity.BOTTOM, 0, 600)
                            toast.show()
                        }
                    }
                    else if(binding.llCajasDeProductoElegirProducto.isVisible) {
                        if(binding.etNumeroDeCajas.text.isNotBlank() && binding.etArticulosPorCaja.text.isNotBlank()) {
                            if (cantidad.toInt() < binding.etNumeroDeCajas.text.toString().toInt()
                                    .times(binding.etArticulosPorCaja.text.toString().toInt())
                            ) {
                                val inflater = requireActivity().layoutInflater
                                val layout = inflater.inflate(R.layout.toast_custom, null)
                                val text = layout.findViewById<TextView>(R.id.text_view_toast)
                                text.text = "La cantidad es mayor que la cantidad en inventario"
                                val toast = Toast(requireContext())
                                toast.duration = Toast.LENGTH_LONG
                                toast.view = layout
                                toast.setGravity(Gravity.BOTTOM, 0, 600)
                                toast.show()
                            }
                        }
                    }
                }
                catch(e: Exception){
                    Toast.makeText(requireContext(), "La excepcion es $e",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.i("Sebastián", "$e")
                }
            },
            { error ->
                Toast.makeText(
                    requireContext(),
                    "El error es $error",
                    Toast.LENGTH_LONG
                ).show()
                Log.i("Sebastián", "$error")
            }) {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("ALMACEN_ORIGEN", sharedViewModel.almacen)
                parametros.put("PRODUCTO", DropDownProducto?.text.toString())
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