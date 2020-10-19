package cu.ymv.apklisadmintesis.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import cu.ymv.apklisadmintesis.R
import cu.ymv.apklisadmintesis.models.App
import kotlinx.android.synthetic.main.adapter_item_app_list.view.*

class ItemAdapter(
    private val context: Context, private val appClickListener: AppClickListener
) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
    private var appList: List<App> = ArrayList<App>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_item_app_list, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(appList[position])
        holder.itemView.setOnClickListener {
            appClickListener.onAppClickListener(appList[position])
        }
    }

    override fun getItemCount(): Int {
        return appList.size
    }

    fun setAppList(appList: List<App>) {
        this.appList = appList
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(app: App) {
            Glide.with(itemView.context)
                .load(app.last_release.icon)
                .placeholder(R.drawable.avatar)
                .error(R.drawable.avatar)
                .into(itemView.appIcon)
            itemView.nombre.text = app.name
            itemView.cantDownload.text = app.download_count.toString()
            val reviews: String = "(" + app.reviews_count + ")"
            itemView.reviews_count.text = reviews
            itemView.ratingBar.rating = app.rating
        }
    }

    interface AppClickListener {
        fun onAppClickListener(data: App)
    }
}