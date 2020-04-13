package com.base.common.util.http

import com.base.common.BaseAPP
import com.base.common.util.LogUtil
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.net.URLEncoder
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager


object BaseHttp {
    //网络连接时间
    private const val TIMEOUT = 10
    //缓存大小2Mb
    private const val CACHEMAXSIZE = 1024 * 1024 * 2
    //缓存时间5秒
    private const val CACHETIME = 5

    private const val BASE_URL = "https://easy-mock.com/"

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
            LogUtil.log("BaseHttp", "baseInterceptor  request")

            val proceed = chain.proceed(finalRequest)
            LogUtil.log("BaseHttp", "baseInterceptor  response")

            return proceed
        }
    }

    /**
     * 拦截器，设置缓存时间
     */
    private val cacheInterceptor = object : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            LogUtil.log("BaseHttp", "cacheInterceptor  request")
            val response = chain.proceed(chain.request())
            val finalResponse = response.newBuilder().removeHeader("pragma")
                .header("Cache-Control", "max-age=$CACHETIME").build()

            LogUtil.log("BaseHttp", "cacheInterceptor  response")

            return finalResponse
        }
    }

    /**
     * 日志拦截
     * release 版本需要去掉
     */
    private val httpLoggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)


    /**
     * https忽略所有相关
     */
    private fun getSSLSocketFactory(): SSLSocketFactory {
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustAllCerts, SecureRandom())
        return sslContext.socketFactory
    }

    /**
     * https忽略所有相关
     */
    private val trustAllCerts = arrayOf(object : X509TrustManager {
        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return arrayOf<X509Certificate>()
        }

        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {

        }
    })


    //如果okhttp使用了拦截器，则下载不会实时回调
    private fun getBaseClient(): OkHttpClient {
        LogUtil.log("BaseHttp", "client")
        val cacheFile = File(BaseAPP.baseAppContext.cacheDir, "BaseHttpCache")
        val cache = Cache(cacheFile, CACHEMAXSIZE.toLong())
        return OkHttpClient.Builder()
            .readTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
            .connectTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
            .addInterceptor(baseInterceptor)
            .addNetworkInterceptor(cacheInterceptor)
            .addInterceptor(httpLoggingInterceptor)
//            .sslSocketFactory(getSSLSocketFactory(),trustAllCerts[0])
//            .hostnameVerifier(HostnameVerifier { hostname, session -> true })
            .cache(cache)
            .build()
    }

    /**
     * 带各种拦截器的普通Retrofit，主要用于普通网络请求
     */
    val normalRetrofit: Retrofit by lazy {
        LogUtil.log("BaseHttp", "retrofit")
        Retrofit.Builder()
            .client(getBaseClient())
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * 最简单的Retrofit，不带拦截器，主要用于下载
     */
    val simpleRetrofit: Retrofit by lazy {
        LogUtil.log("BaseHttp", "retrofit")
        Retrofit.Builder()
            .client(
                OkHttpClient.Builder()
                    .readTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
                    .connectTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
//                    .sslSocketFactory(getSSLSocketFactory(), trustAllCerts[0])
//                    .hostnameVerifier(HostnameVerifier { hostname, session -> true })
                    .build()
            )
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}