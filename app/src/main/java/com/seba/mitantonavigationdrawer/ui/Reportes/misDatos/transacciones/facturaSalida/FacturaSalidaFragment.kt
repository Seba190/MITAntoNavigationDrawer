package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.facturaSalida

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
import com.seba.mitantonavigationdrawer.databinding.FragmentFacturaSalidaBinding
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


class FacturaSalidaFragment : Fragment(R.layout.fragment_factura_salida) {

    private var _binding: FragmentFacturaSalidaBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var retrofit: Retrofit
    private lateinit var adapter: FacturaSalidaAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val facturaSalidaViewModel =
            ViewModelProvider(this).get(FacturaSalidaViewModel::class.java)

        _binding = FragmentFacturaSalidaBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.tvTextoNoFacturasDeSalida.isVisible = false
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
        adapter = FacturaSalidaAdapter{navigateToEditarFacturaSalida(it)}
        // adapter2 = CaracteristicasAdapter()
        binding.rvFacturaSalida.setHasFixedSize(true)
        binding.rvFacturaSalida.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFacturaSalida.adapter = adapter
        // binding.rvAlmacenes.adapter = adapter2
    }

    private fun searchByName() {
        binding.progressBarFacturaSalida.isVisible = true
        CoroutineScope(Dispatchers.IO).launch{
            val myResponse : Response<FacturaSalidaDataResponse> = retrofit.create(ApiServiceFacturaSalida::class.java).getFacturasSalida()
            if(myResponse.isSuccessful){
                Log.i("Sebastian", "Funciona!!")
                val response: FacturaSalidaDataResponse? = myResponse.body()
                // val response2: AlmacenesItemResponse? = myResponse2.body()
                if(response != null){
                    Log.i("Sebastian", response.toString())
                    //Log.i("Sebastian", response2.toString())
                    activity?.runOnUiThread {
                        adapter.updateList(response.FacturaSalida)
                        binding.progressBarFacturaSalida.isVisible = false
                        binding.tvTextoNoFacturasDeSalida.isVisible = response.FacturaSalida.isEmpty()
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
    private fun navigateToEditarFacturaSalida(id:String){
        val action = TransaccionesFragmentDirections.actionNavTransaccionesToNavEditarFacturaSalida(id = id)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}