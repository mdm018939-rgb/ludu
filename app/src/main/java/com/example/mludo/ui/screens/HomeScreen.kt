package com.example.mludo.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.mludo.audio.SoundManager
import com.example.mludo.ui.components.BackgroundPattern
import com.example.mludo.ui.components.GlossyButton
import com.example.mludo.ui.components.GlossyButtonStyle
import com.example.mludo.ui.theme.FredokaFontFamily

@Composable
fun HomeScreen(
    hasSavedGame: Boolean,
    onPassNPlayClicked: () -> Unit,
    onResumeGameClicked: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackgroundPattern(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            HomeTopBar(onOpenSettings = onOpenSettings)

            // Main Content Area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Main Logo Graphic
                Image(
                    painter = painterResource(id = R.drawable.mludo_title_logo_1793679148358),
                    contentDescription = "M Ludo Logo",
                    modifier = Modifier.size(240.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Resume Saved Game Button if available
                if (hasSavedGame) {
                    GlossyButton(
                        text = "RESUME GAME",
                        subText = "Continue previous match",
                        onClick = onResumeGameClicked,
                        style = GlossyButtonStyle.GOLD,
                        modifier = Modifier.fillMaxWidth(0.85f),
                        testTag = "resume_game_button"
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // PASS N PLAY Primary Action Button
                GlossyButton(
                    text = "PASS N PLAY",
                    subText = "Local 2 to 6 Players",
                    onClick = onPassNPlayClicked,
                    style = GlossyButtonStyle.GOLD,
                    modifier = Modifier.fillMaxWidth(0.85f),
                    testTag = "pass_n_play_button"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Disabled / Coming soon mode buttons matching screenshot layout
                Row(
                    modifier = Modifier.fillMaxWidth(0.85f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GlossyButton(
                        text = "COMPUTER",
                        onClick = {},
                        enabled = false,
                        subText = "VS AI (Soon)",
                        style = GlossyButtonStyle.BLUE,
                        modifier = Modifier.weight(1f),
                        padding = androidx.compose.foundation.layout.PaddingValues(vertical = 10.dp)
                    )
                    GlossyButton(
                        text = "ONLINE",
                        onClick = {},
                        enabled = false,
                        subText = "Multiplayer (Soon)",
                        style = GlossyButtonStyle.BLUE,
                        modifier = Modifier.weight(1f),
                        padding = androidx.compose.foundation.layout.PaddingValues(vertical = 10.dp)
                    )
                }
            }

            // Bottom Navigation Bar
            HomeBottomNavBar()
        }
    }
}

@Composable
private fun HomeTopBar(
    onOpenSettings: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile Avatar + Level Badge
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0072FF))
                    .border(2.dp, Color(0xFFFFD700), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Player 1",
                    color = Color.White,
                    fontFamily = FredokaFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = "Level 2",
                    color = Color(0xFFFFD700),
                    fontFamily = FredokaFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp
                )
            }
        }

        // Coins Counter + Settings Gear
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Coins Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF062B6D))
                    .border(1.5.dp, Color(0xFFFFD700), RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🪙", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "1,550",
                        color = Color.White,
                        fontFamily = FredokaFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Settings Button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF062B6D))
                    .border(1.5.dp, Color(0xFFFFD700), CircleShape)
                    .clickable { onOpenSettings() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun HomeBottomNavBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF04112E))
            .border(width = 1.dp, color = Color(0xFF0D3685))
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem(icon = Icons.Default.Home, label = "HOME", selected = true)
        BottomNavItem(icon = Icons.Default.Star, label = "EVENT", selected = false)
        BottomNavItem(icon = Icons.Default.Mic, label = "ADDA", selected = false)
        BottomNavItem(icon = Icons.Default.Inventory, label = "INVENTORY", selected = false)
        BottomNavItem(icon = Icons.Default.Group, label = "SOCIAL", selected = false)
    }
}

@Composable
private fun BottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) Color(0xFFFFD700) else Color.White.copy(alpha = 0.6f),
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            color = if (selected) Color(0xFFFFD700) else Color.White.copy(alpha = 0.6f),
            fontSize = 10.sp,
            fontFamily = FredokaFontFamily,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
