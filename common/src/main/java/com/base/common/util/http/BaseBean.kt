package com.base.common.util.http

/**
 * 网络请求返回的bean基类，所有基类都应该继承它
 */
data class BaseBean<T : Any?>(val code: Int, val data: T, val msg: Int)