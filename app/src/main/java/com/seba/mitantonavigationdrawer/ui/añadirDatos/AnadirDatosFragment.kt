package com.seba.mitantonavigationdrawer.ui.añadirDatos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentAnadirDatosBinding
import com.seba.mitantonavigationdrawer.databinding.FragmentEstadisticaBinding
import com.seba.mitantonavigationdrawer.ui.estadística.EstadisticaViewModel
import com.seba.mitantonavigationdrawer.ui.inicio.InicioFragment

class AnadirDatosFragment : Fragment(R.layout.fragment_anadir_datos) {

    private var _binding: FragmentAnadirDatosBinding? = null

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}