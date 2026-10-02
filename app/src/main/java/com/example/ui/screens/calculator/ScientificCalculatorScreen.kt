package com.example.ui.screens.calculator

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.calculator.*
import com.example.ui.MainViewModel
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.TealAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScientificCalculatorScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(CalculatorTab.SCIENTIFIC) }
    var angleMode by remember { mutableStateOf(AngleMode.DEG) }

    // Core scientific display state
    var expression by remember { mutableStateOf("1/2 + 3/4") }
    var calculationResult by remember {
        mutableStateOf(ScientificEvaluator.evaluate("1/2 + 3/4", AngleMode.DEG))
    }
    var showFractionFormat by remember { mutableStateOf(true) } // Toggle S⇄D
    var memoryValue by remember { mutableStateOf(0.0) }
    var historyList by remember {
        mutableStateOf(
            listOf(
                CalculatorHistoryItem(expression = "2 + 3", result = "5"),
                CalculatorHistoryItem(expression = "sin(30)", result = "0.5"),
                CalculatorHistoryItem(expression = "1/2 + 1/4", result = "3/4")
            )
        )
    }

    var showHistoryDialog by remember { mutableStateOf(false) }
    var showExplanationDialog by remember { mutableStateOf(false) }
    var showFractionInputDialog by remember { mutableStateOf(false) }

    // Quick evaluate handler
    fun executeEvaluation() {
        if (expression.isBlank()) return
        val res = ScientificEvaluator.evaluate(expression, angleMode)
        calculationResult = res
        if (!res.isError) {
            val ans = if (res.fractionString != null && showFractionFormat) res.fractionString else res.decimalString
            historyList = listOf(
                CalculatorHistoryItem(
                    expression = expression,
                    result = ans ?: res.decimalString,
                    explanation = res.explanation
                )
            ) + historyList.take(20)
        }
    }

    // Run initial evaluation
    LaunchedEffect(angleMode) {
        if (expression.isNotBlank()) {
            calculationResult = ScientificEvaluator.evaluate(expression, angleMode)
        }
    }

    Scaffold(
        topBar = {
            // Calculator Tab Navigation Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                ScrollableTabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    edgePadding = 12.dp,
                    divider = {},
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                            color = NavyPrimary,
                            height = 3.dp
                        )
                    }
                ) {
                    CalculatorTab.values().forEach { tab ->
                        Tab(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            text = {
                                Text(
                                    text = "${tab.icon} ${tab.title}",
                                    fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            },
                            selectedContentColor = NavyPrimary,
                            unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                CalculatorTab.SCIENTIFIC -> {
                    ScientificKeypadView(
                        expression = expression,
                        onExpressionChange = { expression = it },
                        calculationResult = calculationResult,
                        angleMode = angleMode,
                        onAngleModeChange = { angleMode = it },
                        showFractionFormat = showFractionFormat,
                        onToggleFractionFormat = { showFractionFormat = !showFractionFormat },
                        memoryValue = memoryValue,
                        onMemoryChange = { memoryValue = it },
                        onEvaluate = { executeEvaluation() },
                        onOpenHistory = { showHistoryDialog = true },
                        onOpenExplanation = { showExplanationDialog = true },
                        onOpenFractionBuilder = { showFractionInputDialog = true }
                    )
                }
                CalculatorTab.COMPLEX -> ComplexNumbersTab()
                CalculatorTab.EQUATIONS -> EquationsTab()
                CalculatorTab.MATRICES -> MatricesTab()
                CalculatorTab.STATISTICS -> StatisticsTab()
                CalculatorTab.CONVERTER -> UnitConverterTab()
            }
        }
    }

    // History Dialog
    if (showHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showHistoryDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🕒", fontSize = 20.sp)
                    Text("سجل العمليات السابقة", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                if (historyList.isEmpty()) {
                    Text("لا توجد عمليات سابقة حتى الآن.")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(historyList) { item ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                onClick = {
                                    expression = item.expression
                                    calculationResult = ScientificEvaluator.evaluate(expression, angleMode)
                                    showHistoryDialog = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = item.expression, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                        Text(text = "= ${item.result}", style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF15803D), fontWeight = FontWeight.Bold))
                                    }
                                    Text(text = "استرجاع ↩", fontSize = 11.sp, color = NavyPrimary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { historyList = emptyList() }) {
                    Text("مسح السجل", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                Button(onClick = { showHistoryDialog = false }) {
                    Text("إغلاق")
                }
            }
        )
    }

    // Step-by-Step Explanation Dialog (💡 اشرح الحل)
    if (showExplanationDialog) {
        val exp = calculationResult.explanation
        AlertDialog(
            onDismissRequest = { showExplanationDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("💡", fontSize = 22.sp)
                    Text("شرح الحل خطوة بخطوة", fontWeight = FontWeight.Bold, color = NavyPrimary)
                }
            },
            text = {
                if (exp == null) {
                    Text("قم بإدخال عملية حسابية والضغط على = أولاً لإظهار الشرح النموذجي.")
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(text = exp.title, fontWeight = FontWeight.Bold, color = NavyPrimary, fontSize = 14.sp)
                        HorizontalDivider()

                        if (exp.givens.isNotEmpty()) {
                            Text(text = "المعطيات:", fontWeight = FontWeight.Bold, color = NavyPrimary, fontSize = 13.sp)
                            exp.givens.forEach { g ->
                                Text(text = "• $g", fontSize = 12.sp, lineHeight = 18.sp)
                            }
                        }

                        if (exp.law.isNotBlank()) {
                            Text(text = "القانون المستخدم:", fontWeight = FontWeight.Bold, color = NavyPrimary, fontSize = 13.sp)
                            Text(text = exp.law, fontSize = 12.sp, color = Color(0xFF0F766E), fontWeight = FontWeight.SemiBold, lineHeight = 18.sp)
                        }

                        if (exp.substitution.isNotEmpty()) {
                            Text(text = "التعويض:", fontWeight = FontWeight.Bold, color = NavyPrimary, fontSize = 13.sp)
                            exp.substitution.forEach { s ->
                                Text(text = "• $s", fontSize = 12.sp, lineHeight = 18.sp)
                            }
                        }

                        if (exp.calculation.isNotEmpty()) {
                            Text(text = "الحساب خطوة بخطوة:", fontWeight = FontWeight.Bold, color = NavyPrimary, fontSize = 13.sp)
                            exp.calculation.forEach { c ->
                                Text(text = "• $c", fontSize = 12.sp, lineHeight = 18.sp)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF0FDF4),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF16A34A)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(text = "الإجابة النهائية:", fontWeight = FontWeight.Bold, color = Color(0xFF15803D), fontSize = 12.sp)
                                Text(text = exp.finalAnswer, fontWeight = FontWeight.Bold, color = Color(0xFF166534), fontSize = 16.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showExplanationDialog = false }) {
                    Text("حسناً")
                }
            }
        )
    }

    // Fraction Input Builder Dialog (⅟)
    if (showFractionInputDialog) {
        var numStr by remember { mutableStateOf("3") }
        var denStr by remember { mutableStateOf("4") }

        AlertDialog(
            onDismissRequest = { showFractionInputDialog = false },
            title = {
                Text("إدخال كسر اعتيادي (بسط / مقام)", fontWeight = FontWeight.Bold, color = NavyPrimary)
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("أدخل البسط والمقام بشكل مستقل:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    OutlinedTextField(
                        value = numStr,
                        onValueChange = { numStr = it },
                        label = { Text("البسط") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    HorizontalDivider(modifier = Modifier.width(140.dp), thickness = 3.dp, color = NavyPrimary)

                    OutlinedTextField(
                        value = denStr,
                        onValueChange = { denStr = it },
                        label = { Text("المقام") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val n = numStr.trim()
                        val d = denStr.trim()
                        if (n.isNotBlank() && d.isNotBlank()) {
                            expression = if (expression.isBlank() || expression == "0") "$n/$d" else "$expression $n/$d"
                            showFractionInputDialog = false
                            executeEvaluation()
                        }
                    }
                ) {
                    Text("إضافة الكسر للحاسبة")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFractionInputDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
private fun ScientificKeypadView(
    expression: String,
    onExpressionChange: (String) -> Unit,
    calculationResult: CalculationResult,
    angleMode: AngleMode,
    onAngleModeChange: (AngleMode) -> Unit,
    showFractionFormat: Boolean,
    onToggleFractionFormat: () -> Unit,
    memoryValue: Double,
    onMemoryChange: (Double) -> Unit,
    onEvaluate: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenExplanation: () -> Unit,
    onOpenFractionBuilder: () -> Unit
) {
    val exprScrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Top Calculator Display Card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("calculator_screen_display")
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Top Status Bar: Angle mode chips + History + Explanation buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Angle Mode Switcher (DEG | RAD | GRAD)
                    Row(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                            .padding(2.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        AngleMode.values().forEach { mode ->
                            val isSelected = mode == angleMode
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSelected) NavyPrimary else Color.Transparent,
                                onClick = { onAngleModeChange(mode) },
                                modifier = Modifier.height(26.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                ) {
                                    Text(
                                        text = mode.label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (memoryValue != 0.0) {
                            Surface(shape = RoundedCornerShape(4.dp), color = AmberSecondary) {
                                Text("M", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                            }
                        }

                        IconButton(onClick = onOpenHistory, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.History, contentDescription = "السجل", modifier = Modifier.size(18.dp), tint = NavyPrimary)
                        }

                        FilledTonalButton(
                            onClick = onOpenExplanation,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("💡 اشرح الحل", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                // Formula expression display (scrollable)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(exprScrollState, reverseScrolling = true)
                ) {
                    Text(
                        text = if (expression.isBlank()) "0" else expression,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 20.sp
                        ),
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }

                // Result display (supports Fractions and S⇄D conversion)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // S⇄D Button
                    if (calculationResult.fractionString != null) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFEFF6FF),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NavyPrimary.copy(alpha = 0.5f)),
                            onClick = onToggleFractionFormat,
                            modifier = Modifier.height(26.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 8.dp)) {
                                Text(text = "S⇄D (كسر/عشري)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.width(10.dp))
                    }

                    // Displayed Result or Error
                    if (calculationResult.isError) {
                        Text(
                            text = calculationResult.errorMessage ?: "خطأ",
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                        )
                    } else {
                        val displayedResult = if (calculationResult.fractionString != null && showFractionFormat) {
                            calculationResult.fractionString
                        } else {
                            calculationResult.decimalString
                        }

                        // Check if we can display as visual fraction
                        if (showFractionFormat && displayedResult.contains("/")) {
                            val parts = displayedResult.split("/")
                            if (parts.size == 2) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = parts[0].trim(), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF15803D))
                                    HorizontalDivider(modifier = Modifier.width(28.dp), thickness = 2.dp, color = Color(0xFF16A34A))
                                    Text(text = parts[1].trim(), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF15803D))
                                }
                            } else {
                                Text(
                                    text = "= $displayedResult",
                                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF15803D), fontSize = 24.sp)
                                )
                            }
                        } else {
                            Text(
                                text = "= ${displayedResult ?: calculationResult.decimalString}",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF15803D),
                                    fontSize = 26.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // Memory & Functions Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            MemoryButton(text = "MC") { onMemoryChange(0.0) }
            MemoryButton(text = "MR") {
                if (memoryValue != 0.0) onExpressionChange(expression + memoryValue.toString())
            }
            MemoryButton(text = "M+") {
                onMemoryChange(memoryValue + calculationResult.decimalValue)
            }
            MemoryButton(text = "M−") {
                onMemoryChange(memoryValue - calculationResult.decimalValue)
            }
            MemoryButton(text = "MS") {
                onMemoryChange(calculationResult.decimalValue)
            }
            CalcKey(text = "DEL", bgColor = Color(0xFFFEE2E2), textColor = Color(0xFFB91C1C), modifier = Modifier.weight(1.2f)) {
                if (expression.isNotEmpty()) onExpressionChange(expression.dropLast(1))
            }
            CalcKey(text = "AC", bgColor = Color(0xFFDC2626), textColor = Color.White, modifier = Modifier.weight(1.2f)) {
                onExpressionChange("")
            }
        }

        // Scientific & Power/Trig Rows (Scrollable horizontal or 2 compact rows)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ScientificFuncKey(text = "sin", modifier = Modifier.weight(1f)) { onExpressionChange("${expression}sin(") }
            ScientificFuncKey(text = "cos", modifier = Modifier.weight(1f)) { onExpressionChange("${expression}cos(") }
            ScientificFuncKey(text = "tan", modifier = Modifier.weight(1f)) { onExpressionChange("${expression}tan(") }
            ScientificFuncKey(text = "sin⁻¹", modifier = Modifier.weight(1f)) { onExpressionChange("${expression}sin⁻¹(") }
            ScientificFuncKey(text = "cos⁻¹", modifier = Modifier.weight(1f)) { onExpressionChange("${expression}cos⁻¹(") }
            ScientificFuncKey(text = "tan⁻¹", modifier = Modifier.weight(1f)) { onExpressionChange("${expression}tan⁻¹(") }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ScientificFuncKey(text = "x²", modifier = Modifier.weight(1f)) { onExpressionChange("${expression}²") }
            ScientificFuncKey(text = "x³", modifier = Modifier.weight(1f)) { onExpressionChange("${expression}³") }
            ScientificFuncKey(text = "xʸ", modifier = Modifier.weight(1f)) { onExpressionChange("$expression^") }
            ScientificFuncKey(text = "√x", modifier = Modifier.weight(1f)) { onExpressionChange("${expression}√(") }
            ScientificFuncKey(text = "∛x", modifier = Modifier.weight(1f)) { onExpressionChange("${expression}∛(") }
            ScientificFuncKey(text = "x!", modifier = Modifier.weight(1f)) { onExpressionChange("${expression}!") }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ScientificFuncKey(text = "log", modifier = Modifier.weight(1f)) { onExpressionChange("${expression}log(") }
            ScientificFuncKey(text = "ln", modifier = Modifier.weight(1f)) { onExpressionChange("${expression}ln(") }
            ScientificFuncKey(text = "10ˣ", modifier = Modifier.weight(1f)) { onExpressionChange("${expression}10^") }
            ScientificFuncKey(text = "eˣ", modifier = Modifier.weight(1f)) { onExpressionChange("${expression}e^") }
            ScientificFuncKey(text = "π", modifier = Modifier.weight(1f)) { onExpressionChange("${expression}π") }
            ScientificFuncKey(text = "e", modifier = Modifier.weight(1f)) { onExpressionChange("${expression}e") }
        }

        // Standard Keypad (5 rows)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            CalcKey(text = "(", modifier = Modifier.weight(1f)) { onExpressionChange("$expression(") }
            CalcKey(text = ")", modifier = Modifier.weight(1f)) { onExpressionChange("$expression)") }
            CalcKey(text = "%", modifier = Modifier.weight(1f)) { onExpressionChange("$expression%") }
            CalcKey(text = "±", modifier = Modifier.weight(1f)) {
                onExpressionChange(if (expression.startsWith("-")) expression.removePrefix("-") else "-$expression")
            }
            CalcKey(text = "÷", bgColor = Color(0xFFE0F2FE), textColor = NavyPrimary, modifier = Modifier.weight(1.2f)) {
                onExpressionChange("$expression ÷ ")
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            NumKey(text = "7", modifier = Modifier.weight(1f)) { onExpressionChange(expression + "7") }
            NumKey(text = "8", modifier = Modifier.weight(1f)) { onExpressionChange(expression + "8") }
            NumKey(text = "9", modifier = Modifier.weight(1f)) { onExpressionChange(expression + "9") }
            CalcKey(text = "1/x", modifier = Modifier.weight(1f)) { onExpressionChange("1/($expression)") }
            CalcKey(text = "×", bgColor = Color(0xFFE0F2FE), textColor = NavyPrimary, modifier = Modifier.weight(1.2f)) {
                onExpressionChange("$expression × ")
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            NumKey(text = "4", modifier = Modifier.weight(1f)) { onExpressionChange(expression + "4") }
            NumKey(text = "5", modifier = Modifier.weight(1f)) { onExpressionChange(expression + "5") }
            NumKey(text = "6", modifier = Modifier.weight(1f)) { onExpressionChange(expression + "6") }
            CalcKey(
                text = "⅟",
                bgColor = Color(0xFFFEF3C7),
                textColor = Color(0xFFB45309),
                modifier = Modifier.weight(1f)
            ) {
                onOpenFractionBuilder()
            }
            CalcKey(text = "−", bgColor = Color(0xFFE0F2FE), textColor = NavyPrimary, modifier = Modifier.weight(1.2f)) {
                onExpressionChange("$expression − ")
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            NumKey(text = "1", modifier = Modifier.weight(1f)) { onExpressionChange(expression + "1") }
            NumKey(text = "2", modifier = Modifier.weight(1f)) { onExpressionChange(expression + "2") }
            NumKey(text = "3", modifier = Modifier.weight(1f)) { onExpressionChange(expression + "3") }
            CalcKey(
                text = "S⇄D",
                bgColor = Color(0xFFEFF6FF),
                textColor = NavyPrimary,
                modifier = Modifier.weight(1f)
            ) {
                onToggleFractionFormat()
            }
            CalcKey(text = "+", bgColor = Color(0xFFE0F2FE), textColor = NavyPrimary, modifier = Modifier.weight(1.2f)) {
                onExpressionChange("$expression + ")
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            NumKey(text = "0", modifier = Modifier.weight(2f)) { onExpressionChange(expression + "0") }
            NumKey(text = ".", modifier = Modifier.weight(1f)) { onExpressionChange(expression + ".") }
            CalcKey(
                text = "=",
                bgColor = Color(0xFF15803D),
                textColor = Color.White,
                modifier = Modifier.weight(2.2f)
            ) {
                onEvaluate()
            }
        }
    }
}

@Composable
private fun RowScope.MemoryButton(text: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        modifier = Modifier
            .weight(1f)
            .height(30.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = text, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ScientificFuncKey(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFF1F5F9),
        modifier = modifier.height(36.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = NavyPrimary
            )
        }
    }
}

@Composable
private fun NumKey(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        modifier = modifier.height(46.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun CalcKey(
    text: String,
    bgColor: Color = Color(0xFFF8FAFC),
    textColor: Color = NavyPrimary,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = bgColor,
        modifier = modifier.height(46.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}
