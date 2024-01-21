package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentMisDatosBinding
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.AnadirAlmacenFragmentDirections
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.AlmacenesFragment
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.clientes.ClientesFragment
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos.ProductosFragment
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.proveedores.ProveedoresFragment
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.tiposDeProductos.TiposDeProductosFragment



class MisDatosFragment : Fragment(R.layout.fragment_mis_datos) {


    private var _binding: FragmentMisDatosBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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
        replaceFragment(AlmacenesFragment())
        //findNavController().navigate(R.id.action_nav_mis_datos_to_nav_almacenes)
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.almacenes ->replaceFragment(AlmacenesFragment())
                R.id.tipos_de_productos -> replaceFragment(TiposDeProductosFragment())
                R.id.productos -> replaceFragment(ProductosFragment())
                R.id.proveedores -> replaceFragment(ProveedoresFragment())
                R.id.clientes -> replaceFragment(ClientesFragment())
               /* R.id.almacenes ->findNavController().navigate(R.id.action_nav_mis_datos_to_nav_almacenes)
                R.id.tipos_de_productos -> findNavController().navigate(R.id.action_nav_mis_datos_to_nav_tipos_de_productos)
                R.id.productos -> findNavController().navigate(R.id.action_nav_mis_datos_to_nav_productos)
                R.id.proveedores -> findNavController().navigate(R.id.action_nav_mis_datos_to_nav_proveedores)
                R.id.clientes -> findNavController().navigate(R.id.action_nav_mis_datos_to_nav_clientes)*/

                else ->{

                }
            }
            true
        }

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