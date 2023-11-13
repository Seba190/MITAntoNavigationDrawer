package com.seba.mitantonavigationdrawer.ui.añadirInventario

import android.annotation.SuppressLint
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentAnadirInventarioBinding

class AnadirInventarioFragment : Fragment(R.layout.fragment_anadir_inventario) {

    private var _binding: FragmentAnadirInventarioBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    var TextNombre: EditText? = null
    var TextDireccion: EditText? = null
    var TextEstado: EditText? = null
    var TextUsuario: EditText? = null
    var TextId : EditText? = null
    var CheckBox : CheckBox? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val anadirInventarioViewModel =
            ViewModelProvider(this).get(AnadirInventarioViewModel::class.java)

        _binding = FragmentAnadirInventarioBinding.inflate(inflater, container, false)
        val root: View = binding.root




        //Aquí se programa
       /* binding.button.setOnClickListener {
            // Ejemplo usando Volley
            val queue = Volley.newRequestQueue(requireContext())
            val url = "http://186.64.123.248/insertar.php"

            val stringRequest = StringRequest(
                Request.Method.POST, url,
                Response.Listener<String> { response ->
                    // Manejar la respuesta del servidor (puede ser una confirmación de inserción)
                    Toast.makeText(requireContext(),"Almacen insertado exitosamente", Toast.LENGTH_LONG).show()
                },
                Response.ErrorListener { error ->
                    // Manejar errores
                    Toast.makeText(requireContext(),"Error $error", Toast.LENGTH_LONG).show()
                }
            )
            { override fun getParams(): MutableMap<String, String>? {
                val parametros = HashMap<String, String>()
                parametros.put("ALMACEN", TextNombre?.text.toString())
                parametros.put("DIRECCION_ALMACEN", TextDireccion?.text.toString())
                parametros.put("ID_USUARIO", TextUsuario?.text.toString())
                parametros.put("ESTADO_ALMACEN", 1.toString())
                return parametros
            }

        }*/
        TextNombre = binding.etNombreAlmacen.findViewById(R.id.etNombreAlmacen)
        TextDireccion = binding.etDireccionAlmacen.findViewById(R.id.etDireccionAlmacen)
        TextEstado = binding.etEstadoAlmacen.findViewById(R.id.etEstadoAlmacen)
        TextUsuario = binding.etIdUsuarioResponsable.findViewById(R.id.etIdUsuarioResponsable)
        TextId = binding.etIdAlmacen.findViewById(R.id.etIdAlmacen)
        CheckBox = binding.cbActividad.findViewById(R.id.cbActividad)


        val queue =Volley.newRequestQueue(requireContext())
        val url ="http://186.64.123.248/Almacen/registro.php"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,url, null,
            { response ->
                TextId?.setText(response.getString("ID_ALMACEN"))
                Toast.makeText(requireContext(),"Id ingresado correctamente al formulario.", Toast.LENGTH_LONG).show()
            }, { error ->
                Toast.makeText(requireContext(),"Conecte la aplicación al servidor para ingresar al id del almacen", Toast.LENGTH_LONG).show()
            }
        )
        queue.add(jsonObjectRequest)

        binding.cbActividad.setOnClickListener {
            //Que hara el Checkbox
            if(binding.cbActividad.isChecked){
                binding.etEstadoAlmacen.isEnabled
                binding.etEstadoAlmacen.setText("1")
                !binding.etEstadoAlmacen.isEnabled
            }
            else{
                binding.etEstadoAlmacen.isEnabled
                binding.etEstadoAlmacen.setText("0")
                !binding.etEstadoAlmacen.isEnabled
            }
        }



        binding.button.setOnClickListener {
          //  val url ="http://186.64.123.248/phpmyadmin/index.php?route=/sql&pos=0&db=mitanto&table=almacen"
            val url ="http://186.64.123.248/Almacen/insertar.php"
            val queue =Volley.newRequestQueue(requireContext())
            var resultadoPost =object : StringRequest(Request.Method.POST,url,
                Response.Listener<String> { response ->
                    Toast.makeText(requireContext(),"Almacen insertado exitosamente", Toast.LENGTH_LONG).show()
                }, Response.ErrorListener { error ->
                    Toast.makeText(requireContext(),"No se ha introducido el almacen a la base de datos porque no hay conexión a ella", Toast.LENGTH_LONG).show()
                   // Toast.makeText(requireContext(),"Error $error", Toast.LENGTH_LONG).show()
                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    val parametros = HashMap<String, String>()
                    parametros.put("ID_ALMACEN", TextId?.text.toString())
                    parametros.put("ALMACEN", TextNombre?.text.toString().uppercase())
                    parametros.put("DIRECCION_ALMACEN", TextDireccion?.text.toString().uppercase())
                    parametros.put("ESTADO_ALMACEN", TextEstado?.text.toString())
                    parametros.put("ID_USUARIO",TextUsuario?.text.toString())
                    return parametros


                }
            }
            queue.add(resultadoPost)
       }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}