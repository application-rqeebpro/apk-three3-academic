package com.example.ui.screens.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.calculator.Matrix
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.NavyPrimary

@Composable
fun MatricesTab(modifier: Modifier = Modifier) {
    var matrixSize by remember { mutableStateOf(2) } // 2 for 2x2, 3 for 3x3

    // Matrix A cells (up to 3x3)
    val aCells = remember {
        mutableStateListOf(
            mutableStateListOf("1", "2", "0"),
            mutableStateListOf("3", "4", "0"),
            mutableStateListOf("0", "0", "1")
        )
    }

    // Matrix B cells (up to 3x3)
    val bCells = remember {
        mutableStateListOf(
            mutableStateListOf("2", "0", "0"),
            mutableStateListOf("1", "3", "0"),
            mutableStateListOf("0", "0", "1")
        )
    }

    var resultTitle by remember { mutableStateOf("نتيجة العمليات على المصفوفات") }
    var resultMatrix by remember { mutableStateOf<Matrix?>(null) }
    var singleValResult by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun getMatrixA(): Matrix {
        val data = (0 until matrixSize).map { r ->
            (0 until matrixSize).map { c ->
                aCells[r][c].toDoubleOrNull() ?: 0.0
            }
        }
        return Matrix(matrixSize, matrixSize, data)
    }

    fun getMatrixB(): Matrix {
        val data = (0 until matrixSize).map { r ->
            (0 until matrixSize).map { c ->
                bCells[r][c].toDoubleOrNull() ?: 0.0
            }
        }
        return Matrix(matrixSize, matrixSize, data)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Size Selector
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "رتبة المصفوفة:",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = matrixSize == 2,
                        onClick = { matrixSize = 2 },
                        label = { Text("2 × 2") }
                    )
                    FilterChip(
                        selected = matrixSize == 3,
                        onClick = { matrixSize = 3 },
                        label = { Text("3 × 3") }
                    )
                }
            }
        }

        // Matrix A Input Grid
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = "المصفوفة أ (${matrixSize}×${matrixSize}):", fontWeight = FontWeight.Bold, color = NavyPrimary)
                    for (r in 0 until matrixSize) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            for (c in 0 until matrixSize) {
                                OutlinedTextField(
                                    value = aCells[r][c],
                                    onValueChange = { aCells[r][c] = it },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Matrix B Input Grid
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = "المصفوفة ب (${matrixSize}×${matrixSize}):", fontWeight = FontWeight.Bold, color = NavyPrimary)
                    for (r in 0 until matrixSize) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            for (c in 0 until matrixSize) {
                                OutlinedTextField(
                                    value = bCells[r][c],
                                    onValueChange = { bCells[r][c] = it },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Action Buttons
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "العمليات الرياضية على المصفوفات:", style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                errorMessage = null
                                singleValResult = null
                                resultTitle = "جمع المصفوفتين (أ + ب)"
                                resultMatrix = getMatrixA() + getMatrixB()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("أ + ب")
                        }

                        Button(
                            onClick = {
                                errorMessage = null
                                singleValResult = null
                                resultTitle = "طرح المصفوفتين (أ - ب)"
                                resultMatrix = getMatrixA() - getMatrixB()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("أ - ب")
                        }

                        Button(
                            onClick = {
                                errorMessage = null
                                singleValResult = null
                                resultTitle = "ضرب المصفوفتين (أ × ب)"
                                resultMatrix = getMatrixA() * getMatrixB()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("أ × ب")
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = {
                                errorMessage = null
                                val a = getMatrixA()
                                val det = a.determinant()
                                resultTitle = "محدد المصفوفة أ (|أ|)"
                                resultMatrix = null
                                singleValResult = "|أ| = ${Matrix.formatVal(det)}"
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("محدد |أ|")
                        }

                        OutlinedButton(
                            onClick = {
                                errorMessage = null
                                singleValResult = null
                                resultTitle = "معكوس المصفوفة أ (أ⁻¹)"
                                val inv = getMatrixA().inverse()
                                if (inv != null) {
                                    resultMatrix = inv
                                } else {
                                    errorMessage = "المصفوفة أ منفردة (محددها = 0)، لذا ليس لها معكوس."
                                    resultMatrix = null
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("معكوس أ⁻¹")
                        }

                        OutlinedButton(
                            onClick = {
                                errorMessage = null
                                singleValResult = null
                                resultTitle = "منقول المصفوفة أ (أᵀ)"
                                resultMatrix = getMatrixA().transpose()
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("منقول أᵀ")
                        }
                    }
                }
            }
        }

        // Result Card
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFF0FDF4),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF16A34A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = resultTitle,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                    )
                    HorizontalDivider(color = Color(0xFF16A34A).copy(alpha = 0.3f))

                    if (errorMessage != null) {
                        Text(text = errorMessage ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                    } else if (singleValResult != null) {
                        Text(
                            text = singleValResult ?: "",
                            style = MaterialTheme.typography.headlineSmall.copy(color = Color(0xFF166534), fontWeight = FontWeight.Bold)
                        )
                    } else if (resultMatrix != null) {
                        val m = resultMatrix!!
                        Column(
                            modifier = Modifier
                                .background(Color.White, RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            for (r in 0 until m.rows) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    for (c in 0 until m.cols) {
                                        Text(
                                            text = Matrix.formatVal(m[r, c]),
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF166534)
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Text(text = "اضغط على أي عملية بالأعلى لحساب الناتج.", style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF15803D)))
                    }
                }
            }
        }
    }
}
