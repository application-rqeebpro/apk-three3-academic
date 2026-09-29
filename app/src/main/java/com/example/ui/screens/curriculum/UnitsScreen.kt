package com.example.ui.screens.curriculum

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.LessonEntity
import com.example.data.local.entities.UnitEntity
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.theme.*

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

    var selectedUnitId by remember(units) {
        mutableStateOf(units.firstOrNull()?.id ?: 0L)
    }

    val lessonsFlow = remember(selectedUnitId) {
        if (selectedUnitId > 0) {
            viewModel.database.curriculumDao().getLessonsForUnit(selectedUnitId)
        } else {
            viewModel.database.curriculumDao().getLessonsForSubject(subject.id)
        }
    }
    val lessons by lessonsFlow.collectAsState(initial = emptyList())

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("units_screen_content")
    ) {
        // Subject Banner
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
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "📚", fontSize = 22.sp)
                }

                Column {
                    Text(
                        text = subject.name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                    Text(
                        text = subject.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Units Tab Row
        if (units.isNotEmpty()) {
            ScrollableTabRow(
                selectedTabIndex = units.indexOfFirst { it.id == selectedUnitId }.coerceAtLeast(0),
                edgePadding = 0.dp,
                containerColor = Color.Transparent,
                divider = {}
            ) {
                units.forEach { unit ->
                    val isSelected = unit.id == selectedUnitId
                    Tab(
                        selected = isSelected,
                        onClick = { selectedUnitId = unit.id },
                        text = {
                            Text(
                                text = unit.title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Lessons List
        if (lessons.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "جاري إضافة شروحات الدروس لهذه الوحدة من لوحة التحكم...",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(lessons) { lesson ->
                    val isCompleted = completedIds.contains(lesson.id)
                    val isLocked = lesson.isPremium && !isSubscribed

                    LessonCardItem(
                        lesson = lesson,
                        isCompleted = isCompleted,
                        isLocked = isLocked,
                        onClick = {
                            if (isLocked) {
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
        elevation = CardDefaults.cardElevation(defaultElevation = if (isLocked) 0.dp else 2.dp),
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
                    .size(40.dp)
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
                        else -> Icons.Default.PlayLesson
                    },
                    contentDescription = null,
                    tint = when {
                        isLocked -> ErrorRed
                        isCompleted -> SuccessGreen
                        else -> MaterialTheme.colorScheme.primary
                    },
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = lesson.coreIdea,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
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
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(
                        text = "اشترك الآن 🔒",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "فتح الدرس",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
