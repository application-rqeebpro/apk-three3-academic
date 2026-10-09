package com.example.ui

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.DatabaseSeeder
import com.example.data.local.entities.*
import com.example.data.pdf.PdfBookletGenerator
import com.example.data.remote.AttachedFile
import com.example.data.remote.GeminiRepository
import com.example.data.repository.AppRepository
import com.example.data.solver.EducationalSolution
import com.example.data.solver.ProblemVerification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val database = AppDatabase.getInstance(application)
    val repository = AppRepository(database)
    val geminiRepo = GeminiRepository()

    // Navigation Stack
    private val _screenStack = MutableStateFlow<List<Screen>>(listOf(Screen.Home))
    val currentScreen: StateFlow<Screen> = _screenStack
        .map { it.lastOrNull() ?: Screen.Home }
        .stateIn(viewModelScope, SharingStarted.Eagerly, Screen.Home)

    // Dark Mode
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    // Current User & Auth
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // Subscriptions
    val activeSubscription: StateFlow<SubscriptionEntity?> = _currentUser
        .flatMapLatest { user ->
            if (user != null) repository.getActiveSubscription(user.id)
            else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val isSubscribed: StateFlow<Boolean> = activeSubscription
        .map { sub -> sub != null && sub.isActive && sub.endDate > System.currentTimeMillis() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Toast & Alerts
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    fun clearToast() {
        _toastMessage.value = null
    }

    // Curriculum
    val allSubjects: StateFlow<List<SubjectEntity>> = repository.getAllSubjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedSubject = MutableStateFlow<SubjectEntity?>(null)
    val selectedSubject: StateFlow<SubjectEntity?> = _selectedSubject.asStateFlow()

    val unitsForSelectedSubject: StateFlow<List<UnitEntity>> = _selectedSubject
        .flatMapLatest { subject ->
            if (subject != null) repository.getUnitsForSubject(subject.id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedUnit = MutableStateFlow<UnitEntity?>(null)
    val selectedUnit: StateFlow<UnitEntity?> = _selectedUnit.asStateFlow()

    val lessonsForSelectedUnit: StateFlow<List<LessonEntity>> = _selectedUnit
        .flatMapLatest { unit ->
            if (unit != null) repository.getLessonsForUnit(unit.id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedLesson = MutableStateFlow<LessonEntity?>(null)
    val selectedLesson: StateFlow<LessonEntity?> = _selectedLesson.asStateFlow()

    // Document Booklets (PDF & Word files)
    val allDocumentBooklets: StateFlow<List<DocumentBookletEntity>> = repository.getAllDocumentBooklets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedBooklet = MutableStateFlow<DocumentBookletEntity?>(null)
    val selectedBooklet: StateFlow<DocumentBookletEntity?> = _selectedBooklet.asStateFlow()

    fun selectBooklet(booklet: DocumentBookletEntity) {
        _selectedBooklet.value = booklet
        navigateTo(Screen.PdfViewer)
    }

    // Explanation ("اشرح لي")
    private val _explanationLevel = MutableStateFlow("مفصل")
    val explanationLevel: StateFlow<String> = _explanationLevel.asStateFlow()

    private val _simplerExplanationText = MutableStateFlow("")
    val simplerExplanationText: StateFlow<String> = _simplerExplanationText.asStateFlow()

    private val _isGeneratingSimpler = MutableStateFlow(false)
    val isGeneratingSimpler: StateFlow<Boolean> = _isGeneratingSimpler.asStateFlow()

    // Smart Tutor Chat
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                text = "مرحباً بك يا بطل! أنا معلمك الذكي الخاص بالمنهج اليمني للصف الثالث الثانوي. اسألني في الرياضيات، الكيمياء، الفيزياء، أو أي درس وسأجيبك بأدق التفاصيل والخطوات الوزارية.",
                isUser = false
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isTutorThinking = MutableStateFlow(false)
    val isTutorThinking: StateFlow<Boolean> = _isTutorThinking.asStateFlow()

    val smartTutorSelectedSubject = MutableStateFlow("الرياضيات")
    val smartTutorAttachedImage = MutableStateFlow<Bitmap?>(null)
    val smartTutorRequestedModalities = MutableStateFlow(setOf("TEXT", "VIDEO", "IMAGE", "PDF"))
    val isGeneratingTutorPdf = MutableStateFlow(false)

    // Problem & Exam Solver ("حل سؤالي")
    val solverQuestionInput = MutableStateFlow("")
    val solverSelectedSubject = MutableStateFlow("الرياضيات")
    val solverImageBitmap = MutableStateFlow<Bitmap?>(null)
    val solverAttachedFile = MutableStateFlow<AttachedFile?>(null)
    val solverResultText = MutableStateFlow("")
    val solverStructuredResult = MutableStateFlow<EducationalSolution?>(null)
    val isSolving = MutableStateFlow(false)
    val isVerifyingSolution = MutableStateFlow(false)
    val verificationReport = MutableStateFlow<ProblemVerification?>(null)

    // Exams
    val allExams: StateFlow<List<ExamEntity>> = repository.getAllExams()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentExam = MutableStateFlow<ExamEntity?>(null)
    val currentExam: StateFlow<ExamEntity?> = _currentExam.asStateFlow()

    val examQuestions = MutableStateFlow<List<QuestionEntity>>(emptyList())
    val userExamAnswers = MutableStateFlow<Map<Long, String>>(emptyMap())
    val currentExamResult = MutableStateFlow<ExamResultEntity?>(null)
    val isExamSubmitted = MutableStateFlow(false)

    // Question Bank & Search
    val searchQuery = MutableStateFlow("")
    val questionBankFilterSubject = MutableStateFlow<Long?>(null)
    val questionBankFilterDifficulty = MutableStateFlow("ALL")

    val allQuestions: StateFlow<List<QuestionEntity>> = repository.getAllQuestions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val searchResults: StateFlow<List<LessonEntity>> = searchQuery
        .debounce(300)
        .flatMapLatest { q ->
            if (q.isBlank()) flowOf(emptyList())
            else repository.searchLessons(q)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Progress & Favorites
    val completedLessonIds: StateFlow<List<Long>> = _currentUser
        .flatMapLatest { user ->
            if (user != null) repository.getCompletedLessonIds(user.id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userFavorites: StateFlow<List<FavoriteEntity>> = _currentUser
        .flatMapLatest { user ->
            if (user != null) repository.getUserFavorites(user.id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userExamResults: StateFlow<List<ExamResultEntity>> = _currentUser
        .flatMapLatest { user ->
            if (user != null) repository.getUserExamResults(user.id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationEntity>> = repository.getAllNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin Dashboard States
    val adminAllUsers: StateFlow<List<UserEntity>> = repository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminStudentCount: StateFlow<Int> = adminAllUsers
        .map { users -> users.count { it.role == "STUDENT" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val adminActiveStudentCount: StateFlow<Int> = adminAllUsers
        .map { users -> users.count { it.role == "STUDENT" && it.isActive } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val adminAllPayments: StateFlow<List<PaymentRequestEntity>> = repository.getAllPaymentRequests()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminPendingPaymentCount: StateFlow<Int> = adminAllPayments
        .map { list -> list.count { it.status == "PENDING" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val adminAllCodes: StateFlow<List<SubscriptionCodeEntity>> = repository.getAllCodes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminUnusedCodeCount: StateFlow<Int> = adminAllCodes
        .map { list -> list.count { !it.isUsed } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val adminUsedCodeCount: StateFlow<Int> = adminAllCodes
        .map { list -> list.count { it.isUsed } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val adminAllSubscriptions: StateFlow<List<SubscriptionEntity>> = repository.getAllSubscriptions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminActiveSubCount: StateFlow<Int> = adminAllSubscriptions
        .map { list -> list.count { it.isActive && it.endDate > System.currentTimeMillis() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val adminExpiredSubCount: StateFlow<Int> = adminAllSubscriptions
        .map { list -> list.count { !it.isActive || it.endDate <= System.currentTimeMillis() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val adminActivityLogs: StateFlow<List<AdminLogEntity>> = repository.getAllLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            DatabaseSeeder.seedDatabaseIfEmpty(database)
            val admin = repository.getUserByPhoneOrEmail("782916997")
            if (admin != null && _currentUser.value == null) {
                _currentUser.value = admin
            }
        }
    }

    // Navigation Methods
    fun navigateTo(screen: Screen) {
        val current = _screenStack.value
        if (current.lastOrNull() != screen) {
            _screenStack.value = current + screen
        }
    }

    fun navigateBack(): Boolean {
        val current = _screenStack.value
        return if (current.size > 1) {
            _screenStack.value = current.dropLast(1)
            true
        } else {
            false
        }
    }

    // Auth
    fun login(phoneOrEmail: String, pass: String, onError: (String) -> Unit, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val user = repository.getUserByPhoneOrEmail(phoneOrEmail.trim())
            if (user == null) {
                onError("الحساب غير موجود")
            } else if (user.passwordHash != pass) {
                onError("كلمة المرور غير صحيحة")
            } else if (!user.isActive) {
                onError("تم تجميد هذا الحساب من قبل الإدارة")
            } else {
                _currentUser.value = user
                onSuccess()
            }
        }
    }

    fun register(
        name: String,
        phoneOrEmail: String,
        pass: String,
        confirmPass: String,
        track: String,
        subscriptionCode: String = "",
        onSuccess: () -> Unit,
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            if (pass != confirmPass) {
                onError("كلمة المرور وتأكيد كلمة المرور غير متطابقين")
                return@launch
            }
            val existing = repository.getUserByPhoneOrEmail(phoneOrEmail.trim())
            if (existing != null) {
                onError("رقم الهاتف مسجل مسبقاً، يرجى تسجيل الدخول")
                return@launch
            }
            val newUser = UserEntity(
                fullName = name.trim(),
                phoneOrEmail = phoneOrEmail.trim(),
                passwordHash = pass,
                role = "STUDENT",
                track = track
            )
            val id = repository.registerUser(newUser)
            _currentUser.value = newUser.copy(id = id)

            if (subscriptionCode.isNotBlank()) {
                val codeResult = repository.activateCodeByStudent(id, subscriptionCode.trim())
                if (codeResult.isSuccess) {
                    _toastMessage.value = "تم إنشاء الحساب وتفعيل اشتراكك بنجاح! مرحباً بك يا ${name.trim()}"
                } else {
                    _toastMessage.value = "تم إنشاء الحساب بنجاح! كود الاشتراك: " + (codeResult.exceptionOrNull()?.message ?: "غير صالح")
                }
            } else {
                _toastMessage.value = "تم إنشاء الحساب بنجاح! مرحباً بك يا ${name.trim()}"
            }
            onSuccess()
        }
    }

    fun logout() {
        _currentUser.value = null
        _screenStack.value = listOf(Screen.Home)
    }

    // Owner Operations (صلاحيات المالك الجديدة)
    fun changeOwnerPassword(newPassword: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val admin = _currentUser.value
            if (admin == null || admin.role != "ADMIN") {
                onError("غير مصرح لك بتغيير كلمة سر الإدارة")
                return@launch
            }
            if (newPassword.isBlank() || newPassword.length < 6) {
                onError("يجب أن تكون كلمة المرور 6 خانات على الأقل")
                return@launch
            }
            repository.changeUserPassword(admin.id, newPassword.trim())
            _currentUser.value = admin.copy(passwordHash = newPassword.trim())
            repository.addAdminLog("تغيير كلمة المرور", "تم تغيير كلمة مرور المالك بنجاح")
            _toastMessage.value = "تم تحديث كلمة المرور بنجاح"
            onSuccess()
        }
    }

    fun adminCancelSubscription(userId: Long, studentName: String) {
        viewModelScope.launch {
            repository.revokeUserSubscriptions(userId)
            repository.addAdminLog("إلغاء اشتراك", "تم إلغاء اشتراك الطالب $studentName (ID: $userId)")
            _toastMessage.value = "تم إلغاء اشتراك الطالب $studentName بنجاح"
        }
    }

    fun adminAddDocumentBooklet(
        title: String,
        subjectId: Long,
        subjectName: String,
        fileType: String,
        fileName: String,
        description: String,
        fileSize: String = "2.5 MB"
    ) {
        viewModelScope.launch {
            val booklet = DocumentBookletEntity(
                title = title.trim(),
                subjectId = subjectId,
                subjectName = subjectName,
                fileType = fileType,
                fileName = fileName.trim(),
                description = description.trim(),
                fileSize = fileSize
            )
            repository.addDocumentBooklet(booklet)
            _toastMessage.value = "تمت إضافة ملف $fileType: $title إلى المكتبة بنجاح"
        }
    }

    fun adminDeleteDocumentBooklet(booklet: DocumentBookletEntity) {
        viewModelScope.launch {
            repository.deleteDocumentBooklet(booklet)
            _toastMessage.value = "تم حذف الملف بنجاح"
        }
    }

    // Curriculum Selection
    fun selectSubject(subject: SubjectEntity) {
        _selectedSubject.value = subject
        navigateTo(Screen.Units)
    }

    fun selectUnit(unit: UnitEntity) {
        _selectedUnit.value = unit
        navigateTo(Screen.LessonDetail)
    }

    fun selectLesson(lesson: LessonEntity) {
        _selectedLesson.value = lesson
        _simplerExplanationText.value = ""
        navigateTo(Screen.LessonDetail)
    }

    fun toggleLessonCompletion(lessonId: Long, completed: Boolean) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.toggleLessonProgress(user.id, lessonId, _selectedSubject.value?.id ?: 1L, completed)
        }
    }

    fun toggleFavorite(type: String, itemId: Long, title: String, subtitle: String, isFav: Boolean) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.toggleFavorite(user.id, type, itemId, title, subtitle, isFav)
        }
    }

    // Explanation ("اشرح لي")
    fun setExplanationLevel(level: String) {
        _explanationLevel.value = level
    }

    fun requestSimplerExplanation() {
        val lesson = _selectedLesson.value ?: return
        _isGeneratingSimpler.value = true
        viewModelScope.launch {
            val prompt = "اشرح لي درس '${lesson.title}' في مادة '${_selectedSubject.value?.name ?: "الرياضيات"}' بمستوى '${_explanationLevel.value}' وبأمثلة توضيحية مبسطة للغاية."
            val resp = geminiRepo.explainConcept(prompt, _selectedSubject.value?.name ?: "الرياضيات", _explanationLevel.value)
            _simplerExplanationText.value = resp
            _isGeneratingSimpler.value = false
        }
    }

    fun explainCustomTopic(prompt: String, subject: String) {
        _isGeneratingSimpler.value = true
        viewModelScope.launch {
            val resp = geminiRepo.explainConcept(prompt, subject, "مفصل")
            _simplerExplanationText.value = resp
            _isGeneratingSimpler.value = false
        }
    }

    // Smart Tutor ("اسأل أستاذي الذكي")
    fun sendSmartTeacherMessage(
        text: String,
        image: Bitmap? = smartTutorAttachedImage.value,
        subject: String = smartTutorSelectedSubject.value,
        modalities: Set<String> = smartTutorRequestedModalities.value,
        context: Context? = null
    ) {
        if (text.isBlank() && image == null) return

        val userMsg = ChatMessage(
            text = text.ifBlank { "سؤال في صورة مرفقة" },
            isUser = true,
            userImage = image,
            subject = subject
        )
        _chatMessages.value = _chatMessages.value + userMsg
        _isTutorThinking.value = true

        viewModelScope.launch {
            try {
                val response = geminiRepo.askSmartTeacherFull(
                    questionText = text,
                    imageBitmap = image,
                    subject = subject,
                    modalities = modalities
                )

                var pdfPath: String? = null
                if (modalities.contains("PDF") && context != null) {
                    try {
                        val pdfFile = PdfBookletGenerator.generateSmartTeacherAnswerPdf(
                            context = context,
                            question = text.ifBlank { "سؤال في صورة مرفقة بمادة $subject" },
                            answer = response.answerText,
                            subject = subject,
                            keyPoints = response.keyPoints,
                            formulas = response.formulas,
                            videoTitle = response.videoTitle
                        )
                        pdfPath = pdfFile.absolutePath
                    } catch (_: Exception) {}
                }

                val teacherMsg = ChatMessage(
                    text = response.answerText,
                    isUser = false,
                    subject = subject,
                    videoTitle = if (modalities.contains("VIDEO")) response.videoTitle else null,
                    videoUrl = if (modalities.contains("VIDEO")) response.videoQuery else null,
                    videoDescription = if (modalities.contains("VIDEO")) response.videoDescription else null,
                    diagramTitle = if (modalities.contains("IMAGE")) response.diagramTitle else null,
                    diagramContent = if (modalities.contains("IMAGE")) response.diagramSummary else null,
                    pdfTitle = if (modalities.contains("PDF")) (response.pdfTitle ?: "ملزمة وشرح الدرس") else null,
                    pdfFilePath = pdfPath,
                    keyPoints = response.keyPoints,
                    formulas = response.formulas
                )
                _chatMessages.value = _chatMessages.value + teacherMsg
            } catch (e: Exception) {
                val errorMsg = ChatMessage(
                    text = "تعذر الحصول على الرد حالياً (${e.message}). يرجى التحقق من الاتصال وإعادة المحاولة.",
                    isUser = false,
                    subject = subject
                )
                _chatMessages.value = _chatMessages.value + errorMsg
            } finally {
                _isTutorThinking.value = false
                smartTutorAttachedImage.value = null
            }
        }
    }

    fun sendTutorMessage(text: String) {
        sendSmartTeacherMessage(text = text)
    }

    fun generatePdfForMessage(context: Context, message: ChatMessage, onReady: (File) -> Unit) {
        viewModelScope.launch {
            isGeneratingTutorPdf.value = true
            try {
                val file = PdfBookletGenerator.generateSmartTeacherAnswerPdf(
                    context = context,
                    question = "استفسار ومسألة في مادة ${message.subject}",
                    answer = message.text,
                    subject = message.subject,
                    keyPoints = message.keyPoints,
                    formulas = message.formulas,
                    videoTitle = message.videoTitle
                )
                // Update message in list with path
                _chatMessages.value = _chatMessages.value.map { msg ->
                    if (msg.id == message.id) msg.copy(pdfFilePath = file.absolutePath) else msg
                }
                onReady(file)
            } catch (e: Exception) {
                _toastMessage.value = "تعذر توليد ملف PDF: ${e.message}"
            } finally {
                isGeneratingTutorPdf.value = false
            }
        }
    }

    fun clearTutorChat() {
        _chatMessages.value = listOf(
            ChatMessage(
                text = "مرحباً بك يا بطل! أنا أستاذك الذكي الخاص بالمنهج اليمني للصف الثالث الثانوي. يمكنك سؤالي في أي موضوع أو إرفاق صورة مسألة وسأجيبك بالشرح النصي والفيديو والرسم وملف PDF الجاهز.",
                isUser = false
            )
        )
        smartTutorAttachedImage.value = null
    }

    // Problem & 50-Question Exam Solver ("حل سؤالي")
    fun solveQuestion() {
        val text = solverQuestionInput.value.trim()
        val bitmap = solverImageBitmap.value
        val subject = solverSelectedSubject.value

        if (text.isBlank() && bitmap == null) {
            _toastMessage.value = "يرجى كتابة السؤال أو إرفاق صورة نموذج الاختبار"
            return
        }

        isSolving.value = true
        solverStructuredResult.value = null
        solverResultText.value = ""

        viewModelScope.launch {
            try {
                val solution = geminiRepo.solveStudentQuestionStructured(
                    questionText = text,
                    subject = subject,
                    image = bitmap,
                    attachedFile = solverAttachedFile.value
                )
                solverStructuredResult.value = solution
                solverResultText.value = solution.finalAnswer
            } catch (e: Exception) {
                _toastMessage.value = "تعذر الاتصال بخدمة الذكاء الاصطناعي: ${e.message}"
            } finally {
                isSolving.value = false
            }
        }
    }

    fun clearSolver() {
        solverQuestionInput.value = ""
        solverImageBitmap.value = null
        solverAttachedFile.value = null
        solverStructuredResult.value = null
        solverResultText.value = ""
        verificationReport.value = null
    }

    // Exams
    fun startExam(exam: ExamEntity) {
        _currentExam.value = exam
        isExamSubmitted.value = false
        userExamAnswers.value = emptyMap()
        currentExamResult.value = null

        viewModelScope.launch {
            val questions = repository.getQuestionsForExam(exam.id)
            examQuestions.value = questions
            navigateTo(Screen.ExamSession)
        }
    }

    fun startRandomExam(subjectId: Long, count: Int = 10) {
        val exam = ExamEntity(
            subjectId = subjectId,
            title = "اختبار تجريبي وزاري شامل ($count أسئلة)",
            durationMinutes = count * 2,
            totalQuestions = count
        )
        _currentExam.value = exam
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
        if (isExamSubmitted.value) return
        userExamAnswers.value = userExamAnswers.value + (questionId to answer)
    }

    fun submitExam() {
        val exam = _currentExam.value ?: return
        val questions = examQuestions.value
        val answers = userExamAnswers.value
        val user = _currentUser.value ?: return

        var correct = 0
        questions.forEach { q ->
            if (answers[q.id]?.trim()?.equals(q.correctOption.trim(), ignoreCase = true) == true) {
                correct++
            }
        }
        val scorePercent = if (questions.isNotEmpty()) (correct * 100) / questions.size else 0
        val isPassed = scorePercent >= 50

        val result = ExamResultEntity(
            userId = user.id,
            examId = exam.id,
            subjectId = exam.subjectId,
            score = scorePercent,
            totalQuestions = questions.size,
            correctAnswers = correct,
            wrongAnswers = questions.size - correct,
            isPassed = isPassed
        )

        viewModelScope.launch {
            val resultId = repository.saveExamResult(result)
            currentExamResult.value = result.copy(id = resultId)
            isExamSubmitted.value = true
            navigateTo(Screen.ExamResult)
        }
    }

    // Subscriptions & Payments
    fun submitPayment(planName: String, durationMonths: Int, amountYmr: String, walletName: String, transRef: String, receiptUri: String, onSuccess: () -> Unit) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val req = PaymentRequestEntity(
                userId = user.id,
                studentName = user.fullName,
                studentPhone = user.phoneOrEmail,
                walletType = walletName,
                senderNumber = user.phoneOrEmail,
                transferRef = transRef,
                amount = amountYmr
            )
            repository.submitPaymentRequest(req)
            _toastMessage.value = "تم إرسال طلب الاشتراك بنجاح! سيتم التفعيل خلال دقائق."
            onSuccess()
        }
    }

    fun activateCode(code: String, onSuccess: () -> Unit) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val result = repository.activateCodeByStudent(user.id, code.trim())
            if (result.isSuccess) {
                _toastMessage.value = "تم تفعيل الاشتراك بنجاح!"
                onSuccess()
            } else {
                _toastMessage.value = result.exceptionOrNull()?.message ?: "كود التفعيل غير صالح"
            }
        }
    }

    // Admin Operations
    fun adminReviewPayment(paymentId: Long, approve: Boolean, createCode: Boolean = false, reason: String = "") {
        viewModelScope.launch {
            repository.reviewPaymentRequest(paymentId, approve, createCode, reason)
            _toastMessage.value = if (approve) "تمت الموافقة وتفعيل الاشتراك" else "تم رفض الطلب"
        }
    }

    fun adminDirectActivate(studentId: Long, studentName: String, months: Int, planName: String, price: Int) {
        viewModelScope.launch {
            repository.activateSubscriptionDirectly(studentId, studentName, months, planName, price)
            _toastMessage.value = "تم تفعيل اشتراك الطالب $studentName مباشرة بنجاح"
        }
    }

    fun adminExtendSubscription(studentId: Long, studentName: String, days: Int) {
        viewModelScope.launch {
            repository.extendSubscription(studentId, studentName, days)
            _toastMessage.value = "تم تمديد اشتراك الطالب $days يوماً"
        }
    }

    fun adminGenerateCode(months: Int, planName: String, price: Int, tiedPhone: String = "", onDone: (SubscriptionCodeEntity) -> Unit) {
        viewModelScope.launch {
            val code = repository.generateSubscriptionCode(months, planName, price, tiedPhone)
            _toastMessage.value = "تم توليد الكود: ${code.code}"
            onDone(code)
        }
    }

    fun adminCancelCode(codeId: Long, code: String) {
        viewModelScope.launch {
            repository.cancelSubscriptionCode(codeId, code)
            _toastMessage.value = "تم إلغاء الكود $code"
        }
    }
}
