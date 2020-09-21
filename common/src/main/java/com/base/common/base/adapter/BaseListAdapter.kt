package com.base.common.base.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.base.common.R
import com.base.common.util.log
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.scwang.smart.refresh.layout.SmartRefreshLayout

abstract class BaseListAdapter<M, BH : BaseViewHolder>(@LayoutRes private val layoutResId: Int) :
    BaseQuickAdapter<M, BH>(layoutResId) {

    private val TAG = "BaseListAdapter"

    /**
     * 分页加载的下标，从0开始
     */
    val pageDefaultIndex = 0
    private var page: Int = pageDefaultIndex
    private var refreshLayout: SmartRefreshLayout? = null

    /**
     * adapter对应的RecyclerView
     */
    private lateinit var rcView: RecyclerView

    /**
     * 空布局点击事件
     */
    var emptyClick: (() -> Unit)? = null

    init {
        log(TAG, "init")
    }

    /**
     * dapter绑定到recyclerView时会回调
     */
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        rcView = recyclerView
    }

    /**
     * 设置默认空布局
     */
    fun setDefaultEmptyView(context: Context? = null, root: ViewGroup? = null) {
        val view: View
        if (context == null || root == null) {
            if (this::rcView.isInitialized) {
                view = LayoutInflater.from(this.context).inflate(R.layout.list_item_empty, rcView, false)
            } else {
                throw RuntimeException("调用时机过早，rcView和context还未初始化，需要传递参数")
            }
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_empty, root, false)
        }

        view.setOnClickListener {
            log(TAG, "emptyClick")
            //空布局点击重新加载也应该重置分页下标
            page = pageDefaultIndex
            //事件响应
            emptyClick?.invoke()
        }
        setEmptyView(view)
        log(TAG, "set default emptyView")
    }

    /**
     * 设置分页事件
     */
    fun openRefreshAndLoadMore(refreshLayout: SmartRefreshLayout, request: (Int) -> Unit) {
        this.refreshLayout = refreshLayout

        refreshLayout.setOnRefreshListener {
            log(TAG, "Refresh")
            page = pageDefaultIndex
            request.invoke(page)
        }
        refreshLayout.setOnLoadMoreListener {
            log(TAG, "LoadMore")
            page++
            request.invoke(page)
        }
    }

    /**
     * 填充分页加载的数据
     */
    fun addPageData(list: List<M>?) {
        if (page == pageDefaultIndex) {
            //如果是刷新，清空数据
            data.clear()
        }
        if (!list.isNullOrEmpty()) {
            data.addAll(list)
        }
        notifyDataSetChanged()

        if (page != pageDefaultIndex) {//加载更多
            if (list.isNullOrEmpty()) {
                refreshLayout?.finishLoadMoreWithNoMoreData()
            } else {
                refreshLayout?.finishLoadMore(true)
            }
        } else {//刷新
            refreshLayout?.finishRefresh(true)
        }
    }

    override fun convert(holder: BH, item: M) {
        configure(holder, item)

        convertUI(holder, item)
    }

    /**
     * 做必要配置，比如绑定dataBinding
     */
    open fun configure(holder: BH, item: M) {
    }

    /**
     * 设置UI
     */
    abstract fun convertUI(holder: BH, item: M)
}