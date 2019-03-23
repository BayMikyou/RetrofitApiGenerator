package com.mikyou.retrofit.api.generator.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.mikyou.retrofit.api.generator.ext.showDialog
import com.mikyou.retrofit.api.generator.ui.RetrofitApiGeneratorDialog

class RetrofitApiGeneratorAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent?) {
        if (event == null || event.project == null) {
            return
        }

        RetrofitApiGeneratorDialog().showDialog()
    }
}