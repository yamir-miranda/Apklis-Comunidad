package cu.apklis.comunidad.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.kennyc.view.MultiStateView
import cu.apklis.comunidad.PrefrenceFragment
import cu.apklis.comunidad.R
import cu.apklis.comunidad.appDetails.TabDetailsAppFragment
import cu.apklis.comunidad.login.LoginFragment
import cu.apklis.comunidad.misGanancias.MisGananciasFragment
import cu.apklis.comunidad.models.App
import cu.apklis.comunidad.models.AppListResponse
import cu.apklis.comunidad.models.User
import cu.apklis.comunidad.user.UserFragment
import cu.apklis.comunidad.utils.MyPreferences
import cu.apklis.comunidad.utils.NetworkManager
import cu.apklis.comunidad.webservices.VolleySingleton
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*

/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class HomeFragment : Fragment(), MultiStateView.StateListener, ItemAdapter.AppClickListener {
    var userObject: User? = null
    var adapterItem: ItemAdapter? = null
    val TAG = "HomeFragment"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (MyPreferences(requireContext()).userObject == "") {
            addFragmentToFragment(LoginFragment())
        } else {
            userObject =
                Gson().fromJson(MyPreferences(requireContext()).userObject, User::class.java)
        }
        adapterItem = ItemAdapter(requireContext(), this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View = inflater.inflate(R.layout.fragment_home, container, false)
        root.recycler_view.layoutManager = LinearLayoutManager(requireContext())
        root.recycler_view.adapter = adapterItem
        if (adapterItem!!.itemCount > 0) {
            root.multiStateView.animateLayoutChanges = false
            root.multiStateView.viewState = MultiStateView.ViewState.CONTENT
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(requireContext())
            .load(userObject?.avatar)
            .placeholder(R.drawable.avatar)
            .error(R.drawable.avatar)
            .into(userImg)
        multiStateView.listener = this
        multiStateView.getView(MultiStateView.ViewState.ERROR)?.findViewById<Button>(R.id.btnRetry)
            ?.setOnClickListener {
                if (!NetworkManager().isNetworkAvailable(requireContext())) {
                    showTost(resources.getString(R.string.sin_red), false)
                } else {
                    loadApp(false)
                }
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mi_cuenta.setOnClickListener {
            addFragmentToFragmentBack(UserFragment())
        }
        loadApp(false)

        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = true
            loadApp(true)
        }

        view_mis_ganancias.setOnClickListener {
            addFragmentToFragmentBack(MisGananciasFragment())
        }

        ajustes.setOnClickListener {
            addFragmentToFragmentBack(PrefrenceFragment())
        }
    }

    private fun addFragmentToFragment(fragment: Fragment) {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.container, fragment)
        transaction?.disallowAddToBackStack()
        transaction?.commit()
    }

    private fun addFragmentToFragmentBack(fragment: Fragment) {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.container, fragment)
        transaction?.addToBackStack(fragment.tag)
        transaction?.commit()
    }

    private fun loadApp(reload: Boolean) {
        if (adapterItem!!.itemCount == 0 || reload) {
            multiStateView.animateLayoutChanges = true
            if (!reload) {
                multiStateView.viewState = MultiStateView.ViewState.LOADING
            }
            val urlUser: String =
                "https://api.apklis.cu/v1/application/?owner=" + userObject!!.user
            val userRequest: StringRequest = object : StringRequest(Method.GET, urlUser,
                Response.Listener { response ->
                    val appListObject = Gson().fromJson(response, AppListResponse::class.java)
                    adapterItem?.setAppList(appListObject.results)
                    multiStateView?.viewState = MultiStateView.ViewState.CONTENT
                    swipeRefresh?.isRefreshing = false
                }, Response.ErrorListener { error ->
                    when (error) {
                        is AuthFailureError -> {
                            Log.d(TAG, "LoginUser: Invalid credentials given.")
                            MyPreferences(requireContext()).userObject = ""
                            addFragmentToFragment(LoginFragment())
                        }
                        is TimeoutError -> {
                            Log.d(TAG, "LoginUser: tiempo de espera excedido")
                            //show Toast
                        }
                        is NoConnectionError -> {
                            Log.d(TAG, "LoginUser: conexion abortada")
                        }
                        else -> {
                            Log.d(TAG, "LoginUser: error desconocido y no procesado")
                            // show error desconocido
                            error.printStackTrace()
                        }
                    }
                    multiStateView?.viewState = MultiStateView.ViewState.ERROR
                    swipeRefresh?.isRefreshing = false
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] =
                        userObject!!.tokens.token_type + " " + userObject!!.tokens.access_token
                    Log.d(
                        TAG,
                        "getHeaders: " + userObject!!.tokens.token_type + " " + userObject!!.tokens.access_token
                    )
                    return headers
                }

                override fun getRetryPolicy(): RetryPolicy {
                    return DefaultRetryPolicy(
                        1000 * 25,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    )
                }
            }
            VolleySingleton.getInstance(requireContext()).addToRequestQueue(userRequest)
        }
    }

    override fun onStateChanged(viewState: MultiStateView.ViewState) {
        Log.d(TAG, "onStateChanged: change")
    }

    private fun showTost(text: String, corto: Boolean) {
        if (corto) {
            Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()
        }
    }

    override fun onAppClickListener(data: App) {
        addFragmentToFragmentBack(
            TabDetailsAppFragment.newInstance(
                data.id.toString(),
                data.package_name,
                data.last_release.icon,
                data.price
            )
        )
        Log.d(TAG, "onAppClickListener: " + data.price)
    }
}