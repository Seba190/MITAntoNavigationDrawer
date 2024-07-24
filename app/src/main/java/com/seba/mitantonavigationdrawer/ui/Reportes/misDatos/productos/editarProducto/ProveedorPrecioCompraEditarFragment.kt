package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos.editarProducto

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentProveedorPrecioCompraBinding
import com.seba.mitantonavigationdrawer.databinding.FragmentProveedorPrecioCompraEditarBinding
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto.AnadirProductoFragment
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto.ApiServicePrecioCompra
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto.OnTextChangeListenerPrecioCompra
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto.ProveedorPrecioCompraAdapter
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto.ProveedorPrecioCompraAdapter.*
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto.ProveedorPrecioCompraDataResponse
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto.ProveedorPrecioCompraItemResponse
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto.ProveedorPrecioCompraViewHolder
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos.editarProducto.ProveedorPrecioCompraEditarAdapter.OnCheckBoxClickListenerPrecioCompraEditar
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import com.seba.mitantonavigationdrawer.ui.estadística.EstadisticaViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.SocketTimeoutException

class ProveedorPrecioCompraEditarFragment : Fragment(R.layout.fragment_proveedor_precio_compra_editar)
    , OnTextChangeListenerPrecioCompraEditar,
    OnCheckBoxClickListenerPrecioCompraEditar {

    private var _binding: FragmentProveedorPrecioCompraEditarBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var retrofit: Retrofit
    private val sharedViewModel by activityViewModels<SharedViewModel>()
    private lateinit var adapterPrecioCompra: ProveedorPrecioCompraEditarAdapter
    private var listaDeProveedores: List<ProveedorPrecioCompraItemResponseEditar> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val proveedorPrecioCompraViewModel =
            ViewModelProvider(this).get(ProveedorPrecioCompraEditarViewModel::class.java)

        _binding = FragmentProveedorPrecioCompraEditarBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa
        retrofit = getRetrofit()
        gestionarRecyclerView()

        /* binding.bPrecioCompraProveedorVolver.setOnClickListener {
             binding.rlProveedorPrecioCompra.isVisible = false
             val anadirProductoFragment = AnadirProductoFragment()
             parentFragmentManager.beginTransaction()
                 .replace(R.id.rlProveedorPrecioCompra, anadirProductoFragment)
                 .commitNow()
         }*/
        binding.rlProveedorPrecioCompraEditar.setOnClickListener {
            binding.rlProveedorPrecioCompraEditar.isVisible = false
            val editarProductoFragment = EditarProductoFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.rlProveedorPrecioCompraEditar, editarProductoFragment)
                .commitNow()
        }
        binding.bPrecioCompraProveedorEliminarEditar.setOnClickListener {
            sharedViewModel.listaDePreciosCompra.clear()
            sharedViewModel.listaDeProveedores.clear()
            sharedViewModel.ListasDeProveedores.clear()
            sharedViewModel.ListasDePreciosDeCompra.clear()
            sharedViewModel.ListasDeProductosPrecioCompra.clear()
            sharedViewModel.numeroPreciosCompra.clear()
            Toast.makeText(requireContext(), "Se han eliminado los precios exitosamente", Toast.LENGTH_LONG).show()
        }
        binding.bAgregarProveedorEditar.setOnClickListener {
            adapterPrecioCompra.getAllEditTextContents()
            adapterPrecioCompra.getAllTextViewContents()
            //Toast.makeText(requireContext(),"${sharedViewModel.listaDeAlertas} y ${sharedViewModel.listaDeBodegas}",Toast.LENGTH_LONG).show()
            Log.i("Sebastian2", "${sharedViewModel.listaDeProveedores} y ${sharedViewModel.listaDePreciosCompra}")
           // if(!sharedViewModel.listaDePreciosCompra.all { it.isBlank() }){
            if (/*sharedViewModel.listaDePreciosCompra.size > 0 &&*/ sharedViewModel.numeroPreciosCompra.isEmpty()) {
                for (i in 0..<adapterPrecioCompra.proveedorPrecioCompraList.size) {
                   // setFragmentResult("PreciosCompraProveedores$i", bundleOf("Proveedores$i" to sharedViewModel.listaDeProveedores[i], "PreciosCompra$i" to sharedViewModel.listaDePreciosCompra[i]))
                    sharedViewModel.numeroPreciosCompra.add(adapterPrecioCompra.proveedorPrecioCompraList[i].Nombre)

                }
                Toast.makeText(requireContext(), "Se han agregado exitosamente los precios de compra", Toast.LENGTH_LONG).show()
            }//}
                binding.rlProveedorPrecioCompraEditar.isVisible = false
                val editarProductoFragment = EditarProductoFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.rlProveedorPrecioCompraEditar, editarProductoFragment)
                    .commitNow()
        }
        /*if(sharedViewModel.listaDePreciosCompra.lastIndexOf("") != -1){
            sharedViewModel.listaDePreciosCompra.removeAt(sharedViewModel.listaDePreciosCompra.lastIndexOf(""))
        }*/

        return root
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl("http://186.64.123.248/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun gestionarRecyclerView() {
        adapterPrecioCompra = ProveedorPrecioCompraEditarAdapter(listaDeProveedores, sharedViewModel, this, this)
        val recyclerView = binding.rvProveedoresEditar.findViewById<RecyclerView>(R.id.rvProveedoresEditar)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapterPrecioCompra
        searchByNameBuyPrice()
    }

    private fun searchByNameBuyPrice() {
        binding.progressBarPrecioCompraEditar.isVisible = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val myResponse: Response<ProveedorPrecioCompraDataResponseEditar> = retrofit.create(
                    ApiServicePreciosCompraEditar::class.java
                ).getProveedorPrecioCompra()
                // retrofit.create(ApiServiceAlertas::class.java).getAlertasAlmacenes()
                if (myResponse.isSuccessful) {
                    Log.i("Sebastian", "Funciona!!")
                    val response: ProveedorPrecioCompraDataResponseEditar? = myResponse.body()
                    if (response != null) {
                        Log.i("Sebastian", response.toString())
                        //Log.i("Sebastian", response2.toString())
                        activity?.runOnUiThread {
                            adapterPrecioCompra.updateList(response.Proveedores)
                            binding.progressBarPrecioCompraEditar.isVisible = false

                        }
                    }

                } else {
                    Log.i("Sebastian", "No funciona.")
                }
            } catch (e: SocketTimeoutException) {

            } catch (e: Exception) {

            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun afterTextChange(text: String, viewHolder: ProveedorPrecioCompraEditarViewHolder) {
       /* if (!binding.bAgregarProveedorEditar.isPressed && text.isNotEmpty()) {
            Toast.makeText(requireContext(), "Se he agregado el precio de compra", Toast.LENGTH_SHORT).show()
            sharedViewModel.listaDePreciosCompra[viewHolder.adapterPosition] = text
            sharedViewModel.listaDeProveedores[viewHolder.adapterPosition] = adapterPrecioCompra.proveedorPrecioCompraList[viewHolder.adapterPosition].Nombre
            Log.i("Sebastian", "${sharedViewModel.listaDePreciosCompra} y ${sharedViewModel.listaDeProveedores}")
        } else if (!binding.bAgregarProveedorEditar.isPressed && text.isEmpty() && sharedViewModel.listaDePreciosCompra.isNotEmpty() &&
            sharedViewModel.listaDeProveedores.isNotEmpty()) {
            Toast.makeText(requireContext(), "Se ha eliminado el precio de compra", Toast.LENGTH_SHORT).show()
            sharedViewModel.listaDePreciosCompra[viewHolder.adapterPosition] = ""
            sharedViewModel.listaDeProveedores[viewHolder.adapterPosition] = ""
            Log.i("Sebastian", "${sharedViewModel.listaDePreciosCompra} y ${sharedViewModel.listaDeProveedores}")
        }*/

    }

    override fun onTextChange(text: String, viewHolder: ProveedorPrecioCompraEditarViewHolder) {
        TODO("Not yet implemented")
    }

    override fun onCheckBoxClick(id: Int, isChecked: Boolean) {
        TODO("Not yet implemented")
    }
}