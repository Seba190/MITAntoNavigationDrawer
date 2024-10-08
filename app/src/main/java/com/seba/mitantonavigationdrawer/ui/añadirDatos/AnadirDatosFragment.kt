package com.seba.mitantonavigationdrawer.ui.añadirDatos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentAnadirDatosBinding
import com.seba.mitantonavigationdrawer.databinding.FragmentEstadisticaBinding
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import com.seba.mitantonavigationdrawer.ui.estadística.EstadisticaViewModel
import com.seba.mitantonavigationdrawer.ui.inicio.InicioFragment

class AnadirDatosFragment : Fragment(R.layout.fragment_anadir_datos) {

    private var _binding: FragmentAnadirDatosBinding? = null
    private val sharedViewModel: SharedViewModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val anadirDatosViewModel =
            ViewModelProvider(this).get(AnadirDatosViewModel::class.java)

        _binding = FragmentAnadirDatosBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa
        val añadirAlmacen = root.findViewById<CardView>(R.id.tvAñadirAlmacen)
        val añadirDatos = root.findViewById<CardView>(R.id.containerAñadirDatos)
        val añadirProducto = root.findViewById<CardView>(R.id.tvAñadirProducto)
        val añadirProveedor = root.findViewById<CardView>(R.id.tvAñadirProveedor)
        val añadirCliente = root.findViewById<CardView>(R.id.tvAñadirCliente)
        val añadirTipoDeProducto = root.findViewById<CardView>(R.id.tvAñadirTipoProducto)

        añadirAlmacen.setOnClickListener {
            findNavController().navigate(R.id.action_nav_añadir_datos_to_nav_añadir_almacen)
        }

        añadirDatos.setOnClickListener {
            showFragment()
        }
        añadirProducto.setOnClickListener {
            findNavController().navigate(R.id.action_nav_añadir_datos_to_nav_añadir_producto)
        }
        añadirProveedor.setOnClickListener {
            findNavController().navigate(R.id.action_nav_añadir_datos_to_nav_añadir_proveedor)
        }
        añadirCliente.setOnClickListener {
            findNavController().navigate(R.id.action_nav_añadir_datos_to_nav_añadir_cliente)
        }
        añadirTipoDeProducto.setOnClickListener {
            findNavController().navigate(R.id.action_nav_añadir_datos_to_nav_añadir_tipo_de_producto)
        }
        borrarListas()
        return root
    }
   /* private fun hideFragment() {
        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)

        // Reemplaza "R.id.fragment_container" con el ID del contenedor donde agregaste el fragmento en tu diseño
        val myFragment = fragmentManager.findFragmentById(R.id.fragment_add_data)
        // Oculta el fragmento
        if (myFragment != null) {
            // getView()?.setVisibility(View.GONE)
            fragmentTransaction.hide(myFragment)
        }

        // Commit la transacción
        fragmentTransaction.commit()

    }*/

    private fun showFragment() {
        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)

        // Reemplaza "R.id.fragment_container" con el ID del contenedor donde agregaste el fragmento en tu diseño
        val myFragment = InicioFragment()
        fragmentTransaction.replace(R.id.fragment_add_data, myFragment)

        // Puedes agregar transiciones o personalizar según tus necesidades
        // fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)

        // Commit la transacción
        fragmentTransaction.commit()
    }

    fun borrarListas(){
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