package cu.ymv.apklisadmintesis.appDetails

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import com.google.gson.Gson
import cu.ymv.apklisadmintesis.MainActivity
import cu.ymv.apklisadmintesis.R
import cu.ymv.apklisadmintesis.login.LoginFragment
import cu.ymv.apklisadmintesis.models.*
import cu.ymv.apklisadmintesis.utils.DateUtils
import cu.ymv.apklisadmintesis.utils.MyPreferences
import cu.ymv.apklisadmintesis.webservices.VolleySingleton
import kotlinx.android.synthetic.main.adapter_item_coment.view.*
import kotlinx.android.synthetic.main.adapter_item_coment_reply.view.*
import kotlinx.android.synthetic.main.adapter_item_coment_reply.view.full_name_comment_reply
import kotlinx.android.synthetic.main.adapter_item_coment_reply.view.userAvatarComentReply
import kotlinx.android.synthetic.main.adapter_item_coment_reply_end.view.*
import kotlinx.android.synthetic.main.adapter_item_coment_response.view.*
import kotlinx.android.synthetic.main.adapter_item_coment_with_reply.view.*
import org.json.JSONObject


class CommentsAdapter(
    private val context: Context, private val tokens: String, private val userObj: User
) :
    RecyclerView.Adapter<CommentsAdapter.BaseViewHolder<*>>() {
    private var data: ArrayList<Any> = ArrayList()
    val TAG = "CommentsFragment"

    companion object {
        private const val TYPE_COMENTS = 0
        private const val TYPE_COMENTS_WITH_REPLY = 1
        private const val TYPE_COMENTS_REPLY = 2
        private const val TYPE_COMENTS_REPLY_END = 3
        private const val TYPE_COMENTS_RESP = 4
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
                    .into(itemView.userAvatarComent)
            }
            itemView.commentOption.setOnClickListener {
                val popupMenu = PopupMenu(itemView.context, itemView.commentOption)
                popupMenu.inflate(R.menu.comment_option)
                popupMenu.setOnMenuItemClickListener { itemm: MenuItem? ->
                    when (itemm!!.itemId) {
                        R.id.reponder -> {
                            val pos = data.indexOf(item)
                            val commentChange = data[pos] as Comment
                            data[pos] = CommentWithReply(
                                commentChange.id,
                                commentChange.user,
                                commentChange.comment,
                                commentChange.rating,
                                commentChange.published,
                                commentChange.public
                            )
                            notifyItemChanged(pos)
                            data.add(pos + 1, CommentRes(item.id))
                            notifyItemInserted(pos + 1)
                        }
                    }
                    true
                }
                popupMenu.show()
            }
        }
    }

    inner class CommentResViewHolder(itemView: View) : BaseViewHolder<CommentRes>(itemView) {

        override fun bind(item: CommentRes) {
            itemView.progressBarComment.visibility = View.GONE
            itemView.send_comment.visibility = View.VISIBLE
            if (userObj.avatar != null) {
                Glide.with(itemView.context)
                    .load(userObj.avatar)
                    .into(itemView.userAvatarComentResponse)
            }
            itemView.commentOptionResp.setOnClickListener {
                val popupMenu = PopupMenu(itemView.context, itemView.commentOptionResp)
                popupMenu.inflate(R.menu.comment_option_resp)
                popupMenu.setOnMenuItemClickListener { itemm: MenuItem? ->
                    when (itemm!!.itemId) {
                        R.id.cancel -> {
                            val pos = data.indexOf(item)
                            data.remove(item)
                            notifyItemRemoved(pos)
                            if (data[pos - 1] is CommentReply) {
                                val posAnterior = pos - 1
                                val commentChange: CommentReply = data[posAnterior] as CommentReply
                                data[posAnterior] = CommentReplyEnd(
                                    commentChange.id,
                                    commentChange.comment,
                                    commentChange.published,
                                    commentChange.user,
                                    commentChange.public
                                )
                                notifyItemChanged(posAnterior)
                            } else if (data[pos - 1] is CommentWithReply) {
                                val posAnterior = pos - 1
                                val commentChange: CommentWithReply =
                                    data[posAnterior] as CommentWithReply
                                data[posAnterior] = Comment(
                                    commentChange.id,
                                    commentChange.user,
                                    commentChange.comment,
                                    commentChange.rating,
                                    commentChange.published,
                                    commentChange.public
                                )
                                notifyItemChanged(posAnterior)
                            }
                        }
                    }
                    true
                }
                popupMenu.show()
            }

            itemView.comment_response.clearComposingText()
            itemView.send_comment.setOnClickListener {
                if (itemView.comment_response.text.isEmpty()) {
                    itemView.comment_response.error = "comentario requerido"

                } else {
                    val url = "https://api.apklis.cu/v1/review/reply/"
                    itemView.send_comment.visibility = View.GONE
                    itemView.progressBarComment.visibility = View.VISIBLE
                    val stringRequest: StringRequest = object : StringRequest(Method.POST, url,
                        Response.Listener { response ->
                            Log.d(TAG, "bind: $response")
                            val byte: ByteArray = response.toByteArray(charset("ISO-8859-1"))
                            val commentObject =
                                Gson().fromJson(
                                    String(byte, charset("UTF-8")),
                                    CommentsRepliesReponse::class.java
                                )
                            val pos = data.indexOf(item)
                            data[pos] = CommentReplyEnd(
                                commentObject.id,
                                commentObject.comment,
                                commentObject.published,
                                commentObject.user,
                                commentObject.public
                            )
                            /*itemView.progressBarComment.visibility = View.GONE
                            itemView.send_comment.visibility = View.VISIBLE*/
                            notifyItemChanged(pos)
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
                            itemView.progressBarComment.visibility = View.GONE
                            itemView.send_comment.visibility = View.VISIBLE
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

                        override fun getBodyContentType(): String {
                            return "application/json; charset=utf-8"
                        }

                        override fun getBody(): ByteArray {
                            val jsonBody = JSONObject()
                            jsonBody.put("comment", itemView.comment_response.text.toString())
                            jsonBody.put("review", item.id)
                            return jsonBody.toString().toByteArray(charset("utf-8"))
                        }
                    }
                    VolleySingleton.getInstance(context).addToRequestQueue(stringRequest)
                }
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
                    .into(itemView.userAvatarComentWithReply)
            }
            itemView.commentOptionWR.setOnClickListener {
                val popupMenu = PopupMenu(itemView.context, itemView.commentOptionWR)
                popupMenu.inflate(R.menu.comment_option)
                popupMenu.setOnMenuItemClickListener { itemm: MenuItem? ->
                    when (itemm!!.itemId) {
                        R.id.reponder -> {
                            var pos = data.indexOf(item) + 1
                            var accion: Boolean
                            while (true) {
                                if (data[pos] is CommentReplyEnd || data[pos] is CommentRes) {
                                    if (data[pos] is CommentRes) {
                                        accion = false
                                        break
                                    } else {
                                        accion = true
                                        break
                                    }
                                }
                                pos++
                            }
                            if (accion) {
                                val commentChange = data[pos] as CommentReplyEnd
                                data[pos] = CommentReply(
                                    commentChange.id,
                                    commentChange.comment,
                                    commentChange.published,
                                    commentChange.user,
                                    commentChange.public
                                )
                                notifyItemChanged(pos)
                                data.add(pos + 1, CommentRes(item.id))
                                notifyItemInserted(pos + 1)
                            }
                        }
                    }
                    true
                }
                popupMenu.show()
            }
        }
    }

    inner class CommentReplyViewHolder(itemView: View) : BaseViewHolder<CommentReply>(itemView) {

        override fun bind(item: CommentReply) {
            itemView.progressBarDeleteR.visibility = View.GONE
            val fullName = item.user.first_name + " " + item.user.last_name
            itemView.full_name_comment_reply.text = fullName
            itemView.commentDateR.text = DateUtils().getTimeAgo(
                DateUtils().loadDateofString(
                    "yyyy-MM-dd'T'HH:mm:ss",
                    item.published
                ), itemView.context
            )
            itemView.commentTextR.text = item.comment
            if (item.user.avatar != null) {
                Glide.with(itemView.context)
                    .load(item.user.avatar)
                    .into(itemView.userAvatarComentReply)
            }
            itemView.commentOptionR.setOnClickListener {
                val popupMenu = PopupMenu(itemView.context, itemView.commentOptionR)
                popupMenu.inflate(R.menu.comment_option_admin)
                popupMenu.setOnMenuItemClickListener { itemm: MenuItem? ->
                    when (itemm!!.itemId) {
                        R.id.delete -> {
                            val builder = AlertDialog.Builder(itemView.context)
                            builder.setTitle("Eliminar Comentario")
                            builder.setMessage("¿Está seguro que desea eliminar el comentario?, Los cambios serán irreversibles")
                            builder.setPositiveButton(
                                "Eliminar"
                            ) { dialog, id ->
                                deleteComment(item.id.toString(), itemView.context, item, itemView.progressBarDeleteR)
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
                }
                popupMenu.show()
            }
        }
    }

    inner class CommentReplyEndViewHolder(itemView: View) :
        BaseViewHolder<CommentReplyEnd>(itemView) {

        override fun bind(item: CommentReplyEnd) {
            itemView.progressBarDeleteRE.visibility = View.GONE
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
                                deleteComment(item.id.toString(), itemView.context, item, itemView.progressBarDeleteRE)
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
            TYPE_COMENTS_RESP -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.adapter_item_coment_response, parent, false)
                CommentResViewHolder(view)
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
            is CommentResViewHolder -> holder.bind(element as CommentRes)
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is Comment -> TYPE_COMENTS
            is CommentWithReply -> TYPE_COMENTS_WITH_REPLY
            is CommentReply -> TYPE_COMENTS_REPLY
            is CommentReplyEnd -> TYPE_COMENTS_REPLY_END
            is CommentRes -> TYPE_COMENTS_RESP
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

    fun cleaData(){
        data.clear()
    }

    private fun showToast(text: String, context: Context) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    private fun deleteComment(id: String, context: Context, obj: Any, progress: ProgressBar) {
        val urlCommentsApp = "https://api.apklis.cu/v1/review/$id/"
        progress.visibility = View.VISIBLE
        val userRequest: StringRequest = object : StringRequest(Method.DELETE, urlCommentsApp,
            Response.Listener { response ->
                Log.d("Adapter", "deleteComment: $response")
                val pos = data.indexOf(obj)
                data.remove(obj)
                notifyItemRemoved(pos)
                if (obj is CommentReplyEnd) {
                    if (data[pos - 1] is CommentReply) {
                        val posAnterior = pos - 1
                        val commentChange: CommentReply = data[posAnterior] as CommentReply
                        data[posAnterior] = CommentReplyEnd(
                            commentChange.id,
                            commentChange.comment,
                            commentChange.published,
                            commentChange.user,
                            commentChange.public
                        )
                        notifyItemChanged(posAnterior)
                    } else if (data[pos - 1] is CommentWithReply) {
                        val posAnterior = pos - 1
                        val commentChange: CommentWithReply = data[posAnterior] as CommentWithReply
                        data[posAnterior] = Comment(
                            commentChange.id,
                            commentChange.user,
                            commentChange.comment,
                            commentChange.rating,
                            commentChange.published,
                            commentChange.public
                        )
                        notifyItemChanged(posAnterior)
                    }
                }

                showToast("Comentario eliminado con exito", context)
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
                progress.visibility = View.GONE
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
