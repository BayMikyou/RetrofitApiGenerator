package com.mikyou.retrofit.api.generator.helper

import com.mikyou.retrofit.api.generator.ext.underlineToCamel
import com.mikyou.retrofit.api.generator.platform.IGenerator
import com.mikyou.retrofit.api.generator.ui.model.ViewDataModelClass
import com.mikyou.retrofit.api.generator.ui.model.ViewDataParams
import org.json.JSONArray
import org.json.JSONObject

object JsonStrParseHelper {
    fun parse(generator: IGenerator, rootModelName: String, jsonString: String, listenerBuilder: ListenerBuilder.() -> Unit) {
        val modelClassList: MutableList<ViewDataModelClass> = mutableListOf()
        val listener = ListenerBuilder().also(listenerBuilder)
        val jsonTrim = jsonString.trim()
        if (jsonTrim.startsWith(prefix = "[")) {
            val jsonArray = JSONArray(jsonTrim)
            if (jsonArray.length() > 0 && jsonArray[0] is JSONObject) {
                handleJsonObject(generator, rootModelName, modelClassList, jsonArray[0] as JSONObject, listener)
            }
        } else if (jsonTrim.startsWith(prefix = "{")) {
            var jsonObject: JSONObject? = null
            try {
                jsonObject = JSONObject(jsonTrim)
            } catch (e: Exception) {
                listener.mJsonParseErrorAction?.invoke(e)
            }

            if (jsonObject != null) {
                handleJsonObject(generator, rootModelName, modelClassList, jsonObject, listener)
            }
        }
    }

    private fun handleJsonObject(generator: IGenerator, rootModelName: String, modelClassList: MutableList<ViewDataModelClass>, jsonObject: JSONObject, listener: ListenerBuilder) {
        parseJsonObject(generator, rootModelName, modelClassList, jsonObject)
        listener.mJsonParseCompletedAction?.invoke(modelClassList)
    }

    private fun parseJsonObject(generator: IGenerator, rootModelName: String, modelClassList: MutableList<ViewDataModelClass>, jsonObject: JSONObject) {
        val keySet = jsonObject.keySet()
        val viewDataModelClass = ViewDataModelClass(rootModelName)
        for (key in keySet) {
            val viewDataParam = ViewDataParams(key.underlineToCamel(), generator.typeByValue(key, jsonObject[key], modelClassList))
            viewDataModelClass.modelParams.add(viewDataParam)
        }

        modelClassList.add(viewDataModelClass)
    }

    fun parseSubClass(generator: IGenerator, key: String, type: Any, viewDataModelClassList: MutableList<ViewDataModelClass>): String {
        if (type is JSONObject) {
            val subClassName = getSubClassName(key)
            parseJsonObject(generator, subClassName, viewDataModelClassList, type)
            return subClassName
        } else if (type is JSONArray && type.length() > 0) {
            return listTypeByValue(generator, key, type.get(0), viewDataModelClassList)
        }

        return generator.getGenericUnknownSymbol()
    }

    private fun listTypeByValue(generator: IGenerator, key: String, type: Any, viewDataModelClassList: MutableList<ViewDataModelClass>): String {
        return when (type) {
            is JSONObject -> parseSubClass(generator, key, type, viewDataModelClassList)
            is JSONArray -> "List<${parseSubClass(generator, key, type, viewDataModelClassList)}>"
            else -> type.javaClass.simpleName
        }
    }


    private fun getSubClassName(key: String): String {
        if (key.isBlank()) {
            return key
        }
        val strings = key.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val stringBuilder = StringBuilder()
        for (string in strings) {
            stringBuilder.append(captureName(string))
        }

        return stringBuilder.toString()
    }

    private fun captureName(name: String): String {
        if (name.isEmpty()) {
            return ""
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1)
    }
}

class ListenerBuilder {
    internal var mJsonParseCompletedAction: ((List<ViewDataModelClass>) -> Unit)? = null
    internal var mJsonParseErrorAction: ((Exception) -> Unit)? = null
    fun onJsonParseCompleted(action: (List<ViewDataModelClass>) -> Unit) {
        mJsonParseCompletedAction = action
    }

    fun onJsonParseError(action: (Exception) -> Unit) {
        mJsonParseErrorAction = action
    }
}
