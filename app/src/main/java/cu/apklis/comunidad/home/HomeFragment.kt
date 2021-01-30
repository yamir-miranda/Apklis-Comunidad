package cu.apklis.comunidad.home

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import cu.apklis.comunidad.MainActivity
import cu.apklis.comunidad.R
import cu.apklis.comunidad.login.LoginFragment
import cu.apklis.comunidad.misApps.MisAppsFragment
import cu.apklis.comunidad.misGanancias.MisGananciasFragment
import cu.apklis.comunidad.models.MisGananciasResponse
import cu.apklis.comunidad.models.User
import cu.apklis.comunidad.user.UserFragment
import cu.apklis.comunidad.utils.DateUtils
import cu.apklis.comunidad.utils.MyPreferences
import cu.apklis.comunidad.webservices.VolleySingleton
import kotlinx.android.synthetic.main.fragment_home.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    var userObject: User? = null
    val TAG = "HomeFragment"
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
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loadDataApi()
        mi_cuenta.setOnClickListener {
            addFragmentToFragmentBack(UserFragment())
        }

        mis_apps.setOnClickListener {
            addFragmentToFragmentBack(MisAppsFragment())
        }

        swipeRefresh.setOnRefreshListener {
            loadDataApi()
        }

        view_mis_ganancias.setOnClickListener {
            addFragmentToFragmentBack(MisGananciasFragment())
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


    private fun showTost(text: String, corto: Boolean) {
        if (corto) {
            Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()
        }
    }


    fun roundOffDecimal(number: Double): Double? {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.FLOOR
        return df.format(number).toDouble()
    }

    private fun loadDataApi() {
        sin_datos?.visibility = View.GONE
        val now = Calendar.getInstance()
        val fecha_inicio = Calendar.getInstance()
        fecha_inicio.set(now[Calendar.YEAR], now[Calendar.MONTH], 1)
        val mes = now[Calendar.MONTH] + 1
        val global_fecha_inicio = "" + now[Calendar.YEAR] + "-" + mes + "-01"
        val global_fecha_final =
            "" + now[Calendar.YEAR] + "-" + mes + "-" + now[Calendar.DAY_OF_MONTH]
        val seller = userObject!!.phone.substring(6, 13)
        val fecha_hoy = DateUtils().formatStringDate("yyyy-MM-dd", "yyyy-MM-dd", global_fecha_final)
        val url =
            "https://api.apklis.cu/v1/payment/sales/?lte=$global_fecha_final 23:59&gte=$global_fecha_inicio&seller=%2B53%205%20$seller"
        val stringRequest: StringRequest = object : StringRequest(Method.GET, url,
            Response.Listener { response ->
                val byte: ByteArray = response.toByteArray(charset("ISO-8859-1"))
                val gananciasObject =
                    Gson().fromJson(
                        String(byte, charset("UTF-8")),
                        Array<MisGananciasResponse>::class.java
                    )
                val list = ArrayList<MisGananciasResponse>()
                list.addAll(gananciasObject)
                if (list.size == 0) {
                    // sin ventas reportadas en el mes
                } else {
                    // con ventas reportadas en el mes
                }
                var today_sales_import = 0.00
                var today_sales_count = 0
                var importeTotal = 0.00
                var cantidad_ventas = 0
                val data = ArrayList<Float>()
                Log.d(TAG, "loadDataApi: ${fecha_hoy}")
                for (element in gananciasObject) {
                    importeTotal += element.ammount
                    cantidad_ventas += element.sales
                    data.add((roundOffDecimal(element.ammount * 66.5)!!.div(100)).toFloat())
                    if (element.day.equals(fecha_hoy)) {
                        today_sales_count = element.sales
                        today_sales_import = element.ammount
                    }
                }

                sparkview?.adapter = GraficAdapter(data)
                if (data.isEmpty()) {
                    sin_datos?.visibility = View.VISIBLE
                }

                val text10 = cantidad_ventas.toString() + " Ventas"
                view_mes_ventas?.text = text10
                val text_total = "Total: " + roundOffDecimal(importeTotal).toString() + " CUP"
                view_mes_total?.text = text_total
                val text_impuesto =
                    "Impuestos: " + roundOffDecimal((importeTotal * 0.30) + (importeTotal * 0.035)).toString() + " CUP"
                view_mes_impuestos?.text = text_impuesto
                val text_monto = roundOffDecimal(importeTotal * 0.665).toString() + " CUP"
                view_mes_ganancias?.text = text_monto

                val text100 = today_sales_count.toString() + " Ventas"
                view_hoy_ventas?.text = text100
                val text_total2 =
                    "Total: " + roundOffDecimal(today_sales_import).toString() + " CUP"
                view_hoy_total?.text = text_total2
                val text_impuesto2 =
                    "Impuestos: " + roundOffDecimal((today_sales_import * 0.30) + (today_sales_import * 0.035)).toString() + " CUP"
                view_hoy_impuestos?.text = text_impuesto2
                val text_monto2 = roundOffDecimal(today_sales_import * 0.665).toString() + " CUP"
                view_hoy_ganancias?.text = text_monto2

                swipeRefresh?.isRefreshing = false
                view_hoy_load_data?.visibility = View.INVISIBLE
                view_mes_load_data?.visibility = View.INVISIBLE
                view_comportamiento_load_data?.visibility = View.INVISIBLE


            }, Response.ErrorListener { error ->
                when (error) {
                    is AuthFailureError -> {
                        Log.d("HOME", "LoginUser: Invalid credentials given.")
                        MyPreferences(context).userObject = ""
                        (context as MainActivity).addFragmentToActivity(LoginFragment())
                    }
                    is TimeoutError -> {
                        Log.d("HOME", "LoginUser: tiempo de espera excedido")
                    }
                    is NoConnectionError -> {
                        Log.d("HOME", "LoginUser: conexion abortada")
                    }
                    else -> {
                        Log.d("HOME", "LoginUser: error desconocido y no procesado")
                        MyPreferences(context).userObject = ""
                        (context as MainActivity).addFragmentToActivity(LoginFragment())
                        error.printStackTrace()
                    }
                }

                swipeRefresh?.isRefreshing = false
                view_hoy_load_data?.visibility = View.INVISIBLE
                view_mes_load_data?.visibility = View.INVISIBLE
                view_comportamiento_load_data?.visibility = View.INVISIBLE

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
}