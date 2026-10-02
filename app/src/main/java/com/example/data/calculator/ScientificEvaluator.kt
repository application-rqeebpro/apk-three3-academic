package com.example.data.calculator

import kotlin.math.*

enum class AngleMode(val label: String) {
    DEG("DEG"),
    RAD("RAD"),
    GRAD("GRAD")
}

data class CalculationResult(
    val decimalValue: Double,
    val decimalString: String,
    val fractionValue: Fraction? = null,
    val fractionString: String? = null,
    val expressionFormatted: String,
    val explanation: CalculationExplanation? = null,
    val isError: Boolean = false,
    val errorMessage: String? = null
)

data class CalculationExplanation(
    val title: String,
    val givens: List<String>,
    val law: String,
    val substitution: List<String>,
    val calculation: List<String>,
    val finalAnswer: String
)

object ScientificEvaluator {

    val PI = Math.PI
    val E = Math.E
    const val LIGHT_SPEED = 299792458.0 // c
    const val GRAVITY = 9.8 // g
    const val PLANCK = 6.62607015e-34 // h

    /**
     * Safely evaluates an arithmetic / scientific expression string.
     */
    fun evaluate(rawExpr: String, angleMode: AngleMode): CalculationResult {
        val clean = sanitizeExpression(rawExpr)
        if (clean.isBlank()) {
            return CalculationResult(
                decimalValue = 0.0,
                decimalString = "0",
                expressionFormatted = "",
                isError = false
            )
        }

        return try {
            val parser = ExpressionParser(clean, angleMode)
            val value = parser.parseExpression()

            if (value.isNaN()) {
                return CalculationResult(
                    decimalValue = 0.0,
                    decimalString = "",
                    expressionFormatted = rawExpr,
                    isError = true,
                    errorMessage = "قيمة غير معرفة رياضياً"
                )
            }

            if (value.isInfinite()) {
                return CalculationResult(
                    decimalValue = 0.0,
                    decimalString = "",
                    expressionFormatted = rawExpr,
                    isError = true,
                    errorMessage = "لا يمكن القسمة على صفر."
                )
            }

            val decStr = formatDecimal(value)
            val frac = Fraction.fromDouble(value, maxDenominator = 10000L)
            val fracStr = if (!frac.isInteger && abs(value) < 1e7 && abs(frac.toDouble() - value) < 1e-6) {
                "${frac.num}/${frac.den}"
            } else null

            val explanation = generateExplanation(rawExpr, value, decStr, fracStr, angleMode)

            CalculationResult(
                decimalValue = value,
                decimalString = decStr,
                fractionValue = frac,
                fractionString = fracStr,
                expressionFormatted = rawExpr,
                explanation = explanation,
                isError = false
            )
        } catch (e: ArithmeticException) {
            CalculationResult(
                decimalValue = 0.0,
                decimalString = "",
                expressionFormatted = rawExpr,
                isError = true,
                errorMessage = e.message ?: "لا يمكن القسمة على صفر."
            )
        } catch (e: IllegalArgumentException) {
            CalculationResult(
                decimalValue = 0.0,
                decimalString = "",
                expressionFormatted = rawExpr,
                isError = true,
                errorMessage = e.message ?: "العملية غير صحيحة، تحقق من القيم والأقواس."
            )
        } catch (e: Exception) {
            CalculationResult(
                decimalValue = 0.0,
                decimalString = "",
                expressionFormatted = rawExpr,
                isError = true,
                errorMessage = "العملية غير صحيحة، تحقق من القيم والأقواس."
            )
        }
    }

    private fun sanitizeExpression(expr: String): String {
        return expr
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("π", "PI")
            .replace("e", "E")
            .replace("√", "sqrt")
            .replace("∛", "cbrt")
            .replace("sin⁻¹", "asin")
            .replace("cos⁻¹", "acos")
            .replace("tan⁻¹", "atan")
            .replace("²", "^2")
            .replace("³", "^3")
            .replace(" ", "")
    }

    private fun formatDecimal(v: Double): String {
        if (abs(v - v.roundToLong()) < 1e-9) {
            return v.roundToLong().toString()
        }
        return if (abs(v) < 1e-6 || abs(v) >= 1e9) {
            String.format(java.util.Locale.US, "%.6e", v)
        } else {
            String.format(java.util.Locale.US, "%.6f", v).trimEnd('0').trimEnd('.')
        }
    }

    private fun generateExplanation(
        expr: String,
        result: Double,
        decStr: String,
        fracStr: String?,
        angleMode: AngleMode
    ): CalculationExplanation {
        val givens = mutableListOf<String>()
        var law = "ترتيب العمليات الرياضية (الأقواس → الأسس والجذور → الضرب والقسمة → الجمع والطرح)"
        val subst = mutableListOf<String>()
        val calcs = mutableListOf<String>()

        val clean = expr.trim()
        val finalAns = if (fracStr != null) "$fracStr  (أو $decStr)" else decStr

        when {
            // Fraction addition / subtraction / multiplication
            clean.contains("/") && (clean.contains("+") || clean.contains("−") || clean.contains("×") || clean.contains("÷")) -> {
                law = "توحيد المقامات في الجمع والطرح، وضرب البسط في البسط والمقام في المقام في الضرب."
                givens.add("المقدار العددي: $clean")
                subst.add("تطبيق خواص العمليات على الكسور وتبسيط الناتج لأبسط صورة.")
                calcs.add("حساب البسط والمقام بدقة = $finalAns")
            }
            // Trigonometry
            clean.startsWith("sin") || clean.startsWith("cos") || clean.startsWith("tan") -> {
                law = "حساب النسبة المثلثية للزاوية بوضع قياس الزوايا: ${angleMode.label}"
                givens.add("العملية: $clean [النظام: ${angleMode.label}]")
                subst.add("التعويض بقياس الزاوية في الدالة المثلثية")
                calcs.add("قيمة النسبة المثلثية = $finalAns")
            }
            // Square root or cube root
            clean.contains("√") || clean.contains("sqrt") || clean.contains("∛") -> {
                law = "إيجاد الجذر واستخراج العوامل المربعة أو المكعبة."
                givens.add("المقدار تحت الجذر: $clean")
                subst.add("التعويض المباشر في دالة الجذر")
                calcs.add("الناتج الجذري = $finalAns")
            }
            // Power
            clean.contains("^") || clean.contains("²") || clean.contains("³") -> {
                law = "قوانين الأسس: سⁿ تعني ضرب الأساس في نفسه ن من المرات."
                givens.add("المقدار: $clean")
                subst.add("رفع الأساس إلى القوة المحددة")
                calcs.add("الناتج = $finalAns")
            }
            // Logarithm
            clean.startsWith("log") || clean.startsWith("ln") -> {
                law = if (clean.startsWith("ln")) "اللوغاريتم الطبيعي للأساس e (ln x)" else "اللوغاريتم العشري للأساس 10 (log x)"
                givens.add("المقدار: $clean")
                subst.add("تطبيق تعريف اللوغاريتم")
                calcs.add("الناتج = $finalAns")
            }
            else -> {
                givens.add("العملية الحسابية: $clean")
                subst.add("حساب القيم وفق أسبقية العمليات الحسابية المعتمدة")
                calcs.add("الناتج النهائي = $finalAns")
            }
        }

        return CalculationExplanation(
            title = "شرح العملية الحسابية: $clean",
            givens = givens,
            law = law,
            substitution = subst,
            calculation = calcs,
            finalAnswer = finalAns
        )
    }

    /**
     * Recursive Descent Parser for safe arithmetic & math functions
     */
    private class ExpressionParser(
        private val str: String,
        private val angleMode: AngleMode
    ) {
        private var pos = -1
        private var ch = 0

        private fun nextChar() {
            ch = if (++pos < str.length) str[pos].code else -1
        }

        private fun eat(charToEat: Int): Boolean {
            while (ch == ' '.code) nextChar()
            if (ch == charToEat) {
                nextChar()
                return true
            }
            return false
        }

        fun parseExpression(): Double {
            nextChar()
            val x = parseAddSub()
            if (pos < str.length) throw IllegalArgumentException("رمز غير متوقع: '${str[pos]}'")
            return x
        }

        // Addition and Subtraction
        private fun parseAddSub(): Double {
            var x = parseMulDiv()
            while (true) {
                when {
                    eat('+'.code) -> x += parseMulDiv()
                    eat('-'.code) -> x -= parseMulDiv()
                    else -> return x
                }
            }
        }

        // Multiplication, Division, and Modulo
        private fun parseMulDiv(): Double {
            var x = parseFactor()
            while (true) {
                when {
                    eat('*'.code) -> x *= parseFactor()
                    eat('/'.code) -> {
                        val div = parseFactor()
                        if (abs(div) < 1e-12) throw ArithmeticException("لا يمكن القسمة على صفر.")
                        x /= div
                    }
                    eat('%'.code) -> {
                        x = x / 100.0
                    }
                    else -> return x
                }
            }
        }

        // Unary signs, Parentheses, Powers, and Functions
        private fun parseFactor(): Double {
            if (eat('+'.code)) return parseFactor() // unary plus
            if (eat('-'.code)) return -parseFactor() // unary minus

            var x: Double
            val startPos = this.pos

            if (eat('('.code)) {
                x = parseAddSub()
                if (!eat(')'.code)) throw IllegalArgumentException("أقواس غير مغلقة")
            } else if ((ch in '0'.code..'9'.code) || ch == '.'.code) {
                // Numbers
                while ((ch in '0'.code..'9'.code) || ch == '.'.code) nextChar()
                val numStr = str.substring(startPos, this.pos)
                x = numStr.toDoubleOrNull() ?: throw IllegalArgumentException("رقم غير صالح: $numStr")
            } else if (ch in 'a'.code..'z'.code || ch in 'A'.code..'Z'.code) {
                // Function or Constant Name (letters only)
                while (ch in 'a'.code..'z'.code || ch in 'A'.code..'Z'.code) nextChar()
                val func = str.substring(startPos, this.pos)
                when (func) {
                    "PI" -> x = PI
                    "E" -> x = E
                    "c" -> x = LIGHT_SPEED
                    "g" -> x = GRAVITY
                    "h" -> x = PLANCK
                    else -> {
                        val arg = parseFactor()
                        x = evaluateFunction(func, arg)
                    }
                }
            } else {
                throw IllegalArgumentException("رمز غير متوقع: '${if (ch != -1) ch.toChar().toString() else "نهاية التعبير"}'")
            }

            // Factorial operator: e.g. 5!
            if (eat('!'.code)) {
                val n = x.roundToLong()
                if (abs(x - n) > 1e-6 || n < 0 || n > 20) {
                    throw IllegalArgumentException("المضروب ! معرف للأعداد الصحيحة الموجبة حتى 20")
                }
                x = StatisticsEngine.factorial(n).toDouble()
            }

            // Power operator: e.g. 2^3
            if (eat('^'.code)) {
                val exponent = parseFactor()
                x = x.pow(exponent)
            }

            return x
        }

        private fun evaluateFunction(func: String, arg: Double): Double {
            return when (func) {
                "sin" -> sin(toRadians(arg))
                "cos" -> cos(toRadians(arg))
                "tan" -> {
                    val rad = toRadians(arg)
                    if (abs(cos(rad)) < 1e-10) throw ArithmeticException("دالة الظل (tan) غير معرفة عند هذه الزاوية")
                    tan(rad)
                }
                "asin" -> {
                    if (arg < -1.0 || arg > 1.0) throw IllegalArgumentException("قيمة sin⁻¹ يجب أن تكون بين -1 و 1")
                    fromRadians(asin(arg))
                }
                "acos" -> {
                    if (arg < -1.0 || arg > 1.0) throw IllegalArgumentException("قيمة cos⁻¹ يجب أن تكون بين -1 و 1")
                    fromRadians(acos(arg))
                }
                "atan" -> fromRadians(atan(arg))
                "sqrt" -> {
                    if (arg < 0) throw ArithmeticException("لا يوجد جذر تربيعي لعدد سالب في مجموعة الأعداد الحقيقية")
                    sqrt(arg)
                }
                "cbrt" -> cbrt(arg)
                "log" -> {
                    if (arg <= 0) throw ArithmeticException("لوغاريتم لقيمة غير موجبة غير معرف")
                    log10(arg)
                }
                "ln" -> {
                    if (arg <= 0) throw ArithmeticException("اللوغاريتم الطبيعي لقيمة غير موجبة غير معرف")
                    ln(arg)
                }
                "abs" -> abs(arg)
                "exp" -> exp(arg)
                else -> throw IllegalArgumentException("دالة غير معروفة: $func")
            }
        }

        private fun toRadians(angle: Double): Double {
            return when (angleMode) {
                AngleMode.DEG -> Math.toRadians(angle)
                AngleMode.RAD -> angle
                AngleMode.GRAD -> angle * (Math.PI / 200.0)
            }
        }

        private fun fromRadians(rad: Double): Double {
            return when (angleMode) {
                AngleMode.DEG -> Math.toDegrees(rad)
                AngleMode.RAD -> rad
                AngleMode.GRAD -> rad * (200.0 / Math.PI)
            }
        }
    }
}
