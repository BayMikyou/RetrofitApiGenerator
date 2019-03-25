package com.mikyou.retrofit.api.generator.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.mikyou.retrofit.api.generator.ext.showDialog
import com.mikyou.retrofit.api.generator.ui.RetrofitApiGeneratorDialog
import com.mikyou.retrofit.api.generator.ui.model.ViewDataRetrofitApi

class RetrofitApiGeneratorAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent?) {
        if (event == null || event.project == null) {
            return
        }

        val retrofitApiGeneratorDialog = RetrofitApiGeneratorDialog()
        retrofitApiGeneratorDialog.setRetrofitApiGenerateCallback(object : RetrofitApiGeneratorDialog.RetrofitApiGenerateCallback {
            override fun onGenerateClicked(viewDataRetrofitApis: MutableList<ViewDataRetrofitApi>) {
                println("generate params $viewDataRetrofitApis")
            }

            override fun onCancelClicked() {

            }

        })
        retrofitApiGeneratorDialog.showDialog()
    }
}