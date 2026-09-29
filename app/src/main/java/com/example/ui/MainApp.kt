package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.LayoutDirection
import com.example.ui.components.AcademyBottomNav
import com.example.ui.components.AcademyTopBar
import com.example.ui.screens.admin.*
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.auth.RegisterScreen
import com.example.ui.screens.curriculum.LessonDetailScreen
import com.example.ui.screens.curriculum.SubjectsScreen
import com.example.ui.screens.curriculum.UnitsScreen
import com.example.ui.screens.exams.ExamResultScreen
import com.example.ui.screens.exams.ExamSessionScreen
import com.example.ui.screens.exams.ExamsScreen
import com.example.ui.screens.exams.QuestionBankScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.profile.FavoritesScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.profile.ProgressScreen
import com.example.ui.screens.profile.SearchScreen
import com.example.ui.screens.smarttutor.SmartTutorScreen
import com.example.ui.screens.solver.SolveMyQuestionScreen
import com.example.ui.screens.subscription.ActivateCodeScreen
import com.example.ui.screens.subscription.PaymentFormScreen
import com.example.ui.screens.subscription.SubscriptionScreen
import com.example.ui.screens.explain.ExplainMeScreen
import com.example.ui.screens.subscription.SubscriptionGateScreen
import com.example.ui.theme.MyApplicationTheme

@Composable
fun MainApp(viewModel: MainViewModel) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val isSubscribed by viewModel.isSubscribed.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var showOwnerLoginDialog by remember { mutableStateOf(false) }

    val isLockedStudent = currentUser != null && currentUser?.role != "ADMIN" && !isSubscribed
    val isNotLoggedIn = currentUser == null

    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.toastMessage.value = null
        }
    }

    // Force RTL for Arabic Educational Academy
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MyApplicationTheme(darkTheme = isDarkMode) {
            val canNavigateBack = !isNotLoggedIn && !isLockedStudent && currentScreen != Screen.Home
            BackHandler(enabled = canNavigateBack) {
                viewModel.navigateBack()
            }

            val showBottomNav = !isNotLoggedIn && !isLockedStudent && currentScreen in listOf(
                Screen.Home,
                Screen.Subjects,
                Screen.ExplainMe,
                Screen.SmartTutor,
                Screen.SolveMyQuestion,
                Screen.Profile
            )

            val displayTitle = when {
                isNotLoggedIn -> if (currentScreen == Screen.Register) "إنشاء حساب طالب جديد" else "تسجيل الدخول للأكاديمية"
                isLockedStudent -> "تفعيل اشتراك الأكاديمية 🇾🇪"
                else -> currentScreen.title
            }

            Scaffold(
                topBar = {
                    AcademyTopBar(
                        title = displayTitle,
                        canNavigateBack = canNavigateBack,
                        onNavigateBack = { viewModel.navigateBack() },
                        isDarkMode = isDarkMode,
                        onToggleDarkMode = { viewModel.toggleDarkMode() },
                        isAdmin = currentUser?.role == "ADMIN",
                        onAdminClick = {
                            if (currentUser?.role == "ADMIN") {
                                viewModel.navigateTo(Screen.AdminDashboard)
                            } else {
                                showOwnerLoginDialog = true
                            }
                        }
                    )
                },
                bottomBar = {
                    if (showBottomNav) {
                        AcademyBottomNav(
                            currentScreen = currentScreen,
                            onSelectScreen = { screen ->
                                if (currentScreen != screen) {
                                    viewModel.navigateTo(screen)
                                }
                            }
                        )
                    }
                },
                snackbarHost = { SnackbarHost(snackbarHostState) },
                contentWindowInsets = WindowInsets.safeDrawing
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when {
                        // 1. Not logged in -> Must Login or Register
                        isNotLoggedIn -> {
                            when (currentScreen) {
                                Screen.Register -> RegisterScreen(viewModel)
                                Screen.AdminDashboard -> AdminDashboardScreen(viewModel)
                                else -> LoginScreen(viewModel)
                            }
                        }

                        // 2. Student logged in but NOT subscribed -> Must activate code or pay
                        isLockedStudent -> {
                            when (currentScreen) {
                                Screen.PaymentForm -> PaymentFormScreen(viewModel)
                                Screen.AdminDashboard -> AdminDashboardScreen(viewModel)
                                else -> SubscriptionGateScreen(viewModel)
                            }
                        }

                        // 3. Fully Authorized (Admin or Subscribed Student)
                        else -> {
                            when (currentScreen) {
                                Screen.Home -> HomeScreen(viewModel)
                                Screen.Subjects -> SubjectsScreen(viewModel)
                                Screen.Units -> UnitsScreen(viewModel)
                                Screen.LessonDetail -> LessonDetailScreen(viewModel)
                                Screen.ExplainMe -> ExplainMeScreen(viewModel)
                                Screen.SmartTutor -> SmartTutorScreen(viewModel)
                                Screen.SolveMyQuestion -> SolveMyQuestionScreen(viewModel)
                                Screen.Exams -> ExamsScreen(viewModel)
                                Screen.ExamSession -> ExamSessionScreen(viewModel)
                                Screen.ExamResult -> ExamResultScreen(viewModel)
                                Screen.QuestionBank -> QuestionBankScreen(viewModel)
                                Screen.Subscription -> SubscriptionScreen(viewModel)
                                Screen.PaymentForm -> PaymentFormScreen(viewModel)
                                Screen.ActivateCode -> ActivateCodeScreen(viewModel)
                                Screen.SubscriptionGate -> SubscriptionGateScreen(viewModel)
                                Screen.Search -> SearchScreen(viewModel)
                                Screen.Favorites -> FavoritesScreen(viewModel)
                                Screen.Progress -> ProgressScreen(viewModel)
                                Screen.Profile -> ProfileScreen(viewModel)
                                Screen.Login -> LoginScreen(viewModel)
                                Screen.Register -> RegisterScreen(viewModel)
                                Screen.AdminDashboard -> AdminDashboardScreen(viewModel)
                                Screen.AdminStudents -> AdminStudentsScreen(viewModel)
                                Screen.AdminPayments -> AdminPaymentsScreen(viewModel)
                                Screen.AdminCodes -> AdminCodesScreen(viewModel)
                                Screen.AdminContent -> AdminContentScreen(viewModel)
                                Screen.AdminNotifications -> AdminNotificationsScreen(viewModel)
                                Screen.AdminLogs -> AdminLogsScreen(viewModel)
                            }
                        }
                    }
                }
            }

            if (showOwnerLoginDialog) {
                var adminPassword by remember { mutableStateOf("") }
                var errorMessage by remember { mutableStateOf<String?>(null) }

                AlertDialog(
                    onDismissRequest = { showOwnerLoginDialog = false },
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "👑", fontSize = 22.sp)
                            Text(text = "دخول المالك والمدير العام", fontWeight = FontWeight.Bold)
                        }
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "هذا القسم مخصص لإدارة الأكاديمية والاشتراكات والمحافظ وأكواد التفعيل.",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )

                            OutlinedTextField(
                                value = adminPassword,
                                onValueChange = { adminPassword = it },
                                label = { Text("كلمة مرور المالك") },
                                placeholder = { Text("أدخل كلمة المرور (الافتراضية: admin123)") },
                                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (errorMessage != null) {
                                Text(
                                    text = errorMessage ?: "",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            // Quick login shortcut for Owner
                            Surface(
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.login("782916997", "admin123") {
                                            showOwnerLoginDialog = false
                                            viewModel.navigateTo(Screen.AdminDashboard)
                                        }
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(text = "⚡", fontSize = 18.sp)
                                    Column {
                                        Text(
                                            text = "الدخول السريع كمالك (Admin)",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "الحساب: 782916997 • فتح لوحة التحكم فوراً",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                                        )
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (adminPassword.isNotBlank()) {
                                    viewModel.login("782916997", adminPassword) {
                                        showOwnerLoginDialog = false
                                        viewModel.navigateTo(Screen.AdminDashboard)
                                    }
                                } else {
                                    errorMessage = "يرجى كتابة كلمة المرور"
                                }
                            }
                        ) {
                            Text("دخول")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showOwnerLoginDialog = false }) {
                            Text("إلغاء")
                        }
                    }
                )
            }
        }
    }
}
