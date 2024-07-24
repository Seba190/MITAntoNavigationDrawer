package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos.editarProducto

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
import com.seba.mitantonavigationdrawer.databinding.FragmentClientePrecioVentaEditarBinding
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto.AnadirProductoFragment
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto.ApiServicePrecioVenta
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto.ClientePrecioVentaAdapter
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto.ClientePrecioVentaAdapter.*
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto.ClientePrecioVentaDataResponse
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto.ClientePrecioVentaItemResponse
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto.ClientePrecioVentaViewHolder
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto.ClientePrecioVentaViewModel
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto.OnTextChangeListenerPrecioVenta
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos.editarProducto.ClientePrecioVentaEditarAdapter.OnCheckBoxClickListenerPrecioVentaEditar
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.SocketTimeoutException


class ClientePrecioVentaEditarFragment : Fragment(R.layout.fragment_cliente_precio_venta_editar),
    OnTextChangeListenerPrecioVentaEditar,
    OnCheckBoxClickListenerPrecioVentaEditar {

    private var _binding: FragmentClientePrecioVentaEditarBinding? = null
    private val sharedViewModel by activityViewModels<SharedViewModel>()
    private var listaDeClientes : List<ClientePrecioVentaItemResponseEditar> = emptyList()
    private lateinit var adapterPrecioVenta: ClientePrecioVentaEditarAdapter
    private lateinit var retrofit: Retrofit

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val clientePrecioVentaEditarViewModel =
            ViewModelProvider(this).get(ClientePrecioVentaEditarViewModel::class.java)

        _binding = FragmentClientePrecioVentaEditarBinding.inflate(inflater, container, false)
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
        binding.rlPrecioVentaClientesEditar.setOnClickListener {
            binding.rlPrecioVentaClientesEditar.isVisible = false
            val editarProductoFragment = EditarProductoFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.rlPrecioVentaClientesEditar, editarProductoFragment)
                .commitNow()
        }
        binding.bPrecioVentaClientesEliminarEditar.setOnClickListener {
            sharedViewModel.listaDeClientes.clear()
            sharedViewModel.listaDePreciosVenta.clear()
            sharedViewModel.ListasDeClientes.clear()
            sharedViewModel.ListasDePreciosDeVenta.clear()
            sharedViewModel.ListasDeProductosPrecioVenta.clear()
            sharedViewModel.numeroPreciosVenta.clear()
            Toast.makeText(requireContext(), "Se han eliminado los precios exitosamente", Toast.LENGTH_LONG).show()
        }

        binding.bAgregarClienteEditar.setOnClickListener {
            adapterPrecioVenta.getAllEditTextContents()
            adapterPrecioVenta.getAllTextViewContents()
            //Toast.makeText(requireContext(),"${sharedViewModel.listaDeAlertas} y ${sharedViewModel.listaDeBodegas}",Toast.LENGTH_LONG).show()
            Log.i("Sebastian2", "${sharedViewModel.listaDeClientes} y ${sharedViewModel.listaDePreciosVenta}")

            if (sharedViewModel.numeroPreciosVenta.isEmpty()) {
                for (i in 0..<adapterPrecioVenta.clientePrecioVentaList.size) {
                   // setFragmentResult("PreciosVentaClientes$i", bundleOf("Clientes$i" to sharedViewModel.listaDeClientes[i], "PreciosDeVenta$i" to sharedViewModel.listaDePreciosVenta[i]))
                        sharedViewModel.numeroPreciosVenta.add(adapterPrecioVenta.clientePrecioVentaList[i].Nombre)

                }
                Toast.makeText(requireContext(), "Se han agregado exitosamente los precios de venta", Toast.LENGTH_LONG).show()
            }
                binding.rlPrecioVentaClientesEditar.isVisible = false
                val editarProductoFragment = EditarProductoFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.rlPrecioVentaClientesEditar, editarProductoFragment)
                    .commitNow()

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
        adapterPrecioVenta = ClientePrecioVentaEditarAdapter(listaDeClientes,sharedViewModel,this,this)
        val recyclerView = binding.rvClientesEditar.findViewById<RecyclerView>(R.id.rvClientesEditar)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapterPrecioVenta
        searchByNameSellPrice()
    }

    private fun searchByNameSellPrice() {
        binding.progressBarPrecioVentaEditar.isVisible = true
        CoroutineScope(Dispatchers.IO).launch{
            try{
                val myResponse : Response<ClientePrecioVentaDataResponseEditar> = retrofit.create(
                    ApiServicePreciosVentaEditar::class.java).getClientePrecioVenta()
                // retrofit.create(ApiServiceAlertas::class.java).getAlertasAlmacenes()
                if(myResponse.isSuccessful){
                    Log.i("Sebastian", "Funciona!!")
                    val response: ClientePrecioVentaDataResponseEditar? = myResponse.body()
                    if(response != null){
                        Log.i("Sebastian", response.toString())
                        //Log.i("Sebastian", response2.toString())
                        activity?.runOnUiThread {
                            adapterPrecioVenta.updateList(response.Clientes)
                            binding.progressBarPrecioVentaEditar.isVisible = false

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
        TODO("Not yet implemented")
    }

    override fun onTextChange(text: String, viewHolder: ClientePrecioVentaEditarViewHolder) {
        TODO("Not yet implemented")
    }

    override fun afterTextChange(text: String, viewHolder: ClientePrecioVentaEditarViewHolder) {
        /*if (!binding.bAgregarClienteEditar.isPressed && text.isNotEmpty()){
            Toast.makeText(requireContext(), "Se he agregado el precio de venta", Toast.LENGTH_SHORT).show()
            sharedViewModel.listaDePreciosVenta[viewHolder.adapterPosition] = text
            sharedViewModel.listaDeClientes[viewHolder.adapterPosition] = adapterPrecioVenta.clientePrecioVentaList[viewHolder.adapterPosition].Nombre
            Log.i("Sebastian", "${sharedViewModel.listaDePreciosVenta} y ${sharedViewModel.listaDeClientes}")}
        if (!binding.bAgregarClienteEditar.isPressed && text.isEmpty() && sharedViewModel.listaDePreciosVenta.isNotEmpty() &&
            sharedViewModel.listaDeClientes.isNotEmpty()){
            Toast.makeText(requireContext(), "Se ha eliminado el precio de venta", Toast.LENGTH_SHORT).show()
            sharedViewModel.listaDePreciosVenta[viewHolder.adapterPosition] = ""
            sharedViewModel.listaDeClientes[viewHolder.adapterPosition] = ""
            Log.i("Sebastian", "${sharedViewModel.listaDePreciosVenta} y ${sharedViewModel.listaDeClientes}")
            //sharedViewModel.listaDePreciosVenta.removeAt(position)
            //sharedViewModel.listaDeClientes.removeAt(position)
        }*/
    }
}