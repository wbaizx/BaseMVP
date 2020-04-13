package com.base.common.update

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming

interface UpdateAPI {
    /**
     * HttpLoggingInterceptor拦截器如果level设置成Body，则下载不会实时回调
     * 改成其他即可
     */
    @Streaming
    @GET("https://htdrp.oss-cn-hangzhou.aliyuncs.com/wan-ya/6mybatis%E7%9A%84%E4%BA%8C%E7%BA%A7%E7%BC%93%E5%AD%98%E5%AD%98%E5%9C%A8%E4%BB%80%E4%B9%88%E9%97%AE%E9%A2%98%EF%BC%9F%E6%80%8E%E4%B9%88%E9%81%BF%E5%85%8D%EF%BC%9F.mp4")
    suspend fun downLoadApk(): ResponseBody
}