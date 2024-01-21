package com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto

import android.content.Context
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
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentAlertasAlmacenesBinding
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto.AlertasAlmacenesAdapter.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.SocketTimeoutException


class AlertasAlmacenesFragment : Fragment(R.layout.fragment_alertas_almacenes),
    OnTextChangeListener,OnCheckBoxClickListener {
    companion object{
        const val REQUEST_KEY_1 = "requestKey"
        const val REQUEST_KEY_2 = "Otro mas"
    }
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
                //binding.cvAlertasAlmacenes.isVisible = false
                //val bundle = Bundle()
                //bundle.putString("key", "value")
                val anadirProductoFragment = AnadirProductoFragment()
                val result = "result"
                val resultado = "Otro mensaje"
                setFragmentResult(REQUEST_KEY_1, bundleOf("bundleKey" to result, "mensaje" to resultado))
                //anadirProductoFragment.arguments = bundle
                parentFragmentManager.beginTransaction()
                    .replace(R.id.rlAlertasAlmacenes, anadirProductoFragment)
                    .commitNow()

            /*childFragmentManager.beginTransaction()
                .remove(AlertasAlmacenesFragment())
                .commit()*/
            // parentFragmentManager.popBackStack()
        }
        binding.rlAlertasAlmacenes.setOnClickListener {
                binding.rlAlertasAlmacenes.isVisible = false
                //binding.cvAlertasAlmacenes.isVisible = false
                //val result = "result"
                //setResult("requestKey", bundleOf("bundleKey" to result))
               // val bundle = Bundle()
               // bundle.putString("key", "value")
               val result = "result"
               val resultado = "Otro mensaje"
               setFragmentResult("requestKey", bundleOf("bundleKey" to result, "mensaje" to resultado))
                val anadirProductoFragment = AnadirProductoFragment()
               // anadirProductoFragment.arguments = bundle
                parentFragmentManager.beginTransaction()
                    .replace(R.id.rlAlertasAlmacenes, anadirProductoFragment)
                    .commitNow()

            /*childFragmentManager.beginTransaction()
                .remove(AlertasAlmacenesFragment())
                .commit()*/
            //parentFragmentManager.popBackStack()
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
        adapter = AlertasAlmacenesAdapter(listaDeAlmacenes,this@AlertasAlmacenesFragment,this@AlertasAlmacenesFragment)
        val recyclerView = binding.rvAlmacenes.findViewById<RecyclerView>(R.id.rvAlmacenes)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        searchByName()


    }

    private fun searchByName() {
        checkIfFragmentAttached {
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

                        }
                    }

                } else {
                    Log.i("Sebastian", "No funciona.")
                }
            } catch (e: SocketTimeoutException) {
               // requireActivity().runOnUiThread {
                    /*Toast.makeText(
                        requireContext(),
                        "Se acabo el tiempo de espera para conectar la aplicación al servidor",
                        Toast.LENGTH_LONG
                    ).show()*/
               // }
            } catch (e: Exception) {
               // requireActivity().runOnUiThread {
                  /*  Toast.makeText(
                        requireContext(),
                        "No se puedo conectar la aplicación al servidor",
                        Toast.LENGTH_LONG
                    ).show()*/
                //}
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

    fun obtenerTextoDesdeAdapter(posicion: Int): AlertasAlmacenesItemResponse {
        return adapter.alertasAlmacenesList[posicion]
    }

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

    override fun onTextChange(text: String, viewHolder: AlertasAlmacenesViewHolder) {
        binding.bAgregarAlmacen.setOnClickListener {
            val anadirProductoFragment = AnadirProductoFragment()
            val resultado = text + " " + adapter.alertasAlmacenesList[viewHolder.adapterPosition].Nombre
            anadirProductoFragment.view?.findViewById<EditText>(R.id.etNombreImagen)?.setText(resultado)
            val nombreImagen =anadirProductoFragment.view?.findViewById<TextView>(R.id.tvUnidadesEmbalaje)
            nombreImagen?.isInEditMode
            nombreImagen?.text = resultado
            setFragmentResult(REQUEST_KEY_2, bundleOf("mensaje" to resultado))
            Toast.makeText(requireContext(), "Texto recibido: ${adapter.alertasAlmacenesList[viewHolder.adapterPosition].Nombre} y $text", Toast.LENGTH_LONG).show()
            //val anadirProductoFragment = parentFragmentManager.findFragmentByTag("tag_añadir_producto") as AnadirProductoFragment?

        }
    }

}