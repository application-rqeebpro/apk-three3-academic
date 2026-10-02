package com.example.ui.screens.calculator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
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
import com.example.data.calculator.EquationResult
import com.example.data.calculator.EquationSolver
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.NavyPrimary

@Composable
fun EquationsTab(modifier: Modifier = Modifier) {
    var subMode by remember { mutableStateOf(0) } // 0: Linear, 1: Quadratic, 2: System 2x2, 3: System 3x3
    val modeTitles = listOf("خطية (ax+b=c)", "تربيعية (ax²+bx+c=0)", "نظام معادلتين (2×2)", "نظام 3 معادلات (3×3)")

    // Linear Inputs: a·x + b = c
    var linA by remember { mutableStateOf("2") }
    var linB by remember { mutableStateOf("5") }
    var linC by remember { mutableStateOf("15") }

    // Quadratic Inputs: a·x² + b·x + c = 0
    var quadA by remember { mutableStateOf("1") }
    var quadB by remember { mutableStateOf("-5") }
    var quadC by remember { mutableStateOf("6") }

    // System 2x2:
    // a1 x + b1 y = c1
    // a2 x + b2 y = c2
    var s2A1 by remember { mutableStateOf("1") }
    var s2B1 by remember { mutableStateOf("1") }
    var s2C1 by remember { mutableStateOf("10") }
    var s2A2 by remember { mutableStateOf("1") }
    var s2B2 by remember { mutableStateOf("-1") }
    var s2C2 by remember { mutableStateOf("2") }

    // System 3x3
    var s3A1 by remember { mutableStateOf("1") }
    var s3B1 by remember { mutableStateOf("1") }
    var s3C1 by remember { mutableStateOf("1") }
    var s3D1 by remember { mutableStateOf("6") }
    var s3A2 by remember { mutableStateOf("0") }
    var s3B2 by remember { mutableStateOf("2") }
    var s3C2 by remember { mutableStateOf("5") }
    var s3D2 by remember { mutableStateOf("-4") }
    var s3A3 by remember { mutableStateOf("2") }
    var s3B3 by remember { mutableStateOf("5") }
    var s3C3 by remember { mutableStateOf("-1") }
    var s3D3 by remember { mutableStateOf("27") }

    var solutionResult by remember { mutableStateOf<EquationResult?>(null) }
    var showExplanation by remember { mutableStateOf(false) }

    fun solve() {
        showExplanation = false
        solutionResult = when (subMode) {
            0 -> {
                val a = linA.toDoubleOrNull() ?: 1.0
                val b = linB.toDoubleOrNull() ?: 0.0
                val c = linC.toDoubleOrNull() ?: 0.0
                EquationSolver.solveLinear(a, b, c)
            }
            1 -> {
                val a = quadA.toDoubleOrNull() ?: 1.0
                val b = quadB.toDoubleOrNull() ?: 0.0
                val c = quadC.toDoubleOrNull() ?: 0.0
                EquationSolver.solveQuadratic(a, b, c)
            }
            2 -> {
                val a1 = s2A1.toDoubleOrNull() ?: 1.0
                val b1 = s2B1.toDoubleOrNull() ?: 1.0
                val c1 = s2C1.toDoubleOrNull() ?: 0.0
                val a2 = s2A2.toDoubleOrNull() ?: 1.0
                val b2 = s2B2.toDoubleOrNull() ?: -1.0
                val c2 = s2C2.toDoubleOrNull() ?: 0.0
                EquationSolver.solveSystem2x2(a1, b1, c1, a2, b2, c2)
            }
            else -> {
                val a1 = s3A1.toDoubleOrNull() ?: 1.0
                val b1 = s3B1.toDoubleOrNull() ?: 1.0
                val c1 = s3C1.toDoubleOrNull() ?: 1.0
                val d1 = s3D1.toDoubleOrNull() ?: 0.0
                val a2 = s3A2.toDoubleOrNull() ?: 0.0
                val b2 = s3B2.toDoubleOrNull() ?: 1.0
                val c2 = s3C2.toDoubleOrNull() ?: 1.0
                val d2 = s3D2.toDoubleOrNull() ?: 0.0
                val a3 = s3A3.toDoubleOrNull() ?: 1.0
                val b3 = s3B3.toDoubleOrNull() ?: 0.0
                val c3 = s3C3.toDoubleOrNull() ?: 1.0
                val d3 = s3D3.toDoubleOrNull() ?: 0.0
                EquationSolver.solveSystem3x3(a1, b1, c1, d1, a2, b2, c2, d2, a3, b3, c3, d3)
            }
        }
    }

    LaunchedEffect(subMode) {
        solve()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Mode Selector Chips
        item {
            androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(modeTitles.size) { idx ->
                    FilterChip(
                        selected = idx == subMode,
                        onClick = { subMode = idx },
                        label = { Text(modeTitles[idx], fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NavyPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Equation Input Card
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
                    when (subMode) {
                        0 -> {
                            Text(text = "معادلة خطية: أ س + ب = جـ", fontWeight = FontWeight.Bold, color = NavyPrimary)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = linA,
                                    onValueChange = { linA = it },
                                    label = { Text("أ") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                )
                                Text("س +", fontWeight = FontWeight.Bold)
                                OutlinedTextField(
                                    value = linB,
                                    onValueChange = { linB = it },
                                    label = { Text("ب") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                )
                                Text("=", fontWeight = FontWeight.Bold)
                                OutlinedTextField(
                                    value = linC,
                                    onValueChange = { linC = it },
                                    label = { Text("جـ") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        1 -> {
                            Text(text = "معادلة تربيعية: أ س² + ب س + جـ = 0", fontWeight = FontWeight.Bold, color = NavyPrimary)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = quadA,
                                    onValueChange = { quadA = it },
                                    label = { Text("أ") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                )
                                Text("س² +", fontWeight = FontWeight.Bold)
                                OutlinedTextField(
                                    value = quadB,
                                    onValueChange = { quadB = it },
                                    label = { Text("ب") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                )
                                Text("س +", fontWeight = FontWeight.Bold)
                                OutlinedTextField(
                                    value = quadC,
                                    onValueChange = { quadC = it },
                                    label = { Text("جـ") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        2 -> {
                            Text(text = "نظام معادلتين خطيتين (2×2)", fontWeight = FontWeight.Bold, color = NavyPrimary)
                            // Row 1
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = s2A1,
                                    onValueChange = { s2A1 = it },
                                    label = { Text("أ₁") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                )
                                Text("س +", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                OutlinedTextField(
                                    value = s2B1,
                                    onValueChange = { s2B1 = it },
                                    label = { Text("ب₁") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                )
                                Text("ص =", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                OutlinedTextField(
                                    value = s2C1,
                                    onValueChange = { s2C1 = it },
                                    label = { Text("جـ₁") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            // Row 2
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = s2A2,
                                    onValueChange = { s2A2 = it },
                                    label = { Text("أ₂") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                )
                                Text("س +", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                OutlinedTextField(
                                    value = s2B2,
                                    onValueChange = { s2B2 = it },
                                    label = { Text("ب₂") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                )
                                Text("ص =", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                OutlinedTextField(
                                    value = s2C2,
                                    onValueChange = { s2C2 = it },
                                    label = { Text("جـ₂") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        3 -> {
                            Text(text = "نظام ثلاث معادلات خطية (3×3)", fontWeight = FontWeight.Bold, color = NavyPrimary)
                            // Row 1
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(value = s3A1, onValueChange = { s3A1 = it }, label = { Text("أ₁") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), shape = RoundedCornerShape(6.dp), modifier = Modifier.weight(1f))
                                Text("س+", fontSize = 10.sp)
                                OutlinedTextField(value = s3B1, onValueChange = { s3B1 = it }, label = { Text("ب₁") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), shape = RoundedCornerShape(6.dp), modifier = Modifier.weight(1f))
                                Text("ص+", fontSize = 10.sp)
                                OutlinedTextField(value = s3C1, onValueChange = { s3C1 = it }, label = { Text("جـ₁") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), shape = RoundedCornerShape(6.dp), modifier = Modifier.weight(1f))
                                Text("ع=", fontSize = 10.sp)
                                OutlinedTextField(value = s3D1, onValueChange = { s3D1 = it }, label = { Text("د₁") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), shape = RoundedCornerShape(6.dp), modifier = Modifier.weight(1f))
                            }
                            // Row 2
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(value = s3A2, onValueChange = { s3A2 = it }, label = { Text("أ₂") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), shape = RoundedCornerShape(6.dp), modifier = Modifier.weight(1f))
                                Text("س+", fontSize = 10.sp)
                                OutlinedTextField(value = s3B2, onValueChange = { s3B2 = it }, label = { Text("ب₂") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), shape = RoundedCornerShape(6.dp), modifier = Modifier.weight(1f))
                                Text("ص+", fontSize = 10.sp)
                                OutlinedTextField(value = s3C2, onValueChange = { s3C2 = it }, label = { Text("جـ₂") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), shape = RoundedCornerShape(6.dp), modifier = Modifier.weight(1f))
                                Text("ع=", fontSize = 10.sp)
                                OutlinedTextField(value = s3D2, onValueChange = { s3D2 = it }, label = { Text("د₂") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), shape = RoundedCornerShape(6.dp), modifier = Modifier.weight(1f))
                            }
                            // Row 3
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(value = s3A3, onValueChange = { s3A3 = it }, label = { Text("أ₃") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), shape = RoundedCornerShape(6.dp), modifier = Modifier.weight(1f))
                                Text("س+", fontSize = 10.sp)
                                OutlinedTextField(value = s3B3, onValueChange = { s3B3 = it }, label = { Text("ب₃") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), shape = RoundedCornerShape(6.dp), modifier = Modifier.weight(1f))
                                Text("ص+", fontSize = 10.sp)
                                OutlinedTextField(value = s3C3, onValueChange = { s3C3 = it }, label = { Text("جـ₃") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), shape = RoundedCornerShape(6.dp), modifier = Modifier.weight(1f))
                                Text("ع=", fontSize = 10.sp)
                                OutlinedTextField(value = s3D3, onValueChange = { s3D3 = it }, label = { Text("د₃") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), shape = RoundedCornerShape(6.dp), modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    Button(
                        onClick = { solve() },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("حل المعادلة ⚖️", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Solution Result Card
        solutionResult?.let { res ->
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFF0FDF4),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF16A34A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = res.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                        )

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "الإجابة النهائية:",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF15803D), fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = res.finalAnswer,
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        color = Color(0xFF166534),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                )
                            }
                        }

                        // Button to Explain Solution
                        Button(
                            onClick = { showExplanation = !showExplanation },
                            colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.align(Alignment.Start)
                        ) {
                            Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (showExplanation) "إخفاء خطوات الحل ▲" else "اشرح الحل 💡",
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Step-by-Step Whiteboard Explanation
                        AnimatedVisibility(visible = showExplanation) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, RoundedCornerShape(12.dp))
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // 1. المعطيات
                                if (res.givens.isNotEmpty()) {
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(text = "المعطيات:", fontWeight = FontWeight.Bold, color = NavyPrimary)
                                        res.givens.forEach { g ->
                                            Text(text = g, style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp))
                                        }
                                    }
                                }

                                // 2. القانون
                                if (res.law.isNotBlank()) {
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(text = "القانون:", fontWeight = FontWeight.Bold, color = NavyPrimary)
                                        Text(text = res.law, style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF0F766E), fontWeight = FontWeight.SemiBold, lineHeight = 22.sp))
                                    }
                                }

                                // 3. التعويض
                                if (res.substitution.isNotEmpty()) {
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(text = "التعويض:", fontWeight = FontWeight.Bold, color = NavyPrimary)
                                        res.substitution.forEach { s ->
                                            Text(text = s, style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp))
                                        }
                                    }
                                }

                                // 4. الحساب
                                if (res.calculation.isNotEmpty()) {
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(text = "الحل والحساب:", fontWeight = FontWeight.Bold, color = NavyPrimary)
                                        res.calculation.forEach { c ->
                                            Text(text = c, style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp))
                                        }
                                    }
                                }

                                // 5. الإجابة
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(text = "الإجابة:", fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                                    Text(text = res.finalAnswer, style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF166534), fontWeight = FontWeight.Bold))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
