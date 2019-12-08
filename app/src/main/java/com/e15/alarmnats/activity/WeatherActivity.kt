package com.e15.alarmnats.activity

import android.app.AlertDialog
import android.graphics.Typeface
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

import com.e15.alarmnats.utils.Function
import com.e15.alarmnats.R

import org.json.JSONException
import org.json.JSONObject

import java.text.DateFormat
import java.util.Date
import java.util.Locale

class WeatherActivity : Fragment() {

    internal lateinit var selectCity: TextView
    internal lateinit var cityField: TextView
    internal lateinit var detailsField: TextView
    internal lateinit var currentTemperatureField: TextView
    internal lateinit var humidity_field: TextView
    internal lateinit var pressure_field: TextView
    internal lateinit var weatherIcon: TextView
    internal lateinit var updatedField: TextView
    internal lateinit var loader: ProgressBar
    internal lateinit var weatherFont: Typeface
    internal var city = "Hà nội, VN"
    /* Please Put your API KEY here */
    internal var OPEN_WEATHER_MAP_API = "8f0353b63a295dc141a142ef7cb3f4ed"
    /* Please Put your API KEY here */

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_weather, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        ((FragmentActivity)getActivity()).getSupportActionBar().hide();

        loader = getView()!!.findViewById<View>(R.id.loader) as ProgressBar
        selectCity = getView()!!.findViewById<View>(R.id.selectCity) as TextView
        cityField = getView()!!.findViewById<View>(R.id.city_field) as TextView
        updatedField = getView()!!.findViewById<View>(R.id.updated_field) as TextView
        detailsField = getView()!!.findViewById<View>(R.id.details_field) as TextView
        currentTemperatureField = getView()!!.findViewById<View>(R.id.current_temperature_field) as TextView
        humidity_field = getView()!!.findViewById<View>(R.id.humidity_field) as TextView
        pressure_field = getView()!!.findViewById<View>(R.id.pressure_field) as TextView
        weatherIcon = getView()!!.findViewById<View>(R.id.weather_icon) as TextView
        weatherFont = Typeface.createFromAsset(context!!.assets, "fonts/weathericons-regular-webfont.ttf")
        weatherIcon.typeface = weatherFont

        taskLoadUp(city)

        selectCity.setOnClickListener {
            val alertDialog = AlertDialog.Builder(context)
            alertDialog.setTitle("Change City")
            val input = EditText(context)
            input.setText(city)
            val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT)
            input.layoutParams = lp
            alertDialog.setView(input)

            alertDialog.setPositiveButton("Change"
            ) { dialog, which ->
                city = input.text.toString()
                taskLoadUp(city)
            }
            alertDialog.setNegativeButton("Cancel"
            ) { dialog, which -> dialog.cancel() }
            alertDialog.show()
        }

    }


    fun taskLoadUp(query: String) {
        if (Function.isNetworkAvailable(context!!)) {
            val task = DownloadWeather()
            task.execute(query)
        } else {
            Toast.makeText(context, "No Internet Connection", Toast.LENGTH_LONG).show()
        }
    }


    internal inner class DownloadWeather : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            loader.visibility = View.VISIBLE

        }

        override fun doInBackground(vararg args: String): String? {
            return Function.excuteGet("http://api.openweathermap.org/data/2.5/weather?q=" + args[0] +
                    "&units=metric&appid=" + OPEN_WEATHER_MAP_API)
        }

        override fun onPostExecute(xml: String?) {
            if (xml == null) return
            try {
                val json = JSONObject(xml)
                if (json != null) {
                    val details = json.getJSONArray("weather").getJSONObject(0)
                    val main = json.getJSONObject("option_display")
                    val df = DateFormat.getDateTimeInstance()

                    cityField.text = json.getString("name").toUpperCase(Locale.US) + ", " + json.getJSONObject("sys").getString("country")
                    detailsField.text = details.getString("description").toUpperCase(Locale.US)
                    currentTemperatureField.text = String.format("%.2f", main.getDouble("temp")) + "°"
                    humidity_field.text = "Humidity: " + main.getString("humidity") + "%"
                    pressure_field.text = "Pressure: " + main.getString("pressure") + " hPa"
                    updatedField.text = df.format(Date(json.getLong("dt") * 1000))
                    weatherIcon.text = Html.fromHtml(Function.setWeatherIcon(details.getInt("id"),
                            json.getJSONObject("sys").getLong("sunrise") * 1000,
                            json.getJSONObject("sys").getLong("sunset") * 1000))

                    loader.visibility = View.GONE

                }
            } catch (e: JSONException) {
                Toast.makeText(context, "Error, Check City", Toast.LENGTH_SHORT).show()
            }


        }
    }
}
