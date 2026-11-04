package com.example.ui.screens.explain

import android.speech.tts.TextToSpeech
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.data.remote.ExplainOption
import com.example.ui.MainViewModel
import com.example.ui.components.copyToClipboard
import com.example.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExplainMeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = { viewModel.navigateBack() }
) {
    val context = LocalContext.current
    val selectedLesson by viewModel.selectedLesson.collectAsState()
    val explanationText by viewModel.simplerExplanationText.collectAsState()
    val isGenerating by viewModel.isGeneratingSimpler.collectAsState()
    val elapsedSeconds by viewModel.explainDurationSeconds.collectAsState()
    val selectedOption by viewModel.selectedExplainOption.collectAsState()
    val currentSubject by viewModel.explainSubject.collectAsState()
    val currentTopicPrompt by viewModel.explainTopicPrompt.collectAsState()

    var topicInput by remember(currentTopicPrompt, selectedLesson) {
        mutableStateOf(
            if (currentTopicPrompt.isNotBlank()) currentTopicPrompt
            else selectedLesson?.title ?: ""
        )
    }

    val subjects = listOf("الرياضيات", "الفيزياء", "الكيمياء", "الأحياء", "اللغة العربية", "القرآن الكريم", "اللغة الإنجليزية")
    var activeSubject by remember(currentSubject) { mutableStateOf(currentSubject) }

    // TTS state
    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    var isSpeaking by remember { mutableStateOf(false) }

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

    // Quick Yemeni high-school topic suggestions
    val topicSuggestions = when (activeSubject) {
        "الفيزياء" -> listOf("الاحتكاك في نقيل يسلح", "الحركة التوافقية البسيطة", "تأثير دوبلر في الصوت", "الموجات الكهرومغناطيسية", "التيار المتردد والمكثفات")
        "الكيمياء" -> listOf("الاتزان الكيميائي وقاعدة لوشاتيليه", "الخلايا الجلفانية وبطاريات الطاقة", "سرعة التفاعل ونظرية التصادم", "المحاليل المنظمة")
        "الأحياء" -> listOf("تجارب مندل والسيادة التامة", "الكروموسومات والانقسام المنصف", "الجهاز العصبي والسيال العصبي", "الهندسة الوراثية")
        else -> listOf("مبرهنة ديموافر والجذور النونية", "القطع المكافئ والمخروطيات", "النهايات والاشتقاق", "حساب التكامل والمساحات", "المحددات وطريقة كرامر")
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // 1. Hero Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = NavyPrimary)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = AmberSecondary.copy(alpha = 0.2f),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("💡", fontSize = 20.sp)
                                }
                            }
                            Column {
                                Text(
                                    text = "خدمة «لم أفهم؟»",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        fontSize = 18.sp
                                    )
                                )
                                Text(
                                    text = "منهاج الصف الثالث الثانوي بالجمهورية اليمنية 🇾🇪",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = AmberLight,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        // Badge Time notice
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "1 - 2 دقيقة",
                                    color = Color.White,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Text(
                        text = "اختر ما تريده: شرح مبسط بالتشبيهات، تفكيك خطوة بخطوة، لماذا هذا القانون، أسئلة متوقعة، أو تدريبات محلولة خالية من الرموز المشوشة.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    )
                }
            }
        }

        // 2. The 5 Options Selector
        item {
            Text(
                text = "ما الذي تحتاجه في هذا الدرس أو المفهوم؟",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = NavyPrimary)
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                ExplainOption.entries.forEach { opt ->
                    val isSelected = selectedOption == opt
                    Surface(
                        onClick = {
                            viewModel.selectedExplainOption.value = opt
                            if (topicInput.isNotBlank() && !isGenerating) {
                                viewModel.explainTopicWithOption(topicInput, activeSubject, opt)
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) Color(0xFFEFF6FF) else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) RaqeebRoyalBlue else MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("option_${opt.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(text = opt.emoji, fontSize = 22.sp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = opt.title,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                    color = if (isSelected) RaqeebRoyalBlue else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = opt.subtitle,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) Color(0xFF1E3A8A) else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
                                    )
                                )
                            }
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    viewModel.selectedExplainOption.value = opt
                                    if (topicInput.isNotBlank() && !isGenerating) {
                                        viewModel.explainTopicWithOption(topicInput, activeSubject, opt)
                                    }
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = RaqeebRoyalBlue)
                            )
                        }
                    }
                }
            }
        }

        // 3. Subject Selector & Topic Input
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "المادة والموضوع المراد فهمه:",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )

                    // Subject Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(subjects) { sub ->
                            FilterChip(
                                selected = activeSubject == sub,
                                onClick = {
                                    activeSubject = sub
                                    viewModel.explainSubject.value = sub
                                },
                                label = { Text(sub, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = RaqeebRoyalBlue,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    // Input Field
                    OutlinedTextField(
                        value = topicInput,
                        onValueChange = {
                            topicInput = it
                            viewModel.explainTopicPrompt.value = it
                        },
                        label = { Text("اكتب اسم الدرس، المفهوم، أو القانون") },
                        placeholder = { Text("مثال: لماذا نستخدم مبرهنة ديموافر، أو الاحتكاك في المسائل الفيزيائية...") },
                        modifier = Modifier.fillMaxWidth().testTag("topic_input_field"),
                        minLines = 2,
                        maxLines = 4,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Suggestions Row
                    Text(
                        text = "أمثلة شائعة من المنهج اليمني:",
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(topicSuggestions) { sug ->
                            SuggestionChip(
                                onClick = {
                                    topicInput = sug
                                    viewModel.explainTopicPrompt.value = sug
                                    viewModel.explainTopicWithOption(sug, activeSubject, selectedOption)
                                },
                                label = { Text(sug, fontSize = 11.sp) }
                            )
                        }
                    }

                    // Explain Button with Timing Indication
                    Button(
                        onClick = {
                            viewModel.explainTopicWithOption(topicInput, activeSubject, selectedOption)
                        },
                        enabled = !isGenerating && topicInput.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("start_explain_button")
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("جاري استخراج الشرح والتدقيق (1-2 دقيقة) ⏱️", fontSize = 13.sp)
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AmberSecondary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "اشرح لي «${selectedOption.title}» الآن 🚀",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "يستغرق الشرح والتدقيق الوزاري من دقيقة إلى دقيقتين لضمان أعلى درجات الدقة.",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.5.sp
                            )
                        )
                    }
                }
            }
        }

        // 4. Live Timer & Progress Stages (During Generation)
        if (isGenerating) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                    border = BorderStroke(1.5.dp, Color(0xFF86EFAC))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val minutes = elapsedSeconds / 60
                        val seconds = elapsedSeconds % 60
                        val timeFormatted = String.format("%02d:%02d", minutes, seconds)
                        val estimatedMax = 90f // 1.5 minutes target
                        val progress = (elapsedSeconds.toFloat() / estimatedMax).coerceIn(0.05f, 0.95f)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier.size(36.dp),
                                    color = Color(0xFF16A34A),
                                    strokeWidth = 3.5.dp
                                )
                                Column {
                                    Text(
                                        text = "جاري إعداد الشرح الوزاري النموذجي...",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp,
                                        color = Color(0xFF166534)
                                    )
                                    Text(
                                        text = "يستغرق عادة من دقيقة إلى دقيقتين ⏱️",
                                        fontSize = 11.sp,
                                        color = Color(0xFF15803D)
                                    )
                                }
                            }

                            // Live Timer Badge
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFDCFCE7)
                            ) {
                                Text(
                                    text = "$timeFormatted د",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    color = Color(0xFF166534),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFF16A34A),
                            trackColor = Color(0xFFBBF7D0)
                        )

                        // Dynamic stage based on elapsed time
                        val currentStage = when {
                            elapsedSeconds < 15 -> "📖 قراءة وتحليل المفهوم والمعطيات ومطابقتها لمنهج ثالث ثانوي يمني..."
                            elapsedSeconds < 35 -> "🇾🇪 استدعاء القوانين الوزارية وتوليد التشبيهات الواقعية من بيئة الطالب..."
                            elapsedSeconds < 60 -> "📐 تطبيق الخطوات وتفكيك المعادلات مع إزالة الرموز المشوشة (*$&#)..."
                            elapsedSeconds < 85 -> "🔍 تدقيق الحسابات والوحدات والأسئلة لضمان الدرجة الكاملة..."
                            else -> "✨ اللمسات النهائية لإخراج الشرح بأوضح وأبسط صورة..."
                        }

                        Text(
                            text = currentStage,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF166534),
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }
            }
        }

        // 5. Result Explanation Display
        if (!explanationText.isNullOrBlank() && !isGenerating) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Result Top Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(selectedOption.emoji, fontSize = 22.sp)
                                Column {
                                    Text(
                                        text = selectedOption.title,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = NavyPrimary
                                        )
                                    )
                                    Text(
                                        text = "وفق المعايير الوزارية بالجمهورية اليمنية 🇾🇪",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF15803D))
                                    )
                                }
                            }

                            // Actions: Copy & Listen
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(
                                    onClick = {
                                        explanationText?.let { text ->
                                            copyToClipboard(context, text, "الشرح المبسط")
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "نسخ", tint = MaterialTheme.colorScheme.primary)
                                }

                                IconButton(
                                    onClick = {
                                        explanationText?.let { text ->
                                            if (isSpeaking) {
                                                tts?.stop()
                                                isSpeaking = false
                                            } else {
                                                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "explain_tts")
                                                isSpeaking = true
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (isSpeaking) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                        contentDescription = "استماع",
                                        tint = AmberSecondary
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        // Text content with clean typography
                        Text(
                            text = explanationText ?: "",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                lineHeight = 26.sp,
                                fontSize = 14.5.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        // Quick switch to other options
                        Text(
                            text = "شاهد هذا الموضوع بطريقة أخرى:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(ExplainOption.entries.filter { it != selectedOption }) { opt ->
                                OutlinedButton(
                                    onClick = {
                                        viewModel.explainTopicWithOption(topicInput, activeSubject, opt)
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text("${opt.emoji} ${opt.title}", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
