package cu.apklis.comunidad.appDetails

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
import com.google.gson.Gson
import com.kennyc.view.MultiStateView
import cu.apklis.comunidad.MainActivity
import cu.apklis.comunidad.R
import cu.apklis.comunidad.login.LoginFragment
import cu.apklis.comunidad.models.AppListResponse
import cu.apklis.comunidad.models.User
import cu.apklis.comunidad.utils.DateUtils
import cu.apklis.comunidad.utils.MyPreferences
import cu.apklis.comunidad.utils.NetworkManager
import cu.apklis.comunidad.webservices.VolleySingleton
import kotlinx.android.synthetic.main.fragment_app_details.*
import kotlinx.android.synthetic.main.fragment_ventas_app.*
import java.math.RoundingMode
import java.text.DecimalFormat

private const val ARG_PACKAGE = "app_package"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AppDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AppDetailsFragment : Fragment(), MultiStateView.StateListener {
    private var appPackage: String? = null
    private var param2: String? = null
    var userObject: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            appPackage = it.getString(ARG_PACKAGE)
            param2 = it.getString(ARG_PARAM2)
        }
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
        return inflater.inflate(R.layout.fragment_app_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        multiStateViewAppDetalles.listener = this
        multiStateViewAppDetalles.getView(MultiStateView.ViewState.ERROR)
            ?.findViewById<Button>(R.id.btnRetryDetalles)
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
        loadDataApi(
            false
        )

        swipeRefreshDetalles.setOnRefreshListener {
            loadDataApi(false)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param appPackage Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AppDetailsFragment.
         */
        @JvmStatic
        fun newInstance(appPackage: String, param2: String) =
            AppDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PACKAGE, appPackage)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun loadDataApi(loading: Boolean) {
        if (loading) {
            multiStateViewAppDetalles.viewState = MultiStateView.ViewState.LOADING
        }
        val url = "https://api.apklis.cu/v1/application/?package_name=$appPackage"
        Log.d(TAG, "loadDataApi: $url")
        val stringRequest: StringRequest = object : StringRequest(Method.GET, url,
            Response.Listener { response ->
                val byte: ByteArray = response.toByteArray(charset("ISO-8859-1"))
                val appObject =
                    Gson().fromJson(
                        String(byte, charset("UTF-8")),
                        AppListResponse::class.java
                    ).results[0]

                Log.d(TAG, "loadDataApi: $appObject")

                var description = appObject.description
                description = description.replace("<p>&nbsp;</p>", "")
                description = description.replace("&nbsp;", "")
                description = description.replace("</p>", "\n")
                description = description.replace("<p>", "")
                app_details_description?.text = description
                app_details_reviews_count?.text = appObject.reviews_count.toString()
                app_details_start?.rating = appObject.rating
                if (appObject.reviews_count !== 0) {
                    app_details_rating?.text =
                        roundOffDecimal(appObject.rating.toDouble()).toString()
                    val porc5 = ((appObject.reviews_star_5 * 100) / appObject.reviews_count)
                    val porc5Text = "$porc5 %"
                    app_details_reviews_star_5_bar?.progress = porc5
                    app_details_reviews_star_5_text?.text = porc5Text

                    val porc4 = appObject.reviews_star_4 * 100 / appObject.reviews_count
                    val porc4Text = "$porc4 %"
                    app_details_reviews_star_4_bar?.progress = porc4
                    app_details_reviews_star_4_text?.text = porc4Text

                    val porc3 = appObject.reviews_star_3 * 100 / appObject.reviews_count
                    val porc3Text = "$porc3 %"
                    app_details_reviews_star_3_bar?.progress = porc3
                    app_details_reviews_star_3_text?.text = porc3Text

                    val porc2 = appObject.reviews_star_2 * 100 / appObject.reviews_count
                    val porc2Text = "$porc2 %"
                    app_details_reviews_star_2_bar?.progress = porc2
                    app_details_reviews_star_2_text?.text = porc2Text

                    val porc1 = appObject.reviews_star_1 * 100 / appObject.reviews_count
                    val porc1Text = "$porc1 %"
                    app_details_reviews_star_1_bar?.progress = porc1
                    app_details_reviews_star_1_text?.text = porc1Text

                }

                val categories = StringBuilder()
                for(categoria in appObject.categories){
                    categories.append(categoria.name + "/ ")
                }
                app_details_categorias?.text = categories.toString()

                if (appObject.last_release.abi.size !=0){
                    card_arquitectura?.visibility = View.VISIBLE
                    val arquitecturas = StringBuilder()
                    for(arquitc in appObject.last_release.abi){
                        arquitecturas.append(arquitc.abi + "/ ")
                    }
                    app_details_arquitecturas?.text = arquitecturas.toString()
                }
                view_release_version_name?.text = appObject.last_release.version_name
                view_release_version_code?.text = appObject.last_release.version_code.toString()
                view_release_size?.text = appObject.last_release.human_readable_size
                view_release_sdk_min?.text = appObject.last_release.version_sdk_name
                view_release_sdk_object?.text = appObject.last_release.version_target_sdk_name

                if (appObject.public){
                    view_app_visibiliti?.text = "Publica"
                }else{
                    view_app_visibiliti?.text = "Privada"
                }

                view_app_download_count?.text = appObject.download_count.toString()
                view_app_sales_count?.text = appObject.sale_count.toString()
                val text = appObject.price.toString()+" CUP"
                view_app_price?.text = text
                view_app_name?.text = appObject.name
                view_app_package?.text = appObject.package_name

                view_app_actualizada?.text = DateUtils().getTimeAgo(
                    DateUtils().loadDateofString(
                        "yyyy-MM-dd'T'HH:mm:ss",
                        appObject.last_release.published
                    ), requireContext()
                )

                view_app_publicada?.text = DateUtils().getTimeAgo(
                    DateUtils().loadDateofString(
                        "yyyy-MM-dd'T'HH:mm:ss",
                        appObject.last_updated
                    ), requireContext()
                )

                view_app_capturas?.text = appObject.last_release.screenshots.size.toString()


                    multiStateViewAppDetalles?.viewState = MultiStateView.ViewState.CONTENT
                swipeRefreshDetalles?.isRefreshing = false

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
                        error.printStackTrace()
                    }
                }
                multiStateViewAppDetalles?.viewState = MultiStateView.ViewState.ERROR
                swipeRefreshDetalles?.isRefreshing = false
                view_load_data?.visibility = View.INVISIBLE
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

    private fun addFragmentToFragment(fragment: Fragment) {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.container, fragment)
        transaction?.disallowAddToBackStack()
        transaction?.commit()
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

    fun roundOffDecimal(number: Double): Double? {
        val df = DecimalFormat("#.#")
        df.roundingMode = RoundingMode.FLOOR
        return df.format(number).toDouble()
    }
}