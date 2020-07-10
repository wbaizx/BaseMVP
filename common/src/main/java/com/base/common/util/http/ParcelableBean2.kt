package com.base.common.util.http

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ParcelableBean2(val a: String, val b: String?) : Parcelable