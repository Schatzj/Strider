package com.vainpower.strider

import android.app.Service
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.*
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.vainpower.strider.databinding.ActiveFragmentBinding
import com.vainpower.strider.model.PaceViewModel

class ActiveFragment : Fragment(), SensorEventListener {
    companion object{
        var model : PaceViewModel = PaceViewModel()
    }

    private var binding: ActiveFragmentBinding? = null
    private val sharedViewModel : PaceViewModel by activityViewModels()
    private var sensorManager: SensorManager? = null
    private val TAG = "ActiveFragement"
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = ActiveFragmentBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        //setHasOptionsMenu(true)
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.startActivity();
        model = sharedViewModel
        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
        }
        
    }

    override fun onResume() {
        super.onResume()
        
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

        if (stepSensor == null) {
            Toast.makeText(context, "No sensor detected on this device", Toast.LENGTH_SHORT).show()
        } else {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        sharedViewModel.handleStep()
        if(model.playSound == 1){
            model.playSound = 0
            playSound1()
        }
        if(model.playSound == 2){
            model.playSound = 0
            playSound2()
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.layout_menu, menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.settings -> {
//                findNavController().navigate(R.id.action_activeFragment_to_settingFragment)
//                return true
//            }
//            else -> true//super.onOptionsItemSelected(item)
//        }
//    }

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
        soundOne = soundPool!!.load(context, com.vainpower.strider.R.raw.short_beep, 1)
        soundTwo = soundPool!!.load(context, com.vainpower.strider.R.raw.long_beep, 1)

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
            val vibratorManager = activity?.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            val vibrator = vibratorManager.getDefaultVibrator();
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, mAmplitudes, -1))
        }else{
            val v = activity?.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
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