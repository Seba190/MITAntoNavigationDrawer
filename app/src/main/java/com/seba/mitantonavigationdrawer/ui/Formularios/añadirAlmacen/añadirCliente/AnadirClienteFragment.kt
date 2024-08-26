package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirCliente

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentAnadirClienteBinding
import org.json.JSONObject


class AnadirClienteFragment : Fragment(R.layout.fragment_anadir_cliente) {

    private var _binding: FragmentAnadirClienteBinding? = null
    var TextEstado :Int = 1
    var TextNombre: EditText? = null
    var TextRut: EditText? = null
    var TextCorreo: EditText? = null
    var TextTelefono: EditText? = null
    var TextDireccion: EditText? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val anadirClienteViewModel =
            ViewModelProvider(this).get(AnadirClienteViewModel::class.java)

        _binding = FragmentAnadirClienteBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa
        TextNombre = binding.etNombreCliente.findViewById(R.id.etNombreCliente)
        TextRut = binding.etRutCliente.findViewById(R.id.etRutCliente)
        TextCorreo = binding.etCorreoCliente.findViewById(R.id.etCorreoCliente)
        TextTelefono = binding.etTelefonoCliente.findViewById(R.id.etTelefonoCliente)
        TextDireccion = binding.etDireccionCliente.findViewById(R.id.etDireccionCliente)

        binding.etNombreCliente.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etRutCliente.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etCorreoCliente.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etTelefonoCliente.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etDireccionCliente.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)

        binding.buttonCliente.setOnClickListener {
            ValidacionesIdInsertarDatos()
        }

        return root
    }

    private fun ValidacionesIdInsertarDatos() {
        val queue = Volley.newRequestQueue(requireContext())
        val url = "http://186.64.123.248/Cliente/registro.php"
        val jsonObjectRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                if (!TextNombre?.text.toString().isBlank()) {
                    val id = JSONObject(response).getString("ID_CLIENTE")
                    //unico = 1
                    //no unico = 0
                    val unico = JSONObject(response).getString("CLIENTE_UNICO")
                    if (unico == "1") {
                        //Aqui va el código para validar el almacen
                        val url1 = "http://186.64.123.248/Cliente/insertar.php" // Reemplaza esto con tu URL de la API
                        val queue1 = Volley.newRequestQueue(requireContext())
                        val stringRequest = object : StringRequest(
                            Request.Method.POST,
                            url1,
                            { response ->
                                Toast.makeText(
                                    requireContext(),
                                    "Cliente agregado exitosamente. El id de ingreso es el número $id ",
                                    Toast.LENGTH_SHORT
                                ).show()
                                TextNombre?.setText("")
                                TextRut?.setText("")
                                TextCorreo?.setText("")
                                TextTelefono?.setText("")
                                TextDireccion?.setText("")
                            },
                            { error ->
                                Toast.makeText(
                                    requireContext(),
                                    "No se ha introducido el cliente a la base de datos",
                                    Toast.LENGTH_SHORT
                                ).show()
                                // Toast.makeText(requireContext(),"Error $error", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            override fun getParams(): MutableMap<String, String> {
                                val parametros = HashMap<String, String>()
                                parametros.put("ID_CLIENTE", id.toString())
                                parametros.put("CLIENTE", TextNombre?.text.toString().uppercase())
                                parametros.put("RUT_CLIENTE", TextRut?.text.toString())
                                parametros.put("CORREO_CLIENTE", TextCorreo?.text.toString())
                                parametros.put("TELEFONO_CLIENTE", TextTelefono?.text.toString())
                                parametros.put("DIRECCION_CLIENTE", TextDireccion?.text.toString())
                                parametros.put("ESTADO_CLIENTE", TextEstado.toString())
                                return parametros
                            }
                        }
                        queue1.add(stringRequest)

                    } else if (unico == "0") {
                        //VolleyError("El cliente ya se encuentra en la base de datos")
                        //queue1.cancelAll(TAG)
                        //jsonObjectRequest1.cancel()
                        Toast.makeText(
                            requireContext(),
                            "El cliente ya se encuentra en la base de datos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "El nombre del cliente es obligatorio",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                // TextId?.setText(response.getString("ID_ALMACEN"))
                // Toast.makeText(requireContext(),"Id ingresado correctamente al formulario.", Toast.LENGTH_SHORT).show()
            }, { error ->
                Toast.makeText(requireContext(), "Conecte la aplicación al servidor", Toast.LENGTH_SHORT).show()
               // Toast.makeText(requireContext(),"Error $error", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("CLIENTE", TextNombre?.text.toString().uppercase())
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