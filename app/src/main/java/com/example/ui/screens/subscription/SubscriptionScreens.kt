package com.example.ui.screens.subscription

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(
    viewModel: MainViewModel,
    onNavigate: (Screen) -> Unit,
    onBack: () -> Unit
) {
    val isSubscribed by viewModel.isSubscribed.collectAsState()
    val activeSub by viewModel.activeSubscription.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("باقات الاشتراك") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "رجوع")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSubscribed) Color(0xFFF0FDF4) else MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (isSubscribed) "أنت مشترك حالياً في باقة ${activeSub?.planName ?: "التميز"}" else "اشترك في باقات أكاديمية رقيب",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "احصل على وصول كامل لكافة نماذج الامتحانات، الملازم الدراسية، الشرح الذكي، وحل المسائل غير المحدود.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            item {
                Button(
                    onClick = { onNavigate(Screen.ActivateCode) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.VpnKey, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("لديك كود تفعيل؟ أدخله هنا")
                }
            }

            // 4 Subscription Tiers
            item {
                Text(
                    text = "الباقات المتاحة:",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // Plan 1: 1 Month
            item {
                SubscriptionTierCard(
                    title = "باقة شهر واحد",
                    duration = "30 يوماً",
                    price = "5,000 ريال يمني",
                    description = "وصول كامل لكافة المواد والدروس والملازم والآلة الحاسبة وبنك الأسئلة.",
                    badge = null,
                    onSubscribe = { onNavigate(Screen.PaymentForm) }
                )
            }

            // Plan 2: 3 Months
            item {
                SubscriptionTierCard(
                    title = "باقة ثلاثة أشهر",
                    duration = "90 يوماً",
                    price = "12,000 ريال يمني",
                    description = "وفر 3,000 ريال مع وصول كامل لكافة المواد وشروحات الذكاء الاصطناعي والملازم.",
                    badge = "الأكثر طلباً ⭐",
                    isHighlight = true,
                    onSubscribe = { onNavigate(Screen.PaymentForm) }
                )
            }

            // Plan 3: 6 Months
            item {
                SubscriptionTierCard(
                    title = "باقة ستة أشهر",
                    duration = "180 يوماً",
                    price = "24,000 ريال يمني",
                    description = "تغطية الفصل الدراسي بالكامل مع النماذج الوزارية المؤتمتة والشروحات التفصيلية.",
                    badge = "فصل دراسي كامل",
                    onSubscribe = { onNavigate(Screen.PaymentForm) }
                )
            }

            // Plan 4: 8 Months
            item {
                SubscriptionTierCard(
                    title = "باقة ثمانية أشهر",
                    duration = "240 يوماً",
                    price = "30,000 ريال يمني",
                    description = "العام الدراسي بالكامل حتى الاختبارات الوزارية النهائية + الملازم والملخصات الشاملة.",
                    badge = "الأوفر حتى الوزاري 🏆",
                    onSubscribe = { onNavigate(Screen.PaymentForm) }
                )
            }

            // WhatsApp Subscription Request
            item {
                val context = LocalContext.current
                val user = viewModel.currentUser.collectAsState().value
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                    border = BorderStroke(1.5.dp, Color(0xFF22C55E))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF16A34A))
                            Column {
                                Text(
                                    text = "الاشتراك المباشر عبر واتساب (785502919)",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF14532D)
                                )
                                Text(
                                    text = "تواصل مع الإدارة لسداد الرسوم عبر المحافظ الإلكترونية واستلام كود التفعيل فوراً",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF166534)
                                )
                            }
                        }

                        Button(
                            onClick = {
                                val message = "السلام عليكم، أرغب بالاشتراك في منصة رقيب للطالب ${user?.fullName ?: "الثانوية العامة"} (رقم: ${user?.phoneOrEmail ?: ""}). يرجى تزويدي برقم الحساب لسداد الرسوم."
                                try {
                                    val encoded = java.net.URLEncoder.encode(message, "UTF-8")
                                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                                        data = android.net.Uri.parse("https://api.whatsapp.com/send?phone=967785502919&text=$encoded")
                                    }
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    android.widget.Toast.makeText(context, "رقم التواصل عبر واتساب: 785502919", android.widget.Toast.LENGTH_LONG).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("مراسلة الإدارة عبر واتساب (785502919)", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentFormScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    var selectedWallet by remember { mutableStateOf("محفظة جيب (Jaib)") }
    var transRef by remember { mutableStateOf("") }
    val wallets = listOf(
        "محفظة جيب (Jaib)",
        "محفظة جوالي (Jawali)",
        "محفظة ون كاش (One Cash)",
        "كريمي إكسبرس (Kuraimi)"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تأكيد عملية الدفع") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "رجوع")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("بيانات التحويل المعتمدة للمنصة:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("رقم الحساب الموحد:", style = MaterialTheme.typography.labelSmall)
                            Text("782916997", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                        }
                        OutlinedButton(
                            onClick = {
                                val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                                clipboard?.setPrimaryClip(android.content.ClipData.newPlainText("رقم الحساب", "782916997"))
                                Toast.makeText(context, "تم نسخ رقم الحساب 782916997 بنجاح", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("نسخ الحساب")
                        }
                    }
                    Text("المستفيد: إدارة أكاديمية رقيب التعليمية", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    Text("المحافظ المعتمدة: محفظة جيب • محفظة جوالي • محفظة ون كاش", style = MaterialTheme.typography.labelSmall, color = Color(0xFF047857))
                }
            }

            Text("اختر المحفظة المستخدمة للتحويل:", fontWeight = FontWeight.Bold)
            wallets.forEach { w ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioButton(
                        selected = selectedWallet == w,
                        onClick = { selectedWallet = w }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(w, fontWeight = if (selectedWallet == w) FontWeight.Bold else FontWeight.Normal)
                }
            }

            OutlinedTextField(
                value = transRef,
                onValueChange = { transRef = it },
                label = { Text("رقم الحوالة أو إشعار الإيداع") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (transRef.isBlank()) {
                        Toast.makeText(context, "يرجى إدخال رقم الحوالة", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.submitPayment(
                            planName = "باقة الفصل الدراسي",
                            durationMonths = 5,
                            amountYmr = "10000",
                            walletName = selectedWallet,
                            transRef = transRef,
                            receiptUri = "",
                            onSuccess = onSuccess
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("إرسال طلب التفعيل للإدارة")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivateCodeScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    var codeInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تفعيل كود الاشتراك") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "رجوع")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.VpnKey, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            Text("أدخل كود الاشتراك الممنوح لك من الإدارة", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = codeInput,
                onValueChange = { codeInput = it },
                label = { Text("كود التفعيل (مثال: RAQ-8492)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.activateCode(codeInput, onSuccess)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("تفعيل الاشتراك الآن")
            }
        }
    }
}

@Composable
fun SubscriptionGateScreen(
    onNavigateToSubscription: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(72.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            Text("هذا المحتوى متاح للمشتركين فقط", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text("قم بترقية حسابك للوصول غير المحدود للملازم والاختبارات ومساعد الذكاء الاصطناعي.")
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onNavigateToSubscription, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Text("عرض باقات الاشتراك")
            }
            TextButton(onClick = onBack) {
                Text("العودة للخلف")
            }
        }
    }
}

@Composable
fun SubscriptionTierCard(
    title: String,
    duration: String,
    price: String,
    description: String,
    badge: String? = null,
    isHighlight: Boolean = false,
    onSubscribe: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isHighlight) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.surfaceVariant
        ),
        border = BorderStroke(
            if (isHighlight) 2.dp else 1.dp,
            if (isHighlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        )
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
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                badge?.let {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFD97706)
                    ) {
                        Text(
                            text = it,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Text(
                text = price,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "• المدة: $duration\n• $description",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(
                onClick = onSubscribe,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("اشترك الآن بالباقة")
            }
        }
    }
}
