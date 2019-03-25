package com.mikyou.retrofit.api.generator.ext

fun String.underlineToCamel(): String {
    if ("" == this.trim()) {
        return ""
    }

    val params = split("_")
    val stringBuilder = StringBuilder()
    for (param in params) {
        if (!param.contains("_")) {
            stringBuilder.append(param)
            continue
        }

        if (stringBuilder.isEmpty()) {
            stringBuilder.append(param.toLowerCase())
        } else {
            stringBuilder.append(param.substring(0, 1).toUpperCase())
            stringBuilder.append(param.substring(1).toLowerCase())
        }
    }
    return stringBuilder.toString()
}