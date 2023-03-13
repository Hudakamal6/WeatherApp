package com.example.weatherapp.alerts.view

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.weatherapp.R
import com.example.weatherapp.alerts.WeatherWorker
import com.example.weatherapp.alerts.viewmodel.AlertsViewModel
import com.example.weatherapp.alerts.viewmodel.AlertsViewModelFactory
import com.example.weatherapp.database.AlertState
import com.example.weatherapp.database.LocalSource
import com.example.weatherapp.databinding.FragmentAlertsBinding
import com.example.weatherapp.maps.MapsFragmentArgs
import com.example.weatherapp.maps.MapsFragmentDirections
import com.example.weatherapp.model.AlertData
import com.example.weatherapp.model.Repository
import com.example.weatherapp.model.Settings
import com.example.weatherapp.network.RemoteSource
import com.example.weatherapp.utilities.Constants.MY_SHARED_PREFERENCES
import com.example.weatherapp.utilities.Constants.NOTIFICATION_ID
import com.example.weatherapp.utilities.Constants.NOTIFICATION_WORK
import com.example.weatherapp.utilities.Convertors
import com.example.weatherapp.utilities.CurrentUser
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import java.util.*
import java.util.concurrent.TimeUnit

class AlertsFragment : Fragment(),OnClickAlertListener{
    private lateinit var binding: FragmentAlertsBinding
    private lateinit var navController: NavController
    private lateinit var addAlert: Dialog
    private lateinit var datePickerDialog: Dialog
    private lateinit var timePickerDialog: Dialog
    private lateinit var alertViewModel: AlertsViewModel
    private lateinit var alertViewModelFactory: AlertsViewModelFactory
    private lateinit var alertAdapter: AlertsAdapter
    private lateinit var alertLayoutManager: LinearLayoutManager
    private var settings: Settings? = null
    private  var newAlert: AlertData? = null
    private val args: AlertsFragmentArgs by navArgs()
    var alertFromDate: Date? = Date()
    var alertToDate: Date? = Date()
    var notifyType:Boolean = true

    private lateinit var checkNotificationPermission: ActivityResultLauncher<String>
    private var isPermission = false


   lateinit private  var repo: Repository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alerts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAlertsBinding.bind(view)
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)

        checkNotificationPermission = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            isPermission = isGranted
        }


        initViewModel()
        setupRecycler()
        addBtnClicked()
        checkPermission()

        lifecycleScope.launch(Dispatchers.Main) {
                Log.i(TAG, "get dataaa in homeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee ")
                getData()
            }

        checkNotificationPermission = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            isPermission = isGranted
        }
    }

    fun initViewModel(){
        alertViewModelFactory = AlertsViewModelFactory(
            Repository.getInstance(
                RemoteSource.getInstance(),
                LocalSource.getInstance(requireActivity()),
                requireContext(),
                requireContext().getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE)
            )
        )
        alertViewModel =
            ViewModelProvider(this, alertViewModelFactory).get(AlertsViewModel::class.java)

        settings = alertViewModel.getStoredSettings()
    }
    fun addBtnClicked(){
        addAlert = Dialog(requireContext())
        addAlert.setContentView(R.layout.add_alert_dialog)
        addAlert.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var fromLinear: LinearLayout = addAlert.findViewById(R.id.linearAlertFrom)
        var fromDatatxt: TextView = addAlert.findViewById(R.id.fromDateDialog)
        var fromTimetxt: TextView = addAlert.findViewById(R.id.fromTimeDialog)

        var toLinear: LinearLayout = addAlert.findViewById(R.id.linearAlertTo)
        var toDatatxt: TextView = addAlert.findViewById(R.id.toDateDialog)
        var toTimetxt: TextView = addAlert.findViewById(R.id.toTimeDialog)

        var saveBtn: Button = addAlert.findViewById(R.id.addAlertBtn)
        var notification: RadioButton = addAlert.findViewById(R.id.notificationForAlert)
        var alarm: RadioButton = addAlert.findViewById(R.id.alarmForAlert)


        notification.isChecked = true

        notification.setOnClickListener { notifyType = true }

        alarm.setOnClickListener { notifyType = false }

        binding.locBtn.setOnClickListener {

            val action = AlertsFragmentDirections.actionAlertsFragmentToMapsFragment().setIsAlert(true)
            navController.navigate(action)
        }
        binding.floatingAddAlert.setOnClickListener {
            addAlert.show()
            fromLinear.setOnClickListener {

                datePickerDialog = Dialog(requireContext())
                datePickerDialog.setContentView(R.layout.date_picker_dialog)
                datePickerDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                var datePicker: DatePicker = datePickerDialog.findViewById(R.id.datePicker)
                var dateOk: Button = datePickerDialog.findViewById(R.id.dateOk)
                var dateCancel: Button = datePickerDialog.findViewById(R.id.dateCancel)

                datePickerDialog.show()

                dateOk.setOnClickListener {
                    var fDay = datePicker.dayOfMonth
                    var fMonth = datePicker.month
                    var fYear = datePicker.year


                    timePickerDialog = Dialog(requireContext())
                    timePickerDialog.setContentView(R.layout.time_picker_dialog)
                    timePickerDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                    var timePicker: TimePicker = timePickerDialog.findViewById(R.id.timePicker)
                    var timeOk: Button = timePickerDialog.findViewById(R.id.timeOk)
                    var timeCancel: Button = timePickerDialog.findViewById(R.id.timeCancel)

                    timePickerDialog.show()

                    timeOk.setOnClickListener {
                        var fHour = timePicker.hour
                        var fMinute = timePicker.minute

                        fromDatatxt.text = Convertors.getDateFromInt(fDay, fMonth, fYear)
                        fromTimetxt.text = "$fHour:$fMinute"

                        alertFromDate = Date(fYear, fMonth, fDay, fHour, fMinute)

                        timePickerDialog.dismiss()
                        datePickerDialog.dismiss()
                    }


                    timeCancel.setOnClickListener {
                        timePickerDialog.dismiss()
                        datePickerDialog.dismiss()
                    }

                }
                dateCancel.setOnClickListener { datePickerDialog.dismiss() }

            }
            toLinear.setOnClickListener {

                datePickerDialog = Dialog(requireContext())
                datePickerDialog.setContentView(R.layout.date_picker_dialog)
                datePickerDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                var datePicker: DatePicker = datePickerDialog.findViewById(R.id.datePicker)
                var dateOk: Button = datePickerDialog.findViewById(R.id.dateOk)
                var dateCancel: Button = datePickerDialog.findViewById(R.id.dateCancel)

                datePickerDialog.show()

                dateOk.setOnClickListener {
                    var tDay = datePicker.dayOfMonth
                    var tMonth = datePicker.month
                    var tYear = datePicker.year

                    timePickerDialog = Dialog(requireContext())
                    timePickerDialog.setContentView(R.layout.time_picker_dialog)
                    timePickerDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                    var timePicker: TimePicker = timePickerDialog.findViewById(R.id.timePicker)
                    var timeOk: Button = timePickerDialog.findViewById(R.id.timeOk)
                    var timeCancel: Button = timePickerDialog.findViewById(R.id.timeCancel)

                    timePickerDialog.show()

                    timeOk.setOnClickListener {
                        var tHour = timePicker.hour
                        var tMinute = timePicker.minute

                        toDatatxt.text = Convertors.getDateFromInt(tDay, tMonth, tYear)
                        toTimetxt.text = "$tHour:$tMinute"

                        alertToDate = Date(tYear, tMonth, tDay, tHour, tMinute)

                        timePickerDialog.dismiss()
                        datePickerDialog.dismiss()
                    }

                    timeCancel.setOnClickListener {
                        timePickerDialog.dismiss()
                        datePickerDialog.dismiss()
                    }

                }
                dateCancel.setOnClickListener { datePickerDialog.dismiss() }
            }
            saveBtn.setOnClickListener{
                lifecycleScope.launch(Dispatchers.IO) {
                    addNew()
                    Log.i(TAG,
                        "insert alerttt in sav btnn homeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee ")
                    lifecycleScope.launch(Dispatchers.Main) {
                        getData()
                        Log.i(TAG, "get all  alerttt in sav btnn homeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee ")
                    }

                }
            }
        }

    }
    fun addNew(){
     CurrentUser.alertLoc = LatLng(args.lat.toDouble(),args.lon.toDouble())
        Log.i(TAG,"latttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt  {${args.lat }")
        if(settings?.notification as Boolean) {
            if (alertFromDate != null && alertToDate != null && isPermission) {
                newAlert = AlertData(alertFromDate as Date, alertToDate as Date,CurrentUser.alertLoc.latitude,CurrentUser.alertLoc.longitude, notifyType,)
                val customCalendar = Calendar.getInstance()
                customCalendar.set(
                    alertFromDate!!.year,
                    alertFromDate!!.month,
                    alertFromDate!!.date,
                    alertFromDate!!.hours,
                    alertFromDate!!.minutes,
                    0
                )
                val customTime = customCalendar.timeInMillis
                val currentTime = System.currentTimeMillis()
                if (customTime > currentTime) {
                    val data = Data.Builder().putInt(NOTIFICATION_ID, 0).build()
                    val delay = customTime - currentTime
                    scheduleNotification(delay, data)
                }
                alertViewModel.addAlert(newAlert as AlertData)
                Log.i(TAG, "insert in homeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee ")
            }
        }
        else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                checkNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS) }


            val dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder.setMessage(getString(R.string.sorry))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok)) { dialog, id ->
                    dialog.cancel()
                }
            val alert = dialogBuilder.create()
            alert.show()
        }
        addAlert.dismiss()
    }
    override fun onRemoveAlertClickListener(alert: AlertData) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage(getString(R.string.deleteMsg))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.delete)) { dialog, id ->
                lifecycleScope.launch(Dispatchers.IO) {
                    alertViewModel.deleteAlert(alert)
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
    suspend fun getData() = lifecycleScope.launch() {
        alertViewModel.getAllAlert().collectLatest{
            when (it) {
                is AlertState.onFail -> {
                    Log.i(TAG, "get data alerttt on faillll homeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee ${it.toString()}")
                } //hide loader show alert
                is AlertState.onSuccessList -> { alertAdapter.setAlertsList(it.alertList)
                    Log.i(TAG, "get  success  dataaa in homeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee ${it.alertList} ")}

                else -> {  Log.i(TAG, "get data alert elsee homeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee ")}//Still loading
            }
            alertAdapter.notifyDataSetChanged()
            Log.i(TAG, "adapter notify changeee homeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee ")

        }
        Log.i(TAG, "get dataaa in homeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee before job.join")
    }.job.join()
    fun setupRecycler(){
        alertAdapter = AlertsAdapter(requireContext(), emptyList(),this)
        alertLayoutManager = LinearLayoutManager(requireContext())
        binding.alertRecycler.adapter = alertAdapter
        binding.alertRecycler.layoutManager = alertLayoutManager
    }


    // ***************************************Worker**************************************************//

    private fun scheduleNotification(delay: Long, data: Data) {
        val notificationWork = OneTimeWorkRequest.Builder(WeatherWorker::class.java)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS).setInputData(data).build()

        val instanceWorkManager = WorkManager.getInstance(requireContext())
        instanceWorkManager.beginUniqueWork(NOTIFICATION_WORK,
            ExistingWorkPolicy.REPLACE, notificationWork).enqueue()


    }
    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                isPermission = true
            } else {
                isPermission = false

                checkNotificationPermission.launch (Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            isPermission = true
        }
    }
}