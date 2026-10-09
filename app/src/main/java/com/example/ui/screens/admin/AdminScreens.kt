package com.example.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.DocumentBookletEntity
import com.example.data.local.entities.PaymentRequestEntity
import com.example.data.local.entities.UserEntity
import com.example.ui.MainViewModel
import com.example.ui.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: MainViewModel,
    onNavigate: (Screen) -> Unit
) {
    val context = LocalContext.current
    val studentCount by viewModel.adminStudentCount.collectAsState()
    val activeSubCount by viewModel.adminActiveSubCount.collectAsState()
    val pendingPayments by viewModel.adminPendingPaymentCount.collectAsState()
    val unusedCodes by viewModel.adminUnusedCodeCount.collectAsState()
    val booklets by viewModel.allDocumentBooklets.collectAsState()
    val students by viewModel.adminAllUsers.collectAsState()

    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showAddBookletDialog by remember { mutableStateOf(false) }
    var newPasswordInput by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") }

    // Booklet Form State
    var bookletTitle by remember { mutableStateOf("") }
    var bookletSubject by remember { mutableStateOf("الرياضيات") }
    var bookletType by remember { mutableStateOf("PDF") }
    var bookletFileName by remember { mutableStateOf("") }
    var bookletDesc by remember { mutableStateOf("") }

    // Cancel Subscription Confirmation Dialog
    var userToCancelSub by remember { mutableStateOf<UserEntity?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
    ) {
        // Owner Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
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
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color.White)
                            }
                            Column {
                                Text(
                                    text = "لوحة تحكم المالك والمدير العام",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "إدارة كاملة: تغيير كلمة السر، رفع الملازم، وإلغاء الاشتراكات",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    // Direct Action: Change Owner Password
                    Button(
                        onClick = { showChangePasswordDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.LockReset, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("تغيير كلمة السر الخاصة بالمالك", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Stats Overview (4 Cards)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("إجمالي الطلاب", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        Text("$studentCount", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("الاشتراكات النشطة", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        Text("$activeSubCount", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("طلبات الدفع المعلقة", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        Text("$pendingPayments", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color(0xFFEA580C))
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("الملفات والملازم", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        Text("${booklets.size}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                    }
                }
            }
        }

        // Section: Booklets & Study Files Management (إضافة ملفات PDF و Word)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📁 إدارة ملفات PDF وملفات Word المرفوعة",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = { showAddBookletDialog = true },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إضافة ملف")
                }
            }
        }

        items(booklets) { booklet ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (booklet.fileType == "PDF") Color(0xFFEF4444) else Color(0xFF3B82F6)
                        ) {
                            Text(
                                text = booklet.fileType,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        Column {
                            Text(
                                text = booklet.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${booklet.subjectName} • ${booklet.fileName} • ${booklet.fileSize}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                    IconButton(
                        onClick = { viewModel.adminDeleteDocumentBooklet(booklet) }
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "حذف الملف", tint = Color.Red)
                    }
                }
            }
        }

        // Section: Subscribers & Cancelling Subscriptions (صلاحية إلغاء اشتراك أي مشترك)
        item {
            Text(
                text = "👥 المشتركون والطلاب (إلغاء الاشتراكات)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(students.filter { it.role == "STUDENT" }) { student ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = student.fullName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "هاتف: ${student.phoneOrEmail} | الشعبة: ${student.track}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }

                    Button(
                        onClick = { userToCancelSub = student },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("إلغاء الاشتراك", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }

    // Dialog: Change Owner Password
    if (showChangePasswordDialog) {
        AlertDialog(
            onDismissRequest = { showChangePasswordDialog = false },
            title = { Text("تغيير كلمة السر الخاصة بالمالك") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("أدخل كلمة المرور الجديدة لحساب الإدارة (رقم الهاتف: 782916997):")
                    OutlinedTextField(
                        value = newPasswordInput,
                        onValueChange = { newPasswordInput = it },
                        label = { Text("كلمة المرور الجديدة") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = confirmPasswordInput,
                        onValueChange = { confirmPasswordInput = it },
                        label = { Text("تأكيد كلمة المرور") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPasswordInput != confirmPasswordInput) {
                            Toast.makeText(context, "كلمتا المرور غير متطابقتين", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.changeOwnerPassword(
                                newPassword = newPasswordInput,
                                onSuccess = {
                                    showChangePasswordDialog = false
                                    newPasswordInput = ""
                                    confirmPasswordInput = ""
                                    Toast.makeText(context, "تم تغيير كلمة سر المالك بنجاح!", Toast.LENGTH_SHORT).show()
                                },
                                onError = { msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                ) {
                    Text("حفظ التغيير")
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangePasswordDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Dialog: Add PDF or Word Document
    if (showAddBookletDialog) {
        val subjectsList = listOf("الرياضيات", "الكيمياء", "الفيزياء", "الأحياء", "اللغة العربية", "اللغة الإنجليزية")
        AlertDialog(
            onDismissRequest = { showAddBookletDialog = false },
            title = { Text("إضافة ملزمة أو ملف دراسي جديد") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = bookletTitle,
                        onValueChange = { bookletTitle = it },
                        label = { Text("عنوان الملزمة أو الملف") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Subject Chips
                    Text("المادة:", fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        subjectsList.take(3).forEach { s ->
                            FilterChip(
                                selected = bookletSubject == s,
                                onClick = { bookletSubject = s },
                                label = { Text(s, style = MaterialTheme.typography.bodySmall) }
                            )
                        }
                    }

                    // Format Toggle: PDF or Word
                    Text("نوع الملف:", fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        FilterChip(
                            selected = bookletType == "PDF",
                            onClick = { bookletType = "PDF" },
                            label = { Text("ملف PDF") }
                        )
                        FilterChip(
                            selected = bookletType == "WORD",
                            onClick = { bookletType = "WORD" },
                            label = { Text("ملف Word (docx)") }
                        )
                    }

                    OutlinedTextField(
                        value = bookletFileName,
                        onValueChange = { bookletFileName = it },
                        label = { Text("اسم الملف (مثال: math_unit1.pdf)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = bookletDesc,
                        onValueChange = { bookletDesc = it },
                        label = { Text("وصف ومحتوى الملزمة") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (bookletTitle.isBlank()) {
                            Toast.makeText(context, "يرجى كتابة عنوان الملزمة", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.adminAddDocumentBooklet(
                                title = bookletTitle,
                                subjectId = 1L,
                                subjectName = bookletSubject,
                                fileType = bookletType,
                                fileName = bookletFileName.ifEmpty { "${bookletTitle}.${if (bookletType == "PDF") "pdf" else "docx"}" },
                                description = bookletDesc
                            )
                            showAddBookletDialog = false
                            bookletTitle = ""
                            bookletFileName = ""
                            bookletDesc = ""
                        }
                    }
                ) {
                    Text("إضافة ونشر")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddBookletDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Dialog: Confirm Cancel Subscription
    if (userToCancelSub != null) {
        val student = userToCancelSub!!
        AlertDialog(
            onDismissRequest = { userToCancelSub = null },
            title = { Text("تأكيد إلغاء الاشتراك") },
            text = {
                Text("هل أنت متأكد من رغبتك في إلغاء اشتراك الطالب '${student.fullName}' (${student.phoneOrEmail})؟ سيتم تجميد وصوله للمحتوى المدفوع فوراً.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.adminCancelSubscription(student.id, student.fullName)
                        userToCancelSub = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("نعم، إلغاء الاشتراك فوراً")
                }
            },
            dismissButton = {
                TextButton(onClick = { userToCancelSub = null }) {
                    Text("تراجع")
                }
            }
        )
    }
}
