package com.example.ui.screens.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.calculator.ComplexNumber
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.TealAccent

@Composable
fun ComplexNumbersTab(modifier: Modifier = Modifier) {
    // Cartesian Input: z1 = a + bi
    var aStr by remember { mutableStateOf("4") }
    var bStr by remember { mutableStateOf("0") }

    // Cartesian Input: z2 = c + di
    var cStr by remember { mutableStateOf("0") }
    var dStr by remember { mutableStateOf("0") }

    // Polar Input: r and theta (for conversion)
    var polarRStr by remember { mutableStateOf("4") }
    var polarThetaStr by remember { mutableStateOf("60") }

    var resultText by remember { mutableStateOf("ع = [4 ، 60°]\nطول الجذر: √4 = 2\nالزاوية: 60° ÷ 2 = 30°\n√ع = [2 ، 30°]") }
    var resultTitle by remember { mutableStateOf("مثال الأعداد المركبة (المنهج اليمني)") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun getZ1(): ComplexNumber? {
        val a = aStr.toDoubleOrNull() ?: return null
        val b = bStr.toDoubleOrNull() ?: return null
        return ComplexNumber(a, b)
    }

    fun getZ2(): ComplexNumber? {
        val c = cStr.toDoubleOrNull() ?: return null
        val d = dStr.toDoubleOrNull() ?: return null
        return ComplexNumber(c, d)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section 1: Cartesian operations
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "ℂ الأعداد المركبة بالصورة الجبرية (أ + ب ت)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
                    )

                    // z1 Input
                    Text(text = "العدد المركب الأول (ع₁):", style = MaterialTheme.typography.labelMedium.copy(color = NavyPrimary, fontWeight = FontWeight.Bold))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = aStr,
                            onValueChange = { aStr = it },
                            label = { Text("الحقيقي أ") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )
                        Text("+", fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = bStr,
                            onValueChange = { bStr = it },
                            label = { Text("التخيلي ب") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )
                        Text("ت", fontWeight = FontWeight.Bold, color = NavyPrimary)
                    }

                    // z2 Input
                    Text(text = "العدد المركب الثاني (ع₂):", style = MaterialTheme.typography.labelMedium.copy(color = NavyPrimary, fontWeight = FontWeight.Bold))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = cStr,
                            onValueChange = { cStr = it },
                            label = { Text("الحقيقي جـ") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )
                        Text("+", fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = dStr,
                            onValueChange = { dStr = it },
                            label = { Text("التخيلي د") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )
                        Text("ت", fontWeight = FontWeight.Bold, color = NavyPrimary)
                    }

                    // Operation Action Buttons
                    Text(text = "العمليات على العددين:", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val z1 = getZ1()
                                val z2 = getZ2()
                                if (z1 != null && z2 != null) {
                                    val sum = z1 + z2
                                    resultTitle = "جمع العددين (ع₁ + ع₂)"
                                    resultText = "ع₁ + ع₂ = (${z1.toAlgebraicString()}) + (${z2.toAlgebraicString()})\n= ${sum.toAlgebraicString()}\nبالصورة القطبية: ${sum.toPolarString()}"
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("ع₁ + ع₂")
                        }

                        Button(
                            onClick = {
                                val z1 = getZ1()
                                val z2 = getZ2()
                                if (z1 != null && z2 != null) {
                                    val diff = z1 - z2
                                    resultTitle = "طرح العددين (ع₁ - ع₂)"
                                    resultText = "ع₁ - ع₂ = (${z1.toAlgebraicString()}) - (${z2.toAlgebraicString()})\n= ${diff.toAlgebraicString()}\nبالصورة القطبية: ${diff.toPolarString()}"
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("ع₁ - ع₂")
                        }

                        Button(
                            onClick = {
                                val z1 = getZ1()
                                val z2 = getZ2()
                                if (z1 != null && z2 != null) {
                                    val prod = z1 * z2
                                    resultTitle = "ضرب العددين (ع₁ × ع₂)"
                                    resultText = "ع₁ × ع₂ = (${z1.toAlgebraicString()}) × (${z2.toAlgebraicString()})\n= ${prod.toAlgebraicString()}\nبالصورة القطبية: ${prod.toPolarString()}"
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("ع₁ × ع₂")
                        }

                        Button(
                            onClick = {
                                val z1 = getZ1()
                                val z2 = getZ2()
                                if (z1 != null && z2 != null) {
                                    try {
                                        val quot = z1 / z2
                                        resultTitle = "قسمة العددين (ع₁ ÷ ع₂)"
                                        resultText = "ع₁ ÷ ع₂ = (${z1.toAlgebraicString()}) ÷ (${z2.toAlgebraicString()})\n= ${quot.toAlgebraicString()}\nبالصورة القطبية: ${quot.toPolarString()}"
                                    } catch (e: Exception) {
                                        errorMessage = e.message
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("ع₁ ÷ ع₂")
                        }
                    }

                    // Properties for z1
                    Text(text = "خواص العدد ع₁ (المقياس، السعة، المرافق، الصورة القطبية):", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val z1 = getZ1() ?: return@OutlinedButton
                                resultTitle = "مرافق ومقياس وسعة العدد ع₁"
                                resultText = "العدد: ع₁ = ${z1.toAlgebraicString()}\n" +
                                        "المرافق: ع̄₁ = ${z1.conjugate().toAlgebraicString()}\n" +
                                        "المقياس (ر): |ع₁| = √(أ² + ب²) = ${formatD(z1.r)}\n" +
                                        "السعة (هـ): ${formatD(z1.thetaDeg)}° (${formatD(z1.thetaRad)} راديان)\n" +
                                        "الصورة القطبية: [ر ، هـ] = ${z1.toPolarString()}"
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("المرافق والسعة", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                val z1 = getZ1() ?: return@OutlinedButton
                                val sq = z1.sqrt()
                                resultTitle = "الجذر التربيعي للعدد ع₁ (قانون دي موافر)"
                                resultText = "ع₁ = ${z1.toPolarString()}\n" +
                                        "طول الجذر: √${formatD(z1.r)} = ${formatD(sq.r)}\n" +
                                        "الزاوية: ${formatD(z1.thetaDeg)}° ÷ 2 = ${formatD(sq.thetaDeg)}°\n" +
                                        "إذن: √ع₁ = ${sq.toPolarString()}\n" +
                                        "بالصورة الجبرية: ${sq.toAlgebraicString()}"
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("الجذر التربيعي √ع", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section 2: Polar Form to Cartesian Form
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "📐 التحويل بين الصورة القطبية [ر ، هـ] والجبرية (أ + ب ت)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = polarRStr,
                            onValueChange = { polarRStr = it },
                            label = { Text("المقياس (ر)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = polarThetaStr,
                            onValueChange = { polarThetaStr = it },
                            label = { Text("الزاوية هـ بالدرجات (°)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val r = polarRStr.toDoubleOrNull() ?: 4.0
                                val deg = polarThetaStr.toDoubleOrNull() ?: 60.0
                                val z = ComplexNumber.fromPolar(r, deg)
                                resultTitle = "تحويل من قطبية إلى جبرية"
                                resultText = "الصورة القطبية: [$r ، $deg°]\n" +
                                        "أ = ر جتا(هـ) = $r × جتا($deg°) = ${formatD(z.real)}\n" +
                                        "ب = ر جا(هـ) = $r × جا($deg°) = ${formatD(z.imag)}\n" +
                                        "الصورة الجبرية: ${z.toAlgebraicString()}"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("حوّل إلى أ + ب ت", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val r = polarRStr.toDoubleOrNull() ?: 4.0
                                val deg = polarThetaStr.toDoubleOrNull() ?: 60.0
                                val rootR = kotlin.math.sqrt(r)
                                val rootDeg = deg / 2.0
                                val z = ComplexNumber.fromPolar(rootR, rootDeg)
                                resultTitle = "الجذر التربيعي للعدد ع = [$r ، $deg°]"
                                resultText = "ع = [$r ، $deg°]\n\n" +
                                        "طول الجذر:\n√$r = ${formatD(rootR)}\n\n" +
                                        "الزاوية:\n$deg° ÷ 2 = $rootDeg°\n\n" +
                                        "إذن:\n√ع = [${formatD(rootR)} ، $rootDeg°]\n\n" +
                                        "الإجابة:\n[${formatD(rootR)} ، $rootDeg°]"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("احسب الجذر التربيعي", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section 3: Result Display Card
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFF0FDF4),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF16A34A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = resultTitle,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                    )
                    HorizontalDivider(color = Color(0xFF16A34A).copy(alpha = 0.3f))
                    Text(
                        text = resultText,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color(0xFF166534),
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 24.sp
                        )
                    )
                }
            }
        }
    }
}

private fun formatD(d: Double): String {
    if (kotlin.math.abs(d - d.toLong()) < 1e-6) {
        return d.toLong().toString()
    }
    return String.format(java.util.Locale.US, "%.3f", d).trimEnd('0').trimEnd('.')
}
