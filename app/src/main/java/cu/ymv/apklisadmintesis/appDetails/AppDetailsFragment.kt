package cu.ymv.apklisadmintesis.appDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import cu.ymv.apklisadmintesis.R
import kotlinx.android.synthetic.main.fragment_app_details.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AppDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AppDetailsFragment : Fragment() {
    // TODO: Rename and change types of parameters
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
        return inflater.inflate(R.layout.fragment_app_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBarChart()
    }

    private fun setBarChart() {
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(16500f, 0))
        entries.add(BarEntry(15000f, 1))
        entries.add(BarEntry(15655f, 2))

        val barDataSet = BarDataSet(entries, "Aplicaciones")
        val labels = ArrayList<String>()
        labels.add("Apklis Admin")
        labels.add("CubaDebate")
        labels.add("CuBanco")
        val data = BarData(labels, barDataSet)
        barChart.data = data
        barDataSet.color = resources.getColor(R.color.colorPrimary)
        barChart.animateY(2000)

        val entriesPie = ArrayList<Entry>()
        entries.add(BarEntry(10f, 0))
        entries.add(BarEntry(20f, 1))
        entries.add(BarEntry(5f, 2))

        val pieDataSet = PieDataSet(entriesPie, "Descargas")
        val labelsPie = ArrayList<String>()
        labelsPie.add("Apklis Admin")
        labelsPie.add("CubaDebate")
        labelsPie.add("CuBanco")
        val dataPie = PieData(labelsPie, pieDataSet)
        piechart.data = dataPie
        piechart.animateXY(2000, 2000)

        horizontalBarChart.setDrawBarShadow(false)
        horizontalBarChart.setDrawValueAboveBar(false)
        horizontalBarChart.setDescription("")
        horizontalBarChart.setPinchZoom(false)
        horizontalBarChart.setDrawGridBackground(false)
        horizontalBarChart.animateY(2500)
        horizontalBarChart.legend.isEnabled = true
        horizontalBarChart.setTouchEnabled(false)
        horizontalBarChart.setMaxVisibleValueCount(60)

        val xl: XAxis = horizontalBarChart.xAxis
        xl.position = XAxisPosition.BOTTOM
        xl.setDrawAxisLine(true)
        xl.setDrawGridLines(false)
        xl.mAxisMaximum = 6000F
        xl.mAxisMinimum = 0F
        xl.textColor = resources.getColor(R.color.colorBlanco)

        val yl: YAxis = horizontalBarChart.axisLeft
        yl.setDrawAxisLine(true)
        yl.setDrawGridLines(true)

        val yr: YAxis = horizontalBarChart.axisRight
        yr.setDrawAxisLine(true)
        yr.setDrawGridLines(false)


        val entriesHorizontal = ArrayList<BarEntry>()
        entriesHorizontal.add(BarEntry(5100f, 0))
        entriesHorizontal.add(BarEntry(309f, 1))
        entriesHorizontal.add(BarEntry(4978f, 2))
        entriesHorizontal.add(BarEntry(3098f, 3))
        entriesHorizontal.add(BarEntry(3257f, 4))
        entriesHorizontal.add(BarEntry(1128f, 5))
        entriesHorizontal.add(BarEntry(5100f, 6))
        entriesHorizontal.add(BarEntry(309f, 7))
        entriesHorizontal.add(BarEntry(4978f, 8))
        entriesHorizontal.add(BarEntry(3098f, 9))
        entriesHorizontal.add(BarEntry(3257f, 10))
        entriesHorizontal.add(BarEntry(1128f, 11))
        entriesHorizontal.add(BarEntry(5100f, 12))
        entriesHorizontal.add(BarEntry(309f, 13))
        entriesHorizontal.add(BarEntry(4978f, 14))
        entriesHorizontal.add(BarEntry(3098f, 15))
        entriesHorizontal.add(BarEntry(3257f, 16))
        entriesHorizontal.add(BarEntry(1128f, 17))
        entriesHorizontal.add(BarEntry(5100f, 18))
        entriesHorizontal.add(BarEntry(309f, 19))
        entriesHorizontal.add(BarEntry(4978f, 20))
        entriesHorizontal.add(BarEntry(3098f, 21))
        entriesHorizontal.add(BarEntry(3257f, 22))
        entriesHorizontal.add(BarEntry(1128f, 23))


        val datasetHorizontal = BarDataSet(entriesHorizontal, "Descargas")
        datasetHorizontal.color = resources.getColor(R.color.colorPrimary)
        datasetHorizontal.setDrawValues(false)

        val dataSets: ArrayList<IBarDataSet> = ArrayList<IBarDataSet>()
        dataSets.add(datasetHorizontal)

        val labelsPieHorizontal = ArrayList<String>()
        labelsPieHorizontal.add("1.0.0")
        labelsPieHorizontal.add("1.0.1")
        labelsPieHorizontal.add("1.0.2")
        labelsPieHorizontal.add("1.0.3")
        labelsPieHorizontal.add("1.0.4")
        labelsPieHorizontal.add("1.0.5")
        labelsPieHorizontal.add("1.0.0")
        labelsPieHorizontal.add("1.0.1")
        labelsPieHorizontal.add("1.0.2")
        labelsPieHorizontal.add("1.0.3")
        labelsPieHorizontal.add("1.0.4")
        labelsPieHorizontal.add("1.0.5")
        labelsPieHorizontal.add("1.0.0")
        labelsPieHorizontal.add("1.0.1")
        labelsPieHorizontal.add("1.0.2")
        labelsPieHorizontal.add("1.0.3")
        labelsPieHorizontal.add("1.0.4")
        labelsPieHorizontal.add("1.0.5")
        labelsPieHorizontal.add("1.0.0")
        labelsPieHorizontal.add("1.0.1")
        labelsPieHorizontal.add("1.0.2")
        labelsPieHorizontal.add("1.0.3")
        labelsPieHorizontal.add("1.0.4")
        labelsPieHorizontal.add("1.0.5")
        val barData = BarData(labelsPieHorizontal, dataSets)
        barData.setValueTextSize(10f)
        barData.isHighlightEnabled = true
        horizontalBarChart.data = barData
        horizontalBarChart.isAutoScaleMinMaxEnabled = true
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AppDetailsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AppDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}