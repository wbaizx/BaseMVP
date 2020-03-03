package com.basemvp.main.fragment_example.vp_fragment

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.base.common.test.fm.TestFragment
import com.base.common.test.fm.TestViewFragment

/**
 * FragmentStateAdapter内部本身就使用了 setMaxLifecycle 控制
 */
class ViewPager2Adapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    private val fragmentList = arrayListOf(
        TestFragment("1"),
        TestFragment("2"),
        TestFragment("3"),
        TestFragment("4"),
        TestViewFragment("MVP 5"),
        TestViewFragment("MVP 6"),
        TestViewFragment("MVP 7")
    )

    override fun getItemCount() = fragmentList.size

    override fun createFragment(position: Int) = fragmentList[position]

}