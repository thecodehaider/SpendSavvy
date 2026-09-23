package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentPink
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.AccentPurpleDark
import com.example.ui.theme.AccentPurpleLight
import com.example.ui.theme.AccentTeal
import com.example.ui.theme.NeumorphicBg
import com.example.ui.theme.NeumorphicDark
import com.example.ui.theme.NeumorphicInsetDark
import com.example.ui.theme.NeumorphicLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.cos
import kotlin.math.sin

/**
 * Modifier that applies custom soft Neumorphic dual shadows (embossed/raised).
 * Top-left has soft white illumination, bottom-right has soft ambient dark shadow.
 */
fun Modifier.neumorphicRaised(
  cornerRadius: Dp = 16.dp,
  elevation: Dp = 6.dp,
  lightShadowColor: Color = NeumorphicLight,
  darkShadowColor: Color = NeumorphicDark,
  backgroundColor: Color = NeumorphicBg,
  isPressed: Boolean = false,
): Modifier = this.drawBehind {
  val shadowOffset = if (isPressed) elevation.toPx() * 0.3f else elevation.toPx()
  val blurRadius = if (isPressed) elevation.toPx() * 0.8f else elevation.toPx() * 1.5f
  val radiusPx = cornerRadius.toPx()

  // Draw bottom-right dark shadow
  drawContext.canvas.nativeCanvas.apply {
    val darkPaint = android.graphics.Paint().apply {
      isAntiAlias = true
      color = darkShadowColor.toArgb()
      setShadowLayer(
        blurRadius,
        shadowOffset,
        shadowOffset,
        darkShadowColor.copy(alpha = if (isPressed) 0.35f else 0.55f).toArgb()
      )
    }
    drawRoundRect(
      shadowOffset * 0.5f,
      shadowOffset * 0.5f,
      size.width + shadowOffset * 0.5f,
      size.height + shadowOffset * 0.5f,
      radiusPx,
      radiusPx,
      darkPaint
    )

    // Draw top-left light reflection
    val lightPaint = android.graphics.Paint().apply {
      isAntiAlias = true
      color = lightShadowColor.toArgb()
      setShadowLayer(
        blurRadius * 0.9f,
        -shadowOffset,
        -shadowOffset,
        lightShadowColor.copy(alpha = if (isPressed) 0.5f else 0.9f).toArgb()
      )
    }
    drawRoundRect(
      -shadowOffset * 0.3f,
      -shadowOffset * 0.3f,
      size.width - shadowOffset * 0.3f,
      size.height - shadowOffset * 0.3f,
      radiusPx,
      radiusPx,
      lightPaint
    )
  }

  // Draw base surface
  drawRoundRect(
    color = backgroundColor,
    size = size,
    cornerRadius = CornerRadius(radiusPx, radiusPx)
  )
}

/**
 * Modifier for recessed/debossed neumorphic channel (inner shadow effect).
 */
fun Modifier.neumorphicInset(
  cornerRadius: Dp = 20.dp,
  depth: Dp = 4.dp,
  backgroundColor: Color = NeumorphicBg,
): Modifier = this
  .clip(RoundedCornerShape(cornerRadius))
  .background(backgroundColor)
  .drawBehind {
    val strokeWidthPx = depth.toPx()
    val radiusPx = cornerRadius.toPx()

    // Inner top-left dark shadow
    drawRoundRect(
      color = NeumorphicInsetDark.copy(alpha = 0.45f),
      topLeft = Offset(0f, 0f),
      size = size,
      cornerRadius = CornerRadius(radiusPx, radiusPx),
      style = Stroke(width = strokeWidthPx)
    )

    // Inner bottom-right subtle light glow
    drawRoundRect(
      color = NeumorphicLight.copy(alpha = 0.65f),
      topLeft = Offset(strokeWidthPx * 0.6f, strokeWidthPx * 0.6f),
      size = Size(size.width - strokeWidthPx * 0.6f, size.height - strokeWidthPx * 0.6f),
      cornerRadius = CornerRadius(radiusPx, radiusPx),
      style = Stroke(width = strokeWidthPx * 0.5f)
    )
  }

/**
 * Neumorphic Card container.
 */
@Composable
fun NeumorphicCard(
  modifier: Modifier = Modifier,
  cornerRadius: Dp = 22.dp,
  elevation: Dp = 6.dp,
  backgroundColor: Color = NeumorphicBg,
  content: @Composable () -> Unit,
) {
  Box(
    modifier = modifier
      .neumorphicRaised(
        cornerRadius = cornerRadius,
        elevation = elevation,
        backgroundColor = backgroundColor
      )
      .padding(16.dp)
  ) {
    content()
  }
}

/**
 * Interactive Neumorphic Button with press response.
 */
@Composable
fun NeumorphicButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  cornerRadius: Dp = 16.dp,
  elevation: Dp = 5.dp,
  backgroundColor: Color = NeumorphicBg,
  content: @Composable () -> Unit,
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()

  Box(
    modifier = modifier
      .neumorphicRaised(
        cornerRadius = cornerRadius,
        elevation = elevation,
        backgroundColor = backgroundColor,
        isPressed = isPressed
      )
      .clip(RoundedCornerShape(cornerRadius))
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        role = Role.Button,
        onClick = onClick
      )
      .padding(horizontal = 16.dp, vertical = 10.dp),
    contentAlignment = Alignment.Center
  ) {
    content()
  }
}

/**
 * Neumorphic Icon Button (circle or squircle).
 */
@Composable
fun NeumorphicIconButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  size: Dp = 44.dp,
  cornerRadius: Dp = size / 2,
  elevation: Dp = 4.dp,
  backgroundColor: Color = NeumorphicBg,
  content: @Composable () -> Unit,
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()

  Box(
    modifier = modifier
      .size(size)
      .neumorphicRaised(
        cornerRadius = cornerRadius,
        elevation = elevation,
        backgroundColor = backgroundColor,
        isPressed = isPressed
      )
      .clip(RoundedCornerShape(cornerRadius))
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        role = Role.Button,
        onClick = onClick
      ),
    contentAlignment = Alignment.Center
  ) {
    content()
  }
}

/**
 * Neumorphic 2-way toggle switch (as shown on top right of "Statistics" in the image).
 */
@Composable
fun NeumorphicToggleSwitch(
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  modifier: Modifier = Modifier,
  label: String = "Chart",
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
  ) {
    Box(
      modifier = Modifier
        .width(64.dp)
        .height(32.dp)
        .neumorphicInset(cornerRadius = 16.dp, depth = 3.dp)
        .clickable { onCheckedChange(!checked) }
        .padding(3.dp),
      contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart
    ) {
      val thumbOffset by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = tween(220),
        label = "thumb"
      )

      Box(
        modifier = Modifier
          .size(26.dp)
          .neumorphicRaised(cornerRadius = 13.dp, elevation = 3.dp)
          .background(NeumorphicBg, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        // Tiny dots pattern inside switch knob
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
          Box(modifier = Modifier.size(3.dp).background(TextMuted, CircleShape))
          Box(modifier = Modifier.size(3.dp).background(TextMuted, CircleShape))
        }
      }
    }

    if (label.isNotEmpty()) {
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = label,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = TextSecondary
      )
    }
  }
}

/**
 * The Signature Neumorphic Doughnut Ring Gauge (Exact match to Image Screen 2).
 * Shows concentric raised circular layers with a recessed track and vivid gradient arc.
 */
@Composable
fun NeumorphicDoughnutGauge(
  percentage: Float, // 0.0f to 1.0f (e.g. 0.73f for 73%)
  modifier: Modifier = Modifier,
  size: Dp = 190.dp,
  title: String = "${(percentage * 100).toInt()}%",
  subtitle: String = "Settled",
) {
  val animatedProgress by animateFloatAsState(
    targetValue = percentage.coerceIn(0f, 1f),
    animationSpec = tween(900),
    label = "progress"
  )

  Box(
    modifier = modifier.size(size),
    contentAlignment = Alignment.Center
  ) {
    // Canvas for the soft dual-shadow circular plate and gradient arc
    Canvas(modifier = Modifier.fillMaxSize()) {
      val center = Offset(this.size.width / 2f, this.size.height / 2f)
      val outerRadius = this.size.width / 2f - 16.dp.toPx()
      val strokeWidth = 24.dp.toPx()

      // 1. Draw outer neumorphic disc shadow
      drawContext.canvas.nativeCanvas.apply {
        // Dark bottom-right shadow
        val darkPaint = android.graphics.Paint().apply {
          isAntiAlias = true
          color = NeumorphicDark.toArgb()
          setShadowLayer(26f, 10f, 10f, NeumorphicDark.copy(alpha = 0.55f).toArgb())
        }
        drawCircle(center.x + 3f, center.y + 3f, outerRadius, darkPaint)

        // Light top-left reflection
        val lightPaint = android.graphics.Paint().apply {
          isAntiAlias = true
          color = NeumorphicLight.toArgb()
          setShadowLayer(24f, -10f, -10f, NeumorphicLight.copy(alpha = 0.9f).toArgb())
        }
        drawCircle(center.x - 3f, center.y - 3f, outerRadius, lightPaint)
      }

      // 2. Base circular disc
      drawCircle(
        color = NeumorphicBg,
        radius = outerRadius,
        center = center
      )

      // 3. Recessed circular track for the arc
      val trackRadius = outerRadius - strokeWidth * 0.45f
      drawCircle(
        color = NeumorphicDark.copy(alpha = 0.25f),
        radius = trackRadius,
        center = center,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
      )

      // 4. Vivid Purple/Violet Gradient Arc (exact match to image!)
      val gradientBrush = Brush.linearGradient(
        colors = listOf(
          Color(0xFF7C4DFF), // Purple
          Color(0xFF903AFF), // Vivid violet
          Color(0xFFB388FF), // Lavender
          Color(0xFF6C5CE7)
        ),
        start = Offset(center.x - outerRadius, center.y + outerRadius),
        end = Offset(center.x + outerRadius, center.y - outerRadius)
      )

      val sweepAngle = 360f * animatedProgress
      val startAngle = 135f // Arc starting position matching the image's orientation

      drawArc(
        brush = gradientBrush,
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        useCenter = false,
        topLeft = Offset(center.x - trackRadius, center.y - trackRadius),
        size = Size(trackRadius * 2, trackRadius * 2),
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
      )

      // 5. Center recessed inner core
      val innerRadius = outerRadius - strokeWidth * 1.05f
      drawContext.canvas.nativeCanvas.apply {
        val innerDarkPaint = android.graphics.Paint().apply {
          isAntiAlias = true
          color = NeumorphicDark.toArgb()
          setShadowLayer(14f, 4f, 4f, NeumorphicInsetDark.copy(alpha = 0.45f).toArgb())
        }
        drawCircle(center.x, center.y, innerRadius, innerDarkPaint)
      }

      drawCircle(
        color = NeumorphicBg,
        radius = innerRadius,
        center = center
      )
    }

    // Centered percentage label
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = title,
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
      )
      if (subtitle.isNotEmpty()) {
        Text(
          text = subtitle,
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium,
          color = TextSecondary
        )
      }
    }
  }
}

/**
 * Vertical Recessed Equalizer Track with Vibrant Pill (Exact match to Image Screen 2).
 * Shows the debossed channel with a rounded gradient capsule filling up to a percentage.
 */
@Composable
fun NeumorphicEqualizerTrack(
  percentage: Float, // 0.0f to 1.0f
  label: String,
  gradientColors: List<Color>,
  modifier: Modifier = Modifier,
  trackWidth: Dp = 38.dp,
  trackHeight: Dp = 130.dp,
  valueText: String = "${(percentage * 100).toInt()}%",
) {
  val animatedFill by animateFloatAsState(
    targetValue = percentage.coerceIn(0f, 1f),
    animationSpec = tween(800),
    label = "fill"
  )

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
  ) {
    // Recessed Capsule Slot
    Box(
      modifier = Modifier
        .width(trackWidth)
        .height(trackHeight)
        .neumorphicInset(cornerRadius = trackWidth / 2, depth = 3.dp),
      contentAlignment = Alignment.BottomCenter
    ) {
      if (animatedFill > 0.03f) {
        val pillHeight = (trackHeight.value * animatedFill).coerceAtLeast(trackWidth.value).dp

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(pillHeight)
            .padding(3.dp)
            .clip(RoundedCornerShape(trackWidth / 2))
            .background(
              Brush.verticalGradient(gradientColors)
            )
            // Soft outer glow for the vibrant pill
            .drawBehind {
              drawRoundRect(
                color = gradientColors.first().copy(alpha = 0.3f),
                cornerRadius = CornerRadius(size.width / 2, size.width / 2)
              )
            }
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    Text(
      text = label,
      fontSize = 11.sp,
      fontWeight = FontWeight.SemiBold,
      color = TextSecondary
    )
    Text(
      text = valueText,
      fontSize = 10.sp,
      fontWeight = FontWeight.Medium,
      color = TextMuted
    )
  }
}
