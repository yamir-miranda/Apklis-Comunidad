package cu.ymv.infodevcuba

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.gson.Gson
import cu.ymv.infodevcuba.login.LoginFragment
import cu.ymv.infodevcuba.models.User
import cu.ymv.infodevcuba.utils.MyPreferences
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
        if (MyPreferences(requireContext()).darkMode) {
            root.switchDarkMode.isChecked = true
            root.darkModeAutomaticContent.visibility = View.GONE
        } else {
            root.switchDarkMode.isChecked = false
        }

        if (MyPreferences(requireContext()).darkModeAutomatic) {
            root.switchDarkModeAutomatic.isChecked = true
        } else {
            root.switchDarkModeAutomatic.isChecked = false
            root.darkModeAutomaticAvancedContent.visibility = View.GONE
        }

        root.CheckBoxDarkModeTime.isChecked = MyPreferences(requireContext()).darkModeTime
        root.CheckBoxDarkModeBrillo.isChecked = MyPreferences(requireContext()).darkModeBrillo
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

        Glide.with(requireContext())
            .load(userObject?.avatar)
            .placeholder(R.drawable.avatar)
            .error(R.drawable.avatar)
            .into(userImgPreference)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        darkModeContent.setOnClickListener {
            switchDarkMode.isChecked = !switchDarkMode.isChecked
        }
        switchDarkMode.setOnCheckedChangeListener { _, b ->
            Log.d(TAG, "onActivityCreated: $b")
            MyPreferences(requireContext()).darkMode = b
            val intent: Intent = requireActivity().intent
            requireActivity().finish()
            startActivity(intent)
        }

        contentdarkModeAutomatic.setOnClickListener {
            switchDarkModeAutomatic.isChecked = !switchDarkModeAutomatic.isChecked
        }
        switchDarkModeAutomatic.setOnCheckedChangeListener { _, b ->
            Log.d(TAG, "onActivityCreated: $b")
            MyPreferences(requireContext()).darkModeAutomatic = b
        }

        contentdarkModeTime.setOnClickListener {
            CheckBoxDarkModeTime.isChecked = !CheckBoxDarkModeTime.isChecked
        }
        CheckBoxDarkModeTime.setOnCheckedChangeListener { _, b ->
            Log.d(TAG, "onActivityCreated: $b")
            MyPreferences(requireContext()).darkModeTime = b
        }

        contentdarkModeBrillo.setOnClickListener {
            CheckBoxDarkModeTime.isChecked = !CheckBoxDarkModeTime.isChecked
        }
        CheckBoxDarkModeBrillo.setOnCheckedChangeListener { _, b ->
            Log.d(TAG, "onActivityCreated: $b")
            MyPreferences(requireContext()).darkModeBrillo = b
        }
    }
}