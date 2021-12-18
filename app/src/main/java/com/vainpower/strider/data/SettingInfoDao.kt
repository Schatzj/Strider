package com.vainpower.strider.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.vainpower.strider.data.ConfigInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SettingInfo)

    @Update
    suspend fun update (item: SettingInfo)

    @Delete
    suspend fun delete(item: SettingInfo)

    @Query("SELECT * from SettingInfo WHERE id = :id")
    suspend fun getItem(id: Int): SettingInfo

    @Query("SELECT * from SettingInfo WHERE id = :id")
    fun getConfig(id: Int): SettingInfo
}