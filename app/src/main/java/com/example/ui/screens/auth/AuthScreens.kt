package com.example.ui.screens.auth

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.MainViewModel
import com.example.ui.Screen
import java.net.URLEncoder

data class PlanItem(
    val id: String,
    val title: String,
    val durationText: String,
    val price: Int,
    val priceFormatted: String,
    val badge: String? = null,
    val description: String
)

val subscriptionPlanList = listOf(
    PlanItem(
        id = "plan_1m",
        title = "باقة شهر واحد",
        durationText = "30 يوماً",
        price = 5000,
        priceFormatted = "5,000 ريال",
        badge = null,
        description = "وصول كامل للدروس والملازم وبنك الأسئلة والآلة الحاسبة"
    ),
    PlanItem(
        id = "plan_3m",
        title = "ثلاثة أشهر",
        durationText = "90 يوماً",
        price = 12000,
        priceFormatted = "12,000 ريال",
        badge = "الأكثر طلباً ⭐",
        description = "وفر 3,000 ريال مع وصول كامل لجميع خدمات المنصة والذكاء الاصطناعي"
    ),
    PlanItem(
        id = "plan_6m",
        title = "ستة أشهر",
        durationText = "180 يوماً",
        price = 24000,
        priceFormatted = "24,000 ريال",
        badge = "فصل دراسي كامل",
        description = "تغطية الفصل الدراسي بالكامل مع المراجعات الوزارية الشاملة"
    ),
    PlanItem(
        id = "plan_8m",
        title = "ثمانية أشهر",
        durationText = "240 يوماً",
        price = 30000,
        priceFormatted = "30,000 ريال",
        badge = "الأوفر حتى الوزاري 🏆",
        description = "العام الدراسي بالكامل حتى الاختبارات الوزارية النهائية"
    )
)

const val WHATSAPP_CONTACT_NUMBER = "785502919"
const val WALLET_ACCOUNT_NUMBER = "782916997"

fun openWhatsAppSubscription(
    context: Context,
    studentName: String,
    phoneNumber: String,
    track: String,
    planTitle: String,
    planPrice: String
) {
    val message = """
        *طلب اشتراك جديد في منصة رقيب للتعليم الثانوي* 🇾🇪
        ━━━━━━━━━━━━━━━━━━━━━━━━
        👤 *اسم الطالب:* ${if (studentName.isNotBlank()) studentName else "طالب في الثانوية"}
        📱 *رقم الهاتف:* ${if (phoneNumber.isNotBlank()) phoneNumber else "غير محدد"}
        🎓 *الصف / القسم:* الصف الثالث الثانوي ($track)
        📦 *باقة الاشتراك:* $planTitle
        💰 *المبلغ:* $planPrice ريال يمني
        ━━━━━━━━━━━━━━━━━━━━━━━━
        💳 *حساب السداد المعتمد (جيب / جوالي / ون كاش):*
        رقم الحساب: $WALLET_ACCOUNT_NUMBER
        اسم المستفيد: إدارة أكاديمية رقيب التعليمية
        ━━━━━━━━━━━━━━━━━━━━━━━━
        السلام عليكم ورحمة الله،
        أود سداد المبلغ إلى حسابكم رقم $WALLET_ACCOUNT_NUMBER عبر (محفظة جيب / محفظة جوالي / محفظة ون كاش). يرجى تأكيد استلام الحوالة وتزويدي بكود التفعيل لتشغيل الحساب فوراً.
    """.trimIndent()

    try {
        val encodedMessage = URLEncoder.encode(message, "UTF-8")
        val fullPhone = "967$WHATSAPP_CONTACT_NUMBER"
        val uri = Uri.parse("https://api.whatsapp.com/send?phone=$fullPhone&text=$encodedMessage")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    } catch (e: Exception) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = ClipData.newPlainText("طلب اشتراك رقيب", message)
        clipboard?.setPrimaryClip(clip)
        Toast.makeText(
            context,
            "تم نسخ تفاصيل الطلب ورقم الواتساب $WHATSAPP_CONTACT_NUMBER إلى الحافظة بنجاح",
            Toast.LENGTH_LONG
        ).show()
    }
}

fun copyToClipboard(context: Context, text: String, label: String = "تم النسخ") {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard?.setPrimaryClip(clip)
    Toast.makeText(context, "$label: $text", Toast.LENGTH_SHORT).show()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: MainViewModel,
    onNavigate: (Screen) -> Unit,
    onSuccess: () -> Unit
) {
    AuthScreenContainer(
        initialTabIndex = 0,
        viewModel = viewModel,
        onNavigate = onNavigate,
        onSuccess = onSuccess
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: MainViewModel,
    onNavigate: (Screen) -> Unit,
    onSuccess: () -> Unit
) {
    AuthScreenContainer(
        initialTabIndex = 1,
        viewModel = viewModel,
        onNavigate = onNavigate,
        onSuccess = onSuccess
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreenContainer(
    initialTabIndex: Int,
    viewModel: MainViewModel,
    onNavigate: (Screen) -> Unit,
    onSuccess: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(initialTabIndex) }
    var showOwnerLoginDialog by remember { mutableStateOf(false) }

    // Owner Login Modal Dialog
    if (showOwnerLoginDialog) {
        OwnerLoginDialog(
            viewModel = viewModel,
            onDismiss = { showOwnerLoginDialog = false },
            onSuccess = {
                showOwnerLoginDialog = false
                onNavigate(Screen.AdminDashboard)
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_raqeeb_logo),
                            contentDescription = "شعار رقيب",
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Column {
                            Text(
                                text = if (selectedTab == 0) "تسجيل الدخول" else "إنشاء حساب واشتراك",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "أكاديمية رقيب للتعليم الثانوي",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "رجوع")
                    }
                },
                actions = {
                    // Owner / Admin direct entry icon
                    IconButton(onClick = { showOwnerLoginDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "دخول مالك التطبيق والمدير العام",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Elegant Tab Selector
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "تسجيل الدخول",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    icon = {
                        Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(20.dp))
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "إنشاء حساب واشتراك",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    icon = {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(20.dp))
                    }
                )
            }

            if (selectedTab == 0) {
                LoginFormContent(
                    viewModel = viewModel,
                    onSwitchToRegister = { selectedTab = 1 },
                    onOpenOwnerDialog = { showOwnerLoginDialog = true },
                    onSuccess = onSuccess
                )
            } else {
                RegisterFormContent(
                    viewModel = viewModel,
                    onSwitchToLogin = { selectedTab = 0 },
                    onSuccess = onSuccess
                )
            }
        }
    }
}

@Composable
fun LoginFormContent(
    viewModel: MainViewModel,
    onSwitchToRegister: () -> Unit,
    onOpenOwnerDialog: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 20.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            // Header Hero Banner with Logo R
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_raqeeb_logo),
                        contentDescription = "شعار التطبيق - حرف R الأكاديمي",
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                    )
                    Text(
                        text = "أكاديمية رقيب للتعليم الثانوي",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "المنصة التعليمية الأولى لطلاب الثانوية العامة في الجمهورية اليمنية",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "تسجيل دخول الطالب:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                // Phone Number Field
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("رقم الهاتف") },
                    placeholder = { Text("أدخل رقم هاتفك المسجل") },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )

                // Password Field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("كلمة السر") },
                    placeholder = { Text("أدخل كلمة المرور") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (passwordVisible) "إخفاء كلمة المرور" else "إظهار كلمة المرور"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )
            }
        }

        item {
            // Login Action Button
            Button(
                onClick = {
                    if (phone.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "يرجى إدخال رقم الهاتف وكلمة السر", Toast.LENGTH_SHORT).show()
                    } else {
                        isLoading = true
                        viewModel.login(
                            phoneOrEmail = phone,
                            pass = password,
                            onError = { err ->
                                isLoading = false
                                Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                            },
                            onSuccess = {
                                isLoading = false
                                Toast.makeText(context, "تم تسجيل الدخول بنجاح", Toast.LENGTH_SHORT).show()
                                onSuccess()
                            }
                        )
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Login, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("تسجيل الدخول", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            OutlinedButton(
                onClick = onSwitchToRegister,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("طالب جديد؟ أنشئ حسابك واشترك الآن")
            }
        }

        // Owner / Admin Access Button
        item {
            OutlinedButton(
                onClick = onOpenOwnerDialog,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            ) {
                Icon(
                    Icons.Default.AdminPanelSettings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "بوابة دخول المالك والمدير العام 🛡️",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun RegisterFormContent(
    viewModel: MainViewModel,
    onSwitchToLogin: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current

    // Form inputs
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var track by remember { mutableStateOf("علمي") } // علمي أو أدبي
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Subscription & Code inputs
    var selectedPlanId by remember { mutableStateOf("plan_3m") }
    var subscriptionCodeInput by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    val selectedPlan = subscriptionPlanList.firstOrNull { it.id == selectedPlanId } ?: subscriptionPlanList[1]

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Step 1: Student Information
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("1", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = "بيانات الطالب الأساسية",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Student Name
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("اسم الطالب الرباعي") },
                        placeholder = { Text("الاسم الكامل للطالب") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Phone Number
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("رقم الهاتف") },
                        placeholder = { Text("رقم هاتف الطالب أو ولي الأمر") },
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Track Selection: علمي او ادبي
                    Text(
                        text = "الصف / الشعبة الدراسية (علمي أو أدبي):",
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TrackSelectionCard(
                            title = "علمي",
                            subtitle = "رياضيات • فيزياء • كيمياء • أحياء",
                            icon = "🔬",
                            isSelected = track == "علمي",
                            modifier = Modifier.weight(1f),
                            onClick = { track = "علمي" }
                        )
                        TrackSelectionCard(
                            title = "أدبي",
                            subtitle = "جغرافيا • تاريخ • فلسفة • أدب",
                            icon = "📚",
                            isSelected = track == "أدبي",
                            modifier = Modifier.weight(1f),
                            onClick = { track = "أدبي" }
                        )
                    }

                    // Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("كلمة السر") },
                        placeholder = { Text("أدخل كلمة المرور") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Confirm Password
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("تأكيد كلمة السر") },
                        placeholder = { Text("أعد كتابة كلمة السر للتأكيد") },
                        leadingIcon = {
                            Icon(Icons.Default.LockReset, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        // Step 2: Subscription Plans Selection
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0284C7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("2", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text(
                                text = "اختر باقة الاشتراك المطلوبة",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "وصول كامل للدروس والملازم والاختبارات الوزارية المؤتمتة",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // 4 Specific Plans
                    subscriptionPlanList.forEach { plan ->
                        SubscriptionPlanCard(
                            plan = plan,
                            isSelected = selectedPlanId == plan.id,
                            onClick = { selectedPlanId = plan.id }
                        )
                    }
                }
            }
        }

        // Step 3: Approved Wallets & WhatsApp to 785502919
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF0FDF4)
                ),
                border = BorderStroke(1.5.dp, Color(0xFF22C55E))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF25D366)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "طرق سداد الرسوم وطلب التفعيل (واتساب)",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF14532D),
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = "إرسال طلبك مباشرة إلى خدمة العملاء على الرقم $WHATSAPP_CONTACT_NUMBER",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF166534)
                            )
                        }
                    }

                    // Approved Wallets Card: Jaib, Jawali, One Cash
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFF86EFAC))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "المحافظ الإلكترونية المعتمدة للسداد:",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF15803D),
                                fontSize = 13.sp
                            )
                            Text(
                                text = "✓ محفظة جيب (Jaib)\n✓ محفظة جوالي (Jawali)\n✓ محفظة ون كاش (One Cash)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF166534)
                            )
                            Divider(color = Color(0xFFE2E8F0))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("رقم الحساب الموحد لجميع المحافظ:", style = MaterialTheme.typography.labelSmall, color = Color.DarkGray)
                                    Text(
                                        text = WALLET_ACCOUNT_NUMBER,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF15803D)
                                    )
                                    Text("المستفيد: إدارة أكاديمية رقيب", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                }
                                OutlinedButton(
                                    onClick = {
                                        copyToClipboard(context, WALLET_ACCOUNT_NUMBER, "تم نسخ رقم الحساب")
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("نسخ الحساب", fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    // Main WhatsApp Action Button
                    Button(
                        onClick = {
                            openWhatsAppSubscription(
                                context = context,
                                studentName = fullName,
                                phoneNumber = phone,
                                track = track,
                                planTitle = selectedPlan.title,
                                planPrice = selectedPlan.priceFormatted
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF25D366),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "إرسال طلب الاشتراك إلى الواتساب ($WHATSAPP_CONTACT_NUMBER)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Step 4: Enter Subscription Code
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF7C3AED)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("3", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text(
                                text = "إدخال كود الاشتراك (تفعيل فوري)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "إذا استلمت كود تفعيل بعد سداد المبلغ، أدخله هنا لتفعيل اشتراكك فوراً",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    OutlinedTextField(
                        value = subscriptionCodeInput,
                        onValueChange = { subscriptionCodeInput = it.uppercase() },
                        label = { Text("كود الاشتراك (اختياري عند التسجيل)") },
                        placeholder = { Text("أدخل الكود الممنوح لك من الإدارة") },
                        leadingIcon = {
                            Icon(Icons.Default.VpnKey, contentDescription = null, tint = Color(0xFF7C3AED))
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Text(
                        text = "💡 يمكنك إنشاء حسابك الآن وإدخال كود التفعيل لاحقاً في أي وقت من شاشة 'إدارة الاشتراك'.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Final Submit: Register Account
        item {
            Button(
                onClick = {
                    when {
                        fullName.isBlank() -> {
                            Toast.makeText(context, "يرجى كتابة اسم الطالب الكامل", Toast.LENGTH_SHORT).show()
                        }
                        phone.isBlank() || phone.length < 9 -> {
                            Toast.makeText(context, "يرجى إدخال رقم هاتف صحيح (9 أرقام على الأقل)", Toast.LENGTH_SHORT).show()
                        }
                        password.isBlank() || password.length < 4 -> {
                            Toast.makeText(context, "كلمة المرور يجب أن لا تقل عن 4 خانات", Toast.LENGTH_SHORT).show()
                        }
                        password != confirmPassword -> {
                            Toast.makeText(context, "كلمة المرور وتأكيدها غير متطابقين", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            isSubmitting = true
                            viewModel.register(
                                name = fullName,
                                phoneOrEmail = phone,
                                pass = password,
                                confirmPass = confirmPassword,
                                track = track,
                                subscriptionCode = subscriptionCodeInput.trim(),
                                onError = { err ->
                                    isSubmitting = false
                                    Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                                },
                                onSuccess = {
                                    isSubmitting = false
                                    onSuccess()
                                }
                            )
                        }
                    }
                },
                enabled = !isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "إنشاء الحساب وبدء التعلم 🚀",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        item {
            TextButton(
                onClick = onSwitchToLogin,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "لديك حساب مسجل بالفعل؟ تسجيل الدخول",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun OwnerLoginDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var ownerPass by remember { mutableStateOf("") }
    var ownerError by remember { mutableStateOf("") }
    var ownerLoading by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text("بوابة دخول المالك والمدير العام", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "هذه البوابة مخصصة لمالك التطبيق لإدارة حسابات الطلاب، وتوليد أكواد الاشتراكات، ومراجعة الحوالات المالية.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = ownerPass,
                    onValueChange = {
                        ownerPass = it
                        ownerError = ""
                    },
                    label = { Text("كلمة مرور الإدارة") },
                    placeholder = { Text("أدخل كلمة المرور الخاصة بالمالك") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                if (ownerError.isNotBlank()) {
                    Text(
                        text = ownerError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (ownerPass.isBlank()) {
                        ownerError = "يرجى كتابة كلمة المرور"
                        return@Button
                    }
                    ownerLoading = true
                    viewModel.login(
                        phoneOrEmail = "782916997",
                        pass = ownerPass.trim(),
                        onError = { err ->
                            ownerLoading = false
                            ownerError = err
                        },
                        onSuccess = {
                            ownerLoading = false
                            onSuccess()
                        }
                    )
                },
                enabled = !ownerLoading
            ) {
                if (ownerLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                } else {
                    Text("تسجيل الدخول كمالك")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

@Composable
fun TrackSelectionCard(
    title: String,
    subtitle: String,
    icon: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = icon, fontSize = 20.sp)
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun SubscriptionPlanCard(
    plan: PlanItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick
            )

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = plan.title,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                    plan.badge?.let { badgeText ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFD97706)
                        ) {
                            Text(
                                text = badgeText,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Text(
                    text = "${plan.durationText} • ${plan.description}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = plan.priceFormatted,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "ريال يمني",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
