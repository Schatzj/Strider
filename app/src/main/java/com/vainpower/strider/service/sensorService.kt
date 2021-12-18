package com.vainpower.strider.service

import android.R
import android.R.raw
import android.app.*
import android.content.Context
import android.content.Intent
import android.hardware.SensorManager
import android.os.*
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.vainpower.strider.MainActivity
import com.vainpower.strider.model.PaceViewModel
import com.vainpower.strider.sensors.walkingSensor
import android.app.NotificationManager

import android.app.NotificationChannel

import android.os.Build
import android.media.MediaPlayer

import java.io.FileInputStream
import android.media.SoundPool
import android.media.AudioManager

import android.media.AudioAttributes

class sensorService : Service(){
    val TAG = "Sensor_Service"

    companion object {
        const val CHANNEL_ID = "SensoreServiceChannel"
    }

    private var sensorManager: SensorManager? = null
    private val model = PaceViewModel()
    private var wakeLock : PowerManager.WakeLock? = null
    private var tracking = true

    override fun onCreate() {
        super.onCreate()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service")
            .setAutoCancel(false)
            .setOngoing(true)
            .setContentText("Strider Service running!")
            .setSmallIcon(R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)

        sensorManager = this.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        var paces = Integer.parseInt(intent.getStringExtra("paces"))
        var paceDistance = Integer.parseInt(intent.getStringExtra("pacesDistance"))
        var pacesUnit = Integer.parseInt(intent.getStringExtra("pacesUnit"))
        var targetDistance = intent.getStringExtra("targetDistance")?.toDouble() ?: 1.0
        var targetDistanceUnit = Integer.parseInt(intent.getStringExtra("targetDistanceUnit"))

        var paceChime = intent.getBooleanExtra("paceChime", true)
        var paceVibrate = intent.getBooleanExtra("paceVibrate", true)
        var targetChime = intent.getBooleanExtra("targetChime", true)
        var targetVibrate = intent.getBooleanExtra("targetVibrate", true)

        model.setPaces(paces)
        model.setPaceDistance(paceDistance)
        model.setPaceDistanceUnit(pacesUnit)
        model.setTargetDistance(targetDistance)
        model.setTargetDistanceUnit(targetDistanceUnit)
        model.setPaceChimeSetting(paceChime)
        model.setPaceVibrateSetting(paceVibrate)
        model.setTargetChimeSetting(targetChime)
        model.setTargetVibrateSetting(targetVibrate)
        model.startActivity()
        var mySensor = walkingSensor(model, sensorManager)

        Thread{
            //insure the CPU does not sleep while the service is running
            wakeLock =
                (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                    newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Strider::MyWakelockTag").apply {
                        acquire()
                    }
                }
            while(tracking){
                sensorManager?.flush(mySensor)
                if(mySensor.playSound1){
                    playSound1()
                    mySensor.resetPlaySound1()
                }
                if(mySensor.playSound2){
                    playSound2()
                    mySensor.resetPlaySound2()
                }
                Thread.sleep(250)
            }
        }.start()

        return START_STICKY
    }

    override fun onDestroy() {
        tracking = false
        wakeLock?.release()
        super.onDestroy()
    }

    @Nullable
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    //@RequiresApi(Build.VERSION_CODES.O) //THIS IS AN ISSUE. NEED TO SUPPORT LOWER VERSIONS. THIS IS LIKELY WHY IT DOESN'T WORK FOR RICHARDS
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel =
            NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        } else {
            //TODO("VERSION.SDK_INT < O")
        }
    }

    private fun playSound1(){
        val pattern = longArrayOf(0, 1000, 1000, 1000, 1000, 1000)
        val mAmplitudes = intArrayOf(0, 255, 0, 255, 0, 255)
        if(model.getSettingInfo().paceChime && model.getSettingInfo().paceVibrate){
            playBeepSound(false)
            vibrate(pattern, mAmplitudes)
        }else if(model.getSettingInfo().paceChime == true){
            playBeepSound(false)
        }else if(model.getSettingInfo().paceVibrate == true){
            vibrate(pattern, mAmplitudes)
        }

    }

    private fun playSound2(){
        val pattern = longArrayOf(0, 500, 500, 500, 1000, 2000, 1000, 2000)
        val mAmplitudes = intArrayOf(0, 255, 0, 255, 0, 255, 0, 255)
        if(model.getSettingInfo().targetChime && model.getSettingInfo().targetVibrate){
            playBeepSound(true)
            vibrate(pattern, mAmplitudes)
        }else if(model.getSettingInfo().targetChime){
            playBeepSound(true)
        }else if (model.getTargetVibrateSetting()){
            vibrate(pattern, mAmplitudes)
        }

    }

    var soundPool: SoundPool? = null
    var soundOne : Int = 0
    var soundTwo : Int = 0
    private fun prepareSoundPool(){
        soundPool = if (Build.VERSION.SDK_INT
            >= Build.VERSION_CODES.LOLLIPOP
        ) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(
                    AudioAttributes.USAGE_ASSISTANCE_SONIFICATION
                )
                .setContentType(
                    AudioAttributes.CONTENT_TYPE_SONIFICATION
                )
                .build()
            SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(
                    audioAttributes
                )
                .build()
        } else {
            SoundPool(
                1,
                AudioManager.STREAM_MUSIC,
                0
            )
        }
        soundOne = soundPool!!.load(this, com.vainpower.strider.R.raw.short_beep, 1)
        soundTwo = soundPool!!.load(this, com.vainpower.strider.R.raw.long_beep, 1)

    }

    private fun playBeepSound(playLongBeep : Boolean){
        if(soundPool == null) {
            prepareSoundPool()
        }
        if(playLongBeep){
            soundPool?.play(soundTwo, 1f, 1f, 0, 0, 1f);
        }else{
            soundPool?.play(soundOne, 1f, 1f, 0, 0, 1f);
        }

    }

    private fun vibrate(pattern : LongArray, mAmplitudes : IntArray){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            val vibratorManager = this.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            val vibrator = vibratorManager.getDefaultVibrator();
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, mAmplitudes, -1))
        }else{
            val v = getSystemService(VIBRATOR_SERVICE) as Vibrator
            // Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createWaveform(pattern, mAmplitudes,-1))
            } else {
                //deprecated in API 26
                v.vibrate(pattern, -1)
            }
        }
    }
}