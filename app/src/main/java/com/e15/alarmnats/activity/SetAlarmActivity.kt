package com.e15.alarmnats.activity

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast

import com.e15.alarmnats.AlarmReceiver
import com.e15.alarmnats.Model.Alarm
import com.e15.alarmnats.Model.AlarmItem
import com.e15.alarmnats.R

import butterknife.BindView

import butterknife.ButterKnife
import java.util.*

//import android.support.v7.graphics.Palette;
// import android.widget.TextClock;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;

class SetAlarmActivity : AppCompatActivity() {
    // Set song
    //    private Spinner spChoose;
    //
    //    private ImageView preview;
    //
    //    private ArrayAdapter<String> adapterSong;
    //
    //    private LinearLayout background;
    //
    //    private ImageView imgAlbum;
    //
    //    private boolean itemClicked = false;

    private var calendar: Calendar? = null

    private var ringtoneUri: Uri? = null
    private var ringtoneName: String? = null

    private var receiverIntent: Intent? = null
    private var pendingIntent: PendingIntent? = null

    private var alarm: Alarm? = null

    @JvmField
    @BindView(R.id.chooseTask)
    var chooseTask: LinearLayout? = null
    @JvmField
    @BindView(R.id.tvTask)
    var tvTask: TextView? = null
    @JvmField
    @BindView(R.id.imageTask)
    var imageTask: ImageView? = null

    @JvmField
    @BindView(R.id.timePicker)
    var timePicker: TimePicker? = null

    @JvmField
    @BindView(R.id.cardViewLabel)
    var cardViewLabel: CardView? = null

    @JvmField
    @BindView(R.id.tvLabelInfo)
    var tvLabelInfo: TextView? = null

    @JvmField
    @BindView(R.id.cardViewRingtone)
    var cardViewRingtone: CardView? = null

    @JvmField
    @BindView(R.id.tvRingtoneInfo)
    var tvRingtoneInfo: TextView? = null

    @JvmField
    @BindView(R.id.cardViewSave)
    var cardViewSave: CardView? = null

    @JvmField
    @BindView(R.id.cardViewCancel)
    var cardViewCancel: CardView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_alarm)
        ButterKnife.bind(this)
        title = "Set Alarm"

        alarm = intent.getSerializableExtra("alarmObject") as Alarm

        timePicker!!.setIs24HourView(true)

        //choose selection

        //        adapterSong=new ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,stringResults);
        //
        //        txtSong=(AutoCompleteTextView)findViewById(R.id.txtSong);
        //
        //        txtSong.setAdapter(adapterSong);
        //
        //        spChoose=(Spinner)findViewById(R.id.spChoose);
        //
        //        spChoose.setOnItemSelectedListener(this);
        //
        //        ArrayAdapter<CharSequence>adapter_selection=ArrayAdapter.createFromResource(this,R.array.SearchMusic,android.R.layout.simple_spinner_item);
        //
        //        spChoose.setAdapter(adapter_selection);
        //
        //        spChoose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        //            @Override
        //            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
        //                // your code here
        //
        //                if(position!=0){
        //                    ringtonePickerButton.setEnabled(false);
        //
        //                    txtSong.setEnabled(true);
        //
        //                }
        //
        //
        //
        //            }
        //
        //            @Override
        //            public void onNothingSelected(AdapterView<?> parentView) {
        //                // your code here
        //            }
        //
        //        });
        //
        //        imgAlbum=(ImageView)findViewById(R.id.imgAlbum);
        //
        //        background=findViewById(R.id.constraintLayout);
        //
        //        preview = (ImageView) findViewById(R.id.preview);
        //
        //        txtSong.addTextChangedListener(new TextWatcher() {
        //            @Override
        //            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        //
        //            @Override
        //            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        //
        //            @Override
        //            public void afterTextChanged(Editable editable) {
        //
        //                // if the change is due to the user clicking a suggested song, don't suggest more
        //                if(itemClicked) {
        //                    itemClicked = false;
        //                    return;
        //                }
        //
        //                // when users enters another character..
        //                updateSongs(trackField.getText().toString());
        //
        //            }
        //        });
        //
        //        // when a user clicks a selected track in from search suggestions, load that into alarmItem
        //        trackField.setOnItemClickListener((adapterView, view12, i, l) -> {
        //
        //            // stops suggesting
        //            itemClicked = true;
        //            AlarmItem searchItem = searchResultsItems.get(i);
        //
        //            // updates alarmItem with attributes from search item
        //            alarmItem.setName(searchItem.getName());
        //            alarmItem.setArtist(searchItem.getArtist());
        //            alarmItem.setImageUrl(searchItem.getImageUrl());
        //            alarmItem.setTrackUri(searchItem.getTrackUri());
        //
        //            try {
        //                alarmItem.jsonify(); // updates json in alarmItem
        //            } catch (JSONException e) {
        //                Log.e("AlarmListActivity", "error converting into json");
        //                e.printStackTrace();}
        //
        //            // updates UI
        //            trackField.setText(alarmItem.getArtist() + " - " + alarmItem.getName());
        //            updateAlbumArt(alarmItem.getImageUrl());
        //            preview.setVisibility(View.VISIBLE);
        //
        //            // hides keybaord
        //            hideKeyboard();
        //
        //        });
        //
        //        // when a users selects a track from clicking enter on keyboard
        //        trackField.setOnKeyListener((view1, keyCode, keyEvent) -> {
        //
        //            itemClicked = true;
        //
        //            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
        //                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
        //
        //                // Perform search on enter press
        //                searchTrack(trackField.getText().toString().replaceAll(" ", "+"));
        //                hideKeyboard();
        //
        //                return true;
        //            }
        //
        //            return false;
        //        });

        // calendar instance
        calendar = Calendar.getInstance()

        // receiverIntent
        receiverIntent = Intent(applicationContext, AlarmReceiver::class.java)
        receiverIntent!!.putExtra("alarmTime", alarm!!.alarmTime)

        // show old data
        tvTask!!.text = alarm!!.question //show task
        if (alarm!!.question == getString(R.string.default_question))
            imageTask!!.setImageResource(R.drawable.baseline_alarm_black_48)
        else if (alarm!!.question == getString(R.string.qr_question))
            imageTask!!.setImageResource(R.mipmap.qrcode_alarme)
        else if (alarm!!.question == getString(R.string.math_question))
            imageTask!!.setImageResource(R.drawable.ic_math)
        else if (alarm!!.question == getString(R.string.recaptcha_question))
            imageTask!!.setImageResource(R.drawable.ic_recaptcha)

        timePicker!!.hour = Integer.parseInt(alarm!!.alarmTime!!.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]) //show alarm time
        timePicker!!.minute = Integer.parseInt(alarm!!.alarmTime!!.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]) //

        tvLabelInfo!!.text = alarm!!.label // show label

        tvRingtoneInfo!!.text = alarm!!.ringtoneName // show ringtone

        ringtoneUri = Uri.parse(alarm!!.ringtoneUri)
        if (ringtoneUri!!.toString().split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0] == "spotify")
            setSpotifyMusic(AlarmItem(ringtoneUri!!.toString(), alarm!!.ringtoneName!!))
        else
            setRingtone(ringtoneUri)
    }

    // result from activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                RINGTONE_TYPE_REQUEST_CODE -> {
                    if (data!!.extras!!.getString("ringtoneType") == "spotify") {

                        val intent = Intent(this@SetAlarmActivity, SearchSongActivity::class.java)

                        startActivityForResult(intent, SPOTIFY_REQUEST_CODE)

                    } else if (data.extras!!.getString("ringtoneType") == "system") {

                        //Activity Action: Shows a ringtone picker.
                        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)

                        // EXTRA_RINGTONE_TYPE = "android.intent.extra.ringtone.TYPE"
                        //TYPE_ALARM = 4
                        //Type that refers to sounds that are used for the alarm.
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)

                        startActivityForResult(intent, RINGTONE_REQUEST_CODE)

                        println("start here")
                    }
                }
                RINGTONE_REQUEST_CODE -> {
                    println("to here")

                    //EXTRA_RINGTONE_PICKED_URI = "android.intent.extra."
                    //Returned from (the ringtone picker) as a (Uri).
                    //It will be one of:
                    //the picked ringtone,
                    //a Uri that equals Settings.System.DEFAULT_RINGTONE_URI, Settings.System.DEFAULT_NOTIFICATION_URI, or Settings.System.DEFAULT_ALARM_ALERT_URI if the default was chosen,
                    //(null) if the "Silent" item was picked.

                    //getParcelableExtra
                    //Retrieve (extended data) from the intent.
                    ringtoneUri = data!!.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)

                    setRingtone(ringtoneUri)

                }
                SPOTIFY_REQUEST_CODE -> {

                    val alarmItem = data!!.extras!!.getSerializable("AlarmItem") as AlarmItem

                    Log.d(TAG, String.format("AlarmItem == null? %b", alarmItem == null))

                    if (alarmItem != null) {

                        Log.d(TAG, alarmItem.name)

                        setSpotifyMusic(alarmItem)

                    }
                }
                AlarmListActivity.SCAN_QR_CODE_INTENT_REQUEST_CODE -> {

                    alarm!!.question = getString(R.string.qr_question)

                    alarm!!.answer = data!!.getStringExtra("code")

                    tvTask!!.text = alarm!!.question

                    imageTask!!.setImageResource(R.mipmap.qrcode_alarme)

                }
                AlarmListActivity.CHOOSE_TASK_REQUEST_CODE -> run {

                    taskSelected(data!!.extras!!.getString("task"))

                    tvTask!!.text = alarm!!.question

                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == AlarmListActivity.CAMERA_PERMISSION_REQUEST_CODE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(this, QRscanActivity::class.java)
                intent.putExtra("isSettingNewAlarm", true)
                startActivityForResult(intent, AlarmListActivity.SCAN_QR_CODE_INTENT_REQUEST_CODE)
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
            return
        }
    }

    fun taskSelected(selected: String?) {
        if (selected == getString(R.string.default_question)) {
            alarm!!.question = selected
            alarm!!.answer = "default"
            imageTask!!.setImageResource(R.drawable.baseline_alarm_black_48)
        } else if (selected == getString(R.string.qr_question)) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                println("permission!!")
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),
                        AlarmListActivity.CAMERA_PERMISSION_REQUEST_CODE)
            } else {
                val intent = Intent(this, QRscanActivity::class.java)
                intent.putExtra("isSettingNewAlarm", true)
                startActivityForResult(intent, AlarmListActivity.SCAN_QR_CODE_INTENT_REQUEST_CODE)
            }
        } else if (selected == getString(R.string.math_question)) { //continue here
            alarm!!.question = selected
            alarm!!.answer = "default"
            imageTask!!.setImageResource(R.drawable.ic_math)
        } else if (selected == getString(R.string.recaptcha_question)) {
            alarm!!.question = selected
            alarm!!.answer = "default"
            imageTask!!.setImageResource(R.drawable.ic_recaptcha)
        }
    }

    // set tvRingtoneInfo
    private fun setRingtone(uri: Uri?) {

        //RingtoneManager provides access to ringtones, notification, and other types of sounds.
        // It manages querying the different media providers and combines the results into a single cursor.
        // It also provides a Ringtone for each ringtone.
        // We generically call these sounds ringtones, however the TYPE_RINGTONE refers
        // to the type of sounds that are suitable for the phone ringer.
        val ringtone = RingtoneManager.getRingtone(this, uri)
        ringtoneName = ringtone.getTitle(this)
        ringtoneUri = uri

        tvRingtoneInfo!!.text = ringtoneName

        receiverIntent!!.putExtra("ringtoneUri", ringtoneUri!!.toString())

        alarm!!.ringtoneUri = ringtoneUri!!.toString()
        alarm!!.ringtoneName = ringtoneName
    }

    private fun setSpotifyMusic(alarmItem: AlarmItem) {
        ringtoneName = alarmItem.name

        tvRingtoneInfo!!.text = ringtoneName

        receiverIntent!!.putExtra("ringtoneUri", alarmItem.trackUri)

        alarm!!.ringtoneUri = alarmItem.trackUri
        alarm!!.ringtoneName = ringtoneName
    }

    fun chooseTaskClick(view: View) {
        val intent = Intent(this, ChooseTaskActivity::class.java)
        startActivityForResult(intent, AlarmListActivity.CHOOSE_TASK_REQUEST_CODE)
    }

    fun labelClick(view: View) {
        val dialogBuilder = AlertDialog.Builder(this).create()
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.label_dialog, null)

        val editText = dialogView.findViewById<View>(R.id.editTextLabel) as EditText
        val buttonSubmit = dialogView.findViewById<View>(R.id.buttonSubmitLabel) as Button
        val buttonCancel = dialogView.findViewById<View>(R.id.buttonCancelLabel) as Button

        buttonCancel.setOnClickListener { dialogBuilder.dismiss() }
        buttonSubmit.setOnClickListener {
            alarm!!.label = editText.text.toString()
            tvLabelInfo!!.text = editText.text.toString()
            dialogBuilder.dismiss()
        }

        dialogBuilder.setView(dialogView)
        dialogBuilder.show()
    }

    fun chooseRingtoneClick(view: View) {
        val intent = Intent(this, ChooseRingToneActivity::class.java)
        startActivityForResult(intent, RINGTONE_TYPE_REQUEST_CODE)
    }

    fun saveAlarmClick(view: View) {
        //                calendar.setTimeInMillis(System.currentTimeMillis());
        calendar!!.set(Calendar.HOUR_OF_DAY, timePicker!!.hour)
        calendar!!.set(Calendar.MINUTE, timePicker!!.minute)
        calendar!!.set(Calendar.SECOND, 0)
        calendar!!.set(Calendar.MILLISECOND, 0)

//        Toast.makeText(applicationContext,calendar.toString(),Toast.LENGTH_SHORT).show()

        if (calendar!!.timeInMillis < System.currentTimeMillis()) {
            calendar!!.add(Calendar.DATE, 1)

            Toast.makeText(this@SetAlarmActivity, "Delay for 1 day", Toast.LENGTH_SHORT).show()

        } else {

            Toast.makeText(this@SetAlarmActivity, "Alarm is set for " + timePicker!!.hour + ":" + timePicker!!.minute, Toast.LENGTH_SHORT).show()

        }

        alarm!!.alarmTime = timePicker!!.hour.toString() + ":" + timePicker!!.minute
        if (intent.extras!!.getBoolean("isNewAlarm")) {
            alarm!!.setFlag((System.currentTimeMillis() / 1000).toInt())
        }
        alarm!!.alarmTimeInMillis = calendar!!.timeInMillis

        // SET ALARM MANAGER
        receiverIntent!!.putExtra("question", alarm!!.question)
        receiverIntent!!.putExtra("answer", alarm!!.answer)
        receiverIntent!!.putExtra("alarmTime", alarm!!.alarmTime)
        receiverIntent!!.putExtra("label", alarm!!.label)

        this.pendingIntent = PendingIntent.getBroadcast(applicationContext, alarm!!.flag!!,
                receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT)


        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar!!.timeInMillis, pendingIntent)
        //                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        /// send result to mainActivity
        val intentReturnToMain = Intent()
        intentReturnToMain.putExtra("alarmObject", alarm)

        setResult(Activity.RESULT_OK, intentReturnToMain)

        finish()
    }

    fun cancelClick(view: View) {
        finish()
    }

    companion object {

        private val TAG = "SetAlarmActivity"

        private val RINGTONE_REQUEST_CODE = 1
        private val SPOTIFY_REQUEST_CODE = 2
        private val RINGTONE_TYPE_REQUEST_CODE = 3
    }


    //    @Override
    //    public void onErrorResponse(VolleyError error) {
    //
    //    }
    //
    //    //Called when a response is received.
    //    @Override
    //    public void onResponse(String response) {
    //
    //        try {
    //            JSONObject reader = new JSONObject(response);
    //            JSONObject tracks  = reader.getJSONObject("tracks");
    //            JSONArray items  = tracks.getJSONArray("items");
    //
    //            //searchResultsItems.clear();
    //            stringResults.clear();
    //
    //            for(int i = 0; i < items.length(); i++) {
    //                JSONObject result = items.getJSONObject(i);
    //                String uri = result.getString("uri");
    //                String name = result.getString("name");
    //                String artist = result.getJSONArray("artists").getJSONObject(0).getString("name");
    //                String imageUrl = result.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url");
    //
    //                // creating a minimum AlarmItem for storing uri, image, name and artist..
    //                // the rest of the attributes will be set separately
    ////                AlarmItem item = new AlarmItem(uri, imageUrl, name, artist,
    ////                        0,
    ////                        0,
    ////                        false,
    ////                        0);
    //
    //                stringResults.add(name);
    //                //searchResultsItems.add(item);
    //            }
    //
    //            try {
    //                adapterSong = new ArrayAdapter<String>(getBaseContext(),
    //                        android.R.layout.simple_dropdown_item_1line,
    //                        stringResults);
    //            } catch (NullPointerException e) {
    //                e.printStackTrace();
    //            }
    //
    //            txtSong.setAdapter(adapterSong);
    //
    //            adapterSong.notifyDataSetChanged();
    //
    //
    //        } catch (JSONException e) {
    //            e.printStackTrace();
    //        }
    //
    //
    //    }
    //
    //    public void updateAlbumArt(String imageUrl) {
    //
    //        // setting thumbnail ..
    //        Picasso.with(getBaseContext())
    //                .load(imageUrl)
    //                .fit()
    //                .centerCrop()
    //                .into(imgAlbum, new com.squareup.picasso.Callback() {
    //                    @Override
    //                    public void onSuccess() {
    //                        updateBackgroundColor();
    //                    }
    //                    @Override
    //                    public void onError() {
    //                        Log.e("AlarmListActivity", "Error setting image using Picasso");
    //                    }
    //                });
    //    }
    //
    //    public void updateBackgroundColor() {
    //        Palette.PaletteAsyncListener paletteListener = new Palette.PaletteAsyncListener() {
    //            public void onGenerated(Palette p) {
    //
    //                int d = 0x0E0E0E;
    //
    //                GradientDrawable gd = new GradientDrawable(
    //                        GradientDrawable.Orientation.TOP_BOTTOM,
    //                        new int[] {p.getDarkMutedColor(d), d});
    //
    //                Drawable[] grads = {background.getBackground(), gd};
    //
    //                TransitionDrawable transitionDrawable = new TransitionDrawable(grads);
    //                background.setBackground(transitionDrawable);
    //                transitionDrawable.startTransition(500);
    //
    //            }
    //        };
    //
    //        Bitmap bitmap = ((BitmapDrawable) imgAlbum.getDrawable()).getBitmap();
    //        Palette.from(bitmap).generate(paletteListener);
    //    }
    //
    //    public void setTrackFromTitle(String title) {
    //
    //        try {
    //            JSONObject reader = new JSONObject(title);
    //            JSONObject tracks  = reader.getJSONObject("tracks");
    //            JSONArray items  = tracks.getJSONArray("items");
    //            JSONObject result = items.getJSONObject(0);
    //            String uri = result.getString("uri");
    //            String name = result.getString("name");
    //            String artist = items.getJSONObject(0).getJSONArray("artists").getJSONObject(0).getString("name");
    //            String imageUrl = result.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url");
    //
    //            // updates UI
    //            txtSong.setText(artist + " - " + name);
    //
    //            // updates album art
    //            updateAlbumArt(imageUrl);
    //
    //            preview.setVisibility(View.VISIBLE);
    //
    //            // updates alarmItem
    ////            alarmItem.setArtist(artist);
    ////            alarmItem.setName(name);
    ////            alarmItem.setTrackUri(uri);
    ////            alarmItem.setImageUrl(imageUrl);
    //
    ////            try {
    ////                alarmItem.jsonify(); // updates json in alarmItem
    ////            } catch (JSONException e) {}
    //
    //
    //        } catch (JSONException e) {
    //            e.printStackTrace();
    //        }
    //    }
    //    public void cancelAlarm(int flag) {
    //        receiverIntent = new Intent(this, AlarmReceiver.class);
    //        pendingIntent = PendingIntent.getBroadcast(this, flag, receiverIntent, 0);
    //
    //        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    //        alarmManager.cancel(pendingIntent);
    //
    //        finish();
    //    }

}
