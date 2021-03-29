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

    @JvmStatic
    fun replaceIn(list: List<String>, vararg replacements: Any): List<String> {
        if (replacements.isEmpty()) {
            return list
        }

        val final = mutableListOf<String>()

        for (line in list) {
            val placeholders = StringUtils.substringsBetween(line, "{", "}")
            if (placeholders.isNullOrEmpty()) {
                final.add(line)
                continue
            }

            var handled = line
            for (placeholder in placeholders) {
                val index = placeholder.toIntOrNull() ?: continue
                try {
                    val replacement = replacements[index]
                    handled = handled.replace("{$placeholder}", replacement.toString(), true)
                } catch (ignored: Throwable) {
                }
            }

            final.add(handled)
        }

        return final
    }

    @JvmStatic
    fun insertCollection(into: List<String>, at: String, insert: Collection<String>) : List<String> {
        return into.flatMap { elem -> if (elem == at) insert else listOf(elem) }.toMutableList()
    }
}