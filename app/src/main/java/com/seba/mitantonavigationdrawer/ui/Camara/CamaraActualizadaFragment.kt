package com.seba.mitantonavigationdrawer.ui.Camara

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.*
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.fragment.app.activityViewModels
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentCamaraActualizadaBinding
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class CamaraActualizadaFragment : Fragment(R.layout.fragment_camara_actualizada) {
    private lateinit var imageView: ImageView
    private lateinit var galeria: Button
    private lateinit var camara: Button
    private lateinit var currentPhotoPath: String
    private var _binding: FragmentCamaraActualizadaBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            takePictureFromCamera()
        } else {
            Toast.makeText(requireContext(), "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    private val readMediaImagesPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pickImageFromGallery()
        } else {
            Toast.makeText(requireContext(), "Permiso de galería denegado", Toast.LENGTH_SHORT).show()
        }
    }

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
           // val imageBitmap = result.data?.extras?.get("data") as Bitmap
            val imageBitmap = BitmapFactory.decodeFile(currentPhotoPath)
            val resizedBitmap = resizeBitmap(imageBitmap, 800, 800)
           // val imageByteArray: ByteArray = bitmapToByteArray(imageBitmap)
            val imageByteArray: ByteArray = bitmapToByteArray(resizedBitmap)
            sharedViewModel.setImageData(imageByteArray)
            imageView.setImageBitmap(imageBitmap)
            roundImageView(imageBitmap)
        }
    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data
            imageUri?.let {
                val imageBitmap = getBitmapFromUri(it)
                val resizedBitmap = resizeBitmap(imageBitmap, 800 , 800)
               // val imageByteArray: ByteArray = bitmapToByteArray(imageBitmap)
                val imageByteArray: ByteArray = bitmapToByteArray(resizedBitmap)
                sharedViewModel.setImageData(imageByteArray)
                imageView.setImageBitmap(imageBitmap)
                roundImageView(imageBitmap)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflar el diseño para este fragmento
        _binding = FragmentCamaraActualizadaBinding.inflate(inflater, container, false)
        val root: View = binding.root
       // val view = inflater.inflate(R.layout.fragment_camara_actualizada, container, false)
        imageView = binding.imageView.findViewById(R.id.imageView)
        galeria = binding.selectImageButton.findViewById(R.id.selectImageButton)
        camara = binding.bCamara.findViewById(R.id.bCamara)

        galeria.setOnClickListener {
            pickImageFromGallery()
        }

        imageView.setOnClickListener {
            showImagePickerDialog()
        }

        camara.setOnClickListener {
            takePictureFromCamera()
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermissions()
    }

    private fun checkPermissions() {
        val permissions = arrayOf(
            CAMERA,
            READ_MEDIA_IMAGES
        )

        val permissionsToRequest = permissions.filter {
            //ContextCompat.checkSelfPermission(requireContext(),READ_MEDIA_VISUAL_USER_SELECTED) == PERMISSION_GRANTED
            ContextCompat.checkSelfPermission(requireContext(), it) != PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(requireActivity(), permissionsToRequest.toTypedArray(), 0)
        }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Tomar foto", "Seleccionar de la galería")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Elige una opción")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> cameraPermissionLauncher.launch(CAMERA)
                1 -> readMediaImagesPermissionLauncher.launch(READ_MEDIA_IMAGES)
            }
        }
        builder.show()
    }

    private fun takePictureFromCamera() {
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            Toast.makeText(requireContext(), "Error creando archivo de imagen", Toast.LENGTH_SHORT).show()
            null
        }
        photoFile?.also {
            val photoURI: Uri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                it
            )
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            }
            takePictureLauncher.launch(intent)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "PNG_${timestamp}_",
            ".png",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(Images.Media.EXTERNAL_CONTENT_URI,"image/*")
        pickImageLauncher.launch(intent)
    }
    private fun getBitmapFromUri(uri: Uri): Bitmap {
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        return BitmapFactory.decodeStream(inputStream)
    }

    private fun roundImageView(bitmap: Bitmap) {
        val roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(resources, bitmap)
        roundedBitmapDrawable.isCircular = true
        imageView.setImageDrawable(roundedBitmapDrawable)
    }
    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    private fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val scaleFactor = Math.min(maxWidth.toFloat() / width, maxHeight.toFloat() / height)
        val newWidth = (width * scaleFactor).toInt()
        val newHeight = (height * scaleFactor).toInt()
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
}
