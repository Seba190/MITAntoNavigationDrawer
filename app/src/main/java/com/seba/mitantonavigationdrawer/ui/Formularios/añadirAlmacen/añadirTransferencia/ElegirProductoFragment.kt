package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirTransferencia

import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
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
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentElegirProductoBinding
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import org.json.JSONObject
import android.text.TextUtils
import kotlin.Exception

class ElegirProductoFragment : Fragment(R.layout.fragment_elegir_producto) {

    interface RefreshInterface{
        fun refreshAdapterAnadirTransferenciaFragment()
    }

    private var _binding: FragmentElegirProductoBinding? = null
    private lateinit var anadirTransferenciaUpdater: AnadirTransferenciaUpdater
    private var requestCamara: ActivityResultLauncher<String>? = null
    private lateinit var adapter: AnadirTransferenciaAdapter
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    var itemSelectedProducto : MutableList<String> = mutableListOf()
    var itemSelectedProveedor : MutableList<String> = mutableListOf()
    var DropDownProducto: AutoCompleteTextView? = null
    var TextCodigoDeBarra: EditText? = null
    var Transferencia: String? = null
    private var encontrarProducto: Boolean = false
    private val sharedViewModel by activityViewModels<SharedViewModel>()
    private var adapterProductos : ArrayAdapter<String>? = null
   // var DropDownProveedor: AutoCompleteTextView? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val elegirProductoViewModel =
            ViewModelProvider(this).get(ElegirProductoViewModel::class.java)
        _binding = FragmentElegirProductoBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa
        DropDownProducto = binding.tvListaDesplegableElegirProducto.findViewById(R.id.tvListaDesplegableElegirProducto)
        TextCodigoDeBarra = binding.etCodigoDeBarra.findViewById(R.id.etCodigoDeBarra)
        // Poner los edittext con borde gris
        binding.etCantidad.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etArticulosPorCaja.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etNumeroDeCajas.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
       binding.etCodigoDeBarra.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
           PorterDuff.Mode.SRC_ATOP)

       adapter = AnadirTransferenciaAdapter(sharedViewModel.listaDeCantidades, sharedViewModel.listaDeProductos,sharedViewModel) { position -> onDeletedItem(position)}

       if(sharedViewModel.listaDeAlmacenesTransferencia.size > 1) {
           for (i in 1..<sharedViewModel.listaDeAlmacenesTransferencia.size){
               if(sharedViewModel.listaDeAlmacenesTransferencia[i]==sharedViewModel.listaDeAlmacenesTransferencia[i-1]){
                   ListaDesplegableElegirProducto()
               }else if(sharedViewModel.listaDeAlmacenesTransferencia[i]!=sharedViewModel.listaDeAlmacenesTransferencia[i-1]){
                   sharedViewModel.opcionesListTransferencia.clear()
                   sharedViewModel.listaDeProductos.clear()
                   sharedViewModel.listaDeCantidades.clear()
                   ListaDesplegableElegirProducto()
               }
           }
       }else{
           ListaDesplegableElegirProducto()
       }

       // ListaDesplegableElegirProveedor()

        binding.llCajasDeProductoElegirProducto.isVisible = false
        binding.bUnidades.setOnClickListener {
            binding.llUnidadesElegirProducto.isVisible = false
            binding.llCajasDeProductoElegirProducto.isVisible = true

        }
        binding.bCajasDeProducto.setOnClickListener {
            binding.llCajasDeProductoElegirProducto.isVisible = false
            binding.llUnidadesElegirProducto.isVisible = true
        }

        binding.rlElegirProducto.setOnClickListener {
            binding.rlElegirProducto.isVisible = false
            val anadirTransferenciaFragment = AnadirTransferenciaFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.rlElegirProducto,anadirTransferenciaFragment)
                .commit()
            adapter.notifyDataSetChanged()
        }


            binding.bmas.setOnClickListener {
                val cantidadActual = binding.etCantidad.text.toString().toIntOrNull() ?: 0
                val cantidadNueva = cantidadActual + 1
                binding.etCantidad.setText(cantidadNueva.toString())
            }
            binding.bmenos.setOnClickListener {
                val cantidadActual = binding.etCantidad.text.toString().toIntOrNull() ?: 0
                val cantidadNueva = if (cantidadActual > 0) cantidadActual - 1 else 0
                binding.etCantidad.setText(cantidadNueva.toString())
            }

        binding.bAnadirFactura.setOnClickListener {
            encontrarProducto = sharedViewModel.opcionesListTransferencia.any { item ->
                val trimmedItem = item.substringBefore(" (")
                Log.i("Sebastian2", "${sharedViewModel.opcionesListTransferencia}, $trimmedItem, ${DropDownProducto?.text.toString().substringBefore(" (").uppercase()}")
                trimmedItem == DropDownProducto?.text.toString().substringBefore(" (").uppercase()
            }

            if(binding.llUnidadesElegirProducto.isVisible){
                if(binding.etCantidad.text.isNotEmpty()) {
                    sharedViewModel.cantidadTotalTransferencia.add(
                        binding.etCantidad.text.toString().toInt())
                }
            }else{
                if(binding.etNumeroDeCajas.text.isNotEmpty() && binding.etArticulosPorCaja.text.isNotEmpty()) {
                        val cantidad = binding.etNumeroDeCajas.text.toString().toInt()
                            .times(binding.etArticulosPorCaja.text.toString().toInt())
                        sharedViewModel.cantidadTotalTransferencia.add(cantidad)
                }
            }

                if (sharedViewModel.cantidadTotalTransferencia.sum() <= 10000000) {
                    if (encontrarProducto && DropDownProducto?.text.toString().contains("(") &&
                        DropDownProducto?.text.toString().contains(")")
                    ) {
                        val queue = Volley.newRequestQueue(requireContext())
                        val url = "http://186.64.123.248/Transferencia/preguntarInventario.php"
                        val jsonObjectRequest = object : StringRequest(
                            Request.Method.POST, url,
                            { response ->
                                try {
                                    //Si no tiene parentesis, hay que agregar (0 unid.) y si tiene se deja tal cual
                                    //Aquí si tiene parentesis, se com
                                    val cantidadUnidades =
                                        JSONObject(response).getString("Cantidad")
                                    if (binding.llUnidadesElegirProducto.isVisible) {
                                        if (binding.etCantidad.text.isNotBlank()) {
                                            if (cantidadUnidades.toInt() < binding.etCantidad.text.toString()
                                                    .toInt()
                                            ) {
                                                val inflater = requireActivity().layoutInflater
                                                val layout =
                                                    inflater.inflate(R.layout.toast_custom, null)
                                                val text =
                                                    layout.findViewById<TextView>(R.id.text_view_toast)
                                                text.text =
                                                    "La cantidad es mayor que la cantidad en inventario"
                                                val toast = Toast(requireContext())
                                                toast.duration = Toast.LENGTH_SHORT
                                                toast.view = layout
                                                toast.setGravity(Gravity.BOTTOM, 0, 600)
                                                toast.show()
                                            } else {
                                                sharedViewModel.listaDeProductos.add(
                                                    "${
                                                        DropDownProducto?.text.toString()
                                                            .substringBefore("(").uppercase()
                                                    }( 0 unid. )"
                                                )
                                                sharedViewModel.listaDeCantidades.add(binding.etCantidad.text.toString())
                                                eliminarElementoDeListaDesplegableConParentesis(
                                                    DropDownProducto?.text.toString()
                                                )
                                                someFunctionToUpdateAnadirTransferenciaFragment()
                                                sendDataToAnadirTransferenciaFragment(
                                                    sharedViewModel.listaDeCantidades,
                                                    sharedViewModel.listaDeProductos
                                                )
                                                //  refreshAdapterAnadirTransferenciaFragment()
                                                binding.tvListaDesplegableElegirProducto.setText(
                                                    "Elija una opción",
                                                    false
                                                )
                                                binding.etCodigoDeBarra.setText("")
                                                binding.etCantidad.setText("")
                                                adapter.notifyDataSetChanged()
                                                Toast.makeText(
                                                    requireContext(),
                                                    "Se ha agregado el producto a la transferencia",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        } else {
                                            Toast.makeText(
                                                requireContext(),
                                                "Falta ingresar la cantidad",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } else if (binding.llCajasDeProductoElegirProducto.isVisible) {
                                        if (binding.etNumeroDeCajas.text.isNotBlank() && binding.etArticulosPorCaja.text.isNotBlank()) {
                                            if (cantidadUnidades.toInt() < binding.etNumeroDeCajas.text.toString()
                                                    .toInt() * binding.etArticulosPorCaja.text.toString()
                                                    .toInt()
                                            ) {
                                                val inflater = requireActivity().layoutInflater
                                                val layout =
                                                    inflater.inflate(R.layout.toast_custom, null)
                                                val text =
                                                    layout.findViewById<TextView>(R.id.text_view_toast)
                                                text.text =
                                                    "La cantidad es mayor que la cantidad en inventario"
                                                val toast = Toast(requireContext())
                                                toast.duration = Toast.LENGTH_SHORT
                                                toast.view = layout
                                                toast.setGravity(Gravity.BOTTOM, 0, 600)
                                                toast.show()
                                            } else {
                                                val cantidad =
                                                    binding.etNumeroDeCajas.text.toString().toInt()
                                                        .times(
                                                            binding.etArticulosPorCaja.text.toString()
                                                                .toInt()
                                                        )
                                                sharedViewModel.listaDeProductos.add(
                                                    "${
                                                        DropDownProducto?.text.toString()
                                                            .substringBefore("(").uppercase()
                                                    }( 0 unid. )"
                                                )
                                                sharedViewModel.listaDeCantidades.add(cantidad.toString())
                                                eliminarElementoDeListaDesplegableConParentesis(
                                                    DropDownProducto?.text.toString()
                                                )
                                                someFunctionToUpdateAnadirTransferenciaFragment()
                                                sendDataToAnadirTransferenciaFragment(
                                                    sharedViewModel.listaDeCantidades,
                                                    sharedViewModel.listaDeProductos
                                                )
                                                // refreshAdapterAnadirTransferenciaFragment()
                                                binding.tvListaDesplegableElegirProducto.setText(
                                                    "Elija una opción",
                                                    false
                                                )
                                                binding.etArticulosPorCaja.setText("")
                                                binding.etNumeroDeCajas.setText("")
                                                binding.etCodigoDeBarra.setText("")
                                                adapter.notifyDataSetChanged()
                                                Toast.makeText(
                                                    requireContext(),
                                                    "Se ha agregado el producto a la transferencia",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        } else {
                                            Toast.makeText(
                                                requireContext(),
                                                "Falta ingresar al menos los articulos por caja o el número de cajas",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } else {
                                        Toast.makeText(
                                            requireContext(),
                                            "No se ha podido ingresar la factura",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                } catch (e: Exception) {
                                    Toast.makeText(
                                        requireContext(),
                                        "La excepcion es $e",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    Log.i("Sebastián", "$e")
                                }
                            },
                            { error ->
                                Toast.makeText(
                                    requireContext(),
                                    "No hay producto para transferir en el almacén de origen",
                                    Toast.LENGTH_SHORT
                                ).show()
                                binding.tvListaDesplegableElegirProducto.setText(
                                    "Elija una opción",
                                    false
                                )
                                binding.etArticulosPorCaja.setText("")
                                binding.etNumeroDeCajas.setText("")
                                binding.etCodigoDeBarra.setText("")
                                binding.etCantidad.setText("")

                                Log.i("Sebastián", "$error")
                            }) {
                            override fun getParams(): MutableMap<String, String> {
                                val parametros = HashMap<String, String>()
                                parametros.put("ALMACEN_ORIGEN", sharedViewModel.almacen)
                                parametros.put(
                                    "PRODUCTO",
                                    "${
                                        DropDownProducto?.text.toString().substringBefore('(')
                                            .uppercase()
                                    }( 0 unid. )"
                                )
                                return parametros
                            }
                        }
                        queue.add(jsonObjectRequest)
                    } else if (!encontrarProducto && DropDownProducto?.text.toString() != "Elija una opción") {
                        Toast.makeText(
                            requireContext(),
                            "El nombre del producto no es válido",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (DropDownProducto?.text.toString() == "Elija una opción") {
                        Toast.makeText(requireContext(), "Debe elegir producto", Toast.LENGTH_SHORT)
                            .show()
                    } else if (encontrarProducto && !DropDownProducto?.text.toString()
                            .contains("(") &&
                        !DropDownProducto?.text.toString().contains(")")
                    ) {
                        val queue = Volley.newRequestQueue(requireContext())
                        val url = "http://186.64.123.248/Transferencia/preguntarInventario.php"
                        val jsonObjectRequest = object : StringRequest(
                            Request.Method.POST, url,
                            { response ->
                                try {
                                    //Si no tiene parentesis, hay que agregar (0 unid.)
                                    val cantidadUnidades =
                                        JSONObject(response).getString("Cantidad")
                                    if (binding.llUnidadesElegirProducto.isVisible) {
                                        if (binding.etCantidad.text.isNotBlank()) {
                                            if (cantidadUnidades.toInt() < binding.etCantidad.text.toString()
                                                    .toInt()
                                            ) {
                                                val inflater = requireActivity().layoutInflater
                                                val layout =
                                                    inflater.inflate(R.layout.toast_custom, null)
                                                val text =
                                                    layout.findViewById<TextView>(R.id.text_view_toast)
                                                text.text =
                                                    "La cantidad es mayor que la cantidad en inventario"
                                                val toast = Toast(requireContext())
                                                toast.duration = Toast.LENGTH_SHORT
                                                toast.view = layout
                                                toast.setGravity(Gravity.BOTTOM, 0, 600)
                                                toast.show()
                                            } else {
                                                sharedViewModel.listaDeProductos.add(
                                                    "${
                                                        DropDownProducto?.text.toString()
                                                            .uppercase()
                                                    }( 0 unid. )"
                                                )
                                                sharedViewModel.listaDeCantidades.add(binding.etCantidad.text.toString())
                                                eliminarElementoDeListaDesplegableSinParentesis(
                                                    DropDownProducto?.text.toString().uppercase()
                                                )
                                                someFunctionToUpdateAnadirTransferenciaFragment()
                                                sendDataToAnadirTransferenciaFragment(
                                                    sharedViewModel.listaDeCantidades,
                                                    sharedViewModel.listaDeProductos
                                                )
                                                //  refreshAdapterAnadirTransferenciaFragment()
                                                binding.tvListaDesplegableElegirProducto.setText(
                                                    "Elija una opción",
                                                    false
                                                )
                                                binding.etCodigoDeBarra.setText("")
                                                binding.etCantidad.setText("")
                                                adapter.notifyDataSetChanged()
                                                Toast.makeText(
                                                    requireContext(),
                                                    "Se ha agregado el producto a la transferencia",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        } else {
                                            Toast.makeText(
                                                requireContext(),
                                                "Falta ingresar la cantidad",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } else if (binding.llCajasDeProductoElegirProducto.isVisible) {
                                        if (binding.etNumeroDeCajas.text.isNotBlank() && binding.etArticulosPorCaja.text.isNotBlank()) {
                                            if (cantidadUnidades.toInt() < binding.etNumeroDeCajas.text.toString()
                                                    .toInt() * binding.etArticulosPorCaja.text.toString()
                                                    .toInt()
                                            ) {
                                                val inflater = requireActivity().layoutInflater
                                                val layout =
                                                    inflater.inflate(R.layout.toast_custom, null)
                                                val text =
                                                    layout.findViewById<TextView>(R.id.text_view_toast)
                                                text.text =
                                                    "La cantidad es mayor que la cantidad en inventario"
                                                val toast = Toast(requireContext())
                                                toast.duration = Toast.LENGTH_SHORT
                                                toast.view = layout
                                                toast.setGravity(Gravity.BOTTOM, 0, 600)
                                                toast.show()
                                            } else {
                                                val cantidad =
                                                    binding.etNumeroDeCajas.text.toString().toInt()
                                                        .times(
                                                            binding.etArticulosPorCaja.text.toString()
                                                                .toInt()
                                                        )
                                                sharedViewModel.listaDeProductos.add(
                                                    "${
                                                        DropDownProducto?.text.toString()
                                                            .uppercase()
                                                    }( 0 unid. )"
                                                )
                                                sharedViewModel.listaDeCantidades.add(cantidad.toString())
                                                eliminarElementoDeListaDesplegableSinParentesis(
                                                    DropDownProducto?.text.toString().uppercase()
                                                )
                                                someFunctionToUpdateAnadirTransferenciaFragment()
                                                sendDataToAnadirTransferenciaFragment(
                                                    sharedViewModel.listaDeCantidades,
                                                    sharedViewModel.listaDeProductos
                                                )
                                                // refreshAdapterAnadirTransferenciaFragment()
                                                binding.tvListaDesplegableElegirProducto.setText(
                                                    "Elija una opción",
                                                    false
                                                )
                                                binding.etArticulosPorCaja.setText("")
                                                binding.etNumeroDeCajas.setText("")
                                                binding.etCodigoDeBarra.setText("")
                                                adapter.notifyDataSetChanged()
                                                Toast.makeText(
                                                    requireContext(),
                                                    "Se ha agregado el producto a la transferencia",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        } else {
                                            Toast.makeText(
                                                requireContext(),
                                                "Falta ingresar al menos los articulos por caja o el número de cajas",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } else {
                                        Toast.makeText(
                                            requireContext(),
                                            "No se ha podido ingresar la factura",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                } catch (e: Exception) {
                                    Toast.makeText(
                                        requireContext(),
                                        "La excepcion es $e",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    Log.i("Sebastián", "$e")
                                }
                            },
                            { error ->
                                Toast.makeText(
                                    requireContext(),
                                    "No hay producto para transferir en el almacén de origen",
                                    Toast.LENGTH_SHORT
                                ).show()
                                binding.tvListaDesplegableElegirProducto.setText(
                                    "Elija una opción",
                                    false
                                )
                                binding.etArticulosPorCaja.setText("")
                                binding.etNumeroDeCajas.setText("")
                                binding.etCodigoDeBarra.setText("")
                                binding.etCantidad.setText("")

                                Log.i("Sebastián", "$error")
                            }) {
                            override fun getParams(): MutableMap<String, String> {
                                val parametros = HashMap<String, String>()
                                parametros.put("ALMACEN_ORIGEN", sharedViewModel.almacen)
                                parametros.put(
                                    "PRODUCTO",
                                    "${DropDownProducto?.text.toString().uppercase()}( 0 unid. )"
                                )
                                return parametros
                            }
                        }
                        queue.add(jsonObjectRequest)
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "La cantidad total no puede superar los diez millones de unidades",
                        Toast.LENGTH_SHORT
                    ).show()
                    sharedViewModel.cantidadTotalTransferencia.removeLast()
                }
            }

       binding.bVolver.setOnClickListener {
           binding.rlElegirProducto.isVisible = false
           val anadirTransferenciaFragment = AnadirTransferenciaFragment()
           parentFragmentManager.beginTransaction()
               .replace(R.id.rlElegirProducto,anadirTransferenciaFragment)
               .commit()
           adapter.notifyDataSetChanged()
       }
      /* requestCamara = registerForActivityResult(ActivityResultContracts.RequestPermission(),){
           if(it){
               findNavController().navigate(R.id.action_nav_añadir_transferencia_to_nav_barcode_scan)
           }else{
               Toast.makeText(requireContext(),"Permiso denegado",Toast.LENGTH_SHORT).show()
           }
       }
       binding.bEscanearCodigoDeBarra.setOnClickListener {
           requestCamara?.launch(android.Manifest.permission.CAMERA)
       }*/


           binding.etCantidad.addTextChangedListener {
               if(binding.etCantidad.text.isNotBlank()) {
                   preguntarInventario()
               }
           }
           binding.etNumeroDeCajas.addTextChangedListener{
               if(binding.etNumeroDeCajas.text.isNotBlank()) {
                   preguntarInventario()
               }
           }
           binding.etArticulosPorCaja.addTextChangedListener {
               if(binding.etArticulosPorCaja.text.isNotBlank()) {
                   preguntarInventario()
               }
           }


        return root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        anadirTransferenciaUpdater = context as AnadirTransferenciaUpdater
    }

    private fun sendDataToAnadirTransferenciaFragment(dataCantidad: MutableList<String>, dataProducto: MutableList<String>){
        anadirTransferenciaUpdater.updateRecyclerView(dataCantidad,dataProducto)
    }
    private fun someFunctionToUpdateAnadirTransferenciaFragment(){
        val newDataCantidad = sharedViewModel.listaDeCantidades
        val newDataProducto = sharedViewModel.listaDeProductos
        sendDataToAnadirTransferenciaFragment(newDataCantidad,newDataProducto)
    }

    private fun onDeletedItem(position: Int) {
        sharedViewModel.listaDeCantidades.removeAt(position)
        sharedViewModel.listaDeProductos.removeAt(position)
        adapter.notifyItemRemoved(position)
        adapter.notifyDataSetChanged()}

   /* fun refreshAdapterAnadirTransferenciaFragment(){
        val fragmentAnadirTransferencia = requireActivity().supportFragmentManager.findFragmentById(R.id.clAnadirTransferencia) as AnadirTransferenciaFragment?
        fragmentAnadirTransferencia?.refreshAdapter()!!
    }*/

    private fun setCantidad() {
      //  binding.etCantidad.text.toString().toIntOrNull() = cantidad.toString().toIntOrNull()
    }

    private fun ListaDesplegableElegirProducto(){
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 ="http://186.64.123.248/Transferencia/elegirProductoCantidad.php"
        val jsonObjectRequest = object : StringRequest(
            Request.Method.POST, url1,
            { response ->
                // Obtén el array de opciones desde el objeto JSON
                val jsonArray = JSONObject(response).getJSONArray("Lista")
                // Convierte el array JSON a una lista mutable
                for (i in 0..<jsonArray.length()) {
                    if (!sharedViewModel.opcionesListTransferencia.contains(jsonArray.getString(i).replace("'", "")) &&
                        !sharedViewModel.listaDeProductos.contains("${jsonArray.getString(i).replace("'", "").substringBefore('(')}( 0 unid. )")) {
                        sharedViewModel.opcionesListTransferencia.add(jsonArray.getString(i).replace("'", ""))
                    }
                }
                sharedViewModel.opcionesListTransferencia.removeAll { elemento ->
                    Log.i("ListaTransferenciaInventario5", "${sharedViewModel.opcionesListTransferencia}, $elemento, ${sharedViewModel.listaDeProductos}")
                    sharedViewModel.listaDeProductos.any{it == elemento}
                }
                sharedViewModel.opcionesListTransferencia.sort()
                //Crea un adpatador para el dropdown
                adapterProductos = ArrayAdapter(requireContext(),android.R.layout.simple_dropdown_item_1line,sharedViewModel.opcionesListTransferencia)
                //binding.tvholaMundo?.setText(response.getString("Lista"))
                DropDownProducto?.setAdapter(adapterProductos)
                DropDownProducto?.threshold = 1
                DropDownProducto?.onItemClickListener = AdapterView.OnItemClickListener {
                        parent, view, position, id ->
                        binding.etArticulosPorCaja.setText("")
                        binding.etNumeroDeCajas.setText("")
                        binding.etCodigoDeBarra.setText("")
                        binding.etCantidad.setText("")
                        precioYCantidad(parent.getItemAtPosition(position).toString())
                        Handler(Looper.getMainLooper()).postDelayed({
                            preguntarInventario()
                        },200)
                    }
                binding.etCodigoDeBarra.addTextChangedListener(object: TextWatcher{
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        if(s != null && s.length == 13) {
                            codigoDeBarra()
                            Handler(Looper.getMainLooper()).postDelayed({
                                if(DropDownProducto?.text.toString().contains("(") &&
                                    DropDownProducto?.text.toString().contains(")")){
                                    binding.etArticulosPorCaja.setText("")
                                    binding.etNumeroDeCajas.setText("")
                                    binding.etCodigoDeBarra.setText("")
                                    binding.etCantidad.setText("")
                                    precioYCantidad(DropDownProducto?.text.toString())
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        preguntarInventario()
                                    },200)
                                }else if(!DropDownProducto?.text.toString().contains("(") &&
                                    !DropDownProducto?.text.toString().contains(")")){
                                    Log.i("PrecioyCantidad2","${DropDownProducto?.text.toString().uppercase()}( 0 unid. )")
                                    binding.etArticulosPorCaja.setText("")
                                    binding.etNumeroDeCajas.setText("")
                                    binding.etCodigoDeBarra.setText("")
                                    binding.etCantidad.setText("")
                                    precioYCantidad("${DropDownProducto?.text.toString().uppercase()}( 0 unid. )")
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        preguntarInventario()
                                    },200)
                                }
                            }, 300)
                        }
                    }

                })
                DropDownProducto?.setOnClickListener {
                    if(DropDownProducto?.text.toString() == "Elija una opción"){
                        binding.tvListaDesplegableElegirProducto.setText("",false)
                        DropDownProducto?.showDropDown()
                    }
                }
                DropDownProducto?.setOnFocusChangeListener { _, hasFocus ->
                    if(hasFocus && DropDownProducto?.text.toString() == "Elija una opción"){
                        binding.tvListaDesplegableElegirProducto.setText("",false)
                        DropDownProducto?.showDropDown()
                    }
                    encontrarProducto = sharedViewModel.opcionesListTransferencia.any { item ->
                        val trimmedItem = item.substringBefore(" (")
                        trimmedItem == DropDownProducto?.text.toString().substringBefore(" (").uppercase()
                    }
                    //Si es un valor no válido con los parentesis
                    if((binding.tvListaDesplegableElegirProducto.text.toString().contains("(") &&
                                binding.tvListaDesplegableElegirProducto.text.toString().contains(")"))) {
                        if (!hasFocus && !sharedViewModel.opcionesListTransferencia.contains(
                                DropDownProducto?.text.toString())) {
                            Toast.makeText(requireContext(), "El nombre del producto no es válido", Toast.LENGTH_SHORT).show()
                        }
                        //Si es un valor no válido sin los parentesis
                    }else if(!binding.tvListaDesplegableElegirProducto.text.toString().contains("(") &&
                                !binding.tvListaDesplegableElegirProducto.text.toString().contains(")")){

                         if(!hasFocus && !encontrarProducto){
                             Toast.makeText(requireContext(), "El nombre del producto no es válido", Toast.LENGTH_SHORT).show()
                         }
                        if(!hasFocus){
                            binding.etArticulosPorCaja.setText("")
                            binding.etNumeroDeCajas.setText("")
                            binding.etCodigoDeBarra.setText("")
                            binding.etCantidad.setText("")
                            precioYCantidad("${DropDownProducto?.text.toString().uppercase()}( 0 unid. )")
                            Handler(Looper.getMainLooper()).postDelayed({
                                preguntarInventario()
                            },200)
                        }
                    }
                }
            },
            { error ->
                Toast.makeText(requireContext(), " La aplicación no se ha conectado con el servidor", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("ALMACEN_ORIGEN", sharedViewModel.almacen)
                return parametros
            }
        }
        queue1.add(jsonObjectRequest)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.CodigoDeBarraTransferencia.observe(viewLifecycleOwner) { newText ->
            binding.etCodigoDeBarra.setText(newText)
            Handler(Looper.getMainLooper()).postDelayed({
                if(DropDownProducto?.text.toString().contains("(") &&
                    DropDownProducto?.text.toString().contains(")")){
                    binding.etArticulosPorCaja.setText("")
                    binding.etNumeroDeCajas.setText("")
                    binding.etCodigoDeBarra.setText("")
                    binding.etCantidad.setText("")
                    precioYCantidad(DropDownProducto?.text.toString())
                    Handler(Looper.getMainLooper()).postDelayed({
                        preguntarInventario()
                    },200)
                }else if(!DropDownProducto?.text.toString().contains("(") &&
                    !DropDownProducto?.text.toString().contains(")")){
                    Log.i("PrecioyCantidad","${DropDownProducto?.text.toString().uppercase()}( 0 unid. )")
                    binding.etArticulosPorCaja.setText("")
                    binding.etNumeroDeCajas.setText("")
                    binding.etCodigoDeBarra.setText("")
                    binding.etCantidad.setText("")
                    precioYCantidad("${DropDownProducto?.text.toString().uppercase()}( 0 unid. )")
                    Handler(Looper.getMainLooper()).postDelayed({
                        preguntarInventario()
                    },200)
                }
            }, 300)
        }
        Handler(Looper.getMainLooper()).postDelayed({
        codigoDeBarra()
        }, 100)

        }
      // codigoDeBarra()


   /* private fun ListaDesplegableElegirProveedor() {
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 ="http://186.64.123.248/Transferencia/elegirProveedor.php"
        val jsonObjectRequest1 = JsonObjectRequest(
            Request.Method.GET,url1, null,
            { response ->
                // Obtén el array de opciones desde el objeto JSON
                val jsonArray = response.getJSONArray("Lista")
                // Convierte el array JSON a una lista mutable
                val opcionesList = mutableListOf<String>()
                for (i in 0 until jsonArray.length()) {
                    opcionesList.add(jsonArray.getString(i).replace("'",""))
                }
                //Crea un adpatador para el dropdown
                val adapter = ArrayAdapter(requireContext(),R.layout.list_item,opcionesList)
                //binding.tvholaMundo?.setText(response.getString("Lista"))
                DropDownProveedor?.setAdapter(adapter)

                DropDownProveedor?.onItemClickListener = AdapterView.OnItemClickListener {
                        parent, view, position, id ->
                    itemSelectedProveedor.add(parent.getItemAtPosition(position).toString())
                }
                DropDownProveedor?.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                       // InsertarPrecioYCantidad()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        TODO("Not yet implemented")
                    }

                }
            }, { error ->
                //Toast.makeText(requireContext(), " La aplicación no se ha conectado con el servidor", Toast.LENGTH_SHORT).show()
            }
        )
        queue1.add(jsonObjectRequest1)
    }*/

    /*override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bAnadirParametros.setOnClickListener {
            precioYCantidad(DropDownProducto?.text.toString())
        }
    }*/

    fun precioYCantidad(producto: String){
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 = "http://186.64.123.248/Transferencia/precioYCantidad.php"
        val jsonObjectRequest1 = object : StringRequest(
            Request.Method.POST, url1,
            { response ->
                val precio = JSONObject(response).getString("Precio")
                val unidades = JSONObject(response).getString("Unidades de embalaje")
                // binding.etPrecioPorUnidadUnidades.setText("200")
                //binding.etCantidad.setText("12")
                if (binding.llCajasDeProductoElegirProducto.isVisible) {
                    binding.etArticulosPorCaja.setText(unidades)
                    //binding.etPrecioPorUnidadCajasDeProductos.setText("200")
                }
                //binding.tvListaDesplegableElegirProveedor.setText("Elija una opción",false)
                // binding.tvListaDesplegableElegirProducto.setText("Elija una opción",false)

            }, { error ->
                /*if(binding.tvListaDesplegableElegirProveedor.text.toString() != "Elija una opción" &&
            binding.tvListaDesplegableElegirProducto.text.toString() != "Elija una opción") {*/
                Toast.makeText(requireContext(), "No se ha podido cargar los parametros $error", Toast.LENGTH_SHORT).show()
                // binding.etCantidad.setText(unidadesEmbalaje)
                Log.i("Runtime","$error")
            }

            // }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
               // parametros.put("PRODUCTO", DropDownProducto?.text.toString())
                parametros.put("PRODUCTO", producto)
                return parametros

            }
        }
        queue1.add(jsonObjectRequest1)
    }

    private fun codigoDeBarra(){
            if (binding.etCodigoDeBarra.text.isNotBlank()) {
                val queue = Volley.newRequestQueue(requireContext())
                val url = "http://186.64.123.248/Transferencia/codigoDeBarraProducto.php"
                val jsonObjectRequest = object : StringRequest(
                    Request.Method.POST, url,
                    { response ->
                        try {
                            val codigoBarraProducto = JSONObject(response).getString("PRODUCTO")
                            val codigoBarraEmbalaje = JSONObject(response).getString("EMBALAJE")
                            if (codigoBarraEmbalaje == "") {
                                Toast.makeText(
                                    requireContext(),
                                    "EL código de barra pertenece al producto $codigoBarraProducto",
                                    Toast.LENGTH_SHORT
                                ).show()
                                binding.tvListaDesplegableElegirProducto.postDelayed({
                                    binding.tvListaDesplegableElegirProducto.setSelection(sharedViewModel.opcionesListTransferencia.indexOf("$codigoBarraProducto ( 0 unid. )"))
                                    binding.tvListaDesplegableElegirProducto.setText("$codigoBarraProducto ( 0 unid. )",false)
                                },100)

                                // DropDownProducto?.performClick()


                                //DropDownProducto?.setSelection(opcionesList.indexOf("$codigoBarraProducto ( 0 unid. )"))


                            } else if (codigoBarraProducto == "") {
                                Toast.makeText(
                                    requireContext(),
                                    "Código de barra de embalaje que pertenece al producto $codigoBarraEmbalaje",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        catch(e: Exception){
                            Toast.makeText(requireContext(), "No hay producto o embalaje asociado a este código de barra",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    { error ->
                        Toast.makeText(requireContext(),
                            "No hay producto o embalaje asociado a este código de barra",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                    override fun getParams(): MutableMap<String, String> {
                        val parametros = HashMap<String, String>()
                        parametros.put("CODIGO_BARRA_PRODUCTO", binding.etCodigoDeBarra.text.toString())
                        return parametros
                    }
                }
                queue.add(jsonObjectRequest)
            }
    }

    fun preguntarInventario(){
        if (DropDownProducto?.text.toString().contains("(") &&
            DropDownProducto?.text.toString().contains(")")
        ) {
        val queue = Volley.newRequestQueue(requireContext())
        val url = "http://186.64.123.248/Transferencia/preguntarInventario.php"
        val jsonObjectRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                try {
                    val cantidad = JSONObject(response).getString("Cantidad")
                    if(binding.llUnidadesElegirProducto.isVisible) {
                        Log.i(
                            "CeroTransferencia",
                            "${cantidad.toInt()}"
                        )
                        if(binding.etCantidad.text.isNotBlank()) {
                            if (cantidad.toInt() < binding.etCantidad.text.toString().toInt()) {
                                val inflater = requireActivity().layoutInflater
                                val layout = inflater.inflate(R.layout.toast_custom, null)
                                val text = layout.findViewById<TextView>(R.id.text_view_toast)
                                text.text = "La cantidad es mayor que la cantidad en inventario"
                                val toast = Toast(requireContext())
                                toast.duration = Toast.LENGTH_SHORT
                                toast.view = layout
                                toast.setGravity(Gravity.BOTTOM, 0, 600)
                                toast.show()
                                binding.tvListaDesplegableElegirProducto.setText(
                                    "Elija una opción",
                                    false
                                )
                                binding.etArticulosPorCaja.setText("")
                                binding.etNumeroDeCajas.setText("")
                                binding.etCodigoDeBarra.setText("")
                                binding.etCantidad.setText("")
                            }
                        }else {
                            if (cantidad.toInt() == 0
                            ) {
                                Log.i(
                                    "CeroDentroConPar",
                                    "${cantidad.toInt()}"
                                )
                                val inflaterRemover = requireActivity().layoutInflater
                                val layoutRemover =
                                    inflaterRemover.inflate(
                                        R.layout.toast_custom_remover,
                                        null
                                    )
                                val textRemover =
                                    layoutRemover.findViewById<TextView>(R.id.text_view_toast_remover)
                                textRemover.text =
                                    "No existe inventario en este almacén"
                                val toast = Toast(requireContext())
                                toast.duration = Toast.LENGTH_SHORT
                                toast.view = layoutRemover
                                toast.setGravity(Gravity.BOTTOM, 0, 600)
                                toast.show()
                                binding.tvListaDesplegableElegirProducto.setText(
                                    "Elija una opción",
                                    false
                                )
                                Handler(Looper.getMainLooper()).postDelayed({
                                    binding.etArticulosPorCaja.setText("")
                                    binding.etNumeroDeCajas.setText("")
                                    binding.etCodigoDeBarra.setText("")
                                    binding.etCantidad.setText("")
                                },300)
                            }
                        }
                    }
                    else if(binding.llCajasDeProductoElegirProducto.isVisible) {
                        if(binding.etNumeroDeCajas.text.isNotBlank() && binding.etArticulosPorCaja.text.isNotBlank()) {
                            if (cantidad.toInt() < binding.etNumeroDeCajas.text.toString().toInt()
                                    .times(binding.etArticulosPorCaja.text.toString().toInt())
                            ) {
                                val inflater = requireActivity().layoutInflater
                                val layout = inflater.inflate(R.layout.toast_custom, null)
                                val text = layout.findViewById<TextView>(R.id.text_view_toast)
                                text.text = "La cantidad es mayor que la cantidad en inventario"
                                val toast = Toast(requireContext())
                                toast.duration = Toast.LENGTH_SHORT
                                toast.view = layout
                                toast.setGravity(Gravity.BOTTOM, 0, 600)
                                toast.show()
                                binding.tvListaDesplegableElegirProducto.setText(
                                    "Elija una opción",
                                    false
                                )
                                binding.etArticulosPorCaja.setText("")
                                binding.etNumeroDeCajas.setText("")
                                binding.etCodigoDeBarra.setText("")
                                binding.etCantidad.setText("")
                            }
                        } else {
                            if (cantidad.toInt() == 0) {
                                Log.i(
                                    "CeroDentroCajas",
                                    "${cantidad.toInt()}"
                                )
                                val inflaterRemover = requireActivity().layoutInflater
                                val layoutRemover =
                                    inflaterRemover.inflate(
                                        R.layout.toast_custom_remover,
                                        null
                                    )
                                val textRemover =
                                    layoutRemover.findViewById<TextView>(R.id.text_view_toast_remover)
                                textRemover.text =
                                    "No existe inventario en este almacén"
                                val toast = Toast(requireContext())
                                toast.duration = Toast.LENGTH_SHORT
                                toast.view = layoutRemover
                                toast.setGravity(Gravity.BOTTOM, 0, 600)
                                toast.show()
                                binding.tvListaDesplegableElegirProducto.setText(
                                    "Elija una opción",
                                    false
                                )
                                Handler(Looper.getMainLooper()).postDelayed({
                                    binding.etArticulosPorCaja.setText("")
                                    binding.etCantidad.setText("")
                                    binding.etNumeroDeCajas.setText("")
                                    binding.etCodigoDeBarra.setText("")
                                }, 300)

                            }
                        }
                    }
                }
                catch(e: Exception){
                    Toast.makeText(requireContext(), "La excepcion es $e",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.i("Sebastián", "$e")
                }
            },
            { error ->
              //  Toast.makeText(requireContext(), "El error es $error", Toast.LENGTH_SHORT).show()
                Log.i("Sebastián", "$error")
            }) {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("ALMACEN_ORIGEN", sharedViewModel.almacen)
                parametros.put("PRODUCTO", DropDownProducto?.text.toString())
                return parametros
            }
        }
        queue.add(jsonObjectRequest)
        }else if (!DropDownProducto?.text.toString().contains("(") &&
            !DropDownProducto?.text.toString().contains(")")
        ) {
            val queue = Volley.newRequestQueue(requireContext())
            val url = "http://186.64.123.248/Transferencia/preguntarInventario.php"
            val jsonObjectRequest = object : StringRequest(
                Request.Method.POST, url,
                { response ->
                    try {
                        val cantidad = JSONObject(response).getString("Cantidad")
                        if(binding.llUnidadesElegirProducto.isVisible) {
                            if(binding.etCantidad.text.isNotBlank()) {
                                if (cantidad.toInt() < binding.etCantidad.text.toString().toInt()) {
                                    val inflater = requireActivity().layoutInflater
                                    val layout = inflater.inflate(R.layout.toast_custom, null)
                                    val text = layout.findViewById<TextView>(R.id.text_view_toast)
                                    text.text = "La cantidad es mayor que la cantidad en inventario"
                                    val toast = Toast(requireContext())
                                    toast.duration = Toast.LENGTH_SHORT
                                    toast.view = layout
                                    toast.setGravity(Gravity.BOTTOM, 0, 600)
                                    toast.show()
                                    binding.tvListaDesplegableElegirProducto.setText(
                                        "Elija una opción",
                                        false
                                    )
                                    binding.etArticulosPorCaja.setText("")
                                    binding.etNumeroDeCajas.setText("")
                                    binding.etCodigoDeBarra.setText("")
                                    binding.etCantidad.setText("")
                                }
                            }else {
                                if (cantidad.toInt() == 0) {
                                    val inflaterRemover = requireActivity().layoutInflater
                                    val layoutRemover =
                                        inflaterRemover.inflate(
                                            R.layout.toast_custom_remover,
                                            null
                                        )
                                    val textRemover =
                                        layoutRemover.findViewById<TextView>(R.id.text_view_toast_remover)
                                    textRemover.text =
                                        "No existe inventario en este almacén"
                                    val toast = Toast(requireContext())
                                    toast.duration = Toast.LENGTH_SHORT
                                    toast.view = layoutRemover
                                    toast.setGravity(Gravity.BOTTOM, 0, 600)
                                    toast.show()
                                    binding.tvListaDesplegableElegirProducto.setText(
                                        "Elija una opción",
                                        false
                                    )
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        binding.etArticulosPorCaja.setText("")
                                        binding.etCantidad.setText("")
                                        binding.etNumeroDeCajas.setText("")
                                        binding.etCodigoDeBarra.setText("")
                                    }, 300)
                                }
                            }
                        }
                        else if(binding.llCajasDeProductoElegirProducto.isVisible) {
                            if(binding.etNumeroDeCajas.text.isNotBlank() && binding.etArticulosPorCaja.text.isNotBlank()) {
                                if (cantidad.toInt() < binding.etNumeroDeCajas.text.toString().toInt()
                                        .times(binding.etArticulosPorCaja.text.toString().toInt())
                                ) {
                                    val inflater = requireActivity().layoutInflater
                                    val layout = inflater.inflate(R.layout.toast_custom, null)
                                    val text = layout.findViewById<TextView>(R.id.text_view_toast)
                                    text.text = "La cantidad es mayor que la cantidad en inventario"
                                    val toast = Toast(requireContext())
                                    toast.duration = Toast.LENGTH_SHORT
                                    toast.view = layout
                                    toast.setGravity(Gravity.BOTTOM, 0, 600)
                                    toast.show()
                                    binding.tvListaDesplegableElegirProducto.setText(
                                        "Elija una opción",
                                        false
                                    )
                                    binding.etArticulosPorCaja.setText("")
                                    binding.etNumeroDeCajas.setText("")
                                    binding.etCodigoDeBarra.setText("")
                                    binding.etCantidad.setText("")
                                }
                            } else {
                                if (cantidad.toInt() == 0
                                ) {
                                    Log.i(
                                        "CeroDentroCajasSinParentesis",
                                        "${cantidad.toInt()}"
                                    )
                                    val inflaterRemover = requireActivity().layoutInflater
                                    val layoutRemover =
                                        inflaterRemover.inflate(
                                            R.layout.toast_custom_remover,
                                            null
                                        )
                                    val textRemover =
                                        layoutRemover.findViewById<TextView>(R.id.text_view_toast_remover)
                                    textRemover.text =
                                        "No existe inventario en este almacén"
                                    val toast = Toast(requireContext())
                                    toast.duration = Toast.LENGTH_SHORT
                                    toast.view = layoutRemover
                                    toast.setGravity(Gravity.BOTTOM, 0, 600)
                                    toast.show()
                                    binding.tvListaDesplegableElegirProducto.setText(
                                        "Elija una opción",
                                        false
                                    )
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        binding.etArticulosPorCaja.setText("")
                                        binding.etCantidad.setText("")
                                        binding.etNumeroDeCajas.setText("")
                                        binding.etCodigoDeBarra.setText("")
                                    }, 300)
                                }
                            }
                        }
                    }
                    catch(e: Exception){
                        Toast.makeText(requireContext(), "La excepcion es $e",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.i("Sebastián", "$e")
                    }
                },
                { error ->
                    //  Toast.makeText(requireContext(), "El error es $error", Toast.LENGTH_SHORT).show()
                    Log.i("Sebastián", "$error")
                }) {
                override fun getParams(): MutableMap<String, String> {
                    val parametros = HashMap<String, String>()
                    parametros.put("ALMACEN_ORIGEN", sharedViewModel.almacen)
                    parametros.put("PRODUCTO", "${DropDownProducto?.text.toString().uppercase()}( 0 unid. )")
                    return parametros
                }
            }
            queue.add(jsonObjectRequest)
        }
    }

    fun eliminarElementoDeListaDesplegableSinParentesis(nombre: String) {
        // Filtra la lista para encontrar el elemento a eliminar
        val itemToRemove = sharedViewModel.opcionesListTransferencia.find { it.startsWith(nombre) }
        Log.i("eliminarElementoDeListaDesplegable","$itemToRemove")
        // Si se encuentra el elemento, eliminarlo
        if (itemToRemove != null) {
            sharedViewModel.opcionesListTransferencia.remove(itemToRemove)
            // Notificar al adaptador que los datos han cambiado
            adapterProductos = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sharedViewModel.opcionesListTransferencia)
            DropDownProducto?.threshold = 1
            DropDownProducto?.setAdapter(adapterProductos)
            adapterProductos!!.notifyDataSetChanged()

        }

    }

    fun eliminarElementoDeListaDesplegableConParentesis(nombre: String) {
        // Filtra la lista para encontrar el elemento a eliminar
        val itemToRemove = nombre
        // Si se encuentra el elemento, eliminarlo
        sharedViewModel.opcionesListTransferencia.remove(itemToRemove)
        // Notificar al adaptador que los datos han cambiado
        adapterProductos = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sharedViewModel.opcionesListTransferencia)
        DropDownProducto?.threshold = 1
        DropDownProducto?.setAdapter(adapterProductos)
        adapterProductos!!.notifyDataSetChanged()

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}