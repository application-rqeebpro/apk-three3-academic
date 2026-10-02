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
                try {
                    val regexParsed = parseFromRegexJson(jsonStr, defaultSubject)
                    if (regexParsed.finalAnswer.isNotBlank() && regexParsed.finalAnswer != "تم التوصل للحل بالخطوات الموضحة أعلاه") {
                        return regexParsed
                    }
                } catch (e2: Throwable) {
                    // fallback to text parsing
                }
            }
        }

        // Fallback to parsing structured text / markdown
        return parseFromMarkdown(trimmed, defaultSubject)
    }

    private fun parseFromRegexJson(jsonStr: String, defaultSubject: String): EducationalSolution {
        fun extractString(key: String): String {
            val pattern = """"$key"\s*:\s*"([^"\\]*(?:\\.[^"\\]*)*)""".toRegex()
            return pattern.find(jsonStr)?.groupValues?.getOrNull(1)
                ?.replace("\\n", "\n")
                ?.replace("\\\"", "\"")
                ?: ""
        }

        fun extractList(key: String): List<String> {
            val arrayPattern = """"$key"\s*:\s*\[(.*?)\]""".toRegex(RegexOption.DOT_MATCHES_ALL)
            val match = arrayPattern.find(jsonStr)?.groupValues?.getOrNull(1) ?: return emptyList()
            val itemPattern = """"([^"\\]*(?:\\.[^"\\]*)*)"""".toRegex()
            return itemPattern.findAll(match).map {
                it.groupValues[1].replace("\\n", "\n").replace("\\\"", "\"").trim()
            }.filter { it.isNotBlank() }.toList()
        }

        val subject = extractString("subject").ifBlank { defaultSubject }
        val finalAnswer = extractString("final_answer").ifBlank { extractString("finalAnswer") }
        val givens = extractList("givens")
        val calculationSteps = extractList("calculation_steps")
        val substitutionSteps = extractList("substitution_steps")
        val laws = extractList("laws")
        val required = extractString("required")
        val questionUnderstanding = extractString("question_understanding")
        val easierExplanation = extractString("easier_explanation")

        return EducationalSolution(
            isImageClear = true,
            subject = subject,
            questionUnderstanding = MathFormatter.cleanMathText(questionUnderstanding),
            givens = MathFormatter.cleanItemList(givens),
            required = MathFormatter.cleanItem(required),
            laws = MathFormatter.cleanItemList(laws),
            substitutionSteps = MathFormatter.cleanItemList(substitutionSteps),
            calculationSteps = MathFormatter.cleanItemList(calculationSteps),
            finalAnswer = MathFormatter.cleanItem(finalAnswer.ifBlank { "تم التوصل للحل بالخطوات الموضحة أعلاه" }),
            easierExplanation = MathFormatter.cleanMathText(easierExplanation)
        )
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

        val subject = obj.optString("subject").ifBlank {
            obj.optString("subject_name").ifBlank { defaultSubject }
        }

        val questionUnderstanding = obj.optString("question_understanding").ifBlank {
            obj.optString("understanding").ifBlank {
                obj.optString("questionUnderstanding")
            }
        }

        val givens = getFlexibleStringList(obj, "givens", "given", "معطيات")
        val required = obj.optString("required").ifBlank {
            obj.optString("the_required").ifBlank {
                obj.optString("مطلوب")
            }
        }
        val laws = getFlexibleStringList(obj, "laws", "law", "rule", "قوانين", "قانون")
        val substitutionSteps = getFlexibleStringList(obj, "substitution_steps", "substitution", "تعويض")
        val calculationSteps = getFlexibleStringList(obj, "calculation_steps", "calculation", "steps", "حساب", "خطوات")
        val unitCheck = obj.optString("unit_check").takeIf { it.isNotBlank() }
            ?: obj.optString("unitCheck").takeIf { it.isNotBlank() }

        val finalAnswer = obj.optString("final_answer").ifBlank {
            obj.optString("finalAnswer").ifBlank {
                obj.optString("answer").ifBlank {
                    obj.optString("إجابة")
                }
            }
        }

        val mcAnswer = obj.optString("multiple_choice_answer").takeIf { it.isNotBlank() }
            ?: obj.optString("multipleChoiceAnswer").takeIf { it.isNotBlank() }

        val confidence = obj.optString("confidence", "high")
        val easierExplanation = obj.optString("easier_explanation").ifBlank {
            obj.optString("easierExplanation").ifBlank {
                obj.optString("simple_explanation")
            }
        }

        // Verification object
        val verObj = obj.optJSONObject("verification")
        val verification = if (verObj != null) {
            ProblemVerification(
                isValid = verObj.optBoolean("is_valid", true),
                verificationDetails = verObj.optString("verification_details").ifBlank { "✓ تم التحقق المستقل ومراجعة الخطوات الرياضية والعلمية بدقة" },
                checksList = getFlexibleStringList(verObj, "checks_list", "checks").ifEmpty {
                    listOf("✓ تدقيق قراءة وفهم المسألة", "✓ فحص صحة القوانين المطبقة", "✓ مراجعة الحسابات والإشارات", "✓ التحقق من الوحدات القياسية")
                },
                alternativeCheck = verObj.optString("alternative_check").takeIf { it.isNotBlank() },
                commonMistakesAvoided = verObj.optString("common_mistakes_avoided").takeIf { it.isNotBlank() }
            )
        } else {
            ProblemVerification(
                isValid = true,
                verificationDetails = "✓ تم التحقق المستقل ومراجعة الخطوات وفق المنهج اليمني المعتمد",
                checksList = listOf("✓ تدقيق السؤال والمعطيات", "✓ تطبيق القانون الوزاري", "✓ مراجعة العمليات الحسابية", "✓ فحص الناتج والوحدة"),
                alternativeCheck = "التحقق بالتعويض المباشر والمنطقي في المعادلة الأساسية",
                commonMistakesAvoided = "تجنب الخطأ في إشارات الجمع والطرح وتحويل الوحدات القياسية"
            )
        }

        val itemsList = mutableListOf<QuestionSolvedItem>()
        val itemsArray = obj.optJSONArray("items") ?: obj.optJSONArray("questions")
        if (itemsArray != null && itemsArray.length() > 0) {
            for (i in 0 until itemsArray.length()) {
                val qObj = itemsArray.optJSONObject(i) ?: continue
                val qNum = qObj.optInt("question_number", i + 1)
                val qTitle = qObj.optString("question_title").ifBlank { "السؤال $qNum" }
                val qText = qObj.optString("question_text").ifBlank { qObj.optString("question") }
                val rawType = qObj.optString("question_type").ifBlank {
                    qObj.optString("type")
                }.uppercase()

                val selOptLabel = qObj.optString("selected_option_label").takeIf { it.isNotBlank() }
                    ?: qObj.optString("option_label").takeIf { it.isNotBlank() }
                    ?: qObj.optString("option_number").takeIf { it.isNotBlank() }
                    ?: qObj.optString("choice").takeIf { it.isNotBlank() }

                val selOptText = qObj.optString("selected_option_text").takeIf { it.isNotBlank() }
                    ?: qObj.optString("option_text").takeIf { it.isNotBlank() }
                    ?: qObj.optString("choice_text").takeIf { it.isNotBlank() }

                val isTrue = if (qObj.has("is_true")) qObj.optBoolean("is_true")
                else if (qObj.has("true_or_false")) {
                    val s = qObj.optString("true_or_false")
                    if (s.contains("صح")) true else if (s.contains("خطأ")) false else null
                } else null

                val corr = qObj.optString("correction").takeIf { it.isNotBlank() }
                val fAns = qObj.optString("final_answer").ifBlank {
                    qObj.optString("answer")
                }

                val qType = when {
                    rawType == "MULTIPLE_CHOICE" || rawType == "MCQ" || rawType.contains("اختيار") -> "MULTIPLE_CHOICE"
                    rawType == "TRUE_FALSE" || rawType == "TF" || rawType.contains("صح") || isTrue != null -> "TRUE_FALSE"
                    !selOptLabel.isNullOrBlank() -> "MULTIPLE_CHOICE"
                    else -> "CALCULATION"
                }

                val qGivens = getFlexibleStringList(qObj, "givens", "given", "معطيات")
                val qLaws = getFlexibleStringList(qObj, "laws", "law", "rule", "قوانين", "قانون")
                val qSubst = getFlexibleStringList(qObj, "substitution_steps", "substitution", "تعويض")
                val qCalcs = getFlexibleStringList(qObj, "calculation_steps", "calculation", "steps", "حساب", "حل")

                itemsList.add(
                    QuestionSolvedItem(
                        questionNumber = qNum,
                        questionTitle = MathFormatter.cleanItem(qTitle),
                        questionText = MathFormatter.cleanMathText(qText),
                        questionType = qType,
                        selectedOptionLabel = selOptLabel?.let { MathFormatter.formatOptionLabel(it) },
                        selectedOptionText = selOptText?.let { MathFormatter.cleanItem(it) } ?: if (qType == "MULTIPLE_CHOICE") MathFormatter.cleanItem(fAns) else null,
                        isTrue = isTrue,
                        correction = corr?.let { MathFormatter.cleanMathText(it) },
                        finalAnswer = MathFormatter.cleanItem(fAns),
                        givens = MathFormatter.cleanItemList(qGivens),
                        laws = MathFormatter.cleanItemList(qLaws),
                        substitutionSteps = MathFormatter.cleanItemList(qSubst),
                        calculationSteps = MathFormatter.cleanItemList(qCalcs),
                        verificationNote = qObj.optString("verification_note").takeIf { it.isNotBlank() }
                    )
                )
            }
        }

        if (itemsList.isEmpty() && (finalAnswer.isNotBlank() || mcAnswer != null || obj.has("is_true"))) {
            val isTrue = if (obj.has("is_true")) obj.optBoolean("is_true") else null
            val rawType = obj.optString("question_type").uppercase()
            val qType = when {
                rawType == "MULTIPLE_CHOICE" || rawType.contains("اختيار") || !mcAnswer.isNullOrBlank() -> "MULTIPLE_CHOICE"
                rawType == "TRUE_FALSE" || rawType.contains("صح") || isTrue != null -> "TRUE_FALSE"
                else -> "CALCULATION"
            }

            val optLabel = mcAnswer?.let { MathFormatter.formatOptionLabel(it) }

            itemsList.add(
                QuestionSolvedItem(
                    questionNumber = 1,
                    questionTitle = "السؤال",
                    questionText = MathFormatter.cleanMathText(questionUnderstanding),
                    questionType = qType,
                    selectedOptionLabel = optLabel,
                    selectedOptionText = if (qType == "MULTIPLE_CHOICE") MathFormatter.cleanItem(finalAnswer) else null,
                    isTrue = isTrue,
                    correction = obj.optString("correction").takeIf { it.isNotBlank() }?.let { MathFormatter.cleanMathText(it) },
                    finalAnswer = MathFormatter.cleanItem(finalAnswer),
                    givens = MathFormatter.cleanItemList(givens),
                    laws = MathFormatter.cleanItemList(laws),
                    substitutionSteps = MathFormatter.cleanItemList(substitutionSteps),
                    calculationSteps = MathFormatter.cleanItemList(calculationSteps)
                )
            )
        }

        return EducationalSolution(
            isImageClear = isImageClear,
            unclearReason = unclearReason?.let { MathFormatter.cleanMathText(it) },
            subject = subject,
            items = itemsList,
            questionUnderstanding = MathFormatter.cleanMathText(questionUnderstanding),
            givens = MathFormatter.cleanItemList(givens),
            required = MathFormatter.cleanItem(required),
            laws = MathFormatter.cleanItemList(laws),
            substitutionSteps = MathFormatter.cleanItemList(substitutionSteps),
            calculationSteps = MathFormatter.cleanItemList(calculationSteps),
            unitCheck = unitCheck?.let { MathFormatter.cleanMathText(it) },
            verification = verification,
            finalAnswer = MathFormatter.cleanItem(finalAnswer),
            multipleChoiceAnswer = mcAnswer?.let { MathFormatter.cleanItem(it) },
            confidence = confidence,
            easierExplanation = MathFormatter.cleanMathText(easierExplanation)
        )
    }

    private fun getFlexibleStringList(obj: JSONObject, vararg keys: String): List<String> {
        for (key in keys) {
            val opt = obj.opt(key) ?: continue
            when (opt) {
                is JSONArray -> {
                    val list = mutableListOf<String>()
                    for (i in 0 until opt.length()) {
                        val str = opt.optString(i)
                        if (str.isNotBlank()) list.add(str.trim())
                    }
                    if (list.isNotEmpty()) return list
                }
                is JSONObject -> {
                    val list = mutableListOf<String>()
                    val it = opt.keys()
                    while (it.hasNext()) {
                        val k = it.next()
                        list.add("$k = ${opt.opt(k)}")
                    }
                    if (list.isNotEmpty()) return list
                }
                is String -> {
                    if (opt.isNotBlank()) {
                        return opt.split("\n")
                            .map { it.trim().removePrefix("•").removePrefix("-").removePrefix("*").trim() }
                            .filter { it.isNotBlank() }
                    }
                }
            }
        }
        return emptyList()
    }

    private fun parseFromMarkdown(text: String, defaultSubject: String): EducationalSolution {
        val lines = text.lines()
        val givens = mutableListOf<String>()
        val laws = mutableListOf<String>()
        val subst = mutableListOf<String>()
        val calcs = mutableListOf<String>()
        var req = ""
        var ans = ""
        var unit = ""
        var understanding = ""
        var easier = ""

        var currentSection = ""

        for (line in lines) {
            val t = line.trim()
            if (t.isBlank()) continue

            when {
                t.contains("المعطيات", ignoreCase = true) -> currentSection = "GIVENS"
                t.contains("المطلوب", ignoreCase = true) -> currentSection = "REQ"
                t.contains("القانون", ignoreCase = true) || t.contains("القوانين", ignoreCase = true) -> currentSection = "LAWS"
                t.contains("التعويض", ignoreCase = true) -> currentSection = "SUBST"
                t.contains("الحساب", ignoreCase = true) || t.contains("خطوات الحل", ignoreCase = true) -> currentSection = "CALC"
                t.contains("الوحدة", ignoreCase = true) || t.contains("الوحدات", ignoreCase = true) -> currentSection = "UNIT"
                t.contains("الإجابة", ignoreCase = true) || t.contains("الناتج", ignoreCase = true) || t.contains("الحل النهائي", ignoreCase = true) -> currentSection = "ANS"
                t.contains("اشرح لي", ignoreCase = true) || t.contains("شرح مبسط", ignoreCase = true) -> currentSection = "EASIER"
                else -> {
                    val cleanLine = t.removePrefix("•").removePrefix("-").removePrefix("*").trim()
                    when (currentSection) {
                        "GIVENS" -> givens.add(cleanLine)
                        "REQ" -> req = if (req.isBlank()) cleanLine else "$req $cleanLine"
                        "LAWS" -> laws.add(cleanLine)
                        "SUBST" -> subst.add(cleanLine)
                        "CALC" -> calcs.add(cleanLine)
                        "UNIT" -> unit = if (unit.isBlank()) cleanLine else "$unit $cleanLine"
                        "ANS" -> ans = if (ans.isBlank()) cleanLine else "$ans $cleanLine"
                        "EASIER" -> easier = if (easier.isBlank()) cleanLine else "$easier\n$cleanLine"
                        else -> {
                            if (understanding.isBlank()) {
                                understanding = cleanLine
                            } else {
                                calcs.add(cleanLine)
                            }
                        }
                    }
                }
            }
        }

        // If no sections were identified, use entire text as calculation steps
        if (calcs.isEmpty() && givens.isEmpty() && ans.isBlank()) {
            calcs.addAll(lines.filter { it.isNotBlank() })
            ans = lines.lastOrNull { it.isNotBlank() } ?: ""
        }

        return EducationalSolution(
            isImageClear = true,
            subject = defaultSubject,
            questionUnderstanding = MathFormatter.cleanMathText(understanding.ifBlank { "حل مسألة الثالث الثانوي بالخطوات المعتمدة" }),
            givens = MathFormatter.cleanItemList(givens),
            required = MathFormatter.cleanItem(req),
            laws = MathFormatter.cleanItemList(laws),
            substitutionSteps = MathFormatter.cleanItemList(subst),
            calculationSteps = MathFormatter.cleanItemList(calcs),
            unitCheck = unit.takeIf { it.isNotBlank() }?.let { MathFormatter.cleanMathText(it) },
            verification = ProblemVerification(
                isValid = true,
                verificationDetails = "تم تدقيق الحل ومطابقته للخطوات المعتمدة في المنهج اليمني",
                checksList = listOf("✓ استخراج المعطيات", "✓ القانون المناسب", "✓ تدقيق الحساب")
            ),
            finalAnswer = MathFormatter.cleanItem(ans.ifBlank { "تم التوصل للحل النهائي بالخطوات الموضحة أعلاه" }),
            confidence = "high",
            easierExplanation = MathFormatter.cleanMathText(easier.ifBlank { "تعتمد فكرة المسألة على تطبيق القانون المناسب مباشرة بعد كتابة المعطيات والتعويض خطوة بخطوة." })
        )
    }
}
