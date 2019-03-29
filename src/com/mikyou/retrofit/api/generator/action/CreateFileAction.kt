package com.mikyou.retrofit.api.generator.action

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.DataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFileManager
import org.apache.commons.io.FileUtils
import java.io.File
import java.nio.charset.Charset

class CreateFileAction(exportFile: String, content: String, dataContext: DataContext) : Runnable {
    private val mExportFile: String = exportFile
    private val mContent: String = content
    private val mDataContext: DataContext = dataContext

    override fun run() {
        try {
            val virtualFileManager = VirtualFileManager.getInstance()
            var virtualFile = virtualFileManager.refreshAndFindFileByUrl(VfsUtil.pathToUrl(mExportFile))

            if (virtualFile != null && virtualFile.exists()) {
                virtualFile.setBinaryContent(mContent.toByteArray(Charset.defaultCharset()))
            } else {
                val file = File(mExportFile)
                FileUtils.writeStringToFile(file, mContent)
                virtualFile = virtualFileManager.refreshAndFindFileByUrl(VfsUtil.pathToUrl(mExportFile))
            }

            val finalVirtualFile = virtualFile
            val project = DataKeys.PROJECT.getData(mDataContext)
            if (finalVirtualFile == null || project == null) {
                return
            }

            ApplicationManager.getApplication().invokeLater {
                FileEditorManager.getInstance(project).openFile(finalVirtualFile, true)
            }
        } catch (e: Exception) {
            ApplicationManager.getApplication().invokeLater {
                Messages.showMessageDialog("create file fail", "Generate Code Failed", null)
            }
        }
    }
}