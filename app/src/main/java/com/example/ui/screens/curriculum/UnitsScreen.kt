package com.example.ui.screens.curriculum

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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.LessonEntity
import com.example.data.local.entities.UnitEntity
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val selectedSubject by viewModel.selectedSubject.collectAsState()
    val units by viewModel.unitsForSelectedSubject.collectAsState()
    val isSubscribed by viewModel.isSubscribed.collectAsState()
    val completedIds by viewModel.completedLessonIds.collectAsState()

    val subject = selectedSubject ?: return

    // State: Current active unit for independent page view
    var activeUnitId by remember { mutableStateOf<Long?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    // Search query or unit filter
    val lessonsForSubjectFlow = remember(subject.id) {
        viewModel.database.curriculumDao().getLessonsForSubject(subject.id)
    }
    val allSubjectLessons by lessonsForSubjectFlow.collectAsState(initial = emptyList())

    val filteredLessons = remember(allSubjectLessons, searchQuery, activeUnitId) {
        if (searchQuery.isNotBlank()) {
            allSubjectLessons.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.coreIdea.contains(searchQuery, ignoreCase = true) ||
                it.formulas.contains(searchQuery, ignoreCase = true) ||
                it.keyPoints.contains(searchQuery, ignoreCase = true) ||
                it.symbolsExplanation.contains(searchQuery, ignoreCase = true) ||
                it.solvedExample.contains(searchQuery, ignoreCase = true) ||
                it.simplifiedExplanation.contains(searchQuery, ignoreCase = true) ||
                it.practiceExercise.contains(searchQuery, ignoreCase = true)
            }
        } else if (activeUnitId != null) {
            allSubjectLessons.filter { it.unitId == activeUnitId }
        } else {
            emptyList()
        }
    }

    val activeUnit = units.find { it.id == activeUnitId }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("units_screen_content"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Breadcrumb Path
        val branchTitle = when (subject.name) {
            "الفيزياء" -> "الفيزياء الحديثة والذرية"
            "الرياضيات" -> "منهج الرياضيات للصف الثالث الثانوي"
            "الكيمياء" -> "منهج الكيمياء للصف الثالث الثانوي"
            else -> "منهج ${subject.name} للصف الثالث الثانوي"
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
                Text(subject.name, color = NavyPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable { activeUnitId = null; searchQuery = "" })
                Text("←", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                Text(branchTitle, color = Color(0xFF15803D), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { activeUnitId = null; searchQuery = "" })
                if (activeUnit != null) {
                    Text("←", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                    Text(activeUnit.title.substringBefore(" (").take(22), color = TealAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // 2. Search Field inside Subject
        val searchPlaceholder = when (subject.name) {
            "الكيمياء" -> "ابحث في الكيمياء: درس، معادلة، عنصر، مركب، مصطلح، قانون، مسألة..."
            "الرياضيات" -> "ابحث في الرياضيات: درس، مبرهنة، قانون، مسألة..."
            else -> "ابحث في ${subject.name}: درس، قانون، مفهوم، أو مسألة..."
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text(searchPlaceholder, fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "بحث", tint = NavyPrimary) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "مسح", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // 3. Subject Banner (only when not searching)
        if (searchQuery.isBlank() && activeUnit == null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(14.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        val icon = when (subject.name) {
                            "الفيزياء" -> "⚡"
                            "الكيمياء" -> "🧪"
                            "الرياضيات" -> "📐"
                            else -> "📚"
                        }
                        Text(text = icon, fontSize = 22.sp)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = branchTitle,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                        Text(
                            text = "جميع الوحدات والدروس مرتبة بدقة وشمولية وفق المنهج الوزاري اليمني مع أمثلة واختبارات.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }

        // 4. View Mode:
        // A) Search Results Mode
        if (searchQuery.isNotBlank()) {
            Text(
                text = "نتائج البحث في المنهج (${filteredLessons.size} درس):",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = NavyPrimary
            )

            if (filteredLessons.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("لا توجد دروس مطابقة لكلمة البحث في المنهج.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredLessons) { lesson ->
                        LessonCardItem(
                            lesson = lesson,
                            isCompleted = completedIds.contains(lesson.id),
                            isLocked = lesson.isPremium && !isSubscribed,
                            onClick = {
                                if (lesson.isPremium && !isSubscribed) {
                                    viewModel.navigateTo(Screen.Subscription)
                                } else {
                                    viewModel.selectLesson(lesson)
                                }
                            }
                        )
                    }
                }
            }
        }
        // B) Dedicated Unit Page Mode (صفحة الوحدة المستقلة)
        else if (activeUnit != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { activeUnitId = null },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "رجوع", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("العودة لقائمة الوحدات", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFEFF6FF)
                        ) {
                            Text(
                                text = "${filteredLessons.size} دروس",
                                color = NavyPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Text(
                        text = activeUnit.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary,
                            fontSize = 17.sp
                        )
                    )

                    if (activeUnit.description.isNotBlank()) {
                        Text(
                            text = activeUnit.description,
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }

                    // Button to take comprehensive unit exam
                    FilledTonalButton(
                        onClick = {
                            viewModel.startRandomExam(subject.id, count = 7)
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("📝 اختبار وتقويم الوحدة الشامل", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Text(
                text = "دروس الوحدة بالترتيب:",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = NavyPrimary
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredLessons) { lesson ->
                    LessonCardItem(
                        lesson = lesson,
                        isCompleted = completedIds.contains(lesson.id),
                        isLocked = lesson.isPremium && !isSubscribed,
                        onClick = {
                            if (lesson.isPremium && !isSubscribed) {
                                viewModel.navigateTo(Screen.Subscription)
                            } else {
                                viewModel.selectLesson(lesson)
                            }
                        }
                    )
                }
            }
        }
        // C) Units Overview List Mode (قائمة الوحدات الـ 7)
        else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "وحدات المنهج المقرر (${units.size} وحدات):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = NavyPrimary
                )
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(units) { unitItem ->
                    val unitLessonCount = allSubjectLessons.count { it.unitId == unitItem.id }
                    val completedInUnit = allSubjectLessons.count { it.unitId == unitItem.id && completedIds.contains(it.id) }

                    UnitSummaryCard(
                        unit = unitItem,
                        lessonCount = unitLessonCount,
                        completedCount = completedInUnit,
                        onOpenUnit = { activeUnitId = unitItem.id },
                        onTakeExam = { viewModel.startRandomExam(subject.id, count = 6) }
                    )
                }
            }
        }
    }
}

@Composable
fun UnitSummaryCard(
    unit: UnitEntity,
    lessonCount: Int,
    completedCount: Int,
    onOpenUnit: () -> Unit,
    onTakeExam: () -> Unit
) {
    Card(
        onClick = onOpenUnit,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFEFF6FF)
                ) {
                    Text(
                        text = if (lessonCount > 0) "$lessonCount دروس مرتبة" else "وحدة دراسية",
                        color = NavyPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                if (lessonCount > 0 && completedCount == lessonCount) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFDCFCE7)
                    ) {
                        Text(
                            text = "مكتملة 100% ✅",
                            color = Color(0xFF15803D),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Text(
                text = unit.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary,
                    fontSize = 16.sp
                )
            )

            if (unit.description.isNotBlank()) {
                Text(
                    text = unit.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onTakeExam,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("تقويم الوحدة 📝", fontSize = 11.sp)
                }

                Button(
                    onClick = onOpenUnit,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("فتح الوحدة 📖", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun LessonCardItem(
    lesson: LessonEntity,
    isCompleted: Boolean,
    isLocked: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isLocked) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isLocked) 0.dp else 1.5.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isLocked -> Color(0xFFFEE2E2)
                            isCompleted -> Color(0xFFDCFCE7)
                            else -> MaterialTheme.colorScheme.primaryContainer
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        isLocked -> Icons.Default.Lock
                        isCompleted -> Icons.Default.CheckCircle
                        else -> Icons.Default.MenuBook
                    },
                    contentDescription = null,
                    tint = when {
                        isLocked -> ErrorRed
                        isCompleted -> SuccessGreen
                        else -> MaterialTheme.colorScheme.primary
                    },
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = lesson.coreIdea.ifBlank { lesson.simplifiedExplanation },
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (isLocked) {
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text(
                        text = "اشتراك 🔒",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "فتح الدرس",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
