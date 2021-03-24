package me.aiglez.gangs.utils

import org.apache.commons.lang.StringUtils

object Placeholders {

    @JvmStatic
    fun replaceIn(string: String, vararg replacements: Any): String {
        val placeholders = StringUtils.substringsBetween(string, "{", "}")
        if (replacements.isEmpty() || placeholders.isNullOrEmpty()) return string

        var handled = string
        for (placeholder in placeholders) {
            val index = placeholder.toIntOrNull() ?: continue
            try {
                val replacement = replacements[index]
                handled = handled.replace("{$placeholder}", replacement.toString(), true)
            } catch (ignored: Throwable) {
            }
        }

        return handled
    }
}