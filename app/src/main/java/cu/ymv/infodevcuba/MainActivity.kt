package cu.ymv.infodevcuba

import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import cu.ymv.infodevcuba.home.HomeFragment
import cu.ymv.infodevcuba.login.LoginFragment
import cu.ymv.infodevcuba.utils.DateUtils
import cu.ymv.infodevcuba.utils.MyPreferences


class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        if (MyPreferences(this).darkMode) {
            setTheme(R.style.DarkTheme)
        } else {
            if (MyPreferences(this).darkModeAutomatic) {
                if (MyPreferences(this).darkModeTime && MyPreferences(this).darkModeBrillo) {
                    if (DateUtils().esHora()) {
                        val brillo: Int = Settings.System.getInt(
                            contentResolver,
                            Settings.System.SCREEN_BRIGHTNESS
                        )
                        if (brillo < 77) {
                            setTheme(R.style.DarkTheme)
                        } else {
                            setTheme(R.style.LightTheme)
                        }
                    } else {
                        val brillo: Int = Settings.System.getInt(
                            contentResolver,
                            Settings.System.SCREEN_BRIGHTNESS
                        )
                        if (brillo < 77) {
                            setTheme(R.style.DarkTheme)
                        } else {
                            setTheme(R.style.LightTheme)
                        }
                    }
                } else if (MyPreferences(this).darkModeTime) {
                    if (DateUtils().esHora()) {
                        setTheme(R.style.DarkTheme)
                    } else {
                        setTheme(R.style.LightTheme)
                    }
                } else if (MyPreferences(this).darkModeBrillo) {
                    val brillo: Int =
                        Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS)
                    if (brillo < 77) {
                        setTheme(R.style.DarkTheme)
                    } else {
                        setTheme(R.style.LightTheme)
                    }
                } else {
                    setTheme(R.style.LightTheme)
                }
            } else {
                setTheme(R.style.LightTheme)
            }
        }

        Log.d(TAG, "onCreate: " + DateUtils().esHora())

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (MyPreferences(this).userObject == "") {
            addFragmentToActivity(LoginFragment())
        } else {
            addFragmentToActivity(HomeFragment())
           //addFragmentToActivity(DownloadAppFragment())
        }
    }

    fun addFragmentToActivity(fragment: Fragment?) {
        if (fragment == null) return
        val fm = supportFragmentManager
        val tr = fm.beginTransaction()
        tr.replace(R.id.container, fragment)
        tr.commitAllowingStateLoss()
    }
}