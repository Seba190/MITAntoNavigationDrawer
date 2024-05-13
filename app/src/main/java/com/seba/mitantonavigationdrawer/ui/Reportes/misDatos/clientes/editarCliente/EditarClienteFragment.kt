package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.clientes.editarCliente

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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentEditarClienteBinding
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.MisDatosFragmentArgs
import org.json.JSONObject


class EditarClienteFragment : Fragment(R.layout.fragment_editar_cliente), RadioGroup.OnCheckedChangeListener  {
    companion object {
        const val EXTRA_ID = "extra_id"
    }

    private var _binding: FragmentEditarClienteBinding? = null
    private val args: MisDatosFragmentArgs by navArgs()

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

        binding.buttonCliente.setOnClickListener {
            borrarRegistros()
            Handler(Looper.getMainLooper()).postDelayed({
                actualizarRegistros()
            },500)



        }
        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val queue1 = Volley.newRequestQueue(requireContext())
        //val id = arguments?.getString(EXTRA_ID).toString()
        val id = args.id
        Log.i("Sebastian", "Valor de Id de destino es: $id")
        //  val id2 = this.arguments
        // val id3 = id2?.get(EXTRA_ID)
        //val id4 = almacenesItemResponse.Id
        val url1 = "http://186.64.123.248/Reportes/Clientes/registroInsertar.php?ID_CLIENTE=$id"
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
                Toast.makeText(requireContext(), "El id es $id", Toast.LENGTH_LONG).show()
            }
        )
        queue1.add(jsonObjectRequest1)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun borrarRegistros(){
        val url = "http://186.64.123.248/Reportes/Clientes/borrar.php"
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
                Toast.makeText(requireContext(), "Cliente actualizado exitosamente. El id de ingreso es el número ${args.id} ", Toast.LENGTH_LONG).show()
                //   TextNombre?.setText("")
                //   TextDireccion?.setText("")
                //   TextDropdown?.setText("Eliga una opción",false)
            },
            { error ->
                Toast.makeText(requireContext(),"El error es $error", Toast.LENGTH_LONG).show()
                // Toast.makeText(requireContext(),"Solo se ha podido borrar el almacen.", Toast.LENGTH_LONG).show()
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

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when(checkedId){
            radio1?.id -> TextEstado = 1
            radio2?.id -> TextEstado = 0
        }
    }

}