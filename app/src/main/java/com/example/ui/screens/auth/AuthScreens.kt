package com.example.ui.screens.auth

import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.components.RaqeebBadge
import com.example.ui.components.RaqeebLogoEmblem
import com.example.ui.theme.*

// -------------------------------------------------------------
// LOGIN SCREEN
// -------------------------------------------------------------
@Composable
fun LoginScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var phoneOrEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(true) }
    var showForgotDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
            .testTag("login_screen_content"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            RaqeebLogoEmblem(
                size = 96.dp,
                showBackground = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "رَقِــيـب",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = RaqeebDeepNavy,
                    fontSize = 28.sp
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
                text = "تسجيل الدخول إلى حسابك الدراسي",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    OutlinedTextField(
                        value = phoneOrEmail,
                        onValueChange = { phoneOrEmail = it },
                        label = { Text("رقم الهاتف أو البريد الإلكتروني") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("كلمة المرور") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = rememberMe, onCheckedChange = { rememberMe = it })
                            Text(text = "تذكرني", fontSize = 13.sp)
                        }

                        TextButton(onClick = { showForgotDialog = true }) {
                            Text(text = "نسيت كلمة المرور؟", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.login(phoneOrEmail, password) {
                                viewModel.navigateTo(Screen.Home)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RaqeebRoyalBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("login_button")
                    ) {
                        Text(text = "تسجيل الدخول", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "ليس لديك حساب؟", color = MaterialTheme.colorScheme.onSurfaceVariant)
                TextButton(onClick = { viewModel.navigateTo(Screen.Register) }) {
                    Text(text = "إنشاء حساب جديد", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showForgotDialog) {
        var forgotInput by remember { mutableStateOf("") }
        var resetPassInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showForgotDialog = false },
            title = { Text("استعادة كلمة المرور") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("أدخل رقم هاتفك أو بريدك المسجل وكلمة المرور الجديدة:")
                    OutlinedTextField(
                        value = forgotInput,
                        onValueChange = { forgotInput = it },
                        label = { Text("رقم الهاتف أو البريد") }
                    )
                    OutlinedTextField(
                        value = resetPassInput,
                        onValueChange = { resetPassInput = it },
                        label = { Text("كلمة المرور الجديدة") },
                        visualTransformation = PasswordVisualTransformation()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (forgotInput.isNotBlank() && resetPassInput.isNotBlank()) {
                            viewModel.resetPassword(forgotInput, resetPassInput) {
                                showForgotDialog = false
                            }
                        }
                    }
                ) {
                    Text("استعادة وتغيير")
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

// -------------------------------------------------------------
// REGISTER SCREEN
// -------------------------------------------------------------
@Composable
fun RegisterScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var fullName by remember { mutableStateOf("") }
    var phoneOrEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var track by remember { mutableStateOf("علمي") } // "علمي" or "أدبي"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
            .testTag("register_screen_content"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            RaqeebLogoEmblem(
                size = 76.dp,
                showBackground = true
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "إنشاء حساب طالب جديد 📝",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = RaqeebDeepNavy)
            )
            Text(
                text = "تطبيق رقيب للتعليم الثانوي - الجمهورية اليمنية",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = RaqeebElectricBlue,
                    fontWeight = FontWeight.SemiBold
                )
            )
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
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("الاسم الرباعي الكامل للطالب *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = phoneOrEmail,
                        onValueChange = { phoneOrEmail = it },
                        label = { Text("رقم الهاتف أو البريد الإلكتروني *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Track selector
                    Text(text = "المسار الدراسي:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilterChip(
                            selected = track == "علمي",
                            onClick = { track = "علمي" },
                            label = { Text("القسم العلمي 🔬", fontWeight = FontWeight.Bold) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = track == "أدبي",
                            onClick = { track = "أدبي" },
                            label = { Text("القسم الأدبي 📚", fontWeight = FontWeight.Bold) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("كلمة المرور *") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("تأكيد كلمة المرور *") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = {
                            viewModel.register(fullName, phoneOrEmail, password, confirmPassword, track) {
                                viewModel.navigateTo(Screen.Home)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_register_button")
                    ) {
                        Text(text = "إنشاء الحساب وبدء الدراسة 🚀", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "لديك حساب بالفعل؟", color = MaterialTheme.colorScheme.onSurfaceVariant)
                TextButton(onClick = { viewModel.navigateTo(Screen.Login) }) {
                    Text(text = "تسجيل الدخول", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
