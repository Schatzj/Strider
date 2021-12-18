package com.vainpower.strider.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ConfigInfo(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    //pace Configuration Info.
    @ColumnInfo(name = "paces")
    var paces: Int,
    @ColumnInfo(name = "paceDistance")
    var paceDistance: Int,
    @ColumnInfo(name = "paceUnit")
    var paceUnit: Int
)