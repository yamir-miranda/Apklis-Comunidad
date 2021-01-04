package cu.apklis.comunidad.appDetails

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.hadiidbouk.charts.BarData
import com.kennyc.view.MultiStateView
import cu.apklis.comunidad.MainActivity
import cu.apklis.comunidad.R
import cu.apklis.comunidad.login.LoginFragment
import cu.apklis.comunidad.models.DownloadVersionResponse
import cu.apklis.comunidad.utils.MyPreferences
import cu.apklis.comunidad.webservices.VolleySingleton
import kotlinx.android.synthetic.main.fragment_download_app.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
val TAG = "DownloadAppFragment"

/**
 * A simple [Fragment] subclass.
 * Use the [DownloadAppFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DownloadAppFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_download_app, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DownloadAppFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DownloadAppFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loadDataApi()
    }

    private suspend fun loadGrafic(dataList: ArrayList<BarData>, max_value: Int) {

        withContext(Dispatchers.IO) {
            ChartProgressBar?.setDataList(dataList)
            ChartProgressBar?.setMaxValue(max_value.toFloat())
            withContext(Dispatchers.Main) {
                ChartProgressBar?.build()
                multiStateViewDownload?.viewState = MultiStateView.ViewState.CONTENT
            }
        }
    }

    private fun loadDataApi() {
        val url = "https://api.apklis.cu/v2/download/by_releases/"
        val stringRequest: StringRequest = object : StringRequest(Method.POST, url,
            Response.Listener { response ->
                Log.d(TAG, "bind: $response")
                val byte: ByteArray = response.toByteArray(charset("ISO-8859-1"))
                val downloadObject =
                    Gson().fromJson(
                        String(byte, charset("UTF-8")),
                        DownloadVersionResponse::class.java
                    )

                val dataList: ArrayList<BarData> = ArrayList()
                var max_value = 0
                for (element in downloadObject.downloads) {
                    if (max_value < element.values.elementAt(0)) {
                        max_value = element.values.elementAt(0)
                    }
                    dataList.add(
                        BarData(
                            element.keys.elementAt(0),
                            element.values.elementAt(0).toFloat(),
                            element.values.elementAt(0).toString()
                        )
                    )

                }

                lifecycleScope.launch {
                    loadGrafic(dataList, max_value + 20)
                }
            }, Response.ErrorListener { error ->
                when (error) {
                    is AuthFailureError -> {
                        Log.d(TAG, "LoginUser: Invalid credentials given.")
                        MyPreferences(context).userObject = ""
                        (context as MainActivity).addFragmentToActivity(LoginFragment())
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
                multiStateViewDownload?.viewState = MultiStateView.ViewState.ERROR
            }) {

            override fun getRetryPolicy(): RetryPolicy {
                return DefaultRetryPolicy(
                    1000 * 25, 0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
            }

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                val jsonBody = JSONObject()
                jsonBody.put("app", param1!!.toInt())
                return jsonBody.toString().toByteArray(charset("utf-8"))
            }
        }
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(stringRequest)
    }

}