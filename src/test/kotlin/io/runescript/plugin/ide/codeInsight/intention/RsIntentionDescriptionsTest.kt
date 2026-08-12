package io.runescript.plugin.ide.codeInsight.intention

import junit.framework.TestCase
import java.nio.file.Files
import java.nio.file.Path

class RsIntentionDescriptionsTest : TestCase() {
    fun testEveryRegisteredIntentionHasDescription() {
        val pluginXml = Files.readString(Path.of("src/main/resources/META-INF/plugin.xml"))
        val classNames =
            CLASS_NAME_PATTERN
                .findAll(pluginXml)
                .map { it.groupValues[1].substringAfterLast('.') }
                .toList()

        assertTrue("No intentions found in plugin.xml", classNames.isNotEmpty())
        val missing =
            classNames.filter { className ->
                javaClass.classLoader.getResource("intentionDescriptions/$className/description.html") == null
            }
        assertTrue("Missing intention descriptions: ${missing.joinToString()}", missing.isEmpty())
    }

    companion object {
        private val CLASS_NAME_PATTERN = "<className>([^<]+)</className>".toRegex()
    }
}
