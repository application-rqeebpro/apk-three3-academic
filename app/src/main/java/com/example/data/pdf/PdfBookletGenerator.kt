package com.example.data.pdf

import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.example.data.local.entities.LessonEntity
import com.example.data.local.entities.SubjectEntity
import com.example.data.local.entities.UnitEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object PdfBookletGenerator {

    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN = 36f

    suspend fun generateLessonBooklet(
        context: Context,
        lesson: LessonEntity,
        subject: SubjectEntity?,
        unit: UnitEntity? = null
    ): File = withContext(Dispatchers.IO) {
        val pdfDir = File(context.filesDir, "pdfs").apply { mkdirs() }
        val sanitizedTitle = lesson.title.replace(Regex("[^\\w\\p{L}]+"), "_")
        val file = File(pdfDir, "ملزمة_${subject?.name ?: "مادة"}_${sanitizedTitle}.pdf")

        if (file.exists() && file.length() > 1024) {
            return@withContext file
        }

        val pdfDoc = PdfDocument()

        // Page 1: Cover
        val coverPageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        val coverPage = pdfDoc.startPage(coverPageInfo)
        val coverCanvas = coverPage.canvas

        drawOfficialCover(coverCanvas, lesson, subject, unit)
        pdfDoc.finishPage(coverPage)

        // Pages 2+: Content
        val sections = mutableListOf<PdfSection>()
        if (lesson.coreIdea.isNotBlank()) {
            sections.add(PdfSection("📌 الفكرة الجوهرية للدرس", lesson.coreIdea, Color.rgb(0, 68, 179)))
        }
        if (lesson.simplifiedExplanation.isNotBlank()) {
            sections.add(PdfSection("💡 الشرح الوزاري الميسر", lesson.simplifiedExplanation, Color.rgb(16, 120, 70)))
        }
        if (lesson.formulas.isNotBlank()) {
            sections.add(PdfSection("📐 القوانين والمعادلات المعتمدة", lesson.formulas, Color.rgb(194, 65, 12), isFormulaBox = true))
        }
        if (lesson.symbolsExplanation.isNotBlank()) {
            sections.add(PdfSection("🔍 دلالات الرموز والوحدات", lesson.symbolsExplanation, Color.rgb(79, 70, 229)))
        }
        if (lesson.keyPoints.isNotBlank()) {
            sections.add(PdfSection("⭐ مفاتيح الحل ونقاط التركيز", lesson.keyPoints, Color.rgb(15, 118, 110)))
        }
        if (lesson.solvedExample.isNotBlank()) {
            sections.add(PdfSection("✍️ مثال وزاري نموذجي محلول", lesson.solvedExample, Color.rgb(0, 68, 179), isExample = true))
        }
        if (lesson.practiceExercise.isNotBlank()) {
            sections.add(PdfSection("📝 تدريب تطبيقي مع الحل", lesson.practiceExercise, Color.rgb(88, 28, 135)))
        }

        renderContentToPages(pdfDoc, sections, lesson.title, subject?.name ?: "المنهج الثانوي")

        FileOutputStream(file).use { out ->
            pdfDoc.writeTo(out)
        }
        pdfDoc.close()
        file
    }

    suspend fun generateSmartTeacherAnswerPdf(
        context: Context,
        question: String,
        answer: String,
        subject: String,
        keyPoints: List<String> = emptyList(),
        formulas: List<String> = emptyList(),
        videoTitle: String? = null
    ): File = withContext(Dispatchers.IO) {
        val pdfDir = File(context.filesDir, "pdfs").apply { mkdirs() }
        val sanitizedSubject = subject.replace(Regex("[^\\w\\p{L}]+"), "_")
        val timestamp = System.currentTimeMillis()
        val file = File(pdfDir, "شرح_أستاذي_الذكي_${sanitizedSubject}_$timestamp.pdf")

        val pdfDoc = PdfDocument()

        val sections = mutableListOf<PdfSection>()
        if (question.isNotBlank()) {
            sections.add(PdfSection("❓ سؤال واستفسار الطالب", question, Color.rgb(0, 68, 179)))
        }
        sections.add(PdfSection("👨‍🏫 إجابة وشرح الأستاذ الذكي", answer, Color.rgb(16, 120, 70)))

        if (formulas.isNotEmpty()) {
            sections.add(PdfSection("📐 القوانين والمعادلات المعتمدة", formulas.joinToString("\n• "), Color.rgb(194, 65, 12), isFormulaBox = true))
        }
        if (keyPoints.isNotEmpty()) {
            sections.add(PdfSection("⭐ مفاتيح المذاكرة ونقاط التركيز", keyPoints.joinToString("\n• "), Color.rgb(15, 118, 110)))
        }
        if (!videoTitle.isNullOrBlank()) {
            sections.add(PdfSection("🎬 درس الفيديو التعليمي المرتبط", "العنوان الموصى به: $videoTitle", Color.rgb(79, 70, 229)))
        }

        renderContentToPages(pdfDoc, sections, "شرح الأستاذ الذكي", subject)

        FileOutputStream(file).use { out ->
            pdfDoc.writeTo(out)
        }
        pdfDoc.close()
        file
    }

    suspend fun savePdfToDownloads(context: Context, sourceFile: File): File = withContext(Dispatchers.IO) {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val destFile = File(downloadsDir, sourceFile.name)
        sourceFile.copyTo(destFile, overwrite = true)
        destFile
    }

    private fun drawOfficialCover(
        canvas: Canvas,
        lesson: LessonEntity,
        subject: SubjectEntity?,
        unit: UnitEntity?
    ) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.WHITE
        canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), PAGE_HEIGHT.toFloat(), paint)

        // Borders
        paint.style = Paint.Style.STROKE
        paint.color = Color.rgb(0, 68, 179)
        paint.strokeWidth = 3f
        canvas.drawRoundRect(MARGIN, MARGIN, PAGE_WIDTH - MARGIN, PAGE_HEIGHT - MARGIN, 12f, 12f, paint)

        paint.strokeWidth = 1f
        paint.color = Color.rgb(217, 119, 6)
        canvas.drawRoundRect(MARGIN + 6f, MARGIN + 6f, PAGE_WIDTH - MARGIN - 6f, PAGE_HEIGHT - MARGIN - 6f, 8f, 8f, paint)
        paint.style = Paint.Style.FILL

        // Header
        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 12f
            color = Color.rgb(30, 41, 59)
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("الجمهورية اليمنية • وزارة التربية والتعليم", PAGE_WIDTH / 2f, MARGIN + 40f, textPaint)

        // Academy Tag
        val brandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(0, 68, 179) }
        val brandRect = RectF(PAGE_WIDTH / 2f - 140f, MARGIN + 60f, PAGE_WIDTH / 2f + 140f, MARGIN + 95f)
        canvas.drawRoundRect(brandRect, 8f, 8f, brandPaint)

        val brandTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 13f
            color = Color.WHITE
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("🇾🇪 منصة رَقِــيـب للتعليم الثانوي (الإصدار 4)", PAGE_WIDTH / 2f, MARGIN + 83f, brandTextPaint)

        // Center Card
        val centerCard = RectF(MARGIN + 20f, 180f, PAGE_WIDTH - MARGIN - 20f, 380f)
        paint.color = Color.rgb(248, 250, 252)
        canvas.drawRoundRect(centerCard, 16f, 16f, paint)

        paint.style = Paint.Style.STROKE
        paint.color = Color.rgb(226, 232, 240)
        canvas.drawRoundRect(centerCard, 16f, 16f, paint)
        paint.style = Paint.Style.FILL

        // Subject Badge
        val subBadge = RectF(PAGE_WIDTH / 2f - 75f, 200f, PAGE_WIDTH / 2f + 75f, 230f)
        paint.color = Color.rgb(0, 68, 179)
        canvas.drawRoundRect(subBadge, 14f, 14f, paint)

        val subPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 12f
            color = Color.WHITE
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("مادة ${subject?.name ?: "الرياضيات"}", PAGE_WIDTH / 2f, 220f, subPaint)

        // Title
        val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 18f
            color = Color.rgb(15, 23, 42)
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
        }
        val layout = StaticLayout.Builder.obtain("ملزمة: " + lesson.title, 0, ("ملزمة: " + lesson.title).length, titlePaint, (PAGE_WIDTH - 120).toInt())
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .build()
        canvas.save()
        canvas.translate(PAGE_WIDTH / 2f - (PAGE_WIDTH - 120) / 2f, 255f)
        layout.draw(canvas)
        canvas.restore()

        val subInfoPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 11f
            color = Color.rgb(71, 85, 105)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(unit?.title ?: "الوحدة الوزارية المقررة", PAGE_WIDTH / 2f, 330f, subInfoPaint)

        // Feature list
        val featurePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 10f
            color = Color.rgb(16, 120, 70)
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("✓ شرح كامل  •  ✓ قوانين وزارية  •  ✓ أمثلة محلولة  •  ✓ طباعة عالية الدقة", PAGE_WIDTH / 2f, 360f, featurePaint)

        // Footer
        val footerPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 9f
            color = Color.rgb(148, 163, 184)
            textAlign = Paint.Align.CENTER
        }
        val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        canvas.drawText("منصة رقيب للتعليم الثانوي بالجمهورية اليمنية • صادر بتاريخ: $dateStr", PAGE_WIDTH / 2f, PAGE_HEIGHT - MARGIN - 20f, footerPaint)
    }

    private fun renderContentToPages(
        pdfDoc: PdfDocument,
        sections: List<PdfSection>,
        lessonTitle: String,
        subjectName: String
    ) {
        var pageNum = 2
        var currentY = MARGIN + 45f
        val maxY = PAGE_HEIGHT - MARGIN - 35f

        var pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNum).create()
        var page = pdfDoc.startPage(pageInfo)
        var canvas = page.canvas

        drawPageHeader(canvas, lessonTitle, subjectName)
        drawPageFooter(canvas, pageNum)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        for (sec in sections) {
            val contentWidth = (PAGE_WIDTH - MARGIN * 2 - 24f).toInt()
            val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 10f
                color = Color.rgb(30, 41, 59)
            }
            val textLayout = StaticLayout.Builder.obtain(sec.content, 0, sec.content.length, textPaint, contentWidth)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(0f, 1.25f)
                .setIncludePad(true)
                .build()

            val headerHeight = 28f
            val cardHeight = headerHeight + textLayout.height + 20f

            if (currentY + cardHeight > maxY) {
                pdfDoc.finishPage(page)
                pageNum++
                pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNum).create()
                page = pdfDoc.startPage(pageInfo)
                canvas = page.canvas
                drawPageHeader(canvas, lessonTitle, subjectName)
                drawPageFooter(canvas, pageNum)
                currentY = MARGIN + 45f
            }

            // Draw Card
            val cardRect = RectF(MARGIN, currentY, PAGE_WIDTH - MARGIN, currentY + cardHeight)
            paint.color = if (sec.isFormulaBox) Color.rgb(255, 247, 237) else Color.rgb(248, 250, 252)
            canvas.drawRoundRect(cardRect, 8f, 8f, paint)

            paint.style = Paint.Style.STROKE
            paint.color = sec.accentColor
            paint.strokeWidth = 1f
            canvas.drawRoundRect(cardRect, 8f, 8f, paint)
            paint.style = Paint.Style.FILL

            // Title
            val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 10.5f
                color = sec.accentColor
                typeface = Typeface.DEFAULT_BOLD
                textAlign = Paint.Align.RIGHT
            }
            canvas.drawText(sec.title, PAGE_WIDTH - MARGIN - 14f, currentY + 18f, titlePaint)

            // Text
            canvas.save()
            canvas.translate(MARGIN + 12f, currentY + headerHeight + 4f)
            textLayout.draw(canvas)
            canvas.restore()

            currentY += cardHeight + 12f
        }

        pdfDoc.finishPage(page)
    }

    private fun drawPageHeader(canvas: Canvas, lessonTitle: String, subjectName: String) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(226, 232, 240)
            strokeWidth = 1f
        }
        canvas.drawLine(MARGIN, MARGIN + 22f, PAGE_WIDTH - MARGIN, MARGIN + 22f, paint)

        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 8.5f
            color = Color.rgb(100, 116, 139)
        }
        textPaint.textAlign = Paint.Align.RIGHT
        canvas.drawText("منصة رقيب • مادة $subjectName • $lessonTitle", PAGE_WIDTH - MARGIN, MARGIN + 16f, textPaint)

        textPaint.textAlign = Paint.Align.LEFT
        canvas.drawText("المنهج اليمني 🇾🇪", MARGIN, MARGIN + 16f, textPaint)
    }

    private fun drawPageFooter(canvas: Canvas, pageNumber: Int) {
        val y = PAGE_HEIGHT - MARGIN
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(226, 232, 240)
            strokeWidth = 1f
        }
        canvas.drawLine(MARGIN, y - 16f, PAGE_WIDTH - MARGIN, y - 16f, paint)

        val footerPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 8.5f
            color = Color.rgb(148, 163, 184)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("الصفحة $pageNumber", PAGE_WIDTH / 2f, y - 4f, footerPaint)
    }

    private data class PdfSection(
        val title: String,
        val content: String,
        val accentColor: Int,
        val isFormulaBox: Boolean = false,
        val isExample: Boolean = false
    )
}
