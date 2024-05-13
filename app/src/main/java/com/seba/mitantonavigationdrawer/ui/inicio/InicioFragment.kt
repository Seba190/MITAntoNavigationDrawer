package com.seba.mitantonavigationdrawer.ui.inicio

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentEditarTiposDeProductosBinding
import com.seba.mitantonavigationdrawer.databinding.FragmentInicioBinding
import com.seba.mitantonavigationdrawer.ui.añadirDatos.AnadirDatosFragment


class InicioFragment : Fragment() {

    private var _binding: FragmentInicioBinding? = null
    private lateinit var container_layout: FrameLayout
    private var fragmentVisible = false

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //val inicioViewModel = ViewModelProvider(this).get(InicioViewModel::class.java)


      //  _binding = FragmentInicioBinding.inflate(inflater, container, false)
       // val root: View = binding.root
        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //val estadistics = root.findViewById<TextView>(R.id.tvStatistics)
        val addInventory = root.findViewById<CardView>(R.id.cvAddInventory)
        val removeInventory = root.findViewById<CardView>(R.id.cvRemoveInventory)
       // val inicio = root.findViewById<ViewGroup>(R.id.nav_inicio)
       // estadistics.setOnClickListener {
         //       findNavController().navigate(R.id.action_nav_inicio_to_nav_statistics)
          //  }
        addInventory.setOnClickListener {
            findNavController().navigate(R.id.action_nav_inicio_to_nav_añadir_inventario)
        }
        removeInventory.setOnClickListener {
            findNavController().navigate(R.id.action_nav_inicio_to_nav_remover_inventario)
        }

       // addData.setOnClickListener {
        //    findNavController().navigate(R.id.action_nav_inicio_to_nav_añadir_datos)
       // }

      //  val home = binding.root.findViewById<RelativeLayout>(R.id.fragment_home)

      /*  home.setOnClickListener {
            showFragment()
        }*/
       try {
           inventarioTotal()
       }catch(e:Exception){
           binding.tvAInventoryNumber.text = 0.toString()
       }
        return root

        }

   private fun showFragment() {
        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)

        // Reemplaza "R.id.fragment_container" con el ID del contenedor donde agregaste el fragmento en tu diseño
        val myFragment = AnadirDatosFragment()
        fragmentTransaction.replace(R.id.fragment_home, myFragment)

        // Puedes agregar transiciones o personalizar según tus necesidades
        // fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)

        // Commit la transacción
        fragmentTransaction.commit()
    }
    /*  private fun initListeners() {
         // val estadistics = requireView().findViewById<TextView>(R.id.tvStatistics)
          val estadistics = binding.root.findViewById<TextView>(R.id.tvStatistics)
          estadistics.setOnClickListener {
              goToStatistics()
          }

      }

      private fun goToStatistics() {
       //  Navigation.createNavigateOnClickListener(R.id.fragment_estadisticas, null)
        findNavController().navigate(R.id.action_nav_inicio_to_nav_statistics2)
      }*/

    private fun inventarioTotal(){
        val queue2 = Volley.newRequestQueue(requireContext())
        val url2 = "http://186.64.123.248/Inicio/inventarioTotal.php"
        val jsonObjectRequest2 = JsonObjectRequest(
            Request.Method.GET, url2, null,
            { response ->
                try {
                    val inventario = response.getString("Inventario Total")
                    binding.tvAInventoryNumber.text = inventario.toString()
                }catch(e:Exception){

                }
            },{error ->
                Toast.makeText(requireContext(),"$error", Toast.LENGTH_LONG).show()

            } )
        queue2.add(jsonObjectRequest2)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}