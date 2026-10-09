package com.example.data.solver

import org.json.JSONArray
import org.json.JSONObject

object EducationalSolutionParser {

    fun parse(rawText: String, defaultSubject: String = "عام"): EducationalSolution {
        val trimmed = rawText.trim()
        if (trimmed.isBlank()) {
            return EducationalSolution(
                isImageClear = false,
                unclearReason = "لم يتم استلام أي إجابة من الذكاء الاصطناعي. يرجى إعادة المحاولة.",
                subject = defaultSubject,
                confidence = "unclear"
            )
        }

        // Try extracting JSON
        val jsonStr = extractJsonString(trimmed)
        if (jsonStr != null) {
            try {
                val obj = JSONObject(jsonStr)
                return parseFromJson(obj, defaultSubject)
            } catch (e: Throwable) {
                // regex or fallback
            }
        }

        // Fallback to text parsing
        return parseFromMarkdown(trimmed, defaultSubject)
    }

    private fun extractJsonString(text: String): String? {
        val cleaned = text
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        val firstBrace = cleaned.indexOf('{')
        val lastBrace = cleaned.lastIndexOf('}')
        if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
            return cleaned.substring(firstBrace, lastBrace + 1)
        }
        return null
    }

    private fun parseFromJson(obj: JSONObject, defaultSubject: String): EducationalSolution {
        val isImageClear = when {
            obj.has("is_image_clear") -> obj.optBoolean("is_image_clear", true)
            obj.has("isImageClear") -> obj.optBoolean("isImageClear", true)
            else -> true
        }

        val unclearReason = obj.optString("unclear_reason").takeIf { it.isNotBlank() }
            ?: obj.optString("unclearReason").takeIf { it.isNotBlank() }

        val subject = obj.optString("subject").ifBlank { defaultSubject }
        val questionUnderstanding = obj.optString("question_understanding").ifBlank { obj.optString("understanding") }
        val givens = getFlexibleStringList(obj, "givens")
        val required = obj.optString("required")
        val laws = getFlexibleStringList(obj, "laws")
        val substitutionSteps = getFlexibleStringList(obj, "substitution_steps")
        val calculationSteps = getFlexibleStringList(obj, "calculation_steps")
        val finalAnswer = obj.optString("final_answer").ifBlank { obj.optString("answer") }
        val mcAnswer = obj.optString("multiple_choice_answer").takeIf { it.isNotBlank() }
        val easierExplanation = obj.optString("easier_explanation").ifBlank { obj.optString("easierExplanation") }

        // Items list for full exams (e.g. 1 to 50 questions!)
        val itemsList = mutableListOf<QuestionSolvedItem>()
        val jsonArray = obj.optJSONArray("items") ?: obj.optJSONArray("questions")
        if (jsonArray != null) {
            for (i in 0 until jsonArray.length()) {
                val itemObj = jsonArray.optJSONObject(i) ?: continue
                val qNum = itemObj.optInt("question_number", i + 1)
                val qTitle = itemObj.optString("question_title").ifBlank { "السؤال $qNum" }
                val qText = itemObj.optString("question_text").ifBlank { itemObj.optString("text") }
                val qType = itemObj.optString("question_type").ifBlank {
                    if (itemObj.has("is_true")) "TRUE_FALSE" else "MULTIPLE_CHOICE"
                }
                val optLabel = itemObj.optString("selected_option_label").ifBlank { itemObj.optString("selected_option") }
                val optText = itemObj.optString("selected_option_text").takeIf { it.isNotBlank() }
                val isTrueVal = if (itemObj.has("is_true")) itemObj.optBoolean("is_true") else null
                val corr = itemObj.optString("correction").takeIf { it.isNotBlank() }
                val itemAns = itemObj.optString("final_answer").ifBlank { optText ?: optLabel }

                val itemGivens = getFlexibleStringList(itemObj, "givens")
                val itemLaws = getFlexibleStringList(itemObj, "laws")
                val itemSub = getFlexibleStringList(itemObj, "substitution_steps")
                val itemCalc = getFlexibleStringList(itemObj, "calculation_steps")

                itemsList.add(
                    QuestionSolvedItem(
                        questionNumber = qNum,
                        questionTitle = qTitle,
                        questionText = MathFormatter.cleanMathText(qText),
                        questionType = qType,
                        selectedOptionLabel = MathFormatter.formatOptionLabel(optLabel),
                        selectedOptionText = optText?.let { MathFormatter.cleanItem(it) },
                        isTrue = isTrueVal,
                        correction = corr?.let { MathFormatter.cleanItem(it) },
                        finalAnswer = MathFormatter.cleanItem(itemAns),
                        givens = MathFormatter.cleanItemList(itemGivens),
                        laws = MathFormatter.cleanItemList(itemLaws),
                        substitutionSteps = MathFormatter.cleanItemList(itemSub),
                        calculationSteps = MathFormatter.cleanItemList(itemCalc)
                    )
                )
            }
        }

        val isExam = itemsList.size > 1

        return EducationalSolution(
            isImageClear = isImageClear,
            unclearReason = unclearReason,
            subject = subject,
            questionUnderstanding = MathFormatter.cleanMathText(questionUnderstanding),
            givens = MathFormatter.cleanItemList(givens),
            required = MathFormatter.cleanItem(required),
            laws = MathFormatter.cleanItemList(laws),
            substitutionSteps = MathFormatter.cleanItemList(substitutionSteps),
            calculationSteps = MathFormatter.cleanItemList(calculationSteps),
            finalAnswer = MathFormatter.cleanItem(finalAnswer.ifBlank { if (isExam) "تم حل جميع أسئلة النموذج بالتفصيل (${itemsList.size} سؤالاً)" else "تم التوصل للحل بالخطوات الموضحة أعلاه" }),
            multipleChoiceAnswer = mcAnswer?.let { MathFormatter.formatOptionLabel(it) },
            easierExplanation = MathFormatter.cleanMathText(easierExplanation),
            items = itemsList,
            isExamSheet = isExam,
            totalQuestionsCount = itemsList.size
        )
    }

    private fun parseFromMarkdown(text: String, defaultSubject: String): EducationalSolution {
        return EducationalSolution(
            isImageClear = true,
            subject = defaultSubject,
            questionUnderstanding = "المسألة المدخلة",
            finalAnswer = MathFormatter.cleanItem(text.take(300)),
            calculationSteps = listOf(MathFormatter.cleanMathText(text))
        )
    }

    private fun getFlexibleStringList(obj: JSONObject, vararg keys: String): List<String> {
        for (k in keys) {
            val arr = obj.optJSONArray(k)
            if (arr != null) {
                val list = mutableListOf<String>()
                for (i in 0 until arr.length()) {
                    val s = arr.optString(i)
                    if (s.isNotBlank()) list.add(s)
                }
                if (list.isNotEmpty()) return list
            }
            val str = obj.optString(k)
            if (str.isNotBlank()) {
                return str.split("\n", "،", ";").map { it.trim() }.filter { it.isNotBlank() }
            }
        }
        return emptyList()
    }
}
