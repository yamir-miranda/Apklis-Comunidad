package cu.ymv.infodevcuba.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.TimeoutError
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import cu.ymv.infodevcuba.R
import cu.ymv.infodevcuba.home.HomeFragment
import cu.ymv.infodevcuba.models.Tokens
import cu.ymv.infodevcuba.models.User
import cu.ymv.infodevcuba.utils.MyPreferences
import cu.ymv.infodevcuba.utils.NetworkManager
import cu.ymv.infodevcuba.webservices.VolleySingleton
import kotlinx.android.synthetic.main.fragment_login.*

/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {

    val TAG = "LoginFragment"
    val urlLogin: String = "https://api.apklis.cu/o/token/"
    private val networkManager: NetworkManager = NetworkManager()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btnLogin.setOnClickListener { loginUser() }
        usuario_input.doOnTextChanged { text, _, _, _ ->
            if (text != null) {
                if (text.isNotEmpty()) {
                    usuario.isHelperTextEnabled = false
                    usuario.isErrorEnabled = false
                }
            }
        }
        password_input.doOnTextChanged { text, _, _, _ ->
            if (text != null) {
                if (text.isNotEmpty()) {
                    password.isHelperTextEnabled = false
                    password.isErrorEnabled = false
                }
            }
        }
    }

    private fun loginUser() {
        var bandera = true
        mensajeAlert.visibility = View.GONE
        if (!networkManager.isNetworkAvailable(requireContext())) {
            showTost(resources.getString(R.string.sin_red), false)
            return
        }
        if (usuario_input.text.isEmpty()) {
            usuario.error = resources.getString(R.string.campo_obligatorio)
            bandera = false
        }
        if (password_input.text.isEmpty()) {
            password.error = resources.getString(R.string.campo_obligatorio)
            bandera = false
        }
        if (bandera) {
            changeLoadingVisibility(true)
            val stringRequest: StringRequest = object : StringRequest(Method.POST, urlLogin,
                Response.Listener { response ->
                    val tokensObject = Gson().fromJson(response, Tokens::class.java)
                    Log.d(TAG, "LoginUser: response token -> ${tokensObject.access_token}")

                    val urlUser: String =
                        "https://api.apklis.cu/v1/developer/" + usuario_input.text.toString() + "/"
                    val userRequest: StringRequest = object : StringRequest(Method.GET, urlUser,
                        Response.Listener { response ->
                            val userObject = Gson().fromJson(response, User::class.java)
                            userObject.tokens = tokensObject
                            val userJson = Gson().toJson(userObject)
                            Log.d(TAG, "loginUser: response user complete $userJson")
                            MyPreferences(requireContext()).userObject = userJson
                            addFragmentToFragment(HomeFragment())

                        }, Response.ErrorListener { error ->
                            when (error) {
                                is AuthFailureError -> {
                                    Log.d(TAG, "LoginUser: Invalid credentials given.")
                                    mensajeAlert.visibility = View.VISIBLE
                                    mensaje.text =
                                        resources.getString(R.string.credenciales_incorrectas)
                                }
                                is TimeoutError -> {
                                    Log.d(TAG, "LoginUser: tiempo de espera excedido")
                                    mensajeAlert.visibility = View.VISIBLE
                                    mensaje.text = resources.getString(R.string.time_out)
                                }
                                else -> {
                                    Log.d(TAG, "LoginUser: error desconocido y no procesado")
                                    mensajeAlert.visibility = View.VISIBLE
                                    mensaje.text = resources.getString(R.string.error_desconocido)
                                    error.printStackTrace()
                                }
                            }
                            changeLoadingVisibility(false)
                        }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            Log.d(
                                TAG,
                                "getHeaders: " + tokensObject.token_type + " " + tokensObject.access_token
                            )
                            headers["Authorization"] =
                                tokensObject.token_type + " " + tokensObject.access_token
                            return headers
                        }
                    }
                    VolleySingleton.getInstance(requireContext()).addToRequestQueue(userRequest)

                }, Response.ErrorListener { error ->
                    val networkResponse = error.networkResponse
                    if (networkResponse?.data != null && networkResponse.statusCode == 400) {
                        Log.d(TAG, "LoginUser: Invalid credentials given.")
                        mensajeAlert.visibility = View.VISIBLE
                        mensaje.text = resources.getString(R.string.credenciales_incorrectas)
                    } else if (error is TimeoutError) {
                        Log.d(TAG, "LoginUser: tiempo de espera excedido")
                        mensajeAlert.visibility = View.VISIBLE
                        mensaje.text = resources.getString(R.string.time_out)
                    } else {
                        Log.d(TAG, "LoginUser: error desconocido y no procesado")
                        mensajeAlert.visibility = View.VISIBLE
                        mensaje.text = resources.getString(R.string.error_desconocido)
                        error.printStackTrace()
                    }
                    changeLoadingVisibility(false)
                }) {
                override fun getParams(): MutableMap<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["grant_type"] = "password"
                    params["client_id"] = "7UMqh5p9CPPPEPi0s4Ksrv2vR5tBjBJH8XlaNMMB"
                    params["username"] = usuario_input.text.toString()
                    params["password"] = password_input.text.toString()
                    return params
                }

                override fun getBodyContentType(): String {
                    return "application/x-www-form-urlencoded"
                }
            }
            VolleySingleton.getInstance(requireContext()).addToRequestQueue(stringRequest)
        }
    }

    private fun addFragmentToFragment(fragment: Fragment) {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.container, fragment)
        transaction?.disallowAddToBackStack()
        transaction?.commit()
    }

    private fun showTost(text: String, corto: Boolean) {
        if (corto) {
            Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()
        }
    }

    private fun changeLoadingVisibility(loading: Boolean) {
        if (loading) {
            btnLogin.visibility = View.GONE
            btnLoginLoading.visibility = View.VISIBLE
        } else {
            btnLoginLoading.visibility = View.GONE
            btnLogin.visibility = View.VISIBLE
        }
    }
}