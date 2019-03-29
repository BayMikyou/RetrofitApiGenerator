package com.mikyou.retrofit.api.generator.platform

import com.intellij.openapi.actionSystem.AnActionEvent
import com.mikyou.retrofit.api.generator.ext.underlineToCamel
import com.mikyou.retrofit.api.generator.helper.JsonStrParseHelper
import com.mikyou.retrofit.api.generator.helper.VelocityEngineHelper
import com.mikyou.retrofit.api.generator.ui.model.ViewDataModelClass
import com.mikyou.retrofit.api.generator.ui.model.ViewDataParams
import com.mikyou.retrofit.api.generator.ui.model.ViewDataRetrofitApi
import org.json.JSONArray
import org.json.JSONObject

class KotlinRxJavaGenerator : IGenerator {
    override fun evaluateModel(anActionEvent: AnActionEvent, retrofitApi: ViewDataRetrofitApi, exportPath: String) {
        VelocityEngineHelper.evaluateToFile(anActionEvent, retrofitApi, PATH_TEMPLATE_MODEL, exportPath)
    }

    override fun evaluateApi(retrofitApiList: List<ViewDataRetrofitApi>): String {
        return VelocityEngineHelper.evaluate(composeParams(retrofitApiList), PATH_TEMPLATE_API)
    }

    override fun getFileSuffix(): String {
        return "kt"
    }

    override fun getGenericUnknownSymbol(): String {
        return "*" //List<*>
    }

    override fun typeByValue(key: String, type: Any, viewDataModelClassList: MutableList<ViewDataModelClass>): String {
        return when (type) {
            is Int -> "Int"
            is Double -> "Double"
            is Boolean -> "Boolean"
            is Long -> "Long"
            is String -> "String"
            is Float -> "Float"
            is Short -> "Short"
            is Byte -> "Byte"
            is JSONObject -> JsonStrParseHelper.parseSubClass(this, key, type, viewDataModelClassList)
            is JSONArray -> "List<${JsonStrParseHelper.parseSubClass(this, key, type, viewDataModelClassList)}>"
            else -> "Any"
        }
    }


    override fun composeModelParamsString(modelParams: MutableList<ViewDataParams>): String {
        return modelParams.joinToString(separator = ",\n") { "val ${it.paramName}: ${it.paramType}" }
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
        if (resultTrim.isNotBlank() && resultTrim.last() == ',' && resultTrim.length > 1) {
            return resultTrim.substring(0, resultTrim.length - 1)
        }

        return resultTrim
    }

    companion object {
        private const val PATH_TEMPLATE_MODEL: String = "/template/model/Model_Kotlin.vm"
        private const val PATH_TEMPLATE_API: String = "/template/api/Api_Kotlin_RxJava.vm"
    }
}