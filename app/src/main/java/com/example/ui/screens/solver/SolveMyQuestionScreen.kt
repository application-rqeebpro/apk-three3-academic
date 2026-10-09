package com.example.ui.screens.solver

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.solver.EducationalSolution
import com.example.data.solver.QuestionSolvedItem
import com.example.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolveMyQuestionScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val questionInput by viewModel.solverQuestionInput.collectAsState()
    val selectedSubject by viewModel.solverSelectedSubject.collectAsState()
    val imageBitmap by viewModel.solverImageBitmap.collectAsState()
    val isSolving by viewModel.isSolving.collectAsState()
    val structuredSolution by viewModel.solverStructuredResult.collectAsState()

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var expandedItems by remember { mutableStateOf<Set<Int>>(emptySet()) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            try {
                val bmp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                        decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                        decoder.isMutableRequired = true
                    }
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
                viewModel.solverImageBitmap.value = bmp
            } catch (e: Exception) {
                Toast.makeText(context, "خطأ في قراءة الصورة: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val subjects = listOf("الرياضيات", "الكيمياء", "الفيزياء", "الأحياء", "اللغة العربية", "اللغة الإنجليزية")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
    ) {
        // Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
                        }
                        Column {
                            Text(
                                text = "مصحح ومحلل الاختبارات والمسائل الذكي",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "يدعم حل نماذج الأتمتة الوزارية بالكامل حتى 50 سؤالاً (صح/خطأ واختيارات)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }

        // Subject Selector Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("المادة الدراسية:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    subjects.take(3).forEach { sub ->
                        FilterChip(
                            selected = selectedSubject == sub,
                            onClick = { viewModel.solverSelectedSubject.value = sub },
                            label = { Text(sub, style = MaterialTheme.typography.bodySmall) }
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    subjects.drop(3).forEach { sub ->
                        FilterChip(
                            selected = selectedSubject == sub,
                            onClick = { viewModel.solverSelectedSubject.value = sub },
                            label = { Text(sub, style = MaterialTheme.typography.bodySmall) }
                        )
                    }
                }
            }
        }

        // Input & Attachment Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = questionInput,
                        onValueChange = { viewModel.solverQuestionInput.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 6,
                        placeholder = { Text("اكتب نص المسألة، أو ارفع صورة نموذج الاختبار (حتى 50 سؤالاً)...") },
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Image Preview if selected
                    if (imageBitmap != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black.copy(alpha = 0.05f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                bitmap = imageBitmap!!.asImageBitmap(),
                                contentDescription = "الصورة المحددة",
                                modifier = Modifier.fillMaxSize()
                            )
                            IconButton(
                                onClick = { viewModel.solverImageBitmap.value = null },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "إزالة الصورة", tint = Color.White)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { photoPickerLauncher.launch("image/*") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (imageBitmap == null) "رفع صورة الاختبار" else "تغيير الصورة")
                        }

                        if (questionInput.isNotEmpty() || imageBitmap != null) {
                            IconButton(
                                onClick = { viewModel.clearSolver() }
                            ) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "مسح")
                            }
                        }
                    }

                    Button(
                        onClick = { viewModel.solveQuestion() },
                        enabled = !isSolving && (questionInput.isNotBlank() || imageBitmap != null),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isSolving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("جارٍ قراءة المسائل وحل النموذج كاملاً...")
                        } else {
                            Icon(Icons.Default.DoneAll, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("حل المسألة / نموذج الـ 50 سؤالاً بالكامل", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Solution Display Section
        if (structuredSolution != null) {
            val sol = structuredSolution!!
            val itemsList = sol.resolvedItems()

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📋 الحل النموذجي والخطوات الوزارية (${itemsList.size} سؤال):",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = {
                        val fullText = buildString {
                            appendLine("حل أسئلة الاختبار الوزاري - أكاديمية رقيب")
                            appendLine("المادة: $selectedSubject")
                            appendLine("-------------------------")
                            itemsList.forEachIndexed { i, q ->
                                val ans = q.finalAnswer.ifEmpty { q.selectedOptionLabel }
                                val steps = q.calculationSteps.joinToString(" • ")
                                appendLine("س${i + 1}: ${q.questionText}")
                                appendLine("الإجابة الصحيحة: $ans")
                                if (steps.isNotBlank()) appendLine("الخطوات: $steps")
                                appendLine("-------------------------")
                            }
                        }
                        clipboardManager.setText(AnnotatedString(fullText))
                        Toast.makeText(context, "تم نسخ جميع الحلول بنجاح", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "نسخ كل الحلول")
                    }
                }
            }

            // Summary Final Answer if present
            if (sol.finalAnswer.isNotBlank()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                        border = BorderStroke(1.dp, Color(0xFF86EFAC))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("النتيجة والخلاصة النهائية:", fontWeight = FontWeight.Bold, color = Color(0xFF166534))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(sol.finalAnswer, color = Color(0xFF14532D), lineHeight = 22.sp)
                        }
                    }
                }
            }

            // If exam has multiple questions (e.g. 50 questions), render each question card
            itemsIndexed(itemsList) { index, questionItem ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Text(
                                    text = "السؤال ${questionItem.questionNumber}",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (questionItem.questionType.contains("صح")) Color(0xFFFEF3C7) else Color(0xFFE0E7FF)
                            ) {
                                Text(
                                    text = questionItem.questionType,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    color = Color.DarkGray,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }

                        Text(
                            text = questionItem.questionText,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )

                        // Correct Choice Banner
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFDCFCE7),
                            border = BorderStroke(1.dp, Color(0xFF86EFAC)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A))
                                Text(
                                    text = "الإجابة الصحيحة: ${questionItem.correctChoice}",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF15803D),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        // Ministerial Steps / Reason
                        if (questionItem.stepsOrReason.isNotBlank()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Black.copy(alpha = 0.03f), RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "الخطوات والتعليل الوزاري:",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = questionItem.stepsOrReason,
                                    style = MaterialTheme.typography.bodySmall,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
