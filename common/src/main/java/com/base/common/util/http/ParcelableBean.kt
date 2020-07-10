package com.base.common.util.http

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ParcelableBean(val a: String, val b: String?, val c: ArrayList<String>, val d: ParcelableBean2) : Parcelable {

}