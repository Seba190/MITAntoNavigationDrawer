package com.seba.mitantonavigationdrawer.ui.removerInventario

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
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentRemoverInventarioBinding
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirInventario.AnadirInventarioAdapter
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirInventario.ElegirProductoAnadirFragment
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


class RemoverInventarioFragment : Fragment(R.layout.fragment_remover_inventario) {

    private var _binding: FragmentRemoverInventarioBinding? = null
    private val sharedViewModel by activityViewModels<SharedViewModel>()
    var TextNombre: EditText? = null
    var TextFecha: EditText? = null
    var TextComentarios: EditText? = null
    var DropDownCliente: AutoCompleteTextView? = null
    var DropDownAlmacen: AutoCompleteTextView? = null
    private lateinit var retrofit: Retrofit
    private var requestCamara: ActivityResultLauncher<String>? = null
    private lateinit var adapter: RemoverInventarioAdapter
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val removerInventarioViewModel =
            ViewModelProvider(this).get(RemoverInventarioViewModel::class.java)

        _binding = FragmentRemoverInventarioBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa
        TextNombre = binding.etNombreFacturaSalida.findViewById(R.id.etNombreFacturaSalida)
        TextFecha = binding.etFacturaSalidaFechaTransaccion.findViewById(R.id.etFacturaSalidaFechaTransaccion)
        TextComentarios = binding.etFacturaSalidaComentarios.findViewById(R.id.etFacturaSalidaComentarios)
        DropDownCliente = binding.tvListaDesplegableCliente.findViewById(R.id.tvListaDesplegableCliente)
        DropDownAlmacen = binding.tvListaDesplegableAlmacen.findViewById(R.id.tvListaDesplegableAlmacen)

        binding.etNombreFacturaSalida.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etFacturaSalidaFechaTransaccion.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etFacturaSalidaComentarios.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)

        ListaDesplegableCliente()
        ListaDesplegableAlmacen()
      //  retrofit = getRetrofit()

        requestCamara = registerForActivityResult(ActivityResultContracts.RequestPermission(),){
            if(it){
                findNavController().navigate(R.id.action_nav_remover_inventario_to_nav_barcode_scan_remover)
            }else{
                Toast.makeText(requireContext(),"Permiso denegado",Toast.LENGTH_SHORT).show()
            }
        }
        binding.bEscanearCodigoDeBarraRemover.setOnClickListener {
            requestCamara?.launch(android.Manifest.permission.CAMERA)
        }
       // searchByName()

        binding.FacturaSalidaButtonEnviar.setOnClickListener {
                if ((sharedViewModel.listaDeCantidadesRemover.size > 0 && sharedViewModel.listaDeProductosRemover.size > 0 && sharedViewModel.listaDePreciosRemover.size > 0) &&
                    (sharedViewModel.opcionesListRemoverCliente.contains(DropDownCliente?.text.toString()) &&
                    sharedViewModel.opcionesListRemoverAlmacen.contains(DropDownAlmacen?.text.toString()))) {
                    ValidacionesIdInsertarDatos()
                    adapter.notifyDataSetChanged()
                    binding.rvElegirProductoRemover.requestLayout()
                }else if(sharedViewModel.listaDeCantidadesRemover.size == 0 &&
                    sharedViewModel.listaDeProductosRemover.size == 0 &&
                    sharedViewModel.listaDePreciosRemover.size == 0) {
                    Toast.makeText(requireContext(),
                        "Debe elegir al menos un producto para remover inventario", Toast.LENGTH_SHORT).show()
                }else if(!sharedViewModel.opcionesListRemoverCliente.contains(DropDownCliente?.text.toString()) ||
                      !sharedViewModel.opcionesListRemoverAlmacen.contains(DropDownAlmacen?.text.toString())){
                Toast.makeText(requireContext(),"El nombre del cliente o del almacén no es válido",
                    Toast.LENGTH_SHORT).show()
               }else if(DropDownCliente?.text.toString() == "Elija una opción" ||
                    DropDownAlmacen?.text.toString() == "Elija una opción"){
                   Toast.makeText(requireContext(),"Debe elegir el cliente y el almacén", Toast.LENGTH_SHORT).show()
                }
        }

        binding.tvProductosAnadidosRemover.isVisible = false
       /* val productosOrdenados = sharedViewModel.listaDeProductosRemover.sorted().toMutableList()
        val listaCombinada = sharedViewModel.listaDeProductosRemover.zip(sharedViewModel.listaDeCantidadesRemover)
        val listaOrdenadaCombinada = listaCombinada.sortedBy { it.first }
        val cantidadesOrdenadas = listaOrdenadaCombinada.map { it.second }.toMutableList()
        val listaCombinada2 = sharedViewModel.listaDeProductosRemover.zip(sharedViewModel.listaDePreciosRemover)
        val listaOrdenadaCombinada2 = listaCombinada2.sortedBy { it.first }
        val preciosOrdenados = listaOrdenadaCombinada2.map { it.second }.toMutableList()*/
        ordenarListas()
        recyclerViewElegirProducto(sharedViewModel.listaDeCantidadesRemover,sharedViewModel.listaDeProductosRemover,sharedViewModel.listaDePreciosRemover)
        adapter.updateList(sharedViewModel.listaDeCantidadesRemover,sharedViewModel.listaDeProductosRemover,sharedViewModel.listaDePreciosRemover)
        adapter.notifyDataSetChanged()
        binding.rvElegirProductoRemover.requestLayout()
        binding.bActualizarRecyclerViewRemover.setOnClickListener {
           /* val productosOrdenadosActualizado = sharedViewModel.listaDeProductosRemover.sorted().toMutableList()
            val listaCombinadaActualizada = sharedViewModel.listaDeProductosRemover.zip(sharedViewModel.listaDeCantidadesRemover)
            val listaOrdenadaCombinadaActualizada = listaCombinadaActualizada.sortedBy { it.first }
            val cantidadesOrdenadasActualizada = listaOrdenadaCombinadaActualizada.map { it.second }.toMutableList()
            val listaCombinadaActualizada2 = sharedViewModel.listaDeProductosRemover.zip(sharedViewModel.listaDePreciosRemover)
            val listaOrdenadaCombinadaActualizada2 = listaCombinadaActualizada2.sortedBy { it.first }
            val preciosOrdenadosActualizado = listaOrdenadaCombinadaActualizada2.map { it.second }.toMutableList()*/
            ordenarListas()
            adapter.updateList(sharedViewModel.listaDeCantidadesRemover,sharedViewModel.listaDeProductosRemover,sharedViewModel.listaDePreciosRemover)
            binding.tvProductosAnadidosRemover.isVisible = !(sharedViewModel.listaDeCantidadesRemover.size == 0 || sharedViewModel.listaDeProductosRemover.size == 0 || sharedViewModel.listaDePreciosRemover.size == 0)
            binding.llMontoRemover.isVisible = !(sharedViewModel.listaDeCantidadesRemover.size == 0 || sharedViewModel.listaDeProductosRemover.size == 0 || sharedViewModel.listaDePreciosRemover.size == 0)
            adapter.notifyDataSetChanged()
            Handler(Looper.getMainLooper()).postDelayed({
                binding.tvMontoRemover.text = sharedViewModel.listaDePreciosDeProductosRemover.sum().toString()
                sharedViewModel.facturaTotalRemover = sharedViewModel.listaDePreciosDeProductosRemover
            }, 300)
            sharedViewModel.listaDePreciosDeProductosRemover.clear()
            binding.nsvElegirProductoRemover.isVisible = !(sharedViewModel.listaDeCantidadesRemover.size == 0 || sharedViewModel.listaDeProductosRemover.size == 0 || sharedViewModel.listaDePreciosRemover.size == 0)
            binding.rvElegirProductoRemover.requestLayout()
        }
        binding.nsvElegirProductoRemover.isVisible = false
        binding.llMontoRemover.isVisible = false

        binding.bAnadirNuevoProductoRemover.setOnClickListener {
            if(sharedViewModel.opcionesListRemoverCliente.contains(DropDownCliente?.text.toString()) &&
                sharedViewModel.opcionesListRemoverAlmacen.contains(DropDownAlmacen?.text.toString())){
                sharedViewModel.almacenRemover = DropDownAlmacen?.text.toString()
                sharedViewModel.listaDeAlmacenesRemover.add(DropDownAlmacen?.text.toString())
                sharedViewModel.clienteRemover = DropDownCliente?.text.toString()
                binding.nsvElegirProductoRemover.isVisible = true
                binding.llMontoRemover.isVisible = true
                //setFragmentResult("Proveedor", bundleOf("Proveedor" to DropDownProveedor?.text.toString()))
                //Toast.makeText(requireContext(),"Funciona el traspaso de información", Toast.LENGTH_SHORT).show()
                val elegirProductoRemoverFragment = ElegirProductoRemoverFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.clRemoverInventario, elegirProductoRemoverFragment)
                    .commit()
                binding.tvProductosAnadidosRemover.isVisible = true
            }else if ((!sharedViewModel.opcionesListRemoverCliente.contains(DropDownCliente?.text.toString()) &&
                !sharedViewModel.opcionesListRemoverAlmacen.contains(DropDownAlmacen?.text.toString())) &&
                (DropDownCliente?.text.toString() == "Elija una opción" ||
                DropDownAlmacen?.text.toString() == "Elija una opción")){
                Toast.makeText(requireContext(),"Debe elegir cliente y almacén", Toast.LENGTH_SHORT).show()

            } else if((DropDownCliente?.text.toString() != "Elija una opción" ||
                        DropDownAlmacen?.text.toString() != "Elija una opción") &&
                    (!sharedViewModel.opcionesListRemoverCliente.contains(DropDownCliente?.text.toString()) ||
                      !sharedViewModel.opcionesListRemoverAlmacen.contains(DropDownAlmacen?.text.toString()))){
                Toast.makeText(requireContext(),"El nombre del cliente o del almacén no es válido", Toast.LENGTH_SHORT).show()
            }
        }

       /* binding.llCajasDeProductoSalida.isVisible = false
        binding.bUnidadesSalida.setOnClickListener {
            binding.llUnidadesSalida.isVisible = false
            binding.llCajasDeProductoSalida.isVisible = true
        }
        binding.bCajasDeProductoSalida.setOnClickListener {
            binding.llCajasDeProductoSalida.isVisible = false
            binding.llUnidadesSalida.isVisible = true
        }
        binding.llProductosSalida.isVisible = false

        binding.ivTrashSalida.setOnClickListener {
            binding.llProductosSalida.isVisible = false
        }*/
        binding.etFacturaSalidaFechaTransaccion.setText(SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Calendar.getInstance().time))
        return root
    }

    fun updateData(dataCantidad:MutableList<String>, dataProducto: MutableList<String>,dataPrecio: MutableList<String>){
        adapter.updateData(dataCantidad,dataProducto,dataPrecio)
        binding.rvElegirProductoRemover.adapter?.notifyDataSetChanged()
        binding.rvElegirProductoRemover.requestLayout()
    }

    fun recyclerViewElegirProducto(listaDeCantidades: MutableList<String>, listaDeProductos: MutableList<String>, listaDePrecios: MutableList<String>){
        adapter = RemoverInventarioAdapter(listaDeCantidades,listaDeProductos,listaDePrecios,sharedViewModel) { position ->
            onDeletedItem(position)}
        binding.rvElegirProductoRemover.setHasFixedSize(true)
        binding.rvElegirProductoRemover.adapter = adapter
        binding.rvElegirProductoRemover.layoutManager = LinearLayoutManager(requireContext())
       //adapter.updateList(listaDeCantidades,listaDeProductos,listaDePrecios)
        adapter.updateList(sharedViewModel.listaDeCantidadesRemover,sharedViewModel.listaDeProductosRemover,sharedViewModel.listaDePreciosRemover)
        adapter.notifyDataSetChanged()
        binding.rvElegirProductoRemover.requestLayout()
    }

    private fun onDeletedItem(position: Int){
      //Lo comentado no funciona
      //  if(sharedViewModel.listaDeProductosRemover.isEmpty() && sharedViewModel.listaDeCantidadesRemover.isEmpty()){
      //      binding.nsvElegirProductoRemover.isVisible = false
      //  }
       // sharedViewModel.opcionesListRemover.add(position,sharedViewModel.listaDeProductosRemover[position])
        try{
            sharedViewModel.listaDeCantidadesRemover.removeAt(position)
            sharedViewModel.listaDeProductosRemover.removeAt(position)
            sharedViewModel.listaDePreciosRemover.removeAt(position)
            /* sharedViewModel.facturaTotalRemover.removeAt(position)
            val productosOrdenadosActualizado = sharedViewModel.listaDeProductosRemover.sorted().toMutableList()
            val listaCombinadaActualizada = sharedViewModel.listaDeProductosRemover.zip(sharedViewModel.listaDeCantidadesRemover)
            val listaOrdenadaCombinadaActualizada = listaCombinadaActualizada.sortedBy { it.first }
            val cantidadesOrdenadasActualizada = listaOrdenadaCombinadaActualizada.map { it.second }.toMutableList()
            val listaCombinadaActualizada2 = sharedViewModel.listaDeProductosRemover.zip(sharedViewModel.listaDePreciosRemover)
            val listaOrdenadaCombinadaActualizada2 = listaCombinadaActualizada2.sortedBy { it.first }
            val preciosOrdenadosActualizado = listaOrdenadaCombinadaActualizada2.map { it.second }.toMutableList()*/
            ordenarListas()
            adapter.updateList(sharedViewModel.listaDeCantidadesRemover,sharedViewModel.listaDeProductosRemover,sharedViewModel.listaDePreciosRemover)
            adapter.notifyItemRemoved(position)
            adapter.notifyDataSetChanged()
            Handler(Looper.getMainLooper()).postDelayed({
                binding.tvMontoRemover.text = sharedViewModel.listaDePreciosDeProductosRemover.sum().toString()
                sharedViewModel.facturaTotalRemover = sharedViewModel.listaDePreciosDeProductosRemover
            }, 300)
            sharedViewModel.listaDePreciosDeProductosRemover.clear()
            binding.tvProductosAnadidosRemover.isVisible = !(adapter.listaDeCantidadesRemover.size == 0 || adapter.listaDeProductosRemover.size == 0 || adapter.listaDePreciosRemover.size == 0)
            binding.llMontoRemover.isVisible = !(adapter.listaDeCantidadesRemover.size == 0 || adapter.listaDeProductosRemover.size == 0 || adapter.listaDePreciosRemover.size == 0)
            binding.rvElegirProductoRemover.requestLayout()
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

    private fun ValidacionesIdInsertarDatos() {
        //INICIO EXPERIMIENTO!!!!!!!!!!!!!!!!!!!!!!!!!! (FUNCIONO)
        val queue =Volley.newRequestQueue(requireContext())
        val url ="http://186.64.123.248/FacturaSalida/registro.php"
        val jsonObjectRequest = object: StringRequest(
            Request.Method.POST,url,
            { response ->
                if(TextNombre?.text.toString().isNotBlank()){
                    val id = JSONObject(response).getString("ID_FACTURA_SALIDA")
                    //unico = 1
                    //no unico = 0
                    val unico = JSONObject(response).getString("FACTURA_SALIDA_UNICA")
                    if (unico == "1") {
                        //Aqui va el código para validar el almacen
                        val url1 = "http://186.64.123.248/FacturaSalida/insertar.php" // Reemplaza esto con tu URL de la API
                        val queue1 =Volley.newRequestQueue(requireContext())
                        val stringRequest = object: StringRequest(
                            Request.Method.POST,
                            url1,
                            { response ->
                                if(binding.etNombreFacturaSalida.text.isNotBlank() && binding.tvListaDesplegableCliente.text.toString() != "Elija una opción" && binding.tvListaDesplegableAlmacen.text.toString() != "Elija una opción") {
                                 //   Handler(Looper.getMainLooper()).postDelayed({
                                    removerInventario()
                                 //   }, 2500)
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        InsertarPreciosYCantidades()
                                    }, 200)
                                    binding.tvProductosAnadidosRemover.isVisible = !(adapter.listaDeCantidadesRemover.size == 0 && adapter.listaDeProductosRemover.size == 0 && adapter.listaDePreciosRemover.size == 0)
                                }
                                Toast.makeText(requireContext(), "Factura agregada exitosamente. El id de ingreso es el número $id ", Toast.LENGTH_SHORT).show()
                               /* TextNombre?.setText("")
                                TextFecha?.setText("")
                                DropDownCliente?.setText("Elija una opción",false)
                                DropDownAlmacen?.setText("Elija una opción",false)
                                TextComentarios?.setText("")*/

                            },
                            { error ->
                                Toast.makeText(requireContext(),"El nombre de la factura es demasiado largo", Toast.LENGTH_SHORT).show()
                                //Toast.makeText(requireContext(), "Conecte la aplicación al servidor", Toast.LENGTH_SHORT).show()
                            }
                        )

                        {
                            override fun getParams(): MutableMap<String, String> {
                                val parametros = HashMap<String, String>()
                                parametros.put("ID_FACTURA_SALIDA", id.toString())
                                parametros.put("FACTURA_SALIDA", TextNombre?.text.toString().uppercase())
                                parametros.put("CLIENTE", DropDownCliente?.text.toString())
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
                        Toast.makeText(requireContext(), "La factura ya se encuentra en la base de datos", Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    Toast.makeText(requireContext(), "El nombre de la factura es obligatorio", Toast.LENGTH_SHORT).show()
                }

                // TextId?.setText(response.getString("ID_ALMACEN"))
                // Toast.makeText(requireContext(),"Id ingresado correctamente al formulario.", Toast.LENGTH_SHORT).show()
            }, { error ->
                Toast.makeText(requireContext(),"Conecte la aplicación al servidor", Toast.LENGTH_SHORT).show()
                //Toast.makeText(requireContext(), "Conecte la aplicación al servidor", Toast.LENGTH_SHORT).show()
            }
        ){
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("FACTURA_SALIDA", TextNombre?.text.toString().uppercase())
                return parametros
            }
        }
        queue.add(jsonObjectRequest)
    }

    private fun ListaDesplegableAlmacen() {
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 ="http://186.64.123.248/FacturaSalida/almacen.php"
        val jsonObjectRequest1 = JsonObjectRequest(
            Request.Method.GET,url1, null,
            { response ->
                // Obtén el array de opciones desde el objeto JSON
                val jsonArray = response.getJSONArray("Lista")
                // Convierte el array JSON a una lista mutable
                sharedViewModel.opcionesListRemoverAlmacen.clear()
                if(sharedViewModel.opcionesListRemoverAlmacen.isEmpty()) {
                  for (i in 0 until jsonArray.length()) {
                    sharedViewModel.opcionesListRemoverAlmacen.add(jsonArray.getString(i).removeSurrounding("'", "'"))
                    }
                }
                sharedViewModel.opcionesListRemoverAlmacen.sort()
                //Crea un adpatador para el dropdown
                val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_dropdown_item_1line,sharedViewModel.opcionesListRemoverAlmacen)
                //binding.tvholaMundo?.setText(response.getString("Lista"))
                DropDownAlmacen?.setAdapter(adapter)
                DropDownAlmacen?.threshold = 1
                DropDownAlmacen?.onItemClickListener = AdapterView.OnItemClickListener {
                        parent, view, position, id ->
                    sharedViewModel.listaDeProductosRemover.clear()
                    sharedViewModel.listaDeCantidadesRemover.clear()
                    sharedViewModel.listaDePreciosRemover.clear()
                    binding.tvProductosAnadidosRemover.isVisible = sharedViewModel.listaDeProductosRemover.size != 0
                    binding.llMontoRemover.isVisible = sharedViewModel.listaDeProductosRemover.size != 0
                    binding.nsvElegirProductoRemover.isVisible = false
                    binding.rvElegirProductoRemover.requestLayout()
                    sharedViewModel.opcionesListRemover.clear()
                    val itemSelected = parent.getItemAtPosition(position)
                }
                DropDownAlmacen?.setOnClickListener {
                    if(DropDownAlmacen?.text.toString() == "Elija una opción"){
                        binding.tvListaDesplegableAlmacen.setText("",false)
                        DropDownAlmacen?.showDropDown()
                    }
                }
                DropDownAlmacen?.setOnFocusChangeListener { _, hasFocus ->
                    if(hasFocus && DropDownAlmacen?.text.toString() == "Elija una opción"){
                        binding.tvListaDesplegableAlmacen.setText("",false)
                        DropDownAlmacen?.showDropDown()
                    }
                    else if (!hasFocus && !sharedViewModel.opcionesListRemoverAlmacen.contains(DropDownAlmacen?.text.toString())){
                        Toast.makeText(requireContext(),"El nombre del almacén no es válido", Toast.LENGTH_SHORT).show()
                    }
                }
            }, { error ->
                Toast.makeText(requireContext(), " La aplicación no se ha conectado con el servidor", Toast.LENGTH_SHORT).show()
            }
        )
        queue1.add(jsonObjectRequest1)
    }


    private fun ListaDesplegableCliente() {
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 ="http://186.64.123.248/FacturaSalida/cliente.php"
        val jsonObjectRequest1 = JsonObjectRequest(
            Request.Method.GET,url1, null,
            { response ->
                // Obtén el array de opciones desde el objeto JSON
                val jsonArray = response.getJSONArray("Lista")
                // Convierte el array JSON a una lista mutable
                sharedViewModel.opcionesListRemoverCliente.clear()
                if(sharedViewModel.opcionesListRemoverCliente.isEmpty()) {
                    for (i in 0 until jsonArray.length()) {
                        sharedViewModel.opcionesListRemoverCliente.add(jsonArray.getString(i).removeSurrounding("'", "'"))
                    }
                }
                sharedViewModel.opcionesListRemoverCliente.sort()
                //Crea un adpatador para el dropdown
                val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_dropdown_item_1line,sharedViewModel.opcionesListRemoverCliente)
                //binding.tvholaMundo?.setText(response.getString("Lista"))
                DropDownCliente?.setAdapter(adapter)
                DropDownCliente?.threshold = 1
                DropDownCliente?.onItemClickListener = AdapterView.OnItemClickListener {
                        parent, view, position, id ->
                    val itemSelected = parent.getItemAtPosition(position)
                }
                DropDownCliente?.setOnClickListener {
                    if(DropDownCliente?.text.toString() == "Elija una opción"){
                        binding.tvListaDesplegableCliente.setText("",false)
                        DropDownCliente?.showDropDown()
                    }
                }
                DropDownCliente?.setOnFocusChangeListener { _, hasFocus ->
                   if(hasFocus && DropDownCliente?.text.toString() == "Elija una opción"){
                       binding.tvListaDesplegableCliente.setText("",false)
                       DropDownCliente?.showDropDown()
                   }
                   else if (!hasFocus && !sharedViewModel.opcionesListRemoverCliente.contains(DropDownCliente?.text.toString())){
                       Toast.makeText(requireContext(),"El nombre del cliente no es válido", Toast.LENGTH_SHORT).show()
                   }
                }

            }, { error ->
                Toast.makeText(requireContext(), " La aplicación no se ha conectado con el servidor", Toast.LENGTH_SHORT).show()
            }
        )
        queue1.add(jsonObjectRequest1)
    }

    private fun InsertarPreciosYCantidades(){
        val queue =Volley.newRequestQueue(requireContext())
        val url ="http://186.64.123.248/FacturaSalida/registroProductos.php"
        val jsonObjectRequest = object: StringRequest(
            Request.Method.POST,url,
            { response ->
                if(TextNombre?.text.toString().isNotBlank()){
                    val id = JSONObject(response).getString("ID_FACTURA_SALIDA")
                    //unico = 1
                    //no unico = 0
                    //Aqui va el código para validar el almacen
                    val url1 = "http://186.64.123.248/FacturaSalida/insertarProductos.php" // Reemplaza esto con tu URL de la API
                    val queue1 =Volley.newRequestQueue(requireContext())
                    val stringRequest = object: StringRequest(
                        Request.Method.POST,
                        url1,
                        { response ->
                            Toast.makeText(requireContext(),"Productos removidos exitosamente", Toast.LENGTH_SHORT).show()
                            TextNombre?.setText("")
                            binding.etFacturaSalidaFechaTransaccion.setText(SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Calendar.getInstance().time))
                            binding.tvListaDesplegableCliente.setText("Elija una opción",false)
                            binding.tvListaDesplegableAlmacen.setText("Elija una opción",false)
                            TextComentarios?.setText("")
                            sharedViewModel.listaDeCantidadesRemover.clear()
                            sharedViewModel.listaDeProductosRemover.clear()
                            sharedViewModel.listaDePreciosRemover.clear()
                          /*  val productosOrdenadosActualizado = sharedViewModel.listaDeProductosRemover.sorted().toMutableList()
                            val listaCombinadaActualizada = sharedViewModel.listaDeProductosRemover.zip(sharedViewModel.listaDeCantidadesRemover)
                            val listaOrdenadaCombinadaActualizada = listaCombinadaActualizada.sortedBy { it.first }
                            val cantidadesOrdenadasActualizada = listaOrdenadaCombinadaActualizada.map { it.second }.toMutableList()
                            val listaCombinadaActualizada2 = sharedViewModel.listaDeProductosRemover.zip(sharedViewModel.listaDePreciosRemover)
                            val listaOrdenadaCombinadaActualizada2 = listaCombinadaActualizada2.sortedBy { it.first }
                            val preciosOrdenadosActualizado = listaOrdenadaCombinadaActualizada2.map { it.second }.toMutableList()
                            adapter.updateList(cantidadesOrdenadasActualizada,productosOrdenadosActualizado,preciosOrdenadosActualizado)*/
                            adapter.notifyDataSetChanged()
                            binding.rvElegirProductoRemover.requestLayout()
                            binding.tvProductosAnadidosRemover.isVisible = false
                            binding.nsvElegirProductoRemover.isVisible = false
                            binding.llMontoRemover.isVisible = false
                            sharedViewModel.opcionesListRemover.clear()
                            sharedViewModel.facturaTotalRemover.clear()
                            sharedViewModel.listaDePreciosDeProductosRemover.clear()
                            sharedViewModel.opcionesListRemover.clear()
                            Handler(Looper.getMainLooper()).postDelayed({
                                findNavController().navigate(R.id.action_nav_remover_inventario_to_nav_inicio)
                            },500)

                        },
                        { error ->
                           /* TextNombre?.setText("")
                            TextFecha?.setText("")
                            binding.tvListaDesplegableCliente.setText("Elija una opción",false)
                            binding.tvListaDesplegableAlmacen.setText("Elija una opción",false)
                            TextComentarios?.setText("")*/
                            //Toast.makeText(requireContext(),"Error $error y ${sharedViewModel.listaDeProductosRemover} y ${sharedViewModel.listaDeCantidadesRemover}", Toast.LENGTH_SHORT).show()
                            Toast.makeText(requireContext(),"Conecte la aplicación al servidor", Toast.LENGTH_SHORT).show()
                        }
                    )

                    {
                        override fun getParams(): MutableMap<String, String> {
                            val parametros = HashMap<String, String>()
                            parametros.put("ID_FACTURA_SALIDA", id.toString())
                            parametros.put("NUMERO_DE_PRODUCTOS",sharedViewModel.listaDeProductosRemover.size.toString())
                            for (i in 0..<sharedViewModel.listaDeProductosRemover.size) {
                                parametros["PRODUCTO$i"] = sharedViewModel.listaDeProductosRemover[i].uppercase()
                                parametros["CANTIDAD$i"] = sharedViewModel.listaDeCantidadesRemover[i]
                                parametros["PRECIO_VENTA$i"] = sharedViewModel.listaDePreciosRemover[i]
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
                Toast.makeText(requireContext(), "Conecte la aplicación al servidor", Toast.LENGTH_SHORT).show()
            }
        ){
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("FACTURA_SALIDA", TextNombre?.text.toString().uppercase())
                return parametros
            }
        }
        queue.add(jsonObjectRequest)
    }

    fun removerInventario(){
        //Si el inventario es menor que la transferencia que no la haga y si el almacen de destino no existe, que lo cree
        val url1 = "http://186.64.123.248/FacturaSalida/removerInventario.php" // Reemplaza esto con tu URL de la API
        val queue1 =Volley.newRequestQueue(requireContext())
        val stringRequest = object: StringRequest(
            Request.Method.POST,
            url1,
            { response ->
                Toast.makeText(requireContext(),"Cantidad de inventario removida  exitosamente", Toast.LENGTH_SHORT).show()

            },
            { error ->
                /*  TextNombre?.setText("")
                  TextFecha?.setText("")
                  DropDownOrigen?.setText("Elija una opción",false)
                  DropDownDestino?.setText("Elija una opción",false)
                  TextComentarios?.setText("")
                  TextCodigoDeBarra?.setText("")*/
                //Toast.makeText(requireContext(),"Error $error y ${sharedViewModel.listaDeProductosRemover} y ${sharedViewModel.listaDeCantidadesRemover}", Toast.LENGTH_SHORT).show()
                Toast.makeText(requireContext(), "Conecte la aplicación al servidor", Toast.LENGTH_SHORT).show()
            }
        )

        {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("NUMERO_DE_PRODUCTOS",sharedViewModel.listaDeProductosRemover.size.toString())
                parametros.put("ALMACEN", DropDownAlmacen?.text.toString())
                for (i in 0..<sharedViewModel.listaDeProductosRemover.size) {
                    parametros["PRODUCTO$i"] = sharedViewModel.listaDeProductosRemover[i].uppercase()
                    parametros["CANTIDAD$i"] = sharedViewModel.listaDeCantidadesRemover[i]
                }

                return parametros
            }
        }
        queue1.add(stringRequest)
    }/*,

    /*  private fun getRetrofit(): Retrofit {
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
                                  binding.rvProductosSalida.isVisible = false
                                  binding.etProductosSalida.addTextChangedListener { userFilter ->
                                      binding.rvProductosSalida.isVisible = true
                                      if (binding.etProductosSalida.text.isNullOrBlank()) {
                                          binding.rvProductosSalida.isVisible = false
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
              }catch (e: SocketTimeoutException){
                  activity?.runOnUiThread { Toast.makeText(requireContext(),"Se acabo el tiempo de espera para conectar la aplicación al servidor",Toast.LENGTH_SHORT).show()}
              }catch(e:Exception){
                  activity?.runOnUiThread { Toast.makeText(requireContext(),"No se puedo conectar la aplicación al servidor",Toast.LENGTH_SHORT).show()}
              }
          }
      }

      private fun appearQuantity(id:String) {
          val nombre = adapter.obtenerNombreEnPosicion(id.toInt())
          binding.llProductosSalida.isVisible = true
          binding.tvProductosSalida.setText(nombre)
      }

      private fun setUpRecyclerView() {
          //adapter = AnadirInventarioAdapter{appearQuantity(it)}
          // adapter2 = CaracteristicasAdapter()
          binding.rvProductosSalida.setHasFixedSize(true)
          binding.rvProductosSalida.layoutManager = LinearLayoutManager(requireContext())
          binding.rvProductosSalida.adapter = adapter
      }*/*/

    private fun ordenarListas(){
        val listaCombinada = sharedViewModel.listaDeProductosRemover.indices.map{ i ->
            Triple(sharedViewModel.listaDeProductosRemover[i], sharedViewModel.listaDeCantidadesRemover[i], sharedViewModel.listaDePreciosRemover[i])
        }
        val listaOrdenada = listaCombinada.sortedBy { it.first }

        sharedViewModel.listaDeProductosRemover = listaOrdenada.map { it.first }.toMutableList()
        sharedViewModel.listaDeCantidadesRemover = listaOrdenada.map { it.second }.toMutableList()
        sharedViewModel.listaDePreciosRemover = listaOrdenada.map { it.third }.toMutableList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}