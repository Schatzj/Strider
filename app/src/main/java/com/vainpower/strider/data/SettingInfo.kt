package com.vainpower.strider.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class SettingInfo (
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "pace_chime")
    var paceChime : Boolean,
    @ColumnInfo(name = "pace_vibrate")
    var paceVibrate : Boolean,

    @ColumnInfo(name = "target_chime")
    var targetChime : Boolean,
    @ColumnInfo(name = "target_vibrate")
    var targetVibrate: Boolean
)
