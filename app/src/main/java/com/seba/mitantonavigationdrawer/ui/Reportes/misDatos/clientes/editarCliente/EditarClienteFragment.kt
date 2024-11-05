package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.clientes.editarCliente

import android.app.AlertDialog
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentEditarClienteBinding
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.MisDatosFragmentArgs
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.clientes.ClientesFragment
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import org.json.JSONObject


class EditarClienteFragment : Fragment(R.layout.fragment_editar_cliente), RadioGroup.OnCheckedChangeListener  {
    companion object {
        const val EXTRA_ID = "extra_id"
    }

    private var _binding: FragmentEditarClienteBinding? = null
    private val args: MisDatosFragmentArgs by navArgs()
    private val sharedViewModel : SharedViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    var TextNombre: EditText? = null
    var TextDireccion: EditText? = null
    var TextRut: EditText? = null
    var TextCorreo: EditText? = null
    var TextTelefono: EditText? = null
    var TextEstado: Int = 1
    var radioGroup: RadioGroup? = null
    var radio1: RadioButton? = null
    var radio2: RadioButton? = null
    //var EliminarCliente: ImageView? = null
    //var TextId: EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val editarClienteViewModel =
            ViewModelProvider(this).get(EditarClienteViewModel::class.java)

        _binding = FragmentEditarClienteBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa
        TextNombre = binding.etNombreCliente.findViewById(R.id.etNombreCliente)
        TextDireccion = binding.etDireccionCliente.findViewById(R.id.etDireccionCliente)
        TextRut = binding.etRutCliente.findViewById(R.id.etRutCliente)
        TextCorreo = binding.etCorreoCliente.findViewById(R.id.etCorreoCliente)
        TextTelefono = binding.etTelefonoCliente.findViewById(R.id.etTelefonoCliente)
       // EliminarCliente = binding.ivEliminarCliente.findViewById(R.id.ivEliminarCliente)
        radio1 = binding.EstadoClienteRadioButton1.findViewById(R.id.EstadoClienteRadioButton1)
        radio2 = binding.EstadoClienteRadioButton2.findViewById(R.id.EstadoClienteRadioButton2)
        radioGroup = binding.radioGroupEstadoCliente.findViewById(R.id.radioGroupEstadoCliente)
        radioGroup?.setOnCheckedChangeListener(this)

        binding.etNombreCliente.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etDireccionCliente.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etRutCliente.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etCorreoCliente.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etTelefonoCliente.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)

        //   Mostrar los usuarios responsables en la lista desplegable

      //  EliminarCliente?.setOnClickListener {
      //      mensajeEliminarCliente()
      //  }

        binding.buttonCliente.setOnClickListener {
            if (TextNombre?.text.toString().isNotBlank()) {
                /*  borrarRegistros()
            Handler(Looper.getMainLooper()).postDelayed({
                actualizarRegistros()
            },500)*/
                actualizarCliente()
            }else {
                Toast.makeText(
                    requireContext(),
                    "El nombre del cliente es obligatorio",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        try {
            sharedViewModel.id.add(args.id)
        }
        catch(e:Exception){
        }
        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val queue1 = Volley.newRequestQueue(requireContext())
        //val id = arguments?.getString(EXTRA_ID).toString()
        //val id = args.id
        Log.i("Sebastian", "Valor de Id de destino es: ${sharedViewModel.id.last()}")
        //  val id2 = this.arguments
        // val id3 = id2?.get(EXTRA_ID)
        //val id4 = almacenesItemResponse.Id
        val url1 =
            "http://186.64.123.248/Reportes/Clientes/registroInsertar.php?ID_CLIENTE=${sharedViewModel.id.last()}"
        val jsonObjectRequest1 = JsonObjectRequest(
            Request.Method.GET, url1, null,
            { response ->
                TextNombre?.setText(response.getString("CLIENTE"))
                TextRut?.setText(response.getString("RUT_CLIENTE"))
                TextCorreo?.setText(response.getString("CORREO_CLIENTE"))
                TextTelefono?.setText(response.getString("TELEFONO_CLIENTE"))
                TextDireccion?.setText(response.getString("DIRECCION_CLIENTE"))
                TextEstado = response.getString("ESTADO_CLIENTE").toInt()
                if (TextEstado == 1) {
                    radio1?.isChecked = true
                } else if (TextEstado == 0) {
                    radio2?.isChecked = true
                }
            }, { error ->
                Toast.makeText(
                    requireContext(),
                    "El id es ${sharedViewModel.id.last()}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
        queue1.add(jsonObjectRequest1)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Navegar a MisDatosFragment y pasar un identificador para saber qué fragmento llamar luego
               // val action = EditarClienteFragmentDirections.actionEditarClienteFragmentToMisDatosFragment("clientes")
                val action = EditarClienteFragmentDirections.actionNavEditarClienteToNavMisDatos(destino = "clientes")
                findNavController().navigate(action)
                borrarListas()
            }
        })

    }

    private fun actualizarCliente() {
        val url1 = "http://186.64.123.248/Reportes/Clientes/actualizarCliente.php"
        val queue1 =Volley.newRequestQueue(requireContext())
        val stringRequest = object: StringRequest(
            Request.Method.POST,
            url1,
            { response ->
                Toast.makeText(requireContext(), "Cliente actualizado exitosamente. El id de ingreso es el número ${sharedViewModel.id.last()} ", Toast.LENGTH_SHORT).show()
                //   TextNombre?.setText("")
                //   TextDireccion?.setText("")
                //   TextDropdown?.setText("Elija una opción",false)
            },
            { error ->
                Log.i("Sebastian","Error: $error")
                Toast.makeText(requireContext(), "Conecte la aplicación al servidor", Toast.LENGTH_SHORT).show()
                // Toast.makeText(requireContext(),"Solo se ha podido borrar el almacen.", Toast.LENGTH_SHORT).show()
            }
        )

        {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("ID_CLIENTE", sharedViewModel.id.last())
                parametros.put("CLIENTE", TextNombre?.text.toString().uppercase())
                parametros.put("RUT_CLIENTE", TextRut?.text.toString())
                parametros.put("CORREO_CLIENTE", TextCorreo?.text.toString())
                parametros.put("TELEFONO_CLIENTE", TextTelefono?.text.toString())
                parametros.put("DIRECCION_CLIENTE", TextDireccion?.text.toString().uppercase())
                parametros.put("ESTADO_CLIENTE", TextEstado.toString())
                return parametros
            }
        }
        queue1.add(stringRequest)
    }

    private fun borrarRegistros(){
        val url = "http://186.64.123.248/Reportes/Clientes/borrar.php"
        val queue = Volley.newRequestQueue(requireContext())
        val jsonObjectRequest = object : StringRequest(Request.Method.POST, url,
            { response ->
                //  Toast.makeText(requireContext(), "Exito", Toast.LENGTH_SHORT).show()

            },{error ->
                //Toast.makeText(requireContext(), "Conecte la aplicación al servidor", Toast.LENGTH_SHORT).show()
            })
        {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("ID_CLIENTE", args.id)
                return parametros
            }
        }
        queue.add(jsonObjectRequest)
    }
    private fun actualizarRegistros() {
        val url1 = "http://186.64.123.248/Reportes/Clientes/insertar.php"
        val queue1 =Volley.newRequestQueue(requireContext())
        val stringRequest = object: StringRequest(
            Request.Method.POST,
            url1,
            { response ->
                Toast.makeText(requireContext(), "Cliente actualizado exitosamente. El id de ingreso es el número ${args.id} ", Toast.LENGTH_SHORT).show()
                //   TextNombre?.setText("")
                //   TextDireccion?.setText("")
                //   TextDropdown?.setText("Elija una opción",false)
            },
            { error ->
                Toast.makeText(requireContext(), "Conecte la aplicación al servidor", Toast.LENGTH_SHORT).show()
                // Toast.makeText(requireContext(),"Solo se ha podido borrar el almacen.", Toast.LENGTH_SHORT).show()
            }
        )

        {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("ID_CLIENTE", args.id)
                parametros.put("CLIENTE", TextNombre?.text.toString().uppercase())
                parametros.put("RUT_CLIENTE", TextRut?.text.toString())
                parametros.put("CORREO_CLIENTE", TextCorreo?.text.toString())
                parametros.put("TELEFONO_CLIENTE", TextTelefono?.text.toString())
                parametros.put("DIRECCION_CLIENTE", TextDireccion?.text.toString().uppercase())
                parametros.put("ESTADO_CLIENTE", TextEstado.toString())
                return parametros
            }
        }
        queue1.add(stringRequest)
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
                                //Toast.makeText(requireContext(), "Conecte la aplicación al servidor", Toast.LENGTH_SHORT).show()
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
                                //Toast.makeText(requireContext(), "Conecte la aplicación al servidor", Toast.LENGTH_SHORT).show()
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
                // Toast.makeText(requireContext(), "Conecte la aplicación al servidor", Toast.LENGTH_SHORT).show()
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

    private fun mensajeEliminarCliente(){
        AlertDialog.Builder(context).apply {
            setTitle("¿Quieres eliminar este cliente?")
            setMessage("Esta acción no se puede deshacer.")
            setPositiveButton("Sí") { dialog, _ ->
                eliminarCliente()
                findNavController().navigate(R.id.action_nav_editar_cliente_to_nav_mis_datos)
                dialog.dismiss()
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }

    }

    private fun eliminarCliente(){
        val url = "http://186.64.123.248/Reportes/Clientes/borrar.php"
        val queue = Volley.newRequestQueue(requireContext())
        var jsonObjectRequest = object : StringRequest(Request.Method.POST, url,
            { response ->
                Toast.makeText(requireContext(), "Cliente eliminado exitosamente", Toast.LENGTH_SHORT).show()

            },{error ->
                Toast.makeText(requireContext(), "No se pudo eliminar el cliente", Toast.LENGTH_SHORT).show()
            })
        {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("ID_CLIENTE", sharedViewModel.id.last())
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