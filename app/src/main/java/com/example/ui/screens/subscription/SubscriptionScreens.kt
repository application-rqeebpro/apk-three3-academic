package com.example.ui.screens.subscription

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.SubscriptionPlanEntity
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.components.copyToClipboard
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SubscriptionScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val isSubscribed by viewModel.isSubscribed.collectAsState()
    val activeSub by viewModel.activeSubscription.collectAsState()
    val plans by viewModel.repository.getAllActivePlans().collectAsState(initial = emptyList())

    val paymentWalletNumber = "782916997"
    val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

    val daysRemaining = remember(activeSub) {
        activeSub?.let {
            val diff = it.endDate - System.currentTimeMillis()
            (diff / (1000 * 60 * 60 * 24)).coerceAtLeast(0)
        } ?: 0L
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("subscription_screen_content"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Status Card
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isSubscribed) CardNavy else CardGold
                ),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(18.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (isSubscribed) NavyPrimary else AmberSecondary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isSubscribed) Icons.Default.Verified else Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }

                        Column {
                            Text(
                                text = if (isSubscribed) "اشتراكك مفعل (${activeSub?.planName})" else "الحساب المجاني (ميزات محدودة)",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSubscribed) NavyPrimary else AmberSecondary
                                )
                            )
                            if (isSubscribed && activeSub != null) {
                                Text(
                                    text = "تاريخ الانتهاء: ${dateFormat.format(Date(activeSub!!.endDate))} (متبقي $daysRemaining يوم)",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextSecondaryLight,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            } else {
                                Text(
                                    text = "اشترك للوصول لكافة المواد والشروحات وبنك الأسئلة الكامل",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryLight)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Action: Activate Code Shortcut
        item {
            OutlinedButton(
                onClick = { viewModel.navigateTo(Screen.ActivateCode) },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("open_activate_code_button")
            ) {
                Icon(imageVector = Icons.Default.VpnKey, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "لديك كود تفعيل؟ اضغط هنا لتفعيله 🔑", fontWeight = FontWeight.Bold)
            }
        }

        // Yemeni Wallets Payment Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
                        Text(text = "🇾🇪", fontSize = 20.sp)
                        Column {
                            Text(
                                text = "أرقام حسابات الدفع بالمحافظ الإلكترونية",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary
                                )
                            )
                            Text(
                                text = "حول المبلغ عبر إحدى المحافظ التالية، ثم أرسل الطلب للرقم: 785502919",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    // 1. OneCash (ون كاش)
                    WalletAccountRow(
                        walletName = "محفظة ون كاش (OneCash)",
                        accountNumber = "192737631",
                        badgeColor = Color(0xFFEFF6FF),
                        textColor = Color(0xFF1D4ED8),
                        onCopy = { copyToClipboard(context, "192737631", "حساب ون كاش") }
                    )

                    // 2. Jeeb (جيب)
                    WalletAccountRow(
                        walletName = "محفظة جيب (Jeeb)",
                        accountNumber = "913554",
                        badgeColor = Color(0xFFFEF3C7),
                        textColor = Color(0xFFB45309),
                        onCopy = { copyToClipboard(context, "913554", "حساب جيب") }
                    )

                    // 3. Jawali (جوالي)
                    WalletAccountRow(
                        walletName = "محفظة جوالي (Jawali)",
                        accountNumber = "782916997",
                        badgeColor = Color(0xFFECFDF5),
                        textColor = Color(0xFF047857),
                        onCopy = { copyToClipboard(context, "782916997", "حساب جوالي") }
                    )

                    // Recipient phone badge & Direct WhatsApp button
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "رقم استقبال وتأكيد طلبات الاشتراك:",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Text(
                                    text = "785502919 📲",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = NavyPrimary
                                    )
                                )
                            }

                            Button(
                                onClick = {
                                    val generalMsg = "مرحباً إدارة أكاديمية الثالث الثانوي اليمني، أود الاستفسار والاشتراك في الأكاديمية عبر الرقم 785502919."
                                    sendViaWhatsApp(context, "785502919", generalMsg)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp)
                            ) {
                                Text(
                                    text = "📲 إرسال طلب اشتراك واتساب للرقم 785502919",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Plans Section
        item {
            Text(
                text = "خطط الاشتراك المدفوعة:",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        val defaultPlans = if (plans.isNotEmpty()) plans else listOf(
            SubscriptionPlanEntity(name = "اشتراك 3 أشهر", durationMonths = 3, priceYmr = 12000, description = "تغطية الفصل الدراسي الأول أو التحضير المكثف."),
            SubscriptionPlanEntity(name = "اشتراك 6 أشهر", durationMonths = 6, priceYmr = 24000, description = "الخيار الأنسب للطلاب طوال فترة الامتحانات.", isPopular = true),
            SubscriptionPlanEntity(name = "اشتراك سنوي (12 شهر)", durationMonths = 12, priceYmr = 30000, description = "تغطية شاملة لجميع المواد حتى نهاية العام الوزاري.")
        )

        items(defaultPlans) { plan ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (plan.isPopular) CardGold else MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = if (plan.isPopular) androidx.compose.foundation.BorderStroke(1.5.dp, AmberSecondary) else null,
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
                        Text(
                            text = plan.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        )
                        if (plan.isPopular) {
                            Surface(shape = RoundedCornerShape(6.dp), color = AmberSecondary) {
                                Text(
                                    text = "الأكثر طلباً ⭐",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = "${plan.priceYmr} ريال يمني",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = if (plan.isPopular) Color(0xFF78350F) else NavyPrimary
                        )
                    )

                    Text(
                        text = plan.description,
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.navigateTo(Screen.PaymentForm) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (plan.isPopular) AmberSecondary else NavyPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                        ) {
                            Text(text = "نموذج الحوالة 💳", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                val planMsg = """
*طلب اشتراك في أكاديمية الثالث الثانوي اليمني* 🇾🇪
• الخطة المطلوبة: ${plan.name}
• المبلغ: ${plan.priceYmr} ريال يمني
أود تأكيد الاشتراك وتحويل الرسوم للرقم 785502919.
                                """.trimIndent()
                                sendViaWhatsApp(context, "785502919", planMsg)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                        ) {
                            Text(text = "طلب بالواتساب 📲", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// PAYMENT FORM SCREEN (SUBMIT PROOF)
// -------------------------------------------------------------
@Composable
fun PaymentFormScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()

    var studentName by remember { mutableStateOf(currentUser?.fullName ?: "") }
    var studentPhone by remember { mutableStateOf(currentUser?.phoneOrEmail ?: "") }
    var selectedPlanName by remember { mutableStateOf("اشتراك 3 أشهر") }
    var selectedDuration by remember { mutableStateOf(3) }
    var amountYmr by remember { mutableStateOf(12000) }
    var walletName by remember { mutableStateOf("جيب") }
    var transferRefNumber by remember { mutableStateOf("") }
    var receiptNote by remember { mutableStateOf("") }

    val walletNumber = "782916997"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("payment_form_content"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "نموذج إرسال إشعار الدفع 📤",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                    Text(
                        text = "حول المبلغ لحساب المحفظة المناسبة ثم أرسل إشعار التحويل للرقم: 785502919",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                        )
                    )
                }
            }
        }

        // Plan Choice
        item {
            Text(text = "اختر خطة الاشتراك:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            val plansList = listOf(
                Triple("اشتراك 3 أشهر", 3, 12000),
                Triple("اشتراك 6 أشهر", 6, 24000),
                Triple("اشتراك سنوي (12 شهر)", 12, 30000)
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                plansList.forEach { (name, months, price) ->
                    val isSelected = selectedPlanName == name
                    Surface(
                        onClick = {
                            selectedPlanName = name
                            selectedDuration = months
                            amountYmr = price
                        },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = name, fontWeight = FontWeight.Bold)
                            Text(text = "$price ريال يمني", color = NavyPrimary, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }

        // Wallet Choice with Account numbers
        item {
            Text(text = "اختر المحفظة المحول منها:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            val walletAccounts = listOf(
                Triple("ون كاش", "192737631", "محفظة ون كاش"),
                Triple("جيب", "913554", "محفظة جيب"),
                Triple("جوالي", "782916997", "محفظة جوالي")
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                walletAccounts.forEach { (wName, accNum, fullName) ->
                    val isSelected = walletName == wName
                    Surface(
                        onClick = { walletName = wName },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = fullName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(
                                    text = "رقم الحساب: $accNum",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isSelected) NavyPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Button(
                                onClick = { copyToClipboard(context, accNum, fullName) },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text(text = "نسخ الرقم", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Fields
        item {
            OutlinedTextField(
                value = studentName,
                onValueChange = { studentName = it },
                label = { Text("اسم الطالب الكامل *") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = studentPhone,
                onValueChange = { studentPhone = it },
                label = { Text("رقم هاتف الطالب *") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = transferRefNumber,
                onValueChange = { transferRefNumber = it },
                label = { Text("رقم عملية التحويل (رقم الحوالة) *") },
                placeholder = { Text("أدخل رقم السند أو الإشعار") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("transfer_ref_input")
            )
        }

        item {
            OutlinedTextField(
                value = receiptNote,
                onValueChange = { receiptNote = it },
                label = { Text("ملاحظة إضافية عن التحويل (اختياري)") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Target Recipient Phone notice
        item {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = CardNavy,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = NavyPrimary)
                    Text(
                        text = "عند الإرسال سيتم توجيه طلب الاشتراك إلى الرقم 785502919 عبر واتساب ورسائل المنظومة للمراجعة والتفعيل.",
                        style = MaterialTheme.typography.bodySmall.copy(color = NavyPrimary, fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }

        // Submit Button
        item {
            var showSendToNumberDialog by remember { mutableStateOf(false) }

            Button(
                onClick = {
                    viewModel.submitPayment(
                        planName = selectedPlanName,
                        durationMonths = selectedDuration,
                        amountYmr = amountYmr,
                        walletName = walletName,
                        transRef = transferRefNumber,
                        receiptUri = receiptNote.takeIf { it.isNotBlank() },
                        onSuccess = {
                            val msg = """
*طلب اشتراك جديد في أكاديمية الثالث الثانوي اليمني* 🇾🇪
• اسم الطالب: $studentName
• رقم الهاتف: $studentPhone
• الخطة المطلوبة: $selectedPlanName
• المبلغ المحول: $amountYmr ريال يمني
• المحفظة: $walletName
• رقم عملية التحويل: $transferRefNumber
${if (receiptNote.isNotBlank()) "• ملاحظة: $receiptNote\n" else ""}يرجى تفعيل الاشتراك وشكراً لكم.
                            """.trimIndent()
                            sendViaWhatsApp(context, "785502919", msg)
                            showSendToNumberDialog = true
                        }
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_payment_button")
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "إرسال طلب الاشتراك للرقم 785502919 🚀", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            if (showSendToNumberDialog) {
                val formattedMessage = """
*طلب اشتراك جديد في أكاديمية الثالث الثانوي اليمني* 🇾🇪
• اسم الطالب: $studentName
• رقم الهاتف: $studentPhone
• الخطة المطلوبة: $selectedPlanName
• المبلغ المحول: $amountYmr ريال يمني
• المحفظة: $walletName
• رقم عملية التحويل: $transferRefNumber
${if (receiptNote.isNotBlank()) "• ملاحظة: $receiptNote\n" else ""}
يرجى تفعيل الاشتراك وشكراً لكم.
                """.trimIndent()

                AlertDialog(
                    onDismissRequest = {
                        showSendToNumberDialog = false
                        viewModel.navigateTo(Screen.Subscription)
                    },
                    title = {
                        Text("تم تسجيل طلبك بنجاح! 🎉", fontWeight = FontWeight.Bold)
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("أرسل تفاصيل طلبك الآن مباشرة إلى الرقم 785502919 لتأكيد واستلام تفعيل اشتراكك بأسرع وقت:")

                            Button(
                                onClick = {
                                    sendViaWhatsApp(context, "785502919", formattedMessage)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("📲 إرسال عبر واتساب إلى 785502919", color = Color.White, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    sendViaSms(context, "785502919", formattedMessage)
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("💬 إرسال رسالة نصية SMS إلى 785502919")
                            }

                            TextButton(
                                onClick = {
                                    copyToClipboard(context, formattedMessage, "طلب الاشتراك")
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("📋 نسخ نص الرسالة")
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showSendToNumberDialog = false
                                viewModel.navigateTo(Screen.Subscription)
                            }
                        ) {
                            Text("تم ومتابعة")
                        }
                    }
                )
            }
        }
    }
}

// -------------------------------------------------------------
// ACTIVATE CODE SCREEN
// -------------------------------------------------------------
@Composable
fun ActivateCodeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var enteredCode by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("activate_code_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "تفعيل كود الاشتراك 🔑",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
                Text(
                    text = "إذا حصلت على كود تفعيل من الإدارة، أدخله هنا لتفعيل اشتراكك فوراً بدون انتظار.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                    )
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "أدخل كود الاشتراك:",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                OutlinedTextField(
                    value = enteredCode,
                    onValueChange = { enteredCode = it.uppercase() },
                    placeholder = { Text("مثال: YEM-8K4P-29MX") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("code_input_field")
                )

                Button(
                    onClick = {
                        viewModel.activateCode(enteredCode) {
                            viewModel.navigateTo(Screen.Subscription)
                        }
                    },
                    enabled = enteredCode.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_code_button")
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "تفعيل الكود الآن 🚀", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun WalletAccountRow(
    walletName: String,
    accountNumber: String,
    badgeColor: Color,
    textColor: Color,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = badgeColor,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = walletName,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                )
                Text(
                    text = accountNumber,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = textColor,
                        fontSize = 18.sp
                    )
                )
            }

            Button(
                onClick = onCopy,
                colors = ButtonDefaults.buttonColors(containerColor = textColor),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "نسخ الرقم", fontSize = 11.sp, color = Color.White)
            }
        }
    }
}

fun sendViaWhatsApp(context: android.content.Context, phone: String, message: String) {
    try {
        val cleanPhone = if (phone.startsWith("+") || phone.startsWith("967")) phone else "967$phone"
        val encodedMsg = java.net.URLEncoder.encode(message, "UTF-8")
        val uri = android.net.Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhone&text=$encodedMsg")
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    } catch (e: Exception) {
        sendViaSms(context, phone, message)
    }
}

fun sendViaSms(context: android.content.Context, phone: String, message: String) {
    try {
        val intent = android.content.Intent(android.content.Intent.ACTION_SENDTO, android.net.Uri.parse("smsto:$phone"))
        intent.putExtra("sms_body", message)
        context.startActivity(intent)
    } catch (e: Exception) {
        copyToClipboard(context, message, "تفاصيل الطلب")
    }
}

