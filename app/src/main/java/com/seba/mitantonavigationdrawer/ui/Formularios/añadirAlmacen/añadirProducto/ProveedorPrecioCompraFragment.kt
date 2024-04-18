package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto

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
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto.ProveedorPrecioCompraAdapter.*
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import com.seba.mitantonavigationdrawer.ui.estadística.EstadisticaViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.SocketTimeoutException

class ProveedorPrecioCompraFragment : Fragment(R.layout.fragment_proveedor_precio_compra)
    ,OnTextChangeListenerPrecioCompra,
    OnCheckBoxClickListenerPrecioCompra {

    private var _binding: FragmentProveedorPrecioCompraBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var retrofit: Retrofit
    private val sharedViewModel by activityViewModels<SharedViewModel>()
    private lateinit var adapterPrecioCompra: ProveedorPrecioCompraAdapter
    private var listaDeProveedores : List<ProveedorPrecioCompraItemResponse> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val proveedorPrecioCompraViewModel =
            ViewModelProvider(this).get(EstadisticaViewModel::class.java)

        _binding = FragmentProveedorPrecioCompraBinding.inflate(inflater, container, false)
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
        binding.rlProveedorPrecioCompra.setOnClickListener {
            binding.rlProveedorPrecioCompra.isVisible = false
            val anadirProductoFragment = AnadirProductoFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.rlProveedorPrecioCompra, anadirProductoFragment)
                .commitNow()
        }
        binding.bPrecioCompraProveedorEliminar.setOnClickListener {
            sharedViewModel.listaDePreciosCompra.clear()
            sharedViewModel.listaDeProveedores.clear()
            sharedViewModel.ListasDeProveedores.clear()
            sharedViewModel.ListasDePreciosDeCompra.clear()
            sharedViewModel.ListasDeProductosPrecioCompra.clear()
            Toast.makeText(requireContext(), "Se han eliminado los precios exitosamente", Toast.LENGTH_LONG).show()
        }


        return root
    }
    private fun getRetrofit(): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl("http://186.64.123.248/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    private fun gestionarRecyclerView(){
        adapterPrecioCompra = ProveedorPrecioCompraAdapter(listaDeProveedores,sharedViewModel,this,this)
        val recyclerView = binding.rvProveedores.findViewById<RecyclerView>(R.id.rvProveedores)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapterPrecioCompra
        searchByNameBuyPrice()
    }

    private fun searchByNameBuyPrice() {
        binding.progressBar.isVisible = true
        CoroutineScope(Dispatchers.IO).launch{
            try {
            val myResponse : Response<ProveedorPrecioCompraDataResponse> = retrofit.create(ApiServicePrecioCompra::class.java).getProveedorPrecioCompra()
            // retrofit.create(ApiServiceAlertas::class.java).getAlertasAlmacenes()
            if(myResponse.isSuccessful){
                Log.i("Sebastian", "Funciona!!")
                val response: ProveedorPrecioCompraDataResponse? = myResponse.body()
                if(response != null){
                    Log.i("Sebastian", response.toString())
                    //Log.i("Sebastian", response2.toString())
                    activity?.runOnUiThread {
                        adapterPrecioCompra.updateList(response.Proveedores)
                        binding.progressBar.isVisible = false

                    }
                }

            }else{
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

    override fun afterTextChange(text: String, viewHolder: ProveedorPrecioCompraViewHolder, position: Int) {
        if (!binding.bAgregarProveedor.isPressed && text.isNotEmpty()){
            Toast.makeText(requireContext(), "Se he agregado el precio de compra", Toast.LENGTH_SHORT).show()
            sharedViewModel.listaDePreciosCompra.add(text)
            sharedViewModel.listaDeProveedores.add(adapterPrecioCompra.proveedorPrecioCompraList[viewHolder.adapterPosition].Nombre)}
        else if (!binding.bAgregarProveedor.isPressed && text.isEmpty() && sharedViewModel.listaDePreciosCompra.isNotEmpty() &&
            sharedViewModel.listaDeProveedores.isNotEmpty()){
            Toast.makeText(requireContext(), "Se ha eliminado el precio de compra", Toast.LENGTH_SHORT).show()
            sharedViewModel.listaDePreciosCompra.removeAt(position)
            sharedViewModel.listaDeProveedores.removeAt(position)
        }
        binding.bAgregarProveedor.setOnClickListener {
            if (sharedViewModel.listaDePreciosCompra.size > 0) {
                for (i in 0..<sharedViewModel.listaDePreciosCompra.size) {
                    setFragmentResult("PreciosCompraProveedores$i", bundleOf("Proveedores$i" to sharedViewModel.listaDeProveedores[i], "PreciosCompra$i" to sharedViewModel.listaDePreciosCompra[i]))
                }
                Toast.makeText(requireContext(), "Se han agregado exitosamente los precios de compra", Toast.LENGTH_LONG).show()
                binding.rlProveedorPrecioCompra.isVisible = false
                val anadirProductoFragment = AnadirProductoFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.rlProveedorPrecioCompra, anadirProductoFragment)
                    .commitNow()
            }
        }
    }

    override fun onTextChange(text: String, viewHolder: ProveedorPrecioCompraViewHolder) {
        TODO("Not yet implemented")
    }

    override fun onCheckBoxClick(id: Int, isChecked: Boolean) {
        TODO("Not yet implemented")
    }
}