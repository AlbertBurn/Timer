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
import androidx.preference.PreferenceManager

class MainActivity : AppCompatActivity() {

    lateinit var timerSeekBar: SeekBar
    lateinit var timerText: TextView
    lateinit var mediaPlayer: MediaPlayer
    lateinit var audioManager: AudioManager
    var isTimerOn: Boolean = false
    lateinit var startStopButton: Button
    lateinit var countDownTimer: CountDownTimer
    var timeProgress: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        val maxTime: Int = 120
        timeProgress = 60

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Set sound
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        //mediaPlayer = MediaPlayer.create(this, R.raw.bell_sound)
        //Time
        timerText = findViewById(R.id.timeText)
        //SEEKBAR
        timerSeekBar = findViewById(R.id.seekBar)
        timerSeekBar.max = maxTime
        timerSeekBar.setProgress(timeProgress, true)
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
                        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                        if (sharedPreferences.getBoolean("enable_sound", true)){
                            val melodyName = sharedPreferences.getString("timer_melody", "bell")
                            if (melodyName.equals("bell")){
                                mediaPlayer = MediaPlayer.create(applicationContext, R.raw.bell_sound)
                            } else if (melodyName.equals("alarm_siren")){
                                mediaPlayer = MediaPlayer.create(applicationContext, R.raw.alarm_siren_sound)
                            }else if (melodyName.equals("beep")){
                                mediaPlayer = MediaPlayer.create(applicationContext, R.raw.bip_sound)
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
        timerText.text = "01:00"
        timerSeekBar.isEnabled = true
        timerSeekBar.setProgress(timeProgress)
        isTimerOn = false
        countDownTimer.cancel()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater: MenuInflater = getMenuInflater()
        menuInflater.inflate(R.menu.timer_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == R.id.settings) {
            var openSettings = Intent(this, SettingsActivity::class.java)
            startActivity(openSettings)
            return true
        } else if (id == R.id.about){
            var openAbout = Intent(this, AboutActivity::class.java)
            startActivity(openAbout)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}