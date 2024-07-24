package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos.editarProducto

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentAlertasAlmacenesEditarBinding
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto.AlertasAlmacenesAdapter.*
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto.AnadirProductoFragment
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.productos.editarProducto.AlertasAlmacenesEditarAdapter.*
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.SocketTimeoutException


class AlertasAlmacenesEditarFragment() : Fragment(R.layout.fragment_alertas_almacenes_editar),
    OnTextChangeListenerEditar, OnCheckBoxClickListenerEditar {
    /* companion object {
         const val REQUEST_KEY_1 = "Prueba"
         const val REQUEST_KEY_2 = "AlertasAlmacen"
         const val REQUEST_KEY_3 = "Codigo de Barra"
         const val REQUEST_KEY_4 = "Codigo de Barra Transferencia"
     }*/
    private val sharedViewModel by activityViewModels<SharedViewModel>()
    private var listaDeAlmacenes : List<AlertasAlmacenesItemResponseEditar> = emptyList()
    private var _binding: FragmentAlertasAlmacenesEditarBinding? = null
    private lateinit var adapter: AlertasAlmacenesEditarAdapter
    private lateinit var retrofit: Retrofit
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val alertasAlmacenesEditarViewHolder =
            ViewModelProvider(this).get(AlertasAlmacenesEditarViewModel::class.java)

        _binding = FragmentAlertasAlmacenesEditarBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa

        retrofit = getRetrofit()
        gestionarRecyclerView()


        /* binding.bAlertaAlmacenVolver.setOnClickListener {
                 binding.rlAlertasAlmacenes.isVisible = false
                 val anadirProductoFragment = AnadirProductoFragment()
                 parentFragmentManager.beginTransaction()
                     .replace(R.id.rlAlertasAlmacenes, anadirProductoFragment)
                     .commitNow()
         }*/
        binding.rlAlertasAlmacenesEditar.setOnClickListener {
            binding.rlAlertasAlmacenesEditar.isVisible = false
            val editarProductoFragment = EditarProductoFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.rlAlertasAlmacenesEditar, editarProductoFragment)
                .commitNow()
        }
        binding.bAlertaAlmacenEliminarEditar.setOnClickListener {
            sharedViewModel.listaDeBodegas.clear()
            sharedViewModel.listaDeAlertas.clear()
            sharedViewModel.ListasDeAlertas.clear()
            sharedViewModel.ListasDeAlmacenes.clear()
            sharedViewModel.ListasDeProductosAlertas.clear()
            sharedViewModel.numeroAlertas.clear()
            Toast.makeText(requireContext(), "Se han eliminado las alertas exitosamente", Toast.LENGTH_LONG).show()
        }

        binding.bAgregarAlmacenEditar.setOnClickListener {
            adapter.getAllEditTextContents()
            adapter.getAllTextViewContents()
            //Toast.makeText(requireContext(),"${sharedViewModel.listaDeAlertas} y ${sharedViewModel.listaDeBodegas}",Toast.LENGTH_LONG).show()


            if (sharedViewModel.numeroAlertas.isEmpty()) {
                for (i in 0..<adapter.alertasAlmacenesList.size) {
                  //  if(sharedViewModel.listaDeAlertas[i] != "") {
                      //  setFragmentResult("AlertaAlmacen$i", bundleOf("Almacen$i" to sharedViewModel.listaDeBodegas[i], "Alerta$i" to sharedViewModel.listaDeAlertas[i] ) )
                       sharedViewModel.numeroAlertas.add(adapter.alertasAlmacenesList[i].Nombre)
                   // }
                }
                Toast.makeText(requireContext(), "Se han agregado exitosamente las alertas", Toast.LENGTH_LONG).show()

            }
                binding.rlAlertasAlmacenesEditar.isVisible = false
                val editarProductoFragment = EditarProductoFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.rlAlertasAlmacenesEditar, editarProductoFragment)
                    .commit()
            Log.i("Sebastian2", "${sharedViewModel.listaDeBodegas} , ${sharedViewModel.listaDeAlertas} , ${sharedViewModel.numeroAlertas} ")
        }

        /* binding.bAgregarAlmacen.setOnClickListener {
             //findNavController().navigate(R.id.action_nav_alertas_almacenes_to_nav_añadir_producto)
            // navigateToAnadirProducto(adapter.alertasAlmacenesList.lastIndex)
             val result = "result"
             val resultado = "Otro mensaje"
             setFragmentResult(REQUEST_KEY_1, bundleOf("bundleKey" to result, "mensaje" to resultado))
         }*/

      /*  if(sharedViewModel.listaDeAlertas.lastIndexOf("") != -1){
            sharedViewModel.listaDeAlertas.removeAt(sharedViewModel.listaDeAlertas.lastIndexOf(""))
        }*/

        return root
    }
    private fun gestionarRecyclerView(){
        adapter = AlertasAlmacenesEditarAdapter(listaDeAlmacenes,sharedViewModel,this,this)
        val recyclerView = binding.rvAlmacenesEditar.findViewById<RecyclerView>(R.id.rvAlmacenesEditar)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        searchByName()
    }

    private fun searchByName() {
        checkIfFragmentAttached {
            binding.progressBarEditar.isVisible = true
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val myResponse: Response<AlertasAlmacenesDataResponseEditar> =
                        retrofit.create(ApiServiceAlertasEditar::class.java).getAlertasAlmacenes()
                    // retrofit.create(ApiServiceAlertas::class.java).getAlertasAlmacenes()
                    if (myResponse.isSuccessful) {
                        Log.i("Sebastian", "Funciona!!")
                        val response: AlertasAlmacenesDataResponseEditar? = myResponse.body()
                        if (response != null) {
                            Log.i("Sebastian", response.toString())
                            //Log.i("Sebastian", response2.toString())
                            activity?.runOnUiThread {
                                adapter.updateList(response.Almacenes)
                                binding.progressBarEditar.isVisible = false
                            }
                        }

                    } else {
                        Log.i("Sebastian", "No funciona.")
                    }
                } catch (e: SocketTimeoutException) {

                } catch (e: Exception) {

                }
            }
        }
    }
    private fun getRetrofit(): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl("http://186.64.123.248/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //fun obtenerTextoDesdeAdapter(posicion: Int): AlertasAlmacenesItemResponse {
    //   return adapter.alertasAlmacenesList[posicion]
    // }

    override fun onCheckBoxClick(id: Int, isChecked: Boolean) {
        //val nombre = listaDeAlmacenes[id].Nombre
        //  val nombre = "Otro mensaje"
        // setFragmentResult("Principal", bundleOf("mensaje" to nombre))


    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun checkIfFragmentAttached(operation: Context.() -> Unit) {
        if (isAdded && context != null) {
            operation(requireContext())
        }
    }

    // override fun onEditTextValueChanged(position: Int, value: String, viewHolder: AlertasAlmacenesViewHolder) {
    /* while (sharedViewModel.listaDeAlertas.size <= position){
         sharedViewModel.listaDeAlertas.add("")
         sharedViewModel.listaDeBodegas.add("")
     }
     sharedViewModel.listaDeAlertas[position] = value
     sharedViewModel.listaDeBodegas[position] = adapter.alertasAlmacenesList[viewHolder.adapterPosition].Nombre

    // Toast.makeText(requireContext(), "Se he agregado la alerta", Toast.LENGTH_SHORT).show()
 }*/
    /*  @SuppressLint("InflateParams")
      fun obtenerBinding(context: Context): ItemAlertBinding {
          val inflater = LayoutInflater.from(context)
          val view = inflater.inflate(R.layout.item_alert,null)
          return ItemAlertBinding.bind(view)*/
    // }

    // var codeExecuted = false
    override fun afterTextChange(text: String, viewHolder: AlertasAlmacenesEditarViewHolder) {
       /* if (!binding.bAgregarAlmacenEditar.isPressed && text.isNotEmpty()){
            Toast.makeText(requireContext(), "Se he agregado la alerta", Toast.LENGTH_SHORT).show()
            sharedViewModel.listaDeAlertas[viewHolder.adapterPosition] = text
            sharedViewModel.listaDeBodegas[viewHolder.adapterPosition] = adapter.alertasAlmacenesList[viewHolder.adapterPosition].Nombre
            Log.i("Sebastian", "${sharedViewModel.listaDeBodegas} y ${sharedViewModel.listaDeAlertas}")}
        if (!binding.bAgregarAlmacenEditar.isPressed && text.isEmpty() && sharedViewModel.listaDeAlertas.size >0 &&
            sharedViewModel.listaDeBodegas.size > 0){
            sharedViewModel.listaDeAlertas[viewHolder.adapterPosition] = ""
            sharedViewModel.listaDeBodegas[viewHolder.adapterPosition] = ""
          //  sharedViewModel.listaDeAlertas.removeAt(position)
          //  sharedViewModel.listaDeBodegas.removeAt(position)
            Log.i("Sebastian", "${sharedViewModel.listaDeAlertas} y ${sharedViewModel.listaDeBodegas}")
            Toast.makeText(requireContext(), "Se ha eliminado la alerta", Toast.LENGTH_SHORT).show()

           }*/

    }



    override fun getAllItems(): List<AlertasAlmacenesItemResponseEditar> {
        TODO("Not yet implemented")
    }
}