package com.example.data.solver

/**
 * Utility to format and clean mathematical and scientific text according to
 * the 3rd Secondary Yemeni curriculum guidelines:
 * - Strips all raw Markdown (**, *, ###, backticks)
 * - Converts raw LaTeX codes into visual Unicode math (√, °, ², θ, π, ×, ÷, etc.)
 * - Strips $ and $$ delimiters
 * - Converts superscripts (2^2 -> 2², 10^-3 -> 10⁻³)
 * - Removes chatty / conversational phrases ("هل فهمت؟", "هل وضحت الصورة؟")
 * - Cleans verbose / redundant sub-headings
 */
object MathFormatter {

    private val superscriptMap = mapOf(
        '0' to '⁰', '1' to '¹', '2' to '²', '3' to '³', '4' to '⁴',
        '5' to '⁵', '6' to '⁶', '7' to '⁷', '8' to '⁸', '9' to '⁹',
        '+' to '⁺', '-' to '⁻', '=' to '⁼', '(' to '⁽', ')' to '⁾',
        'n' to 'ⁿ', 'x' to 'ˣ', 'y' to 'ʸ'
    )

    fun toSuperscript(str: String): String {
        val sb = StringBuilder()
        for (c in str) {
            sb.append(superscriptMap[c] ?: c)
        }
        return sb.toString()
    }

    fun cleanMathText(input: String): String {
        if (input.isBlank()) return ""

        var text = input

        // 1. Remove LaTeX display math delimiters $$...$$ and inline $...$
        text = text.replace("$$", "")

        // 2. Remove / replace specific LaTeX commands BEFORE stripping single $
        text = text
            // Angles and degrees
            .replace("\\circ", "°")
            .replace("^{\\circ}", "°")
            .replace("^\\circ", "°")
            .replace("^{°}", "°")
            .replace("^°", "°")

            // Square root and roots: simple term like \sqrt{4} -> √4, complex expression -> √(a+b)
            .replace(Regex("""\\sqrt\{([0-9a-zA-Z\u0600-\u06FF]+)\}""")) { "√${it.groupValues[1]}" }
            .replace(Regex("""\\sqrt\{([^}]+)\}""")) { "√(${it.groupValues[1]})" }
            .replace(Regex("""\\sqrt\s*([0-9a-zA-Z\u0600-\u06FF]+)""")) { "√${it.groupValues[1]}" }
            .replace("\\sqrt", "√")

            // Fractions: \frac{a}{b} -> (a ÷ b) or a ÷ b
            .replace(Regex("""\\frac\{([^}]+)\}\{([^}]+)\}""")) { match ->
                val num = match.groupValues[1].trim()
                val den = match.groupValues[2].trim()
                "$num ÷ $den"
            }

            // Greek symbols common in Yemeni curriculum
            .replace("\\theta", "θ")
            .replace("\\Theta", "Θ")
            .replace("\\pi", "π")
            .replace("\\alpha", "α")
            .replace("\\beta", "β")
            .replace("\\gamma", "γ")
            .replace("\\lambda", "λ")
            .replace("\\Delta", "Δ")
            .replace("\\omega", "ω")
            .replace("\\Omega", "Ω")
            .replace("\\mu", "μ")
            .replace("\\sigma", "σ")
            .replace("\\phi", "φ")
            .replace("\\Phi", "Φ")

            // Operators
            .replace("\\times", "×")
            .replace("\\div", "÷")
            .replace("\\pm", "±")
            .replace("\\mp", "∓")
            .replace("\\approx", "≈")
            .replace("\\neq", "≠")
            .replace("\\leq", "≤")
            .replace("\\le", "≤")
            .replace("\\geq", "≥")
            .replace("\\ge", "≥")
            .replace("\\cdot", "·")
            .replace("\\to", "→")
            .replace("\\rightarrow", "→")
            .replace("\\leftarrow", "←")
            .replace("\\infty", "∞")

            // Formatting wrappers
            .replace(Regex("""\\text\{([^}]+)\}""")) { it.groupValues[1] }
            .replace(Regex("""\\mathbf\{([^}]+)\}""")) { it.groupValues[1] }
            .replace(Regex("""\\mathit\{([^}]+)\}""")) { it.groupValues[1] }
            .replace(Regex("""\\mathrm\{([^}]+)\}""")) { it.groupValues[1] }
            .replace("\\left", "")
            .replace("\\right", "")

        // 3. Strip remaining $ signs
        text = text.replace("$", "")

        // 4. Superscript conversion for curly braces: ^{...}
        text = text.replace(Regex("""\^\{([^}]+)\}""")) { match ->
            toSuperscript(match.groupValues[1])
        }

        // 5. Superscript conversion for single-token powers:
        // Negative powers: e.g. 10^-3 or s^-1 -> 10⁻³, s⁻¹
        text = text.replace(Regex("""\^(-?[0-9]+|[nx])""")) { match ->
            toSuperscript(match.groupValues[1])
        }

        // 6. Clean Markdown formatting:
        // Bold: **text** -> text
        text = text.replace(Regex("""\*\*([^*]+)\*\*""")) { it.groupValues[1] }
        // Italic: *text* -> text (when not used as multiplication)
        text = text.replace(Regex("""(?<!\w)\*([^*]+)\*(?!\w)""")) { it.groupValues[1] }
        // Headers: ### Title -> Title
        text = text.replace(Regex("""(?m)^#{1,6}\s*"""), "")
        // Bold underscore: __text__ -> text
        text = text.replace(Regex("""__([^_]+)__"""), "$1")
        // Backticks: `code` -> code
        text = text.replace("`", "")

        // 7. Strip verbose/unwanted sub-headings and conversational chatter
        val filteredLines = mutableListOf<String>()
        for (rawLine in text.lines()) {
            val line = rawLine.trim()
            if (line.isBlank()) {
                filteredLines.add("")
                continue
            }

            // Remove conversational questions at the end
            if (line.contains("هل وضحت الصورة") ||
                line.contains("هل فهمت") ||
                line.contains("هل الشرح واضح") ||
                line.contains("أتمنى لك التوفيق") ||
                line.contains("هل لديك أي استفسار")
            ) {
                continue
            }

            // Remove long redundant headings like "الجذر للطول" or "القسمة للزاوية"
            var cleanL = line
                .replace("الجذر للطول:", "طول الجذر:")
                .replace("الجذر للطول", "طول الجذر:")
                .replace("القسمة للزاوية:", "الزاوية:")
                .replace("القسمة للزاوية", "الزاوية:")

            // Remove box drawing characters if any leaked
            cleanL = cleanL.replace(Regex("""[┌┐└┘├┤─│┬┴┼]"""), "").trim()

            if (cleanL.isNotBlank()) {
                filteredLines.add(cleanL)
            }
        }

        // Deduplicate excessive empty lines
        val result = filteredLines.joinToString("\n").replace(Regex("""\n{3,}"""), "\n\n")
        return result.trim()
    }

    /**
     * Cleans a single line or phrase (e.g. given, law, answer).
     */
    fun cleanItem(item: String): String {
        return cleanMathText(item).removePrefix("•").removePrefix("-").trim()
    }

    /**
     * Cleans a list of items and removes blanks/redundancies.
     */
    fun cleanItemList(items: List<String>): List<String> {
        val seen = mutableSetOf<String>()
        val result = mutableListOf<String>()
        for (it in items) {
            val cleaned = cleanItem(it)
            if (cleaned.isNotBlank() && !seen.contains(cleaned)) {
                seen.add(cleaned)
                result.add(cleaned)
            }
        }
        return result
    }

    fun toCircledNumber(num: Int): String {
        return when (num) {
            1 -> "①"
            2 -> "②"
            3 -> "③"
            4 -> "④"
            5 -> "⑤"
            6 -> "⑥"
            7 -> "⑦"
            8 -> "⑧"
            9 -> "⑨"
            10 -> "⑩"
            else -> num.toString()
        }
    }

    /**
     * Formats option labels into standard Yemeni exam form: "الاختيار ③"
     * Handles inputs like: "3", "③", "الاختيار 3", "ج", "خيار 3", "(3)", etc.
     */
    fun formatOptionLabel(input: String?): String {
        if (input.isNullOrBlank()) return ""
        val trimmed = cleanItem(input).trim()

        // Check for circled digits first
        if (trimmed.contains("①")) return "الاختيار ①"
        if (trimmed.contains("②")) return "الاختيار ②"
        if (trimmed.contains("③")) return "الاختيار ③"
        if (trimmed.contains("④")) return "الاختيار ④"
        if (trimmed.contains("⑤")) return "الاختيار ⑤"

        // Check for standalone numbers or digits
        val digitMatch = Regex("""\b([1-5]|[١-٥])\b""").find(trimmed)
            ?: Regex("""([1-5]|[١-٥])""").find(trimmed)
        if (digitMatch != null) {
            val d = digitMatch.value
            return when (d) {
                "1", "١" -> "الاختيار ①"
                "2", "٢" -> "الاختيار ②"
                "3", "٣" -> "الاختيار ③"
                "4", "٤" -> "الاختيار ④"
                "5", "٥" -> "الاختيار ⑤"
                else -> "الاختيار $d"
            }
        }

        // Arabic letter options: أ, ب, ج, د
        if (trimmed == "أ" || trimmed == "(أ)" || trimmed.endsWith(" أ")) return "الاختيار ①"
        if (trimmed == "ب" || trimmed == "(ب)" || trimmed.endsWith(" ب")) return "الاختيار ②"
        if (trimmed == "ج" || trimmed == "(ج)" || trimmed.endsWith(" ج")) return "الاختيار ③"
        if (trimmed == "د" || trimmed == "(د)" || trimmed.endsWith(" د")) return "الاختيار ④"
        if (trimmed == "هـ" || trimmed == "(هـ)" || trimmed.endsWith(" هـ")) return "الاختيار ⑤"

        return if (!trimmed.startsWith("الاختيار")) "الاختيار $trimmed" else trimmed
    }
}
