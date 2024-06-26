package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.proveedores.editarProveedor

import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentEditarProveedorBinding
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.MisDatosFragmentArgs
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import org.json.JSONObject


class EditarProveedorFragment: Fragment(R.layout.fragment_editar_proveedor),RadioGroup.OnCheckedChangeListener {
    companion object {
        const val EXTRA_ID = "extra_id"
    }

    private var _binding: FragmentEditarProveedorBinding? = null
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
    //var TextId: EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val editarProveedorViewModel =
            ViewModelProvider(this).get(EditarProveedorViewModel::class.java)

        _binding = FragmentEditarProveedorBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa
        TextNombre = binding.etNombreProveedor.findViewById(R.id.etNombreProveedor)
        TextDireccion = binding.etDireccionProveedor.findViewById(R.id.etDireccionProveedor)
        TextRut = binding.etRutProveedor.findViewById(R.id.etRutProveedor)
        TextCorreo = binding.etCorreoProveedor.findViewById(R.id.etCorreoProveedor)
        TextTelefono = binding.etTelefonoProveedor.findViewById(R.id.etTelefonoProveedor)
        radio1 = binding.EstadoProveedorRadioButton1.findViewById(R.id.EstadoProveedorRadioButton1)
        radio2 = binding.EstadoProveedorRadioButton2.findViewById(R.id.EstadoProveedorRadioButton2)
        radioGroup = binding.radioGroupEstadoProveedor.findViewById(R.id.radioGroupEstadoProveedor)
        radioGroup?.setOnCheckedChangeListener(this)

        binding.etNombreProveedor.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etDireccionProveedor.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
       binding.etRutProveedor.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etCorreoProveedor.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etTelefonoProveedor.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)

        //   Mostrar los usuarios responsables en la lista desplegable
        binding.buttonProveedor.setOnClickListener {
          /*  borrarRegistros()
            Handler(Looper.getMainLooper()).postDelayed({
                actualizarRegistros()
            },500)*/
            actualizarProveedor()
        }
        try {
            sharedViewModel.id.add(args.id)
        }
        catch(e:Exception){
        }
        return root
    }

    private fun actualizarProveedor() {
        val url1 = "http://186.64.123.248/Reportes/Proveedores/actualizarProveedor.php"
        val queue1 =Volley.newRequestQueue(requireContext())
        val stringRequest = object: StringRequest(
            Request.Method.POST,
            url1,
            { response ->
                Toast.makeText(requireContext(), "Proveedor actualizado exitosamente. El id de ingreso es el número ${sharedViewModel.id.last()} ", Toast.LENGTH_LONG).show()
                //   TextNombre?.setText("")
                //   TextDireccion?.setText("")
                //   TextDropdown?.setText("Eliga una opción",false)
            },
            { error ->
                Log.i("Sebastian","Error: $error")
                Toast.makeText(requireContext(),"$error", Toast.LENGTH_LONG).show()
                // Toast.makeText(requireContext(),"Solo se ha podido borrar el almacen.", Toast.LENGTH_LONG).show()
            }
        )

        {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("ID_PROVEEDOR", sharedViewModel.id.last())
                parametros.put("PROVEEDOR", TextNombre?.text.toString().uppercase())
                parametros.put("RUT_PROVEEDOR", TextRut?.text.toString())
                parametros.put("CORREO_PROVEEDOR", TextCorreo?.text.toString())
                parametros.put("TELEFONO_PROVEEDOR", TextTelefono?.text.toString())
                parametros.put("DIRECCION_PROVEEDOR", TextDireccion?.text.toString().uppercase())
                parametros.put("ESTADO_PROVEEDOR", TextEstado.toString())
                return parametros
            }
        }
        queue1.add(stringRequest)
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
        val url1 = "http://186.64.123.248/Reportes/Proveedores/registroInsertar.php?ID_PROVEEDOR=${sharedViewModel.id.last()}"
        val jsonObjectRequest1 = JsonObjectRequest(
            Request.Method.GET, url1, null,
            { response ->
                TextNombre?.setText(response.getString("PROVEEDOR"))
                TextRut?.setText(response.getString("RUT_PROVEEDOR"))
                TextCorreo?.setText(response.getString("CORREO_PROVEEDOR"))
                TextTelefono?.setText(response.getString("TELEFONO_PROVEEDOR"))
                TextDireccion?.setText(response.getString("DIRECCION_PROVEEDOR"))
                TextEstado = response.getString("ESTADO_PROVEEDOR").toInt()
                if (TextEstado == 1) {
                    radio1?.isChecked = true
                } else if (TextEstado == 0) {
                    radio2?.isChecked = true
                }

            }, { error ->
                Toast.makeText(requireContext(), "El id es $id", Toast.LENGTH_LONG).show()
            }
        )
        queue1.add(jsonObjectRequest1)
    }

    private fun borrarRegistros(){
        val url = "http://186.64.123.248/Reportes/Proveedores/borrar.php"
        val queue = Volley.newRequestQueue(requireContext())
        var jsonObjectRequest = object : StringRequest(Request.Method.POST, url,
            { response ->
                //  Toast.makeText(requireContext(), "Exito", Toast.LENGTH_LONG).show()

            },{error ->
                //Toast.makeText(requireContext(), "$error", Toast.LENGTH_LONG).show()
            })
        {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("ID_PROVEEDOR", args.id)
                return parametros
            }
        }
        queue.add(jsonObjectRequest)
    }
    private fun actualizarRegistros() {
        val url1 = "http://186.64.123.248/Reportes/Proveedores/insertar.php"
        val queue1 =Volley.newRequestQueue(requireContext())
        val stringRequest = object: StringRequest(
            Request.Method.POST,
            url1,
            { response ->
                Toast.makeText(requireContext(), "Proveedor actualizado exitosamente. El id de ingreso es el número ${args.id} ", Toast.LENGTH_LONG).show()
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
                parametros.put("ID_PROVEEDOR", args.id)
                parametros.put("PROVEEDOR", TextNombre?.text.toString().uppercase())
                parametros.put("RUT_PROVEEDOR", TextRut?.text.toString())
                parametros.put("CORREO_PROVEEDOR", TextCorreo?.text.toString())
                parametros.put("TELEFONO_PROVEEDOR", TextTelefono?.text.toString())
                parametros.put("DIRECCION_PROVEEDOR", TextDireccion?.text.toString().uppercase())
                parametros.put("ESTADO_PROVEEDOR", TextEstado.toString())
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
                                //  Toast.makeText(requireContext(), "Exito", Toast.LENGTH_LONG).show()

                            },{error ->
                                //Toast.makeText(requireContext(), "$error", Toast.LENGTH_LONG).show()
                            })
                        {
                            override fun getParams(): MutableMap<String, String> {
                                val parametros = HashMap<String, String>()
                                parametros.put("ID_PROVEEDOR", args.id)
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
                                //  Toast.makeText(requireContext(), "Exito", Toast.LENGTH_LONG).show()

                            },{error ->
                                //Toast.makeText(requireContext(), "$error", Toast.LENGTH_LONG).show()
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
                        Toast.makeText(requireContext(), "El almacén ya se encuentra en la base de datos", Toast.LENGTH_LONG).show()
                    }
                }
                else{
                    Toast.makeText(requireContext(), "El nombre del almacén es obligatorio", Toast.LENGTH_LONG).show()
                }

                // TextId?.setText(response.getString("ID_ALMACEN"))
                // Toast.makeText(requireContext(),"Id ingresado correctamente al formulario.", Toast.LENGTH_LONG).show()
            }, { error ->
                Toast.makeText(requireContext(),"Conecte la aplicación al servidor", Toast.LENGTH_LONG).show()
                // Toast.makeText(requireContext(),"Error $error", Toast.LENGTH_LONG).show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when(checkedId){
            radio1?.id -> TextEstado = 1
            radio2?.id -> TextEstado = 0
        }
    }

}