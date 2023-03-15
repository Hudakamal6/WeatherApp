package com.example.weatherapp.homescreen.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.example.weatherapp.R
import com.example.weatherapp.database.FavState
import com.example.weatherapp.database.LocalSource
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.homescreen.viewmodel.HomeViewModel
import com.example.weatherapp.homescreen.viewmodel.HomeViewModelFactory
import com.example.weatherapp.model.Repository
import com.example.weatherapp.model.WeatherForecast
import com.example.weatherapp.network.RemoteSource
import com.example.weatherapp.network.RetroFitState
import com.example.weatherapp.utilities.Constants.MY_SHARED_PREFERENCES
import com.example.weatherapp.utilities.Convertors
import com.example.weatherapp.utilities.UserHelper
import com.example.weatherapp.utilities.LocalHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.job
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {
    lateinit var viewModelFactory: HomeViewModelFactory
    lateinit var animLoading: LottieAnimationView
    lateinit var homeViewModel: HomeViewModel
    lateinit var hourlyAdapter: HourlyWeatherAdapter
    lateinit var dailyAdapter: DailyWeatherAdapter
    lateinit var layoutManagerHourly: LinearLayoutManager
    lateinit var layoutManagerDaily: LinearLayoutManager
    lateinit var binding: FragmentHomeBinding
    private lateinit var preferences: Preference
    private lateinit var units: String
    private lateinit var language: String
    val args: HomeFragmentArgs by navArgs()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModelFactory = HomeViewModelFactory(
            Repository(
                RemoteSource.getInstance(),
                LocalSource.getInstance(requireContext()),
                requireContext(),
                requireContext().getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE)
            )
        )
        homeViewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)
        loadSettings()

        if (UserHelper.networkState) {
            lifecycleScope.launch(Dispatchers.Main) {
                getData()}
        } else {
            Toast.makeText(requireContext(), "Please Connect to The Network", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun loadSettings() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        units = sharedPreferences.getString("unit", "")!!
        language = sharedPreferences.getString("language", "en")!!


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
        animLoading = view.findViewById(R.id.animationLoading)
        preferences = Preference(requireContext())

    }

    suspend fun getData() {
        lifecycleScope.launch(Dispatchers.Main) {
           homeViewModel.getWeather(
                UserHelper.location.latitude,
                UserHelper.location.longitude,
                language,
                units
            )
                .collectLatest {
                    when (it) {
                        is RetroFitState.onFail -> {} //hide loader show alert
                        is RetroFitState.onSuccess -> {
                            updateUi(it.weatherForecast)
                            setupRecyclerViews()
                        }
                        else -> {}
                    }
                }
        }.job.join()
    }


        fun setupRecyclerViews() {
            layoutManagerHourly =
                LinearLayoutManager(context as Context, LinearLayoutManager.HORIZONTAL, false)
            layoutManagerDaily = LinearLayoutManager(context as Context)
            binding.hourlyRecycler.adapter = hourlyAdapter
            binding.dailyRecycler.adapter = dailyAdapter
            binding.hourlyRecycler.layoutManager = layoutManagerHourly
            binding.dailyRecycler.layoutManager = layoutManagerDaily
        }

        fun updateUi(currentWeather: WeatherForecast?) {
            currentWeather as WeatherForecast

            animLoading.visibility = View.GONE
            binding.currCity.text = LocalHelper.getAddressFromLatLng(
                requireActivity(),
                UserHelper.location.latitude, UserHelper.location.longitude
            )

            val humidity = currentWeather.current.humidity.toString()
            val windSpeed = currentWeather.current.wind_speed.toString()
            val pressure = currentWeather.current.pressure.toString()
            val clouds = currentWeather.current.clouds.toString()
            val description = currentWeather.current.weather[0].description
            val icon = currentWeather.current.weather[0].icon
            val temp = currentWeather.current.temp.toInt().toString()
            Log.e("TAG", icon)
            when (icon) {
                "01d" -> binding.ivIcon.setImageResource(R.drawable.sun)
                "02d" -> binding.ivIcon.setImageResource(R.drawable.few_cloudy)
                "03d" -> binding.ivIcon.setImageResource(R.drawable.clouds)
                "04d" -> binding.ivIcon.setImageResource(R.drawable.icon1)
                "09d" -> binding.ivIcon.setImageResource(R.drawable.shower_rain)
                "10d" -> binding.ivIcon.setImageResource(R.drawable.rainy)
                "11d" -> binding.ivIcon.setImageResource(R.drawable.thunderstorm)
                "13d" -> binding.ivIcon.setImageResource(R.drawable.snow)
                "50d" -> binding.ivIcon.setImageResource(R.drawable.icon2)
                "01n" -> binding.ivIcon.setImageResource(R.drawable.sun)
                "02n" -> binding.ivIcon.setImageResource(R.drawable.scarred)
                "03n" -> binding.ivIcon.setImageResource(R.drawable.clouds)
                "04n" -> binding.ivIcon.setImageResource(R.drawable.icon2)
                "09n" -> binding.ivIcon.setImageResource(R.drawable.rainy)
                "10n" -> binding.ivIcon.setImageResource(R.drawable.rain)
                "11n" -> binding.ivIcon.setImageResource(R.drawable.thunderstorm)
                "13n" -> binding.ivIcon.setImageResource(R.drawable.snow)
                "50n" -> binding.ivIcon.setImageResource(R.drawable.icon2)

            }
            binding.tvTempreture.text = temp
            binding.tvDiscription.text = description
            binding.tvHumidityTemp.text = "$humidity %"
            binding.tvPressureUnit.text = "$pressure hpa"
            binding.tvWindSpeedUnit.text = "$windSpeed m/s"
            binding.tvCloudsUnit.text = "$clouds %"

            if (temp.toInt() <= 32) {
                binding.tvUnit.text = "C"
                binding.tvWindSpeedUnit.text = "$windSpeed m/s"
            } else if (temp.toInt() in 33..273) {
                binding.tvUnit.text = "F"
                binding.tvWindSpeedUnit.text = "$windSpeed ms/h"
            } else {
                binding.tvUnit.text = "K"
                binding.tvWindSpeedUnit.text = "$windSpeed m/s"
            }

            binding.currDate.text = Convertors.getDateFormat(currentWeather.current.dt)
            binding.currTime.text = Convertors.getTimeFormat(currentWeather.current.dt)
            binding.tvTempreture.text = currentWeather.current.temp.toString()
            binding.tvDiscription.text = currentWeather.current.weather[0].description
            binding.tvHumidity.text = currentWeather.current.humidity.toString()
            binding.tvWindSpeed.text = currentWeather.current.wind_speed.toString()
            binding.tvClouds.text = currentWeather.current.clouds.toString()
            binding.tvPressure.text = currentWeather.current.pressure.toString()
            binding.tvWindSpeedUnit.text = getString(R.string.windMile)
            Glide.with(context as Context)
                .load("https://openweathermap.org/img/wn/" + currentWeather.current.weather[0].icon + "@2x.png")
                .into(binding.ivIcon)

            hourlyAdapter = HourlyWeatherAdapter(requireContext(), currentWeather.hourly, units)
            dailyAdapter = DailyWeatherAdapter(requireContext(), currentWeather.daily, units)


        }
    }
