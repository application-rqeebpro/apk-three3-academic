package com.example.ui.screens.smarttutor

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.example.data.pdf.PdfShareHelper
import com.example.ui.ChatMessage
import com.example.ui.MainViewModel
import java.io.File
import java.net.URLEncoder
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartTutorScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit = { viewModel.navigateBack() }
) {
    val context = LocalContext.current
    val messages by viewModel.chatMessages.collectAsState()
    val isThinking by viewModel.isTutorThinking.collectAsState()
    val selectedSubject by viewModel.smartTutorSelectedSubject.collectAsState()
    val attachedImage by viewModel.smartTutorAttachedImage.collectAsState()
    val requestedModalities by viewModel.smartTutorRequestedModalities.collectAsState()
    val isGeneratingPdf by viewModel.isGeneratingTutorPdf.collectAsState()

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Sheet and Dialog states
    var showAttachSheet by remember { mutableStateOf(false) }
    var previewImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showMathSymbolsBar by remember { mutableStateOf(false) }

    // Text To Speech state
    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    var speakingMessageId by remember { mutableStateOf<String?>(null) }

    DisposableEffect(context) {
        var ttsInstance: TextToSpeech? = null
        ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsInstance?.language = Locale("ar")
            }
        }
        tts = ttsInstance
        onDispose {
            ttsInstance?.stop()
            ttsInstance?.shutdown()
        }
    }

    // Auto scroll to bottom
    LaunchedEffect(messages.size, isThinking) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bmp: Bitmap? ->
        if (bmp != null) {
            viewModel.smartTutorAttachedImage.value = bmp
            Toast.makeText(context, "تم التقاط صورة المسألة بالكاميرا بنجاح 📸", Toast.LENGTH_SHORT).show()
        }
    }

    // Camera permission launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(context, "يلزم منح إذن الكاميرا لالتقاط صورة للمسألة", Toast.LENGTH_LONG).show()
        }
    }

    // Photo picker launcher (Gallery)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val bmp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                        decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                        decoder.isMutableRequired = true
                    }
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
                viewModel.smartTutorAttachedImage.value = bmp
                Toast.makeText(context, "تم إرفاق صورة المسألة من المعرض 🖼️", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "خطأ في قراءة الصورة: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val subjects = listOf("الرياضيات", "الفيزياء", "الكيمياء", "الأحياء", "اللغة العربية", "اللغة الإنجليزية", "القرآن والتربية الإسلامية", "عام")

    val mathSymbols = listOf("√", "π", "²", "³", "×", "÷", "±", "Δ", "°", "∫", "lim", "α", "β", "θ", "→", "≠", "≤", "≥", "∞")

    val quickQuestions = listOf(
        "اشرح لي قاعدة لنز في الفيزياء مع فيديو ورسم توضيحي",
        "حل مسألة إيجاد القيم العظمى والصغرى في التفاضل وملف PDF",
        "ما هي تفاعلات الأكسدة والاختزال في الكيمياء؟",
        "شرح قانون مندل الأول في الوراثة مع مخطط المفاهيم",
        "إعراب جملة تفصيلية في مادة اللغة العربية مع القواعد",
        "قوانين الحركة الدائرية والجاذبية في ورقة واحدة"
    )

    Scaffold(
        bottomBar = {
            Surface(
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    // Attached Image Preview banner
                    if (attachedImage != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Image(
                                        bitmap = attachedImage!!.asImageBitmap(),
                                        contentDescription = "صورة مرفقة",
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { previewImageBitmap = attachedImage }
                                            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                                    )
                                    Column {
                                        Text(
                                            text = "تم إرفاق صورة المسألة للمناقشة ✓",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "انقر للمعاينه بالحجم الكامل",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.Gray
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = { viewModel.smartTutorAttachedImage.value = null },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "حذف الصورة", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }

                    // Quick Math & Science Symbols Ribbon
                    AnimatedVisibility(visible = showMathSymbolsBar) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp)
                        ) {
                            LazyRow(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(mathSymbols) { sym ->
                                    Surface(
                                        onClick = { inputText += sym },
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.surface,
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                    ) {
                                        Text(
                                            text = sym,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Modality Toggle Ribbon (Icons for output formatting)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "صيغ الرد المعتمدة:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutputModalityChip(
                                label = "نص",
                                icon = Icons.Default.Description,
                                isSelected = requestedModalities.contains("TEXT"),
                                tint = Color(0xFF2563EB)
                            ) {
                                toggleModality(viewModel, "TEXT")
                            }

                            OutputModalityChip(
                                label = "فيديو",
                                icon = Icons.Default.PlayCircle,
                                isSelected = requestedModalities.contains("VIDEO"),
                                tint = Color(0xFFDC2626)
                            ) {
                                toggleModality(viewModel, "VIDEO")
                            }

                            OutputModalityChip(
                                label = "رسم",
                                icon = Icons.Default.Draw,
                                isSelected = requestedModalities.contains("IMAGE"),
                                tint = Color(0xFF0284C7)
                            ) {
                                toggleModality(viewModel, "IMAGE")
                            }

                            OutputModalityChip(
                                label = "PDF",
                                icon = Icons.Default.PictureAsPdf,
                                isSelected = requestedModalities.contains("PDF"),
                                tint = Color(0xFF16A34A)
                            ) {
                                toggleModality(viewModel, "PDF")
                            }
                        }
                    }

                    // Main Input Row with Camera & Gallery attachments
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Attachment Menu Trigger (Camera + Gallery)
                        IconButton(
                            onClick = { showAttachSheet = true },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Icon(
                                Icons.Default.AddAPhoto,
                                contentDescription = "إرفاق صورة للمسألة (كاميرا أو معرض)",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Toggle Math Symbols Ribbon
                        IconButton(
                            onClick = { showMathSymbolsBar = !showMathSymbolsBar },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (showMathSymbolsBar) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(
                                Icons.Default.Functions,
                                contentDescription = "رموز رياضية وعلمية",
                                tint = if (showMathSymbolsBar) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Input Text Field
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("اكتب سؤالك أو ارفق صورة المسألة...") },
                            maxLines = 4,
                            shape = RoundedCornerShape(22.dp)
                        )

                        // Send Button
                        IconButton(
                            onClick = {
                                if ((inputText.isNotBlank() || attachedImage != null) && !isThinking) {
                                    val text = inputText
                                    val img = attachedImage
                                    inputText = ""
                                    viewModel.sendSmartTeacherMessage(
                                        text = text,
                                        image = img,
                                        subject = selectedSubject,
                                        modalities = requestedModalities,
                                        context = context
                                    )
                                }
                            },
                            enabled = (inputText.isNotBlank() || attachedImage != null) && !isThinking,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    if ((inputText.isNotBlank() || attachedImage != null) && !isThinking)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        Color.Gray.copy(alpha = 0.3f)
                                )
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Send,
                                contentDescription = "إرسال السؤال",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            // Enhanced Header Card (Shown on empty conversation)
            if (messages.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                                Column {
                                    Text("اسأل أستاذي الذكي 👨‍🏫 (المنهج اليمني)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    Text("اطرح أي مسألة أو التقط صورة بالكاميرا وستحصل على الشرح بـ 4 أنماط", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                }
                            }

                            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                            // Features Icons Row (PDF, Video, Text, Image)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                FeatureIconPill(icon = Icons.Default.Description, label = "نص وشرح", color = Color(0xFF2563EB))
                                FeatureIconPill(icon = Icons.Default.PlayCircle, label = "فيديو تعليمي", color = Color(0xFFDC2626))
                                FeatureIconPill(icon = Icons.Default.Draw, label = "رسم ولوحة", color = Color(0xFF0284C7))
                                FeatureIconPill(icon = Icons.Default.PictureAsPdf, label = "ملف PDF", color = Color(0xFF16A34A))
                            }
                        }
                    }
                }
            }

            // Subject Selector Row
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "اختر مادة السؤال:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(subjects) { subj ->
                            FilterChip(
                                selected = selectedSubject == subj,
                                onClick = { viewModel.smartTutorSelectedSubject.value = subj },
                                label = { Text(subj, fontSize = 12.sp) }
                            )
                        }
                    }
                }
            }

            // Quick Prompt suggestions
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "💡 مقترحات سريعة لطرح المسائل:",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(quickQuestions) { question ->
                            SuggestionChip(
                                onClick = {
                                    viewModel.sendSmartTeacherMessage(
                                        text = question,
                                        subject = selectedSubject,
                                        modalities = requestedModalities,
                                        context = context
                                    )
                                },
                                label = { Text(question, fontSize = 11.sp, maxLines = 1) }
                            )
                        }
                    }
                }
            }

            // Messages Stream
            items(messages) { msg ->
                EnhancedTeacherChatBubble(
                    message = msg,
                    context = context,
                    viewModel = viewModel,
                    isSpeaking = speakingMessageId == msg.id,
                    onPreviewImage = { previewImageBitmap = it },
                    onToggleTts = {
                        if (speakingMessageId == msg.id) {
                            tts?.stop()
                            speakingMessageId = null
                        } else {
                            tts?.stop()
                            speakingMessageId = msg.id
                            tts?.speak(msg.text, TextToSpeech.QUEUE_FLUSH, null, msg.id)
                        }
                    }
                )
            }

            // Thinking State Indicator
            if (isThinking) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                Column {
                                    Text("أستاذي الذكي يعد الشرح والحل النموذجي...", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                    Text("يتم تجهيز النص، فيديو الشرح، الرسم التوضيحي، وملف الـ PDF...", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Attachment Options Bottom Sheet (Camera vs Gallery)
    if (showAttachSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAttachSheet = false },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "إرفاق صورة المسألة لأستاذي الذكي 📸",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "يمكنك التقاط صورة فورية بالكاميرا لصفحة الكتاب أو ورقة الاختبار، أو الاختيار من المعرض:",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Option 1: Take Photo via Camera
                    Card(
                        onClick = {
                            showAttachSheet = false
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                cameraLauncher.launch(null)
                            } else {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                            Text("التقاط بالكاميرا", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("صورة فورية للورقة", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        }
                    }

                    // Option 2: Pick from Gallery
                    Card(
                        onClick = {
                            showAttachSheet = false
                            photoPickerLauncher.launch("image/*")
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.secondary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                            Text("اختيار من المعرض", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("من ألبوم الصور", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }

    // Full-screen Image Preview Dialog
    if (previewImageBitmap != null) {
        Dialog(onDismissRequest = { previewImageBitmap = null }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.Black.copy(alpha = 0.95f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("معاينة صورة المسألة", color = Color.White, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { previewImageBitmap = null }) {
                            Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.White)
                        }
                    }

                    Image(
                        bitmap = previewImageBitmap!!.asImageBitmap(),
                        contentDescription = "معاينة كاملة",
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            }
        }
    }
}

@Composable
fun FeatureIconPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun OutputModalityChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    tint: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) tint else tint.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, if (isSelected) tint else tint.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else tint,
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = label,
                color = if (isSelected) Color.White else tint,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun toggleModality(viewModel: MainViewModel, modality: String) {
    val current = viewModel.smartTutorRequestedModalities.value.toMutableSet()
    if (current.contains(modality)) {
        if (current.size > 1) {
            current.remove(modality)
        }
    } else {
        current.add(modality)
    }
    viewModel.smartTutorRequestedModalities.value = current
}

@Composable
fun EnhancedTeacherChatBubble(
    message: ChatMessage,
    context: Context,
    viewModel: MainViewModel,
    isSpeaking: Boolean,
    onPreviewImage: (Bitmap) -> Unit,
    onToggleTts: () -> Unit
) {
    val isUser = message.isUser
    var activeTab by remember { mutableStateOf("ALL") }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (isUser) 18.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 18.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isUser) 1.dp else 3.dp),
            modifier = Modifier.widthIn(max = 350.dp)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header (Teacher label or User label)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (isUser) Icons.Default.Person else Icons.Default.Psychology,
                            contentDescription = null,
                            tint = if (isUser) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (isUser) "سؤالي (${message.subject})" else "أستاذي الذكي 👨‍🏫 (${message.subject})",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isUser) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.primary
                        )
                    }

                    // TTS Voice speaker button for teacher responses
                    if (!isUser) {
                        IconButton(
                            onClick = onToggleTts,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                contentDescription = "قراءة صوتية",
                                tint = if (isSpeaking) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // If user attached an image (clickable to expand)
                if (message.userImage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onPreviewImage(message.userImage) }
                    ) {
                        Image(
                            bitmap = message.userImage.asImageBitmap(),
                            contentDescription = "صورة المسألة",
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 220.dp)
                        )
                        Surface(
                            shape = RoundedCornerShape(bottomStart = 8.dp),
                            color = Color.Black.copy(alpha = 0.6f),
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Text(
                                text = "🔍 تكبير",
                                color = Color.White,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Formatting Filter Tabs for Teacher response (All, Text, Video, Diagram, PDF)
                if (!isUser) {
                    ScrollableTabRow(
                        selectedTabIndex = when (activeTab) {
                            "TEXT" -> 1
                            "VIDEO" -> 2
                            "IMAGE" -> 3
                            "PDF" -> 4
                            else -> 0
                        },
                        edgePadding = 0.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp)),
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ) {
                        Tab(
                            selected = activeTab == "ALL",
                            onClick = { activeTab = "ALL" },
                            text = { Text("الكل", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = activeTab == "TEXT",
                            onClick = { activeTab = "TEXT" },
                            icon = { Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(14.dp)) },
                            text = { Text("الشرح", fontSize = 11.sp) }
                        )
                        if (!message.videoTitle.isNullOrBlank()) {
                            Tab(
                                selected = activeTab == "VIDEO",
                                onClick = { activeTab = "VIDEO" },
                                icon = { Icon(Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFDC2626)) },
                                text = { Text("فيديو", fontSize = 11.sp) }
                            )
                        }
                        if (!message.diagramTitle.isNullOrBlank()) {
                            Tab(
                                selected = activeTab == "IMAGE",
                                onClick = { activeTab = "IMAGE" },
                                icon = { Icon(Icons.Default.Draw, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF0284C7)) },
                                text = { Text("رسم", fontSize = 11.sp) }
                            )
                        }
                        if (!message.pdfTitle.isNullOrBlank() || message.pdfFilePath != null) {
                            Tab(
                                selected = activeTab == "PDF",
                                onClick = { activeTab = "PDF" },
                                icon = { Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF16A34A)) },
                                text = { Text("PDF", fontSize = 11.sp) }
                            )
                        }
                    }
                }

                // 1. Text Explanation Section
                if (isUser || activeTab == "ALL" || activeTab == "TEXT") {
                    Text(
                        text = message.text,
                        color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )
                }

                // Teacher Formulas Box
                if (!isUser && (activeTab == "ALL" || activeTab == "TEXT") && message.formulas.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFFFBEB),
                        border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.Functions, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(16.dp))
                                Text(
                                    text = "📐 القوانين والمعادلات المعتمدة:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFFB45309)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            for (f in message.formulas) {
                                Text("• $f", fontSize = 12.sp, color = Color(0xFF78350F), fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                // Teacher Key Points Box
                if (!isUser && (activeTab == "ALL" || activeTab == "TEXT") && message.keyPoints.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF0FDF4),
                        border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(16.dp))
                                Text(
                                    text = "⭐ مفاتيح الحل ونقاط التركيز:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF15803D)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            for (kp in message.keyPoints) {
                                Text("• $kp", fontSize = 12.sp, color = Color(0xFF166534))
                            }
                        }
                    }
                }

                // 2. Video Solution Card
                if (!isUser && (activeTab == "ALL" || activeTab == "VIDEO") && !message.videoTitle.isNullOrBlank()) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                        border = BorderStroke(1.dp, Color(0xFFFECACA)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.PlayCircle, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(20.dp))
                                Text(
                                    text = "🎬 درس وفيديو تعليمي موصى به",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF991B1B)
                                )
                            }
                            Text(
                                text = message.videoTitle,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = Color(0xFF7F1D1D)
                            )
                            if (!message.videoDescription.isNullOrBlank()) {
                                Text(
                                    text = message.videoDescription,
                                    fontSize = 11.sp,
                                    color = Color.DarkGray
                                )
                            }
                            Button(
                                onClick = {
                                    val query = message.videoUrl ?: message.videoTitle
                                    val searchUrl = "https://www.youtube.com/results?search_query=${URLEncoder.encode(query, "UTF-8")}"
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(searchUrl)).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    try {
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "تعذر فتح مشغل الفيديو: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.SmartDisplay, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("مشاهدة شرح الفيديو الآن ↗", fontSize = 12.sp, color = Color.White)
                            }
                        }
                    }
                }

                // 3. Board Diagram / Concept Map Card
                if (!isUser && (activeTab == "ALL" || activeTab == "IMAGE") && (!message.diagramTitle.isNullOrBlank() || !message.diagramContent.isNullOrBlank())) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        border = BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Draw, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
                                Text(
                                    text = "🖼️ لوحة ورسم المخطط البصري: ${message.diagramTitle ?: "مفاهيم الدرس"}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF38BDF8)
                                )
                            }
                            if (!message.diagramContent.isNullOrBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF1E293B),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = message.diagramContent,
                                        modifier = Modifier.padding(8.dp),
                                        color = Color(0xFFF1F5F9),
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. PDF Document Solution Card
                if (!isUser && (activeTab == "ALL" || activeTab == "PDF") && (!message.pdfTitle.isNullOrBlank() || message.pdfFilePath != null)) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                        border = BorderStroke(1.dp, Color(0xFF86EFAC)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(20.dp))
                                Text(
                                    text = "📄 مذكرة وملف PDF جاهز للطباعة والتحميل",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF15803D)
                                )
                            }
                            Text(
                                text = message.pdfTitle ?: "شرح وحل المسألة مع الملاحظات الوزارية",
                                fontSize = 12.sp,
                                color = Color(0xFF166534),
                                fontWeight = FontWeight.SemiBold
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // View PDF Button
                                Button(
                                    onClick = {
                                        if (message.pdfFilePath != null && File(message.pdfFilePath).exists()) {
                                            PdfShareHelper.openPdfInExternalViewer(context, File(message.pdfFilePath))
                                        } else {
                                            viewModel.generatePdfForMessage(context, message) { file ->
                                                PdfShareHelper.openPdfInExternalViewer(context, file)
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(15.dp), tint = Color.White)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("عرض PDF", fontSize = 11.sp, color = Color.White)
                                }

                                // Share PDF Button
                                OutlinedButton(
                                    onClick = {
                                        if (message.pdfFilePath != null && File(message.pdfFilePath).exists()) {
                                            PdfShareHelper.sharePdfFile(context, File(message.pdfFilePath), message.pdfTitle ?: "شرح الأستاذ الذكي")
                                        } else {
                                            viewModel.generatePdfForMessage(context, message) { file ->
                                                PdfShareHelper.sharePdfFile(context, file, message.pdfTitle ?: "شرح الأستاذ الذكي")
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("مشاركة", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
