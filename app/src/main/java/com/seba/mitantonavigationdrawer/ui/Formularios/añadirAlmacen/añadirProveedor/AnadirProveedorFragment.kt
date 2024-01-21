package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProveedor

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.JsonObject
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentAnadirProveedorBinding
import org.json.JSONObject


class AnadirProveedorFragment : Fragment(R.layout.fragment_anadir_proveedor) {

    private var _binding: FragmentAnadirProveedorBinding? = null
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
        val anadirProveedorViewModel =
            ViewModelProvider(this).get(AnadirProveedorViewModel::class.java)

        _binding = FragmentAnadirProveedorBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa
        TextNombre = binding.etNombreProveedor.findViewById(R.id.etNombreProveedor)
        TextRut = binding.etRutProveedor.findViewById(R.id.etRutProveedor)
        TextCorreo = binding.etCorreoProveedor.findViewById(R.id.etCorreoProveedor)
        TextTelefono = binding.etTelefonoProveedor.findViewById(R.id.etTelefonoProveedor)
        TextDireccion = binding.etDireccionProveedor.findViewById(R.id.etDireccionProveedor)

        binding.etNombreProveedor.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etRutProveedor.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etCorreoProveedor.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etTelefonoProveedor.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etDireccionProveedor.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)

        binding.buttonProveedor.setOnClickListener {
            ValidacionesIdInsertarDatos()
        }
        return root
    }

    private fun ValidacionesIdInsertarDatos() {
        val queue = Volley.newRequestQueue(requireContext())
        val url = "http://186.64.123.248/Proveedor/registro.php"
        val jsonObjectRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                if (!TextNombre?.text.toString().isBlank()) {
                    val id = JSONObject(response).getString("ID_PROVEEDOR")
                    //unico = 1
                    //no unico = 0
                    val unico = JSONObject(response).getString("PROVEEDOR_UNICO")
                    if (unico == "1") {
                        //Aqui va el código para validar el almacen
                        val url1 = "http://186.64.123.248/Proveedor/insertar.php" // Reemplaza esto con tu URL de la API
                        val queue1 = Volley.newRequestQueue(requireContext())
                        val stringRequest = object : StringRequest(
                            Request.Method.POST,
                            url1,
                            { response ->
                                Toast.makeText(
                                    requireContext(),
                                    "Proveedor agregado exitosamente. El id de ingreso es el número $id ",
                                    Toast.LENGTH_LONG
                                ).show()
                                TextNombre?.setText("")
                                TextRut?.setText("")
                                TextCorreo?.setText("")
                                TextTelefono?.setText("")
                                TextDireccion?.setText("")
                            },
                            { error ->
                               // Toast.makeText(requireContext(), "No se ha introducido el proveedor a la base de datos", Toast.LENGTH_LONG).show()
                                 Toast.makeText(requireContext(),"Error $error", Toast.LENGTH_LONG).show()
                            }
                        ) {
                            override fun getParams(): MutableMap<String, String> {
                                val parametros = HashMap<String, String>()
                                parametros.put("ID_PROVEEDOR", id.toString())
                                parametros.put("PROVEEDOR", TextNombre?.text.toString().uppercase())
                                parametros.put("RUT_PROVEEDOR", TextRut?.text.toString())
                                parametros.put("CORREO_PROVEEDOR", TextCorreo?.text.toString())
                                parametros.put("TELEFONO_PROVEEDOR", TextTelefono?.text.toString())
                                parametros.put("DIRECCION_PROVEEDOR", TextDireccion?.text.toString())
                                parametros.put("ESTADO_PROVEEDOR", TextEstado.toString())
                                return parametros
                            }
                        }
                        queue1.add(stringRequest)

                    } else if (unico == "0") {
                        //VolleyError("El proveedor ya se encuentra en la base de datos")
                        //queue1.cancelAll(TAG)
                        //jsonObjectRequest1.cancel()
                        Toast.makeText(
                            requireContext(),
                            "El proveedor ya se encuentra en la base de datos",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "El nombre del proveedor es obligatorio",
                        Toast.LENGTH_LONG
                    ).show()
                }

                // TextId?.setText(response.getString("ID_ALMACEN"))
                // Toast.makeText(requireContext(),"Id ingresado correctamente al formulario.", Toast.LENGTH_LONG).show()
            }, { error ->
                Toast.makeText(
                    requireContext(),
                    "Conecte la aplicación al servidor",
                    Toast.LENGTH_LONG
                ).show()
                //Toast.makeText(requireContext(),"Error $error", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("PROVEEDOR", TextNombre?.text.toString().uppercase())
                return parametros
            }
        }
        queue.add(jsonObjectRequest)

    }
    private fun ValidacionesIdInsertarDatos2() {
        val queue3 = Volley.newRequestQueue(requireContext())
        val url3 = "http://186.64.123.248/Proveedor/registro.php"
        val jsonObjectRequest3 = JsonObjectRequest(
            Request.Method.POST, url3, null,
            { response ->
                if (!TextNombre?.text.toString().isBlank()) {
                    val id =  response.getJSONArray("Lista")[0]
                    //unico = 1
                    //no unico = 0
                    val unico = response.getJSONArray("Lista")[1]
                    Toast.makeText(requireContext(),"Id es $id y unico es $unico",Toast.LENGTH_LONG).show()
                    if (unico == "1") {
                        //Aqui va el código para validar el almacen
                        val url1 =
                            "http://186.64.123.248/Proveedor/insertar.php" // Reemplaza esto con tu URL de la API
                        val queue1 = Volley.newRequestQueue(requireContext())
                        val stringRequest = object : StringRequest(
                            Request.Method.POST,
                            url1,
                            { response ->
                                Toast.makeText(
                                    requireContext(),
                                    "Proveedor agregado exitosamente. El id de ingreso es el número $id ",
                                    Toast.LENGTH_LONG
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
                                    "No se ha introducido el proveedor a la base de datos",
                                    Toast.LENGTH_LONG
                                ).show()
                                // Toast.makeText(requireContext(),"Error $error", Toast.LENGTH_LONG).show()
                            }
                        ) {
                            override fun getParams(): MutableMap<String, String> {
                                val parametros = HashMap<String, String>()
                                parametros.put("ID_PROVEEDOR", id.toString())
                                parametros.put("PROVEEDOR", TextNombre?.text.toString().uppercase())
                                parametros.put("RUT_PROVEEDOR", TextRut?.text.toString())
                                parametros.put("CORREO_PROVEEDOR", TextCorreo.toString())
                                parametros.put("TELEFONO_PROVEEDOR", TextTelefono?.text.toString())
                                parametros.put(
                                    "DIRECCION_PROVEEDOR",
                                    TextDireccion?.text.toString()
                                )
                                parametros.put("ESTADO_PROVEEDOR", TextEstado.toString())
                                return parametros
                            }
                        }
                        queue1.add(stringRequest)

                    } else if (unico == "0") {
                        VolleyError("El proveedor ya se encuentra en la base de datos")
                        //queue1.cancelAll(TAG)
                        //jsonObjectRequest1.cancel()
                        Toast.makeText(
                            requireContext(),
                            "El proveedor ya se encuentra en la base de datos",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "El nombre del proveedor es obligatorio",
                        Toast.LENGTH_LONG
                    ).show()
                }

                // TextId?.setText(response.getString("ID_ALMACEN"))
                // Toast.makeText(requireContext(),"Id ingresado correctamente al formulario.", Toast.LENGTH_LONG).show()
            }, { error ->
                //Toast.makeText(requireContext(), "Conecte la aplicación al servidor", Toast.LENGTH_LONG).show()
                Toast.makeText(requireContext(),"Error $error", Toast.LENGTH_LONG).show()
               // Toast.makeText(requireContext(),"Id da $id y unico da $unico", Toast.LENGTH_LONG).show()
            }
        )
        queue3.add(jsonObjectRequest3)
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}