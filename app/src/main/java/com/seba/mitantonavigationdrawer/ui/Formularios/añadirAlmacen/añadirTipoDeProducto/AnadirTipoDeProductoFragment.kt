package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirTipoDeProducto

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
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentAnadirTipoDeProductoBinding
import org.json.JSONObject


class AnadirTipoDeProductoFragment : Fragment(R.layout.fragment_anadir_tipo_de_producto) {

    private var _binding: FragmentAnadirTipoDeProductoBinding? = null
    var TextEstado :Int = 1
    var TextNombre: EditText? = null
    var TextDescripcion: EditText? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val anadirTipoDeProductoViewModel =
            ViewModelProvider(this).get(AnadirTipoDeProductoViewModel::class.java)

        _binding = FragmentAnadirTipoDeProductoBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa
        TextNombre = binding.etNombreTipoDeProducto.findViewById(R.id.etNombreTipoDeProducto)
        TextDescripcion = binding.etDescripcionTipoDeProducto.findViewById(R.id.etDescripcionTipoDeProducto)

        binding.etNombreTipoDeProducto.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etDescripcionTipoDeProducto.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)

        binding.buttonTipoDeProducto.setOnClickListener {
            ValidacionesIdInsertarDatos()
        }
        return root
    }

    private fun ValidacionesIdInsertarDatos() {
        val queue = Volley.newRequestQueue(requireContext())
        val url = "http://186.64.123.248/TipoDeProducto/registro.php"
        val jsonObjectRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                if (!TextNombre?.text.toString().isBlank()) {
                    val id = JSONObject(response).getString("ID_TIPO_PRODUCTO")
                    //unico = 1
                    //no unico = 0
                    val unico = JSONObject(response).getString("TIPO_PRODUCTO_UNICO")
                    if (unico == "1") {
                        //Aqui va el código para validar el almacen
                        val url1 = "http://186.64.123.248/TipoDeProducto/insertar.php" // Reemplaza esto con tu URL de la API
                        val queue1 = Volley.newRequestQueue(requireContext())
                        val stringRequest = object : StringRequest(
                            Request.Method.POST,
                            url1,
                            { response ->
                                Toast.makeText(
                                    requireContext(),
                                    "Tipo de producto agregado exitosamente. El id de ingreso es el número $id ",
                                    Toast.LENGTH_LONG
                                ).show()
                                TextNombre?.setText("")
                                TextDescripcion?.setText("")
                            },
                            { error ->
                                Toast.makeText(
                                    requireContext(),
                                    "No se ha introducido el tipo de producto a la base de datos",
                                    Toast.LENGTH_LONG
                                ).show()
                                // Toast.makeText(requireContext(),"Error $error", Toast.LENGTH_LONG).show()
                            }
                        ) {
                            override fun getParams(): MutableMap<String, String> {
                                val parametros = HashMap<String, String>()
                                parametros.put("ID_TIPO_PRODUCTO", id.toString())
                                parametros.put("TIPO_PRODUCTO", TextNombre?.text.toString().uppercase())
                                parametros.put("ESTADO_TIPO_PRODUCTO", TextEstado.toString())
                                parametros.put("DESCRIPCION", TextDescripcion?.text.toString().uppercase())
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
                            "El tipo de productoya se encuentra en la base de datos",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "El nombre del tipo de producto es obligatorio",
                        Toast.LENGTH_LONG
                    ).show()
                }

                // TextId?.setText(response.getString("ID_ALMACEN"))
                // Toast.makeText(requireContext(),"Id ingresado correctamente al formulario.", Toast.LENGTH_LONG).show()
            }, { error ->
                Toast.makeText(requireContext(), "Conecte la aplicación al servidor", Toast.LENGTH_LONG).show()
                // Toast.makeText(requireContext(),"Error $error", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("TIPO_PRODUCTO", TextNombre?.text.toString().uppercase())
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