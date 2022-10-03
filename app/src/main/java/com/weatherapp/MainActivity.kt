package com.weatherapp

import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

val API_CODE = "eb8c674b7d46afd8894dc07aaf7d859e"
val CITY = "hanoi,vn"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MyAsynctask().execute(CITY) //default city: Hanoi
        //search other city
        icon_search.setOnClickListener {
            if(edt_search.isVisible){
                val city = edt_search.text.toString()
                if (city.trim().length == 0){
                    Toast.makeText(this,"Nah",Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager // close keyboard after search
                imm.hideSoftInputFromWindow(it.windowToken,0)
                edt_search.visibility = View.INVISIBLE
                MyAsynctask().execute(city)
                edt_search.text.clear()
            }else{
                edt_search.visibility = View.VISIBLE
                edt_search.requestFocus()
            }
        }
    }



    inner class MyAsynctask(): AsyncTask<String, Void, String>() {
        var processDialog = ProgressDialog(this@MainActivity)
        override fun onPreExecute() {
            super.onPreExecute()
            processDialog.setTitle("Đang tải")
            processDialog.setMessage("Chờ chút")
            processDialog.show()
        }
        override fun doInBackground(vararg p0: String?): String {
            var response:String
            val city = p0[0]
            try {
                response = URL("https://api.openweathermap.org/data/2.5/weather?units=metric&q=$city&APPID=$API_CODE").readText(Charsets.UTF_8)
            }catch (ex:Exception){
                Log.e("error",ex.toString())
                response = ""
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                val jsonObject = JSONObject(result)
                val jsonMain = jsonObject.getJSONObject("main")
                val jsonWind = jsonObject.getJSONObject("wind")
                val jsonSys  = jsonObject.getJSONObject("sys")
                val jsonWeather = jsonObject.getJSONArray("weather").getJSONObject(0)
                // weather
                var temp = jsonMain.getString("temp")
                var temp_min = jsonMain.getString("temp_min")
                var temp_max = jsonMain.getString("temp_max")
                var humidity = jsonMain.getString("humidity")
                var pressure = jsonMain.getString("pressure")
                var temp_feels_like = jsonMain.getString("feels_like")
                var weather = jsonWeather.getString("main")
                //sunrise sunset
                var sunrise = jsonSys.getLong("sunrise")
                var sunset = jsonSys.getLong("sunset")
                //wind
                var wind = jsonWind.getString("speed")
                //location
                var location = jsonObject.getString("name")+" "+jsonSys.getString("country")
                var visibility = jsonObject.getString("visibility")
                //set data to view
                txt_city.text = location
                txt_current_date.text = SimpleDateFormat("dd-MM-yyyy HH:mm").format(Date()).toString()
                txt_temp.text = temp+"°C"
                txt_min_max_temp.text = temp_min+"°C / "+temp_max+"°C"
                txt_current_weather.text = weather
                txt_sunrise.text = SimpleDateFormat("hh:mm a",Locale.ENGLISH).format(Date(sunrise*1000))
                txt_sunset.text = SimpleDateFormat("hh:mm a",Locale.ENGLISH).format(Date(sunset*1000))
                txt_wind.text = wind
                txt_pressure.text = pressure
                txt_humidity.text = humidity
                txt_visibility.text = visibility
                processDialog.dismiss()

            }catch (ex:Exception){
                Log.e("error",ex.toString())
                Toast.makeText(this@MainActivity,"Opps! Something went wrong ",Toast.LENGTH_LONG).show()
                processDialog.dismiss()
            }
        }
    }
}