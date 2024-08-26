package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transferencias

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentEstadisticaBinding
import com.seba.mitantonavigationdrawer.databinding.FragmentTransferenciasBinding
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.MisDatosFragmentDirections
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.clientes.ApiServiceClientes
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.clientes.ClientesAdapter
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.clientes.ClientesDataResponse
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos.editarProducto.AlertasAlmacenesItemResponseEditar
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import com.seba.mitantonavigationdrawer.ui.estadística.EstadisticaViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TransferenciasFragment : Fragment(R.layout.fragment_transferencias) {
    private var _binding: FragmentTransferenciasBinding? = null
    private val binding get() = _binding!!
    private lateinit var retrofit: Retrofit
    private lateinit var adapter: TransferenciasAdapter
    private val sharedViewModel by activityViewModels<SharedViewModel>()
    private var listaDeTransferencias : List<TransferenciasItemResponse> = emptyList()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val transferenciasViewModel =
            ViewModelProvider(this).get(TransferenciasViewModel::class.java)

        _binding = FragmentTransferenciasBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.tvTextoNoTransferencias.isVisible = false
        //Aquí se programa
        retrofit = getRetrofit()
        initUI()
        sharedViewModel.transferenciasId.removeAll(sharedViewModel.transferenciasId)
        return root
    }

    private fun initUI() {
        searchByName()
        adapter = TransferenciasAdapter(listaDeTransferencias,sharedViewModel){navigateToEditarTransferencia(it)}
        // adapter2 = CaracteristicasAdapter()
        binding.rvTransferencias.setHasFixedSize(true)
        binding.rvTransferencias.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTransferencias.adapter = adapter
        // binding.rvAlmacenes.adapter = adapter2
    }

    private fun searchByName() {
        binding.progressBarTransferencias.isVisible = true
        CoroutineScope(Dispatchers.IO).launch{
            val myResponse : Response<TransferenciasDataResponse> = retrofit.create(ApiServiceTransferencias::class.java).getTransferencias()
            if(myResponse.isSuccessful){
                Log.i("Sebastian", "Funciona!!")
                val response: TransferenciasDataResponse? = myResponse.body()
                // val response2: AlmacenesItemResponse? = myResponse2.body()
                if(response != null){
                    Log.i("Sebastian", response.toString())
                    //Log.i("Sebastian", response2.toString())
                    activity?.runOnUiThread {
                        adapter.updateList(response.Transferencias)
                        binding.progressBarTransferencias.isVisible = false
                        binding.tvTextoNoTransferencias.isVisible = response.Transferencias.isEmpty()
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
    private fun navigateToEditarTransferencia(id:String){
        val action = TransferenciasFragmentDirections.actionNavTransferenciasToNavEditarTransferencias(id = id)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}