package com.example.data.solver

/**
 * منسق النصوص والمعادلات الرياضية والعلمية
 * يقوم بإزالة الرموز المشوشة مثل *$&# والرموز غير المرتبطة بالمسائل،
 * ويستبدلها بتنسيق عربي وأكاديمي واضح ومريح لطلاب الثانوية العامة.
 */
object MathFormatter {

    fun cleanMathText(raw: String): String {
        if (raw.isBlank()) return ""
        var text = raw.trim()

        // 1. إزالة رموز الماركداون والرموز المشوشة المطلوبة في التوجيه: *$&#
        text = text.replace("***", "")
        text = text.replace("**", "")
        text = text.replace("*", "")
        text = text.replace("$$", "")
        text = text.replace("$", "")
        text = text.replace("&amp;", " و ")
        text = text.replace("&nbsp;", " ")
        text = text.replace("&", " و ")
        text = text.replace("####", "")
        text = text.replace("###", "")
        text = text.replace("##", "")
        text = text.replace("#", "")
        text = text.replace("`", "")
        text = text.replace("~", "")

        // 2. إزالة أقواس وتنسيقات LaTeX المسربة
        text = text.replace("\\[", "")
        text = text.replace("\\]", "")
        text = text.replace("\\(", "")
        text = text.replace("\\)", "")
        text = text.replace("\\text{", "")
        text = text.replace("\\mathbf{", "")
        text = text.replace("\\mathrm{", "")
        text = text.replace("\\left", "")
        text = text.replace("\\right", "")

        // 3. تحويل الرموز اللاتينية واليونانية إلى رموز رياضية عربية مفهومة
        text = text.replace("\\circ", "°")
        text = text.replace("\\times", " × ")
        text = text.replace("\\div", " ÷ ")
        text = text.replace("\\pm", " ± ")
        text = text.replace("\\mp", " ∓ ")
        text = text.replace("\\sqrt", "√")
        text = text.replace("\\pi", "ط (π)")
        text = text.replace("\\theta", "هـ (θ)")
        text = text.replace("\\alpha", "ألفا (α)")
        text = text.replace("\\beta", "بيتا (β)")
        text = text.replace("\\gamma", "جاما (γ)")
        text = text.replace("\\lambda", "لمدا (λ)")
        text = text.replace("\\omega", "أوميجا (ω)")
        text = text.replace("\\mu", "ميكرو (μ)")
        text = text.replace("\\Delta", "Δ")
        text = text.replace("\\infty", "∞")
        text = text.replace("\\leq", " ≤ ")
        text = text.replace("\\geq", " ≥ ")
        text = text.replace("\\neq", " ≠ ")
        text = text.replace("\\approx", " ≈ ")
        text = text.replace("\\rightarrow", " ➔ ")
        text = text.replace("\\to", " ➔ ")
        text = text.replace("\\in", " ∈ ")
        text = text.replace("\\subset", " ⊂ ")

        // 4. استبدال الرموز المشوشة الشائعة في البرمجة بالرموز المنهجية
        text = text.replace(" / ", " ÷ ")
        // معالجة علامة الجمع والطرح الزائدة والمسافات
        text = text.replace("  ", " ")

        return text.trim()
    }

    fun cleanItem(item: String): String {
        return cleanMathText(item)
    }

    fun cleanItemList(list: List<String>): List<String> {
        return list.map { cleanMathText(it) }.filter { it.isNotBlank() }
    }

    fun formatOptionLabel(label: String?): String {
        if (label.isNullOrBlank()) return ""
        val clean = cleanMathText(label).trim()
        if (clean.contains("1") || clean.contains("①") || clean.contains("أ") || clean.contains("A")) {
            return "الاختيار ①"
        }
        if (clean.contains("2") || clean.contains("②") || clean.contains("ب") || clean.contains("B")) {
            return "الاختيار ②"
        }
        if (clean.contains("3") || clean.contains("③") || clean.contains("ج") || clean.contains("C")) {
            return "الاختيار ③"
        }
        if (clean.contains("4") || clean.contains("④") || clean.contains("د") || clean.contains("D")) {
            return "الاختيار ④"
        }
        return clean
    }
}
