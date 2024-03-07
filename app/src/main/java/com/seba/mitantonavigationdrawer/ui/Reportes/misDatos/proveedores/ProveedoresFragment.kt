package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.proveedores

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
import com.seba.mitantonavigationdrawer.databinding.FragmentProveedoresBinding
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


class ProveedoresFragment : Fragment(R.layout.fragment_proveedores) {

    private var _binding: FragmentProveedoresBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var retrofit: Retrofit
    private lateinit var adapter: ProveedoresAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val proveedoresViewModel =
            ViewModelProvider(this).get(ProveedoresViewModel::class.java)

        _binding = FragmentProveedoresBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa
        retrofit = getRetrofit()
        initUI()
        return root
    }
    private fun initUI() {
        //  binding.rvAlmacenes.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
        //    override fun onQueryTextSubmit(query: String?): Boolean {
        searchByName()
        //      return false
        // }

        //override fun onQueryTextChange(newText: String?): Boolean { return false }
        // })
        adapter = ProveedoresAdapter{navigateToEditarProveedor(it)}
        // adapter2 = CaracteristicasAdapter()
        binding.rvProveedores.setHasFixedSize(true)
        binding.rvProveedores.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProveedores.adapter = adapter
        // binding.rvAlmacenes.adapter = adapter2
    }

    private fun searchByName() {
        binding.progressBar.isVisible = true
        CoroutineScope(Dispatchers.IO).launch{
            val myResponse : Response<ProveedoresDataResponse> = retrofit.create(ApiServiceProveedores::class.java).getProveedores()
            if(myResponse.isSuccessful){
                Log.i("Sebastian", "Funciona!!")
                val response: ProveedoresDataResponse? = myResponse.body()
                // val response2: AlmacenesItemResponse? = myResponse2.body()
                if(response != null){
                    Log.i("Sebastian", response.toString())
                    //Log.i("Sebastian", response2.toString())
                    activity?.runOnUiThread {
                        adapter.updateList(response.Proveedores)
                        binding.progressBar.isVisible = false
                    }
                }

            }else{
                Log.i("Sebastian", "No funciona.")
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
    private fun navigateToEditarProveedor(id:String){
        val action = MisDatosFragmentDirections.actionNavMisDatosToNavEditarProveedor(id = id)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}