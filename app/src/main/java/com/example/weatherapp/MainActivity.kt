package com.example.weatherapp

import android.os.Bundle
import android.view.View
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.weatherapp.databinding.ActivityMainBinding
import com.ismaeldivita.chipnavigation.ChipNavigationBar

class MainActivity : AppCompatActivity() {
//    lateinit var navigationDrawer: DrawerLayout
//    lateinit var navigationView: NavigationView
//    lateinit var navController: NavController
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        navigationDrawer = findViewById(R.id.mainDrawer)
//        navigationView = findViewById(R.id.mainNavView)
//
//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
//        navController = navHostFragment.findNavController()
//
//        navigationView.setupWithNavController(navController)
//
//    }
//}
val bottomChip by lazy { findViewById<ChipNavigationBar>(R.id.bottomChip) }

 private lateinit var navController: NavController
 private lateinit var binding: ActivityMainBinding

 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  binding = ActivityMainBinding.inflate(layoutInflater)
  setContentView(binding.root)

  val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
  navController = navHostFragment.findNavController()
  bottomChip.setItemSelected(R.id.home)
  bottomChip.setOnItemSelectedListener { id->

   when(id) {R.id.home->
    findNavController(this@MainActivity, R.id.nav_host_fragment)
      .navigate(R.id.homeFragment2)

  R.id.favorites ->
   findNavController(this@MainActivity,R.id.nav_host_fragment)
      .navigate(R.id.favoriteFragment)


    R.id.alerts->
     findNavController(this@MainActivity,R.id.nav_host_fragment)
      .navigate(R.id.alertsFragment)


    else ->
  findNavController(this@MainActivity,R.id.nav_host_fragment)
      .navigate(R.id.settingsFragment2)
   }
  }

  handelNavigationVisual()

 }


 fun handelNavigationVisual() {
  navController.addOnDestinationChangedListener(object :
   NavController.OnDestinationChangedListener {
   override fun onDestinationChanged(
    @NonNull controller: NavController,
    @NonNull destination: NavDestination,
    arguments: Bundle?
   ) {
    when (destination.id) {

    R.id.splashFragment,
    R.id.intialFragment,
   // R.id.mapsFragment
     -> bottomChip.setVisibility(
      View.GONE
     )
     else -> bottomChip.setVisibility(View.VISIBLE)
    }
   }
  })
 }








}