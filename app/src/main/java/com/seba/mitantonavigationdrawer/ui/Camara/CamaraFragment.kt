package com.seba.mitantonavigationdrawer.ui.Camara

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.TextureView
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.impl.PreviewConfig
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.CircleCropTransformation
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentCamaraBinding
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import java.io.ByteArrayOutputStream
import java.lang.System.load


class CamaraFragment : Fragment(R.layout.fragment_camara){

    private var _binding: FragmentCamaraBinding? = null
    private val sharedViewModel: SharedViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val CAMERA_REQUEST_CODE = 1
    private val GALLERY_REQUEST_CODE = 2


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val camaraViewModel =
            ViewModelProvider(this).get(CamaraViewModel::class.java)

        _binding = FragmentCamaraBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa

        binding.buttonCamera.setOnClickListener {
            cameraCheckPermission()
        }

        binding.buttonGallery.setOnClickListener {
            galleryCheckPermission()
        }
       // binding.buttonVolver.setOnClickListener {
        //    findNavController().navigate(R.id.action_nav_camara_to_nav_añadir_producto)
       // }

       // sharedViewModel.setImageData(bitmap)

        binding.imageView.setOnClickListener{
            val pictureDialog = AlertDialog.Builder(requireContext())
            pictureDialog.setTitle("Seleccionar")
            val pictureDialogItem = arrayOf("Seleccionar de la galería",
                "Capturar foto de la camara")
            pictureDialog.setItems(pictureDialogItem){
                dialog, which ->
                when(which){
                 0 -> gallery()
                 1 -> camera()
                }
            }
            pictureDialog.show()
        }


        return root
    }
    private fun galleryCheckPermission(){
        Dexter.withContext(requireContext()).withPermission(
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(object: PermissionListener{
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                gallery()
            }

            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                Toast.makeText(requireContext(),"Tienes denegado los permisos de almacenamiento para seleccionar la imagen "
                    ,Toast.LENGTH_SHORT).show()

                showRotationalDIalogForPermmission()
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: PermissionRequest?,
                p1: PermissionToken?
            ) {
               showRotationalDIalogForPermmission()
            }

        }).onSameThread().check()

    }

    private fun gallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }
    private fun cameraCheckPermission(){
        Dexter.withContext(requireContext())
            .withPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA).withListener(
                    object : MultiplePermissionsListener{
                        override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                            report?.let{
                                if(report.areAllPermissionsGranted()){
                                    camera()
                                }
                            }
                        }

                        override fun onPermissionRationaleShouldBeShown(
                            p0: MutableList<PermissionRequest>?,
                            p1: PermissionToken?
                        ) {
                           showRotationalDIalogForPermmission()
                        }

                    }
                ).onSameThread().check()
    }
    private fun camera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent,CAMERA_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){

            when(requestCode){
                CAMERA_REQUEST_CODE -> {
                    //aqui iba el bitmap, pero ahora esta arriba
                        val bitmap = data?.extras?.get("data") as Bitmap
                        binding.imageView.load(bitmap) {
                        crossfade(true)
                        crossfade(1000)
                        transformations(CircleCropTransformation())

                    }
                    val imageByteArray: ByteArray = bitmap.toByteArray()
                    sharedViewModel.setImageData(imageByteArray)
                }
                GALLERY_REQUEST_CODE ->{
                        val Uri:Uri? = data?.data
                        var os =ByteArrayOutputStream()
                        var inputStream = Uri?.let {this.requireContext().contentResolver.openInputStream(it)}
                        var byteArray = inputStream?.readBytes()
                        binding.imageView.load(Uri){
                        crossfade(true)
                        crossfade(1000)
                        transformations(CircleCropTransformation())
                    }
                    val imageByteArray: ByteArray? = byteArray
                    sharedViewModel.setImageData(imageByteArray)
                }
            }
        }
    }

    private fun showRotationalDIalogForPermmission(){
        AlertDialog.Builder(requireContext())
            .setMessage("Parece que has desactivado los permisos necesarios para esta función." +
                    "Se puede habilitar en la configuración de la aplicación.")
            .setPositiveButton("Ir a la configuración."){_,_->
                try{
                    val intent= Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts(  "package",requireContext().packageName,null)
                    intent.data = uri
                    startActivity(intent)

                }catch(e:ActivityNotFoundException){
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancelar"){dialog, _->
                dialog.dismiss()
            }.show()
    }

    fun Bitmap.toByteArray(): ByteArray {
        val stream = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.PNG,100,stream)
        return stream.toByteArray()
    }

    fun ByteArray.toBitmap(): Bitmap {
        return BitmapFactory.decodeByteArray(this, 0, this.size)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}