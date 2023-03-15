package com.example.weatherapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.weatherapp.alerts.Alert
import com.example.weatherapp.alerts.viewmodel.AlertsViewModel
import com.example.weatherapp.alerts.viewmodel.AlertsViewModelFactory
import com.example.weatherapp.database.LocalSource
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.model.AlertData
import com.example.weatherapp.model.Repository
import com.example.weatherapp.network.RemoteSource
import com.example.weatherapp.utilities.Constants
import com.example.weatherapp.utilities.UserHelper
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
 val bottomChip by lazy { findViewById<ChipNavigationBar>(R.id.bottomChip) }
 private lateinit var navController: NavController
 private lateinit var binding: ActivityMainBinding
 private lateinit var alertViewModel:AlertsViewModel
 private lateinit var alertsViewModelFactory: AlertsViewModelFactory



 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  binding = ActivityMainBinding.inflate(layoutInflater)
  setContentView(binding.root)


     alertsViewModelFactory = AlertsViewModelFactory( Repository(
         RemoteSource.getInstance(),
         LocalSource.getInstance(this ), this, this.getSharedPreferences(
             Constants.MY_SHARED_PREFERENCES, MODE_PRIVATE)))

     alertViewModel = ViewModelProvider(this,alertsViewModelFactory).get(AlertsViewModel::class.java)
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

     networkChange()
     handelNavigationVisual()
     sendAlert()


 }
 private fun sendAlert() {
       val alertsManager = Alert(this)
       var alerts : List<AlertData>
       lifecycleScope.launch {
          alertViewModel.getAllAlertsInVM().observe(this@MainActivity) { it ->
             it.forEach {
             Log.i("alertsssMain", "sendAlerts :sendddddddddddd ")
             alertsManager.fireAlert(it)
             }
          }
       }
 }


 fun handelNavigationVisual() {
  navController.addOnDestinationChangedListener(object :
   NavController.OnDestinationChangedListener {
   override fun onDestinationChanged(
    @NonNull controller: NavController,
    @NonNull destination: NavDestination,
    arguments: Bundle?) {
    when (destination.id) {

    R.id.splashFragment,
    R.id.intialFragment,
    R.id.mapsFragment
     -> bottomChip.setVisibility(View.GONE)
     else -> bottomChip.setVisibility(View.VISIBLE) }
   }
   })
 }


    private fun networkChange() {
        UserHelper.networkState =checkConnection(this@MainActivity)
    }

    fun checkConnection(context: Context): Boolean {

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {

                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }
}