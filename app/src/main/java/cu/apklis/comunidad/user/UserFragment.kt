package cu.apklis.comunidad.user

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.kennyc.view.MultiStateView
import cu.apklis.comunidad.MainActivity
import cu.apklis.comunidad.R
import cu.apklis.comunidad.appDetails.TAG
import cu.apklis.comunidad.login.LoginFragment
import cu.apklis.comunidad.models.User
import cu.apklis.comunidad.models.UserResponse
import cu.apklis.comunidad.utils.MyPreferences
import cu.apklis.comunidad.utils.NetworkManager
import cu.apklis.comunidad.webservices.VolleySingleton
import kotlinx.android.synthetic.main.fragment_user.*

/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class UserFragment : Fragment(), MultiStateView.StateListener {
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

        theme.setOnClickListener {
            (activity as MainActivity).changeTheme()
        }

        exit.setOnClickListener {
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

        multiStateViewUser.listener = this
        multiStateViewUser.getView(MultiStateView.ViewState.ERROR)
            ?.findViewById<Button>(R.id.btnRetryUser)
            ?.setOnClickListener {
                if (!NetworkManager().isNetworkAvailable(requireContext())) {
                    showTost(resources.getString(R.string.sin_red), false)
                } else {
                    loadDataApi(true)
                }
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        swipeRefressUser?.setOnRefreshListener {
            loadDataApi(false)
        }
        loadDataApi(true)
    }

    private fun addFragmentToFragment(fragment: Fragment) {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.container, fragment)
        transaction?.disallowAddToBackStack()
        transaction?.commit()
    }

    private fun loadDataApi(loading: Boolean) {
        if (loading) {
            multiStateViewUser?.viewState = MultiStateView.ViewState.LOADING
        }
        val url =
            "https://api.apklis.cu/v1/user/" + userObject?.username + "/"
        val stringRequest: StringRequest = object : StringRequest(Method.GET, url,
            Response.Listener { response ->
                val byte: ByteArray = response.toByteArray(charset("ISO-8859-1"))
                val usuarioObject =
                    Gson().fromJson(
                        String(byte, charset("UTF-8")),
                        UserResponse::class.java
                    )

                Glide.with(requireContext())
                    .load(usuarioObject.avatar)
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .into(userAvatar)
                val name = usuarioObject.first_name + " " + usuarioObject.last_name
                view_user_name?.text = name
                view_user_username?.text = usuarioObject.username
                view_apps_deseos?.text = usuarioObject.wishlist.size.toString()
                view_apps_fav?.text = usuarioObject.favorite_apps.size.toString()
                view_apps_shop?.text = usuarioObject.payed.size.toString()

                if (usuarioObject.seller.active){
                    view_user_banner?.text = "Desarrollador | Vendedor"
                }else{
                    view_user_banner?.text = "Desarrollador"
                }

                view_user_namee?.text = usuarioObject.first_name
                view_user_apellidos?.text = usuarioObject.last_name
                view_user_movil?.text = usuarioObject.phone_number
                view_user_email?.text = usuarioObject.email
                view_user_sha1?.text = usuarioObject.sha1

                if (usuarioObject.is_developer_active){
                    view_user_dev_status?.text = "Activo"
                }else{
                    view_user_dev_status?.text = "Desactivado"
                }

                view_seller_name?.text = usuarioObject.seller.name
                view_seller_movil?.text = usuarioObject.seller.phone_number
                view_seller_email?.text = usuarioObject.seller.email
                view_seller_ci?.text = usuarioObject.seller.cid
                view_seller_tarjeta?.text = usuarioObject.seller.account
                view_seller_municipio?.text = usuarioObject.seller.municipality
                view_seller_provincia?.text = usuarioObject.seller.province
                view_seller_direccion?.text = usuarioObject.seller.address

                if (usuarioObject.seller.active){
                    view_seller_status?.text = "Activo"
                }else{
                    view_seller_status?.text = "Desactivado"
                }

                multiStateViewUser?.viewState = MultiStateView.ViewState.CONTENT
                swipeRefressUser?.isRefreshing = false

            }, Response.ErrorListener { error ->
                when (error) {
                    is AuthFailureError -> {
                        Log.d(TAG, "LoginUser: Invalid credentials given.")
                        MyPreferences(context).userObject = ""
                        (context as MainActivity).addFragmentToActivity(LoginFragment())
                    }
                    is TimeoutError -> {
                        Log.d(TAG, "LoginUser: tiempo de espera excedido")
                    }
                    is NoConnectionError -> {
                        Log.d(TAG, "LoginUser: conexion abortada")
                    }
                    else -> {
                        Log.d(TAG, "LoginUser: error desconocido y no procesado")
                        MyPreferences(context).userObject = ""
                        (context as MainActivity).addFragmentToActivity(LoginFragment())
                        error.printStackTrace()
                    }
                }
                multiStateViewUser?.viewState = MultiStateView.ViewState.ERROR
                swipeRefressUser?.isRefreshing = false
            }) {

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] =
                    userObject!!.tokens.token_type + " " + userObject!!.tokens.access_token
                return headers
            }

            override fun getRetryPolicy(): RetryPolicy {
                return DefaultRetryPolicy(
                    1000 * 25, 0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
            }
        }
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(stringRequest)
    }


    private fun showTost(text: String, corto: Boolean) {
        if (corto) {
            Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()
        }
    }

    override fun onStateChanged(viewState: MultiStateView.ViewState) {
        Log.d(TAG, "onStateChanged: ")
    }
}