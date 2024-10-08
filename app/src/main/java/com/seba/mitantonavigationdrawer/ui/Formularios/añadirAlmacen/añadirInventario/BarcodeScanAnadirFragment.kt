package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirInventario

import android.annotation.SuppressLint
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentBarcodeScanAnadirBinding
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import java.io.IOException

class BarcodeScanAnadirFragment: Fragment(R.layout.fragment_barcode_scan_anadir) {

    private val viewModel by activityViewModels<SharedViewModel>()
    private var _binding: FragmentBarcodeScanAnadirBinding? = null
    private lateinit var barcodeDetector: BarcodeDetector
    private lateinit var cameraSource: CameraSource
    var intentData = "algo"
    var codigoDeBarraAnadir: String? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val barcodeScanAnadirViewModel =
            ViewModelProvider(this).get(BarcodeScanAnadirViewModel::class.java)

        _binding = FragmentBarcodeScanAnadirBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa
        return root
    }

    private fun iniBc(){
        barcodeDetector = BarcodeDetector
            .Builder(requireContext())
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()
        cameraSource = CameraSource.Builder(requireContext(),barcodeDetector)
            .setRequestedPreviewSize(1920,1080)
            .setAutoFocusEnabled(true)
            //.setFacing(CameraSource.CAMERA_FACING_FRONT)
            .build()
        binding.svSurfaceViewAnadir!!.holder.addCallback(object : SurfaceHolder.Callback{
            @SuppressLint("MissingPermission")
            override fun surfaceCreated(holder: SurfaceHolder) {
                try{
                    cameraSource.start(binding.svSurfaceViewAnadir!!.holder)
                }catch(e: IOException){
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
        var segundaVez = false
        barcodeDetector.setProcessor(object : Detector.Processor<Barcode>{
            override fun release() {

            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if(barcodes.size()!=0){
                    if(!segundaVez){
                        val mediaPlayer = MediaPlayer.create(context, R.raw.beep_barcode_scan)  // Reemplaza "beep_sound" con el nombre de tu archivo de sonido
                        mediaPlayer.start()
                        Handler(Looper.getMainLooper()).postDelayed({
                            mediaPlayer.release()
                        },500)
                        segundaVez = true
                    }
                    binding.bActionAnadir.text = "ENCONTRADO"
                    binding.bActionAnadir.setBackgroundColor(Color.GREEN)
                    binding.txtBarcodeValueAnadir!!.post{
                        for (i in 0 until barcodes.size()) {
                            Handler(Looper.getMainLooper()).postDelayed({
                                intentData = barcodes.valueAt(i).displayValue
                                binding.txtBarcodeValueAnadir.text = intentData
                                codigoDeBarraAnadir = intentData
                                viewModel.CodigoDeBarraAnadir.value = codigoDeBarraAnadir
                            //activity?.finish()
                            },600)
                        }

                        setFragmentResult("Codigo de barra transferencia", bundleOf("codigo" to codigoDeBarraAnadir))
                    }
                   // val codigo = activity?.findViewById<EditText>(R.id.etCodigoDeBarra)
                   // codigo?.setText(codigoDeBarraAnadir)
                   // activity?.runOnUiThread {
                   //     Toast.makeText(requireContext(),codigoDeBarraAnadir, Toast.LENGTH_SHORT).show()
                        //obtenerCodigo(codigoDeBarra!!)
                   // }

                    //obtenerCodigo(arguments?.getString(CODIGO_DE_BARRA)!!)
                    // findNavController().navigate(R.id.action_nav_barcode_scan_to_nav_añadir_transferencia)
                }

            }

        })
    }

    override fun onPause(){
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