package com.seba.mitantonavigationdrawer.ui.exportarDatos

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentExportarDatosBinding


class ExportarDatosFragment : Fragment(R.layout.fragment_exportar_datos) {

    private var _binding: FragmentExportarDatosBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExportarDatosBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa
        initListeners()
        return root
    }

    private fun initListeners() {
        //Exportar a PDF

        binding.cvWarehouses.setOnClickListener {
            goToDialogSafeOrSend()
        }
        binding.cvTypesOfProductsExport.setOnClickListener {
            goToDialogSafeOrSend()
        }
        binding.cvProducts.setOnClickListener {
            goToDialogSafeOrSend()
        }
        binding.cvSuppliers.setOnClickListener {
            goToDialogSafeOrSend()
        }
        binding.cvCustomers.setOnClickListener {
            goToDialogSafeOrSend()
        }
        binding.cvTransactionsExport.setOnClickListener {
            goToDialogSafeOrSend()
        }
        binding.cvTranfersExport.setOnClickListener {
            goToDialogSafeOrSend()
        }
        binding.cvAlerts.setOnClickListener {
            goToDialogSafeOrSend()
        }
        binding.InventoriesExport.setOnClickListener {
            goToDialogSafeOrSend()
        }
        //Exportar a CSV

        binding.cvWarehousesExport2.setOnClickListener {
            goToDialogSafeOrSend()
        }
        binding.cvTypesOfProducts2.setOnClickListener {
            goToDialogSafeOrSend()
        }
        binding.cvProducts2.setOnClickListener {
            goToDialogSafeOrSend()
        }
        binding.cvSuppliers2.setOnClickListener {
            goToDialogSafeOrSend()
        }
        binding.cvCustomers2.setOnClickListener {
            goToDialogSafeOrSend()
        }
        binding.cvTransactionsExport2.setOnClickListener {
            goToDialogSafeOrSend()
        }
        binding.cvTranfersExport2.setOnClickListener {
            goToDialogSafeOrSend()
        }
        binding.cvAlerts2.setOnClickListener {
            goToDialogSafeOrSend()
        }
        binding.InventoriesExport2.setOnClickListener {
            goToDialogSafeOrSend()
        }

    }

    private fun goToDialogSafeOrSend() {
        val dialog = Dialog(requireActivity())
        dialog.setContentView(R.layout.safe_or_send)

        val tvGoBackExport : TextView =dialog.findViewById(R.id.tvGoBackExport)
        tvGoBackExport.setOnClickListener {
            dialog.hide()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}