package com.example.ui.screens.solver

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.solver.EducationalSolution
import com.example.data.solver.ProblemVerification
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.components.copyToClipboard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolveMyQuestionScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val questionInput by viewModel.solverQuestionInput.collectAsState()
    val selectedSubject by viewModel.solverSelectedSubject.collectAsState()
    val imageBitmap by viewModel.solverImageBitmap.collectAsState()
    val solutionResult by viewModel.solverResultText.collectAsState()
    val structuredSolution by viewModel.solverStructuredResult.collectAsState()
    val isSolving by viewModel.isSolving.collectAsState()
    val isVerifying by viewModel.isVerifyingSolution.collectAsState()
    val verificationReport by viewModel.verificationReport.collectAsState()

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showVerifyDialog by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            try {
                val bmp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
                viewModel.solverImageBitmap.value = bmp
            } catch (e: Exception) {
                viewModel.toastMessage.value = "تعذر تحميل الصورة"
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("solver_screen_content"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🛡️", fontSize = 24.sp)
                    }

                    Column {
                        Text(
                            text = "نظام حل المسائل التعليمي الدقيق",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                        Text(
                            text = "حل منظم وفق الخطوات الوزارية المعتمدة: قراءة دقيقة، استخراج المعطيات، القانون الوزاري، تدقيق العمليات، وفحص الوحدات.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                        )
                    }
                }
            }
        }

        // Subject Selector Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "حدد مادة السؤال:",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                val subjects = listOf("الرياضيات", "الفيزياء", "الكيمياء", "الأحياء", "اللغة العربية", "اللغة الإنجليزية")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(subjects) { subj ->
                        val isSelected = selectedSubject == subj
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.solverSelectedSubject.value = subj },
                            label = { Text(text = subj, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }
            }
        }

        // Question Input Field
        item {
            OutlinedTextField(
                value = questionInput,
                onValueChange = { viewModel.solverQuestionInput.value = it },
                label = { Text("اكتب نص المسألة أو السؤال هنا...") },
                placeholder = { Text("مثال: احسب ممانعة دائرة تيار متردد فيها R=30 و XL=80 و XC=40...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 110.dp)
                    .testTag("solver_question_input"),
                shape = RoundedCornerShape(14.dp)
            )
        }

        // Math Equation Symbols Quick Bar
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "رموز سريعة للمسائل والمعادلات الرياضية والعلمية:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    val symbols = listOf("²", "³", "√", "π", "θ", "Δ", "±", "×", "÷", "λ", "Ω", "∞", "∫", "جا", "جتا", "ظا", "ت")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(symbols) { sym ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier
                                    .clickable {
                                        viewModel.solverQuestionInput.value += sym
                                    }
                                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            ) {
                                Text(
                                    text = sym,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Image / Camera Upload Actions
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "إرفاق صورة السؤال من الكتاب أو ورقة الامتحان (تحليل دقيق دون تخمين):",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { photoPickerLauncher.launch("image/*") },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "تصوير / رفع صورة المسألة", fontSize = 12.sp)
                        }

                        if (imageBitmap != null) {
                            Button(
                                onClick = {
                                    viewModel.solverImageBitmap.value = null
                                    selectedImageUri = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(text = "حذف الصورة 🗑️", color = ErrorRed, fontSize = 12.sp)
                            }
                        }
                    }

                    if (imageBitmap != null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = CardTeal,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = TealAccent)
                                Text(
                                    text = "تم إرفاق صورة المسألة بنجاح وهي جاهزة للتحليل الدقيق",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF134E4A), fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Action Buttons: Solve and Clear
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { viewModel.solveQuestion() },
                    enabled = !isSolving,
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("solve_question_button")
                ) {
                    if (isSolving) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "جاري قراءة وتدقيق وحل المسألة...", color = Color.White)
                    } else {
                        Icon(imageVector = Icons.Default.Calculate, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "حل المسألة بالخطوات الوزارية 🚀", fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedButton(
                    onClick = { viewModel.clearSolver() },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.height(52.dp)
                ) {
                    Text(text = "مسح")
                }
            }
        }

        // Solution Display
        if (structuredSolution != null) {
            val sol = structuredSolution!!

            // Case A: Unclear Image Warning
            if (!sol.isImageClear) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, ErrorRed),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = ErrorRed)
                                Text(
                                    text = "تنبيه: الصورة غير واضحة لمنع التخمين الخاطئ",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = ErrorRed,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Text(
                                text = sol.unclearReason ?: "الجزء الخاص بالسؤال غير واضح في الصورة، أرسل صورة أوضح حتى أحل السؤال بدقة وبشكل صحيح.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFF7F1D1D),
                                    lineHeight = 22.sp
                                )
                            )
                            Button(
                                onClick = { photoPickerLauncher.launch("image/*") },
                                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.PhotoCamera, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("إعادة التقاط أو اختيار صورة أوضح")
                            }
                        }
                    }
                }
            } else {
                // Case B: Fully Clear Structured Solution
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(18.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("solution_result_card")
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(18.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Top Bar of Card
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFECFDF5)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(text = "🛡️", fontSize = 16.sp)
                                        Text(
                                            text = "الحل الوزاري المعتمد (مُدقق 100%)",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = TealAccent
                                            )
                                        )
                                    }
                                }

                                IconButton(onClick = {
                                    copyToClipboard(context, solutionResult ?: "", "الحل الكامل")
                                }) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "نسخ", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            // 1. Understanding
                            if (sol.questionUnderstanding.isNotBlank()) {
                                SolutionSection(title = "📘 قراءة وفهم المسألة:", content = sol.questionUnderstanding)
                            }

                            // 2. Givens
                            if (sol.givens.isNotEmpty()) {
                                Text(
                                    text = "📋 المعطيات:",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
                                )
                                sol.givens.forEach { g ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFEFF6FF),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "• $g",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = Color(0xFF1E3A8A)),
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }

                            // 3. Required
                            if (sol.required.isNotBlank()) {
                                SolutionSection(title = "🎯 المطلوب:", content = sol.required)
                            }

                            // 4. Laws
                            if (sol.laws.isNotEmpty()) {
                                Text(
                                    text = "📜 القانون المعتمد في المنهج اليمني:",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
                                )
                                sol.laws.forEach { law ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFFEF3C7),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = law,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF78350F)),
                                            modifier = Modifier.padding(10.dp)
                                        )
                                    }
                                }
                            }

                            // 5. Substitution
                            if (sol.substitutionSteps.isNotEmpty()) {
                                Text(
                                    text = "✍️ التعويض بالأرقام:",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
                                )
                                sol.substitutionSteps.forEach { step ->
                                    Text(text = "• $step", style = MaterialTheme.typography.bodyMedium)
                                }
                            }

                            // 6. Calculation Steps
                            if (sol.calculationSteps.isNotEmpty()) {
                                Text(
                                    text = "🔢 الحساب والتبسيط خطوة بخطوة:",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
                                )
                                sol.calculationSteps.forEach { calc ->
                                    Text(
                                        text = calc,
                                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp)
                                    )
                                }
                            }

                            // 7. Unit Check
                            if (!sol.unitCheck.isNullOrBlank()) {
                                SolutionSection(title = "📏 فحص وتوحيد الوحدات:", content = sol.unitCheck)
                            }

                            // 8. Final Answer Box
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = CardTeal,
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, TealAccent),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "🏆 الإجابة النهائية:",
                                        style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFF0F766E), fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = sol.finalAnswer,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFF115E59),
                                            fontSize = 16.sp
                                        )
                                    )
                                    if (!sol.multipleChoiceAnswer.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color.White
                                        ) {
                                            Text(
                                                text = "الخيار الصحيح في السؤال: ${sol.multipleChoiceAnswer}",
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF0F766E)
                                                ),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // 9. Verification & Safety Shield
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF0FDF4),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(text = "🛡️", fontSize = 16.sp)
                                        Text(
                                            text = "مرحلة التدقيق المستقل (VERIFY_SOLUTION):",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = SuccessGreen)
                                        )
                                    }
                                    Text(
                                        text = sol.verification.verificationDetails,
                                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF166534))
                                    )
                                    sol.verification.checksList.forEach { c ->
                                        Text(text = "  $c", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF14532D)))
                                    }
                                    if (!sol.verification.commonMistakesAvoided.isNullOrBlank()) {
                                        Text(
                                            text = "⚠️ تجنب الأخطاء الشائعة: ${sol.verification.commonMistakesAvoided}",
                                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF854D0E), fontWeight = FontWeight.SemiBold)
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                            // Mandatory Buttons Requested by User:
                            // 1. «تحقق من الحل»
                            // 2. «اشرح لي بطريقة أسهل»
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.verifyCurrentSolution()
                                        showVerifyDialog = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.FactCheck, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "تحقق من الحل ✓", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        viewModel.requestEasierExplanationForCurrentProblem()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "اشرح لي بطريقة أسهل 💡", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog for «تحقق من الحل»
    if (showVerifyDialog && verificationReport != null) {
        val rep = verificationReport!!
        AlertDialog(
            onDismissRequest = { showVerifyDialog = false },
            icon = {
                Text(text = if (rep.isValid) "✅" else "⚠️", fontSize = 28.sp)
            },
            title = {
                Text(
                    text = if (rep.isValid) "✓ الحل صحيح ومُدقق بالكامل" else "تقرير تدقيق الحل",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = rep.verificationDetails, style = MaterialTheme.typography.bodyMedium)
                    HorizontalDivider()
                    Text(text = "قائمة الفحوصات المنفذة:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    rep.checksList.forEach { ch ->
                        Text(text = ch, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (!rep.alternativeCheck.isNullOrBlank()) {
                        Text(text = "• طريقة ثانية للتحقق: ${rep.alternativeCheck}", fontSize = 12.sp, color = NavyPrimary)
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showVerifyDialog = false }) {
                    Text("تم والحمد لله")
                }
            }
        )
    }
}

@Composable
private fun SolutionSection(title: String, content: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp, color = MaterialTheme.colorScheme.onSurface)
        )
    }
}
