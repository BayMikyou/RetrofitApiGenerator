package com.mikyou.retrofit.api.generator.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.vfs.VirtualFile
import com.mikyou.retrofit.api.generator.ext.showDialog
import com.mikyou.retrofit.api.generator.helper.InsertCodeHelper
import com.mikyou.retrofit.api.generator.helper.JsonStrParseHelper
import com.mikyou.retrofit.api.generator.helper.VelocityEngineHelper
import com.mikyou.retrofit.api.generator.platform.IGenerator
import com.mikyou.retrofit.api.generator.platform.JavaRxJavaGenerator
import com.mikyou.retrofit.api.generator.platform.KotlinCoroutineGenerator
import com.mikyou.retrofit.api.generator.platform.KotlinRxJavaGenerator
import com.mikyou.retrofit.api.generator.ui.RetrofitApiGeneratorDialog
import com.mikyou.retrofit.api.generator.ui.model.ViewDataGeneratorType
import com.mikyou.retrofit.api.generator.ui.model.ViewDataModelClass
import com.mikyou.retrofit.api.generator.ui.model.ViewDataRetrofitApi
import com.mikyou.retrofit.api.generator.ui.model.ViewDataRetrofitApiWrapper

class RightGenerateAction : AnAction() {
    private lateinit var mGenerator: IGenerator
    override fun actionPerformed(event: AnActionEvent?) {
        if (event == null || event.project == null) {
            return
        }
        VelocityEngineHelper.initEngine()
        val retrofitApiGeneratorDialog = RetrofitApiGeneratorDialog(true)
        retrofitApiGeneratorDialog.setRetrofitApiGenerateCallback(object : RetrofitApiGeneratorDialog.RetrofitApiGenerateCallback {
            override fun onGenerateClicked(retrofitApisWrapper: ViewDataRetrofitApiWrapper) {
                generateCode(event, retrofitApisWrapper)
                println("generate params ${retrofitApisWrapper.viewDataRetrofitApis}")
            }

            override fun onCancelClicked() {

            }

        })
        retrofitApiGeneratorDialog.showDialog()
    }

    private fun generateCode(event: AnActionEvent, retrofitApisWrapper: ViewDataRetrofitApiWrapper) {
        mGenerator = getPlatformGenerator(retrofitApisWrapper.generatorType)
        try {
            val exportPath = getInterfaceExportPath(event, retrofitApisWrapper.interfaceName)
            mGenerator.evaluateInterfaceApi(event, retrofitApisWrapper, exportPath)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            generateModelCode(event, retrofitApisWrapper.viewDataRetrofitApis)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun generateModelCode(event: AnActionEvent, viewDataRetrofitApis: List<ViewDataRetrofitApi>) {
        for (viewDataRetrofitApi in viewDataRetrofitApis) {
            val exportPath = getModelExportPath(event, viewDataRetrofitApi.response.modelName)
            println("export path: $exportPath")
            JsonStrParseHelper.parse(mGenerator, viewDataRetrofitApi.response.modelName, viewDataRetrofitApi.response.modelJson) {
                onJsonParseCompleted {
                    viewDataRetrofitApi.response.modelClassList.clear()
                    viewDataRetrofitApi.response.modelClassList.addAll(getComposeViewDataClassList(it))
                }

                onJsonParseError {
                    println(it.message)
                }
            }

            mGenerator.evaluateModel(event, viewDataRetrofitApi, exportPath)
        }
    }

    private fun getComposeViewDataClassList(modelClassList: List<ViewDataModelClass>): List<ViewDataModelClass> {
        return modelClassList.map { modelClass ->
            modelClass.modelParamsString = mGenerator.composeModelParamsString(modelClass.modelParams)
            return@map modelClass
        }
    }

    private fun getModelExportPath(event: AnActionEvent, modelClassName: String): String {
        val selectedVirtualFile = (DataKeys.VIRTUAL_FILE.getData(event.dataContext) as VirtualFile)
        val parentPath = if (selectedVirtualFile.isDirectory) {
            selectedVirtualFile.path
        } else {
            selectedVirtualFile.parent.path
        }

        return "$parentPath/model/$modelClassName.${mGenerator.getFileSuffix()}"
    }

    private fun getInterfaceExportPath(event: AnActionEvent, interfaceClassName: String): String {
        val selectedVirtualFile = (DataKeys.VIRTUAL_FILE.getData(event.dataContext) as VirtualFile)
        val parentPath = if (selectedVirtualFile.isDirectory) {
            selectedVirtualFile.path
        } else {
            selectedVirtualFile.parent.path
        }

        return "$parentPath/$interfaceClassName.${mGenerator.getFileSuffix()}"
    }

    private fun getPlatformGenerator(generatorType: ViewDataGeneratorType): IGenerator {
        return when (generatorType) {
            ViewDataGeneratorType.KOTLIN_COROUTINE -> KotlinCoroutineGenerator()
            ViewDataGeneratorType.KOTLIN_RXJAVA -> KotlinRxJavaGenerator()
            ViewDataGeneratorType.JAVA_RXJAVA -> JavaRxJavaGenerator()
        }
    }

    override fun update(event: AnActionEvent?) {
        event ?: return

        if (event.project == null) {
            event.presentation.isEnabled = false
        }

        val editor: Editor? = event.dataContext.getData(DataKeys.EDITOR)
        if (editor == null) {
            event.presentation.isEnabled = false
        }

        event.presentation.isEnabled = true
    }
}