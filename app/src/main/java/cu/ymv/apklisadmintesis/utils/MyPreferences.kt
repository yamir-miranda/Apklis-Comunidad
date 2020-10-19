package cu.ymv.apklisadmintesis.utils

import android.content.Context
import androidx.preference.PreferenceManager

class MyPreferences(context: Context?) {

    companion object {
        private const val DARK_MODE = "DARK_MODE"
        private const val DARK_MODE_AUTOMATIC = "DARK_MODE_AUTOMATIC"
        private const val DARK_MODE_TIME = "DARK_MODE_TIME"
        private const val DARK_MODE_BRILLO = "DARK_MODE_BRILLO"
        private const val USER_OBEJECT = "USER_OBEJECT"
    }

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    var darkMode = preferences.getBoolean(DARK_MODE, false)
        set(value) = preferences.edit().putBoolean(DARK_MODE, value).apply()

    var darkModeAutomatic = preferences.getBoolean(DARK_MODE_AUTOMATIC, true)
        set(value) = preferences.edit().putBoolean(DARK_MODE_AUTOMATIC, value).apply()

    var darkModeTime = preferences.getBoolean(DARK_MODE_TIME, true)
        set(value) = preferences.edit().putBoolean(DARK_MODE_TIME, value).apply()

    var darkModeBrillo = preferences.getBoolean(DARK_MODE_BRILLO, false)
        set(value) = preferences.edit().putBoolean(DARK_MODE_BRILLO, value).apply()

    var userObject = preferences.getString(USER_OBEJECT, "")
        set(value) = preferences.edit().putString(USER_OBEJECT, value).apply()

}