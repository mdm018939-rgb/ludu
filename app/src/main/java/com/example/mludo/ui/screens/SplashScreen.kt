package com.example.mludo.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.mludo.ui.components.BackgroundPattern
import com.example.mludo.ui.theme.FredokaFontFamily
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val scaleAnim = remember { Animatable(0.7f) }

    LaunchedEffect(Unit) {
        scaleAnim.animateTo(
            targetValue = 1.05f,
            animationSpec = tween(durationMillis = 800)
        )
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 400)
        )
        delay(300)
        onSplashFinished()
    }

    BackgroundPattern(
        modifier = Modifier.clickable { onSplashFinished() }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.mludo_title_logo_1793679148358),
                    contentDescription = "M Ludo Logo",
                    modifier = Modifier
                        .size(280.dp)
                        .scale(scaleAnim.value)
                )

                Spacer(modifier = Modifier.height(32.dp))

                LinearProgressIndicator(
                    color = Color(0xFFFFD700),
                    trackColor = Color(0x33FFFFFF),
                    modifier = Modifier
                        .size(width = 180.dp, height = 8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Loading M Ludo...",
                    color = Color.White.copy(alpha = 0.8f),
                    fontFamily = FredokaFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}
