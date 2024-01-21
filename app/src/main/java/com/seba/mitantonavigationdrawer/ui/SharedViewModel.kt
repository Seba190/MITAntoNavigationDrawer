package com.seba.mitantonavigationdrawer.ui

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val selectedImage = MutableLiveData<ByteArray?>()

    fun setImageData(data: ByteArray?) {
        selectedImage.value = data
    }
}