
package com.vainpower.strider

import android.app.Application
import com.vainpower.strider.data.MyDatabase

class Strider : Application() {
    val database: MyDatabase by lazy { MyDatabase.getDatabase(this) }


}
//If this is needed place in manifest: android:name="com.vainpower.strider.Strider"
