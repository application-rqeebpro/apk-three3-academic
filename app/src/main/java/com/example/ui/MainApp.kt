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
import com.example.ui.screens.auth.OwnerLoginDialog
import com.example.ui.screens.calculator.ScientificCalculatorScreen
import com.example.ui.screens.materials.StudyMaterialsScreen
import com.example.ui.screens.materials.MyContentScreen
import com.example.ui.screens.profile.StudentNotificationsScreen
import com.example.ui.theme.MyApplicationTheme

@Composable
fun MainApp(viewModel: MainViewModel) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val isSubscribed by viewModel.isSubscribed.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var showOwnerIconUnlocked by remember { mutableStateOf(false) }
    var showOwnerLoginFromTopBar by remember { mutableStateOf(false) }

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
            val canNavigateBack = if (currentScreen == Screen.ScientificCalculator) true else (!isNotLoggedIn && !isLockedStudent && currentScreen != Screen.Home)
            BackHandler(enabled = canNavigateBack) {
                viewModel.navigateBack()
            }

            val showBottomNav = !isNotLoggedIn && !isLockedStudent && currentScreen in listOf(
                Screen.Home,
                Screen.MyContent,
                Screen.Notifications,
                Screen.Profile
            )

            val displayTitle = when {
                currentScreen == Screen.ScientificCalculator -> "🧮 الحاسبة العلمية"
                isNotLoggedIn -> if (currentScreen == Screen.Register) "إنشاء حساب طالب جديد" else "تسجيل الدخول - رقيب للتعليم الثانوي"
                isLockedStudent -> "تفعيل اشتراك رقيب للتعليم الثانوي 🇾🇪"
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
                        showOwnerIcon = (currentUser?.role == "ADMIN") || showOwnerIconUnlocked,
                        onAdminClick = {
                            if (currentUser?.role == "ADMIN") {
                                viewModel.navigateTo(Screen.AdminDashboard)
                            } else {
                                showOwnerLoginFromTopBar = true
                            }
                        },
                        onOwnerHoldSuccess = {
                            showOwnerIconUnlocked = true
                            showOwnerLoginFromTopBar = true
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
                                Screen.ScientificCalculator -> ScientificCalculatorScreen(viewModel)
                                else -> LoginScreen(viewModel)
                            }
                        }

                        // 2. Student logged in but NOT subscribed -> Must activate code or pay
                        isLockedStudent -> {
                            when (currentScreen) {
                                Screen.PaymentForm -> PaymentFormScreen(viewModel)
                                Screen.ScientificCalculator -> ScientificCalculatorScreen(viewModel)
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
                                Screen.AdminDashboard -> {
                                    if (currentUser?.role == "ADMIN") {
                                        AdminDashboardScreen(viewModel)
                                    } else {
                                        LaunchedEffect(Unit) { viewModel.navigateTo(Screen.Home) }
                                        HomeScreen(viewModel)
                                    }
                                }
                                Screen.AdminStudents -> {
                                    if (currentUser?.role == "ADMIN") {
                                        AdminStudentsScreen(viewModel)
                                    } else {
                                        LaunchedEffect(Unit) { viewModel.navigateTo(Screen.Home) }
                                        HomeScreen(viewModel)
                                    }
                                }
                                Screen.AdminPayments -> {
                                    if (currentUser?.role == "ADMIN") {
                                        AdminPaymentsScreen(viewModel)
                                    } else {
                                        LaunchedEffect(Unit) { viewModel.navigateTo(Screen.Home) }
                                        HomeScreen(viewModel)
                                    }
                                }
                                Screen.AdminCodes -> {
                                    if (currentUser?.role == "ADMIN") {
                                        AdminCodesScreen(viewModel)
                                    } else {
                                        LaunchedEffect(Unit) { viewModel.navigateTo(Screen.Home) }
                                        HomeScreen(viewModel)
                                    }
                                }
                                Screen.AdminContent -> {
                                    if (currentUser?.role == "ADMIN") {
                                        AdminContentScreen(viewModel)
                                    } else {
                                        LaunchedEffect(Unit) { viewModel.navigateTo(Screen.Home) }
                                        HomeScreen(viewModel)
                                    }
                                }
                                Screen.AdminNotifications -> {
                                    if (currentUser?.role == "ADMIN") {
                                        AdminNotificationsScreen(viewModel)
                                    } else {
                                        LaunchedEffect(Unit) { viewModel.navigateTo(Screen.Home) }
                                        HomeScreen(viewModel)
                                    }
                                }
                                Screen.AdminLogs -> {
                                    if (currentUser?.role == "ADMIN") {
                                        AdminLogsScreen(viewModel)
                                    } else {
                                        LaunchedEffect(Unit) { viewModel.navigateTo(Screen.Home) }
                                        HomeScreen(viewModel)
                                    }
                                }
                                Screen.AdminStudyMaterials -> {
                                    if (currentUser?.role == "ADMIN") {
                                        AdminStudyMaterialsScreen(viewModel)
                                    } else {
                                        LaunchedEffect(Unit) { viewModel.navigateTo(Screen.Home) }
                                        HomeScreen(viewModel)
                                    }
                                }
                                Screen.StudyMaterials -> StudyMaterialsScreen(viewModel)
                                Screen.PdfViewer -> StudyMaterialsScreen(viewModel)
                                Screen.ScientificCalculator -> ScientificCalculatorScreen(viewModel)
                                Screen.MyContent -> MyContentScreen(viewModel)
                                Screen.Notifications -> StudentNotificationsScreen(viewModel)
                            }
                        }
                    }
                }
            }

            if (showOwnerLoginFromTopBar) {
                OwnerLoginDialog(
                    viewModel = viewModel,
                    onDismiss = { showOwnerLoginFromTopBar = false },
                    onSuccess = {
                        showOwnerLoginFromTopBar = false
                        viewModel.navigateTo(Screen.AdminDashboard)
                    }
                )
            }
        }
    }
}
