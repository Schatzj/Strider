package com.vainpower.strider

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.vainpower.strider.data.SettingInfo
import com.vainpower.strider.databinding.SettingsFragmentBinding
import com.vainpower.strider.model.*

class SettingFragment : Fragment(){

    private var binding: SettingsFragmentBinding? = null
    private val sharedViewModel : PaceViewModel by activityViewModels()
    private val TAG = "SettingFragment"

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
    ): View? {
        val fragmentBinding = SettingsFragmentBinding.inflate(inflater, container, false)
        Log.d(TAG, "onCreateView: setting fragment")
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
        }

        binding?.paceChimeSwitch?.setOnCheckedChangeListener { _, isChecked ->
            sharedViewModel.setPaceChimeSetting(isChecked)
        }

        binding?.paceVibrateSwitch?.setOnCheckedChangeListener { _, isChecked ->
            sharedViewModel.setPaceVibrateSetting(isChecked)
        }

        binding?.targetChimeSwitch?.setOnCheckedChangeListener { _, isChecked ->
            sharedViewModel.setTargetChimeSetting(isChecked)
        }

        binding?.targetVibrateSwitch?.setOnCheckedChangeListener { _, isChecked ->
            sharedViewModel.setTargetVibrateSetting(isChecked)
        }
    }

    override fun onStop() {
        super.onStop()
        settingInfoRepo.saveSettings(sharedViewModel.getSettingInfo())
    }
}