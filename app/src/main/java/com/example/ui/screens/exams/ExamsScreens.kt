package com.example.ui.screens.exams

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.ExamEntity
import com.example.data.local.entities.QuestionEntity
import com.example.ui.MainViewModel
import com.example.ui.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamsScreen(
    viewModel: MainViewModel,
    onNavigate: (Screen) -> Unit
) {
    val exams by viewModel.allExams.collectAsState()
    val subjects by viewModel.allSubjects.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("الاختبارات الوزارية والأتمتة") },
                actions = {
                    IconButton(onClick = { onNavigate(Screen.QuestionBank) }) {
                        Icon(Icons.Default.ListAlt, contentDescription = "بنك الأسئلة")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "🎯 نظام اختبارات الأتمتة الوزارية",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "تدرب على نماذج مطابقة تماماً لاختبارات وزارة التربية والتعليم في صنعاء وعدن مع تصحيح فوري وحساب النسبة المئوية.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Button(
                            onClick = { viewModel.startRandomExam(subjects.firstOrNull()?.id ?: 1L, 10) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("بدء اختبار تجريبي عشوائي (10 أسئلة)")
                        }
                    }
                }
            }

            item {
                Text("النماذج الوزارية المتاحة:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            items(exams) { exam ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.startExam(exam) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(exam.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "⏱️ ${exam.durationMinutes} دقيقة • 📝 ${exam.totalQuestions} سؤالاً",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                        Button(
                            onClick = { viewModel.startExam(exam) },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("بدء")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamSessionScreen(
    viewModel: MainViewModel,
    onNavigate: (Screen) -> Unit,
    onBack: () -> Unit
) {
    val exam by viewModel.currentExam.collectAsState()
    val questions by viewModel.examQuestions.collectAsState()
    val answers by viewModel.userExamAnswers.collectAsState()

    var showSubmitDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(exam?.title ?: "جلسة الاختبار") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Close, contentDescription = "إنهاء")
                    }
                },
                actions = {
                    TextButton(onClick = { showSubmitDialog = true }) {
                        Text("تسليم الاختبار", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp)
        ) {
            itemsIndexed(questions) { index, q ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "سؤال ${index + 1} من ${questions.size}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = if (q.type == "TRUE_FALSE") "صح أو خطأ" else "اختيار من متعدد",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        }

                        Text(q.questionText, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)

                        val currentAns = answers[q.id] ?: ""

                        if (q.type == "TRUE_FALSE") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.setExamAnswer(q.id, "TRUE") },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (currentAns == "TRUE") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                                    ),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                                ) {
                                    Text(
                                        "صح (✓)",
                                        color = if (currentAns == "TRUE") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Button(
                                    onClick = { viewModel.setExamAnswer(q.id, "FALSE") },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (currentAns == "FALSE") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                                    ),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                                ) {
                                    Text(
                                        "خطأ (✗)",
                                        color = if (currentAns == "FALSE") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        } else {
                            // Multiple choice A, B, C, D
                            val options = listOf("A" to q.optionA, "B" to q.optionB, "C" to q.optionC, "D" to q.optionD)
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                options.filter { it.second.isNotBlank() }.forEach { opt ->
                                    val isSelected = currentAns == opt.first
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { viewModel.setExamAnswer(q.id, opt.first) },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                                        border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            RadioButton(
                                                selected = isSelected,
                                                onClick = { viewModel.setExamAnswer(q.id, opt.first) }
                                            )
                                            Text(
                                                text = "${opt.first}) ${opt.second}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = { showSubmitDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("تسليم الإجابات وإنهاء الاختبار", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showSubmitDialog) {
        AlertDialog(
            onDismissRequest = { showSubmitDialog = false },
            title = { Text("تسليم الاختبار") },
            text = {
                val answered = answers.size
                Text("لقد قمت بالإجابة على $answered من أصل ${questions.size} أسئلة. هل أنت متأكد من رغبتك في تسليم الاختبار الآن؟")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSubmitDialog = false
                        viewModel.submitExam()
                    }
                ) {
                    Text("تأكيد التسليم")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamResultScreen(
    viewModel: MainViewModel,
    onNavigate: (Screen) -> Unit
) {
    val result by viewModel.currentExamResult.collectAsState()
    val exam by viewModel.currentExam.collectAsState()

    val currentResult = result ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("نتيجة الاختبار") },
                navigationIcon = {
                    IconButton(onClick = { onNavigate(Screen.Home) }) {
                        Icon(Icons.Default.Home, contentDescription = "الرئيسية")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (currentResult.isPassed) Color(0xFFF0FDF4) else Color(0xFFFEF2F2)
                ),
                border = BorderStroke(2.dp, if (currentResult.isPassed) Color(0xFF10B981) else Color(0xFFEF4444))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = if (currentResult.isPassed) Icons.Default.CheckCircle else Icons.Default.Cancel,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = if (currentResult.isPassed) Color(0xFF10B981) else Color(0xFFEF4444)
                    )

                    Text(
                        text = if (currentResult.isPassed) "مبارك! لقد اجتزت الاختبار بنجاح 🎉" else "للأسف، لم تجتز الاختبار، حاول مرة أخرى 💪",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (currentResult.isPassed) Color(0xFF15803D) else Color(0xFFB91C1C)
                    )

                    Text(
                        text = "${currentResult.score}%",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (currentResult.isPassed) Color(0xFF10B981) else Color(0xFFEF4444)
                    )

                    Divider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("الإجابات الصحيحة", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            Text("${currentResult.correctAnswers}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("الإجابات الخاطئة", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            Text("${currentResult.wrongAnswers}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("إجمالي الأسئلة", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            Text("${currentResult.totalQuestions}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Button(
                onClick = { onNavigate(Screen.Exams) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("العودة لقائمة الاختبارات")
            }

            OutlinedButton(
                onClick = { onNavigate(Screen.Home) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("الصفحة الرئيسية")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionBankScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val questions by viewModel.allQuestions.collectAsState()
    var search by remember { mutableStateOf("") }

    val filtered = questions.filter {
        search.isBlank() || it.questionText.contains(search)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("بنك الأسئلة الوزارية الشامل") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "رجوع")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("بحث في نصوص الأسئلة الوزارية...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filtered) { q ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(q.questionText, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text("الإجابة الصحيحة: ${q.correctOption}", color = Color(0xFF16A34A), fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                            if (q.explanation.isNotEmpty()) {
                                Text("التعليل الوزاري: ${q.explanation}", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}
