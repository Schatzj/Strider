package com.vainpower.strider

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.vainpower.strider.databinding.ActiveFragmentBinding
import com.vainpower.strider.model.PaceViewModel
import kotlin.math.log

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
        Log.d(TAG, "onSensorChanged: in ActiveFragement.")
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }
}