package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentAlmacenesBinding
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.MisDatosFragmentDirections
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.editarAlmacen.EditarAlmacenFragment
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.editarAlmacen.EditarAlmacenFragment.Companion.EXTRA_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class AlmacenesFragment : Fragment(R.layout.fragment_almacenes) {
    private var _binding: FragmentAlmacenesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var retrofit: Retrofit
    private lateinit var adapter: AlmacenesAdapter
    private var listaDeAlmacenesMutableList : MutableList<AlmacenesItemResponse> = mutableListOf()
    private val listaDeActividad: List<String> = listOf("TODOS", "ACTIVOS", "INACTIVOS")
    var DropdownActividad:  AutoCompleteTextView? = null
   // private lateinit var adapter2: CaracteristicasAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val almacenesViewModel =
            ViewModelProvider(this).get(AlmacenesViewModel::class.java)

        _binding = FragmentAlmacenesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.tvTextoNoAlmacenes.isVisible = false
        //Aquí se programa
        DropdownActividad = binding.tvAutoCompleteFiltradoAlmacenes.findViewById(R.id.tvAutoCompleteFiltradoAlmacenes)
        retrofit = getRetrofit()
        listaDesplegableActividad()
        initUI()

        return root
    }

    private fun initUI() {
      //  binding.rvAlmacenes.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
        //    override fun onQueryTextSubmit(query: String?): Boolean {
    //      return false
           // }
        searchByName()
       // val valorEditText : String = binding.etFiltradoAlmacenes.text.toString()
       // binding.etFiltradoAlmacenes.setText("")
       // Handler(Looper.getMainLooper()).postDelayed({
       // binding.etFiltradoAlmacenes.setText(valorEditText)
       // }, 10)

            //override fun onQueryTextChange(newText: String?): Boolean { return false }
       // })
        adapter = AlmacenesAdapter(listaDeAlmacenesMutableList){navigateToEditarAlmacen(it)}
       // adapter2 = CaracteristicasAdapter()
        binding.rvAlmacenes.setHasFixedSize(true)
        binding.rvAlmacenes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAlmacenes.adapter = adapter
       // binding.rvAlmacenes.adapter = adapter2
    }

    private fun searchByName() {
      binding.progressBar.isVisible = true
      CoroutineScope(Dispatchers.IO).launch {
          Log.i("Seba",DropdownActividad?.text.toString())
          val myResponse: Response<AlmacenesDataResponse> =
              retrofit.create(ApiService::class.java)
                  .postAlmacenes(DropdownActividad?.text.toString())
          if (myResponse.isSuccessful) {
              Log.i("Sebastian", "Funciona!!")
              val response: AlmacenesDataResponse? = myResponse.body()
              // val response2: AlmacenesItemResponse? = myResponse2.body()
              if (response != null) {
                  Log.i("Sebastian", response.toString())
                  //Log.i("Sebastian", response2.toString())
                  activity?.runOnUiThread {
                      adapter.updateList(response.Almacenes.sortedBy { it.Nombre })
                      binding.progressBar.isVisible = false
                      listaDeAlmacenesMutableList.clear()
                      listaDeAlmacenesMutableList.addAll(response.Almacenes)
                      binding.etFiltradoAlmacenes.addTextChangedListener { filtro ->
                          // Convierte 'filtro' a String de manera segura manejando el caso null
                          val filtroTexto = filtro.toString()
                          // Filtra la lista con el texto del filtro
                          val almacenesFiltrados =
                              listaDeAlmacenesMutableList.filter { bodega ->
                                  bodega.Nombre.lowercase().contains(filtroTexto.lowercase())
                              }.sortedBy { it.Nombre }
                          Log.i(
                              "SebaAntes",
                              "$almacenesFiltrados , $filtroTexto , $listaDeAlmacenesMutableList "
                          )
                          adapter.updateList(almacenesFiltrados)
                          Log.i(
                              "SebaDespues",
                              "$almacenesFiltrados , $filtroTexto , $listaDeAlmacenesMutableList "
                          )
                      }
                      binding.tvTextoNoAlmacenes.isVisible = response.Almacenes.isEmpty()
                  }
              }

          } else {
                  Log.i("Sebastian", "No funciona.")
              }
      }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun getRetrofit():Retrofit{
         return Retrofit
             .Builder()
             .baseUrl("http://186.64.123.248/Reportes/")
             .addConverterFactory(GsonConverterFactory.create())
             .build()
    }

    fun NavController.safeNavigateWithArgs(direction: NavDirections, bundle: Bundle?) {
        currentDestination?.getAction(direction.actionId)?.run {
            navigate(direction.actionId, bundle)
        }
    }

    //Esta lista desplegable te muestra TODOS, ACTIVOS e INACTIVOS
    private fun listaDesplegableActividad(){
        val adapter = ArrayAdapter(requireContext(),R.layout.list_item,listaDeActividad)
        //binding.tvholaMundo?.setText(response.getString("Lista"))
        binding.tvAutoCompleteFiltradoAlmacenes.setAdapter(adapter)
        binding.tvAutoCompleteFiltradoAlmacenes.onItemClickListener = AdapterView.OnItemClickListener {
                parent, view, position, id ->
           // val itemSelected = parent.getItemAtPosition(position).toString()
            searchByName()
            val valorEditText : String = binding.etFiltradoAlmacenes.text.toString()
            binding.etFiltradoAlmacenes.setText("")
            Handler(Looper.getMainLooper()).postDelayed({
                binding.etFiltradoAlmacenes.setText(valorEditText)
            }, 100)
            //bodegasPorActividad(itemSelected)
        }

    }

    //Esta función te muestra las listas dependiendo de la actividad
   /* private fun bodegasPorActividad(actividad : String) {
        val url1 ="http://186.64.123.248/Reportes/Almacenes/almacenesActivosInactivos.php"
        val queue1 = Volley.newRequestQueue(requireContext())
        val stringRequest = object : StringRequest(
            Request.Method.POST,
            url1,
            { response ->
                val jsonArray = JSONObject(response).getJSONArray("Lista")
                val listaActividad : MutableList<String> = mutableListOf()
                for (i in 0 until jsonArray.length()) {
                    listaActividad.add(jsonArray.getString(i).removeSurrounding("'", "'"))
                }
                binding.etFiltradoAlmacenes.addTextChangedListener { filtro ->
                    // Convierte 'filtro' a String de manera segura manejando el caso null
                    val filtroTexto = filtro.toString()
                    // Filtra la lista con el texto del filtro
                    val almacenesFiltrados = listaActividad.filter { bodega ->
                        bodega.lowercase().contains(filtroTexto.lowercase())
                    }
                    Log.i("Seba", "$almacenesFiltrados , $filtroTexto , $listaDeAlmacenesMutableList ")

                }
            },
            { error ->

            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val parametros = HashMap<String, String>()
                parametros.put("ACTIVIDAD", actividad)

                return parametros
            }
        }
        queue1.add(stringRequest)
    }*/

    private fun navigateToEditarAlmacen(id:String){
        val fragmentoDestino = EditarAlmacenFragment()
        val bundle = bundleOf(EXTRA_ID to id)
         //bundle.getString(EXTRA_ID,id)
        // bundle.putString(EXTRA_ID,id)
        //fragmentoDestino.arguments = bundle
        Log.i("Sebastian", "Valor de Id: $id")
        //val action = AlmacenesFragmentDirections.actionNavAlmacenesToNavEditarAlmacen(argumentoId = id)
        val action = MisDatosFragmentDirections.actionNavMisDatosToNavEditarAlmacen(id = id)
        /*val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragmentoDestino)
        fragmentTransaction.commit()*/
        //findNavController().safeNavigateWithArgs(Fragment)
        //findNavController().navigate(R.id.action_nav_mis_datos_to_nav_editar_almacen)
        //findNavController().navigate(R.id.action_nav_mis_datos_to_nav_editar_almacen)
        findNavController().navigate(action)
    }
}