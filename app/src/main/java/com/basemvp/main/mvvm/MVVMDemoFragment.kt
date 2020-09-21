package com.basemvp.main.mvvm

import android.os.Handler
import com.base.common.base.mvvm.BaseMVVMFragment
import com.basemvp.R
import com.basemvp.databinding.FragmentMvvmDemoFBinding
import com.basemvp.main.mvvm.adapter.MVMMBindAdapter
import com.basemvp.main.mvvm.adapter.MVMMListAdapter
import kotlinx.android.synthetic.main.fragment_mvvm_demo_f.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * 测试 mvvm 下的 adapter
 */
class MVVMDemoFragment : BaseMVVMFragment<FragmentMvvmDemoFBinding>() {
    private val TAG = "MVVMDemoFragment"

    /**
     * viewModel 使用koin注入方式
     */
    override val viewModel by sharedViewModel<MVVMDemoViewModel>()

    override fun getContentView() = R.layout.fragment_mvvm_demo_f

    override fun bindModelId(binding: FragmentMvvmDemoFBinding) {
        binding.viewModel = viewModel
    }

    override fun createView() {
        init1()
        init2()
    }

    private fun init1() {
        //xml中设置了头部脚部
//        refreshLayout1.setRefreshHeader(ClassicsHeader(context))
//        refreshLayout1.setRefreshFooter(ClassicsFooter(context))

        recyclerView1.adapter = MVMMBindAdapter().apply {
            openRefreshAndLoadMore(refreshLayout1) {
                Handler().postDelayed({ addPageData(null) }, 1500)
            }
        }
    }

    private fun init2() {
        val adapter = MVMMListAdapter()
        recyclerView2.adapter = adapter
        adapter.setDefaultEmptyView()

        adapter.openRefreshAndLoadMore(refreshLayout2) {
            Handler().postDelayed({ adapter.addPageData(null) }, 1500)
        }
    }


    override fun onFirstVisible() {
    }

    override fun onVisible() {
    }

    override fun onHide() {
    }
}