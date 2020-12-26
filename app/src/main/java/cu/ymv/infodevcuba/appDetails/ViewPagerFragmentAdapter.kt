package cu.ymv.infodevcuba.appDetails

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerFragmentAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val appId: String?,
    packetName: String?
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                AppDetailsFragment()
            }
            1 -> {
                VentasAppFragment.newInstance(appId!!, "")
            }
            2 -> {
                CommentsFragment.newInstance(appId!!)
            }
            3-> {
                DownloadAppFragment.newInstance(appId!!, "")
            }
            4 -> {
                VersionAppFragment()
            }
            else -> {
                AppDetailsFragment()
            }
        }
    }

}