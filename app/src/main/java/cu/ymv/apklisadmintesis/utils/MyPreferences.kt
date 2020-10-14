package cu.ymv.apklisadmintesis.utils

import android.content.Context
import androidx.preference.PreferenceManager

class MyPreferences(context: Context?) {

    companion object {
        private const val DARK_STATUS = "DARK_MODE_PERMANENT"
    }

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    var darkModePermanent = preferences.getInt(DARK_STATUS, 0)
        set(value) = preferences.edit().putInt(DARK_STATUS, value).apply()

}