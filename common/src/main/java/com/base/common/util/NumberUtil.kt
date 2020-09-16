package com.base.common.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * DecimalFormat 主要用于格式化数字
 *
 * BigDecimal 主要用于做对精度要求高的运算，比如涉及到钱的，初始化尽量使用字符串作为参数
 *
 * 四舍五入规则，考虑负值（一般不会为负）
 * RoundingMode.UP      舍弃部分始终进 1
 * RoundingMode.DOWN    舍弃部分直接截掉不进 1
 * RoundingMode.CEILING 用于别人向我给钱，数值会偏大（负值反向表示给出去，偏大给的就少） 1.2 -> 2  -1.2 -> -1
 * RoundingMode.FLOOR   用于我向别人给钱，数值会偏小（负值反向表示给进来，偏小给的就多） 1.8 -> 1  -1.8 -> -2
 * RoundingMode.HALF_UP 标准四舍五入
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