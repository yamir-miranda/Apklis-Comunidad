package cu.apklis.comunidad

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import cu.apklis.comunidad.login.LoginFragment
import cu.apklis.comunidad.models.User
import cu.apklis.comunidad.utils.MyPreferences
import kotlinx.android.synthetic.main.fragment_prefrence.*
import kotlinx.android.synthetic.main.fragment_prefrence.view.*

/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class PrefrenceFragment : Fragment() {
    var userObject: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (MyPreferences(requireContext()).userObject == "") {
            addFragmentToFragment(LoginFragment())
        } else {
            userObject =
                Gson().fromJson(MyPreferences(requireContext()).userObject, User::class.java)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View = inflater.inflate(R.layout.fragment_prefrence, container, false)
        root.switchDarkMode.isChecked = MyPreferences(requireContext()).darkMode
        return root
    }

    private fun addFragmentToFragment(fragment: Fragment) {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.container, fragment)
        transaction?.disallowAddToBackStack()
        transaction?.commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logoPreference.setOnClickListener {
            (activity as MainActivity).onBackPressed()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        darkModeContent.setOnClickListener {
            switchDarkMode.isChecked = !switchDarkMode.isChecked
        }
        switchDarkMode.setOnCheckedChangeListener { _, b ->
            MyPreferences(requireContext()).darkMode = b
            val intent: Intent = requireActivity().intent
            requireActivity().finish()
            startActivity(intent)
        }
    }
}