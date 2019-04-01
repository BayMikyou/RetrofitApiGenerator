package com.mikyou.retrofit.api.generator.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages

class RightGenerateSwaggerAction : AnAction() {
    override fun actionPerformed(p0: AnActionEvent?) {
        Messages.showInfoMessage("功能正在开发中...", "来自RetrofitApiGenerator提示")
    }
}