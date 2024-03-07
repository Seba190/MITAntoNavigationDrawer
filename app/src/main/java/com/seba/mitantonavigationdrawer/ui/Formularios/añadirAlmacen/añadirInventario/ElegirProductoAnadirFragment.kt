package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirInventario

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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentElegirProductoAnadirBinding
import com.seba.mitantonavigationdrawer.databinding.FragmentEstadisticaBinding
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirTransferencia.AnadirTransferenciaFragment
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import com.seba.mitantonavigationdrawer.ui.estadística.EstadisticaViewModel
import org.json.JSONObject
import java.lang.Exception

class ElegirProductoAnadirFragment : Fragment(R.layout.fragment_elegir_producto_anadir) {
    private var _binding: FragmentElegirProductoAnadirBinding? = null
    private val binding get() = _binding!!
    var DropDownProducto: AutoCompleteTextView? = null
    var TextCodigoDeBarra: EditText? = null
    private val sharedViewModel by activityViewModels<SharedViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val elegirProductoAnadirViewModel =
            ViewModelProvider(this).get(ElegirProductoAnadirViewModel::class.java)

        _binding = FragmentElegirProductoAnadirBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa
        DropDownProducto = binding.tvListaDesplegableElegirProductoAnadir.findViewById(R.id.tvListaDesplegableElegirProductoAnadir)
        TextCodigoDeBarra = binding.etCodigoDeBarraAnadir.findViewById(R.id.etCodigoDeBarraAnadir)
        // Poner los edittext con borde gris
        binding.etCantidadAnadir.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etArticulosPorCajaAnadir.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etNumeroDeCajasAnadir.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etCodigoDeBarraAnadir.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)

        ListaDesplegableElegirProducto()
        // ListaDesplegableElegirProveedor()

        binding.llCajasDeProductoElegirProductoAnadir.isVisible = false
        binding.bUnidadesAnadir.setOnClickListener {
            binding.llUnidadesElegirProductoAnadir.isVisible = false
            binding.llCajasDeProductoElegirProductoAnadir.isVisible = true

        }
        binding.bCajasDeProductoAnadir.setOnClickListener {
            binding.llCajasDeProductoElegirProductoAnadir.isVisible = false
            binding.llUnidadesElegirProductoAnadir.isVisible = true
        }

        binding.rlElegirProductoAnadir.setOnClickListener {
            binding.rlElegirProductoAnadir.isVisible = false
            val anadirInventarioFragment = AnadirInventarioFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.rlElegirProductoAnadir,anadirInventarioFragment)
                .commit()
        }

        binding.bmasAnadir.setOnClickListener {
            val cantidadActual = binding.etCantidadAnadir.text.toString().toIntOrNull() ?: 0
            val cantidadNueva = cantidadActual + 1
            binding.etCantidadAnadir.setText(cantidadNueva.toString())
        }
        binding.bmenosAnadir.setOnClickListener {
            val cantidadActual = binding.etCantidadAnadir.text.toString().toIntOrNull() ?: 0
            val cantidadNueva = if (cantidadActual > 0) cantidadActual - 1 else 0
            binding.etCantidadAnadir.setText(cantidadNueva.toString())
        }

        binding.bAnadirFacturaAnadir.setOnClickListener {
            if(binding.tvListaDesplegableElegirProductoAnadir.text.toString() != "Eliga una opción") {
                if (binding.llUnidadesElegirProductoAnadir.isVisible) {
                    if(binding.etCantidadAnadir.text.isNotBlank()){
                        sharedViewModel.listaDeProductosAnadir.add(binding.tvListaDesplegableElegirProductoAnadir.text.toString())
                        sharedViewModel.listaDeCantidadesAnadir.add(binding.etCantidadAnadir.text.toString())
                        sharedViewModel.listaDePreciosAnadir.add(binding.etPrecioAnadir.text.toString())
                        //  refreshAdapterAnadirTransferenciaFragment()
                        binding.tvListaDesplegableElegirProductoAnadir.setText("Eliga una opción",false)
                        binding.etCantidadAnadir.setText("")
                        binding.etPrecioAnadir.setText("")
                        Toast.makeText(
                            requireContext(),
                            "Se ha agregado el producto a la factura",
                            Toast.LENGTH_LONG
                        ).show()

                    }else{
                        Toast.makeText(requireContext(),"Falta ingresar la cantidad", Toast.LENGTH_SHORT).show()
                    }
                } else if (binding.llCajasDeProductoElegirProductoAnadir.isVisible) {
                    if (binding.etNumeroDeCajasAnadir.text.isNotBlank() && binding.etArticulosPorCajaAnadir.text.isNotBlank()) {
                        val cantidad = binding.etNumeroDeCajasAnadir.text.toString().toInt()
                            .times(binding.etArticulosPorCajaAnadir.text.toString().toInt())
                        sharedViewModel.listaDeProductosAnadir.add(binding.tvListaDesplegableElegirProductoAnadir.text.toString())
                        sharedViewModel.listaDeCantidadesAnadir.add(cantidad.toString())
                        sharedViewModel.listaDePreciosAnadir.add(binding.etPrecioAnadir.text.toString())
                        // refreshAdapterAnadirTransferenciaFragment()
                        binding.tvListaDesplegableElegirProductoAnadir.setText("Eliga una opción",false)
                        binding.etArticulosPorCajaAnadir.setText("")
                        binding.etNumeroDeCajasAnadir.setText("")
                        binding.etPrecioAnadir.setText("")
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

        binding.bVolverAnadir.setOnClickListener {
            binding.rlElegirProductoAnadir.isVisible = false
            val anadirInventarioFragment = AnadirInventarioFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.rlElegirProductoAnadir,anadirInventarioFragment)
                .commit()
        }

        return root
    }

    val opcionesList = mutableListOf<String>()
    private fun ListaDesplegableElegirProducto() {
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 ="http://186.64.123.248/FacturaEntrada/elegirProducto.php"
        val jsonObjectRequest1 = JsonObjectRequest(
            Request.Method.GET,url1, null,
            { response ->
                // Obtén el array de opciones desde el objeto JSON
                val jsonArray = response.getJSONArray("Lista")
                // Convierte el array JSON a una lista mutable
                for (i in 0 until jsonArray.length()) {
                    opcionesList.add(jsonArray.getString(i).replace("'",""))
                }
                //Crea un adpatador para el dropdown
                val adapter = ArrayAdapter(requireContext(),R.layout.list_item,opcionesList)
                //binding.tvholaMundo?.setText(response.getString("Lista"))
                DropDownProducto?.setAdapter(adapter)
                DropDownProducto?.onItemClickListener = AdapterView.OnItemClickListener {
                        parent, view, position, id ->
                    if(binding.llCajasDeProductoElegirProductoAnadir.isVisible){
                        precioYCantidad(parent.getItemAtPosition(position).toString())
                    }
                }
                binding.etCodigoDeBarraAnadir.setOnClickListener {
                    codigoDeBarra()
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (DropDownProducto?.text.toString() != "Eliga una opción") {
                            precioYCantidad(DropDownProducto?.text.toString())
                        }
                    }, 300)
                }
            }, { error ->
                Toast.makeText(requireContext(), " La aplicación no se ha conectado con el servidor", Toast.LENGTH_LONG).show()
            }
        )
        queue1.add(jsonObjectRequest1)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.CodigoDeBarraAnadir.observe(viewLifecycleOwner) { newText ->
            binding.etCodigoDeBarraAnadir.setText(newText)
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
          //  sharedViewModel.ListaDeProveedoresAnadir.add(bundle.getString("Proveedor")!!) }
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 = "http://186.64.123.248/FacturaEntrada/precioYCantidad.php"
        val jsonObjectRequest1 = object : StringRequest(
            Request.Method.POST, url1,
            { response ->
                val precio = JSONObject(response).getString("Precio")
                val unidades = JSONObject(response).getString("Unidades de embalaje")
                // binding.etPrecioPorUnidadUnidades.setText("200")
                //binding.etCantidad.setText("12")
                if (binding.llCajasDeProductoElegirProductoAnadir.isVisible) {
                    binding.etArticulosPorCajaAnadir.setText(unidades)
                }
                binding.etPrecioAnadir.setText(precio)
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
                parametros.put("PROVEEDOR", sharedViewModel.ListaDeProveedoresAnadir[0])
                parametros.put("PRODUCTO", producto)
                return parametros

            }
        }
        queue1.add(jsonObjectRequest1)
    }

    private fun codigoDeBarra(){
        if (binding.etCodigoDeBarraAnadir.text.isNotBlank()) {
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
                                "EL código de barra pertenece al producto $codigoBarraProducto y ${opcionesList.indexOf("$codigoBarraProducto ( 0 unid. )")}",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.tvListaDesplegableElegirProductoAnadir.postDelayed({
                                binding.tvListaDesplegableElegirProductoAnadir.setSelection(opcionesList.indexOf("$codigoBarraProducto ( 0 unid. )"))
                                binding.tvListaDesplegableElegirProductoAnadir.setText("$codigoBarraProducto ( 0 unid. )",false)
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
                        binding.etCodigoDeBarraAnadir.text.toString()
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
                if (binding.llCajasDeProductoElegirProductoAnadir.isVisible) {
                    binding.etArticulosPorCajaAnadir.setText(unidades)
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