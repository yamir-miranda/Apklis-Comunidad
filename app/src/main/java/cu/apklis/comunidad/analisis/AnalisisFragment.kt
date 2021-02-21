package cu.apklis.comunidad.analisis

import android.app.AlertDialog
import android.graphics.Color
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
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.gson.Gson
import com.kennyc.view.MultiStateView
import cu.apklis.comunidad.MainActivity
import cu.apklis.comunidad.R
import cu.apklis.comunidad.login.LoginFragment
import cu.apklis.comunidad.models.AppListResponse
import cu.apklis.comunidad.models.User
import cu.apklis.comunidad.utils.MyPreferences
import cu.apklis.comunidad.utils.NetworkManager
import cu.apklis.comunidad.webservices.VolleySingleton
import im.dacer.androidcharts.LineView
import kotlinx.android.synthetic.main.fragment_analisis.*
import java.math.RoundingMode
import java.text.DecimalFormat


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AnalisisFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AnalisisFragment : Fragment(), MultiStateView.StateListener {
    var userObject: User? = null
    val TAG = "AnalsisAppsFragment"

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
        val root: View = inflater.inflate(R.layout.fragment_analisis, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        multiStateViewAnalisis.listener = this
        multiStateViewAnalisis.getView(MultiStateView.ViewState.ERROR)
            ?.findViewById<Button>(R.id.btnRetry)
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

        loadApp(false)

        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = true
            loadApp(true)
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

    private fun loadApp(reload: Boolean) {

        val urlUser: String =
            "https://api.apklis.cu/v1/application/?owner=" + userObject!!.user
        val userRequest: StringRequest = object : StringRequest(Method.GET, urlUser,
            Response.Listener { response ->
                val appListObject = Gson().fromJson(response, AppListResponse::class.java)

                var total_download = 0
                var total_ventas = 0
                var total_ganancias = 0.0

                val LineTitle = ArrayList<String>()
                LineTitle.add("1 estrella   ")
                LineTitle.add("2 estrellas  ")
                LineTitle.add("3 estrellas  ")
                LineTitle.add("4 estrellas  ")
                LineTitle.add("5 estrellas  ")
                val dataLists: ArrayList<ArrayList<Int>> = ArrayList()
                for (element in appListObject.results) {
                    val ddataList: ArrayList<Int> = ArrayList()
                    ddataList.add(element.reviews_star_1)
                    ddataList.add(element.reviews_star_2)
                    ddataList.add(element.reviews_star_3)
                    ddataList.add(element.reviews_star_4)
                    ddataList.add(element.reviews_star_5)
                    dataLists.add(ddataList)
                    total_download += element.download_count
                    total_ventas += element.sale_count
                    total_ganancias += element.price * element.sale_count
                }

                view_grafic_comentarios?.setBottomTextList(LineTitle)
                val colors: ArrayList<Int> = ArrayList()
                for (c in ColorTemplate.VORDIPLOM_COLORS){
                    colors.add(c)
                }

                view_grafic_comentarios?.setColorArray(
                    colors.toIntArray()
                )
                view_grafic_comentarios?.setDrawDotLine(true)
                view_grafic_comentarios?.setShowPopup(LineView.SHOW_POPUPS_NONE)
                view_grafic_comentarios?.setDataList(dataLists)


                val yvalues = ArrayList<PieEntry>()
                val ysales = ArrayList<PieEntry>()
                val yganancias = ArrayList<PieEntry>()
                for (element in appListObject.results) {
                    yvalues.add(PieEntry(((element.download_count*100)/total_download).toFloat(), element.name, 0))
                    ysales.add(PieEntry(((element.sale_count*100)/total_ventas).toFloat(), element.name, 0))
                    yganancias.add(PieEntry((((element.sale_count * element.price)*100)/total_ganancias).toFloat(), element.name, 0))
                }

                val dataSet = PieDataSet(yvalues, "")
                val data = PieData(dataSet)

                val dataSetSales = PieDataSet(ysales, "")
                val dataSales = PieData(dataSetSales)

                val dataSetGanancias = PieDataSet(yganancias, "")
                val dataGanancias = PieData(dataSetGanancias)

                data.setValueFormatter(PercentFormatter())
                dataSales.setValueFormatter(PercentFormatter())
                dataGanancias.setValueFormatter(PercentFormatter())

                val description = Description()
                description.text = ""
                view_grafic_ganancias?.description = description
                view_grafic_ganancias?.description?.isEnabled = false
                view_grafic_ganancias?.isDrawHoleEnabled = false
                view_grafic_ganancias?.setUsePercentValues(true)
                view_grafic_ganancias?.setNoDataText("Sin Datos")
                dataSetGanancias.setColors(*ColorTemplate.VORDIPLOM_COLORS)
                dataGanancias.setValueTextSize(20f)
                dataGanancias.setValueTextColor(R.color.colorPrimary)
                view_grafic_ganancias?.data = dataGanancias
                view_grafic_ganancias?.invalidate()


                pieChart?.description = description
                pieChart?.description?.isEnabled = false
                pieChart?.isDrawHoleEnabled = false
                pieChart?.setUsePercentValues(true)
                pieChart?.setNoDataText("Sin Datos")
                dataSet.setColors(*ColorTemplate.VORDIPLOM_COLORS)
                data.setValueTextSize(20f)
                data.setValueTextColor(R.color.colorPrimary)
                pieChart?.data = data
                pieChart?.invalidate()


                view_grafic_sales?.description = description
                view_grafic_sales?.description?.isEnabled = false
                view_grafic_sales?.isDrawHoleEnabled = false
                view_grafic_sales?.setUsePercentValues(true)
                view_grafic_sales?.setNoDataText("Sin Datos")
                dataSetSales.setColors(*ColorTemplate.VORDIPLOM_COLORS)
                dataSales.setValueTextSize(20f)
                dataSales.setValueTextColor(R.color.colorPrimary)
                view_grafic_sales?.data = dataSales
                view_grafic_sales?.invalidate()

                Log.d(TAG, "loadApp: entro")
                if (appListObject.results.isEmpty()) {
                    Log.d(TAG, "loadApp: entro vacia")
                    multiStateViewAnalisis?.viewState = MultiStateView.ViewState.EMPTY
                } else {
                    Log.d(TAG, "loadApp: entro contenido")
                    multiStateViewAnalisis?.viewState = MultiStateView.ViewState.CONTENT
                }

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
                multiStateViewAnalisis?.viewState = MultiStateView.ViewState.ERROR
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

    fun roundOffDecimal(number: Double): Double? {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.FLOOR
        return df.format(number).toDouble()
    }


}