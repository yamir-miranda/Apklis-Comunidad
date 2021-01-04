package cu.apklis.comunidad

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import cu.apklis.comunidad.utils.PaidCheked
import kotlinx.android.synthetic.main.fragment_paid_check.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PaidCheckFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PaidCheckFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_paid_check, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var option = 0
        val list0: MutableList<String> = ArrayList()
        val appId = "cu.ymv.infodevcuba"
        when {
            PaidCheked().Cheked(requireContext(), appId) == "code00" -> {
                option = 0
                list0.add("Debe instalar APKLIS en su dispositivo móvil")
                list0.add("Iniciar sección en APKLIS")
                list0.add("Realizar la compra en APKLIS")
                list0.add("Compra verificada")
            }
            PaidCheked().Cheked(requireContext(), appId) == "code02" -> {
                option = 1
                list0.add("APKLIS instalada")
                list0.add("Usted debe iniciar sección en APKLIS")
                list0.add("Realizar la compra en APKLIS")
                list0.add("Compra verificada")
            }
            PaidCheked().Cheked(requireContext(), appId) == "code03" -> {
                option = 2
                list0.add("APKLIS instalada")
                list0.add("Sección iniciada en APKLIS")
                list0.add("Realizar la compra en APKLIS")
                list0.add("Compra verificada")
            }
            PaidCheked().Cheked(requireContext(), appId) == "code04" -> {
                option = 4
                list0.add("APKLIS instalada")
                list0.add("Sección iniciada en APKLIS")
                list0.add("Compra realizada en APKLIS")
                list0.add("Compra verificada")
            }
        }

if (param1.equals("blanco")){
    step_view.setStepsViewIndicatorComplectingPosition(option)
        .reverseDraw(false)
        .setTextSize(20)
        .setStepViewTexts(list0)
        .setStepsViewIndicatorCompletedLineColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorPrimary
            )
        )
        .setStepsViewIndicatorUnCompletedLineColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorPrimary
            )
        )
        .setStepViewComplectedTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorNegro
            )
        )
        .setStepViewUnComplectedTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorNegro
            )
        )
        .setStepsViewIndicatorCompleteIcon(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_check
            )
        )
        .setStepsViewIndicatorDefaultIcon(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_check_un
            )
        )
        .setStepsViewIndicatorAttentionIcon(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_proccess
            )
        )
}else{
    step_view.setStepsViewIndicatorComplectingPosition(option)
        .reverseDraw(false)
        .setTextSize(20)
        .setStepViewTexts(list0)
        .setStepsViewIndicatorCompletedLineColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorPrimary
            )
        )
        .setStepsViewIndicatorUnCompletedLineColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorPrimary
            )
        )
        .setStepViewComplectedTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorBlanco
            )
        )
        .setStepViewUnComplectedTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorBlanco
            )
        )
        .setStepsViewIndicatorCompleteIcon(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_check
            )
        )
        .setStepsViewIndicatorDefaultIcon(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_check_un
            )
        )
        .setStepsViewIndicatorAttentionIcon(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.attention
            )
        )
}


    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PaidCheckFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PaidCheckFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ajustes_paid.setOnClickListener {
            addFragmentToFragmentBack(PrefrenceFragment())
        }
    }

    private fun addFragmentToFragmentBack(fragment: Fragment) {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.container, fragment)
        transaction?.addToBackStack(fragment.tag)
        transaction?.commit()
    }
}