package com.seba.mitantonavigationdrawer.ui.preferencias

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentPreferenciasBinding

class PreferenciasFragment : Fragment(R.layout.fragment_preferencias) {

    private var _binding: FragmentPreferenciasBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       // val preferenciasViewModel =
         //   ViewModelProvider(this).get(PreferenciasViewModel::class.java)

        _binding = FragmentPreferenciasBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}