package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<GeminiContent>,
    @Json(name = "generationConfig")
    val generationConfig: GeminiGenerationConfig? = null,
    @Json(name = "systemInstruction")
    val systemInstruction: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    val role: String = "user",
    val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiPart(
    val text: String? = null,
    @Json(name = "inline_data")
    val inlineData: GeminiInlineData? = null,
    @Json(name = "thoughtSignature")
    val thoughtSignature: String? = null
)

data class AttachedFile(
    val fileName: String,
    val mimeType: String,
    val base64Data: String,
    val sizeBytes: Long
)

@JsonClass(generateAdapter = true)
data class GeminiInlineData(
    @Json(name = "mime_type")
    val mimeType: String,
    val data: String
)

@JsonClass(generateAdapter = true)
data class GeminiGenerationConfig(
    val temperature: Float? = 0.2f,
    val topP: Float? = 0.95f,
    val topK: Int? = 40,
    val maxOutputTokens: Int? = 4096,
    @Json(name = "response_mime_type")
    val responseMimeType: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    val content: GeminiContent? = null
)
