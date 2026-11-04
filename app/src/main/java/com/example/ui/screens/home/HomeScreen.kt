package com.example.ui.screens.home

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entities.SubjectEntity
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.components.RaqeebLogoEmblem
import com.example.ui.components.SectionHeader
import com.example.ui.components.SubscriptionStatusBadge
import com.example.ui.components.secretOwnerHoldTrigger
import com.example.ui.screens.auth.OwnerLoginDialog
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val isSubscribed by viewModel.isSubscribed.collectAsState()
    val activeSub by viewModel.activeSubscription.collectAsState()
    val subjects by viewModel.allSubjects.collectAsState()
    val completedLessonIds by viewModel.completedLessonIds.collectAsState()
    val totalLessons by viewModel.database.curriculumDao().getLessonCount().collectAsState(initial = 10)

    var showImagePickerDialog by remember { mutableStateOf(false) }
    var showOwnerLoginDialog by remember { mutableStateOf(false) }

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
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. الترويسة الرئيسية الرسمية للهوية البصرية والترحيب
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(RaqeebDeepNavy, RaqeebRoyalBlue, RaqeebElectricBlue)
                            ),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "رقيب للتعليم الثانوي 🇾🇪",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "«افهم • حل • اختبر • تفوق»",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = AmberLight,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "مرحبًا بك، في رحلتك نحو التفوق",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 12.sp
                                    )
                                )
                            }

                            RaqeebLogoEmblem(
                                size = 48.dp,
                                showBackground = true,
                                modifier = Modifier.secretOwnerHoldTrigger(holdDurationMs = 20_000L) {
                                    showOwnerLoginDialog = true
                                }
                            )
                        }

                        // Welcome & Student Status Pill
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "الطالب: ${currentUser?.fullName ?: "طالبنا المتميز"}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp
                                    )
                                )
                                Text(
                                    text = "الصف الثالث الثانوي",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = RaqeebCyanAccent,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. حالة الاشتراك
        item {
            SubscriptionStatusBadge(
                isSubscribed = isSubscribed,
                planName = activeSub?.planName,
                daysRemaining = daysRemaining,
                onClick = { viewModel.navigateTo(Screen.Subscription) }
            )
        }

        // 2.5 بانر خدمة «لم أفهم؟» التفاعلية بالمنهج اليمني
        item {
            Card(
                onClick = { viewModel.navigateTo(Screen.ExplainMe) },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_ididnotunderstand_banner")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFF59E0B),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("💡", fontSize = 18.sp)
                            }
                        }
                        Column {
                            Text(
                                text = "لم أفهم درساً أو قانوناً؟ اشرح لي الآن",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF78350F)
                                )
                            )
                            Text(
                                text = "شرح مبسط • خطوة بخطوة • لماذا القانون • أسئلة وتدريبات (1-2 دقيقة) ⏱️",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF92400E),
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color(0xFFB45309),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // 3. البطاقات التسع الوظيفية (Responsive Grid)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "الخدمات والأنشطة التعليمية",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                )

                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    val isWideScreen = maxWidth >= 600.dp
                    val columns = if (isWideScreen) 3 else 2

                    val cardsList = listOf(
                        FunctionalCardItem(
                            id = 0,
                            title = "خدمة لم أفهم؟",
                            desc = "شرح مبسط • خطوة بخطوة • لماذا القانون • تدريبات وتشبيهات يمنية ⏱️",
                            icon = Icons.Default.Lightbulb,
                            badgeEmoji = "💡",
                            containerColor = Color(0xFFFEF3C7),
                            iconTintColor = Color(0xFFD97706),
                            onClick = { viewModel.navigateTo(Screen.ExplainMe) }
                        ),
                        FunctionalCardItem(
                            id = 1,
                            title = "المعلم الذكي",
                            desc = "شرح الدروس والإجابة عن استفسارات الطالب باستخدام الذكاء الاصطناعي",
                            icon = Icons.Default.Psychology,
                            badgeEmoji = "🤖",
                            containerColor = Color(0xFFEFF6FF),
                            iconTintColor = Color(0xFF1D4ED8),
                            onClick = { viewModel.navigateTo(Screen.SmartTutor) }
                        ),
                        FunctionalCardItem(
                            id = 2,
                            title = "حل سؤالي",
                            desc = "حلول واضحة ومفصلة للأسئلة مع شرح خطوات الحل",
                            icon = Icons.Default.EditNote,
                            badgeEmoji = "✍️",
                            containerColor = Color(0xFFECFDF5),
                            iconTintColor = Color(0xFF047857),
                            onClick = { viewModel.navigateTo(Screen.SolveMyQuestion) }
                        ),
                        FunctionalCardItem(
                            id = 3,
                            title = "الكتب والملازم",
                            desc = "الكتب والملازم التعليمية لجميع المواد الدراسية",
                            icon = Icons.Default.MenuBook,
                            badgeEmoji = "📚",
                            containerColor = Color(0xFFFEF3C7),
                            iconTintColor = Color(0xFFB45309),
                            onClick = {
                                viewModel.studyMaterialsFilterType.value = "ALL"
                                viewModel.navigateTo(Screen.StudyMaterials)
                            }
                        ),
                        FunctionalCardItem(
                            id = 4,
                            title = "النماذج والملخصات",
                            desc = "نماذج اختبارات وملخصات مركزة للمراجعة",
                            icon = Icons.Default.Assignment,
                            badgeEmoji = "📝",
                            containerColor = Color(0xFFF3E8FF),
                            iconTintColor = Color(0xFF7E22CE),
                            onClick = {
                                viewModel.studyMaterialsFilterType.value = "SUMMARY"
                                viewModel.navigateTo(Screen.StudyMaterials)
                            }
                        ),
                        FunctionalCardItem(
                            id = 5,
                            title = "بنك الأسئلة",
                            desc = "أسئلة متنوعة تساعد الطالب على فهم المواد والتدرب عليها",
                            icon = Icons.Default.FormatListBulleted,
                            badgeEmoji = "📖",
                            containerColor = Color(0xFFF1F5F9),
                            iconTintColor = Color(0xFF334155),
                            onClick = { viewModel.navigateTo(Screen.QuestionBank) }
                        ),
                        FunctionalCardItem(
                            id = 6,
                            title = "اختبر نفسك",
                            desc = "اختبارات تفاعلية لتقييم مستوى الطالب ومعرفة نقاط القوة والضعف",
                            icon = Icons.Default.TrackChanges,
                            badgeEmoji = "🎯",
                            containerColor = Color(0xFFFDF2F8),
                            iconTintColor = Color(0xFFBE185D),
                            onClick = { viewModel.navigateTo(Screen.Exams) }
                        ),
                        FunctionalCardItem(
                            id = 7,
                            title = "الحاسبة العلمية",
                            desc = "أداة علمية لحل المسائل الرياضية بسرعة ودقة",
                            icon = Icons.Default.Calculate,
                            badgeEmoji = "🧮",
                            containerColor = Color(0xFFF0FDF4),
                            iconTintColor = Color(0xFF15803D),
                            onClick = { viewModel.navigateTo(Screen.ScientificCalculator) }
                        ),
                        FunctionalCardItem(
                            id = 8,
                            title = "اختيار صورة",
                            desc = "صوّر سؤالك أو اختر صورة من جهازك",
                            icon = Icons.Default.AddPhotoAlternate,
                            badgeEmoji = "📷",
                            containerColor = Color(0xFFE0F2FE),
                            iconTintColor = Color(0xFF0284C7),
                            onClick = { showImagePickerDialog = true }
                        )
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // First 8 cards in grid of 2 (mobile) or 3 (tablet)
                        cardsList.chunked(columns).forEach { rowItems ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                rowItems.forEach { item ->
                                    Box(modifier = Modifier.weight(1f)) {
                                        FunctionalCard(item = item)
                                    }
                                }
                                if (rowItems.size < columns) {
                                    repeat(columns - rowItems.size) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }

                        // 9. بطاقة تقدمي الدراسي (بطاقة عريضة مميزة)
                        Card(
                            onClick = { viewModel.navigateTo(Screen.Progress) },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F9FF)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                            border = BorderStroke(1.dp, Color(0xFFBAE6FD)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("functional_card_progress")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF0284C7).copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.School,
                                                contentDescription = null,
                                                tint = Color(0xFF0284C7),
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                        Column {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Text(
                                                    text = "تقدمي الدراسي",
                                                    style = MaterialTheme.typography.titleSmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF0369A1),
                                                        fontSize = 14.sp
                                                    )
                                                )
                                                Text(text = "📊", fontSize = 13.sp)
                                            }
                                            Text(
                                                text = "تابع مستواك ودرجاتك وتطورك باستمرار",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = Color(0xFF0369A1).copy(alpha = 0.8f),
                                                    fontSize = 11.sp
                                                )
                                            )
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFF0284C7)
                                    ) {
                                        Text(
                                            text = "$progressPercent%",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                LinearProgressIndicator(
                                    progress = { progressPercent / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(CircleShape),
                                    color = Color(0xFF0284C7),
                                    trackColor = Color(0xFFBAE6FD)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. المواد الدراسية (المنهج اليمني)
        item {
            SectionHeader(
                title = "المواد الدراسية (المنهج اليمني)",
                subtitle = "جميع مواد الصف الثالث الثانوي للقسمين العلمي والأدبي",
                actionText = "عرض الكل",
                onActionClick = { viewModel.navigateTo(Screen.Subjects) }
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(subjects) { subject ->
                    SubjectCard(
                        subject = subject,
                        onClick = { viewModel.selectSubject(subject) }
                    )
                }
            }
        }

        // 5. قسم وصول المالك / الإدارة (يظهر حصرياً للمالك عند تسجيل دخوله فقط)
        if (currentUser?.role == "ADMIN") {
            item {
                Surface(
                    onClick = { viewModel.navigateTo(Screen.AdminDashboard) },
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFFEF3C7),
                    border = BorderStroke(1.dp, AmberSecondary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("owner_home_banner")
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(AmberSecondary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "👑", fontSize = 18.sp)
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "👑 لوحة تحكم المالك (مفتوحة)",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF78350F),
                                    fontSize = 13.sp
                                )
                            )
                            Text(
                                text = "إدارة المنصة، الاشتراكات، المحافظ، ورفع الملازم والكتب",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF92400E),
                                    fontSize = 11.sp
                                )
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = AmberSecondary
                        ) {
                            Text(
                                text = "اللوحة ⚙️",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialog: اختيار صورة والتحليل بالذكاء الاصطناعي (البطاقة 8)
    if (showImagePickerDialog) {
        ImagePickerAndAiSolverDialog(
            viewModel = viewModel,
            onDismiss = { showImagePickerDialog = false },
            onSolved = {
                showImagePickerDialog = false
                viewModel.navigateTo(Screen.SolveMyQuestion)
            }
        )
    }

    // Dialog: تسجيل دخول المالك بالبيانات الأربعة السرية (يظهر عند الضغط 20 ثانية على الشعار)
    if (showOwnerLoginDialog) {
        OwnerLoginDialog(
            viewModel = viewModel,
            onDismiss = { showOwnerLoginDialog = false },
            onSuccess = {
                showOwnerLoginDialog = false
                viewModel.navigateTo(Screen.AdminDashboard)
            }
        )
    }
}

data class FunctionalCardItem(
    val id: Int,
    val title: String,
    val desc: String,
    val icon: ImageVector,
    val badgeEmoji: String,
    val containerColor: Color,
    val iconTintColor: Color,
    val onClick: () -> Unit
)

@Composable
fun FunctionalCard(
    item: FunctionalCardItem,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = item.onClick,
        colors = CardDefaults.cardColors(containerColor = item.containerColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        border = BorderStroke(1.dp, item.iconTintColor.copy(alpha = 0.15f)),
        modifier = modifier
            .fillMaxWidth()
            .height(125.dp)
            .testTag("functional_card_${item.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
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
                        .background(item.iconTintColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = item.iconTintColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = item.badgeEmoji,
                    fontSize = 15.sp
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = item.iconTintColor,
                        fontSize = 13.5.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.desc,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = item.iconTintColor.copy(alpha = 0.85f),
                        fontSize = 10.5.sp,
                        lineHeight = 14.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun ImagePickerAndAiSolverDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit,
    onSolved: () -> Unit
) {
    val context = LocalContext.current
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var selectedSubject by remember { mutableStateOf("الرياضيات") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val isSolving by viewModel.isSolving.collectAsState()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val bmp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
                selectedBitmap = bmp
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "تعذر قراءة الصورة من الجهاز"
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bmp: Bitmap? ->
        if (bmp != null) {
            selectedBitmap = bmp
            errorMessage = null
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE0F2FE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Color(0xFF0284C7))
                        }
                        Column {
                            Text(
                                text = "اختيار صورة السؤال 📷",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "صوّر سؤالك أو اختره ليتم حله بالذكاء الاصطناعي",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                // Image Preview or Placeholder
                if (selectedBitmap != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.05f)),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.foundation.Image(
                            bitmap = selectedBitmap!!.asImageBitmap(),
                            contentDescription = "معاينة الصورة",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Clear Button
                        IconButton(
                            onClick = { selectedBitmap = null },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(6.dp)
                                .size(28.dp)
                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "حذف الصورة", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF8FAFC))
                            .clickable { galleryLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = RaqeebRoyalBlue, modifier = Modifier.size(36.dp))
                            Text(
                                text = "اضغط لاختيار صورة السؤال من هاتفك",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = NavyPrimary
                            )
                            Text(
                                text = "يدعم JPG, PNG ومسائل الرياضيات والفيزياء",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                // Action buttons: Camera & Gallery
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { cameraLauncher.launch(null) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("التقاط كاميرا", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("معرض الصور", fontSize = 11.sp)
                    }
                }

                // Subject Selector
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "مادة المسألة (المنهج اليمني):",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(listOf("الرياضيات", "الفيزياء", "الكيمياء", "الأحياء", "اللغة العربية", "اللغة الإنجليزية")) { subj ->
                            FilterChip(
                                selected = selectedSubject == subj,
                                onClick = { selectedSubject = subj },
                                label = { Text(subj, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Send to AI Button
                Button(
                    onClick = {
                        val bmp = selectedBitmap
                        if (bmp == null) {
                            errorMessage = "يرجى التقاط صورة أو اختيارها أولاً"
                            return@Button
                        }
                        viewModel.solverSelectedSubject.value = selectedSubject
                        viewModel.solverImageBitmap.value = bmp
                        viewModel.solveQuestion()
                        onSolved()
                    },
                    enabled = selectedBitmap != null && !isSolving,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RaqeebRoyalBlue)
                ) {
                    if (isSolving) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("جاري قراءة وتحليل السؤال بالذكاء الاصطناعي...")
                    } else {
                        Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("تحليل وحل المسألة الآن 🚀", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SubjectCard(
    subject: SubjectEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = modifier
            .width(140.dp)
            .height(115.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
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
                    Text(text = iconText, fontSize = 16.sp)
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (subject.track == "SCIENTIFIC") Color(0xFFEFF6FF) else Color(0xFFFFFBEB)
                ) {
                    Text(
                        text = if (subject.track == "SCIENTIFIC") "علمي" else if (subject.track == "LITERARY") "أدبي" else "مشترك",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (subject.track == "SCIENTIFIC") NavyPrimary else AmberSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Column {
                Text(
                    text = subject.name,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subject.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
