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

class JavaRxJavaGenerator : IGenerator {
    override fun evaluateModel(anActionEvent: AnActionEvent, retrofitApi: ViewDataRetrofitApi, exportPath: String) {
        VelocityEngineHelper.evaluateToFile(anActionEvent, retrofitApi, PATH_TEMPLATE_MODEL, exportPath)
    }

    override fun evaluateApi(retrofitApiList: List<ViewDataRetrofitApi>): String {
        return VelocityEngineHelper.evaluate(composeParams(retrofitApiList), PATH_TEMPLATE_API)
    }

    override fun getFileSuffix(): String {
        return "java"
    }

    override fun getGenericUnknownSymbol(): String {
        return "?" //List<?>
    }

    override fun typeByValue(key: String, type: Any, viewDataModelClassList: MutableList<ViewDataModelClass>): String {
        return when (type) {
            is Int -> "int"
            is Double -> "double"
            is Boolean -> "boolean"
            is Long -> "long"
            is String -> "String"
            is Float -> "float"
            is Short -> "short"
            is Byte -> "byte"
            is JSONObject -> JsonStrParseHelper.parseSubClass(this, key, type, viewDataModelClassList)
            is JSONArray -> "List<${JsonStrParseHelper.parseSubClass(this, key, type, viewDataModelClassList)}>"
            else -> "Object"
        }
    }

    override fun composeModelParamsString(modelParams: MutableList<ViewDataParams>): String {
        return modelParams.joinToString(separator = ",\n") { "public ${it.paramType} ${it.paramName}" }
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

    companion object {
        private const val PATH_TEMPLATE_MODEL: String = "/template/model/Model_Java.vm"
        private const val PATH_TEMPLATE_API: String = "/template/api/Api_Java_RxJava.vm"
    }
}