package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentMisDatosBinding
import com.seba.mitantonavigationdrawer.databinding.FragmentTransaccionesBinding
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.AnadirAlmacenFragmentDirections
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.MisDatosViewModel
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.AlmacenesFragment
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.clientes.ClientesFragment
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos.ProductosFragment
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.proveedores.ProveedoresFragment
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.tiposDeProductos.TiposDeProductosFragment
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.facturaEntrada.FacturaEntradaFragment
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.facturaSalida.FacturaSalidaFragment
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.sinFactura.SinFacturaFragment
import com.seba.mitantonavigationdrawer.ui.SharedViewModel

class TransaccionesFragment : Fragment(R.layout.fragment_transacciones) {


    private var _binding: FragmentTransaccionesBinding? = null
    private val sharedViewModel by activityViewModels<SharedViewModel>()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val transaccionesViewModelViewModel =
            ViewModelProvider(this).get(TransaccionesViewModel::class.java)

        _binding = FragmentTransaccionesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa
        replaceFragment(FacturaEntradaFragment())
        //findNavController().navigate(R.id.action_nav_mis_datos_to_nav_almacenes)
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.nav_factura_entrada ->replaceFragment(FacturaEntradaFragment())
                R.id.nav_factura_salida-> replaceFragment(FacturaSalidaFragment())
                R.id.nav_sin_factura -> replaceFragment(SinFacturaFragment())
                else ->{

                }
            }
            true
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Navegar a MisDatosFragment y pasar un identificador para saber qué fragmento llamar luego
                // val action = EditarClienteFragmentDirections.actionEditarClienteFragmentToMisDatosFragment("clientes")
                val action = R.id.action_nav_transacciones_to_nav_inicio
                findNavController().navigate(action)
            }
        })

        return root
    }

    override fun onStart() {
        super.onStart()
        borrarListas()
    }

    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()

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