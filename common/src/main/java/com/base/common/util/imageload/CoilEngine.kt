package com.base.common.util.imageload

import android.os.Build
import android.util.Log
import android.widget.ImageView
import coil.Coil
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.load
import coil.request.ImageRequest
import coil.transform.BlurTransformation
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import coil.util.DebugLogger
import com.base.common.BaseAPP
import com.base.common.R
import com.base.common.util.log
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File

class CoilEngine : LoadEngine {

    //缓存大小10Mb
    private val CACHEMAXSIZE = 1024 * 1024 * 10L

    //缓存时间600秒
    private val CACHETIME = 600

    init {
        //通过配置imageLoader设置Gif和Svg支持，以及磁盘缓存配置
        val imageLoader = ImageLoader.Builder(BaseAPP.baseAppContext)
            .availableMemoryPercentage(0.25) // Use 25% of the application's available memory.
            .componentRegistry {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    add(ImageDecoderDecoder())
                } else {
                    add(GifDecoder())
                }
                add(SvgDecoder(BaseAPP.baseAppContext))
            }
            .okHttpClient {
                val cacheDirectory = File(BaseAPP.baseAppContext.cacheDir, "image_cache").apply { mkdirs() }
                val cache = Cache(cacheDirectory, CACHEMAXSIZE)//缓存大小10Mb
                val dispatcher = Dispatcher().apply { maxRequestsPerHost = maxRequests }

                OkHttpClient.Builder()
                    .cache(cache)
                    .dispatcher(dispatcher)
                     //和网络请求相同的网络log打印
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    //拦截器，设置缓存时间
                    .addNetworkInterceptor(object : Interceptor {
                        override fun intercept(chain: Interceptor.Chain): Response {
                            log("CoilEngine", "cacheInterceptor  request")
                            val response = chain.proceed(chain.request())
                            val finalResponse = response.newBuilder().removeHeader("pragma")
                                .header("Cache-Control", "max-age=$CACHETIME").build()

                            log("CoilEngine", "cacheInterceptor  response")

                            return finalResponse
                        }
                    })
                    .build()
            }
            .apply {
                if (BaseAPP.isDebug()) {
                    logger(DebugLogger(Log.VERBOSE))
                }
            }
            .build()

        //配置
        Coil.setImageLoader(imageLoader)
    }

    override fun load(url: String, img: ImageView, type: Int) {
        img.load(url) {
            loadFromType(this, img, type)
        }
    }

    override fun load(file: File, img: ImageView, type: Int) {
        img.load(file) {
            loadFromType(this, img, type)
        }
    }

    override fun load(id: Int, img: ImageView, type: Int) {
        img.load(id) {
            loadFromType(this, img, type)
        }
    }

    private fun loadFromType(builder: ImageRequest.Builder, img: ImageView, type: Int) {
        when (type) {
            LoadImage.NORMAL -> {
                builder.crossfade(true)
                builder.placeholder(R.mipmap.placeholder_icon)
                builder.error(R.mipmap.test_icon)
            }
            LoadImage.BLUR -> {
                builder.transformations(BlurTransformation(img.context, 25f, 3f))
            }
            LoadImage.CIRCLE -> {
                builder.transformations(CircleCropTransformation())
            }
            LoadImage.ROUND -> {
                builder.transformations(RoundedCornersTransformation(50f, 50f, 50f, 50f))
            }
        }
    }

}