package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentProductosBinding
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.MisDatosFragmentDirections
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.clientes.ApiServiceClientes
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.clientes.ClientesAdapter
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.clientes.ClientesDataResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ProductosFragment : Fragment(R.layout.fragment_productos) {

    private var _binding: FragmentProductosBinding? = null
    private lateinit var retrofit: Retrofit
    private lateinit var adapter: ProductosAdapter
    private lateinit var  productosViewModel: ProductosViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        productosViewModel = ViewModelProvider(this).get(ProductosViewModel::class.java)
        _binding = FragmentProductosBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa
        retrofit = getRetrofit()
        initUI()

        return root
    }

    private fun initUI() {
        //  binding.rvAlmacenes.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
        //    override fun onQueryTextSubmit(query: String?): Boolean {
        //      return false
        // }

        //override fun onQueryTextChange(newText: String?): Boolean { return false }
        // })
        adapter = ProductosAdapter{navigateToEditarCliente(it)}
        binding.rvProductos.setHasFixedSize(true)
        binding.rvProductos.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProductos.adapter = adapter

        productosViewModel.productos.observe(viewLifecycleOwner) { productos ->
            adapter.updateList(productos)
        }
        searchByName()
    }

    private fun searchByName() {
        binding.progressBarProductos.isVisible = true
        CoroutineScope(Dispatchers.IO).launch{
            val myResponse : Response<ProductosDataResponse> = retrofit.create(ApiServiceProductos::class.java).getProductos()
            if(myResponse.isSuccessful){
                Log.i("Sebastian", "Funciona!!")
                val response: ProductosDataResponse? = myResponse.body()
                // val response2: AlmacenesItemResponse? = myResponse2.body()
               // if(response != null){
                response?.let {
                    activity?.runOnUiThread {
                        productosViewModel.updateProductos(it.Productos)
                       // adapter.updateList(response.Productos)
                        binding.progressBarProductos.isVisible = false
                        Log.i("Sebastian", response.toString())
                    }
                }
            //Log.i("Sebastian", response2.toString())
            }
            else{
                Log.i("Sebastian", "No funciona.")
                activity?.runOnUiThread {
                    binding.progressBarProductos.isVisible = false
                }
            }
        }
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl("http://186.64.123.248/Reportes/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun navigateToEditarCliente(id:String){
        //val action = MisDatosFragmentDirections.actionNavMisDatosToNavEditarCliente(id = id)
        val action = MisDatosFragmentDirections.actionNavMisDatosToNavEditarProducto(id = id)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}