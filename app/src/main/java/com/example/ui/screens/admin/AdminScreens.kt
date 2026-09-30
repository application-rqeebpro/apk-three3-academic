package com.example.ui.screens.admin

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.*
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.components.copyToClipboard
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

// -------------------------------------------------------------
// MAIN ADMIN DASHBOARD
// -------------------------------------------------------------
@Composable
fun AdminDashboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val totalStudents by viewModel.adminStudentCount.collectAsState()
    val activeStudents by viewModel.adminActiveStudentCount.collectAsState()
    val activeSubs by viewModel.adminActiveSubCount.collectAsState()
    val expiredSubs by viewModel.adminExpiredSubCount.collectAsState()
    val pendingPayments by viewModel.adminPendingPaymentCount.collectAsState()
    val unusedCodes by viewModel.adminUnusedCodeCount.collectAsState()
    val usedCodes by viewModel.adminUsedCodeCount.collectAsState()
    val subjectCount by viewModel.database.curriculumDao().getSubjectCount().collectAsState(initial = 9)
    val lessonCount by viewModel.database.curriculumDao().getLessonCount().collectAsState(initial = 10)
    val examCount by viewModel.database.examDao().getExamCount().collectAsState(initial = 4)

    var adminPasswordInput by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf<String?>(null) }

    if (currentUser?.role != "ADMIN") {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(20.dp)
                .testTag("admin_login_gate"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFEF3C7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "👑", fontSize = 32.sp)
                    }

                    Text(
                        text = "بوابة دخول المالك والتحكم",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                    )

                    Text(
                        text = "هذا القسم مخصص لمالك وإدارة أكاديمية الثالث الثانوي اليمني للتحكم بالاشتراكات وتأكيد المحافظ وإصدار الأكواد والدروس.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    )

                    OutlinedTextField(
                        value = adminPasswordInput,
                        onValueChange = { adminPasswordInput = it },
                        label = { Text("كلمة مرور المالك") },
                        placeholder = { Text("أدخل كلمة مرور المالك السرية") },
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    if (loginError != null) {
                        Text(
                            text = loginError ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Button(
                        onClick = {
                            if (adminPasswordInput.isNotBlank()) {
                                viewModel.login(
                                    phoneOrEmail = "782916997",
                                    pass = adminPasswordInput,
                                    onError = { loginError = it }
                                ) {
                                    loginError = null
                                }
                            } else {
                                loginError = "يرجى إدخال كلمة المرور"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(text = "دخول إلى لوحة التحكم 🔐", fontWeight = FontWeight.Bold)
                    }

                    TextButton(onClick = { viewModel.navigateTo(Screen.Home) }) {
                        Text("العودة للصفحة الرئيسية")
                    }
                }
            }
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("admin_dashboard_content"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Admin Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "👑", fontSize = 26.sp)
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "لوحة تحكم المالك",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Color.White)
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
                            Text(
                                text = "مرحباً بك يا ${currentUser?.fullName} • 782916997",
                                style = MaterialTheme.typography.bodySmall.copy(color = AmberLight, fontWeight = FontWeight.Bold)
                            )
                        }

                        OutlinedButton(
                            onClick = { viewModel.logout() },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("خروج", fontSize = 11.sp)
                        }
                    }

                    Text(
                        text = "إدارة الطلاب، تفعيل الاشتراكات، مراجعة المحافظ (ون كاش 192737631 • جيب 913554 • جوالي 782916997)، وإصدار الأكواد",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.85f))
                    )
                }
            }
        }

        // Metrics Grid
        item {
            Text(
                text = "📊 إحصائيات المنظومة:",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            val metrics = listOf(
                MetricData("إجمالي الطلاب", "$totalStudents", Color(0xFFEFF6FF), NavyPrimary),
                MetricData("الطلاب النشطون", "$activeStudents", Color(0xFFDCFCE7), SuccessGreen),
                MetricData("الاشتراكات النشطة", "$activeSubs", Color(0xFFFEF3C7), AmberSecondary),
                MetricData("الاشتراكات المنتهية", "$expiredSubs", Color(0xFFFEE2E2), ErrorRed),
                MetricData("طلبات دفع معلقة", "$pendingPayments", if (pendingPayments > 0) Color(0xFFFEE2E2) else Color(0xFFF1F5F9), if (pendingPayments > 0) ErrorRed else TextSecondaryLight),
                MetricData("الأكواد غير المستخدمة", "$unusedCodes", Color(0xFFF5F3FF), Color(0xFF6D28D9)),
                MetricData("الأكواد المستخدمة", "$usedCodes", Color(0xFFECFDF5), TealAccent),
                MetricData("المواد والدروس", "$subjectCount مادة / $lessonCount درس", Color(0xFFF8FAFC), TextPrimaryLight)
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                metrics.chunked(2).forEach { rowMetrics ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowMetrics.forEach { m ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = m.bgColor),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(80.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(10.dp),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = m.label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 11.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = m.value,
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = m.textColor,
                                            fontSize = 18.sp
                                        )
                                    )
                                }
                            }
                        }
                        if (rowMetrics.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // Admin Management Modules Navigation
        item {
            Text(
                text = "الأقسام الإدارية:",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            val modules = listOf(
                AdminModuleItem("إدارة الطلاب وتفعيل الاشتراكات", "عرض وتعديل وتفعيل وتمديد الاشتراكات", Icons.Default.People, Screen.AdminStudents),
                AdminModuleItem("طلبات الدفع بالمحافظ الإلكترونية", "مراجعة إيصالات جيب وجوالي ون كاش والقبول/الرفض", Icons.Default.Payment, Screen.AdminPayments),
                AdminModuleItem("نظام أكواد الاشتراك", "إنشاء وتوليد وربط وإلغاء أكواد التفعيل", Icons.Default.VpnKey, Screen.AdminCodes),
                AdminModuleItem("إدارة المناهج والدروس", "إضافة وتعديل وحذف المواد والدروس والشروحات", Icons.Default.MenuBook, Screen.AdminContent),
                AdminModuleItem("مركز الإشعارات الوزارية", "إرسال رسائل وتنبيهات لجميع الطلاب أو فئات محددة", Icons.Default.Campaign, Screen.AdminNotifications),
                AdminModuleItem("سجل العمليات الإدارية", "سجل تدقيق كامل لكافة الأنشطة مع التواريخ", Icons.Default.History, Screen.AdminLogs)
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                modules.forEach { mod ->
                    Card(
                        onClick = { viewModel.navigateTo(mod.screen) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = mod.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = mod.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(
                                    text = mod.desc,
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }

                            Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

data class MetricData(val label: String, val value: String, val bgColor: Color, val textColor: Color)
data class AdminModuleItem(val title: String, val desc: String, val icon: ImageVector, val screen: Screen)

// -------------------------------------------------------------
// ADMIN STUDENTS MANAGEMENT
// -------------------------------------------------------------
@Composable
fun AdminStudentsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val students by viewModel.adminAllUsers.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    var selectedStudentForDirectActivation by remember { mutableStateOf<UserEntity?>(null) }
    var selectedStudentForExtend by remember { mutableStateOf<UserEntity?>(null) }

    val filtered = remember(students, searchQuery) {
        students.filter {
            it.fullName.contains(searchQuery, ignoreCase = true) ||
            it.phoneOrEmail.contains(searchQuery, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("admin_students_content"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "إدارة الطلاب (${filtered.size}) 👥",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("بحث بالاسم أو رقم الهاتف...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }

        items(filtered) { student ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
                        Column {
                            Text(text = student.fullName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(
                                text = "الهاتف: ${student.phoneOrEmail} • ${student.track}",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (student.isActive) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
                        ) {
                            Text(
                                text = if (student.role == "ADMIN") "مدير" else if (student.isActive) "نشط" else "معطل",
                                color = if (student.isActive) SuccessGreen else ErrorRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Admin Action Buttons for this student
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { selectedStudentForDirectActivation = student },
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "تفعيل اشتراك ⚡", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { selectedStudentForExtend = student },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "تمديد الاشتراك ➕", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    // Direct Activation Dialog
    if (selectedStudentForDirectActivation != null) {
        val s = selectedStudentForDirectActivation!!
        AlertDialog(
            onDismissRequest = { selectedStudentForDirectActivation = null },
            title = { Text("تفعيل اشتراك مباشر للطالب") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("الطالب: ${s.fullName}")
                    Text("اختر خطة التفعيل الفوري:")
                    val plans = listOf(
                        Triple("3 أشهر", 3, 12000),
                        Triple("6 أشهر", 6, 24000),
                        Triple("سنة كاملة", 12, 30000)
                    )
                    plans.forEach { (name, months, price) ->
                        Button(
                            onClick = {
                                viewModel.adminDirectActivate(s.id, s.fullName, months, name, price)
                                selectedStudentForDirectActivation = null
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("$name — $price ريال")
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { selectedStudentForDirectActivation = null }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Extend Subscription Dialog
    if (selectedStudentForExtend != null) {
        val s = selectedStudentForExtend!!
        var customDays by remember { mutableStateOf(30) }

        AlertDialog(
            onDismissRequest = { selectedStudentForExtend = null },
            title = { Text("تمديد اشتراك الطالب") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("الطالب: ${s.fullName}")
                    Text("اختر مدة التمديد التي ستضاف لتاريخ الانتهاء:")

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(selected = customDays == 30, onClick = { customDays = 30 }, label = { Text("شهر") })
                        FilterChip(selected = customDays == 90, onClick = { customDays = 90 }, label = { Text("3 أشهر") })
                        FilterChip(selected = customDays == 180, onClick = { customDays = 180 }, label = { Text("6 أشهر") })
                        FilterChip(selected = customDays == 365, onClick = { customDays = 365 }, label = { Text("سنة") })
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.adminExtendSubscription(s.id, s.fullName, customDays)
                        selectedStudentForExtend = null
                    }
                ) {
                    Text("تأكيد التمديد")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedStudentForExtend = null }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

// -------------------------------------------------------------
// ADMIN PAYMENTS MANAGEMENT
// -------------------------------------------------------------
@Composable
fun AdminPaymentsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val payments by viewModel.adminAllPayments.collectAsState()
    var selectedFilter by remember { mutableStateOf("PENDING") } // "PENDING", "APPROVED", "REJECTED", "ALL"

    var rejectingPaymentId by remember { mutableStateOf<Long?>(null) }
    var rejectionReason by remember { mutableStateOf("") }

    val filtered = remember(payments, selectedFilter) {
        if (selectedFilter == "ALL") payments else payments.filter { it.status == selectedFilter }
    }

    val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("admin_payments_content"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "طلبات الدفع بالمحافظ الإلكترونية 💳",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = selectedFilter == "PENDING", onClick = { selectedFilter = "PENDING" }, label = { Text("قيد المراجعة") })
                FilterChip(selected = selectedFilter == "APPROVED", onClick = { selectedFilter = "APPROVED" }, label = { Text("مقبول") })
                FilterChip(selected = selectedFilter == "REJECTED", onClick = { selectedFilter = "REJECTED" }, label = { Text("مرفوض") })
                FilterChip(selected = selectedFilter == "ALL", onClick = { selectedFilter = "ALL" }, label = { Text("الكل") })
            }
        }

        if (filtered.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                    Text("لا توجد طلبات في هذا القسم حالياً.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        items(filtered) { pay ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
                        Text(text = pay.studentName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (pay.status) {
                                "APPROVED" -> Color(0xFFDCFCE7)
                                "REJECTED" -> Color(0xFFFEE2E2)
                                else -> Color(0xFFFEF3C7)
                            }
                        ) {
                            Text(
                                text = when (pay.status) {
                                    "APPROVED" -> "مقبول ✅"
                                    "REJECTED" -> "مرفوض ❌"
                                    else -> "قيد المراجعة ⏳"
                                },
                                fontWeight = FontWeight.Bold,
                                color = when (pay.status) {
                                    "APPROVED" -> SuccessGreen
                                    "REJECTED" -> ErrorRed
                                    else -> AmberSecondary
                                },
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(text = "الخطة: ${pay.planName} • المبلغ: ${pay.amountYmr} ريال يمني")
                    Text(text = "المحفظة: ${pay.walletName} • هاتف الطالب: ${pay.studentPhone}")
                    Text(text = "رقم عملية التحويل: ${pay.transferRefNumber}", fontWeight = FontWeight.Bold, color = NavyPrimary)
                    Text(text = "التاريخ: ${dateFormat.format(Date(pay.createdAt))}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    if (pay.receiptImageUri != null) {
                        Text(text = "ملاحظة الطالب: ${pay.receiptImageUri}", style = MaterialTheme.typography.bodySmall)
                    }

                    if (pay.status == "REJECTED" && pay.rejectionReason != null) {
                        Text(text = "سبب الرفض: ${pay.rejectionReason}", color = ErrorRed, fontWeight = FontWeight.SemiBold)
                    }

                    // Admin Actions if Pending
                    if (pay.status == "PENDING") {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.adminReviewPayment(pay.id, approve = true, createCode = false) },
                                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "قبول وتفعيل مباشر", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { viewModel.adminReviewPayment(pay.id, approve = true, createCode = true) },
                                colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "قبول وتوليد كود", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    rejectingPaymentId = pay.id
                                    rejectionReason = "رقم الحوالة غير صحيح أو لم يصل المبلغ إلى الحساب"
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "رفض الطلب", color = ErrorRed, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Rejection Dialog with reason
    if (rejectingPaymentId != null) {
        AlertDialog(
            onDismissRequest = { rejectingPaymentId = null },
            title = { Text("رفض طلب الدفع") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("اكتب سبب الرفض ليظهر للطالب في الإشعارات:")
                    OutlinedTextField(
                        value = rejectionReason,
                        onValueChange = { rejectionReason = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val id = rejectingPaymentId!!
                        rejectingPaymentId = null
                        viewModel.adminReviewPayment(id, approve = false, reason = rejectionReason)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("تأكيد الرفض")
                }
            },
            dismissButton = {
                TextButton(onClick = { rejectingPaymentId = null }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

// -------------------------------------------------------------
// ADMIN CODES MANAGEMENT
// -------------------------------------------------------------
@Composable
fun AdminCodesScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val codes by viewModel.adminAllCodes.collectAsState()
    var showGenerateDialog by remember { mutableStateOf(false) }

    val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("admin_codes_content"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "أكواد الاشتراكات (${codes.size}) 🔑",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                Button(
                    onClick = { showGenerateDialog = true },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إنشاء كود جديد")
                }
            }
        }

        items(codes) { codeEntity ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(14.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = codeEntity.code,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = NavyPrimary,
                                fontSize = 18.sp
                            )
                        )

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (codeEntity.status) {
                                "UNUSED" -> Color(0xFFDCFCE7)
                                "USED" -> Color(0xFFEFF6FF)
                                else -> Color(0xFFFEE2E2)
                            }
                        ) {
                            Text(
                                text = when (codeEntity.status) {
                                    "UNUSED" -> "غير مستخدم 🟢"
                                    "USED" -> "مستخدم ⚪"
                                    else -> "ملغي 🔴"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(text = "الخطة: ${codeEntity.planName} • السعر: ${codeEntity.priceYmr} ريال")
                    if (codeEntity.tiedStudentPhone != null) {
                        Text(text = "مرتبط برقم الطالب: ${codeEntity.tiedStudentPhone}", fontWeight = FontWeight.Bold, color = AmberSecondary)
                    } else {
                        Text(text = "نوع الكود: عام (يمكن لأي طالب استخدامه مرة واحدة)")
                    }

                    if (codeEntity.status == "USED") {
                        Text(
                            text = "استخدمه الطالب: ${codeEntity.usedByStudentName ?: "طالب"} بتاريخ: ${codeEntity.usedAt?.let { dateFormat.format(Date(it)) }}",
                            color = SuccessGreen,
                            fontSize = 12.sp
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { copyToClipboard(context, codeEntity.code, "كود الاشتراك") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("نسخ الكود", fontSize = 12.sp)
                        }

                        if (codeEntity.status == "UNUSED") {
                            Button(
                                onClick = { viewModel.adminCancelCode(codeEntity.id, codeEntity.code) },
                                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("إلغاء الكود", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Generate Code Dialog
    if (showGenerateDialog) {
        var selectedPlanName by remember { mutableStateOf("اشتراك 3 أشهر") }
        var selectedMonths by remember { mutableStateOf(3) }
        var priceYmr by remember { mutableStateOf(12000) }
        var tiedPhoneInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showGenerateDialog = false },
            title = { Text("إنشاء كود اشتراك جديد") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("اختر الخطة:")
                    val plans = listOf(
                        Triple("اشتراك 3 أشهر", 3, 12000),
                        Triple("اشتراك 6 أشهر", 6, 24000),
                        Triple("اشتراك سنوي (12 شهر)", 12, 30000)
                    )
                    plans.forEach { (name, m, p) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedPlanName = name
                                    selectedMonths = m
                                    priceYmr = p
                                }
                        ) {
                            RadioButton(
                                selected = selectedPlanName == name,
                                onClick = {
                                    selectedPlanName = name
                                    selectedMonths = m
                                    priceYmr = p
                                }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(name)
                        }
                    }

                    OutlinedTextField(
                        value = tiedPhoneInput,
                        onValueChange = { tiedPhoneInput = it },
                        label = { Text("رقم هاتف الطالب لربط الكود به (اختياري)") },
                        placeholder = { Text("اتركه فارغاً لجعله كوداً عاماً") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.adminGenerateCode(
                            months = selectedMonths,
                            planName = selectedPlanName,
                            price = priceYmr,
                            tiedPhone = tiedPhoneInput.takeIf { it.isNotBlank() }
                        ) {
                            showGenerateDialog = false
                        }
                    }
                ) {
                    Text("توليد الكود الآن")
                }
            },
            dismissButton = {
                TextButton(onClick = { showGenerateDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

// -------------------------------------------------------------
// ADMIN CONTENT MANAGEMENT
// -------------------------------------------------------------
@Composable
fun AdminContentScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val subjects by viewModel.allSubjects.collectAsState()
    var showAddSubjectDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("admin_content_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "إدارة المواد والدروس 📚",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                Button(
                    onClick = { showAddSubjectDialog = true },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إضافة مادة")
                }
            }
        }

        items(subjects) { subj ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                        Text(text = subj.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(text = subj.description, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                    }

                    IconButton(
                        onClick = { viewModel.adminDeleteSubject(subj) }
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف", tint = ErrorRed)
                    }
                }
            }
        }
    }

    if (showAddSubjectDialog) {
        var nameInput by remember { mutableStateOf("") }
        var descInput by remember { mutableStateOf("") }
        var trackInput by remember { mutableStateOf("BOTH") }

        AlertDialog(
            onDismissRequest = { showAddSubjectDialog = false },
            title = { Text("إضافة مادة جديدة للمنهج") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("اسم المادة") }
                    )
                    OutlinedTextField(
                        value = descInput,
                        onValueChange = { descInput = it },
                        label = { Text("وصف المادة") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nameInput.isNotBlank()) {
                            viewModel.adminAddSubject(nameInput, descInput, trackInput)
                            showAddSubjectDialog = false
                        }
                    }
                ) {
                    Text("إضافة")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSubjectDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

// -------------------------------------------------------------
// ADMIN NOTIFICATIONS MANAGEMENT
// -------------------------------------------------------------
@Composable
fun AdminNotificationsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("ALL") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("admin_notifications_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "إرسال إشعار للطلاب 📢",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان الإشعار") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("نص الرسالة أو الإشعار") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp)
                )

                Text(text = "الفئة المستهدفة:")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = target == "ALL", onClick = { target = "ALL" }, label = { Text("جميع الطلاب") })
                    FilterChip(selected = target == "SCIENTIFIC", onClick = { target = "SCIENTIFIC" }, label = { Text("القسم العلمي") })
                    FilterChip(selected = target == "LITERARY", onClick = { target = "LITERARY" }, label = { Text("القسم الأدبي") })
                }

                Button(
                    onClick = {
                        if (title.isNotBlank() && message.isNotBlank()) {
                            viewModel.adminBroadcastNotification(title, message, target)
                            title = ""
                            message = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("إرسال الإشعار فوراً")
                }
            }
        }
    }
}

// -------------------------------------------------------------
// ADMIN ACTIVITY LOGS SCREEN
// -------------------------------------------------------------
@Composable
fun AdminLogsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val logs by viewModel.adminActivityLogs.collectAsState()
    val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("admin_logs_screen"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "سجل العمليات الإدارية (${logs.size}) 📜",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }

        items(logs) { log ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = log.details, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "المسؤول: ${log.adminName} ${log.studentName?.let { "• الطالب: $it" } ?: ""}",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Text(
                            text = dateFormat.format(Date(log.timestamp)),
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
            }
        }
    }
}
