package com.example.data.calculator

import kotlin.math.abs
import kotlin.math.roundToLong
import kotlin.math.sqrt

/**
 * Result of solving an equation or system of equations
 */
data class EquationResult(
    val title: String,
    val finalAnswer: String,
    val givens: List<String>,
    val law: String,
    val substitution: List<String>,
    val calculation: List<String>,
    val isValid: Boolean = true,
    val errorMessage: String? = null
)

object EquationSolver {

    /**
     * Solves linear equation: a·x + b = c  -->  x = (c - b) / a
     */
    fun solveLinear(a: Double, b: Double, c: Double): EquationResult {
        if (abs(a) < 1e-9) {
            return EquationResult(
                title = "معادلة خطية",
                finalAnswer = if (abs(b - c) < 1e-9) "عدد لا نهائي من الحلول (متطابقة)" else "لا يوجد حل (مستحيلة الحل)",
                givens = listOf("المعادلة: ${fmt(a)} س + ${fmt(b)} = ${fmt(c)}"),
                law = "س = (جـ - ب) ÷ أ",
                substitution = emptyList(),
                calculation = listOf("معامل س = 0، لذا لا يمكن القسمة على صفر."),
                isValid = false,
                errorMessage = "معامل س يساوي صفراً"
            )
        }

        val numerator = c - b
        val x = numerator / a
        val xFmt = fmt(x)

        return EquationResult(
            title = "معادلة خطية: ${fmt(a)}س + ${fmt(b)} = ${fmt(c)}",
            finalAnswer = "س = $xFmt",
            givens = listOf(
                "أ (معامل س) = ${fmt(a)}",
                "ب (الحد الثابت) = ${fmt(b)}",
                "جـ (الطرف الأيسر) = ${fmt(c)}"
            ),
            law = "س = (جـ - ب) ÷ أ",
            substitution = listOf("س = (${fmt(c)} - ${fmt(b)}) ÷ ${fmt(a)}"),
            calculation = listOf(
                "${fmt(c)} - ${fmt(b)} = ${fmt(numerator)}",
                "س = ${fmt(numerator)} ÷ ${fmt(a)} = $xFmt"
            )
        )
    }

    /**
     * Solves quadratic equation: a·x² + b·x + c = 0
     * using discriminant Δ = b² - 4ac
     */
    fun solveQuadratic(a: Double, b: Double, c: Double): EquationResult {
        if (abs(a) < 1e-9) {
            return solveLinear(b, c, 0.0)
        }

        val delta = b * b - 4 * a * c
        val givens = listOf(
            "أ (معامل س²) = ${fmt(a)}",
            "ب (معامل س) = ${fmt(b)}",
            "جـ (الحد المطلق) = ${fmt(c)}"
        )
        val law = "المميز: Δ = ب² - 4 أ جـ\nالقانون العام: س = (-ب ± √Δ) ÷ (2 أ)"

        val subst = listOf(
            "Δ = (${fmt(b)})² - 4 × (${fmt(a)}) × (${fmt(c)})",
            "س = (-(${fmt(b)}) ± √Δ) ÷ (2 × ${fmt(a)})"
        )

        return when {
            delta > 1e-9 -> {
                val sqrtDelta = sqrt(delta)
                val x1 = (-b + sqrtDelta) / (2 * a)
                val x2 = (-b - sqrtDelta) / (2 * a)
                EquationResult(
                    title = "معادلة تربيعية: ${fmt(a)}س² + ${fmt(b)}س + ${fmt(c)} = 0",
                    finalAnswer = "س₁ = ${fmt(x1)}  ،  س₂ = ${fmt(x2)}",
                    givens = givens,
                    law = law,
                    substitution = subst,
                    calculation = listOf(
                        "حساب المميز: Δ = ${fmt(b * b)} - ${fmt(4 * a * c)} = ${fmt(delta)} (موجب > 0، للمسألة جذران حقيقيان مختلفان)",
                        "√Δ = √${fmt(delta)} = ${fmt(sqrtDelta)}",
                        "س₁ = (-${fmt(b)} + ${fmt(sqrtDelta)}) ÷ ${fmt(2 * a)} = ${fmt(x1)}",
                        "س₂ = (-${fmt(b)} - ${fmt(sqrtDelta)}) ÷ ${fmt(2 * a)} = ${fmt(x2)}"
                    )
                )
            }
            abs(delta) <= 1e-9 -> {
                val x = -b / (2 * a)
                EquationResult(
                    title = "معادلة تربيعية: ${fmt(a)}س² + ${fmt(b)}س + ${fmt(c)} = 0",
                    finalAnswer = "س = ${fmt(x)} (جذر حقيقي مضاعف)",
                    givens = givens,
                    law = law,
                    substitution = subst,
                    calculation = listOf(
                        "حساب المميز: Δ = 0 (للمعادلة جذر حقيقي واحد مكرر)",
                        "س = -ب ÷ (2 أ) = -${fmt(b)} ÷ ${fmt(2 * a)} = ${fmt(x)}"
                    )
                )
            }
            else -> {
                // Complex roots: x = -b/(2a) ± i(sqrt(-delta)/(2a))
                val realPart = -b / (2 * a)
                val imagPart = sqrt(-delta) / (2 * a)
                val ansStr = "س₁ = ${fmt(realPart)} + ${fmt(abs(imagPart))}ت  ،  س₂ = ${fmt(realPart)} - ${fmt(abs(imagPart))}ت"
                EquationResult(
                    title = "معادلة تربيعية: ${fmt(a)}س² + ${fmt(b)}س + ${fmt(c)} = 0",
                    finalAnswer = ansStr,
                    givens = givens,
                    law = law,
                    substitution = subst,
                    calculation = listOf(
                        "حساب المميز: Δ = ${fmt(delta)} (سالب < 0، لا توجد جذور حقيقية، الجذور في حقل الأعداد المركبة ℂ)",
                        "√Δ = √(${fmt(delta)}) = √(${fmt(-delta)}) ت = ${fmt(sqrt(-delta))} ت",
                        "س = (${fmt(-b)} ± ${fmt(sqrt(-delta))}ت) ÷ ${fmt(2 * a)}",
                        "س₁ = ${fmt(realPart)} + ${fmt(abs(imagPart))}ت",
                        "س₂ = ${fmt(realPart)} - ${fmt(abs(imagPart))}ت"
                    )
                )
            }
        }
    }

    /**
     * Solves system of 2 linear equations via Cramer's Rule:
     * a1·x + b1·y = c1
     * a2·x + b2·y = c2
     */
    fun solveSystem2x2(
        a1: Double, b1: Double, c1: Double,
        a2: Double, b2: Double, c2: Double
    ): EquationResult {
        val givens = listOf(
            "المعادلة 1: ${fmt(a1)}س + ${fmt(b1)}ص = ${fmt(c1)}",
            "المعادلة 2: ${fmt(a2)}س + ${fmt(b2)}ص = ${fmt(c2)}"
        )
        val law = "طريقة كرامر (المحددات):\nΔ = (أ₁ ب₂ - أ₂ ب₁)\nΔس = (جـ₁ ب₂ - جـ₂ ب₁)\nΔص = (أ₁ جـ₂ - أ₂ جـ₁)\nس = Δس ÷ Δ  ،  ص = Δص ÷ Δ"

        val delta = a1 * b2 - a2 * b1
        val deltaX = c1 * b2 - c2 * b1
        val deltaY = a1 * c2 - a2 * c1

        if (abs(delta) < 1e-9) {
            val hasInf = abs(deltaX) < 1e-9 && abs(deltaY) < 1e-9
            return EquationResult(
                title = "نظام معادلتين خطيتين",
                finalAnswer = if (hasInf) "عدد لا نهائي من الحلول (المستقيمان متطابقان)" else "لا يوجد حل (المستقيمان متوازيان)",
                givens = givens,
                law = law,
                substitution = listOf("Δ = (${fmt(a1)} × ${fmt(b2)}) - (${fmt(a2)} × ${fmt(b1)}) = 0"),
                calculation = listOf("محدد النظام Δ = 0، لذا لا يمكن تطبيق القسمة."),
                isValid = false,
                errorMessage = "محدد النظام يساوي صفر"
            )
        }

        val x = deltaX / delta
        val y = deltaY / delta

        return EquationResult(
            title = "نظام معادلتين خطيتين بمتغيرين (س ، ص)",
            finalAnswer = "س = ${fmt(x)}  ،  ص = ${fmt(y)}",
            givens = givens,
            law = law,
            substitution = listOf(
                "Δ = (${fmt(a1)} × ${fmt(b2)}) - (${fmt(a2)} × ${fmt(b1)})",
                "Δس = (${fmt(c1)} × ${fmt(b2)}) - (${fmt(c2)} × ${fmt(b1)})",
                "Δص = (${fmt(a1)} × ${fmt(c2)}) - (${fmt(a2)} × ${fmt(c1)})"
            ),
            calculation = listOf(
                "حساب محدد المعاملات: Δ = ${fmt(delta)}",
                "حساب محدد س: Δس = ${fmt(deltaX)}",
                "حساب محدد ص: Δص = ${fmt(deltaY)}",
                "س = Δس ÷ Δ = ${fmt(deltaX)} ÷ ${fmt(delta)} = ${fmt(x)}",
                "ص = Δص ÷ Δ = ${fmt(deltaY)} ÷ ${fmt(delta)} = ${fmt(y)}"
            )
        )
    }

    /**
     * Solves system of 3 linear equations via Cramer's Rule:
     * a1·x + b1·y + c1·z = d1
     * a2·x + b2·y + c2·z = d2
     * a3·x + b3·y + c3·z = d3
     */
    fun solveSystem3x3(
        a1: Double, b1: Double, c1: Double, d1: Double,
        a2: Double, b2: Double, c2: Double, d2: Double,
        a3: Double, b3: Double, c3: Double, d3: Double
    ): EquationResult {
        val givens = listOf(
            "المعادلة 1: ${fmt(a1)}س + ${fmt(b1)}ص + ${fmt(c1)}ع = ${fmt(d1)}",
            "المعادلة 2: ${fmt(a2)}س + ${fmt(b2)}ص + ${fmt(c2)}ع = ${fmt(d2)}",
            "المعادلة 3: ${fmt(a3)}س + ${fmt(b3)}ص + ${fmt(c3)}ع = ${fmt(d3)}"
        )
        val law = "طريقة كرامر للمحددات 3×3:\nس = Δس ÷ Δ  ،  ص = Δص ÷ Δ  ،  ع = Δع ÷ Δ"

        fun det3(
            x1: Double, y1: Double, z1: Double,
            x2: Double, y2: Double, z2: Double,
            x3: Double, y3: Double, z3: Double
        ): Double {
            return x1 * (y2 * z3 - y3 * z2) - y1 * (x2 * z3 - x3 * z2) + z1 * (x2 * y3 - x3 * y2)
        }

        val delta = det3(a1, b1, c1, a2, b2, c2, a3, b3, c3)
        val deltaX = det3(d1, b1, c1, d2, b2, c2, d3, b3, c3)
        val deltaY = det3(a1, d1, c1, a2, d2, c2, a3, d3, c3)
        val deltaZ = det3(a1, b1, d1, a2, b2, d2, a3, b3, d3)

        if (abs(delta) < 1e-9) {
            return EquationResult(
                title = "نظام 3 معادلات خطية",
                finalAnswer = "النظام ليس له حل وحيد (محدد النظام = 0)",
                givens = givens,
                law = law,
                substitution = listOf("Δ = 0"),
                calculation = listOf("محدد مصفوفة المعاملات يساوي صفر، لذا لا يوجد حل وحيد."),
                isValid = false,
                errorMessage = "المحدد يساوي صفراً"
            )
        }

        val x = deltaX / delta
        val y = deltaY / delta
        val z = deltaZ / delta

        return EquationResult(
            title = "نظام ثلاث معادلات خطية (س ، ص ، ع)",
            finalAnswer = "س = ${fmt(x)}  ،  ص = ${fmt(y)}  ،  ع = ${fmt(z)}",
            givens = givens,
            law = law,
            substitution = listOf(
                "Δ = ${fmt(delta)}",
                "Δس = ${fmt(deltaX)}  ،  Δص = ${fmt(deltaY)}  ،  Δع = ${fmt(deltaZ)}"
            ),
            calculation = listOf(
                "محدد النظام: Δ = ${fmt(delta)}",
                "س = Δس ÷ Δ = ${fmt(deltaX)} ÷ ${fmt(delta)} = ${fmt(x)}",
                "ص = Δص ÷ Δ = ${fmt(deltaY)} ÷ ${fmt(delta)} = ${fmt(y)}",
                "ع = Δع ÷ Δ = ${fmt(deltaZ)} ÷ ${fmt(delta)} = ${fmt(z)}"
            )
        )
    }

    private fun fmt(d: Double): String {
        if (abs(d - d.roundToLong()) < 1e-6) {
            return d.roundToLong().toString()
        }
        return String.format(java.util.Locale.US, "%.3f", d).trimEnd('0').trimEnd('.')
    }
}
