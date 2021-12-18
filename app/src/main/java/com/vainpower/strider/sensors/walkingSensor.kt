package com.vainpower.strider.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.vainpower.strider.model.PaceViewModel

class walkingSensor : SensorEventListener {
    
    constructor(viewModel : PaceViewModel, sensorMan : SensorManager?){
        model = viewModel
        sensorManager = sensorMan
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR, true)
        if (stepSensor != null) {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_GAME, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    private var sensorManager: SensorManager? = null
    private val TAG = "WalkingSensor"
    private var model : PaceViewModel
    var playSound1 = false
    var playSound2 = false

    override fun onSensorChanged(event: SensorEvent?) {
        model.handleStep()
        if(model.playSound == 1){
            model.playSound = 0
            playSound1 = true
        }
        if(model.playSound == 2){
            model.playSound = 0
            playSound2 = true
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        //do nothing
    }

    public fun resetPlaySound1(){
        playSound1 = false
    }

    public fun resetPlaySound2(){
        playSound2 = false
    }
}