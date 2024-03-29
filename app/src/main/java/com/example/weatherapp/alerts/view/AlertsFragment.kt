package com.example.weatherapp.alerts.view

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.alerts.viewmodel.AlertsViewModel
import com.example.weatherapp.alerts.viewmodel.AlertsViewModelFactory
import com.example.weatherapp.database.LocalSource
import com.example.weatherapp.databinding.FragmentAlertsBinding
import com.example.weatherapp.model.AlertData
import com.example.weatherapp.model.Repository
import com.example.weatherapp.network.RemoteSource
import com.example.weatherapp.utilities.Constants.MY_SHARED_PREFERENCES
import com.example.weatherapp.utilities.Convertors
import com.example.weatherapp.utilities.UserHelper
import com.example.weatherapp.utilities.LocalHelper
import kotlinx.coroutines.*
import java.util.*

class AlertsFragment : Fragment(),OnClickAlertListener{


    private  val TAG = "AlertFragment"
    lateinit var binding: FragmentAlertsBinding
    lateinit var alertsAdapter: AlertsAdapter
    lateinit var sharedPreferences: SharedPreferences
    lateinit var sharedPreferencesEditor: SharedPreferences.Editor
    lateinit var builder: AlertDialog.Builder
    lateinit var dialogView: View
    lateinit var alertDialog: AlertDialog
    lateinit var viewModel: AlertsViewModel
    lateinit var viewModelFactory: AlertsViewModelFactory


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAlertsBinding.inflate(inflater, container, false)
       // viewModel = ViewModelProvider(this).get(AlertsViewModel::class.java)
        viewModelFactory = AlertsViewModelFactory( Repository(RemoteSource.getInstance(),
            LocalSource.getInstance(requireContext()),requireContext(),requireContext().getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE))
        )
        viewModel = ViewModelProvider(this,viewModelFactory).get(AlertsViewModel::class.java)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFrag()
        activateFABAlerts()
        setupAlertsAdapter()

    }


    private fun initFrag() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        sharedPreferencesEditor = sharedPreferences.edit()

        builder = AlertDialog.Builder(activity)
        dialogView = layoutInflater.inflate(R.layout.add_alert_dialog, null)

    }

    private fun activateFABAlerts() {
        binding.fabAlerts.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                val builder = AlertDialog.Builder(requireContext())

                builder.setMessage("This app needs permission to show notifications. Please enable it in the app's settings.")
                    .setTitle("Permission required")

                builder.setPositiveButton("OK") { _, _ ->
                    val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri: Uri = Uri.fromParts("package", requireActivity().packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }

                val dialog = builder.create()
                dialog.show()
            }
            if (!android.provider.Settings.canDrawOverlays(requireContext())) {

                val builder = AlertDialog.Builder(requireContext())

                builder.setMessage("This app needs permission to display an alarm over other apps. Please enable it in the app's settings.")
                    .setTitle("Permission required")

                builder.setPositiveButton("OK") { _, _ ->
                    val intent = Intent(android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    intent.data = Uri.parse("package:" + requireActivity().packageName)
                    startActivityForResult(intent, 0)
                }

                val dialog = builder.create()
                dialog.show()
            }
            if ((ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
                        ) && (android.provider.Settings.canDrawOverlays(requireContext()))
            ) {
                showAddAlertDialog();
            }
        }
    }

    private fun showAddAlertDialog() {

        (dialogView.parent as ViewGroup?)?.removeView(dialogView)

        builder.setView(dialogView)
        val textViewStartDate = dialogView.findViewById<TextView>(R.id.tv_start_date)
        // val textViewEndDate = dialogView.findViewById<TextView>(R.id.tv_end_date)
        val tvLocation = dialogView.findViewById<TextView>(R.id.tv_location)
        textViewStartDate.setOnClickListener { showDatePicker(textViewStartDate) }
        // textViewEndDate.setOnClickListener { showDatePicker(textViewEndDate) }
        tvLocation.setOnClickListener { tvLocationClicked() }
        builder.setPositiveButton("Save") { _, i -> saveClicked(textViewStartDate, textViewStartDate) }
        builder.setNegativeButton("Cancel") { dialogInterface, i -> dialogInterface.dismiss() }

        alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.rgb(29,41,86)))
        // alertDialog.window?.setBackgroundDrawable(Drawable.createFromPath("@drawable/rounded_corners"))

        alertDialog.setOnShowListener(DialogInterface.OnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.WHITE)
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE)
        })
        alertDialog.show()


    }


    private fun showDatePicker(textView: TextView) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireActivity(),
            { datePicker, year, month, day ->
                val date = day.toString() + " " + NativeDate.getMonth(month) + ", " + year
                showTimePicker(textView, date)
            }, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH]
        )
        datePickerDialog.show()
    }
    private fun showTimePicker(textView: TextView, date: String) {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            activity,
            { timePicker, hourOfDay, minute ->
                val time = "$hourOfDay:$minute"
                val dateTime = "$date $time"
                textView.text = dateTime
            }, calendar[Calendar.HOUR_OF_DAY], calendar[Calendar.MINUTE], false
        )
        timePickerDialog.show()
    }
    class NativeDate {
        companion object {
            fun getMonth(month: Int): String {
                val months = arrayOf(
                    "January",
                    "February",
                    "March",
                    "April",
                    "May",
                    "June",
                    "July",
                    "August",
                    "September",
                    "October",
                    "November",
                    "December"
                )
                return months[month]
            }
        }
    }

    private fun tvLocationClicked() {
        sharedPreferencesEditor.putBoolean("tvLocationClicked", true)
        sharedPreferencesEditor.apply()

        findNavController().navigate(AlertsFragmentDirections.actionAlertsFragmentToMapsFragment().setIsAlert(true))


    }

    private fun saveClicked(textViewStartDate: TextView, textViewEndDate: TextView) {
        if (dialogView.findViewById<TextView>(R.id.tv_location).text.isNotEmpty() && textViewStartDate.text.isNotEmpty() && textViewEndDate.text.isNotEmpty() && (dialogView.findViewById<RadioButton>(
                R.id.rbNotification
            ).isChecked || dialogView.findViewById<RadioButton>(R.id.rbAlarm).isChecked)
        ) {

            //val formatter = SimpleDateFormat("d MMMM, yyyy HH:mm")
            // formatter.timeZone = TimeZone.getTimeZone("GMT+2")
            val dateStart = Convertors.getDateFormat(textViewStartDate.text.toString())
            // val dateEnd = formatter.parse(textViewStartDate.text.toString())
            val unixTimeDTStart = dateStart?.time?.div(1000)
            //  val unixTimeDTEnd = dateEnd?.time?.div(1000)

            lifecycleScope.launch {
                if (unixTimeDTStart != null
                //&& unixTimeDTEnd != null
                ) {

                    var alertType = ""
                    if (dialogView.findViewById<RadioButton>(R.id.rbNotification).isChecked) {
                        alertType = "notification"
                    } else if (dialogView.findViewById<RadioButton>(R.id.rbAlarm).isChecked) {
                        alertType = "alarm"
                    }

                    val alertItem = AlertData(
                        address = LocalHelper.getAddressFromLatLng(
                            requireContext(),
                            UserHelper.alertLoc.latitude,
                            UserHelper.alertLoc.longitude
                        ),
                        longitudeString = UserHelper.alertLoc.longitude.toString(),
                        latitudeString = UserHelper.alertLoc.latitude.toString(),
                        startString = textViewStartDate.text.toString(),
                        endString = textViewEndDate.text.toString(),
                        startDT = unixTimeDTStart.toInt(),
                        endDT = 30,
                        idHashLongFromLonLatStartStringEndStringAlertType = (

                                UserHelper.location.longitude.toString() + UserHelper.location.latitude.toString()
                                        + textViewStartDate.text.toString() + textViewEndDate.text.toString() + alertType


                                ).hashCode()
                            .toLong(),
                        alertType = alertType
                    )
                    viewModel.addAlertInVM(alertItem)
                }
            }
        }
    }

    private fun setupAlertsAdapter() {
        val mlayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        alertsAdapter = AlertsAdapter(lifecycleScope,this)

        binding.rvAlerts.apply {
            layoutManager = mlayoutManager
            adapter = alertsAdapter
        }

        viewModel.getAllAlertsInVM().observe(viewLifecycleOwner) {
            alertsAdapter.submitList(it)
        }

    }

    override fun onPause() {

        super.onPause()
        if (sharedPreferences.getBoolean("tvLocationClicked", false)) {

            sharedPreferencesEditor.putBoolean("tvLocationClicked", false)

            sharedPreferencesEditor.putString(
                "start_date",
                dialogView.findViewById<TextView>(R.id.tv_start_date).text.toString()
            )
            sharedPreferencesEditor.putString(
                "end_date",
                //dialogView.findViewById<TextView>(R.id.tv_end_date).text.toString()
                "s"
            )
            sharedPreferencesEditor.putString(
                "ALERT_ADDRESS",
                dialogView.findViewById<TextView>(R.id.tv_location).text.toString()
            )

            var alartTypeSelected = ""

            if (dialogView.findViewById<RadioButton>(R.id.rbNotification).isChecked) {
                alartTypeSelected = "notification"
            } else if (dialogView.findViewById<RadioButton>(R.id.rbAlarm).isChecked) {
                alartTypeSelected = "alarm"
            }

            sharedPreferencesEditor.putString("alarm_type_selected", alartTypeSelected)

            sharedPreferencesEditor.apply()

            alertDialog.dismiss()
        }

    }


    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        Log.i(TAG, "onViewStateRestored: ")

        if (sharedPreferences.getString("start_date", "")!!
                .isNotEmpty() || sharedPreferences.getString("end_date", "")!!
                .isNotEmpty() || sharedPreferences.getString("alarm_type_selected", "")!!
                .isNotEmpty() || sharedPreferences.getString("ALERT_ADDRESS", "")!!.isNotEmpty()
        ) {
            showAddAlertDialog()
            if (sharedPreferences.getString("start_date", "")!!.isNotEmpty()) {
                //    dialogView.findViewById<TextView>(R.id.tv_start_date).text =
                sharedPreferences.getString("start_date", "")
            }
            if (sharedPreferences.getString("end_date", "")!!.isNotEmpty()) {
                //  dialogView.findViewById<TextView>(R.id.tv_end_date).text = sharedPreferences.getString("end_date", "")
            }
            if (sharedPreferences.getString("ALERT_ADDRESS", "")!!.isNotEmpty()) {
                dialogView.findViewById<TextView>(R.id.tv_location).text =
                    sharedPreferences.getString("ALERT_ADDRESS", "")
            }
            if (sharedPreferences.getString("alarm_type_selected", "") == "notification") {
                dialogView.findViewById<RadioButton>(R.id.rbNotification).isChecked = true
            } else if (sharedPreferences.getString("alarm_type_selected", "") == "alarm") {
                dialogView.findViewById<RadioButton>(R.id.rbAlarm).isChecked = true
            }

            sharedPreferencesEditor.putString("start_date", "")
            sharedPreferencesEditor.putString("end_date", "")
            sharedPreferencesEditor.putString("alarm_type_selected", "")
            sharedPreferencesEditor.putString("ALERT_ADDRESS", "")

            sharedPreferencesEditor.apply()


        }


    }

    override fun onRemoveAlertClickListener(alert: AlertData) {
        viewModel.removeAlertInVM(alert)
    }


}

//    private lateinit var binding: FragmentAlertsBinding
//    private lateinit var navController: NavController
//    private lateinit var addAlert: Dialog
//    private lateinit var datePickerDialog: Dialog
//    private lateinit var timePickerDialog: Dialog
//    private lateinit var alertViewModel: AlertsViewModel
//    private lateinit var alertViewModelFactory: AlertsViewModelFactory
//    private lateinit var alertAdapter: AlertsAdapter
//    private lateinit var alertLayoutManager: LinearLayoutManager
//    private var settings: Settings? = null
//    private  var newAlert: AlertData? = null
//    private val args: AlertsFragmentArgs by navArgs()
//    var alertFromDate: Date? = Date()
//    var alertToDate: Date? = Date()
//    var notifyType:Boolean = true
//    private lateinit var checkNotificationPermission: ActivityResultLauncher<String>
//    private var isPermission = false
//   lateinit private  var repo: Repository
//
//
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_alerts, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding = FragmentAlertsBinding.bind(view)
//        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
//
//        checkNotificationPermission = registerForActivityResult(
//            ActivityResultContracts.RequestPermission()
//        ) { isGranted: Boolean ->
//            isPermission = isGranted
//        }
//
//        initViewModel()
//        setupRecycler()
//        addBtnClicked()
//        checkPermission()
//
//        lifecycleScope.launch(Dispatchers.Main) {
//                Log.i(TAG, "get dataaa in homeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee ")
//                getData()
//            }
//
//        checkNotificationPermission = registerForActivityResult(
//            ActivityResultContracts.RequestPermission()
//        ) { isGranted: Boolean ->
//            isPermission = isGranted
//        }
//    }
//
//    fun initViewModel(){
//        alertViewModelFactory = AlertsViewModelFactory(
//            Repository.getInstance(
//                RemoteSource.getInstance(),
//                LocalSource.getInstance(requireActivity()),
//                requireContext(),
//                requireContext().getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE)
//            )
//        )
//        alertViewModel =
//            ViewModelProvider(this, alertViewModelFactory).get(AlertsViewModel::class.java)
//
//        settings = alertViewModel.getStoredSettings()
//    }
//    fun addBtnClicked(){
//        addAlert = Dialog(requireContext())
//        addAlert.setContentView(R.layout.add_alert_dialog)
//        addAlert.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//
//        var fromLinear: LinearLayout = addAlert.findViewById(R.id.linearAlertFrom)
//        var fromDatatxt: TextView = addAlert.findViewById(R.id.fromDateDialog)
//        var fromTimetxt: TextView = addAlert.findViewById(R.id.fromTimeDialog)
//
//        var toLinear: LinearLayout = addAlert.findViewById(R.id.linearAlertTo)
//        var toDatatxt: TextView = addAlert.findViewById(R.id.toDateDialog)
//        var toTimetxt: TextView = addAlert.findViewById(R.id.toTimeDialog)
//
//        var saveBtn: Button = addAlert.findViewById(R.id.addAlertBtn)
//        var notification: RadioButton = addAlert.findViewById(R.id.notificationForAlert)
//        var alarm: RadioButton = addAlert.findViewById(R.id.alarmForAlert)
//
//
//        notification.isChecked = true
//
//        notification.setOnClickListener { notifyType = true }
//
//        alarm.setOnClickListener { notifyType = false }
//
//        binding.locBtn.setOnClickListener {
//
//            val action = AlertsFragmentDirections.actionAlertsFragmentToMapsFragment().setIsAlert(true)
//            navController.navigate(action)
//        }
//        binding.floatingAddAlert.setOnClickListener {
//            addAlert.show()
//            fromLinear.setOnClickListener {
//
//                datePickerDialog = Dialog(requireContext())
//                datePickerDialog.setContentView(R.layout.date_picker_dialog)
//                datePickerDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//
//                var datePicker: DatePicker = datePickerDialog.findViewById(R.id.datePicker)
//                var dateOk: Button = datePickerDialog.findViewById(R.id.dateOk)
//                var dateCancel: Button = datePickerDialog.findViewById(R.id.dateCancel)
//
//                datePickerDialog.show()
//
//                dateOk.setOnClickListener {
//                    var fDay = datePicker.dayOfMonth
//                    var fMonth = datePicker.month
//                    var fYear = datePicker.year
//
//
//                    timePickerDialog = Dialog(requireContext())
//                    timePickerDialog.setContentView(R.layout.time_picker_dialog)
//                    timePickerDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//
//                    var timePicker: TimePicker = timePickerDialog.findViewById(R.id.timePicker)
//                    var timeOk: Button = timePickerDialog.findViewById(R.id.timeOk)
//                    var timeCancel: Button = timePickerDialog.findViewById(R.id.timeCancel)
//
//                    timePickerDialog.show()
//
//                    timeOk.setOnClickListener {
//                        var fHour = timePicker.hour
//                        var fMinute = timePicker.minute
//
//                        fromDatatxt.text = Convertors.getDateFromInt(fDay, fMonth, fYear)
//                        fromTimetxt.text = "$fHour:$fMinute"
//
//                        alertFromDate = Date(fYear, fMonth, fDay, fHour, fMinute)
//
//                        timePickerDialog.dismiss()
//                        datePickerDialog.dismiss()
//                    }
//
//
//                    timeCancel.setOnClickListener {
//                        timePickerDialog.dismiss()
//                        datePickerDialog.dismiss()
//                    }
//
//                }
//                dateCancel.setOnClickListener { datePickerDialog.dismiss() }
//
//            }
//            toLinear.setOnClickListener {
//
//                datePickerDialog = Dialog(requireContext())
//                datePickerDialog.setContentView(R.layout.date_picker_dialog)
//                datePickerDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//
//                var datePicker: DatePicker = datePickerDialog.findViewById(R.id.datePicker)
//                var dateOk: Button = datePickerDialog.findViewById(R.id.dateOk)
//                var dateCancel: Button = datePickerDialog.findViewById(R.id.dateCancel)
//
//                datePickerDialog.show()
//
//                dateOk.setOnClickListener {
//                    var tDay = datePicker.dayOfMonth
//                    var tMonth = datePicker.month
//                    var tYear = datePicker.year
//
//                    timePickerDialog = Dialog(requireContext())
//                    timePickerDialog.setContentView(R.layout.time_picker_dialog)
//                    timePickerDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//
//                    var timePicker: TimePicker = timePickerDialog.findViewById(R.id.timePicker)
//                    var timeOk: Button = timePickerDialog.findViewById(R.id.timeOk)
//                    var timeCancel: Button = timePickerDialog.findViewById(R.id.timeCancel)
//
//                    timePickerDialog.show()
//
//                    timeOk.setOnClickListener {
//                        var tHour = timePicker.hour
//                        var tMinute = timePicker.minute
//
//                        toDatatxt.text = Convertors.getDateFromInt(tDay, tMonth, tYear)
//                        toTimetxt.text = "$tHour:$tMinute"
//
//                        alertToDate = Date(tYear, tMonth, tDay, tHour, tMinute)
//
//                        timePickerDialog.dismiss()
//                        datePickerDialog.dismiss()
//                    }
//
//                    timeCancel.setOnClickListener {
//                        timePickerDialog.dismiss()
//                        datePickerDialog.dismiss()
//                    }
//
//                }
//                dateCancel.setOnClickListener { datePickerDialog.dismiss() }
//            }
//            saveBtn.setOnClickListener{
//                lifecycleScope.launch(Dispatchers.IO) {
//                    addNew()
//                    Log.i(TAG,
//                        "insert alerttt in sav btnn homeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee ")
//                    lifecycleScope.launch(Dispatchers.Main) {
//                        getData()
//                        Log.i(TAG, "get all  alerttt in sav btnn homeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee ")
//                    }
//
//                }
//            }
//        }
//
//    }
//    fun addNew(){
//     CurrentUser.alertLoc = LatLng(args.lat.toDouble(),args.lon.toDouble())
//        Log.i(TAG,"latttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt  {${args.lat }")
//        if(settings?.notification as Boolean) {
//            if (alertFromDate != null && alertToDate != null && isPermission) {
//                newAlert = AlertData(alertFromDate as Date, alertToDate as Date,CurrentUser.alertLoc.latitude,CurrentUser.alertLoc.longitude, notifyType)
//                val customCalendar = Calendar.getInstance()
//                customCalendar.set(
//                    alertFromDate!!.year,
//                    alertFromDate!!.month,
//                    alertFromDate!!.date,
//                    alertFromDate!!.hours,
//                    alertFromDate!!.minutes,
//                    0
//                )
//                val customTime = customCalendar.timeInMillis
//                val currentTime = System.currentTimeMillis()
//                if (customTime > currentTime) {
//                    val data = Data.Builder().putInt(NOTIFICATION_ID, 0).build()
//                    val delay = customTime - currentTime
//                    scheduleNotification(delay, data)
//                }
//                alertViewModel.addAlert(newAlert as AlertData)
//                Log.i(TAG, "insert in homeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee ")
//            }
//        }
//        else{
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                checkNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS) }
//
//
//            val dialogBuilder = AlertDialog.Builder(requireContext())
//            dialogBuilder.setMessage(getString(R.string.sorry))
//                .setCancelable(false)
//                .setPositiveButton(getString(R.string.ok)) { dialog, id ->
//                    dialog.cancel()
//                }
//            val alert = dialogBuilder.create()
//            alert.show()
//        }
//        addAlert.dismiss()
//    }
//    override fun onRemoveAlertClickListener(alert: AlertData) {
//        val dialogBuilder = AlertDialog.Builder(requireContext())
//        dialogBuilder.setMessage(getString(R.string.deleteMsg))
//            .setCancelable(false)
//            .setPositiveButton(getString(R.string.delete)) { dialog, id ->
//                lifecycleScope.launch(Dispatchers.IO) {
//                    alertViewModel.deleteAlert(alert)
//                    withContext(Dispatchers.Main) {
//                        dialog.cancel()
//                        getData()
//                    }
//                }
//            }
//            .setNegativeButton(getString(R.string.cancel)) { dialog, id -> dialog.cancel() }
//        val alert = dialogBuilder.create()
//        alert.show()
//    }
//    suspend fun getData() = lifecycleScope.launch() {
//        alertViewModel.getAllAlert().collectLatest{
//            when (it) {
//                is AlertState.onFail -> {
//                    Log.i(TAG, "get data alerttt on faillll homeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee ${it.toString()}")
//                } //hide loader show alert
//                is AlertState.onSuccessList -> { alertAdapter.setAlertsList(it.alertList)
//                    Log.i(TAG, "get  success  dataaa in homeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee ${it.alertList} ")}
//
//                else -> {  Log.i(TAG, "get data alert elsee homeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee ")}//Still loading
//            }
//            alertAdapter.notifyDataSetChanged()
//            Log.i(TAG, "adapter notify changeee homeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee ")
//
//        }
//        Log.i(TAG, "get dataaa in homeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee before job.join")
//    }.job.join()
//    fun setupRecycler(){
//        alertAdapter = AlertsAdapter(requireContext(), emptyList(),this)
//        alertLayoutManager = LinearLayoutManager(requireContext())
//        binding.alertRecycler.adapter = alertAdapter
//        binding.alertRecycler.layoutManager = alertLayoutManager
//    }
//
//
//    // ***************************************Worker**************************************************//
//
//    private fun scheduleNotification(delay: Long, data: Data) {
//        val notificationWork = OneTimeWorkRequest.Builder(WeatherWorker::class.java)
//            .setInitialDelay(delay, TimeUnit.MILLISECONDS).setInputData(data).build()
//
//        val instanceWorkManager = WorkManager.getInstance(requireContext())
//        instanceWorkManager.beginUniqueWork(NOTIFICATION_WORK,
//            ExistingWorkPolicy.REPLACE, notificationWork).enqueue()
//
//
//    }
//    private fun checkPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(
//                    requireContext(),
//                    Manifest.permission.POST_NOTIFICATIONS
//                ) == PackageManager.PERMISSION_GRANTED
//            ) {
//                isPermission = true
//            } else {
//                isPermission = false
//
//                checkNotificationPermission.launch (Manifest.permission.POST_NOTIFICATIONS)
//            }
//        } else {
//            isPermission = true
//        }
//    }
//}