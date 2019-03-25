package com.mikyou.retrofit.api.generator.helper

import com.intellij.openapi.util.io.FileUtil
import com.mikyou.retrofit.api.generator.ui.model.ViewDataRetrofitApi
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.runtime.RuntimeConstants
import org.apache.velocity.runtime.log.NullLogChute
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader
import java.io.StringWriter

object VelocityEngineHelper {
    private lateinit var mVelocityEngine: VelocityEngine
    fun initEngine() {
        mVelocityEngine = VelocityEngine().apply {
            setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath")
            setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, NullLogChute::class.java.name)
            setProperty("classpath.resource.loader.class", ClasspathResourceLoader::class.java.name)
            init()
        }
    }

    fun evaluate(retrofitApiList: List<ViewDataRetrofitApi>, templatePath: String): String {
        val velocityTemplate: String = FileUtil.loadTextAndClose(this::class.java.getResourceAsStream(templatePath))
        val velocityContext = VelocityContext().apply {
            put("retrofitApiList", retrofitApiList)
        }
        val sWriter = StringWriter()
        mVelocityEngine.evaluate(velocityContext, sWriter, "", velocityTemplate)
        return sWriter.toString()
    }
}