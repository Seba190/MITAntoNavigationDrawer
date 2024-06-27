package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.transferencias.editarTransferencia

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentBarcodeScanBinding
import java.io.IOException
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.widget.CheckBox
import android.widget.EditText
import androidx.navigation.fragment.findNavController
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import com.seba.mitantonavigationdrawer.MainActivity
import com.seba.mitantonavigationdrawer.databinding.FragmentBarcodeScanEditarTransferenciaBinding
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto.AlertasAlmacenesFragment
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirTransferencia.AnadirTransferenciaFragment.Companion.CODIGO_DE_BARRA
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirTransferencia.BarcodeScanViewModel
import com.seba.mitantonavigationdrawer.ui.SharedViewModel


class BarcodeScanEditarTransferenciaFragment : Fragment(R.layout.fragment_barcode_scan_editar_transferencia) {


    private val viewModel by activityViewModels<SharedViewModel>()

    // private val args: AnadirTransferenciaFragmentArgs by navArgs()
    private var _binding: FragmentBarcodeScanEditarTransferenciaBinding? = null

    // private var codigoDeBarra: String? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var barcodeDetector: BarcodeDetector
    private lateinit var cameraSource: CameraSource
    var intentData = "algo"
    var codigoDeBarraTransferencia: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val barcodeScanEditarTransferenciaViewModel =
            ViewModelProvider(this).get(BarcodeScanEditarTransferenciaViewModel::class.java)

        _binding = FragmentBarcodeScanEditarTransferenciaBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa
        // binding.bAction.setBackgroundColor(resources.getColor(R.color.red))
        //  if(binding.txtBarcodeValue.text == intentData){
        //     obtenerCodigo(arguments?.getString(CODIGO_DE_BARRA)!!)
        // }
        return root

    }

    private fun iniBc() {
        barcodeDetector = BarcodeDetector
            .Builder(requireContext())
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()
        cameraSource = CameraSource.Builder(requireContext(), barcodeDetector)
            .setRequestedPreviewSize(1920, 1080)
            .setAutoFocusEnabled(true)
            //.setFacing(CameraSource.CAMERA_FACING_FRONT)
            .build()
        binding.svSurfaceViewEditarTransferencia!!.holder.addCallback(object : SurfaceHolder.Callback {
            @SuppressLint("MissingPermission")
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    cameraSource.start(binding.svSurfaceViewEditarTransferencia!!.holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {

            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }

        })
        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {

            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() != 0) {
                    binding.bActionEditarTransferencia.text = "ENCONTRADO"
                    binding.bActionEditarTransferencia.setBackgroundColor(Color.GREEN)
                    val mediaPlayer = MediaPlayer.create(
                        context,
                        R.raw.beep_barcode_scan
                    )  // Reemplaza "beep_sound" con el nombre de tu archivo de sonido
                    mediaPlayer.start()
                    Handler(Looper.getMainLooper()).postDelayed({
                        mediaPlayer.release()
                    }, 500)
                    binding.txtBarcodeValueEditarTransferencia!!.post {
                        intentData = barcodes.valueAt(0).displayValue
                        binding.txtBarcodeValueEditarTransferencia.setText(intentData)
                        //activity?.finish()
                        codigoDeBarraTransferencia = intentData
                        /*val bundle = Bundle()
                        bundle.putString(CODIGO_DE_BARRA,codigoDeBarra)
                        arguments = bundle*/
                        viewModel.CodigoDeBarraTransferencia.value = codigoDeBarraTransferencia
                        setFragmentResult(
                            "Codigo de barra transferencia",
                            bundleOf("codigo" to codigoDeBarraTransferencia)
                        )
                    }
                    val codigo = activity?.findViewById<EditText>(R.id.etCodigoDeBarra)
                    codigo?.setText(codigoDeBarraTransferencia)
                  /*  activity?.runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            codigoDeBarraTransferencia,
                            Toast.LENGTH_LONG
                        ).show()
                        //obtenerCodigo(codigoDeBarra!!)
                    }*/

                    //obtenerCodigo(arguments?.getString(CODIGO_DE_BARRA)!!)
                    // findNavController().navigate(R.id.action_nav_barcode_scan_to_nav_añadir_transferencia)
                }

            }

        })
    }

    /* override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         setHasOptionsMenu(true)
     }

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
         super.onViewCreated(view, savedInstanceState)
        /* requireActivity().onBackPressedDispatcher
             .addCallback(viewLifecycleOwner) {
                 activity?.runOnUiThread {Toast.makeText(requireContext(),codigoDeBarraTransferencia,Toast.LENGTH_LONG).show()}
             }*/
         (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
     }*/

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // binding.clBarcodeScanProducto.isVisible = false
                /* val anadirProductoFragment = AnadirProductoFragment()
                 parentFragmentManager.beginTransaction()
                     .replace(R.id.clBarcodeScanProducto, anadirProductoFragment)
                     .commitNow()*/
                findNavController().navigate(R.id.action_nav_barcode_scan_to_nav_añadir_transferencia)
                return true

            }

        }

        return super.onOptionsItemSelected(item)

    }*/

    override fun onPause() {
        super.onPause()
        cameraSource!!.release()
    }

    override fun onResume() {
        super.onResume()
        iniBc()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}