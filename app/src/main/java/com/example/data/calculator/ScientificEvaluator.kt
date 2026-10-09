package com.example.data.calculator

import kotlin.math.*

object ScientificEvaluator {

    fun evaluate(expression: String, angleMode: AngleMode = AngleMode.DEGREE): CalculationResult {
        val expr = expression.trim()
        if (expr.isEmpty()) return CalculationResult()

        try {
            var sanitized = expr
                .replace("×", "*")
                .replace("÷", "/")
                .replace("−", "-")
                .replace("π", "${Math.PI}")
                .replace("e", "${Math.E}")

            val result = evalSimple(sanitized, angleMode)
            val formatted = String.format(java.util.Locale.US, "%.6f", result).trimEnd('0').trimEnd('.')

            val fraction = Fraction.fromDouble(result)?.toString()

            return CalculationResult(
                primaryDisplay = formatted,
                expression = expr,
                decimalForm = formatted,
                fractionForm = fraction
            )
        } catch (e: Exception) {
            return CalculationResult(
                primaryDisplay = "خطأ",
                expression = expr,
                isError = true,
                errorMessage = e.message
            )
        }
    }

    private fun evalSimple(str: String, angleMode: AngleMode): Double {
        class Parser {
            var pos = -1
            var ch = ' '

            fun nextChar() {
                ch = if (++pos < str.length) str[pos] else '\u0000'
            }

            fun eat(charToEat: Char): Boolean {
                while (ch == ' ') nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < str.length) throw IllegalArgumentException("رمز غير متوقع: ${str[pos]}")
                return x
            }

            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+')) x += parseTerm()
                    else if (eat('-')) x -= parseTerm()
                    else return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*')) x *= parseFactor()
                    else if (eat('/')) {
                        val divisor = parseFactor()
                        if (divisor == 0.0) throw ArithmeticException("القسمة على صفر")
                        x /= divisor
                    } else return x
                }
            }

            fun parseFactor(): Double {
                if (eat('+')) return parseFactor()
                if (eat('-')) return -parseFactor()

                var x: Double
                val startPos = pos
                if (eat('(')) {
                    x = parseExpression()
                    eat(')')
                } else if ((ch in '0'..'9') || ch == '.') {
                    while ((ch in '0'..'9') || ch == '.') nextChar()
                    x = str.substring(startPos, pos).toDouble()
                } else if (ch in 'a'..'z') {
                    while (ch in 'a'..'z') nextChar()
                    val func = str.substring(startPos, pos)
                    val arg = parseFactor()
                    val rad = if (angleMode == AngleMode.DEGREE) Math.toRadians(arg) else arg
                    x = when (func) {
                        "sin" -> sin(rad)
                        "cos" -> cos(rad)
                        "tan" -> tan(rad)
                        "sqrt" -> sqrt(arg)
                        "ln" -> ln(arg)
                        "log" -> log10(arg)
                        else -> throw IllegalArgumentException("دالة غير معروفة: $func")
                    }
                } else {
                    throw IllegalArgumentException("رمز غير متوقع: $ch")
                }

                if (eat('^')) x = x.pow(parseFactor())

                return x
            }
        }

        return Parser().parse()
    }
}
