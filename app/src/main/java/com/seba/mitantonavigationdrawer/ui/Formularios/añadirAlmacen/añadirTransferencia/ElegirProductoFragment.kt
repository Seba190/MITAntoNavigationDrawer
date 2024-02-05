package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirTransferencia

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
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentElegirProductoBinding
import org.json.JSONObject

class ElegirProductoFragment : Fragment(R.layout.fragment_elegir_producto) {

    private var _binding: FragmentElegirProductoBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    var itemSelectedProducto : MutableList<String> = mutableListOf()
    var itemSelectedProveedor : MutableList<String> = mutableListOf()
    var DropDownProducto: AutoCompleteTextView? = null
    var DropDownProveedor: AutoCompleteTextView? = null
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
        DropDownProveedor = binding.tvListaDesplegableElegirProveedor.findViewById(R.id.tvListaDesplegableElegirProveedor)

        // Poner los edittext con borde gris
        binding.etCantidad.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etArticulosPorCaja.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etNumeroDeCajas.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etPrecioPorUnidadUnidades.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etPrecioPorUnidadCajasDeProductos.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etUbicacionInterna.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)

        ListaDesplegableElegirProducto()
        ListaDesplegableElegirProveedor()

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
            Toast.makeText(
                requireContext(),
                "Se ha agregado el producto a la factura",
                Toast.LENGTH_LONG
            ).show()

            if (binding.llUnidadesElegirProducto.isVisible) {
                setFragmentResult(
                    "ElegirProducto1", bundleOf(
                        "Cantidad" to binding.etCantidad.text.toString(),
                        "PrecioUnidades" to binding.etPrecioPorUnidadUnidades.text.toString(),
                        "Producto" to binding.tvListaDesplegableElegirProducto.text.toString()
                    )
                )
            }
            else if (binding.llCajasDeProductoElegirProducto.isVisible) {
                setFragmentResult(
                    "ElegirProducto2", bundleOf(
                        "Cantidad" to binding.etCantidad.text.toString(),
                        "PrecioCajas" to binding.etPrecioPorUnidadCajasDeProductos.text.toString(),
                        "Producto" to binding.tvListaDesplegableElegirProducto.text.toString()
                    )
                )
            }
        }
        return root
    }

    private fun setCantidad() {
      //  binding.etCantidad.text.toString().toIntOrNull() = cantidad.toString().toIntOrNull()
    }


    private fun ListaDesplegableElegirProducto() {
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 ="http://186.64.123.248/Transferencia/elegirProducto.php"
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
                DropDownProducto?.setAdapter(adapter)

                DropDownProducto?.onItemClickListener = AdapterView.OnItemClickListener {
                        parent, view, position, id ->
                    itemSelectedProducto.add(parent.getItemAtPosition(position).toString())
                }
            }, { error ->
                Toast.makeText(requireContext(), " La aplicación no se ha conectado con el servidor", Toast.LENGTH_LONG).show()
            }
        )
        queue1.add(jsonObjectRequest1)
    }

    private fun ListaDesplegableElegirProveedor() {
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
                //Toast.makeText(requireContext(), " La aplicación no se ha conectado con el servidor", Toast.LENGTH_LONG).show()
            }
        )
        queue1.add(jsonObjectRequest1)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bAnadirParametros.setOnClickListener {
            val queue1 = Volley.newRequestQueue(requireContext())
            val url1 = "http://186.64.123.248/Transferencia/precioYCantidad.php"
                val jsonObjectRequest1 = object : StringRequest(
                    Request.Method.POST, url1,
                    { response ->
                        val precioCompra = JSONObject(response).getString("Lista")
                        val unidadesEmbalaje = JSONObject(response).getString("Lista2")
                        binding.etPrecioPorUnidadUnidades.setText(precioCompra)
                        // binding.etPrecioPorUnidadUnidades.setText("200")
                        binding.etCantidad.setText(unidadesEmbalaje)
                        //binding.etCantidad.setText("12")
                        if (binding.llCajasDeProductoElegirProducto.isVisible) {
                            binding.etPrecioPorUnidadCajasDeProductos.setText(precioCompra)
                            //binding.etPrecioPorUnidadCajasDeProductos.setText("200")
                        }
                        Toast.makeText(
                            requireContext(),
                            "${DropDownProducto?.text} y ${DropDownProveedor?.text} exito!!",
                            Toast.LENGTH_LONG
                        ).show()
                        //binding.tvListaDesplegableElegirProveedor.setText("Eliga una opción",false)
                        // binding.tvListaDesplegableElegirProducto.setText("Eliga una opción",false)

                    }, { error ->
                        /*if(binding.tvListaDesplegableElegirProveedor.text.toString() != "Eliga una opción" &&
                    binding.tvListaDesplegableElegirProducto.text.toString() != "Eliga una opción") {*/
                        Toast.makeText(requireContext(), "No se ha podido cargar los parametros $error", Toast.LENGTH_LONG).show()
                        binding.etPrecioPorUnidadUnidades.setText("200")
                        // binding.etCantidad.setText(unidadesEmbalaje)
                        binding.etCantidad.setText("12")
                        Log.i("Runtime","$error")
                        if (binding.llCajasDeProductoElegirProducto.isVisible) {
                            //binding.etPrecioPorUnidadCajasDeProductos.setText(precioCompra)
                            binding.etPrecioPorUnidadCajasDeProductos.setText("200")

                        }
                    }

                    // }
                ) {
                    override fun getParams(): MutableMap<String, String> {
                        val parametros = HashMap<String, String>()
                        parametros.put("PRODUCTO", DropDownProducto?.text.toString())
                        parametros.put("PROVEEDOR", DropDownProveedor?.text.toString())
                        return parametros

                    }
                }
                queue1.add(jsonObjectRequest1)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}