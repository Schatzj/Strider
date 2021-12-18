package com.vainpower.strider.model

import androidx.lifecycle.*
import com.vainpower.strider.data.ConfigInfo
import com.vainpower.strider.data.ConfigInfoDao
import kotlinx.coroutines.launch
import android.app.Application
import com.vainpower.strider.data.SettingInfo
import com.vainpower.strider.data.SettingInfoDao

class SettingInfoModel(private val settingInfoDao: SettingInfoDao) : ViewModel() {

    private val TAG = "SettingInfoDao"
    private var settingInfo : SettingInfo? = null

    init {
        viewModelScope.launch {
            settingInfo = settingInfoDao.getItem(1)
        }
    }

    private fun insertSettings(info: SettingInfo) {
        viewModelScope.launch {
            settingInfoDao.insert(info)
        }
    }

    private fun getNewInfoEntry(paces: Int, paceDistance: Int, paceUnit: Int): ConfigInfo {
        return ConfigInfo(
            paces = paces,
            paceDistance = paceDistance,
            paceUnit = paceUnit
        )
    }

    fun readSettings(): SettingInfo? {
        return settingInfoDao.getConfig(1)
    }

    fun saveSettings(settings : SettingInfo) {
        settings.id = 1
        insertSettings(settings)
    }
}

class SettingInfoViewModelFactory(private val settingInfoDao: SettingInfoDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingInfoModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingInfoModel(settingInfoDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}