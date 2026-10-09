package com.example.ui.screens.home

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.SubjectEntity
import com.example.ui.MainViewModel
import com.example.ui.Screen

data class GridActionItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    val screen: Screen
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigate: (Screen) -> Unit
) {
    val subjects by viewModel.allSubjects.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val isSubscribed by viewModel.isSubscribed.collectAsState()
    val booklets by viewModel.allDocumentBooklets.collectAsState()

    val actionItems = listOf(
        GridActionItem(
            title = "الملازم والملفات",
            subtitle = "PDF & Word",
            icon = Icons.Default.Description,
            color = Color(0xFFD97706),
            screen = Screen.PdfViewer
        ),
        GridActionItem(
            title = "حل سؤالي",
            subtitle = "حل حتى 50 سؤالاً",
            icon = Icons.Default.AutoAwesome,
            color = Color(0xFF2563EB),
            screen = Screen.SolveMyQuestion
        ),
        GridActionItem(
            title = "المواد والدروس",
            subtitle = "المنهج الوزاري",
            icon = Icons.Default.MenuBook,
            color = Color(0xFF0D9488),
            screen = Screen.Subjects
        ),
        GridActionItem(
            title = "الاختبارات الوزارية",
            subtitle = "أتمتة وزارية",
            icon = Icons.Default.Assignment,
            color = Color(0xFF7C3AED),
            screen = Screen.Exams
        ),
        GridActionItem(
            title = "اشرح لي الدرس",
            subtitle = "تبسيط المفاهيم",
            icon = Icons.Default.Lightbulb,
            color = Color(0xFFEA580C),
            screen = Screen.ExplainMe
        ),
        GridActionItem(
            title = "اسأل أستاذي الذكي",
            subtitle = "نص • فيديو • رسم • PDF",
            icon = Icons.Default.School,
            color = Color(0xFF0284C7),
            screen = Screen.SmartTutor
        ),
        GridActionItem(
            title = "الآلة الحاسبة",
            subtitle = "علمية ومصفوفات",
            icon = Icons.Default.Calculate,
            color = Color(0xFF4F46E5),
            screen = Screen.ScientificCalculator
        ),
        GridActionItem(
            title = "لوحة الإدارة",
            subtitle = "صلاحيات المالك",
            icon = Icons.Default.AdminPanelSettings,
            color = Color(0xFFDC2626),
            screen = Screen.AdminDashboard
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
    ) {
        // Hero Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "أهلاً بك يا ${currentUser?.fullName ?: "طالبنا المتفوق"}! 👋",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "الصف الثالث الثانوي العلمي - المنهج اليمني الوزاري",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSubscribed) Color(0xFF10B981) else MaterialTheme.colorScheme.primary
                        ) {
                            Text(
                                text = if (isSubscribed) "مشترك نشط ✓" else "طالب رسمي",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                color = Color.White,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Direct Quick Action: Ask Smart Teacher
                    Button(
                        onClick = { onNavigate(Screen.SmartTutor) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                    ) {
                        Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("اسأل أستاذي الذكي (نص • فيديو • رسم • PDF)", fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    // Direct Quick Action for PDF Booklets
                    OutlinedButton(
                        onClick = { onNavigate(Screen.PdfViewer) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("تصفح وتحميل ملازم الدروس (PDF & Word)", fontWeight = FontWeight.Bold)
                    }

                    if (currentUser == null) {
                        FilledTonalButton(
                            onClick = { onNavigate(Screen.Login) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("تسجيل الدخول / إنشاء حساب جديد واشتراك", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section Title: Quick Actions Grid
        item {
            Text(
                text = "الخدمات السريعة",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // 2x4 Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                for (row in actionItems.chunked(2)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (item in row) {
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(96.dp)
                                    .clickable { onNavigate(item.screen) },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(item.color.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = null,
                                            tint = item.color,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = item.title,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = item.subtitle,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                            maxLines = 1
                                            )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section Title: Booklets Highlight
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "أحدث الملازم والملفات الدراسية",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { onNavigate(Screen.PdfViewer) }) {
                    Text("عرض الكل (${booklets.size})")
                }
            }
        }

        // Horizontal Booklets Preview
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(booklets.take(5)) { booklet ->
                    Card(
                        modifier = Modifier
                            .width(220.dp)
                            .clickable { viewModel.selectBooklet(booklet) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (booklet.fileType == "PDF") Color(0xFFFEF3C7) else Color(0xFFDBEAFE)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (booklet.fileType == "PDF") Color(0xFFDC2626) else Color(0xFF2563EB)
                                ) {
                                    Text(
                                        text = booklet.fileType,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = booklet.fileSize,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.DarkGray
                                )
                            }
                            Text(
                                text = booklet.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = booklet.subjectName,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }

        // Section Title: Subjects
        item {
            Text(
                text = "المواد الدراسية المقررة",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(subjects) { subject ->
            SubjectCard(
                subject = subject,
                onClick = { viewModel.selectSubject(subject) }
            )
        }
    }
}

@Composable
fun SubjectCard(
    subject: SubjectEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = subject.iconRes,
                    fontSize = 24.sp
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subject.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subject.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
