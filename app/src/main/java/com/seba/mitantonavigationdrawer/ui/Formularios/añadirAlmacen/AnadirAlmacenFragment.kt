package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen

import android.graphics.PorterDuff
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentAnadirAlmacenBinding
import org.json.JSONObject


class AnadirAlmacenFragment : Fragment(R.layout.fragment_anadir_almacen) {

    private var _binding: FragmentAnadirAlmacenBinding? = null


    // This property is only valid between onCreateView and
    // onDestroyView.

    private val binding get() = _binding!!
    var TextNombre: EditText? = null
    var TextDireccion: EditText? = null
    var TextEstado: Int = 1
   // var TextUsuario: EditText? = null
   // var TextId : EditText? = null
   // var CheckBox : CheckBox? = null
    var TvHolaMundo: AutoCompleteTextView? = null
   // var stringRequest: StringRequest? = null
    //var queue1 : RequestQueue? = null
    val TAG = "MyTag"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val anadirInventarioViewModel =
            ViewModelProvider(this).get(AnadirAlmacenViewModel::class.java)

        _binding = FragmentAnadirAlmacenBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //Aquí se programa

        TextNombre = binding.etNombreAlmacen.findViewById(R.id.etNombreAlmacen)
        TextDireccion = binding.etDireccionAlmacen.findViewById(R.id.etDireccionAlmacen)
       // TextUsuario = binding.etIdUsuarioResponsable.findViewById(R.id.etIdUsuarioResponsable)
       // TextId = binding.etIdAlmacen.findViewById(R.id.etIdAlmacen)
       // CheckBox = binding.cbActividad.findViewById(R.id.cbActividad)
        TvHolaMundo = binding.tvholaMundo.findViewById(R.id.tvholaMundo)

        binding.etNombreAlmacen.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etDireccionAlmacen.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)

     //   Mostrar los usuarios responsables en la lista desplegable
        ListaDesplegable()

        //Botón enviar datos
        binding.button.setOnClickListener {
            ValidacionesIdInsertarDatos()
        }






        // val items = queue1

       // val items = listOf<String>("1","2")
        //val items = queue1
       // val adapter = ArrayAdapter(requireContext(),R.layout.list_item,items)

        // binding.tvholaMundo?.setAdapter(adapter)

    /*    binding.tvholaMundo?.onItemClickListener = AdapterView.OnItemClickListener {
                parent, view, position, id ->
            val itemSelected = parent.getItemAtPosition(position)
            Toast.makeText(requireContext(), "Item: $itemSelected", Toast.LENGTH_SHORT).show()
        }*/






        //INICIO CÓDIGO BUENO!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        /*
       // Obtener el próximo id y mostrarlo en el formulario
        //Aqui tengo que presionar el boton enviar y que salga el toast con el mensaje del id actualizado
        binding.button.setOnClickListener {
        val queue =Volley.newRequestQueue(requireContext())
        val url ="http://186.64.123.248/Almacen/registro.php"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,url, null,
            { response ->
                val id = response.getString("ID_ALMACEN")
                //Aqui va el código para validar el almacen
                val url1 = "http://186.64.123.248/Almacen/insertar.php" // Reemplaza esto con tu URL de la API
                val queue1 =Volley.newRequestQueue(requireContext())
                val stringRequest = object: StringRequest(
                        Request.Method.POST,
                        url1,
                        { response ->
                            val unico = JSONObject(response).getString("ALMACEN_UNICO")
                            if (unico == "0"){
                                VolleyError("El almacen ya se encuentra en la base de datos")
                                queue1.cancelAll(TAG)
                                jsonObjectRequest1.cancel()
                                Toast.makeText(requireContext(), "El almacen ya se encuentra en la base de datos", Toast.LENGTH_LONG).show()
                            }
                           else if (unico == "1") {
                                Toast.makeText(requireContext(), "Almacen agregado exitosamente. El id de ingreso es el número $id ", Toast.LENGTH_LONG).show()
                              }
                        },
                        { error ->
                           // Toast.makeText(requireContext(),"No se ha introducido el almacen a la base de datos", Toast.LENGTH_LONG).show()
                            Toast.makeText(requireContext(),"Error $error", Toast.LENGTH_LONG).show()
                        }
                    ){
                        override fun getParams(): MutableMap<String, String> {
                            val parametros = HashMap<String, String>()
                            parametros.put("ID_ALMACEN", id.toString())
                            parametros.put("ALMACEN", TextNombre?.text.toString().uppercase())
                            parametros.put("DIRECCION_ALMACEN", TextDireccion?.text.toString().uppercase())
                            parametros.put("ESTADO_ALMACEN", TextEstado.toString())
                            parametros.put("USUARIO", TvHolaMundo?.text.toString())
                            return parametros
                        }
                    }
                    queue1.add(stringRequest)


               // TextId?.setText(response.getString("ID_ALMACEN"))
               // Toast.makeText(requireContext(),"Id ingresado correctamente al formulario.", Toast.LENGTH_LONG).show()
            }, { error ->
               // Toast.makeText(requireContext(),"Conecte la aplicación al servidor", Toast.LENGTH_LONG).show()
                Toast.makeText(requireContext(),"Error $error", Toast.LENGTH_LONG).show()
            }
        )
        queue.add(jsonObjectRequest)
            }*/
        // FIN CODIGO BUENO!!!!!!!!!!!!!!!!!!!!!!!!


         // Actividad en el estado almacen
       /* binding.cbActividad.setOnClickListener {
            val inputMethodManager = getActivity()?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
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
        }*/
        // boton actualizar el id
       /* binding.ibActualizar.setOnClickListener {
            val queue =Volley.newRequestQueue(requireContext())
            val url ="http://186.64.123.248/Almacen/registro.php"
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET,url, null,
                { response ->
                    TextId?.setText(response.getString("ID_ALMACEN"))
                    Toast.makeText(requireContext(),"Id actualizado correctamente", Toast.LENGTH_LONG).show()
                }, { error ->
                    Toast.makeText(requireContext(),"Conecte la aplicación al servidor para actualizar id", Toast.LENGTH_LONG).show()
                }
            )
            queue.add(jsonObjectRequest)
        }*/


          //Insertar los datos en la base de datos
       /* binding.button.setOnClickListener {
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
                    parametros.put("ESTADO_ALMACEN", TextEstado.text.toString())
                    parametros.put("USUARIO",TvHolaMundo?.text.toString())
                    return parametros


                }
            }
            queue.add(resultadoPost)
       }*/


        return root
    }

    private fun ListaDesplegable() {
        val queue1 =Volley.newRequestQueue(requireContext())
        val url1 ="http://186.64.123.248/Almacen/usuario.php"
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
                TvHolaMundo?.setAdapter(adapter)

                TvHolaMundo?.onItemClickListener = AdapterView.OnItemClickListener {
                        parent, view, position, id ->
                    val itemSelected = parent.getItemAtPosition(position)
                }
            }, { error ->
                Toast.makeText(requireContext(), " La aplicación no se ha conectado con el servidor", Toast.LENGTH_LONG).show()
            }
        )
        queue1.add(jsonObjectRequest1)
    }

    private fun ValidacionesIdInsertarDatos() {
        //INICIO EXPERIMIENTO!!!!!!!!!!!!!!!!!!!!!!!!!! (FUNCIONO)
        val queue =Volley.newRequestQueue(requireContext())
        val url ="http://186.64.123.248/Almacen/registro2.php"
        val jsonObjectRequest = object: StringRequest(
            Request.Method.POST,url,
            { response ->
                if(!TextNombre?.text.toString().isBlank()){
                    val id = JSONObject(response).getString("ID_ALMACEN")
                    //unico = 1
                    //no unico = 0
                    val unico = JSONObject(response).getString("ALMACEN_UNICO")
                    if (unico == "1") {
                        //Aqui va el código para validar el almacen
                        val url1 = "http://186.64.123.248/Almacen/insertar.php" // Reemplaza esto con tu URL de la API
                        val queue1 =Volley.newRequestQueue(requireContext())
                        val stringRequest = object: StringRequest(
                            Request.Method.POST,
                            url1,
                            { response ->
                                Toast.makeText(requireContext(), "Almacén agregado exitosamente. El id de ingreso es el número $id ", Toast.LENGTH_LONG).show()
                                TextNombre?.setText("")
                                TextDireccion?.setText("")
                                TvHolaMundo?.setText("Eliga una opción",false)
                            },
                            { error ->
                                Toast.makeText(requireContext(),"El usuario responsable del almacén es obligatorio", Toast.LENGTH_LONG).show()
                                //Toast.makeText(requireContext(),"Error $error", Toast.LENGTH_LONG).show()
                            }
                        )

                        {
                            override fun getParams(): MutableMap<String, String> {
                                val parametros = HashMap<String, String>()
                                parametros.put("ID_ALMACEN", id.toString())
                                parametros.put("ALMACEN", TextNombre?.text.toString().uppercase())
                                parametros.put("DIRECCION_ALMACEN", TextDireccion?.text.toString().uppercase())
                                parametros.put("ESTADO_ALMACEN", TextEstado.toString())
                                parametros.put("USUARIO", TvHolaMundo?.text.toString())
                                return parametros
                            }
                        }
                        queue1.add(stringRequest)

                    }
                    else if (unico == "0"){
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
                val parametros = HashMap<String, String>()
                parametros.put("ALMACEN", TextNombre?.text.toString().uppercase())
                return parametros
            }
        }
        queue.add(jsonObjectRequest)
    }

    //FIN EXPERIMIENTO!!!!!!!!!!!!!!!!!!!!!!!!!!! (FUNCIONO)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}