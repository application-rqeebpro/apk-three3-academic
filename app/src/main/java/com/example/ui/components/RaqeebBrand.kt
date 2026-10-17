package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import android.widget.Toast
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.*
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ميزة الضغط المطول لمدة 20 ثانية على شعار التطبيق لإظهار أيقونة المالك وبوابة الدخول
 */
fun Modifier.secretOwnerHoldTrigger(
    holdDurationMs: Long = 20_000L,
    onTrigger: () -> Unit
): Modifier = composed {
    val coroutineScope = rememberCoroutineScope()
    var isHolding by remember { mutableStateOf(false) }
    var holdProgress by remember { mutableFloatStateOf(0f) }
    var holdJob by remember { mutableStateOf<Job?>(null) }
    val context = LocalContext.current

    this
        .drawWithContent {
            drawContent()
            if (isHolding && holdProgress > 0f) {
                drawCircle(
                    color = Color(0xFFFFD700).copy(alpha = 0.45f * holdProgress),
                    radius = (size.maxDimension / 2f) + (6.dp.toPx() * holdProgress),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5.dp.toPx())
                )
            }
        }
        .pointerInput(onTrigger, holdDurationMs) {
            awaitEachGesture {
                awaitFirstDown(requireUnconsumed = false)
                isHolding = true
                holdProgress = 0f
                val startTime = System.currentTimeMillis()

                holdJob?.cancel()
                holdJob = coroutineScope.launch {
                    while (isHolding) {
                        val elapsed = System.currentTimeMillis() - startTime
                        holdProgress = (elapsed.toFloat() / holdDurationMs).coerceIn(0f, 1f)
                        if (elapsed >= holdDurationMs) {
                            Toast.makeText(context, "تم التحقق من طلب المالك 👑", Toast.LENGTH_SHORT).show()
                            onTrigger()
                            break
                        }
                        delay(50L)
                    }
                }

                do {
                    val event = awaitPointerEvent()
                } while (event.changes.any { it.pressed })

                isHolding = false
                holdJob?.cancel()
                holdProgress = 0f
            }
        }
}

@Composable
fun RaqeebHeaderLogo(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_raqeeb_logo),
            contentDescription = "شعار رقيب",
            modifier = Modifier.size(36.dp)
        )
        Column {
            Text(
                text = "رَقِــيـب",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = NavyPrimary,
                    fontSize = 17.sp
                )
            )
            Text(
                text = "للتعليم الثانوي 🇾🇪",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            )
        }
    }
}

@Composable
fun RaqeebLogoEmblem(
    size: Dp = 48.dp,
    showBackground: Boolean = true,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .let {
                if (showBackground) {
                    it
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                } else it
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_raqeeb_logo),
            contentDescription = "شعار رقيب",
            modifier = Modifier.size(size * 0.8f)
        )
    }
}

@Composable
fun RaqeebBadge(
    fontSize: Int = 11,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFF0044B3),
        modifier = modifier
    ) {
        Text(
            text = "رقيب للتعليم الثانوي 🇾🇪",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun RaqeebHeroHeader(
    onUnderstandClick: () -> Unit,
    onSolveClick: () -> Unit,
    onQuizClick: () -> Unit,
    onExcelClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = NavyPrimary
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "رَقِــيـب للتعليم الثانوي",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    )
                    Text(
                        text = "منهاج الصف الثالث الثانوي بالجمهورية اليمنية 🇾🇪",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AmberLight,
                            fontSize = 11.sp
                        )
                    )
                }
                RaqeebLogoEmblem(size = 46.dp, showBackground = true)
            }

            Text(
                text = "افهم دروسك • حل مسائلك خطوة بخطوة • اختبر مستواك • تفوق بامتياز",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 11.5.sp
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = onUnderstandClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RaqeebElectricBlue
                    ),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                ) {
                    Text("افهم الدرس", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onSolveClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberSecondary
                    ),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                ) {
                    Text("حل مسألتك", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onQuizClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RaqeebEmeraldGreen
                    ),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                ) {
                    Text("اختبر نفسك", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SubscriptionStatusBadge(
    isSubscribed: Boolean,
    planName: String?,
    daysRemaining: Long?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSubscribed) Color(0xFFF0FDF4) else Color(0xFFFEF2F2)
        ),
        border = BorderStroke(
            1.dp,
            if (isSubscribed) Color(0xFF86EFAC) else Color(0xFFFECACA)
        ),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = if (isSubscribed) Icons.Default.CheckCircle else Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (isSubscribed) Color(0xFF16A34A) else Color(0xFFDC2626)
                )
                Column {
                    Text(
                        text = if (isSubscribed) "اشتراك مفعل: ${planName ?: "باقة دراسية"}" else "الاشتراك غير مفعل",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isSubscribed) Color(0xFF166534) else Color(0xFF991B1B)
                        )
                    )
                    if (isSubscribed && daysRemaining != null) {
                        Text(
                            text = "متبقي $daysRemaining يوماً على نهاية الاشتراك",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF15803D)
                            )
                        )
                    } else if (!isSubscribed) {
                        Text(
                            text = "اضغط لتفعيل الاشتراك أو إدخال كود التفعيل",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFFB91C1C)
                            )
                        )
                    }
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = if (isSubscribed) Color(0xFF16A34A) else Color(0xFFDC2626),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
