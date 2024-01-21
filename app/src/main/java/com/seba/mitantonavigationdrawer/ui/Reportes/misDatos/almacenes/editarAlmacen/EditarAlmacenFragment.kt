package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.editarAlmacen

import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentEditarAlmacenBinding
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.MisDatosFragmentArgs
import org.json.JSONObject


class EditarAlmacenFragment : Fragment(R.layout.fragment_editar_almacen) {
    companion object {
        const val EXTRA_ID = "extra_id"
    }

    private var _binding: FragmentEditarAlmacenBinding? = null
    private val args: MisDatosFragmentArgs by navArgs()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    var TextNombre: EditText? = null
    var TextDireccion: EditText? = null
    var TextDropdown:  AutoCompleteTextView? = null
    val TextEstado: Int = 1
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
        TextDropdown = binding.tvAutoCompleteEditable.findViewById(R.id.tvAutoCompleteEditable)
        //TextId = binding.etId.findViewById(R.id.etId)
        binding.etNombreAlmacenEditable.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etDireccionAlmacenEditable.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        //   Mostrar los usuarios responsables en la lista desplegable
         ListaDesplegable()
        binding.buttonEditable.setOnClickListener {
            borrarRegistros()
            Thread.sleep(500)
            actualizarRegistros()


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
                val opcionesList = mutableListOf<String>()
                for (i in 0 until jsonArray.length()) {
                    opcionesList.add(jsonArray.getString(i).removeSurrounding("'","'"))
                }
                //Crea un adpatador para el dropdown
                val adapter = ArrayAdapter(requireContext(),R.layout.list_item,opcionesList)
                //binding.tvholaMundo?.setText(response.getString("Lista"))
                binding.tvAutoCompleteEditable.setAdapter(adapter)

                binding.tvAutoCompleteEditable.onItemClickListener = AdapterView.OnItemClickListener {
                        parent, view, position, id ->
                    val itemSelected = parent.getItemAtPosition(position)
                }
            }, { error ->
                Toast.makeText(requireContext(), " La aplicación no se ha conectado con el servidor", Toast.LENGTH_LONG).show()
            }
        )
        queue1.add(jsonObjectRequest1)
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
        val url1 = "http://186.64.123.248/Reportes/Almacenes/registroInsertar.php?ID_ALMACEN=$id"
        val jsonObjectRequest1 = JsonObjectRequest(
            Request.Method.GET, url1, null,
            { response ->
                TextNombre?.setText(response.getString("ALMACEN"))
                TextDireccion?.setText(response.getString("DIRECCION_ALMACEN"))
                TextDropdown?.setText(response.getString("USUARIO"),false)

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
    private fun actualizarRegistros() {
        val url1 = "http://186.64.123.248/Reportes/Almacenes/insertar.php" // Reemplaza esto con tu URL de la API
        val queue1 =Volley.newRequestQueue(requireContext())
        val stringRequest = object: StringRequest(
            Request.Method.POST,
            url1,
            { response ->
                Toast.makeText(requireContext(), "Almacén actualizado exitosamente. El id de ingreso es el número ${args.id} ", Toast.LENGTH_LONG).show()
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
                parametros.put("ID_ALMACEN", args.id)
                parametros.put("ALMACEN", TextNombre?.text.toString().uppercase())
                parametros.put("DIRECCION_ALMACEN", TextDireccion?.text.toString().uppercase())
                parametros.put("ESTADO_ALMACEN", TextEstado.toString())
                parametros.put("USUARIO", TextDropdown?.text.toString())
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

}