package com.base.common.util

import android.os.Environment
import com.base.common.BaseAPP
import java.io.File

object FileUtil {
    private val TAG = "FileUtil"

    /**
     * 跟随app的文件存储
     * 不需要动态权限
     */
    fun getDiskFilePath(name: String): String {
        val path: String

        //外部文件存储，能看
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() && !Environment.isExternalStorageRemovable()) {
            val externalFilesDir = BaseAPP.baseAppContext.getExternalFilesDir(name)
            if (externalFilesDir != null) {
                //外部文件存储，能看
                path = externalFilesDir.absolutePath
                log(TAG, "externalFilesDir - $path")
                return path
            }
        }

        //内部文件存储，不能看
        path = BaseAPP.baseAppContext.filesDir.absolutePath + File.separator + name
        checkExists(File(path))
        log(TAG, "filesDir - $path")
        return path
    }

    private fun checkExists(file: File) {
        if (!file.exists()) {
            file.mkdirs()
        }
    }

    /**
     * 文件存储
     * 卸载不会删除，能看(过时了)
     * 需要动态权限
     */
    fun getSDcardFilePath(name: String): String {
        return Environment.getExternalStorageDirectory().absolutePath + File.separator + name
    }
}