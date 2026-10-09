package com.example.data.pdf

enum class BookletCategory(val displayName: String) {
    ALL("الكل"),
    LESSON_SUMMARY("ملازم الدروس المفصلة"),
    UNIT_BOOKLET("ملازم الوحدات الشاملة"),
    FORMULA_SHEET("كراسات القوانين والمعادلات"),
    MINISTER_EXAMS("نماذج الاختبارات الوزارية")
}

data class PdfBookletItem(
    val id: String,
    val title: String,
    val subjectId: Long,
    val subjectName: String,
    val unitName: String,
    val lessonId: Long? = null,
    val description: String,
    val estimatedPages: Int,
    val fileSizeLabel: String,
    val category: BookletCategory = BookletCategory.LESSON_SUMMARY,
    val isOfficialCurriculum: Boolean = true
)
