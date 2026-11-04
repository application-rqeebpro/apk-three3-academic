package com.example.ui.screens.solver

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Base64
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.os.Bundle
import androidx.core.content.ContextCompat
import java.io.File
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
    val solverElapsedSeconds by viewModel.solverElapsedSeconds.collectAsState()

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showVerifyDialog by remember { mutableStateOf(false) }
    var expandedExplanations by remember(structuredSolution) { mutableStateOf(setOf<Int>()) }
    var selectedFilterType by remember(structuredSolution) { mutableStateOf("ALL") }

    var currentInputMode by remember { mutableStateOf("IMAGE") } // IMAGE, TEXT, VIDEO, PDF, AUDIO
    var isRecordingAudio by remember { mutableStateOf(false) }
    var audioRecorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var audioRecordFile by remember { mutableStateOf<File?>(null) }

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

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val contentResolver = context.contentResolver
                val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() }
                if (bytes != null) {
                    val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
                    val cursor = contentResolver.query(uri, null, null, null, null)
                    val name = cursor?.use {
                        if (it.moveToFirst()) {
                            val idx = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                            if (idx >= 0) it.getString(idx) else null
                        } else null
                    } ?: "فيديو_المسألة.mp4"

                    viewModel.solverAttachedFile.value = AttachedFile(
                        fileName = name,
                        mimeType = contentResolver.getType(uri) ?: "video/mp4",
                        base64Data = base64,
                        sizeBytes = bytes.size.toLong()
                    )
                    viewModel.toastMessage.value = "تم إرفاق فيديو المسألة: $name 🎥"
                }
            } catch (e: Exception) {
                viewModel.toastMessage.value = "تعذر قراءة ملف الفيديو: ${e.localizedMessage}"
            }
        }
    }

    val pdfPickerLauncher = rememberLauncherForActivityResult(
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
                        viewModel.toastMessage.value = "تم إرفاق ملف PDF بنجاح: $name 📑"
                    }
                }
            } catch (e: Exception) {
                viewModel.toastMessage.value = "تعذر قراءة ملف الـ PDF: ${e.localizedMessage}"
            }
        }
    }

    val speechInputLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
        if (!spokenText.isNullOrBlank()) {
            if (viewModel.solverQuestionInput.value.isBlank()) {
                viewModel.solverQuestionInput.value = spokenText
            } else {
                viewModel.solverQuestionInput.value += "\n$spokenText"
            }
            viewModel.toastMessage.value = "تم تحويل الصوت إلى نص السؤال بدقة 🎙️"
        }
    }

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Launch speech recognizer intent
            try {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-YE")
                    putExtra(RecognizerIntent.EXTRA_PROMPT, "تحدث بسؤالك أو مسألتك الآن بوضوح...")
                }
                speechInputLauncher.launch(intent)
            } catch (e: Exception) {
                viewModel.toastMessage.value = "ميزة التعرف الصوتي غير متوفرة على الجهاز مباشرة"
            }
        } else {
            viewModel.toastMessage.value = "يرجى منح إذن الميكروفون للتسجيل الصوتي"
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

        // -------------------------------------------------------------
        // Dedicated Input Modality Cards (5 Cards: Image, Text, Video, PDF, Voice)
        // -------------------------------------------------------------
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "اختر طريقة إدخال ورقة الأسئلة / المسألة:",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF1F5F9)
                        ) {
                            Text(
                                text = "حتى 30 سؤالاً ⚡",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // 5 Input Mode Selection Cards (Horizontal Bar)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val inputOptions = listOf(
                            Triple("IMAGE", "رفع صورة 📷", Icons.Default.PhotoCamera),
                            Triple("TEXT", "كتابة نص ✍️", Icons.Default.Edit),
                            Triple("VIDEO", "رفع فيديو 🎥", Icons.Default.Videocam),
                            Triple("PDF", "ملف PDF 📄", Icons.Default.PictureAsPdf),
                            Triple("AUDIO", "تسجيل صوتي 🎙️", Icons.Default.Mic)
                        )

                        inputOptions.forEach { (mode, label, icon) ->
                            val isSelected = currentInputMode == mode
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) NavyPrimary else Color(0xFFF8FAFC),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) NavyPrimary else Color(0xFFE2E8F0)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { currentInputMode = mode }
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = label,
                                        modifier = Modifier.size(20.dp),
                                        tint = if (isSelected) Color.White else Color(0xFF475569)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else Color(0xFF334155),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                    // Dynamic Active Card Body based on currentInputMode
                    when (currentInputMode) {
                        // 1. CARD: IMAGE UPLOAD
                        "IMAGE" -> {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = "📸 بطاقة رفع صورة الأسئلة (يدعم حتى 30 سؤالاً في الورقة):",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
                                )
                                Text(
                                    text = "التقط صورة واضحة لورقة الامتحان أو اخترها من الاستوديو. سيقوم المحرك بحل جميع الأسئلة بالكامل (صح/خطأ، اختيارات، ومسائل) مع المعطيات.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 18.sp
                                )
                                Button(
                                    onClick = { photoPickerLauncher.launch("image/*") },
                                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("اختيار أو تصوير ورقة الأسئلة 📷", fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // 2. CARD: TEXT INPUT
                        "TEXT" -> {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = "✍️ بطاقة كتابة نص الأسئلة أو المسألة:",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
                                )
                                OutlinedTextField(
                                    value = questionInput,
                                    onValueChange = { viewModel.solverQuestionInput.value = it },
                                    label = { Text("اكتب الأسئلة أو المسائل هنا...") },
                                    placeholder = { Text("يمكنك كتابة حتى 30 سؤالاً مرقمة (1، 2، 3 ...) مع خياراتها وسيتم حلها جميعاً.") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 120.dp)
                                        .testTag("solver_question_input"),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                // Math Equation Quick Symbols
                                Text(
                                    text = "رموز سريعة للمسائل والمعادلات:",
                                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                                val symbols = listOf("²", "³", "√", "π", "θ", "Δ", "±", "×", "÷", "λ", "Ω", "∞", "∫", "جا", "جتا", "ظا", "ت")
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items(symbols) { sym ->
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFFF1F5F9),
                                            modifier = Modifier
                                                .clickable { viewModel.solverQuestionInput.value += sym }
                                                .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                                        ) {
                                            Text(
                                                text = sym,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = NavyPrimary
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 3. CARD: VIDEO UPLOAD
                        "VIDEO" -> {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = "🎥 بطاقة رفع فيديو للمسألة أو ورقة الأسئلة:",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
                                )
                                Text(
                                    text = "قم برفع مقطع فيديو قصير يمر على ورقة الأسئلة أو مسألة يحللها المحرك الذكي صوتياً وبصرياً بدقة.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 18.sp
                                )
                                Button(
                                    onClick = { videoPickerLauncher.launch("video/*") },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.VideoFile, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("اختيار فيديو المسألة 🎥", fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // 4. CARD: PDF UPLOAD
                        "PDF" -> {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = "📄 بطاقة رفع ملف PDF أو مستند اختبارات:",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
                                )
                                Text(
                                    text = "ارفع ملف PDF لنماذج اختبارات أو ملازم أو أوراق وزارية (حتى 30 سؤالاً) وسيقوم النظام باستخراج الأسئلة وحلها بالكامل.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 18.sp
                                )
                                Button(
                                    onClick = { pdfPickerLauncher.launch("application/pdf") },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC026D3)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("اختيار ملف PDF 📑", fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // 5. CARD: VOICE RECORDING
                        "AUDIO" -> {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = "🎙️ بطاقة التسجيل الصوتي أو إلقاء السؤال شفهياً:",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
                                )
                                Text(
                                    text = "انطق السؤال أو نص المسألة بصوتك. سيقوم النظام بتحويله فوراً إلى نص وإرساله للحل الدقيق واستخراج المعطيات.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 18.sp
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                                                try {
                                                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-YE")
                                                        putExtra(RecognizerIntent.EXTRA_PROMPT, "انطق المسألة أو السؤال الآن...")
                                                    }
                                                    speechInputLauncher.launch(intent)
                                                } catch (e: Exception) {
                                                    viewModel.toastMessage.value = "تعذر تشغيل التسجيل: ${e.localizedMessage}"
                                                }
                                            } else {
                                                audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.Mic, contentDescription = null)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("تحدث بالسؤال الآن 🎙️", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            // Quick prompt voice sample
                                            viewModel.solverQuestionInput.value = "احسب القوة الدافعة الكهربائية الحثية المتولدة في ملف عدد لفاته 500 لفة إذا تغير الفيض المغناطيسي بمعدل 0.04 ويبر لكل ثانية."
                                            viewModel.toastMessage.value = "تم إدراج نموذج مسألة صوتية تجريبية 🎙️"
                                        },
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("نموذج مسألة", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }

                    // Previews of Attached Content
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
                                        text = "تم إرفاق صورة ورقة الأسئلة بنجاح 📷 (جاهزة للحل)",
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
                        val fileIcon = when {
                            attachedFile!!.mimeType.contains("video") -> Icons.Default.Videocam
                            attachedFile!!.mimeType.contains("pdf") -> Icons.Default.PictureAsPdf
                            else -> Icons.Default.Description
                        }
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
                                    Icon(imageVector = fileIcon, contentDescription = null, tint = NavyPrimary)
                                    Column {
                                        Text(
                                            text = attachedFile!!.fileName,
                                            style = MaterialTheme.typography.bodySmall.copy(color = NavyPrimary, fontWeight = FontWeight.Bold),
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "${attachedFile!!.sizeBytes / 1024} كيلوبايت • جاهز للتحليل وحل جميع الأسئلة بالكامل",
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
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "جاري الحل والتدقيق (1-2 دقيقة) ⏱️", color = Color.White, fontSize = 13.sp)
                    } else {
                        Icon(imageVector = Icons.Default.Timer, contentDescription = null, tint = AmberSecondary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "حل المسألة بالخطوات الوزارية (1-2 دقيقة) ⏱️", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
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

        // Live Timer Card during Solving
        if (isSolving) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF86EFAC))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val minutes = solverElapsedSeconds / 60
                        val seconds = solverElapsedSeconds % 60
                        val timeFormatted = String.format("%02d:%02d", minutes, seconds)
                        val estimatedMax = 90f // 1.5 min target
                        val progress = (solverElapsedSeconds.toFloat() / estimatedMax).coerceIn(0.05f, 0.95f)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier.size(36.dp),
                                    color = Color(0xFF16A34A),
                                    strokeWidth = 3.5.dp
                                )
                                Column {
                                    Text(
                                        text = "جاري استخراج وتدقيق الحل الوزاري...",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp,
                                        color = Color(0xFF166534)
                                    )
                                    Text(
                                        text = "يستغرق عادة من دقيقة إلى دقيقتين ⏱️",
                                        fontSize = 11.sp,
                                        color = Color(0xFF15803D)
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFDCFCE7)
                            ) {
                                Text(
                                    text = "$timeFormatted د",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    color = Color(0xFF166534),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFF16A34A),
                            trackColor = Color(0xFFBBF7D0)
                        )

                        val currentStage = when {
                            solverElapsedSeconds < 15 -> "📖 قراءة وفهم مسألة $selectedSubject وتحديد المعطيات والمطلوب..."
                            solverElapsedSeconds < 35 -> "🇾🇪 استدعاء القوانين الوزارية المعتمدة لمنهاج ثالث ثانوي باليمن..."
                            solverElapsedSeconds < 60 -> "📐 تنفيذ التعويض الرياضي والحسابات خطوة بخطوة وإزالة الرموز المشوشة (*$&#)..."
                            solverElapsedSeconds < 85 -> "🔍 التدقيق المستقل للوحدات والإشارات لضمان الدرجة الكاملة..."
                            else -> "✨ إعداد التفسير المبسط واللمسات الإيضاحية للحل..."
                        }

                        Text(
                            text = currentStage,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF166534),
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.5.sp
                            )
                        )
                    }
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
            } else {
                // Case B: Clear Structured Solution in Yemeni Blackboard Format
                item {
                    val allItems = sol.resolvedItems()
                    val totalCount = allItems.size
                    val isMultiple = totalCount > 1

                    val mcCount = sol.countMultipleChoice()
                    val tfCount = sol.countTrueFalse()
                    val calcCount = sol.countCalculations()

                    val displayedItems = when (selectedFilterType) {
                        "MCQ" -> allItems.filter { it.questionType == "MULTIPLE_CHOICE" }
                        "TF" -> allItems.filter { it.questionType == "TRUE_FALSE" }
                        "CALC" -> allItems.filter { it.questionType != "MULTIPLE_CHOICE" && it.questionType != "TRUE_FALSE" }
                        else -> allItems
                    }

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
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Header bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFF1F5F9)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(text = "✍️", fontSize = 16.sp)
                                        Text(
                                            text = if (isMultiple) "إجابات الأسئلة ($totalCount سؤالاً بالمعطيات)" else "نتيجة الحل (المنهج الوزاري)",
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
                                        copyToClipboard(context, fullSolText, "الحل الكامل لجميع الأسئلة")
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier
                                        .height(34.dp)
                                        .testTag("copy_solution_button")
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "نسخ", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "نسخ كل الإجابات", fontSize = 12.sp)
                                }
                            }

                            // Multiple Questions Summary & Quick Controls
                            if (isMultiple) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFF0FDF4),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(20.dp))
                                        Text(
                                            text = "تم حل جميع الأسئلة الـ ($totalCount) بنجاح مع استخراج المعطيات وتحديد الإجابات الدقيقة.",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Color(0xFF166534),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                        )
                                    }
                                }

                                // Filter Chips Bar
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    FilterChip(
                                        selected = selectedFilterType == "ALL",
                                        onClick = { selectedFilterType = "ALL" },
                                        label = { Text("الكل ($totalCount)", fontSize = 11.sp) }
                                    )
                                    if (tfCount > 0) {
                                        FilterChip(
                                            selected = selectedFilterType == "TF",
                                            onClick = { selectedFilterType = "TF" },
                                            label = { Text("صح وخطأ ($tfCount)", fontSize = 11.sp) }
                                        )
                                    }
                                    if (mcCount > 0) {
                                        FilterChip(
                                            selected = selectedFilterType == "MCQ",
                                            onClick = { selectedFilterType = "MCQ" },
                                            label = { Text("اختيارات ($mcCount)", fontSize = 11.sp) }
                                        )
                                    }
                                    if (calcCount > 0) {
                                        FilterChip(
                                            selected = selectedFilterType == "CALC",
                                            onClick = { selectedFilterType = "CALC" },
                                            label = { Text("مسائل ($calcCount)", fontSize = 11.sp) }
                                        )
                                    }
                                }

                                // Quick Controls: Expand/Collapse All
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "عرض ${displayedItems.size} من أصل $totalCount سؤال:",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )

                                    TextButton(
                                        onClick = {
                                            expandedExplanations = if (expandedExplanations.size == allItems.size) {
                                                emptySet()
                                            } else {
                                                allItems.indices.toSet()
                                            }
                                        },
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (expandedExplanations.size == allItems.size) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (expandedExplanations.size == allItems.size) "طي الشروحات" else "توسيع كل الشروحات",
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                            // Each question rendered independently
                            displayedItems.forEach { item ->
                                val actualIndex = item.questionNumber - 1
                                val isExpanded = expandedExplanations.contains(actualIndex)
                                val qNumLabel = if (isMultiple) "السؤال ${item.questionNumber} من $totalCount" else (item.questionTitle.takeIf { it.isNotBlank() && it != "السؤال" } ?: "السؤال 1")

                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                    shape = RoundedCornerShape(14.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("question_item_${item.questionNumber}")
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(14.dp)
                                            .fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        // Header Row: Question Number and Type Badge
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
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

                                            // Type Badge
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = when (item.questionType) {
                                                    "MULTIPLE_CHOICE" -> Color(0xFFE0E7FF)
                                                    "TRUE_FALSE" -> Color(0xFFFEF3C7)
                                                    else -> Color(0xFFDCFCE7)
                                                }
                                            ) {
                                                Text(
                                                    text = when (item.questionType) {
                                                        "MULTIPLE_CHOICE" -> "🎯 اختيار من متعدد"
                                                        "TRUE_FALSE" -> "✍️ صح أو خطأ"
                                                        else -> "📐 مسألة حسابية"
                                                    },
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = when (item.questionType) {
                                                            "MULTIPLE_CHOICE" -> Color(0xFF3730A3)
                                                            "TRUE_FALSE" -> Color(0xFF92400E)
                                                            else -> Color(0xFF166534)
                                                        },
                                                        fontWeight = FontWeight.Bold
                                                    ),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                                )
                                            }
                                        }

                                        // Question Text
                                        if (item.questionText.isNotBlank()) {
                                            Text(
                                                text = item.questionText,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    fontWeight = FontWeight.Medium,
                                                    lineHeight = 22.sp
                                                )
                                            )
                                        }

                                        // 1. Result box based on question type
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
                                                                text = "نص الاختيار: $optionText",
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
                                                                    fontWeight = FontWeight.SemiBold,
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
                                                            text = "الناتج النهائي: $cleanAns",
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

                                        // 2. Givens Box (المعطيات 📋) - Directly accessible as requested
                                        val cleanedGivens = MathFormatter.cleanItemList(item.givens)
                                        if (cleanedGivens.isNotEmpty()) {
                                            Surface(
                                                shape = RoundedCornerShape(10.dp),
                                                color = Color(0xFFF1F5F9),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(modifier = Modifier.padding(10.dp)) {
                                                    Text(
                                                        text = "المعطيات المستخرجة 📋:",
                                                        style = MaterialTheme.typography.labelMedium.copy(
                                                            fontWeight = FontWeight.Bold,
                                                            color = NavyPrimary
                                                        )
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    cleanedGivens.forEach { g ->
                                                        Text(
                                                            text = "• $g",
                                                            style = MaterialTheme.typography.bodySmall.copy(
                                                                color = MaterialTheme.colorScheme.onSurface,
                                                                fontWeight = FontWeight.Medium,
                                                                fontSize = 12.sp,
                                                                lineHeight = 18.sp
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        // 3. Explanation Toggle and Whiteboard Steps
                                        val cleanedLaws = MathFormatter.cleanItemList(item.laws)
                                        val cleanedSubst = MathFormatter.cleanItemList(item.substitutionSteps)
                                        val cleanedCalcs = MathFormatter.cleanItemList(item.calculationSteps)
                                        val hasSteps = cleanedLaws.isNotEmpty() || cleanedSubst.isNotEmpty() || cleanedCalcs.isNotEmpty()

                                        if (hasSteps) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                OutlinedButton(
                                                    onClick = {
                                                        expandedExplanations = if (isExpanded) {
                                                            expandedExplanations - actualIndex
                                                        } else {
                                                            expandedExplanations + actualIndex
                                                        }
                                                    },
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                                    modifier = Modifier.testTag("toggle_explain_button_$actualIndex")
                                                ) {
                                                    Icon(
                                                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.Lightbulb,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(16.dp),
                                                        tint = AmberSecondary
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = if (isExpanded) "إخفاء الشرح ▲" else "اشرح الحل والقانون 💡",
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = NavyPrimary
                                                    )
                                                }

                                                IconButton(
                                                    onClick = {
                                                        val singleSolText = buildString {
                                                            append("🔹 $qNumLabel\n")
                                                            if (item.questionText.isNotBlank()) append("السؤال: ${item.questionText}\n")
                                                            if (cleanedGivens.isNotEmpty()) append("المعطيات: ${cleanedGivens.joinToString(" ، ")}\n")
                                                            if (item.selectedOptionLabel != null) append("الإجابة الصحيحة: ${item.selectedOptionLabel}\n")
                                                            if (item.finalAnswer.isNotBlank()) append("الإجابة: ${item.finalAnswer}\n")
                                                        }
                                                        copyToClipboard(context, singleSolText, "حل $qNumLabel")
                                                    },
                                                    modifier = Modifier.size(34.dp)
                                                ) {
                                                    Icon(Icons.Default.ContentCopy, contentDescription = "نسخ حل هذا السؤال", modifier = Modifier.size(16.dp), tint = NavyPrimary)
                                                }
                                            }

                                            // Whiteboard Steps
                                            AnimatedVisibility(visible = isExpanded) {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .background(Color.White, RoundedCornerShape(10.dp))
                                                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                                                        .padding(12.dp),
                                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    if (cleanedLaws.isNotEmpty()) {
                                                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                                            Text(
                                                                text = "القانون الوزاري 📜:",
                                                                style = MaterialTheme.typography.labelMedium.copy(
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
                                                                        lineHeight = 20.sp
                                                                    )
                                                                )
                                                            }
                                                        }
                                                    }

                                                    if (cleanedSubst.isNotEmpty()) {
                                                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                                            Text(
                                                                text = "التعويض العددي ✍️:",
                                                                style = MaterialTheme.typography.labelMedium.copy(
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = NavyPrimary
                                                                )
                                                            )
                                                            cleanedSubst.forEach { s ->
                                                                Text(
                                                                    text = s,
                                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                                        color = MaterialTheme.colorScheme.onSurface,
                                                                        lineHeight = 20.sp
                                                                    )
                                                                )
                                                            }
                                                        }
                                                    }

                                                    if (cleanedCalcs.isNotEmpty()) {
                                                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                                            Text(
                                                                text = "خطوات الحساب والتبسيط 🔢:",
                                                                style = MaterialTheme.typography.labelMedium.copy(
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = NavyPrimary
                                                                )
                                                            )
                                                            cleanedCalcs.forEach { c ->
                                                                Text(
                                                                    text = c,
                                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                                        color = MaterialTheme.colorScheme.onSurface,
                                                                        lineHeight = 20.sp
                                                                    )
                                                                )
                                                            }
                                                        }
                                                    }

                                                    if (!item.verificationNote.isNullOrBlank()) {
                                                        Text(
                                                            text = "✓ تدقيق الحل: ${item.verificationNote}",
                                                            style = MaterialTheme.typography.labelSmall.copy(
                                                                color = Color(0xFF15803D),
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                            // Bottom Action Buttons: «تحقق من الحل» and «اشرح لي بطريقة أسهل»
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
                                    Icon(Icons.Default.HelpOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "لم أفهم الحل؟ اشرح لي 💡", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
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
