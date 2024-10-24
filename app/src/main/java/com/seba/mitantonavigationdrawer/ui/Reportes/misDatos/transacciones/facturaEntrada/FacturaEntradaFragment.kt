package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.facturaEntrada

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
import com.seba.mitantonavigationdrawer.databinding.FragmentFacturaEntradaBinding
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.MisDatosFragmentDirections
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.AlmacenesAdapter
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.AlmacenesItemResponse
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.clientes.ApiServiceClientes
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.clientes.ClientesAdapter
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.clientes.ClientesDataResponse
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.clientes.ClientesViewModel
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.TransaccionesFragmentDirections
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class FacturaEntradaFragment : Fragment(R.layout.fragment_factura_entrada) {

    private var _binding: FragmentFacturaEntradaBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var retrofit: Retrofit
    private lateinit var adapter: FacturaEntradaAdapter
    private var listaDeFacturasEntradaMutableList : MutableList<FacturaEntradaItemResponse> = mutableListOf()
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val facturaEntradaViewModel =
            ViewModelProvider(this).get(FacturaEntradaViewModel::class.java)

        _binding = FragmentFacturaEntradaBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.tvTextoNoFacturasDeEntrada.isVisible = false
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
        //  binding.rvAlmacenes.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
        //    override fun onQueryTextSubmit(query: String?): Boolean {
        searchByName()
        //      return false
        // }

        //override fun onQueryTextChange(newText: String?): Boolean { return false }
        // })
        adapter = FacturaEntradaAdapter(listaDeFacturasEntradaMutableList){navigateToEditarFacturaEntrada(it)}
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
                        val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                        adapter.updateList(response.FacturaEntrada.sortedBy { it.FacturaEntrada }.sortedByDescending { LocalDate.parse(it.FechaCompleta,dateFormatter)})
                        binding.progressBarFacturaEntrada.isVisible = false
                        listaDeFacturasEntradaMutableList.clear()
                        listaDeFacturasEntradaMutableList.addAll(response.FacturaEntrada)
                        binding.etFiltradoFacturaEntrada.addTextChangedListener { filtro ->
                            // Convierte 'filtro' a String de manera segura manejando el caso null
                            val filtroTexto = filtro.toString()
                            // Filtra la lista con el texto del filtro
                            val almacenesFiltrados =
                                listaDeFacturasEntradaMutableList.filter { bodega ->
                                    bodega.FacturaEntrada.lowercase().contains(filtroTexto.lowercase())
                                }.sortedBy { it.FacturaEntrada }

                            adapter.updateList(almacenesFiltrados)
                        }
                        binding.tvTextoNoFacturasDeEntrada.isVisible = response.FacturaEntrada.isEmpty()
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