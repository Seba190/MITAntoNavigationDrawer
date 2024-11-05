package com.seba.mitantonavigationdrawer.ui

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class ConnectionChecker(private val context: Context, private val serverUrl: String, val activity: Activity) {
    private val handler = Handler(Looper.getMainLooper())
    private val checkInterval: Long = 3000 // 3 segundos
    private val queue = Volley.newRequestQueue(context)
    private var lastConnectionStatus = true

    private val checkConnectionRunnable = object : Runnable {
        override fun run() {
            val stringRequest = StringRequest(
                Request.Method.GET, serverUrl,
                Response.Listener<String> {
                    if (!lastConnectionStatus){
                        lastConnectionStatus = true
                        Toast.makeText(context,"Conectado al servidor", Toast.LENGTH_SHORT).show()
                    }
                                          },
                Response.ErrorListener {
                    if (lastConnectionStatus) {  // Solo ejecuta el diálogo si la última conexión fue exitosa
                        lastConnectionStatus = false
                        dialogServer(activity)  // Muestra el diálogo de error
                    }
                }

            )
            queue.add(stringRequest)
            handler.postDelayed(this, checkInterval)
        }
    }

    fun startCheckingConnection() {
        handler.post(checkConnectionRunnable)
    }

    fun stopCheckingConnection() {
        handler.removeCallbacks(checkConnectionRunnable)
    }

      fun dialogServer(activity: Activity){
        val options = arrayOf("Recargar", "Salir")
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Hay problemas con conectar la aplicación al servidor")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> handler.post(checkConnectionRunnable)
                1 -> activity.finishAffinity()
            }
        }
        builder.show()
    }
}
