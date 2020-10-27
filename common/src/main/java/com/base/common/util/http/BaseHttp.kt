package com.base.common.util.http

import com.base.common.BaseAPP
import com.base.common.util.log
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

abstract class BaseHttp {
    private val TAG = "BaseHttp"

    //网络连接时间秒
    private val TIMEOUT = 10

    //缓存大小2Mb
    private val CACHEMAXSIZE = 1024 * 1024 * 2L

    //缓存时间5秒
    private val CACHETIME = 5

    //基础ip
    abstract val BASE_URL: String

    /**
     * 拦截器，设置头部，解析头部等
     */
    private val baseInterceptor = object : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val finalRequest = request.newBuilder()
                .header("token", "token")
                .header("test", URLEncoder.encode("中文编码", "UTF-8"))
                .build()
            log(TAG, "baseInterceptor  request")

            val proceed = chain.proceed(finalRequest)
            log(TAG, "baseInterceptor  response")
            log(TAG, "baseInterceptor  ${proceed.header("Date", "111")}")

            return proceed
        }
    }

    /**
     * 拦截器，设置缓存时间
     */
    private val cacheInterceptor = object : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            log(TAG, "cacheInterceptor  request")
            val response = chain.proceed(chain.request())
            val finalResponse = response.newBuilder().removeHeader("pragma")
                .header("Cache-Control", "max-age=$CACHETIME").build()

            log(TAG, "cacheInterceptor  response")

            return finalResponse
        }
    }

    /**
     * 日志拦截
     * HttpLoggingInterceptor拦截器如果level设置成Body，则下载不会实时回调
     * 改成其他即可
     */
    private val httpLoggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    /**
     * 带各种拦截器的普通Retrofit，主要用于普通网络请求
     */
    protected fun <T> getApi(api: Class<T>, baseUrl: String = BASE_URL, needCache: Boolean = false): T {
        log(TAG, "getApi $baseUrl")

        val clientBuilder = OkHttpClient.Builder()
        clientBuilder.readTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
            .connectTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
            .addInterceptor(baseInterceptor)

        if (BaseAPP.isDebug()) {
            clientBuilder.addInterceptor(httpLoggingInterceptor)
        }

        clientBuilder.sslSocketFactory(SSLSocketClient.socketFactory, SSLSocketClient.trustAllCerts)
            .hostnameVerifier(SSLSocketClient.hostnameVerifier)

        if (needCache) {
            val cacheFile = File(BaseAPP.baseAppContext.cacheDir, "${api.simpleName}Cache")
            val cache = Cache(cacheFile, CACHEMAXSIZE)
            clientBuilder.addNetworkInterceptor(cacheInterceptor)
                .cache(cache)
        }

        val retrofitBuilder = Retrofit.Builder().client(clientBuilder.build())
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofitBuilder.create(api)
    }
}