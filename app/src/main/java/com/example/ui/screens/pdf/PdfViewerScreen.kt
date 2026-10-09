package com.example.ui.screens.pdf

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.DocumentBookletEntity
import com.example.ui.MainViewModel
import com.example.ui.Screen
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfViewerScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onNavigate: (Screen) -> Unit
) {
    val context = LocalContext.current
    val booklets by viewModel.allDocumentBooklets.collectAsState()
    val selectedBooklet by viewModel.selectedBooklet.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var filterType by remember { mutableStateOf("ALL") } // "ALL", "PDF", "WORD"
    var viewingBooklet by remember { mutableStateOf<DocumentBookletEntity?>(selectedBooklet) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredList = booklets.filter { b ->
        (filterType == "ALL" || b.fileType == filterType) &&
                (searchQuery.isBlank() || b.title.contains(searchQuery) || b.subjectName.contains(searchQuery))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(viewingBooklet?.title ?: "الملازم والملفات الدراسية") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (viewingBooklet != null) {
                            viewingBooklet = null
                        } else {
                            onBack()
                        }
                    }) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "رجوع")
                    }
                },
                actions = {
                    if (currentUser?.role == "ADMIN") {
                        IconButton(onClick = { onNavigate(Screen.AdminContent) }) {
                            Icon(Icons.Default.Add, contentDescription = "إضافة ملف جديد")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (viewingBooklet != null) {
                // Reading View for Selected Booklet
                BookletReaderView(
                    booklet = viewingBooklet!!,
                    context = context,
                    onClose = { viewingBooklet = null }
                )
            } else {
                // Booklets Catalog
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Search Field
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("بحث في الملازم والملخصات...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = null)
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    // Filter Chips (All, PDF, Word)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = filterType == "ALL",
                            onClick = { filterType = "ALL" },
                            label = { Text("الكل (${booklets.size})") }
                        )
                        FilterChip(
                            selected = filterType == "PDF",
                            onClick = { filterType = "PDF" },
                            label = { Text("ملفات PDF (${booklets.count { it.fileType == "PDF" }})") },
                            leadingIcon = { Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                        FilterChip(
                            selected = filterType == "WORD",
                            onClick = { filterType = "WORD" },
                            label = { Text("ملفات Word (${booklets.count { it.fileType == "WORD" }})") },
                            leadingIcon = { Icon(Icons.Default.Article, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                    }

                    if (filteredList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.FolderOpen,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                                Text(
                                    "لا توجد ملازم دراسية متطابقة مع البحث",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                            items(filteredList) { booklet ->
                                BookletItemCard(
                                    booklet = booklet,
                                    onRead = { viewingBooklet = booklet },
                                    onDownload = { downloadBooklet(context, booklet) },
                                    onShare = { shareBooklet(context, booklet) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookletItemCard(
    booklet: DocumentBookletEntity,
    onRead: () -> Unit,
    onDownload: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onRead),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (booklet.fileType == "PDF") Color(0xFFEF4444) else Color(0xFF3B82F6)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (booklet.fileType == "PDF") Icons.Default.PictureAsPdf else Icons.Default.Description,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = booklet.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${booklet.subjectName} • ${booklet.fileSize}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (booklet.fileType == "PDF") Color(0xFFFEE2E2) else Color(0xFFDBEAFE)
                ) {
                    Text(
                        text = booklet.fileType,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = if (booklet.fileType == "PDF") Color(0xFFDC2626) else Color(0xFF2563EB),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (booklet.description.isNotEmpty()) {
                Text(
                    text = booklet.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onRead,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("قراءة الملف")
                }
                OutlinedButton(
                    onClick = onDownload,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = "تحميل")
                }
                IconButton(onClick = onShare) {
                    Icon(Icons.Default.Share, contentDescription = "مشاركة")
                }
            }
        }
    }
}

@Composable
fun BookletReaderView(
    booklet: DocumentBookletEntity,
    context: Context,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
                    text = booklet.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "المادة: ${booklet.subjectName} | الصيغة: ${booklet.fileType} | الحجم: ${booklet.fileSize}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }

        // Reader Content Simulator
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📖 قارئ الملازم الأكاديمي (النسخة الوزارية)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "الصفحة 1 من 12",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }

                Divider()

                Text(
                    text = "جمهورية اليمن - وزارة التربية والتعليم\nقطاع المناهج والتوجيه - الإدارة العامة للاختبارات",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = booklet.description.ifEmpty {
                        "ملزمة شاملة تغطي الشرح النظري والقوانين الأساسية ونماذج حل المسائل الوزارية لأعوام 2018 - 2024م وفقاً للنظام الأتمتة الحديث."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 24.sp
                )

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "💡 تنبيه وإرشادات للطالب:",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            text = "يمكنك حفظ هذا الملف إلى وحدة التخزين بجهازك لفتحه عبر قارئ الـ PDF الخارجي أو تطبيق Word، أو طباعته ورقياً.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { downloadBooklet(context, booklet) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Download, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("تنزيل الملف إلى الجهاز")
            }
            OutlinedButton(
                onClick = { shareBooklet(context, booklet) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("مشاركة الرابط")
            }
        }
    }
}

fun downloadBooklet(context: Context, booklet: DocumentBookletEntity) {
    try {
        val ext = if (booklet.fileType == "PDF") ".pdf" else ".docx"
        val cleanName = booklet.fileName.ifEmpty { "${booklet.title}$ext" }
        val destFile = File(context.getExternalFilesDir(null), cleanName)

        // Copy asset or generate file
        val assetName = if (booklet.fileType == "PDF") "booklets/math_calculus_notes.pdf" else "booklets/biology_genetics_summary.docx"
        try {
            context.assets.open(assetName).use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
        } catch (_: Exception) {
            destFile.writeText("ملف دراسي: ${booklet.title}\nأكاديمية رقيب للصف الثالث الثانوي")
        }

        Toast.makeText(context, "تم حفظ الملف بنجاح: ${destFile.name}", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(context, "خطأ أثناء حفظ الملف: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

fun shareBooklet(context: Context, booklet: DocumentBookletEntity) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "ملزمة دراسية من تطبيق أكاديمية رقيب:\n${booklet.title} (${booklet.fileType})\nمادة: ${booklet.subjectName}")
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, "مشاركة الملزمة"))
}
