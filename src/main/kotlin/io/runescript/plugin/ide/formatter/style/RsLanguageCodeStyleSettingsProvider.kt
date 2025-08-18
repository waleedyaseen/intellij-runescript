package io.runescript.plugin.ide.formatter.style

import com.intellij.application.options.IndentOptionsEditor
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizableOptions
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider
import io.runescript.plugin.lang.RuneScript


class RsLanguageCodeStyleSettingsProvider : LanguageCodeStyleSettingsProvider() {

    override fun getLanguage() = RuneScript

    override fun customizeSettings(consumer: CodeStyleSettingsCustomizable, settingsType: SettingsType) {
        if (settingsType == SettingsType.SPACING_SETTINGS) {
            val spacesWithin = CodeStyleSettingsCustomizableOptions.getInstance().SPACES_WITHIN
            val spacesBeforeParen = CodeStyleSettingsCustomizableOptions.getInstance().SPACES_BEFORE_PARENTHESES
            val spacesOther = CodeStyleSettingsCustomizableOptions.getInstance().SPACES_OTHER
            consumer.showStandardOptions(
                // Before parentheses
                "SPACE_BEFORE_IF_PARENTHESES",
                "SPACE_BEFORE_WHILE_PARENTHESES",
                "SPACE_BEFORE_SWITCH_PARENTHESES",
                "SPACE_BEFORE_CALC_PARENTHESES",
                "SPACE_BEFORE_RETURN_PARENTHESES",
                "SPACE_BEFORE_METHOD_CALL_PARENTHESES",


                // Around operators
                "SPACE_AROUND_ASSIGNMENT_OPERATORS",
                "SPACE_AROUND_LOGICAL_OPERATORS",
                "SPACE_AROUND_EQUALITY_OPERATORS",
                "SPACE_AROUND_RELATIONAL_OPERATORS",
                "SPACE_AROUND_BITWISE_OPERATORS",
                "SPACE_AROUND_MULTIPLICATIVE_OPERATORS",
                "SPACE_AROUND_ADDITIVE_OPERATORS",

                // Before keywords
                "SPACE_BEFORE_ELSE_KEYWORD",

                // Before left brace
                "SPACE_BEFORE_SWITCH_LBRACE",
                "SPACE_BEFORE_WHILE_LBRACE",
                "SPACE_BEFORE_IF_LBRACE",

                // Within
                "SPACE_WITHIN_IF_PARENTHESES",
                "SPACE_WITHIN_SWITCH_PARENTHESES",
                "SPACE_WITHIN_WHILE_PARENTHESES",
                "SPACE_WITHIN_PARENTHESES",
                "SPACE_WITHIN_METHOD_CALL_PARENTHESES",
                "SPACE_WITHIN_EMPTY_METHOD_CALL_PARENTHESES",

                // Others
                "SPACE_BEFORE_COMMA",
                "SPACE_AFTER_COMMA",
                "SPACE_AFTER_SEMICOLON",
                "SPACE_BEFORE_SEMICOLON"
            )
            consumer.renameStandardOption("SPACE_BEFORE_METHOD_CALL_PARENTHESES", "Call parentheses")
            consumer.renameStandardOption("SPACE_AROUND_ASSIGNMENT_OPERATORS", "Assignment operators (=)")
            consumer.renameStandardOption("SPACE_AROUND_LOGICAL_OPERATORS", "Logical operators (&, |)")
            consumer.renameStandardOption("SPACE_AROUND_EQUALITY_OPERATORS", "Equality operators (=, !)")
            consumer.renameStandardOption("SPACE_AROUND_BITWISE_OPERATORS", "Bitwise operators (&, |)")
            consumer.renameStandardOption("SPACE_WITHIN_METHOD_CALL_PARENTHESES", "Call parentheses")
            consumer.renameStandardOption("SPACE_WITHIN_EMPTY_METHOD_CALL_PARENTHESES", "Empty call parentheses")
            consumer.renameStandardOption("SPACE_BEFORE_SEMICOLON", "Before semicolon")
            consumer.renameStandardOption("SPACE_AFTER_SEMICOLON", "After semicolon")


            consumer.showCustomOption(
                RsCodeStyleSettings::class.java,
                "SPACE_BEFORE_CALC_PARENTHESES",
                "'calc' parentheses",
                spacesBeforeParen
            )

            consumer.showCustomOption(
                RsCodeStyleSettings::class.java,
                "SPACE_BEFORE_RETURN_PARENTHESES",
                "'return' parentheses",
                spacesBeforeParen
            )

            consumer.showCustomOption(
                RsCodeStyleSettings::class.java,
                "SPACE_WITHIN_CALC_PARENTHESES",
                "'calc' parentheses",
                spacesWithin
            )

            consumer.showCustomOption(
                RsCodeStyleSettings::class.java,
                "SPACE_WITHIN_RETURN_LIST_PARENTHESES",
                "Return list parentheses",
                spacesWithin
            )
            consumer.showCustomOption(
                RsCodeStyleSettings::class.java,
                "SPACE_WITHIN_RETURN_ARGUMENTS_PARENTHESES",
                "Return arguments parentheses",
                spacesWithin
            )

            consumer.showCustomOption(
                RsCodeStyleSettings::class.java,
                "SPACE_WITHIN_ARRAY_BOUNDS",
                "Array bounds",
                spacesWithin
            )

            consumer.showCustomOption(
                RsCodeStyleSettings::class.java,
                "SPACE_BEFORE_ARRAY_BOUNDS",
                "Array bounds",
                spacesBeforeParen
            )

            consumer.showCustomOption(
                RsCodeStyleSettings::class.java,
                "SPACE_BEFORE_COMMA_IN_RETURN_LIST",
                "Before comma in return type",
                spacesOther,
                CodeStyleSettingsCustomizable.OptionAnchor.AFTER,
                "SPACE_AFTER_COMMA"
            )

            consumer.showCustomOption(
                RsCodeStyleSettings::class.java,
                "SPACE_AFTER_COMMA_IN_RETURN_LIST",
                "After comma in return type",
                spacesOther,
                CodeStyleSettingsCustomizable.OptionAnchor.AFTER,
                "SPACE_AFTER_COMMA"
            )

            consumer.showCustomOption(
                RsCodeStyleSettings::class.java,
                "SPACE_BEFORE_COMMA_IN_RETURN_ARGUMENTS",
                "Before comma in return arguments",
                spacesOther,
                CodeStyleSettingsCustomizable.OptionAnchor.AFTER,
                "SPACE_AFTER_COMMA"
            )

            consumer.showCustomOption(
                RsCodeStyleSettings::class.java,
                "SPACE_AFTER_COMMA_IN_RETURN_ARGUMENTS",
                "After comma in return arguments",
                spacesOther,
                CodeStyleSettingsCustomizable.OptionAnchor.AFTER,
                "SPACE_AFTER_COMMA"
            )
        }
    }

    override fun getIndentOptionsEditor(): IndentOptionsEditor {
        return RsIndentOptionsEditor()
    }

    override fun getCodeSample(settingsType: SettingsType) =
        $$"""
            [proc,countodd](int $count, intarray $array)(int)
            def_int $index = 0;
            while (1 = 1){
                def_int $odd_count = 1;
            }
            def_int $odd_count = 0;
            while ($index < $count){
                if ($index = 1) {
                    // Index is 1
                } else if ($index = 2) {
                    // Index is 2
                } else {
                    // Index is otherwise
                }
                if (testbit($array($index),0) = 1) {
                    $odd_count = add($odd_count, 1);
                } else {
                    $test = 1;
                }
                $index = calc($index * 2);
            }
            /*
            * First line of multi line comment.
            */
            switch_int ($odd_count) {
                case 0, 1, 2 :
                    ~debug_mes("<col=ff0000>No odd numbers were found.</col>");
                    hello();
                case 1 :
                    ~debug_mes("Only one odd number was found.");
                case default :
                    ~debug_mes("Many odd numbers were found (<~tostring($odd_count)>).");
            }
            // Single line comment.
            return($odd_count);
            
            [proc,tostring](string $text)
            
            [proc,multiple_returns]()(int,int,string)
            return(0,1,"two");
        """.trimIndent()
}