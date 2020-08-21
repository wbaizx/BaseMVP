package com.login.http

import com.base.common.util.http.BaseBean
import com.login.home.bean.LoginBean
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming

interface LoginAPI {
//    如果同时携带String或json类型数据（Date也是String型），需要使用RequestBody包装，例如
//    @Part("recordTime") RequestBody recordTime
//    String传递时这样包装 RequestBody.create(MediaType.parse("multipart/form-data"), Str);
//    json 传递时这样包装 RequestBody.create(MediaType.parse("application/json; charset=utf-8"), Str);

//    @Multipart  组合 @Part
//    @POST  组合 @FormUrlEncoded 组合 @Field
//    @Body  body对应的参数中，如果某一字段传空，在实际传输时这个字段会被抹掉，而不是赋空值(可能gson转换规则就是这样，百度好像可以配置)
//    @Query
//    @Url
//    @Path
//    @Header
//    @FieldMap   post方式使用map提交参数，用于参数较多或不固定的情况
//    @QueryMap   get方式使用map提交参数，用于参数较多或不固定的情况

    @GET("mock/5e6208aeb261f976d93ed585/example/aaaa")
    suspend fun loginBean(): BaseBean<LoginBean>

    @GET("http://www.baidu.com/")
    suspend fun loginResponseBody(): ResponseBody


    /**
     * HttpLoggingInterceptor拦截器如果level设置成Body，则下载不会实时回调
     * 改成其他即可
     */
    @Streaming
    @GET("https://htdrp.oss-cn-hangzhou.aliyuncs.com/wan-ya/6mybatis%E7%9A%84%E4%BA%8C%E7%BA%A7%E7%BC%93%E5%AD%98%E5%AD%98%E5%9C%A8%E4%BB%80%E4%B9%88%E9%97%AE%E9%A2%98%EF%BC%9F%E6%80%8E%E4%B9%88%E9%81%BF%E5%85%8D%EF%BC%9F.mp4")
    suspend fun downLoadApk(): ResponseBody
}