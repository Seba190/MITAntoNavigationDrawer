package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.tiposDeProductos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentTiposDeProductosBinding


class TiposDeProductosFragment : Fragment(R.layout.fragment_tipos_de_productos) {

    private var _binding: FragmentTiposDeProductosBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val tiposDeProductosViewModel =
            ViewModelProvider(this).get(TiposDeProductosViewModel::class.java)

        _binding = FragmentTiposDeProductosBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}