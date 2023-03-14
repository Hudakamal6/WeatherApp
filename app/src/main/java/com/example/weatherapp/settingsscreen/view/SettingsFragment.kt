package com.example.weatherapp.settingsscreen.view



import android.app.AlertDialog
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.preference.*
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.example.weatherapp.R
import com.example.weatherapp.database.LocalSource
import com.example.weatherapp.databinding.FragmentSettingsBinding
import com.example.weatherapp.model.Repository
import com.example.weatherapp.model.Settings
import com.example.weatherapp.network.RemoteSource
import com.example.weatherapp.settingsscreen.viewmodel.SettingsViewModel
import com.example.weatherapp.settingsscreen.viewmodel.SettingsViewModelFactory
import com.example.weatherapp.utilities.Constants.MY_SHARED_PREFERENCES
import com.example.weatherapp.utilities.LocalHelper
import java.util.*

//class SettingsFragment : PreferenceFragmentCompat() {
//
//    var changeLanguage = false
//    private lateinit var sharedPreferences: SharedPreferences
//    private lateinit var prefrenceScreen: PreferenceScreen
//    private lateinit var editor: SharedPreferences.Editor
//
//
//    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
//        setPreferencesFromResource(R.xml.root_preferences, rootKey)
//        prefrenceScreen = preferenceScreen
//        sharedPreferences = getDefaultSharedPreferences(requireContext())
//        editor = sharedPreferences.edit()
//
//        setLanguage()
//        setMapLocation()
//        setNotification()
//        setUnitPreference()
//        setGPS()
//    }
//
//    private fun setGPS() {
//        val location = findPreference<SwitchPreference>("USE_DEVICE_LOCATION")
//        location?.setOnPreferenceClickListener {
//            //get location to pass it to home???
//            it.isEnabled
//        }
//
//    }
//
//    private fun setUnitPreference() {
//        val unitPreference = findPreference<ListPreference>("unit")
//        unitPreference?.setOnPreferenceChangeListener { prefrs, obj ->
//            val items = obj as String
//            when (items) {
//                "METRIC" -> {
//                    editor.putString("unit", "C")
//                    editor.commit()
//                }
//                "IMPERIAL" -> {
//                    editor.putString("unit", "F")
//                    editor.commit()
//                }
//                "STANDARD" -> {
//                    editor.putString("unit", "K")
//                    editor.commit()
//                }
//
//            }
//            restartApp()
//            true
//        }
//    }
//
//    private fun setNotification() {
//        val notifictaio = findPreference<SwitchPreference>("notification_prefrence_key")
//        notifictaio?.setOnPreferenceClickListener {
//            if (it.key == "notification_prefrence_key") {
//                // Navigation.findNavController(requireView()).navigate(R.id.alertsFragment)
//            }
//            true
//        }
//    }
//
//    //======================================================
//    private fun setMapLocation() {
//        val locationUsingMap = findPreference<Preference>("CUSTOM_LOCATION")!!
//        locationUsingMap.setOnPreferenceClickListener {
//            val shared = PreferenceManager.getDefaultSharedPreferences(requireContext())
//            if (shared.getBoolean("CUSTOM_LOCATION", true)) {
//                val bundle = Bundle()
//                bundle.putInt("setting", 1)
//                Navigation.findNavController(requireView())
//                    .navigate(R.id.action_settingsFragment2_to_mapsFragment, bundle)
//            }
//            true
//        }
//    }
//
//    //========================================================
//    private fun setLanguage() {
//        var language = findPreference<ListPreference>("language")
//        language?.setOnPreferenceChangeListener { prefrs, obj ->
//            changeLanguage = false
//            val items = obj as String
//            if (prefrs.key == "language") {
//                when (items) {
//                    "ar" -> setLocale("ar")
//                    "en" -> setLocale("en")
//                }
//
//                restartApp()
//            }
//            editor.putBoolean("isUpdated", true)
//            editor.commit()
//            false
//        }
//    }
//
//    private fun setLocale(lng: String) {
//        val locale = Locale(lng)
//        Locale.setDefault(locale)
//        val config = Configuration()
//        config.setLocale(locale)
//        resources.updateConfiguration(config, resources.displayMetrics)
//    }
//
//    //=========================================================
//
//
//    override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean) {
//    }
//
//    private fun restartApp() {
//        val intent = requireActivity().intent
//        intent.addFlags(
//            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
//                    or Intent.FLAG_ACTIVITY_NO_ANIMATION
//        )
//        requireActivity().overridePendingTransition(0, 0)
//        requireActivity().finish()
//        requireActivity().overridePendingTransition(0, 0)
//        startActivity(intent)
//    }
//
//}


/*class SettingsFragment : Fragment() {

    lateinit var settingsViewModel: SettingsViewModel
    lateinit var settingsViewModelFactory: SettingsViewModelFactory
    lateinit var binding: FragmentSettingsBinding
    var settings: Settings? = null
    lateinit var navController: NavController
    var connectivity : ConnectivityManager? = null
    var info : NetworkInfo? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSettingsBinding.bind(view)
        navController = Navigation.findNavController(requireActivity(),R.id.nav_host_fragment)
        settingsViewModelFactory = SettingsViewModelFactory(
            Repository.getInstance(
                RemoteSource.getInstance()
                , LocalSource.getInstance(requireActivity())
                ,requireContext()
                ,requireContext().getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE)))
        settingsViewModel = ViewModelProvider(this,settingsViewModelFactory).get(SettingsViewModel::class.java)

        settings = settingsViewModel.getStoredSettings()

        setupUI()

        binding.notiEnable.setOnClickListener{
            settings?.notification = true
            settingsViewModel.setSettingsSharedPrefs(settings as Settings)
        }

        binding.notiDisable.setOnClickListener{
            settings?.notification = false
            settingsViewModel.setSettingsSharedPrefs(settings as Settings)

            val dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder.setMessage(getString(R.string.warning))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok)) { dialog, id ->
                    dialog.cancel()
                }
            val alert = dialogBuilder.create()
            alert.show()
        }

        binding.GPS.setOnClickListener{
            settings?.location = 1
            settingsViewModel.setSettingsSharedPrefs(settings as Settings)
            connectivity = context?.getSystemService(Service.CONNECTIVITY_SERVICE) as ConnectivityManager
            if ( connectivity != null) {
                info = connectivity!!.activeNetworkInfo
                Log.i("TAG", "connectivity != null")
                if (info != null) {
                    if (info!!.state == NetworkInfo.State.CONNECTED) {

                    }
                    else{
                        val dialogBuilder = AlertDialog.Builder(requireContext())
                        dialogBuilder.setMessage(getString(R.string.networkWarning))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.ok)) { dialog, id ->
                                dialog.cancel()
                            }
                        val alert = dialogBuilder.create()
                        alert.show()
                    }
                }
            }
        }

        binding.map.setOnClickListener{
            settings?.location = 2
            settingsViewModel.setSettingsSharedPrefs(settings as Settings)
            connectivity = context?.getSystemService(Service.CONNECTIVITY_SERVICE) as ConnectivityManager
            if ( connectivity != null) {
                info = connectivity!!.activeNetworkInfo
                Log.i("TAG", "connectivity != null")
                if (info != null) {
                    if (info!!.state == NetworkInfo.State.CONNECTED) {

                        val action = SettingsFragmentDirections.actionSettingsFragment2ToMapsFragment()
                        navController.navigate(action)
                    }
                    else{
                        val dialogBuilder = AlertDialog.Builder(requireContext())
                        dialogBuilder.setMessage(getString(R.string.networkWarning))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.ok)) { dialog, id ->
                                dialog.cancel()
                            }
                        val alert = dialogBuilder.create()
                        alert.show()
                    }
                }
            }
        }

        binding.standardUnit.setOnClickListener{
            settings?.unit = 0
            settingsViewModel.setSettingsSharedPrefs(settings as Settings)
        }

        binding.metricUnit.setOnClickListener{
            settings?.unit = 1
            settingsViewModel.setSettingsSharedPrefs(settings as Settings)
        }

        binding.imperialUnit.setOnClickListener{
            settings?.unit = 2
            settingsViewModel.setSettingsSharedPrefs(settings as Settings)
        }

        binding.arabicLang.setOnClickListener{
            settings?.language = false
            settingsViewModel.setSettingsSharedPrefs(settings as Settings)
            LocalHelper.setLocale(requireContext(),"ar")
        }

        binding.englishLang.setOnClickListener{
            settings?.language = true
            settingsViewModel.setSettingsSharedPrefs(settings as Settings)
            LocalHelper.setLocale(requireContext(),"en")
        }

    }

    fun setupUI(){
        if(settings?.language as Boolean){ binding.englishLang.isChecked = true }
        else{ binding.arabicLang.isChecked = true }

        if(settings?.unit as Int == 0){binding.standardUnit.isChecked = true}
        else if(settings?.unit as Int == 1){binding.metricUnit.isChecked = true}
        else{binding.metricUnit.isChecked = true}

        if(settings?.location  as Int== 1){binding.GPS.isChecked = true}
        else if(settings?.location as Int == 2){binding.map.isChecked = true}

        if(settings?.notification as Boolean){binding.notiEnable.isChecked = true}
        else{binding.notiDisable.isChecked = true}
    }

}*/


class SettingsFragment : PreferenceFragmentCompat() {

    var changeLanguage = false
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var prefrenceScreen: PreferenceScreen
    private lateinit var editor: SharedPreferences.Editor

    lateinit var settingsViewModel: SettingsViewModel
    lateinit var settingsViewModelFactory: SettingsViewModelFactory
    var settings: Settings? = null
    lateinit var navController: NavController
    var connectivity : ConnectivityManager? = null
    var info : NetworkInfo? = null


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        prefrenceScreen = preferenceScreen
        sharedPreferences = getDefaultSharedPreferences(requireContext())
        navController = Navigation.findNavController(requireActivity(),R.id.nav_host_fragment)
        editor = sharedPreferences.edit()

        settingsViewModelFactory = SettingsViewModelFactory(
            Repository.getInstance(
                RemoteSource.getInstance()
                , LocalSource.getInstance(requireActivity())
                ,requireContext()
                ,requireContext().getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE)))
        settingsViewModel = ViewModelProvider(this,settingsViewModelFactory).get(SettingsViewModel::class.java)

        settings = settingsViewModel.getStoredSettings()


        setLanguage()
        setMapLocation()
        setNotification()
        setUnitPreference()
        setGPS()
    }

    private fun setGPS() {
        val location = findPreference<SwitchPreference>("USE_DEVICE_LOCATION")
        location?.setOnPreferenceClickListener {
            //get location to pass it to home???
            settings?.location = 1
            settingsViewModel.setSettingsSharedPrefs(settings as Settings)
            connectivity = context?.getSystemService(Service.CONNECTIVITY_SERVICE) as ConnectivityManager
            if ( connectivity != null) {
                info = connectivity!!.activeNetworkInfo
                Log.i("TAG", "connectivity != null")
                if (info != null) {
                    if (info!!.state == NetworkInfo.State.CONNECTED) {

                    }
                    else{
                        val dialogBuilder = AlertDialog.Builder(requireContext())
                        dialogBuilder.setMessage(getString(R.string.networkWarning))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.ok)) { dialog, id ->
                                dialog.cancel()
                            }
                        val alert = dialogBuilder.create()
                        alert.show()
                    }
                }
            }
            it.isEnabled
        }

    }

    private fun setUnitPreference() {
        val unitPreference = findPreference<ListPreference>("unit")
        unitPreference?.setOnPreferenceChangeListener { prefrs, obj ->
            val items = obj as String
            when (items) {
                "METRIC" -> {
                    editor.putString("unit", "C")
                    editor.commit()
                }
                "IMPERIAL" -> {
                    editor.putString("unit", "F")
                    editor.commit()
                }
                "STANDARD" -> {
                    editor.putString("unit", "K")
                    editor.commit()
                }

            }
            restartApp()
            true
        }
    }

    private fun setNotification() {
        val notifictaion = findPreference<SwitchPreference>("notification_prefrence_key")
        notifictaion?.setOnPreferenceClickListener {
            if (it.key == "notification_prefrence_key") {
                //Navigation.findNavController(requireView()).navigate(R.id.alertsFragment)
                settings?.notification = true
                settingsViewModel.setSettingsSharedPrefs(settings as Settings)
            }
            true
        }

    }


    //======================================================
    private fun setMapLocation() {
        val locationUsingMap = findPreference<Preference>("CUSTOM_LOCATION")!!
        locationUsingMap.setOnPreferenceClickListener {
            settings?.location = 2
            settingsViewModel.setSettingsSharedPrefs(settings as Settings)
            connectivity =
                context?.getSystemService(Service.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivity != null) {
                info = connectivity!!.activeNetworkInfo
                Log.i("TAG", "connectivity != null")
                if (info != null) {
                    if (info!!.state == NetworkInfo.State.CONNECTED) {

                        val action =
                            SettingsFragmentDirections.actionSettingsFragment2ToMapsFragment().setIsSettings(true)
                        navController.navigate(action)
                    } else {
                        val dialogBuilder = AlertDialog.Builder(requireContext())
                        dialogBuilder.setMessage(getString(R.string.networkWarning))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.ok)) { dialog, id ->
                                dialog.cancel()
                            }
                        val alert = dialogBuilder.create()
                        alert.show()
                    }
                }
            }
            true
        }
    }


    //========================================================
    private fun setLanguage() {
        var language = findPreference<ListPreference>("language")
        language?.setOnPreferenceChangeListener { prefrs, obj ->
            changeLanguage = false
            val items = obj as String
            if (prefrs.key == "language") {
                when (items) {
                    "ar" -> LocalHelper.setLocale(requireContext(),"ar")
                    "en" -> LocalHelper.setLocale(requireContext(),"en")
                }
                restartApp()
            }
            editor.putBoolean("isUpdated", true)
            editor.commit()
            false
        }
    }


    //=========================================================


    override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean) {
    }

    private fun restartApp() {
        val intent = requireActivity().intent
        intent.addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    or Intent.FLAG_ACTIVITY_NO_ANIMATION
        )
        requireActivity().overridePendingTransition(0, 0)
        requireActivity().finish()
        requireActivity().overridePendingTransition(0, 0)
        startActivity(intent)
    }

}