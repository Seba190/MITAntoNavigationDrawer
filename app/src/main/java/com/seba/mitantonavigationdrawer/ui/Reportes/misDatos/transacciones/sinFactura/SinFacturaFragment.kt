package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.sinFactura

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.facturaEntrada.FacturaEntradaItemResponse
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class SinFacturaFragment : Fragment(R.layout.fragment_sin_factura) {

    private var _binding: FragmentSinFacturaBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var retrofit: Retrofit
    private lateinit var adapter: SinFacturaAdapter
    private var listaDeSinFacturasMutableList : MutableList<SinFacturaItemResponse> = mutableListOf()
    private val sharedViewModel: SharedViewModel by viewModels()

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
        borrarListas()
        return root
    }
    override fun onResume() {
        super.onResume()
        borrarListas()
    }
    private fun initUI() {
        searchByName()
        adapter = SinFacturaAdapter(listaDeSinFacturasMutableList){navigateToEditarSinFactura(it)}
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
                        val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                        adapter.updateList(response.SinFactura.sortedBy { it.Producto }.sortedByDescending { LocalDate.parse(it.FechaCompleta,dateFormatter) })
                        binding.progressBarSinFactura.isVisible = false
                        listaDeSinFacturasMutableList.clear()
                        listaDeSinFacturasMutableList.addAll(response.SinFactura)
                        binding.etFiltradoSinFactura.addTextChangedListener { filtro ->
                            // Convierte 'filtro' a String de manera segura manejando el caso null
                            val filtroTexto = filtro.toString()
                            // Filtra la lista con el texto del filtro
                            val almacenesFiltrados =
                                listaDeSinFacturasMutableList.filter { bodega ->
                                    bodega.Producto.lowercase().contains(filtroTexto.lowercase())
                                }.sortedBy { it.Producto }
                            Log.i(
                                "SebaAntes",
                                "$almacenesFiltrados , $filtroTexto , $listaDeSinFacturasMutableList "
                            )
                            adapter.updateList(almacenesFiltrados)
                            Log.i(
                                "SebaDespues",
                                "$almacenesFiltrados , $filtroTexto , $listaDeSinFacturasMutableList "
                            )
                        }
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