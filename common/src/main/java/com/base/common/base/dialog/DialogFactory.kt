package com.base.common.base.dialog

import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import com.base.common.R
import com.base.common.util.AndroidUtil
import kotlinx.android.synthetic.main.normal_dialog_view.*

object DialogFactory {
    fun createNormalDialog(
        activity: FragmentActivity,
        title: String,
        content: String,
        yesName: String,
        yesClick: () -> Unit,
        noName: String,
        noClick: (() -> Unit)? = null
    ): NormalDialog {
        val dialog = NormalDialog()
        dialog.setContext(activity)
        dialog.title = title
        dialog.content = content
        dialog.yesName = yesName
        dialog.yesClick = yesClick
        dialog.noName = noName
        dialog.noClick = noClick
        return dialog
    }
}


class NormalDialog : BaseFragmentDialog() {
    var title: String = "标题"
    var content: String = "内容"
    var yesName: String = "确定"
    var noName: String = "取消"
    var yesClick: (() -> Unit) = {}
    var noClick: (() -> Unit)? = null

    override fun setDialogConfigure() {
        setCanceledOnTouchOutside(false)
        setCanceledOnBack(true)
    }

    override fun getLayout() = R.layout.normal_dialog_view

    override fun setWindowConfigure(win: Window) {
        val params = win.attributes
        params.gravity = Gravity.CENTER
        params.width = AndroidUtil.getScreenWidth() / 10 * 7
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        win.attributes = params
    }


    override fun initView(view: View) {
        titleTex.text = title
        contentTex.text = content

        yes.text = yesName
        yes.setOnClickListener {
            dismiss()
            yesClick.invoke()
        }

        no.text = noName
        no.setOnClickListener {
            dismiss()
            noClick?.invoke()
        }
    }
}