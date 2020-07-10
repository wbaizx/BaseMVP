package com.base.common.util.http

import java.io.Serializable

data class SerializableBean(val a: String, val b: String?, val c: ArrayList<String>) : Serializable