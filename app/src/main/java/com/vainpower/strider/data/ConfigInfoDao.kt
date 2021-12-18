package com.vainpower.strider.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.vainpower.strider.data.ConfigInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface ConfigInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ConfigInfo)

    @Update
    suspend fun update (item: ConfigInfo)

    @Delete
    suspend fun delete(item: ConfigInfo)

    @Query("SELECT * from ConfigInfo WHERE id = :id")
    suspend fun getItem(id: Int): ConfigInfo

    @Query("SELECT * from ConfigInfo WHERE id = :id")
    fun getConfig(id: Int): ConfigInfo
}