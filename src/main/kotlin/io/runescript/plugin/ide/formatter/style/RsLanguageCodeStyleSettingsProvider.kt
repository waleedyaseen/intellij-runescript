package io.runescript.plugin.ide.formatter.style

import com.intellij.application.options.IndentOptionsEditor
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider
import io.runescript.plugin.lang.RuneScript


class RsLanguageCodeStyleSettingsProvider : LanguageCodeStyleSettingsProvider() {

    override fun getLanguage() = RuneScript

    override fun customizeSettings(consumer: CodeStyleSettingsCustomizable, settingsType: SettingsType) {
    }

    override fun getIndentOptionsEditor(): IndentOptionsEditor {
        return RsIndentOptionsEditor()
    }

    override fun getCodeSample(settingsType: SettingsType) =
            """
            [proc,countodd](int ${'$'}count, intarray ${'$'}array)(int)
            def_int ${'$'}index = 0;
            while (1 = 1) {
                def_int ${'$'}odd_count = 1;
            }
            def_int ${'$'}odd_count = 0;
            while (${'$'}index < ${'$'}count) {
                if (testbit(${'$'}array(${'$'}index), 0) = 1) {
                    ${'$'}odd_count = add(${'$'}odd_count, 1);
                }
                ${'$'}index = calc(${'$'}index + 1);
            }
        
            /*
            * First line of multi line comment.
            */
            switch_int (${'$'}odd_count) {
                case 0, 1, 2 :
                    ~debug_mes("<col=ff0000>No odd numbers were found.</col>");
                hello();
                case 1 :
                    ~debug_mes("Only one odd number was found.");
                case default :
                    ~debug_mes("Many odd numbers were found (<~tostring(${'$'}odd_count)>).");
            }
            // Single line comment.
            return(${'$'}odd_count);
        
            [proc,tostring](component ${'$'}text)
        """.trimIndent()
}