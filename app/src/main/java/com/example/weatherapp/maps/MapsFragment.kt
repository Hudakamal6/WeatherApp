package com.example.weatherapp.maps


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.weatherapp.R
import com.example.weatherapp.database.LocalSource
import com.example.weatherapp.model.Repository
import com.example.weatherapp.model.Settings
import com.example.weatherapp.network.RemoteSource
import com.example.weatherapp.utilities.Constants.MY_SHARED_PREFERENCES
import com.example.weatherapp.utilities.UserHelper
import com.example.weatherapp.utilities.LocalHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {
    private lateinit var navController: NavController

    private val args: MapsFragmentArgs by navArgs()
    private lateinit var repo: Repository

    private var setting: Settings? = null
    private val callback = OnMapReadyCallback { googleMap ->
        moveCamera(googleMap)
        onMapClicked(googleMap)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        repo = Repository.getInstance(
            RemoteSource.getInstance(), LocalSource(requireContext()), requireContext(),
            requireContext().getSharedPreferences(
              MY_SHARED_PREFERENCES,
                Context.MODE_PRIVATE
            )
        )
        setting = Settings()
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
    }

    fun moveCamera(
        map: GoogleMap,
        location: LatLng = LatLng(
            UserHelper.location.latitude,
            UserHelper.location.longitude
        )
    ) {
        map.addMarker(MarkerOptions().position(location))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))

    }

    fun onMapClicked(map: GoogleMap) {
        map.setOnMapClickListener {
            //clear all marker
            map.clear()
            moveCamera(map, LatLng(it.latitude, it.longitude))

            val dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder.setMessage(getString(R.string.saveLocation))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.save)) { dialog, id ->
                    goTo(it)
                    dialog.cancel()
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, id -> dialog.cancel() }
            val alert = dialogBuilder.create()
            alert.show()
        }
    }

    fun goTo(loc: LatLng) {

        repo.addWeatherToSHP(loc)
        if (args.isHome) {
            UserHelper.location = loc
            repo.addSettingsToSharedPreferences(setting!!)
            repo.addWeatherToSHP(LatLng(loc.latitude, loc.longitude))

            val action = MapsFragmentDirections.actionMapsFragmentToHomeFragment2().setLat(loc.latitude.toFloat()).setLongg(loc.longitude.toFloat())
            navController.navigate(action)
        }
         if(args.isAlert){
            val action = MapsFragmentDirections.actionMapsFragmentToAlertsFragment().setLat(loc.latitude.toFloat()).setLon(loc.longitude.toFloat())
            navController.navigate(action)
        }
         if(args.isSettings) {
             UserHelper.location = loc
             repo.addSettingsToSharedPreferences(setting!!)
             repo.addWeatherToSHP(LatLng(loc.latitude, loc.longitude))

             val action = MapsFragmentDirections.actionMapsFragmentToSettingsFragment2()
             navController.navigate(action)

        }

        if (args.isFav) {
           val action = MapsFragmentDirections.actionMapsFragmentToFavoriteFragment()
               .setLon(loc.longitude.toFloat())
               .setLat(loc.latitude.toFloat())
              .setName(
                   LocalHelper.getAddressFromLatLng(
                       requireContext(),
                       loc.latitude,
                       loc.longitude
                   )
               ).setIsMap(true)
           navController.navigate(action)
      }
    }

}


//    private lateinit var navController: NavController
//
//    private val args: MapsFragmentArgs by navArgs()
//    private lateinit var repo: Repository
//
//    // TODO: duplicated setting object we need setting class
//    private var setting: Settings? = null
//    private val callback = OnMapReadyCallback { googleMap ->
//        moveCamera(googleMap)
//        onMapClicked(googleMap)
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//
//        val api_Key = getString(R.string.google_maps_key)
//        if (!Places.isInitialized()) {
//            Places.initialize(activity?.applicationContext,api_Key)
//        }
//        val PlacesClient = Places.createClient(activity?.applicationContext)
//
//        // Initialize the AutocompleteSupportFragment.
//        val autocompleteFragment = parentFragmentManager?.findFragmentById(R.id.autocomplete_fragment)
//                    as? AutocompleteSupportFragment
//
//        // Specify the types of place data to return.
//        autocompleteFragment?.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
//
//        // Set up a PlaceSelectionListener to handle the response.
//        autocompleteFragment?.setOnPlaceSelectedListener(object : PlaceSelectionListener {
//            override fun onPlaceSelected(place: Place) {
//                // TODO: Get info about the selected place.
//                Log.i(TAG, "Place: ${place.name}, ${place.id}")
//            }
//
//            override fun onError(status: Status) {
//                // TODO: Handle the error.
//                Log.i(TAG, "An error occurred: $status")
//            }
//        })
//    }
//
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//
//        repo = Repository.getInstance(
//            RemoteSource.getInstance(), LocalSource(requireContext()), requireContext(),
//            requireContext().getSharedPreferences(
//                MY_SHARED_PREFERENCES,
//                Context.MODE_PRIVATE
//            )
//        )
//        setting = Settings()
//        return inflater.inflate(R.layout.fragment_maps, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
//        mapFragment?.getMapAsync(callback)
//        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
//    }
//
//    fun moveCamera(
//        map: GoogleMap,
//        location: LatLng = LatLng(
//            CurrentUser.location.latitude,
//            CurrentUser.location.longitude
//        )
//    ) {
//        map.addMarker(MarkerOptions().position(location))
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
//
//    }
//
//    fun onMapClicked(map: GoogleMap) {
//        map.setOnMapClickListener {
//            //get clicked lat and long adress name to put it in favorite
//            var addressName =
//                LocalHelper.getAddressFromLatLng(requireContext(), it.latitude, it.longitude)
//
//            //clear all marker
//            map.clear()
//            moveCamera(map, LatLng(it.latitude, it.longitude))
//
//
//            // TODO:  Duplicated alertDialog make in seperate class
//            val dialogBuilder = AlertDialog.Builder(requireContext())
//            dialogBuilder.setMessage(getString(R.string.saveLocation))
//                .setCancelable(false)
//                .setPositiveButton(getString(R.string.save)) { dialog, id ->
//                    //there ara another argument here
//                    nextDestination(it)
//                    dialog.cancel()
//                }
//                .setNegativeButton(getString(R.string.cancel)) { dialog, id -> dialog.cancel() }
//            val alert = dialogBuilder.create()
//            alert.show()
//        }
//    }
//
//    fun nextDestination(loc: LatLng) {
//
//        repo.addWeatherToSHP(loc)
//        if (args.isHome) {
//            CurrentUser.location = loc
//            repo.addSettingsToSharedPreferences(setting!!)
//            repo.addWeatherToSHP(LatLng(loc.latitude, loc.longitude))
//            CurrentUser.location = LatLng(loc.latitude, loc.longitude)
//
//            // TODO: Remove current user and use args
//            val action = MapsFragmentDirections.actionMapsFragmentToHomeFragment2(
//                loc.latitude.toFloat(),
//                loc.longitude.toFloat()
//            )
//            navController.navigate(action)
//            //Navigation.findNavController(requireActivity(), R.id.dashBoardContainer).navigate(R.id.homeFragment)
////        } else {
////            val action = MapFragmentDirections.actionMapFragmentToFavoriteFragment()
////                .setLongt(loc.longitude.toFloat())
////                .setLat(loc.latitude.toFloat())
////                .setName(
////                    LocalityManager.getAddressFromLatLng(
////                        requireContext(),
////                        loc.latitude,
////                        loc.longitude
////                    )
////                )
////            navController.navigate(action)
////        }
//
//
//        }
//    }
//}