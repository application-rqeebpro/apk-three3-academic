package com.example.ui

import android.graphics.Bitmap
import java.util.UUID

enum class SolutionModality(val label: String, val iconName: String) {
    TEXT("شرح ونص", "Description"),
    VIDEO("فيديو تعليمي", "PlayCircle"),
    IMAGE("رسم ولوحة", "Image"),
    PDF("ملف PDF", "PictureAsPdf")
}

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val userImage: Bitmap? = null,
    val subject: String = "عام",
    val timestamp: Long = System.currentTimeMillis(),

    // Video solution components
    val videoTitle: String? = null,
    val videoUrl: String? = null,
    val videoDescription: String? = null,

    // Image / Board Diagram components
    val diagramTitle: String? = null,
    val diagramContent: String? = null,

    // PDF Booklet components
    val pdfTitle: String? = null,
    val pdfFilePath: String? = null,

    // Key educational takeaways
    val keyPoints: List<String> = emptyList(),
    val formulas: List<String> = emptyList()
)
