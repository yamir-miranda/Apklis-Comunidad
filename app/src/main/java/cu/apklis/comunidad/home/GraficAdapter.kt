package cu.apklis.comunidad.home

import com.robinhood.spark.SparkAdapter


class GraficAdapter(private val yData: ArrayList<Float>) : SparkAdapter() {
    override fun getCount(): Int {
        return yData.size
    }

    override fun getItem(index: Int): Any {
        return yData[index]
    }

    override fun getY(index: Int): Float {
        return yData[index]
    }

}