package com.example.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.auth.RegisterScreen
import com.example.ui.screens.calculator.ScientificCalculatorScreen
import com.example.ui.screens.curriculum.LessonDetailScreen
import com.example.ui.screens.curriculum.SubjectsScreen
import com.example.ui.screens.curriculum.UnitsScreen
import com.example.ui.screens.exams.*
import com.example.ui.screens.explain.ExplainMeScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.pdf.PdfViewerScreen
import com.example.ui.screens.profile.*
import com.example.ui.screens.smarttutor.SmartTutorScreen
import com.example.ui.screens.solver.SolveMyQuestionScreen
import com.example.ui.screens.subscription.*
import com.example.ui.theme.Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val currentScreen by viewModel.currentScreen.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var showOwnerLoginDialog by remember { mutableStateOf(false) }
    var ownerPasswordInput by remember { mutableStateOf("") }
    var ownerLoginError by remember { mutableStateOf("") }

    // Toast handler
    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    // Back handling
    BackHandler(enabled = currentScreen != Screen.Home) {
        viewModel.navigateBack()
    }

    Theme(darkTheme = isDarkMode) {
        Scaffold(
            topBar = {
                if (currentScreen == Screen.Home) {
                    TopAppBar(
                        title = {
                            Text("أكاديمية رقيب", fontWeight = FontWeight.ExtraBold)
                        },
                        actions = {
                            IconButton(onClick = { viewModel.navigateTo(Screen.Login) }) {
                                Icon(Icons.Default.AccountCircle, contentDescription = "تسجيل الدخول / الحساب")
                            }
                            IconButton(onClick = {
                                if (currentUser?.role == "ADMIN") {
                                    viewModel.navigateTo(Screen.AdminDashboard)
                                } else {
                                    showOwnerLoginDialog = true
                                }
                            }) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = "دخول المالك")
                            }
                            IconButton(onClick = { viewModel.navigateTo(Screen.Search) }) {
                                Icon(Icons.Default.Search, contentDescription = "بحث")
                            }
                        }
                    )
                }
            },
            bottomBar = {
                // Show bottom bar on primary tabs
                val primaryScreens = listOf(Screen.Home, Screen.Subjects, Screen.PdfViewer, Screen.SolveMyQuestion, Screen.Profile)
                if (currentScreen in primaryScreens) {
                    NavigationBar {
                        NavigationBarItem(
                            selected = currentScreen == Screen.Home,
                            onClick = { viewModel.navigateTo(Screen.Home) },
                            icon = { Icon(Icons.Default.Home, contentDescription = null) },
                            label = { Text("الرئيسية") }
                        )
                        NavigationBarItem(
                            selected = currentScreen == Screen.Subjects,
                            onClick = { viewModel.navigateTo(Screen.Subjects) },
                            icon = { Icon(Icons.Default.MenuBook, contentDescription = null) },
                            label = { Text("المواد") }
                        )
                        NavigationBarItem(
                            selected = currentScreen == Screen.PdfViewer,
                            onClick = { viewModel.navigateTo(Screen.PdfViewer) },
                            icon = { Icon(Icons.Default.Description, contentDescription = null) },
                            label = { Text("الملازم") }
                        )
                        NavigationBarItem(
                            selected = currentScreen == Screen.SolveMyQuestion,
                            onClick = { viewModel.navigateTo(Screen.SolveMyQuestion) },
                            icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null) },
                            label = { Text("حل سؤالي") }
                        )
                        NavigationBarItem(
                            selected = currentScreen == Screen.Profile,
                            onClick = { viewModel.navigateTo(Screen.Profile) },
                            icon = { Icon(Icons.Default.Person, contentDescription = null) },
                            label = { Text("حسابي") }
                        )
                    }
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when (currentScreen) {
                    is Screen.Home -> HomeScreen(viewModel = viewModel, onNavigate = { viewModel.navigateTo(it) })
                    is Screen.Subjects -> SubjectsScreen(viewModel = viewModel, onNavigate = { viewModel.navigateTo(it) })
                    is Screen.Units -> UnitsScreen(viewModel = viewModel, onBack = { viewModel.navigateBack() }, onNavigate = { viewModel.navigateTo(it) })
                    is Screen.LessonDetail -> LessonDetailScreen(viewModel = viewModel, onBack = { viewModel.navigateBack() }, onNavigate = { viewModel.navigateTo(it) })
                    is Screen.PdfViewer -> PdfViewerScreen(viewModel = viewModel, onBack = { viewModel.navigateBack() }, onNavigate = { viewModel.navigateTo(it) })
                    is Screen.SolveMyQuestion -> SolveMyQuestionScreen(viewModel = viewModel)
                    is Screen.Exams -> ExamsScreen(viewModel = viewModel, onNavigate = { viewModel.navigateTo(it) })
                    is Screen.ExamSession -> ExamSessionScreen(viewModel = viewModel, onNavigate = { viewModel.navigateTo(it) }, onBack = { viewModel.navigateBack() })
                    is Screen.ExamResult -> ExamResultScreen(viewModel = viewModel, onNavigate = { viewModel.navigateTo(it) })
                    is Screen.QuestionBank -> QuestionBankScreen(viewModel = viewModel, onBack = { viewModel.navigateBack() })
                    is Screen.ExplainMe -> ExplainMeScreen(viewModel = viewModel, onBack = { viewModel.navigateBack() })
                    is Screen.SmartTutor -> SmartTutorScreen(viewModel = viewModel, onBack = { viewModel.navigateBack() })
                    is Screen.ScientificCalculator -> ScientificCalculatorScreen(viewModel = viewModel, onBack = { viewModel.navigateBack() })
                    is Screen.AdminDashboard,
                    is Screen.AdminContent,
                    is Screen.AdminStudents,
                    is Screen.AdminPayments,
                    is Screen.AdminCodes,
                    is Screen.AdminNotifications,
                    is Screen.AdminLogs -> AdminDashboardScreen(viewModel = viewModel, onNavigate = { viewModel.navigateTo(it) })
                    is Screen.Profile -> ProfileScreen(viewModel = viewModel, onNavigate = { viewModel.navigateTo(it) })
                    is Screen.Favorites -> FavoritesScreen(viewModel = viewModel, onBack = { viewModel.navigateBack() })
                    is Screen.Progress -> ProgressScreen(viewModel = viewModel, onBack = { viewModel.navigateBack() })
                    is Screen.Search -> SearchScreen(viewModel = viewModel, onBack = { viewModel.navigateBack() })
                    is Screen.Subscription -> SubscriptionScreen(viewModel = viewModel, onNavigate = { viewModel.navigateTo(it) }, onBack = { viewModel.navigateBack() })
                    is Screen.PaymentForm -> PaymentFormScreen(viewModel = viewModel, onBack = { viewModel.navigateBack() }, onSuccess = { viewModel.navigateTo(Screen.Home) })
                    is Screen.ActivateCode -> ActivateCodeScreen(viewModel = viewModel, onBack = { viewModel.navigateBack() }, onSuccess = { viewModel.navigateTo(Screen.Home) })
                    is Screen.SubscriptionGate -> SubscriptionGateScreen(onNavigateToSubscription = { viewModel.navigateTo(Screen.Subscription) }, onBack = { viewModel.navigateBack() })
                    is Screen.Login -> LoginScreen(viewModel = viewModel, onNavigate = { viewModel.navigateTo(it) }, onSuccess = { viewModel.navigateTo(Screen.Home) })
                    is Screen.Register -> RegisterScreen(viewModel = viewModel, onNavigate = { viewModel.navigateTo(it) }, onSuccess = { viewModel.navigateTo(Screen.Home) })
                }
            }
        }

        // Dialog: Owner Direct Login
        if (showOwnerLoginDialog) {
            AlertDialog(
                onDismissRequest = {
                    showOwnerLoginDialog = false
                    ownerLoginError = ""
                    ownerPasswordInput = ""
                },
                title = { Text("تسجيل دخول المالك والمدير العام") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("رقم هاتف المالك: 782916997")
                        OutlinedTextField(
                            value = ownerPasswordInput,
                            onValueChange = {
                                ownerPasswordInput = it
                                ownerLoginError = ""
                            },
                            label = { Text("كلمة سر المالك") },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (ownerLoginError.isNotEmpty()) {
                            Text(ownerLoginError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (ownerPasswordInput.isBlank()) {
                                ownerLoginError = "يرجى كتابة كلمة المرور"
                            } else {
                                viewModel.login(
                                    phoneOrEmail = "782916997",
                                    pass = ownerPasswordInput,
                                    onError = { err -> ownerLoginError = err },
                                    onSuccess = {
                                        showOwnerLoginDialog = false
                                        ownerPasswordInput = ""
                                        ownerLoginError = ""
                                        viewModel.navigateTo(Screen.AdminDashboard)
                                    }
                                )
                            }
                        }
                    ) {
                        Text("دخول للوحة الإدارة")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showOwnerLoginDialog = false
                        ownerLoginError = ""
                        ownerPasswordInput = ""
                    }) {
                        Text("إلغاء")
                    }
                }
            )
        }
    }
}
