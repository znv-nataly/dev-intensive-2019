package ru.skillbranch.devintensive.utils

import java.util.*

object Utils {

    fun pastFullName(fullName: String?): Pair<String?, String?> {
        val fullNameTrim = fullName?.replace("  ", " ")?.trim()
        if (fullNameTrim.isNullOrBlank())
            return null to null
        val parts: List<String>? = fullNameTrim.trim().split(" ")
        val firstName = parts?.getOrNull(0)
        val lastName = parts?.getOrNull(1)

        return firstName to lastName // Pair(firstName, lastName)
    }

    @ExperimentalStdlibApi
    fun transliteration(payload: String, divider: String = " "): String {
        val rules: Map<String, String> = mapOf(
            "а" to "a", "б" to "b", "в" to "v", "г" to "g", "д" to "d", "е" to "e", "ё" to "e",
            "ж" to "zh", "з" to "z", "и" to "i", "й" to "i", "к" to "k", "л" to "l", "м" to "m",
            "н" to "n", "о" to "o", "п" to "p", "р" to "r", "с" to "s", "т" to "t", "у" to "u",
            "ф" to "f", "х" to "h", "ц" to "c", "ч" to "ch", "ш" to "sh", "щ" to "sh'", "ъ" to "",
            "ы" to "i", "ь" to "", "э" to "e", "ю" to "yu", "я" to "ya"
        )

        var result = payload.trim()
        for (letter in rules) {
            result = letter.key.toRegex().replace(result, letter.value)
            result = letter.key.toUpperCase(Locale("ru")).toRegex()
                .replace(result, letter.value.capitalize(Locale("ru")))
        }

        if (divider != " ") {
            result = result.replace(" ", divider)
        }
        return result
    }

    fun toInitials(firstName: String?, lastName: String?): String? {
        if (firstName.isNullOrBlank() && lastName.isNullOrBlank())
            return null

        val firstNameTrim = firstName?.trim()
        val lastNameTrim = lastName?.trim()
        var initials = ""
        if (!firstNameTrim.isNullOrBlank())
            initials = firstNameTrim.getOrNull(0)?.toUpperCase().toString()
        if (!lastNameTrim.isNullOrBlank())
            initials += lastNameTrim.getOrNull(0)?.toUpperCase().toString()

        return initials
    }
}