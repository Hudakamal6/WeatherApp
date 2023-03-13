package com.example.weatherapp.favorite.view

import androidx.fragment.app.Fragment


class FavDetailFragment : Fragment() {

/*
    lateinit var viewModelFactory: MyFactory
    lateinit var detailsViewModel: ViewModelHome
    lateinit var hourlyAdapter: HourlyAdapter
    lateinit var dailyAdapter: DailyAdapter
    lateinit var layoutManagerHourly: LinearLayoutManager
    lateinit var layoutManagerDaily: LinearLayoutManager
    lateinit var binding:FragmentFavDetailBinding
   // private lateinit var navController: NavController
    private var settings: Setting? = null
    val weatherToShow:FavDetailFragment   by navArgs()
    /*val callback1 = requireActivity().onBackPressedDispatcher.addCallback(this) {
    }*/

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navController.navigateUp()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("TAG", "onCreate: ")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.i("TAG", "onCreateView: ")
        return inflater.inflate(R.layout.fragment_fav_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.i("TAG", "onViewCreated: ")
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFavDetailBinding.bind(view)
        navController = Navigation.findNavController(requireActivity(),R.id.nav_host_fragment)

        viewModelFactory = MyFactory(
            Repo.getInstance(
                NetworkingManager.getInstance(),
                DBManager.getInstance(requireActivity()),
                requireContext(),
                requireContext().getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE)))
        detailsViewModel = ViewModelProvider(this,viewModelFactory).get(ViewModelHome::class.java)



        settings = detailsViewModel.get()
        setupRecyclerViews()

        binding.currCity.text = weatherToShow.weather.timezone
        binding.currDate.text = Formmater.getDateFormat(weatherToShow.weather.current.dt)
        binding.currTime.text = Formmater.getTimeFormat(weatherToShow.weather.current.dt)
        binding.currTemp.text = weatherToShow.weather.current.temp.toString()
        binding.currDesc.text = weatherToShow.weather.current.weather[0].description

        binding.currHumidity.text = weatherToShow.weather.current.humidity.toString()
        binding.currWindSpeed.text = weatherToShow.weather.current.wind_speed.toString()
        binding.currClouds.text = weatherToShow.weather.current.clouds.toString()
        binding.currPressure.text = weatherToShow.weather.current.pressure.toString()
        binding.currUnit.text = getString(R.string.Kelvin)
        /*
        when(arrayOfUnits[settings?.unit as Int]) {
            "standard" ->{
                binding.currUnit.text = getString(R.string.Kelvin)
                binding.currWindUnit.text = getString(R.string.windMeter)
            }
            "metric" ->{
                binding.currUnit.text = getString(R.string.Celsius)
                binding.currWindUnit.text = getString(R.string.windMeter)
            }
            "imperial" ->{
                binding.currUnit.text = getString(R.string.Fahrenheit)
                binding.currWindUnit.text = getString(R.string.windMile)
            }
        }

         */

        Glide.with(context as Context)
            .load("https://openweathermap.org/img/wn/"+weatherToShow.weather.current.weather[0].icon+"@2x.png")
            .into(binding.currIcon)
        hourlyAdapter.setHourlyWeatherList(weatherToShow.weather.hourly)
        dailyAdapter.setDailyWeatherList(weatherToShow.weather.daily)

        hourlyAdapter.notifyDataSetChanged()
        dailyAdapter.notifyDataSetChanged()
        Log.i("TAG", "onViewCreated: finished")

    }

    fun setupRecyclerViews(){
        Log.i("TAG", "setupRecyclerViews: ")
        hourlyAdapter = HourlyAdapter(context as Context, arrayListOf())
        dailyAdapter = DailyAdapter(context as Context, arrayListOf())

        layoutManagerHourly = LinearLayoutManager(context as Context,
            LinearLayoutManager.HORIZONTAL,false)
        layoutManagerDaily = LinearLayoutManager(context as Context)
        binding.hourlyRecycler.adapter = hourlyAdapter
        binding.dailyRecycler.adapter = dailyAdapter
        binding.hourlyRecycler.layoutManager = layoutManagerHourly
        binding.dailyRecycler.layoutManager = layoutManagerDaily
    }


 */

}