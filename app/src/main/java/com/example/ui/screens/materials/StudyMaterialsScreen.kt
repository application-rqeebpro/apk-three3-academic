package com.example.ui.screens.materials

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.theme.*

@Composable
fun StudyMaterialsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val materials by viewModel.allStudyMaterials.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    var selectedTypeFilter by remember { mutableStateOf(viewModel.studyMaterialsFilterType.value) }
    var selectedSubjectFilter by remember { mutableStateOf("ALL") }

    val filtered = remember(materials, selectedTypeFilter, selectedSubjectFilter) {
        materials.filter { item ->
            val matchType = (selectedTypeFilter == "ALL" || item.type == selectedTypeFilter)
            val matchSubj = (selectedSubjectFilter == "ALL" || item.subjectName == selectedSubjectFilter)
            matchType && matchSubj
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("study_materials_student_screen"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Compact Header & Quick Upload if Admin
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "الكتب والملازم والملخصات 📑",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = NavyPrimary,
                        fontSize = 16.sp
                    )
                )
                Text(
                    text = "منهج الصف الثالث الثانوي المعتمد في الجمهورية اليمنية",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                )
            }

            if (currentUser?.role == "ADMIN") {
                Surface(
                    onClick = { viewModel.navigateTo(Screen.AdminStudyMaterials) },
                    shape = RoundedCornerShape(8.dp),
                    color = AmberSecondary
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Text("رفع جديد 👑", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // Horizontal scrolling Type Filter Bar (No overflow, compact)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(vertical = 2.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedTypeFilter == "ALL",
                    onClick = { selectedTypeFilter = "ALL" },
                    label = { Text("الكل (${materials.size})", fontSize = 11.sp) }
                )
            }
            item {
                FilterChip(
                    selected = selectedTypeFilter == "BOOKLET",
                    onClick = { selectedTypeFilter = "BOOKLET" },
                    label = { Text("ملازم 📑", fontSize = 11.sp) }
                )
            }
            item {
                FilterChip(
                    selected = selectedTypeFilter == "BOOK",
                    onClick = { selectedTypeFilter = "BOOK" },
                    label = { Text("كتب مدرسية 📕", fontSize = 11.sp) }
                )
            }
            item {
                FilterChip(
                    selected = selectedTypeFilter == "SUMMARY",
                    onClick = { selectedTypeFilter = "SUMMARY" },
                    label = { Text("ملخصات 📝", fontSize = 11.sp) }
                )
            }
            item {
                FilterChip(
                    selected = selectedTypeFilter == "EXAM_SHEET",
                    onClick = { selectedTypeFilter = "EXAM_SHEET" },
                    label = { Text("نماذج اختبارات 📋", fontSize = 11.sp) }
                )
            }
        }

        // Horizontal scrolling Subject Filter Bar
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(vertical = 2.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedSubjectFilter == "ALL",
                    onClick = { selectedSubjectFilter = "ALL" },
                    label = { Text("جميع المواد", fontSize = 10.sp) }
                )
            }
            items(listOf("الرياضيات", "الفيزياء", "الكيمياء", "الأحياء", "اللغة العربية", "اللغة الإنجليزية", "القرآن الكريم", "التربية الإسلامية")) { subj ->
                FilterChip(
                    selected = selectedSubjectFilter == subj,
                    onClick = { selectedSubjectFilter = subj },
                    label = { Text(subj, fontSize = 10.sp) }
                )
            }
        }

        // Materials List
        if (filtered.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "لا توجد ملفات متوفرة في هذا التصنيف حالياً.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filtered) { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        when (item.type) {
                                            "BOOK" -> Color(0xFFEFF6FF)
                                            "BOOKLET" -> Color(0xFFFEF3C7)
                                            "SUMMARY" -> Color(0xFFECFDF5)
                                            else -> Color(0xFFF3E8FF)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = when (item.type) {
                                        "BOOK" -> "📕"
                                        "BOOKLET" -> "📑"
                                        "SUMMARY" -> "📝"
                                        else -> "📋"
                                    },
                                    fontSize = 24.sp
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = NavyPrimary,
                                        fontSize = 14.sp
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Text(
                                            text = item.subjectName,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = when (item.type) {
                                            "BOOK" -> "كتاب وزاري"
                                            "BOOKLET" -> "ملزمة دراسية"
                                            "SUMMARY" -> "ملخص مركز"
                                            else -> "نموذج وزاري"
                                        },
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                                if (item.description.isNotBlank()) {
                                    Text(
                                        text = item.description,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color.Gray,
                                            fontSize = 11.sp
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                // Open Button
                                FilledTonalButton(
                                    onClick = {
                                        if (!item.fileUriOrPath.isNullOrBlank()) {
                                            try {
                                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                                    setDataAndType(Uri.parse(item.fileUriOrPath), "application/pdf")
                                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                }
                                                context.startActivity(intent)
                                            } catch (e: Exception) {
                                                viewModel.toastMessage.value = "فتح الملف: ${item.title}"
                                            }
                                        } else {
                                            viewModel.toastMessage.value = "فتح الملف: ${item.title}"
                                        }
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("عرض", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                // Download Button
                                OutlinedButton(
                                    onClick = {
                                        viewModel.toastMessage.value = "جاري تحميل ${item.title}..."
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Download, contentDescription = "تحميل", modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
