package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.tiposDeProductos

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
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentTiposDeProductosBinding
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.MisDatosFragmentDirections
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class TiposDeProductosFragment : Fragment(R.layout.fragment_tipos_de_productos) {

    private var _binding: FragmentTiposDeProductosBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var retrofit: Retrofit
    private lateinit var adapter: TiposDeProductosAdapter
    private var listaDeTiposDeProductoMutableList : MutableList<TiposDeProductosItemResponse> = mutableListOf()
    private val listaDeActividad: List<String> = listOf("TODOS", "ACTIVOS", "INACTIVOS")
    var DropdownActividad:  AutoCompleteTextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val tiposDeProductosViewModel =
            ViewModelProvider(this).get(TiposDeProductosViewModel::class.java)

        _binding = FragmentTiposDeProductosBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.tvTextoNoTiposDeProducto.isVisible = false
        //Aquí se programa
        DropdownActividad = binding.tvAutoCompleteFiltradoTiposDeProducto.findViewById(R.id.tvAutoCompleteFiltradoTiposDeProducto)
        retrofit = getRetrofit()
        listaDesplegableActividad()
        initUI()
        return root
    }

    private fun initUI() {
        searchByName()
        adapter = TiposDeProductosAdapter{navigateToEditarTiposDeProductos(it)}
        binding.rvTiposDeProductos.setHasFixedSize(true)
        binding.rvTiposDeProductos.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTiposDeProductos.adapter = adapter
    }

    private fun searchByName() {
        binding.progressBarTiposDeProductos.isVisible = true
        CoroutineScope(Dispatchers.IO).launch{
            val myResponse : Response<TiposDeProductosDataResponse> = retrofit.create(
                ApiServiceTiposDeProductos::class.java).postTiposDeProductos(DropdownActividad?.text.toString())
            if(myResponse.isSuccessful){
                Log.i("Sebastian", "Funciona!!")
                val response: TiposDeProductosDataResponse? = myResponse.body()
                // val response2: AlmacenesItemResponse? = myResponse2.body()
                if(response != null){
                    Log.i("Sebastian", response.toString())
                    //Log.i("Sebastian", response2.toString())
                    activity?.runOnUiThread {
                        adapter.updateList(response.TiposDeProductos.sortedBy { it.Nombre })
                        binding.progressBarTiposDeProductos.isVisible = false
                        listaDeTiposDeProductoMutableList.clear()
                        listaDeTiposDeProductoMutableList.addAll(response.TiposDeProductos)
                        binding.etFiltradoTiposDeProducto.addTextChangedListener { filtro ->
                            // Convierte 'filtro' a String de manera segura manejando el caso null
                            val filtroTexto = filtro.toString()
                            // Filtra la lista con el texto del filtro
                            val almacenesFiltrados =
                                listaDeTiposDeProductoMutableList.filter { bodega ->
                                    bodega.Nombre.lowercase().contains(filtroTexto.lowercase())
                                }.sortedBy { it.Nombre}
                            Log.i(
                                "SebaAntes",
                                "$almacenesFiltrados , $filtroTexto , $listaDeTiposDeProductoMutableList "
                            )
                            adapter.updateList(almacenesFiltrados)
                            Log.i(
                                "SebaDespues",
                                "$almacenesFiltrados , $filtroTexto , $listaDeTiposDeProductoMutableList "
                            )
                        }
                        binding.tvTextoNoTiposDeProducto.isVisible = response.TiposDeProductos.isEmpty()
                    }
                }

            }else{
                Log.i("Sebastian", "No funciona.")
            }
        }
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl("http://186.64.123.248/Reportes/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun listaDesplegableActividad(){
        val adapter = ArrayAdapter(requireContext(),R.layout.list_item,listaDeActividad)
        //binding.tvholaMundo?.setText(response.getString("Lista"))
        DropdownActividad?.setAdapter(adapter)
        DropdownActividad?.onItemClickListener = AdapterView.OnItemClickListener {
                parent, view, position, id ->
            // val itemSelected = parent.getItemAtPosition(position).toString()
            searchByName()
            val valorEditText : String = binding.etFiltradoTiposDeProducto.text.toString()
            binding.etFiltradoTiposDeProducto.setText("")
            Handler(Looper.getMainLooper()).postDelayed({
                binding.etFiltradoTiposDeProducto.setText(valorEditText)
            }, 100)
            //bodegasPorActividad(itemSelected)
        }

    }

    private fun navigateToEditarTiposDeProductos(id:String){
        val action = MisDatosFragmentDirections.actionNavMisDatosToNavEditarTiposDeProductos(id = id)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}