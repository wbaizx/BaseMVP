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

abstract class BaseListAdapter<M, BH : BaseViewHolder>(@LayoutRes private val layoutResId: Int) : BaseQuickAdapter<M, BH>(layoutResId) {
    private val TAG = "BaseListAdapter"

    /**
     * 分页加载的下标，从0开始
     */
    val pageDefaultIndex = 0
    var page = pageDefaultIndex

    /**
     * 对应分页控件
     */
    private var refreshLayout: SmartRefreshLayout? = null

    /**
     * adapter对应的RecyclerView
     */
    private lateinit var rcView: RecyclerView

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
     *
     * 如果在分页列表中使用自定义空布局并且有点击重新加载的功能，一定要注意调用resetPage()重置分页下标
     */
    fun setDefaultEmptyView(context: Context? = null, root: ViewGroup? = null, emptyClick: ((Int) -> Unit)? = null) {
        val view: View
        if (context == null || root == null) {
            if (this::rcView.isInitialized) {
                view = LayoutInflater.from(this.context).inflate(R.layout.list_item_empty, rcView, false)
            } else {
                throw RuntimeException("调用时机过早，rcView和context还未初始化，需要传递context，root参数")
            }
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_empty, root, false)
        }

        view.setOnClickListener {
            //空布局点击重新加载也应该重置分页下标
            resetPage()
            log(TAG, "emptyClick $page")
            //事件响应
            emptyClick?.invoke(page)
        }

        setEmptyView(view)
        log(TAG, "set default emptyView")
    }

    /**
     * 设置分页事件
     * 仅分页列表使用
     */
    fun setRefreshAndLoadMore(refreshLayout: SmartRefreshLayout, request: (Int) -> Unit) {
        this.refreshLayout = refreshLayout

        refreshLayout.setOnRefreshListener {
            resetPage()
            log(TAG, "Refresh $page")
            request.invoke(page)
        }
        refreshLayout.setOnLoadMoreListener {
            page++
            log(TAG, "LoadMore $page")
            request.invoke(page)
        }
    }

    /**
     * 填充分页加载的数据，同时操作对应刷新布局控件
     * 仅分页列表使用
     */
    fun addPageData(list: List<M>?) {
        if (page == pageDefaultIndex) { //刷新，或者空布局点击刷新
            //是刷新需要清空数据
            data.clear()

            if (list.isNullOrEmpty()) {
                //刷新的数据为空，此时应该禁止上拉加载
                refreshLayout?.setEnableLoadMore(false)

                log(TAG, "addPageData Refresh Null")
            } else {
                refreshLayout?.setEnableLoadMore(true)
                //不为空添加数据
                data.addAll(list)

                log(TAG, "addPageData Refresh")
            }

            refreshLayout?.finishRefresh(true)

        } else { //加载更多
            if (list.isNullOrEmpty()) {
                //加载更多的数据为空，此时应该标记没有数据了
                refreshLayout?.finishLoadMoreWithNoMoreData()

                log(TAG, "addPageData LoadMore Null")
            } else {
                //不为空添加数据
                data.addAll(list)

                refreshLayout?.finishLoadMore(true)

                log(TAG, "addPageData LoadMore")
            }
        }

        notifyDataSetChanged()
    }

    /**
     * 主动重置分页下标
     */
    fun resetPage() {
        log(TAG, "resetPage")
        page = pageDefaultIndex
    }

    /**
     * 刷新失败或加载数据失败时，手动调用此方法
     */
    fun loadPageError() {
        if (page == pageDefaultIndex) { //刷新，或者空布局点击刷新
            refreshLayout?.finishRefresh(false)

        } else { //加载更多
            refreshLayout?.finishLoadMore(false)
            //如果是加载出错，page需要回退1
            page--
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