package cu.ymv.apklisadmintesis.appDetails

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.kennyc.view.MultiStateView
import cu.ymv.apklisadmintesis.R
import cu.ymv.apklisadmintesis.login.LoginFragment
import cu.ymv.apklisadmintesis.models.*
import cu.ymv.apklisadmintesis.utils.MyPreferences
import cu.ymv.apklisadmintesis.webservices.VolleySingleton
import kotlinx.android.synthetic.main.fragment_comments.*
import kotlinx.android.synthetic.main.fragment_comments.view.*
import kotlinx.android.synthetic.main.fragment_home.*

// TODO: Rename parameter arguments, choose names that match
private const val ARG_APP_ID = "param_app_id"

/**
 * A simple [Fragment] subclass.
 * Use the [CommentsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CommentsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var app_id: String? = null
    var adapterComments: CommentsAdapter? = null
    val TAG = "CommentsFragment"
    var userObject: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            app_id = it.getString(ARG_APP_ID)
        }
        if (MyPreferences(requireContext()).userObject == "") {
            addFragmentToFragment(LoginFragment())
        } else {
            userObject =
                Gson().fromJson(MyPreferences(requireContext()).userObject, User::class.java)
        }
        adapterComments = CommentsAdapter(
            requireContext(),
            userObject!!.tokens.token_type + " " + userObject!!.tokens.access_token
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_comments, container, false)
        root.recycler_comments.layoutManager = LinearLayoutManager(requireContext())
        root.recycler_comments.adapter = adapterComments
        if (adapterComments!!.itemCount > 0) {
            root.multiStateViewComments.animateLayoutChanges = false
            root.multiStateViewComments.viewState = MultiStateView.ViewState.CONTENT
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loadComments(false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param app_id Parameter 1.
         * @return A new instance of fragment CommentsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(app_id: String) =
            CommentsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_APP_ID, app_id)
                }
            }
    }

    private fun loadComments(reload: Boolean) {
        if (adapterComments!!.itemCount == 0 || reload) {
            Log.d(TAG, "loadComments: entro a la carga")
            multiStateViewComments.animateLayoutChanges = true
            if (!reload) {
                multiStateViewComments.viewState = MultiStateView.ViewState.LOADING
            }
            val urlCommentsApp: String =
                "https://api.apklis.cu/v1/review/?application=$app_id&limit=10000&ordering=-published"
            val userRequest: StringRequest = object : StringRequest(Method.GET, urlCommentsApp,
                Response.Listener { response ->
                    val byte: ByteArray = response.toByteArray(charset("ISO-8859-1"))
                    val commentsListObject = Gson().fromJson(
                        String(byte, charset("UTF-8")),
                        CommentsResponse::class.java
                    )
                    val commentsData: ArrayList<Any> = ArrayList()
                    for (comments in commentsListObject.results) {
                        if (comments.replies.isEmpty()) {
                            commentsData.add(
                                Comment(
                                    comments.id,
                                    comments.user,
                                    comments.comment,
                                    comments.rating,
                                    comments.published,
                                    comments.public
                                )
                            )
                        } else {
                            commentsData.add(
                                CommentWithReply(
                                    comments.id,
                                    comments.user,
                                    comments.comment,
                                    comments.rating,
                                    comments.published,
                                    comments.public
                                )
                            )
                            val idlatest: Int = comments.replies[comments.replies.size - 1].id
                            for (reply in comments.replies) {
                                if (reply.id == idlatest) {
                                    commentsData.add(
                                        CommentReplyEnd(
                                            reply.id,
                                            reply.comment,
                                            reply.published,
                                            reply.user,
                                            reply.public
                                        )
                                    )
                                } else {
                                    commentsData.add(
                                        CommentReply(
                                            reply.id,
                                            reply.comment,
                                            reply.published,
                                            reply.user,
                                            reply.public
                                        )
                                    )
                                }

                            }
                        }
                    }
                    if (commentsData.isEmpty()) {
                        multiStateViewComments.viewState = MultiStateView.ViewState.EMPTY
                    } else {
                        adapterComments!!.setData(commentsData)
                        multiStateViewComments.viewState = MultiStateView.ViewState.CONTENT
                    }


                    if (swipeRefresh != null) {
                        swipeRefresh.isRefreshing = false
                    }
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
                    multiStateViewComments.viewState = MultiStateView.ViewState.ERROR
                    if (swipeRefresh != null) {
                        swipeRefresh.isRefreshing = false
                    }
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] =
                        userObject!!.tokens.token_type + " " + userObject!!.tokens.access_token
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
    }

    private fun addFragmentToFragment(fragment: Fragment) {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.container, fragment)
        transaction?.disallowAddToBackStack()
        transaction?.commit()
    }
}