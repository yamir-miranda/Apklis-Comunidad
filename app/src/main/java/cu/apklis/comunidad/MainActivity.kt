package cu.apklis.comunidad

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import cu.apklis.comunidad.home.HomeFragment
import cu.apklis.comunidad.login.LoginFragment
import cu.apklis.comunidad.utils.MyPreferences
import cu.apklis.comunidad.utils.PaidCheked


class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    var theme = "blanco"
    override fun onCreate(savedInstanceState: Bundle?) {
        if (MyPreferences(this).darkMode) {
            setTheme(R.style.DarkTheme)
            theme = "negro"
        } else {
            setTheme(R.style.LightTheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
}