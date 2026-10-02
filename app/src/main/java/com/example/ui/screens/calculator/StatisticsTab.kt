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
import com.example.data.calculator.StatisticsEngine
import com.example.data.calculator.StatisticsReport
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.TealAccent

@Composable
fun StatisticsTab(modifier: Modifier = Modifier) {
    var rawValuesInput by remember { mutableStateOf("10, 12, 14, 12, 16, 18, 20") }
    var report by remember { mutableStateOf<StatisticsReport?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Combinatorics state: n and r
    var nInputStr by remember { mutableStateOf("5") }
    var rInputStr by remember { mutableStateOf("2") }
    var combResultStr by remember { mutableStateOf<String?>(null) }
    var combErrorMessage by remember { mutableStateOf<String?>(null) }

    // Run initial calculation
    LaunchedEffect(Unit) {
        try {
            val parsed = rawValuesInput.split(",", " ", "،")
                .mapNotNull { it.trim().toDoubleOrNull() }
            if (parsed.isNotEmpty()) {
                report = StatisticsEngine.calculate(parsed)
            }
        } catch (_: Exception) {}
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section 1: Descriptive Statistics
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
                        text = "📊 مقاييس النزعة المركزية والتشتت (الإحصاء)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
                    )

                    Text(
                        text = "أدخل القيم مفصولة بفاصلة أو مسافة:",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )

                    OutlinedTextField(
                        value = rawValuesInput,
                        onValueChange = { rawValuesInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("stats_input_field"),
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text("مثال: 5, 8, 12, 8, 15") }
                    )

                    Button(
                        onClick = {
                            errorMessage = null
                            try {
                                val parsed = rawValuesInput.split(",", " ", "،")
                                    .mapNotNull { it.trim().toDoubleOrNull() }
                                if (parsed.isEmpty()) {
                                    errorMessage = "يرجى إدخال أرقام صحيحة مفصولة بفاصلة"
                                } else {
                                    report = StatisticsEngine.calculate(parsed)
                                }
                            } catch (e: Exception) {
                                errorMessage = e.message ?: "خطأ في حساب الإحصاء"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("احسب المقاييس الإحصائية 📊", fontWeight = FontWeight.Bold)
                    }

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    // Display Results Table
                    report?.let { rep ->
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatRow("عدد القيم (ن)", "${rep.count}")
                            StatRow("مجموع القيم (مجـ س)", formatVal(rep.sum))
                            StatRow("المتوسط الحسابي (س̄)", formatVal(rep.mean), isHighlight = true)
                            StatRow("الوسيط", formatVal(rep.median), isHighlight = true)
                            StatRow("المنوال", if (rep.modes.isEmpty()) "لا يوجد" else rep.modes.joinToString(", ") { formatVal(it) }, isHighlight = true)
                            StatRow("المدى (العظمى - الصغرى)", formatVal(rep.range))
                            StatRow("التباين (ع²)", formatVal(rep.variance), isHighlight = true)
                            StatRow("الانحراف المعياري (ع)", formatVal(rep.stdDev), isHighlight = true)
                        }

                        // Steps Breakdown
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "خطوات الحساب بالتفصيل:",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
                                )
                                rep.steps.forEach { step ->
                                    Text(text = step, style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 20.sp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Combinatorics (المضروب n! ، التباديل nPr ، التوافيق nCr)
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
                        text = "🎲 التباديل والتوافيق والمضروب (مقرر الجبر)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = nInputStr,
                            onValueChange = { nInputStr = it },
                            label = { Text("قيمة n") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = rInputStr,
                            onValueChange = { rInputStr = it },
                            label = { Text("قيمة r") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // n! Button
                        Button(
                            onClick = {
                                combErrorMessage = null
                                try {
                                    val n = nInputStr.toLongOrNull() ?: 0L
                                    val res = StatisticsEngine.factorial(n)
                                    combResultStr = "$n! = $res\nالقانون: n! = n × (n-1) × ... × 1"
                                } catch (e: Exception) {
                                    combErrorMessage = e.message
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("n!", fontWeight = FontWeight.Bold)
                        }

                        // nPr Button
                        Button(
                            onClick = {
                                combErrorMessage = null
                                try {
                                    val n = nInputStr.toLongOrNull() ?: 0L
                                    val r = rInputStr.toLongOrNull() ?: 0L
                                    val res = StatisticsEngine.nPr(n, r)
                                    combResultStr = "$n P $r = $res\nالقانون: nPr = n! ÷ (n - r)!"
                                } catch (e: Exception) {
                                    combErrorMessage = e.message
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("nPr", fontWeight = FontWeight.Bold)
                        }

                        // nCr Button
                        Button(
                            onClick = {
                                combErrorMessage = null
                                try {
                                    val n = nInputStr.toLongOrNull() ?: 0L
                                    val r = rInputStr.toLongOrNull() ?: 0L
                                    val res = StatisticsEngine.nCr(n, r)
                                    combResultStr = "$n C $r = $res\nالقانون: nCr = n! ÷ (r! × (n - r)!)"
                                } catch (e: Exception) {
                                    combErrorMessage = e.message
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("nCr", fontWeight = FontWeight.Bold)
                        }
                    }

                    if (combErrorMessage != null) {
                        Text(
                            text = combErrorMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    combResultStr?.let { resText ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF0FDF4),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF16A34A)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = resText,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = Color(0xFF166534),
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 22.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String, isHighlight: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isHighlight) Color(0xFFEFF6FF) else Color.Transparent,
                RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal,
                color = if (isHighlight) NavyPrimary else MaterialTheme.colorScheme.onSurface
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                color = if (isHighlight) NavyPrimary else MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

private fun formatVal(d: Double): String {
    if (kotlin.math.abs(d - d.toLong()) < 1e-6) {
        return d.toLong().toString()
    }
    return String.format(java.util.Locale.US, "%.3f", d).trimEnd('0').trimEnd('.')
}
