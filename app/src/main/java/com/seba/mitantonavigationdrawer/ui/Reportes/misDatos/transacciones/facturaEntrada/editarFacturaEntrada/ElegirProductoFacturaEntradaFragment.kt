package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.facturaEntrada.editarFacturaEntrada

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
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentElegirProductoFacturaEntradaBinding
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirInventario.AnadirInventarioFragment
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirInventario.ElegirProductoAnadirViewModel
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirTransferencia.AnadirTransferenciaFragment
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import org.json.JSONObject
import java.lang.Exception

class ElegirProductoFacturaEntradaFragment : Fragment(R.layout.fragment_elegir_producto_factura_entrada) {
    private var _binding: FragmentElegirProductoFacturaEntradaBinding? = null
    private val binding get() = _binding!!
    var DropDownProducto: AutoCompleteTextView? = null
    var TextCodigoDeBarra: EditText? = null
    private val sharedViewModel by activityViewModels<SharedViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val elegirProductoFacturaEntradaViewModel =
            ViewModelProvider(this).get(ElegirProductoFacturaEntradaViewModel::class.java)

        _binding = FragmentElegirProductoFacturaEntradaBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa
        DropDownProducto = binding.tvListaDesplegableElegirProductoFacturaEntrada.findViewById(R.id.tvListaDesplegableElegirProductoFacturaEntrada)
        TextCodigoDeBarra = binding.etCodigoDeBarraFacturaEntrada.findViewById(R.id.etCodigoDeBarraFacturaEntrada)
        // Poner los edittext con borde gris
        binding.etCantidadFacturaEntrada.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etArticulosPorCajaFacturaEntrada.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etNumeroDeCajasFacturaEntrada.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etCodigoDeBarraFacturaEntrada.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)

        ListaDesplegableElegirProducto()
        // ListaDesplegableElegirProveedor()

        binding.llCajasDeProductoElegirProductoFacturaEntrada.isVisible = false
        binding.bUnidadesFacturaEntrada.setOnClickListener {
            binding.llUnidadesElegirProductoFacturaEntrada.isVisible = false
            binding.llCajasDeProductoElegirProductoFacturaEntrada.isVisible = true

        }
        binding.bCajasDeProductoFacturaEntrada.setOnClickListener {
            binding.llCajasDeProductoElegirProductoFacturaEntrada.isVisible = false
            binding.llUnidadesElegirProductoFacturaEntrada.isVisible = true
        }

        binding.rlElegirProductoFacturaEntrada.setOnClickListener {
            binding.rlElegirProductoFacturaEntrada.isVisible = false
            val editarFacturaEntradaFragment = EditarFacturaEntradaFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.rlElegirProductoFacturaEntrada,editarFacturaEntradaFragment)
                .commit()
        }

        binding.bmasFacturaEntrada.setOnClickListener {
            val cantidadActual = binding.etCantidadFacturaEntrada.text.toString().toIntOrNull() ?: 0
            val cantidadNueva = cantidadActual + 1
            binding.etCantidadFacturaEntrada.setText(cantidadNueva.toString())
        }
        binding.bmenosFacturaEntrada.setOnClickListener {
            val cantidadActual = binding.etCantidadFacturaEntrada.text.toString().toIntOrNull() ?: 0
            val cantidadNueva = if (cantidadActual > 0) cantidadActual - 1 else 0
            binding.etCantidadFacturaEntrada.setText(cantidadNueva.toString())
        }

        binding.bAnadirFacturaEntrada.setOnClickListener {
            if(binding.tvListaDesplegableElegirProductoFacturaEntrada.text.toString() != "Eliga una opción") {
                if (binding.llUnidadesElegirProductoFacturaEntrada.isVisible) {
                    if(binding.etCantidadFacturaEntrada.text.isNotBlank()){
                        sharedViewModel.listaDeProductosAnadir.add(binding.tvListaDesplegableElegirProductoFacturaEntrada.text.toString())
                        sharedViewModel.listaDeCantidadesAnadir.add(binding.etCantidadFacturaEntrada.text.toString())
                        sharedViewModel.listaDePreciosAnadir.add(binding.etPrecioFacturaEntrada.text.toString())
                        //  refreshAdapterFacturaEntradaTransferenciaFragment()
                        binding.tvListaDesplegableElegirProductoFacturaEntrada.setText("Eliga una opción",false)
                        binding.etCantidadFacturaEntrada.setText("")
                        binding.etPrecioFacturaEntrada.setText("")
                        sharedViewModel.opcionesListEntrada.removeAll(sharedViewModel.listaDeProductosAnadir)
                        Toast.makeText(
                            requireContext(),
                            "Se ha agregado el producto a la factura",
                            Toast.LENGTH_LONG
                        ).show()

                    }else{
                        Toast.makeText(requireContext(),"Falta ingresar la cantidad", Toast.LENGTH_SHORT).show()
                    }
                } else if (binding.llCajasDeProductoElegirProductoFacturaEntrada.isVisible) {
                    if (binding.etNumeroDeCajasFacturaEntrada.text.isNotBlank() && binding.etArticulosPorCajaFacturaEntrada.text.isNotBlank()) {
                        val cantidad = binding.etNumeroDeCajasFacturaEntrada.text.toString().toInt()
                            .times(binding.etArticulosPorCajaFacturaEntrada.text.toString().toInt())
                        sharedViewModel.listaDeProductosAnadir.add(binding.tvListaDesplegableElegirProductoFacturaEntrada.text.toString())
                        sharedViewModel.listaDeCantidadesAnadir.add(cantidad.toString())
                        sharedViewModel.listaDePreciosAnadir.add(binding.etPrecioFacturaEntrada.text.toString())
                        // refreshAdapterFacturaEntradaTransferenciaFragment()
                        binding.tvListaDesplegableElegirProductoFacturaEntrada.setText("Eliga una opción",false)
                        binding.etArticulosPorCajaFacturaEntrada.setText("")
                        binding.etNumeroDeCajasFacturaEntrada.setText("")
                        binding.etPrecioFacturaEntrada.setText("")
                        sharedViewModel.opcionesListEntrada.removeAll(sharedViewModel.listaDeProductosAnadir)
                        Toast.makeText(
                            requireContext(), "Se ha agregado el producto a la factura",
                            Toast.LENGTH_LONG).show()

                    } else {
                        Toast.makeText(requireContext(), "Falta ingresar al menos los articulos por caja o el número de cajas", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "No se ha podido ingresar la factura", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(requireContext(),"No olvide elegir el producto", Toast.LENGTH_SHORT).show()
            }
        }

        binding.bVolverFacturaEntrada.setOnClickListener {
            binding.rlElegirProductoFacturaEntrada.isVisible = false
            val editarFacturaEntradaFragment = EditarFacturaEntradaFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.rlElegirProductoFacturaEntrada,editarFacturaEntradaFragment)
                .commit()
        }

        return root
    }

    private fun ListaDesplegableElegirProducto(){
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 ="http://186.64.123.248/Reportes/Transacciones/FacturaEntrada/elegirProductoCantidad.php"
        val jsonObjectRequest = object : StringRequest(
            Request.Method.POST, url1,
            { response ->
                // Obtén el array de opciones desde el objeto JSON
                val jsonArray = JSONObject(response).getJSONArray("Lista")
                // Convierte el array JSON a una lista mutable
                for (i in 0..<jsonArray.length()) {
                    if (!sharedViewModel.opcionesListEntrada.contains(jsonArray.getString(i).replace("'", ""))){
                        sharedViewModel.opcionesListEntrada.add(jsonArray.getString(i).replace("'", ""))
                    }
                }
                sharedViewModel.opcionesListEntrada.removeAll { elemento ->
                    val elementoABuscar = "${elemento.substringBefore('(')}( 0 unid. )"
                    sharedViewModel.listaDeProductosAnadir.contains(elementoABuscar)
                }
                //Crea un adpatador para el dropdown
                val adapter = ArrayAdapter(requireContext(),R.layout.list_item,sharedViewModel.opcionesListEntrada)
                //binding.tvholaMundo?.setText(response.getString("Lista"))
                DropDownProducto?.setAdapter(adapter)
                DropDownProducto?.onItemClickListener = AdapterView.OnItemClickListener {
                       parent, view, position, id ->
                        precioYCantidad(parent?.getItemAtPosition(position).toString())
                }
                binding.etCodigoDeBarraFacturaEntrada.setOnClickListener {
                    codigoDeBarra()
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (DropDownProducto?.text.toString() != "Eliga una opción") {
                            precioYCantidad(DropDownProducto?.text.toString())
                        }
                    }, 300)
                }
            },
            { error ->
                Toast.makeText(requireContext(), " La aplicación no se ha conectado con el servidor", Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("ALMACEN", sharedViewModel.almacenAnadir)
                return parametros
            }
        }
        queue1.add(jsonObjectRequest)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.CodigoDeBarraAnadir.observe(viewLifecycleOwner) { newText ->
            binding.etCodigoDeBarraFacturaEntrada.setText(newText)
            codigoDeBarra()
            Handler(Looper.getMainLooper()).postDelayed({
                if (DropDownProducto?.text.toString() != "Eliga una opción") {
                    precioYCantidad(DropDownProducto?.text.toString())
                }
            }, 300)
        }

    }

    fun precioYCantidad(producto: String){
        //setFragmentResultListener("Proveedor"){key,bundle ->
        //  sharedViewModel.ListaDeProveedoresFacturaEntrada.add(bundle.getString("Proveedor")!!) }
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 = "http://186.64.123.248/FacturaEntrada/precioYCantidad.php"
        val jsonObjectRequest1 = object : StringRequest(
            Request.Method.POST, url1,
            { response ->
                val precio = JSONObject(response).getString("Precio")
                val unidades = JSONObject(response).getString("Unidades de embalaje")
                // binding.etPrecioPorUnidadUnidades.setText("200")
                //binding.etCantidad.setText("12")
                if (binding.llCajasDeProductoElegirProductoFacturaEntrada.isVisible) {
                    binding.etArticulosPorCajaFacturaEntrada.setText(unidades)
                }
                binding.etPrecioFacturaEntrada.setText(precio)
                //binding.tvListaDesplegableElegirProveedor.setText("Eliga una opción",false)
                // binding.tvListaDesplegableElegirProducto.setText("Eliga una opción",false)

            }, { error ->
                /*if(binding.tvListaDesplegableElegirProveedor.text.toString() != "Eliga una opción" &&
            binding.tvListaDesplegableElegirProducto.text.toString() != "Eliga una opción") {*/
                cantidad(producto)
            }

            // }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                // parametros.put("PRODUCTO", DropDownProducto?.text.toString())
                parametros.put("PROVEEDOR", sharedViewModel.proveedorAnadir)
                parametros.put("PRODUCTO", producto)
                return parametros

            }
        }
        queue1.add(jsonObjectRequest1)
    }

    private fun codigoDeBarra(){
        if (binding.etCodigoDeBarraFacturaEntrada.text.isNotBlank()) {
            val queue = Volley.newRequestQueue(requireContext())
            val url = "http://186.64.123.248/FacturaEntrada/codigoDeBarraProducto.php"
            val jsonObjectRequest = object : StringRequest(
                Request.Method.POST, url,
                { response ->
                    try {
                        val codigoBarraProducto = JSONObject(response).getString("PRODUCTO")
                        val codigoBarraEmbalaje = JSONObject(response).getString("EMBALAJE")
                        if (codigoBarraEmbalaje == "") {
                            Toast.makeText(
                                requireContext(),
                                "EL código de barra pertenece al producto $codigoBarraProducto y ${sharedViewModel.opcionesListEntrada.indexOf("$codigoBarraProducto ( 0 unid. )")}",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.tvListaDesplegableElegirProductoFacturaEntrada.postDelayed({
                                binding.tvListaDesplegableElegirProductoFacturaEntrada.setSelection(sharedViewModel.opcionesListEntrada.indexOf("$codigoBarraProducto ( 0 unid. )"))
                                binding.tvListaDesplegableElegirProductoFacturaEntrada.setText("$codigoBarraProducto ( 0 unid. )",false)
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
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                { error ->
                    Toast.makeText(
                        requireContext(),
                        "No hay producto o embalaje asociado a este código de barra $error",
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                override fun getParams(): MutableMap<String, String> {
                    val parametros = HashMap<String, String>()
                    parametros.put(
                        "CODIGO_BARRA_PRODUCTO",
                        binding.etCodigoDeBarraFacturaEntrada.text.toString()
                    )
                    return parametros
                }
            }
            queue.add(jsonObjectRequest)
        }
    }

    fun cantidad(producto: String){
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 = "http://186.64.123.248/FacturaEntrada/cantidad.php"
        val jsonObjectRequest1 = object : StringRequest(
            Request.Method.POST, url1,
            { response ->
                //val precio = JSONObject(response).getString("Precio")
                val unidades = JSONObject(response).getString("Unidades de embalaje")
                // binding.etPrecioPorUnidadUnidades.setText("200")
                //binding.etCantidad.setText("12")
                if (binding.llCajasDeProductoElegirProductoFacturaEntrada.isVisible) {
                    binding.etArticulosPorCajaFacturaEntrada.setText(unidades)
                    //binding.etPrecioPorUnidadCajasDeProductos.setText("200")
                }
                //binding.tvListaDesplegableElegirProveedor.setText("Eliga una opción",false)
                // binding.tvListaDesplegableElegirProducto.setText("Eliga una opción",false)

            }, { error ->
                /*if(binding.tvListaDesplegableElegirProveedor.text.toString() != "Eliga una opción" &&
            binding.tvListaDesplegableElegirProducto.text.toString() != "Eliga una opción") {*/
                Toast.makeText(requireContext(), "No se ha podido cargar los parametros $error", Toast.LENGTH_LONG).show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}