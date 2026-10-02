package com.example.ui.screens.subscription

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.components.RaqeebBadge
import com.example.ui.components.RaqeebLogoEmblem
import com.example.ui.components.copyToClipboard
import com.example.ui.theme.*

@Composable
fun SubscriptionGateScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    var isActivating by remember { mutableStateOf(false) }

    var codeInput by remember { mutableStateOf("") }
    var codeError by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("subscription_gate_content"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Academy Header
        item {
            Spacer(modifier = Modifier.height(8.dp))
            RaqeebLogoEmblem(
                size = 85.dp,
                showBackground = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "رَقِــيـب",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = RaqeebDeepNavy,
                    fontSize = 24.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            RaqeebBadge(fontSize = 11)
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = RaqeebCyanLight,
                border = androidx.compose.foundation.BorderStroke(1.dp, RaqeebCyanAccent)
            ) {
                Text(
                    text = "الإصدار الثالث 3.0 • V3.0 🇾🇪",
                    color = RaqeebDeepNavy,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "مرحباً بك يا ${currentUser?.fullName ?: "طالبنا العزيز"} 👋",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        // Lock Warning Notice
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(ErrorRed.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🔒", fontSize = 22.sp)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "محتوى المنصة مقفل بانتظار التفعيل",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = ErrorRed
                            )
                        )
                        Text(
                            text = "لا يمكنك استخدام شروحات المواد، أو المعلم الذكي، أو حل المسائل، أو نماذج الامتحانات إلا بعد إدخال رمز التفعيل أو الاشتراك عبر المحافظ المعتمدة.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF991B1B),
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                        )
                    }
                }
            }
        }

        // Section 1: Activation Code Input
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(18.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "🔑", fontSize = 20.sp)
                        Text(
                            text = "إدخال رمز التفعيل الرسمي (Activation Code)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary
                            )
                        )
                    }

                    Text(
                        text = "إذا حصلت على كود تفعيل رسمي من الإدارة أو من أحد وكلاء المنصة المعتمدين، أدخله هنا لتفعيل اشتراكك فوراً:",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )

                    OutlinedTextField(
                        value = codeInput,
                        onValueChange = {
                            codeInput = it.uppercase()
                            codeError = null
                        },
                        label = { Text("رمز التفعيل الرسمي") },
                        placeholder = { Text("مثال: YEM-XXXX-XXXX") },
                        leadingIcon = { Icon(Icons.Default.VpnKey, contentDescription = null, tint = AmberSecondary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("gate_code_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    if (codeError != null) {
                        Text(
                            text = codeError ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Button(
                        onClick = {
                            if (codeInput.isBlank()) {
                                codeError = "يرجى كتابة رمز التفعيل"
                            } else {
                                isActivating = true
                                viewModel.activateCode(codeInput.trim()) {
                                    isActivating = false
                                }
                            }
                        },
                        enabled = !isActivating,
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("gate_activate_button")
                    ) {
                        if (isActivating) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "جاري التحقق والتفعيل...", color = Color.White)
                        } else {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "تفعيل الكود وفتح التطبيق فوراً 🚀",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Test code suggestion chip
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFEF3C7),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                codeInput = "YEMEN-2026"
                                codeError = null
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "💡", fontSize = 16.sp)
                            Text(
                                text = "انقر هنا لتجربة كود التفعيل المعتمد: YEMEN-2026 (اشتراك سنوي كامل)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF78350F),
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // Section 2: How to Subscribe via Yemeni Wallets
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(18.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "💳", fontSize = 20.sp)
                        Text(
                            text = "ليس لديك كود؟ اشترك الآن عبر المحافظ اليمنية",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary
                            )
                        )
                    }

                    Text(
                        text = "حول رسوم الاشتراك إلى إحدى المحافظ التالية، ثم أرسل إشعار التحويل للرقم 785502919 لتحصل على رمز التفعيل فوراً:",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )

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
                }
            }
        }

        // Section 3: Send subscription to 785502919
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(18.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "📲", fontSize = 20.sp)
                        Text(
                            text = "إرسال طلب الاشتراك إلى الرقم 785502919",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }

                    Text(
                        text = "تواصل مباشرة مع الإدارة لتأكيد التحويل واستلام كود التفعيل الخاص بك بأسرع وقت:",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                        )
                    )

                    Button(
                        onClick = {
                            val msg = """
*طلب اشتراك وتفعيل كود في تطبيق رقيب للتعليم الثانوي* 🇾🇪
• اسم الطالب: ${currentUser?.fullName ?: "طالب جديد"}
• رقم الهاتف: ${currentUser?.phoneOrEmail ?: ""}
• أرغب بالاشتراك وتزويدي برمز التفعيل بعد التحويل عبر المحافظ المعتمدة.
                            """.trimIndent()
                            sendViaWhatsApp(context, "785502919", msg)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = "📲 إرسال طلب اشتراك عبر واتساب إلى 785502919",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val msg = "طلب اشتراك في تطبيق رقيب للتعليم الثانوي - الطالب: ${currentUser?.fullName ?: ""}"
                                sendViaSms(context, "785502919", msg)
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("💬 رسالة SMS إلى 785502919", fontSize = 11.sp)
                        }

                        Button(
                            onClick = { viewModel.navigateTo(Screen.PaymentForm) },
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("📝 نموذج التحويل", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Account management / Owner entry
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { viewModel.logout() }
                ) {
                    Icon(imageVector = Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "تسجيل الخروج أو تبديل الحساب", fontSize = 12.sp)
                }

                Surface(
                    onClick = { viewModel.navigateTo(Screen.AdminDashboard) },
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFEF3C7)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "👑", fontSize = 14.sp)
                        Text(
                            text = "دخول المالك",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF78350F)
                            )
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
