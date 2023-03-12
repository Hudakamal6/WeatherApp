package com.example.weatherapp.homescreen.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.example.weatherapp.R
import com.example.weatherapp.database.LocalSource
import com.example.weatherapp.databinding.FragmentHomeBinding
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


class HomeFragment : Fragment() {
    lateinit var viewModelFactory: HomeViewModelFactory
    lateinit var homeViewModel: HomeViewModel
    lateinit var hourlyAdapter: HourlyWeatherAdapter
    lateinit var dailyAdapter: DailyWeatherAdapter
    lateinit var layoutManagerHourly: LinearLayoutManager
    lateinit var layoutManagerDaily: LinearLayoutManager
    lateinit var binding: FragmentHomeBinding
    lateinit var settings : Settings
    val args: HomeFragmentArgs by navArgs()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModelFactory = HomeViewModelFactory( Repository(RemoteSource.getInstance(),
            LocalSource.getInstance(requireContext()),requireContext(),requireContext().getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE))
        )
        homeViewModel = ViewModelProvider(this,viewModelFactory).get(HomeViewModel::class.java)
        settings = Settings()
        getData()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_home, container, false)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHomeBinding.bind(view)



    }
    fun getData() {
        lifecycleScope.launch (Dispatchers.Main){
            val weather = homeViewModel.getWeather(args.lat.toDouble(),args.longg.toDouble())
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
            CurrentUser.location.latitude
            ,CurrentUser.location.longitude
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

        hourlyAdapter = HourlyWeatherAdapter(requireContext(),currentWeather.hourly,settings.unit.toString())

        dailyAdapter = DailyWeatherAdapter(requireContext(),currentWeather.daily,settings.unit.toString())



    }
}