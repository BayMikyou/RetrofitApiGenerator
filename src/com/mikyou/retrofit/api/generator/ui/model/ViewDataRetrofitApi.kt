package com.mikyou.retrofit.api.generator.ui.model


data class ViewDataParams(
        val paramName: String,
        val paramType: String
)

data class ViewDataResponse(
        val modelName: String,
        val isList: Boolean,
        val modelJson: String,
        val modelClassList: MutableList<ViewDataModelClass>
)

data class ViewDataModelClass(
        val className: String,
        val modelParams: MutableList<ViewDataParams> = mutableListOf(),
        var modelParamsString: String = ""
)

enum class ViewDataSupportLanguage {
    LANGUAGE_KOTLIN,
    LANGUAGE_JAVA
}

enum class ViewDataSupportLibrary {
    LIBRARY_RXJAVA,
    LIBRARY_COROUTINE
}

enum class ViewDataGeneratorType {
    KOTLIN_RXJAVA,
    KOTLIN_COROUTINE,
    JAVA_RXJAVA
}

data class ViewDataRetrofitApi(
        val requestMethod: String,
        val requestUrl: String,
        val isFormUrlEncoded: Boolean,
        val headerParams: List<ViewDataParams>,
        val requestBodyParams: List<ViewDataParams>,
        val queryParams: List<ViewDataParams>,
        val fieldParams: List<ViewDataParams>,
        val paths: Set<String>,
        var paramsString: String,
        val response: ViewDataResponse,
        val methodName: String,
        val supportLanguage: ViewDataSupportLanguage,
        val supportLibrary: ViewDataSupportLibrary
)

data class ViewDataRetrofitApiWrapper(
        val viewDataRetrofitApis: List<ViewDataRetrofitApi>,
        val interfaceName: String,
        val generatorType: ViewDataGeneratorType
)