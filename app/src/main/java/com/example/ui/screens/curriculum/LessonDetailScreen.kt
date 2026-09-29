package com.example.ui.screens.curriculum

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.components.MathEquationBox
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonDetailScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val lesson by viewModel.selectedLesson.collectAsState()
    val currentSubject by viewModel.selectedSubject.collectAsState()
    val explanationLevel by viewModel.explanationLevel.collectAsState()
    val simplerText by viewModel.simplerExplanationText.collectAsState()
    val isGeneratingSimpler by viewModel.isGeneratingSimpler.collectAsState()
    val completedIds by viewModel.completedLessonIds.collectAsState()
    val favorites by viewModel.userFavorites.collectAsState()

    val currentLesson = lesson ?: return

    val isCompleted = completedIds.contains(currentLesson.id)
    val isFav = favorites.any { it.itemType == "LESSON" && it.itemId == currentLesson.id }

    var isPracticeAnswerVisible by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("lesson_detail_content"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Lesson Header Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(18.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
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
                                text = currentSubject?.name ?: "المنهج اليمني",
                                color = Color.White,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
                                    .size(36.dp)
                                    .background(MaterialTheme.colorScheme.surface, CircleShape)
                            ) {
                                Icon(
                                    imageVector = if (isFav) Icons.Default.Star else Icons.Outlined.StarBorder,
                                    contentDescription = "المفضلة",
                                    tint = if (isFav) AmberSecondary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
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
                                    .size(36.dp)
                                    .background(
                                        if (isCompleted) SuccessGreen.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                                        CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Outlined.CheckCircleOutline,
                                    contentDescription = "إكمال الدرس",
                                    tint = if (isCompleted) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = currentLesson.title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 20.sp
                        )
                    )
                }
            }
        }

        // Explanation Level Selector
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "اختر مستوى الشرح:",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    val levels = listOf("بسيط جداً", "عادي", "مفصل", "أمثلة", "اختبرني")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        levels.forEach { level ->
                            val isSelected = explanationLevel == level
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    viewModel.setExplanationLevel(level)
                                    if (level == "اختبرني") {
                                        viewModel.startRandomExam(currentLesson.subjectId, count = 3)
                                    }
                                },
                                label = {
                                    Text(
                                        text = level,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Section: الفكرة الأساسية
        item {
            LessonSectionCard(
                title = "💡 الفكرة الأساسية",
                badgeColor = Color(0xFFEFF6FF),
                titleColor = NavyPrimary
            ) {
                Text(
                    text = currentLesson.coreIdea,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }

        // Section: شرح مبسط (step by step)
        item {
            LessonSectionCard(
                title = "📖 الشرح المبسط (خطوة بخطوة)",
                badgeColor = Color(0xFFECFDF5),
                titleColor = TealAccent
            ) {
                val displayText = when (explanationLevel) {
                    "بسيط جداً" -> currentLesson.coreIdea + "\n\n" + (currentLesson.easierExplanation.ifBlank { currentLesson.simplifiedExplanation })
                    "مفصل" -> currentLesson.simplifiedExplanation + "\n\n" + currentLesson.keyPoints
                    "أمثلة" -> currentLesson.solvedExample
                    else -> currentLesson.simplifiedExplanation
                }

                Text(
                    text = displayText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        lineHeight = 23.sp
                    )
                )
            }
        }

        // Prominent Button: "اشرح لي بطريقة أسهل"
        item {
            Button(
                onClick = { viewModel.requestSimplerExplanation() },
                colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("explain_simpler_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AutoFixHigh,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "اشرح لي بطريقة أسهل ✨",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                )
            }
        }

        // Dynamic Simpler Explanation Box (if clicked)
        if (simplerText != null || isGeneratingSimpler) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardGold),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "🌟", fontSize = 20.sp)
                            Text(
                                text = "الشرح المبسط جداً بأمثلة الحياة اليومية:",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF78350F)
                                )
                            )
                        }

                        if (isGeneratingSimpler) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = AmberSecondary,
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    text = "جاري تبسيط الفكرة بواسطة المعلم الذكي...",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF78350F))
                                )
                            }
                        } else {
                            Text(
                                text = simplerText ?: "",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 14.sp,
                                    lineHeight = 22.sp,
                                    color = Color(0xFF451A03)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Section: أهم النقاط
        if (currentLesson.keyPoints.isNotBlank()) {
            item {
                LessonSectionCard(
                    title = "📌 أهم النقاط للحفظ والفهم",
                    badgeColor = Color(0xFFFFFBEB),
                    titleColor = AmberSecondary
                ) {
                    val points = currentLesson.keyPoints.split("\n")
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        points.forEach { pt ->
                            if (pt.isNotBlank()) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(text = "•", color = AmberSecondary, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = pt.trim(),
                                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, lineHeight = 21.sp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section: القوانين والرموز
        if (currentLesson.formulas.isNotBlank()) {
            item {
                LessonSectionCard(
                    title = "📐 القوانين والرموز والوحدات",
                    badgeColor = Color(0xFFF5F3FF),
                    titleColor = Color(0xFF6D28D9)
                ) {
                    MathEquationBox(equation = currentLesson.formulas)
                }
            }
        }

        // Section: مثال محلول خطوة بخطوة
        if (currentLesson.solvedExample.isNotBlank()) {
            item {
                LessonSectionCard(
                    title = "✍️ مثال محلول خطوة بخطوة",
                    badgeColor = Color(0xFFEFF6FF),
                    titleColor = NavyPrimary
                ) {
                    Text(
                        text = currentLesson.solvedExample,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            lineHeight = 23.sp
                        )
                    )
                }
            }
        }

        // Section: تدريب وسؤال للتطبيق
        if (currentLesson.practiceExercise.isNotBlank()) {
            item {
                LessonSectionCard(
                    title = "🎯 تدريب (حاول الحل بنفسك)",
                    badgeColor = Color(0xFFFDF2F8),
                    titleColor = Color(0xFFBE185D)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = currentLesson.practiceExercise.substringBefore("طريقة الحل:").trim(),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        )

                        OutlinedButton(
                            onClick = { isPracticeAnswerVisible = !isPracticeAnswerVisible },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = if (isPracticeAnswerVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = if (isPracticeAnswerVisible) "إخفاء الحل النموذجي" else "عرض الإجابة وطريقة الحل والسبب")
                        }

                        AnimatedVisibility(visible = isPracticeAnswerVisible) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    val solutionText = if (currentLesson.practiceExercise.contains("طريقة الحل:")) {
                                        currentLesson.practiceExercise.substringAfter("طريقة الحل:").trim()
                                    } else {
                                        currentLesson.practiceExercise
                                    }
                                    Text(
                                        text = "طريقة الحل والتفسير:\n$solutionText",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontSize = 13.sp,
                                            lineHeight = 21.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Footer Actions: Ask Smart Tutor about this lesson
        item {
            Card(
                onClick = { viewModel.navigateTo(Screen.SmartTutor) },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
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
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "هل لديك سؤال عن هذا الدرس؟",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "اسأل المعلم الذكي وسيشرح لك أي نقطة فوراً",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "اسأل المعلم",
                        tint = MaterialTheme.colorScheme.primary
                    )
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
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badgeColor
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = titleColor,
                            fontSize = 14.sp
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            content()
        }
    }
}
