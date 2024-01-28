package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto

import android.annotation.SuppressLint
import android.graphics.Color
import android.media.MediaPlayer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentBarcodeScanBinding
import com.seba.mitantonavigationdrawer.databinding.FragmentBarcodeScanProductoBinding
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirTransferencia.AnadirTransferenciaFragment
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirTransferencia.BarcodeScanFragmentDirections
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirTransferencia.BarcodeScanViewModel
import java.io.IOException

class BarcodeScanProductoFragment : Fragment() {

    // private val args: AnadirTransferenciaFragmentArgs by navArgs()
    private var _binding: FragmentBarcodeScanProductoBinding? = null
    // private var codigoDeBarra: String? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var barcodeDetector: BarcodeDetector
    private lateinit var cameraSource: CameraSource
    var intentData = "algo"
    var codigoDeBarra : String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val barcodeScanProductoViewModel =
            ViewModelProvider(this).get(BarcodeScanProductoViewModel::class.java)

        _binding = FragmentBarcodeScanProductoBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa
        // binding.bAction.setBackgroundColor(resources.getColor(R.color.red))
      //  if(binding.txtBarcodeValueProducto.text == intentData){
       //     obtenerCodigo(arguments?.getString(AnadirTransferenciaFragment.CODIGO_DE_BARRA)!!)
       // }
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
        binding.svSurfaceViewProducto!!.holder.addCallback(object : SurfaceHolder.Callback{
            @SuppressLint("MissingPermission")
            override fun surfaceCreated(holder: SurfaceHolder) {
                try{
                    cameraSource.start(binding.svSurfaceViewProducto!!.holder)
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
        barcodeDetector.setProcessor(object : Detector.Processor<Barcode>{
            override fun release() {
                Toast.makeText(requireContext()," El scanner del código de barra ha sido detenido",
                    Toast.LENGTH_LONG).show()


            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if(barcodes.size()!=0){
                    binding.bActionProducto.text = "ENCONTRADO"
                    binding.bActionProducto.setBackgroundColor(Color.GREEN)
                    val mediaPlayer = MediaPlayer.create(context, R.raw.beep_barcode_scan)  // Reemplaza "beep_sound" con el nombre de tu archivo de sonido
                    mediaPlayer.start()
                    Handler(Looper.getMainLooper()).postDelayed({
                        mediaPlayer.release()
                    },500)
                    binding.txtBarcodeValueProducto!!.post{
                        intentData = barcodes.valueAt(0).displayValue
                        binding.txtBarcodeValueProducto.setText(intentData)
                        //activity?.finish()
                        codigoDeBarra = intentData
                        /*val bundle = Bundle()
                        bundle.putString(AnadirTransferenciaFragment.CODIGO_DE_BARRA,codigoDeBarra)
                        arguments = bundle*/
                        setFragmentResult("Codigo de barra producto", bundleOf("codigo" to codigoDeBarra))
                    }
                    val codigo = activity?.findViewById<EditText>(R.id.tvCodigoBarraProducto)
                    codigo?.setText(codigoDeBarra)
                    activity?.runOnUiThread {
                        Toast.makeText(requireContext(),codigoDeBarra, Toast.LENGTH_LONG).show()
                        //obtenerCodigo(codigoDeBarra!!)
                    }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner) {
                activity?.runOnUiThread { Toast.makeText(requireContext(),codigoDeBarra, Toast.LENGTH_LONG).show()}
            }



    }

    private fun obtenerCodigo(code: String){
     //   val action = BarcodeScanFragmentDirections.actionNavBarcodeScanToNavAñadirTransferencia(code)
      //  findNavController().navigate(action)
    }


}