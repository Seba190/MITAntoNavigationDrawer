package com.seba.mitantonavigationdrawer.ui.inicio

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentInicioBinding

class InicioFragment : Fragment() {

    private var _binding: FragmentInicioBinding? = null

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
        val root = inflater.inflate(R.layout.fragment_inicio, container, false)
        val estadistics = root.findViewById<TextView>(R.id.tvStatistics)
        val addInventory = root.findViewById<CardView>(R.id.cvAddInventory)
       // val inicio = root.findViewById<ViewGroup>(R.id.nav_inicio)
        val currentActivity = activity
        estadistics.setOnClickListener {
                findNavController().navigate(R.id.action_nav_inicio_to_nav_statistics)
            }
        addInventory.setOnClickListener {
            findNavController().navigate(R.id.action_nav_inicio_to_nav_añadir_inventario)
        }
    /*    search.setOnClickListener {
            val intent = Intent(currentActivity, SearchActivity::class.java)
            startActivity(intent)
        }*/

             //findNavController().navigate(R.id.action_nav_statistics_to_nav_inicio)

           /*findNavController().addOnDestinationChangedListener{_,destination,_ ->
            if (root.findViewById<View?>(R.id.nav_inicio) == destination){
                findNavController().popBackStack()
            }
        }*/
        //Aquí se programa
      //  initListeners()

        return root
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
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}