package com.vainpower.strider.service

import android.R
import android.app.*
import android.content.Context
import android.content.Intent
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.vainpower.strider.MainActivity
import com.vainpower.strider.model.PaceViewModel
import com.vainpower.strider.sensors.walkingSensor

class sensorService : Service(){ //, SensorEventListener {
    val TAG = "Sensor_Service"

    companion object {
        const val CHANNEL_ID = "SensoreServiceChannel"
    }

    private var sensorManager: SensorManager? = null
    private val model = PaceViewModel()
    private var wakeLock : PowerManager.WakeLock? = null

    override fun onCreate() {
        super.onCreate()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
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
        var pacesUnit = Integer.parseInt(intent.getStringExtra("pacesUnit"))
        var targetDistance = intent.getStringExtra("targetDistance")?.toDouble() ?: 1.0
        var targetDistanceUnit = Integer.parseInt(intent.getStringExtra("targetDistanceUnit"))

        model.setPaces(paces)
        model.setPaceDistanceUnit(pacesUnit)
        model.setTargetDistance(targetDistance)
        model.setTargetDistanceUnit(targetDistanceUnit)
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
            while(true){
                sensorManager?.flush(mySensor)
                if(mySensor.playSound1){
                    playSound1()
                    mySensor.resetPlaySound1()
                }
                if(mySensor.playSound2){
                    Log.d(TAG, "onStartCommand: PLAYING SOUND 2")
                    playSound2()
                    mySensor.resetPlaySound2()
                }
                Thread.sleep(250)
            }
        }.start()

        return START_STICKY
    }

    override fun onDestroy() {
        wakeLock?.release()
        super.onDestroy()
    }

    @Nullable
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            "Foreground Service Channel",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(
            NotificationManager::class.java
        )
        manager.createNotificationChannel(serviceChannel)
    }

    private fun playSound1(){
        val tg : ToneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 100)
        tg.startTone(ToneGenerator.TONE_PROP_BEEP, 1500) //cdma answer softErrorLite
    }

    private fun playSound2(){
        val tg : ToneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 100)
        tg.startTone(ToneGenerator.TONE_CDMA_ANSWER, 1500)
        tg.startTone(ToneGenerator.TONE_CDMA_ANSWER, 1500)
        tg.startTone(ToneGenerator.TONE_CDMA_ANSWER, 1500)
    }
}