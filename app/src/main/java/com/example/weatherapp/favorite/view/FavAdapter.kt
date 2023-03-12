    package com.example.weatherapp.favorite.view

    import android.content.Context
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.ImageView
    import android.widget.TextView
    import androidx.constraintlayout.widget.ConstraintLayout
    import androidx.recyclerview.widget.RecyclerView
    import com.bumptech.glide.Glide
    import com.example.weatherapp.R
    import com.example.weatherapp.model.WeatherForecast
    import com.example.weatherapp.utilities.Convertors
    import com.example.weatherapp.utilities.LocalHelper


    class FavAdapter(var context: Context, var favWeatherList:List<WeatherForecast>
                     , var onClickHandler:OnFavWeatherClickListener)
        :RecyclerView.Adapter<FavAdapter.FavoriteViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.favorite_item,parent,false)
            return FavoriteViewHolder(view)
        }

        override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {

            holder.time.text = Convertors.getTimeFormat(favWeatherList[position].current.dt)
            holder.addressName.text = LocalHelper.getAddressFromLatLng(context,favWeatherList[position].lat,favWeatherList[position].lon)
            Glide.with(context as Context)
                .load("https://openweathermap.org/img/wn/"+favWeatherList[position].current.weather[0].icon+"@2x.png")
                .into(holder.icon)

            holder.temp. text =  favWeatherList[position].current.temp.toString()
            holder.desc. text =  favWeatherList[position].current.weather[0].main

            holder.removeFav.setOnClickListener{ onClickHandler.onRemoveBtnClick(favWeatherList[position]) }
            holder.favConstraint.setOnClickListener { onClickHandler.onFavItemClick(favWeatherList[position])}
        }

        override fun getItemCount(): Int {
            return favWeatherList.size
        }

        @JvmName("setFavWeatherList1")
        fun setFavWeatherList(weatherList:List<WeatherForecast>){
            this.favWeatherList = weatherList
        }

        inner class FavoriteViewHolder( view: View): RecyclerView.ViewHolder(view) {
            val favConstraint: ConstraintLayout = view.findViewById(R.id.favConstraint)
            val addressName: TextView = view.findViewById(R.id.currCity)
            val removeFav: ImageView = view.findViewById(R.id.removeFav)
            val temp: TextView = view.findViewById(R.id.currTemp)
            val time: TextView = view.findViewById(R.id.testTime)
            val desc: TextView = view.findViewById(R.id.currDesc)
            val icon: ImageView = view.findViewById(R.id.currIcon)

        }
    }