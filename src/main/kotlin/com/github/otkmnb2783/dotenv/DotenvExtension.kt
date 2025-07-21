package io.github.nickolasjadams.dotenv

import org.gradle.api.*
import java.io.*

open class DotenvExtension(
    private val project: Project,
    private val env: MutableMap<String, String>,
    private val systemProperty: String?,
    private val extraEnv: String?
) {

    private val observer = object : Observer {
        override fun update() {
            load()
        }
    }

    var dir by PropertySubject(project, observer, String::class.java, "")

    internal fun load() {
        var envFileExt: String? = "development" // default file to check.
        val fallback: String = ".env" // fallback to .env

        println("Loading dotenv file extension passed from system property specified at ext { envSystemProperty }: $systemProperty. (.env is used if not specified.)")
        println("Loading and merging an additional dotenv file if specified in system property \"envExtraFile\": $extraEnv.")

        // Check if a specific environment file is set via system property
        if (systemProperty != null) {
            try {
                envFileExt = System.getProperty(systemProperty)
                println("Value of $systemProperty is: $envFileExt")
            } catch (e: Exception) {
                // This is optional.
                // Fail gracefully so we can continue.
            }
        }

        // Load the first dotenv file based on the environment extension or fallback to .env
        loadFile(".env.$envFileExt", fallback)

        // Check if an additional environment file is specified
        if (extraEnv != null) {
            try {
                println("Value of envExtraFile is: $extraEnv")

                // Load and merge the extra dotenv file if specified
                loadFile(extraEnv)
            } catch (e: Exception) {
                // This is optional.
                // Fail gracefully so we can continue.
            }
        }

        project.logger.debug("dotenv file is loaded. env:={}", env)
        project.logger.info("dotenv file is loaded.")
    }

    internal fun loadFile(filename: String, fallbackFilename: String? = null) {
        val envFileName = filename
        val dir = dir.let { if (it.isNotEmpty()) project.file(it) else project.rootDir }
        val fileName by PropertySubject(project, observer, String::class.java, envFileName)
        val separator = File.separator

        project.logger.debug("loaded configuration dir:={}", dir)
        project.logger.debug("loaded configuration fileName:={}", fileName)

        var f = File("$dir$separator$envFileName")
        project.logger.debug("dotenv file path:={}", f)
        if (!f.exists()) {
            if (fallbackFilename == null) {
                return
            }
            val defaultFile = File("$dir$separator$fallbackFilename")
            if (!defaultFile.exists()) {
                project.logger.warn("dotenv file is not found.")
                return
            } else {
                f = defaultFile
            }
        }
        f.forEachLine {
            project.logger.debug("line is:={}", it)
            if (it.ifBlank { "#" }.trim().isComment() || it.ifBlank { ";" }.trim().isComment()) {
                project.logger.debug("skipping whitespace or comment line.")
                return@forEachLine
            }
            val matcher = it.parse() ?: return@forEachLine
            val (key, _, value) = matcher.destructured
            project.logger.debug("line is env definition matched: key:={}value:={}", key, value)
            env[key] = value.normalized()
        }
    }
}
