package com.basemvp.main.mvvm

import androidx.recyclerview.widget.LinearLayoutManager
import com.base.common.base.mvvm.BaseMVVMFragment
import com.base.common.util.log
import com.basemvp.R
import com.basemvp.databinding.FragmentMvvmDemoFBinding
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
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = MVMMListAdapter()

        //xml中设置了头部脚部
//        refreshLayout.setRefreshHeader(ClassicsHeader(context))
//        refreshLayout.setRefreshFooter(ClassicsFooter(context))

        refreshLayout.setOnRefreshListener {
            log(TAG, "Refresh")
        }
        refreshLayout.setOnLoadMoreListener {
            log(TAG, "LoadMore")
        }
    }

    override fun onFirstVisible() {
    }

    override fun onVisible() {
    }

    override fun onHide() {
    }
}