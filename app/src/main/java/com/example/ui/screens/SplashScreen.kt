package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.neumorphicInset
import com.example.ui.components.neumorphicRaised
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentPink
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.AccentPurpleDark
import com.example.ui.theme.AccentTeal
import com.example.ui.theme.NeumorphicBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
  onFinishSplash: () -> Unit,
  modifier: Modifier = Modifier
) {
  val scaleAnim = remember { Animatable(0.75f) }
  val alphaAnim = remember { Animatable(0f) }
  val progressAnim = remember { Animatable(0f) }

  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.98f,
    targetValue = 1.03f,
    animationSpec = infiniteRepeatable(
      animation = tween(1400, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulseScale"
  )

  LaunchedEffect(Unit) {
    // Staggered entrance animation
    scaleAnim.animateTo(
      targetValue = 1f,
      animationSpec = tween(700, easing = FastOutSlowInEasing)
    )
    alphaAnim.animateTo(
      targetValue = 1f,
      animationSpec = tween(500)
    )
    // Smooth progress bar fill
    progressAnim.animateTo(
      targetValue = 1f,
      animationSpec = tween(1200, easing = LinearEasing)
    )
    delay(200)
    onFinishSplash()
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(NeumorphicBg)
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onFinishSplash
      )
      .testTag("splash_screen"),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier
        .padding(horizontal = 32.dp)
        .scale(scaleAnim.value)
        .alpha(alphaAnim.value)
    ) {
      // Outer Neumorphic Glowing Ring
      Box(
        modifier = Modifier
          .size(160.dp)
          .scale(pulseScale)
          .neumorphicRaised(
            cornerRadius = 80.dp,
            elevation = 12.dp,
            lightShadowColor = Color.White.copy(alpha = 0.95f),
            darkShadowColor = Color(0xFFA6B4C4).copy(alpha = 0.8f)
          )
          .clip(CircleShape)
          .background(NeumorphicBg),
        contentAlignment = Alignment.Center
      ) {
        // Debossed inner bezel
        Box(
          modifier = Modifier
            .size(136.dp)
            .clip(CircleShape)
            .neumorphicInset(
              cornerRadius = 68.dp,
              depth = 4.dp
            )
            .background(NeumorphicBg),
          contentAlignment = Alignment.Center
        ) {
          // App Logo Image
          Image(
            painter = painterResource(id = R.drawable.spendsavvy_icon_1790049463904),
            contentDescription = "SpendSavvy Logo",
            modifier = Modifier
              .size(116.dp)
              .clip(CircleShape),
            contentScale = ContentScale.Crop
          )
        }
      }

      Spacer(modifier = Modifier.height(32.dp))

      // Branding Title
      Text(
        text = "SPENDSAVVY",
        fontSize = 28.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 4.sp,
        color = TextPrimary
      )

      Spacer(modifier = Modifier.height(6.dp))

      // Tagline
      Text(
        text = "Housemate & Shared Living Equalizer",
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.5.sp,
        color = TextSecondary
      )

      Spacer(modifier = Modifier.height(36.dp))

      // Neumorphic Recessed Progress Loader
      Box(
        modifier = Modifier
          .width(180.dp)
          .height(8.dp)
          .clip(RoundedCornerShape(4.dp))
          .background(NeumorphicBg)
          .neumorphicInset(cornerRadius = 4.dp, depth = 2.dp)
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth(progressAnim.value.coerceIn(0f, 1f))
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(
              Brush.horizontalGradient(
                listOf(AccentCyan, AccentPurple, AccentPink)
              )
            )
        )
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Feature tags
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        SplashPill(text = "Equalizer", color = AccentCyan)
        SplashPill(text = "Debt Simplifier", color = AccentPurple)
        SplashPill(text = "Pro", color = AccentAmber)
      }
    }

    // Bottom Footer
    Box(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 32.dp)
    ) {
      Text(
        text = "SpendSavvy v1.0 · Tap to skip",
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = TextMuted
      )
    }
  }
}

@Composable
private fun SplashPill(text: String, color: Color) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(12.dp))
      .background(color.copy(alpha = 0.12f))
      .padding(horizontal = 10.dp, vertical = 4.dp)
  ) {
    Text(
      text = text,
      fontSize = 10.sp,
      fontWeight = FontWeight.Bold,
      color = color
    )
  }
}
