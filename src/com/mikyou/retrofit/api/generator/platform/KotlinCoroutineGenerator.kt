package com.mikyou.retrofit.api.generator.platform

import com.mikyou.retrofit.api.generator.ext.underlineToCamel
import com.mikyou.retrofit.api.generator.helper.VelocityEngineHelper
import com.mikyou.retrofit.api.generator.ui.model.ViewDataRetrofitApi

class KotlinCoroutineGenerator : IGenerator {
    override fun evaluate(retrofitApiList: List<ViewDataRetrofitApi>): String {
        return VelocityEngineHelper.evaluate(composeParams(retrofitApiList), "/template/Api_Kotlin_Coroutine.vm")
    }

    private fun composeParams(retrofitApiList: List<ViewDataRetrofitApi>): List<ViewDataRetrofitApi> {
        return retrofitApiList.map { viewDataRetrofitApi ->
            viewDataRetrofitApi.paramsString = getParamsString(viewDataRetrofitApi)
            return@map viewDataRetrofitApi
        }
    }

    private fun getParamsString(viewDataRetrofitApi: ViewDataRetrofitApi): String {
        val stringBuilder = StringBuilder()
        for (path in viewDataRetrofitApi.paths) {
            stringBuilder.append("@Path(value = \"$path\") ${path.underlineToCamel()}: String, ")
        }

        for (header in viewDataRetrofitApi.headerParams) {
            stringBuilder.append("@Header(value = \"${header.paramName}\") ${header.paramName.underlineToCamel()}: ${header.paramType}, ")
        }

        for (queryParam in viewDataRetrofitApi.queryParams) {
            stringBuilder.append("@Query(value = \"${queryParam.paramName}\") ${queryParam.paramName.underlineToCamel()}: ${queryParam.paramType}, ")
        }

        for (requestBodyParam in viewDataRetrofitApi.requestBodyParams) {
            stringBuilder.append("@Body ${requestBodyParam.paramName.underlineToCamel()}: ${requestBodyParam.paramType}, ")
        }

        for (fieldParam in viewDataRetrofitApi.fieldParams) {
            stringBuilder.append("@Field(value = \"${fieldParam.paramName}\") ${fieldParam.paramName.underlineToCamel()}: ${fieldParam.paramType}, ")
        }

        val resultTrim = stringBuilder.toString().trim()
        if (resultTrim.isNotBlank() && resultTrim.last() == ',' && resultTrim.length > 2) {
            return resultTrim.substring(0, resultTrim.length - 2)
        }

        return resultTrim
    }
}