package cu.ymv.apklisadmintesis.user

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.gson.Gson
import cu.ymv.apklisadmintesis.MainActivity
import cu.ymv.apklisadmintesis.R
import cu.ymv.apklisadmintesis.login.LoginFragment
import cu.ymv.apklisadmintesis.models.User
import cu.ymv.apklisadmintesis.utils.MyPreferences
import kotlinx.android.synthetic.main.fragment_user.*

/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class UserFragment : Fragment() {
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logOut.setOnClickListener {
            val builder = AlertDialog.Builder(view.context)
            builder.setTitle("Cerrar Sección")
            builder.setMessage("Usted esta seguro que desea cerrar la sección")
            builder.setPositiveButton(
                "Cerrar Sección"
            ) { dialog, id ->
                MyPreferences(requireContext()).userObject = ""
                addFragmentToFragment(LoginFragment())
            }
            builder.setNegativeButton(
                "Cancelar"
            ) { dialog, id ->
                dialog.dismiss()
            }
            builder.show()
        }

        userImgUser.setOnClickListener {
            (activity as MainActivity).onBackPressed()
        }

        Glide.with(requireContext())
            .load(userObject?.avatar)
            .placeholder(R.drawable.avatar)
            .error(R.drawable.avatar)
            .into(userImgUser)
        Glide.with(requireContext())
            .load(userObject?.avatar)
            .placeholder(R.drawable.avatar)
            .error(R.drawable.avatar)
            .into(userAvatar)
        nombreApllidos.text = userObject!!.fullname
        sha1.text = userObject!!.sha1
        email.text = userObject!!.email
        user.text = userObject!!.username
        descripcion.text = userObject!!.description
        apps.text = userObject!!.apps
        ubicacion.text = userObject!!.province
    }

    private fun addFragmentToFragment(fragment: Fragment) {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.container, fragment)
        transaction?.disallowAddToBackStack()
        transaction?.commit()
    }
}