package com.example.weatherapp.startscreen

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.weatherapp.databinding.FragmentIntialBinding
import com.example.weatherapp.model.Repository
import com.example.weatherapp.model.Settings
import com.example.weatherapp.network.RemoteSource
import com.example.weatherapp.utilities.Constants.MY_SHARED_PREFERENCES
import com.example.weatherapp.utilities.UserHelper
import com.example.weatherapp.utilities.UserHelper.settings
import com.example.weatherapp.R
import com.example.weatherapp.database.LocalSource
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng


class IntialFragment : Fragment() {

    private val TAG = "Initial Fragment "

    lateinit var binding: FragmentIntialBinding
    val PERMISSION_ID = 100
    lateinit var fusedLocation: FusedLocationProviderClient
    lateinit var initialDialog: Dialog
    lateinit var navController: NavController
    var connectivity : ConnectivityManager? = null
    var info : NetworkInfo? = null
    private lateinit var setting: Settings
    private lateinit var repo: Repository
    lateinit var gpsLocation: RadioButton
    lateinit var startBtn: Button


    val locationCallBack = object : LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            Log.i("TAG", "onLocationResult:")
            var loc = p0?.lastLocation as Location
            Log.i("TAG", "latttitudeee :"+loc.latitude+" looooooongttide : "+loc.longitude)
            settings.location = 1
            repo.addSettingsToSharedPreferences(setting)
            repo.addWeatherToSHP(LatLng(loc.latitude,loc.longitude))
            UserHelper.location = LatLng(loc.latitude,loc.longitude)

            val action = IntialFragmentDirections.actionIntialFragmentToHomeFragment22().setLat(loc.latitude.toFloat()).setLongg(loc.longitude.toFloat())
            navController.navigate(action)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentIntialBinding.inflate(layoutInflater)
        setting = Settings()

        navController = Navigation.findNavController(requireActivity(),R.id.nav_host_fragment)
        repo  = Repository(RemoteSource.getInstance(), LocalSource(requireContext())
            ,requireContext(),
            requireContext().getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        fusedLocation = LocationServices.getFusedLocationProviderClient(requireContext())

        initDialog()
        startBtnClicked()
        return binding.root

    }

    fun initDialog() {
        initialDialog = Dialog(requireContext())
        initialDialog.setContentView(R.layout.initial_setup_dialog)
        initialDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        initialDialog
        gpsLocation  = initialDialog.findViewById(R.id.gpsLocation)
        startBtn = initialDialog.findViewById(R.id.initialSetupBtn)
        initialDialog.show()
    }
    fun startBtnClicked() {
        startBtn.setOnClickListener {
            connectivity = context?.getSystemService(Service.CONNECTIVITY_SERVICE) as ConnectivityManager

            //check  network connection
            if ( connectivity != null) {
                info = connectivity!!.activeNetworkInfo

                if (info != null) {
                    if (info!!.state == NetworkInfo.State.CONNECTED) {
                        if(gpsLocation.isChecked) {
                            getFreshLocationRequest()
                            Log.i(TAG, "get location successfulllllllllllllll")
                        }
                        else{

                            val action =  IntialFragmentDirections.actionIntialFragmentToMapsFragment().setIsHome(true)
                            navController.navigate(action)
                        }
                    }
                    else{
                        Toast.makeText(requireContext(), " Please Check Your Network Connection!", Toast.LENGTH_LONG).show()
                    }
                }
            }
            initialDialog.dismiss()
        }
    }
    fun checkPermissions():Boolean{
        Log.i(TAG, "checkPermissions: ")
        return ActivityCompat.checkSelfPermission(context as Context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context as Context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun isLocationEnabled():Boolean{
        var lm: LocationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    fun enableLocationSettings(){
        var settingsIntent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(settingsIntent)
    }

    fun requestLocationPermissions(){
        Log.i("TAG", "requestPermissions:::::::::::::::::::::::::::::::::::::::::::::: ")
        requestPermissions(arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION),
            PERMISSION_ID)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        Log.i(TAG, "onRequestPermissionsResult: ")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == PERMISSION_ID){
            if(grantResults.size>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.i(TAG, "onRequestPermissionsResult::::::::::::::::::::::::::::::::::::::::::::")
                requestNewLocationData()
            }
            else{
                Toast.makeText(context as Context, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun requestNewLocationData(){
        Log.i(TAG, "requestNewLocationData: ")
        val locationRequest = LocationRequest.create()
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest.setInterval(5)
        locationRequest.setFastestInterval(0)
        locationRequest.setNumUpdates(1)

        fusedLocation.requestLocationUpdates(locationRequest,locationCallBack, Looper.myLooper())
    }

    fun getFreshLocationRequest(){
        if(checkPermissions()) {
            Log.i(TAG, "permissions checked successfullyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy ")
            if(isLocationEnabled()) {
                Log.i(TAG, " permissions enabeled successfullyyyyyyyyyyyyyyyyyyyyyyyyyy ")
                requestNewLocationData();
            }
            else{
                enableLocationSettings();
            }
        }
        else{
            Log.i(TAG, "request permissions ")
            Toast.makeText(activity, "Please Enable  Location Permission", Toast.LENGTH_SHORT).show();
            requestLocationPermissions()
        }
    }


}