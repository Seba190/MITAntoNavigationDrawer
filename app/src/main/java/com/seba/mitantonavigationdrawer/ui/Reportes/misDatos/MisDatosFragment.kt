package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavArgs
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentMisDatosBinding
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.AnadirAlmacenFragmentDirections
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.AlmacenesFragment
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.clientes.ClientesFragment
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos.ProductosFragment
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos.editarProducto.EditarProductoFragment
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.proveedores.ProveedoresFragment
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.tiposDeProductos.TiposDeProductosFragment
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones.sinFactura.editarSinFactura.EditarSinFacturaFragmentDirections
import com.seba.mitantonavigationdrawer.ui.SharedViewModel


class MisDatosFragment : Fragment(R.layout.fragment_mis_datos) {


    private var _binding: FragmentMisDatosBinding? = null
    private val sharedViewModel by activityViewModels<SharedViewModel>()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var fragmentoAnteriorTag: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val misDatosViewModel =
            ViewModelProvider(this).get(MisDatosViewModel::class.java)

        _binding = FragmentMisDatosBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa
        //childFragmentManager.beginTransaction().add(R.id.nav_host_fragment,AlmacenesFragment()).commit()
        replaceFragment(AlmacenesFragment())
        //findNavController().navigate(R.id.action_nav_mis_datos_to_nav_almacenes)

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.nav_almacenes -> replaceFragment(AlmacenesFragment())
                R.id.nav_tipos_de_productos -> replaceFragment(TiposDeProductosFragment())
                R.id.nav_productos ->  replaceFragment(ProductosFragment())
                R.id.nav_proveedores -> replaceFragment(ProveedoresFragment())
                R.id.nav_clientes -> replaceFragment(ClientesFragment())
                else -> {}

            }
            true
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Navegar a MisDatosFragment y pasar un identificador para saber qué fragmento llamar luego
                // val action = EditarClienteFragmentDirections.actionEditarClienteFragmentToMisDatosFragment("clientes")
                val action = R.id.action_nav_mis_datos_to_nav_inicio
                findNavController().navigate(action)
            }
        })

      /*  val destino = arguments?.getString("destino")

        Log.i("destino","$destino")
        when (destino) {
            "clientes" -> replaceFragment(ClientesFragment())
            "proveedores" -> replaceFragment(ProveedoresFragment())
            "productos" -> replaceFragment(ProductosFragment())
            "tipos de producto" -> replaceFragment(TiposDeProductosFragment())
            "almacenes" -> replaceFragment(AlmacenesFragment())
        }*/

       /* binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.almacenes -> {
                    fragmentoAnteriorTag = "fragment_almacenes"
                    replaceFragment(AlmacenesFragment(),"fragment_almacenes")
                    true
                }
                R.id.tipos_de_productos -> {
                    fragmentoAnteriorTag = "fragment_tipos_producto"
                    replaceFragment(TiposDeProductosFragment(),"fragment_tipos_producto")
                    true
                }
                R.id.productos ->  {
                    fragmentoAnteriorTag = "tag_añadir_producto_editar"
                    replaceFragment(ProductosFragment(),"tag_añadir_producto_editar")
                    true
                }
                R.id.proveedores -> {
                    fragmentoAnteriorTag = "fragment_proveedores"
                    replaceFragment(ProveedoresFragment(),"fragment_proveedores")
                    true
                }
                R.id.clientes -> {
                    fragmentoAnteriorTag = "fragment_clientes"
                    replaceFragment(ClientesFragment())
                    true
                }
                else -> false

            }
        }*/

      /*  val navHostFragment = childFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController =navHostFragment.findNavController()

        Log.i("navController","$navController")

        binding.bottomNavigationView.setupWithNavController(navController)

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.nav_almacenes -> {
                    navController.navigate(R.id.nav_almacenes)
                    true
                }
                R.id.nav_tipos_de_productos -> {
                    navController.navigate(R.id.nav_tipos_de_productos)
                    true
                }
                R.id.nav_productos ->  {
                    navController.navigate(R.id.nav_productos)
                    true
                }
                R.id.nav_proveedores -> {
                    navController.navigate(R.id.nav_proveedores)
                    true
                }
                R.id.nav_clientes -> {
                    navController.navigate(R.id.nav_clientes)
                    true
                }
                else -> false

            }
        }*/


        return root
    }
    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_fragment,fragment)
        fragmentTransaction.commit()

    }

    override fun onStart() {
        super.onStart()
        borrarListas()
    }

    fun showFragmentBIfNeeded() {
        val fragmentManager = parentFragmentManager
        val backStackEntryCount = fragmentManager.backStackEntryCount

        if (backStackEntryCount > 0) {
            // Obtener el tag del fragmento anterior
            val previousFragmentTag = fragmentManager.getBackStackEntryAt(backStackEntryCount - 1).name
            val previousFragment = fragmentManager.findFragmentByTag(previousFragmentTag)

            // Verificar si el fragmento anterior era FragmentA
            if (previousFragment is EditarProductoFragment) {
                // Mostrar FragmentB si FragmentA era el anterior
                val fragmentB = ProductosFragment()
                val transaction = fragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, fragmentB, "FRAGMENT_B_TAG")
                transaction.addToBackStack("FRAGMENT_B_TAG")
                transaction.commit()
            }
        }
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