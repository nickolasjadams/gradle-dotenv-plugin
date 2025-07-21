package io.github.nickolasjadams.dotenv

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logging
import java.io.File

private const val EXTENSION_NAME = "dotenv"
private const val VAR_NAME = "env"

class DotenvPlugin: Plugin<Project> {

    private val logger = Logging.getLogger(DotenvPlugin::class.java)

    override fun apply(project: Project) {
        logger.debug("gradle dotenv plugin applying")
        val systemProperty = project.extensions.extraProperties
            .takeIf { it.has("envSystemProperty") }
            ?.get("envSystemProperty") as? String
        // First check if defined in ext block in build.gradle
        var extraEnv = project.extensions.extraProperties
            .takeIf { it.has("envExtraFile") }
            ?.get("envExtraFile") as? String
        if (extraEnv == null) {
            // Check if the envExtraFile system property is set
            try {
                extraEnv = System.getProperty("envExtraFile")
            } catch (e: Exception) {
                // This is optional.
                // Fail gracefully so we can continue.
            }
        }
        val env = mutableMapOf<String, String>()
        project.extensions.add(VAR_NAME, env)
        val extension = project.extensions.create(EXTENSION_NAME, DotenvExtension::class.java, project, env, systemProperty, extraEnv)
        extension.load()
        logger.debug("gradle dotenv plugin applied.")
    }
}
