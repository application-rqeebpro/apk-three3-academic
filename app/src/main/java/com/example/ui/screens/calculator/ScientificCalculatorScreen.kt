package com.example.ui.screens.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.calculator.ScientificEvaluator
import com.example.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScientificCalculatorScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = { viewModel.navigateBack() }
) {
    var displayExpression by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("0") }
    var selectedTab by remember { mutableStateOf(0) } // 0: Scientific, 1: Complex, 2: Matrices

    val tabs = listOf("علمية متقدمة", "أعداد مركبة", "مصفوفات ومعادلات")

    val buttons = listOf(
        "C", "(", ")", "/",
        "sin", "cos", "tan", "*",
        "7", "8", "9", "-",
        "4", "5", "6", "+",
        "1", "2", "3", "^",
        "0", ".", "DEL", "="
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, style = MaterialTheme.typography.bodySmall) }
                    )
                }
            }

            // Display Screen
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = displayExpression.ifEmpty { "0" },
                        fontSize = 20.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.End,
                        maxLines = 2
                    )
                    Text(
                        text = resultText,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.End,
                        maxLines = 1
                    )
                }
            }

            if (selectedTab == 0) {
                // Keypad Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(buttons) { btn ->
                        val isOp = btn in listOf("/", "*", "-", "+", "^", "=")
                        val isAction = btn in listOf("C", "DEL")
                        val isSpecial = btn in listOf("sin", "cos", "tan", "(", ")")

                        val btnColor = when {
                            btn == "=" -> MaterialTheme.colorScheme.primary
                            isAction -> MaterialTheme.colorScheme.errorContainer
                            isOp -> MaterialTheme.colorScheme.secondaryContainer
                            isSpecial -> MaterialTheme.colorScheme.tertiaryContainer
                            else -> MaterialTheme.colorScheme.surface
                        }

                        val textColor = when {
                            btn == "=" -> MaterialTheme.colorScheme.onPrimary
                            isAction -> MaterialTheme.colorScheme.onErrorContainer
                            isOp -> MaterialTheme.colorScheme.onSecondaryContainer
                            isSpecial -> MaterialTheme.colorScheme.onTertiaryContainer
                            else -> MaterialTheme.colorScheme.onSurface
                        }

                        Surface(
                            modifier = Modifier
                                .aspectRatio(1.25f)
                                .clickable {
                                    when (btn) {
                                        "C" -> {
                                            displayExpression = ""
                                            resultText = "0"
                                        }
                                        "DEL" -> {
                                            if (displayExpression.isNotEmpty()) {
                                                displayExpression = displayExpression.dropLast(1)
                                            }
                                        }
                                        "=" -> {
                                            try {
                                                val res = ScientificEvaluator.evaluate(displayExpression)
                                                resultText = res.formattedResult
                                            } catch (e: Exception) {
                                                resultText = "خطأ حسابي"
                                            }
                                        }
                                        else -> {
                                            displayExpression += btn
                                        }
                                    }
                                },
                            shape = RoundedCornerShape(12.dp),
                            color = btnColor
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (btn == "DEL") {
                                    Icon(Icons.Default.Backspace, contentDescription = null, tint = textColor, modifier = Modifier.size(20.dp))
                                } else {
                                    Text(
                                        text = btn,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textColor
                                    )
                                }
                            }
                        }
                    }
                }
            } else if (selectedTab == 1) {
                // Complex Numbers Quick Tool
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("حاسبة الأعداد المركبة (الصورة الجبرية والقطبية):", fontWeight = FontWeight.Bold)
                        Text("مثال: ع = 3 + 4ت", style = MaterialTheme.typography.bodyMedium)
                        Text("المقياس (ر) = 5", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Text("السعة (هـ) = 53.13 درجة (0.927 راديان)", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                        Text("الصورة القطبية: ع = 5 [جتا(53.13) + ت جا(53.13)]")
                    }
                }
            } else {
                // Matrices Tool
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("حاسبة المحددات والمصفوفات (2×2 و 3×3):", fontWeight = FontWeight.Bold)
                        Text("قاعدة كرامر لحل نظم المعادلات الخطية الوزارية")
                        Text("محدد المصفوفة |A| ومصفوفة المعكوس A⁻¹ بخطوات وزارية كاملة")
                    }
            }
        }
    }
}
