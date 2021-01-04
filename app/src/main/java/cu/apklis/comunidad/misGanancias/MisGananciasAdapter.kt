package cu.apklis.comunidad.misGanancias

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import cu.apklis.comunidad.R
import cu.apklis.comunidad.models.MisGananciasResponse
import cu.apklis.comunidad.models.User
import cu.apklis.comunidad.utils.DateUtils
import kotlinx.android.synthetic.main.adapter_item_mis_ganancias.view.*


class MisGananciasAdapter(
    private val context: Context, private val tokens: String, private val userObj: User
) :
    RecyclerView.Adapter<MisGananciasAdapter.BaseViewHolder<*>>() {
    private var data: ArrayList<MisGananciasResponse> = ArrayList()
    val TAG = "GananciasAdapter"

    companion object {
        private const val TYPE_VENTAS_ITEM = 0
    }

    abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T)
    }

    inner class ReportVentasViewHolder(itemView: View) :
        BaseViewHolder<MisGananciasResponse>(itemView) {

        override fun bind(item: MisGananciasResponse) {
            itemView.gananciasFecha.text =
                DateUtils().formatStringDate("yyyy-MM-dd", "dd/MM/yyyy", item.day)
            val text1 = item.ammount.toString() + " CUP"
            itemView.gananciasImporte.text = text1
            val text2 = item.sales.toString() + " Ventas"
            itemView.gananciasVentas.text = text2
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when (viewType) {
            TYPE_VENTAS_ITEM -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.adapter_item_mis_ganancias, parent, false)
                ReportVentasViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val element = data[position]
        when (holder) {
            is ReportVentasViewHolder -> holder.bind(element as MisGananciasResponse)
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is MisGananciasResponse -> TYPE_VENTAS_ITEM
            else -> throw IllegalArgumentException("Invalid type of data $position")
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setData(data: ArrayList<MisGananciasResponse>) {
        this.data = data
        notifyDataSetChanged()
    }

    fun cleaData() {
        data.clear()
    }

    private fun showToast(text: String, context: Context) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }
}
