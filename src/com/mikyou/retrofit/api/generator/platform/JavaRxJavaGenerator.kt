package com.mikyou.retrofit.api.generator.platform

import com.mikyou.retrofit.api.generator.ext.underlineToCamel
import com.mikyou.retrofit.api.generator.helper.VelocityEngineHelper
import com.mikyou.retrofit.api.generator.ui.model.ViewDataRetrofitApi

class JavaRxJavaGenerator : IGenerator {
    override fun evaluate(retrofitApiList: List<ViewDataRetrofitApi>): String {
        return VelocityEngineHelper.evaluate(composeParams(retrofitApiList), "/template/api/Api_Java_RxJava.vm")
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
            stringBuilder.append("@Path(\"$path\") String ${path.underlineToCamel()}, ")
        }

        for (header in viewDataRetrofitApi.headerParams) {
            stringBuilder.append("@Header(\"${header.paramName}\") ${header.paramType} ${header.paramName.underlineToCamel()}, ")
        }

        for (queryParam in viewDataRetrofitApi.queryParams) {
            stringBuilder.append("@Query(\"${queryParam.paramName}\") ${queryParam.paramType} ${queryParam.paramName.underlineToCamel()}, ")
        }

        for (requestBodyParam in viewDataRetrofitApi.requestBodyParams) {
            stringBuilder.append("@Body ${requestBodyParam.paramType} ${requestBodyParam.paramName.underlineToCamel()}, ")
        }

        for (fieldParam in viewDataRetrofitApi.fieldParams) {
            stringBuilder.append("@Field(\"${fieldParam.paramName}\") ${fieldParam.paramType} ${fieldParam.paramName.underlineToCamel()}, ")
        }

        val resultTrim = stringBuilder.toString().trim()
        if (resultTrim.isNotBlank() && resultTrim.last() == ',' && resultTrim.length > 1) {
            return resultTrim.substring(0, resultTrim.length - 1)
        }

        return resultTrim
    }
}