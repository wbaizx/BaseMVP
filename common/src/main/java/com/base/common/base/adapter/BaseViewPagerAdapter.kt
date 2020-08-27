package com.base.common.base.adapter

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.base.common.base.BaseFragment

/**
 * ViewPager2+Fragment的基础adapter
 * FragmentStateAdapter内部本身就使用了 setMaxLifecycle 控制 , 完美配合 BaseFragment
 *
 * viewPager禁用手动滑动
 * viewPager.isUserInputEnabled = false
 *
 * 模拟拖拽
 * viewPager.fakeDragBy()
 *
 * 根据需求设置缓存数量
 * viewPager.offscreenPageLimit
 */
class BaseViewPagerAdapter(fragmentActivity: FragmentActivity, private val fragmentList: ArrayList<BaseFragment>) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount() = fragmentList.size

    override fun createFragment(position: Int) = fragmentList[position]
}