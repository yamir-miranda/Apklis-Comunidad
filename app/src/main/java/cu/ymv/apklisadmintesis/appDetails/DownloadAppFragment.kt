package cu.ymv.apklisadmintesis.appDetails

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Cartesian
import com.anychart.core.cartesian.series.Column
import com.anychart.enums.Anchor
import com.anychart.enums.HoverMode
import com.anychart.enums.Position
import com.anychart.enums.TooltipPositionMode
import com.google.gson.Gson
import cu.ymv.apklisadmintesis.MainActivity
import cu.ymv.apklisadmintesis.R
import cu.ymv.apklisadmintesis.login.LoginFragment
import cu.ymv.apklisadmintesis.models.DownloadVersionResponse
import cu.ymv.apklisadmintesis.utils.MyPreferences
import cu.ymv.apklisadmintesis.webservices.VolleySingleton
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
        // Inflate the layout for this fragment
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loadDataApi()
        type_of_grafic.setOnSpinnerItemSelectedListener<String> { position, item ->
            if (position == 0) {
                lifecycleScope.launch {
                    Log.d("asd", "onActivityCreated: $item")

                }
            } else if (position == 1) {
                lifecycleScope.launch {
                    Log.d("asd", "onActivityCreated: $item")
                    loadGrafic2(item)
                }
            }

        }
    }

    private suspend fun loadGrafic(data: MutableList<DataEntry>) {
        any_chart_view.setProgressBar(progress_bar)
        withContext(Dispatchers.IO) {

            val cartesian: Cartesian = AnyChart.column()

            val column: Column = cartesian.column(data)

            column.tooltip()
                .titleFormat("{%X}")
                .position(Position.AUTO)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0.0)
                .offsetY(5.0)
                .format("{%Value}{groupsSeparator: }")

            cartesian.animation(true, 1500)
            // cartesian.title("$type de la aplicación")

            cartesian.yScale().minimum(0.0)

            cartesian.xGrid(true)

            cartesian.yGrid(true)

            cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }")

            cartesian.tooltip().positionMode(TooltipPositionMode.POINT)
            cartesian.interactivity().hoverMode(HoverMode.BY_X)

            cartesian.xAxis(0).title("Versiones")
            cartesian.yAxis(0).title("Descargas")

            withContext(Dispatchers.Main) {
                any_chart_view.setChart(cartesian)
            }

        }
    }

    private suspend fun loadGrafic2(type: String) {
        any_chart_view.setProgressBar(progress_bar)
        withContext(Dispatchers.IO) {

            val cartesian: Cartesian = AnyChart.column()

            val data: MutableList<DataEntry> = ArrayList()
            data.add(ValueDataEntry("1.0.0", 120))
            data.add(ValueDataEntry("1.0.1", 309))
            data.add(ValueDataEntry("1.0.2", 230))
            data.add(ValueDataEntry("1.0.3", 52))
            data.add(ValueDataEntry("1.0.4", 63))
            data.add(ValueDataEntry("1.0.5", 20))
            data.add(ValueDataEntry("1.0.6", 80))
            data.add(ValueDataEntry("1.0.8", 200))
            data.add(ValueDataEntry("1.0.9", 300))

            val column: Column = cartesian.column(data)

            column.tooltip()
                .titleFormat("{%X}")
                .position(Position.AUTO)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0.0)
                .offsetY(5.0)
                .format("{%Value}{groupsSeparator: }")

            cartesian.animation(true, 1500)
            // cartesian.title("$type de la aplicación")

            cartesian.yScale().minimum(0.0)

            cartesian.xGrid(true)

            cartesian.yGrid(true)

            cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }")

            cartesian.tooltip().positionMode(TooltipPositionMode.POINT)
            cartesian.interactivity().hoverMode(HoverMode.BY_X)

            cartesian.xAxis(0).title("Versiones")
            cartesian.yAxis(0).title("Descargas")



            withContext(Dispatchers.Main) {
                any_chart_view.setChart(cartesian)
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

                val data: MutableList<DataEntry> = ArrayList()
                for (element in downloadObject.downloads) {
                    Log.d(TAG, "loadDataApi: " + element.keys.elementAt(0))
                    Log.d(TAG, "loadDataApi: " + element.values.elementAt(0))
                    data.add(ValueDataEntry(element.keys.elementAt(0), element.values.elementAt(0)))

                }

                lifecycleScope.launch {
                    loadGrafic(data)
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