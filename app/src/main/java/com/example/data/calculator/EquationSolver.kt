package com.example.data.calculator

import kotlin.math.*

data class EquationResult(
    val title: String = "",
    val finalAnswer: String = "",
    val givens: List<String> = emptyList(),
    val law: String = "",
    val substitution: List<String> = emptyList(),
    val calculation: List<String> = emptyList(),
    val roots: List<String> = emptyList(),
    val explanation: String = ""
)

object EquationSolver {

    fun solveLinear(a: Double, b: Double, c: Double): EquationResult {
        if (abs(a) < 1e-9) {
            val ans = if (abs(b - c) < 1e-9) "متطابقة صحيحة لجميع الأعداد الحقيقية (عدد لا نهائي من الحلول)" else "مستحيلة الحل (ليس لها حل في ح)"
            return EquationResult(
                title = "معادلة خطية: ${a}س + $b = $c",
                finalAnswer = ans,
                givens = listOf("معامل س: أ = $a", "الحد الثابت: ب = $b", "الطرف الأيسر: جـ = $c"),
                law = "أ س + ب = جـ  ==>  س = (جـ - ب) / أ",
                roots = emptyList()
            )
        }
        val x = (c - b) / a
        val xStr = String.format(java.util.Locale.US, "%.4f", x).trimEnd('0').trimEnd('.')
        return EquationResult(
            title = "معادلة خطية من الدرجة الأولى",
            finalAnswer = "س = $xStr",
            givens = listOf("أ س + ب = جـ", "أ = $a ، ب = $b ، جـ = $c"),
            law = "س = (جـ - ب) / أ",
            substitution = listOf("س = ($c - ($b)) / $a"),
            calculation = listOf("س = ${c - b} / $a = $xStr"),
            roots = listOf("س = $xStr"),
            explanation = "حل المعادلة الخطية بنقل الحد الثابت والقسمة على المعامل."
        )
    }

    fun solveQuadratic(a: Double, b: Double, c: Double): EquationResult {
        if (abs(a) < 1e-9) {
            return solveLinear(b, c, 0.0)
        }

        val delta = b * b - 4 * a * c
        val deltaStr = String.format(java.util.Locale.US, "%.2f", delta)

        val givensList = listOf("أ = $a", "ب = $b", "جـ = $c")
        val lawText = "القانون العام: س = (-ب ± √Δ) / 2أ ، حيث Δ = ب² - 4أ جـ"
        val subList = listOf("Δ = ($b)² - 4($a)($c) = $deltaStr")

        if (delta > 0) {
            val r1 = (-b + sqrt(delta)) / (2 * a)
            val r2 = (-b - sqrt(delta)) / (2 * a)
            val r1Str = String.format(java.util.Locale.US, "%.4f", r1).trimEnd('0').trimEnd('.')
            val r2Str = String.format(java.util.Locale.US, "%.4f", r2).trimEnd('0').trimEnd('.')
            return EquationResult(
                title = "معادلة من الدرجة الثانية (جذران حقيقيان)",
                finalAnswer = "س₁ = $r1Str ، س₂ = $r2Str",
                givens = givensList,
                law = lawText,
                substitution = subList,
                calculation = listOf(
                    "س₁ = (-($b) + √$deltaStr) / (2 × $a) = $r1Str",
                    "س₂ = (-($b) - √$deltaStr) / (2 × $a) = $r2Str"
                ),
                roots = listOf("س₁ = $r1Str", "س₂ = $r2Str"),
                explanation = "المميز Δ > 0 ، يوجد جذران حقيقيان مختلفان."
            )
        } else if (abs(delta) < 1e-9) {
            val r = -b / (2 * a)
            val rStr = String.format(java.util.Locale.US, "%.4f", r).trimEnd('0').trimEnd('.')
            return EquationResult(
                title = "معادلة من الدرجة الثانية (جذر مضاعف)",
                finalAnswer = "س = $rStr",
                givens = givensList,
                law = lawText,
                substitution = subList,
                calculation = listOf("س = -($b) / (2 × $a) = $rStr"),
                roots = listOf("س = $rStr"),
                explanation = "المميز Δ = 0 ، يوجد جذر حقيقي مكرر."
            )
        } else {
            val real = -b / (2 * a)
            val imag = sqrt(-delta) / (2 * a)
            val realStr = String.format(java.util.Locale.US, "%.4f", real).trimEnd('0').trimEnd('.')
            val imagStr = String.format(java.util.Locale.US, "%.4f", abs(imag)).trimEnd('0').trimEnd('.')
            val r1 = "$realStr + ${imagStr}ت"
            val r2 = "$realStr - ${imagStr}ت"
            return EquationResult(
                title = "معادلة من الدرجة الثانية (جذران مركبان مترافقان)",
                finalAnswer = "س₁ = $r1 ، س₂ = $r2",
                givens = givensList,
                law = lawText,
                substitution = subList,
                calculation = listOf(
                    "س₁ = $realStr + √(${deltaStr}) / 2أ = $r1",
                    "س₂ = $realStr - √(${deltaStr}) / 2أ = $r2"
                ),
                roots = listOf("س₁ = $r1", "س₂ = $r2"),
                explanation = "المميز Δ < 0 ، يوجد جذران تخيليان مترافقان في مجموعة الأعداد المركبة (ك)."
            )
        }
    }

    fun solveSystem2x2(a1: Double, b1: Double, c1: Double, a2: Double, b2: Double, c2: Double): EquationResult {
        val det = a1 * b2 - a2 * b1
        val detX = c1 * b2 - c2 * b1
        val detY = a1 * c2 - a2 * c1

        if (abs(det) < 1e-9) {
            return EquationResult(
                title = "نظام معادلتين خطيتين",
                finalAnswer = if (abs(detX) < 1e-9 && abs(detY) < 1e-9) "المعادلتان منطبقتان (عدد لا نهائي من الحلول)" else "المعادلتان متوازيتان (لا يوجد حل)",
                givens = listOf("المعادلة الأولى: ${a1}س + ${b1}ص = $c1", "المعادلة الثانية: ${a2}س + ${b2}ص = $c2"),
                law = "طريقة كرامر: المحدد العام Δ = أ₁ ب₂ - أ₂ ب₁"
            )
        }

        val x = detX / det
        val y = detY / det
        val xStr = String.format(java.util.Locale.US, "%.4f", x).trimEnd('0').trimEnd('.')
        val yStr = String.format(java.util.Locale.US, "%.4f", y).trimEnd('0').trimEnd('.')

        return EquationResult(
            title = "حل نظام معادلتين بمتغيرين (طريقة كرامر)",
            finalAnswer = "س = $xStr ، ص = $yStr",
            givens = listOf("${a1}س + ${b1}ص = $c1", "${a2}س + ${b2}ص = $c2"),
            law = "س = Δس / Δ  ،  ص = Δص / Δ",
            substitution = listOf("Δ = ($a1)($b2) - ($a2)($b1) = $det", "Δس = ($c1)($b2) - ($c2)($b1) = $detX", "Δص = ($a1)($c2) - ($a2)($c1) = $detY"),
            calculation = listOf("س = $detX / $det = $xStr", "ص = $detY / $det = $yStr"),
            roots = listOf("س = $xStr", "ص = $yStr")
        )
    }

    fun solveSystem3x3(
        a1: Double, b1: Double, c1: Double, d1: Double,
        a2: Double, b2: Double, c2: Double, d2: Double,
        a3: Double, b3: Double, c3: Double, d3: Double
    ): EquationResult {
        fun det3(m11: Double, m12: Double, m13: Double, m21: Double, m22: Double, m23: Double, m31: Double, m32: Double, m33: Double): Double {
            return m11 * (m22 * m33 - m23 * m32) - m12 * (m21 * m33 - m23 * m31) + m13 * (m21 * m32 - m22 * m31)
        }

        val det = det3(a1, b1, c1, a2, b2, c2, a3, b3, c3)
        val detX = det3(d1, b1, c1, d2, b2, c2, d3, b3, c3)
        val detY = det3(a1, d1, c1, a2, d2, c2, a3, d3, c3)
        val detZ = det3(a1, b1, d1, a2, b2, d2, a3, b3, d3)

        if (abs(det) < 1e-9) {
            return EquationResult(
                title = "نظام 3 معادلات خطية",
                finalAnswer = "محدد المعاملات Δ = 0 (النظام ليس له حل وحيد)",
                givens = listOf("نظام من ثلاث معادلات بثلاثة مجاهيل (س، ص، ع)"),
                law = "طريقة كرامر بالمحددات من الرتبة الثالثة"
            )
        }

        val x = detX / det
        val y = detY / det
        val z = detZ / det
        val xStr = String.format(java.util.Locale.US, "%.4f", x).trimEnd('0').trimEnd('.')
        val yStr = String.format(java.util.Locale.US, "%.4f", y).trimEnd('0').trimEnd('.')
        val zStr = String.format(java.util.Locale.US, "%.4f", z).trimEnd('0').trimEnd('.')

        return EquationResult(
            title = "حل نظام 3 معادلات بـ 3 مجاهيل (طريقة كرامر)",
            finalAnswer = "س = $xStr ، ص = $yStr ، ع = $zStr",
            givens = listOf(
                "${a1}س + ${b1}ص + ${c1}ع = $d1",
                "${a2}س + ${b2}ص + ${c2}ع = $d2",
                "${a3}س + ${b3}ص + ${c3}ع = $d3"
            ),
            law = "س = Δس / Δ ، ص = Δص / Δ ، ع = Δع / Δ",
            substitution = listOf("Δ = $det", "Δس = $detX", "Δص = $detY", "Δع = $detZ"),
            calculation = listOf("س = $detX / $det = $xStr", "ص = $detY / $det = $yStr", "ع = $detZ / $det = $zStr"),
            roots = listOf("س = $xStr", "ص = $yStr", "ع = $zStr")
        )
    }
}
