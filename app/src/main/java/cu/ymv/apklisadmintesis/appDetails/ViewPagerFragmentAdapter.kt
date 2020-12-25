package cu.ymv.apklisadmintesis.appDetails

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
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                AppDetailsFragment()
            }
            1 -> {
                DownloadAppFragment.newInstance(appId!!, "")
            }
            2 -> {
                CommentsFragment.newInstance(appId!!)
            }
            3 -> {
                VersionAppFragment()
            }
            else -> {
                AppDetailsFragment()
            }
        }
    }

}