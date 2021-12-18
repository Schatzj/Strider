package com.vainpower.strider

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.vainpower.strider.databinding.StartFragementBinding
import android.widget.AdapterView

import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.vainpower.strider.model.*
import com.vainpower.strider.service.sensorService


class StartFragment : Fragment(){

    private val TAG = "startFragment"
    private var binding: StartFragementBinding? = null
    private val sharedViewModel : PaceViewModel by activityViewModels()
    private var service : Intent? = null

    private val configInfoRepo: ConfigInfoModel by activityViewModels {
        ConfigInfoViewModelFactory(
            (activity?.application as Strider).database
                .configInfoDao()
        )
    }

    private val settingInfoRepo: SettingInfoModel by activityViewModels {
        SettingInfoViewModelFactory(
            (activity?.application as Strider).database
                .settingInfoDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = StartFragementBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        setHasOptionsMenu(true)

        return fragmentBinding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
        }

        if(service != null){
            context?.stopService(service)
        }

        sharedViewModel.resetData()
        var configInfo = configInfoRepo.readConfigInfo()
        sharedViewModel.setPaces(configInfo?.paces ?: 0)
        sharedViewModel.setPaceDistance(configInfo?.paceDistance ?: 100)
        sharedViewModel.setPaceDistanceUnit(configInfo?.paceUnit ?: 0)

        settingInfoRepo.readSettings()?.let { sharedViewModel.setSettingsInfo(it) }
        if(sharedViewModel.getSettingInfo() == null || sharedViewModel.getSettingInfo().id != 1){
            sharedViewModel.setPaceChimeSetting(true)
            sharedViewModel.setPaceVibrateSetting(true)
            sharedViewModel.setTargetChimeSetting(true)
            sharedViewModel.setTargetVibrateSetting(true)
        }

        binding?.button?.setOnClickListener {
            requestPermission()

        }
        val appContext = context
        val configSpinner: Spinner? = binding?.PaceUnit
        // Create an ArrayAdapter using the string array and a default spinner layout
        if (appContext != null) {
            ArrayAdapter.createFromResource(
                appContext,
                R.array.pace_distances,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                if (configSpinner != null) {
                    configSpinner.adapter = adapter
                }
            }
        }

        if (configSpinner != null) {
            configSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View?, position: Int,
                    id: Long
                ) {
                    val unit: Int = if (position < 1) {
                        0
                    } else {
                        2
                    }
                    sharedViewModel.setPaceDistanceUnit(unit)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }


        val distanceSpinner: Spinner? = binding?.TargetDistanceUnit
        // Create an ArrayAdapter using the string array and a default spinner layout
        if (appContext != null) {
            ArrayAdapter.createFromResource(
                appContext,
                R.array.distances,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                if (distanceSpinner != null) {
                    distanceSpinner.adapter = adapter
                }
            }
        }

        if (distanceSpinner != null) {
            distanceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View?, position: Int,
                    id: Long
                ) {
                    var unit: Int = 0
                    when {
                        position < 1 -> {
                            unit = 0
                        }
                        position == 1 -> {
                            unit = 1
                        }
                        position == 2 -> {
                            unit = 2
                        }
                        position > 2 -> {
                            unit = 3
                        }
                    }
                    sharedViewModel.setTargetDistanceUnit(unit)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun requestPermission(){
        val CONTEXT = context;
        when {
            ContextCompat.checkSelfPermission(
                CONTEXT!!,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                service = Intent(context, sensorService::class.java)
//                service = PendingIntent(sensorService::class.java);
                //service?.setFlags(PendingIntent.FLAG_IMMUTABLE)
                service?.putExtra("paces", sharedViewModel.paces.value.toString())
                service?.putExtra("pacesDistance", sharedViewModel.paceDistance.value.toString())
                service?.putExtra("pacesUnit", sharedViewModel.paceDistanceUnit.value.toString())
                service?.putExtra("targetDistance", sharedViewModel.targetDistance.value.toString())
                service?.putExtra("targetDistanceUnit", sharedViewModel.targetDistanceUnit.value.toString())

                service?.putExtra("paceChime", sharedViewModel.getPaceChimeSetting())
                service?.putExtra("paceVibrate", sharedViewModel.getPaceVibrateSetting())
                service?.putExtra("targetChime", sharedViewModel.getTargetChimeSetting())
                service?.putExtra("targetVibrate", sharedViewModel.getTargetVibrateSetting())

                CONTEXT.startForegroundService(service)

                configInfoRepo.saveConfig(sharedViewModel.paces.value!!, sharedViewModel.paceDistance.value!!,
                    sharedViewModel.paceDistanceUnit.value!!)

                findNavController().navigate(R.id.action_startFragment_to_activeFragment)
                //Navigation.findNavController(view).navigate(R.id.action_startFragment_to_activeFragment)

            }
            // shouldShowRequestPermissionRationale(CONTEXT!!,
            //   Manifest.permission.ACTIVITY_RECOGNITION) -> {
            // In an educational UI, explain to the user why your app requires this
            // permission for a specific feature to behave as expected. In this UI,
            // include a "cancel" or "no thanks" button that allows the user to
            // continue using your app without granting the permission.
            //  showInContextUI(...)
            // }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    Manifest.permission.ACTIVITY_RECOGNITION)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            isGranted: Boolean ->
        if(isGranted){

        }else{

        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.layout_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                findNavController().navigate(R.id.action_startFragment_to_settingFragment)
                return true
            }
            else -> true//super.onOptionsItemSelected(item)
        }
    }
}

