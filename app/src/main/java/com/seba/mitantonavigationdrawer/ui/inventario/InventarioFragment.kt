package com.seba.mitantonavigationdrawer.ui.inventario

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.SearchView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentAlmacenesBinding
import com.seba.mitantonavigationdrawer.databinding.FragmentInventarioBinding
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.MisDatosFragment
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.MisDatosFragmentDirections
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.AlmacenesAdapter
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.AlmacenesDataResponse
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.AlmacenesViewModel
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.ApiService
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.editarAlmacen.EditarAlmacenFragment
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes.editarAlmacen.EditarAlmacenFragment.Companion.EXTRA_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class InventarioFragment: Fragment(R.layout.fragment_inventario) {
    private var _binding: FragmentInventarioBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var retrofit: Retrofit
    private lateinit var adapter: InventarioAdapter
    var DropdownInventario:  AutoCompleteTextView? = null
    // private lateinit var adapter2: CaracteristicasAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inventarioViewModel =
            ViewModelProvider(this).get(AlmacenesViewModel::class.java)

        _binding = FragmentInventarioBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Aquí se programa
        DropdownInventario = binding.tvAutoCompleteEditable.findViewById(R.id.tvAutoCompleteEditable)
        binding.tvTextoNoProductos.isVisible = false
        ListaDesplegable()
        retrofit = getRetrofit()
        initUI()
        //navigateToEditarAlmacen()

        return root
    }

    private fun ListaDesplegable() {
        val queue1 = Volley.newRequestQueue(requireContext())
        val url1 ="http://186.64.123.248/Inventario/listaDeAlmacenes.php"
        val jsonObjectRequest1 = JsonObjectRequest(
            Request.Method.GET,url1, null,
            { response ->
                // Obtén el array de opciones desde el objeto JSON
                val jsonArray = response.getJSONArray("Lista")
                // Convierte el array JSON a una lista mutable
                val opcionesList = mutableListOf<String>()
                for (i in 0 until jsonArray.length()) {
                    opcionesList.add(jsonArray.getString(i).removeSurrounding("'","'"))
                }
                //Crea un adpatador para el dropdown
                val adapter = ArrayAdapter(requireContext(),R.layout.list_item,opcionesList)
                //binding.tvholaMundo?.setText(response.getString("Lista"))
                DropdownInventario?.setAdapter(adapter)

                DropdownInventario?.onItemClickListener = AdapterView.OnItemClickListener {
                        parent, view, position, id ->
                    searchByName()
                    val itemSelected = parent.getItemAtPosition(position)
                }
            }, { error ->
                Toast.makeText(requireContext(), " La aplicación no se ha conectado con el servidor", Toast.LENGTH_SHORT).show()
            }
        )
        queue1.add(jsonObjectRequest1)
    }

    private fun initUI() {
        searchByName()
        adapter = InventarioAdapter()
        // adapter2 = CaracteristicasAdapter()
        binding.rvInventario.setHasFixedSize(true)
        binding.rvInventario.layoutManager = LinearLayoutManager(requireContext())
        binding.rvInventario.adapter = adapter
        // binding.rvAlmacenes.adapter = adapter2
    }

    private fun searchByName() {
        binding.progressBarInventario.isVisible = true
        CoroutineScope(Dispatchers.IO).launch{
            val myResponse : Response<InventarioDataResponse>  = retrofit.create(ApiServiceInventario::class.java).getInventario(DropdownInventario?.text.toString())
            if(myResponse.isSuccessful){
                Log.i("Sebastian", "Funciona!!")
                val response: InventarioDataResponse? = myResponse.body()
                // val response2: AlmacenesItemResponse? = myResponse2.body()
                if(response != null){
                    Log.i("Sebastian", response.toString())
                    //Log.i("Sebastian", response2.toString())
                    activity?.runOnUiThread {
                        binding.progressBarInventario.isVisible = false
                        adapter.updateList(response.ProductosPorAlmacen)
                        binding.tvTextoNoProductos.isVisible = response.ProductosPorAlmacen.isEmpty()
                    }

                }

            }else{
                Log.i("Sebastian", "No funciona.")
            }
        }
    }

    private fun getRetrofit():Retrofit{
        return Retrofit
            .Builder()
            .baseUrl("http://186.64.123.248/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}