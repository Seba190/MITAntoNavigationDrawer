package com.seba.mitantonavigationdrawer.ui.añadirInventario

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentAnadirInventarioBinding
import com.seba.mitantonavigationdrawer.ui.añadirInventario.AnadirInventarioViewModel

class AnadirInventarioFragment : Fragment(R.layout.fragment_anadir_inventario) {

    private var _binding: FragmentAnadirInventarioBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    var TextNombre: EditText? = null
    var TextDireccion: EditText? = null
    var TextEstado: EditText? = null
    var TextUsuario: EditText? = null


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
        TextNombre = binding.editTextText.findViewById(R.id.editTextText)
        TextDireccion = binding.editTextText2.findViewById(R.id.editTextText2)
        TextEstado = binding.editTextText4.findViewById(R.id.editTextText4)
        TextUsuario = binding.editTextText3.findViewById(R.id.editTextText3)


        binding.button.setOnClickListener {
          //  val url ="http://186.64.123.248/phpmyadmin/index.php?route=/sql&pos=0&db=mitanto&table=almacen"
            val url ="http://186.64.123.248/insertar.php"
            val queue =Volley.newRequestQueue(requireContext())
            var resultadoPost =object : StringRequest(Request.Method.POST,url,
                Response.Listener<String> { response ->
                    Toast.makeText(requireContext(),"Almacen insertado exitosamente", Toast.LENGTH_LONG).show()
                }, Response.ErrorListener { error ->
                    Toast.makeText(requireContext(),"Almacen insertado exitosamente", Toast.LENGTH_LONG).show()
                   // Toast.makeText(requireContext(),"Error $error", Toast.LENGTH_LONG).show()
                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    val parametros = HashMap<String, String>()
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