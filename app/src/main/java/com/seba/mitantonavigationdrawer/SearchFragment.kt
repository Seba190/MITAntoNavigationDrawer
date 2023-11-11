package com.seba.mitantonavigationdrawer


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentEstadisticaBinding
import com.seba.mitantonavigationdrawer.databinding.FragmentSearchBinding
import com.seba.mitantonavigationdrawer.ui.estadística.EstadisticaViewModel

class EstadisticaFragment : Fragment(R.layout.fragment_search) {

    private var _binding: FragmentSearchBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       // val estadisticaViewModel =
        //    ViewModelProvider(this).get(SearchViewModel::class.java)

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}