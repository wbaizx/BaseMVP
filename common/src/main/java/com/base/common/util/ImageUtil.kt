package com.base.common.util

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import com.base.common.BaseAPP
import java.io.*

object ImageUtil {
    private val TAG = "ImageUtil"

    private val MIME_TYPE_PNG = "image/png"
    private val MIME_TYPE_JPEG = "image/jpeg"
    private val MIME_TYPE_BMP = "image/bmp"
    private val MIME_TYPE_GIF = "image/gif"
    private val MIME_TYPE_WEBP = "image/webp"

    private val MIME_TYPE_MP4 = "video/mp4"

    /**
     * 将view转换成bitmap
     * 注意如果是 ImageView 直接返回的 bitmap，用完后不要 recycle
     */
    fun createBitmapFromView(view: View): Bitmap {
        //是ImageView直接获取
        if (view is ImageView) {
            val drawable = view.drawable
            if (drawable is BitmapDrawable) {
                return drawable.bitmap
            }
        }
        view.clearFocus()
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        if (bitmap != null) {
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            canvas.setBitmap(null)
        }
        return bitmap
    }

    fun savePicture(bitmap: Bitmap, name: String): File {
        val file = File(FileUtil.getDiskFilePath("IMG") + File.separator + name)
        FileOutputStream(file).use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            it.flush()
        }
        return file
    }

    /**
     * 文件已经保存，插入到相册显示，一般需要 WRITE_EXTERNAL_STORAGE 权限
     */
    fun updateGallery(file: File, width: Int, height: Int): Boolean {
        log(TAG, "updateGallery - ${file.length()} -- ${file.absolutePath}")

        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, file.name)
        values.put(MediaStore.Images.Media.MIME_TYPE, MIME_TYPE_JPEG)
        values.put(MediaStore.Images.Media.WIDTH, width)
        values.put(MediaStore.Images.Media.HEIGHT, height)
        values.put(MediaStore.Images.Media.DESCRIPTION, "description")
        val uri = BaseAPP.baseAppContext.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        if (uri != null) {
            val out = BaseAPP.baseAppContext.contentResolver.openOutputStream(uri)
            if (out != null) {
                BufferedOutputStream(out).use { o ->
                    BufferedInputStream(FileInputStream(file)).use { i ->
                        val byteArray = ByteArray(1024 * 1024)
                        var cout = i.read(byteArray)
                        while (cout > 0) {
                            o.write(byteArray, 0, cout)
                            cout = i.read(byteArray)
                        }
                        o.flush()
                    }
                }
                return true
            }
        }

        return false
    }
}