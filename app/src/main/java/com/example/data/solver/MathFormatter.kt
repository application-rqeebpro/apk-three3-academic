package com.example.data.solver

object MathFormatter {

    fun cleanMathText(raw: String): String {
        var text = raw.trim()
        text = text.replace("**", "").replace("*", "")
        text = text.replace("`", "")
        text = text.replace("$$", "").replace("$", "")
        text = text.replace("\\circ", "°")
        text = text.replace("\\times", "×")
        text = text.replace("\\div", "÷")
        text = text.replace("\\pm", "±")
        text = text.replace("\\sqrt", "√")
        text = text.replace("\\pi", "π")
        text = text.replace("\\theta", "هـ")
        text = text.replace("\\alpha", "ألفا")
        text = text.replace("\\beta", "بيتا")
        text = text.replace("\\infty", "∞")
        text = text.replace("\\leq", "≤")
        text = text.replace("\\geq", "≥")
        text = text.replace("\\neq", "≠")
        return text
    }

    fun cleanItem(item: String): String {
        return cleanMathText(item)
    }

    fun cleanItemList(list: List<String>): List<String> {
        return list.map { cleanMathText(it) }.filter { it.isNotBlank() }
    }

    fun formatOptionLabel(label: String?): String {
        if (label.isNullOrBlank()) return ""
        var clean = cleanMathText(label).trim()
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
