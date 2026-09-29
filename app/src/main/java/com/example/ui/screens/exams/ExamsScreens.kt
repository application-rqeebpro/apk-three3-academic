package com.example.ui.screens.exams

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.ExamEntity
import com.example.data.local.entities.QuestionEntity
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.theme.*

// -------------------------------------------------------------
// EXAMS LIST SCREEN
// -------------------------------------------------------------
@Composable
fun ExamsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val exams by viewModel.allExams.collectAsState()
    val subjects by viewModel.allSubjects.collectAsState()
    val results by viewModel.userExamResults.collectAsState()
    val isSubscribed by viewModel.isSubscribed.collectAsState()

    var showRandomExamDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("exams_screen_content"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "📝", fontSize = 24.sp)
                    }

                    Column {
                        Text(
                            text = "اختبر نفسك (النماذج الوزارية) 🇾🇪",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                        Text(
                            text = "اختبارات تدريبية تجريبية لقياس مدى استيعابك للمنهج مع تقرير تفصيلي بالحلول.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }

        // Action: Random Exam
        item {
            Button(
                onClick = { showRandomExamDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("create_random_exam_button")
            ) {
                Icon(imageVector = Icons.Default.Shuffle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "إنشاء اختبار عشوائي مخصص 🎲",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        // Exams List
        item {
            Text(
                text = "النماذج المتوفرة:",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        items(exams) { exam ->
            val isLocked = exam.isPremium && !isSubscribed
            Card(
                onClick = {
                    if (isLocked) {
                        viewModel.navigateTo(Screen.Subscription)
                    } else {
                        viewModel.startExam(exam)
                    }
                },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = exam.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "⏱️ ${exam.durationMinutes} دقيقة",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            Text(
                                text = "❓ ${exam.totalQuestions} أسئلة",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }

                    if (isLocked) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CardGold
                        ) {
                            Text(
                                text = "مدفوع 🔒",
                                color = AmberSecondary,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    } else {
                        Button(
                            onClick = { viewModel.startExam(exam) },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(text = "ابدأ", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Previous Results
        if (results.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "نتائجك السابقة:",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            items(results) { res ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(14.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = res.examTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(
                                text = "صحيحة: ${res.correctCount} • خاطئة: ${res.wrongCount}",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (res.scorePercentage >= 50) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
                        ) {
                            Text(
                                text = "${res.scorePercentage}%",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (res.scorePercentage >= 50) SuccessGreen else ErrorRed
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Random Exam Dialog
    if (showRandomExamDialog) {
        var selectedSubjectId by remember { mutableStateOf(subjects.firstOrNull()?.id ?: 1L) }
        var questionCount by remember { mutableStateOf(5) }

        AlertDialog(
            onDismissRequest = { showRandomExamDialog = false },
            title = { Text("إنشاء اختبار عشوائي") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("اختر المادة:")
                    subjects.take(5).forEach { subj ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedSubjectId = subj.id }
                        ) {
                            RadioButton(
                                selected = selectedSubjectId == subj.id,
                                onClick = { selectedSubjectId = subj.id }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(subj.name)
                        }
                    }

                    Text("عدد الأسئلة: $questionCount")
                    Slider(
                        value = questionCount.toFloat(),
                        onValueChange = { questionCount = it.toInt() },
                        valueRange = 3f..10f,
                        steps = 6
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRandomExamDialog = false
                        viewModel.startRandomExam(selectedSubjectId, questionCount)
                    }
                ) {
                    Text("ابدأ الاختبار")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRandomExamDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

// -------------------------------------------------------------
// EXAM SESSION SCREEN
// -------------------------------------------------------------
@Composable
fun ExamSessionScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val exam by viewModel.currentExam.collectAsState()
    val questions by viewModel.examQuestions.collectAsState()
    val userAnswers by viewModel.userExamAnswers.collectAsState()

    var currentQuestionIndex by remember { mutableStateOf(0) }

    if (questions.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val q = questions.getOrNull(currentQuestionIndex) ?: return

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("exam_session_content"),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = exam?.title ?: "الاختبار",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "السؤال ${currentQuestionIndex + 1} من ${questions.size}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            LinearProgressIndicator(
                progress = { (currentQuestionIndex + 1).toFloat() / questions.size },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary
            )
        }

        val options = remember(q) {
            q.options.split("||").map { it.trim() }.filter { it.isNotBlank() }
        }

        // Question Card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 16.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        text = q.questionText,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            lineHeight = 24.sp
                        )
                    )
                }

                items(options) { opt ->
                    val isSelected = userAnswers[q.id] == opt
                    Surface(
                        onClick = { viewModel.setExamAnswer(q.id, opt) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(14.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { viewModel.setExamAnswer(q.id, opt) }
                            )
                            Text(
                                text = opt,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // Navigation Footer Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (currentQuestionIndex > 0) {
                OutlinedButton(
                    onClick = { currentQuestionIndex-- },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text("السابق")
                }
            }

            if (currentQuestionIndex < questions.size - 1) {
                Button(
                    onClick = { currentQuestionIndex++ },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text("التالي")
                }
            } else {
                Button(
                    onClick = { viewModel.submitExam() },
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("submit_exam_button")
                ) {
                    Text("إنهاء الاختبار وتسليم الإجابات ✅", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// EXAM RESULT SCREEN
// -------------------------------------------------------------
@Composable
fun ExamResultScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val result by viewModel.currentExamResult.collectAsState()
    val questions by viewModel.examQuestions.collectAsState()
    val userAnswers by viewModel.userExamAnswers.collectAsState()

    val res = result ?: return

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("exam_result_content"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Result Score Card
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (res.scorePercentage >= 50) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (res.scorePercentage >= 50) "🎉 مبارك لك النجاح!" else "💪 حاول مرة أخرى وراجع الشروحات",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (res.scorePercentage >= 50) Color(0xFF14532D) else Color(0xFF7F1D1D)
                        )
                    )

                    Text(
                        text = "${res.scorePercentage}%",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = if (res.scorePercentage >= 50) SuccessGreen else ErrorRed
                        )
                    )

                    Text(
                        text = "الإجابات الصحيحة: ${res.correctCount} من ${res.totalQuestions}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
        }

        // Return Home or Retake
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { viewModel.navigateTo(Screen.Exams) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("قائمة الاختبارات")
                }
                OutlinedButton(
                    onClick = { viewModel.navigateTo(Screen.Home) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("الرئيسية")
                }
            }
        }

        // Detailed Review of Each Question
        item {
            Text(
                text = "مراجعة تفصيلية للإجابات:",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        items(questions) { q ->
            val userAns = userAnswers[q.id]
            val isCorrect = userAns != null && userAns.equals(q.correctAnswer.trim(), ignoreCase = true)

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(14.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = q.questionText,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                            contentDescription = null,
                            tint = if (isCorrect) SuccessGreen else ErrorRed
                        )
                    }

                    Text(
                        text = "إجابتك: ${userAns ?: "لم يتم الإجابة"}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (isCorrect) SuccessGreen else ErrorRed,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    if (!isCorrect) {
                        Text(
                            text = "الإجابة الصحيحة: ${q.correctAnswer}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SuccessGreen,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "الشرح والتوضيح:\n${q.explanation}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 18.sp),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// QUESTION BANK SCREEN
// -------------------------------------------------------------
@Composable
fun QuestionBankScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val questions by viewModel.allQuestions.collectAsState()
    val subjects by viewModel.allSubjects.collectAsState()

    var filterSubjectId by remember { mutableStateOf<Long?>(null) }
    var filterDifficulty by remember { mutableStateOf<String?>(null) }

    val filteredQuestions = remember(questions, filterSubjectId, filterDifficulty) {
        questions.filter { q ->
            (filterSubjectId == null || q.subjectId == filterSubjectId) &&
            (filterDifficulty == null || q.difficulty == filterDifficulty)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("question_bank_content"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "📖", fontSize = 22.sp)
                    }

                    Column {
                        Text(
                            text = "بنك أسئلة الثالث الثانوي اليمني",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                        Text(
                            text = "تصفح مئات الأسئلة المصنفة حسب المادة ومستوى الصعوبة.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                            )
                        )
                    }
                }
            }
        }

        // Filters Row
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "فلترة حسب المادة:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
                androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = filterSubjectId == null,
                            onClick = { filterSubjectId = null },
                            label = { Text("الكل") }
                        )
                    }
                    items(subjects) { s ->
                        FilterChip(
                            selected = filterSubjectId == s.id,
                            onClick = { filterSubjectId = s.id },
                            label = { Text(s.name) }
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "الأسئلة (${filteredQuestions.size}):",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        items(filteredQuestions) { q ->
            var showSolution by remember { mutableStateOf(false) }

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(14.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (q.difficulty) {
                                "EASY" -> Color(0xFFDCFCE7)
                                "HARD" -> Color(0xFFFEE2E2)
                                else -> Color(0xFFFEF3C7)
                            }
                        ) {
                            Text(
                                text = when (q.difficulty) {
                                    "EASY" -> "سهل"
                                    "HARD" -> "متقدم"
                                    else -> "متوسط"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = when (q.questionType) {
                                "MCQ" -> "اختيار من متعدد"
                                "TRUE_FALSE" -> "صح وخطأ"
                                else -> "سؤال قصير"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }

                    Text(
                        text = q.questionText,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    )

                    OutlinedButton(
                        onClick = { showSolution = !showSolution },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(text = if (showSolution) "إخفاء الحل" else "عرض الإجابة والتفسير", fontSize = 12.sp)
                    }

                    AnimatedVisibility(visible = showSolution) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFECFDF5),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "الإجابة الصحيحة: ${q.correctAnswer}",
                                    fontWeight = FontWeight.Bold,
                                    color = TealAccent,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = q.explanation,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
