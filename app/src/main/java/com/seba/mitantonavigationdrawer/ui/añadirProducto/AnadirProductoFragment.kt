package com.seba.mitantonavigationdrawer.ui.añadirProducto

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.core.graphics.get
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentAnadirProductoBinding
import org.json.JSONObject


//@Suppress("UNREACHABLE_CODE")
class AnadirProductoFragment : Fragment(R.layout.fragment_anadir_producto){
   // UploadRequestBody.UploadCallback {

    private var selectedImageUrl: Uri? = null
    private var _binding: FragmentAnadirProductoBinding? = null
   // private lateinit var  viewModel: AnadirProductoViewModel
    private var base64 = ""
    var TextEstado :Int = 1
    var TextTipoDeProducto : Int = 1
    var TvAutoCompleteTipoDeProducto: AutoCompleteTextView? = null
    var TextNombre: EditText? = null
    var TextPeso: EditText? = null
    var TextVolumen: EditText? = null
    var TextFoto: EditText? = null
    var TextCodigoBarra: EditText? = null
    var TextCodigoEmbalaje: EditText? = null
    var TextUnidadesEmbalaje: EditText? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //El codigo del onActivityResult

   /* private val camaraLauncher = registerForActivityResult(StartActivityForResult()){
     result ->
        if(result.resultCode == RESULT_OK){
            val extras = result.data?.extras
            val imgBitmap = extras?.get("data") as Bitmap?
            binding.ivImagen.setImageBitmap(imgBitmap)
        }
    }*/


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       // val anadirDatosViewModel =
        //    ViewModelProvider(this).get(AnadirProductoViewModel::class.java)

        _binding = FragmentAnadirProductoBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa

        ///////La lista desplegable y la subida de los datos al servidor////////////
        TvAutoCompleteTipoDeProducto = binding.tvAutoCompleteTipoDeProducto.findViewById(R.id.tvAutoCompleteTipoDeProducto)
        TextNombre = binding.etNombreProducto.findViewById(R.id.etNombreProducto)
        TextPeso = binding.etPesoProducto.findViewById(R.id.etPesoProducto)
        TextVolumen = binding.etVolumenProducto.findViewById(R.id.etVolumenProducto)
        TextCodigoBarra = binding.etCodigoBarraProducto.findViewById(R.id.etCodigoBarraProducto)
        TextCodigoEmbalaje = binding.etCodigoBarraEmbalaje.findViewById(R.id.etCodigoBarraEmbalaje)
        TextUnidadesEmbalaje = binding.etUnidadesEmbalaje.findViewById(R.id.etUnidadesEmbalaje)
        TextFoto = binding.etNombreImagen.findViewById(R.id.etNombreImagen)

        binding.etNombreProducto.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etPesoProducto.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etVolumenProducto.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etCodigoBarraProducto.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etCodigoBarraEmbalaje.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)
        binding.etUnidadesEmbalaje.getBackground().setColorFilter(getResources().getColor(R.color.color_list),
            PorterDuff.Mode.SRC_ATOP)

        ListaDesplegableProducto()

        binding.buttonProducto.setOnClickListener {
            ValidacionesIdInsertarDatos()
        }






        /*     binding.ivImagen.setOnClickListener {
            opeinImageChooser()
        }
        binding.buttonImagen.setOnClickListener {
            uploadImage()

        }*/


     /*  anadirDatosViewModel.mensaje.observe(viewLifecycleOwner){
           Toast.makeText(requireContext(),it,Toast.LENGTH_LONG).show()
       }*/

     /*   binding.buttonTomar.setOnClickListener {
            val isPermiso = Permisos().checkCamaraPermiso(requireActivity())
            if(isPermiso){
                abrirCamara()
            }
        }*/

      /*  binding.buttonImagen.setOnClickListener {
            base64 = viewModel.convertirBitmapToBase64(binding.ivImagen)
            if(binding.etNombreImagen.text.isNullOrBlank()){
                Toast.makeText(requireContext(),"Debes poner el nombre de la imagen",Toast.LENGTH_LONG).show()
            } else{
                val imagen = ImageModel(//System.currentTimeMillis().toString(),binding.etNombreProducto.text.toString(),TextTipoDeProducto.toString(),TextEstado.toString(),binding.etPesoProducto.toString(),binding.etVolumenProducto.toString(),
                    base64,
                   /* "","",""*/)
                viewModel.enviarFoto(imagen)
                binding.etNombreImagen.setText("")
                binding.ivImagen.setImageResource(0)
            }
        }*/
        return root

    }

    private fun ValidacionesIdInsertarDatos() {
        //INICIO EXPERIMIENTO!!!!!!!!!!!!!!!!!!!!!!!!!! (FUNCIONO)
        val queue =Volley.newRequestQueue(requireContext())
        val url ="http://186.64.123.248/Producto/registro.php"
        val jsonObjectRequest = object: StringRequest(
            Request.Method.POST,url,
            { response ->
                if(!TextNombre?.text.toString().isBlank()){
                    val id = JSONObject(response).getString("ID_PRODUCTO")
                    //unico = 1
                    //no unico = 0
                    val unico = JSONObject(response).getString("PRODUCTO_UNICO")
                    if (unico == "1") {
                        //Aqui va el código para validar el almacen
                        val url1 = "http://186.64.123.248/Producto/insertar.php" // Reemplaza esto con tu URL de la API
                        val queue1 =Volley.newRequestQueue(requireContext())
                        val stringRequest = object: StringRequest(
                            Request.Method.POST,
                            url1,
                            { response ->
                                Toast.makeText(requireContext(), "Producto agregado exitosamente. El id de ingreso es el número $id ", Toast.LENGTH_LONG).show()
                                TextNombre?.setText("")
                                TextPeso?.setText("")
                                TextVolumen?.setText("")
                                TextFoto?.setText("")
                                TextCodigoBarra?.setText("")
                                TextCodigoEmbalaje?.setText("")
                                TextUnidadesEmbalaje?.setText("")
                                TvAutoCompleteTipoDeProducto?.setText("Eliga una opción")
                            },
                            { error ->
                                Toast.makeText(requireContext(),"No se ha introducido el producto a la base de datos", Toast.LENGTH_LONG).show()
                               // Toast.makeText(requireContext(),"Error $error", Toast.LENGTH_LONG).show()
                            }
                        )

                        {
                            override fun getParams(): MutableMap<String, String> {
                                val parametros = HashMap<String, String>()
                                parametros.put("ID_PRODUCTO", id.toString())
                                parametros.put("PRODUCTO", TextNombre?.text.toString().uppercase())
                                parametros.put("TIPO_PRODUCTO", TvAutoCompleteTipoDeProducto?.text.toString())
                                parametros.put("ESTADO_PRODUCTO", TextEstado.toString())
                                parametros.put("PESO_PRODUCTO", TextPeso?.text.toString())
                                parametros.put("VOLUMEN_PRODUCTO", TextVolumen?.text.toString())
                                parametros.put("FOTO_PRODUCTO", TextFoto?.text.toString().uppercase())
                                parametros.put("CODIGO_BARRA_PRODUCTO", TextCodigoBarra?.text.toString().uppercase())
                                parametros.put("CODIGO_BARRA_EMBALAJE", TextCodigoEmbalaje?.text.toString().uppercase())
                                parametros.put("UNIDADES_EMBALAJE", TextUnidadesEmbalaje?.text.toString())
                                return parametros
                            }
                        }
                        queue1.add(stringRequest)

                    }
                    else if (unico == "0"){
                        VolleyError("El almacén ya se encuentra en la base de datos")
                        //queue1.cancelAll(TAG)
                        //jsonObjectRequest1.cancel()
                        Toast.makeText(requireContext(), "El almacén ya se encuentra en la base de datos", Toast.LENGTH_LONG).show()
                    }
                }
                else{
                    Toast.makeText(requireContext(), "El nombre del almacén es obligatorio", Toast.LENGTH_LONG).show()
                }

                // TextId?.setText(response.getString("ID_ALMACEN"))
                // Toast.makeText(requireContext(),"Id ingresado correctamente al formulario.", Toast.LENGTH_LONG).show()
            }, { error ->
                // Toast.makeText(requireContext(),"Conecte la aplicación al servidor", Toast.LENGTH_LONG).show()
                Toast.makeText(requireContext(),"Error $error", Toast.LENGTH_LONG).show()
            }
        ){
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("PRODUCTO", TextNombre?.text.toString().uppercase())
                return parametros
            }
        }
        queue.add(jsonObjectRequest)
    }

    private fun ListaDesplegableProducto() {
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 ="http://186.64.123.248/Producto/TipoDeProducto.php"
        val jsonObjectRequest1 = JsonObjectRequest(
            Request.Method.GET,url1, null,
            { response ->
                // Obtén el array de opciones desde el objeto JSON
                val jsonArray = response.getJSONArray("Lista2")
                // Convierte el array JSON a una lista mutable
                val opcionesList = mutableListOf<String>()
                for (i in 0 until jsonArray.length()) {
                    opcionesList.add(jsonArray.getString(i).removeSurrounding("'","'"))
                }
                //Crea un adpatador para el dropdown
                val adapter = ArrayAdapter(requireContext(),R.layout.list_item_producto,opcionesList)
                //binding.tvholaMundo?.setText(response.getString("Lista"))
                TvAutoCompleteTipoDeProducto?.setAdapter(adapter)

                TvAutoCompleteTipoDeProducto?.onItemClickListener = AdapterView.OnItemClickListener {
                        parent, view, position, id ->
                    val itemSelected = parent.getItemAtPosition(position)
                }
            }, { error ->
                Toast.makeText(requireContext(), " La aplicación no se ha conectado con el servidor", Toast.LENGTH_LONG).show()
            }
        )
        queue1.add(jsonObjectRequest1)
    }


   /* private fun abrirCamara() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(intent.resolveActivity(requireContext().packageManager) != null){
            camaraLauncher.launch(intent)

        }
    }*/


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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


}

/////Upload Image to Server using Retrofit in Android Studio Kotlin Tutorial ////////////////////


   /* private fun uploadImage() {
        if (selectedImageUrl == null) {
            binding.layoutRoot.snackbar("Selecciona una imagen primero")
            return
        }

        val parcelFileDescriptor = context?.contentResolver?.openFileDescriptor(
            selectedImageUrl!!,"r", null
        )?: return

        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val file = requireContext().contentResolver.getFileName(selectedImageUrl!!)
            ?.let { File(context?.cacheDir, it) }
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
        binding.pbProducto.progress = 0
        val body =UploadRequestBody(file!!,"image", this)
        MiApi().uploadImage(MultipartBody.Part.createFormData(
            "image",
            file.name,
            body
        ),
           RequestBody.create(MediaType.parse("multipart/form-data"),"json")
        ).enqueue(object  : retrofit2.Callback<UploadResponse> {
            override fun onResponse(
                call: Call<UploadResponse>,
                response: Response<UploadResponse>
            ) {
               response.body()?.let{
                   binding.layoutRoot.snackbar(it.message)
                   binding.pbProducto.progress = 100
               }
            }

            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                binding.layoutRoot.snackbar(t.message!!)
                binding.pbProducto.progress = 0
            }

        })

    }*/

// private fun opeinImageChooser() {
// Intent(Intent.ACTION_PICK).also {
// it.type = "image/*"
// val mimeTypes = arrayOf("image/jpeg", "image/png")
// it.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
// // startActivityForResult(it, REQUEST_CODE_IMAGE)
// }
// }*/
//
// override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
// super.onActivityResult(requestCode, resultCode, data)
// //   if (requestCode == Activity.RESULT_OK) {
// //     when (requestCode) {
// //       REQUEST_CODE_IMAGE -> {
// //         selectedImageUrl = data?.data
// //       binding.ivImagen.setImageURI(selectedImageUrl)
// // }
// // }
// // }
// }
//
// companion object {
// const val REQUEST_CODE_IMAGE = 101
// }
//
//
//
//
// override fun onProgressUpdate(percentage: Int) {
// binding.pbProducto.progress = percentage
// }
// }
//
//
//
// private fun ContentResolver.getFileName(selectedImageUri: Uri): String{
// var name = ""
// val returnCursor = this.query(selectedImageUri,null,null,null,null)
// if(returnCursor!=null){
// val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
// returnCursor.moveToFirst()
// name = returnCursor.getString(nameIndex)
// returnCursor.close()
// }
// return name
// }
// private fun View.snackbar(message:String){
// Snackbar.make(this,message,Snackbar.LENGTH_LONG).also{
// snackbar -> snackbar.setAction("OK"){
// snackbar.dismiss()
// }
// }.show()
// }