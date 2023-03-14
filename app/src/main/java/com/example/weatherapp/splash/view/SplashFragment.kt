package com.example.weatherapp.splash.view

import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.weatherapp.R
import com.example.weatherapp.database.LocalSource
import com.example.weatherapp.databinding.FragmentSplashBinding
import com.example.weatherapp.homescreen.view.HomeFragmentArgs
import com.example.weatherapp.model.Repository
import com.example.weatherapp.model.Settings
import com.example.weatherapp.network.RemoteSource
import com.example.weatherapp.splash.viewmodel.SplashViewModel
import com.example.weatherapp.splash.viewmodel.SplashViewModelFactory
import com.example.weatherapp.utilities.*
import com.example.weatherapp.utilities.Constants.MY_SHARED_PREFERENCES
import com.google.android.gms.maps.model.LatLng


class SplashFragment : Fragment() {
    private lateinit var binding: FragmentSplashBinding
    private lateinit var navController: NavController
    private  var currentWeather: LatLng? = null
    private var settings: Settings? = null
    private lateinit var splashViewModel : SplashViewModel
    private lateinit var splashFactory : SplashViewModelFactory
     val args :HomeFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        splashFactory = SplashViewModelFactory(
            Repository.getInstance(RemoteSource.getInstance(),
            LocalSource.getInstance(requireContext()),
                requireContext(),
                requireContext().getSharedPreferences(MY_SHARED_PREFERENCES,Context.MODE_PRIVATE)))
        splashViewModel = ViewModelProvider(this,splashFactory).get(SplashViewModel::class.java)

        binding = FragmentSplashBinding.inflate(layoutInflater)
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)

        Handler().postDelayed({
           if (currentWeather == null) {
               navController.navigate(R.id.action_splashFragment_to_intialFragment)
            }
            else{
                CurrentUser.location = currentWeather as LatLng
                val action = SplashFragmentDirections.actionSplashFragmentToHomeFragment2()
//                    .setLat(
//                    currentWeather!!.latitude.toFloat()).setLongg(currentWeather!!.longitude.toFloat())
                navController.navigate(action)
            }
        }, 1000)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentWeather = splashViewModel.getLocatinSP()

//       settings = repo.getSettingsSharedPreferences()
//        if(settings == null){
//            this.settings = Settings()
//            repo.addSettingsToSharedPreferences(settings as Settings)
//        }
//        else{
//            if(settings?.language as Boolean) {
//                LocalHelper.setLocale(requireContext(), "en")
//            }
//            else{
//                LocalHelper.setLocale(requireContext(), "ar")
//            }
//        }
//    }
    }
    }

