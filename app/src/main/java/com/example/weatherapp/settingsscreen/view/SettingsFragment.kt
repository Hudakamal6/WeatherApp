package com.example.weatherapp.settingsscreen.view



import android.app.AlertDialog
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.preference.*
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.example.weatherapp.R
import com.example.weatherapp.database.LocalSource
import com.example.weatherapp.model.Repository
import com.example.weatherapp.model.Settings
import com.example.weatherapp.network.RemoteSource
import com.example.weatherapp.settingsscreen.viewmodel.SettingsViewModel
import com.example.weatherapp.settingsscreen.viewmodel.SettingsViewModelFactory
import com.example.weatherapp.utilities.Constants.MY_SHARED_PREFERENCES
import com.example.weatherapp.utilities.UserHelper
import com.example.weatherapp.utilities.LocalHelper


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
            if ( UserHelper.networkState) {
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
            if(UserHelper.networkState) {
                val action =
                         SettingsFragmentDirections.actionSettingsFragment2ToMapsFragment().setIsSettings(true)
                      navController.navigate(action)

            }else {
                        val dialogBuilder = AlertDialog.Builder(requireContext())
                        dialogBuilder.setMessage(getString(R.string.networkWarning))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.ok)) { dialog, id ->
                                dialog.cancel()
                            }
                        val alert = dialogBuilder.create()
                        alert.show()
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