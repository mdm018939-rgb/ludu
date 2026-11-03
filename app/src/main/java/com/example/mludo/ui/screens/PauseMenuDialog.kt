package com.example.mludo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.mludo.ui.components.GlossyButton
import com.example.mludo.ui.components.GlossyButtonStyle
import com.example.mludo.ui.theme.FredokaFontFamily

@Composable
fun PauseMenuDialog(
    soundEnabled: Boolean,
    debugMode: Boolean,
    onToggleSound: (Boolean) -> Unit,
    onToggleDebug: (Boolean) -> Unit,
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onExitHome: () -> Unit
) {
    Dialog(onDismissRequest = onResume) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .shadow(16.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0A2B70),
                            Color(0xFF061A47),
                            Color(0xFF04102C)
                        )
                    )
                )
                .border(2.5.dp, Color(0xFFFFD700), RoundedCornerShape(24.dp))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header Title
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0564B5))
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "GAME PAUSED",
                        color = Color(0xFFFFD700),
                        fontFamily = FredokaFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Sound FX Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF07193F))
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sound Effects",
                        color = Color.White,
                        fontFamily = FredokaFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Switch(
                        checked = soundEnabled,
                        onCheckedChange = onToggleSound,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFFFFD700),
                            checkedTrackColor = Color(0xFF0072FF)
                        )
                    )
                }

                // Debug Dice Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF07193F))
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Debug Dice Selector",
                        color = Color.White,
                        fontFamily = FredokaFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Switch(
                        checked = debugMode,
                        onCheckedChange = onToggleDebug,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFFFFD700),
                            checkedTrackColor = Color(0xFF0072FF)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                GlossyButton(
                    text = "Resume",
                    onClick = onResume,
                    style = GlossyButtonStyle.GOLD,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "pause_resume_button"
                )

                GlossyButton(
                    text = "Restart Match",
                    onClick = onRestart,
                    style = GlossyButtonStyle.BLUE,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "pause_restart_button"
                )

                GlossyButton(
                    text = "Exit to Home",
                    onClick = onExitHome,
                    style = GlossyButtonStyle.RED,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "pause_exit_button"
                )
            }
        }
    }
}
