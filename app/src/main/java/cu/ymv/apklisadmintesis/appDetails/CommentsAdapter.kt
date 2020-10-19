package cu.ymv.apklisadmintesis.appDetails

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import cu.ymv.apklisadmintesis.MainActivity
import cu.ymv.apklisadmintesis.R
import cu.ymv.apklisadmintesis.login.LoginFragment
import cu.ymv.apklisadmintesis.models.Comment
import cu.ymv.apklisadmintesis.models.CommentReply
import cu.ymv.apklisadmintesis.models.CommentReplyEnd
import cu.ymv.apklisadmintesis.models.CommentWithReply
import cu.ymv.apklisadmintesis.utils.DateUtils
import cu.ymv.apklisadmintesis.utils.MyPreferences
import cu.ymv.apklisadmintesis.webservices.VolleySingleton
import kotlinx.android.synthetic.main.adapter_item_coment.view.*
import kotlinx.android.synthetic.main.adapter_item_coment_reply.view.*
import kotlinx.android.synthetic.main.adapter_item_coment_reply.view.full_name_comment_reply
import kotlinx.android.synthetic.main.adapter_item_coment_reply.view.userAvatarComentReply
import kotlinx.android.synthetic.main.adapter_item_coment_reply_end.view.*
import kotlinx.android.synthetic.main.adapter_item_coment_with_reply.view.*

class CommentsAdapter(
    private val context: Context, private val tokens: String
) :
    RecyclerView.Adapter<CommentsAdapter.BaseViewHolder<*>>() {
    private var data: ArrayList<Any> = ArrayList()
    val TAG = "CommentsFragment"

    companion object {
        private const val TYPE_COMENTS = 0
        private const val TYPE_COMENTS_WITH_REPLY = 1
        private const val TYPE_COMENTS_REPLY = 2
        private const val TYPE_COMENTS_REPLY_END = 3
    }

    abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T)
    }

    inner class CommentViewHolder(itemView: View) : BaseViewHolder<Comment>(itemView) {

        override fun bind(item: Comment) {
            val fullName = item.user.first_name + " " + item.user.last_name
            Log.d("Adapter", "bind: $fullName")
            itemView.full_name_comment.text = fullName
            itemView.commentText.text = item.comment
            itemView.commentDate.text = DateUtils().getTimeAgo(
                DateUtils().loadDateofString(
                    "yyyy-MM-dd'T'HH:mm:ss",
                    item.published
                ), itemView.context
            )
            itemView.ratingBarComment.rating = item.rating.toFloat()
            if (item.user.avatar != null) {
                Glide.with(itemView.context)
                    .load(item.user.avatar)
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .into(itemView.userAvatarComent)
            }
            itemView.commentOption.setOnClickListener {
                val popupMenu = PopupMenu(itemView.context, itemView.commentOption)
                popupMenu.inflate(R.menu.comment_option)
                popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->
                    when (item!!.itemId) {
                        R.id.reponder -> {
                            showTost("Presiono Responder", itemView.context)
                        }
                    }
                    true
                })
                popupMenu.show()
            }
        }
    }

    inner class CommentWithReplyViewHolder(itemView: View) :
        BaseViewHolder<CommentWithReply>(itemView) {

        override fun bind(item: CommentWithReply) {
            val fullName = item.user.first_name + " " + item.user.last_name
            itemView.full_name_comment_with_reply.text = fullName
            itemView.commentTextWR.text = item.comment
            itemView.ratingBarCommentWith.rating = item.rating.toFloat()
            itemView.commentDateWR.text = DateUtils().getTimeAgo(
                DateUtils().loadDateofString(
                    "yyyy-MM-dd'T'HH:mm:ss",
                    item.published
                ), itemView.context
            )
            if (item.user.avatar != null) {
                Glide.with(itemView.context)
                    .load(item.user.avatar)
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .into(itemView.userAvatarComentWithReply)
            }
            itemView.commentOptionWR.setOnClickListener {
                val popupMenu = PopupMenu(itemView.context, itemView.commentOptionWR)
                popupMenu.inflate(R.menu.comment_option)
                popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->
                    when (item!!.itemId) {
                        R.id.reponder -> {
                            showTost("Presiono Responder", itemView.context)
                        }
                    }
                    true
                })
                popupMenu.show()
            }
        }
    }

    inner class CommentReplyViewHolder(itemView: View) : BaseViewHolder<CommentReply>(itemView) {

        override fun bind(item: CommentReply) {
            val fullName = item.user.first_name + " " + item.user.last_name
            itemView.full_name_comment_reply.text = fullName
            itemView.commentTextR.text = item.comment
            itemView.commentTextR.text = DateUtils().getTimeAgo(
                DateUtils().loadDateofString(
                    "yyyy-MM-dd'T'HH:mm:ss",
                    item.published
                ), itemView.context
            )
            if (item.user.avatar != null) {
                Glide.with(itemView.context)
                    .load(item.user.avatar)
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .into(itemView.userAvatarComentReply)
            }
            itemView.commentOptionR.setOnClickListener {
                val popupMenu = PopupMenu(itemView.context, itemView.commentOptionR)
                popupMenu.inflate(R.menu.comment_option_admin)
                popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { itemm: MenuItem? ->
                    when (itemm!!.itemId) {
                        R.id.delete -> {
                            val builder = AlertDialog.Builder(itemView.context)
                            builder.setTitle("Eliminar Comentario")
                            builder.setMessage("¿Está seguro que desea eliminar el comentario?, Los cambios serán irreversibles")
                            builder.setPositiveButton(
                                "Eliminar"
                            ) { dialog, id ->
                                deleteComment(item.id.toString(), itemView.context, item)
                            }
                            builder.setNegativeButton(
                                "Cancelar"
                            ) { dialog, id ->
                                dialog.dismiss()
                            }
                            builder.show()
                        }
                    }
                    true
                })
                popupMenu.show()
            }
        }
    }

    inner class CommentReplyEndViewHolder(itemView: View) :
        BaseViewHolder<CommentReplyEnd>(itemView) {

        override fun bind(item: CommentReplyEnd) {
            val fullName = item.user.first_name + " " + item.user.last_name
            itemView.full_name_comment_reply.text = fullName
            itemView.commentTextRE.text = item.comment
            itemView.commentDateRE.text = DateUtils().getTimeAgo(
                DateUtils().loadDateofString(
                    "yyyy-MM-dd'T'HH:mm:ss",
                    item.published
                ), itemView.context
            )
            if (item.user.avatar != null) {
                Glide.with(itemView.context)
                    .load(item.user.avatar)
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .into(itemView.userAvatarComentReply)
            }

            itemView.commentOptionRE.setOnClickListener {
                val popupMenu = PopupMenu(itemView.context, itemView.commentOptionRE)
                popupMenu.inflate(R.menu.comment_option_admin)
                popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { itemm: MenuItem? ->
                    when (itemm!!.itemId) {
                        R.id.delete -> {
                            val builder = AlertDialog.Builder(itemView.context)
                            builder.setTitle("Eliminar Comentario")
                            builder.setMessage("¿Está seguro que desea eliminar el comentario?, Los cambios serán irreversibles")
                            builder.setPositiveButton(
                                "Eliminar"
                            ) { dialog, id ->
                                deleteComment(item.id.toString(), itemView.context, item)
                            }
                            builder.setNegativeButton(
                                "Cancelar"
                            ) { dialog, id ->
                                dialog.dismiss()
                            }
                            builder.show()
                        }
                    }
                    true
                })
                popupMenu.show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when (viewType) {
            TYPE_COMENTS -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.adapter_item_coment, parent, false)
                CommentViewHolder(view)
            }
            TYPE_COMENTS_WITH_REPLY -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.adapter_item_coment_with_reply, parent, false)
                CommentWithReplyViewHolder(view)
            }
            TYPE_COMENTS_REPLY -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.adapter_item_coment_reply, parent, false)
                CommentReplyViewHolder(view)
            }
            TYPE_COMENTS_REPLY_END -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.adapter_item_coment_reply_end, parent, false)
                CommentReplyEndViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val element = data[position]
        when (holder) {
            is CommentViewHolder -> holder.bind(element as Comment)
            is CommentWithReplyViewHolder -> holder.bind(element as CommentWithReply)
            is CommentReplyViewHolder -> holder.bind(element as CommentReply)
            is CommentReplyEndViewHolder -> holder.bind(element as CommentReplyEnd)
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is Comment -> TYPE_COMENTS
            is CommentWithReply -> TYPE_COMENTS_WITH_REPLY
            is CommentReply -> TYPE_COMENTS_REPLY
            is CommentReplyEnd -> TYPE_COMENTS_REPLY_END
            else -> throw IllegalArgumentException("Invalid type of data $position")
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setData(data: ArrayList<Any>) {
        this.data = data
        notifyDataSetChanged()
    }

    private fun showTost(text: String, context: Context) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    private fun deleteComment(id: String, context: Context, obj: Any) {
        val urlCommentsApp = "https://api.apklis.cu/v1/review/$id/"
        val userRequest: StringRequest = object : StringRequest(Method.DELETE, urlCommentsApp,
            Response.Listener { response ->
                Log.d("Adapter", "deleteComment: $response")
                val pos = data.indexOf(obj)
                data.remove(obj)
                notifyItemRemoved(pos)
                showTost("Comentario eliminado con exito", context)
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
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = tokens
                return headers
            }

            override fun getRetryPolicy(): RetryPolicy {
                return DefaultRetryPolicy(
                    1000 * 25, 0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
            }

        }
        VolleySingleton.getInstance(context).addToRequestQueue(userRequest)
    }

}
