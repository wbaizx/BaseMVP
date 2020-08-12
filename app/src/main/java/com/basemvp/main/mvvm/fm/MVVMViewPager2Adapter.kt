package com.basemvp.main.mvvm.fm

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * FragmentStateAdapter内部本身就使用了 setMaxLifecycle 控制
 */
class MVVMViewPager2Adapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    private val fragmentList = arrayListOf(
        MVVMDemoFragment(),
        MVVMDemoFragment()
    )

    override fun getItemCount() = fragmentList.size

    override fun createFragment(position: Int) = fragmentList[position]

}