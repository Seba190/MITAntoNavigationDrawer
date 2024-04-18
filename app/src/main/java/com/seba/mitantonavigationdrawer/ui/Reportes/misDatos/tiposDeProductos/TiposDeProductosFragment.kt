package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.tiposDeProductos

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
import com.seba.mitantonavigationdrawer.databinding.FragmentTiposDeProductosBinding
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.MisDatosFragmentDirections
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.proveedores.ApiServiceProveedores
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.proveedores.ProveedoresAdapter
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.proveedores.ProveedoresDataResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class TiposDeProductosFragment : Fragment(R.layout.fragment_tipos_de_productos) {

    private var _binding: FragmentTiposDeProductosBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var retrofit: Retrofit
    private lateinit var adapter: TiposDeProductosAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val tiposDeProductosViewModel =
            ViewModelProvider(this).get(TiposDeProductosViewModel::class.java)

        _binding = FragmentTiposDeProductosBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa
        retrofit = getRetrofit()
        initUI()
        return root
    }

    private fun initUI() {
        searchByName()
        adapter = TiposDeProductosAdapter{navigateToEditarProveedor(it)}
        binding.rvTiposDeProductos.setHasFixedSize(true)
        binding.rvTiposDeProductos.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTiposDeProductos.adapter = adapter
    }

    private fun searchByName() {
        binding.progressBarTiposDeProductos.isVisible = true
        CoroutineScope(Dispatchers.IO).launch{
            val myResponse : Response<TiposDeProductosDataResponse> = retrofit.create(
                ApiServiceTiposDeProductos::class.java).getTiposDeProductos()
            if(myResponse.isSuccessful){
                Log.i("Sebastian", "Funciona!!")
                val response: TiposDeProductosDataResponse? = myResponse.body()
                // val response2: AlmacenesItemResponse? = myResponse2.body()
                if(response != null){
                    Log.i("Sebastian", response.toString())
                    //Log.i("Sebastian", response2.toString())
                    activity?.runOnUiThread {
                        adapter.updateList(response.TiposDeProductos)
                        binding.progressBarTiposDeProductos.isVisible = false
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