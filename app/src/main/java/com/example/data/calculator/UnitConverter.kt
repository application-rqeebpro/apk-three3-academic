package com.example.data.calculator

data class ConversionUnit(
    val symbol: String,
    val nameArabic: String,
    val factorToBase: Double // multiplier to convert this unit into SI base unit
)

data class UnitCategory(
    val nameArabic: String,
    val icon: String,
    val units: List<ConversionUnit>
)

object UnitConverter {

    val categories = listOf(
        UnitCategory(
            nameArabic = "الطول والمسافة",
            icon = "📏",
            units = listOf(
                ConversionUnit("m", "متر", 1.0),
                ConversionUnit("km", "كيلومتر", 1000.0),
                ConversionUnit("cm", "سنتيمتر", 0.01),
                ConversionUnit("mm", "مليمتر", 0.001),
                ConversionUnit("µm", "ميكرومتر", 1e-6),
                ConversionUnit("nm", "نانومتر", 1e-9),
                ConversionUnit("Å", "أنغستروم", 1e-10)
            )
        ),
        UnitCategory(
            nameArabic = "الكتلة والوزن",
            icon = "⚖️",
            units = listOf(
                ConversionUnit("kg", "كيلوجرام", 1.0),
                ConversionUnit("g", "جرام", 0.001),
                ConversionUnit("mg", "مليجرام", 1e-6),
                ConversionUnit("ton", "طن متري", 1000.0),
                ConversionUnit("amu", "وحدة كتل ذرية", 1.66054e-27)
            )
        ),
        UnitCategory(
            nameArabic = "الزمن",
            icon = "⏱️",
            units = listOf(
                ConversionUnit("s", "ثانية", 1.0),
                ConversionUnit("min", "دقيقة", 60.0),
                ConversionUnit("h", "ساعة", 3600.0),
                ConversionUnit("ms", "مليثانية", 0.001),
                ConversionUnit("µs", "ميكروثانية", 1e-6),
                ConversionUnit("ns", "نانوثانية", 1e-9)
            )
        ),
        UnitCategory(
            nameArabic = "القوة والضغط",
            icon = "💥",
            units = listOf(
                ConversionUnit("N", "نيوتن", 1.0),
                ConversionUnit("kN", "كيلونيوتن", 1000.0),
                ConversionUnit("dyne", "داين", 1e-5),
                ConversionUnit("Pa", "باسكال", 1.0),
                ConversionUnit("atm", "ضغط جوي", 101325.0),
                ConversionUnit("bar", "بار", 100000.0),
                ConversionUnit("mmHg", "ملم زئبق (تور)", 133.322)
            )
        ),
        UnitCategory(
            nameArabic = "الطاقة والشغل والقدرة",
            icon = "⚡",
            units = listOf(
                ConversionUnit("J", "جول", 1.0),
                ConversionUnit("kJ", "كيلوجول", 1000.0),
                ConversionUnit("cal", "سعر حراري", 4.184),
                ConversionUnit("kcal", "كيلوسعر", 4184.0),
                ConversionUnit("eV", "إلكترون فولت", 1.602e-19),
                ConversionUnit("MeV", "ميجا إلكترون فولت", 1.602e-13),
                ConversionUnit("kWh", "كيلوواط.ساعة", 3.6e6),
                ConversionUnit("W", "واط (قدرة)", 1.0),
                ConversionUnit("kW", "كيلوواط", 1000.0),
                ConversionUnit("hp", "حصان ميكانيكي", 745.7)
            )
        ),
        UnitCategory(
            nameArabic = "الكهرباء والمغناطيسية",
            icon = "🔋",
            units = listOf(
                ConversionUnit("V", "فولت", 1.0),
                ConversionUnit("kV", "كيلوفولت", 1000.0),
                ConversionUnit("mV", "مليفولت", 0.001),
                ConversionUnit("A", "أمبير", 1.0),
                ConversionUnit("mA", "مليأمبير", 0.001),
                ConversionUnit("µA", "ميكروأمبير", 1e-6),
                ConversionUnit("Ω", "أوم", 1.0),
                ConversionUnit("kΩ", "كيلوأوم", 1000.0),
                ConversionUnit("MΩ", "ميجاأوم", 1e6),
                ConversionUnit("F", "فاراد", 1.0),
                ConversionUnit("µF", "ميكروفاراد", 1e-6),
                ConversionUnit("pF", "بيكوفاراد", 1e-12),
                ConversionUnit("T", "تسلا", 1.0),
                ConversionUnit("G", "غاوس", 1e-4)
            )
        )
    )

    fun convert(value: Double, from: ConversionUnit, to: ConversionUnit): Double {
        val inBase = value * from.factorToBase
        return inBase / to.factorToBase
    }

    fun format(value: Double): String {
        return if (kotlin.math.abs(value) >= 1e6 || (kotlin.math.abs(value) > 0 && kotlin.math.abs(value) < 1e-4)) {
            String.format(java.util.Locale.US, "%.4e", value)
        } else {
            String.format(java.util.Locale.US, "%.5f", value).trimEnd('0').trimEnd('.')
        }
    }
}
