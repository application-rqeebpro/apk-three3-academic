package com.example.ui.screens.solver

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.data.remote.AttachedFile
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
import com.example.data.solver.MathFormatter
import com.example.data.solver.ProblemVerification
import com.example.data.solver.toFormattedEducationalText
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
    val attachedFile by viewModel.solverAttachedFile.collectAsState()
    val solutionResult by viewModel.solverResultText.collectAsState()
    val structuredSolution by viewModel.solverStructuredResult.collectAsState()
    val isSolving by viewModel.isSolving.collectAsState()
    val isVerifying by viewModel.isVerifyingSolution.collectAsState()
    val verificationReport by viewModel.verificationReport.collectAsState()

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showVerifyDialog by remember { mutableStateOf(false) }
    var expandedExplanations by remember(structuredSolution) { mutableStateOf(setOf<Int>()) }

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
                viewModel.toastMessage.value = "تم إرفاق صورة المسألة بنجاح 📷"
            } catch (e: Exception) {
                viewModel.toastMessage.value = "تعذر تحميل الصورة"
            }
        }
    }

    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val contentResolver = context.contentResolver
                val mimeType = contentResolver.getType(uri) ?: "application/pdf"
                val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() }
                if (bytes != null) {
                    if (mimeType.startsWith("text/") || uri.toString().endsWith(".txt")) {
                        val textContent = String(bytes, Charsets.UTF_8)
                        if (viewModel.solverQuestionInput.value.isBlank()) {
                            viewModel.solverQuestionInput.value = textContent
                        } else {
                            viewModel.solverQuestionInput.value += "\n$textContent"
                        }
                        viewModel.toastMessage.value = "تم استخراج وقراءة نص المسألة من الملف بنجاح 📄"
                    } else if (mimeType.startsWith("image/")) {
                        val bmp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
                        } else {
                            @Suppress("DEPRECATION")
                            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                        }
                        viewModel.solverImageBitmap.value = bmp
                        viewModel.toastMessage.value = "تم تحميل صورة المسألة بنجاح 📷"
                    } else {
                        val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
                        val cursor = contentResolver.query(uri, null, null, null, null)
                        val name = cursor?.use {
                            if (it.moveToFirst()) {
                                val idx = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                                if (idx >= 0) it.getString(idx) else null
                            } else null
                        } ?: "ملف_المسألة.pdf"

                        viewModel.solverAttachedFile.value = AttachedFile(
                            fileName = name,
                            mimeType = mimeType,
                            base64Data = base64,
                            sizeBytes = bytes.size.toLong()
                        )
                        viewModel.toastMessage.value = "تم إرفاق الملف: $name بنجاح 📄"
                    }
                }
            } catch (e: Exception) {
                viewModel.toastMessage.value = "تعذر قراءة الملف: ${e.localizedMessage}"
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
                        text = "إرفاق صورة أو ملف المسألة (تحليل محتوى الملف والصورة بالذكاء الاصطناعي):",
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
                            Text(text = "صورة المسألة 📷", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { documentPickerLauncher.launch("*/*") },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Description, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "ملف PDF / نص 📄", fontSize = 12.sp)
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
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(imageVector = Icons.Default.Image, contentDescription = null, tint = TealAccent)
                                    Text(
                                        text = "تم إرفاق صورة المسألة بنجاح 📷",
                                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF134E4A), fontWeight = FontWeight.Bold)
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        viewModel.solverImageBitmap.value = null
                                        selectedImageUri = null
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "حذف الصورة", tint = ErrorRed)
                                }
                            }
                        }
                    }

                    if (attachedFile != null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFEFF6FF),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(imageVector = Icons.Default.Description, contentDescription = null, tint = NavyPrimary)
                                    Column {
                                        Text(
                                            text = attachedFile!!.fileName,
                                            style = MaterialTheme.typography.bodySmall.copy(color = NavyPrimary, fontWeight = FontWeight.Bold),
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "${attachedFile!!.sizeBytes / 1024} كيلوبايت • جاهز للتحليل بالذكاء الاصطناعي",
                                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF3B82F6), fontSize = 10.sp)
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = { viewModel.solverAttachedFile.value = null },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "حذف الملف", tint = ErrorRed)
                                }
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
                                    text = if (sol.confidence == "error") "تنبيه: تعذر إتمام الحل حالياً" else "تنبيه: الصورة غير واضحة لمنع التخمين الخاطئ",
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
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { viewModel.solveQuestion() },
                                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("إعادة المحاولة 🔄")
                                }

                                OutlinedButton(
                                    onClick = { photoPickerLauncher.launch("image/*") },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.PhotoCamera, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("صورة أوضح 📷")
                                }
                            }
                        }
                    }
                }
                // Case B: Clear Structured Solution in Yemeni Blackboard Format
                item {
                    val questionItems = sol.resolvedItems()
                    val isMultiple = questionItems.size > 1

                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(18.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("solution_result_card")
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(18.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Header bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFF1F5F9)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(text = "✍️", fontSize = 16.sp)
                                        Text(
                                            text = if (isMultiple) "إجابات الأسئلة (${questionItems.size} أسئلة)" else "نتيجة الحل (المنهج اليمني)",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = NavyPrimary
                                            )
                                        )
                                    }
                                }

                                FilledTonalButton(
                                    onClick = {
                                        val fullSolText = sol.toFormattedEducationalText()
                                        copyToClipboard(context, fullSolText, "الحل الكامل")
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier
                                        .height(34.dp)
                                        .testTag("copy_solution_button")
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "نسخ", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "نسخ الإجابة", fontSize = 12.sp)
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                            // Each question rendered independently
                            questionItems.forEachIndexed { index, item ->
                                val isExpanded = expandedExplanations.contains(index)
                                val qNumLabel = if (isMultiple) "السؤال ${index + 1}" else (item.questionTitle.takeIf { it.isNotBlank() && it != "السؤال" } ?: "")

                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                    shape = RoundedCornerShape(14.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("question_item_$index")
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(14.dp)
                                            .fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        if (qNumLabel.isNotBlank()) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = NavyPrimary
                                            ) {
                                                Text(
                                                    text = qNumLabel,
                                                    style = MaterialTheme.typography.labelMedium.copy(
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold
                                                    ),
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }

                                        if (item.questionText.isNotBlank()) {
                                            Text(
                                                text = item.questionText,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    lineHeight = 22.sp
                                                )
                                            )
                                        }

                                        // 1. Result summary based on question type
                                        when (item.questionType) {
                                            "MULTIPLE_CHOICE" -> {
                                                val optionLabel = MathFormatter.formatOptionLabel(item.selectedOptionLabel)
                                                val optionText = MathFormatter.cleanItem(item.selectedOptionText ?: item.finalAnswer)

                                                Surface(
                                                    shape = RoundedCornerShape(12.dp),
                                                    color = Color(0xFFF0FDF4),
                                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF16A34A)),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Column(
                                                        modifier = Modifier.padding(12.dp),
                                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                                    ) {
                                                        if (optionLabel.isNotBlank()) {
                                                            Text(
                                                                text = "الإجابة الصحيحة: $optionLabel",
                                                                style = MaterialTheme.typography.titleMedium.copy(
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = Color(0xFF15803D),
                                                                    fontSize = 16.sp
                                                                )
                                                            )
                                                        }
                                                        if (optionText.isNotBlank()) {
                                                            Text(
                                                                text = "الإجابة: $optionText",
                                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                                    fontWeight = FontWeight.SemiBold,
                                                                    color = Color(0xFF166534),
                                                                    fontSize = 15.sp
                                                                )
                                                            )
                                                        }
                                                    }
                                                }
                                            }

                                            "TRUE_FALSE" -> {
                                                val isCorrect = item.isTrue == true
                                                val isWrong = item.isTrue == false

                                                Surface(
                                                    shape = RoundedCornerShape(12.dp),
                                                    color = if (isCorrect) Color(0xFFF0FDF4) else Color(0xFFFEF2F2),
                                                    border = androidx.compose.foundation.BorderStroke(
                                                        1.5.dp,
                                                        if (isCorrect) Color(0xFF16A34A) else Color(0xFFDC2626)
                                                    ),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Column(
                                                        modifier = Modifier.padding(12.dp),
                                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                                    ) {
                                                        Text(
                                                            text = when {
                                                                isCorrect -> "الإجابة: صح ✅"
                                                                isWrong -> "الإجابة: خطأ ❌"
                                                                else -> "الإجابة: ${MathFormatter.cleanItem(item.finalAnswer)}"
                                                            },
                                                            style = MaterialTheme.typography.titleMedium.copy(
                                                                fontWeight = FontWeight.Bold,
                                                                color = if (isCorrect) Color(0xFF15803D) else Color(0xFFB91C1C),
                                                                fontSize = 16.sp
                                                            )
                                                        )
                                                        if (isWrong && !item.correction.isNullOrBlank()) {
                                                            Spacer(modifier = Modifier.height(2.dp))
                                                            Text(
                                                                text = "التصحيح: ${MathFormatter.cleanMathText(item.correction)}",
                                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                                    color = Color(0xFF991B1B),
                                                                    fontWeight = FontWeight.Medium,
                                                                    lineHeight = 22.sp
                                                                )
                                                            )
                                                        }
                                                    }
                                                }
                                            }

                                            else -> { // CALCULATION or default
                                                val cleanAns = MathFormatter.cleanItem(item.finalAnswer)
                                                Surface(
                                                    shape = RoundedCornerShape(12.dp),
                                                    color = Color(0xFFF0FDF4),
                                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF16A34A)),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Column(
                                                        modifier = Modifier.padding(12.dp),
                                                        verticalArrangement = Arrangement.spacedBy(2.dp)
                                                    ) {
                                                        Text(
                                                            text = "الإجابة النهائية: $cleanAns",
                                                            style = MaterialTheme.typography.titleMedium.copy(
                                                                fontWeight = FontWeight.Bold,
                                                                color = Color(0xFF15803D),
                                                                fontSize = 16.sp
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        // 2. Optional "اشرح الحل" button
                                        val cleanedGivens = MathFormatter.cleanItemList(item.givens)
                                        val cleanedLaws = MathFormatter.cleanItemList(item.laws)
                                        val cleanedSubst = MathFormatter.cleanItemList(item.substitutionSteps)
                                        val cleanedCalcs = MathFormatter.cleanItemList(item.calculationSteps)
                                        val hasSteps = cleanedGivens.isNotEmpty() || cleanedLaws.isNotEmpty() ||
                                                cleanedSubst.isNotEmpty() || cleanedCalcs.isNotEmpty()

                                        if (hasSteps) {
                                            OutlinedButton(
                                                onClick = {
                                                    expandedExplanations = if (isExpanded) {
                                                        expandedExplanations - index
                                                    } else {
                                                        expandedExplanations + index
                                                    }
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                                modifier = Modifier
                                                    .align(Alignment.Start)
                                                    .testTag("toggle_explain_button_$index")
                                            ) {
                                                Icon(
                                                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.Lightbulb,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp),
                                                    tint = AmberSecondary
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = if (isExpanded) "إخفاء الشرح ▲" else "اشرح الحل 💡",
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = NavyPrimary
                                                )
                                            }

                                            // 3. Step-by-Step Whiteboard Explanation
                                            AnimatedVisibility(visible = isExpanded) {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .background(Color.White, RoundedCornerShape(10.dp))
                                                        .padding(12.dp),
                                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                                ) {
                                                    if (cleanedGivens.isNotEmpty()) {
                                                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                                            Text(
                                                                text = "المعطيات:",
                                                                style = MaterialTheme.typography.labelLarge.copy(
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = NavyPrimary
                                                                )
                                                            )
                                                            cleanedGivens.forEach { g ->
                                                                Text(
                                                                    text = g,
                                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                                        color = MaterialTheme.colorScheme.onSurface,
                                                                        lineHeight = 22.sp
                                                                    )
                                                                )
                                                            }
                                                        }
                                                    }

                                                    if (cleanedLaws.isNotEmpty()) {
                                                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                                            Text(
                                                                text = "القانون:",
                                                                style = MaterialTheme.typography.labelLarge.copy(
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = NavyPrimary
                                                                )
                                                            )
                                                            cleanedLaws.forEach { l ->
                                                                Text(
                                                                    text = l,
                                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                                        color = Color(0xFF0F766E),
                                                                        fontWeight = FontWeight.SemiBold,
                                                                        lineHeight = 22.sp
                                                                    )
                                                                )
                                                            }
                                                        }
                                                    }

                                                    if (cleanedSubst.isNotEmpty()) {
                                                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                                            Text(
                                                                text = "التعويض:",
                                                                style = MaterialTheme.typography.labelLarge.copy(
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = NavyPrimary
                                                                )
                                                            )
                                                            cleanedSubst.forEach { s ->
                                                                Text(
                                                                    text = s,
                                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                                        color = MaterialTheme.colorScheme.onSurface,
                                                                        lineHeight = 22.sp
                                                                    )
                                                                )
                                                            }
                                                        }
                                                    }

                                                    if (cleanedCalcs.isNotEmpty()) {
                                                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                                            Text(
                                                                text = "الحل:",
                                                                style = MaterialTheme.typography.labelLarge.copy(
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = NavyPrimary
                                                                )
                                                            )
                                                            cleanedCalcs.forEach { c ->
                                                                Text(
                                                                    text = c,
                                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                                        color = MaterialTheme.colorScheme.onSurface,
                                                                        lineHeight = 22.sp
                                                                    )
                                                                )
                                                            }
                                                        }
                                                    }

                                                    if (item.finalAnswer.isNotBlank()) {
                                                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                                            Text(
                                                                text = "الإجابة:",
                                                                style = MaterialTheme.typography.labelLarge.copy(
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = Color(0xFF15803D)
                                                                )
                                                            )
                                                            Text(
                                                                text = MathFormatter.cleanItem(item.finalAnswer),
                                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = Color(0xFF166534)
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

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                            // Action Buttons: «تحقق من الحل» and «اشرح لي بطريقة أسهل»
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.verifyCurrentSolution()
                                        showVerifyDialog = true
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("verify_solution_button")
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
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("easier_explanation_button")
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
