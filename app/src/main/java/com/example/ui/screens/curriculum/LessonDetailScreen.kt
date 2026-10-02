package com.example.ui.screens.curriculum

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.data.local.entities.QuestionEntity
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonDetailScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val lesson by viewModel.selectedLesson.collectAsState()
    val currentSubject by viewModel.selectedSubject.collectAsState()
    val simplerText by viewModel.simplerExplanationText.collectAsState()
    val isGeneratingSimpler by viewModel.isGeneratingSimpler.collectAsState()
    val completedIds by viewModel.completedLessonIds.collectAsState()
    val favorites by viewModel.userFavorites.collectAsState()

    val currentLesson = lesson ?: return

    val isCompleted = completedIds.contains(currentLesson.id)
    val isFav = favorites.any { it.itemType == "LESSON" && it.itemId == currentLesson.id }

    var isPracticeAnswerVisible by remember(currentLesson.id) { mutableStateOf(false) }

    // Interactive quiz questions for this lesson
    val lessonQuestionsFlow = remember(currentLesson.id) {
        viewModel.database.questionDao().getQuestionsForLesson(currentLesson.id)
    }
    val lessonQuestions by lessonQuestionsFlow.collectAsState(initial = emptyList())

    // State for interactive practice questions in lesson
    var selectedAnswers by remember(currentLesson.id) { mutableStateOf(mapOf<Long, String>()) }
    var expandedExplanations by remember(currentLesson.id) { mutableStateOf(setOf<Long>()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("lesson_detail_content"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 0. Breadcrumb Path
        item {
            val subName = currentSubject?.name ?: "الكيمياء"
            val branchTitle = when (subName) {
                "الفيزياء" -> "الفيزياء الحديثة والذرية"
                "الرياضيات" -> "منهج الرياضيات للصف الثالث الثانوي"
                "الكيمياء" -> "منهج الكيمياء للصف الثالث الثانوي"
                else -> "منهج $subName للصف الثالث الثانوي"
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF1F5F9),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("المواد", color = NavyPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable { viewModel.navigateTo(Screen.Subjects) })
                    Text("←", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                    Text(subName, color = NavyPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable { viewModel.navigateTo(Screen.Units) })
                    Text("←", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                    Text(branchTitle, color = Color(0xFF15803D), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { viewModel.navigateTo(Screen.Units) })
                    Text("←", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                    Text(currentLesson.title.take(18) + if (currentLesson.title.length > 18) "…" else "", color = TealAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // 1. Header Card: اسم الدرس ورقمه والمسار
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Text(
                                text = currentSubject?.name ?: "الرياضيات",
                                color = Color.White,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Favorite Button
                            IconButton(
                                onClick = {
                                    viewModel.toggleFavorite(
                                        type = "LESSON",
                                        itemId = currentLesson.id,
                                        title = currentLesson.title,
                                        subtitle = currentSubject?.name ?: "",
                                        isFav = !isFav
                                    )
                                },
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(MaterialTheme.colorScheme.surface, CircleShape)
                            ) {
                                Icon(
                                    imageVector = if (isFav) Icons.Default.Star else Icons.Outlined.StarBorder,
                                    contentDescription = "المفضلة",
                                    tint = if (isFav) AmberSecondary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Mark Completed Button
                            IconButton(
                                onClick = {
                                    viewModel.toggleLessonCompletion(
                                        lessonId = currentLesson.id,
                                        subjectId = currentLesson.subjectId,
                                        completed = !isCompleted
                                    )
                                },
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(
                                        if (isCompleted) Color(0xFFDCFCE7) else MaterialTheme.colorScheme.surface,
                                        CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Outlined.CheckCircleOutline,
                                    contentDescription = "إكمال الدرس",
                                    tint = if (isCompleted) Color(0xFF15803D) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = currentLesson.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 19.sp
                        )
                    )
                }
            }
        }

        // Action Buttons Row: [اشرح لي الدرس] • [حل التمارين] • [اختبرني]
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 12. زر: "اشرح لي الدرس"
                Button(
                    onClick = { viewModel.requestSimplerExplanation() },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    modifier = Modifier.weight(1f).height(42.dp)
                ) {
                    Text("💡 اشرح لي الدرس", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // 13. زر: "حل التمارين"
                FilledTonalButton(
                    onClick = { isPracticeAnswerVisible = true },
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    modifier = Modifier.weight(1f).height(42.dp)
                ) {
                    Text("✍️ حل التمارين", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // 14. زر: "اختبرني"
                Button(
                    onClick = { viewModel.startRandomExam(currentLesson.subjectId, count = 5) },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    modifier = Modifier.weight(1f).height(42.dp)
                ) {
                    Text("📝 اختبرني", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Dynamic Simpler Explanation (if requested)
        if (simplerText != null || isGeneratingSimpler) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardGold),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "🌟", fontSize = 18.sp)
                            Text(
                                text = "الشرح المبسط جداً بأمثلة الحياة اليومية:",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF78350F))
                            )
                        }

                        if (isGeneratingSimpler) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = AmberSecondary, strokeWidth = 2.dp)
                                Text(text = "جاري تبسيط الفكرة بواسطة المعلم الذكي...", fontSize = 12.sp, color = Color(0xFF78350F))
                            }
                        } else {
                            Text(
                                text = simplerText ?: "",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, lineHeight = 21.sp, color = Color(0xFF451A03))
                            )
                        }
                    }
                }
            }
        }

        // 2. شرح مختصر وسهل
        item {
            LessonSectionCard(
                title = "📖 2. شرح مختصر وسهل",
                badgeColor = Color(0xFFEFF6FF),
                titleColor = NavyPrimary
            ) {
                Text(
                    text = currentLesson.simplifiedExplanation.ifBlank { currentLesson.coreIdea },
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, lineHeight = 22.sp)
                )
            }
        }

        // 3. المفاهيم الأساسية
        if (currentLesson.coreIdea.isNotBlank()) {
            item {
                LessonSectionCard(
                    title = "💡 3. المفاهيم الأساسية",
                    badgeColor = Color(0xFFECFDF5),
                    titleColor = TealAccent
                ) {
                    Text(
                        text = currentLesson.coreIdea,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, lineHeight = 22.sp, fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }

        // 4. القوانين المهمة
        if (currentLesson.formulas.isNotBlank()) {
            item {
                LessonSectionCard(
                    title = "📐 4. القوانين الرياضية المهمة",
                    badgeColor = Color(0xFFF5F3FF),
                    titleColor = Color(0xFF6D28D9)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFAF5FF),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE9D5FF)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = currentLesson.formulas,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 14.sp,
                                    lineHeight = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF581C87)
                                )
                            )
                        }
                    }
                }
            }
        }

        // 5. شرح معنى كل رمز في القانون
        if (currentLesson.symbolsExplanation.isNotBlank()) {
            item {
                LessonSectionCard(
                    title = "🔍 5. معنى كل رمز في القانون",
                    badgeColor = Color(0xFFFFFBEB),
                    titleColor = AmberSecondary
                ) {
                    val symbols = currentLesson.symbolsExplanation.split("\n")
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        symbols.forEach { sym ->
                            if (sym.isNotBlank()) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("🔹", fontSize = 12.sp)
                                    Text(text = sym.trim(), fontSize = 13.sp, lineHeight = 20.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 6. مثال محلول أول خطوة بخطوة (المعطيات ↓ القانون ↓ التعويض ↓ الحساب ↓ الإجابة)
        if (currentLesson.solvedExample.isNotBlank()) {
            item {
                LessonSectionCard(
                    title = "✍️ 6. مثال محلول أول (خطوة بخطوة)",
                    badgeColor = Color(0xFFEFF6FF),
                    titleColor = NavyPrimary
                ) {
                    FormattedSolutionDisplay(text = currentLesson.solvedExample)
                }
            }
        }

        // 7. مثال محلول ثانٍ خطوة بخطوة
        if (currentLesson.solvedExample2.isNotBlank()) {
            item {
                LessonSectionCard(
                    title = "✍️ 7. مثال محلول ثانٍ (خطوة بخطوة)",
                    badgeColor = Color(0xFFEFF6FF),
                    titleColor = NavyPrimary
                ) {
                    FormattedSolutionDisplay(text = currentLesson.solvedExample2)
                }
            }
        }

        // 8. مثال ثالث عند الحاجة
        if (currentLesson.solvedExample3.isNotBlank()) {
            item {
                LessonSectionCard(
                    title = "✍️ 8. مثال محلول ثالث (تطبيقي)",
                    badgeColor = Color(0xFFEFF6FF),
                    titleColor = NavyPrimary
                ) {
                    FormattedSolutionDisplay(text = currentLesson.solvedExample3)
                }
            }
        }

        // 9. ملاحظات مهمة للطالب
        if (currentLesson.keyPoints.isNotBlank()) {
            item {
                LessonSectionCard(
                    title = "📌 9. ملاحظات وتنبيهات مهمة للطالب",
                    badgeColor = Color(0xFFFEF3C7),
                    titleColor = Color(0xFFB45309)
                ) {
                    val points = currentLesson.keyPoints.split("\n")
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        points.forEach { pt ->
                            if (pt.isNotBlank()) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text("⚠️", fontSize = 14.sp)
                                    Text(
                                        text = pt.trim(),
                                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, lineHeight = 20.sp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 10. تمارين للتدريب
        if (currentLesson.practiceExercise.isNotBlank()) {
            item {
                LessonSectionCard(
                    title = "🎯 10. تمارين للتدريب والتطبيق",
                    badgeColor = Color(0xFFFDF2F8),
                    titleColor = Color(0xFFBE185D)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        val exerciseQuestion = currentLesson.practiceExercise.substringBefore("طريقة الحل:").trim()
                        Text(
                            text = exerciseQuestion,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp, lineHeight = 21.sp)
                        )

                        OutlinedButton(
                            onClick = { isPracticeAnswerVisible = !isPracticeAnswerVisible },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = if (isPracticeAnswerVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = if (isPracticeAnswerVisible) "إخفاء الحل النموذجي" else "عرض الإجابة والحل النموذجي", fontSize = 12.sp)
                        }

                        AnimatedVisibility(visible = isPracticeAnswerVisible) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF0FDF4),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    val solutionText = if (currentLesson.practiceExercise.contains("طريقة الحل:")) {
                                        currentLesson.practiceExercise.substringAfter("طريقة الحل:").trim()
                                    } else {
                                        currentLesson.practiceExercise
                                    }
                                    Text(
                                        text = "طريقة الحل النموذجية:\n$solutionText",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontSize = 13.sp,
                                            lineHeight = 21.sp,
                                            color = Color(0xFF14532D)
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 11. أسئلة اختيار من متعدد تفاعلية
        if (lessonQuestions.isNotEmpty()) {
            item {
                LessonSectionCard(
                    title = "📝 11. أسئلة تدريبية واختيار من متعدد",
                    badgeColor = Color(0xFFEFF6FF),
                    titleColor = NavyPrimary
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        lessonQuestions.forEachIndexed { qIdx, question ->
                            val userChoice = selectedAnswers[question.id]
                            val isSolved = userChoice != null
                            val isExpanded = expandedExplanations.contains(question.id)

                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "السؤال ${qIdx + 1}: ${question.questionText}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = NavyPrimary
                                    )

                                    val optionsList = question.options.split("||").map { it.trim() }

                                    // Display Options
                                    optionsList.forEachIndexed { optIdx, option ->
                                        val optNumber = when (optIdx) {
                                            0 -> "①"
                                            1 -> "②"
                                            2 -> "③"
                                            3 -> "④"
                                            else -> "•"
                                        }
                                        val isSelected = userChoice == option

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = when {
                                                !isSolved -> MaterialTheme.colorScheme.surface
                                                option == question.correctAnswer -> Color(0xFFDCFCE7)
                                                isSelected && option != question.correctAnswer -> Color(0xFFFEE2E2)
                                                else -> MaterialTheme.colorScheme.surface
                                            },
                                            border = androidx.compose.foundation.BorderStroke(
                                                1.dp,
                                                if (isSelected) NavyPrimary else Color(0xFFE2E8F0)
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable(enabled = !isSolved) {
                                                    selectedAnswers = selectedAnswers + (question.id to option)
                                                }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text(optNumber, fontWeight = FontWeight.Bold, color = NavyPrimary, fontSize = 13.sp)
                                                Text(option, fontSize = 13.sp, modifier = Modifier.weight(1f))
                                                if (isSolved && option == question.correctAnswer) {
                                                    Text("✅", fontSize = 12.sp)
                                                } else if (isSolved && isSelected && option != question.correctAnswer) {
                                                    Text("❌", fontSize = 12.sp)
                                                }
                                            }
                                        }
                                    }

                                    // Feedback and "اشرح الحل"
                                    if (isSolved) {
                                        val correctOptIdx = optionsList.indexOf(question.correctAnswer)
                                        val correctOptNum = when (correctOptIdx) {
                                            0 -> "الاختيار ①"
                                            1 -> "الاختيار ②"
                                            2 -> "الاختيار ③"
                                            3 -> "الاختيار ④"
                                            else -> ""
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFFF0FDF4),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                if (question.questionType == "TRUE_FALSE") {
                                                    val isCorrectVal = question.correctAnswer == "صح"
                                                    Text(
                                                        text = if (isCorrectVal) "الإجابة: صح ✅" else "الإجابة: خطأ ❌",
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isCorrectVal) Color(0xFF15803D) else Color(0xFFB91C1C),
                                                        fontSize = 13.sp
                                                    )
                                                } else {
                                                    Text(
                                                        text = "الإجابة الصحيحة: $correctOptNum",
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF15803D),
                                                        fontSize = 13.sp
                                                    )
                                                    Text(
                                                        text = "الإجابة: [ ${question.correctAnswer} ]",
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = Color(0xFF166534),
                                                        fontSize = 12.sp
                                                    )
                                                }

                                                Spacer(modifier = Modifier.height(6.dp))

                                                OutlinedButton(
                                                    onClick = {
                                                        expandedExplanations = if (isExpanded) {
                                                            expandedExplanations - question.id
                                                        } else {
                                                            expandedExplanations + question.id
                                                        }
                                                    },
                                                    shape = RoundedCornerShape(6.dp),
                                                    modifier = Modifier.fillMaxWidth().height(32.dp),
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                                ) {
                                                    Text(if (isExpanded) "إخفاء الشرح" else "اشرح الحل 💡", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }

                                                AnimatedVisibility(visible = isExpanded) {
                                                    Text(
                                                        text = question.explanation,
                                                        fontSize = 12.sp,
                                                        lineHeight = 18.sp,
                                                        color = Color(0xFF1F2937),
                                                        modifier = Modifier.padding(top = 6.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 15. Previous & Next Lesson Navigation Buttons
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { viewModel.navigateToAdjacentLesson(next = false) },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Text("الدرس السابق ➡️", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { viewModel.navigateToAdjacentLesson(next = true) },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Text("⬅️ الدرس التالي", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun FormattedSolutionDisplay(text: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFF8FAFC),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val lines = text.split("\n")
            lines.forEach { line ->
                val trimmed = line.trim()
                when {
                    trimmed.startsWith("المعطيات:") -> {
                        Text(trimmed, fontWeight = FontWeight.Bold, color = NavyPrimary, fontSize = 13.sp)
                    }
                    trimmed.startsWith("القانون:") -> {
                        Text(trimmed, fontWeight = FontWeight.Bold, color = Color(0xFF0F766E), fontSize = 13.sp)
                    }
                    trimmed.startsWith("التعويض:") -> {
                        Text(trimmed, fontWeight = FontWeight.Bold, color = Color(0xFFB45309), fontSize = 13.sp)
                    }
                    trimmed.startsWith("الحساب:") || trimmed.startsWith("الحل:") -> {
                        Text(trimmed, fontWeight = FontWeight.Bold, color = Color(0xFF4338CA), fontSize = 13.sp)
                    }
                    trimmed.startsWith("الإجابة:") || trimmed.startsWith("النتيجة:") -> {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFDCFCE7),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = "✅ $trimmed",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF15803D),
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    trimmed.isNotBlank() -> {
                        Text(trimmed, fontSize = 13.sp, lineHeight = 20.sp, color = Color(0xFF334155))
                    }
                }
            }
        }
    }
}

@Composable
fun LessonSectionCard(
    title: String,
    badgeColor: Color,
    titleColor: Color,
    content: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = badgeColor
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = titleColor,
                        fontSize = 13.sp
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            content()
        }
    }
}
