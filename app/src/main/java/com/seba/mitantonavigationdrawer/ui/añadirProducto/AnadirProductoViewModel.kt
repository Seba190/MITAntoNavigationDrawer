package com.seba.mitantonavigationdrawer.ui.añadirProducto

import androidx.lifecycle.ViewModel
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Base64
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

// class AnadirProductoViewModel : ViewModel() {
// var mensaje = MutableLiveData<String>()
// fun enviarFoto(image: ImageModel){
// viewModelScope.launch(Dispatchers.IO) {
// val response = RetrofitClient.webService.enviarImage(//image.ID_PRODUCTO,image.PRODUCTO,image.ID_TIPO_PRODUCTO,image.ESTADO_PRODUCTO,image.PESO_PRODUCTO,image.VOLUMEN_PRODUCTO,
// image.FOTO_PRODUCTO/*,image.CODIGO_BARRA_PRODUCTO,image.CODIGO_BARRA_EMBALAJE,image.UNIDADES_EMBALAJE)*/)
// withContext(Dispatchers.Main){
// if (response.isSuccess){
// mensaje.value = response.toString()
// }
//
// }
// }
// }
// fun convertirBitmapToBase64(ivFoto: ImageView): String{
// val bitmap = (ivFoto.drawable as BitmapDrawable).bitmap
// var fotoEnBase64 = ""
// if(bitmap != null){
// val byteArrayOutputStrean = ByteArrayOutputStream()
// bitmap.compress(Bitmap.CompressFormat.PNG, 100,byteArrayOutputStrean)
// val byteArray = byteArrayOutputStrean.toByteArray()
// fotoEnBase64 = Base64.encodeToString(byteArray,Base64.DEFAULT)
// }
// return fotoEnBase64
// }
//
// }