package io.runescript.plugin.ide.highlight

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import io.runescript.plugin.ide.RsIcons
import io.runescript.plugin.ide.RuneScriptBundle
import io.runescript.plugin.lang.lexer.RuneScriptLexerInfo
import javax.swing.Icon

class RsColorSettingsPage : ColorSettingsPage {

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> {
        return DESCRIPTORS
    }

    override fun getColorDescriptors(): Array<ColorDescriptor> {
        return ColorDescriptor.EMPTY_ARRAY
    }

    override fun getDisplayName(): String {
        return RuneScriptBundle.message("runescript.title")
    }

    override fun getIcon(): Icon {
        return RsIcons.ClientScript
    }

    override fun getHighlighter(): SyntaxHighlighter {
        return RsSyntaxHighlighter(LEXER_INFO)
    }

    override fun getDemoText(): String {
        return DEMO_TEXT
    }

    override fun getAdditionalHighlightingTagToDescriptorMap(): MutableMap<String, TextAttributesKey>? {
        return TAG_TO_DESCRIPTOR_MAP
    }

    companion object {
        private val DEMO_TEXT = """
            <RUNESCRIPT_SCRIPT_DECLARATION>[proc,countodd]</RUNESCRIPT_SCRIPT_DECLARATION>(int ${'$'}count, intarray ${'$'}array)(int)
            def_int <RUNESCRIPT_LOCAL_VARIABLE>${'$'}index</RUNESCRIPT_LOCAL_VARIABLE> = 0;
            def_int <RUNESCRIPT_LOCAL_VARIABLE>${'$'}odd_count</RUNESCRIPT_LOCAL_VARIABLE> = 0;
            while (<RUNESCRIPT_LOCAL_VARIABLE>${'$'}index</RUNESCRIPT_LOCAL_VARIABLE> < <RUNESCRIPT_LOCAL_VARIABLE>${'$'}count</RUNESCRIPT_LOCAL_VARIABLE>) {
                if (<RUNESCRIPT_COMMAND_CALL>testbit</RUNESCRIPT_COMMAND_CALL>(<RUNESCRIPT_LOCAL_VARIABLE>${'$'}array</RUNESCRIPT_LOCAL_VARIABLE>(<RUNESCRIPT_LOCAL_VARIABLE>${'$'}index</RUNESCRIPT_LOCAL_VARIABLE>), 0) = 1) {
                    <RUNESCRIPT_LOCAL_VARIABLE>${'$'}odd_count</RUNESCRIPT_LOCAL_VARIABLE> = <RUNESCRIPT_COMMAND_CALL>add</RUNESCRIPT_COMMAND_CALL>>(<RUNESCRIPT_LOCAL_VARIABLE>${'$'}odd_count</RUNESCRIPT_LOCAL_VARIABLE>, 1);
                }
                <RUNESCRIPT_LOCAL_VARIABLE>${'$'}index</RUNESCRIPT_LOCAL_VARIABLE> = calc(<RUNESCRIPT_LOCAL_VARIABLE>${'$'}index</RUNESCRIPT_LOCAL_VARIABLE> + 1);
            }
            /*
             * First line of multi line comment.
             * Second line of multi line comment.
             */
            switch_int (<RUNESCRIPT_LOCAL_VARIABLE>${'$'}odd_count</RUNESCRIPT_LOCAL_VARIABLE>) {
                case 0 :
                    <RUNESCRIPT_PROC_CALL>~debug_mes</RUNESCRIPT_PROC_CALL>("<col=ff0000>No odd numbers were found.</col>");
                case 1 :
                    <RUNESCRIPT_PROC_CALL>~debug_mes</RUNESCRIPT_PROC_CALL>("Only one odd number was found.");
                case default :
                    <RUNESCRIPT_PROC_CALL>~debug_mes</RUNESCRIPT_PROC_CALL>("Many odd numbers were found (<RUNESCRIPT_COMMAND_CALL>tostring</RUNESCRIPT_COMMAND_CALL>>(<RUNESCRIPT_LOCAL_VARIABLE>${'$'}odd_count</RUNESCRIPT_LOCAL_VARIABLE>)>).");
            }
            // Single line comment.
            return(<RUNESCRIPT_LOCAL_VARIABLE>${'$'}odd_count</RUNESCRIPT_LOCAL_VARIABLE>);
         """.trimIndent()
        private val DESCRIPTORS = arrayOf(
            AttributesDescriptor(RuneScriptBundle.message("runescript.color.settings.description.identifier"), RsSyntaxHighlighterColors.IDENTIFIER),
            AttributesDescriptor(RuneScriptBundle.message("runescript.color.settings.description.number"), RsSyntaxHighlighterColors.NUMBER),
            AttributesDescriptor(RuneScriptBundle.message("runescript.color.settings.description.keyword"), RsSyntaxHighlighterColors.KEYWORD),
            AttributesDescriptor(RuneScriptBundle.message("runescript.color.settings.description.type_name"), RsSyntaxHighlighterColors.TYPE_NAME),
            AttributesDescriptor(RuneScriptBundle.message("runescript.color.settings.description.array_type_name"), RsSyntaxHighlighterColors.ARRAY_TYPE_NAME),
            AttributesDescriptor(RuneScriptBundle.message("runescript.color.settings.description.string"), RsSyntaxHighlighterColors.STRING),
            AttributesDescriptor(RuneScriptBundle.message("runescript.color.settings.description.string_tag"), RsSyntaxHighlighterColors.STRING_TAG),
            AttributesDescriptor(RuneScriptBundle.message("runescript.color.settings.description.block_comment"), RsSyntaxHighlighterColors.BLOCK_COMMENT),
            AttributesDescriptor(RuneScriptBundle.message("runescript.color.settings.description.line_comment"), RsSyntaxHighlighterColors.LINE_COMMENT),
            AttributesDescriptor(RuneScriptBundle.message("runescript.color.settings.description.operation_sign"), RsSyntaxHighlighterColors.OPERATION_SIGN),
            AttributesDescriptor(RuneScriptBundle.message("runescript.color.settings.description.braces"), RsSyntaxHighlighterColors.BRACES),
            AttributesDescriptor(RuneScriptBundle.message("runescript.color.settings.description.semicolon"), RsSyntaxHighlighterColors.SEMICOLON),
            AttributesDescriptor(RuneScriptBundle.message("runescript.color.settings.description.comma"), RsSyntaxHighlighterColors.COMMA),
            AttributesDescriptor(RuneScriptBundle.message("runescript.color.settings.description.parenthesis"), RsSyntaxHighlighterColors.PARENTHESIS),
            AttributesDescriptor(RuneScriptBundle.message("runescript.color.settings.description.brackets"), RsSyntaxHighlighterColors.BRACKETS),
            AttributesDescriptor(RuneScriptBundle.message("runescript.color.settings.description.local_variable"), RsSyntaxHighlighterColors.LOCAL_VARIABLE),
            AttributesDescriptor(RuneScriptBundle.message("runescript.color.settings.description.script_declaration"), RsSyntaxHighlighterColors.SCRIPT_DECLARATION),
            AttributesDescriptor(RuneScriptBundle.message("runescript.color.settings.description.command_call"), RsSyntaxHighlighterColors.COMMAND_CALL),
            AttributesDescriptor(RuneScriptBundle.message("runescript.color.settings.description.proc_call"), RsSyntaxHighlighterColors.PROC_CALL),
        )
        private val TAG_TO_DESCRIPTOR_MAP = mutableMapOf(
            "RUNESCRIPT_IDENTIFIER" to RsSyntaxHighlighterColors.IDENTIFIER,
            "RUNESCRIPT_NUMBER" to RsSyntaxHighlighterColors.NUMBER,
            "RUNESCRIPT_KEYWORD" to RsSyntaxHighlighterColors.KEYWORD,
            "RUNESCRIPT_TYPE_NAME" to RsSyntaxHighlighterColors.TYPE_NAME,
            "RUNESCRIPT_ARRAY_TYPE_NAME" to RsSyntaxHighlighterColors.ARRAY_TYPE_NAME,
            "RUNESCRIPT_STRING" to RsSyntaxHighlighterColors.STRING,
            "RUNESCRIPT_STRING_TAG" to RsSyntaxHighlighterColors.STRING_TAG,
            "RUNESCRIPT_BLOCK_COMMENT" to RsSyntaxHighlighterColors.BLOCK_COMMENT,
            "RUNESCRIPT_LINE_COMMENT" to RsSyntaxHighlighterColors.LINE_COMMENT,
            "RUNESCRIPT_OPERATION_SIGN" to RsSyntaxHighlighterColors.OPERATION_SIGN,
            "RUNESCRIPT_BRACES" to RsSyntaxHighlighterColors.BRACES,
            "RUNESCRIPT_SEMICOLON" to RsSyntaxHighlighterColors.SEMICOLON,
            "RUNESCRIPT_COMMA" to RsSyntaxHighlighterColors.COMMA,
            "RUNESCRIPT_PARENTHESIS" to RsSyntaxHighlighterColors.PARENTHESIS,
            "RUNESCRIPT_BRACKETS" to RsSyntaxHighlighterColors.BRACKETS,
            "RUNESCRIPT_LOCAL_VARIABLE" to RsSyntaxHighlighterColors.LOCAL_VARIABLE,
            "RUNESCRIPT_SCRIPT_DECLARATION" to RsSyntaxHighlighterColors.SCRIPT_DECLARATION,
            "RUNESCRIPT_COMMAND_CALL" to RsSyntaxHighlighterColors.COMMAND_CALL,
            "RUNESCRIPT_PROC_CALL" to RsSyntaxHighlighterColors.PROC_CALL,
        )

        private val LEXER_INFO = RuneScriptLexerInfo(mutableListOf("int", "component", "string"))
    }
}