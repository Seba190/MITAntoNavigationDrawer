package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos.editarProducto

import android.annotation.SuppressLint
import android.graphics.Color
import android.media.MediaPlayer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentBarcodeScanBinding
import com.seba.mitantonavigationdrawer.databinding.FragmentBarcodeScanEditarProductoBinding
import com.seba.mitantonavigationdrawer.databinding.FragmentBarcodeScanProductoBinding
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto.BarcodeScanProductoViewModel
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirTransferencia.AnadirTransferenciaFragment
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirTransferencia.BarcodeScanFragmentDirections
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirTransferencia.BarcodeScanViewModel
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import java.io.IOException

class BarcodeScanEditarProductoFragment : Fragment() {

    private val viewModel by activityViewModels<SharedViewModel>()
    // private val args: AnadirTransferenciaFragmentArgs by navArgs()
    private var _binding: FragmentBarcodeScanEditarProductoBinding? = null
    // private var codigoDeBarra: String? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var barcodeDetector: BarcodeDetector
    private lateinit var cameraSource: CameraSource
    var intentData = "algo"
    var codigoDeBarraProducto : String? = null
    var anadirProducto: MutableList<String> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val barcodeScanProductoViewModel =
            ViewModelProvider(this).get(BarcodeScanProductoViewModel::class.java)

        _binding = FragmentBarcodeScanEditarProductoBinding.inflate(inflater, container, false)
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
        var segundaVez = false
        barcodeDetector.setProcessor(object : Detector.Processor<Barcode>{
            override fun release() {
                // Toast.makeText(requireContext()," El scanner del código de barra ha sido detenido",
                //   Toast.LENGTH_SHORT).show()


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
                    binding.bActionProducto.text = "ENCONTRADO"
                    binding.bActionProducto.setBackgroundColor(Color.GREEN)
                    binding.txtBarcodeValueProducto!!.post{
                        for (i in 0 until barcodes.size()) {
                            Handler(Looper.getMainLooper()).postDelayed({
                                intentData = barcodes.valueAt(i).displayValue
                                binding.txtBarcodeValueProducto.text = intentData
                                codigoDeBarraProducto = intentData
                                viewModel.CodigoDeBarra.value = codigoDeBarraProducto
                            }, 600)
                            //activity?.finish()
                        }
                        //activity?.finish()
                        //setFragmentResult("Codigo de barra producto", bundleOf("codigo" to codigoDeBarra))
                    }
                    val codigo = activity?.findViewById<EditText>(R.id.tvCodigoBarraProducto)
                    codigo?.setText(codigoDeBarraProducto)
                  //  activity?.runOnUiThread {
                  //      Toast.makeText(requireContext(),codigoDeBarraProducto, Toast.LENGTH_SHORT).show()
                        //obtenerCodigo(codigoDeBarra!!)
                  //  }



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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner) {
                activity?.runOnUiThread { Toast.makeText(requireContext(),codigoDeBarraProducto, Toast.LENGTH_SHORT).show()}
            }*/
        setFragmentResultListener("Añadir Producto") {key, bundle ->
            anadirProducto.add(bundle.getString("nombre")!!)
            anadirProducto.add(bundle.getString("peso")!!)
            anadirProducto.add(bundle.getString("volumen")!!)
            anadirProducto.add(bundle.getString("tipoDeProducto")!!)
            anadirProducto.add(bundle.getString("unidadesEmbalaje")!!)
        }
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                viewModel.CodigoDeBarra.observe(viewLifecycleOwner) { newText ->
                    // if(!segundaVez) {
                    val pictureDialog = AlertDialog.Builder(requireContext())
                    pictureDialog.setTitle("¿Cómo quieres ingresar tu código?")
                    val pictureDialogItem = arrayOf(
                        "Código de Producto",
                        "Código de embalaje"
                    )
                    pictureDialog.setItems(pictureDialogItem) { dialog, which ->
                        when (which) {
                            0 -> setFragmentResult("Producto", bundleOf("Producto" to newText))
                            1 -> setFragmentResult("Embalaje", bundleOf("Embalaje" to newText))
                        }
                    }
                    pictureDialog.show()
                    // segundaVez = true
                    //}
                }
                // binding.clBarcodeScanProducto.isVisible = false
                /* val anadirProductoFragment = AnadirProductoFragment()
                 parentFragmentManager.beginTransaction()
                     .replace(R.id.clBarcodeScanProducto, anadirProductoFragment)
                     .commitNow()*/
                setFragmentResult("Añadir Producto vuelta",
                    bundleOf("nombre" to anadirProducto[0],
                        "peso" to anadirProducto[1],
                        "volumen" to anadirProducto[2],
                        "tipoDeProducto" to anadirProducto[3],
                        "unidadesEmbalaje" to anadirProducto[4]))
                findNavController().navigate(R.id.action_nav_barcode_scan_editar_producto_to_nav_editar_producto)
                return true

            }

        }

        return super.onOptionsItemSelected(item)

    }


    private fun obtenerCodigo(code: String){
        //   val action = BarcodeScanFragmentDirections.actionNavBarcodeScanToNavAñadirTransferencia(code)
        //  findNavController().navigate(action)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}