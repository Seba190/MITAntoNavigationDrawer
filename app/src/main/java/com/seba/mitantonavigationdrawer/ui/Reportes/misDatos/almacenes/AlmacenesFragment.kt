package com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.almacenes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
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
import com.seba.mitantonavigationdrawer.R
import com.seba.mitantonavigationdrawer.databinding.FragmentAlmacenesBinding
import com.seba.mitantonavigationdrawer.ui.Reportes.misDatos.MisDatosFragment
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
        //Aquí se programa
        retrofit = getRetrofit()
        initUI()
        //navigateToEditarAlmacen()

        return root
    }

    private fun initUI() {
      //  binding.rvAlmacenes.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
        //    override fun onQueryTextSubmit(query: String?): Boolean {
        searchByName()
    //      return false
           // }

            //override fun onQueryTextChange(newText: String?): Boolean { return false }
       // })
        adapter = AlmacenesAdapter{navigateToEditarAlmacen(it)}
       // adapter2 = CaracteristicasAdapter()
        binding.rvAlmacenes.setHasFixedSize(true)
        binding.rvAlmacenes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAlmacenes.adapter = adapter
       // binding.rvAlmacenes.adapter = adapter2
    }

    private fun searchByName() {
      binding.progressBar.isVisible = true
      CoroutineScope(Dispatchers.IO).launch{
          val myResponse : Response<AlmacenesDataResponse>  = retrofit.create(ApiService::class.java).getAlmacenes()
          if(myResponse.isSuccessful){
              Log.i("Sebastian", "Funciona!!")
              val response: AlmacenesDataResponse? = myResponse.body()
             // val response2: AlmacenesItemResponse? = myResponse2.body()
              if(response != null){
                  Log.i("Sebastian", response.toString())
                  //Log.i("Sebastian", response2.toString())
                  activity?.runOnUiThread {
                      adapter.updateList(response.Almacenes)
                  }
              }

          }else{
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