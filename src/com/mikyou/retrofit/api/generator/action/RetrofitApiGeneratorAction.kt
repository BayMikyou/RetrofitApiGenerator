package com.mikyou.retrofit.api.generator.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataKeys
import com.intellij.openapi.editor.Editor
import com.mikyou.retrofit.api.generator.ext.showDialog
import com.mikyou.retrofit.api.generator.helper.InsertCodeHelper
import com.mikyou.retrofit.api.generator.helper.VelocityEngineHelper
import com.mikyou.retrofit.api.generator.platform.IGenerator
import com.mikyou.retrofit.api.generator.platform.JavaRxJavaGenerator
import com.mikyou.retrofit.api.generator.platform.KotlinCoroutineGenerator
import com.mikyou.retrofit.api.generator.platform.KotlinRxJavaGenerator
import com.mikyou.retrofit.api.generator.ui.RetrofitApiGeneratorDialog
import com.mikyou.retrofit.api.generator.ui.model.ViewDataGeneratorType
import com.mikyou.retrofit.api.generator.ui.model.ViewDataRetrofitApiWrapper

class RetrofitApiGeneratorAction : AnAction() {
    private lateinit var mGenerator: IGenerator
    override fun actionPerformed(event: AnActionEvent?) {
        if (event == null || event.project == null) {
            return
        }
        VelocityEngineHelper.initEngine()
        val retrofitApiGeneratorDialog = RetrofitApiGeneratorDialog()
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
        val generateCode: String = mGenerator.evaluate(retrofitApisWrapper.viewDataRetrofitApis)
        val editor: Editor? = event.dataContext.getData(DataKeys.EDITOR)
        if (editor != null) {
            try {
                InsertCodeHelper.doInsert(generateCode, editor)
                println("generated code is succeed, generated code is :\n $generateCode")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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