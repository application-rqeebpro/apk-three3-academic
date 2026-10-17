package com.example.ui.screens.curriculum

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.SubjectEntity
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val subjects by viewModel.allSubjects.collectAsState()
    var selectedTrack by remember { mutableStateOf("ALL") } // "ALL", "SCIENTIFIC", "LITERARY"

    val filteredSubjects = remember(subjects, selectedTrack) {
        when (selectedTrack) {
            "SCIENTIFIC" -> subjects.filter { it.track == "SCIENTIFIC" || it.track == "BOTH" }
            "LITERARY" -> subjects.filter { it.track == "LITERARY" || it.track == "BOTH" }
            else -> subjects
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("subjects_screen_content")
    ) {
        // Track Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedTrack == "ALL",
                onClick = { selectedTrack = "ALL" },
                label = { Text("جميع المواد (${subjects.size})") }
            )
            FilterChip(
                selected = selectedTrack == "SCIENTIFIC",
                onClick = { selectedTrack = "SCIENTIFIC" },
                label = { Text("القسم العلمي 🔬") }
            )
            FilterChip(
                selected = selectedTrack == "LITERARY",
                onClick = { selectedTrack = "LITERARY" },
                label = { Text("القسم الأدبي 📚") }
            )
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredSubjects) { subject ->
                SubjectRowItem(
                    subject = subject,
                    onClick = { viewModel.selectSubject(subject) }
                )
            }
        }
    }
}

@Composable
fun SubjectRowItem(
    subject: SubjectEntity,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                val iconText = when (subject.iconName) {
                    "math" -> "📐"
                    "physics" -> "⚡"
                    "chemistry" -> "🧪"
                    "biology" -> "🧬"
                    "arabic" -> "📖"
                    "english" -> "🔤"
                    "islamic" -> "🕌"
                    "history" -> "🏛️"
                    "geography" -> "🌍"
                    else -> "📚"
                }
                Text(text = iconText, fontSize = 24.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = subject.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (subject.track == "SCIENTIFIC") Color(0xFFEFF6FF) else Color(0xFFFFFBEB)
                    ) {
                        Text(
                            text = if (subject.track == "SCIENTIFIC") "علمي" else if (subject.track == "LITERARY") "أدبي" else "مشترك",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (subject.track == "SCIENTIFIC") NavyPrimary else AmberSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subject.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "فتح المادة",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
