package com.seba.mitantonavigationdrawer

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
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
import androidx.navigation.fragment.findNavController
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        //val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
        val navView: NavigationView = findViewById(R.id.nav_view)

        NavigationUI.setupWithNavController(navView, navController)
        drawerLayout = findViewById(R.id.drawer_layout)
        // val drawerLayout: DrawerLayout = binding.drawerLayout
        //  val navView: NavigationView = binding.navView
        // val navController = findNavController(R.id.navHostFragment)
        // val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_inicio, R.id.nav_exportar_datos, R.id.nav_preferencias, R.id.nav_statistics, R.id.nav_añadir_inventario
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
                R.id.nav_añadir_inventario ->navController.navigate(R.id.nav_añadir_inventario)
                R.id.nav_search -> navController.navigate(R.id.nav_search)
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

    /* fun onMyButtonClick(view : View) {
         view.setOnClickListener {
             findNavController(R.id.nav_search).navigate(R.id.action_nav_inicio_to_searchFragment)
         }
    }*/

}