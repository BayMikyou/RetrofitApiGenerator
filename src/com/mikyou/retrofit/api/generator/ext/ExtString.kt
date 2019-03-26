package com.mikyou.retrofit.api.generator.ext

fun String.underlineToCamel(): String {
    if ("" == this.trim()) {
        return ""
    }

    val params = split("_")
    val stringBuilder = StringBuilder()
    params.forEachIndexed { index, param ->
        if (index == 0) {
            stringBuilder.append(param.toLowerCase())
        } else {
            stringBuilder.append(param.substring(0, 1).toUpperCase())
            stringBuilder.append(param.substring(1).toLowerCase())
        }
    }

    return stringBuilder.toString()
}