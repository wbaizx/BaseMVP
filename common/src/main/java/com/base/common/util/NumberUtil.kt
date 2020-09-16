package com.base.common.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * DecimalFormat 主要用于格式化数字
 *
 * BigDecimal 主要用于做对精度要求高的运算，比如涉及到钱的，初始化尽量使用字符串作为参数
 */

/**
 *  DecimalFormat格式化数字
 *  format: "#.00" , "#.##"
 *
 *  需注意有四舍五入规则
 */
fun numberFormat(number: Double, format: String): String {
    val df = DecimalFormat(format)
    df.roundingMode = RoundingMode.HALF_UP
    return df.format(number)
}

/**
 * BigDecimal格式化数字
 * scale: 要保留的小数位数
 *
 * 需注意有四舍五入规则
 */
fun numberScale(number: String, scale: Int): String {
    return BigDecimal(number).setScale(scale, RoundingMode.HALF_UP).toString()
}