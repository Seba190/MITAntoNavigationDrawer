package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.seba.mitantonavigationdrawer.databinding.FragmentClientePrecioVentaBinding
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto.ClientePrecioVentaAdapter.*
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.SocketTimeoutException


class ClientePrecioVentaFragment : Fragment(R.layout.fragment_cliente_precio_venta),
    OnTextChangeListenerPrecioVenta, OnCheckBoxClickListenerPrecioVenta {

    private var _binding: FragmentClientePrecioVentaBinding? = null
    private val sharedViewModel by activityViewModels<SharedViewModel>()
    private var listaDeClientes : List<ClientePrecioVentaItemResponse> = emptyList()
    private lateinit var adapterPrecioVenta: ClientePrecioVentaAdapter
    private lateinit var retrofit: Retrofit

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val clientePrecioVentaViewHolder =
            ViewModelProvider(this).get(ClientePrecioVentaViewModel::class.java)

        _binding = FragmentClientePrecioVentaBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa
        retrofit = getRetrofit()
        gestionarRecyclerView()

       /* binding.bPrecioVentaClientesVolver.setOnClickListener {
            binding.rlPrecioVentaClientes.isVisible = false
            val anadirProductoFragment = AnadirProductoFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.rlPrecioVentaClientes, anadirProductoFragment)
                .commitNow()
        }*/
        binding.rlPrecioVentaClientes.setOnClickListener {
            binding.rlPrecioVentaClientes.isVisible = false
            val anadirProductoFragment = AnadirProductoFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.rlPrecioVentaClientes, anadirProductoFragment)
                .commitNow()
        }
        binding.bPrecioVentaClientesEliminar.setOnClickListener {
            sharedViewModel.listaDeClientesAnadir.clear()
            sharedViewModel.listaDePreciosVentaAnadir.clear()
            sharedViewModel.ListasDeClientes.clear()
            sharedViewModel.ListasDePreciosDeVenta.clear()
            sharedViewModel.ListasDeProductosPrecioVenta.clear()
            Toast.makeText(requireContext(), "Se han eliminado los precios exitosamente", Toast.LENGTH_SHORT).show()
        }

        binding.bAgregarCliente.setOnClickListener {
            adapterPrecioVenta.getAllEditTextContents()
            adapterPrecioVenta.getAllTextViewContents()
            Log.i("Sebastian2", "${sharedViewModel.listaDePreciosVentaAnadir} , ${sharedViewModel.listaDeClientesAnadir}")
            if(!sharedViewModel.listaDePreciosVentaAnadir.all { it.isBlank() }){
            for (i in 0..<sharedViewModel.listaDePreciosVentaAnadir.size) {
                if(sharedViewModel.listaDePreciosVentaAnadir[i] != "" && sharedViewModel.listaDeClientesAnadir[i] != "") {
                    setFragmentResult("PreciosVentaClientes$i", bundleOf("Clientes$i" to sharedViewModel.listaDeClientesAnadir[i], "PreciosDeVenta$i" to sharedViewModel.listaDePreciosVentaAnadir[i]))
                    }
                }
            }

                binding.rlPrecioVentaClientes.isVisible = false
                val anadirProductoFragment = AnadirProductoFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.rlPrecioVentaClientes, anadirProductoFragment)
                    .commitNow()
            Toast.makeText(requireContext(), "Se han agregado exitosamente los precios de venta", Toast.LENGTH_SHORT).show()
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
        adapterPrecioVenta = ClientePrecioVentaAdapter(listaDeClientes,sharedViewModel,this,this)
        val recyclerView = binding.rvClientes.findViewById<RecyclerView>(R.id.rvClientes)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapterPrecioVenta
        searchByNameSellPrice()
    }

    private fun searchByNameSellPrice() {
        binding.progressBarPrecioVenta.isVisible = true
        CoroutineScope(Dispatchers.IO).launch{
            try{
            val myResponse : Response<ClientePrecioVentaDataResponse> = retrofit.create(ApiServicePrecioVenta::class.java).getClientePrecioVenta()
            // retrofit.create(ApiServiceAlertas::class.java).getAlertasAlmacenes()
            if(myResponse.isSuccessful){
                Log.i("Sebastian", "Funciona!!")
                val response: ClientePrecioVentaDataResponse? = myResponse.body()
                if(response != null){
                    Log.i("Sebastian", response.toString())
                    //Log.i("Sebastian", response2.toString())
                    activity?.runOnUiThread {
                        adapterPrecioVenta.updateList(response.Clientes.sortedBy { it.Nombre })
                        binding.progressBarPrecioVenta.isVisible = false

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

    override fun onCheckBoxClick(id: Int, isChecked: Boolean) {

    }

    override fun onTextChange(text: String, viewHolder: ClientePrecioVentaViewHolder) {

    }

    override fun afterTextChange(text: String, viewHolder: ClientePrecioVentaViewHolder) {
      /*  if (!binding.bAgregarCliente.isPressed && text.isNotEmpty()){
            Toast.makeText(requireContext(), "Se he agregado el precio de venta", Toast.LENGTH_SHORT).show()
            sharedViewModel.listaDePreciosVentaAnadir[viewHolder.adapterPosition] = text
            sharedViewModel.listaDeClientesAnadir[viewHolder.adapterPosition] = adapterPrecioVenta.clientePrecioVentaList[viewHolder.adapterPosition].Nombre
            Log.i("Sebastian", "${sharedViewModel.listaDePreciosVentaAnadir} y ${sharedViewModel.listaDeClientesAnadir}")}
        else if (!binding.bAgregarCliente.isPressed && text.isEmpty() && sharedViewModel.listaDePreciosVentaAnadir.isNotEmpty() &&
            sharedViewModel.listaDeClientesAnadir.isNotEmpty()){
            Toast.makeText(requireContext(), "Se ha eliminado el precio de venta", Toast.LENGTH_SHORT).show()
            sharedViewModel.listaDePreciosVentaAnadir[viewHolder.adapterPosition] = ""
            sharedViewModel.listaDeClientesAnadir[viewHolder.adapterPosition] = ""
            Log.i("Sebastian", "${sharedViewModel.listaDePreciosVentaAnadir} y ${sharedViewModel.listaDeClientesAnadir}")
        }*/

    }
}