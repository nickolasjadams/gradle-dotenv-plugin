package io.github.nickolasjadams.dotenv

import io.kotlintest.*
import org.gradle.testfixtures.ProjectBuilder
import kotlin.test.Test
import kotlin.test.assertNotNull

class DotenvPluginTest {
    @Test fun `plugin load test`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.github.nickolasjadams.dotenv")
        project.plugins.getPlugin(DotenvPlugin::class.java) shouldNotBe null
    }
}
