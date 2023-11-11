package com.seba.mitantonavigationdrawer.ui.estadística

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentEstadisticaBinding

class EstadisticaFragment : Fragment(R.layout.fragment_estadistica) {

    private var _binding: FragmentEstadisticaBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val estadisticaViewModel =
            ViewModelProvider(this).get(EstadisticaViewModel::class.java)

        _binding = FragmentEstadisticaBinding.inflate(inflater, container, false)
        val root: View = binding.root
       //Aquí se programa

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}