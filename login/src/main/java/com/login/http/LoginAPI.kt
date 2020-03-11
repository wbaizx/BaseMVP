package com.login.http

import com.login.home.bean.LoginBean
import retrofit2.http.*

interface LoginAPI {
//    如果同时携带String或json类型数据（Date也是String型），需要使用RequestBody包装，例如
//    @Part("recordTime") RequestBody recordTime
//    String传递时这样包装 RequestBody.create(MediaType.parse("multipart/form-data"), Str);
//    json 传递时这样包装 RequestBody.create(MediaType.parse("application/json; charset=utf-8"), Str);

//    @Multipart 组合 @Part
//    @POST 组合 @FormUrlEncoded 组合 @Field
//    @Body
//    @Query
//    @Url
//    @Path
//    @Streaming  //如果okhttp使用了拦截器，则下载不会实时回调
//    @Header

//    ResponseBody

    @GET("mock/5e6208aeb261f976d93ed585/example/aaaa")
    suspend fun login(): LoginBean
}