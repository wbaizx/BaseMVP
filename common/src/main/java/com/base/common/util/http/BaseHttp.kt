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
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager


object BaseHttp {
    private const val TIMEOUT = 10
    //2Mb
    private const val CACHEMAXSIZE = 1024 * 1024 * 2
    //5秒
    private const val CACHETIME = 5

    private const val BASE_URL = "https://easy-mock.com/"

    /**
     * 拦截消息头，按设置缓存时间
     */
    private val cacheInterceptor = object : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            LogUtil.log("BaseHttp", "cacheInterceptor")
            val proceed = chain.proceed(chain.request())
            return proceed.newBuilder().removeHeader("pragma")
                .header("Cache-Control", "max-age=$CACHETIME").build()
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


    private val client by lazy {
        LogUtil.log("BaseHttp", "client")
        val cacheFile = File(BaseAPP.baseAppContext.cacheDir, "BaseHttpCache")
        val cache = Cache(cacheFile, CACHEMAXSIZE.toLong())
        OkHttpClient.Builder()
            .readTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
            .connectTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
            .addNetworkInterceptor(cacheInterceptor)
            .addInterceptor(httpLoggingInterceptor)
//            .sslSocketFactory(getSSLSocketFactory(),trustAllCerts[0])
//            .hostnameVerifier(HostnameVerifier { hostname, session -> true })
            .cache(cache)
            .build()
    }

    val retrofit: Retrofit by lazy {
        LogUtil.log("BaseHttp", "retrofit")
        Retrofit.Builder()
            .client(client)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}