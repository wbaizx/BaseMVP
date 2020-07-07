package com.basemvp.main.special_rc.qq_album

import com.basemvp.R
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class QQAlbumAdapter : BaseDelegateMultiAdapter<String, BaseViewHolder>() {
    private val IMG = 1
    private val TEXT = 2

    init {
        repeat(41) {
            data.add("啦啦")
        }

        setMultiTypeDelegate(object : BaseMultiTypeDelegate<String>() {
            override fun getItemType(data: List<String>, position: Int): Int {
                if (position == 0 || position == 4 || position == 12 || position == 17
                    || position == 22 || position == 28 || position == 36 || position == 39
                ) {
                    return TEXT
                } else {
                    return IMG
                }
            }
        })
        getMultiTypeDelegate()
            ?.addItemType(TEXT, R.layout.multi_text_layout)
            ?.addItemType(IMG, R.layout.multi_image_layout)

        setGridSpanSizeLookup { gridLayoutManager, viewType, position ->
            if (viewType == TEXT) {
                return@setGridSpanSizeLookup gridLayoutManager.spanCount
            } else {
                return@setGridSpanSizeLookup 1
            }
        }
    }

    override fun convert(holder: BaseViewHolder, item: String) {
    }
}