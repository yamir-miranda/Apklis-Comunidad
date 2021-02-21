package cu.apklis.comunidad.appDetails

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerFragmentAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val appId: String?,
    private val appPackage: String?,
    private val price: Double?
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                VentasAppFragment.newInstance(appId!!, price!!)
            }
            1 -> {
                CommentsFragment.newInstance(appId!!)
            }
            2 -> {
                AppDetailsFragment.newInstance(appPackage!!, "")
            }
            3-> {
                DownloadAppFragment.newInstance(appId!!, "")
            }
            4-> {
                DownloadAppFragment.newInstance(appId!!, "")
            }
            else -> {
                AppDetailsFragment.newInstance(appPackage!!, "")
            }
        }
    }

}