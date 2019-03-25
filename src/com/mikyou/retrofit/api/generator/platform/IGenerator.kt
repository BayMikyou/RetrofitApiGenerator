package com.mikyou.retrofit.api.generator.platform

import com.mikyou.retrofit.api.generator.ui.model.ViewDataRetrofitApi

interface IGenerator {
    fun evaluate(retrofitApiList: List<ViewDataRetrofitApi>): String
}