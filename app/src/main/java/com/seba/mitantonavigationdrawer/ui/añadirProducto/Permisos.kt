package com.seba.mitantonavigationdrawer.ui.añadirProducto

import android.Manifest.*
import android.Manifest.permission.*
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/*class Permisos {
    fun checkCamaraPermiso(activity: Activity): Boolean{
        return if(ContextCompat.checkSelfPermission(activity, CAMERA) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(activity, CAMERA)){
                false
            } else{
                ActivityCompat.requestPermissions(activity, arrayOf(CAMERA),0)
                true
            }
        } else{
            true
        }
    }
}*/