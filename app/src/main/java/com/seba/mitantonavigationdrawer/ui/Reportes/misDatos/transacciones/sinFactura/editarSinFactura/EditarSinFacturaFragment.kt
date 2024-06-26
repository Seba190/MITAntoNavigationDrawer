package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.sinFactura.editarSinFactura

import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
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
import com.seba.mitantonavigationdrawer.databinding.FragmentEditarSinFacturaBinding
import com.seba.mitantonavigationdrawer.databinding.FragmentEstadisticaBinding
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.MisDatosFragmentArgs
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.TransaccionesFragmentArgs
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import com.seba.mitantonavigationdrawer.ui.estadística.EstadisticaViewModel

class EditarSinFacturaFragment : Fragment(R.layout.fragment_editar_sin_factura), RadioGroup.OnCheckedChangeListener {
    private var _binding: FragmentEditarSinFacturaBinding? = null
    private val binding get() = _binding!!
    private val args: TransaccionesFragmentArgs by navArgs()
    private val sharedViewModel : SharedViewModel by activityViewModels()
   // var TextNombre: EditText? = null
    var TextAlmacen: EditText? = null
    var TextProducto: EditText? = null
    var TextCantidad: EditText? = null
    var TextEstado: String? = null
    var radioGroup: RadioGroup? = null
    var radio1: RadioButton? = null
    var radio2: RadioButton? = null
    var TextFecha: EditText? = null
    var DropDownAlmacen: AutoCompleteTextView? = null
    var DropDownProducto: AutoCompleteTextView? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val editarSinFacturaViewModel =
            ViewModelProvider(this).get(EditarSinFacturaViewModel::class.java)

        _binding = FragmentEditarSinFacturaBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa
       // TextNombre = binding.etNombreSinFactura.findViewById(R.id.etNombreSinFactura)
        DropDownAlmacen = binding.tvListaDesplegableAlmacen.findViewById(R.id.tvListaDesplegableAlmacen)
        DropDownProducto = binding.tvListaDesplegableProducto.findViewById(R.id.tvListaDesplegableProducto)
        TextCantidad  =  binding.etCantidadSinFactura.findViewById(R.id.etCantidadSinFactura)
        TextFecha  =  binding.etFechaSinFactura.findViewById(R.id.etFechaSinFactura)
        radio1 = binding.SinFacturaRadioButton1.findViewById(R.id.SinFacturaRadioButton1)
        radio2 = binding.SinFacturaRadioButton2.findViewById(R.id.SinFacturaRadioButton2)
        radioGroup = binding.radioGroupSinFactura.findViewById(R.id.radioGroupSinFactura)
        radioGroup?.setOnCheckedChangeListener(this)

        //binding.etNombreSinFactura.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
         //   PorterDuff.Mode.SRC_ATOP)
        binding.etCantidadSinFactura.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etFechaSinFactura.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)

        ListaDesplegableAlmacen()
        ListaDesplegableProducto()

        binding.buttonEditarSinFactura.setOnClickListener {
            actualizarRegistros()
            Handler(Looper.getMainLooper()).postDelayed({
            modificarInventario()
            }, 2500)
        }

        try {
            sharedViewModel.id.add(args.id)
        }
        catch(e:Exception){

        }

        return root
    }

    private fun ListaDesplegableAlmacen() {
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 ="http://186.64.123.248/Reportes/Transacciones/SinFactura/listaDesplegableAlmacen.php"
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
                DropDownAlmacen?.setAdapter(adapter)

                DropDownAlmacen?.onItemClickListener = AdapterView.OnItemClickListener {
                        parent, view, position, id ->
                    val itemSelected = parent.getItemAtPosition(position)
                }
            }, { error ->
                Toast.makeText(requireContext(), " La aplicación no se ha conectado con el servidor", Toast.LENGTH_LONG).show()
            }
        )
        queue1.add(jsonObjectRequest1)
    }

    private fun ListaDesplegableProducto() {
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 ="http://186.64.123.248/Reportes/Transacciones/SinFactura/listaDesplegableProducto.php"
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
                DropDownProducto?.setAdapter(adapter)

                DropDownProducto?.onItemClickListener = AdapterView.OnItemClickListener {
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
        Log.i("Sebastian", "Valor de Id de destino es: ${sharedViewModel.id.last()}")
        //  val id2 = this.arguments
        // val id3 = id2?.get(EXTRA_ID)
        //val id4 = almacenesItemResponse.Id
        val url1 = "http://186.64.123.248/Reportes/Transacciones/SinFactura/registroInsertar.php?ID_FACTURA=${sharedViewModel.id.last()}"
        val jsonObjectRequest1 = JsonObjectRequest(
            Request.Method.GET, url1, null,
            { response ->
              //  TextNombre?.setText("Factura ${sharedViewModel.id.last()}")
                DropDownAlmacen?.setText(response.getString("ALMACEN"),false)
                DropDownProducto?.setText(response.getString("PRODUCTO"),false)
                TextCantidad?.setText(response.getString("UNIDADES"))
                TextFecha?.setText(response.getString("FECHA_FACTURA"))
                TextEstado = response.getString("TIPO_FACTURA")
                if (TextEstado == "FACTURA_ENTRADA") {
                    radio1?.isChecked = true
                } else if (TextEstado == "FACTURA_SALIDA") {
                    radio2?.isChecked = true
                }
            }, { error ->
                Toast.makeText(requireContext(), "El id es ${sharedViewModel.id.last()}", Toast.LENGTH_LONG).show()
            }
        )
        queue1.add(jsonObjectRequest1)
    }

    private fun actualizarRegistros() {
        val url1 = "http://186.64.123.248/Reportes/Transacciones/SinFactura/actualizarSinFactura.php"
        val queue1 =Volley.newRequestQueue(requireContext())
        val stringRequest = object: StringRequest(
            Request.Method.POST,
            url1,
            { response ->
                Toast.makeText(requireContext(), "Registro sin factura actualizado exitosamente. El id de ingreso es el número ${sharedViewModel.id.last()} ", Toast.LENGTH_LONG).show()
            },
            { error ->
                Toast.makeText(requireContext(),"El error es $error", Toast.LENGTH_LONG).show()
                // Toast.makeText(requireContext(),"Solo se ha podido borrar el almacen.", Toast.LENGTH_LONG).show()
            }
        )

        {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("ID_FACTURA", sharedViewModel.id.last())
                parametros.put("ALMACEN", DropDownAlmacen?.text.toString())
                parametros.put("PRODUCTO", DropDownProducto?.text.toString())
                parametros.put("UNIDADES", TextCantidad?.text.toString())
                parametros.put("TIPO_FACTURA", TextEstado.toString())
                parametros.put("FECHA_FACTURA", TextFecha?.text.toString())

                return parametros
            }
        }
        queue1.add(stringRequest)
    }

    private fun modificarInventario(){
        val url1 = "http://186.64.123.248/Reportes/Transacciones/SinFactura/modificarInventarioSinFactura.php"
        val queue1 =Volley.newRequestQueue(requireContext())
        val stringRequest = object: StringRequest(
            Request.Method.POST,
            url1,
            { response ->
                Toast.makeText(requireContext(), "Inventario actualizado exitosamente. El id de ingreso es el número ${sharedViewModel.id.last()} ", Toast.LENGTH_LONG).show()
                DropDownAlmacen?.setText("Eliga una opción", false)
                DropDownProducto?.setText("Eliga una opción", false)
                TextCantidad?.setText("")
                TextFecha?.setText("")
            },
            { error ->
                Toast.makeText(requireContext(),"El error es $error", Toast.LENGTH_LONG).show()
                // Toast.makeText(requireContext(),"Solo se ha podido borrar el almacen.", Toast.LENGTH_LONG).show()
            }
        )

        {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("ALMACEN", DropDownAlmacen?.text.toString())
                parametros.put("PRODUCTO", DropDownProducto?.text.toString())
                parametros.put("UNIDADES", TextCantidad?.text.toString())
                return parametros
            }
        }
        queue1.add(stringRequest)
    }
    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when(checkedId){
            radio1?.id -> TextEstado = "FACTURA_ENTRADA"
            radio2?.id -> TextEstado = "FACTURA_SALIDA"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}