package com.seba.mitantonavigationdrawer.ui.añadirProducto

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/*interface MiApi {
    @Multipart
    @POST("insertar.php")
    fun uploadImage(
        @Part image: MultipartBody.Part,
        create: RequestBody,
       // @Part("desc") desc: RequestBody
    ): Call<UploadResponse>
    companion object {
        operator fun invoke(): MiApi{
            return Retrofit.Builder()
                .baseUrl("http://186.64.123.248/Producto/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MiApi::class.java)
        }
    }
}*/