package com.vainpower.strider.model

import androidx.lifecycle.*
import com.vainpower.strider.data.ConfigInfo
import com.vainpower.strider.data.ConfigInfoDao
import kotlinx.coroutines.launch
import android.app.Application
import com.vainpower.strider.data.SettingInfoDao

class ConfigInfoModel(private val configInfoDao: ConfigInfoDao) : ViewModel() {

    private val TAG = "ConfigInfoModel"
    private var configInfo : ConfigInfo? = null

    /*constructor(configInfoDao: ConfigInfoDao, id : Int) : this(configInfoDao) {
        viewModelScope.launch {
            configInfo = configInfoDao.getItem(id)
            Log.d(TAG, "correct constructor called")
            Log.d(TAG, "${configInfo?.paces}: ")
        }
        Log.d(TAG, "and values are: ${configInfo?.paces}: ")
    }*/

    init {
        viewModelScope.launch {
            configInfo = configInfoDao.getItem(1)
        }
    }

    private fun insertConfigInfo(info: ConfigInfo) {
        viewModelScope.launch {
            configInfoDao.insert(info)
        }
    }

    private fun getNewInfoEntry(paces: Int, paceDistance: Int, paceUnit: Int): ConfigInfo {
        return ConfigInfo(
            paces = paces,
            paceDistance = paceDistance,
            paceUnit = paceUnit
        )
    }

    fun readConfigInfo(): ConfigInfo? {
        return configInfoDao.getConfig(1)
//        configInfo = configInfoDao.getItem(1)
//        viewModelScope.launch {
//            configInfo = configInfoDao.getItem(1)
//        }
//        return configInfo
    }
//    fun getAllCourses(): LiveData<List<ConfigInfo?>?>? {
//        configInfo = configInfoDao.getItem(1)
//        return configInfo
//    }
    /*fun readConfigInfo(): ConfigInfo? {
        var result : ConfigInfo? = null;
        viewModelScope.launch {
            var configInfo = configInfoDao.getItem(1)
            result = configInfo
        }

        return result
    }*/

    fun addNewItem(paces: Int, paceDistance: Int, paceUnit: Int) {
        var configInfo = getNewInfoEntry(paces, paceDistance, paceUnit)
        configInfo.id = 1
        insertConfigInfo(configInfo)
    }

    fun saveConfig(paces: Int, paceDistance: Int, paceUnit: Int) {
        var configInfo = getNewInfoEntry(paces, paceDistance, paceUnit)
        configInfo.id = 1
        insertConfigInfo(configInfo)
    }
}

class ConfigInfoViewModelFactory(private val configInfoDao: ConfigInfoDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConfigInfoModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ConfigInfoModel(configInfoDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}