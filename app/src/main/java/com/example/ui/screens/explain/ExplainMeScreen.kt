package com.example.ui.screens.explain

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExplainMeScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val selectedLesson by viewModel.selectedLesson.collectAsState()
    val explanationText by viewModel.simplerExplanationText.collectAsState()
    val isGenerating by viewModel.isGeneratingSimpler.collectAsState()

    var customPrompt by remember { mutableStateOf("") }
    val subjects = listOf("الرياضيات", "الكيمياء", "الفيزياء", "الأحياء")
    var selectedSubject by remember { mutableStateOf("الرياضيات") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("اشرح لي الدرس بأسلوب مبسط") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "رجوع")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "💡 معلم الشرح الفوري الذكي",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = if (selectedLesson != null) {
                                "شرح تفصيلي ومبسط لدرس: ${selectedLesson!!.title}"
                            } else {
                                "اكتب أي مفهوم أو درس صعب وسيقوم الذكاء الاصطناعي بتبسيطه بأمثلة واقعية وفق المنهج اليمني."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            if (selectedLesson == null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("اختر المادة واكتب عنوان المفهوم:", fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                subjects.forEach { sub ->
                                    FilterChip(
                                        selected = selectedSubject == sub,
                                        onClick = { selectedSubject = sub },
                                        label = { Text(sub) }
                                    )
                                }
                            }
                            OutlinedTextField(
                                value = customPrompt,
                                onValueChange = { customPrompt = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("مثال: اشرح لي مبرهنة ديموافر والجذور النونية...") },
                                minLines = 2,
                                shape = RoundedCornerShape(12.dp)
                            )
                            Button(
                                onClick = { viewModel.explainCustomTopic(customPrompt, selectedSubject) },
                                enabled = !isGenerating && customPrompt.isNotBlank(),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                if (isGenerating) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                                } else {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("شرح المفهوم الآن")
                                }
                            }
                        }
                    }
                }
            } else {
                item {
                    Button(
                        onClick = { viewModel.requestSimplerExplanation() },
                        enabled = !isGenerating,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                        } else {
                            Icon(Icons.Default.Psychology, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("توليد شرح إضافي مبسط للدرس")
                        }
                    }
                }
            }

            if (explanationText.isNotBlank()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.School, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("الشرح المبسط التفاعلي:", fontWeight = FontWeight.Bold)
                            }
                            Divider()
                            Text(
                                text = explanationText,
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = 24.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
