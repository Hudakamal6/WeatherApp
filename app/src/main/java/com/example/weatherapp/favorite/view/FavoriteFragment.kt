package com.example.weatherapp.favorite.view

import android.app.AlertDialog
import android.app.Service
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.R
import com.example.weatherapp.database.LocalSource
import com.example.weatherapp.database.LocalState
import com.example.weatherapp.databinding.FragmentFavoriteBinding
import com.example.weatherapp.favorite.viewModel.FavFactory
import com.example.weatherapp.favorite.viewModel.FavViewModel
import com.example.weatherapp.model.Repository
import com.example.weatherapp.model.WeatherForecast
import com.example.weatherapp.network.RemoteSource
import com.example.weatherapp.utilities.Constants.MY_SHARED_PREFERENCES
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest


class FavoriteFragment : Fragment(), OnFavWeatherClickListener{

   private  val TAG = "FavoriteFragment"
   private lateinit var navController: NavController
   private lateinit var favAdapter: FavAdapter
   private lateinit var layoutManager: LinearLayoutManager
   private lateinit var favViewModelFactory: FavFactory
   private lateinit var favViewModel: FavViewModel
   private lateinit var binding: FragmentFavoriteBinding
   var connectivity : ConnectivityManager? = null
   val args :FavoriteFragmentArgs by navArgs()
   var info : NetworkInfo? = null
   var weatherForecast:WeatherForecast? = null


   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
   }
   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
      return inflater.inflate(R.layout.fragment_favorite, container, false)
   }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      super.onViewCreated(view, savedInstanceState)
      binding = FragmentFavoriteBinding.bind(view)
      navController = Navigation.findNavController(requireActivity(),R.id.nav_host_fragment)

      connectivity = context?.getSystemService(Service.CONNECTIVITY_SERVICE) as ConnectivityManager
      setupFavRecycler()
      initViewModel()
      checkConnection()
      addBtnClicked()

      lifecycleScope.launch(Dispatchers.IO){
         addNew()
         withContext(Dispatchers.Main) {
            getData()
         }
      }

   }


   fun addBtnClicked() {
      binding.floatingAddFav.setOnClickListener {
         val action = FavoriteFragmentDirections.actionFavoriteFragmentToMapsFragment().setIsHome(false)
         navController.navigate(action)

      }
   }
   suspend fun addNew() {
      weatherForecast = favViewModel.getWeather(args.lat.toDouble(),args.lon.toDouble())
      Log.i(TAG, "addNew:  ${args.lat}+ ${args.lon}")
      if (weatherForecast !=null )
         favViewModel.addtoFav(weatherForecast as WeatherForecast)
   }
   suspend fun getData() {
      lifecycleScope.launch() {
         favViewModel.getAllFav().collectLatest{

            when (it) {
               is LocalState.onFail -> { } //hide loader show alert
               is LocalState.onSuccessList -> { favAdapter.setFavWeatherList(it.weatherList) }
               else -> { }//Still loading
            }
            favAdapter.notifyDataSetChanged()
         }
         delay(1000)
      }.job.join()

   }
   fun checkConnection() {
      if ( connectivity != null) {
         info = connectivity!!.activeNetworkInfo
         if (info != null) {
            if (info!!.state == NetworkInfo.State.CONNECTED) {
               updateWeatherDatabase()
            } else{
               binding.floatingAddFav.isEnabled = false
            }
         } else{
            binding.floatingAddFav.isEnabled = false
         }
      }
   }
   fun initViewModel() {
      favViewModelFactory = FavFactory(
         Repository.getInstance(
            RemoteSource.getInstance(),
            LocalSource.getInstance(requireActivity()),
            requireContext(),
            requireContext().getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE)))
      favViewModel = ViewModelProvider(this,favViewModelFactory).get(FavViewModel::class.java)
   }
   fun setupFavRecycler(){
      favAdapter = FavAdapter(requireContext(), emptyList(),this)
      layoutManager = LinearLayoutManager(requireContext())
      binding.favoriteRecycler.adapter = favAdapter
      binding.favoriteRecycler.layoutManager = layoutManager
   }
   override fun onRemoveBtnClick(weather: WeatherForecast) {

      val dialogBuilder = AlertDialog.Builder(requireContext())
      dialogBuilder.setMessage(getString(R.string.deleteMsg))
         .setCancelable(false)
         .setPositiveButton(getString(R.string.delete)) { dialog, id ->//  favViewModel.removeAddressFromFavorites(address)
            lifecycleScope.launch(Dispatchers.IO) {
               favViewModel.delete(weather)
               withContext(Dispatchers.Main) {
                  dialog.cancel()
                  getData()
               }

            }
         }
         .setNegativeButton(getString(R.string.cancel)) { dialog, id -> dialog.cancel() }
      val alert = dialogBuilder.create()
      alert.show()
   }
   override fun onFavItemClick(weather: WeatherForecast) {
      /*
      Log.i("TAG", "onFavItemClick: ")
      //val latIn4Digits: Double = String.format("%.4f", address.lat).toDouble()
      //val lonIn4Digits: Double = String.format("%.4f", address.lon).toDouble()
      favViewModel.getOneWeather(address.lat,address.lon).observe(viewLifecycleOwner) {
          if(it == null){
              Log.i("TAG", "nnnnnnnnnnuuuuuuuuullllllllllllllllllllll222222222222222222222")
          }

          if(navController.currentDestination?.id == R.id.favoriteFragment) {
              val action =
                  FavoriteFragmentDirections.actionFavoriteFragmentToFavoriteDetailsFragment(it)
              navController.navigate(action)
          }
      }

       */
   }
   fun updateWeatherDatabase(){
/*
        val observerName1 = Observer<List<Location>> {
            for (favWeather in it){
                favViewModel.getFavWholeWeather(favWeather.lat,favWeather.lon,"metric")

                val observerName2 = Observer<WeatherForecast> { item ->
                    favViewModel.addOneFavWeather(item) }
                favViewModel.favWeatherFromNetwork.observe(viewLifecycleOwner,observerName2)
            }

 */
   }
   // favViewModel.getAllAddresses().observe(viewLifecycleOwner, observerName1)
   /*favViewModel.getAllAddresses().observe(viewLifecycleOwner){
       for (favWeather in it){
           favViewModel.getFavWholeWeather(favWeather.lat,favWeather.lon,"metric")
           favViewModel.favWeatherFromNetwork.observe(viewLifecycleOwner) {item ->
               favViewModel.addOneFavWeather(item)
           }
       }
   }
}


    */
   override fun onDestroy() {
      super.onDestroy()
      Log.i("TAG", "onDestroy: ")
   }


   }