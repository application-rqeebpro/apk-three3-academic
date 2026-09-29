package com.example.ui.screens.solver

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
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
import com.example.ui.Screen
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolveMyQuestionScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val questionInput by viewModel.solverQuestionInput.collectAsState()
    val selectedSubject by viewModel.solverSelectedSubject.collectAsState()
    val imageBitmap by viewModel.solverImageBitmap.collectAsState()
    val solutionResult by viewModel.solverResultText.collectAsState()
    val isSolving by viewModel.isSolving.collectAsState()

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            try {
                val bmp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
                viewModel.solverImageBitmap.value = bmp
            } catch (e: Exception) {
                viewModel.toastMessage.value = "تعذر تحميل الصورة"
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("solver_screen_content"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "✏️", fontSize = 24.sp)
                    }

                    Column {
                        Text(
                            text = "حل سؤالي خطوة بخطوة 🧠",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                        Text(
                            text = "اكتب مسألتك أو التقط صورة لها وسيقوم المعلم الذكي بحلها وفق الخطوات الوزارية التسع.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }

        // Subject Selector Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "حدد مادة السؤال:",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                val subjects = listOf("الرياضيات", "الفيزياء", "الكيمياء", "الأحياء", "اللغة العربية", "اللغة الإنجليزية")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(subjects) { subj ->
                        val isSelected = selectedSubject == subj
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.solverSelectedSubject.value = subj },
                            label = { Text(text = subj, fontSize = 12.sp) }
                        )
                    }
                }
            }
        }

        // Question Input Field
        item {
            OutlinedTextField(
                value = questionInput,
                onValueChange = { viewModel.solverQuestionInput.value = it },
                label = { Text("اكتب نص المسألة أو السؤال هنا...") },
                placeholder = { Text("مثال: س² - 4 = 0 أو احسب طاقة حركة الإلكترون عندما...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp)
                    .testTag("solver_question_input"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )
        }

        // Math Equation Symbols Quick Bar
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "رموز سريعة للمسائل الرياضية والعلمية:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    val symbols = listOf("²", "³", "√", "π", "θ", "Δ", "∑", "→", "±", "×", "÷", "λ", "Ω", "∞", "∫", "جا", "جتا", "ظا")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(symbols) { sym ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier
                                    .clickable {
                                        viewModel.solverQuestionInput.value += sym
                                    }
                                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            ) {
                                Text(
                                    text = sym,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Image / PDF / Camera Upload Actions
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "إرفاق صورة أو صفحة من الكتاب (JPG, PNG, WEBP, PDF):",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { photoPickerLauncher.launch("image/*") },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "تصوير / اختيار صورة", fontSize = 12.sp)
                        }

                        if (imageBitmap != null) {
                            Button(
                                onClick = {
                                    viewModel.solverImageBitmap.value = null
                                    selectedImageUri = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(text = "حذف الصورة 🗑️", color = ErrorRed, fontSize = 12.sp)
                            }
                        }
                    }

                    if (imageBitmap != null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = CardTeal,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = TealAccent)
                                Text(
                                    text = "تم إرفاق صورة المسألة بنجاح وهي جاهزة للتحليل",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF134E4A), fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Action Buttons: Solve and Clear
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { viewModel.solveQuestion() },
                    enabled = !isSolving,
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("solve_question_button")
                ) {
                    if (isSolving) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "جاري قراءة وحل المسألة...", color = Color.White)
                    } else {
                        Icon(imageVector = Icons.Default.Calculate, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "حل المسألة بالخطوات التسع 🚀", fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedButton(
                    onClick = { viewModel.clearSolver() },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.height(52.dp)
                ) {
                    Text(text = "مسح")
                }
            }
        }

        // Solution Display
        if (solutionResult != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("solution_result_card")
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFECFDF5)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(text = "🛡️", fontSize = 16.sp)
                                    Text(
                                        text = "الحل الوزاري الدقيق (مفحوص ومُدقق)",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TealAccent
                                        )
                                    )
                                }
                            }

                            IconButton(onClick = {
                                com.example.ui.components.copyToClipboard(context, solutionResult ?: "", "الحل الكامل")
                            }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "نسخ", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Text(
                            text = solutionResult ?: "",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                lineHeight = 24.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                        // "خدمة اشرح لي هذا الحل"
                        Text(
                            text = "💡 خدمة اشرح لي هذا الحل:",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = AmberSecondary
                            )
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        val prompt = "اشرح لي حل هذه المسألة بأسلوب بسيط جداً وتشبيه من الواقع:\n${solutionResult?.take(500)}"
                                        viewModel.explainCustomTopic(prompt, selectedSubject)
                                        viewModel.navigateTo(Screen.ExplainMe)
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("💡 اشرحه بأبسط لغة", fontSize = 11.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        val prompt = "لماذا استخدمنا هذا القانون بالذات في حل هذه المسألة؟ وماذا لو استخدمنا غيره؟\n${solutionResult?.take(500)}"
                                        viewModel.explainCustomTopic(prompt, selectedSubject)
                                        viewModel.navigateTo(Screen.ExplainMe)
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("❓ لماذا هذا القانون؟", fontSize = 11.sp)
                                }
                            }

                            OutlinedButton(
                                onClick = {
                                    val prompt = "ما فهمت الخطوة الثانية في هذا الحل، يرجى تفكيكها وشرحها بالتفصيل:\n${solutionResult?.take(500)}"
                                    viewModel.explainCustomTopic(prompt, selectedSubject)
                                    viewModel.navigateTo(Screen.ExplainMe)
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("🔍 ما فهمت الخطوة الثانية، فصّلها لي", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
