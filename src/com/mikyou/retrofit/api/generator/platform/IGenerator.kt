package com.mikyou.retrofit.api.generator.platform

import com.intellij.openapi.actionSystem.AnActionEvent
import com.mikyou.retrofit.api.generator.ui.model.ViewDataModelClass
import com.mikyou.retrofit.api.generator.ui.model.ViewDataParams
import com.mikyou.retrofit.api.generator.ui.model.ViewDataRetrofitApi
import com.mikyou.retrofit.api.generator.ui.model.ViewDataRetrofitApiWrapper

interface IGenerator {
    fun evaluateApi(retrofitApiList: List<ViewDataRetrofitApi>): String

    fun evaluateInterfaceApi(anActionEvent: AnActionEvent, retrofitApiWrapper: ViewDataRetrofitApiWrapper, exportPath: String)

    fun evaluateModel(anActionEvent: AnActionEvent, retrofitApi: ViewDataRetrofitApi, exportPath: String)

    fun getFileSuffix(): String

    fun getGenericUnknownSymbol(): String

    fun typeByValue(key: String, type: Any, viewDataModelClassList: MutableList<ViewDataModelClass>): String

    fun composeModelParamsString(modelParams: MutableList<ViewDataParams>): String
}