package cu.apklis.comunidad.misGanancias

import android.app.AlertDialog
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
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.gson.Gson
import com.kennyc.view.MultiStateView
import cu.apklis.comunidad.MainActivity
import cu.apklis.comunidad.R
import cu.apklis.comunidad.home.GraficAdapter
import cu.apklis.comunidad.login.LoginFragment
import cu.apklis.comunidad.models.User
import cu.apklis.comunidad.utils.MyPreferences
import cu.apklis.comunidad.utils.NetworkManager
import cu.apklis.comunidad.webservices.VolleySingleton
import kotlinx.android.synthetic.main.fragment_mis_ganancias.*
import kotlinx.android.synthetic.main.fragment_mis_ganancias.exit
import kotlinx.android.synthetic.main.fragment_mis_ganancias.sparkview
import kotlinx.android.synthetic.main.fragment_mis_ganancias.theme
import kotlinx.android.synthetic.main.fragment_mis_ganancias.view.*

import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import cu.apklis.comunidad.models.MisGananciasResponse as MisGananciasResponse1

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val TAG = "MisGanancias"

/**
 * A simple [Fragment] subclass.
 * Use the [MisGananciasFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MisGananciasFragment : Fragment(), MultiStateView.StateListener {
    private var param1: String? = null
    private var param2: Int? = null
    private var global_fecha_inicio: String? = null
    private var global_fecha_final: String? = null
    var userObject: User? = null
    var adapterReportMisGanancias: MisGananciasAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getInt(ARG_PARAM2)
        }
        if (MyPreferences(requireContext()).userObject == "") {
            addFragmentToFragment(LoginFragment())
        } else {
            userObject =
                Gson().fromJson(MyPreferences(requireContext()).userObject, User::class.java)
        }
        adapterReportMisGanancias = MisGananciasAdapter(
            requireContext(),
            userObject!!.tokens.token_type + " " + userObject!!.tokens.access_token, userObject!!
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_mis_ganancias, container, false)
        if (param2 == 0) {
            root.sin_soporte_ventas.visibility = View.VISIBLE
        } else {
            root.multiStateViewGanancias.visibility = View.VISIBLE
        }
        root.recycler_ReportGanancias.layoutManager = LinearLayoutManager(requireContext())
        root.recycler_ReportGanancias.adapter = adapterReportMisGanancias
        if (adapterReportMisGanancias!!.itemCount > 0) {
            root.multiStateViewGanancias.animateLayoutChanges = false
            root.multiStateViewGanancias.viewState = MultiStateView.ViewState.CONTENT
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        multiStateViewGanancias.listener = this
        multiStateViewGanancias.getView(MultiStateView.ViewState.ERROR)
            ?.findViewById<Button>(R.id.btnRetryVentas)
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
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        val now = Calendar.getInstance()
        val fecha_inicio = Calendar.getInstance()
        fecha_inicio.set(now[Calendar.YEAR], now[Calendar.MONTH], 1)
        val mes = now[Calendar.MONTH] + 1
        val text1 = "01/" + mes + "/" + now[Calendar.YEAR]
        global_fecha_inicio = "" + now[Calendar.YEAR] + "-" + mes + "-01"
        view_date_start?.text = text1
        val text2 =
            "" + now[Calendar.DAY_OF_MONTH] + "/" + mes + "/" + now[Calendar.YEAR]
        global_fecha_final =
            "" + now[Calendar.YEAR] + "-" + mes + "-" + now[Calendar.DAY_OF_MONTH]
        view_date_end?.text = text2
        val millionSeconds = now.timeInMillis - fecha_inicio.timeInMillis
        val days_diff = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(millionSeconds) + 1
        var text3 = ""
        text3 = if (days_diff.toInt() == 1) {
            "$days_diff día"
        } else {
            "$days_diff días"
        }
        view_date_cont_day?.text = text3
        builder.setTitleText("Selecione rango para reporte")
        builder.setSelection(androidx.core.util.Pair(fecha_inicio.timeInMillis, now.timeInMillis))
        val picker = builder.build()
        view_card_date.setOnClickListener {
            if (!picker.isVisible){
                picker.show(activity?.supportFragmentManager!!, picker.toString())
            }
        }
        val outputDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val outputDateFormatAPI = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        picker.addOnPositiveButtonClickListener {
            view_date_start?.text = outputDateFormat.format(it.first)
            global_fecha_inicio = outputDateFormatAPI.format(it.first)
            view_date_end?.text = outputDateFormat.format(it.second)
            global_fecha_final = outputDateFormatAPI.format(it.second)
            val diff: Long = it.second!! - it.first!!
            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = (hours / 24) + 1

            var text4 = if (days.toInt() == 1) {
                "$days día"
            } else {
                "$days días"
            }
            view_date_cont_day.text = text4
            view_load_data.visibility = View.VISIBLE
            loadDataApi(false)
        }
        if (param2 != 0) {
            loadDataApi(false)
        }

        swipeRefreshReportVentas.setOnRefreshListener {
            loadDataApi(false)
        }

        theme.setOnClickListener {
            (activity as MainActivity).changeTheme()
        }

        exit.setOnClickListener {
            val builder = AlertDialog.Builder(context)
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
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment VersionAppFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: Int) =
            MisGananciasFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putInt(ARG_PARAM2, param2)
                }
            }
    }

    fun roundOffDecimal(number: Double): Double? {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.FLOOR
        return df.format(number).toDouble()
    }

    private fun loadDataApi(loading: Boolean) {
        if (loading) {
            multiStateViewGanancias?.viewState = MultiStateView.ViewState.LOADING
        }
        view_comportamiento_sin_datos?.visibility = View.GONE
        val seller = userObject!!.phone.substring(6, 13)
        Log.d(TAG, "loadDataApi: $seller")
        val url =
            "https://api.apklis.cu/v1/payment/sales/?lte=$global_fecha_final 23:59&gte=$global_fecha_inicio&seller=%2B53%205%20$seller"
        val stringRequest: StringRequest = object : StringRequest(Method.GET, url,
            Response.Listener { response ->
                val byte: ByteArray = response.toByteArray(charset("ISO-8859-1"))
                val gananciasObject =
                    Gson().fromJson(
                        String(byte, charset("UTF-8")),
                        Array<MisGananciasResponse1>::class.java
                    )
                val list = ArrayList<MisGananciasResponse1>()
                list.addAll(gananciasObject)
                if (list.size == 0) {
                    recycler_ReportGanancias?.visibility = View.GONE
                    sin_ventas_ganancias?.visibility = View.VISIBLE
                } else {
                    sin_ventas_ganancias?.visibility = View.GONE
                    recycler_ReportGanancias?.visibility = View.VISIBLE
                }
                adapterReportMisGanancias?.setData(list)
                var importeTotal = 0.00
                var cantidad_ventas= 0
                val data = ArrayList<Float>()
                for (element in gananciasObject) {
                    importeTotal += element.ammount
                    cantidad_ventas+= element.sales
                    data.add((roundOffDecimal(element.ammount * 66.5)!!.div(100)).toFloat())
                }
                sparkview?.adapter = GraficAdapter(data)
                if (data.isEmpty() || data.size == 1) {
                    view_comportamiento_sin_datos?.visibility = View.VISIBLE
                }
                view_ventas?.text = "$cantidad_ventas Ventas"
                val text_total = "Total: " + roundOffDecimal(importeTotal).toString() + " CUP"
                view_total?.text = text_total
                val text_impuesto = "Impuestos: " + roundOffDecimal((importeTotal * 0.30) + (importeTotal * 0.035)).toString() + " CUP"
                view_impuestos?.text = text_impuesto
                val text_monto =
                    roundOffDecimal(importeTotal * 0.665).toString() + " CUP"
                view_ganancias?.text = text_monto
                multiStateViewGanancias?.viewState = MultiStateView.ViewState.CONTENT
                swipeRefreshReportVentas?.isRefreshing = false
                view_load_data?.visibility = View.INVISIBLE

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
                multiStateViewGanancias?.viewState = MultiStateView.ViewState.ERROR
                swipeRefreshReportVentas?.isRefreshing = false
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
}