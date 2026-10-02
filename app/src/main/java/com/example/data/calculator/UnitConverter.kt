package com.example.data.calculator

import kotlin.math.abs
import kotlin.math.roundToLong

data class UnitCategory(
    val id: String,
    val nameArabic: String,
    val icon: String,
    val units: List<ConversionUnit>
)

data class ConversionUnit(
    val symbol: String,
    val nameArabic: String,
    val factorToBase: Double = 1.0, // Factor to multiply to get base unit
    val isTemperature: Boolean = false
)

object UnitConverter {

    val categories = listOf(
        UnitCategory(
            id = "length",
            nameArabic = "الطول والمسافة",
            icon = "📏",
            units = listOf(
                ConversionUnit("mm", "ملم (مليمتر)", 0.001),
                ConversionUnit("cm", "سم (سنتيمتر)", 0.01),
                ConversionUnit("m", "م (متر - الوحدة الأساسية)", 1.0),
                ConversionUnit("km", "كم (كيلومتر)", 1000.0),
                ConversionUnit("inch", "بوصة (إنش)", 0.0254),
                ConversionUnit("ft", "قدم", 0.3048)
            )
        ),
        UnitCategory(
            id = "mass",
            nameArabic = "الكتلة والوزن",
            icon = "⚖️",
            units = listOf(
                ConversionUnit("mg", "ملجم (مليجرام)", 0.000001),
                ConversionUnit("g", "جم (جرام)", 0.001),
                ConversionUnit("kg", "كجم (كيلوجرام)", 1.0),
                ConversionUnit("ton", "طن متري", 1000.0)
            )
        ),
        UnitCategory(
            id = "time",
            nameArabic = "الزمن والوقت",
            icon = "⏱️",
            units = listOf(
                ConversionUnit("ms", "مللي ثانية (ms)", 0.001),
                ConversionUnit("s", "ثانية (s)", 1.0),
                ConversionUnit("min", "دقيقة (min)", 60.0),
                ConversionUnit("hour", "ساعة (h)", 3600.0),
                ConversionUnit("day", "يوم (day)", 86400.0)
            )
        ),
        UnitCategory(
            id = "speed",
            nameArabic = "السرعة",
            icon = "🚀",
            units = listOf(
                ConversionUnit("m/s", "م/ث (متر لكل ثانية)", 1.0),
                ConversionUnit("km/h", "كم/ساعة", 1.0 / 3.6)
            )
        ),
        UnitCategory(
            id = "angle",
            nameArabic = "الزوايا",
            icon = "📐",
            units = listOf(
                ConversionUnit("Degree", "درجة ستينية (°)", 1.0),
                ConversionUnit("Radian", "راديان (دائري)", 180.0 / Math.PI)
            )
        ),
        UnitCategory(
            id = "temperature",
            nameArabic = "درجة الحرارة",
            icon = "🌡️",
            units = listOf(
                ConversionUnit("°C", "سيلزيوس (°C)", isTemperature = true),
                ConversionUnit("°F", "فهرنهايت (°F)", isTemperature = true),
                ConversionUnit("K", "كلفن (K)", isTemperature = true)
            )
        ),
        UnitCategory(
            id = "force",
            nameArabic = "القوة",
            icon = "💪",
            units = listOf(
                ConversionUnit("N", "نيوتن (N)", 1.0),
                ConversionUnit("kN", "كيلو نيوتن (kN)", 1000.0),
                ConversionUnit("dyne", "داين (Dyne)", 1e-5)
            )
        ),
        UnitCategory(
            id = "energy",
            nameArabic = "الطاقة والشغل",
            icon = "⚡",
            units = listOf(
                ConversionUnit("J", "جول (J)", 1.0),
                ConversionUnit("kJ", "كيلو جول (kJ)", 1000.0),
                ConversionUnit("cal", "سعر حراري (Cal)", 4.184),
                ConversionUnit("kWh", "كيلو واط ساعة", 3.6e6),
                ConversionUnit("eV", "إلكترون فولت (eV)", 1.60218e-19)
            )
        ),
        UnitCategory(
            id = "power",
            nameArabic = "القدرة",
            icon = "💡",
            units = listOf(
                ConversionUnit("W", "واط (W)", 1.0),
                ConversionUnit("kW", "كيلو واط (kW)", 1000.0),
                ConversionUnit("hp", "حصان ميكانيكي (hp)", 745.7)
            )
        ),
        UnitCategory(
            id = "pressure",
            nameArabic = "الضغط",
            icon = "🎛️",
            units = listOf(
                ConversionUnit("Pa", "باسكال (Pa)", 1.0),
                ConversionUnit("kPa", "كيلو باسكال (kPa)", 1000.0),
                ConversionUnit("bar", "بار (bar)", 100000.0),
                ConversionUnit("atm", "ضغط جوي (atm)", 101325.0),
                ConversionUnit("mmHg", "ملم زئبق (Torr)", 133.322)
            )
        )
    )

    fun convert(value: Double, fromUnit: ConversionUnit, toUnit: ConversionUnit): Double {
        if (fromUnit == toUnit) return value

        if (fromUnit.isTemperature && toUnit.isTemperature) {
            // First convert fromUnit to Celsius
            val celsius = when (fromUnit.symbol) {
                "°C" -> value
                "°F" -> (value - 32.0) * 5.0 / 9.0
                "K" -> value - 273.15
                else -> value
            }
            // Convert Celsius to toUnit
            return when (toUnit.symbol) {
                "°C" -> celsius
                "°F" -> celsius * 9.0 / 5.0 + 32.0
                "K" -> celsius + 273.15
                else -> celsius
            }
        }

        // Standard linear conversion: value * fromFactor / toFactor
        val baseValue = value * fromUnit.factorToBase
        return baseValue / toUnit.factorToBase
    }

    fun format(v: Double): String {
        if (abs(v - v.roundToLong()) < 1e-6) {
            return v.roundToLong().toString()
        }
        return if (abs(v) < 1e-4 || abs(v) >= 1e6) {
            String.format(java.util.Locale.US, "%.4e", v)
        } else {
            String.format(java.util.Locale.US, "%.4f", v).trimEnd('0').trimEnd('.')
        }
    }
}
