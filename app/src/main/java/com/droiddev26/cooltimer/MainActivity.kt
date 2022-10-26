package com.droiddev26.cooltimer

import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceChangeListener
import androidx.preference.PreferenceManager

class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    lateinit var timerSeekBar: SeekBar
    lateinit var timerText: TextView
    lateinit var mediaPlayer: MediaPlayer
    lateinit var audioManager: AudioManager
    var isTimerOn: Boolean = false
    lateinit var startStopButton: Button
    lateinit var countDownTimer: CountDownTimer
    var timeProgress: Int = 10
    var defaultInterval: Int = 0
    val maxTime: Int = 120
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        //Set sound
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        //mediaPlayer = MediaPlayer.create(this, R.raw.bell_sound)
        //Time
        timerText = findViewById(R.id.timeText)
        //SEEKBAR
        timerSeekBar = findViewById(R.id.seekBar)
        timerSeekBar.max = maxTime
        setIntervalFromSharedPreferences(sharedPreferences)
        //timerSeekBar.setProgress(timeProgress, true)
        //Start & Stop Button
        startStopButton = findViewById(R.id.button)
        //SeekBar Handler
        timerSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    updateTimer(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // you can probably leave this empty
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // you can probably leave this empty
            }
        })

        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    //button handler
    fun startStop(view: View) {

        if (!isTimerOn) {
            startStopButton.text = "Stop"
            isTimerOn = true
            timerSeekBar.isEnabled = false

            //TIMER
            countDownTimer =
                object : CountDownTimer((timerSeekBar.progress * 1000).toLong(), 1000) {
                    override fun onTick(p0: Long) {
                        val tickSeconds = p0 / 1000
                        updateTimer(tickSeconds)
                    }

                    override fun onFinish() {
                        val sharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(applicationContext)
                        if (sharedPreferences.getBoolean("enable_sound", true)) {
                            val melodyName = sharedPreferences.getString("timer_melody", "bell")
                            if (melodyName.equals("bell")) {
                                mediaPlayer =
                                    MediaPlayer.create(applicationContext, R.raw.bell_sound)
                            } else if (melodyName.equals("alarm_siren")) {
                                mediaPlayer =
                                    MediaPlayer.create(applicationContext, R.raw.alarm_siren_sound)
                            } else if (melodyName.equals("beep")) {
                                mediaPlayer =
                                    MediaPlayer.create(applicationContext, R.raw.bip_sound)
                            }

                            mediaPlayer.start()
                            Log.d("CDTimer", "Finita!!!")
                        }
                        resetTimer()
                    }
                }
            countDownTimer.start()
        } else {
            resetTimer()
        }
    }

    //fuction for update text of time
    fun updateTimer(seconds: Long) {
        val minutes = seconds / 60
        val seconds = seconds % 60
        var minutesStr = ""
        var secondsStr = ""

        if (minutes < 10) {
            minutesStr = "0" + minutes
        } else {
            minutesStr = minutes.toString()
        }

        if (seconds < 10) {
            secondsStr = "0" + seconds
        } else {
            secondsStr = seconds.toString()
        }
        //Set time to TextView
        timerText.text = minutesStr + ":" + secondsStr
    }

    fun resetTimer() {
        startStopButton.text = "Start"
        //timerText.text = "01:00"
        timerSeekBar.isEnabled = true
        //timerSeekBar.setProgress(timeProgress)
        isTimerOn = false
        countDownTimer.cancel()
        setIntervalFromSharedPreferences(sharedPreferences)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater: MenuInflater = getMenuInflater()
        menuInflater.inflate(R.menu.timer_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == R.id.settings) {
            val openSettings = Intent(this, SettingsActivity::class.java)
            startActivity(openSettings)
            return true
        } else if (id == R.id.about) {
            val openAbout = Intent(this, AboutActivity::class.java)
            startActivity(openAbout)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun setIntervalFromSharedPreferences(sharedPreferences: SharedPreferences) {
        defaultInterval =
            sharedPreferences.getString("default_interval", timeProgress.toString())!!
                .toInt()
        updateTimer(defaultInterval.toLong())
        timerSeekBar.setProgress(defaultInterval, true)
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        //val preference: Preference? = findPreference(p1!!)
        if (p1.equals("default_interval")) {
            setIntervalFromSharedPreferences(sharedPreferences)
        }
    }
}