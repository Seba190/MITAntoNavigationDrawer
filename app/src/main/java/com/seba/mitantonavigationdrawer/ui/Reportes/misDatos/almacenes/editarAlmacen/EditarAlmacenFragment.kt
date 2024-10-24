package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.editarAlmacen

import android.app.AlertDialog
import android.graphics.Color
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
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentEditarAlmacenBinding
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.MisDatosFragmentArgs
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.clientes.editarCliente.EditarClienteFragmentDirections
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos.editarProducto.EditarCantidadProductosFragment
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import org.json.JSONObject


class EditarAlmacenFragment : Fragment(R.layout.fragment_editar_almacen),RadioGroup.OnCheckedChangeListener {
    companion object {
        const val EXTRA_ID = "extra_id"
    }

    private var _binding: FragmentEditarAlmacenBinding? = null
    private val args: MisDatosFragmentArgs by navArgs()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val sharedViewModel : SharedViewModel by activityViewModels()
    private val binding get() = _binding!!
    var TextNombre: EditText? = null
    var TextDireccion: EditText? = null
    var TextDropdown:  AutoCompleteTextView? = null
    var TextEstado: Int = 1
    var radioGroup: RadioGroup? = null
    var radio1: RadioButton? = null
    var radio2: RadioButton? = null
   // var EliminarAlmacen: ImageView? = null
    //var TextId: EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val editarAlmacenViewModel =
            ViewModelProvider(this).get(EditarAlmacenViewModel::class.java)

        _binding = FragmentEditarAlmacenBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa
        TextNombre = binding.etNombreAlmacenEditable.findViewById(R.id.etNombreAlmacenEditable)
        TextDireccion = binding.etDireccionAlmacenEditable.findViewById(R.id.etDireccionAlmacenEditable)
        //EliminarAlmacen = binding.ivEliminarAlmacen.findViewById(R.id.ivEliminarAlmacen)
        TextDropdown = binding.tvAutoCompleteEditable.findViewById(R.id.tvAutoCompleteEditable)
        radio1 = binding.EstadoAlmacenRadioButton1.findViewById(R.id.EstadoAlmacenRadioButton1)
        radio2 = binding.EstadoAlmacenRadioButton2.findViewById(R.id.EstadoAlmacenRadioButton2)
        radioGroup = binding.radioGroupEstadoAlmacen.findViewById(R.id.radioGroupEstadoAlmacen)
        radioGroup?.setOnCheckedChangeListener(this)
        //TextId = binding.etId.findViewById(R.id.etId)
        binding.etNombreAlmacenEditable.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etDireccionAlmacenEditable.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        //   Mostrar los usuarios responsables en la lista desplegable
         ListaDesplegable()
        binding.buttonEditable.setOnClickListener {
            if(sharedViewModel.opcionesListEditarAlmacenUsuario.contains(TextDropdown?.text.toString()) &&
                TextDropdown?.text.toString() != "Elija una opción"){
                actualizarAlmacen()
            }else if(!sharedViewModel.opcionesListEditarAlmacenUsuario.contains(TextDropdown?.text.toString()) &&
                TextDropdown?.text.toString() == "Elija una opción"){
                Toast.makeText(requireContext(),"Debe elegir nombre de usuario", Toast.LENGTH_SHORT).show()
            }
            else if(!sharedViewModel.opcionesListEditarAlmacenUsuario.contains(TextDropdown?.text.toString()) &&
                TextDropdown?.text.toString() != "Elija una opción"){
                Toast.makeText(requireContext(),"El usuario no es válido", Toast.LENGTH_SHORT).show()
            }
        }

       // EliminarAlmacen?.setOnClickListener {
       //     mensajeEliminarAlmacen()
       // }

        try {
            sharedViewModel.id.add(args.id)
        }
        catch(e:Exception){
        }
        return root
    }

    private fun ListaDesplegable() {
        val queue1 =Volley.newRequestQueue(requireContext())
        val url1 ="http://186.64.123.248/Reportes/Almacenes/usuario.php"
        val jsonObjectRequest1 = JsonObjectRequest(
            Request.Method.GET,url1, null,
            { response ->
                // Obtén el array de opciones desde el objeto JSON
                val jsonArray = response.getJSONArray("Lista")
                // Convierte el array JSON a una lista mutable
                if(sharedViewModel.opcionesListEditarAlmacenUsuario.isEmpty()){
                for (i in 0 until jsonArray.length()) {
                    sharedViewModel.opcionesListEditarAlmacenUsuario.add(jsonArray.getString(i).removeSurrounding("'", "'"))
                  }
                }
                sharedViewModel.opcionesListEditarAlmacenUsuario.sort()
                //Crea un adpatador para el dropdown
                val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_dropdown_item_1line,sharedViewModel.opcionesListEditarAlmacenUsuario)
                //binding.TextDropdown?.setText(response.getString("Lista"))
                binding.tvAutoCompleteEditable.setAdapter(adapter)
                binding.tvAutoCompleteEditable.threshold = 1
                binding.tvAutoCompleteEditable.onItemClickListener = AdapterView.OnItemClickListener {
                        parent, view, position, id ->
                    val itemSelected = parent.getItemAtPosition(position)
                }

                TextDropdown?.setOnClickListener {
                    if(TextDropdown?.text.toString() == "Elija una opción"){
                        binding.tvAutoCompleteEditable.setText("",false)
                        TextDropdown?.showDropDown()
                    }
                }
                TextDropdown?.setOnFocusChangeListener { _, hasFocus ->
                    if(hasFocus && TextDropdown?.text.toString() == "Elija una opción"){
                        binding.tvAutoCompleteEditable.setText("",false)
                        TextDropdown?.showDropDown()
                    }
                    else if (!hasFocus && !sharedViewModel.opcionesListEditarAlmacenUsuario.contains(TextDropdown?.text.toString())){
                        Toast.makeText(requireContext(),"El nombre del usuario no es válido", Toast.LENGTH_SHORT).show()
                    }
                }

            }, { error ->
                Toast.makeText(requireContext(), " La aplicación no se ha conectado con el servidor", Toast.LENGTH_SHORT).show()
            }
        )
        queue1.add(jsonObjectRequest1)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val queue1 = Volley.newRequestQueue(requireContext())
        //val id = arguments?.getString(EXTRA_ID).toString()
        Log.i("Sebastian", "Valor de Id de destino es: ${sharedViewModel.id.last()}")
        //  val id2 = this.arguments
        // val id3 = id2?.get(EXTRA_ID)
        //val id4 = almacenesItemResponse.Id
        val url1 = "http://186.64.123.248/Reportes/Almacenes/registroInsertar.php?ID_ALMACEN=${sharedViewModel.id.last()}"
        val jsonObjectRequest1 = JsonObjectRequest(
            Request.Method.GET, url1, null,
            { response ->
                inventarioDeProductos()
                TextNombre?.setText(response.getString("ALMACEN"))
                TextDireccion?.setText(response.getString("DIRECCION_ALMACEN"))
                TextDropdown?.setText(response.getString("USUARIO"),false)
                TextEstado = response.getString("ESTADO_ALMACEN").toInt()
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
                val action = EditarAlmacenFragmentDirections.actionNavEditarAlmacenToNavMisDatos(destino = "almacenes")
                findNavController().navigate(action)
                borrarListas()
            }
        })
    }

    private fun actualizarAlmacen() {
        if (TextNombre?.text.toString().isNotBlank()) {
            val url1 =
                "http://186.64.123.248/Reportes/Almacenes/actualizarAlmacen.php" // Reemplaza esto con tu URL de la API
            val queue1 = Volley.newRequestQueue(requireContext())
            val stringRequest = object : StringRequest(
                Request.Method.POST,
                url1,
                { response ->
                    Toast.makeText(
                        requireContext(),
                        "Almacén actualizado exitosamente. El id de ingreso es el número ${sharedViewModel.id.last()} ",
                        Toast.LENGTH_SHORT
                    ).show()
                    //   TextNombre?.setText("")
                    //   TextDireccion?.setText("")
                    //   TextDropdown?.setText("Elija una opción",false)
                },
                { error ->
                    Log.i("Sebastian", "Error: $error")
                    Toast.makeText(requireContext(), "El error es $error", Toast.LENGTH_SHORT).show()
                    // Toast.makeText(requireContext(),"Solo se ha podido borrar el almacen.", Toast.LENGTH_SHORT).show()
                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    val parametros = HashMap<String, String>()
                    parametros.put("ID_ALMACEN", sharedViewModel.id.last())
                    parametros.put("ALMACEN", TextNombre?.text.toString().uppercase())
                    parametros.put("DIRECCION_ALMACEN", TextDireccion?.text.toString().uppercase())
                    parametros.put("ESTADO_ALMACEN", TextEstado.toString())
                    parametros.put("USUARIO", TextDropdown?.text.toString())
                    return parametros
                }
            }
            queue1.add(stringRequest)
        }else {
            Toast.makeText(
                requireContext(),
                "El nombre del almacén es obligatorio",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun borrarRegistros(){
        val url = "http://186.64.123.248/Reportes/Almacenes/borrar.php"
        val queue = Volley.newRequestQueue(requireContext())
        var jsonObjectRequest = object : StringRequest(Request.Method.POST, url,
            { response ->
              //  Toast.makeText(requireContext(), "Exito", Toast.LENGTH_SHORT).show()

            },{error ->
                //Toast.makeText(requireContext(), "$error", Toast.LENGTH_SHORT).show()
            })
        {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("ID_ALMACEN", sharedViewModel.id.last())
                return parametros
            }
        }
        queue.add(jsonObjectRequest)
    }
    private fun actualizarRegistros() {
        val url1 = "http://186.64.123.248/Reportes/Almacenes/insertar.php" // Reemplaza esto con tu URL de la API
        val queue1 =Volley.newRequestQueue(requireContext())
        val stringRequest = object: StringRequest(
            Request.Method.POST,
            url1,
            { response ->
                Toast.makeText(requireContext(), "Almacén actualizado exitosamente. El id de ingreso es el número ${sharedViewModel.id.last()} ", Toast.LENGTH_SHORT).show()
             //   TextNombre?.setText("")
             //   TextDireccion?.setText("")
             //   TextDropdown?.setText("Elija una opción",false)
            },
            { error ->
                Toast.makeText(requireContext(),"El error es $error", Toast.LENGTH_SHORT).show()
               // Toast.makeText(requireContext(),"Solo se ha podido borrar el almacen.", Toast.LENGTH_SHORT).show()
            }
        )

        {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("ID_ALMACEN", sharedViewModel.id.last())
                parametros.put("ALMACEN", TextNombre?.text.toString().uppercase())
                parametros.put("DIRECCION_ALMACEN", TextDireccion?.text.toString().uppercase())
                parametros.put("ESTADO_ALMACEN", TextEstado.toString())
                parametros.put("USUARIO", TextDropdown?.text.toString())
                return parametros
            }
        }
        queue1.add(stringRequest)
    }

    private fun inventarioDeProductos(){
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 = "http://186.64.123.248/Reportes/Almacenes/inventariosDeProductoCompleto.php?ID_ALMACEN=${sharedViewModel.id.last()}"
        val jsonObjectRequest1 = JsonObjectRequest(
            Request.Method.GET, url1, null,
            { response ->
               try {
                   // val matriz = response.getJSONArray("Lista")
                   val productos = response.getJSONArray("Productos")
                   val inventario = response.getJSONArray("Inventario")
                   var listaProductos = mutableListOf<String>()
                   var listaInventario = mutableListOf<String>()
                   for (i in (0 until productos.length())) {
                       listaProductos.add(productos.getString(i))
                   }
                   for (i in (0 until inventario.length())) {
                       listaInventario.add(inventario.getString(i))
                   }
                   val listaCombinada = listaProductos.zip(listaInventario)
                   val listaOrdenadaCombinada = listaCombinada.sortedBy { it.first }
                   val (productosOrdenados, inventarioOrdenado) = listaOrdenadaCombinada.unzip()
                        listaProductos = productosOrdenados.toMutableList()
                        listaInventario = inventarioOrdenado.toMutableList()
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
                      // if(listaInventario[i] != "0"){
                       registro.findViewById<TableRow>(R.id.trFilaTabla).setOnClickListener {
                           //  binding.clEditarTiposDeProductos.isVisible = false
                           sharedViewModel.inventario.add(listaInventario[i])
                           sharedViewModel.productos.add(listaProductos[i])
                           val editarCantidadAlmacenesFragment = EditarCantidadAlmacenesFragment()
                           parentFragmentManager.beginTransaction()
                               .replace(R.id.rlEditarAlmacen, editarCantidadAlmacenesFragment)
                               .commit()
                       }
                       //}
                   }
               }catch(e:Exception){
                   val queue3 = Volley.newRequestQueue(requireContext())
                   val url3 = "http://186.64.123.248/Reportes/Almacenes/productos.php"
                   val jsonObjectRequest3 = JsonObjectRequest(
                       Request.Method.GET, url3, null,
                       { response ->
                           val productos3 = response.getJSONArray("Lista")
                           val listaProductos3 = mutableListOf<String>()
                           for (i in (0 until productos3.length())) {
                               listaProductos3.add(productos3.getString(i))
                           }
                           val productosOrdenados3 = listaProductos3.sorted().toMutableList()
                           for (i in (0 until listaProductos3.count())) {
                               val registro = LayoutInflater.from(requireContext())
                                   .inflate(R.layout.filas_inventario, null, false)
                               val tv0 = registro.findViewById<View>(R.id.tv0) as TextView
                               val tv1 = registro.findViewById<View>(R.id.tv1) as TextView
                               tv0.text = productosOrdenados3[i]
                               tv1.text = "0"
                               tv0.setTextColor(Color.DKGRAY)
                               tv1.setTextColor(Color.DKGRAY)
                               tv0.gravity = Gravity.CENTER
                               tv1.gravity = Gravity.CENTER
                               binding.tlInventariosDeProducto.addView(registro)
                               registro.findViewById<TableRow>(R.id.trFilaTabla).setOnClickListener {
                                   //  binding.clEditarTiposDeProductos.isVisible = false
                                   sharedViewModel.inventario.add(0.toString())
                                   sharedViewModel.productos.add(productosOrdenados3[i])
                                   val editarCantidadAlmacenesFragment = EditarCantidadAlmacenesFragment()
                                   parentFragmentManager.beginTransaction()
                                       .replace(R.id.rlEditarAlmacen, editarCantidadAlmacenesFragment)
                                       .commit()
                               }
                           }
                       },{error ->

                       } )
                   queue3.add(jsonObjectRequest3)
               }

                //Toast.makeText(requireContext(),"${listaAlmacenes[0][0]} y $listaAlmacenes",Toast.LENGTH_SHORT).show()

            }, { error ->
                val queue2 = Volley.newRequestQueue(requireContext())
                val url2 = "http://186.64.123.248/Reportes/Almacenes/productos.php"
                val jsonObjectRequest2 = JsonObjectRequest(
                    Request.Method.GET, url2, null,
                    { response ->
                        val productos2 = response.getJSONArray("Lista")
                        val listaProductos2 = mutableListOf<String>()
                        for (i in (0 until productos2.length())) {
                            listaProductos2.add(productos2.getString(i))
                        }
                        val productosOrdenados2 = listaProductos2.sorted().toMutableList()
                        for (i in (0 until listaProductos2.count())) {
                            val registro = LayoutInflater.from(requireContext())
                                .inflate(R.layout.filas_inventario, null, false)
                            val tv0 = registro.findViewById<View>(R.id.tv0) as TextView
                            val tv1 = registro.findViewById<View>(R.id.tv1) as TextView
                            tv0.text = productosOrdenados2[i]
                            tv1.text = "0"
                            tv0.setTextColor(Color.DKGRAY)
                            tv1.setTextColor(Color.DKGRAY)
                            tv0.gravity = Gravity.CENTER
                            tv1.gravity = Gravity.CENTER
                            binding.tlInventariosDeProducto.addView(registro)
                            registro.findViewById<TableRow>(R.id.trFilaTabla).setOnClickListener {
                                //  binding.clEditarTiposDeProductos.isVisible = false
                                sharedViewModel.inventario.add(tv1.text.toString())
                                sharedViewModel.productos.add(productosOrdenados2[i])
                                val editarCantidadAlmacenesFragment = EditarCantidadAlmacenesFragment()
                                parentFragmentManager.beginTransaction()
                                    .replace(R.id.rlEditarAlmacen, editarCantidadAlmacenesFragment)
                                    .commit()
                            }
                        }
                    },{error ->

                    } )
                queue2.add(jsonObjectRequest2)
            }
        )
        queue1.add(jsonObjectRequest1)
    }
    private fun borrarRegistros2(){
        val queue =Volley.newRequestQueue(requireContext())
        val url ="http://186.64.123.248/Reportes/Almacenes/registro2.php"
        val jsonObjectRequest = object: StringRequest(
            Request.Method.POST,url,
            { response ->
                if(!TextNombre?.text.toString().isBlank()){
                    //val id = JSONObject(response).getString("ID_ALMACEN")
                    //unico = 1
                    //no unico = 0
                    val unico = JSONObject(response).getString("ALMACEN_UNICO")
                    val almacen = JSONObject(response).getString("ALMACEN")
                    //Es unico ,se borra
                    if (unico == "1" || (unico == "0" && TextNombre?.text.toString() == almacen.toString())) {
                        //Aqui va el código para validar el almacen
                        val url = "http://186.64.123.248/Reportes/Almacenes/borrar.php"
                        val queue = Volley.newRequestQueue(requireContext())
                        var jsonObjectRequest = object : StringRequest(Request.Method.POST, url,
                            { response ->
                                //  Toast.makeText(requireContext(), "Exito", Toast.LENGTH_SHORT).show()

                            },{error ->
                                //Toast.makeText(requireContext(), "$error", Toast.LENGTH_SHORT).show()
                            })
                        {
                            override fun getParams(): MutableMap<String, String> {
                                val parametros = HashMap<String, String>()
                                parametros.put("ID_ALMACEN", args.id)
                                return parametros
                            }
                        }
                        queue.add(jsonObjectRequest)

                    }
                    /*//No es unico pero es el mismo que el del formulario
                    else if (unico == "0" && TextNombre?.text.toString() == almacen.toString()){
                        //Aqui va el código para validar el almacen
                        val url = "http://186.64.123.248/Reportes/Almacenes/borrar.php"
                        val queue = Volley.newRequestQueue(requireContext())
                        var jsonObjectRequest = object : StringRequest(Request.Method.POST, url,
                            { response ->
                                //  Toast.makeText(requireContext(), "Exito", Toast.LENGTH_SHORT).show()

                            },{error ->
                                //Toast.makeText(requireContext(), "$error", Toast.LENGTH_SHORT).show()
                            })
                        {
                            override fun getParams(): MutableMap<String, String> {
                                val parametros = HashMap<String, String>()
                                parametros.put("ID_ALMACEN", args.id)
                                return parametros
                            }
                        }
                        queue.add(jsonObjectRequest)
                    }*/
                    else if (unico == "0" && TextNombre?.text.toString() != almacen.toString()){
                        //VolleyError("El almacén ya se encuentra en la base de datos")
                        //queue1.cancelAll(TAG)
                        //jsonObjectRequest1.cancel()
                        Toast.makeText(requireContext(), "El almacén ya se encuentra en la base de datos", Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    Toast.makeText(requireContext(), "El nombre del almacén es obligatorio", Toast.LENGTH_SHORT).show()
                }

                // TextId?.setText(response.getString("ID_ALMACEN"))
                // Toast.makeText(requireContext(),"Id ingresado correctamente al formulario.", Toast.LENGTH_SHORT).show()
            }, { error ->
                Toast.makeText(requireContext(),"Conecte la aplicación al servidor", Toast.LENGTH_SHORT).show()
                // Toast.makeText(requireContext(),"Error $error", Toast.LENGTH_SHORT).show()
            }
        ){
            override fun getParams(): MutableMap<String, String> {
                val parametros1 = HashMap<String, String>()
                parametros1.put("ALMACEN", TextNombre?.text.toString().uppercase())
                return parametros1
            }
        }
        queue.add(jsonObjectRequest)
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when(checkedId){
            radio1?.id -> TextEstado = 1
            radio2?.id -> TextEstado = 0
        }
    }

    private fun mensajeEliminarAlmacen(){
        AlertDialog.Builder(context).apply {
            setTitle("¿Quieres eliminar este almacén?")
            setMessage("Esta acción no se puede deshacer.")
            setPositiveButton("Sí") { dialog, _ ->
                eliminarProveedor()
                findNavController().navigate(R.id.action_nav_editar_almacen_to_nav_mis_datos)
                dialog.dismiss()
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }

    }
    private fun eliminarProveedor(){
        val url = "http://186.64.123.248/Reportes/Almacenes/borrar.php"
        val queue = Volley.newRequestQueue(requireContext())
        var jsonObjectRequest = object : StringRequest(Request.Method.POST, url,
            { response ->
                Toast.makeText(requireContext(), "Almacén eliminado exitosamente", Toast.LENGTH_SHORT).show()

            },{error ->
                Toast.makeText(requireContext(), "No se pudo eliminar el almacén", Toast.LENGTH_SHORT).show()
            })
        {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("ID_ALMACEN", sharedViewModel.id.last())
                return parametros
            }
        }
        queue.add(jsonObjectRequest)
    }

    private fun borrarListas(){
        sharedViewModel.listaDeProductos.clear()
        sharedViewModel.listaDeCantidades.clear()
        sharedViewModel.listaDeProductosAnadir.clear()
        sharedViewModel.listaDeCantidadesAnadir.clear()
        sharedViewModel.listaDePreciosAnadir.clear()
        sharedViewModel.listaDePreciosDeProductos.clear()
        sharedViewModel.listaDeProductosRemover.clear()
        sharedViewModel.listaDeCantidadesRemover.clear()
        sharedViewModel.listaDePreciosRemover.clear()
        sharedViewModel.listaDePreciosDeProductosRemover.clear()
        sharedViewModel.listaDeBodegasAnadir.clear()
        sharedViewModel.listaDeAlertasAnadir.clear()
        sharedViewModel.ListasDeAlertas.clear()
        sharedViewModel.ListasDeAlmacenes.clear()
        sharedViewModel.ListasDeProductosAlertas.clear()
        sharedViewModel.listaDeClientesAnadir.clear()
        sharedViewModel.listaDePreciosVentaAnadir.clear()
        sharedViewModel.ListasDeClientes.clear()
        sharedViewModel.ListasDePreciosDeVenta.clear()
        sharedViewModel.ListasDeProductosPrecioVenta.clear()
        sharedViewModel.listaDePreciosCompraAnadir.clear()
        sharedViewModel.listaDeProveedoresAnadir.clear()
        sharedViewModel.ListasDeProveedores.clear()
        sharedViewModel.ListasDePreciosDeCompra.clear()
        sharedViewModel.ListasDeProductosPrecioCompra.clear()
        sharedViewModel.id.clear()
        sharedViewModel.listaDeAlertas.clear()
        sharedViewModel.listaDePreciosVenta.clear()
        sharedViewModel.listaDePreciosCompra.clear()
        sharedViewModel.listaDeBodegas.clear()
        sharedViewModel.listaDeClientes.clear()
        sharedViewModel.listaDeProveedores.clear()
        sharedViewModel.numeroAlertas.clear()
        sharedViewModel.numeroPreciosCompra.clear()
        sharedViewModel.numeroPreciosVenta.clear()
        sharedViewModel.listaDeAlmacenesAnadir.clear()
        sharedViewModel.listaDeAlmacenesRemover.clear()
        sharedViewModel.listaDeAlmacenesEntrada.clear()
        sharedViewModel.listaDeAlmacenesSalida.clear()
        sharedViewModel.listaDeAlmacenesTransferencia.clear()
        sharedViewModel.listaDeAlmacenesEditarTransferencia.clear()
        sharedViewModel.productos.clear()
        sharedViewModel.inventario.clear()

        sharedViewModel.facturaTotalAnadir.clear()
        sharedViewModel.facturaTotalRemover.clear()
        sharedViewModel.facturaTotalEntrada.clear()
        sharedViewModel.facturaTotalSalida.clear()
        sharedViewModel.cantidadTotalTransferencia.clear()
        sharedViewModel.cantidadTotalEditarTransferencia.clear()

        sharedViewModel.listaCombinadaEntrada.clear()
        sharedViewModel.listaCombinadaSalida.clear()
        sharedViewModel.listaCombinadaTransferencia.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}