package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.sinFactura

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
import com.seba.mitantonavigationdrawer.databinding.FragmentClientesBinding
import com.seba.mitantonavigationdrawer.databinding.FragmentSinFacturaBinding
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.MisDatosFragmentDirections
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.AlmacenesAdapter
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.clientes.ApiServiceClientes
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.clientes.ClientesAdapter
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.clientes.ClientesDataResponse
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.clientes.ClientesViewModel
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.TransaccionesFragmentDirections
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SinFacturaFragment : Fragment(R.layout.fragment_sin_factura) {

    private var _binding: FragmentSinFacturaBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var retrofit: Retrofit
    private lateinit var adapter: SinFacturaAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val sinFacturaViewModel =
            ViewModelProvider(this).get(SinFacturaViewModel::class.java)

        _binding = FragmentSinFacturaBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.tvTextoNoFacturas.isVisible = false
        //Aquí se programa
        retrofit = getRetrofit()
        initUI()
        return root
    }
    private fun initUI() {
        searchByName()
        adapter = SinFacturaAdapter{navigateToEditarSinFactura(it)}
        // adapter2 = CaracteristicasAdapter()
        binding.rvSinFactura.setHasFixedSize(true)
        binding.rvSinFactura.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSinFactura.adapter = adapter
    }

    private fun searchByName() {
        binding.progressBarSinFactura.isVisible = true
        CoroutineScope(Dispatchers.IO).launch{
            val myResponse : Response<SinFacturaDataResponse> = retrofit.create(ApiServiceSinFactura::class.java).getSinFacturas()
            if(myResponse.isSuccessful){
                Log.i("Sebastian", "Funciona!!")
                val response: SinFacturaDataResponse? = myResponse.body()
                // val response2: AlmacenesItemResponse? = myResponse2.body()
                if(response != null){
                    Log.i("Sebastian", response.toString())
                    //Log.i("Sebastian", response2.toString())
                    activity?.runOnUiThread {
                        adapter.updateList(response.SinFactura)
                        binding.progressBarSinFactura.isVisible = false
                        binding.tvTextoNoFacturas.isVisible = response.SinFactura.isEmpty()
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
    private fun navigateToEditarSinFactura(id:String){
        val action = TransaccionesFragmentDirections.actionNavTransaccionesToNavEditarSinFactura(id = id)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}