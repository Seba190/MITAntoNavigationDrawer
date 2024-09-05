package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transacciones

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
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

class TransaccionesFragment : Fragment(R.layout.fragment_transacciones) {


    private var _binding: FragmentTransaccionesBinding? = null

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
    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}