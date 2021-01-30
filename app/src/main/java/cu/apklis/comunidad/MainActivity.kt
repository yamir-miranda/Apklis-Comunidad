package cu.apklis.comunidad

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import cu.apklis.comunidad.home.HomeFragment
import cu.apklis.comunidad.login.LoginFragment
import cu.apklis.comunidad.utils.MyPreferences
import cu.apklis.comunidad.utils.PaidCheked
import cu.apklis.comunidad.utils.StatusBarUtil


class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    var theme = "blanco"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (MyPreferences(this).darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        StatusBarUtil.immersive(this)

        if (MyPreferences(this).userObject == "") {
            addFragmentToActivity(LoginFragment())
        } else {
            addFragmentToActivity(HomeFragment())
        }

        if (MyPreferences(this).Paid) {
            if (MyPreferences(this).userObject == "") {
                addFragmentToActivity(LoginFragment())
            } else {
                addFragmentToActivity(HomeFragment())
            }
        } else {
            if ( PaidCheked().Cheked(this, "cu.ymv.infodevcuba") == "code04") {
                MyPreferences(this).Paid = true
                if (MyPreferences(this).userObject == "") {
                    addFragmentToActivity(LoginFragment())
                } else {
                    addFragmentToActivity(HomeFragment())
                }
            } else {
                MyPreferences(this).Paid = false
                addFragmentToActivity(PaidCheckFragment.newInstance(theme, ""))
            }
        }
    }

    fun addFragmentToActivity(fragment: Fragment?) {
        if (fragment == null) return
        val fm = supportFragmentManager
        val tr = fm.beginTransaction()
        tr.replace(R.id.container, fragment)
        tr.commitAllowingStateLoss()
    }

    fun changeTheme() {
        if (MyPreferences(this).darkMode) {
            MyPreferences(this).darkMode = false
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        } else {
            MyPreferences(this).darkMode = true
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
}