package com.vainpower.strider.model

import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import android.text.Editable
import android.util.Log
import androidx.databinding.BaseObservable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.play.core.internal.ce
import java.math.BigDecimal
import java.text.DecimalFormat

const val METER = 0
const val KILOMETER = 1
const val FEET = 2
const val MILE = 3

class PaceViewModel : ViewModel() {

    private val _paces = MutableLiveData<Int>()
    val paces : LiveData<Int> = _paces
    private var _paceDistance = MutableLiveData<Int>()
    val paceDistance : LiveData<Int> = _paceDistance

    private var _targetDistance = MutableLiveData<Double>()
    val targetDistance : LiveData<Double> = _targetDistance

    private var _tDistance = MutableLiveData<String>()
    val tDistance : LiveData<String> = _tDistance

    private var _currentPaces = MutableLiveData<Int>()
    val currentPaces : LiveData<Int> = _currentPaces
    private var _currentDistance = MutableLiveData<Double>()
    val currentDistance : LiveData<Double> = _currentDistance

    private var _remainingPaces = MutableLiveData<Int>()
    val remainingPaces : LiveData<Int> = _remainingPaces
    private var _remainingDistance = MutableLiveData<Double>()
    val remainingDistance : LiveData<Double> = _remainingDistance

    private val _paceDistanceUnit = MutableLiveData<Int>()
    val paceDistanceUnit : LiveData<Int> = _paceDistanceUnit

    private val _targetDistanceUnit = MutableLiveData<Int>()
    val targetDistanceUnit : LiveData<Int> = _targetDistanceUnit

    private val _activeUnitString = MutableLiveData<String>()
    val activeUnitString : LiveData<String> = _activeUnitString

    private var _stepDistance : Double = 0.0
    private var countStep : Boolean = false
    private var totalStepsRequired : Int = 0
    var playSound = 0;

    private val TAG = "ViewModel"

    init {
        resetData()
    }

    fun setPaceDistanceUnit(unit : Int){
        _paceDistanceUnit.value = unit
    }

    fun setTargetDistanceUnit(unit : Int){
        _targetDistanceUnit.value = unit
    }

    fun startActivity(){
        countStep = false
        _stepDistance = ((_paceDistance.value?.div(_paces.value!!))?.toDouble() ?: 1.0)
        if(_paceDistanceUnit.value == METER){
            convertTargetDistanceToMeters();
            _activeUnitString.value = "Meters"
        }else{
            convertTargetDistanceToFeet()
            _activeUnitString.value = "Feet"
        }
        _remainingPaces.value = ((_targetDistance.value?.div(_stepDistance))?.toInt() ?: 1)
        _currentDistance.value = 0.0
        _remainingDistance.value = _targetDistance.value
        totalStepsRequired = _remainingPaces.value ?: 0
    }

    fun handleStep(){
        if(countStep){
            increaseCurrentPaces();
            _remainingPaces.value = _remainingPaces.value?.minus(1)
            _remainingDistance.value = remainingDistance.value?.minus(_stepDistance)
            _currentDistance.value = currentDistance.value?.plus(_stepDistance)
        }
        countStep = !countStep

        var stepsTaken = _currentPaces.value ?: 1
        var configuredSteps = _paces.value ?: 1
        if(stepsTaken % configuredSteps == 0){
            playSound = 1
        }

        if(stepsTaken >= totalStepsRequired){
            Log.d(TAG, "handleStep: model is set to play sound 2")
            playSound = 2
        }
    }

    fun handleSound(){
        var stepsTaken = _currentPaces.value ?: 1
        var configuredSteps = _paces.value ?: 1
        if(stepsTaken % configuredSteps == 0){
            val tg :ToneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 100)//STREAM_NOTIFICATION, 100)
            tg.startTone(ToneGenerator.TONE_PROP_BEEP, 1500); //cdma answer softErrorLite
            tg.startTone(ToneGenerator.TONE_PROP_BEEP, 1500);
        }

        if(stepsTaken >= totalStepsRequired){
            val tg :ToneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 100)//STREAM_NOTIFICATION, 100)
            tg.startTone(ToneGenerator.TONE_CDMA_ANSWER, 1500);
            tg.startTone(ToneGenerator.TONE_CDMA_ANSWER, 1500);
            tg.startTone(ToneGenerator.TONE_CDMA_ANSWER, 1500);
        }
    }

    private fun convertTargetDistanceToFeet() {
        if(_targetDistanceUnit.value == FEET){
            //do nothing
        }else if(_targetDistanceUnit.value == KILOMETER){
            _targetDistance.value = _targetDistance.value?.times(3280.84)
        }else if(_targetDistanceUnit.value == METER){
            _targetDistance.value = _targetDistance.value?.times(3.28084)
        }else if(_targetDistanceUnit.value == MILE){
            _targetDistance.value = _targetDistance.value?.times(5280)
        }
    }

    private fun convertTargetDistanceToMeters() {
        if(_targetDistanceUnit.value == METER){
            //do nothing
        }else if(_targetDistanceUnit.value == KILOMETER){
            _targetDistance.value = _targetDistance.value?.times(1000)
        }else if(_targetDistanceUnit.value == FEET){
            _targetDistance.value = _targetDistance.value?.times(0.3047)
        }else if(_targetDistanceUnit.value == MILE){
            _targetDistance.value = _targetDistance.value?.times(1609.34)
        }
    }

    fun increaseCurrentPaces(){
        var paces = (_currentPaces.value)?: 0
        _currentPaces.value = paces + 1
    }

    fun resetData(){
        _paces.value = 0
        _paceDistance.value = 100
        _targetDistance.value = 0.0
        _currentPaces.value = 0
        _currentDistance.value = 0.0
        _remainingPaces.value = 0
        _remainingDistance.value = 0.0

        _paceDistanceUnit.value = METER
        _targetDistanceUnit.value = METER
        _activeUnitString.value = "METERS"
    }

    public fun setPaces(value : Editable){
        if(value != null && value.isEmpty() == false){
            _paces.value = Integer.parseInt(value.toString())
        }else{
            _paces.value = 0
        }
    }

    public fun setPaceDistance(value : Editable){
        if(value != null && value.isEmpty() == false){
            _paceDistance.value = Integer.parseInt(value.toString())
        }else{
            _paceDistance.value = 0
        }
    }

    public fun setPaces(value : Int){
        _paces.value = value
    }

    public fun setPacesDistance(value : Editable){
        if(value != null && value.isEmpty() == false){
            _paceDistance.value = Integer.parseInt(value.toString())
        }else{
            _paceDistance.value = 100
        }

    }

    public fun setTargetDistance(value : Editable){
        if(value != null && value.isEmpty() == false) {
            _targetDistance.value = value.toString().toDouble()
        }else{
            _targetDistance.value = 1.0
        }


    }
    public fun setTargetDistance(value : Double){
        _targetDistance.value = value
    }


    public fun setTargetDistance(value : BigDecimal){
        _targetDistance.value = value.toDouble()
    }

    public fun setTDistance(value : String){
        _tDistance.value = value.toString()
        _targetDistance.value = value.toDouble()
    }

    public fun getDisplayDistance(): BigDecimal {
        if(targetDistance.value != null){
            return targetDistance.value!!.toBigDecimal()
        }else{
            return BigDecimal.ONE.stripTrailingZeros()
        }
    }
}