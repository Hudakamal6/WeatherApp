package com.example.weatherapp.homescreen.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.example.safyweather.db.LocalSource
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.homescreen.viewmodel.HomeViewModel
import com.example.weatherapp.homescreen.viewmodel.HomeViewModelFactory
import com.example.weatherapp.model.Repository
import com.example.weatherapp.model.WeatherForecast
import com.example.weatherapp.network.RemoteSource
import com.example.weatherapp.utilities.Convertors
import com.example.weatherapp.utilities.MY_SHARED_PREFERENCES
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {
    lateinit var viewModelFactory: HomeViewModelFactory
    lateinit var animLoading: LottieAnimationView
    lateinit var viewModel: HomeViewModel
    lateinit var hourlyAdapter: HourlyWeatherAdapter
    lateinit var dailyAdapter: DailyWeatherAdapter
    lateinit var layoutManagerHourly: LinearLayoutManager
    lateinit var layoutManagerDaily: LinearLayoutManager
    lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_home, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHomeBinding.bind(view)
        viewModelFactory = HomeViewModelFactory(Repository.getInstance(RemoteSource.getInstance()
            ,LocalSource.getInstance(requireActivity())
            ,requireContext()
            ,requireContext().getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE)))

        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)
        animLoading = view.findViewById(R.id.animationView)

         lifecycleScope.launch{   setupRecyclerViews()
            applyUIChange(viewModel._repo.getCurrentWeatherWithLocationInRepo(30.0444,31.2357,"metric"))
             hourlyAdapter.notifyDataSetChanged()
             dailyAdapter.notifyDataSetChanged()}

    }

    fun setupRecyclerViews() {
        hourlyAdapter = HourlyWeatherAdapter(context as Context, arrayListOf(), "metric")
        dailyAdapter = DailyWeatherAdapter(context as Context, arrayListOf(), "metric")
        layoutManagerHourly =
            LinearLayoutManager(context as Context, LinearLayoutManager.HORIZONTAL, false)
        layoutManagerDaily = LinearLayoutManager(context as Context)
        binding.hourlyRecycler.adapter = hourlyAdapter
        binding.dailyRecycler.adapter = dailyAdapter
        binding.hourlyRecycler.layoutManager = layoutManagerHourly
        binding.dailyRecycler.layoutManager = layoutManagerDaily
    }

    fun applyUIChange(currWeather: WeatherForecast?) {
        currWeather as WeatherForecast
        animLoading.visibility = View.GONE
        binding.currCity.text = currWeather.timezone
        binding.currDate.text = Convertors.getDateFormat(currWeather.current.dt)
        binding.currTime.text = Convertors.getTimeFormat(currWeather.current.dt)
        binding.currTemp.text = currWeather.current.temp.toString()
        binding.currDesc.text = currWeather.current.weather[0].description
        Log.i("TAG", "applyUIChange:---------------------------------------- ${currWeather.current.weather[0].description}")
        binding.currHumidity.text = currWeather.current.humidity.toString()
        binding.currWindSpeed.text = currWeather.current.wind_speed.toString()
        binding.currClouds.text = currWeather.current.clouds.toString()
        binding.currPressure.text = currWeather.current.pressure.toString()

        binding.currUnit.text = getString(R.string.Celsius)
        binding.currWindUnit.text = getString(R.string.windMeter)
        Glide.with(context as Context)
            .load("https://openweathermap.org/img/wn/"+currWeather.current.weather[0].icon+"@2x.png")
            .into(binding.currIcon)
        hourlyAdapter.setHourlyWeatherList(currWeather.hourly)
        dailyAdapter.setDailyWeatherList(currWeather.daily)
    }
}