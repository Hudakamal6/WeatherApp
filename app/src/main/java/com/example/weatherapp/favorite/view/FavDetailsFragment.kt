package com.example.weatherapp.favorite.view

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.weatherapp.R
import com.example.weatherapp.database.LocalSource
import com.example.weatherapp.databinding.FragmentFavDetailsBinding
import com.example.weatherapp.homescreen.view.DailyWeatherAdapter
import com.example.weatherapp.homescreen.view.HourlyWeatherAdapter
import com.example.weatherapp.homescreen.viewmodel.HomeViewModel
import com.example.weatherapp.homescreen.viewmodel.HomeViewModelFactory
import com.example.weatherapp.model.Repository
import com.example.weatherapp.model.Settings
import com.example.weatherapp.model.WeatherForecast
import com.example.weatherapp.network.RemoteSource
import com.example.weatherapp.utilities.Constants.MY_SHARED_PREFERENCES
import com.example.weatherapp.utilities.Convertors
import com.example.weatherapp.utilities.CurrentUser
import com.example.weatherapp.utilities.LocalHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FavDetailsFragment : Fragment() {

    lateinit var viewModelFactory: HomeViewModelFactory
    lateinit var detailsViewModel: HomeViewModel
    lateinit var hourlyAdapter: HourlyWeatherAdapter
    lateinit var dailyAdapter: DailyWeatherAdapter
    lateinit var layoutManagerHourly: LinearLayoutManager
    lateinit var layoutManagerDaily: LinearLayoutManager
    lateinit var binding: FragmentFavDetailsBinding
    private var settings: Settings? = null
    val favDetailsArgs:FavDetailsFragmentArgs  by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModelFactory = HomeViewModelFactory(
            Repository.getInstance(
                RemoteSource.getInstance(),
                LocalSource.getInstance(requireActivity()),
                requireContext(),
                requireContext().getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE)))
        detailsViewModel = ViewModelProvider(this,viewModelFactory).get(HomeViewModel::class.java)
        settings = detailsViewModel.getStoredSettings()
        getData()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fav_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentFavDetailsBinding.bind(view)
      //  navController = Navigation.findNavController(requireActivity(),R.id.nav_host_fragment)


    }

    fun getData() {
        lifecycleScope.launch (Dispatchers.Main){
           Log.i(TAG,"lattttttttttttttttttttttttttttttttttttttttttttt${favDetailsArgs.lat}")
            val weather = detailsViewModel.getWeather(favDetailsArgs.lat.toDouble(),favDetailsArgs.lon.toDouble())
            updateUi(weather)
            setupRecyclerViews()

        }
    }


    fun setupRecyclerViews() {
        layoutManagerHourly =LinearLayoutManager(context as Context, LinearLayoutManager.HORIZONTAL, false)
        layoutManagerDaily = LinearLayoutManager(context as Context)
        binding.hourlyRecycler.adapter = hourlyAdapter
        binding.dailyRecycler.adapter = dailyAdapter
        binding.hourlyRecycler.layoutManager = layoutManagerHourly
        binding.dailyRecycler.layoutManager = layoutManagerDaily
    }

    fun updateUi(currentWeather: WeatherForecast?) {
        currentWeather as WeatherForecast

        binding.currCity.text = LocalHelper.getAddressFromLatLng(requireActivity(),
            currentWeather.lat
            ,currentWeather.lon
        )

        binding.currDate.text = Convertors.getDateFormat(currentWeather.current.dt)
        binding.currTime.text = Convertors.getTimeFormat(currentWeather.current.dt)
        binding.tvTempreture.text = currentWeather.current.temp.toString()
        binding.tvDiscription.text = currentWeather.current.weather[0].description
        binding.tvHumidity.text = currentWeather.current.humidity.toString()
        binding.tvWindSpeed.text = currentWeather.current.wind_speed.toString()
        binding.tvClouds.text = currentWeather.current.clouds.toString()
        binding.tvPressure.text = currentWeather.current.pressure.toString()
        binding.tvWindSpeedUnit.text = getString(R.string.windMile)
        //use picasso
        Glide.with(context as Context)
            .load("https://openweathermap.org/img/wn/"+currentWeather.current.weather[0].icon+"@2x.png")
            .into(binding.ivIcon)

        hourlyAdapter = HourlyWeatherAdapter(requireContext(),currentWeather.hourly,settings?.unit.toString())

        dailyAdapter = DailyWeatherAdapter(requireContext(),currentWeather.daily,settings?.unit.toString())



    }
}