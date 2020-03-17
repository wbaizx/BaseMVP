package com.base.common.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.util.TypedValue
import android.view.Gravity
import android.widget.Toast
import androidx.core.content.FileProvider
import com.base.common.BaseAPP
import java.io.File

object AndroidUtil {
    private val TAG = "AndroidUtil"

    /**
     * 获取屏幕宽度
     */
    fun getScreenWidth(): Int {
        return BaseAPP.baseAppContext.resources.displayMetrics.widthPixels
    }

    /**
     * 获取屏幕高度
     */
    fun getScreenHeight(): Int {
        return BaseAPP.baseAppContext.resources.displayMetrics.heightPixels
    }

    fun sp2px(f: Float) =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, f, BaseAPP.baseAppContext.resources.displayMetrics)

    fun dp2px(f: Float) =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, f, BaseAPP.baseAppContext.resources.displayMetrics)

    /**
     * 获取手机网络连接状况
     */
    fun isNetworkAvailable(): Boolean {
        val connectivityManager = BaseAPP.baseAppContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        if (connectivityManager != null) {
            val activeNetwork = connectivityManager.activeNetwork
            if (activeNetwork != null) {
                val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
                if (networkCapabilities != null) {
                    if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        LogUtil.log(TAG, "is wifi")
                        return true
                    }
                    if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        LogUtil.log(TAG, "is mobile network")
                        return true
                    }
                }
            }
        }
        return false
    }

    /**
     * 获取当前版本号
     */
    fun getVersionCode(): Int {
        val manager = BaseAPP.baseAppContext.packageManager
        val info = manager.getPackageInfo(BaseAPP.baseAppContext.packageName, 0)
        return info.versionCode
    }

    /**
     * 获取apk的版本号
     */
    fun getVersionCodeFromApk(filePath: String): Int {
        val pm = BaseAPP.baseAppContext.packageManager
        val packInfo = pm.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES)
        return packInfo!!.versionCode
    }

    /**
     * 安装apk
     * 另外还需要在manifest和xml中配置
     * 注意权限 REQUEST_INSTALL_PACKAGES
     */
    fun installApk(file: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val type = "application/vnd.android.package-archive"
        val uri: Uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(BaseAPP.baseAppContext, BaseAPP.baseAppContext.packageName + ".fileProvider", file)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
            uri = Uri.fromFile(file)
        }
        intent.setDataAndType(uri, type)
        BaseAPP.baseAppContext.startActivity(intent)
    }

    fun showToast(msg: String) {
        val toast = Toast.makeText(BaseAPP.baseAppContext, msg, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }
}