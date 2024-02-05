package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirTransferencia

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI.navigateUp
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.seba.mitantonavigationdrawer.MainActivity
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentAnadirTransferenciaBinding
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto.AlertasAlmacenesFragment
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import org.json.JSONObject



class AnadirTransferenciaFragment : Fragment(R.layout.fragment_anadir_transferencia) {

    companion object {
        const val CODIGO_DE_BARRA = "CODIGO_DE_BARRA"
    }


    private val viewModel by activityViewModels<SharedViewModel>()
    private var _binding: FragmentAnadirTransferenciaBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    var TextNombre: EditText? = null
    var TextFecha: EditText? = null
    var TextComentarios: EditText? = null
    var DropDownOrigen: AutoCompleteTextView? = null
    var DropDownDestino: AutoCompleteTextView? = null
    private var requestCamara:ActivityResultLauncher<String>? = null
    private var CodigoDeBarra: EditText? = null
    private val ListaDeProductos : MutableList<String> = mutableListOf()
    private val ListaDeCantidades : MutableList<String> = mutableListOf()
    private val ListaDePreciosUnidad : MutableList<String> = mutableListOf()
    private val ListaDePreciosCajas : MutableList<String> = mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val anadirTransferenciaViewModel =
            ViewModelProvider(this).get(AnadirTransferenciaViewModel::class.java)

        _binding = FragmentAnadirTransferenciaBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa
        //Definir los editText
        TextNombre = binding.etNombreTransferencia.findViewById(R.id.etNombreTransferencia)
        TextFecha = binding.etFechaTransferencia.findViewById(R.id.etFechaTransferencia)
        TextComentarios = binding.etTransferenciaComentarios.findViewById(R.id.etTransferenciaComentarios)
        DropDownOrigen = binding.tvListaDesplegableAlmacenOrigen.findViewById(R.id.tvListaDesplegableAlmacenOrigen)
        DropDownDestino = binding.tvListaDesplegableAlmacenDestino.findViewById(R.id.tvListaDesplegableAlmacenDestino)

        //Poner los edit text con sombra gris
        binding.etNombreTransferencia.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etFechaTransferencia.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etTransferenciaComentarios.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etCodigoDeBarra.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)

        ListaDesplegableOrigen()
        ListaDesplegableDestino()

        binding.bAnadirNuevoProducto.setOnClickListener {
                val elegirProductoFragment = ElegirProductoFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.clAnadirTransferencia,elegirProductoFragment)
                    .commit()}



        binding.TransferenciaButtonEnviar.setOnClickListener {
           // binding.etCodigoDeBarra.setText(args.code)
            ValidacionesIdInsertarDatos()
        }

        requestCamara = registerForActivityResult(ActivityResultContracts.RequestPermission(),){
            if(it){
             findNavController().navigate(R.id.action_nav_añadir_transferencia_to_nav_barcode_scan)
            }else{
                Toast.makeText(requireContext(),"Permiso denegado",Toast.LENGTH_LONG).show()
            }
        }
        binding.bEscanearCodigoDeBarra.setOnClickListener {
            requestCamara?.launch(android.Manifest.permission.CAMERA)
        }

       //  CodigoDeBarra = activity?.findViewById(R.id.txtBarcodeValue)
       // if(CodigoDeBarra?.text!!.isNullOrBlank()){

        //    binding.etCodigoDeBarra.setText(CodigoDeBarra!!.text)

       // }
       // var EditTextEmpty = binding.etCodigoDeBarra.text.toString()
       // val action = AnadirTransferenciaFragmentDirections.actionNavAñadirTransferenciaToNavBarcodeScan(EditTextEmpty)
       // findNavController().navigate(action)
        parentFragmentManager.setFragmentResultListener("Codigo de barra transferencia",this){
                key,bundle ->
            binding.bAnadirNuevoProducto.setOnClickListener {
                binding.etCodigoDeBarra.setText(bundle.getString("codigo"))
            }
        }

        return root
    }

    private fun ListaDesplegableDestino() {
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 ="http://186.64.123.248/Transferencia/almacenDestino.php"
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
                DropDownDestino?.setAdapter(adapter)

                DropDownDestino?.onItemClickListener = AdapterView.OnItemClickListener {
                        parent, view, position, id ->
                    val itemSelected = parent.getItemAtPosition(position)
                }
            }, { error ->
                Toast.makeText(requireContext(), " La aplicación no se ha conectado con el servidor", Toast.LENGTH_LONG).show()
            }
        )
        queue1.add(jsonObjectRequest1)
    }

    private fun ListaDesplegableOrigen() {
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 ="http://186.64.123.248/Transferencia/almacenOrigen.php"
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
                DropDownOrigen?.setAdapter(adapter)

                DropDownOrigen?.onItemClickListener = AdapterView.OnItemClickListener {
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
        setFragmentResultListener("ElegirProducto1"){key,bundle ->
            ListaDeCantidades.add(bundle.getString("Cantidad")!!)
            ListaDePreciosUnidad.add(bundle.getString("PrecioUnidades")!!)
            ListaDeProductos.add(bundle.getString("Producto")!!)}

        setFragmentResultListener("ElegirProducto2"){key,bundle ->
            ListaDeCantidades.add(bundle.getString("Cantidad")!!)
            ListaDePreciosCajas.add(bundle.getString("PrecioCajas")!!)
            ListaDeProductos.add(bundle.getString("Producto")!!)}
        val queue =Volley.newRequestQueue(requireContext())
        val url ="http://186.64.123.248/Transferencia/registro.php"
        val jsonObjectRequest = object: StringRequest(
            Request.Method.POST,url,
            { response ->
                if(!TextNombre?.text.toString().isBlank()){
                    val id = JSONObject(response).getString("ID_TRANSFERENCIA")
                    //unico = 1
                    //no unico = 0
                    val unico = JSONObject(response).getString("TRANSFERENCIA_UNICA")
                    if (unico == "1") {
                        //Aqui va el código para validar el almacen
                        val url1 = "http://186.64.123.248/Transferencia/insertar.php" // Reemplaza esto con tu URL de la API
                        val queue1 =Volley.newRequestQueue(requireContext())
                        val stringRequest = object: StringRequest(
                            Request.Method.POST,
                            url1,
                            { response ->
                                Toast.makeText(requireContext(), "Transferencia agregada exitosamente. El id de ingreso es el número $id ", Toast.LENGTH_LONG).show()
                                TextNombre?.setText("")
                                TextFecha?.setText("")
                                DropDownOrigen?.setText("Eliga una opción",false)
                                DropDownDestino?.setText("Eliga una opción",false)
                                TextComentarios?.setText("")
                            },
                            { error ->
                                Toast.makeText(requireContext(),"$error", Toast.LENGTH_LONG).show()
                                //Toast.makeText(requireContext(),"Error $error", Toast.LENGTH_LONG).show()
                            }
                        )

                        {
                            override fun getParams(): MutableMap<String, String> {
                                val parametros = HashMap<String, String>()
                                parametros.put("ID_TRANSFERENCIA", id.toString())
                                parametros.put("TRANSFERENCIA", TextNombre?.text.toString().uppercase())
                                parametros.put("FECHA_TRANSFERENCIA", TextFecha?.text.toString().uppercase())
                                parametros.put("ALMACEN_ORIGEN", DropDownOrigen?.text.toString())
                                parametros.put("ALMACEN_DESTINO", DropDownDestino?.text.toString())
                                parametros.put("COMENTARIOS", TextComentarios?.text.toString().uppercase())
                                parametros.put("PRODUCTO", ListaDeProductos[0].uppercase())
                                parametros.put("CANTIDAD",ListaDeCantidades[0])
                                if(ListaDePreciosUnidad.isEmpty()){
                                    parametros.put("PRECIO", ListaDePreciosCajas[0])
                                }else if(ListaDePreciosCajas.isEmpty()) {
                                    parametros.put("PRECIO", ListaDePreciosUnidad[0])
                                }


                                return parametros
                            }
                        }
                        queue1.add(stringRequest)

                    }
                    else if (unico == "0"){
                        //VolleyError("El almacén ya se encuentra en la base de datos")
                        //queue1.cancelAll(TAG)
                        //jsonObjectRequest1.cancel()
                        Toast.makeText(requireContext(), "La transferencia ya se encuentra en la base de datos", Toast.LENGTH_LONG).show()
                    }
                }
                else{
                    Toast.makeText(requireContext(), "El nombre de la transferencia es obligatorio", Toast.LENGTH_LONG).show()
                }

                // TextId?.setText(response.getString("ID_ALMACEN"))
                // Toast.makeText(requireContext(),"Id ingresado correctamente al formulario.", Toast.LENGTH_LONG).show()
            }, { error ->
                 Toast.makeText(requireContext(),"Conecte la aplicación al servidor", Toast.LENGTH_LONG).show()
                //Toast.makeText(requireContext(),"Error $error", Toast.LENGTH_LONG).show()
            }
        ){
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("TRANSFERENCIA", TextNombre?.text.toString().uppercase())
                return parametros
            }
        }
        queue.add(jsonObjectRequest)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun obtenerMiView(): View {
        return binding.etNombreTransferencia // Tu vista que quieres acceder
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.CodigoDeBarraTransferencia.observe(viewLifecycleOwner) { newText ->
            binding.etCodigoDeBarra.setText(newText)
        }
    }


}

    // En el FragmentDestino o cualquier fragmento que desees personalizar

