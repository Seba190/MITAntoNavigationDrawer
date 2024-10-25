package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.proveedores

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentProveedoresBinding
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.MisDatosFragmentDirections
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
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
    private var listaDeProveedoresMutableList : MutableList<ProveedoresItemResponse> = mutableListOf()
    private val listaDeActividad: List<String> = listOf("TODOS", "ACTIVOS", "INACTIVOS")
    var DropdownActividad:  AutoCompleteTextView? = null
    private val sharedViewModel : SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val proveedoresViewModel =
            ViewModelProvider(this).get(ProveedoresViewModel::class.java)

        _binding = FragmentProveedoresBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.tvTextoNoProveedores.isVisible = false
        //Aquí se programa
        DropdownActividad = binding.tvAutoCompleteFiltradoProveedores.findViewById(R.id.tvAutoCompleteFiltradoProveedores)
        retrofit = getRetrofit()
        listaDesplegableActividad()
        initUI()
        borrarListas()
        return root
    }

    override fun onStart() {
        super.onStart()
        borrarListas()
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
            val myResponse : Response<ProveedoresDataResponse> = retrofit.create(ApiServiceProveedores::class.java).postProveedores(DropdownActividad?.text.toString())
            if(myResponse.isSuccessful){
                Log.i("Sebastian", "Funciona!!")
                val response: ProveedoresDataResponse? = myResponse.body()
                // val response2: AlmacenesItemResponse? = myResponse2.body()
                if(response != null){
                    Log.i("Sebastian", response.toString())
                    //Log.i("Sebastian", response2.toString())
                    activity?.runOnUiThread {
                        adapter.updateList(response.Proveedores.sortedBy { it.Nombre })
                        binding.progressBar.isVisible = false
                        listaDeProveedoresMutableList.clear()
                        listaDeProveedoresMutableList.addAll(response.Proveedores)
                        binding.etFiltradoProveedores.addTextChangedListener { filtro ->
                            // Convierte 'filtro' a String de manera segura manejando el caso null
                            val filtroTexto = filtro.toString()
                            // Filtra la lista con el texto del filtro
                            val almacenesFiltrados =
                                listaDeProveedoresMutableList.filter { bodega ->
                                    bodega.Nombre.lowercase().contains(filtroTexto.lowercase())
                                }.sortedBy { it.Nombre }
                            Log.i(
                                "SebaAntes",
                                "$almacenesFiltrados , $filtroTexto , $listaDeProveedoresMutableList "
                            )
                            adapter.updateList(almacenesFiltrados)
                            Log.i(
                                "SebaDespues",
                                "$almacenesFiltrados , $filtroTexto , $listaDeProveedoresMutableList "
                            )
                        }
                        binding.tvTextoNoProveedores.isVisible = response.Proveedores.isEmpty()
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

    private fun listaDesplegableActividad() {
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, listaDeActividad)
        //binding.tvholaMundo?.setText(response.getString("Lista"))
        DropdownActividad?.setAdapter(adapter)
        DropdownActividad?.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                // val itemSelected = parent.getItemAtPosition(position).toString()
                searchByName()
                val valorEditText: String = binding.etFiltradoProveedores.text.toString()
                binding.etFiltradoProveedores.setText("")
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.etFiltradoProveedores.setText(valorEditText)
                }, 100)
                //bodegasPorActividad(itemSelected)
            }
        }

    private fun navigateToEditarProveedor(id:String){
        val action = MisDatosFragmentDirections.actionNavMisDatosToNavEditarProveedor(id = id)
        findNavController().navigate(action)
    }

   private fun borrarListas(){
        sharedViewModel.listaDeProductos.clear()
        sharedViewModel.listaDeCantidades.clear()
        sharedViewModel.listaDeProductosAnadir.clear()
        sharedViewModel.listaDeCantidadesAnadir.clear()
        sharedViewModel.listaDePreciosAnadir.clear()
        sharedViewModel.listaDePreciosDeProductos.clear()
        sharedViewModel.listaDeProductosRemover.clear()
        sharedViewModel.listaDeCantidadesRemover.clear()
        sharedViewModel.listaDePreciosRemover.clear()
        sharedViewModel.listaDePreciosDeProductosRemover.clear()
        sharedViewModel.listaDeBodegasAnadir.clear()
        sharedViewModel.listaDeAlertasAnadir.clear()
        sharedViewModel.ListasDeAlertas.clear()
        sharedViewModel.ListasDeAlmacenes.clear()
        sharedViewModel.ListasDeProductosAlertas.clear()
        sharedViewModel.listaDeClientesAnadir.clear()
        sharedViewModel.listaDePreciosVentaAnadir.clear()
        sharedViewModel.ListasDeClientes.clear()
        sharedViewModel.ListasDePreciosDeVenta.clear()
        sharedViewModel.ListasDeProductosPrecioVenta.clear()
        sharedViewModel.listaDePreciosCompraAnadir.clear()
        sharedViewModel.listaDeProveedoresAnadir.clear()
        sharedViewModel.ListasDeProveedores.clear()
        sharedViewModel.ListasDePreciosDeCompra.clear()
        sharedViewModel.ListasDeProductosPrecioCompra.clear()
        sharedViewModel.id.clear()
        sharedViewModel.listaDeAlertas.clear()
        sharedViewModel.listaDePreciosVenta.clear()
        sharedViewModel.listaDePreciosCompra.clear()
        sharedViewModel.listaDeBodegas.clear()
        sharedViewModel.listaDeClientes.clear()
        sharedViewModel.listaDeProveedores.clear()
        sharedViewModel.numeroAlertas.clear()
        sharedViewModel.numeroPreciosCompra.clear()
        sharedViewModel.numeroPreciosVenta.clear()
        sharedViewModel.listaDeAlmacenesAnadir.clear()
        sharedViewModel.listaDeAlmacenesRemover.clear()
        sharedViewModel.listaDeAlmacenesEntrada.clear()
        sharedViewModel.listaDeAlmacenesSalida.clear()
        sharedViewModel.listaDeAlmacenesTransferencia.clear()
        sharedViewModel.listaDeAlmacenesEditarTransferencia.clear()
        sharedViewModel.productos.clear()
        sharedViewModel.inventario.clear()

        sharedViewModel.facturaTotalAnadir.clear()
        sharedViewModel.facturaTotalRemover.clear()
        sharedViewModel.facturaTotalEntrada.clear()
        sharedViewModel.facturaTotalSalida.clear()
        sharedViewModel.cantidadTotalTransferencia.clear()
        sharedViewModel.cantidadTotalEditarTransferencia.clear()

        sharedViewModel.listaCombinadaEntrada.clear()
        sharedViewModel.listaCombinadaSalida.clear()
        sharedViewModel.listaCombinadaTransferencia.clear()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}