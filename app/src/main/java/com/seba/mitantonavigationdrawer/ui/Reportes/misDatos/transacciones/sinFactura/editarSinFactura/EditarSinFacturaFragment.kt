package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.sinFactura.editarSinFactura

import android.app.AlertDialog
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
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentEditarSinFacturaBinding
import com.seba.mitantonavigationdrawer.databinding.FragmentEstadisticaBinding
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.MisDatosFragmentArgs
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.clientes.editarCliente.EditarClienteFragmentDirections
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
    var EliminarSinFactura: ImageView? = null
    var cantidadAnterior: String? = null
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
        EliminarSinFactura = binding.ivEliminarSinFactura.findViewById(R.id.ivEliminarSinFactura)
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

        EliminarSinFactura?.setOnClickListener {
            mensajeEliminarSinFactura()
        }

        try {
            sharedViewModel.id.add(args.id)
        }
        catch(e:Exception){

        }
        binding.buttonEditarSinFactura.setOnClickListener {
            if (sharedViewModel.opcionesListSinFacturaProducto.contains(DropDownProducto?.text.toString()) &&
                        sharedViewModel.opcionesListSinFacturaAlmacen.contains(DropDownAlmacen?.text.toString())) {
                actualizarRegistros()
            }else if((!sharedViewModel.opcionesListSinFacturaProducto.contains(DropDownProducto?.text.toString()) ||
                !sharedViewModel.opcionesListSinFacturaAlmacen.contains(DropDownAlmacen?.text.toString()))){
                Toast.makeText(requireContext(),"El nombre del cliente o del almacén no es válido",
                    Toast.LENGTH_SHORT).show()
            }else if(DropDownProducto?.text.toString() == "Elija una opción" ||
                DropDownAlmacen?.text.toString() == "Elija una opción"){
                Toast.makeText(requireContext(),"Debe elegir el almacén y el producto", Toast.LENGTH_SHORT).show()
            }

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
                if(sharedViewModel.opcionesListSinFacturaAlmacen.isEmpty()) {
                    for (i in 0 until jsonArray.length()) {
                        sharedViewModel.opcionesListSinFacturaAlmacen.add(jsonArray.getString(i).removeSurrounding("'", "'"))
                    }
                }
                sharedViewModel.opcionesListSinFacturaAlmacen.sort()
                //Crea un adpatador para el dropdown
                val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_dropdown_item_1line,sharedViewModel.opcionesListSinFacturaAlmacen)
                //binding.tvholaMundo?.setText(response.getString("Lista"))
                DropDownAlmacen?.setAdapter(adapter)
                DropDownAlmacen?.threshold = 1
                DropDownAlmacen?.onItemClickListener = AdapterView.OnItemClickListener {
                        parent, view, position, id ->
                    val itemSelected = parent.getItemAtPosition(position)
                }
                DropDownAlmacen?.setOnClickListener {
                    if(DropDownAlmacen?.text.toString() == "Elija una opción"){
                        binding.tvListaDesplegableAlmacen.setText("",false)
                        DropDownAlmacen?.showDropDown()
                    }
                }
                DropDownAlmacen?.setOnFocusChangeListener { _, hasFocus ->
                    if(hasFocus && DropDownAlmacen?.text.toString() == "Elija una opción"){
                        binding.tvListaDesplegableAlmacen.setText("",false)
                        DropDownAlmacen?.showDropDown()
                    }
                    else if (!hasFocus && !sharedViewModel.opcionesListSinFacturaAlmacen.contains(DropDownAlmacen?.text.toString())){
                        Toast.makeText(requireContext(),"El nombre del almacén no es válido", Toast.LENGTH_SHORT).show()
                    }
                }
            }, { error ->
                Toast.makeText(requireContext(), " La aplicación no se ha conectado con el servidor", Toast.LENGTH_SHORT).show()
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
                if(sharedViewModel.opcionesListSinFacturaProducto.isEmpty()) {
                    for (i in 0 until jsonArray.length()) {
                        sharedViewModel.opcionesListSinFacturaProducto.add(jsonArray.getString(i).removeSurrounding("'", "'"))
                    }
                }
                sharedViewModel.opcionesListSinFacturaProducto.sort()
                //Crea un adpatador para el dropdown
                val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_dropdown_item_1line,sharedViewModel.opcionesListSinFacturaProducto)
                //binding.tvholaMundo?.setText(response.getString("Lista"))
                DropDownProducto?.setAdapter(adapter)
                DropDownProducto?.threshold = 1
                DropDownProducto?.onItemClickListener = AdapterView.OnItemClickListener {
                        parent, view, position, id ->
                    val itemSelected = parent.getItemAtPosition(position)
                }
                DropDownProducto?.setOnClickListener {
                    if(DropDownProducto?.text.toString() == "Elija una opción"){
                        binding.tvListaDesplegableProducto.setText("",false)
                        DropDownProducto?.showDropDown()
                    }
                }
                DropDownProducto?.setOnFocusChangeListener { _, hasFocus ->
                    if(hasFocus && DropDownProducto?.text.toString() == "Elija una opción"){
                        binding.tvListaDesplegableProducto.setText("",false)
                        DropDownProducto?.showDropDown()
                    }
                    else if (!hasFocus && !sharedViewModel.opcionesListSinFacturaProducto.contains(DropDownProducto?.text.toString())){
                        Toast.makeText(requireContext(),"El nombre del almacén no es válido", Toast.LENGTH_SHORT).show()
                    }
                }
            }, { error ->
                Toast.makeText(requireContext(), " La aplicación no se ha conectado con el servidor", Toast.LENGTH_SHORT).show()
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
                cantidadAnterior = response.getString("UNIDADES")
                TextFecha?.setText(response.getString("FECHA_FACTURA"))
                TextEstado = response.getString("TIPO_FACTURA")
                if (TextEstado == "FACTURA_ENTRADA") {
                    radio1?.isChecked = true
                } else if (TextEstado == "FACTURA_SALIDA") {
                    radio2?.isChecked = true
                }
            }, { error ->
                Toast.makeText(requireContext(), "El id es ${sharedViewModel.id.last()}", Toast.LENGTH_SHORT).show()
            }
        )
        queue1.add(jsonObjectRequest1)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Navegar a MisDatosFragment y pasar un identificador para saber qué fragmento llamar luego
                // val action = EditarClienteFragmentDirections.actionEditarClienteFragmentToMisDatosFragment("clientes")
                val action = EditarSinFacturaFragmentDirections.actionNavEditarSinFacturaToNavTransacciones(destino = "sin factura")
                findNavController().navigate(action)
            }
        })
    }

    private fun actualizarRegistros() {
        val url1 = "http://186.64.123.248/Reportes/Transacciones/SinFactura/actualizarSinFactura.php"
        val queue1 =Volley.newRequestQueue(requireContext())
        val stringRequest = object: StringRequest(
            Request.Method.POST,
            url1,
            { response ->
                Toast.makeText(requireContext(), "Registro actualizado exitosamente. El id de ingreso es el número ${sharedViewModel.id.last()}", Toast.LENGTH_SHORT).show()
                    modificarInventario()
                Handler(Looper.getMainLooper()).postDelayed({
                    Toast.makeText(requireContext(), "Inventario actualizado exitosamente", Toast.LENGTH_SHORT).show()
                }, 3000)
            },
            { error ->
                Toast.makeText(requireContext(),"El error es $error", Toast.LENGTH_SHORT).show()
                // Toast.makeText(requireContext(),"Solo se ha podido borrar el almacen.", Toast.LENGTH_SHORT).show()
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
               /* DropDownAlmacen?.setText("Elija una opción", false)
                DropDownProducto?.setText("Elija una opción", false)
                TextCantidad?.setText("")
                TextFecha?.setText("")*/
            },
            { error ->
                Toast.makeText(requireContext(),"El error es $error", Toast.LENGTH_SHORT).show()
                // Toast.makeText(requireContext(),"Solo se ha podido borrar el almacen.", Toast.LENGTH_SHORT).show()
            }
        )

        {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("ALMACEN", DropDownAlmacen?.text.toString())
                parametros.put("PRODUCTO", DropDownProducto?.text.toString())
                parametros.put("UNIDADES", TextCantidad?.text.toString())
                parametros.put("UNIDADES_ANTERIOR", cantidadAnterior.toString())
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

    private fun mensajeEliminarSinFactura(){
        AlertDialog.Builder(context).apply {
            setTitle("¿Quieres eliminar esta factura?")
            setMessage("Esta acción no se puede deshacer.")
            setPositiveButton("Sí") { dialog, _ ->
                eliminarSinFactura()
                findNavController().navigate(R.id.action_nav_editar_sin_factura_to_nav_transacciones)
                dialog.dismiss()
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }

    }
    private fun eliminarSinFactura(){
        val url = "http://186.64.123.248/Reportes/Transacciones/SinFactura/Borrar.php"
        val queue = Volley.newRequestQueue(requireContext())
        var jsonObjectRequest = object : StringRequest(Request.Method.POST, url,
            { response ->
                Toast.makeText(requireContext(), "Factura eliminada exitosamente", Toast.LENGTH_SHORT).show()

            },{error ->
                Toast.makeText(requireContext(), "No se pudo eliminar la factura", Toast.LENGTH_SHORT).show()
            })
        {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("ID_FACTURA", sharedViewModel.id.last())
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