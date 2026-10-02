package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

/**
 * أيقونة وشعار رقيب للتعليم الثانوي المستقل
 */
@Composable
fun RaqeebLogoEmblem(
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    showBackground: Boolean = true
) {
    Box(
        modifier = modifier
            .size(size)
            .then(
                if (showBackground) {
                    Modifier
                        .shadow(4.dp, RoundedCornerShape(size * 0.22f))
                        .clip(RoundedCornerShape(size * 0.22f))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFFFFFFFF), Color(0xFFF0F7FF), Color(0xFFE2F0FE))
                            )
                        )
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_raqeeb_logo),
            contentDescription = "شعار رقيب للتعليم الثانوي",
            modifier = Modifier
                .fillMaxSize(0.9f)
                .padding(size * 0.05f)
        )
    }
}

/**
 * شارة الهوية البصرية: "للتعليم الثانوي" محاطة بالخطوط السماوية
 */
@Composable
fun RaqeebBadge(
    modifier: Modifier = Modifier,
    fontSize: Int = 13
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // الخط السماوي الأيمن
        Box(
            modifier = Modifier
                .width(28.dp)
                .height(3.5.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(RaqeebCyanAccent)
        )

        // شارة التعليم الثانوي الكبسولية
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = RaqeebRoyalBlue,
            shadowElevation = 2.dp
        ) {
            Text(
                text = "للتعليم الثانوي",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = fontSize.sp,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 5.dp)
            )
        }

        // الخط السماوي الأيسر
        Box(
            modifier = Modifier
                .width(28.dp)
                .height(3.5.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(RaqeebCyanAccent)
        )
    }
}

/**
 * الركائز التعليمية الأربع للهوية البصرية:
 * (💡 افهم • 📋 حل • 📝 اختبر • 🎯 تفوق)
 */
@Composable
fun RaqeebPillarsBar(
    modifier: Modifier = Modifier,
    onUnderstandClick: (() -> Unit)? = null,
    onSolveClick: (() -> Unit)? = null,
    onQuizClick: (() -> Unit)? = null,
    onExcelClick: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp)),
        color = Color(0xFFE8F2FD),
        border = BorderStroke(1.2.dp, RaqeebCyanAccent.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PillarItem(
                label = "افهم",
                icon = Icons.Default.Lightbulb,
                accentColor = Color(0xFF0284C7),
                onClick = onUnderstandClick
            )

            PillarDivider()

            PillarItem(
                label = "حل",
                icon = Icons.Default.CheckCircle,
                accentColor = RaqeebRoyalBlue,
                onClick = onSolveClick
            )

            PillarDivider()

            PillarItem(
                label = "اختبر",
                icon = Icons.Default.Assignment,
                accentColor = RaqeebElectricBlue,
                onClick = onQuizClick
            )

            PillarDivider()

            PillarItem(
                label = "تفوق",
                icon = Icons.Default.EmojiEvents,
                accentColor = Color(0xFFD97706),
                onClick = onExcelClick
            )
        }
    }
}

@Composable
private fun PillarDivider() {
    Box(
        modifier = Modifier
            .size(4.dp)
            .clip(CircleShape)
            .background(RaqeebCyanAccent.copy(alpha = 0.6f))
    )
}

@Composable
private fun PillarItem(
    label: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: (() -> Unit)?
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(17.dp)
        )
        Text(
            text = label,
            color = RaqeebDeepNavy,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}

/**
 * البطاقة الترويسية الكبرى للهوية البصرية لتطبيق "رقيب"
 * تطابق تصميم اللوجو بالكامل:
 * - قبعة التخرج وحرف R والكتاب
 * - خط "رقيب" العريض
 * - شارة "للتعليم الثانوي"
 * - الشعار الفرعي "تطبيق لطلاب الثالث الثانوي"
 * - الركائز الأربع (افهم • حل • اختبر • تفوق)
 */
@Composable
fun RaqeebHeroHeader(
    modifier: Modifier = Modifier,
    onUnderstandClick: (() -> Unit)? = null,
    onSolveClick: (() -> Unit)? = null,
    onQuizClick: (() -> Unit)? = null,
    onExcelClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(26.dp)),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.5.dp, Brush.linearGradient(listOf(RaqeebCyanAccent, RaqeebRoyalBlue.copy(alpha = 0.4f))))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFFFFF),
                            Color(0xFFF7FAFD),
                            Color(0xFFEEF6FF)
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. الشعار المركزي (قبعة تخرج + R + كتاب)
            RaqeebLogoEmblem(
                size = 110.dp,
                showBackground = true
            )

            // 2. اسم التطبيق بخط الهوية الفخم "رقيب"
            Text(
                text = "رَقِــيـب",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 34.sp,
                    color = RaqeebDeepNavy,
                    letterSpacing = 1.sp
                ),
                textAlign = TextAlign.Center
            )

            // 3. شارة "للتعليم الثانوي" مع خطوط السهم السماوية
            RaqeebBadge(fontSize = 13)

            // 4. السطر التوضيحي المعتمد
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(1.dp)
                        .background(RaqeebCyanAccent.copy(alpha = 0.5f))
                )
                Text(
                    text = "تطبيق لطلاب الثالث الثانوي 🇾🇪",
                    color = RaqeebDeepNavy.copy(alpha = 0.85f),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(1.dp)
                        .background(RaqeebCyanAccent.copy(alpha = 0.5f))
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 5. شريط الركائز الأربع التفاعلية (افهم • حل • اختبر • تفوق)
            RaqeebPillarsBar(
                onUnderstandClick = onUnderstandClick,
                onSolveClick = onSolveClick,
                onQuizClick = onQuizClick,
                onExcelClick = onExcelClick
            )
        }
    }
}
