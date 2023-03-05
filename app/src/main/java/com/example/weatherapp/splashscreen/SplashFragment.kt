package com.example.weatherapp.splashscreen

import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.safyweather.db.LocalSource
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentSplashBinding
import com.example.weatherapp.model.Repository
import com.example.weatherapp.model.Settings
import com.example.weatherapp.model.WeatherForecast
import com.example.weatherapp.network.RemoteSource
import com.example.weatherapp.utilities.*


class SplashFragment : Fragment() {
    private lateinit var binding: FragmentSplashBinding
    private lateinit var navController: NavController
    private var currentWeather: WeatherForecast? = null
    private var settings: Settings? = null
    private lateinit var repo: Repository



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = FragmentSplashBinding.inflate(layoutInflater)
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)

        Handler().postDelayed({
            if (currentWeather == null) {
                navController.navigate(R.id.action_splashFragment_to_intialFragment)
            }
//            else{
//                val action =SplashFragmentDirections.actionSplashFragmentToHomeFragment(
//                    currentWeather?.lat?.toFloat() as Float,
//                    currentWeather?.lon?.toFloat() as Float,
//                    arrayOfUnits[settings?.unit as Int])
//                navController.navigate(action)
//            }
        }, 1000)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repo = Repository.getInstance(
            RemoteSource.getInstance(),LocalSource.getInstance(requireActivity()),
            requireContext(), requireContext().getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE))
        currentWeather = repo.getWeatherSharedPreferences()
        settings = repo.getSettingsSharedPreferences()
        if(settings == null){
            this.settings = Settings(ENGLISH,STANDARD,NONE,ENABLED)
            repo.addSettingsToSharedPreferences(settings as Settings)
        }
        else{
            if(settings?.language as Boolean) {
                LocalHelper.setLocale(requireContext(), "en")
            }
            else{
                LocalHelper.setLocale(requireContext(), "ar")
            }
        }
    }

}
