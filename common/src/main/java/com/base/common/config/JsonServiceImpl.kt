package com.base.common.config

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.service.SerializationService
import com.google.gson.Gson
import java.lang.reflect.Type

/**
 * Kotlin类中的字段无法注入，简单解决可以在字段上添加 @JvmField
 */
@Route(path = "/service/json")
class JsonServiceImpl : SerializationService {
    override fun init(context: Context?) {
    }

    override fun <T : Any?> json2Object(input: String?, clazz: Class<T>?): T {
        return Gson().fromJson(input, clazz)
    }

    override fun <T : Any?> parseObject(input: String?, clazz: Type?): T {
        return Gson().fromJson(input, clazz)
    }

    override fun object2Json(instance: Any?): String {
        return Gson().toJson(instance)
    }
}