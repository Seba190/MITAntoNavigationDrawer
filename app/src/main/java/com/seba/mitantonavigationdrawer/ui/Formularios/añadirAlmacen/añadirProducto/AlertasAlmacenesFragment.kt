package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto

import android.annotation.SuppressLint
import android.content.Context
import android.location.GnssAntennaInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentAlertasAlmacenesBinding
import com.seba.mitantonavigationdrawer.databinding.ItemAlertBinding
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto.AlertasAlmacenesAdapter.*
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.SocketTimeoutException


class AlertasAlmacenesFragment() : Fragment(R.layout.fragment_alertas_almacenes),
    OnTextChangeListener,OnCheckBoxClickListener{
   /* companion object {
        const val REQUEST_KEY_1 = "Prueba"
        const val REQUEST_KEY_2 = "AlertasAlmacen"
        const val REQUEST_KEY_3 = "Codigo de Barra"
        const val REQUEST_KEY_4 = "Codigo de Barra Transferencia"
    }*/
    private val sharedViewModel by activityViewModels<SharedViewModel>()
    private var listaDeAlmacenes : List<AlertasAlmacenesItemResponse> = emptyList()
    private var _binding: FragmentAlertasAlmacenesBinding? = null
    private lateinit var adapter: AlertasAlmacenesAdapter
    private lateinit var retrofit: Retrofit
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val alertasAlmacenesViewHolder =
            ViewModelProvider(this).get(AlertasAlmacenesViewModel::class.java)

        _binding = FragmentAlertasAlmacenesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa

        retrofit = getRetrofit()
        gestionarRecyclerView()

        binding.bAlertaAlmacenVolver.setOnClickListener {
                binding.rlAlertasAlmacenes.isVisible = false
                val anadirProductoFragment = AnadirProductoFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.rlAlertasAlmacenes, anadirProductoFragment)
                    .commitNow()
        }
        binding.rlAlertasAlmacenes.setOnClickListener {
                binding.rlAlertasAlmacenes.isVisible = false
                val anadirProductoFragment = AnadirProductoFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.rlAlertasAlmacenes, anadirProductoFragment)
                    .commitNow()
        }
        binding.bAlertaAlmacenEliminar.setOnClickListener {
            sharedViewModel.listaDeBodegas.clear()
            sharedViewModel.listaDeAlertas.clear()
            sharedViewModel.ListasDeAlertas.clear()
            sharedViewModel.ListasDeAlmacenes.clear()
            sharedViewModel.ListasDeProductosAlertas.clear()
            Toast.makeText(requireContext(), "Se han eliminado las alertas exitosamente", Toast.LENGTH_LONG).show()
        }


       /* binding.bAgregarAlmacen.setOnClickListener {
            //findNavController().navigate(R.id.action_nav_alertas_almacenes_to_nav_añadir_producto)
           // navigateToAnadirProducto(adapter.alertasAlmacenesList.lastIndex)
            val result = "result"
            val resultado = "Otro mensaje"
            setFragmentResult(REQUEST_KEY_1, bundleOf("bundleKey" to result, "mensaje" to resultado))
        }*/

        return root
    }
    private fun gestionarRecyclerView(){
        adapter = AlertasAlmacenesAdapter(listaDeAlmacenes,this,this)
        val recyclerView = binding.rvAlmacenes.findViewById<RecyclerView>(R.id.rvAlmacenes)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        searchByName()
    }

    private fun searchByName() {
        checkIfFragmentAttached {
            binding.progressBar.isVisible = true
            CoroutineScope(Dispatchers.IO).launch {
            try {
                val myResponse: Response<AlertasAlmacenesDataResponse> =
                    retrofit.create(ApiServiceAlertas::class.java).getAlertasAlmacenes()
                // retrofit.create(ApiServiceAlertas::class.java).getAlertasAlmacenes()
                if (myResponse.isSuccessful) {
                    Log.i("Sebastian", "Funciona!!")
                    val response: AlertasAlmacenesDataResponse? = myResponse.body()
                    if (response != null) {
                        Log.i("Sebastian", response.toString())
                        //Log.i("Sebastian", response2.toString())
                        activity?.runOnUiThread {
                            adapter.updateList(response.Almacenes)
                            binding.progressBar.isVisible = false
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
  /*  @SuppressLint("InflateParams")
    fun obtenerBinding(context: Context): ItemAlertBinding {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_alert,null)
        return ItemAlertBinding.bind(view)
    }*/

    override fun onTextChange(text: String, viewHolder: AlertasAlmacenesViewHolder) {
     TODO("Not yet implemented")

    }
    override fun afterTextChange(text: String, viewHolder: AlertasAlmacenesViewHolder) {
        if (!binding.bAlertaAlmacenVolver.isPressed){
        Toast.makeText(requireContext(), "Se he agregado la alarma", Toast.LENGTH_LONG).show()
        sharedViewModel.listaDeAlertas.add(text)
        sharedViewModel.listaDeBodegas.add(adapter.alertasAlmacenesList[viewHolder.adapterPosition].Nombre)}
        binding.bAgregarAlmacen.setOnClickListener {
            if (sharedViewModel.listaDeAlertas.size > 0) {
                for (i in 0..<sharedViewModel.listaDeAlertas.size) {
                    setFragmentResult("AlertaAlmacen$i", bundleOf("Almacen$i" to sharedViewModel.listaDeBodegas[i], "Alerta$i" to sharedViewModel.listaDeAlertas[i]))
                }
                Toast.makeText(requireContext(), "Se han agregado exitosamente los almacenes con las alarmas con un largo de ${sharedViewModel.listaDeAlertas.size}", Toast.LENGTH_LONG).show()

            }
        }
    }
    override fun getAllItems(): List<AlertasAlmacenesItemResponse> {
        TODO("Not yet implemented")
    }

  //  override fun obtenerListaEnPosicion(position: Int): List<String> {
//        TODO("Not yet implemented")
 //   }
}







    /* override fun onEventOccurred(text: String, viewHolder: AlertasAlmacenesViewHolder) {
         binding.bAgregarAlmacen.setOnClickListener {
             val anadirProductoFragment = AnadirProductoFragment()
             val resultado = text + " " + adapter.alertasAlmacenesList[viewHolder.adapterPosition].Nombre
             anadirProductoFragment.view?.findViewById<EditText>(R.id.etCodigoBarraProducto)?.setText(resultado)
             val nombreImagen =anadirProductoFragment.view?.findViewById<TextView>(R.id.tvUnidadesEmbalaje)
             //nombreImagen?.isInEditMode
             nombreImagen?.text = resultado
             setFragmentResult(REQUEST_KEY_2, bundleOf("mensaje" to resultado))
             Toast.makeText(requireContext(), "Texto recibido: ${adapter.alertasAlmacenesList[viewHolder.adapterPosition].Nombre} y $text", Toast.LENGTH_LONG).show()
         }*/
   /* override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedModel3.sharedData.observe(viewLifecycleOwner){
                newData ->
        }
    }*/
    /*override fun getAllItems(): List<AlertasAlmacenesItemResponse> {
        // Obtiene todos los elementos del RecyclerView
        return alertasAlmacenesAdapter.getAllItems()
    }*/

    /*private val alertasAlmacenesAdapter: AlertasAlmacenesAdapter by lazy {
        AlertasAlmacenesAdapter(listaDeAlmacenes, this,
        {
                text ->
           // Toast.makeText(requireContext(),"El texto es $text",Toast.LENGTH_LONG).show()
        },this)*/
 //   }

//}


