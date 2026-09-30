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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.SubjectEntity
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.components.SectionHeader
import com.example.ui.components.SubscriptionStatusBadge
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isSubscribed by viewModel.isSubscribed.collectAsState()
    val activeSub by viewModel.activeSubscription.collectAsState()
    val subjects by viewModel.allSubjects.collectAsState()
    val completedLessonIds by viewModel.completedLessonIds.collectAsState()
    val totalLessons by viewModel.database.curriculumDao().getLessonCount().collectAsState(initial = 10)

    val progressPercent = if (totalLessons > 0) {
        ((completedLessonIds.size * 100) / totalLessons).coerceIn(0, 100)
    } else 0

    val daysRemaining = remember(activeSub) {
        activeSub?.let {
            val diff = it.endDate - System.currentTimeMillis()
            (diff / (1000 * 60 * 60 * 24)).coerceAtLeast(0)
        } ?: 0L
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_content"),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(NavyPrimary, Color(0xFF1D4ED8), TealAccent)
                            ),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = "أهلاً بك، ${currentUser?.fullName ?: "طالبنا المتميز"} 👋",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "أكاديمية الثالث الثانوي 🇾🇪",
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 19.sp
                                        )
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = AmberSecondary
                                    ) {
                                        Text(
                                            text = "الإصدار 2.0",
                                            color = Color(0xFF78350F),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🎓", fontSize = 26.sp)
                            }
                        }

                        Text(
                            text = "شروحات مبسطة خطوة بخطوة لجميع مواد المنهج الوزاري مع حلول المسائل بالاختيار والذكاء الاصطناعي.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 13.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Progress Bar Mini
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "نسبة تقدمك في المنهج",
                                    style = MaterialTheme.typography.labelMedium.copy(color = Color.White.copy(alpha = 0.85f))
                                )
                                Text(
                                    text = "$progressPercent%",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = AmberLight,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            LinearProgressIndicator(
                                progress = { progressPercent / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(CircleShape),
                                color = AmberLight,
                                trackColor = Color.White.copy(alpha = 0.25f)
                            )
                        }
                    }
                }
            }
        }

        // Subscription Banner
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                SubscriptionStatusBadge(
                    isSubscribed = isSubscribed,
                    planName = activeSub?.planName,
                    daysRemaining = daysRemaining,
                    onClick = { viewModel.navigateTo(Screen.Subscription) }
                )
            }
        }

        // Owner Access Banner / Icon
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                Surface(
                    onClick = { viewModel.navigateTo(Screen.AdminDashboard) },
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFEF3C7),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, AmberSecondary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("owner_home_banner")
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
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(AmberSecondary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "👑", fontSize = 22.sp)
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (currentUser?.role == "ADMIN") "👑 لوحة تحكم المالك (مفتوحة)" else "👑 أيقونة المالك للدخول والتحكم",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF78350F),
                                    fontSize = 15.sp
                                )
                            )
                            Text(
                                text = "دخول المالك لإدارة الأكاديمية والاشتراكات والمحافظ وتوليد الأكواد",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF92400E),
                                    fontSize = 11.sp
                                )
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AmberSecondary
                        ) {
                            Text(
                                text = if (currentUser?.role == "ADMIN") "اللوحة ⚙️" else "دخول 🔐",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Main Grid Navigation Items
        item {
            SectionHeader(
                title = "الأقسام الرئيسية",
                subtitle = "كل ما يحتاجه طالب الشهادة الثانوية للوصول إلى التفوق"
            )

            val gridItems = listOf(
                GridActionItem(
                    title = "📚 المواد الدراسية",
                    desc = "شروحات المنهج والدروس",
                    bgColor = Color(0xFFEFF6FF),
                    textColor = NavyPrimary,
                    screen = Screen.Subjects
                ),
                GridActionItem(
                    title = "🧠 المعلم الذكي",
                    desc = "مدرس يمني يساندك 24/7",
                    bgColor = Color(0xFFFEF3C7),
                    textColor = AmberSecondary,
                    screen = Screen.SmartTutor
                ),
                GridActionItem(
                    title = "💡 خدمة اشرح لي",
                    desc = "شرح مبسط وتشبيهات واقعية",
                    bgColor = Color(0xFFFFFBEB),
                    textColor = Color(0xFFB45309),
                    screen = Screen.ExplainMe
                ),
                GridActionItem(
                    title = "🛡️ حل سؤالي الدقيق",
                    desc = "حل موثوق وتجنب الأخطاء",
                    bgColor = Color(0xFFECFDF5),
                    textColor = TealAccent,
                    screen = Screen.SolveMyQuestion
                ),
                GridActionItem(
                    title = "📝 اختبر نفسك",
                    desc = "نماذج وزارية وتجريبية",
                    bgColor = Color(0xFFFDF2F8),
                    textColor = Color(0xFFBE185D),
                    screen = Screen.Exams
                ),
                GridActionItem(
                    title = "📖 بنك الأسئلة",
                    desc = "تصنيفات واختبار عشوائي",
                    bgColor = Color(0xFFF5F3FF),
                    textColor = Color(0xFF6D28D9),
                    screen = Screen.QuestionBank
                ),
                GridActionItem(
                    title = "🔍 البحث الشامل",
                    desc = "ابحث عن أي قانون أو درس",
                    bgColor = Color(0xFFF1F5F9),
                    textColor = TextPrimaryLight,
                    screen = Screen.Search
                ),
                GridActionItem(
                    title = "⭐ المفضلة",
                    desc = "الدروس والقوانين المحفوظة",
                    bgColor = Color(0xFFFFFBEB),
                    textColor = Color(0xFFB45309),
                    screen = Screen.Favorites
                ),
                GridActionItem(
                    title = "📊 تقدمي الدراسي",
                    desc = "إحصائيات الإنجاز والدرجات",
                    bgColor = Color(0xFFECFEFF),
                    textColor = Color(0xFF0369A1),
                    screen = Screen.Progress
                ),
                GridActionItem(
                    title = "💳 اشتراكي",
                    desc = "المحافظ اليمنية وتفعيل الأكواد",
                    bgColor = CardGold,
                    textColor = Color(0xFF92400E),
                    screen = Screen.Subscription
                ),
                GridActionItem(
                    title = "👤 حسابي",
                    desc = "الملف الشخصي والإعدادات",
                    bgColor = Color(0xFFF8FAFC),
                    textColor = NavyPrimary,
                    screen = Screen.Profile
                ),
                GridActionItem(
                    title = "👑 دخول المالك",
                    desc = "لوحة التحكم والإدارة العامة",
                    bgColor = CardGold,
                    textColor = Color(0xFF78350F),
                    screen = Screen.AdminDashboard
                )
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                gridItems.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowItems.forEach { item ->
                            Card(
                                onClick = { viewModel.navigateTo(item.screen) },
                                colors = CardDefaults.cardColors(containerColor = item.bgColor),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(96.dp)
                                    .testTag("nav_card_${item.screen.javaClass.simpleName}")
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = item.textColor,
                                            fontSize = 14.sp
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = item.desc,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = item.textColor.copy(alpha = 0.8f),
                                            fontSize = 11.sp
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // Subjects Carousel
        item {
            SectionHeader(
                title = "المواد الدراسية (المنهج اليمني)",
                subtitle = "جميع مواد الصف الثالث الثانوي للقسمين العلمي والأدبي",
                actionText = "عرض الكل",
                onActionClick = { viewModel.navigateTo(Screen.Subjects) }
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(subjects) { subject ->
                    SubjectCard(
                        subject = subject,
                        onClick = { viewModel.selectSubject(subject) }
                    )
                }
            }
        }
    }
}

data class GridActionItem(
    val title: String,
    val desc: String,
    val bgColor: Color,
    val textColor: Color,
    val screen: Screen
)

@Composable
fun SubjectCard(
    subject: SubjectEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .width(160.dp)
            .height(130.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    val iconText = when (subject.iconName) {
                        "math" -> "📐"
                        "physics" -> "⚡"
                        "chemistry" -> "🧪"
                        "biology" -> "🧬"
                        "arabic" -> "📖"
                        "english" -> "🔤"
                        "islamic" -> "🕌"
                        "history" -> "🏛️"
                        "geography" -> "🌍"
                        else -> "📚"
                    }
                    Text(text = iconText, fontSize = 18.sp)
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (subject.track == "SCIENTIFIC") Color(0xFFEFF6FF) else Color(0xFFFFFBEB)
                ) {
                    Text(
                        text = if (subject.track == "SCIENTIFIC") "علمي" else if (subject.track == "LITERARY") "أدبي" else "مشترك",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (subject.track == "SCIENTIFIC") NavyPrimary else AmberSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Column {
                Text(
                    text = subject.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subject.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
