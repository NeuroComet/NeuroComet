package com.kyilmaz.neuronetworkingtitle

/**
 * Very small, deterministic text filter intended for Kids mode to reduce exposure to vulgar content.
 *
 * Note: This is not a full profanity filter (language/locale aware). It's a simple heuristic layer,
 * consistent with the project's existing keyword-based moderation.
 */
object ContentFiltering {

    // Keep this intentionally small and aligned with existing moderation keywords.
    private val profaneWords = setOf(
        "shit",
        "damn"
    )

    private val blockedUnder13 = setOf(
        // existing "blockedKeywords" in FeedViewModel
        "kill",
        "harm",
        "abuse",
        "underage",
        "threat",
        "illegal",
        "criminal"
    )

    fun shouldHideTextForKids(text: String, level: KidsFilterLevel): Boolean {
        val lower = text.lowercase()
        return when (level) {
            KidsFilterLevel.STRICT -> {
                blockedUnder13.any { lower.contains(it) } || profaneWords.any { lower.contains(it) }
            }
            KidsFilterLevel.MODERATE -> {
                blockedUnder13.any { lower.contains(it) }
            }
        }
    }

    fun sanitizeForKids(text: String, level: KidsFilterLevel): String {
        if (level == KidsFilterLevel.MODERATE) return text

        var result = text
        profaneWords.forEach { w ->
            val regex = Regex("\\b" + Regex.escape(w) + "\\b", RegexOption.IGNORE_CASE)
            result = result.replace(regex) { match -> mask(match.value) }
        }
        return result
    }

    private fun mask(word: String): String {
        if (word.length <= 2) return "*".repeat(word.length)
        return word.first() + "*".repeat(word.length - 2) + word.last()
    }
}

