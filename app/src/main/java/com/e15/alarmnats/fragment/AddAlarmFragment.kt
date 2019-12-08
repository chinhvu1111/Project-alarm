package com.e15.alarmnats.fragment

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.TransitionDrawable
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.palette.graphics.Palette
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.e15.alarmnats.activity.SearchSongActivity
import com.e15.alarmnats.Model.AlarmItem
import com.e15.alarmnats.R
import com.squareup.picasso.Picasso

import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList
import java.util.HashMap


class AddAlarmFragment : Fragment(), Response.Listener<String>, Response.ErrorListener {
    private var editing: Boolean = false

    private var background: RelativeLayout? = null
    private var trackField: AutoCompleteTextView? = null
    private val timeText: TextView? = null
    private var preview: ImageView? = null
    private var albumImage: ImageView? = null

    private var itemClicked = false

    private var alarmItem = AlarmItem("", "", "", "", 6, 0, true, System.currentTimeMillis().toInt())
    private var oldAlarmItem: AlarmItem? = null

    private var searchAdapter: ArrayAdapter<String>? = null
    private val searchResultsItems = ArrayList<AlarmItem>()
    private val stringResults = ArrayList<String>()

    private var listener: AddFragmentListener? = null

    interface AddFragmentListener {
        fun saveClicked(item: AlarmItem)

        fun deleteClicked(item: AlarmItem?)

        fun updateAlarm(oldAlarm: AlarmItem?, newAlarm: AlarmItem)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context.javaClass == SearchSongActivity::class.java) {
            Log.d("SearchSongActivity", "Correct class")
            listener = context as AddFragmentListener
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_alarm, container, false)
        initUI(view)

        return view
    }

    private fun initUI(view: View) {
        albumImage = view.findViewById<View>(R.id.album_image) as ImageView
        val saveButton = view.findViewById<View>(R.id.addBtn) as Button

        saveButton.setOnClickListener(View.OnClickListener {
            // if no track has been set yet, do nothing
            if (alarmItem.trackUri == "" && !editing)
                return@OnClickListener

            // if editing, remove the old alarm
            if (editing) {
                listener!!.updateAlarm(oldAlarmItem, alarmItem)
            } else {
                listener!!.saveClicked(alarmItem)
            }

            this@AddAlarmFragment.exitFragment()
        })

        background = view.findViewById<View>(R.id.add_background) as RelativeLayout

        searchAdapter = ArrayAdapter(context,
                android.R.layout.simple_dropdown_item_1line,
                stringResults)

        trackField = view.findViewById<View>(R.id.track_field) as AutoCompleteTextView
        trackField!!.setAdapter<ArrayAdapter<String>>(searchAdapter)

        if (editing) {
            updateAlbumArt(oldAlarmItem!!.imageUrl)
            trackField!!.setText(oldAlarmItem!!.artist + " - " + oldAlarmItem!!.name)
            timeText!!.text = oldAlarmItem!!.formatedTime
            saveButton.text = "Save alarm"
        }

        // when users enters text, suggest new songs..
        trackField!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {

                // if the change is due to the user clicking a suggested song, don't suggest more

                if (itemClicked) {
                    itemClicked = false
                    return
                }
                Log.d(TAG, "afterTextChanged")
                // when users enters another character..
                updateSongs(trackField!!.text.toString())
            }
        })

        // when a user clicks a selected track in from search suggestions, load that into alarmItem
        trackField!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view12, i, l ->
            // stops suggesting
            itemClicked = true
            val searchItem = searchResultsItems[i]

            // updates alarmItem with attributes from search item
            alarmItem.name = searchItem.name
            alarmItem.artist = searchItem.artist
            alarmItem.imageUrl = searchItem.imageUrl
            alarmItem.trackUri = searchItem.trackUri

            try {
                alarmItem.jsonify() // updates json in alarmItem
            } catch (e: JSONException) {
                Log.e("AlarmListActivity", "error converting into json")
                e.printStackTrace()
            }

            // updates UI
            trackField!!.setText(alarmItem.artist + " - " + alarmItem.name)
            this@AddAlarmFragment.updateAlbumArt(alarmItem.imageUrl)
            preview!!.visibility = View.VISIBLE

            // hides keybaord
            this@AddAlarmFragment.hideKeyboard()
        }

        // when a users selects a track from clicking enter on keyboard
        trackField!!.setOnKeyListener(View.OnKeyListener { view1, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                itemClicked = true

                // Perform search on enter press
                this@AddAlarmFragment.searchTrack(trackField!!.text.toString().replace(" ".toRegex(), "+"))
                this@AddAlarmFragment.hideKeyboard()

                return@OnKeyListener true
            }

            false
        })

        preview = view.findViewById<View>(R.id.preview) as ImageView

    }

    fun cancelDeleteClicked() {
        exitFragment()
    }

    fun deleteButtonClicked() {
        listener!!.deleteClicked(oldAlarmItem)
        exitFragment()
    }

    // searches for track with http GET
    //    @RequiresApi(api = Build.VERSION_CODES.M)
    fun searchTrack(trackName: String) {


        // if trackname is too short, do nothing
        if (trackName.length < 3)
            return

        val url = "https://api.spotify.com/v1/search?q=$trackName&type=track&limit=1"

        val queue = Volley.newRequestQueue(context)

        // Request a string response from the provided URL.
        val stringRequest = object : StringRequest(Request.Method.GET, url,
                Response.Listener { response -> this@AddAlarmFragment.setTrackFromTitle(response) },
                this) {
            override fun getHeaders(): Map<String, String> {

                val prefs = context!!.getSharedPreferences(getString(R.string.tag_sharedprefs), Context.MODE_PRIVATE)
                val token = prefs.getString(getString(R.string.tag_sharedpref_token), "")

                val params = HashMap<String, String>()
                params["Authorization"] = "Bearer " + token!!
                return params
            }
        }

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    fun updateSongs(input: String): List<String> {

        val songs = ArrayList<String>()

        // if input is too short, return empty array
        if (input.length < 3)
            return songs

        val url = "https://api.spotify.com/v1/search?q=" + input.replace(" ".toRegex(), "+") + "&type=track&limit=3"

        val prefs = context!!.getSharedPreferences(getString(R.string.tag_sharedprefs), Context.MODE_PRIVATE)
        val token = prefs.getString(getString(R.string.tag_sharedpref_token), "")

        Log.d("AlarmListActivity", "url: $url")
        Log.d("AlarmListActivity", "token: " + token!!)

        val queue = Volley.newRequestQueue(context)

        // Request a string response from the provided URL.
        val stringRequest = object : StringRequest(Request.Method.GET, url, this, this) {
            override fun getHeaders(): Map<String, String> {

                val prefs = context!!.getSharedPreferences(getString(R.string.tag_sharedprefs), Context.MODE_PRIVATE)
                val token = prefs.getString(getString(R.string.tag_sharedpref_token), "")

                val params = HashMap<String, String>()
                params["Authorization"] = "Bearer " + token!!
                return params
            }
        }

        // Add the request to the RequestQueue.
        queue.add(stringRequest)

        return songs
    }

    override fun onResponse(response: String) {

        try {
            val reader = JSONObject(response)
            val tracks = reader.getJSONObject("tracks")
            val items = tracks.getJSONArray("items")

            searchResultsItems.clear()
            stringResults.clear()

            for (i in 0 until items.length()) {
                val result = items.getJSONObject(i)
                val uri = result.getString("uri")
                val name = result.getString("name")
                val artist = result.getJSONArray("artists").getJSONObject(0).getString("name")
                val imageUrl = result.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url")

                // creating a minimum AlarmItem for storing uri, image, name and artist..
                // the rest of the attributes will be set separately
                val item = AlarmItem(uri, imageUrl, name, artist,
                        0,
                        0,
                        false,
                        0)

                stringResults.add(String.format("%s - %s", artist, name))
                searchResultsItems.add(item)
            }

            try {
                searchAdapter = ArrayAdapter(context,
                        android.R.layout.simple_dropdown_item_1line,
                        stringResults)
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }

            trackField!!.setAdapter<ArrayAdapter<String>>(searchAdapter)

            searchAdapter!!.notifyDataSetChanged()


        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    fun hideKeyboard() {
        val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    // resets alarm and returns to homefragment
    fun exitFragment() {
        alarmItem = AlarmItem("", "", "", "", 6, 0, true, System.currentTimeMillis().toInt())
        fragmentManager!!.popBackStack()
    }

    fun setTrackFromTitle(title: String) {

        try {
            val reader = JSONObject(title)
            val tracks = reader.getJSONObject("tracks")
            val items = tracks.getJSONArray("items")
            val result = items.getJSONObject(0)
            val uri = result.getString("uri")
            val name = result.getString("name")
            val artist = items.getJSONObject(0).getJSONArray("artists").getJSONObject(0).getString("name")
            val imageUrl = result.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url")

            // updates UI
            trackField!!.setText("$artist - $name")

            // updates album art
            updateAlbumArt(imageUrl)

            preview!!.visibility = View.VISIBLE

            // updates alarmItem
            alarmItem.artist = artist
            alarmItem.name = name
            alarmItem.trackUri = uri
            alarmItem.imageUrl = imageUrl

            try {
                alarmItem.jsonify() // updates json in alarmItem
            } catch (e: JSONException) {
            }


        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    /**
     * Updates album art and updates background color gradient based on image
     *
     * @param imageUrl image url to load as album art
     */
    fun updateAlbumArt(imageUrl: String?) {

        // setting thumbnail ..
        Picasso.with(context)
                .load(imageUrl)
                .fit()
                .centerCrop()
                .into(albumImage!!, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {
                        updateBackgroundColor()
                    }

                    override fun onError() {
                        Log.e("AlarmListActivity", "Error setting image using Picasso")
                    }
                })
    }

    /**
     * Updates the background gradient based on album art
     */
    fun updateBackgroundColor() {
        val paletteListener = object : Palette.PaletteAsyncListener {
            override fun onGenerated(palette: Palette?) {

                val d = 0x0E0E0E

                val gd = GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        intArrayOf(palette!!.getDarkMutedColor(d), d))

                val grads = arrayOf(background!!.background, gd)

                val transitionDrawable = TransitionDrawable(grads)
                background!!.background = transitionDrawable
                transitionDrawable.startTransition(500)

            }
        }

        val bitmap = (albumImage!!.drawable as BitmapDrawable).bitmap
        Palette.from(bitmap).generate(paletteListener)
    }

    override fun onErrorResponse(error: VolleyError) {
        Log.e("AlarmListActivity", error.toString())
        //        Toast.makeText(, "Lỗi mạng!", Toast.LENGTH_SHORT).show();

    }

    fun setOldAlarmItem(oldAlarmItem: AlarmItem) {
        this.oldAlarmItem = oldAlarmItem
    }

    fun setAlarmItem(alarmItem: AlarmItem) {
        this.alarmItem = alarmItem
    }

    fun setEditing() {
        this.editing = true
    }

    class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

        private var timeText: TextView? = null
        private var alarmItem: AlarmItem? = null

        @RequiresApi(api = Build.VERSION_CODES.N)
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // Use the current time as the default values for the picker
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)

            // Create a new instance of TimePickerDialog and return it
            return TimePickerDialog(activity, R.style.Theme_AppCompat_Light_Dialog, this, hour, minute,
                    DateFormat.is24HourFormat(activity))
        }

        override fun onTimeSet(view: TimePicker, hour: Int, minute: Int) {

            // updates alarmItem
            alarmItem!!.hour = hour
            alarmItem!!.minute = minute

            timeText!!.text = alarmItem!!.formatedTime

            try {
                alarmItem!!.jsonify() // updates json in alarmItem
            } catch (e: JSONException) {
                Log.e("AlarmListActivity", "error converting into json")
                e.printStackTrace()
            }

        }

        fun setTimeText(timeText: TextView) {
            this.timeText = timeText
        }

        fun setAlarmItem(alarmItem: AlarmItem) {
            this.alarmItem = alarmItem
        }

    }

    companion object {

        private val TAG = "AddAlarmFragment"
    }
}
