package com.basemvp.util.dialog

import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.basemvp.R
import com.basemvp.util.AndroidUtil

object DialogFactory {
    fun createNormalDialog(
        activity: FragmentActivity,
        title: String,
        content: String,
        yesName: String,
        yesClick: () -> Unit,
        noName: String,
        noClick: (() -> Unit)? = null
    ) {
        val dialog = NormalDialog()
        dialog.setContext(activity)
        dialog.setTitle(title)
        dialog.setContent(content)
        dialog.setYesName(yesName)
        dialog.setYesClick(yesClick)
        dialog.setNoName(noName)
        dialog.setNoClick(noClick)
        dialog.showDialog("normal")
    }
}


class NormalDialog : BaseFragmentDialog() {
    private var title: String = "标题"
    private var content: String = "内容"
    private var yesName: String = "确定"
    private var noName: String = "取消"
    private var yesClick: (() -> Unit)? = null
    private var noClick: (() -> Unit)? = null

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
        view.findViewById<TextView>(R.id.title).text = title
        view.findViewById<TextView>(R.id.content).text = content

        val yes = view.findViewById<Button>(R.id.yes)
        yes.text = yesName
        yes.setOnClickListener {
            dismiss()
            yesClick?.invoke()
        }

        val no = view.findViewById<Button>(R.id.no)
        no.text = noName
        no.setOnClickListener {
            dismiss()
            noClick?.invoke()
        }
    }

    fun setTitle(title: String) {
        this.title = title
    }

    fun setContent(content: String) {
        this.content = content
    }

    fun setYesName(yesName: String) {
        this.yesName = yesName
    }

    fun setYesClick(yesClick: () -> Unit) {
        this.yesClick = yesClick
    }

    fun setNoName(noName: String) {
        this.noName = noName
    }

    fun setNoClick(noClick: (() -> Unit)?) {
        this.noClick = noClick
    }
}