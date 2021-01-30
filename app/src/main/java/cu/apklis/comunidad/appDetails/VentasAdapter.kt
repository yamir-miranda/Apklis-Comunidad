package cu.apklis.comunidad.appDetails

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import cu.apklis.comunidad.R
import cu.apklis.comunidad.models.ReportVentas
import cu.apklis.comunidad.models.User
import cu.apklis.comunidad.utils.DateUtils
import kotlinx.android.synthetic.main.adapter_item_ventas.view.*
import java.math.RoundingMode
import java.text.DecimalFormat


class VentasAdapter(
    private val context: Context, private val tokens: String, private val userObj: User
) :
    RecyclerView.Adapter<VentasAdapter.BaseViewHolder<*>>() {
    private var data: ArrayList<ReportVentas> = ArrayList()
    val TAG = "VentasAdapter"

    companion object {
        private const val TYPE_VENTAS_ITEM = 0
    }

    abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T)
    }

    inner class ReportVentasViewHolder(itemView: View) : BaseViewHolder<ReportVentas>(itemView) {

        override fun bind(item: ReportVentas) {
            itemView.ventasNumber.text = item.phone_number
            itemView.ventasDate.text = DateUtils().getTimeAgo(
                DateUtils().loadDateofString(
                    "yyyy-MM-dd'T'HH:mm:ss",
                    item.date
                ), itemView.context
            )
            when (item.bank) {
                1 -> {
                    Glide.with(itemView.context)
                        .load(R.drawable.banco_bpa)
                        .into(itemView.ventasImagen)
                }
                2 -> {
                    Glide.with(itemView.context)
                        .load(R.drawable.banco_bandec)
                        .into(itemView.ventasImagen)
                }
                3 -> {
                    Glide.with(itemView.context)
                        .load(R.drawable.banco_metropolitano)
                        .into(itemView.ventasImagen)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when (viewType) {
            TYPE_VENTAS_ITEM -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.adapter_item_ventas, parent, false)
                ReportVentasViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val element = data[position]
        when (holder) {
            is ReportVentasViewHolder -> holder.bind(element as ReportVentas)
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is ReportVentas -> TYPE_VENTAS_ITEM
            else -> throw IllegalArgumentException("Invalid type of data $position")
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setData(data: ArrayList<ReportVentas>) {
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
