package com.example.ui.screens.explain

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.copyToClipboard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExplainMeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val explanationResult by viewModel.simplerExplanationText.collectAsState()
    val isGenerating by viewModel.isGeneratingSimpler.collectAsState()

    var selectedSubject by remember { mutableStateOf("الرياضيات") }
    var conceptInput by remember { mutableStateOf("") }
    var selectedStyle by remember { mutableStateOf("بسيط جداً مع تشبيه") }

    val subjectsList = listOf(
        "الرياضيات",
        "الفيزياء",
        "الكيمياء",
        "الأحياء",
        "اللغة العربية",
        "اللغة الإنجليزية"
    )

    val quickTopics = when (selectedSubject) {
        "الرياضيات" -> listOf(
            "مبرهنة رول والقيمة المتوسطة",
            "قاعدة لوبيتال لحساب النهايات 0/0",
            "الصورة القطبية وجذور العدد المركب",
            "المحددات والمصفوفات وقاعدة كرامر",
            "التكامل بالتعويض والتجزئة"
        )
        "الفيزياء" -> listOf(
            "التيار المتردد والممانعة الكلية (Z)",
            "الظاهرة الكهروضوئية ومعادلة أينشتاين",
            "قاعدة لنز وقانون فاراداي للحث",
            "الدائرة المهتزة والتوليف الراديوي",
            "الانشطار والاندماج النووي"
        )
        "الكيمياء" -> listOf(
            "الاتزان الأيوني وحساب الرقم الهيدروجيني (pH)",
            "الخلايا الجلفانية وجهد الخلية القياسي",
            "قوانين الغازات ونظرية الحركة الجزيئية",
            "العوامل المؤثرة في سرعة التفاعل وقاعدة لوشاتيليه"
        )
        "اللغة العربية" -> listOf(
            "إعراب الجمل التي لها محل والتي لا محل لها",
            "أوزان المشتقات وأعمالها (اسم الفاعل والمفعول)",
            "الاستعارة المكنية والتصريحية في البلاغة",
            "إعراب الأفعال الخمسة والأسماء الخمسة"
        )
        else -> listOf(
            "الفكرة الأساسية للدرس",
            "لماذا نستخدم هذا القانون؟",
            "خطوات الحل المعتمدة",
            "أسهل طريقة للحفظ والاستيعاب"
        )
    }

    val styleModes = listOf(
        "بسيط جداً مع تشبيه",
        "خطوة بخطوة بالترتيب",
        "لماذا هذا القانون بالذات؟",
        "ما فهمت الخطوة الثانية",
        "مثال محلول وتدريب"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("explain_me_content"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AmberSecondary),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(18.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "💡", fontSize = 26.sp)
                    }

                    Column {
                        Text(
                            text = "خدمة اشرح لي (المعلم المبسط)",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "إذا واجهت أي فكرة صعبة، أو قانون لم تفهم سببه، سنشرحه لك بأبسط لغة مع تشبيهات واقعية تناسب طلاب المنهج اليمني.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }

        // 1. Choose Subject
        item {
            Text(
                text = "1. اختر المادة الدراسية:",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(subjectsList) { subj ->
                    val isSelected = selectedSubject == subj
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedSubject = subj },
                        label = {
                            Text(
                                text = subj,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
        }

        // 2. Quick Topics Chips
        item {
            Text(
                text = "مواضيع شائعة تحتاج توضيح في $selectedSubject:",
                style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(quickTopics) { topic ->
                    Surface(
                        onClick = { conceptInput = topic },
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = topic,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // 3. Question / Concept Input
        item {
            Text(
                text = "2. اكتب ما تريد شرحه بالتحديد:",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = conceptInput,
                onValueChange = { conceptInput = it },
                placeholder = { Text("مثال: ما سبب استخدام هذا القانون؟ أو اشرح لي خطوة معينة...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("explain_concept_input"),
                shape = RoundedCornerShape(12.dp),
                minLines = 2,
                maxLines = 4
            )
        }

        // 4. Explanation Style
        item {
            Text(
                text = "3. اختر أسلوب الشرح المفضل:",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                styleModes.forEach { style ->
                    val isSelected = selectedStyle == style
                    Surface(
                        onClick = { selectedStyle = style },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RadioButton(selected = isSelected, onClick = { selectedStyle = style })
                            Text(
                                text = style,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    }
                }
            }
        }

        // Submit Button
        item {
            Button(
                onClick = {
                    val prompt = """
اشرح لي التالي في مادة $selectedSubject بأسلوب ($selectedStyle):
الموضوع أو السؤال: $conceptInput

قواعد الشرح:
1. استخدم لغة سهلة جداً يفهمها طالب الثانوية اليمني دون أي تعقيد أكاديمي.
2. إذا اخترت (بسيط جداً مع تشبيه)، اضرب مثلاً وتشبيه من الحياة اليومية.
3. إذا كان هناك قانون، اشرح سبب استخدامه ومعنى كل رمز ووحدته.
4. إذا طلب التركيز على خطوة معينة، ركز عليها وقسمها لخطوات أصغر.
                    """.trimIndent()
                    viewModel.explainCustomTopic(prompt, selectedSubject)
                },
                enabled = !isGenerating && conceptInput.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_explain_me_button")
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "جاري تبسيط وصياغة الشرح...", color = Color.White)
                } else {
                    Icon(imageVector = Icons.Default.AutoFixHigh, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "اشرح لي الآن بأسلوب مبسط ✨",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }

        // Result Card
        if (explanationResult != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardGold),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("explain_result_card")
                ) {
                    Column(
                        modifier = Modifier
                            .padding(18.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
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
                                Text(text = "🌟", fontSize = 20.sp)
                                Text(
                                    text = "الشرح المبسط لطلبك:",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF78350F)
                                    )
                                )
                            }

                            IconButton(onClick = {
                                copyToClipboard(context, com.example.data.solver.MathFormatter.cleanMathText(explanationResult ?: ""), "الشرح المبسط")
                            }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "نسخ", tint = Color(0xFF78350F))
                            }
                        }

                        HorizontalDivider(color = AmberSecondary.copy(alpha = 0.3f))

                        Text(
                            text = com.example.data.solver.MathFormatter.cleanMathText(explanationResult ?: ""),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                lineHeight = 24.sp,
                                color = Color(0xFF451A03)
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Follow-up actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val simplerPrompt = "ما فهمت، أعد الشرح بطريقة أبسط وأقصر بمثال واحد فقط عن:\n$conceptInput"
                                    viewModel.explainCustomTopic(simplerPrompt, selectedSubject)
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("ما فهمت؟ بسطها أكثر 💡", fontSize = 11.sp, color = Color(0xFF78350F))
                            }

                            OutlinedButton(
                                onClick = {
                                    val examplePrompt = "أعطني مثالاً عددياً وتطبيقياً محلولاً خطوة بخطوة على:\n$conceptInput"
                                    viewModel.explainCustomTopic(examplePrompt, selectedSubject)
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("مثال تطبيقي 🎯", fontSize = 11.sp, color = Color(0xFF78350F))
                            }
                        }
                    }
                }
            }
        }
    }
}
