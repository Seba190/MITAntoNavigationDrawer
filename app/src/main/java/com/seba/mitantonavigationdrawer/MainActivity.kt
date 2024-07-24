package com.seba.mitantonavigationdrawer

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.seba.mitantonavigationdrawer.databinding.ActivityMainBinding
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.navigation.fragment.findNavController
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainer
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.seba.mitantonavigationdrawer.databinding.FragmentAnadirDatosBinding
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirProducto.AnadirProductoFragment
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirTransferencia.AnadirTransferenciaFragment
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirTransferencia.AnadirTransferenciaUpdater
import com.seba.mitantonavigationdrawer.ui.Formularios.añadirAlmacen.añadirTransferencia.ElegirProductoFragment
import com.seba.mitantonavigationdrawer.ui.SharedViewModel
import com.seba.mitantonavigationdrawer.ui.añadirDatos.AnadirDatosFragment
import com.seba.mitantonavigationdrawer.ui.inicio.InicioFragment


class MainActivity : AppCompatActivity(),AnadirTransferenciaUpdater{
    private val sharedViewModel: SharedViewModel by viewModels()
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Sobreposicionar fragment añadir_datos sobre fragment inicio

       /* if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AnadirDatosFragment())
                .commit()
        }
        // Luego, en algún momento, puedes reemplazar FragmentA con FragmentB
        showFragmentB()*/
        setSupportActionBar(binding.appBarMain.toolbar)
        //val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val actionBar = supportActionBar
        if (actionBar != null) {
            val lp = binding.appBarMain.toolbar.layoutParams as AppBarLayout.LayoutParams
            lp.height = resources.getDimensionPixelSize(R.dimen.custom_action_bar_size)
            binding.appBarMain.toolbar.layoutParams = lp
        }

        NavigationUI.setupWithNavController(navView, navController)
        drawerLayout = findViewById(R.id.drawer_layout)
        setupDrawerListener()
        // val drawerLayout: DrawerLayout = binding.drawerLayout
        //  val navView: NavigationView = binding.navView
        // val navController = findNavController(R.id.navHostFragment)
        // val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_inicio, R.id.nav_exportar_datos, R.id.nav_preferencias, R.id.nav_statistics,
                R.id.nav_añadir_datos, R.id.nav_añadir_almacen, R.id.nav_añadir_producto, R.id.nav_añadir_proveedor,
                R.id.nav_añadir_cliente,R.id.nav_añadir_tipo_de_producto,R.id.nav_añadir_inventario,R.id.nav_remover_inventario,
                R.id.nav_añadir_transferencia,R.id.nav_elegir_producto, R.id.nav_mis_datos, R.id.nav_almacenes,R.id.nav_productos,R.id.nav_proveedores,
                R.id.nav_editar_almacen,R.id.nav_editar_cliente,R.id.nav_editar_proveedor,R.id.nav_editar_tipos_de_productos,
                R.id.tipos_de_productos, R.id.productos,R.id.nav_tipos_de_productos, R.id.nav_clientes,R.id.proveedores,
                R.id.clientes, R.id.nav_editar_almacen,R.id.nav_editar_producto, R.id.nav_alertas_almacenes,R.id.nav_cliente_precio_venta,
                R.id.nav_proveedor_precio_compra, R.id.nav_transferencias, R.id.nav_editar_transferencias,R.id.nav_transacciones,
                R.id.nav_sin_factura,R.id.nav_editar_sin_factura,R.id.nav_factura_entrada,R.id.nav_editar_factura_entrada,
                R.id.nav_factura_salida,R.id.nav_editar_factura_salida,R.id.nav_inventory

            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        NavigationUI.setupWithNavController(navView, navController)
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_inicio -> navController.navigate(R.id.nav_inicio)
                R.id.nav_exportar_datos -> navController.navigate(R.id.nav_exportar_datos)
                R.id.nav_preferencias -> navController.navigate(R.id.nav_preferencias)
                R.id.nav_statistics -> navController.navigate(R.id.nav_statistics)
                R.id.nav_añadir_almacen ->navController.navigate(R.id.nav_añadir_almacen)
                R.id.nav_añadir_datos -> navController.navigate(R.id.nav_añadir_datos)
                R.id.nav_añadir_producto ->navController.navigate(R.id.nav_añadir_producto)
                R.id.nav_añadir_proveedor ->navController.navigate(R.id.nav_añadir_proveedor)
                R.id.nav_añadir_cliente -> navController.navigate(R.id.nav_añadir_cliente)
                R.id.nav_añadir_tipo_de_producto -> navController.navigate(R.id.nav_añadir_tipo_de_producto)
                R.id.nav_añadir_inventario -> navController.navigate(R.id.nav_añadir_inventario)
                R.id.nav_remover_inventario -> navController.navigate(R.id.nav_remover_inventario)
                R.id.nav_añadir_transferencia -> navController.navigate(R.id.nav_añadir_transferencia)
                R.id.nav_elegir_producto -> navController.navigate(R.id.nav_elegir_producto)
                R.id.nav_mis_datos -> navController.navigate(R.id.nav_mis_datos)
                R.id.nav_almacenes -> navController.navigate(R.id.nav_almacenes)
                R.id.nav_tipos_de_productos -> navController.navigate(R.id.nav_tipos_de_productos)
                R.id.nav_productos -> navController.navigate(R.id.nav_productos)
                R.id.nav_proveedores -> navController.navigate(R.id.nav_proveedores)
                R.id.nav_clientes -> navController.navigate(R.id.nav_clientes)
                R.id.nav_editar_almacen -> navController.navigate(R.id.nav_editar_almacen)
                R.id.nav_alertas_almacenes ->navController.navigate(R.id.nav_alertas_almacenes)
                R.id.nav_proveedor_precio_compra -> navController.navigate(R.id.nav_proveedor_precio_compra)
                R.id.nav_cliente_precio_venta -> navController.navigate(R.id.nav_cliente_precio_venta)
                R.id.nav_editar_producto ->navController.navigate(R.id.nav_editar_producto)
                R.id.nav_editar_tipos_de_productos ->navController.navigate(R.id.nav_editar_tipos_de_productos)
                R.id.nav_transferencias ->navController.navigate(R.id.nav_transferencias)
                R.id.nav_editar_transferencias ->navController.navigate(R.id.nav_editar_transferencias)
                R.id.nav_transacciones ->navController.navigate(R.id.nav_transacciones)
                R.id.nav_sin_factura -> navController.navigate(R.id.nav_sin_factura)
                R.id.nav_editar_sin_factura -> navController.navigate(R.id.nav_editar_sin_factura)
                R.id.nav_factura_entrada -> navController.navigate(R.id.nav_factura_entrada)
                R.id.nav_editar_factura_entrada -> navController.navigate(R.id.nav_editar_factura_entrada)
                R.id.nav_factura_salida -> navController.navigate(R.id.nav_factura_salida)
                R.id.nav_editar_factura_salida -> navController.navigate(R.id.nav_editar_factura_salida)
                R.id.nav_inventory ->navController.navigate(R.id.nav_inventory)
                R.id.nav_salir ->{
                    finishAffinity()
                    Toast.makeText(this,"Saliendo de la aplicación...",Toast.LENGTH_SHORT).show()
                }
            }
            drawerLayout.closeDrawers()
            true

        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        // val navController = findNavController(R.id.navHostFragment)
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun updateRecyclerView(dataCantidad: MutableList<String>,dataProducto: MutableList<String>) {
        val anadirTransferenciaFragment = supportFragmentManager.findFragmentById(R.id.clAnadirTransferencia) as AnadirTransferenciaFragment?
        anadirTransferenciaFragment?.updateData(dataCantidad, dataProducto)
    }

    private fun setupDrawerListener(){
        drawerLayout.addDrawerListener(object :DrawerLayout.DrawerListener{
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                Log.i("Drawer", "Drawer sliding: $slideOffset")
                sharedViewModel.listaDeProductos.clear()
                sharedViewModel.listaDeCantidades.clear()
                sharedViewModel.listaDeProductosAnadir.clear()
                sharedViewModel.listaDeCantidadesAnadir.clear()
                sharedViewModel.listaDePreciosAnadir.clear()
                sharedViewModel.listaDePreciosDeProductos.clear()
                sharedViewModel.listaDeProductosRemover.clear()
                sharedViewModel.listaDeCantidadesRemover.clear()
                sharedViewModel.listaDePreciosRemover.clear()
                sharedViewModel.listaDePreciosDeProductosRemover.clear()
                sharedViewModel.listaDeBodegasAnadir.clear()
                sharedViewModel.listaDeAlertasAnadir.clear()
                sharedViewModel.ListasDeAlertas.clear()
                sharedViewModel.ListasDeAlmacenes.clear()
                sharedViewModel.ListasDeProductosAlertas.clear()
                sharedViewModel.listaDeClientesAnadir.clear()
                sharedViewModel.listaDePreciosVentaAnadir.clear()
                sharedViewModel.ListasDeClientes.clear()
                sharedViewModel.ListasDePreciosDeVenta.clear()
                sharedViewModel.ListasDeProductosPrecioVenta.clear()
                sharedViewModel.listaDePreciosCompraAnadir.clear()
                sharedViewModel.listaDeProveedoresAnadir.clear()
                sharedViewModel.ListasDeProveedores.clear()
                sharedViewModel.ListasDePreciosDeCompra.clear()
                sharedViewModel.ListasDeProductosPrecioCompra.clear()
                sharedViewModel.opcionesListSalida.clear()
                sharedViewModel.opcionesListEntrada.clear()
                sharedViewModel.opcionesListRemover.clear()
                sharedViewModel.opcionesListAnadir.clear()
                sharedViewModel.opcionesListTransferencia.clear()
                sharedViewModel.opcionesListEditarTransferencia.clear()
                sharedViewModel.id.clear()
                sharedViewModel.listaDeAlertas.clear()
                sharedViewModel.listaDePreciosVenta.clear()
                sharedViewModel.listaDePreciosCompra.clear()
                sharedViewModel.listaDeBodegas.clear()
                sharedViewModel.listaDeClientes.clear()
                sharedViewModel.listaDeProveedores.clear()
                sharedViewModel.numeroAlertas.clear()
                sharedViewModel.numeroPreciosCompra.clear()
                sharedViewModel.numeroPreciosVenta.clear()
                sharedViewModel.listaDeAlmacenesAnadir.clear()
                sharedViewModel.listaDeAlmacenesRemover.clear()
                sharedViewModel.listaDeAlmacenesEntrada.clear()
                sharedViewModel.listaDeAlmacenesSalida.clear()
                sharedViewModel.listaDeAlmacenesTransferencia.clear()
                sharedViewModel.listaDeAlmacenesEditarTransferencia.clear()
            }

            override fun onDrawerOpened(drawerView: View) {
                Log.i("Drawer", "Drawer opened")
            }

            override fun onDrawerClosed(drawerView: View) {
                Log.i("Drawer", "Drawer closed")

            }

            override fun onDrawerStateChanged(newState: Int) {
                Log.i("Drawer", "Drawer state changed: $newState")
                when (newState) {
                    DrawerLayout.STATE_IDLE -> {
                        // El drawer está en reposo
                        // Puedes realizar acciones cuando el drawer no está siendo interactuado
                    }
                    DrawerLayout.STATE_DRAGGING -> {
                        // El drawer está siendo arrastrado por el usuario
                        // Puedes realizar acciones cuando el usuario empieza a interactuar con el drawer
                    }
                    DrawerLayout.STATE_SETTLING -> {
                        // El drawer está asentándose a una posición final
                        // Puedes realizar acciones cuando el drawer está en el proceso de abrirse o cerrarse completamente



                    }
                }
            }
    })
    }
}
