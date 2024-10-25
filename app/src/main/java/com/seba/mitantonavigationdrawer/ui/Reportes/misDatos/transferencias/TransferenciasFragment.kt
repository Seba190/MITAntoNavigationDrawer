package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transferencias

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
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
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.facturaEntrada.FacturaEntradaItemResponse
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import com.seba.mitantonavigationdrawer.ui.estadística.EstadisticaViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TransferenciasFragment : Fragment(R.layout.fragment_transferencias) {
    private var _binding: FragmentTransferenciasBinding? = null
    private val binding get() = _binding!!
    private lateinit var retrofit: Retrofit
    private lateinit var adapter: TransferenciasAdapter
    private val sharedViewModel by activityViewModels<SharedViewModel>()
    private var listaDeTransferencias : List<TransferenciasItemResponse> = emptyList()
    private var listaDeTransferenciasMutableList : MutableList<TransferenciasItemResponse> = mutableListOf()
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
        borrarListas()
        sharedViewModel.transferenciasId.clear()
        return root
    }

    override fun onStart() {
        super.onStart()
        borrarListas()
    }

    private fun initUI() {
        searchByName()
        adapter = TransferenciasAdapter(listaDeTransferenciasMutableList,sharedViewModel){navigateToEditarTransferencia(it)}
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
                        val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                       // adapter.updateList(response.Transferencias.sortedWith(compareBy(
                       //     {it.Transferencia} , compareByDescending{it.FechaCompleta})))
                        adapter.updateList(response.Transferencias.sortedBy { it.Transferencia }.sortedByDescending { LocalDate.parse(it.FechaCompleta,dateFormatter) })
                        binding.progressBarTransferencias.isVisible = false
                        listaDeTransferenciasMutableList.clear()
                        listaDeTransferenciasMutableList.addAll(response.Transferencias)
                        binding.etFiltradoTransferencias.addTextChangedListener { filtro ->
                            // Convierte 'filtro' a String de manera segura manejando el caso null
                            val filtroTexto = filtro.toString()
                            // Filtra la lista con el texto del filtro
                            val almacenesFiltrados =
                                listaDeTransferenciasMutableList.filter { bodega ->
                                    bodega.Transferencia.lowercase().contains(filtroTexto.lowercase())
                                }.sortedBy { it.Transferencia }
                            Log.i(
                                "SebaAntes",
                                "$almacenesFiltrados , $filtroTexto , $listaDeTransferenciasMutableList "
                            )
                            adapter.updateList(almacenesFiltrados)
                            Log.i(
                                "SebaDespues",
                                "$almacenesFiltrados , $filtroTexto , $listaDeTransferenciasMutableList "
                            )
                        }
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