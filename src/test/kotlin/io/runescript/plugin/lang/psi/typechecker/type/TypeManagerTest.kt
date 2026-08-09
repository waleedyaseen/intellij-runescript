package io.runescript.plugin.lang.psi.typechecker.type

import junit.framework.TestCase

class TypeManagerTest : TestCase() {
    fun testTypeOptionsAreIsolatedBetweenManagers() {
        val first = TypeManager().apply { registerAll<PrimitiveType>() }
        val second = TypeManager().apply { registerAll<PrimitiveType>() }
        val longType = first.find("long")

        first.changeOptions("long") {
            allowDeclaration = false
        }

        assertFalse(first.getOptions(longType).allowDeclaration)
        assertTrue(second.getOptions(longType).allowDeclaration)
        assertTrue(longType.options.allowDeclaration)
    }
}
