package cu.ymv.infodevcuba.appDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import cu.ymv.infodevcuba.R
import kotlinx.android.synthetic.main.fragment_tab_details_app.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"
private const val ARG_PARAM4 = "param4"

/**
 * A simple [Fragment] subclass.
 * Use the [TabDetailsAppFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TabDetailsAppFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var param3: String? = null
    private var price: Int? = 0
    private val titles =
        arrayOf("Detalles", "Ventas", "Comentarios", "Descargas", "Versiones")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            param3 = it.getString(ARG_PARAM3)
            price = it.getInt(ARG_PARAM4)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab_details_app, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(requireContext())
            .load(param3)
            .into(iconAppDetail)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        viewPager.adapter = ViewPagerFragmentAdapter(
            parentFragmentManager,
            requireActivity().lifecycle,
            param1,
            param2,
            price
        )
        TabLayoutMediator(tabLayout, viewPager,
            TabConfigurationStrategy { tab, position -> tab.text = titles.get(position) }).attach()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @param param3 Parameter 3.
         * @param param4 Parameter 4.
         * @return A new instance of fragment TabDetailsAppFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String, param3: String, param4: Int) =
            TabDetailsAppFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    putString(ARG_PARAM3, param3)
                    putInt(ARG_PARAM4, param4)
                }
            }
    }
}