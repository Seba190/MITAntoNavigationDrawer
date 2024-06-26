package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.facturaEntrada

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
import com.seba.mitantonavigationdrawer.databinding.FragmentFacturaEntradaBinding
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


class FacturaEntradaFragment : Fragment(R.layout.fragment_factura_entrada) {

    private var _binding: FragmentFacturaEntradaBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var retrofit: Retrofit
    private lateinit var adapter: FacturaEntradaAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val facturaEntradaViewModel =
            ViewModelProvider(this).get(FacturaEntradaViewModel::class.java)

        _binding = FragmentFacturaEntradaBinding.inflate(inflater, container, false)
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
        adapter = FacturaEntradaAdapter{navigateToEditarFacturaEntrada(it)}
        // adapter2 = CaracteristicasAdapter()
        binding.rvFacturaEntrada.setHasFixedSize(true)
        binding.rvFacturaEntrada.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFacturaEntrada.adapter = adapter
        // binding.rvAlmacenes.adapter = adapter2
    }

    private fun searchByName() {
        binding.progressBarFacturaEntrada.isVisible = true
        CoroutineScope(Dispatchers.IO).launch{
            val myResponse : Response<FacturaEntradaDataResponse> = retrofit.create(ApiServiceFacturaEntrada::class.java).getFacturasEntrada()
            if(myResponse.isSuccessful){
                Log.i("Sebastian", "Funciona!!")
                val response: FacturaEntradaDataResponse? = myResponse.body()
                // val response2: AlmacenesItemResponse? = myResponse2.body()
                if(response != null){
                    Log.i("Sebastian", response.toString())
                    //Log.i("Sebastian", response2.toString())
                    activity?.runOnUiThread {
                        adapter.updateList(response.FacturaEntrada)
                        binding.progressBarFacturaEntrada.isVisible = false
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
    private fun navigateToEditarFacturaEntrada(id:String){
        val action = TransaccionesFragmentDirections.actionNavTransaccionesToNavEditarFacturaEntrada(id = id)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}