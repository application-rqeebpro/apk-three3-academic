package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.DatabaseSeeder
import com.example.data.local.entities.*
import com.example.data.remote.AttachedFile
import com.example.data.remote.GeminiRepository
import com.example.data.repository.AppRepository
import com.example.data.solver.EducationalSolution
import com.example.data.solver.EducationalSolverEngine
import com.example.data.solver.ProblemVerification
import com.example.data.solver.toFormattedEducationalText
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

sealed class Screen(val title: String) {
    object Home : Screen("الرئيسية")
    object Subjects : Screen("المواد الدراسية")
    object Units : Screen("الوحدات الدراسية")
    object LessonDetail : Screen("شرح الدرس")
    object SmartTutor : Screen("المعلم الذكي")
    object SolveMyQuestion : Screen("حل سؤالي")
    object Exams : Screen("اختبر نفسك")
    object ExamSession : Screen("جلسة الاختبار")
    object ExamResult : Screen("نتيجة الاختبار")
    object QuestionBank : Screen("بنك الأسئلة")
    object Subscription : Screen("الاشتراكات")
    object PaymentForm : Screen("الدفع بالمحفظة")
    object ActivateCode : Screen("تفعيل الكود")
    object Search : Screen("البحث")
    object Favorites : Screen("المفضلة")
    object Progress : Screen("تقدمي الدراسي")
    object Profile : Screen("حسابي")
    object Login : Screen("تسجيل الدخول")
    object Register : Screen("إنشاء حساب جديد")
    object ExplainMe : Screen("خدمة اشرح لي")
    object SubscriptionGate : Screen("تفعيل الاشتراك")
    object AdminDashboard : Screen("لوحة تحكم المالك")
    object AdminStudents : Screen("إدارة الطلاب")
    object AdminPayments : Screen("طلبات الدفع")
    object AdminCodes : Screen("أكواد الاشتراكات")
    object AdminContent : Screen("إدارة المحتوى")
    object AdminNotifications : Screen("إرسال الإشعارات")
    object AdminLogs : Screen("سجل العمليات")
    object ScientificCalculator : Screen("الحاسبة العلمية")
}

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class, kotlinx.coroutines.FlowPreview::class)
class MainViewModel(application: Application) : AndroidViewModel(application) {

    val database = AppDatabase.getInstance(application)
    val repository = AppRepository(database)
    val geminiRepo = GeminiRepository()

    // Screen navigation stack
    private val _screenStack = MutableStateFlow<List<Screen>>(listOf(Screen.Home))
    val currentScreen: StateFlow<Screen> = _screenStack.map { it.lastOrNull() ?: Screen.Home }
        .stateIn(viewModelScope, SharingStarted.Eagerly, Screen.Home)

    // User State
    val currentUser = repository.currentUser
    val activeSubscription = currentUser.flatMapLatest { user ->
        if (user != null) repository.getActiveSubscriptionForUser(user.id) else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val isSubscribed: StateFlow<Boolean> = activeSubscription.map { sub ->
        sub != null && sub.endDate > System.currentTimeMillis()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Dark mode toggle
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode = _isDarkMode.asStateFlow()

    // Curriculum selections
    val allSubjects = repository.getAllSubjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedSubject = MutableStateFlow<SubjectEntity?>(null)
    val selectedSubject = _selectedSubject.asStateFlow()

    val unitsForSelectedSubject = _selectedSubject.flatMapLatest { sub ->
        if (sub != null) repository.getUnitsForSubject(sub.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedUnit = MutableStateFlow<UnitEntity?>(null)
    val selectedUnit = _selectedUnit.asStateFlow()

    val lessonsForSelectedUnit = _selectedUnit.flatMapLatest { unit ->
        if (unit != null) repository.getLessonsForUnit(unit.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedLesson = MutableStateFlow<LessonEntity?>(null)
    val selectedLesson = _selectedLesson.asStateFlow()

    // Explanation Level: "بسيط جداً", "عادي", "مفصل", "أمثلة", "اختبرني"
    private val _explanationLevel = MutableStateFlow("عادي")
    val explanationLevel = _explanationLevel.asStateFlow()

    // Easier Explanation dynamic text
    private val _simplerExplanationText = MutableStateFlow<String?>(null)
    val simplerExplanationText = _simplerExplanationText.asStateFlow()
    val isGeneratingSimpler = MutableStateFlow(false)

    // Smart Tutor Chat
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                text = "مرحباً بك يا بطل! أنا معلمك الذكي الخاص بمنهج الصف الثالث الثانوي اليمني 🇾🇪. اسألني عن أي قانون، أو مسألة، أو اطلب مني شرح أي فكرة خطوة بخطوة. وإذا لم تفهم سأبسطها لك حتى تستوعبها تماماً!",
                isUser = false
            )
        )
    )
    val chatMessages = _chatMessages.asStateFlow()
    val isTutorThinking = MutableStateFlow(false)

    // Solver
    val solverQuestionInput = MutableStateFlow("")
    val solverSelectedSubject = MutableStateFlow("الرياضيات")
    val solverImageBitmap = MutableStateFlow<Bitmap?>(null)
    val solverAttachedFile = MutableStateFlow<AttachedFile?>(null)
    val solverResultText = MutableStateFlow<String?>(null)
    val solverStructuredResult = MutableStateFlow<EducationalSolution?>(null)
    val isSolving = MutableStateFlow(false)
    val isVerifyingSolution = MutableStateFlow(false)
    val verificationReport = MutableStateFlow<ProblemVerification?>(null)

    // Search
    val searchQuery = MutableStateFlow("")
    val searchResults = searchQuery.debounce(300).flatMapLatest { q ->
        if (q.isBlank()) flowOf(emptyList()) else repository.searchLessons(q)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Exams
    val allExams = repository.getAllExams()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    private val _currentExam = MutableStateFlow<ExamEntity?>(null)
    val currentExam = _currentExam.asStateFlow()

    val examQuestions = MutableStateFlow<List<QuestionEntity>>(emptyList())
    val userExamAnswers = MutableStateFlow<Map<Long, String>>(emptyMap())
    val currentExamResult = MutableStateFlow<ExamResultEntity?>(null)
    val isExamSubmitted = MutableStateFlow(false)

    // Question Bank Filters
    val questionBankFilterSubject = MutableStateFlow<Long?>(null)
    val questionBankFilterDifficulty = MutableStateFlow<String?>(null)
    val allQuestions = repository.getAllQuestions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // User Progress & Favorites
    val completedLessonIds = currentUser.flatMapLatest { u ->
        if (u != null) repository.getCompletedLessonIds(u.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userFavorites = currentUser.flatMapLatest { u ->
        if (u != null) repository.getFavoritesForUser(u.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userExamResults = currentUser.flatMapLatest { u ->
        if (u != null) repository.getExamResultsForUser(u.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Notifications
    val notifications = repository.getAllNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin state
    val adminStudentCount = repository.getStudentCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val adminActiveStudentCount = repository.getActiveStudentCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val adminActiveSubCount = repository.getAllActiveSubscriptions().map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val adminExpiredSubCount = repository.getAllExpiredSubscriptions().map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val adminPendingPaymentCount = repository.getPendingPaymentCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val adminAllPayments = repository.getAllPaymentRequests()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val adminAllCodes = repository.getAllCodes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val adminUnusedCodeCount = repository.getUnusedCodeCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val adminUsedCodeCount = repository.getUsedCodeCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val adminAllUsers = repository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val adminActivityLogs = repository.getAllActivityLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteAccount(userId: Long) {
        viewModelScope.launch {
            repository.deleteAccount(userId)
        }
    }

    // Notification toast / alert
    val toastMessage = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch {
            DatabaseSeeder.seedDatabaseIfEmpty(database)
        }
    }

    // ---------------------------------------------------------
    // NAVIGATION
    // ---------------------------------------------------------

    fun navigateTo(screen: Screen) {
        _screenStack.update { it + screen }
    }

    fun navigateBack(): Boolean {
        if (_screenStack.value.size > 1) {
            _screenStack.update { it.dropLast(1) }
            return true
        }
        return false
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    // ---------------------------------------------------------
    // AUTHENTICATION
    // ---------------------------------------------------------

    fun login(
        phoneOrEmail: String,
        pass: String,
        onError: ((String) -> Unit)? = null,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.login(phoneOrEmail, pass)
            result.onSuccess {
                toastMessage.value = "أهلاً بك يا ${it.fullName}"
                onSuccess()
            }.onFailure {
                val err = it.message ?: "فشل تسجيل الدخول"
                toastMessage.value = err
                onError?.invoke(err)
            }
        }
    }

    fun register(name: String, phoneOrEmail: String, pass: String, confirmPass: String, track: String, onSuccess: () -> Unit) {
        if (name.isBlank() || phoneOrEmail.isBlank() || pass.isBlank()) {
            toastMessage.value = "يرجى ملء جميع الحقول المطلوبة"
            return
        }
        if (pass != confirmPass) {
            toastMessage.value = "كلمتا المرور غير متطابقتين"
            return
        }
        viewModelScope.launch {
            val result = repository.register(name, phoneOrEmail, pass, track = track)
            result.onSuccess {
                toastMessage.value = "تم إنشاء الحساب بنجاح!"
                onSuccess()
            }.onFailure {
                toastMessage.value = it.message ?: "فشل إنشاء الحساب"
            }
        }
    }

    fun logout() {
        repository.logout()
        _screenStack.value = listOf(Screen.Home)
        toastMessage.value = "تم تسجيل الخروج"
    }

    fun resetPassword(phoneOrEmail: String, newPass: String, onDone: () -> Unit) {
        viewModelScope.launch {
            val res = repository.resetPassword(phoneOrEmail, newPass)
            res.onSuccess {
                toastMessage.value = it
                onDone()
            }.onFailure {
                toastMessage.value = it.message
            }
        }
    }

    // ---------------------------------------------------------
    // CURRICULUM ACTIONS
    // ---------------------------------------------------------

    fun selectSubject(subject: SubjectEntity) {
        _selectedSubject.value = subject
        _selectedUnit.value = null
        _selectedLesson.value = null
        navigateTo(Screen.Units)
    }

    fun selectUnit(unit: UnitEntity) {
        _selectedUnit.value = unit
    }

    fun selectLesson(lesson: LessonEntity) {
        _selectedLesson.value = lesson
        _simplerExplanationText.value = null
        _explanationLevel.value = "عادي"
        navigateTo(Screen.LessonDetail)
    }

    fun navigateToAdjacentLesson(next: Boolean) {
        val current = _selectedLesson.value ?: return
        viewModelScope.launch {
            val unitLessons = repository.getLessonsForUnit(current.unitId).firstOrNull() ?: emptyList()
            val index = unitLessons.indexOfFirst { it.id == current.id }
            if (index != -1) {
                val targetIndex = if (next) index + 1 else index - 1
                if (targetIndex in unitLessons.indices) {
                    selectLesson(unitLessons[targetIndex])
                }
            }
        }
    }

    fun setExplanationLevel(level: String) {
        _explanationLevel.value = level
    }

    fun requestSimplerExplanation() {
        val lesson = _selectedLesson.value ?: return
        if (lesson.easierExplanation.isNotBlank()) {
            _simplerExplanationText.value = lesson.easierExplanation
        } else {
            viewModelScope.launch {
                isGeneratingSimpler.value = true
                val result = geminiRepo.explainSimpler(lesson.title, lesson.simplifiedExplanation)
                _simplerExplanationText.value = result
                isGeneratingSimpler.value = false
            }
        }
    }

    fun explainCustomTopic(prompt: String, subject: String) {
        viewModelScope.launch {
            isGeneratingSimpler.value = true
            val result = geminiRepo.explainConceptSimpler(prompt, subject)
            _simplerExplanationText.value = result
            isGeneratingSimpler.value = false
        }
    }

    // ---------------------------------------------------------
    // SMART TUTOR CHAT
    // ---------------------------------------------------------

    fun sendTutorMessage(text: String) {
        if (text.isBlank()) return
        val userMsg = ChatMessage(text = text.trim(), isUser = true)
        _chatMessages.update { it + userMsg }

        viewModelScope.launch {
            isTutorThinking.value = true
            val history = _chatMessages.value.map { it.text to it.isUser }
            val replyText = geminiRepo.askSmartTutor(
                chatHistory = history,
                currentPrompt = text,
                subjectContext = _selectedSubject.value?.name
            )
            _chatMessages.update { it + ChatMessage(text = replyText, isUser = false) }
            isTutorThinking.value = false
        }
    }

    // ---------------------------------------------------------
    // PROBLEM SOLVER (حل سؤالي)
    // ---------------------------------------------------------

    fun solveQuestion() {
        val text = solverQuestionInput.value.trim()
        val bmp = solverImageBitmap.value
        val file = solverAttachedFile.value
        if (text.isBlank() && bmp == null && file == null) {
            toastMessage.value = "يرجى كتابة السؤال أو تصوير المسألة أو إرفاق ملفها"
            return
        }

        viewModelScope.launch {
            isSolving.value = true
            verificationReport.value = null
            val solution = geminiRepo.solveStudentQuestionStructured(
                questionText = text,
                imageBitmap = bmp,
                attachedFile = file,
                subject = solverSelectedSubject.value
            )
            solverStructuredResult.value = solution
            solverResultText.value = solution.toFormattedEducationalText()
            isSolving.value = false
        }
    }

    fun verifyCurrentSolution() {
        val current = solverStructuredResult.value ?: return
        viewModelScope.launch {
            isVerifyingSolution.value = true
            val verified = EducationalSolverEngine.verifySolution(current)
            verificationReport.value = verified
            isVerifyingSolution.value = false
            toastMessage.value = if (verified.isValid) "✓ تم التحقق: الحل مطابق للمعايير الوزارية 100%" else "⚠ تم رصد ملاحظات أثناء التحقق المستقل"
        }
    }

    fun requestEasierExplanationForCurrentProblem() {
        val current = solverStructuredResult.value ?: return
        if (current.easierExplanation.isNotBlank()) {
            _simplerExplanationText.value = current.easierExplanation
        } else {
            explainCustomTopic("اشرح لي حل هذه المسألة بأبسط أسلوب ممكن:\n${current.finalAnswer}", current.subject)
        }
        navigateTo(Screen.ExplainMe)
    }

    fun clearSolver() {
        solverQuestionInput.value = ""
        solverImageBitmap.value = null
        solverAttachedFile.value = null
        solverResultText.value = null
        solverStructuredResult.value = null
        verificationReport.value = null
    }

    // ---------------------------------------------------------
    // EXAMS
    // ---------------------------------------------------------

    fun startExam(exam: ExamEntity) {
        _currentExam.value = exam
        isExamSubmitted.value = false
        userExamAnswers.value = emptyMap()
        currentExamResult.value = null

        viewModelScope.launch {
            val questions = repository.getRandomQuestionsForExam(exam.subjectId, exam.totalQuestions)
            examQuestions.value = questions
            navigateTo(Screen.ExamSession)
        }
    }

    fun startRandomExam(subjectId: Long, count: Int = 5) {
        val pseudoExam = ExamEntity(
            id = 999,
            subjectId = subjectId,
            title = "اختبار عشوائي مخصص",
            totalQuestions = count,
            passingScore = 60
        )
        _currentExam.value = pseudoExam
        isExamSubmitted.value = false
        userExamAnswers.value = emptyMap()
        currentExamResult.value = null

        viewModelScope.launch {
            val questions = repository.getRandomQuestionsForExam(subjectId, count)
            examQuestions.value = questions
            navigateTo(Screen.ExamSession)
        }
    }

    fun setExamAnswer(questionId: Long, answer: String) {
        if (!isExamSubmitted.value) {
            userExamAnswers.update { it + (questionId to answer) }
        }
    }

    fun submitExam() {
        val exam = _currentExam.value ?: return
        val questions = examQuestions.value
        val answers = userExamAnswers.value

        var correct = 0
        var wrong = 0

        questions.forEach { q ->
            val userAns = answers[q.id]?.trim()
            if (userAns != null && userAns.equals(q.correctAnswer.trim(), ignoreCase = true)) {
                correct++
            } else {
                wrong++
            }
        }

        val total = questions.size.coerceAtLeast(1)
        val scorePercent = (correct * 100) / total

        val resultEntity = ExamResultEntity(
            userId = currentUser.value?.id ?: 0,
            examId = exam.id,
            examTitle = exam.title,
            subjectName = _selectedSubject.value?.name ?: "عام",
            scorePercentage = scorePercent,
            totalQuestions = total,
            correctCount = correct,
            wrongCount = wrong
        )

        viewModelScope.launch {
            if (currentUser.value != null) {
                repository.saveExamResult(resultEntity)
            }
            currentExamResult.value = resultEntity
            isExamSubmitted.value = true
            navigateTo(Screen.ExamResult)
        }
    }

    // ---------------------------------------------------------
    // SUBSCRIPTION & PAYMENTS
    // ---------------------------------------------------------

    fun submitPayment(
        planName: String,
        durationMonths: Int,
        amountYmr: Int,
        walletName: String,
        transRef: String,
        receiptUri: String? = null,
        onSuccess: () -> Unit
    ) {
        val user = currentUser.value
        if (user == null) {
            toastMessage.value = "يرجى تسجيل الدخول أولاً"
            return
        }
        if (transRef.isBlank()) {
            toastMessage.value = "يرجى إدخال رقم الحوالة أو عملية التحويل"
            return
        }

        viewModelScope.launch {
            val res = repository.submitPaymentRequest(
                userId = user.id,
                studentName = user.fullName,
                studentPhone = user.phoneOrEmail,
                planName = planName,
                durationMonths = durationMonths,
                amountYmr = amountYmr,
                walletName = walletName,
                transferRefNumber = transRef,
                receiptImageUri = receiptUri
            )
            res.onSuccess {
                toastMessage.value = "تم إرسال طلب الاشتراك بنجاح! طلبك الآن قيد المراجعة."
                onSuccess()
            }.onFailure {
                toastMessage.value = it.message ?: "فشل إرسال الطلب"
            }
        }
    }

    fun activateCode(code: String, onSuccess: () -> Unit) {
        val user = currentUser.value
        if (user == null) {
            toastMessage.value = "يرجى تسجيل الدخول أولاً"
            return
        }
        if (code.isBlank()) {
            toastMessage.value = "يرجى إدخال الكود"
            return
        }

        viewModelScope.launch {
            val res = repository.activateCodeByStudent(
                userId = user.id,
                studentName = user.fullName,
                studentPhone = user.phoneOrEmail,
                enteredCode = code
            )
            res.onSuccess {
                toastMessage.value = it
                onSuccess()
            }.onFailure {
                toastMessage.value = it.message ?: "فشل تفعيل الكود"
            }
        }
    }

    // ---------------------------------------------------------
    // ADMIN ACTIONS
    // ---------------------------------------------------------

    fun adminReviewPayment(paymentId: Long, approve: Boolean, createCode: Boolean = false, reason: String? = null) {
        val adminName = currentUser.value?.fullName ?: "المدير العام"
        viewModelScope.launch {
            val res = repository.reviewPaymentRequest(paymentId, approve, createCode, reason, adminName)
            toastMessage.value = res.getOrNull() ?: res.exceptionOrNull()?.message
        }
    }

    fun adminDirectActivate(studentId: Long, studentName: String, months: Int, planName: String, price: Int) {
        val adminName = currentUser.value?.fullName ?: "المدير العام"
        viewModelScope.launch {
            val res = repository.activateSubscriptionDirectly(studentId, studentName, months, planName, price, adminName)
            res.onSuccess {
                toastMessage.value = "تم تفعيل الاشتراك للطالب $studentName بنجاح!"
            }.onFailure {
                toastMessage.value = it.message
            }
        }
    }

    fun adminExtendSubscription(studentId: Long, studentName: String, days: Int) {
        val adminName = currentUser.value?.fullName ?: "المدير العام"
        viewModelScope.launch {
            val res = repository.extendSubscription(studentId, studentName, days, adminName)
            toastMessage.value = res.getOrNull() ?: res.exceptionOrNull()?.message
        }
    }

    fun adminGenerateCode(months: Int, planName: String, price: Int, tiedPhone: String?, onDone: (SubscriptionCodeEntity) -> Unit) {
        val adminName = currentUser.value?.fullName ?: "المدير العام"
        viewModelScope.launch {
            val code = repository.generateSubscriptionCode(months, planName, price, tiedPhone, adminName)
            toastMessage.value = "تم إنشاء الكود: ${code.code}"
            onDone(code)
        }
    }

    fun adminCancelCode(codeId: Long, code: String) {
        val adminName = currentUser.value?.fullName ?: "المدير العام"
        viewModelScope.launch {
            repository.cancelSubscriptionCode(codeId, code, adminName)
            toastMessage.value = "تم إلغاء الكود $code"
        }
    }

    fun adminBroadcastNotification(title: String, message: String, target: String) {
        val adminName = currentUser.value?.fullName ?: "المدير العام"
        viewModelScope.launch {
            repository.sendBroadcastNotification(title, message, target, adminName)
            toastMessage.value = "تم إرسال الإشعار لجميع الطلاب"
        }
    }

    fun adminAddSubject(name: String, desc: String, track: String) {
        viewModelScope.launch {
            repository.addSubject(name, desc, track)
            toastMessage.value = "تمت إضافة المادة بنجاح"
        }
    }

    fun adminDeleteSubject(subject: SubjectEntity) {
        viewModelScope.launch {
            repository.deleteSubject(subject)
            toastMessage.value = "تم حذف المادة"
        }
    }

    fun adminAddLesson(lesson: LessonEntity) {
        viewModelScope.launch {
            repository.addLesson(lesson)
            toastMessage.value = "تمت إضافة الدرس بنجاح"
        }
    }

    fun adminUpdateLesson(lesson: LessonEntity) {
        viewModelScope.launch {
            repository.updateLesson(lesson)
            toastMessage.value = "تم تحديث بيانات الدرس بنجاح"
        }
    }

    fun adminDeleteLesson(lesson: LessonEntity) {
        viewModelScope.launch {
            repository.deleteLesson(lesson)
            toastMessage.value = "تم حذف الدرس"
        }
    }

    fun adminAddUnit(unit: UnitEntity) {
        viewModelScope.launch {
            repository.addUnit(unit)
            toastMessage.value = "تمت إضافة الوحدة بنجاح"
        }
    }

    fun adminUpdateUnit(unit: UnitEntity) {
        viewModelScope.launch {
            repository.updateUnit(unit)
            toastMessage.value = "تم تحديث بيانات الوحدة"
        }
    }

    fun adminDeleteUnit(unit: UnitEntity) {
        viewModelScope.launch {
            repository.deleteUnit(unit)
            toastMessage.value = "تم حذف الوحدة"
        }
    }

    fun adminAddQuestion(question: QuestionEntity) {
        viewModelScope.launch {
            repository.addQuestion(question)
            toastMessage.value = "تمت إضافة السؤال بنجاح"
        }
    }

    fun adminDeleteQuestion(question: QuestionEntity) {
        viewModelScope.launch {
            repository.deleteQuestion(question)
            toastMessage.value = "تم حذف السؤال"
        }
    }

    fun adminAddExam(exam: ExamEntity) {
        viewModelScope.launch {
            repository.addExam(exam)
            toastMessage.value = "تمت إضافة الاختبار بنجاح"
        }
    }

    // ---------------------------------------------------------
    // PROGRESS & FAVORITES
    // ---------------------------------------------------------

    fun toggleLessonCompletion(lessonId: Long, subjectId: Long, completed: Boolean) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.toggleLessonProgress(user.id, lessonId, subjectId, completed)
        }
    }

    fun toggleFavorite(type: String, itemId: Long, title: String, subtitle: String, isFav: Boolean) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.toggleFavorite(user.id, type, itemId, title, subtitle, isFav)
            toastMessage.value = if (isFav) "تمت الإضافة للمفضلة ⭐" else "تمت الإزالة من المفضلة"
        }
    }
}
