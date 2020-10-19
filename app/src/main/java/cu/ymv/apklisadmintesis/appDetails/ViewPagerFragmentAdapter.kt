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
        return if (position == 0) {
            CommentsFragment.newInstance(appId!!)
        } else if (position == 1) {
            CommentsFragment.newInstance(appId!!)
        } else if (position == 2) {
            CommentsFragment.newInstance(appId!!)
        } else {
            CommentsFragment.newInstance(appId!!)
        }
    }

}